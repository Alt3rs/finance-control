package com.example.finance_control.service;
import com.alt3rs.seorganiza.domain.activity.Activity;
import com.alt3rs.seorganiza.domain.type.Type;
import com.alt3rs.seorganiza.domain.user.User;
import com.alt3rs.seorganiza.dto.ActivityRequestDTO;
import com.alt3rs.seorganiza.dto.ActivityResponseDTO;
import com.alt3rs.seorganiza.dto.mapper.ActivityMapper;
import com.alt3rs.seorganiza.exceptions.DatabaseException;
import com.alt3rs.seorganiza.exceptions.ResourceNotFoundException;
import com.alt3rs.seorganiza.repository.activity.ActivityRepository;
import com.alt3rs.seorganiza.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    @Autowired
    private UserRepository userRepository;


    public ActivityResponseDTO insertActivity(ActivityRequestDTO activityRequestDTO) {
        // Buscar o User pelo userId contido no DTO
        User user = userRepository.findById(activityRequestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Converter DTO para entidade
        Activity activity = ActivityMapper.toEntity(activityRequestDTO, user);
        // Salvar a entidade no repositório
        Activity savedActivity = repository.save(activity);
        // Converter a entidade salva para DTO
        return ActivityMapper.toResponseDTO(savedActivity);
    }
    
    public void removeActivity(String id, String userId) {
        // Verificar se o usuário existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Buscar a atividade pelo ID e verificar se pertence ao usuário
        Activity activity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        if (!activity.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("This activity does not belong to the user.");
        }

        // Remover a atividade se for do usuário
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public ActivityResponseDTO updateActivity(String id, ActivityRequestDTO activityRequestDTO) {
        // Verificar se a atividade existe
        Activity existingActivity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        // Buscar o User pelo userId
        User user = userRepository.findById(activityRequestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Atualizar os valores da atividade existente
        existingActivity.setDate(activityRequestDTO.date());
        existingActivity.setDescription(activityRequestDTO.description());
        existingActivity.setValue(activityRequestDTO.value());
        existingActivity.setType(activityRequestDTO.type());
        existingActivity.setUser(user);

        // Salvar a atividade atualizada
        Activity savedActivity = repository.save(existingActivity);

        // Converter a entidade atualizada para DTO
        return ActivityMapper.toResponseDTO(savedActivity);
    }

    public List<ActivityResponseDTO> listActivities(String userId) {
        // Verificar se o usuário existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Buscar atividades associadas ao usuário
        List<Activity> activities = repository.findByUserId(userId);

        // Mapear atividades para DTO
        return activities.stream()
                .map(ActivityMapper::toResponseDTO)
                .toList();
    }

    public Double calculateBalance(String userId) {
        // Verificar se o usuário existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Buscar as atividades do usuário
        List<Activity> activities = repository.findByUserId(userId);

        if (activities.isEmpty()) {
            return 0.0;
        }

        // Calcular o saldo
        return activities.stream()
                .mapToDouble(a -> a.getType() == Type.REVENUE
                        ? a.getValue()
                        : -a.getValue())
                .sum();
    }
}

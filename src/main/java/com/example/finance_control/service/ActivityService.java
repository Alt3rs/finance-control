package com.example.finance_control.service;
import com.example.finance_control.domain.activity.Activity;
import com.example.finance_control.domain.category.Category;
import com.example.finance_control.domain.type.Type;
import com.example.finance_control.domain.user.User;
import com.example.finance_control.dto.ActivityRequestDTO;
import com.example.finance_control.dto.ActivityResponseDTO;
import com.example.finance_control.dto.CategoryReportDTO;
import com.example.finance_control.dto.mapper.ActivityMapper;
import com.example.finance_control.exceptions.DatabaseException;
import com.example.finance_control.exceptions.ResourceNotFoundException;
import com.example.finance_control.repository.activity.ActivityRepository;
import com.example.finance_control.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    @Autowired
    private UserRepository userRepository;


    public ActivityResponseDTO insertActivity(ActivityRequestDTO activityRequestDTO) {
        User user = userRepository.findById(activityRequestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Activity activity = ActivityMapper.toEntity(activityRequestDTO, user);

        Activity savedActivity = repository.save(activity);

        return ActivityMapper.toResponseDTO(savedActivity);
    }
    
    public void removeActivity(String id, String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Activity activity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));

        if (!activity.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("This activity does not belong to the user.");
        }

        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public ActivityResponseDTO updateActivity(String id, ActivityRequestDTO activityRequestDTO) {

        Activity existingActivity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        User user = userRepository.findById(activityRequestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingActivity.setDate(activityRequestDTO.date());
        existingActivity.setDescription(activityRequestDTO.description());
        existingActivity.setValue(activityRequestDTO.value());
        existingActivity.setType(activityRequestDTO.type());
        existingActivity.setCategory(activityRequestDTO.category());
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

    public List<ActivityResponseDTO> getActivitiesByType(String userId, Type type) {
        List<Activity> activities = repository.findByUserIdAndType(userId, type);
        return activities.stream()
                .map(ActivityResponseDTO::new)
                .toList();
    }

    public List<ActivityResponseDTO> getActivitiesByCategory(String userId, Category category) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Activity> activities = repository.findByUserIdAndCategory(userId, category);
        return activities.stream()
                .map(ActivityMapper::toResponseDTO)
                .toList();
    }

    public List<ActivityResponseDTO> getActivitiesByCategoryAndType(String userId, Category category, Type type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Activity> activities = repository.findByUserIdAndCategoryAndType(userId, category, type);
        return activities.stream()
                .map(ActivityMapper::toResponseDTO)
                .toList();
    }

    public List<ActivityResponseDTO> getActivitiesByDateRange(String userId, Instant startDate, Instant endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Activity> activities = repository.findByUserIdAndDateBetween(userId, startDate, endDate);
        return activities.stream()
                .map(ActivityMapper::toResponseDTO)
                .toList();
    }

    public List<ActivityResponseDTO> getActivitiesByCategoryAndDateRange(
            String userId, Category category, Instant startDate, Instant endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Activity> activities = repository.findByUserIdAndCategoryAndDateBetween(
                userId, category, startDate, endDate);
        return activities.stream()
                .map(ActivityMapper::toResponseDTO)
                .toList();
    }

    // Relatório por categorias
    public List<CategoryReportDTO> getCategoryReport(String userId, Type type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Object[]> results = repository.findSumByUserIdAndTypeGroupByCategory(userId, type);

        return results.stream()
                .map(result -> new CategoryReportDTO(
                        (Category) result[0],
                        (Double) result[1]
                ))
                .toList();
    }

    // Balanço por categoria
    public Map<Category, Double> getBalanceByCategory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Activity> activities = repository.findByUserId(userId);

        return activities.stream()
                .collect(Collectors.groupingBy(
                        Activity::getCategory,
                        Collectors.summingDouble(a ->
                                a.getType() == Type.REVENUE ? a.getValue() : -a.getValue())
                ));
    }
}

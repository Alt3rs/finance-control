package com.example.finance_control.repository.activity;


import com.example.finance_control.domain.activity.Activity;
import com.example.finance_control.domain.type.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    // Adicionar metodo para buscar atividades por usuário
    List<Activity> findByUserId(String userId);
    List<Activity> findByUserIdAndType(String userId, Type type);
}

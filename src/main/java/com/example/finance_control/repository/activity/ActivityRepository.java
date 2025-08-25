package com.example.finance_control.repository.activity;


import com.example.finance_control.domain.activity.Activity;
import com.example.finance_control.domain.category.Category;
import com.example.finance_control.domain.type.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    // Adicionar metodo para buscar atividades por usuário
    List<Activity> findByUserId(String userId);
    List<Activity> findByUserIdAndType(String userId, Type type);

    List<Activity> findByUserIdAndCategory(String userId, Category category);

    List<Activity> findByUserIdAndCategoryAndType(String userId, Category category, Type type);

    List<Activity> findByUserIdAndDateBetween(String userId, Instant startDate, Instant endDate);

    List<Activity> findByUserIdAndCategoryAndDateBetween(
            String userId, Category category, Instant startDate, Instant endDate);

    @Query("SELECT a FROM Activity a WHERE a.user.id = :userId AND a.category IN :categories")
    List<Activity> findByUserIdAndCategoryIn(@Param("userId") String userId,
                                             @Param("categories") List<Category> categories);

    // Para relatórios por categoria
    @Query("SELECT a.category, SUM(a.value) FROM Activity a WHERE a.user.id = :userId AND a.type = :type GROUP BY a.category")
    List<Object[]> findSumByUserIdAndTypeGroupByCategory(@Param("userId") String userId,
                                                         @Param("type") Type type);
}

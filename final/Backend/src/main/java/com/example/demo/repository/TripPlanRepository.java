package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.TripPlan.TripPlanSummaryDto;
import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.User;

@Repository
public interface TripPlanRepository extends JpaRepository<TripPlan, Integer> {
    @Query("SELECT new com.example.demo.model.dto.TripPlan.TripPlanSummaryDto(t.id, t.planName, t.plan) FROM TripPlan t "
    + "where t.user=:user")
    Page<TripPlanSummaryDto> findAllByUser(@Param("user") User user, Pageable pageable);
    
    // 특정 사용자의 여행 계획을 계획명으로 검색(옵션)
    List<TripPlan> findByUserAndPlanNameContaining(User user, String planKeyword);
}
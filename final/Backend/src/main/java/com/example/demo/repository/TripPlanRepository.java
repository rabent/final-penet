package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.User;

@Repository
public interface TripPlanRepository extends JpaRepository<TripPlan, Integer> {
    // 특정 사용자의 모든 여행 계획을 조회
    List<TripPlan> findByUser(User user);
    
    // 특정 사용자의 여행 계획을 계획명으로 검색
    List<TripPlan> findByUserAndPlanContaining(User user, String planKeyword);
}
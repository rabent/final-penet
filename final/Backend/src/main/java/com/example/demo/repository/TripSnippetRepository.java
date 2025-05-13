package com.example.demo.repository;

import com.example.demo.model.dto.TPsnippet.TPsnippetSummaryDto;
import com.example.demo.model.entity.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.TripSnippet;
import java.util.List;

@Repository
public interface TripSnippetRepository extends JpaRepository<TripSnippet, Integer> {
    // 특정 plan_id에 해당하는 모든 스니펫 조회
    List<TripSnippet> findByPlan(TripPlan plan);

    @Query("SELECT new com.example.demo.model.dto.TPsnippet.TPsnippetSummaryDto(t.id, a.title, t.price) " +
       "FROM TripSnippet t JOIN t.attraction a WHERE t.plan = :plan")
    List<TPsnippetSummaryDto> getTripSnippetSummaryById(@Param("plan") TripPlan plan);
}
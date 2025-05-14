package com.example.demo.service;

import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetRequestDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetResponseDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetUpdateDto;
import com.example.demo.model.dto.TripPlan.TripPlanRequestDto;
import com.example.demo.model.dto.TripPlan.TripPlanResponseDto;
import com.example.demo.model.dto.TripPlan.TripPlanUpdateDto;
import com.example.demo.model.entity.Attraction;
import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.TripSnippet;
import com.example.demo.model.entity.User;
import com.example.demo.model.dto.TripPlan.TripPlanSummaryDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetSummaryDto;
import com.example.demo.repository.AttractionRepository;
import com.example.demo.repository.TripPlanRepository;
import com.example.demo.repository.TripSnippetRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TripPlanService {

    private final TripPlanRepository tripPlanRepository;
    private final TripSnippetRepository tripSnippetRepository;
    private final UserRepository userRepository;
    private final AttractionRepository attractionRepository;

    private static final int DEFAULT_PAGE_SIZE = 8;

    // TripPlan 관련 메소드

    /**
     * 사용자별 여행 계획 목록을 페이지네이션하여 조회
     */
    public Page<TripPlanSummaryDto> getUserTripPlans(Integer userId, int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("id").descending());
        User user=userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return tripPlanRepository.findAllByUser(user, pageable);
    }

    /**
     * 여행 계획 저장
     */
    public TripPlanResponseDto saveTripPlan(TripPlanRequestDto dto, Integer userId) {
        TripPlan tripPlan=dto.toEntity();
        User user=userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        user.addPlan(tripPlan);
        return TripPlanResponseDto.from(tripPlanRepository.save(tripPlan));
    }

    /**
     * 여행 계획 조회
     */
    public TripPlanWithSnippetsDto getTripPlanById(Integer planId) {
        return getTripPlanWithSnippets(planId);
    }

    /**
     * 여행 계획 수정
     */
    public void UpdateTripPlan(TripPlanUpdateDto dto, Integer id) {
        TripPlan plan=tripPlanRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("여행계획을 찾을 수 없습니다."));
        dto.updateEntity(plan);
    }

    /**
     * 여행 계획 삭제 (연관된 스니펫도 함께 삭제됨 - CascadeType 설정 필요)
     */
    public void deleteTripPlan(Integer planId) {
        tripPlanRepository.deleteById(planId);
    }

    // TripSnippet 관련 메소드

    /**
     * 특정 여행 계획에 속한 스니펫 요약 정보 조회
     */
    public List<TPsnippetSummaryDto> getTripSnippetSummaries(Integer planId) {
        TripPlan plan=tripPlanRepository.findById(planId).orElseThrow(()-> new EntityNotFoundException("여행계획을 찾을 수 없습니다."));
        return tripSnippetRepository.getTripSnippetSummaryById(plan);
    }

    /**
     * 여행 스니펫 저장
     */
    public TPsnippetResponseDto saveTripSnippet(TPsnippetRequestDto dto, Integer no, Integer planId) {
        TripSnippet snippet=dto.toEntity();
        TripPlan tripPlan=tripPlanRepository.findById(planId).orElseThrow(()-> new EntityNotFoundException("여행계획을 찾을 수 없습니다."));
        Attraction attraction=attractionRepository.findById(no).orElseThrow(()-> new EntityNotFoundException("관광지를 찾을 수 없습니다."));
        snippet.setAttraction(attraction);
        tripPlan.addSnippet(snippet); snippet.setPlan(tripPlan);
        tripSnippetRepository.save(snippet);
        AttractionSummaryDto attractionSummaryDto=AttractionSummaryDto.from(attraction);
        return new TPsnippetResponseDto(dto.getPrice(), dto.getSchedule(), attractionSummaryDto);
    }

    /**
     * 여행 스니펫 삭제
     */
    public void deleteTripSnippet(Integer snippetId) {
        tripSnippetRepository.deleteById(snippetId);
    }

    /**
     * 여행 스니펫 수정
     */
    public void UpdateTripSnippet(TPsnippetUpdateDto dto, Integer no, Integer id) {
        TripSnippet snippet=tripSnippetRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("계획 스니펫이 없습니다"));
        dto.updateEntity(snippet);
        if(no!=null) {
            Attraction attraction=attractionRepository.findById(no).orElseThrow(()-> new EntityNotFoundException("관광지를 찾을 수 없습니다."));
            snippet.setAttraction(attraction);
        }
    }

    /**
     * 여행 계획과 그에 속한 스니펫 정보를 함께 조회
     */
    public TripPlanWithSnippetsDto getTripPlanWithSnippets(Integer planId) {
        Optional<TripPlan> planOptional = tripPlanRepository.findById(planId);

        if (planOptional.isPresent()) {
            TripPlan plan = planOptional.get();
            List<TPsnippetSummaryDto> snippets = tripSnippetRepository.getTripSnippetSummaryById(plan);

            return new TripPlanWithSnippetsDto(plan, snippets);
        }

        return null;
    }

    /**
     * 계획과 스니펫을 함께 담는 DTO 클래스 (내부 클래스로 정의)
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class TripPlanWithSnippetsDto {
        private final TripPlan plan;
        private final List<TPsnippetSummaryDto> snippets;

        public TripPlan getPlan() {
            return plan;
        }

        public List<TPsnippetSummaryDto> getSnippets() {
            return snippets;
        }
    }
}
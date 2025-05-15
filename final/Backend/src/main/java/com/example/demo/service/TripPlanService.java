package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetRequestDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetResponseDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetSummaryDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetUpdateDto;
import com.example.demo.model.dto.TripPlan.TripPlanRequestDto;
import com.example.demo.model.dto.TripPlan.TripPlanResponseDto;
import com.example.demo.model.dto.TripPlan.TripPlanSummaryDto;
import com.example.demo.model.dto.TripPlan.TripPlanUpdateDto;
import com.example.demo.model.entity.Attraction;
import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.TripSnippet;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AttractionRepository;
import com.example.demo.repository.TripPlanRepository;
import com.example.demo.repository.TripSnippetRepository;
import com.example.demo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
    public TripPlanWithSnippetsDto saveTripPlan(TripPlanRequestDto dto, Integer userId) {
        TripPlan tripPlan=dto.toEntity();
        User user=userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        user.addPlan(tripPlan);
        Integer id=tripPlanRepository.save(tripPlan).getId();
        return getTripPlanWithSnippets(id);
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
    public TripPlanWithSnippetsDto UpdateTripPlan(TripPlanUpdateDto dto, Integer id) {
        TripPlan plan=tripPlanRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("여행계획을 찾을 수 없습니다."));
        dto.updateEntity(plan);
        return getTripPlanWithSnippets(plan.getId());
    }

    /**
     * 여행 계획 삭제 (연관된 스니펫도 함께 삭제됨 - CascadeType 설정 필요)
     */
    public void deleteTripPlan(Integer planId) {
        TripPlan plan=tripPlanRepository.findById(planId).orElseThrow(()-> new EntityNotFoundException("여행계획을 찾을 수 없습니다."));
        User user=plan.getUser();
        if(user!=null) {
            user.getPlans().remove(plan);
        }
        plan.setUser(null);
        tripPlanRepository.deleteById(planId);
    }

    // TripSnippet 관련 메소드

    /**
     * 여행 스니펫 저장
     */
    public TPsnippetResponseDto saveTripSnippet(TPsnippetRequestDto dto, Integer planId) {
        TripSnippet snippet=dto.toEntity();
        TripPlan tripPlan=tripPlanRepository.findById(planId).orElseThrow(()-> new EntityNotFoundException("여행계획을 찾을 수 없습니다."));
        Attraction attraction=attractionRepository.findById(dto.getNo()).orElseThrow(()-> new EntityNotFoundException("관광지를 찾을 수 없습니다."));
        snippet.setAttraction(attraction);
        tripPlan.addSnippet(snippet); snippet.setPlan(tripPlan);
        tripSnippetRepository.save(snippet);
        AttractionSummaryDto attractionSummaryDto=AttractionSummaryDto.from(attraction);
        return new TPsnippetResponseDto(dto.getPrice(), dto.getSchedule(), attractionSummaryDto);
    }

    /**
     * 여행 스니펫 조회
     */
    public TPsnippetResponseDto getTripSnippet(Integer snippetId) {
        TripSnippet snippet=tripSnippetRepository.findById(snippetId).orElseThrow(()->new EntityNotFoundException("계획 스니펫이 없습니다"));
        AttractionSummaryDto attractionSummaryDto=AttractionSummaryDto.from(snippet.getAttraction());
        return new TPsnippetResponseDto(snippet.getPrice(), snippet.getSchedule(), attractionSummaryDto);
    }

    /**
     * 여행 스니펫 삭제
     */
    public void deleteTripSnippet(Integer snippetId) {
        TripSnippet snippet=tripSnippetRepository.findById(snippetId).orElseThrow(()->new EntityNotFoundException("계획 스니펫이 없습니다"));
        TripPlan plan=snippet.getPlan();
        if(plan!=null) {
            plan.getSnippets().remove(snippet);
        }
        snippet.setPlan(null);
        tripSnippetRepository.deleteById(snippetId);
    }

    /**
     * 여행 스니펫 수정
     */
    public TPsnippetResponseDto UpdateTripSnippet(TPsnippetUpdateDto dto, Integer snipId) {
        TripSnippet snippet=tripSnippetRepository.findById(snipId).orElseThrow(()-> new EntityNotFoundException("계획 스니펫이 없습니다"));
        snippet.setPrice(dto.getPrice()); snippet.setSchedule(dto.getSchedule());
        if(dto.getNo()!=null) {
            Attraction attraction=attractionRepository.findById(dto.getNo()).orElseThrow(()-> new EntityNotFoundException("관광지를 찾을 수 없습니다."));
            snippet.setAttraction(attraction);
            AttractionSummaryDto attractionSummaryDto=AttractionSummaryDto.from(attraction);
            return new TPsnippetResponseDto(snippet.getPrice(), snippet.getSchedule(), attractionSummaryDto);
        }
        return null;
    }

    /**
     * 여행 계획과 그에 속한 스니펫 정보를 함께 조회
     */
    public TripPlanWithSnippetsDto getTripPlanWithSnippets(Integer planId) {
        Optional<TripPlan> planOptional = tripPlanRepository.findById(planId);

        if (planOptional.isPresent()) {
            TripPlanResponseDto plan = TripPlanResponseDto.from(planOptional.get());
            List<TPsnippetSummaryDto> snippets = tripSnippetRepository.getTripSnippetSummaryById(planOptional.get());

            return new TripPlanWithSnippetsDto(plan, snippets);
        }
        else {
            throw new EntityNotFoundException("여행 계획을 찾을 수 없습니다.");
        }
    }

    /**
     * 계획과 스니펫을 함께 담는 DTO 클래스 (내부 클래스로 정의)
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class TripPlanWithSnippetsDto {
        private final TripPlanResponseDto plan;
        private final List<TPsnippetSummaryDto> snippets;

    }
}
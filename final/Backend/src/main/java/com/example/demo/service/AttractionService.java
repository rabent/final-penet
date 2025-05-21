package com.example.demo.service;

import com.example.demo.model.dto.Attraction.*;
import com.example.demo.model.entity.Attraction;
import com.example.demo.repository.AttractionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 주로 조회 작업이므로 readOnly=true로 설정하여 성능 최적화
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private static final int DEFAULT_PAGE_SIZE = 10;

    //페이징 처리된 모든 관광지 요약 정보를 조회
    public Page<AttractionSummaryDto> getAttractionSummaries(int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("no").descending());
        return attractionRepository.findAttractionSummaries(pageable);
    }

    //조건으로 검색 후 조회(모두 nullable)
    public Page<AttractionSummaryDto> searchAttractions(
            AttractionSearchCriteria searchCriteria,
            int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("no").descending());
        return attractionRepository.searchAttractionSummaries(
                searchCriteria.getAreaCode(),
                searchCriteria.getSigunCode(),
                searchCriteria.getContentTypeId(),
                pageable);
    }

    //관광지 상세 정보 조회
    public AttractionResponseDto getAttractionDetail(Integer no) {
        Optional<Attraction> attraction=attractionRepository.findById(no);
        return AttractionResponseDto.fromEntity(attraction.orElseThrow(() -> new EntityNotFoundException("관광지를 찾을 수 없습니다.")));
    }

    public List<ContentTypeDto> getAllContentTypes() {
        return attractionRepository.findAllContentTypes();
    }

    public List<SidoDto> getAllSidos() {
        return attractionRepository.findAllSidos();
    }

    public List<GugunDto> getAllGuguns() {
        return attractionRepository.findAllGuguns();
    }

//    public Page<AttractionResponseDto> searchAttractions(AttractionSearchRequestDto request, int page) {
//        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("no").descending());
//
//        // 검색 요청에서 필터 값 추출
//        String keyword = request.getKeyword();
//        Integer sidoCode = request.getSidoCode();
//        Integer gugunCode = request.getGugunCode();
//        Integer category = request.getCategory();
//
//        // 빈 문자열을 null로 변환 (JPQL에서 IS NULL 조건을 사용하기 위함)
//        if (keyword != null && keyword.trim().isEmpty()) {
//            keyword = null;
//        }
//
//        // 레포지토리의 검색 메서드 호출
//        Page<Attraction> attractions = attractionRepository.searchByFilters(
//                keyword, sidoCode, gugunCode, category, pageable);
//
//        // 엔티티를 DTO로 변환
//        return attractions.map(AttractionResponseDto::fromEntity);
//    }

    public Page<AttractionResponseDto> searchAttractions(AttractionSearchRequestDto request, int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("no").descending());

        // 검색 요청에서 필터 값 추출
        String keyword = request.getKeyword();
        Integer sidoCode = request.getSidoCode();
        Integer gugunCode = request.getGugunCode();
        Integer category = request.getCategory();

        // 빈 문자열을 null로 변환 (JPQL에서 IS NULL 조건을 사용하기 위함)
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // 레포지토리의 검색 메서드 호출
        Page<Attraction> attractions = attractionRepository.searchByFilters(
                keyword, sidoCode, gugunCode, category, pageable);

        // 엔티티를 DTO로 변환하고 추가 정보 매핑
        return attractions.map(attraction -> {
            AttractionResponseDto dto = AttractionResponseDto.fromEntity(attraction);

            // 컨텐츠 타입 이름 매핑
            if (attraction.getContentTypeId() != null) {
                attractionRepository.findContentTypeById(attraction.getContentTypeId())
                        .ifPresent(ct -> dto.setContentTypeName(ct.getContentTypeName()));
            }

            // 지역(시도) 이름 매핑
            if (attraction.getAreaCode() != null) {
                attractionRepository.findSidoByCode(attraction.getAreaCode())
                        .ifPresent(sido -> dto.setAreaName(sido.getSidoName()));
            }

            // 구군 이름 매핑
            if (attraction.getAreaCode() != null && attraction.getSiGunguCode() != null) {
                attractionRepository.findGugunByCode(attraction.getAreaCode(), attraction.getSiGunguCode())
                        .ifPresent(gugun -> dto.setSiGunguName(gugun.getGugunName()));
            }

            // 전체 주소 설정 (선택 사항)
            String fullAddress = attraction.getAddr1();
            if (attraction.getAddr2() != null && !attraction.getAddr2().isEmpty()) {
                fullAddress += " " + attraction.getAddr2();
            }
            dto.setFullAddress(fullAddress);

            return dto;
        });
    }
}


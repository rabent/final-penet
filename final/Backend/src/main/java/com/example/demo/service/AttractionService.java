package com.example.demo.service;

import com.example.demo.model.dto.Attraction.AttractionResponseDto;
import com.example.demo.model.dto.Attraction.AttractionSearchCriteria;
import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
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
}


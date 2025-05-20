package com.example.demo.controller;

import com.example.demo.model.dto.Attraction.AttractionResponseDto;
import com.example.demo.model.dto.Attraction.AttractionSearchCriteria;
import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    // 모든 관광지 요약 정보 페이징 조회
    @GetMapping
    public ResponseEntity<Page<AttractionSummaryDto>> getAttractions(
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(attractionService.getAttractionSummaries(page));
    }

    // 조건 검색 후 관광지 요약 정보 조회
    @GetMapping("/search")
    public ResponseEntity<Page<AttractionSummaryDto>> searchAttractions(
            @ModelAttribute AttractionSearchCriteria searchCriteria,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(attractionService.searchAttractions(searchCriteria, page));
    }

    // 관광지 상세 정보 조회
    @GetMapping("/{attrId}")
    public ResponseEntity<AttractionResponseDto> getAttractionDetail(@PathVariable Integer attrId) {
        return ResponseEntity.ok(attractionService.getAttractionDetail(attrId));
    }
}
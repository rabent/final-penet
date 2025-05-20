package com.example.demo.controller;

import com.example.demo.model.dto.Attraction.AttractionResponseDto;
import com.example.demo.model.dto.Attraction.AttractionSearchCriteria;
import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.service.AttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attractions")
@RequiredArgsConstructor
@Tag(name = "관광지 API", description = "관광지 정보 조회 관련 API")
public class AttractionController {

    private final AttractionService attractionService;

    // 모든 관광지 요약 정보 페이징 조회
    @GetMapping
    @Operation(summary = "관광지 목록 조회", description = "페이징 처리된 관광지 요약 정보 목록을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Page<AttractionSummaryDto>> getAttractions(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(attractionService.getAttractionSummaries(page));
    }

    // 조건 검색 후 관광지 요약 정보 조회
    @GetMapping("/search")
    @Operation(summary = "관광지 검색", description = "검색 조건에 맞는 관광지 요약 정보 목록을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Page<AttractionSummaryDto>> searchAttractions(
            @Parameter(description = "관광지 검색 조건", required = true)
            @ModelAttribute AttractionSearchCriteria searchCriteria,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(attractionService.searchAttractions(searchCriteria, page));
    }

    // 관광지 상세 정보 조회
    @GetMapping("/{attrId}")
    @Operation(summary = "관광지 상세 조회", description = "특정 관광지의 상세 정보를 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AttractionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "관광지 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<AttractionResponseDto> getAttractionDetail(
            @Parameter(description = "관광지 ID", required = true, example = "1")
            @PathVariable Integer attrId) {
        return ResponseEntity.ok(attractionService.getAttractionDetail(attrId));
    }
}
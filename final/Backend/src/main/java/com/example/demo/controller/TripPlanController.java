package com.example.demo.controller;

import com.example.demo.model.dto.TPsnippet.TPsnippetRequestDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetResponseDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetUpdateDto;
import com.example.demo.model.dto.TripPlan.TripPlanRequestDto;
import com.example.demo.model.dto.TripPlan.TripPlanSummaryDto;
import com.example.demo.model.dto.TripPlan.TripPlanUpdateDto;
import com.example.demo.service.TripPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
@Tag(name = "여행 계획 API", description = "여행 계획 및 세부 일정 관리를 위한 API")
@SecurityRequirement(name = "bearerAuth")
public class TripPlanController {
    private final TripPlanService tripPlanService;

    @GetMapping
    @Operation(summary = "여행 계획 목록 조회", description = "인증된 사용자의 여행 계획 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Page<TripPlanSummaryDto>> planSummary(
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal String userId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(tripPlanService.getUserTripPlans(Integer.valueOf(userId), page));
    }

    @PostMapping
    @Operation(summary = "새 여행 계획 생성", description = "새로운 여행 계획을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = TripPlanService.TripPlanWithSnippetsDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<TripPlanService.TripPlanWithSnippetsDto> planPost(
            @Parameter(description = "여행 계획 생성 정보", required = true)
            @RequestBody @Valid TripPlanRequestDto dto,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(tripPlanService.saveTripPlan(dto, Integer.valueOf(userId)));
    }

    @PutMapping("/{planId}")
    @Operation(summary = "여행 계획 수정", description = "기존 여행 계획을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = TripPlanService.TripPlanWithSnippetsDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "여행 계획 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<TripPlanService.TripPlanWithSnippetsDto> planUpdate(
            @Parameter(description = "여행 계획 수정 정보", required = true)
            @RequestBody @Valid TripPlanUpdateDto dto,
            @Parameter(description = "수정할 여행 계획 ID", required = true, example = "1")
            @PathVariable Integer planId) {
        return ResponseEntity.ok(tripPlanService.UpdateTripPlan(dto, planId));
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "여행 계획 삭제", description = "여행 계획을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "여행 계획 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> planDelete(
            @Parameter(description = "삭제할 여행 계획 ID", required = true, example = "1")
            @PathVariable Integer planId) {
        tripPlanService.deleteTripPlan(planId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{planId}")
    @Operation(summary = "여행 계획 상세 조회", description = "여행 계획의 상세 정보와 세부 일정을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TripPlanService.TripPlanWithSnippetsDto.class))),
            @ApiResponse(responseCode = "404", description = "여행 계획 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<TripPlanService.TripPlanWithSnippetsDto> planDetail(
            @Parameter(description = "조회할 여행 계획 ID", required = true, example = "1")
            @PathVariable Integer planId) {
        return ResponseEntity.ok(tripPlanService.getTripPlanById(planId));
    }

    @GetMapping("/{planId}/{snipId}")
    @Operation(summary = "세부 일정 조회", description = "여행 계획의 특정 세부 일정을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TPsnippetResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "세부 일정 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<TPsnippetResponseDto> snippetDetail(
            @Parameter(description = "여행 계획 ID", required = true, example = "1")
            @PathVariable Integer planId,
            @Parameter(description = "세부 일정 ID", required = true, example = "1")
            @PathVariable Integer snipId) {
        return ResponseEntity.ok(tripPlanService.getTripSnippet(snipId));
    }

    @PutMapping("/{planId}/{snipId}")
    @Operation(summary = "세부 일정 수정", description = "여행 계획의 특정 세부 일정을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = TPsnippetResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "세부 일정 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<TPsnippetResponseDto> snippetUpdate(
            @Parameter(description = "세부 일정 수정 정보", required = true)
            @RequestBody @Valid TPsnippetUpdateDto dto,
            @Parameter(description = "여행 계획 ID", required = true, example = "1")
            @PathVariable Integer planId,
            @Parameter(description = "수정할 세부 일정 ID", required = true, example = "1")
            @PathVariable Integer snipId) {
        return ResponseEntity.ok(tripPlanService.UpdateTripSnippet(dto, snipId));
    }

    @PostMapping("/{planId}")
    @Operation(summary = "새 세부 일정 추가", description = "여행 계획에 새 세부 일정을 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공",
                    content = @Content(schema = @Schema(implementation = TPsnippetResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "여행 계획 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<TPsnippetResponseDto> snippetPost(
            @Parameter(description = "세부 일정 생성 정보", required = true)
            @RequestBody @Valid TPsnippetRequestDto dto,
            @Parameter(description = "여행 계획 ID", required = true, example = "1")
            @PathVariable Integer planId) {
        return ResponseEntity.ok(tripPlanService.saveTripSnippet(dto, planId));
    }

    @DeleteMapping("/{planId}/{snipId}")
    @Operation(summary = "세부 일정 삭제", description = "여행 계획의 특정 세부 일정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "세부 일정 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> snippetDelete(
            @Parameter(description = "여행 계획 ID", required = true, example = "1")
            @PathVariable Integer planId,
            @Parameter(description = "삭제할 세부 일정 ID", required = true, example = "1")
            @PathVariable Integer snipId) {
        tripPlanService.deleteTripSnippet(snipId);
        return ResponseEntity.noContent().build();
    }
}
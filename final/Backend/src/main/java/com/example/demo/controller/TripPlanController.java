package com.example.demo.controller;

import com.example.demo.model.dto.TPsnippet.TPsnippetRequestDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetResponseDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetUpdateDto;
import com.example.demo.model.dto.TripPlan.TripPlanRequestDto;
import com.example.demo.model.dto.TripPlan.TripPlanResponseDto;
import com.example.demo.model.dto.TripPlan.TripPlanSummaryDto;
import com.example.demo.model.dto.TripPlan.TripPlanUpdateDto;
import com.example.demo.service.TripPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripPlanController {
    private final TripPlanService tripPlanService;
    //jwt token에서  user id 파싱 필요
    @GetMapping
    public ResponseEntity<Page<TripPlanSummaryDto>> planSummary(Integer userId, @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(tripPlanService.getUserTripPlans(userId, page));
    }

    @PostMapping
    public ResponseEntity<TripPlanService.TripPlanWithSnippetsDto> planPost(@RequestBody @Valid TripPlanRequestDto dto, Integer userId) {
        return ResponseEntity.ok(tripPlanService.saveTripPlan(dto, userId));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<TripPlanService.TripPlanWithSnippetsDto> planUpdate(@RequestBody @Valid TripPlanUpdateDto dto, @PathVariable Integer planId) {
        return ResponseEntity.ok(tripPlanService.UpdateTripPlan(dto,planId));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<?> planDelete(@PathVariable Integer planId) {
        tripPlanService.deleteTripPlan(planId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{planId}")
    public ResponseEntity<TripPlanService.TripPlanWithSnippetsDto> planDetail(@PathVariable Integer planId) {
        return ResponseEntity.ok(tripPlanService.getTripPlanById(planId));
    }

    @GetMapping("/{planId}/{snipId}")
    public ResponseEntity<TPsnippetResponseDto> snippetDetail(@PathVariable Integer planId, @PathVariable Integer snipId) {
        return ResponseEntity.ok(tripPlanService.getTripSnippet(snipId));
    }

    @PutMapping("/{planId}/{snipId}")
    public ResponseEntity<TPsnippetResponseDto> snippetUpdate(@RequestBody @Valid TPsnippetUpdateDto dto, @PathVariable Integer planId, @PathVariable Integer snipId) {
        return ResponseEntity.ok(tripPlanService.UpdateTripSnippet(dto,snipId));
    }

    @PostMapping("/{planId}")
    public ResponseEntity<TPsnippetResponseDto> snippetPost(@RequestBody @Valid TPsnippetRequestDto dto, @PathVariable Integer planId) {
        return ResponseEntity.ok(tripPlanService.saveTripSnippet(dto, planId));
    }

    @DeleteMapping("/{planId}/{snipId}")
    public ResponseEntity<?> snippetDelete(@PathVariable Integer planId, @PathVariable Integer snipId) {
        tripPlanService.deleteTripSnippet(snipId);
        return ResponseEntity.noContent().build();
    }


}

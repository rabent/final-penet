package com.example.demo.model.dto;

import java.util.List;

import com.example.demo.model.entity.TripPlan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripPlanResponseDto {
    private String planName;

    private String plan;

    private List<TPsnippetSummaryDto> snippets;

    public static TripPlanResponseDto from(TripPlan plan) { //snippetRepository에서 snippetsummary를 받아 추가해줘야 함
        return new TripPlanResponseDto(plan.getPlanName(), plan.getPlan(),null);
    }
}

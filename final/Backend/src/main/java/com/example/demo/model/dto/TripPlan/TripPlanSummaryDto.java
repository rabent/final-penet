package com.example.demo.model.dto.TripPlan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanSummaryDto {
    private Integer id;

    private String planName;

    private String plan;

    private String startDate;

    private String endDate;

    private String location;

    private Integer budget=0;
}

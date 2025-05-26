package com.example.demo.model.dto.TripPlan;

import com.example.demo.model.entity.TripPlan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripPlanUpdateDto {
    @Size(max=50)
    private String planName;

    @Size(max=100)
    private String plan;

    private String startDate;

    private String endDate;

    private String location;

    public void updateEntity(TripPlan tripPlan) {
        if (this.planName != null) {
            tripPlan.setPlanName(this.planName);
        }
        if (this.plan != null) {
            tripPlan.setPlan(this.plan);
        }
        if(this.startDate != null) {
            tripPlan.setStartDate(this.startDate);
        }
        if(this.endDate != null) {
            tripPlan.setEndDate(this.endDate);
        }
        if(this.location != null) {
            tripPlan.setLocation(this.location);
        }
    }
}

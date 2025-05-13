package com.example.demo.model.dto.TripPlan;

import com.example.demo.model.entity.TripPlan;

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

    @Size(max=200)
    private String plan;

    public void updateEntity(TripPlan tripPlan) {
        if (this.planName != null) {
            tripPlan.setPlanName(this.planName);
        }
        if (this.plan != null) {
            tripPlan.setPlan(this.plan);
        }
    }
}

package com.example.demo.model.dto;

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
}

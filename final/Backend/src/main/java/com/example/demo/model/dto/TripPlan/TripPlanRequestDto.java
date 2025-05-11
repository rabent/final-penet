package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanRequestDto {
    @NotBlank(message="계획 제목을 입력해주세요")
    private String planName;

    @NotBlank(message="계획 내용을 입력해주세요")
    private String plan;
}

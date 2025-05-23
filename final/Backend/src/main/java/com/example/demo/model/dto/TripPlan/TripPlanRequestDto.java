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
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanRequestDto { //snippet은 서비스단에서 추가
    @NotBlank(message="계획 제목을 입력해주세요")
    @Size(max=50)
    private String planName;

    @NotBlank(message="계획 내용을 입력해주세요")
    @Size(max=100)
    private String plan;

    @NotBlank(message="시작일을 입력해주세요")
    private String startDate;

    @NotBlank(message="종료일을 입력해주세요")
    private String endDate;

    @NotBlank(message="지역을 입력해주세요")
    private String location;

    public TripPlan toEntity() {
        return TripPlan.builder()
        .planName(this.planName)
        .plan(this.plan)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .location(this.location)
        .build();
    }
}

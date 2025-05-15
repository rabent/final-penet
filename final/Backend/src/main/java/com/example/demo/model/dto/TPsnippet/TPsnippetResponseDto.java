package com.example.demo.model.dto.TPsnippet;

import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.entity.Attraction;
import com.example.demo.model.entity.TripSnippet;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TPsnippetResponseDto {
    private String price;

    private String schedule;

    private AttractionSummaryDto attraction;

    //service 단에서 attraction을 dto로 변환해서 넘겨주는 로직 필요
}

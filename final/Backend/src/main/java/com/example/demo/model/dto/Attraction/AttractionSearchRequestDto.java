package com.example.demo.model.dto.Attraction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttractionSearchRequestDto {
    private String keyword;
    private Integer sidoCode;
    private Integer gugunCode;
    private Integer category;
}

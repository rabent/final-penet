package com.example.demo.model.dto.TPsnippet;


import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TPsnippetSummaryDto {
    private Integer id;

    private String attractionName;

    private String price;

    private AttractionSummaryDto attraction;
}

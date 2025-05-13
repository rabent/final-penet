package com.example.demo.model.dto.Attraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttractionSummaryDto {
    private Integer no;

    private String title;

    private String firstImage1;

    private String addr1;

    private String tel;
}

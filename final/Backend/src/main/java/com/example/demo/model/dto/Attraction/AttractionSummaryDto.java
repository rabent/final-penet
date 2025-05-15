package com.example.demo.model.dto.Attraction;

import com.example.demo.model.entity.Attraction;
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

    public static AttractionSummaryDto from(Attraction attraction) {
        return new AttractionSummaryDto(attraction.getNo(),
                attraction.getTitle(),
                attraction.getFirstImage1(),
                attraction.getAddr1(),
                attraction.getTel());
    }
}

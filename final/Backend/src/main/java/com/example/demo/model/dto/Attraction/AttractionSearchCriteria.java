package com.example.demo.model.dto.Attraction;


//관광지 검색 조건을 담는 DTO 클래스

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttractionSearchCriteria {
    private Integer areaCode;
    private Integer sigunCode;
    private Integer contentTypeId;
}
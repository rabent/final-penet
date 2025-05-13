package com.example.demo.model.dto.Attraction;

import com.example.demo.model.entity.Attraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관광지 상세 정보를 표현하기 위한 ResponseDTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttractionResponseDto {
    
    private Integer no;
    private Integer contentId;
    private Integer contentTypeId;
    private String contentTypeName; // 컨텐츠 타입 이름 (예: 관광지, 음식점, 숙박 등)(옵션)
    
    // 지역 정보
    private Integer areaCode;
    private String areaName; // 지역명 (예: 서울, 부산 등)(옵션)
    private Integer siGunguCode;
    private String siGunguName; // 시군구명(옵션)
    
    private String title;
    
    // 이미지 정보
    private String firstImage1;
    private String firstImage2;
    
    // 위치 정보
    private Double latitude;
    private Double longitude;
    private Integer mapLevel;
    
    // 연락처 및 주소 정보
    private String tel;
    private String addr1;
    private String addr2;
    private String fullAddress; // 전체 주소 (addr1 + addr2)(옵션)
    
    // 웹사이트 및 개요
    private String homePage;
    private String overview;
    
    /**
     * Attraction 엔티티를 AttractionDetailResponseDto로 변환하는 정적 메서드
     * 
     * @param attraction 변환할 Attraction 엔티티
     * @return 변환된 AttractionDetailResponseDto 객체
     */
    public static AttractionResponseDto fromEntity(Attraction attraction) {
        return AttractionResponseDto.builder()
                .no(attraction.getNo())
                .contentId(attraction.getContentId())
                .contentTypeId(attraction.getContentTypeId())
                .areaCode(attraction.getAreaCode())
                .siGunguCode(attraction.getSiGunguCode())
                .title(attraction.getTitle())
                .firstImage1(attraction.getFirstImage1())
                .firstImage2(attraction.getFirstImage2())
                .latitude(attraction.getLatitude())
                .longitude(attraction.getLongitude())
                .mapLevel(attraction.getMapLevel())
                .tel(attraction.getTel())
                .addr1(attraction.getAddr1())
                .addr2(attraction.getAddr2())
                .homePage(attraction.getHomePage())
                .overview(attraction.getOverview())
                .build();
    }
}
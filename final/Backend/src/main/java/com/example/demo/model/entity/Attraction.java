package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attraction {
    //select만 하는 테이블이니 attraction까지만 entity로 만들고
    //나머지는 JPQL로 처리하는 방안 생각중

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Integer no;
    
    @Column(name = "content_type_id")
    private Integer contentTypeId;
    
    @Column(name = "si_gun_gu_code")
    private Integer siGunguCode;
    
    @Column(name = "area_code")
    private Integer areaCode;

    @Column(name = "content_id")
    private Integer contentId;

    @Column(name = "title", length = 500, nullable = false)
    private String title;

    @Column(name = "first_image1", length = 100, nullable = true)
    private String firstImage1;

    @Column(name = "first_image2", length = 100, nullable = true)
    private String firstImage2;

    @Column(name = "map_level")
    private Integer mapLevel;
    
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "tel", length = 20,    nullable = true)
    private String tel;

    @Column(name = "addr1", length = 100, nullable = false)
    private String addr1;

    @Column(name = "addr2", length = 100, nullable = true)
    private String addr2;

    @Column(name = "homepage", length = 1000, nullable = true)
    private String homePage;

    @Column(name = "overview", length = 10000, nullable = true)
    private String overview;
}
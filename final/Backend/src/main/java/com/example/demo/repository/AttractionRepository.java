package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.entity.Attraction;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Integer> {
     /**
     * 페이징 처리된 관광지 요약 정보를 조회하는 메서드
     */
    @Query("SELECT new com.example.demo.model.dto.Attraction.AttractionSummaryDto(a.title, a.firstImage1, a.addr1, a.tel) FROM Attraction a")
    Page<AttractionSummaryDto> findAttractionSummaries(Pageable pageable);
    
    @Query("SELECT new com.example.demo.model.dto.Attraction.AttractionSummaryDto(a.title, a.firstImage1, a.addr1, a.tel) " +
           "FROM Attraction a " +
           "WHERE (:areaCode IS NULL OR a.areaCode = :areaCode) " +
           "AND (:sigunCode IS NULL OR a.siGunguCode = :sigunCode) " +
           "AND (:contentTypeId IS NULL OR a.contentTypeId = :contentTypeId) "
           )
    Page<AttractionSummaryDto> searchAttractionSummaries(
            @Param("areaCode") Integer areaCode,
            @Param("sigunCode") String sigunCode,
            @Param("contentTypeId") Integer contentTypeId,
            Pageable pageable);
}
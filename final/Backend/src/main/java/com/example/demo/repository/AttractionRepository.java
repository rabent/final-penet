package com.example.demo.repository;

import com.example.demo.model.dto.Attraction.ContentTypeDto;
import com.example.demo.model.dto.Attraction.GugunDto;
import com.example.demo.model.dto.Attraction.SidoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.entity.Attraction;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Integer> {
     /**
     * 페이징 처리된 관광지 요약 정보를 조회하는 메서드
     */
    @Query("SELECT new com.example.demo.model.dto.Attraction.AttractionSummaryDto(a.no, a.title, a.firstImage1, a.addr1, a.tel) FROM Attraction a")
    Page<AttractionSummaryDto> findAttractionSummaries(Pageable pageable);
    
    @Query("SELECT new com.example.demo.model.dto.Attraction.AttractionSummaryDto(a.no, a.title, a.firstImage1, a.addr1, a.tel) " +
           "FROM Attraction a " +
           "WHERE (:areaCode IS NULL OR a.areaCode = :areaCode) " +
           "AND (:sigunCode IS NULL OR a.siGunguCode = :sigunCode) " +
           "AND (:contentTypeId IS NULL OR a.contentTypeId = :contentTypeId) "
           )
    Page<AttractionSummaryDto> searchAttractionSummaries(
            @Param("areaCode") Integer areaCode,
            @Param("sigunCode") Integer sigunCode,
            @Param("contentTypeId") Integer contentTypeId,
            Pageable pageable);

    @Query(value = "SELECT content_type_id AS contentTypeId, content_type_name AS contentTypeName FROM contenttypes", nativeQuery = true)
    List<ContentTypeDto> findAllContentTypes();

    @Query(value = "SELECT sido_code AS sidoCode, sido_name AS sidoName FROM sidos", nativeQuery = true)
    List<SidoDto> findAllSidos();

    @Query(value = "SELECT sido_code AS sidoCode, gugun_code AS gugunCode, gugun_name AS gugunName FROM guguns", nativeQuery = true)
    List<GugunDto> findAllGuguns();

    @Query(value = "SELECT content_type_id AS contentTypeId, content_type_name AS contentTypeName FROM contenttypes WHERE content_type_id = :contentTypeId", nativeQuery = true)
    Optional<ContentTypeDto> findContentTypeById(@Param("contentTypeId") Integer contentTypeId);

    @Query(value = "SELECT sido_code AS sidoCode, sido_name AS sidoName FROM sidos WHERE sido_code = :sidoCode", nativeQuery = true)
    Optional<SidoDto> findSidoByCode(@Param("sidoCode") Integer sidoCode);

    @Query(value = "SELECT sido_code AS sidoCode, gugun_code AS gugunCode, gugun_name AS gugunName FROM guguns WHERE sido_code = :sidoCode AND gugun_code = :gugunCode", nativeQuery = true)
    Optional<GugunDto> findGugunByCode(@Param("sidoCode") Integer sidoCode, @Param("gugunCode") Integer gugunCode);

    @Query("SELECT a FROM Attraction a WHERE " +
            "(:keyword IS NULL OR a.title LIKE %:keyword% OR a.addr1 LIKE %:keyword% OR a.addr2 LIKE %:keyword%) AND " +
            "(:sidoCode IS NULL OR a.areaCode = :sidoCode) AND " +
            "(:gugunCode IS NULL OR a.siGunguCode = :gugunCode) AND " +
            "(:category IS NULL OR a.contentTypeId = :category)")
    Page<Attraction> searchByFilters(
            @Param("keyword") String keyword,
            @Param("sidoCode") Integer sidoCode,
            @Param("gugunCode") Integer gugunCode,
            @Param("category") Integer category,
            Pageable pageable);
}
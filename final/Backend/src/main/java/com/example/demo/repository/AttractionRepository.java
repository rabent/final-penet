package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Attraction;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Integer> {
    // 지역 코드로 관광지 검색
    List<Attraction> findByAreaCode(Integer areaCode);
    
    // 시군구 코드로 관광지 검색
    List<Attraction> findBySiGunguCode(Integer siGunguCode);
    
    // 컨텐츠 타입별 검색
    List<Attraction> findByContentTypeId(Integer contentTypeId);
    
    // 지역과 컨텐츠 타입 조합으로 검색
    List<Attraction> findByAreaCodeAndContentTypeId(Integer areaCode, Integer contentTypeId);
    
    // 제목으로 검색 (부분 일치)
    List<Attraction> findByTitleContaining(String keyword);
    
    // 특정 위치 반경 내 관광지 검색 (JPQL 사용)
    @Query("SELECT a FROM Attraction a " +
           "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(a.latitude)))) <= :distance")
    List<Attraction> findNearbyAttractions(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("distance") Double distanceInKm
    );
}
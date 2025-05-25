package com.example.demo.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.entity.Attraction;

@DataJpaTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AttractionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AttractionRepository attractionRepository;

    // 테스트용 데이터
    private Attraction attraction1;
    private Attraction attraction2;
    private Attraction attraction3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        attraction1 = Attraction.builder()
                .title("서울 타워")
                .contentTypeId(12) // 관광지
                .siGunguCode(1) // 강남구
                .areaCode(1) // 서울
                .contentId(1001)
                .firstImage1("http://example.com/image1.jpg")
                .firstImage2("http://example.com/image1_thumb.jpg")
                .mapLevel(6)
                .latitude(37.551254)
                .longitude(126.988407)
                .tel("02-123-4567")
                .addr1("서울특별시 중구 남산동")
                .addr2("남산공원")
                .homePage("http://www.seoultower.co.kr")
                .overview("서울의 상징적인 타워입니다.")
                .build();

        attraction2 = Attraction.builder()
                .title("부산 해운대 해수욕장")
                .contentTypeId(12) // 관광지
                .siGunguCode(5) // 해운대구
                .areaCode(6) // 부산
                .contentId(1002)
                .firstImage1("http://example.com/image2.jpg")
                .firstImage2("http://example.com/image2_thumb.jpg")
                .mapLevel(6)
                .latitude(35.158538)
                .longitude(129.160309)
                .tel("051-987-6543")
                .addr1("부산광역시 해운대구 해운대해변로")
                .addr2("해운대구")
                .homePage("http://www.haeundae.go.kr")
                .overview("부산의 유명한 해변입니다.")
                .build();

        attraction3 = Attraction.builder()
                .title("경복궁")
                .contentTypeId(14) // 문화시설
                .siGunguCode(2) // 종로구
                .areaCode(1) // 서울
                .contentId(1003)
                .firstImage1("http://example.com/image3.jpg")
                .firstImage2("http://example.com/image3_thumb.jpg")
                .mapLevel(6)
                .latitude(37.579617)
                .longitude(126.977041)
                .tel("02-3700-3900")
                .addr1("서울특별시 종로구 사직로 161")
                .addr2("세종로")
                .homePage("http://www.royalpalace.go.kr")
                .overview("조선왕조의 법궁입니다.")
                .build();

        // TestEntityManager를 사용하여 테스트 데이터 저장
        entityManager.persist(attraction1);
        entityManager.persist(attraction2);
        entityManager.persist(attraction3);
        entityManager.flush();
    }

    @Test
    @DisplayName("모든 관광지 요약 정보를 페이징하여 조회")
    void findAttractionSummaries() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));

        // when
        Page<AttractionSummaryDto> result = attractionRepository.findAttractionSummaries(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("경복궁"); // 이름순 정렬
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("부산 해운대 해수욕장");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("서울 타워");
        
        // DTO의 필드 검증
        AttractionSummaryDto firstDto = result.getContent().get(0);
        assertThat(firstDto.getTitle()).isEqualTo("경복궁");
        assertThat(firstDto.getFirstImage1()).isEqualTo("http://example.com/image3.jpg");
        assertThat(firstDto.getAddr1()).isEqualTo("서울특별시 종로구 사직로 161");
        assertThat(firstDto.getTel()).isEqualTo("02-3700-3900");
    }

    @Test
    @DisplayName("지역 코드로 관광지 필터링 테스트")
    void searchAttractionSummariesByAreaCode() {
        // given
        Integer areaCode = 1; // 서울
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AttractionSummaryDto> result = attractionRepository.searchAttractionSummaries(
                areaCode, null, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2); // 서울에 있는 관광지 2개
        
        // 서울에 있는 관광지 이름 확인
        List<String> attractionNames = result.getContent().stream()
                .map(AttractionSummaryDto::getTitle)
                .toList();
        assertThat(attractionNames).containsExactlyInAnyOrder("서울 타워", "경복궁");
    }

    @Test
    @DisplayName("컨텐츠 타입으로 관광지 필터링 테스트")
    void searchAttractionSummariesByContentType() {
        // given
        Integer contentTypeId = 14; // 문화시설
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AttractionSummaryDto> result = attractionRepository.searchAttractionSummaries(
                null, null, contentTypeId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1); // 문화시설 1개
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("경복궁");
    }

    @Test
    @DisplayName("시군구 코드로 관광지 필터링 테스트")
    void searchAttractionSummariesBySigunCode() {
        // given
        Integer sigunCode = 1; // 강남구 (문자열로 변환됨)
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AttractionSummaryDto> result = attractionRepository.searchAttractionSummaries(
                null, sigunCode, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1); // 강남구에 있는 관광지 1개
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("서울 타워");
    }

    @Test
    @DisplayName("복합 조건으로 관광지 필터링 테스트")
    void searchAttractionSummariesWithMultipleCriteria() {
        // given
        Integer areaCode = 1; // 서울
        Integer contentTypeId = 12; // 관광지
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AttractionSummaryDto> result = attractionRepository.searchAttractionSummaries(
                areaCode, null, contentTypeId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1); // 서울에 있는 관광지 타입 1개
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("서울 타워");
    }

    @Test
    @DisplayName("조건이 없을 때 모든 관광지 조회")
    void searchAttractionSummariesWithNoConditions() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AttractionSummaryDto> result = attractionRepository.searchAttractionSummaries(
                null, null, null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3); // 모든 관광지 3개
    }

    @Test
    @DisplayName("페이징 및 정렬 테스트")
    void searchAttractionSummariesWithPagingAndSorting() {
        // given
        // 첫 페이지, 페이지당 2개, 이름 내림차순 정렬
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "title"));

        // when
        Page<AttractionSummaryDto> result = attractionRepository.findAttractionSummaries(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2); // 첫 페이지에 2개만 표시
        assertThat(result.getTotalElements()).isEqualTo(3); // 전체 데이터는 3개
        assertThat(result.getTotalPages()).isEqualTo(2); // 총 2페이지
        
        // 내림차순 정렬 확인
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("서울 타워");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("부산 해운대 해수욕장");
        
        // 두번째 페이지 확인
        Pageable secondPage = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "title"));
        Page<AttractionSummaryDto> secondPageResult = attractionRepository.findAttractionSummaries(secondPage);
        
        assertThat(secondPageResult.getContent()).hasSize(1); // 두번째 페이지에는 1개만
        assertThat(secondPageResult.getContent().get(0).getTitle()).isEqualTo("경복궁");
    }
}
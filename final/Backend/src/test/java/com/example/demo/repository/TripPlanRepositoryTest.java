package com.example.demo.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.model.dto.TripPlan.TripPlanSummaryDto;
import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.User;

@DataJpaTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TripPlanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TripPlanRepository tripPlanRepository;

    // 테스트용 사용자 데이터
    private User user1;
    private User user2;

    // 테스트용 여행 계획 데이터
    private TripPlan tripPlan1;
    private TripPlan tripPlan2;
    private TripPlan tripPlan3;
    private TripPlan tripPlan4;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        user1 = User.builder()
                .name("홍길동")
                .email("hong@example.com")
                .password("password123")
                .role("USER")
                .address("서울시 강남구")
                .number("010-1234-5678")
                .build();

        user2 = User.builder()
                .name("김철수")
                .email("kim@example.com")
                .password("password456")
                .role("USER")
                .address("서울시 서초구")
                .number("010-9876-5432")
                .build();

        // 테스트 엔티티 저장
        user1 = entityManager.persist(user1);
        user2 = entityManager.persist(user2);

        // 테스트 여행 계획 생성 (createdAt 필드 없음)
        tripPlan1 = TripPlan.builder()
                .planName("제주도 3박 4일")
                .plan("1일차: 제주공항 도착, 렌트카 수령, 성산일출봉\n2일차: 우도 투어\n3일차: 한라산 등반\n4일차: 올레시장, 귀가")
                .startDate("2025-01-03")
                .endDate("2025-02-03")
                .user(user1)
                .location("제주")
                .build();

        tripPlan2 = TripPlan.builder()
                .planName("부산 여행 계획")
                .plan("1일차: KTX 타고 부산역, 해운대\n2일차: 광안리, 영도")
                .user(user1)
                .startDate("2025-01-03")
                .endDate("2025-02-13")
                .location("부산")
                .build();

        tripPlan3 = TripPlan.builder()
                .planName("도쿄 일본 여행")
                .plan("1일차: 하네다 공항, 시부야\n2일차: 아사쿠사, 도쿄타워\n3일차: 디즈니랜드")
                .user(user2)
                .startDate("2025-01-23")
                .endDate("2025-02-03")
                .location("일본")
                .build();

        tripPlan4 = TripPlan.builder()
                .planName("강원도 속초 당일치기")
                .plan("아침 일찍 출발, 중간에 휴게소 들렸다가 속초 도착, 저녁에 귀가")
                .user(user2)
                .startDate("2025-01-03")
                .endDate("2025-02-23")
                .location("강원")
                .build();

        // 테스트 여행 계획 저장
        entityManager.persist(tripPlan1);
        entityManager.persist(tripPlan2);
        entityManager.persist(tripPlan3);
        entityManager.persist(tripPlan4);
        entityManager.flush();
    }


    @Test
    @DisplayName("특정 사용자의 여행 계획을 페이징하여 조회")
    void findAllByUser() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<TripPlanSummaryDto> user1Plans = tripPlanRepository.findAllByUser(user1, pageable);
        Page<TripPlanSummaryDto> user2Plans = tripPlanRepository.findAllByUser(user2, pageable);

        // then
        assertThat(user1Plans.getContent()).hasSize(2);
        assertThat(user2Plans.getContent()).hasSize(2);
        
        // 여행 계획 이름 확인
        List<String> user1PlanNames = user1Plans.getContent().stream()
                .map(TripPlanSummaryDto::getPlanName)
                .toList();
        assertThat(user1PlanNames).containsExactlyInAnyOrder("제주도 3박 4일", "부산 여행 계획");
        
        List<String> user2PlanNames = user2Plans.getContent().stream()
                .map(TripPlanSummaryDto::getPlanName)
                .toList();
        assertThat(user2PlanNames).containsExactlyInAnyOrder("도쿄 일본 여행", "강원도 속초 당일치기");
    }

    @Test
    @DisplayName("페이징 결과의 내용 확인")
    void checkPagingResultContent() {
        // given
        Pageable pageable = PageRequest.of(0, 1);

        // when
        Page<TripPlanSummaryDto> result = tripPlanRepository.findAllByUser(user1, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        
        // 첫 번째 아이템 내용 확인
        TripPlanSummaryDto firstItem = result.getContent().get(0);
        assertThat(firstItem.getId()).isNotNull();
        assertThat(firstItem.getPlanName()).isIn("제주도 3박 4일", "부산 여행 계획");
        
        // 두 번째 페이지 확인
        Pageable secondPage = PageRequest.of(1, 1);
        Page<TripPlanSummaryDto> secondPageResult = tripPlanRepository.findAllByUser(user1, secondPage);
        
        assertThat(secondPageResult.getContent()).hasSize(1);
        TripPlanSummaryDto secondItem = secondPageResult.getContent().get(0);
        assertThat(secondItem.getPlanName()).isIn("제주도 3박 4일", "부산 여행 계획");
        assertThat(secondItem.getPlanName()).isNotEqualTo(firstItem.getPlanName());
    }

    @Test
    @DisplayName("특정 사용자의 여행 계획을 계획명으로 검색")
    void findByUserAndPlanContaining() {
        // given
        String keyword = "여행";

        // when
        List<TripPlan> user1Results = tripPlanRepository.findByUserAndPlanNameContaining(user1, keyword);
        List<TripPlan> user2Results = tripPlanRepository.findByUserAndPlanNameContaining(user2, keyword);

        // then
        assertThat(user1Results).hasSize(1);
        assertThat(user1Results.get(0).getPlanName()).isEqualTo("부산 여행 계획");
        
        assertThat(user2Results).hasSize(1);
        assertThat(user2Results.get(0).getPlanName()).isEqualTo("도쿄 일본 여행");
    }

    @Test
    @DisplayName("특정 사용자의 여행 계획을 계획명으로 검색 - 결과 없음")
    void findByUserAndPlanContainingNoResults() {
        // given
        String keyword = "존재하지않는키워드";

        // when
        List<TripPlan> results = tripPlanRepository.findByUserAndPlanNameContaining(user1, keyword);

        // then
        assertThat(results).isEmpty();
    }
    
    @Test
    @DisplayName("여행 계획 페이징 - 페이지 크기 테스트")
    void paginationWithDifferentSize() {
        // given
        // 한 페이지에 1개씩
        Pageable pageable = PageRequest.of(0, 1);

        // when
        Page<TripPlanSummaryDto> result = tripPlanRepository.findAllByUser(user1, pageable);

        // then
        assertThat(result.getContent()).hasSize(1); // 페이지당 1개
        assertThat(result.getTotalElements()).isEqualTo(2); // 전체 계획 2개
        assertThat(result.getTotalPages()).isEqualTo(2); // 총 2페이지
    }
}
package com.example.demo.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.model.dto.TPsnippet.TPsnippetSummaryDto;
import com.example.demo.model.entity.Attraction;
import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.TripSnippet;
import com.example.demo.model.entity.User;

@DataJpaTest
class TPsnippetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TripSnippetRepository tripSnippetRepository;

    // 테스트 데이터
    private User user;
    private TripPlan tripPlan1;
    private TripPlan tripPlan2;
    private Attraction attraction1;
    private Attraction attraction2;
    private Attraction attraction3;
    private TripSnippet snippet1;
    private TripSnippet snippet2;
    private TripSnippet snippet3;
    private TripSnippet snippet4;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        user = User.builder()
                .name("홍길동")
                .email("hong@example.com")
                .password("password123")
                .role("USER")
                .address("서울시 강남구")
                .number("010-1234-5678")
                .build();
        user = entityManager.persist(user);

        // 테스트 여행 계획 생성
        tripPlan1 = TripPlan.builder()
                .planName("제주도 3박 4일")
                .plan("제주도 여행 계획")
                .user(user)
                .build();
        tripPlan1 = entityManager.persist(tripPlan1);

        tripPlan2 = TripPlan.builder()
                .planName("부산 여행")
                .plan("부산 여행 계획")
                .user(user)
                .build();
        tripPlan2 = entityManager.persist(tripPlan2);

        // 테스트 관광지 생성
        attraction1 = Attraction.builder()
                .title("성산일출봉")
                .addr1("제주특별자치도 서귀포시 성산읍")
                .contentTypeId(12)
                .firstImage1("http://example.com/seongsan.jpg")
                .tel("064-123-4567")
                .build();
        attraction1 = entityManager.persist(attraction1);

        attraction2 = Attraction.builder()
                .title("우도")
                .addr1("제주특별자치도 제주시 우도면")
                .contentTypeId(12)
                .firstImage1("http://example.com/udo.jpg")
                .tel("064-234-5678")
                .build();
        attraction2 = entityManager.persist(attraction2);

        attraction3 = Attraction.builder()
                .title("해운대 해수욕장")
                .addr1("부산광역시 해운대구")
                .contentTypeId(12)
                .firstImage1("http://example.com/haeundae.jpg")
                .tel("051-987-6543")
                .build();
        attraction3 = entityManager.persist(attraction3);

        // 테스트 여행 스니펫 생성 (price가 String으로 변경됨, schedule 필드 추가)
        snippet1 = TripSnippet.builder()
                .plan(tripPlan1)
                .attraction(attraction1)
                .price("10000원")
                .schedule("첫째날 오전 10시")
                .build();
        entityManager.persist(snippet1);

        snippet2 = TripSnippet.builder()
                .plan(tripPlan1)
                .attraction(attraction2)
                .price("15000원")
                .schedule("둘째날 오후 2시")
                .build();
        entityManager.persist(snippet2);

        snippet3 = TripSnippet.builder()
                .plan(tripPlan2)
                .attraction(attraction3)
                .price("5000원")
                .schedule("첫째날 오전 9시")
                .build();
        entityManager.persist(snippet3);

        snippet4 = TripSnippet.builder()
                .plan(tripPlan2)
                .attraction(attraction1) // 제주도 관광지지만 부산 여행 계획에 포함
                .price("12000원")
                .schedule("둘째날 오전 11시")
                .build();
        entityManager.persist(snippet4);

        entityManager.flush();
    }

    @Test
    @DisplayName("특정 plan에 해당하는 모든 스니펫 조회")
    void findByPlanId() {
        // when
        List<TripSnippet> snippetsPlan1 = tripSnippetRepository.findByPlan(tripPlan1);
        List<TripSnippet> snippetsPlan2 = tripSnippetRepository.findByPlan(tripPlan2);

        // then
        assertThat(snippetsPlan1).hasSize(2);
        assertThat(snippetsPlan2).hasSize(2);

        // 첫 번째 계획의 스니펫 내용 확인
        assertThat(snippetsPlan1.stream().map(snippet -> snippet.getAttraction().getTitle()).toList())
                .containsExactlyInAnyOrder("성산일출봉", "우도");
        assertThat(snippetsPlan1.stream().map(TripSnippet::getPrice).toList())
                .containsExactlyInAnyOrder("10000원", "15000원");
        assertThat(snippetsPlan1.stream().map(TripSnippet::getSchedule).toList())
                .containsExactlyInAnyOrder("첫째날 오전 10시", "둘째날 오후 2시");

        // 두 번째 계획의 스니펫 내용 확인
        assertThat(snippetsPlan2.stream().map(snippet -> snippet.getAttraction().getTitle()).toList())
                .containsExactlyInAnyOrder("해운대 해수욕장", "성산일출봉");
        assertThat(snippetsPlan2.stream().map(TripSnippet::getPrice).toList())
                .containsExactlyInAnyOrder("5000원", "12000원");
        assertThat(snippetsPlan2.stream().map(TripSnippet::getSchedule).toList())
                .containsExactlyInAnyOrder("첫째날 오전 9시", "둘째날 오전 11시");
    }

    @Test
    @DisplayName("특정 계획에 대한 스니펫 요약 정보 조회")
    void getTripSnippetSummaryById() {

        // when
        List<TPsnippetSummaryDto> summaryPlan1 = tripSnippetRepository.getTripSnippetSummaryById(tripPlan1);
        List<TPsnippetSummaryDto> summaryPlan2 = tripSnippetRepository.getTripSnippetSummaryById(tripPlan2);

        // then
        assertThat(summaryPlan1).hasSize(2);
        assertThat(summaryPlan2).hasSize(2);



        boolean hasPricesForPlan1 = summaryPlan1.stream()
                .allMatch(dto -> {
                    String price = dto.getPrice();
                    return price != null && (price.equals("10000원") || price.equals("15000원"));
                });
        assertThat(hasPricesForPlan1).isTrue();


        
        boolean hasPricesForPlan2 = summaryPlan2.stream()
                .allMatch(dto -> {
                    String price = dto.getPrice();
                    return price != null && (price.equals("5000원") || price.equals("12000원"));
                });
        assertThat(hasPricesForPlan2).isTrue();
    }

    @Test
    @DisplayName("없는 tripplan으로 조회했을 때 빈 목록 반환")
    void findByNonExistingPlanId() {
        // when
        TripPlan plan=TripPlan.builder()
                .planName("없는 여행")
                .plan("없는 여행 계획")
                .user(user)
                .build();
        entityManager.persist(plan);
        List<TripSnippet> snippets = tripSnippetRepository.findByPlan(plan);

        // then
        assertThat(snippets).isEmpty();
    }
}
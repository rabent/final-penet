package com.example.demo.integration;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.demo.model.entity.Attraction;
import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.TripSnippet;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AttractionRepository;
import com.example.demo.repository.TripPlanRepository;
import com.example.demo.repository.TripSnippetRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
//@Transactional
public class TripPlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripPlanRepository tripPlanRepository;

    @Autowired
    private TripSnippetRepository tripSnippetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private TripPlan testPlan;
    private TripSnippet testSnippet;
    private Attraction testAttraction;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        tripSnippetRepository.deleteAll();
        tripPlanRepository.deleteAll();
        userRepository.deleteAll();
        
        // 테스트 관광지 생성
        testAttraction = Attraction.builder()
                .contentId(1234)
                .contentTypeId(12)
                .title("테스트 관광지")
                .firstImage1("image1.jpg")
                .firstImage2("image2.jpg")
                .latitude(37.5665)
                .longitude(126.9780)
                .mapLevel(10)
                .tel("02-1234-5678")
                .addr1("서울시 중구")
                .addr2("테스트 주소")
                .areaCode(1)
                .siGunguCode(2)
                .homePage("http://test.com")
                .overview("테스트 관광지 개요")
                .build();
                
        attractionRepository.save(testAttraction);

        // 테스트 사용자 생성
        testUser = User.builder()
                .name("테스트 사용자")
                .password("password123")
                .role("USER")
                .email("test@example.com")
                .address("서울시 테스트구")
                .number("010-1234-5678")
                .build();
        testUser = userRepository.save(testUser);

        // 테스트 여행 계획 생성
        testPlan = TripPlan.builder()
                .planName("테스트 여행 계획")
                .plan("테스트 계획 내용")
                .user(testUser)
                .build();
        testPlan = tripPlanRepository.save(testPlan);
        testUser.getPlans().add(testPlan);

        // 테스트 여행 스니펫 생성
        testSnippet = TripSnippet.builder()
                .price("10000원")
                .schedule("첫째 날 일정")
                .plan(testPlan)
                .attraction(testAttraction)
                .build();
        testSnippet = tripSnippetRepository.save(testSnippet);
        testPlan.getSnippets().add(testSnippet);
    }

    @Test
    void planSummary_returnsUserTripPlans() throws Exception {
        mockMvc.perform(get("/trips")
                .param("userId", testUser.getId().toString())
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].planName", is("테스트 여행 계획")));
    }

    @Test
    void planPost_createsNewTripPlan() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("planName", "새 여행 계획");
        params.add("plan", "새 계획 내용입니다.");

        mockMvc.perform(post("/trips")
                .params(params)
                .param("userId", testUser.getId().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plan.planName", is("새 여행 계획")))
                .andExpect(jsonPath("$.plan.plan", is("새 계획 내용입니다.")));
    }

    @Test
    void planUpdate_updatesExistingPlan() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("planName", "수정된 여행 계획");
        params.add("plan", "수정된 계획 내용입니다.");

        mockMvc.perform(put("/trips/{planId}", testPlan.getId())
                .params(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plan.planName", is("수정된 여행 계획")))
                .andExpect(jsonPath("$.plan.plan", is("수정된 계획 내용입니다.")));
    }

    @Test
    void planDetail_returnsPlanDetails() throws Exception {
        mockMvc.perform(get("/trips/{planId}", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.plan.planName", is("테스트 여행 계획")))
                .andExpect(jsonPath("$.plan.plan", is("테스트 계획 내용")))
                .andExpect(jsonPath("$.snippets", hasSize(1)));
    }

    @Test
    void snippetPost_addsNewSnippetToPlan() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("price", "20000원");
        params.add("schedule", "둘째 날 일정");
        params.add("no", testAttraction.getNo().toString());

        mockMvc.perform(post("/trips/{planId}", testPlan.getId())
                .params(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price", is("20000원")))
                .andExpect(jsonPath("$.schedule", is("둘째 날 일정")))
                .andExpect(jsonPath("$.attraction.title", is("테스트 관광지")));
    }

    @Test
    void snippetUpdate_updatesExistingSnippet() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("price", "15000원");
        params.add("schedule", "수정된 일정");

        mockMvc.perform(put("/trips/{planId}/{snipId}", testPlan.getId(), testSnippet.getId())
                .params(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price", is("15000원")))
                .andExpect(jsonPath("$.schedule", is("수정된 일정")));
    }

    @Test
    void planDelete_deletesPlan() throws Exception {
        mockMvc.perform(delete("/trips/{planId}", testPlan.getId()))
                .andExpect(status().isNoContent());

        // 삭제 확인
        mockMvc.perform(get("/trips/{planId}", testPlan.getId()))
                .andExpect(status().isNotFound());
    }
}
package com.example.demo.integration;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.example.demo.config.GlobalExceptionHandler;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.entity.Attraction;
import com.example.demo.repository.AttractionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Transactional
public class AttractionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    private Attraction testAttraction;

    @BeforeEach
    void setUp() {
        attractionRepository.deleteAll();
        // 테스트용 관광지 데이터 생성 및 저장
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
        testAttraction = attractionRepository.save(testAttraction);
    }

    @Autowired
    private Environment env;

    @Test
    void checkProfile() {
        System.out.println("Active Profiles: " + Arrays.toString(env.getActiveProfiles()));
        System.out.println("SQL Init Mode: " + env.getProperty("spring.sql.init.mode"));
        System.out.println("Schema Locations: " + env.getProperty("spring.sql.init.schema-locations"));
        System.out.println("Data Locations: " + env.getProperty("spring.sql.init.data-locations"));
    }
    @Test
    void getAttractions_returnsPageOfAttractionSummaries() throws Exception {
        mockMvc.perform(get("/attractions")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", is("테스트 관광지")));
    }

    @Test
    void searchAttractions_returnsFilteredAttractions() throws Exception {
        mockMvc.perform(get("/attractions/search")
                .param("page", "0")
                .param("areaCode", "1")
                .param("sigunCode", "2")
                .param("contentTypeId", "12"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", is("테스트 관광지")));
    }

    @Test
    void getAttractionDetail_returnsAttractionDetails() throws Exception {
        mockMvc.perform(get("/attractions/{attrId}", testAttraction.getNo()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("테스트 관광지")))
                .andExpect(jsonPath("$.contentId", is(1234)))
                .andExpect(jsonPath("$.overview", is("테스트 관광지 개요")));
    }

    @Test
    void getAttractionDetail_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/attractions/{attrId}", 9999))
                .andExpect(status().isNotFound());
    }
}

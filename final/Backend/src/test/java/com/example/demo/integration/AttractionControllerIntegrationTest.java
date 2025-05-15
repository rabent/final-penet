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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.entity.Attraction;
import com.example.demo.repository.AttractionRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AttractionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttractionRepository attractionRepository;

    private Attraction testAttraction;

    @BeforeEach
    void setUp() {
        // 테스트용 관광지 데이터 생성 및 저장
        testAttraction = Attraction.builder()
                .no(1)
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

        // 기존에 저장된 데이터가 있다면 삭제하고 새로 저장
        attractionRepository.deleteAll();
        attractionRepository.save(testAttraction);
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

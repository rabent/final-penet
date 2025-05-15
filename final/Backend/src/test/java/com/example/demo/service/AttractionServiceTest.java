package com.example.demo.service;

import com.example.demo.model.dto.Attraction.AttractionResponseDto;
import com.example.demo.model.dto.Attraction.AttractionSearchCriteria;
import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.entity.Attraction;
import com.example.demo.repository.AttractionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttractionServiceTest {

    @Mock
    private AttractionRepository attractionRepository;

    @InjectMocks
    private AttractionService attractionService;

    private Attraction attraction1;
    private Attraction attraction2;
    private AttractionSummaryDto summaryDto1;
    private AttractionSummaryDto summaryDto2;
    private List<AttractionSummaryDto> summaryDtoList;
    private Page<AttractionSummaryDto> summaryDtoPage;
    private AttractionSearchCriteria searchCriteria;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @BeforeEach
    void setUp() {
        // 테스트용 데이터 생성
        attraction1 = new Attraction();
        attraction1.setNo(1);
        attraction1.setTitle("테스트 관광지 1");

        attraction2 = new Attraction();
        attraction2.setNo(2);
        attraction2.setTitle("테스트 관광지 2");

        summaryDto1 = new AttractionSummaryDto(1, "테스트 관광지 1", "이미지URL1", "주소1", "01030422312");
        summaryDto2 = new AttractionSummaryDto(2, "테스트 관광지 2", "이미지URL2", "주소2", "01023132133");

        summaryDtoList = Arrays.asList(summaryDto1, summaryDto2);
        summaryDtoPage = new PageImpl<>(summaryDtoList);

        searchCriteria = new AttractionSearchCriteria();
        searchCriteria.setAreaCode(1);
        searchCriteria.setSigunCode(2);
        searchCriteria.setContentTypeId(3);
    }

    @Test
    @DisplayName("모든 관광지 요약 정보를 페이징하여 조회한다")
    void getAttractionSummaries() {
        // given
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("no").descending());
        when(attractionRepository.findAttractionSummaries(pageable)).thenReturn(summaryDtoPage);

        // when
        Page<AttractionSummaryDto> result = attractionService.getAttractionSummaries(0);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 관광지 1");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("테스트 관광지 2");
        verify(attractionRepository, times(1)).findAttractionSummaries(pageable);
    }

    @Test
    @DisplayName("검색 조건으로 관광지를 조회한다")
    void searchAttractions() {
        // given
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("no").descending());
        when(attractionRepository.searchAttractionSummaries(
                eq(1), eq(2), eq(3), eq(pageable)
        )).thenReturn(summaryDtoPage);

        // when
        Page<AttractionSummaryDto> result = attractionService.searchAttractions(searchCriteria, 0);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(attractionRepository, times(1))
                .searchAttractionSummaries(
                        eq(searchCriteria.getAreaCode()),
                        eq(searchCriteria.getSigunCode()),
                        eq(searchCriteria.getContentTypeId()),
                        eq(pageable)
                );
    }

    @Test
    @DisplayName("관광지 상세 정보를 조회한다")
    void getAttractionDetail() {
        // given
        when(attractionRepository.findById(1)).thenReturn(Optional.of(attraction1));
        AttractionResponseDto expectedDto = AttractionResponseDto.fromEntity(attraction1);

        // when
        AttractionResponseDto result = attractionService.getAttractionDetail(1);

        // then
        assertThat(result).isNotNull();
        verify(attractionRepository, times(1)).findById(1);
        // AttractionResponseDto 객체의 내용을 상세히 비교할 수 있지만,
        // fromEntity 메서드의 구현에 따라 추가 검증이 필요할 수 있습니다.
        assertThat(expectedDto.getTitle()).isEqualTo("테스트 관광지 1");
    }

    @Test
    @DisplayName("존재하지 않는 관광지를 조회하면 EntityNotFoundException이 발생한다")
    void getAttractionDetail_notFound() {
        // given
        when(attractionRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            attractionService.getAttractionDetail(999);
        });
        verify(attractionRepository, times(1)).findById(999);
    }
}
package com.example.demo.service;

import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetRequestDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetResponseDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetSummaryDto;
import com.example.demo.model.dto.TPsnippet.TPsnippetUpdateDto;
import com.example.demo.model.dto.TripPlan.TripPlanRequestDto;
import com.example.demo.model.dto.TripPlan.TripPlanResponseDto;
import com.example.demo.model.dto.TripPlan.TripPlanSummaryDto;
import com.example.demo.model.dto.TripPlan.TripPlanUpdateDto;
import com.example.demo.model.entity.Attraction;
import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.TripSnippet;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AttractionRepository;
import com.example.demo.repository.TripPlanRepository;
import com.example.demo.repository.TripSnippetRepository;
import com.example.demo.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripPlanServiceTest {

    @Mock
    private TripPlanRepository tripPlanRepository;

    @Mock
    private TripSnippetRepository tripSnippetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttractionRepository attractionRepository;

    @InjectMocks
    private TripPlanService tripPlanService;

    private User testUser;
    private TripPlan testTripPlan1;
    private TripPlan testTripPlan2;
    private TripSnippet testTripSnippet1;
    private TripSnippet testTripSnippet2;
    private Attraction testAttraction;
    private TripPlanRequestDto tripPlanRequestDto;
    private TripPlanUpdateDto tripPlanUpdateDto;
    private TPsnippetRequestDto tPsnippetRequestDto;
    private TPsnippetUpdateDto tPsnippetUpdateDto;
    private List<TripPlanSummaryDto> tripPlanSummaryDtos;
    private List<TPsnippetSummaryDto> tPsnippetSummaryDtos;
    private Page<TripPlanSummaryDto> tripPlanSummaryDtoPage;

    private static final int DEFAULT_PAGE_SIZE = 8;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = new User();
        testUser.setId(1);
        testUser.setName("testUser");
        testUser.setEmail("test@example.com");

        // 테스트 관광지 생성
        testAttraction = new Attraction();
        testAttraction.setNo(1);
        testAttraction.setTitle("테스트 관광지");
        testAttraction.setAddr1("테스트 주소");
        testAttraction.setFirstImage1("이미지URL");

        // 테스트 여행 계획 생성
        testTripPlan1 = new TripPlan();
        testTripPlan1.setId(1);
        testTripPlan1.setPlanName("테스트 여행 계획 1");
        testTripPlan1.setPlan("테스트 여행 계획 내용 1");
        testTripPlan1.setUser(testUser);

        testTripPlan2 = new TripPlan();
        testTripPlan2.setId(2);
        testTripPlan2.setPlanName("테스트 여행 계획 2");
        testTripPlan1.setPlan("테스트 여행 계획 내용 2");
        testTripPlan2.setUser(testUser);

        // 테스트 여행 스니펫 생성
        testTripSnippet1 = new TripSnippet();
        testTripSnippet1.setId(1);
        testTripSnippet1.setPrice("10000원");
        testTripSnippet1.setSchedule("테스트 스니펫 계획 1");
        testTripSnippet1.setPlan(testTripPlan1);
        testTripSnippet1.setAttraction(testAttraction);

        testTripSnippet2 = new TripSnippet();
        testTripSnippet2.setId(2);
        testTripSnippet2.setPrice("20000원");
        testTripSnippet2.setSchedule("테스트 스니펫 계획 2");
        testTripSnippet2.setPlan(testTripPlan1);
        testTripSnippet2.setAttraction(testAttraction);

        // 테스트 DTO 생성
        tripPlanRequestDto = new TripPlanRequestDto();
        tripPlanRequestDto.setPlanName("새 여행 계획");
        tripPlanRequestDto.setPlan("새 여행 계획 내용");

        tripPlanUpdateDto = new TripPlanUpdateDto();
        tripPlanUpdateDto.setPlanName("수정된 여행 계획");
        tripPlanUpdateDto.setPlan("수정된 여행 계획 내용");

        tPsnippetRequestDto = new TPsnippetRequestDto();
        tPsnippetRequestDto.setPrice("15000원");
        tPsnippetRequestDto.setSchedule("새 여행 스니펫 내용");

        tPsnippetUpdateDto = new TPsnippetUpdateDto();
        tPsnippetUpdateDto.setPrice("25000원");
        tPsnippetUpdateDto.setSchedule("수정된 여행 스니펫 내용");

        // 테스트 DTO 리스트 생성
        TripPlanSummaryDto summaryDto1 = new TripPlanSummaryDto(1, "테스트 여행 계획 1", "테스트 여행 계획 내용 1");
        TripPlanSummaryDto summaryDto2 = new TripPlanSummaryDto(2, "테스트 여행 계획 2", "테스트 여행 계획 내용 2");
        tripPlanSummaryDtos = Arrays.asList(summaryDto1, summaryDto2);
        tripPlanSummaryDtoPage = new PageImpl<>(tripPlanSummaryDtos);

        AttractionSummaryDto attractionSummaryDto = new AttractionSummaryDto(1, "테스트 관광지", "이미지URL", "테스트 주소", "010303212312");
        TPsnippetSummaryDto snippetSummaryDto1 = new TPsnippetSummaryDto(1, attractionSummaryDto.getTitle(), "10000원");
        TPsnippetSummaryDto snippetSummaryDto2 = new TPsnippetSummaryDto(2, attractionSummaryDto.getTitle(), "20000원");
        tPsnippetSummaryDtos = Arrays.asList(snippetSummaryDto1, snippetSummaryDto2);
    }

    @Test
    @DisplayName("사용자별 여행 계획 목록을 페이지네이션하여 조회한다")
    void getUserTripPlans() {
        // given
        Integer userId = 1;
        int page = 0;
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("id").descending());

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(tripPlanRepository.findAllByUser(testUser, pageable)).thenReturn(tripPlanSummaryDtoPage);

        // when
        Page<TripPlanSummaryDto> result = tripPlanService.getUserTripPlans(userId, page);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getPlanName()).isEqualTo("테스트 여행 계획 1");
        assertThat(result.getContent().get(1).getPlanName()).isEqualTo("테스트 여행 계획 2");

        verify(userRepository, times(1)).findById(userId);
        verify(tripPlanRepository, times(1)).findAllByUser(testUser, pageable);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 여행 계획을 조회하면 예외가 발생한다")
    void getUserTripPlans_UserNotFound() {
        // given
        Integer userId = 999;
        int page = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            tripPlanService.getUserTripPlans(userId, page);
        });

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("여행 계획을 저장한다")
    void saveTripPlan() {
        // given
        Integer userId = 1;
        TripPlan newTripPlan = new TripPlan();
        newTripPlan.setPlanName("새 여행 계획");
        newTripPlan.setPlan("새 여행 계획 내용");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(tripPlanRepository.save(any(TripPlan.class))).thenReturn(newTripPlan);

        // when
        TripPlanResponseDto result = tripPlanService.saveTripPlan(tripPlanRequestDto, userId);

        // then
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findById(userId);
        verify(tripPlanRepository, times(1)).save(any(TripPlan.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 여행 계획을 저장하면 예외가 발생한다")
    void saveTripPlan_UserNotFound() {
        // given
        Integer userId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            tripPlanService.saveTripPlan(tripPlanRequestDto, userId);
        });

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("ID로 여행 계획을 조회한다")
    void getTripPlanById() {
        // given
        Integer planId = 1;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.of(testTripPlan1));
        when(tripSnippetRepository.getTripSnippetSummaryById(testTripPlan1)).thenReturn(tPsnippetSummaryDtos);

        // when
        TripPlanService.TripPlanWithSnippetsDto result = tripPlanService.getTripPlanById(planId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlan()).isEqualTo(testTripPlan1);
        assertThat(result.getSnippets()).hasSize(2);
        assertThat(result.getSnippets().get(0).getAttractionName()).isEqualTo("테스트 관광지");

        verify(tripPlanRepository, times(1)).findById(planId);
        verify(tripSnippetRepository, times(1)).getTripSnippetSummaryById(testTripPlan1);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 여행 계획을 조회하면 null을 반환한다")
    void getTripPlanById_NotFound() {
        // given
        Integer planId = 999;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.empty());

        // when
        TripPlanService.TripPlanWithSnippetsDto result = tripPlanService.getTripPlanById(planId);

        // then
        assertNull(result);

        verify(tripPlanRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("여행 계획을 수정한다")
    void updateTripPlan() {
        // given
        Integer planId = 1;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.of(testTripPlan1));

        // when
        tripPlanService.UpdateTripPlan(tripPlanUpdateDto, planId);

        // then
        verify(tripPlanRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 여행 계획을 수정하면 예외가 발생한다")
    void updateTripPlan_NotFound() {
        // given
        Integer planId = 999;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            tripPlanService.UpdateTripPlan(tripPlanUpdateDto, planId);
        });

        verify(tripPlanRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("여행 계획을 삭제한다")
    void deleteTripPlan() {
        // given
        Integer planId = 1;

        // when
        tripPlanService.deleteTripPlan(planId);

        // then
        verify(tripPlanRepository, times(1)).deleteById(planId);
    }

    @Test
    @DisplayName("특정 여행 계획에 속한 스니펫 요약 정보를 조회한다")
    void getTripSnippetSummaries() {
        // given
        Integer planId = 1;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.of(testTripPlan1));
        when(tripSnippetRepository.getTripSnippetSummaryById(testTripPlan1)).thenReturn(tPsnippetSummaryDtos);

        // when
        List<TPsnippetSummaryDto> result = tripPlanService.getTripSnippetSummaries(planId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAttractionName()).isEqualTo("테스트 관광지");
        assertThat(result.get(0).getPrice()).isEqualTo("10000원");

        verify(tripPlanRepository, times(1)).findById(planId);
        verify(tripSnippetRepository, times(1)).getTripSnippetSummaryById(testTripPlan1);
    }

    @Test
    @DisplayName("존재하지 않는 여행 계획 ID로 스니펫을 조회하면 예외가 발생한다")
    void getTripSnippetSummaries_PlanNotFound() {
        // given
        Integer planId = 999;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            tripPlanService.getTripSnippetSummaries(planId);
        });

        verify(tripPlanRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("여행 스니펫을 저장한다")
    void saveTripSnippet() {
        // given
        Integer attractionNo = 1;
        Integer planId = 1;
        TripSnippet newSnippet = new TripSnippet();
        newSnippet.setPrice("15000원");
        newSnippet.setSchedule("새 여행 스니펫 내용");
        AttractionSummaryDto attractionSummaryDto = new AttractionSummaryDto(1, "테스트 관광지", "이미지URL", "테스트 주소", "010303212312");

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.of(testTripPlan1));
        when(attractionRepository.findById(attractionNo)).thenReturn(Optional.of(testAttraction));
        when(tripSnippetRepository.save(any(TripSnippet.class))).thenReturn(newSnippet);

        // TPsnippetResponseDto 생성을 위한 모킹
        mockStatic(AttractionSummaryDto.class);
        when(AttractionSummaryDto.from(testAttraction)).thenReturn(attractionSummaryDto);

        // when
        TPsnippetResponseDto result = tripPlanService.saveTripSnippet(tPsnippetRequestDto, attractionNo, planId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo("15000원");
        assertThat(result.getSchedule()).isEqualTo("새 여행 스니펫 내용");
        assertThat(result.getAttraction().getTitle()).isEqualTo("테스트 관광지");

        verify(tripPlanRepository, times(1)).findById(planId);
        verify(attractionRepository, times(1)).findById(attractionNo);
        verify(tripSnippetRepository, times(1)).save(any(TripSnippet.class));
    }

    @Test
    @DisplayName("존재하지 않는 여행 계획으로 스니펫을 저장하면 예외가 발생한다")
    void saveTripSnippet_PlanNotFound() {
        // given
        Integer attractionNo = 1;
        Integer planId = 999;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            tripPlanService.saveTripSnippet(tPsnippetRequestDto, attractionNo, planId);
        });

        verify(tripPlanRepository, times(1)).findById(planId);
    }

    @Test
    @DisplayName("존재하지 않는 관광지로 스니펫을 저장하면 예외가 발생한다")
    void saveTripSnippet_AttractionNotFound() {
        // given
        Integer attractionNo = 999;
        Integer planId = 1;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.of(testTripPlan1));
        when(attractionRepository.findById(attractionNo)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            tripPlanService.saveTripSnippet(tPsnippetRequestDto, attractionNo, planId);
        });

        verify(tripPlanRepository, times(1)).findById(planId);
        verify(attractionRepository, times(1)).findById(attractionNo);
    }

    @Test
    @DisplayName("여행 스니펫을 삭제한다")
    void deleteTripSnippet() {
        // given
        Integer snippetId = 1;

        // when
        tripPlanService.deleteTripSnippet(snippetId);

        // then
        verify(tripSnippetRepository, times(1)).deleteById(snippetId);
    }

    @Test
    @DisplayName("여행 스니펫을 수정한다")
    void updateTripSnippet() {
        // given
        Integer snippetId = 1;
        Integer attractionNo = 1;

        when(tripSnippetRepository.findById(snippetId)).thenReturn(Optional.of(testTripSnippet1));
        when(attractionRepository.findById(attractionNo)).thenReturn(Optional.of(testAttraction));

        // when
        tripPlanService.UpdateTripSnippet(tPsnippetUpdateDto, attractionNo, snippetId);

        // then
        verify(tripSnippetRepository, times(1)).findById(snippetId);
        verify(attractionRepository, times(1)).findById(attractionNo);
    }

    @Test
    @DisplayName("존재하지 않는 스니펫을 수정하면 예외가 발생한다")
    void updateTripSnippet_SnippetNotFound() {
        // given
        Integer snippetId = 999;
        Integer attractionNo = 1;

        when(tripSnippetRepository.findById(snippetId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            tripPlanService.UpdateTripSnippet(tPsnippetUpdateDto, attractionNo, snippetId);
        });

        verify(tripSnippetRepository, times(1)).findById(snippetId);
    }

    @Test
    @DisplayName("여행 스니펫 수정 시 관광지를 변경하는 경우")
    void updateTripSnippet_WithNewAttraction() {
        // given
        Integer snippetId = 1;
        Integer attractionNo = 2;
        Attraction newAttraction = new Attraction();
        newAttraction.setNo(2);
        newAttraction.setTitle("새 관광지");

        when(tripSnippetRepository.findById(snippetId)).thenReturn(Optional.of(testTripSnippet1));
        when(attractionRepository.findById(attractionNo)).thenReturn(Optional.of(newAttraction));

        // when
        tripPlanService.UpdateTripSnippet(tPsnippetUpdateDto, attractionNo, snippetId);

        // then
        verify(tripSnippetRepository, times(1)).findById(snippetId);
        verify(attractionRepository, times(1)).findById(attractionNo);
        assertThat(testTripSnippet1.getAttraction()).isEqualTo(newAttraction);
    }

    @Test
    @DisplayName("여행 스니펫 수정 시 관광지 정보가 null인 경우")
    void updateTripSnippet_WithNullAttraction() {
        // given
        Integer snippetId = 1;
        Integer attractionNo = null;

        when(tripSnippetRepository.findById(snippetId)).thenReturn(Optional.of(testTripSnippet1));

        // when
        tripPlanService.UpdateTripSnippet(tPsnippetUpdateDto, attractionNo, snippetId);

        // then
        verify(tripSnippetRepository, times(1)).findById(snippetId);
        // attractionRepository는 호출되지 않아야 함
        assertThat(testTripSnippet1.getAttraction()).isEqualTo(testAttraction); // 기존 관광지 유지
    }

    @Test
    @DisplayName("여행 계획과 그에 속한 스니펫 정보를 함께 조회한다")
    void getTripPlanWithSnippets() {
        // given
        Integer planId = 1;

        when(tripPlanRepository.findById(planId)).thenReturn(Optional.of(testTripPlan1));
        when(tripSnippetRepository.getTripSnippetSummaryById(testTripPlan1)).thenReturn(tPsnippetSummaryDtos);

        // when
        TripPlanService.TripPlanWithSnippetsDto result = tripPlanService.getTripPlanWithSnippets(planId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlan()).isEqualTo(testTripPlan1);
        assertThat(result.getSnippets()).hasSize(2);
        assertThat(result.getSnippets().get(0).getPrice()).isEqualTo("10000원");
        assertThat(result.getSnippets().get(1).getPrice()).isEqualTo("20000원");

        verify(tripPlanRepository, times(1)).findById(planId);
        verify(tripSnippetRepository, times(1)).getTripSnippetSummaryById(testTripPlan1);
    }
}
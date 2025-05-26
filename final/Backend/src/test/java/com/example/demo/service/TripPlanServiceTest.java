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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private TripPlan testTripPlan;
    private TripSnippet testTripSnippet;
    private Attraction testAttraction;
    private AttractionSummaryDto testAttractionSummaryDto;
    private List<TPsnippetSummaryDto> testSnippetSummaries;
    private TripPlanRequestDto tripPlanRequestDto;
    private TripPlanUpdateDto tripPlanUpdateDto;
    private TPsnippetRequestDto tpSnippetRequestDto;
    private TPsnippetUpdateDto tpSnippetUpdateDto;

    @BeforeEach
    void setUp() {
        // 테스트용 User 엔티티 생성
        testUser = User.builder()
                .id(1)
                .name("테스트사용자")
                .email("test@example.com")
                .build();

        // 테스트용 Attraction 엔티티 생성
        testAttraction = Attraction.builder()
                .no(101)
                .title("테스트 관광지")
                .addr1("서울시 종로구")
                .tel("02-1234-5678")
                .firstImage1("image_url.jpg")
                .build();

        // 테스트용 AttractionSummaryDto 생성
        testAttractionSummaryDto = new AttractionSummaryDto(
                101,
                "테스트 관광지",
                "image_url.jpg",
                "서울시 종로구",
                "02-1234-5678"
        );

        // 테스트용 TripPlan 엔티티 생성
        testTripPlan = TripPlan.builder()
                .id(1)
                .planName("서울 여행")
                .plan("서울 여행 계획입니다.")
                .user(testUser)
                .budget(0)
                .build();

        // 테스트용 TripSnippet 엔티티 생성
        testTripSnippet = TripSnippet.builder()
                .id(1)
                .price(10000)
                .schedule("첫째날 오전")
                .plan(testTripPlan)
                .attraction(testAttraction)
                .build();

        // 테스트용 스니펫 요약 DTO 리스트 생성
        testSnippetSummaries = new ArrayList<>();
        TPsnippetSummaryDto snippetSummaryDto = new TPsnippetSummaryDto(
                1,
                10000,testAttractionSummaryDto.getNo(),
                testAttractionSummaryDto
        );
        testSnippetSummaries.add(snippetSummaryDto);

        // 요청 DTO 생성
        tripPlanRequestDto = new TripPlanRequestDto("부산 여행", "부산 여행 계획입니다.", "", "","");
        tripPlanUpdateDto = new TripPlanUpdateDto("제주 여행", "제주 여행 계획으로 변경합니다.", "", "", "");
        tpSnippetRequestDto = new TPsnippetRequestDto(20000, "둘째날 오후","","", 101);
        tpSnippetUpdateDto = new TPsnippetUpdateDto(30000, "셋째날 오전","","", 101);
    }

    @Test
    @DisplayName("사용자별 여행 계획 목록 조회 테스트")
    void getUserTripPlansTest() {
        // Given
        List<TripPlanSummaryDto> summaryDtos = Arrays.asList(
                new TripPlanSummaryDto(1, "서울 여행", "서울 여행 계획입니다.","","","",1)
        );
        Page<TripPlanSummaryDto> expectedPage = new PageImpl<>(summaryDtos);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(tripPlanRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(expectedPage);

        // When
        Page<TripPlanSummaryDto> result = tripPlanService.getUserTripPlans(1, 0);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("서울 여행", result.getContent().get(0).getPlanName());
        verify(userRepository, times(1)).findById(1);
        verify(tripPlanRepository, times(1)).findAllByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    @DisplayName("여행 계획 저장 테스트")
    void saveTripPlanTest() {
        // Given
        TripPlan newPlan = TripPlan.builder()
                .id(2)
                .planName("부산 여행")
                .plan("부산 여행 계획입니다.")
                .user(testUser)
                .build();

        TripPlanResponseDto responseDto = new TripPlanResponseDto(
                "부산 여행",
                "부산 여행 계획입니다.",
                "",
                "",
                "",
                1
        );

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(tripPlanRepository.save(any(TripPlan.class))).thenReturn(newPlan);
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.of(newPlan));
        when(tripSnippetRepository.getTripSnippetSummaryById(any(TripPlan.class))).thenReturn(new ArrayList<>());

        // When
        TripPlanService.TripPlanWithSnippetsDto result = tripPlanService.saveTripPlan(tripPlanRequestDto, 1);

        // Then
        assertNotNull(result);
        assertEquals("부산 여행", result.getPlan().getPlanName());
        assertEquals("부산 여행 계획입니다.", result.getPlan().getPlan());
        verify(userRepository, times(1)).findById(1);
        verify(tripPlanRepository, times(1)).save(any(TripPlan.class));
        verify(tripPlanRepository, times(1)).findById(2);
    }

    @Test
    @DisplayName("여행 계획 조회 테스트")
    void getTripPlanByIdTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.of(testTripPlan));
        when(tripSnippetRepository.getTripSnippetSummaryById(any(TripPlan.class))).thenReturn(testSnippetSummaries);
        when(attractionRepository.findById(anyInt())).thenReturn(Optional.of(testAttraction));

        // When
        TripPlanService.TripPlanWithSnippetsDto result = tripPlanService.getTripPlanById(1);

        // Then
        assertNotNull(result);
        assertEquals("서울 여행", result.getPlan().getPlanName());
        assertEquals(1, result.getSnippets().size());
        assertEquals("테스트 관광지", result.getSnippets().get(0).getAttraction().getTitle());
        assertEquals(10000, result.getSnippets().get(0).getPrice());
        verify(tripPlanRepository, times(1)).findById(1);
        verify(tripSnippetRepository, times(1)).getTripSnippetSummaryById(testTripPlan);
    }

    @Test
    @DisplayName("여행 계획 업데이트 테스트")
    void updateTripPlanTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.of(testTripPlan));
        when(tripSnippetRepository.getTripSnippetSummaryById(any(TripPlan.class))).thenReturn(testSnippetSummaries);
        when(attractionRepository.findById(anyInt())).thenReturn(Optional.of(testAttraction));
        // When
        TripPlanService.TripPlanWithSnippetsDto result = tripPlanService.UpdateTripPlan(tripPlanUpdateDto, 1);

        // Then
        assertNotNull(result);
        assertEquals("제주 여행", testTripPlan.getPlanName());
        assertEquals("제주 여행 계획으로 변경합니다.", testTripPlan.getPlan());
        verify(tripPlanRepository, times(2)).findById(1);
        verify(tripSnippetRepository, times(1)).getTripSnippetSummaryById(testTripPlan);
    }

    @Test
    @DisplayName("여행 계획 삭제 테스트")
    void deleteTripPlanTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.of(testTripPlan));
        doNothing().when(tripPlanRepository).deleteById(anyInt());

        // When
        tripPlanService.deleteTripPlan(1);

        // Then
        assertNull(testTripPlan.getUser());
        verify(tripPlanRepository, times(1)).findById(1);
        verify(tripPlanRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("여행 스니펫 저장 테스트")
    void saveTripSnippetTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.of(testTripPlan));
        when(attractionRepository.findById(anyInt())).thenReturn(Optional.of(testAttraction));
        when(tripSnippetRepository.save(any(TripSnippet.class))).thenReturn(testTripSnippet);

        // When
        TPsnippetResponseDto result = tripPlanService.saveTripSnippet(tpSnippetRequestDto, 1);

        // Then
        assertNotNull(result);
        assertEquals(20000, result.getPrice());
        assertEquals("둘째날 오후", result.getSchedule());
        assertEquals("테스트 관광지", result.getAttraction().getTitle());
        verify(tripPlanRepository, times(1)).findById(1);
        verify(attractionRepository, times(1)).findById(101);
        verify(tripSnippetRepository, times(1)).save(any(TripSnippet.class));
    }

    @Test
    @DisplayName("여행 스니펫 조회 테스트")
    void getTripSnippetTest() {
        // Given
        when(tripSnippetRepository.findById(anyInt())).thenReturn(Optional.of(testTripSnippet));

        // When
        TPsnippetResponseDto result = tripPlanService.getTripSnippet(1);

        // Then
        assertNotNull(result);
        assertEquals(10000, result.getPrice());
        assertEquals("첫째날 오전", result.getSchedule());
        assertEquals("테스트 관광지", result.getAttraction().getTitle());
        verify(tripSnippetRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("여행 스니펫 삭제 테스트")
    void deleteTripSnippetTest() {
        // Given
        when(tripSnippetRepository.findById(anyInt())).thenReturn(Optional.of(testTripSnippet));
        doNothing().when(tripSnippetRepository).deleteById(anyInt());

        // When
        tripPlanService.deleteTripSnippet(1);

        // Then
        assertNull(testTripSnippet.getPlan());
        verify(tripSnippetRepository, times(1)).findById(1);
        verify(tripSnippetRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("여행 스니펫 업데이트 테스트")
    void updateTripSnippetTest() {
        // Given
        when(tripSnippetRepository.findById(anyInt())).thenReturn(Optional.of(testTripSnippet));
        when(attractionRepository.findById(anyInt())).thenReturn(Optional.of(testAttraction));

        // When
        TPsnippetResponseDto result = tripPlanService.UpdateTripSnippet(tpSnippetUpdateDto, 1);

        // Then
        assertNotNull(result);
        assertEquals(30000, result.getPrice());
        assertEquals("셋째날 오전", result.getSchedule());
        assertEquals("테스트 관광지", result.getAttraction().getTitle());
        verify(tripSnippetRepository, times(1)).findById(1);
        verify(attractionRepository, times(1)).findById(101);
    }

    @Test
    @DisplayName("여행 계획과 스니펫 정보 함께 조회 테스트")
    void getTripPlanWithSnippetsTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.of(testTripPlan));
        when(tripSnippetRepository.getTripSnippetSummaryById(any(TripPlan.class))).thenReturn(testSnippetSummaries);
        when(attractionRepository.findById(anyInt())).thenReturn(Optional.of(testAttraction));
        // When
        TripPlanService.TripPlanWithSnippetsDto result = tripPlanService.getTripPlanWithSnippets(1);

        // Then
        assertNotNull(result);
        assertEquals("서울 여행", result.getPlan().getPlanName());
        assertEquals(1, result.getSnippets().size());
        assertEquals("테스트 관광지", result.getSnippets().get(0).getAttraction().getTitle());
        verify(tripPlanRepository, times(1)).findById(1);
        verify(tripSnippetRepository, times(1)).getTripSnippetSummaryById(testTripPlan);
    }

    @Test
    @DisplayName("존재하지 않는 여행 계획 조회 시 예외 발생 테스트")
    void getTripPlanWithSnippetsNotFoundTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tripPlanService.getTripPlanWithSnippets(999)
        );
        // Then
        assertEquals("여행 계획을 찾을 수 없습니다.", exception.getMessage());
        verify(tripPlanRepository, times(1)).findById(999);
        verify(tripSnippetRepository, never()).getTripSnippetSummaryById(any(TripPlan.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 여행 계획 조회 시 예외 발생 테스트")
    void getUserTripPlansWithInvalidUserTest() {
        // Given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tripPlanService.getUserTripPlans(999, 0)
        );
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository, times(1)).findById(999);
        verify(tripPlanRepository, never()).findAllByUser(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("존재하지 않는 여행 계획 수정 시 예외 발생 테스트")
    void updateTripPlanWithInvalidIdTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tripPlanService.UpdateTripPlan(tripPlanUpdateDto, 999)
        );
        assertEquals("여행계획을 찾을 수 없습니다.", exception.getMessage());
        verify(tripPlanRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("존재하지 않는 스니펫 조회 시 예외 발생 테스트")
    void getTripSnippetWithInvalidIdTest() {
        // Given
        when(tripSnippetRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tripPlanService.getTripSnippet(999)
        );
        assertEquals("계획 스니펫이 없습니다", exception.getMessage());
        verify(tripSnippetRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("존재하지 않는 관광지로 스니펫 저장 시 예외 발생 테스트")
    void saveTripSnippetWithInvalidAttractionTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.of(testTripPlan));
        when(attractionRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tripPlanService.saveTripSnippet(tpSnippetRequestDto, 1)
        );
        assertEquals("관광지를 찾을 수 없습니다.", exception.getMessage());
        verify(tripPlanRepository, times(1)).findById(1);
        verify(attractionRepository, times(1)).findById(101);
        verify(tripSnippetRepository, never()).save(any(TripSnippet.class));
    }

    @Test
    @DisplayName("존재하지 않는 여행 계획으로 스니펫 저장 시 예외 발생 테스트")
    void saveTripSnippetWithInvalidPlanTest() {
        // Given
        when(tripPlanRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tripPlanService.saveTripSnippet(tpSnippetRequestDto, 999)
        );
        assertEquals("여행계획을 찾을 수 없습니다.", exception.getMessage());
        verify(tripPlanRepository, times(1)).findById(999);
        verify(attractionRepository, never()).findById(anyInt());
        verify(tripSnippetRepository, never()).save(any(TripSnippet.class));
    }
}
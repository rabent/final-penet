package com.example.demo.service;

import com.example.demo.model.dto.User.UserRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.model.dto.User.UserUpdateDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequestDto requestDto;
    private UserUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        // 테스트용 User 엔티티 생성
        testUser = User.builder()
                .id(1)
                .name("테스트사용자")
                .password("password123")
                .role("USER")
                .email("test@example.com")
                .address("서울시 강남구")
                .number("010-1234-5678")
                .build();

        // 테스트용 요청 DTO 생성
        requestDto = UserRequestDto.builder()
                .name("신규사용자")
                .password("newuser123")
                .role("USER")
                .email("newuser@example.com")
                .address("서울시 서초구")
                .number("010-9876-5432")
                .build();

        // 테스트용 업데이트 DTO 생성
        updateDto = UserUpdateDto.builder()
                .name("변경된이름")
                .email("updated@example.com")
                .address("서울시 마포구")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerSuccess() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Integer userId = userService.register(requestDto);

        // Then
        assertEquals(testUser.getId(), userId);
        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패 테스트")
    void registerFailDueToEmailDuplication() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(requestDto)
        );
        assertEquals("이미 가입된 이메일 입니다.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 상세 조회 성공 테스트")
    void getUserDetailSuccess() {
        // Given
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));

        // When
        UserResponseDto responseDto = userService.userDetail(1);

        // Then
        assertNotNull(responseDto);
        assertEquals(testUser.getId(), responseDto.getId());
        assertEquals(testUser.getName(), responseDto.getName());
        assertEquals(testUser.getEmail(), responseDto.getEmail());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 예외 발생 테스트")
    void getUserDetailFail() {
        // Given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.userDetail(999)
        );
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 성공 테스트")
    void updateUserSuccess() {
        // Given
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));

        // When
        UserResponseDto updatedUser = userService.userUpdate(updateDto, 1);

        // Then
        assertNotNull(updatedUser);
        assertEquals(updateDto.getName(), updatedUser.getName());
        assertEquals(updateDto.getEmail(), updatedUser.getEmail());
        assertEquals(updateDto.getAddress(), updatedUser.getAddress());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 업데이트 시 예외 발생 테스트")
    void updateUserFail() {
        // Given
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.userUpdate(updateDto, 999)
        );
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("사용자 삭제 테스트")
    void deleteUserSuccess() {
        // Given
        doNothing().when(userRepository).deleteById(anyInt());
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));

        // When
        userService.userDelete(1);

        // Then
        verify(userRepository, times(1)).deleteById(1);
    }
}
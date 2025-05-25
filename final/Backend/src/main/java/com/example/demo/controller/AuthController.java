package com.example.demo.controller;

import com.example.demo.model.dto.User.FindIdRequestDto;
import com.example.demo.model.dto.User.FindPasswordRequestDto;
import com.example.demo.model.dto.User.LoginRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.security.JwtProvider;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인 및 사용자 인증 관련 API")
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    @Operation(
            summary = "사용자 로그인",
            description = "이메일과 비밀번호를 통해 사용자 인증을 수행하고 JWT 토큰을 발급합니다."
    )
    @SecurityRequirements  // 이 API는 인증이 필요 없음을 명시
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 형식"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (이메일 또는 비밀번호 오류)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류"
            )
    })
    public ResponseEntity<?> login(
            @Parameter(
                    description = "로그인 요청 정보",
                    required = true,
                    schema = @Schema(implementation = LoginRequestDto.class)
            )
            @RequestBody LoginRequestDto request) {
        UserResponseDto user = userService.login(request.getEmail(), request.getPassword());
        String token = jwtProvider.createToken(user.getId().toString());

        return ResponseEntity.ok(Map.of(
                "Authorization", "Bearer " + token,
                "userId", user.getId()
        ));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(
            @RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(Collections.singletonMap("isDuplicate", isDuplicate));
    }

    @PostMapping("/find-id")
    public ResponseEntity<UserResponseDto> findId(@RequestBody FindIdRequestDto request) {
        UserResponseDto userEmail = userService.findUserEmail(request.getName(), request.getNumber());
        return ResponseEntity.ok(userEmail);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody FindPasswordRequestDto request) {
        String tempPassword = userService.resetPassword(request);
        return ResponseEntity.ok(Collections.singletonMap("tempPassword", tempPassword));
    }
}

// 응답 형식을 위한 스키마 클래스 정의
@Schema(description = "로그인 응답")
class LoginResponse {
    @Schema(description = "Bearer JWT 토큰", example = "Bearer eyJhbGci...")
    private String Authorization;

    @Schema(description = "인증된 사용자 ID", example = "1")
    private Long userId;

    // getter/setter는 생략
}
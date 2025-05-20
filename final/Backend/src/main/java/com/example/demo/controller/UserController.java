package com.example.demo.controller;

import com.example.demo.model.dto.User.UserRequestDto;
import com.example.demo.model.dto.User.UserResponseDto;
import com.example.demo.model.dto.User.UserUpdateDto;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 계정 관리 API")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "회원 가입", description = "새로운 사용자 계정을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "계정 생성 성공",
                    content = @Content(schema = @Schema(type = "integer", example = "1", description = "생성된 사용자 ID"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이메일 중복"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> registerUser(
            @Parameter(description = "사용자 등록 정보", required = true)
            @RequestBody @Valid UserRequestDto dto) {
        Integer id = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponseDto> detailUser(
            @Parameter(description = "조회할 사용자 ID", required = true, example = "1")
            @PathVariable Integer userId) {
        return ResponseEntity.ok(userService.userDetail(userId));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다. 본인 계정 정보만 수정 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (타인의 계정 수정 시도)"),
            @ApiResponse(responseCode = "404", description = "사용자 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "사용자 정보 수정 데이터", required = true)
            @RequestBody @Valid UserUpdateDto dto,
            @Parameter(description = "수정할 사용자 ID", required = true, example = "1")
            @PathVariable Integer userId) {
        return ResponseEntity.ok(userService.userUpdate(dto, userId));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다. 본인 계정만 삭제 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (타인의 계정 삭제 시도)"),
            @ApiResponse(responseCode = "404", description = "사용자 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "삭제할 사용자 ID", required = true, example = "1")
            @PathVariable Integer userId) {
        userService.userDelete(userId);
        return ResponseEntity.noContent().build();
    }
}
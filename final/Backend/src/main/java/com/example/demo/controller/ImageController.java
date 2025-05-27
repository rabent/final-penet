package com.example.demo.controller;

import com.example.demo.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "이미지 API", description = "이미지 업로드 및 조회 기능 제공 API")
@SecurityRequirement(name = "bearerAuth")
public class ImageController {

    private final ImageService imageService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드", description = "게시글용 이미지를 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공",
                    content = @Content(schema = @Schema(type = "object",
                            example = "{\"fileName\": \"uuid-filename.jpg\", \"filePath\": \"/uploads/uuid-filename.jpg\"}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Map<String, String>> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            Map<String, String> result = imageService.uploadImage(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "파일 업로드 실패");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/view/{fileName}")
    @Operation(summary = "이미지 조회", description = "업로드된 이미지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "파일 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Resource> viewImage(
            @Parameter(description = "조회할 파일명", required = true)
            @PathVariable String fileName) {
        try {
            Resource resource = imageService.loadImage(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .contentType(MediaType.IMAGE_JPEG) // 필요에 따라 동적으로 설정
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{fileName}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "이미지 삭제", description = "업로드된 이미지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "파일 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> deleteImage(
            @Parameter(description = "삭제할 파일명", required = true)
            @PathVariable String fileName) {
        try {
            imageService.deleteImage(fileName);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
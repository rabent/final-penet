package com.example.demo.service;

import com.example.demo.repository.BImageRepository;
import com.example.demo.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FileUtil fileUtil;
    private final BImageRepository bImageRepository;

    @Value("${file.dir}")
    private String uploadDir;

    // 허용된 이미지 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 이미지 파일 업로드
     *
     * @param file 업로드할 파일
     * @return 파일명과 경로 정보
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        // 파일 유효성 검증
        validateImageFile(file);

        // 파일 저장
        String savedFileName = fileUtil.saveFile(file);
        String originalFileName = file.getOriginalFilename();
        // 결과 반환
        Map<String, String> result = new HashMap<>();
        result.put("fileName", savedFileName);
        result.put("originalFileName", originalFileName);
        result.put("filePath", "/api/images/view/" + savedFileName);
        result.put("fileSize", String.valueOf(file.getSize()));

        return result;
    }

    /**
     * 이미지 파일 로드
     *
     * @param fileName 파일명
     * @return Resource 객체
     * @throws MalformedURLException URL 형식 오류 시
     */
    public Resource loadImage(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
        }
    }

    /**
     * 이미지 파일 및 DB 레코드 삭제
     *
     * @param fileName 삭제할 파일명
     */
    @Transactional
    public void deleteImage(String fileName) {
        try {
            // 1. DB에서 이미지 정보 삭제
            bImageRepository.deleteByFileName(fileName);

            // 2. 물리적 파일 삭제
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            File file = filePath.toFile();

            if (file.exists()) {
                if (!file.delete()) {
                    throw new RuntimeException("파일 삭제에 실패했습니다: " + fileName);
                }
            }
            // 파일이 존재하지 않아도 DB 삭제는 성공으로 처리

        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 이미지 파일 유효성 검증
     *
     * @param file 검증할 파일
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 5MB까지 허용됩니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }

        String extension = getFileExtension(originalFileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. 허용된 형식: " + ALLOWED_EXTENSIONS);
        }

        // MIME 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
    }

    /**
     * 파일 확장자 추출
     *
     * @param fileName 파일명
     * @return 확장자
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
}

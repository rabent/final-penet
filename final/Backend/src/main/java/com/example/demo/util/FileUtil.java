package com.example.demo.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {
    @Value("${file.dir}")
    private String uploadDir; // 파일이 저장될 경로

    /**
     * 파일 저장 (기존 메서드 - 하위 호환성 유지)
     */
    public String saveFile(MultipartFile file) throws IOException {
        return saveFileWithExtension(file);
    }

    /**
     * 파일 저장 - 확장자 포함하여 저장
     */
    public String saveFileWithExtension(MultipartFile file) throws IOException {
        // 업로드 디렉토리가 없으면 생성
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 원본 파일명에서 확장자 추출
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        // 고유한 파일명 생성 (UUID + 확장자)
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 파일 저장
        System.out.println(uploadDir + uniqueFileName);

        File dest = new File(uploadDir + uniqueFileName);
        file.transferTo(dest);

        return uniqueFileName;
    }

    /**
     * 파일 삭제
     */
    public boolean deleteFile(String fileName) {
        try {
            File file = new File(uploadDir + fileName);
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String fileName) {
        File file = new File(uploadDir + fileName);
        return file.exists();
    }

    /**
     * 업로드 디렉토리 경로 반환
     */
    public String getUploadDir() {
        return uploadDir;
    }
}

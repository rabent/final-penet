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

    public String saveFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리가 없으면 생성
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 고유한 파일명 생성
        String uniqueFileName = UUID.randomUUID().toString();
        
        // 파일 저장
        File dest = new File(uploadDir + uniqueFileName);
        file.transferTo(dest);
        
        return uniqueFileName;
    }
}

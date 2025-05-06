package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.B_image;

@Repository
public interface BImageRepository extends JpaRepository<B_image, Integer> {
    // 파일명으로 이미지 검색
    Optional<B_image> findByFileName(String fileName);
    
    // 특정 경로의 모든 이미지 검색
    List<B_image> findByFilePath(String filePath);
    
    // 파일명 존재 여부 확인
    boolean existsByFileName(String fileName);
}
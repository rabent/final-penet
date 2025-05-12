package com.example.demo.model.dto.Board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardSummaryDto {
    
    // 게시글 ID
    private Integer id;
    
    // 게시글 제목
    private String title;
    
    // 조회수
    private Integer hit;
    
    // 작성자 정보 (필요한 경우)
    private String user;
    
    // 생성 일시
    private LocalDateTime createdAt;
}
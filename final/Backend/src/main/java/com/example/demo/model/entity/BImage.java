package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "b_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id") // plan_id에서 image_id로 수정
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // 게시글 삭제시 이미지도 자동 삭제
    private Board board;

    @Column(name = "file_path", length = 100, nullable = false) // 길이 증가
    private String filePath;

    @Column(name = "file_name", length = 50, nullable = false) // 길이 증가 (UUID + 확장자)
    private String fileName;

    // 원본 파일명 저장 (선택사항)
    @Column(name = "original_file_name", length = 100)
    private String originalFileName;

    // 파일 크기 저장 (선택사항)
    @Column(name = "file_size")
    private Long fileSize;
}

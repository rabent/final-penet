package com.example.demo.model.dto.Board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.entity.BImage;
import com.example.demo.model.entity.Board;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardResponseDto {
    private Integer id;
    private String title;
    private String content;
    private Integer hit;
    private LocalDateTime createdAt;
    private String authorName;
    private Integer authorId;

    // 이미지 정보 추가
    private List<ImageDto> images;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageDto {
        private Integer id;
        private String fileName;
        private String filePath;
        private String originalFileName;

        public static ImageDto from(BImage bImage) {
            return ImageDto.builder()
                    .id(bImage.getId())
                    .fileName(bImage.getFileName())
                    .filePath("/api/images/view/" + bImage.getFileName())
                    .originalFileName(bImage.getFileName()) // 필요시 원본 파일명 필드 추가
                    .build();
        }
    }

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .hit(board.getHit())
                .createdAt(board.getCreatedAt())
                .authorName(board.getUser() != null ? board.getUser().getName() : "알 수 없음")
                .authorId(board.getUser() != null ? board.getUser().getId() : null)
                .images(board.getImages().stream()
                        .map(ImageDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}

package com.example.demo.model.dto.Board;

import com.example.demo.model.entity.Board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BoardRequestDto {
    @NotBlank(message="제목은 필수입니다.")
    @Size(max=200)
    private String title;

    @NotBlank(message="내용은 필수입니다.")
    @Size(max=500)
    private String content;

    // 이미지 파일명 목록 (선택사항)
    private List<String> imageFileNames;

    public Board toEntity() {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .hit(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
    
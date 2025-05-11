package com.example.demo.model.dto;

import com.example.demo.model.entity.Board;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoardRequestDto {
    @NotBlank(message="제목은 필수입니다.")
    private String title;
    
    @NotBlank(message="내용은 필수입니다.")
    private String content;

    public Board toEntity() {
        return Board.builder()
        .title(this.title)
        .content(this.content)
        .build();
    }
}
    
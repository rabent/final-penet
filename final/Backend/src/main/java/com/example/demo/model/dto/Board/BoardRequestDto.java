package com.example.demo.model.dto.Board;

import com.example.demo.model.entity.Board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    public Board toEntity() {
        return Board.builder()
        .title(this.title)
        .content(this.content)
        .build();
    }
}
    
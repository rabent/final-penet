package com.example.demo.model.dto.Board;

import com.example.demo.model.entity.Board;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateDto {
    @Size(max=200)
    private String title;
    
    @Size(max=500)
    private String content;

    // 이미지 파일명 목록 (선택사항 - null이면 이미지 수정하지 않음)
    private List<String> imageFileNames;

    public void updateEntity(Board board) {
        if (this.title != null) {
            board.setTitle(this.title);
        }
        if (this.content != null) {
            board.setContent(this.content);
        }
    }
}

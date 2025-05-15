package com.example.demo.model.dto.Board;

import com.example.demo.model.entity.Board;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateDto {
    @Size(max=200)
    private String title;
    
    @Size(max=500)
    private String content;

    public void updateEntity(Board board) {
        if (this.title != null) {
            board.setTitle(this.title);
        }
        if (this.content != null) {
            board.setContent(this.content);
        }
    }
}

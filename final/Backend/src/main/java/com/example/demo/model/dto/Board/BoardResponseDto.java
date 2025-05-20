package com.example.demo.model.dto.Board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.entity.BImage;
import com.example.demo.model.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDto {
    private Integer userId;
    
    private String username;
 
    private List<String> images;

    private String title;

    private String content;
     
    private Integer hit;
 
    private LocalDateTime createdAt;

    public static BoardResponseDto from(Board board) {
        List<String> list=new ArrayList<>();
        if(board.getImages()!=null) {
            for (BImage image : board.getImages()) {
                list.add(image.getFileName());
            }
        }
        return new BoardResponseDto(
                board.getUser() != null ? board.getUser().getId() : null,
                board.getUser() != null ? board.getUser().getName() : null,
                list,
                board.getTitle(),
                board.getContent(),
                board.getHit(),
                board.getCreatedAt()
        );
    }
}

package com.example.demo.controller;

import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.dto.Board.BoardRequestDto;
import com.example.demo.model.dto.Board.BoardResponseDto;
import com.example.demo.model.dto.Board.BoardSummaryDto;
import com.example.demo.model.dto.Board.BoardUpdateDto;
import com.example.demo.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<Page<BoardSummaryDto>> boardList(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(boardService.getAllBoardSummaries(page));
    }

    @PostMapping
    public ResponseEntity<BoardResponseDto> boardPost(@ModelAttribute @Valid BoardRequestDto dto, Integer id) {//jwt 토큰 내부의 user id를 받아올 방법 필요
        return ResponseEntity.ok(boardService.saveBoard(dto,id));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> boardDetail(@PathVariable Integer boardId) {
        return ResponseEntity.ok(boardService.getBoardDetail(boardId));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> boardDelete(@PathVariable Integer boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> boardUpdate(@PathVariable Integer boardId, @ModelAttribute @Valid BoardUpdateDto dto) {
        return ResponseEntity.ok(boardService.updateBoard(dto,boardId));
    }
}

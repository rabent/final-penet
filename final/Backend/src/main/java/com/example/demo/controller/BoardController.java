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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<Page<BoardSummaryDto>> boardList(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(boardService.getAllBoardSummaries(page));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<BoardResponseDto> boardPost(@RequestBody @Valid BoardRequestDto dto,@AuthenticationPrincipal String userId) {//jwt 토큰 내부의 user id를 받아올 방법 필요
        return ResponseEntity.ok(boardService.saveBoard(dto,Integer.valueOf(userId)));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> boardDetail(@PathVariable Integer boardId, @AuthenticationPrincipal String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("board",boardService.getBoardDetail(boardId));
        if(userId!=null && !userId.equals("anonymousUser")) response.put("userId",Integer.valueOf(userId));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> boardDelete(@PathVariable Integer boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/edit/{boardId}")
    public ResponseEntity<BoardResponseDto> boardUpdate(@PathVariable Integer boardId, @RequestBody @Valid BoardUpdateDto dto) {
        return ResponseEntity.ok(boardService.updateBoard(dto,boardId));
    }
}

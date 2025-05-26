package com.example.demo.controller;

import com.example.demo.model.dto.Board.BoardRequestDto;
import com.example.demo.model.dto.Board.BoardResponseDto;
import com.example.demo.model.dto.Board.BoardSummaryDto;
import com.example.demo.model.dto.Board.BoardUpdateDto;
import com.example.demo.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시판 API", description = "게시글 CRUD 기능 제공 API")
@SecurityRequirement(name = "bearerAuth")  // 인증이 필요한 API 명시
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "페이징 처리된 게시글 요약 정보 목록을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Page<BoardSummaryDto>> boardList(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(boardService.getAllBoardSummaries(page));
    }

    @GetMapping("/main")
    @Operation(summary = "최근 게시물 3개 조회", description = "최근 게시물 중 3개를 제공합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Page<BoardSummaryDto>> boardThree() {
        return ResponseEntity.ok(boardService.getThreeBoardSummaries());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @Operation(summary = "새 게시글 작성", description = "새로운 게시글을 작성합니다. 인증이 필요한 작업입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 작성 성공",
                    content = @Content(schema = @Schema(implementation = BoardResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BoardResponseDto> boardPost(
            @Parameter(description = "게시글 작성 정보", required = true)
            @RequestBody @Valid BoardRequestDto dto,
            @Parameter(description = "인증된 사용자 ID", hidden = true)
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(boardService.saveBoard(dto, Integer.valueOf(userId)));
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object",
                                    example = "{\"board\": {\"id\": 1, \"title\": \"제목\", \"content\": \"내용\"}, \"userId\": 1}"))),
            @ApiResponse(responseCode = "404", description = "게시글 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Map<String, Object>> boardDetail(
            @Parameter(description = "조회할 게시글 ID", required = true, example = "1")
            @PathVariable Integer boardId,
            @Parameter(description = "인증된 사용자 ID (인증 안 된 경우 null)", hidden = true)
            @AuthenticationPrincipal String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("board", boardService.getBoardDetail(boardId));
        if(userId != null && !userId.equals("anonymousUser")) response.put("userId", Integer.valueOf(userId));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{boardId}")
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다. 인증이 필요한 작업입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> boardDelete(
            @Parameter(description = "삭제할 게시글 ID", required = true, example = "1")
            @PathVariable Integer boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/edit/{boardId}")
    @Operation(summary = "게시글 수정", description = "특정 게시글을 수정합니다. 인증이 필요한 작업입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = BoardResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "게시글 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BoardResponseDto> boardUpdate(
            @Parameter(description = "수정할 게시글 ID", required = true, example = "1")
            @PathVariable Integer boardId,
            @Parameter(description = "게시글 수정 정보", required = true)
            @RequestBody @Valid BoardUpdateDto dto) {
        return ResponseEntity.ok(boardService.updateBoard(dto, boardId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자 별 게시글 목록 조회", description = "페이징 처리된 사용자가 작성한 게시글 요약 정보 목록을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Page<BoardSummaryDto>> boardByUser(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "사용자 ID", hidden = true)
            @PathVariable Integer userId
            ) {
        return ResponseEntity.ok(boardService.getBoardsByUser(userId,page));
    }
}
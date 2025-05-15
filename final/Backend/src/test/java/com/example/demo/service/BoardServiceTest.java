package com.example.demo.service;

import com.example.demo.model.dto.Board.BoardResponseDto;
import com.example.demo.model.dto.Board.BoardSummaryDto;
import com.example.demo.model.dto.Board.BoardUpdateDto;
import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BoardService boardService;

    private User testUser;
    private Board testBoard1;
    private Board testBoard2;
    private BoardSummaryDto summaryDto1;
    private BoardSummaryDto summaryDto2;
    private List<BoardSummaryDto> summaryDtoList;
    private Page<BoardSummaryDto> summaryDtoPage;
    private BoardUpdateDto updateDto;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @BeforeEach
    void setUp() {
        // 테스트 유저 생성
        testUser = User.builder().
                id(1)
                .name("testUser")
                .email("test@example.com")
                .build();

        // 테스트 게시글 생성
        testBoard1 = new Board();
        testBoard1.setId(1);
        testBoard1.setTitle("테스트 게시글 1");
        testBoard1.setContent("테스트 내용 1");
        testBoard1.setUser(testUser);
        testBoard1.setHit(10);
        testBoard1.setCreatedAt(LocalDateTime.now());

        testBoard2 = new Board();
        testBoard2.setId(2);
        testBoard2.setTitle("테스트 게시글 2");
        testBoard2.setContent("테스트 내용 2");
        testBoard2.setUser(testUser);
        testBoard2.setHit(5);
        testBoard2.setCreatedAt(LocalDateTime.now().minusDays(1));

        // 테스트 DTO 생성
        summaryDto1 = new BoardSummaryDto(1, "테스트 게시글 1", 10, "testUser", LocalDateTime.now());
        summaryDto2 = new BoardSummaryDto(2, "테스트 게시글 2", 5, "testUser", LocalDateTime.now().minusDays(1));

        summaryDtoList = Arrays.asList(summaryDto1, summaryDto2);
        summaryDtoPage = new PageImpl<>(summaryDtoList);

        // 업데이트 DTO 생성
        updateDto = new BoardUpdateDto();
        updateDto.setTitle("수정된 제목");
        updateDto.setContent("수정된 내용");
    }

    @Test
    @DisplayName("모든 게시글 요약 정보를 기본 페이지 크기로 조회한다")
    void getAllBoardSummaries_DefaultSize() {
        // given
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("createdAt").descending());
        when(boardRepository.findAllForList(pageable)).thenReturn(summaryDtoPage);

        // when
        Page<BoardSummaryDto> result = boardService.getAllBoardSummaries(0);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 게시글 1");
        verify(boardRepository, times(1)).findAllForList(pageable);
    }

    @Test
    @DisplayName("모든 게시글 요약 정보를 사용자 지정 페이지 크기로 조회한다")
    void getAllBoardSummaries_CustomSize() {
        // given
        int customSize = 5;
        Pageable pageable = PageRequest.of(0, customSize, Sort.by("createdAt").descending());
        when(boardRepository.findAllForList(pageable)).thenReturn(summaryDtoPage);

        // when
        Page<BoardSummaryDto> result = boardService.getAllBoardSummaries(0, customSize);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(boardRepository, times(1)).findAllForList(pageable);
    }

    @Test
    @DisplayName("제목으로 게시글을 검색한다")
    void searchBoardsByTitle() {
        // given
        String keyword = "테스트";
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("createdAt").descending());
        when(boardRepository.findByTitleContaining(keyword, pageable)).thenReturn(summaryDtoPage);

        // when
        Page<BoardSummaryDto> result = boardService.searchBoardsByTitle(keyword, 0);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(boardRepository, times(1)).findByTitleContaining(keyword, pageable);
    }

    @Test
    @DisplayName("특정 사용자가 작성한 게시글을 조회한다")
    void getBoardsByUser() {
        // given
        Integer userId = 1;
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("createdAt").descending());
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(boardRepository.findByUser(testUser, pageable)).thenReturn(summaryDtoPage);

        // when
        Page<BoardSummaryDto> result = boardService.getBoardsByUser(userId, 0);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(userRepository, times(1)).findById(userId);
        verify(boardRepository, times(1)).findByUser(testUser, pageable);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 게시글을 조회하면 예외가 발생한다")
    void getBoardsByUser_UserNotFound() {
        // given
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            boardService.getBoardsByUser(userId, 0);
        });
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("게시글 상세 정보를 조회하고 조회수가 증가한다")
    void getBoardDetail() {
        // given
        Integer boardId = 1;
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(testBoard1));

        // when
        BoardResponseDto result = boardService.getBoardDetail(boardId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 게시글 1");
        assertThat(testBoard1.getHit()).isEqualTo(11); // 기존 10에서 1 증가
        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 상세 정보를 조회하면 예외가 발생한다")
    void getBoardDetail_BoardNotFound() {
        // given
        Integer boardId = 999;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            boardService.getBoardDetail(boardId);
        });
        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("새 게시글을 저장한다")
    void saveBoard() {
        // given
        Integer userId = 1;
        Board newBoard = new Board();
        newBoard.setTitle("새 게시글");
        newBoard.setContent("새 내용");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(boardRepository.save(any(Board.class))).thenReturn(newBoard);

        // when
        BoardResponseDto result = boardService.saveBoard(newBoard, userId);

        // then
        assertThat(result).isNotNull();
        assertThat(newBoard.getUser()).isEqualTo(testUser);
        verify(boardRepository, times(1)).save(newBoard);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 게시글을 저장하면 예외가 발생한다")
    void saveBoard_UserNotFound() {
        // given
        Integer userId = 999;
        Board newBoard = new Board();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            boardService.saveBoard(newBoard, userId);
        });
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("게시글을 삭제한다")
    void deleteBoard() {
        // given
        Integer boardId = 1;

        // when
        boardService.deleteBoard(boardId);

        // then
        verify(boardRepository, times(1)).deleteById(boardId);
    }

    @Test
    @DisplayName("게시글을 수정한다")
    void updateBoard() {
        // given
        Integer boardId = 1;
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(testBoard1));

        // when
        BoardResponseDto result = boardService.updateBoard(updateDto, boardId);

        // then
        assertThat(result).isNotNull();
        assertThat(testBoard1.getTitle()).isEqualTo("수정된 제목");
        assertThat(testBoard1.getContent()).isEqualTo("수정된 내용");
        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 수정을 시도하면 예외가 발생한다")
    void updateBoard_BoardNotFound() {
        // given
        Integer boardId = 999;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            boardService.updateBoard(updateDto, boardId);
        });
        verify(boardRepository, times(1)).findById(boardId);
    }
}
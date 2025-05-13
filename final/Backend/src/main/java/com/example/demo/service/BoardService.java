package com.example.demo.service;

import com.example.demo.model.dto.Board.BoardResponseDto;
import com.example.demo.model.dto.Board.BoardUpdateDto;
import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;
import com.example.demo.model.dto.Board.BoardSummaryDto;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 기본 페이지 크기 상수 정의
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 모든 게시글의 요약 정보를 페이징하여 조회
     *
     * @param page 페이지 번호 (0부터 시작)
     * @return 페이징된 게시글 요약 정보
     */
    public Page<BoardSummaryDto> getAllBoardSummaries(int page) {
        return getAllBoardSummaries(page, DEFAULT_PAGE_SIZE);
    }

    /**
     * 모든 게시글의 요약 정보를 페이징하여 조회 (페이지 크기 지정)
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 게시글 요약 정보
     */
    public Page<BoardSummaryDto> getAllBoardSummaries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return boardRepository.findAllForList(pageable);
    }

    /**
     * 제목으로 게시글을 검색하여 페이징된 결과 조회
     *
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (0부터 시작)
     * @return 검색 결과의 페이징된 게시글 요약 정보
     */
    public Page<BoardSummaryDto> searchBoardsByTitle(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("createdAt").descending());
        return boardRepository.findByTitleContaining(keyword, pageable);
    }

    /**
     * 특정 사용자가 작성한 게시글을 페이징하여 조회
     *
     * @param userId 사용자 id
     * @param page 페이지 번호 (0부터 시작)
     * @return 특정 사용자의 페이징된 게시글 요약 정보
     */
    public Page<BoardSummaryDto> getBoardsByUser(Integer userId, int page) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by("createdAt").descending());
        Optional<User> user=userRepository.findById(userId);
        return boardRepository.findByUser(user.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다.")), pageable);
    }

    /**
     * 게시글 상세 조회
     */
    public BoardResponseDto getBoardDetail(Integer id) {
        Board board=boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        incrementHit(board);
        return BoardResponseDto.from(board);
    }


    /**
     * 새 게시글 저장
     *
     * @param board 저장할 게시글 엔티티
     * @return 저장된 게시글 엔티티
     */
    @Transactional
    public BoardResponseDto saveBoard(Board board) {
        boardRepository.save(board);
        return BoardResponseDto.from(board);
    }

    /**
     * 게시글 삭제
     *
     * @param boardId 삭제할 게시글 ID
     */
    @Transactional
    public void deleteBoard(Integer boardId) {
        boardRepository.deleteById(boardId);
    }

    /**
     * 게시글 수정
     * @param updateDto 수정 정보
     * @param id 수정할 게시물 id
     */
    @Transactional
    public BoardResponseDto updateBoard(BoardUpdateDto updateDto, Integer id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        updateDto.updateEntity(board);
        return BoardResponseDto.from(board);
    }

    @Transactional
    private void incrementHit(Board board) {
        board.setHit(board.getHit() + 1);
    }
}
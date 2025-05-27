package com.example.demo.service;

import com.example.demo.model.dto.Board.BoardRequestDto;
import com.example.demo.model.dto.Board.BoardResponseDto;
import com.example.demo.model.dto.Board.BoardUpdateDto;
import com.example.demo.model.entity.BImage;
import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;
import com.example.demo.model.dto.Board.BoardSummaryDto;
import com.example.demo.repository.BImageRepository;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BImageRepository bImageRepository;
    private final ImageService imageService;
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
     * 메인 화면용 최근 3개 찾기
     *
     *
     * @return 최근 게시물 3개
     */
    public Page<BoardSummaryDto> getThreeBoardSummaries() {
        return getAllBoardSummaries(0, 3);
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
        User user=userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return boardRepository.findByUser(user, pageable);
    }

    /**
     * 게시글 상세 조회
     */
    public BoardResponseDto getBoardDetail(Integer id) {
        Board board=boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        board.setHit(board.getHit()+1);
        return BoardResponseDto.from(board);
    }

    /**
     * 새 게시글 저장 (이미지 포함)
     *
     * @param dto 저장할 게시글 dto
     * @param userId 사용자 ID
     * @return 저장된 게시글 정보
     */
    @Transactional
    public BoardResponseDto saveBoard(BoardRequestDto dto, Integer userId) {
        Board board = dto.toEntity();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        user.addBoard(board);
        board.setUser(user);
        board.setHit(0);
        board.setCreatedAt(LocalDateTime.now());

        // 게시글 먼저 저장
        Board savedBoard = boardRepository.save(board);

        // 이미지 파일명이 있으면 BImage 엔티티 생성 및 저장
        if (dto.getImageFileNames() != null && !dto.getImageFileNames().isEmpty()) {
            for (String fileName : dto.getImageFileNames()) {
                BImage bImage = BImage.builder()
                        .board(savedBoard)
                        .fileName(fileName)
                        .filePath("/api/images/view/" + fileName)
                        .build();

                savedBoard.addImage(bImage);
                bImageRepository.save(bImage);
            }
        }

        return BoardResponseDto.from(savedBoard);
    }

    /**
     * 게시글 삭제 (이미지도 함께 삭제)
     *
     * @param boardId 삭제할 게시글 ID
     */
    @Transactional
    public void deleteBoard(Integer boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 연관된 이미지들 삭제
        List<BImage> images = board.getImages();
        for (BImage image : images) {
            try {
                imageService.deleteImage(image.getFileName());
            } catch (Exception e) {
                // 파일 삭제 실패해도 DB에서는 삭제 진행
                System.err.println("파일 삭제 실패: " + image.getFileName() + ", 오류: " + e.getMessage());
            }
        }

        User user = board.getUser();
        if (user != null) {
            user.getBoards().remove(board);
        }
        board.setUser(null);

        boardRepository.deleteById(boardId);
    }

    /**
     * 게시글 수정 (이미지 수정 포함)
     * @param updateDto 수정 정보
     * @param id 수정할 게시물 id
     */
    @Transactional
    public BoardResponseDto updateBoard(BoardUpdateDto updateDto, Integer id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 기본 정보 업데이트
        updateDto.updateEntity(board);

        // 이미지 업데이트 (필요시 구현)
        if (updateDto.getImageFileNames() != null) {
            updateBoardImages(board, updateDto.getImageFileNames());
        }

        return BoardResponseDto.from(board);
    }

    /**
     * 게시글의 이미지 업데이트
     */
    @Transactional
    private void updateBoardImages(Board board, List<String> newImageFileNames) {
        // 기존 이미지들 삭제
        List<BImage> existingImages = board.getImages();
        for (BImage image : existingImages) {
            try {
                imageService.deleteImage(image.getFileName());
            } catch (Exception e) {
                System.err.println("기존 이미지 파일 삭제 실패: " + image.getFileName());
            }
        }

        // DB에서 기존 이미지 레코드 삭제
        bImageRepository.deleteAll(existingImages);
        board.getImages().clear();

        // 새 이미지들 추가
        if (newImageFileNames != null && !newImageFileNames.isEmpty()) {
            for (String fileName : newImageFileNames) {
                BImage bImage = BImage.builder()
                        .board(board)
                        .fileName(fileName)
                        .filePath("/api/images/view/" + fileName)
                        .build();

                board.addImage(bImage);
                bImageRepository.save(bImage);
            }
        }
    }
}
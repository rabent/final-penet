package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.Board.BoardSummaryDto;
import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;



@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    //게시판 리스트용 dto 페이징
    @Query("SELECT new com.example.demo.model.dto.Board.BoardListDto(b.id, b.title, b.hit, b.user, b.createdAt) FROM Board b")
    Page<BoardSummaryDto> findAllForList(Pageable pageable);

    // 제목 검색 결과를 페이징하여 가져오기(10개씩으로 구현 필요)
    @Query("SELECT new com.example.demo.model.dto.Board.BoardListDto(b.id, b.title, b.hit, b.user, b.createdAt) FROM Board b " + 
    "WHERE b.title LIKE %:keyword% ")
    Page<BoardSummaryDto> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    // 특정 사용자의 게시글을 페이징하여 가져오기
    @Query("SELECT new com.example.demo.model.dto.Board.BoardListDto(b.id, b.title, b.hit, b.user, b.createdAt) FROM Board b " + 
    "WHERE b.user=:user ")
    Page<BoardSummaryDto> findByUser(@Param("user") User user, Pageable pageable);  

    // 조회수 기준으로 내림차순 정렬하여 상위 N개 게시글 찾기(옵션)
    @Query("SELECT b FROM Board b ORDER BY b.hit DESC")
    List<Board> findTopNByOrderByHitDesc(Pageable pageable);
    
    // 특정 사용자가 작성한 게시글 수 카운트
    Long countByUser(User user);

    
}
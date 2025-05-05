package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;



@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    // 특정 사용자가 작성한 모든 게시글 찾기
    List<Board> findAllByUser(User user);
    
    // 제목에 특정 키워드가 포함된 게시글 찾기
    List<Board> findByTitleContaining(String keyword);
    
    // 조회수 기준으로 내림차순 정렬하여 상위 N개 게시글 찾기
    @Query("SELECT b FROM Board b ORDER BY b.hit DESC")
    List<Board> findTopNByOrderByHitDesc(Pageable pageable);
    
    // 특정 사용자가 작성한 게시글 수 카운트
    Long countByUser(User user);
}
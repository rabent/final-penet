package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.BImage;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BImageRepository extends JpaRepository<BImage, Integer> {
    /**
     * 특정 게시글의 모든 이미지 조회
     */
    List<BImage> findByBoard(Board board);

    // 필요하다면 게시글 ID로 이미지 조회
    List<BImage> findByBoardId(Integer boardId);

    /**
     * 파일명으로 이미지 조회
     */
    BImage findByFileName(String fileName);

    /**
     * 특정 게시글의 모든 이미지 삭제
     */
    // 필요하다면 게시글 ID로 삭제
    void deleteByBoardId(Integer boardId);

    /**
     * 파일명으로 이미지 삭제
     */
    void deleteByFileName(String fileName);

}
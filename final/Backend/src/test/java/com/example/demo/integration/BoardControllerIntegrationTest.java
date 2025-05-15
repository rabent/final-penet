package com.example.demo.integration;

import com.example.demo.model.dto.Board.BoardRequestDto;
import com.example.demo.model.dto.Board.BoardUpdateDto;
import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Board testBoard;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        boardRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트 사용자 생성
        testUser = User.builder()
                .name("테스트 사용자")
                .password("password123")
                .role("USER")
                .email("test@example.com")
                .address("서울시 테스트구")
                .number("010-1234-5678")
                .build();
        testUser = userRepository.save(testUser);

        // 테스트 게시글 생성
        testBoard = Board.builder()
                .title("테스트 게시글")
                .content("테스트 내용입니다.")
                .hit(0)
                .createdAt(LocalDateTime.now())
                .user(testUser)
                .build();
        testBoard = boardRepository.save(testBoard);
        testUser.getBoards().add(testBoard);
    }

    @Test
    void boardList_returnsPageOfBoardSummaries() throws Exception {
        mockMvc.perform(get("/boards")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", is("테스트 게시글")));
    }

    @Test
    void boardPost_createsNewBoard() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "새 게시글");
        params.add("content", "새 게시글 내용입니다.");

        mockMvc.perform(post("/boards")
                .params(params)
                .param("id", testUser.getId().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("새 게시글")))
                .andExpect(jsonPath("$.content", is("새 게시글 내용입니다.")));
    }

    @Test
    void boardDetail_returnsBoardDetails() throws Exception {
        mockMvc.perform(get("/boards/{boardId}", testBoard.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("테스트 게시글")))
                .andExpect(jsonPath("$.content", is("테스트 내용입니다.")))
                .andExpect(jsonPath("$.username", is("테스트 사용자")));
    }

    @Test
    void boardUpdate_updatesExistingBoard() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "수정된 게시글");
        params.add("content", "수정된 내용입니다.");

        mockMvc.perform(put("/boards/{boardId}", testBoard.getId())
                .params(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("수정된 게시글")))
                .andExpect(jsonPath("$.content", is("수정된 내용입니다.")));
    }

    @Test
    void boardDelete_deletesBoard() throws Exception {
        mockMvc.perform(delete("/boards/{boardId}", testBoard.getId()))
                .andExpect(status().isNoContent());

        // 삭제 확인
        mockMvc.perform(get("/boards/{boardId}", testBoard.getId()))
                .andExpect(status().isNotFound());
    }
}
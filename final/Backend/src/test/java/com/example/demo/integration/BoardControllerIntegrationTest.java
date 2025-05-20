package com.example.demo.integration;

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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.is;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional
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
    private Authentication authentication;

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
        authentication = new TestingAuthenticationToken(testUser.getId().toString(), null, "ROLE_USER");
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
        Map<String, String> boardRequest = new HashMap<>();
        boardRequest.put("title", "새 게시글");
        boardRequest.put("content", "새 게시글 내용입니다.");

        // ObjectMapper를 사용하여 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(boardRequest);


        mockMvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("새 게시글")))
                .andExpect(jsonPath("$.content", is("새 게시글 내용입니다.")));
    }

    @Test
    void boardDetail_returnsBoardDetails() throws Exception {
        mockMvc.perform(get("/boards/{boardId}", testBoard.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.board.title", is("테스트 게시글")))
                .andExpect(jsonPath("$.board.content", is("테스트 내용입니다.")))
                .andExpect(jsonPath("$.board.username", is("테스트 사용자")));
    }

    @Test
    void boardUpdate_updatesExistingBoard() throws Exception {
        Map<String, String> boardRequest = new HashMap<>();
        boardRequest.put("title", "수정된 게시글");
        boardRequest.put("content", "수정된 내용입니다.");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(boardRequest);
        mockMvc.perform(put("/boards/edit/{boardId}", testBoard.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .with(authentication(authentication))
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("수정된 게시글")))
                .andExpect(jsonPath("$.content", is("수정된 내용입니다.")));
    }

    @Test
    void boardDelete_deletesBoard() throws Exception {
        mockMvc.perform(delete("/boards/{boardId}", testBoard.getId())
                .with(authentication(authentication)))
                .andExpect(status().isNoContent());

        // 삭제 확인
        mockMvc.perform(get("/boards/{boardId}", testBoard.getId()))
                .andExpect(status().isNotFound());
    }
}
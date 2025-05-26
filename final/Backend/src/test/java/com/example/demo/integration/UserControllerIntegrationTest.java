package com.example.demo.integration;

import com.example.demo.model.dto.User.UserRequestDto;
import com.example.demo.model.dto.User.UserUpdateDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
//@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 기존 테스트 데이터 삭제
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
    }

    @Test
    void registerUser_createsNewUser() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("새 사용자")
                .password("newpass123")
                .role("USER")
                .email("new@example.com")
                .address("부산시 테스트구")
                .number("010-9876-5432")
                .build();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.any(String.class))); // ID가 반환되는지 확인
    }

    @Test
    void detailUser_returnsUserDetails() throws Exception {
        mockMvc.perform(get("/users/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("테스트 사용자")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    void updateUser_updatesExistingUser() throws Exception {
        Map<String, String> boardRequest = new HashMap<>();
        boardRequest.put("name", "수정된 사용자");
        boardRequest.put("address", "수정된 주소");
        boardRequest.put("number", "010-1111-2222");
        // ObjectMapper를 사용하여 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(boardRequest);

        mockMvc.perform(put("/users/{userId}", testUser.getId())
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("수정된 사용자")))
                .andExpect(jsonPath("$.address", is("수정된 주소")))
                .andExpect(jsonPath("$.number", is("010-1111-2222")));
    }

    @Test
    void deleteUser_deletesUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", testUser.getId()))
                .andExpect(status().isNoContent());

        // 삭제 확인
        mockMvc.perform(get("/users/{userId}", testUser.getId()))
                .andExpect(status().isNotFound());
    }
}
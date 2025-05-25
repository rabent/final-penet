package com.example.demo.repository;

import com.example.demo.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // 테스트용 User 엔티티 생성
        user1 = User.builder()
                .name("홍길동")
                .password("password123")
                .role("USER")
                .email("hong@example.com")
                .address("서울시 강남구")
                .number("010-1234-5678")
                .build();

        user2 = User.builder()
                .name("김철수")
                .password("password456")
                .role("ADMIN")
                .email("kim@example.com")
                .address("서울시 서초구")
                .number("010-8765-4321")
                .build();

        // TestEntityManager를 통해 사용자 저장
        entityManager.persist(user1);
        entityManager.persist(user2);
        // 변경 사항을 즉시 데이터베이스에 반영
        entityManager.flush();
    }

    @Test
    @DisplayName("이메일로 사용자 찾기 테스트")
    void findByEmailTest() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("hong@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("홍길동", foundUser.get().getName());
        assertEquals("USER", foundUser.get().getRole());
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자 찾기 테스트")
    void findByEmailNotFoundTest() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("notexist@example.com");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("이름으로 사용자 찾기 테스트")
    void findByNameTest() {
        // When
        Optional<User> foundUser = userRepository.findByName("김철수");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("kim@example.com", foundUser.get().getEmail());
        assertEquals("ADMIN", foundUser.get().getRole());
    }

    @Test
    @DisplayName("이메일과 비밀번호로 사용자 찾기 테스트(로그인 상황)")
    void findByEmailAndPasswordTest() {
        // When
        Optional<User> foundUser = userRepository.findByEmailAndPassword("hong@example.com", "password123");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("홍길동", foundUser.get().getName());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 사용자 찾기 테스트(로그인 실패)")
    void findByEmailAndWrongPasswordTest() {
        // When
        Optional<User> foundUser = userRepository.findByEmailAndPassword("hong@example.com", "wrongpassword");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("특정 역할을 가진 모든 사용자 찾기 테스트")
    void findAllByRoleTest() {
        // When
        List<User> users = userRepository.findAllByRole("USER");

        // Then
        assertEquals(1, users.size());
        assertEquals("홍길동", users.get(0).getName());
    }

    @Test
    @DisplayName("사용자 추가 테스트")
    void saveUserTest() {
        // Given
        User newUser = User.builder()
                .name("박지성")
                .password("password789")
                .role("USER")
                .email("park@example.com")
                .address("서울시 마포구")
                .number("010-2345-6789")
                .build();

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertNotNull(savedUser.getId());

        // 저장된 사용자 조회 테스트
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("박지성", foundUser.get().getName());
        assertEquals("park@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("사용자 삭제 테스트")
    void deleteUserTest() {
        // When
        userRepository.deleteById(user1.getId());

        // Then
        Optional<User> deletedUser = userRepository.findById(user1.getId());
        assertFalse(deletedUser.isPresent());

        // 다른 사용자는 여전히 존재하는지 확인
        Optional<User> existingUser = userRepository.findById(user2.getId());
        assertTrue(existingUser.isPresent());
    }

    @Test
    @DisplayName("사용자 정보 업데이트 테스트")
    void updateUserTest() {
        // Given
        User userToUpdate = userRepository.findById(user1.getId()).orElseThrow();

        // When
        userToUpdate.setName("홍길순");
        userToUpdate.setAddress("서울시 송파구");
        userRepository.save(userToUpdate);

        // Then
        User updatedUser = userRepository.findById(user1.getId()).orElseThrow();
        assertEquals("홍길순", updatedUser.getName());
        assertEquals("서울시 송파구", updatedUser.getAddress());
        // 변경하지 않은 필드는 그대로인지 확인
        assertEquals("hong@example.com", updatedUser.getEmail());
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 테스트")
    void emailExistsTest() {
        // Given - 이미 setUp()에서 사용자 데이터가 준비됨

        // When & Then
        assertTrue(userRepository.findByEmail("hong@example.com").isPresent());
        assertFalse(userRepository.findByEmail("nonexistent@example.com").isPresent());
    }
}
package com.example.demo.repository;

import com.example.demo.model.dto.Board.BoardSummaryDto;
import com.example.demo.model.entity.Board;
import com.example.demo.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BoardRepository boardRepository;

    // 테스트용 사용자 데이터
    private User user1;
    private User user2;

    // 테스트용 게시글 데이터
    private Board board1;
    private Board board2;
    private Board board3;
    private Board board4;

    @BeforeEach
    void setUp() {
        boardRepository.deleteAll();
        // 테스트 사용자 생성
        user1 = User.builder()
                .name("홍길동")
                .email("hong@example.com")
                .password("password123")
                .role("USER")
                .address("서울시 강남구")
                .number("010-1234-5678")
                .build();

        user2 = User.builder()
                .name("김철수")
                .email("kim@example.com")
                .password("password456")
                .role("USER")
                .address("서울시 서초구")
                .number("010-9876-5432")
                .build();

        // 테스트 엔티티 저장
        user1 = entityManager.persist(user1);
        user2 = entityManager.persist(user2);

        // 테스트 게시글 생성
        board1 = Board.builder()
                .title("첫 번째 게시글")
                .content("첫 번째 게시글 내용입니다.")
                .hit(10)
                .user(user1)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();

        board2 = Board.builder()
                .title("두 번째 게시글")
                .content("두 번째 게시글 내용입니다.")
                .hit(5)
                .user(user1)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        board3 = Board.builder()
                .title("세 번째 게시글 - 여행 후기")
                .content("여행 후기 게시글 내용입니다.")
                .hit(20)
                .user(user2)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        board4 = Board.builder()
                .title("공지사항: 사이트 점검 안내")
                .content("사이트 점검 관련 공지사항입니다.")
                .hit(30)
                .user(user2)
                .createdAt(LocalDateTime.now())
                .build();

        // 테스트 게시글 저장
        entityManager.persist(board1);
        entityManager.persist(board2);
        entityManager.persist(board3);
        entityManager.persist(board4);
        entityManager.flush();
    }

    @Test
    @DisplayName("모든 게시글 목록을 페이징하여 조회")
    void findAllForList() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<BoardSummaryDto> result = boardRepository.findAllForList(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("공지사항: 사이트 점검 안내"); // 최신순 정렬
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("세 번째 게시글 - 여행 후기");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("두 번째 게시글");
        assertThat(result.getContent().get(3).getTitle()).isEqualTo("첫 번째 게시글");
    }

    @Test
    @DisplayName("키워드로 게시글 제목 검색")
    void findByTitleContaining() {
        // given
        String keyword = "게시글";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BoardSummaryDto> result = boardRepository.findByTitleContaining(keyword, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3); // "게시글"이 포함된 제목 3개
        
        // 검색 결과 제목 확인
        List<String> titles = result.getContent().stream()
                .map(BoardSummaryDto::getTitle)
                .toList();
        assertThat(titles).containsExactlyInAnyOrder(
                "첫 번째 게시글", 
                "두 번째 게시글", 
                "세 번째 게시글 - 여행 후기"
        );
    }
    
    @Test
    @DisplayName("특정 키워드로 게시글 검색 - 결과 없음")
    void findByTitleContainingNoResults() {
        // given
        String keyword = "존재하지않는키워드";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BoardSummaryDto> result = boardRepository.findByTitleContaining(keyword, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 사용자의 게시글 조회")
    void findByUser() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BoardSummaryDto> result = boardRepository.findByUser(user1, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2); // user1의 게시글 2개
        
        // 사용자의 게시글 제목 확인
        List<String> titles = result.getContent().stream()
                .map(BoardSummaryDto::getTitle)
                .toList();
        assertThat(titles).containsExactlyInAnyOrder("첫 번째 게시글", "두 번째 게시글");
        
        // 모든 게시글의 작성자가 user1인지 확인
        result.getContent().forEach(dto -> {
            assertThat(dto.getUser()).isEqualTo(user1.getName());
        });
    }

    @Test
    @DisplayName("조회수 기준 내림차순 정렬하여 상위 N개 게시글 조회")
    void findTopNByOrderByHitDesc() {
        // given
        Pageable topThree = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "hit"));

        // when
        List<Board> result = boardRepository.findTopNByOrderByHitDesc(topThree);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3); // 상위 3개 게시글
        
        // 조회수 내림차순 정렬 확인
        assertThat(result.get(0).getHit()).isEqualTo(30); // 가장 높은 조회수
        assertThat(result.get(1).getHit()).isEqualTo(20);
        assertThat(result.get(2).getHit()).isEqualTo(10);
        
        // 제목 확인
        assertThat(result.get(0).getTitle()).isEqualTo("공지사항: 사이트 점검 안내");
        assertThat(result.get(1).getTitle()).isEqualTo("세 번째 게시글 - 여행 후기");
        assertThat(result.get(2).getTitle()).isEqualTo("첫 번째 게시글");
    }

    @Test
    @DisplayName("특정 사용자가 작성한 게시글 수 카운트")
    void countByUser() {
        // when
        Long user1Count = boardRepository.countByUser(user1);
        Long user2Count = boardRepository.countByUser(user2);

        // then
        assertThat(user1Count).isEqualTo(2); // user1의 게시글 수
        assertThat(user2Count).isEqualTo(2); // user2의 게시글 수
    }

    @Test
    @DisplayName("페이징 및 정렬 테스트")
    void testPagingAndSorting() {
        // given - 한 페이지에 2개씩 (현재는 createdAt DESC가 우선, hit DESC가 2차 정렬)
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "hit"));

        // when
        Page<BoardSummaryDto> result = boardRepository.findAllForList(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2); // 페이지당 2개
        assertThat(result.getTotalElements()).isEqualTo(4); // 전체 게시글 4개
        assertThat(result.getTotalPages()).isEqualTo(2); // 총 2페이지

        // 실제 정렬 순서: createdAt DESC 우선, hit DESC는 2차 정렬
        // 첫 페이지: board4(오늘, hit=30), board3(1일전, hit=20)
        assertThat(result.getContent().get(0).getHit()).isEqualTo(30);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("공지사항: 사이트 점검 안내");
        assertThat(result.getContent().get(1).getHit()).isEqualTo(20);
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("세 번째 게시글 - 여행 후기");

        // 두 번째 페이지 확인
        Pageable secondPage = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "hit"));
        Page<BoardSummaryDto> secondPageResult = boardRepository.findAllForList(secondPage);

        secondPageResult.getContent().forEach(board ->
                System.out.println("Title: " + board.getTitle() + ", Hit: " + board.getHit() + ", CreatedAt: " + board.getCreatedAt())
        );

        assertThat(secondPageResult.getContent()).hasSize(2);

        // 두 번째 페이지: board2(2일전, hit=5), board1(3일전, hit=10)
        // createdAt이 우선이므로 더 최근인 board2가 먼저 나옴
        assertThat(secondPageResult.getContent().get(0).getHit()).isEqualTo(5);
        assertThat(secondPageResult.getContent().get(0).getTitle()).isEqualTo("두 번째 게시글");
        assertThat(secondPageResult.getContent().get(1).getHit()).isEqualTo(10);
        assertThat(secondPageResult.getContent().get(1).getTitle()).isEqualTo("첫 번째 게시글");
    }
}
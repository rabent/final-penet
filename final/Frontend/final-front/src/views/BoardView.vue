<template>
  <div class="board-container">
    <h1 class="board-title">게시판</h1>

    <div class="board-filters">
      <div class="search-container">
        <input
          type="text"
          placeholder="검색어를 입력하세요"
          class="search-input"
          v-model="searchQuery"
          @keyup.enter="search"
        />
        <button class="search-button" @click="search">검색</button>
      </div>
      <select class="filter-select" v-model="sortBy" @change="loadPosts">
        <option value="latest">최신순</option>
        <option value="views">조회순</option>
      </select>
    </div>

    <div class="board-list">
      <table class="board-table">
        <thead>
          <tr>
            <th class="col-num">번호</th>
            <th class="col-title">제목</th>
            <th class="col-author">작성자</th>
            <th class="col-date">작성일</th>
            <th class="col-views">조회수</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in posts" :key="post.id" @click="viewPost(post.id)" class="post-row">
            <td>{{ post.id }}</td>
            <td class="post-title">{{ post.title }}</td>
            <td>{{ post.author }}</td>
            <td>{{ formatDate(post.createTime) }}</td>
            <td>{{ post.viewCount }}</td>
          </tr>
          <tr v-if="posts.length === 0">
            <td colspan="5" class="no-posts">게시글이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <button
        class="pagination-button"
        :disabled="currentPage === 1"
        @click="goToPage(currentPage - 1)"
      >
        이전
      </button>
      <div class="page-numbers">
        <button
          v-for="page in pageNumbers"
          :key="page"
          class="page-number"
          :class="{ active: page === currentPage }"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
      </div>
      <button
        class="pagination-button"
        :disabled="currentPage === totalPages"
        @click="goToPage(currentPage + 1)"
      >
        다음
      </button>
    </div>

    <div class="actions">
      <router-link to="/board/write" class="write-button">글쓰기</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const posts = ref([]);
const currentPage = ref(1);
const pageSize = 10;
const totalPosts = ref(0);
const searchQuery = ref('');
const sortBy = ref('latest');

// 페이지 내비게이션에 표시할 페이지 번호
const pageNumbers = computed(() => {
  const totalPages = Math.ceil(totalPosts.value / pageSize);
  // 최대 5개의 페이지 번호만 표시
  const start = Math.max(1, currentPage.value - 2);
  const end = Math.min(totalPages, start + 4);

  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

const totalPages = computed(() => {
  return Math.ceil(totalPosts.value / pageSize);
});

// 게시글 불러오기
const loadPosts = async () => {
  try {
    // 실제 환경에서는 API 호출
    // const response = await axios.get('http://localhost:8080/api/boards', {
    //   params: {
    //     page: currentPage.value - 1,
    //     size: pageSize,
    //     sort: sortBy.value,
    //     query: searchQuery.value
    //   }
    // });

    // TODO: 백엔드 API 완성 후 주석 해제
    // posts.value = response.data.content;
    // totalPosts.value = response.data.totalElements;

    // 임시 데이터 (백엔드 연동 전까지 사용)
    const dummyPosts = [
      { id: 10, title: '게시판 테스트 입니다', author: '관리자', createTime: '2025-05-18T12:00:00', viewCount: 42 },
      { id: 9, title: '여행 정보 공유합니다', author: '여행자123', createTime: '2025-05-15T15:30:00', viewCount: 28 },
      { id: 8, title: '제주도 맛집 추천해주세요', author: '맛집탐험가', createTime: '2025-05-10T09:45:00', viewCount: 76 },
      { id: 7, title: '서울 근교 당일치기 여행', author: '주말여행러', createTime: '2025-05-08T18:12:00', viewCount: 35 },
      { id: 6, title: '여름 휴가 계획 공유', author: '방학맞이', createTime: '2025-05-05T11:24:00', viewCount: 19 },
      { id: 5, title: '해외여행 필수 준비물', author: '세계일주', createTime: '2025-05-03T14:08:00', viewCount: 88 },
      { id: 4, title: '국내 캠핑장 추천', author: '캠핑러버', createTime: '2025-05-01T10:15:00', viewCount: 45 },
      { id: 3, title: '혼자 떠나는 여행 꿀팁', author: '솔로트래블러', createTime: '2025-04-29T16:30:00', viewCount: 92 },
      { id: 2, title: '가족 여행지 추천', author: '행복한가족', createTime: '2025-04-25T13:20:00', viewCount: 51 },
      { id: 1, title: '첫 번째 공지사항입니다', author: '관리자', createTime: '2025-04-20T09:00:00', viewCount: 120 },
    ];

    posts.value = dummyPosts;
    totalPosts.value = 23; // 총 23개의 게시글이 있다고 가정
  } catch (error) {
    console.error('게시글 로드 실패:', error);
  }
};

// 날짜 포맷 함수
const formatDate = (dateString) => {
  const date = new Date(dateString);
  const today = new Date();

  // 오늘 날짜인 경우 시간만 표시
  if (date.toDateString() === today.toDateString()) {
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }

  // 그 외 날짜는 YYYY-MM-DD 형식으로 표시
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};

// 페이지 이동
const goToPage = (page) => {
  currentPage.value = page;
  loadPosts();
};

// 검색 기능
const search = () => {
  currentPage.value = 1; // 검색 시 첫 페이지로 이동
  loadPosts();
};

// 게시글 상세 보기
const viewPost = (postId) => {
  router.push(`/board/post/${postId}`);
};

// 컴포넌트 마운트 시 게시글 로드
onMounted(() => {
  loadPosts();
});
</script>

<style scoped>
.board-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.board-title {
  font-size: 28px;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
}

.board-filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-container {
  display: flex;
  max-width: 400px;
}

.search-input {
  flex: 1;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
}

.search-button {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  border-radius: 0 4px 4px 0;
  transition: background-color 0.3s;
}

.search-button:hover {
  background-color: #2980b9;
}

.filter-select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.board-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 30px;
}

.board-table th,
.board-table td {
  padding: 15px;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.board-table th {
  background-color: #f9f9f9;
  font-weight: 600;
  color: #333;
}

.post-row {
  cursor: pointer;
  transition: background-color 0.3s;
}

.post-row:hover {
  background-color: #f5f5f5;
}

.post-title {
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 400px;
}

.col-num {
  width: 10%;
}

.col-title {
  width: 50%;
}

.col-author {
  width: 15%;
}

.col-date {
  width: 15%;
}

.col-views {
  width: 10%;
}

.no-posts {
  text-align: center;
  padding: 50px 0;
  color: #888;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 30px 0;
}

.pagination-button {
  padding: 8px 15px;
  margin: 0 5px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.pagination-button:hover:not(:disabled) {
  background-color: #eee;
}

.pagination-button:disabled {
  color: #bbb;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  margin: 0 10px;
}

.page-number {
  width: 35px;
  height: 35px;
  margin: 0 5px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-number:hover:not(.active) {
  background-color: #f5f5f5;
}

.page-number.active {
  background-color: #3498db;
  color: white;
  border-color: #3498db;
}

.actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.write-button {
  display: inline-block;
  background-color: #2ecc71;
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.3s;
}

.write-button:hover {
  background-color: #27ae60;
}

@media (max-width: 768px) {
  .board-filters {
    flex-direction: column;
    gap: 10px;
  }

  .search-container {
    width: 100%;
    max-width: 100%;
  }

  .filter-select {
    width: 100%;
  }

  .col-num, .col-date, .col-views {
    display: none;
  }

  .col-title {
    width: 70%;
  }

  .col-author {
    width: 30%;
  }

  .post-title {
    max-width: 200px;
  }
}
</style>

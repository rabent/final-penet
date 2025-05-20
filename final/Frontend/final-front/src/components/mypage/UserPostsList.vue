<template>
  <div class="user-posts-list">
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
    </div>
    <div v-else-if="error" class="error-container">
      <p>{{ error }}</p>
      <button @click="fetchUserPosts" class="retry-btn">다시 시도</button>
    </div>
    <div v-else-if="posts.length === 0" class="empty-container">
      <p>작성한 게시글이 없습니다.</p>
    </div>
    <div v-else class="posts-container">
      <div v-for="post in posts" :key="post.id" class="post-item">
        <div class="post-header">
          <h3 class="post-title">{{ post.title }}</h3>
          <span class="post-date">{{ formatDate(post.createdDate) }}</span>
        </div>
        <p class="post-excerpt">{{ truncateContent(post.content) }}</p>
        <div class="post-footer">
          <router-link :to="`/board/post/${post.id}`" class="view-post-btn">
            자세히 보기
          </router-link>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="pagination" v-if="totalPages > 1">
        <button
          @click="changePage(currentPage - 1)"
          :disabled="currentPage === 1"
          class="page-btn prev"
        >
          이전
        </button>

        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>

        <button
          @click="changePage(currentPage + 1)"
          :disabled="currentPage === totalPages"
          class="page-btn next"
        >
          다음
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '@/utils/axios';


const props = defineProps({
  userId: {
    type: String,
    required: true
  }
})

// 상태 관리
const posts = ref([])
const loading = ref(true)
const error = ref(null)
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 5 // 한 페이지에 표시할 게시글 수

// userId가 변경되면 게시글 다시 불러오기
watch(() => props.userId, () => {
  if (props.userId) {
    fetchUserPosts()
  }
})

// 컴포넌트 마운트 시 게시글 불러오기
onMounted(() => {
  if (props.userId) {
    fetchUserPosts()
  }
})

// 사용자 게시글 조회
const fetchUserPosts = async () => {
  if (!props.userId) return

  loading.value = true
  error.value = null

  try {
    const response = await api.get(`http://localhost:8080/api/boards/user/${props.userId}`, {
      params: {
        page: currentPage.value - 1, // 백엔드는 0부터 시작하는 페이지 인덱스 사용
        size: pageSize
      },
      headers: {
        Authorization: sessionStorage.getItem('token')
      }
    })

    // 응답 데이터 형식에 따라 조정 필요
    posts.value = response.data.content || response.data || []
    totalPages.value = response.data.totalPages || 1

  } catch (error) {
    console.error('게시글 조회 실패:', error)
    error.value = '게시글을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// 페이지 변경
const changePage = (newPage) => {
  if (newPage < 1 || newPage > totalPages.value) return

  currentPage.value = newPage
  fetchUserPosts()
}

// 내용 길이 제한
const truncateContent = (content) => {
  if (!content) return ''
  return content.length > 100 ? content.slice(0, 100) + '...' : content
}

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return ''

  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}
</script>

<style scoped>
.user-posts-list {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  padding: 20px;
  min-height: 300px;
  position: relative;
}

.loading-container,
.error-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.retry-btn {
  margin-top: 15px;
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.post-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.post-item:last-child {
  border-bottom: none;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.post-title {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.post-date {
  font-size: 14px;
  color: #888;
}

.post-excerpt {
  color: #666;
  margin-bottom: 15px;
  line-height: 1.5;
}

.post-footer {
  display: flex;
  justify-content: flex-end;
}

.view-post-btn {
  padding: 6px 12px;
  background-color: #f0f0f0;
  color: #333;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  transition: background-color 0.3s;
}

.view-post-btn:hover {
  background-color: #e0e0e0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 15px;
}

.page-btn {
  padding: 6px 12px;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.page-btn:disabled {
  background-color: #f9f9f9;
  color: #bbb;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}
</style>

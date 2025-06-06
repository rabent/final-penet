<template>
  <div class="detail-container">
    <div v-if="isLoading" class="loading">
      <p>게시글을 불러오는 중입니다...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>{{ error }}</p>
      <button @click="goBackToList" class="back-button">목록으로 돌아가기</button>
    </div>

    <div v-else class="post-detail">
      <h1 class="post-title">{{ post.title }}</h1>

      <div class="post-info">
        <div class="post-author-info">
          <span class="author">{{ post.username }}</span>
          <span class="date">{{ formatDate(post.createdAt) }}</span>
        </div>
        <div class="post-stats">
          <span class="views">조회 {{ post.hit }}</span>
        </div>
      </div>

      <div class="post-content">
        <p v-for="(paragraph, index) in contentParagraphs" :key="index">
          {{ paragraph }}
        </p>

        <!-- 이미지 갤러리 추가 -->
        <div v-if="post.images && post.images.length > 0" class="image-gallery">
          <div v-for="(image, index) in post.images" :key="index" class="image-item">
            <img
              :src="`http://localhost:8080/api/images/view/${image.fileName}`"
              :alt="`게시글 이미지 ${index + 1}`"
              @error="handleImageError"
              @click="openImageModal(image)"
              class="gallery-image"
            />
          </div>
        </div>

        <!-- 이미지 모달 -->
        <div v-if="selectedImage" class="image-modal" @click="closeImageModal">
          <div class="modal-content" @click.stop>
            <img
              :src="`http://localhost:8080/api/images/view/${selectedImage.fileName}`"
              :alt="selectedImage.originalFileName"
            />
            <button class="modal-close" @click="closeImageModal">&times;</button>
          </div>
        </div>
      </div>

      <div class="post-actions">
        <div class="left-actions">
          <button @click="goBackToList" class="list-button">목록</button>
        </div>
        <div class="right-actions" v-if="isAuthor">
          <button @click="editPost" class="edit-button">수정</button>
          <button @click="confirmDelete" class="delete-button">삭제</button>
        </div>
      </div>

      <!-- 댓글 섹션 -->
      <div class="comments-section">
        <h3 class="comments-title">댓글 {{ comments.length }}개</h3>

        <div class="comment-form">
          <textarea
            v-model="newComment"
            placeholder="댓글을 작성하세요"
            class="comment-input"
            rows="3"
          ></textarea>
          <button
            @click="submitComment"
            class="comment-submit"
            :disabled="!newComment.trim()"
          >
            등록
          </button>
        </div>

        <div class="comments-list">
          <div v-if="comments.length === 0" class="no-comments">
            <p>첫 번째 댓글을 작성해보세요!</p>
          </div>

          <div v-else>
            <div v-for="comment in comments" :key="comment.id" class="comment">
              <div class="comment-header">
                <span class="comment-author">{{ comment.author }}</span>
                <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
              </div>
              <div class="comment-body">
                {{ comment.content }}
              </div>
              <div class="comment-actions" v-if="comment.authorId === currentUserId">
                <button @click="deleteComment(comment.id)" class="comment-delete">삭제</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/utils/axios';

const route = useRoute();
const router = useRouter();
const postId = route.params.id;

const post = ref({});
const comments = ref([]);
const newComment = ref('');
const isLoading = ref(true);
const error = ref(null);
const currentUserId = ref(null);

// 게시글 내용을 단락으로 분리
const contentParagraphs = computed(() => {
  if (!post.value.content) return [];
  return post.value.content.split('\n').filter(paragraph => paragraph.trim());
});

// 현재 사용자가 게시글 작성자인지 확인
const isAuthor = computed(() => {
  console.log('현재 사용자:', currentUserId.value);
  console.log('게시글 작성자:', post.value.authorId);
  return currentUserId.value && post.value.authorId === currentUserId.value;
});

// 게시글 불러오기
const loadPost = async () => {
  try {
    isLoading.value = true;
    error.value = null;

    const response = await api.get(`boards/${postId}`);
    post.value = response.data.board;
    currentUserId.value = response.data.userId;

    console.log('게시글 데이터:', post.value); // 디버깅용
    console.log('현재 로그인한 사용자 ID:', currentUserId.value); // 디버깅용

    isLoading.value = false;
  } catch (err) {
    console.error('게시글 로드 실패:', err);
    error.value = '게시글을 불러오는 중 오류가 발생했습니다.';
    isLoading.value = false;
  }
};

// 댓글 불러오기
const loadComments = async () => {
  try {
    // 실제 환경에서는 API 호출
    // const response = await axios.get(`/boards/${postId}/comments`);
    // comments.value = response.data;

    // 임시 데이터 (백엔드 연동 전까지 사용)
    comments.value = [
      {
        id: 1,
        content: '정말 좋은 여행 후기네요! 저도 제주도에 꼭 가보고 싶어졌어요.',
        author: '여행계획중',
        createTime: '2025-05-15T16:30:00',
        authorId: '2'
      },
      {
        id: 2,
        content: '우도는 저도 가봤는데 정말 좋았어요! 자전거 타고 돌아다니는 것도 추천합니다.',
        author: '제주마니아',
        createTime: '2025-05-16T10:15:00',
        authorId: '3'
      },
      {
        id: 3,
        content: '혹시 렌트카는 어디서 빌리셨나요? 가격대가 어떻게 되나요?',
        author: '여행초보자',
        createTime: '2025-05-16T14:22:00',
        authorId: '4'
      }
    ];
  } catch (err) {
    console.error('댓글 로드 실패:', err);
  }
};

// 댓글 등록
const submitComment = async () => {
  if (!newComment.value.trim()) return;

  if (!currentUserId.value) {
    alert('로그인 후 댓글을 작성할 수 있습니다.');
    router.push('/login');
    return;
  }

  try {
    // 실제 환경에서는 API 호출
    // const response = await axios.post(`http://localhost:8080/api/boards/${postId}/comments`, {
    //   content: newComment.value,
    //   userId: currentUserId.value
    // });

    // 임시 처리 (백엔드 연동 전까지 사용)
    const now = new Date().toISOString();
    const newCommentObj = {
      id: comments.value.length + 1,
      content: newComment.value,
      author: '현재 사용자', // 실제로는 로그인한 사용자 이름
      createTime: now,
      authorId: currentUserId.value
    };

    comments.value.unshift(newCommentObj);
    newComment.value = '';

    alert('댓글이 등록되었습니다.');
  } catch (err) {
    console.error('댓글 등록 실패:', err);
    alert('댓글 등록에 실패했습니다.');
  }
};

// 댓글 삭제
const deleteComment = async (commentId) => {
  if (!confirm('정말 이 댓글을 삭제하시겠습니까?')) return;

  try {
    // 실제 환경에서는 API 호출
    // await axios.delete(`http://localhost:8080/api/boards/comments/${commentId}`);

    // 임시 처리 (백엔드 연동 전까지 사용)
    comments.value = comments.value.filter(comment => comment.id !== commentId);

    alert('댓글이 삭제되었습니다.');
  } catch (err) {
    console.error('댓글 삭제 실패:', err);
    alert('댓글 삭제에 실패했습니다.');
  }
};

// 게시글 수정 페이지로 이동
const editPost = () => {
  if (!isAuthor.value) {
    alert('게시글 작성자만 수정할 수 있습니다.');
    return;
  }
  router.push(`/board/edit/${postId}`);
};

// 게시글 삭제 확인
const confirmDelete = () => {
  if (confirm('정말 이 게시글을 삭제하시겠습니까?')) {
    deletePost();
  }
};

// 게시글 삭제
const deletePost = async () => {
  try {
     //실제 환경에서는 API 호출
     await api.delete(`/boards/${postId}`);

    alert('게시글이 삭제되었습니다.');
    router.push('/board');
  } catch (err) {
    console.error('게시글 삭제 실패:', err);
    alert('게시글 삭제에 실패했습니다.');
  }
};

// 목록으로 돌아가기
const goBackToList = () => {
  router.push('/board');
};

// 날짜 포맷 함수
const formatDate = (dateString) => {
  const date = new Date(dateString);
  const today = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);

  // 오늘 날짜인 경우 시간만 표시
  if (date.toDateString() === today.toDateString()) {
    return `오늘 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }

  // 어제 날짜인 경우 '어제'로 표시
  if (date.toDateString() === yesterday.toDateString()) {
    return `어제 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }

  // 그 외 날짜는 YYYY-MM-DD HH:MM 형식으로 표시
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

// 이미지 로드 실패 처리 수정
const handleImageError = (event) => {
  console.error('이미지 로드 실패:', event.target.src);
  // 이미지 로드 실패 시 해당 이미지 컨테이너를 숨김
  event.target.parentElement.style.display = 'none';
};

// 이미지 모달 관련 상태
const selectedImage = ref(null);

// 이미지 모달 열기
const openImageModal = (image) => {
  selectedImage.value = image;
  document.body.style.overflow = 'hidden'; // 모달 열릴 때 스크롤 방지
};

// 이미지 모달 닫기
const closeImageModal = () => {
  selectedImage.value = null;
  document.body.style.overflow = 'auto'; // 모달 닫힐 때 스크롤 복구
};

// 컴포넌트 마운트 시 실행
onMounted(() => {
  // 게시글 로드
  loadPost();
});
</script>

<style scoped>
.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px 20px;
}

.loading,
.error {
  text-align: center;
  padding: 50px 0;
}

.back-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.post-detail {
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.post-title {
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
  word-break: break-word;
}

.post-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 30px;
  font-size: 14px;
  color: #666;
}

.post-author-info {
  display: flex;
  align-items: center;
}

.author {
  font-weight: 500;
  margin-right: 10px;
}

.post-content {
  margin-bottom: 40px;
  line-height: 1.8;
  word-break: break-word;
}

.post-content p {
  margin-bottom: 20px;
}

.post-actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 40px;
}

.right-actions {
  display: flex;
  gap: 10px;
}

.list-button,
.edit-button,
.delete-button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.2s;
}

.list-button:hover {
  background-color: #f5f5f5;
}

.edit-button {
  background-color: #3498db;
  color: white;
  border-color: #3498db;
}

.edit-button:hover {
  background-color: #2980b9;
}

.delete-button {
  background-color: #e74c3c;
  color: white;
  border-color: #e74c3c;
}

.delete-button:hover {
  background-color: #c0392b;
}

/* 댓글 섹션 스타일 */
.comments-section {
  margin-top: 50px;
}

.comments-title {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.comment-form {
  margin-bottom: 40px;  /* 30px에서 40px로 증가 */
  position: relative;   /* 추가 */
  padding-bottom: 10px; /* 추가 */
}

.comment-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 10px;
  font-family: inherit;
}

.comment-input:focus {
  outline: none;
  border-color: #3498db;
}

.comment-submit {
  padding: 8px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  position: absolute;  /* 변경 */
  right: 0;           /* 추가 */
  bottom: -10px;      /* 추가 */
}

.comment-submit:hover {
  background-color: #2980b9;
}

.comment-submit:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.no-comments {
  text-align: center;
  padding: 30px;
  color: #888;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin-top: 20px;  /* 추가 */
}

.comment {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.comment:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.comment-author {
  font-weight: 500;
  color: #333;
}

.comment-date {
  color: #888;
}

.comment-body {
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-actions {
  text-align: right;
}

.comment-delete {
  background: none;
  border: none;
  color: #e74c3c;
  cursor: pointer;
  font-size: 14px;
}

.comment-delete:hover {
  text-decoration: underline;
}

.image-gallery {
  margin: 20px 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 10px;
}

.image-item {
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-item img:hover {
  transform: scale(1.05);
}

/* 이미지 모달 스타일 */
.gallery-image {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.gallery-image:hover {
  transform: scale(1.05);
}

.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  position: relative;
  max-width: 90%;
  max-height: 90vh;
}

.modal-content img {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
  border-radius: 4px;
}

.modal-close {
  position: absolute;
  top: -40px;
  right: -40px;
  width: 40px;
  height: 40px;
  background: none;
  border: none;
  color: white;
  font-size: 30px;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  color: #ddd;
}

@media (max-width: 768px) {
  .post-detail {
    padding: 20px;
  }

  .post-info {
    flex-direction: column;
    align-items: flex-start;
  }

  .post-stats {
    margin-top: 10px;
  }

  .post-actions {
    flex-direction: column;
    gap: 10px;
  }

  .right-actions {
    width: 100%;
  }

  .list-button,
  .edit-button,
  .delete-button {
    flex: 1;
  }

  .image-gallery {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
  }

  .modal-close {
    top: -30px;
    right: 0;
  }
}
</style>

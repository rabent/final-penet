<template>
  <div class="write-container">
    <h1 class="page-title">게시글 작성</h1>

    <form @submit.prevent="submitPost" class="write-form">
      <div class="form-group">
        <label for="title">제목</label>
        <input
          type="text"
          id="title"
          v-model="postForm.title"
          required
          placeholder="제목을 입력하세요"
          maxlength="100"
        />
      </div>

      <div class="form-group">
        <label for="content">내용</label>
        <textarea
          id="content"
          v-model="postForm.content"
          required
          placeholder="내용을 입력하세요"
          rows="15"
        ></textarea>
      </div>

      <div class="form-actions">
        <button type="button" class="cancel-button" @click="goBack">취소</button>
        <button type="submit" class="submit-button" :disabled="!isFormValid">등록</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

// 게시글 폼 데이터
const postForm = reactive({
  title: '',
  content: '',
  userId: null,
});

// 폼 유효성 검사
const isFormValid = computed(() => {
  return postForm.title.trim() && postForm.content.trim();
});

// 사용자 정보 로드
onMounted(() => {
  const userId = sessionStorage.getItem('userId');
  if (!userId) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/login');
    return;
  }

  postForm.userId = userId;
});

// 게시글 등록
const submitPost = async () => {
  try {
    if (!isFormValid.value) {
      return;
    }

    // 실제 환경에서는 API 호출
    // const response = await axios.post('http://localhost:8080/api/boards', postForm);

    // TODO: 백엔드 API 완성 후 주석 해제
    // if (response.data) {
    //   alert('게시글이 등록되었습니다.');
    //   router.push('/board');
    // }

    // 백엔드 연동 전까지는 성공했다고 가정
    alert('게시글이 등록되었습니다.');
    router.push('/board');
  } catch (error) {
    console.error('게시글 등록 실패:', error);
    alert('게시글 등록에 실패했습니다.');
  }
};

// 뒤로 가기
const goBack = () => {
  router.go(-1);
};
</script>

<style scoped>
.write-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px 20px;
}

.page-title {
  font-size: 28px;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
}

.write-form {
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #3498db;
  outline: none;
  box-shadow: 0 0 3px rgba(52, 152, 219, 0.3);
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 30px;
}

.cancel-button,
.submit-button {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.cancel-button {
  background-color: #95a5a6;
  color: white;
}

.cancel-button:hover {
  background-color: #7f8c8d;
}

.submit-button {
  background-color: #3498db;
  color: white;
}

.submit-button:hover {
  background-color: #2980b9;
}

.submit-button:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .write-form {
    padding: 20px;
  }

  .form-actions {
    flex-direction: column;
  }

  .cancel-button,
  .submit-button {
    width: 100%;
  }
}
</style>

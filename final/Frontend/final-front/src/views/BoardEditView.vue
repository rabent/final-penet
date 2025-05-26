<template>
  <div class="edit-container">
    <h1 class="page-title">게시글 수정</h1>

    <div v-if="isLoading" class="loading">
      <p>게시글을 불러오는 중입니다...</p>
    </div>

    <form v-else @submit.prevent="submitPost" class="edit-form">
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

      <!-- 이미지 섹션 추가 -->
      <div class="form-group">
        <label>과거 이미지</label>
        <div v-if="existingImages.length > 0" class="image-gallery">
          <div v-for="(image, index) in existingImages" :key="index" class="image-item">
            <img
              :src="`http://localhost:8080/api/images/view/${image.fileName}`"
              :alt="image.originalFileName"
            />
            <button type="button" @click="removeExistingImage(index)" class="remove-image">×</button>
          </div>
        </div>
        <p v-else class="no-images">등록된 이미지가 없습니다.</p>
      </div>

      <div class="form-group">
        <label for="newImages">변경할 이미지 추가</label>
        <input
          type="file"
          id="newImages"
          @change="handleImageSelect"
          accept="image/*"
          multiple
          class="file-input"
        />
        <div v-if="newImages.length > 0" class="image-gallery">
          <div v-for="(image, index) in newImages" :key="index" class="image-item">
            <img :src="image.previewUrl" :alt="image.file.name" />
            <button type="button" @click="removeNewImage(index)" class="remove-image">×</button>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="cancel-button" @click="goBack">취소</button>
        <button type="submit" class="submit-button" :disabled="!isFormValid || isSubmitting">
          {{ isSubmitting ? '수정 중...' : '수정' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, computed, ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import api from '@/utils/axios';

const router = useRouter();
const route = useRoute();
const postId = route.params.id;
const isLoading = ref(true);
const isSubmitting = ref(false);
const error = ref(null);

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

// 게시글 데이터 불러오기
const loadPost = async () => {
  try {
    isLoading.value = true;
    error.value = null;

    // API 호출로 게시글 데이터 가져오기
    const response = await api.get(`boards/${postId}`);

    // BoardDetailView에서 처럼 response.data에서 board 객체 가져오기
    const postData = response.data.board;

    // 폼에 기존 게시글 데이터 설정
    postForm.title = postData.title;
    postForm.content = postData.content;
    existingImages.value = postData.images || [];

    // 현재 사용자 ID 확인
    const userId = response.data.authorId;

    // 현재 사용자가 작성자인지 확인
    if (postData.userId !== userId) {
      alert('게시글 수정 권한이 없습니다.');
      router.push(`/board/post/${postId}`);
      return;
    }

    isLoading.value = false;
  } catch (err) {
    console.error('게시글 로드 실패:', err);
    error.value = '게시글을 불러오는 중 오류가 발생했습니다.';
    isLoading.value = false;

    if (err.response) {
      if (err.response.status === 404) {
        alert('존재하지 않는 게시글입니다.');
        router.push('/board');
      } else if (err.response.status === 401) {
        alert('로그인이 필요한 서비스입니다.');
        router.push('/login');
      }
    } else {
      alert('서버에 연결할 수 없습니다.');
      router.push('/board');
    }
  }
};

// 이미지 관련 상태 추가
const existingImages = ref([]);
const newImages = ref([]);
const removedImageFileNames = ref([]);

// 이미지 선택 핸들러
const handleImageSelect = (event) => {
  const files = Array.from(event.target.files);
  files.forEach(file => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = (e) => {
        newImages.value.push({
          file,
          previewUrl: e.target.result
        });
      };
      reader.readAsDataURL(file);
    }
  });
};

// 기존 이미지 제거
const removeExistingImage = (index) => {
  const removedImage = existingImages.value[index];
  removedImageFileNames.value.push(removedImage.fileName);
  existingImages.value.splice(index, 1);
};

// 새 이미지 제거
const removeNewImage = (index) => {
  newImages.value.splice(index, 1);
};

// 게시글 수정 제출
const submitPost = async () => {
  if (!isFormValid.value) return;

  try {
    isSubmitting.value = true;

    // 1. 새 이미지 업로드
    const uploadedFileNames = [];
    for (const imageData of newImages.value) {
      const formData = new FormData();
      formData.append('file', imageData.file);

      const imageResponse = await api.post('/images/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });

      if (imageResponse.data && imageResponse.data.fileName) {
        uploadedFileNames.push(imageResponse.data.fileName);
      }
    }

    // 2. 게시글 수정 요청 데이터 준비
    const updateData = {
      title: postForm.title,
      content: postForm.content,
      imageFileNames: [
        // ...existingImages.value.map(img => img.fileName),
        ...uploadedFileNames
      ]
    };

    // 3. 게시글 수정 API 호출
    await api.put(`boards/edit/${postId}`, updateData);

    alert('게시글이 수정되었습니다.');
    router.push(`/board/post/${postId}`);
  } catch (err) {
    console.error('게시글 수정 실패:', err);

    if (err.response) {
      if (err.response.status === 401) {
        alert('로그인이 필요한 서비스입니다.');
        router.push('/login');
      } else if (err.response.status === 403) {
        alert('게시글 수정 권한이 없습니다.');
      } else {
        alert('게시글 수정에 실패했습니다.');
      }
    } else {
      alert('서버에 연결할 수 없습니다.');
    }
  } finally {
    isSubmitting.value = false;
  }
};

// 뒤로 가기
const goBack = () => {
  router.push(`/board/post/${postId}`);
};

// 사용자 인증 확인
onMounted(() => {
  const userId = sessionStorage.getItem('userId');
  if (!userId) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/login');
    return;
  }

  postForm.userId = userId;

  // 게시글 데이터 로드
  loadPost();
});
</script>

<style scoped>
.edit-container {
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

.edit-form {
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.loading {
  text-align: center;
  padding: 50px 0;
  color: #666;
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

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  margin-top: 10px;
}

.image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 4px;
  overflow: hidden;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.remove-image:hover {
  background: rgba(0, 0, 0, 0.7);
}

.file-input {
  width: 100%;
  padding: 10px;
  border: 1px dashed #ddd;
  border-radius: 4px;
  margin-top: 5px;
}

.no-images {
  color: #666;
  text-align: center;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .edit-form {
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

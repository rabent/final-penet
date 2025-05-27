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

      <div class="form-group">
        <label for="images">이미지 첨부</label>
        <input
          type="file"
          id="images"
          @change="handleImageSelect"
          accept="image/*"
          multiple
          class="file-input"
        />
        <div class="preview-container" v-if="imagePreviewUrls.length > 0">
          <div v-for="(item, index) in imageItems" :key="index" class="preview-item">
            <img :src="item.previewUrl" alt="Preview" class="preview-image" />
            <span class="file-name">{{ item.fileName }}</span>
            <button type="button" @click="removeImage(index)" class="remove-button">✕</button>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="cancel-button" @click="goBack">취소</button>
        <button type="submit" class="submit-button" :disabled="!isFormValid">등록</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/utils/axios';

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

// 이미지 관련 상태 수정
const selectedFiles = ref([]);
const imagePreviewUrls = ref([]);
const imageItems = ref([]); // 이미지 정보를 저장할 배열

// 이미지 선택 핸들러 수정
const handleImageSelect = (event) => {
  const files = Array.from(event.target.files);
  files.forEach(file => {
    if (file.type.startsWith('image/')) {
      selectedFiles.value.push(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        imagePreviewUrls.value.push(e.target.result);
        imageItems.value.push({
          fileName: file.name,
          previewUrl: e.target.result
        });
      };
      reader.readAsDataURL(file);
    }
  });
};

// 이미지 제거 함수 수정
const removeImage = (index) => {
  selectedFiles.value.splice(index, 1);
  imagePreviewUrls.value.splice(index, 1);
  imageItems.value.splice(index, 1);
};

// 게시글 등록
const submitPost = async () => {
  try {
    if (!isFormValid.value) {
      return;
    }

    // 1. 이미지 업로드
    const imageFileNames = [];
    for (const file of selectedFiles.value) {
      const formData = new FormData();
      formData.append('file', file);

      const imageResponse = await api.post('/images/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      if (imageResponse.data && imageResponse.data.fileName) {
        imageFileNames.push(imageResponse.data.fileName);
      }
    }

    // 2. 게시글 데이터 준비
    const boardData = {
      ...postForm,
      imageFileNames: imageFileNames
    };

    // 3. 게시글 등록
    const response = await api.post('/boards', boardData);

    if (response.data) {
      alert('게시글이 등록되었습니다.');
      router.push('/board');
    }
  } catch (error) {
    console.error('게시글 등록 실패:', error);
    if (error.response) {
      console.error('에러 상세:', error.response.data);
    }
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

.file-input {
  display: block;
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
}

.preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.preview-item {
  position: relative;
  width: 150px; /* 너비 증가 */
  margin-bottom: 20px; /* 아래 여백 추가 */
}

.file-name {
  display: block;
  font-size: 12px;
  color: #666;
  margin-top: 5px;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.preview-image {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.remove-button {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #e74c3c;
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.remove-button:hover {
  background-color: #c0392b;
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

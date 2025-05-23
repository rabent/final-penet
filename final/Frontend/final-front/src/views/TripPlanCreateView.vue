<!-- views/TripPlanCreateView.vue -->
<template>
  <div class="create-trip-container">
    <div class="create-trip-header">
      <button @click="goBack" class="back-btn">
        ← 취소
      </button>
      <h1>새로운 여행 계획 만들기</h1>
      <p>특별한 여행을 계획해보세요</p>
    </div>

    <form @submit.prevent="handleSubmit" class="create-trip-form">
      <!-- 기본 정보 섹션 -->
      <div class="form-section">
        <h2>기본 정보</h2>

        <div class="form-group">
          <label for="title">여행 제목 *</label>
          <input
            type="text"
            id="title"
            v-model="tripForm.title"
            required
            maxlength="100"
            placeholder="예: 제주도 힐링 여행"
          />
        </div>

        <div class="form-group">
          <label for="description">여행 설명</label>
          <textarea
            id="description"
            v-model="tripForm.description"
            maxlength="500"
            rows="4"
            placeholder="이번 여행에 대한 간단한 설명을 적어주세요"
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="startDate">시작일 *</label>
            <input
              type="date"
              id="startDate"
              v-model="tripForm.startDate"
              required
              :min="today"
            />
          </div>

          <div class="form-group">
            <label for="endDate">종료일 *</label>
            <input
              type="date"
              id="endDate"
              v-model="tripForm.endDate"
              required
              :min="tripForm.startDate || today"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="mainLocation">주요 위치 *</label>
            <input
              type="text"
              id="mainLocation"
              v-model="tripForm.mainLocation"
              required
              maxlength="50"
              placeholder="예: 제주도, 부산, 서울"
            />
          </div>

        </div>
      </div>

      <!-- 안내 메시지 -->
      <div class="info-section">
        <div class="info-content">
          <h3>📝 여행 계획 생성 안내</h3>
          <p>기본 정보를 입력하여 여행 계획을 먼저 생성하세요.</p>
          <p>생성 후 상세 페이지에서 구체적인 여행 일정을 추가할 수 있습니다.</p>
        </div>
      </div>

      <!-- 제출 버튼 -->
      <div class="form-actions">
        <button type="button" @click="goBack" class="cancel-btn">
          취소
        </button>
        <button type="submit" class="submit-btn" :disabled="!isFormValid || isSubmitting">
          {{ isSubmitting ? '생성 중...' : '여행 계획 생성하기' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/axios'

const router = useRouter()
const isSubmitting = ref(false)

const today = new Date().toISOString().split('T')[0]

const tripForm = reactive({
  title: '',
  description: '',
  startDate: '',
  endDate: '',
  mainLocation: ''
})

const isFormValid = computed(() => {
  return tripForm.title &&
         tripForm.startDate &&
         tripForm.endDate &&
         tripForm.mainLocation &&
         tripForm.startDate <= tripForm.endDate
})

let itemIdCounter = 1

const handleSubmit = async () => {
  try {
    isSubmitting.value = true

    // 폼 데이터 정리
    const formData = {
      planName: tripForm.title,
      plan: tripForm.description,
      startDate: tripForm.startDate,
      endDate: tripForm.endDate,
      location: tripForm.mainLocation,
      items: [] // 빈 배열로 시작
    }

    // API 호출
    const response = await api.post('/trips', formData)

    setTimeout(() => {
      alert('여행 계획이 성공적으로 생성되었습니다!')
      router.push(`/trip-plan`)
    }, 1500)

  } catch (error) {
    console.error('여행 계획 생성 실패:', error)
    isSubmitting.value = false

    if (error.response?.status === 401) {
      alert('로그인이 필요합니다.')
      router.push('/login')
    } else {
      alert('여행 계획 생성 중 오류가 발생했습니다.')
    }
  }
}

const goBack = () => {
  if (tripForm.title || tripForm.description) {
    if (confirm('작성 중인 내용이 있습니다. 정말로 나가시겠습니까?')) {
      router.push('/trip-plan')
    }
  } else {
    router.push('/trip-plan')
  }
}
</script>

<style scoped>
.create-trip-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px 60px 20px;
}

.create-trip-header {
  text-align: center;
  margin: 30px 0 50px 0;
}

.back-btn {
  background: none;
  border: 1px solid #ddd;
  color: #666;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  margin-bottom: 30px;
}

.back-btn:hover {
  background-color: #f8f9fa;
  border-color: #3498db;
  color: #3498db;
}

.create-trip-header h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 600;
}

.create-trip-header p {
  font-size: 16px;
  color: #666;
}

.create-trip-form {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.form-section {
  margin-bottom: 50px;
}

.form-section:last-of-type {
  margin-bottom: 30px;
}

.form-section h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 25px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-header h2 {
  margin-bottom: 0;
  border-bottom: none;
  padding-bottom: 0;
}

.info-section {
  margin-bottom: 40px;
}

.info-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
}

.info-content h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
}

.info-content p {
  margin: 8px 0;
  opacity: 0.9;
  font-size: 14px;
  line-height: 1.5;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  border-color: #3498db;
  outline: none;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .form-group {
  flex: 1;
}

.form-row .form-group.flex-2 {
  flex: 2;
}

.empty-items {
  text-align: center;
  padding: 40px 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  color: #666;
}

.trip-items {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.trip-item-form {
  background-color: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
  border: 2px solid #e9ecef;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.item-number {
  background-color: #3498db;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.remove-item-btn {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.2s;
}

.remove-item-btn:hover:not(:disabled) {
  background-color: #c0392b;
}

.remove-item-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.item-form-content .form-group {
  margin-bottom: 16px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.cancel-btn {
  background: none;
  border: 1px solid #ddd;
  color: #666;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background-color: #f8f9fa;
  border-color: #ccc;
}

.submit-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #2980b9;
}

.submit-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .create-trip-container {
    padding: 0 15px 60px 15px;
  }

  .create-trip-form {
    padding: 20px;
  }

  .create-trip-header h1 {
    font-size: 24px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-actions {
    flex-direction: column-reverse;
    gap: 12px;
  }

  .form-actions button {
    width: 100%;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .add-item-btn {
    align-self: flex-end;
  }
}
</style>

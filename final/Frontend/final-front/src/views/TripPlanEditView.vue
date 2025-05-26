<!-- views/TripPlanEditView.vue -->
<template>
  <div class="trip-plan-edit-container">
    <div class="edit-header">
      <button @click="goBack" class="back-btn">
        ← 돌아가기
      </button>
      <h1>여행 계획 수정</h1>
      <p>여행 계획의 세부 정보를 수정하세요</p>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <p>여행 계획을 불러오는 중...</p>
    </div>

    <!-- 여행 계획 편집 폼 -->
    <div v-else-if="tripPlan" class="edit-content">
      <form @submit.prevent="handleSubmit" class="edit-form">
        <div class="form-group">
          <label for="planName">여행 이름 *</label>
          <input
            type="text"
            id="planName"
            v-model="tripPlanForm.planName"
            required
            maxlength="50"
            placeholder="여행 계획의 이름을 입력하세요"
          />
        </div>

        <div class="form-group">
          <label for="plan">여행 설명</label>
          <textarea
            id="plan"
            v-model="tripPlanForm.plan"
            rows="4"
            maxlength="500"
            placeholder="여행에 대한 간단한 설명을 입력하세요"
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="startDate">시작일 *</label>
            <input
              type="date"
              id="startDate"
              v-model="tripPlanForm.startDate"
              required
              @change="validateDates"
            />
          </div>

          <div class="form-group">
            <label for="endDate">종료일 *</label>
            <input
              type="date"
              id="endDate"
              v-model="tripPlanForm.endDate"
              required
              @change="validateDates"
              :min="tripPlanForm.startDate"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="location">주요 위치</label>
            <input
              type="text"
              id="location"
              v-model="tripPlanForm.location"
              maxlength="100"
              placeholder="여행의 주요 위치를 입력하세요"
            />
          </div>

          <div class="form-group">
            <label for="budget">예산 (원)</label>
            <input
              type="number"
              id="budget"
              v-model="tripPlanForm.budget"
              min="0"
              step="1000"
              placeholder="여행 예산을 입력하세요"
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="button" @click="goBack" class="cancel-btn">
            취소
          </button>
          <button type="submit" class="submit-btn" :disabled="!isFormValid || isSubmitting">
            {{ isSubmitting ? '저장 중...' : '여행 계획 저장' }}
          </button>
        </div>
      </form>
    </div>

    <!-- 오류 상태 -->
    <div v-else class="error-state">
      <h3>여행 계획을 찾을 수 없습니다</h3>
      <p>요청하신 여행 계획이 존재하지 않거나 접근 권한이 없습니다.</p>
      <button @click="goBack" class="back-btn">목록으로 돌아가기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/utils/axios'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const tripPlan = ref(null)
const isSubmitting = ref(false)
const dateError = ref('')

// 폼 데이터
const tripPlanForm = reactive({
  planName: '',
  plan: '',
  startDate: '',
  endDate: '',
  location: '',
  budget: 0
})

// 폼 유효성 검사
const isFormValid = computed(() => {
  return (
    tripPlanForm.planName &&
    tripPlanForm.startDate &&
    tripPlanForm.endDate &&
    !dateError.value
  )
})

// 여행 계획 불러오기
const fetchTripPlan = async () => {
  try {
    loading.value = true
    const planId = route.params.id
    const response = await api.get(`/trips/${planId}`)
    tripPlan.value = response.data.plan

    // 폼 데이터 설정
    tripPlanForm.planName = tripPlan.value.planName || ''
    tripPlanForm.plan = tripPlan.value.plan || ''

    // 날짜 포맷 변환 (ISO 날짜를 YYYY-MM-DD 형식으로)
    if (tripPlan.value.startDate) {
      tripPlanForm.startDate = new Date(tripPlan.value.startDate).toISOString().split('T')[0]
    }
    if (tripPlan.value.endDate) {
      tripPlanForm.endDate = new Date(tripPlan.value.endDate).toISOString().split('T')[0]
    }

    tripPlanForm.location = tripPlan.value.location || ''
    tripPlanForm.budget = tripPlan.value.budget || 0

    loading.value = false
  } catch (error) {
    console.error('여행 계획 조회 실패:', error)
    loading.value = false
    tripPlan.value = null
    if (error.response?.status === 401) {
      alert('로그인이 필요합니다.')
      router.push('/login')
    }
  }
}

// 날짜 유효성 검사
const validateDates = () => {
  if (tripPlanForm.startDate && tripPlanForm.endDate) {
    const start = new Date(tripPlanForm.startDate)
    const end = new Date(tripPlanForm.endDate)

    if (end < start) {
      dateError.value = '종료일은 시작일 이후여야 합니다.'
      return false
    }
  }
  dateError.value = ''
  return true
}

// 폼 제출 처리
const handleSubmit = async () => {
  if (!validateDates()) {
    alert(dateError.value)
    return
  }

  try {
    isSubmitting.value = true
    const planId = route.params.id

    // API에 전송할 데이터 구성
    const formData = {
      planName: tripPlanForm.planName,
      plan: tripPlanForm.plan,
      startDate: tripPlanForm.startDate,
      endDate: tripPlanForm.endDate,
      location: tripPlanForm.location,
      budget: tripPlanForm.budget || 0
    }

    const response = await api.put(`/trips/${planId}`, formData)
    console.log('여행 계획 수정 성공:', response.data)

    alert('여행 계획이 성공적으로 수정되었습니다!')
    router.push(`/trip-plan/${planId}`)
  } catch (error) {
    console.error('여행 계획 수정 실패:', error)
    alert('여행 계획 수정 중 오류가 발생했습니다.')
    isSubmitting.value = false
  }
}

// 취소 및 뒤로가기
const goBack = () => {
  const hasChanges =
    tripPlan.value && (
      tripPlanForm.planName !== tripPlan.value.planName ||
      tripPlanForm.plan !== tripPlan.value.plan ||
      tripPlanForm.startDate !== new Date(tripPlan.value.startDate).toISOString().split('T')[0] ||
      tripPlanForm.endDate !== new Date(tripPlan.value.endDate).toISOString().split('T')[0] ||
      tripPlanForm.location !== tripPlan.value.location ||
      tripPlanForm.budget !== tripPlan.value.budget
    )

  if (hasChanges) {
    if (confirm('변경사항이 있습니다. 정말로 나가시겠습니까?')) {
      router.push(`/trip-plan/${route.params.id}`)
    }
  } else {
    router.push(`/trip-plan/${route.params.id}`)
  }
}

onMounted(() => {
  fetchTripPlan()
})
</script>

<style scoped>
.trip-plan-edit-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px 60px 20px;
}

.edit-header {
  text-align: center;
  margin: 30px 0 40px 0;
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

.edit-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 600;
}

.edit-header p {
  font-size: 16px;
  color: #666;
}

.loading {
  text-align: center;
  padding: 40px 20px;
  color: #666;
  font-size: 18px;
}

.error-state {
  text-align: center;
  padding: 60px 20px;
}

.error-state h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 15px;
}

.error-state p {
  color: #666;
  margin-bottom: 30px;
}

.edit-content {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group textarea,
.form-group select {
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
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
  background-color: #27ae60;
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
  background-color: #219a52;
}

.submit-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .edit-content {
    padding: 30px 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>

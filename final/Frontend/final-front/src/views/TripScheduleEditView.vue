<!-- views/TripScheduleEditView.vue -->
<template>
  <div class="trip-item-edit-container">
    <div class="edit-header">
      <button @click="goBack" class="back-btn">
        ← 돌아가기
      </button>
      <h1>여행 일정 수정</h1>
      <p>{{ tripPlan?.planName || '여행 계획' }}의 일정을 수정하세요</p>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <p>여행 일정을 불러오는 중...</p>
    </div>

    <!-- 여행 일정 편집 폼 -->
    <div v-else-if="snippet" class="edit-content">
      <div class="content-grid">
        <!-- 관광지 정보 섹션 -->
        <div class="attraction-section">
          <h2>📍 관광지 정보</h2>

          <div v-if="snippet.attraction" class="selected-attraction">
            <div class="attraction-image">
              <img
                v-if="snippet.attraction.firstimage || snippet.attraction.firstImage1"
                :src="snippet.attraction.firstimage || snippet.attraction.firstImage1"
                :alt="snippet.attraction.name"
              />
              <div v-else class="no-image">
                이미지 없음
              </div>
            </div>

            <div class="attraction-details">
              <h3>{{ snippet.attraction.name }}</h3>
              <p class="attraction-address">{{ snippet.attraction.address }}</p>
              <div class="attraction-meta">
                <span class="category-tag" :class="getCategoryClass(snippet.attraction.category)">
                  {{ getCategoryText(snippet.attraction.category) }}
                </span>
              </div>
            </div>
          </div>

          <div v-else class="no-attraction">
            <p>관광지 정보가 없습니다. 여행 일정을 삭제하고 새로 추가해주세요.</p>
          </div>

          <div class="info-notice">
            <p>※ 관광지 변경은 불가능합니다. 변경이 필요한 경우 일정을 삭제 후 다시 추가해주세요.</p>
          </div>
        </div>

        <!-- 일정 정보 입력 섹션 -->
        <div class="item-form-section">
          <h2>📝 일정 정보 수정</h2>

          <form @submit.prevent="handleSubmit" class="item-form">
            <div class="form-group">
              <label for="scheduledAt">일시 *</label>
              <input
                type="datetime-local"
                id="scheduledAt"
                v-model="itemForm.scheduledAt"
                required
                :min="getMinDateTime()"
                :max="getMaxDateTime()"
              />
            </div>

            <div class="form-group">
              <label for="category">카테고리 *</label>
              <select id="category" v-model="itemForm.category" required>
                <option value="SIGHTSEEING">관광</option>
                <option value="FOOD">음식</option>
                <option value="ACTIVITY">액티비티</option>
                <option value="TRANSPORT">교통</option>
                <option value="SHOPPING">쇼핑</option>
                <option value="ACCOMMODATION">숙박</option>
              </select>
            </div>

            <div class="form-group">
              <label for="price">예산</label>
              <input
                type="number"
                id="price"
                v-model="itemForm.price"
                min="0"
                step="1000"
                placeholder="예상 비용을 입력하세요 (원)"
              />
            </div>

            <div class="form-group">
              <label for="description">설명</label>
              <textarea
                id="description"
                v-model="itemForm.description"
                maxlength="500"
                rows="4"
                placeholder="이 일정에 대한 간단한 설명을 작성해주세요"
              ></textarea>
            </div>

            <div class="form-actions">
              <button type="button" @click="goBack" class="cancel-btn">
                취소
              </button>
              <button type="submit" class="submit-btn" :disabled="!isFormValid || isSubmitting">
                {{ isSubmitting ? '저장 중...' : '일정 저장하기' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 오류 상태 -->
    <div v-else class="error-state">
      <h3>여행 일정을 찾을 수 없습니다</h3>
      <p>요청하신 여행 일정이 존재하지 않거나 접근 권한이 없습니다.</p>
      <button @click="goBackToDetail" class="back-btn">목록으로 돌아가기</button>
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
const snippet = ref(null)
const isSubmitting = ref(false)

// 폼 데이터
const itemForm = reactive({
  scheduledAt: '',
  category: '',
  price: 0,
  description: ''
})

// 폼 유효성 검사
const isFormValid = computed(() => {
  return itemForm.scheduledAt && itemForm.category
})

// 여행 계획 및 일정 불러오기
const fetchData = async () => {
  try {
    loading.value = true
    const planId = route.params.planId
    const snippetId = route.params.scheduleId

    // 여행 계획 조회
    const planResponse = await api.get(`/trips/${planId}`)
    tripPlan.value = planResponse.data.plan

    // 특정 일정 조회
    const snippetResponse = await api.get(`/trips/${planId}/snippets/${snippetId}`)
    snippet.value = snippetResponse.data

    // 폼 데이터 설정
    if (snippet.value) {
      // 날짜 및 시간 포맷 변환 (ISO 날짜를 YYYY-MM-DDTHH:MM 형식으로)
      if (snippet.value.date) {
        const date = new Date(snippet.value.date)
        itemForm.scheduledAt = date.toISOString().slice(0, 16) // YYYY-MM-DDTHH:MM 형식
      }

      itemForm.category = snippet.value.category || 'SIGHTSEEING'
      itemForm.price = snippet.value.price || 0
      itemForm.description = snippet.value.schedule || ''
    }

    loading.value = false
  } catch (error) {
    console.error('데이터 조회 실패:', error)
    loading.value = false
    if (error.response?.status === 401) {
      alert('로그인이 필요합니다.')
      router.push('/login')
    }
  }
}

// 폼 제출 처리
const handleSubmit = async () => {
  try {
    isSubmitting.value = true
    const planId = route.params.planId
    const snippetId = route.params.scheduleId

    // API에 전송할 데이터 구성
    const formData = {
      date: itemForm.scheduledAt,
      category: itemForm.category,
      price: itemForm.price || 0,
      schedule: itemForm.description
    }

    const response = await api.put(`/trips/${planId}/snippets/${snippetId}`, formData)
    console.log('일정 수정 성공:', response.data)

    alert('여행 일정이 성공적으로 수정되었습니다!')
    router.push(`/trip-plan/${planId}`)
  } catch (error) {
    console.error('일정 수정 실패:', error)
    alert('일정 수정 중 오류가 발생했습니다.')
    isSubmitting.value = false
  }
}

// 카테고리 관련 함수
const getCategoryClass = (category) => {
  // 카테고리가 없으면 기본값 반환
  if (!category) return 'sightseeing'

  const categoryMap = {
    'TRANSPORT': 'transport',
    'SIGHTSEEING': 'sightseeing',
    'FOOD': 'food',
    'ACTIVITY': 'activity',
    'SHOPPING': 'shopping',
    'ACCOMMODATION': 'accommodation'
  }

  return categoryMap[category.toUpperCase()] || 'sightseeing'
}

const getCategoryText = (category) => {
  if (!category) return '기타'

  const categoryMap = {
    'TRANSPORT': '교통',
    'SIGHTSEEING': '관광',
    'FOOD': '음식',
    'ACTIVITY': '액티비티',
    'SHOPPING': '쇼핑',
    'ACCOMMODATION': '숙박'
  }

  return categoryMap[category.toUpperCase()] || '기타'
}

// 날짜 관련 함수
const getMinDateTime = () => {
  if (!tripPlan.value || !tripPlan.value.startDate) return ''
  return `${tripPlan.value.startDate.split('T')[0]}T00:00`
}

const getMaxDateTime = () => {
  if (!tripPlan.value || !tripPlan.value.endDate) return ''
  return `${tripPlan.value.endDate.split('T')[0]}T23:59`
}

// 취소 및 뒤로가기
const goBack = () => {
  const hasChanges =
    snippet.value && (
      itemForm.scheduledAt !== new Date(snippet.value.date).toISOString().slice(0, 16) ||
      itemForm.category !== snippet.value.category ||
      itemForm.price !== snippet.value.price ||
      itemForm.description !== snippet.value.schedule
    )

  if (hasChanges) {
    if (confirm('변경사항이 있습니다. 정말로 나가시겠습니까?')) {
      goBackToDetail()
    }
  } else {
    goBackToDetail()
  }
}

const goBackToDetail = () => {
  router.push(`/trip-plan/${route.params.planId}`)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.trip-item-edit-container {
  max-width: 1200px;
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
  margin-bottom: 50px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.attraction-section,
.item-form-section {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.attraction-section h2,
.item-form-section h2 {
  font-size: 20px;
  color: #333;
  margin-bottom: 24px;
  font-weight: 600;
}

.selected-attraction {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  margin-bottom: 20px;
}

.attraction-image {
  width: 160px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.attraction-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  width: 100%;
  height: 100%;
  background-color: #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #666;
}

.attraction-details {
  flex: 1;
}

.attraction-details h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.attraction-address {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.attraction-meta {
  display: flex;
  gap: 8px;
}

.category-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 15px;
  font-weight: 500;
  color: white;
}

.category-tag.transport {
  background-color: #9b59b6;
}

.category-tag.sightseeing {
  background-color: #3498db;
}

.category-tag.food {
  background-color: #e67e22;
}

.category-tag.activity {
  background-color: #27ae60;
}

.category-tag.shopping {
  background-color: #f39c12;
}

.category-tag.accommodation {
  background-color: #34495e;
}

.no-attraction {
  padding: 30px;
  background-color: #f8f9fa;
  border-radius: 8px;
  text-align: center;
  color: #666;
  margin-bottom: 20px;
}

.info-notice {
  background-color: #fff8e1;
  border-left: 4px solid #ffc107;
  padding: 15px;
  margin-top: 20px;
  border-radius: 4px;
  font-size: 14px;
  color: #856404;
}

.info-notice p {
  margin: 0;
}

.item-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>

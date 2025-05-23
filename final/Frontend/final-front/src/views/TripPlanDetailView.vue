<!-- views/TripPlanDetailView.vue -->
<template>
  <div class="trip-detail-container">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <p>여행 계획을 불러오는 중...</p>
    </div>

    <!-- 여행 계획 세부 정보 -->
    <div v-else-if="tripPlan" class="trip-detail">
      <!-- 헤더 영역 -->
      <div class="trip-header">
        <button @click="goBack" class="back-btn">
          ← 목록으로 돌아가기
        </button>

        <div class="trip-header-content">
          <div class="trip-image">
            <img src="" :alt="tripPlan.planName" />
            <div class="trip-overlay">
            </div>
          </div>

          <div class="trip-info">
            <h1 class="trip-title">{{ tripPlan.planName }}</h1>
            <p class="trip-description">{{ tripPlan.plan }}</p>

            <div class="trip-meta">
              <div class="meta-item">
                <span class="icon">📅</span>
                <div>
                  <strong>여행 기간</strong>
                  <p>{{ formatDateRange(tripPlan.startDate, tripPlan.endDate) }}</p>
                </div>
              </div>

              <div class="meta-item">
                <span class="icon">📍</span>
                <div>
                  <strong>주요 위치</strong>
                  <p>{{ tripPlan.location }}</p>
                </div>
              </div>

              <div class="meta-item">
                <span class="icon">💰</span>
                <div>
                  <strong>예산</strong>
                  <p>{{ formatBudget(tripPlan.budget) }}</p>
                </div>
              </div>

              <div class="meta-item">
                <span class="icon">📋</span>
                <div>
                  <strong>총 일정</strong>
                  <p>{{ snippets.length }}개</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 여행 계획 목록 -->
      <div class="trip-items-section">
        <div class="section-header">
          <h2>여행 일정</h2>
          <button @click="addNewItem" class="add-item-btn">
            + 일정 추가
          </button>
        </div>

        <!-- 일정이 없을 때 -->
        <div v-if="snippets.length === 0" class="empty-items">
          <div class="empty-content">
            <div class="empty-icon">📝</div>
            <h3>아직 여행 일정이 없습니다</h3>
            <p>첫 번째 여행 일정을 추가하여 멋진 여행을 계획해보세요!</p>
            <button @click="addNewItem" class="create-first-item-btn">
              첫 번째 일정 추가하기
            </button>
          </div>
        </div>

        <!-- 일정 목록 -->
        <div v-else class="trip-items">
          <div
            v-for="(snippet, index) in snippets"
            :key="snippet.id"
            class="trip-item"
          >
            <div class="item-number">{{ index + 1 }}</div>

            <div class="item-content">
              <div class="item-header">
                <h3 class="item-title">{{ snippet.attraction?.name || '관광지 정보 없음' }}</h3>
                <div class="item-actions">
                  <button @click="editItem(snippet)" class="edit-item-btn">편집</button>
                  <button @click="deleteItem(snippet.id)" class="delete-item-btn">삭제</button>
                </div>
              </div>

              <p class="item-description" v-if="snippet.attraction?.description">
                {{ snippet.attraction.description }}
              </p>

              <div class="item-details">
                <div class="item-location" v-if="snippet.attraction?.address">
                  <span class="icon">📍</span>
                  {{ snippet.attraction.address }}
                </div>

                <div class="item-cost" v-if="snippet.price">
                  <span class="icon">💰</span>
                  {{ formatBudget(snippet.price) }}
                </div>

                <div class="item-category" v-if="snippet.attraction?.category">
                  <span class="category-tag" :class="getCategoryClass(snippet.attraction.category)">
                    {{ getCategoryText(snippet.attraction.category) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 여행 통계 -->
      <div class="trip-statistics">
        <h2>여행 통계</h2>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">{{ snippets.length }}</div>
            <div class="stat-label">총 일정 수</div>
          </div>

          <div class="stat-item">
            <div class="stat-number">{{ getTotalDays }}</div>
            <div class="stat-label">여행 일수</div>
          </div>

          <div class="stat-item">
            <div class="stat-number">{{ formatBudget(getTotalCost) }}</div>
            <div class="stat-label">총 예상 비용</div>
          </div>

          <div class="stat-item">
            <div class="stat-number">{{ getUniqueLocations }}</div>
            <div class="stat-label">방문 지역 수</div>
          </div>
        </div>
      </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/utils/axios'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const tripPlan = ref(null)
const snippets = ref([])
const planId = ref(null)

const getTotalDays = computed(() => {
  if (!tripPlan.value) return 0
  const start = new Date(tripPlan.value.startDate)
  const end = new Date(tripPlan.value.endDate)
  const diffTime = Math.abs(end - start)
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1
})

const getTotalCost = computed(() => {
  if (!snippets.value) return 0
  return snippets.value.reduce((total, snippet) => total + (snippet.price || 0), 0)
})

const getUniqueLocations = computed(() => {
  if (!snippets.value) return 0
  const locations = snippets.value
    .map(snippet => snippet.attraction?.address)
    .filter(location => location)
  return new Set(locations).size
})

const fetchTripPlan = async () => {
  try {
    loading.value = true
    planId.value = route.params.id

    // 실제 API 호출
    const response = await api.get(`/trips/${planId.value}`)
    tripPlan.value = response.data.plan
    snippets.value = response.data.snippets

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

const goBack = () => {
  router.push('/trip-plan')
}

const addNewItem = () => {
  router.push(`/trip-plan/${planId.value}/create`)
}

const editItem = (snippet) => {
  router.push(`/trip-plan/${planId.value}/${snippet.id}/edit`)
}

const deleteItem = async (snippetId) => {
  if (confirm('정말로 이 일정을 삭제하시겠습니까?')) {
    try {
      await api.delete(`/trips/${planId.value}/snippets/${snippetId}`)
      snippets.value = snippets.value.filter(snippet => snippet.id !== snippetId)
      alert('일정이 삭제되었습니다.')
    } catch (error) {
      console.error('일정 삭제 실패:', error)
      alert('일정 삭제에 실패했습니다.')
    }
  }
}

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

const formatDateRange = (startDate, endDate) => {
  if (!startDate || !endDate) return '날짜 정보 없음'

  const start = new Date(startDate).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
  const end = new Date(endDate).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
  return `${start} - ${end}`
}

const formatBudget = (budget) => {
  if (!budget) return '0원'
  return new Intl.NumberFormat('ko-KR').format(budget) + '원'
}

onMounted(() => {
  fetchTripPlan()
})
</script>

<style scoped>
.trip-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px 60px 20px;
}

.loading {
  text-align: center;
  padding: 80px 20px;
  color: #666;
  font-size: 18px;
}

.error-state {
  text-align: center;
  padding: 80px 20px;
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

.trip-header {
  margin-bottom: 50px;
}

.trip-header-content {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.trip-image {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.trip-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.trip-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.3));
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 20px;
}

.trip-info {
  padding: 40px;
}

.trip-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 15px;
}

.trip-description {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 30px;
}

.trip-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 25px;
}

.meta-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.meta-item .icon {
  font-size: 20px;
  margin-top: 2px;
}

.meta-item strong {
  display: block;
  color: #333;
  font-weight: 600;
  margin-bottom: 4px;
}

.meta-item p {
  color: #666;
  margin: 0;
}

.trip-items-section {
  margin-bottom: 50px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-header h2 {
  font-size: 28px;
  color: #333;
  font-weight: 600;
}

.add-item-btn {
  background-color: #27ae60;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.add-item-btn:hover {
  background-color: #219a52;
}

.empty-items {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 16px;
  margin-bottom: 30px;
}

.empty-content {
  max-width: 400px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-content h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 600;
}

.empty-content p {
  color: #666;
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 30px;
}

.create-first-item-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.create-first-item-btn:hover {
  background-color: #2980b9;
}

.trip-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.trip-item {
  display: flex;
  gap: 20px;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border-left: 4px solid #3498db;
}

.item-number {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  background-color: #3498db;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.item-content {
  flex: 1;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.item-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
  flex: 1;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.edit-item-btn,
.delete-item-btn {
  font-size: 12px;
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.edit-item-btn {
  background-color: #3498db;
  color: white;
}

.edit-item-btn:hover {
  background-color: #2980b9;
}

.delete-item-btn {
  background-color: #e74c3c;
  color: white;
}

.delete-item-btn:hover {
  background-color: #c0392b;
}

.item-description {
  color: #666;
  line-height: 1.5;
  margin-bottom: 16px;
}

.item-details {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
}

.item-location,
.item-cost {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #555;
}

.item-details .icon {
  font-size: 14px;
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

.trip-statistics {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.trip-statistics h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #3498db;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

@media (max-width: 768px) {
  .trip-title {
    font-size: 24px;
  }

  .trip-meta {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .trip-item {
    flex-direction: column;
    gap: 16px;
  }

  .item-number {
    align-self: flex-start;
  }

  .item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .item-actions {
    align-items: flex-start;
    flex-direction: row;
    gap: 8px;
  }

  .edit-item-btn,
  .delete-item-btn {
    font-size: 11px;
    padding: 3px 6px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}
</style>

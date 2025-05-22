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
            <img :src="tripPlan.imageUrl || '/api/placeholder/800/400'" :alt="tripPlan.title" />
            <div class="trip-overlay">
              <div class="trip-status" :class="tripPlan.status">
                {{ getStatusText(tripPlan.status) }}
              </div>
            </div>
          </div>

          <div class="trip-info">
            <h1 class="trip-title">{{ tripPlan.title }}</h1>
            <p class="trip-description">{{ tripPlan.description }}</p>

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
                  <p>{{ tripPlan.mainLocation }}</p>
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
                  <p>{{ tripPlan.items.length }}개</p>
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
          <button @click="editTrip" class="edit-btn">수정하기</button>
        </div>

        <div class="trip-items">
          <div
            v-for="(item, index) in tripPlan.items"
            :key="item.id"
            class="trip-item"
          >
            <div class="item-number">{{ index + 1 }}</div>

            <div class="item-content">
              <div class="item-header">
                <h3 class="item-title">{{ item.title }}</h3>
                <div class="item-meta">
                  <span class="item-date">{{ formatDateTime(item.scheduledAt) }}</span>
                  <span class="item-duration" v-if="item.duration">{{ item.duration }}분</span>
                </div>
              </div>

              <p class="item-description" v-if="item.description">
                {{ item.description }}
              </p>

              <div class="item-details">
                <div class="item-location" v-if="item.location">
                  <span class="icon">📍</span>
                  {{ item.location }}
                </div>

                <div class="item-cost" v-if="item.cost">
                  <span class="icon">💰</span>
                  {{ formatBudget(item.cost) }}
                </div>

                <div class="item-category">
                  <span class="category-tag" :class="item.category">
                    {{ getCategoryText(item.category) }}
                  </span>
                </div>
              </div>

              <div class="item-notes" v-if="item.notes">
                <div class="notes-header">
                  <span class="icon">📝</span>
                  <strong>메모</strong>
                </div>
                <p>{{ item.notes }}</p>
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
            <div class="stat-number">{{ tripPlan.items.length }}</div>
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

// 샘플 데이터
const sampleTripPlan = {
  id: 1,
  title: '제주도 힐링 여행',
  description: '아름다운 제주의 자연과 함께하는 3박 4일 힐링 여행. 스트레스를 해소하고 새로운 에너지를 얻는 시간을 가져보세요.',
  startDate: '2025-06-15',
  endDate: '2025-06-18',
  mainLocation: '제주도',
  budget: 500000,
  status: 'planned',
  imageUrl: 'https://via.placeholder.com/800x400?text=제주도+힐링+여행',
  createdAt: '2025-05-10',
  items: [
    {
      id: 1,
      title: '제주공항 도착 및 렌터카 픽업',
      description: '제주공항에 도착하여 렌터카를 픽업하고 여행 준비를 완료합니다.',
      scheduledAt: '2025-06-15T10:30:00',
      duration: 60,
      location: '제주국제공항',
      cost: 0,
      category: 'transport',
      notes: '렌터카 예약 확인서 지참 필요'
    },
    {
      id: 2,
      title: '성산일출봉 관람',
      description: '제주의 대표적인 관광지인 성산일출봉에서 아름다운 경치를 감상합니다.',
      scheduledAt: '2025-06-15T14:00:00',
      duration: 120,
      location: '성산일출봉',
      cost: 5000,
      category: 'sightseeing',
      notes: '편한 신발 착용 권장'
    },
    {
      id: 3,
      title: '섭지코지 산책',
      description: '아름다운 해안 절경을 감상하며 여유로운 산책을 즐깁니다.',
      scheduledAt: '2025-06-15T16:30:00',
      duration: 90,
      location: '섭지코지',
      cost: 0,
      category: 'sightseeing',
      notes: '일몰 시간에 맞춰 방문'
    },
    {
      id: 4,
      title: '흑돼지 구이 저녁식사',
      description: '제주의 대표 음식인 흑돼지 구이를 맛보며 하루를 마무리합니다.',
      scheduledAt: '2025-06-15T19:00:00',
      duration: 90,
      location: '제주시 구시가지',
      cost: 35000,
      category: 'food',
      notes: '미리 예약 필요'
    },
    {
      id: 5,
      title: '한라산 등반',
      description: '제주의 상징인 한라산을 등반하며 자연의 아름다움을 만끽합니다.',
      scheduledAt: '2025-06-16T07:00:00',
      duration: 480,
      location: '한라산 어리목 탐방로',
      cost: 0,
      category: 'activity',
      notes: '등산화, 간식, 충분한 물 준비'
    },
    {
      id: 6,
      title: '카페 거리 투어',
      description: '제주의 유명한 카페들을 돌아보며 특별한 커피를 즐깁니다.',
      scheduledAt: '2025-06-16T15:00:00',
      duration: 180,
      location: '애월 카페거리',
      cost: 15000,
      category: 'food',
      notes: '인스타그램 명소 카페 위주'
    },
    {
      id: 7,
      title: '중문 해수욕장',
      description: '아름다운 중문 해수욕장에서 수영과 해양 스포츠를 즐깁니다.',
      scheduledAt: '2025-06-17T10:00:00',
      duration: 240,
      location: '중문 해수욕장',
      cost: 20000,
      category: 'activity',
      notes: '수영복, 선크림 준비'
    },
    {
      id: 8,
      title: '올레시장 쇼핑',
      description: '제주의 전통 시장에서 기념품과 특산품을 구매합니다.',
      scheduledAt: '2025-06-17T16:00:00',
      duration: 120,
      location: '제주 동문시장',
      cost: 50000,
      category: 'shopping',
      notes: '현금 준비 권장'
    },
    {
      id: 9,
      title: '공항 이동 및 출발',
      description: '렌터카 반납 후 제주공항에서 출발합니다.',
      scheduledAt: '2025-06-18T14:00:00',
      duration: 120,
      location: '제주국제공항',
      cost: 0,
      category: 'transport',
      notes: '출발 2시간 전 공항 도착'
    }
  ]
}

const getTotalDays = computed(() => {
  if (!tripPlan.value) return 0
  const start = new Date(tripPlan.value.startDate)
  const end = new Date(tripPlan.value.endDate)
  const diffTime = Math.abs(end - start)
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1
})

const getTotalCost = computed(() => {
  if (!tripPlan.value) return 0
  return tripPlan.value.items.reduce((total, item) => total + (item.cost || 0), 0)
})

const getUniqueLocations = computed(() => {
  if (!tripPlan.value) return 0
  const locations = tripPlan.value.items
    .map(item => item.location)
    .filter(location => location)
  return new Set(locations).size
})

const fetchTripPlan = async () => {
  try {
    loading.value = true
    const planId = route.params.id

    // 실제 API 호출
    // const response = await api.get(`/trip-plans/${planId}`)
    // tripPlan.value = response.data

    // 임시로 샘플 데이터 사용
    setTimeout(() => {
      if (planId == 1) {
        tripPlan.value = sampleTripPlan
      } else {
        tripPlan.value = null
      }
      loading.value = false
    }, 1000)
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

const editTrip = () => {
  router.push(`/trip-plan/${route.params.id}/edit`)
}

const getStatusText = (status) => {
  const statusMap = {
    planned: '예정',
    ongoing: '진행중',
    completed: '완료'
  }
  return statusMap[status] || '예정'
}

const getCategoryText = (category) => {
  const categoryMap = {
    transport: '교통',
    sightseeing: '관광',
    food: '음식',
    activity: '액티비티',
    shopping: '쇼핑',
    accommodation: '숙박'
  }
  return categoryMap[category] || '기타'
}

const formatDateRange = (startDate, endDate) => {
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

const formatDateTime = (dateTime) => {
  return new Date(dateTime).toLocaleString('ko-KR', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatBudget = (budget) => {
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

.trip-status {
  padding: 8px 16px;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  color: white;
}

.trip-status.planned {
  background-color: #3498db;
}

.trip-status.ongoing {
  background-color: #f39c12;
}

.trip-status.completed {
  background-color: #27ae60;
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

.edit-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.edit-btn:hover {
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
}

.item-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.item-date {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.item-duration {
  font-size: 12px;
  color: #888;
  background-color: #f8f9fa;
  padding: 2px 8px;
  border-radius: 12px;
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

.item-notes {
  background-color: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid #3498db;
}

.notes-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.notes-header .icon {
  font-size: 14px;
}

.notes-header strong {
  color: #333;
  font-size: 14px;
}

.item-notes p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
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

  .item-meta {
    align-items: flex-start;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}
</style>

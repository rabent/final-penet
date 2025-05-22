<!-- views/TripPlanView.vue -->
<template>
  <div class="trip-plan-container">
    <div class="trip-plan-header">
      <h1>나의 여행 계획</h1>
      <p>특별한 여행을 계획하고 추억을 만들어보세요.</p>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <p>여행 계획을 불러오는 중...</p>
    </div>

    <!-- 여행 계획이 없는 경우 -->
    <div v-else-if="tripPlans.length === 0" class="empty-state">
      <div class="empty-content">
        <h3>아직 여행 계획이 없습니다</h3>
        <p>첫 번째 여행 계획을 만들어보세요!</p>
        <button @click="createNewPlan" class="create-first-plan-btn">
          여행 계획 만들기
        </button>
      </div>
    </div>

    <!-- 여행 계획 목록 -->
    <div v-else class="trip-plans-grid">
      <div
        v-for="plan in paginatedPlans"
        :key="plan.id"
        class="trip-plan-card"
        @click="goToDetail(plan.id)"
      >
        <div class="plan-image">
          <img :src="plan.imageUrl || '/api/placeholder/300/200'" :alt="plan.title" />
          <div class="plan-status" :class="plan.status">
            {{ getStatusText(plan.status) }}
          </div>
        </div>
        <div class="plan-content">
          <h3 class="plan-title">{{ plan.title }}</h3>
          <p class="plan-description">{{ plan.description }}</p>
          <div class="plan-info">
            <div class="plan-dates">
              <span class="icon">📅</span>
              {{ formatDateRange(plan.startDate, plan.endDate) }}
            </div>
            <div class="plan-location">
              <span class="icon">📍</span>
              {{ plan.mainLocation }}
            </div>
            <div class="plan-items">
              <span class="icon">📋</span>
              {{ plan.itemCount }}개 일정
            </div>
          </div>
          <div class="plan-meta">
            <span class="created-date">{{ formatDate(plan.createdAt) }} 생성</span>
            <span class="plan-budget" v-if="plan.budget">
              예산: {{ formatBudget(plan.budget) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div v-if="totalPages > 1" class="pagination">
      <button
        @click="changePage(currentPage - 1)"
        :disabled="currentPage === 1"
        class="page-btn"
      >
        이전
      </button>

      <button
        v-for="page in visiblePages"
        :key="page"
        @click="changePage(page)"
        :class="['page-btn', { active: page === currentPage }]"
      >
        {{ page }}
      </button>

      <button
        @click="changePage(currentPage + 1)"
        :disabled="currentPage === totalPages"
        class="page-btn"
      >
        다음
      </button>
    </div>

    <!-- 여행 계획 생성 버튼 -->
    <button @click="createNewPlan" class="floating-create-btn">
      <span class="plus-icon">+</span>
      여행 계획 생성
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/axios'

const router = useRouter()
const loading = ref(true)
const tripPlans = ref([])
const currentPage = ref(1)
const itemsPerPage = 6

// 샘플 데이터 (실제로는 API에서 가져옴)
const sampleTripPlans = [
  {
    id: 1,
    title: '제주도 힐링 여행',
    description: '아름다운 제주의 자연과 함께하는 3박 4일 힐링 여행',
    startDate: '2025-06-15',
    endDate: '2025-06-18',
    mainLocation: '제주도',
    itemCount: 12,
    budget: 500000,
    status: 'planned',
    imageUrl: 'https://via.placeholder.com/300x200?text=제주도',
    createdAt: '2025-05-10'
  },
  {
    id: 2,
    title: '부산 바다 여행',
    description: '시원한 바다와 맛있는 음식이 있는 부산 2박 3일 여행',
    startDate: '2025-07-20',
    endDate: '2025-07-22',
    mainLocation: '부산',
    itemCount: 8,
    budget: 300000,
    status: 'ongoing',
    imageUrl: 'https://via.placeholder.com/300x200?text=부산',
    createdAt: '2025-05-08'
  },
  {
    id: 3,
    title: '서울 문화탐방',
    description: '역사와 현대가 공존하는 서울의 매력을 느끼는 여행',
    startDate: '2025-08-10',
    endDate: '2025-08-12',
    mainLocation: '서울',
    itemCount: 15,
    budget: 400000,
    status: 'completed',
    imageUrl: 'https://via.placeholder.com/300x200?text=서울',
    createdAt: '2025-05-05'
  },
  {
    id: 4,
    title: '경주 역사여행',
    description: '천년 고도 경주의 역사를 만나는 특별한 여행',
    startDate: '2025-09-05',
    endDate: '2025-09-07',
    mainLocation: '경주',
    itemCount: 10,
    budget: 250000,
    status: 'planned',
    imageUrl: 'https://via.placeholder.com/300x200?text=경주',
    createdAt: '2025-05-01'
  }
]

const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return tripPlans.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(tripPlans.value.length / itemsPerPage)
})

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const fetchTripPlans = async () => {
  try {
    loading.value = true
    // 실제 API 호출
    // const response = await api.get('/trip-plans')
    // tripPlans.value = response.data

    // 임시로 샘플 데이터 사용
    setTimeout(() => {
      tripPlans.value = sampleTripPlans
      loading.value = false
    }, 1000)
  } catch (error) {
    console.error('여행 계획 조회 실패:', error)
    loading.value = false
    if (error.response?.status === 401) {
      alert('로그인이 필요합니다.')
      router.push('/login')
    }
  }
}

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const goToDetail = (planId) => {
  router.push(`/trip-plan/${planId}`)
}

const createNewPlan = () => {
  router.push('/trip-plan/create')
}

const getStatusText = (status) => {
  const statusMap = {
    planned: '예정',
    ongoing: '진행중',
    completed: '완료'
  }
  return statusMap[status] || '예정'
}

const formatDateRange = (startDate, endDate) => {
  const start = new Date(startDate).toLocaleDateString('ko-KR', {
    month: 'short',
    day: 'numeric'
  })
  const end = new Date(endDate).toLocaleDateString('ko-KR', {
    month: 'short',
    day: 'numeric'
  })
  return `${start} - ${end}`
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('ko-KR')
}

const formatBudget = (budget) => {
  return new Intl.NumberFormat('ko-KR').format(budget) + '원'
}

onMounted(() => {
  fetchTripPlans()
})
</script>

<style scoped>
.trip-plan-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 100px 20px;
}

.trip-plan-header {
  text-align: center;
  margin: 40px 0 60px 0;
}

.trip-plan-header h1 {
  font-size: 36px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 600;
}

.trip-plan-header p {
  font-size: 18px;
  color: #666;
}

.loading {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-content h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 15px;
}

.empty-content p {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

.create-first-plan-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 15px 30px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.create-first-plan-btn:hover {
  background-color: #2980b9;
}

.trip-plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
  margin-bottom: 60px;
}

.trip-plan-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.trip-plan-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.plan-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.plan-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.plan-status {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.plan-status.planned {
  background-color: #3498db;
}

.plan-status.ongoing {
  background-color: #f39c12;
}

.plan-status.completed {
  background-color: #27ae60;
}

.plan-content {
  padding: 24px;
}

.plan-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.plan-description {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.plan-info {
  margin-bottom: 20px;
}

.plan-info > div {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

.plan-info .icon {
  margin-right: 8px;
  font-size: 16px;
}

.plan-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #eee;
  font-size: 12px;
  color: #888;
}

.plan-budget {
  font-weight: 500;
  color: #27ae60;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 40px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  color: #666;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:not(:disabled):hover {
  background-color: #f8f9fa;
  border-color: #3498db;
  color: #3498db;
}

.page-btn.active {
  background-color: #3498db;
  border-color: #3498db;
  color: white;
}

.page-btn:disabled {
  background-color: #f8f9fa;
  color: #ccc;
  cursor: not-allowed;
}

.floating-create-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background-color: #27ae60;
  color: white;
  border: none;
  padding: 16px 24px;
  border-radius: 50px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(39, 174, 96, 0.3);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.floating-create-btn:hover {
  background-color: #219a52;
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(39, 174, 96, 0.4);
}

.plus-icon {
  font-size: 20px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .trip-plans-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .trip-plan-header h1 {
    font-size: 28px;
  }

  .floating-create-btn {
    bottom: 20px;
    right: 20px;
    padding: 14px 20px;
    font-size: 14px;
  }
}
</style>

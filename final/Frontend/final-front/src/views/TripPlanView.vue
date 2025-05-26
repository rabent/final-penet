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
      >
        <!-- 카드 액션 버튼들 -->
        <div class="card-actions">
          <button
            @click.stop="goToDetail(plan.id,imageUrls[plan.location])"
            class="action-btn view-btn"
            title="상세보기"
          >
            👁️
          </button>
          <button
            @click.stop="showDeleteConfirm(plan)"
            class="action-btn delete-btn"
            title="삭제"
          >
            🗑️
          </button>
        </div>

        <div class="plan-image" @click="goToDetail(plan.id,imageUrls[plan.location])">
          <img :src="imageUrls[plan.location]" loading="lazy" :alt="plan.planName" />
        </div>
        <div class="plan-content" @click="goToDetail(plan.id,imageUrls[plan.location])">
          <h3 class="plan-title">{{ plan.planName }}</h3>
          <p class="plan-description">{{ plan.description }}</p>
          <div class="plan-info">
            <div class="plan-dates">
              <span class="icon">📅</span>
              {{ formatDateRange(plan.startDate, plan.endDate) }}
            </div>
            <div class="plan-location">
              <span class="icon">📍</span>
              {{ plan.location }}
            </div>
          </div>
          <div class="plan-meta">
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

    <!-- 삭제 확인 모달 -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="hideDeleteConfirm">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>여행 계획 삭제</h3>
        </div>
        <div class="modal-body">
          <p><strong>{{ planToDelete?.title }}</strong> 여행 계획을 정말 삭제하시겠습니까?</p>
          <p class="warning-text">삭제된 여행 계획은 복구할 수 없습니다.</p>
        </div>
        <div class="modal-footer">
          <button @click="hideDeleteConfirm" class="cancel-btn">취소</button>
          <button
            @click="confirmDelete"
            class="confirm-delete-btn"
            :disabled="deleting"
          >
            {{ deleting ? '삭제 중...' : '삭제' }}
          </button>
        </div>
      </div>
    </div>
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
const errorMessage = ref('')
const imageUrls = ref({})
// 삭제 관련 상태
const showDeleteModal = ref(false)
const planToDelete = ref(null)
const deleting = ref(false)

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

const getUrl = (region) => {
  imageUrl=getLocationImage(region)
}

const fetchTripPlans = async () => {
  try {
    loading.value = true
    const response = await api.get('/trips', {
      params: {
        page: currentPage.value - 1,
        size: itemsPerPage
      }
    })
    tripPlans.value = response.data.content
    totalPages.value = response.data.totalPages
    loading.value = false
  } catch (error) {
    console.error('여행 계획 조회 실패:', error)
    loading.value = false
    if (error.response?.status === 401) {
      alert('로그인이 필요합니다.')
      router.push('/login')
    }
  }
}

// 삭제 관련 함수들
const showDeleteConfirm = (plan) => {
  planToDelete.value = plan
  showDeleteModal.value = true
}

const hideDeleteConfirm = () => {
  showDeleteModal.value = false
  planToDelete.value = null
  deleting.value = false
}

const confirmDelete = async () => {
  if (!planToDelete.value) return

  try {
    deleting.value = true
    await api.delete(`/trips/${planToDelete.value.id}`)

    tripPlans.value = tripPlans.value.filter(plan => plan.id !== planToDelete.value.id)

    if (paginatedPlans.value.length === 0 && currentPage.value > 1) {
      currentPage.value = currentPage.value - 1
    }

    hideDeleteConfirm()
    alert('여행 계획이 성공적으로 삭제되었습니다.')

  } catch (error) {
    console.error('여행 계획 삭제 실패:', error)
    deleting.value = false

    if (error.response?.status === 401) {
      alert('로그인이 필요합니다.')
      router.push('/login')
    } else if (error.response?.status === 403) {
      alert('해당 여행 계획을 삭제할 권한이 없습니다.')
    } else if (error.response?.status === 404) {
      alert('삭제하려는 여행 계획을 찾을 수 없습니다.')
    } else {
      alert('여행 계획 삭제에 실패했습니다. 다시 시도해주세요.')
    }

    hideDeleteConfirm()
  }
}

const regionImageCounts = {
  부산 : 3,
  충청북도 : 3,
  충청남도 : 3,
  대구 : 3,
  대전 : 3,
  강원특별자치도 : 3,
  광주 : 3,
  경기도 : 3,
  경상북도 : 3,
  경상남도 : 3,
  인천 : 3,
  제주도 : 3,
  전북특별자치도 : 3,
  전라남도 : 3,
  세종특별자치시 : 3,
  서울 : 3,
  울산 : 3
}

const getLocationImage = async (region) => {
  loading.value = true
  errorMessage.value = ''

  try {
    const imageCount = regionImageCounts[region] || 3
    const randomIndex = Math.floor(Math.random() * imageCount) + 1

    console.log(`Loading: ${region}_${randomIndex}.jpg`)

    // 동적 import로 필요한 이미지만 로드
    const module = await import(`@/assets/regions/${region}/${region}_${randomIndex}.jpg`)
    return module.default

  } catch (error) {
    console.error(`이미지 로드 실패: ${region}`, error)
    errorMessage.value = `${region} 이미지를 찾을 수 없습니다.`

    // 기본 이미지 로드 시도
    try {
      const defaultModule = await import('@/assets/regions/default.jpg')
      return defaultModule.default
    } catch (defaultError) {
      console.error('기본 이미지도 로드 실패:', defaultError)
      return null
    }
  } finally {
    loading.value = false
  }
}

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const goToDetail = (planId, imageUrl) => {
  router.push({
  path : `/trip-plan/${planId}`,
  query: {
        imageUrl: encodeURIComponent(imageUrl)
      }
  })
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

const fetchImage = async () => {
                     // 반복문으로 각 지역의 이미지 로드
                     const regions = Object.keys(regionImageCounts)
                     for (const region of regions) {  // 👈 index 없이 in 사용
                       try {
                         const imageUrl = await getLocationImage(region)
                         imageUrls.value[region] = imageUrl  // 지역명을 키로 저장
                       } catch (error) {
                         console.error(`${region} 이미지 로드 실패:`, error)
                         imageUrls.value[region] = null
                       }
                     }
                     loading.value = false
                   }

onMounted(() => {
  fetchTripPlans()
  fetchImage()
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
  position: relative;
}

.trip-plan-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

/* 카드 액션 버튼 스타일 */
.card-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  z-index: 10;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.view-btn {
  background-color: rgba(52, 152, 219, 0.9);
  color: white;
}

.view-btn:hover {
  background-color: rgba(52, 152, 219, 1);
  transform: scale(1.1);
}

.delete-btn {
  background-color: rgba(231, 76, 60, 0.9);
  color: white;
}

.delete-btn:hover {
  background-color: rgba(231, 76, 60, 1);
  transform: scale(1.1);
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
  left: 12px;
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

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  max-width: 400px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 24px 24px 0 24px;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.modal-body {
  padding: 20px 24px;
}

.modal-body p {
  margin: 0 0 12px 0;
  color: #555;
  line-height: 1.5;
}

.warning-text {
  color: #e74c3c;
  font-size: 14px;
  font-weight: 500;
}

.modal-footer {
  padding: 0 24px 24px 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.cancel-btn {
  padding: 10px 20px;
  border: 1px solid #ddd;
  background: white;
  color: #666;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background-color: #f8f9fa;
  border-color: #999;
}

.confirm-delete-btn {
  padding: 10px 20px;
  border: none;
  background-color: #e74c3c;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.confirm-delete-btn:hover:not(:disabled) {
  background-color: #c0392b;
}

.confirm-delete-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
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

  .card-actions {
    top: 8px;
    right: 8px;
  }

  .action-btn {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }

  .modal-content {
    margin: 20px;
  }
}
</style>

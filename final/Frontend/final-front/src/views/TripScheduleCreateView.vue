<!-- views/TripScheduleCreateView.vue -->
<template>
  <div class="trip-item-create-container">
    <div class="create-header">
      <button @click="goBack" class="back-btn">
        ← 돌아가기
      </button>
      <h1>새로운 여행 일정 추가</h1>
      <p>{{ tripPlan?.planName || '여행 계획' }}에 새로운 일정을 추가해보세요</p>
    </div>

    <div class="create-content">
      <!-- 관광지 검색 섹션 -->
      <div class="search-section">
        <h2>📍 관광지 선택</h2>
        <div class="search-container">
          <div class="search-filters">
            <select v-model="searchFilters.sido" @change="onSidoChange" class="filter-select">
              <option value="">시/도 선택</option>
              <option v-for="sido in sidoList" :key="sido.code" :value="sido.code">
                {{ sido.name }}
              </option>
            </select>

            <select v-model="searchFilters.gugun" :disabled="!searchFilters.sido" class="filter-select">
              <option value="">구/군 선택</option>
              <option v-for="gugun in gugunList" :key="gugun.code" :value="gugun.code">
                {{ gugun.name }}
              </option>
            </select>

            <select v-model="searchFilters.contentType" class="filter-select">
              <option value="">관광지 유형</option>
              <option value="12">관광지</option>
              <option value="14">문화시설</option>
              <option value="15">축제공연행사</option>
              <option value="25">여행코스</option>
              <option value="28">레포츠</option>
              <option value="32">숙박</option>
              <option value="38">쇼핑</option>
              <option value="39">음식점</option>
            </select>

            <button @click="searchAttractions" class="search-btn" :disabled="searchLoading">
              {{ searchLoading ? '검색 중...' : '검색' }}
            </button>
          </div>

          <div class="keyword-search">
            <input
              type="text"
              v-model="searchKeyword"
              placeholder="관광지명을 입력하세요"
              @keyup.enter="searchAttractions"
              class="keyword-input"
            />
          </div>
        </div>

        <!-- 검색 결과 -->
        <div v-if="searchLoading" class="loading">
          <p>관광지를 검색하고 있습니다...</p>
        </div>

        <div v-else-if="attractions && attractions.length > 0" class="attractions-grid">
          <div
            v-for="attraction in attractions"
            :key="attraction.contentid || attraction.no"
            class="attraction-card"
            :class="{ selected: selectedAttraction?.contentid === attraction.contentid || selectedAttraction?.no === attraction.no }"
            @click="selectAttraction(attraction)"
          >
            <div class="attraction-image">
              <img
              v-if="attraction.firstimage || attraction.firstImage1"
                :src="attraction.firstimage || attraction.firstImage1"
                :alt="attraction.title"
              />
               <div v-else class="no-image">
                  이미지 없음
                </div>
            </div>
            <div class="attraction-info">
              <h3>{{ attraction.title }}</h3>
              <p class="attraction-addr">{{ attraction.addr1 }}</p>
              <div class="attraction-meta">
                <span class="content-type">{{ getContentTypeText(attraction.contenttypeid || attraction.contentTypeId) }}</span>
                <span class="distance" v-if="attraction.dist">{{ Math.round(attraction.dist) }}m</span>
              </div>
            </div>
            <div v-if="selectedAttraction?.contentid === attraction.contentid || selectedAttraction?.no === attraction.no" class="selected-badge">
              ✓ 선택됨
            </div>
          </div>
        </div>

        <div v-else-if="searchTriggered && !searchLoading" class="no-results">
          <p>검색 결과가 없습니다. 다른 조건으로 검색해보세요.</p>
        </div>

        <div v-else class="search-guide">
          <p>📍 지역을 선택하거나 관광지명을 입력하여 검색해보세요</p>
          <div v-if="sidoLoading" class="loading-small">
            <p>지역 정보 로딩 중...</p>
          </div>
          <div v-if="sidoError" class="error-small">
            <p>지역 정보를 불러오는데 실패했습니다. 페이지를 새로고침 해주세요.</p>
          </div>
        </div>
      </div>

      <!-- 일정 정보 입력 섹션 -->
      <div class="item-form-section">
        <h2>📝 일정 정보 입력</h2>

        <form @submit.prevent="handleSubmit" class="item-form">

          <div class="form-row">
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
           </div>

          <div class="form-group">
            <label for="category">카테고리 *</label>
            <select id="category" v-model="itemForm.category" required>
              <option value="sightseeing">관광</option>
              <option value="food">음식</option>
              <option value="activity">액티비티</option>
              <option value="transport">교통</option>
              <option value="shopping">쇼핑</option>
              <option value="accommodation">숙박</option>
            </select>
          </div>

          <div class="form-group">
            <label for="budget">예산</label>
            <input
              type="number"
              id="budget"
              v-model="itemForm.budget"
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


          <!-- 선택된 관광지 정보 표시 -->
          <div v-if="selectedAttraction" class="selected-attraction-info">
            <h3>선택된 관광지</h3>
            <div class="selected-attraction-card">
              <div class="attraction-image-small">
                <img
                v-if="selectedAttraction.firstimage || selectedAttraction.firstImage1"
                  :src="selectedAttraction.firstimage || selectedAttraction.firstImage1"
                  :alt="selectedAttraction.title"
                />
                <div v-else class="no-image-small">
                    이미지 없음
                  </div>
              </div>
              <div class="attraction-details">
                <h4>{{ selectedAttraction.title }}</h4>
                <p>{{ selectedAttraction.addr1 }}</p>
                <span class="content-type-small">{{ getContentTypeText(selectedAttraction.contenttypeid || selectedAttraction.contentTypeId) }}</span>
              </div>
              <button type="button" @click="clearSelection" class="clear-btn">
                변경
              </button>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" @click="goBack" class="cancel-btn">
              취소
            </button>
            <button type="submit" class="submit-btn" :disabled="!isFormValid || isSubmitting">
              {{ isSubmitting ? '추가 중...' : '일정 추가하기' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '@/utils/axios'

const router = useRouter()
const route = useRoute()

const tripPlan = ref(null)
const searchLoading = ref(false)
const searchTriggered = ref(false)
const isSubmitting = ref(false)
const attractions = ref([])
const selectedAttraction = ref(null)
const searchKeyword = ref('')
const sidoList = ref([])
const gugunList = ref([])
const contentTypeMap = reactive({})
const sidoLoading = ref(false)
const sidoError = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)

const searchFilters = reactive({
  sido: '',
  gugun: '',
  contentType: ''
})

const itemForm = reactive({
  title: '',
  scheduledAt: '',
  duration: null,
  category: 'sightseeing',
  budget: null,
  description: '',
  notes: ''
})

const fetchSidos = async () => {
  try {
    sidoLoading.value = true
    sidoError.value = false

    const response = await api.get('/attractions/sidos')

    // API 응답 데이터 구조를 변환하여 sidoList에 저장
    sidoList.value = [
      { code: '0', name: '전국' },
      ...response.data.map(sido => ({
        code: sido.sidoCode.toString(),
        name: sido.sidoName
      }))
    ]

    sidoLoading.value = false
    console.log('시도 목록 로드 완료:', sidoList.value)
  } catch (error) {
    console.error('시도 목록 로딩 실패:', error)
    sidoLoading.value = false
    sidoError.value = true

    // 에러 발생 시 기본 데이터로 대체
    sidoList.value = [
      { code: '0', name: '전국' },
      { code: '1', name: '서울' },
      { code: '2', name: '인천' },
      { code: '3', name: '대전' },
      { code: '4', name: '대구' },
      { code: '5', name: '광주' },
      { code: '6', name: '부산' },
      { code: '7', name: '울산' }
    ]
  }
}

const onSidoChange = () => {
  searchFilters.gugun = ''
  fetchGuguns(searchFilters.sido)
}

const fetchGuguns = async (sidoCode) => {
  try {
    if (!sidoCode || sidoCode === '0') {
      gugunList.value = []
      console.log('no value')
      return
    }

    const response = await api.get(`/attractions/guguns`)
    // API 응답 데이터 구조를 변환
    gugunList.value = response.data
      .filter(gugun => gugun.sidoCode === parseInt(sidoCode))
      .map(gugun => ({
        code: gugun.gugunCode.toString(),
        name: gugun.gugunName
      }))

    console.log('구군 목록 로드 완료:', gugunList.value)
  } catch (error) {
    console.error('구군 목록 로딩 실패:', error)
    gugunList.value = []

    // 에러 발생 시 기본 데이터로 대체 (선택된 시도에 따라 다르게 제공해야 함)
    if (sidoCode === '1') { // 서울
      gugunList.value = [
        { code: '1', name: '강남구' },
        { code: '2', name: '강동구' },
        { code: '3', name: '강북구' },
        { code: '4', name: '강서구' },
        { code: '5', name: '관악구' }
      ]
    }
  }
}

// AttractionView.vue와 동일한 방식으로 searchAttractions 함수 수정
const searchAttractions = async () => {
  try {
    searchLoading.value = true
    searchTriggered.value = true

    const params = {
      page: currentPage.value,
      size: 10,
      keyword: searchKeyword.value || null,
      sidoCode: searchFilters.sido || null,
      gugunCode: searchFilters.gugun || null,
      category: searchFilters.contentType || null
    }

    console.log('관광지 검색 파라미터:', params)
    // AttractionView.vue와 동일한 API 엔드포인트 사용
    const response = await api.get('/attractions/searchByFilter', { params })

    if (response.data && response.data.content) {
      attractions.value = response.data.content
      totalPages.value = response.data.totalPages
      console.log('페이지 정보:', {
        totalPages: response.data.totalPages,
        totalElements: response.data.totalElements,
        currentPage: response.data.number
      })
    } else {
      attractions.value = response.data
    }

    // undefined 체크
    if (!attractions.value) {
      attractions.value = []
      console.error('API 응답 데이터 구조 문제: attractions이 undefined입니다', response.data)
    }

    searchLoading.value = false
    console.log('검색된 관광지:', attractions.value.length)
  } catch (error) {
    console.error('관광지 검색 실패:', error)
    searchLoading.value = false
    alert('관광지 검색 중 오류가 발생했습니다.')
    attractions.value = []  // 오류 시 빈 배열로 초기화

    // 디버깅을 위한 샘플 데이터 추가 (실제 배포 시 제거)
    /*
    attractions.value = [
      {
        no: 1,
        contentid: '1',
        title: '경복궁',
        addr1: '서울특별시 종로구 사직로 161',
        firstimage: 'https://via.placeholder.com/200x150?text=경복궁',
        contenttypeid: '12',
        dist: '1200'
      },
      {
        no: 2,
        contentid: '2',
        title: '명동성당',
        addr1: '서울특별시 중구 명동길 74',
        firstimage: 'https://via.placeholder.com/200x150?text=명동성당',
        contenttypeid: '14',
        dist: '800'
      }
    ]
    */
  }
}

const fetchContentTypes = async () => {
  try {
    const response = await api.get('/attractions/contenttypes')

    // API 응답에서 contentTypeMap 구성
    response.data.forEach(type => {
      contentTypeMap[type.contentTypeId] = mapContentTypeToCategory(type.contentTypeId)
    })

    console.log('컨텐츠 타입 매핑:', contentTypeMap)
  } catch (error) {
    console.error('카테고리 목록 로딩 실패:', error)

    // 기본 매핑 설정
    contentTypeMap['12'] = 'sightseeing'  // 관광지
    contentTypeMap['14'] = 'sightseeing'  // 문화시설
    contentTypeMap['15'] = 'activity'     // 축제공연행사
    contentTypeMap['25'] = 'activity'     // 여행코스
    contentTypeMap['28'] = 'activity'     // 레포츠
    contentTypeMap['32'] = 'accommodation' // 숙박
    contentTypeMap['38'] = 'shopping'     // 쇼핑
    contentTypeMap['39'] = 'food'         // 음식점
  }
}

const mapContentTypeToCategory = (contentTypeId) => {
  const mapping = {
    '12': 'sightseeing',  // 관광지
    '14': 'sightseeing',  // 문화시설
    '15': 'activity',     // 축제공연행사
    '25': 'activity',     // 여행코스
    '28': 'activity',     // 레포츠
    '32': 'accommodation', // 숙박
    '38': 'shopping',     // 쇼핑
    '39': 'food'          // 음식점
  }

  return mapping[contentTypeId] || 'sightseeing'
}

const selectAttraction = (attraction) => {
  selectedAttraction.value = attraction
  if (!itemForm.title) {
    itemForm.title = attraction.title
  }

  // 컨텐츠 타입 ID에 따라 카테고리 설정
  const contentTypeId = attraction.contenttypeid || attraction.contentTypeId
  const category = contentTypeMap[contentTypeId]
  if (category) {
    itemForm.category = category
  }
}

const clearSelection = () => {
  selectedAttraction.value = null
  itemForm.title = ''
}

const getContentTypeText = (contentTypeId) => {
  const contentTypes = {
    '12': '관광지',
    '14': '문화시설',
    '15': '축제공연행사',
    '25': '여행코스',
    '28': '레포츠',
    '32': '숙박',
    '38': '쇼핑',
    '39': '음식점'
  }
  return contentTypes[contentTypeId] || '기타'
}

const isFormValid = computed(() => {
  return itemForm.scheduledAt &&
         itemForm.category &&
         selectedAttraction.value
})

const fetchTripPlan = async () => {
  try {
    const planId = route.params.planId
    const response = await api.get(`/trips/${planId}`)
    tripPlan.value = response.data.plan // TripPlanResponseDto 구조에 맞게 수정

    console.log('여행 계획 로드 완료:', tripPlan.value)

    // 기본 일정 시간 설정 (시작 날짜의 오전 10시)
    if (tripPlan.value && tripPlan.value.startDate) {
      const defaultDate = new Date(tripPlan.value.startDate)
      defaultDate.setHours(10, 0, 0)
      itemForm.scheduledAt = defaultDate.toISOString().slice(0, 16) // YYYY-MM-DDTHH:MM 형식
    }
  } catch (error) {
    console.error('여행 계획 조회 실패:', error)
    alert('여행 계획을 불러올 수 없습니다.')
    router.push('/trip-plan')
  }
}

const getMinDateTime = () => {
  if (!tripPlan.value || !tripPlan.value.startDate) return ''
  return `${tripPlan.value.startDate.split('T')[0]}T00:00`
}

const getMaxDateTime = () => {
  if (!tripPlan.value || !tripPlan.value.endDate) return ''
  return `${tripPlan.value.endDate.split('T')[0]}T23:59`
}

const handleImageError = (event) => {
  event.target.src = ''
}

const handleSubmit = async () => {
  if (!selectedAttraction.value) {
    alert('관광지를 선택해주세요.')
    return
  }

  try {
    isSubmitting.value = true

    const formData = {
      price: itemForm.budget || 0,
      // contentid 또는 no 필드 처리
      no: selectedAttraction.value.no,
      date: itemForm.scheduledAt,
      category: itemForm.category,
      schedule: itemForm.description
    }

    console.log('전송할 데이터:', formData)
    const response = await api.post(`/trips/${route.params.planId}`, formData)

    console.log('일정 추가 성공:', response.data)
    alert('여행 일정이 성공적으로 추가되었습니다!')
    router.push(`/trip-plan/${route.params.planId}`)

  } catch (error) {
    console.error('일정 생성 실패:', error)
    isSubmitting.value = false
    alert('일정 생성 중 오류가 발생했습니다.')
  }
}

const goBack = () => {
  if (itemForm.description || selectedAttraction.value) {
    if (confirm('작성 중인 내용이 있습니다. 정말로 나가시겠습니까?')) {
      router.push(`/trip-plan/${route.params.planId}`)
    }
  } else {
    router.push(`/trip-plan/${route.params.planId}`)
  }
}

onMounted(async () => {
  try {
    console.log('컴포넌트 마운트 - 데이터 로딩 시작')

    // 시도 목록 먼저 로드
    await fetchSidos()

    // 병렬로 다른 데이터 로드
    await Promise.all([
      fetchContentTypes(),
      fetchTripPlan()
    ])

    console.log('초기 데이터 로딩 완료')
  } catch (error) {
    console.error('초기 데이터 로딩 실패:', error)
    // 사용자에게 오류 표시
    alert('데이터를 불러오는 중 오류가 발생했습니다. 페이지를 새로고침 해주세요.')
  }
})
</script>

<style scoped>
.trip-item-create-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 60px 20px;
}

.create-header {
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

.create-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
  font-weight: 600;
}

.create-header p {
  font-size: 16px;
  color: #666;
}

.create-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.search-section,
.item-form-section {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.search-section h2,
.item-form-section h2 {
  font-size: 20px;
  color: #333;
  margin-bottom: 24px;
  font-weight: 600;
}

.search-container {
  margin-bottom: 30px;
}

.search-filters {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr auto;
  gap: 12px;
  margin-bottom: 16px;
}

.filter-select,
.keyword-input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.filter-select:disabled {
  background-color: #f8f9fa;
  color: #6c757d;
}

.search-btn {
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

.search-btn:hover:not(:disabled) {
  background-color: #2980b9;
}

.search-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.keyword-search {
  width: 100%;
}

.keyword-input {
  width: 100%;
}

.loading {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.loading-small {
  text-align: center;
  padding: 10px;
  color: #666;
  font-size: 14px;
  margin-top: 10px;
}

.error-small {
  text-align: center;
  padding: 10px;
  color: #e74c3c;
  font-size: 14px;
  margin-top: 10px;
  background-color: #fdf0ed;
  border-radius: 6px;
}

.search-guide {
  text-align: center;
  padding: 40px 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  color: #666;
}

.no-results {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.attractions-grid {
  display: grid;
  gap: 16px;
  max-height: 600px;
  overflow-y: auto;
}

.attraction-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.attraction-card:hover {
  border-color: #3498db;
  background-color: #f8f9fa;
}

.attraction-card.selected {
  border-color: #27ae60;
  background-color: #f0fff4;
}

.attraction-image {
  width: 80px;
  height: 60px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
}

.attraction-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attraction-info {
  flex: 1;
}

.attraction-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.attraction-addr {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.attraction-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}

.content-type {
  font-size: 12px;
  padding: 2px 8px;
  background-color: #3498db;
  color: white;
  border-radius: 12px;
}

.distance {
  font-size: 12px;
  color: #888;
}

.selected-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: #27ae60;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 12px;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.selected-attraction-info {
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.selected-attraction-info h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}

.selected-attraction-card {
  display: flex;
  gap: 12px;
  align-items: center;
}

.attraction-image-small {
  width: 60px;
  height: 45px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.attraction-image-small img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attraction-details {
  flex: 1;
}

.attraction-details h4 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.attraction-details p {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.content-type-small {
  font-size: 11px;
  padding: 2px 6px;
  background-color: #3498db;
  color: white;
  border-radius: 8px;
}

.clear-btn {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.2s;
}

.clear-btn:hover {
  background-color: #c0392b;
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
  .create-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .search-filters {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions button {
    width: 100%;
  }
}

.no-image {
  width: 100%;
  height: 100%;
  background-color: #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #666;
}

.no-image-small {
  width: 100%;
  height: 100%;
  background-color: #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #666;
}
</style>

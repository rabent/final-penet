<template>
  <div class="attraction-page">
    <!-- 상단 검색 및 필터 영역 -->
    <div class="search-section">
      <!-- 검색바와 필터 버튼 -->
      <div class="search-bar-container">
        <div class="search-bar">
          <div class="search-input-wrapper">
            <i class="fas fa-search search-icon"></i>
            <input 
              v-model="searchKeyword" 
              placeholder="관광지를 검색해보세요"
              class="search-input"
              @keyup.enter="handleSearch"
            />
          </div>
          <button class="search-action-button" @click="handleSearch">
            검색
          </button>
          <button 
            class="filter-button" 
            :class="{ active: isFilterOpen }"
            @click="isFilterOpen = !isFilterOpen"
          >
            <i class="fas fa-filter"></i>
            <span>필터</span>
          </button>
        </div>
      </div>

      <!-- 필터 패널 -->
      <div class="filter-panel" v-show="isFilterOpen">
        <!-- 시도 선택 -->
        <div class="filter-section">
          <h3>지역 선택</h3>
          <div class="sido-grid">
            <button
              v-for="sido in sidos"
              :key="sido.sidoCode"
              :class="['sido-button', { active: selectedSido === sido.sidoName }]"
              @click="selectSido(sido.sidoName)"
            >
              {{ sido.sidoName }}
            </button>
          </div>
        </div>

        <!-- 구군 선택 -->
        <div class="filter-section" v-if="showGugunFilter && guguns.length > 0 && selectedSido !== '전국'">
          <h3>구/군 선택</h3>
          <div class="gugun-grid">
            <button
              v-for="gugun in guguns"
              :key="gugun.gugunCode"
              :class="['gugun-button', { 
                active: selectedGugun?.gugunCode === gugun.gugunCode 
              }]"
              @click="selectGugun(gugun)"
            >
              {{ gugun.gugunName }}
            </button>
          </div>
        </div>

        <!-- 카테고리 선택 -->
        <div class="filter-section">
          <h3>카테고리 선택</h3>
          <div class="category-grid">
            <button
              v-for="category in categories"
              :key="category.contentTypeId"
              :class="['category-button', { active: selectedCategory === category.contentTypeName }]"
              @click="selectCategory(category.contentTypeName)"
            >
              {{ category.contentTypeName }}
            </button>
          </div>
        </div>

        <!-- 선택된 필터 표시 -->
        <div class="selected-filters" v-if="selectedSido !== '전국' || selectedCategory !== '전체' || selectedGugun">
          <div class="filter-tag" v-if="selectedSido !== '전국'">
            {{ selectedSido }}
            <button class="tag-remove" @click="selectSido('전국')">×</button>
          </div>
          <div class="filter-tag" v-if="selectedGugun">
            {{ selectedGugun.gugunName }}
            <button class="tag-remove" @click="selectGugun(selectedGugun)">×</button>
          </div>
          <div class="filter-tag" v-if="selectedCategory !== '전체'">
            {{ selectedCategory }}
            <button class="tag-remove" @click="selectCategory('전체')">×</button>
          </div>
        </div>
      </div>
    </div>

    <div class="content-wrapper">
      <!-- 왼쪽 관광지 리스트 -->
      <div class="attraction-list">
        <div v-if="isLoading" class="loading-state">
          데이터를 불러오는 중...
        </div>
        <div 
          v-else-if="attractions.length === 0" 
          class="empty-state"
        >
          검색 결과가 없습니다.
        </div>
        <div 
          v-else
          v-for="place in attractions" 
          :key="place.no" 
          class="attraction-card"
          :class="{ active: selectedAttractionId === place.no }"
          @click="handleAttractionClick(place)"
        >
          <div class="attraction-info">
            <h3>{{ place.title }}</h3>
            <p class="address">
              <i class="fas fa-map-marker-alt"></i>
              {{ place.addr1 }}
            </p>
            <p class="overview">{{ truncateText(place.overview, 100) }}</p>
            <div class="info-footer">
              <span class="category-tag">{{ place.contentTypeName }}</span>
              <span class="sido-tag">{{ place.areaName }}</span>
              <span class="gugun-tag">{{ place.siGunguName }}</span>
            </div>
          </div>
        </div>
        <div class="pagination">
          <button 
            class="page-button"
            :disabled="currentPage === 0"
            @click="handlePrevPage"
          >
            <i class="fas fa-chevron-left"></i> 이전
          </button>
          <span class="page-info">{{ currentPage + 1 }} 페이지</span>
          <button 
            class="page-button"
            :disabled="!hasNextPage"
            @click="handleNextPage"
          >
            다음 <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <!-- 오른쪽 지도 영역 -->
      <div class="map-container">
        <KakaoMap
          :lat="mapCenter.lat"
          :lng="mapCenter.lng"
          :level="mapCenter.level"
          :draggable="true"
          :zoomable="true"
          @on-load-kakao-map="handleMapLoad"
          style="width: 100%; height: 100%;"
        >
          <KakaoMapMarker 
            v-for="place in attractions"
            :order = "place.title"
            :key="place.no"
            :lat = Number(place.latitude)
            :lng = Number(place.longitude)
            :clickable="true"
            
            @on-click-kakao-map-marker="handleMarkerClick(place)"
          />

            <!-- 선택된 관광지에 대한 오버레이 추가 -->
          <KakaoMapCustomOverlay
            v-if="selectedAttractionId && isOverlayVisible"
            :lat="Number(selectedPlace?.latitude)"
            :lng="Number(selectedPlace?.longitude)"
            :y-anchor="1.2"
          >
            <template #default>
              <div class="custom-overlay">
                <div class="overlay-content">
                  <div class="overlay-header">
                    <div class="overlay-title">
                      <h4>{{ selectedPlace?.title }}</h4>
                    </div>
                    <div class="overlay-image" v-if="selectedPlace?.firstImage1 || selectedPlace?.firstImage2">
                      <img 
                        :src="selectedPlace?.firstImage1 || selectedPlace?.firstImage2" 
                        alt="관광지 이미지"
                      />
                    </div>
                  </div>
                  <p class="overlay-address">
                    <i class="fas fa-map-marker-alt"></i>
                    {{ selectedPlace?.addr1 }}
                  </p>
                  <div class="overlay-actions">
                    <button @click="navigateToDetail" class="detail-button">
                      <i class="fas fa-info-circle"></i>
                      상세보기
                    </button>
                    <button @click="closeOverlay" class="close-button">
                      <i class="fas fa-times"></i>
                      닫기
                    </button>
                  </div>
                </div>
              </div>
            </template>
          </KakaoMapCustomOverlay>
        
        </KakaoMap>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { KakaoMap, KakaoMapMarker,KakaoMapCustomOverlay } from 'vue3-kakao-maps'
import api from '@/utils/axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const attractions = ref([])

const searchKeyword = ref('')
const selectedCategory = ref('전체')
const selectedAttractionId = ref(null)
const isFilterOpen = ref(false)
const selectedSido = ref('전국')  // 기본값을 '전국'으로 변경
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const hasNextPage = computed(() => currentPage.value < totalPages.value - 1)

const categories = ref([{ contentTypeId: 0, contentTypeName: '전체' }])

const sidos = ref([{ sidoCode: 0, sidoName: '전국' }])

// 구군 관련 상태 추가
const guguns = ref([])
const selectedGugun = ref(null)
const showGugunFilter = ref(false)  // 구군 필터 표시 여부

// script setup 부분에 상태 추가
const mapCenter = ref({
  lat: 36.2683, // 대한민국 중심 위도
  lng: 127.6358, // 대한민국 중심 경도
  level: 4 // 초기 줌 레벨
})

// map 인스턴스 참조를 위한 ref 추가
const map = ref(null) 

// 상태 추가
const isOverlayVisible = ref(false)

// handleMapLoad 함수 수정
const handleMapLoad = (mapInstance) => {
  map.value = mapInstance
}

const selectedPlace = computed(() => 
  attractions.value.find(place => place.no === selectedAttractionId.value)
)

// adjustMapBounds 함수 수정
const adjustMapBounds = () => { 
  if (!map.value || attractions.value.length === 0) return
  const bounds = new kakao.maps.LatLngBounds()
  
  attractions.value.forEach(place => {
    bounds.extend(
      new kakao.maps.LatLng(
        Number(place.latitude), 
        Number(place.longitude)
      )
    )
  })
  
  map.value.setBounds(bounds)
}

// 검색 조건으로 관광지 조회 함수 수정
const searchAttractions = async () => {
  try {
    isLoading.value = true
    const params = {
      page: currentPage.value,
      size: 10,
      keyword: searchKeyword.value || null,
      sidoCode: selectedSido.value !== '전국' ? getSidoCode(selectedSido.value) : null,
      gugunCode: selectedGugun.value?.gugunCode || null,
      category: selectedCategory.value !== '전체' ? getCategoryCode(selectedCategory.value) : null
    }
    
    const response = await api.get('/attractions/searchByFilter', { params })
    attractions.value = response.data.content
    totalPages.value = response.data.totalPages

    // 검색 결과가 있을 경우 지도 경계 조정
    if (response.data.content.length > 0) {
      nextTick(() => {
        adjustMapBounds()
      })
    }
  } catch (error) {
    console.error('관광지 검색 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// 시도 목록 조회 함수
const fetchSidos = async () => {
  try {
    const response = await api.get('/attractions/sidos')
    // '전국' 옵션과 함께 시도 목록 설정
    sidos.value = [
      { sidoCode: 0, sidoName: '전국' },
      ...response.data
    ]
  } catch (error) {
    console.error('시도 목록 로딩 실패:', error)
  }
}

// 구군 목록 조회 함수 수정
const fetchGuguns = async (sidoCode) => {
  try {
    if (!sidoCode || sidoCode === 0) {
      guguns.value = []
      return
    }
    
    const response = await api.get('/attractions/guguns')
    // 선택된 시도코드와 일치하는 구군만 필터링
    guguns.value = response.data.filter(gugun => gugun.sidoCode === sidoCode)
  } catch (error) {
    console.error('구군 목록 로딩 실패:', error)
    guguns.value = []
  }
}

// 시도 코드 매핑 함수
const getSidoCode = (sidoName) => {
  const sido = sidos.value.find(s => s.sidoName === sidoName)
  return sido?.sidoCode || null
}

// 카테고리 코드 매핑 함수
const getCategoryCode = (categoryName) => {
  const category = categories.value.find(c => c.contentTypeName === categoryName)
  return category?.contentTypeId || null
}

// 검색 핸들러 수정
const handleSearch = () => {
  currentPage.value = 0    // 페이지 초기화
  isFilterOpen.value = false  // 필터 패널 닫기
  searchAttractions()      // 검색 실행
}

// 필터 선택 핸들러 수정
const selectCategory = (cat) => {
  if (selectedCategory.value === cat) {
    selectedCategory.value = '전체'
  } else {
    selectedCategory.value = cat
  }
}

// 시도 선택 핸들러 수정
const selectSido = async (sido) => {
  // 이전 상태와 같은 시도를 선택하거나 전국을 선택한 경우
  if (selectedSido.value === sido || sido === '전국') {
    selectedSido.value = '전국'
    selectedGugun.value = null
    showGugunFilter.value = false
    guguns.value = []
  } else {
    selectedSido.value = sido
    selectedGugun.value = null // 시도가 변경될 때 구군 선택 초기화
    const sidoCode = getSidoCode(sido)
    if (sidoCode && sidoCode !== 0) {
      showGugunFilter.value = true
      await fetchGuguns(sidoCode)
    } else {
      showGugunFilter.value = false
      guguns.value = []
    }
  }
}

// 구군 선택 핸들러
const selectGugun = (gugun) => {
  if (selectedGugun.value?.gugunName === gugun.gugunName) {
    selectedGugun.value = null
  } else {
    selectedGugun.value = gugun
  }
}

// 관광지 클릭 핸들러 수정
const handleAttractionClick = (place) => {
  selectedAttractionId.value = place.no
  isOverlayVisible.value = true
  mapCenter.value = {
    lat: Number(place.latitude),
    lng: Number(place.longitude),
  }
  map.value.setCenter(new kakao.maps.LatLng(Number(place.latitude), Number(place.longitude)))
  map.value.setLevel(1) // 줌 레벨 조정
}

// 마커 클릭 핸들러 수정
const handleMarkerClick = (place) => {
  selectedAttractionId.value = place.no
  isOverlayVisible.value = true
  mapCenter.value = {
    lat: Number(place.latitude),
    lng: Number(place.longitude)
  }
  map.value.panTo(new kakao.maps.LatLng(Number(place.latitude), Number(place.longitude)))
}

// 오버레이 닫기 함수 수정
const closeOverlay = () => {
  isOverlayVisible.value = false
}

// 상세 페이지 이동 함수 추가
const navigateToDetail = () => {
  if (selectedAttractionId.value) {
    router.push(`/attractions/${selectedAttractionId.value}`)
  }
}

// 텍스트 길이 제한 함수
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 페이지 이동 핸들러 수정
const handlePrevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
    searchAttractions() // 필터 조건 유지하며 이전 페이지 검색
  }
}

const handleNextPage = () => {
  if (hasNextPage.value) {
    currentPage.value++
    searchAttractions() // 필터 조건 유지하며 다음 페이지 검색
  }
}

// 카테고리 목록 조회 함수
const fetchContentTypes = async () => {
  try {
    const response = await api.get('/attractions/contenttypes')
    // '전체' 옵션과 함께 카테고리 목록 설정
    categories.value = [
      { contentTypeId: 0, contentTypeName: '전체' },
      ...response.data
    ]
  } catch (error) {
    console.error('카테고리 목록 로딩 실패:', error)
  }
}

// 컴포넌트 마운트 시 데이터 로딩
onMounted(async () => {
  try {
    // 초기 데이터 로딩 (시도, 카테고리 목록)
    await Promise.all([
      fetchSidos(),
      fetchContentTypes()
    ])
    // 관광지 데이터 로딩
    await searchAttractions()
  } catch (error) {
    console.error('초기 데이터 로딩 실패:', error)
  }
})
</script>

<style scoped>
.attraction-page {
  padding: 2rem;
  height: calc(100vh - 70px);
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.search-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.search-bar-container {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: white;
  padding: 0.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-input-wrapper {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 1rem;
  color: #666;
}

.search-input {
  width: 100%;
  padding: 0.8rem 1rem 0.8rem 2.5rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #3498db;
  outline: none;
}

.search-action-button {
  padding: 0.8rem 1.5rem;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.search-action-button:hover {
  background: #2980b9;
}

.filter-button {
  padding: 0.8rem 1.2rem;
  background: #f5f5f5;
  border: none;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center; /* 중앙 정렬 */
  gap: 0.5rem;
  transition: all 0.3s;
  min-width: 90px; /* 최소 너비 설정 */
  text-align: center; /* 텍스트 중앙 정렬 */
}

.filter-button i {
  display: flex; /* 아이콘도 flex로 설정 */
  align-items: center;
  justify-content: center;
  margin: 0; /* 기존 마진 제거 */
}

/* 필터 버튼 내부 텍스트 정렬을 위한 추가 스타일 */
.filter-button span {
  flex: 1;
  text-align: center;
}

.filter-button.active {
  background: #e8f4fd;
  color: #3498db;
}

.filter-panel {
  margin-top: 1rem;
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.filter-section {
  margin-bottom: 1.5rem;
}

.filter-section h3 {
  font-size: 1rem;
  color: #333;
  margin-bottom: 1rem;
}

.sido-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 0.8rem;
  margin-bottom: 1.5rem;
}

.sido-button {
  padding: 0.8rem;
  border: none;
  border-radius: 8px;
  background: #f5f5f5;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
}

.sido-button:hover {
  background: #e0e0e0;
}

.sido-button.active {
  background: #3498db;
  color: white;
}

.gugun-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 0.8rem;
  margin-bottom: 1.5rem;
}

.gugun-button {
  padding: 0.8rem;
  border: none;
  border-radius: 8px;
  background: #f5f5f5;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
}

.gugun-button:hover {
  background: #e0e0e0;
}

.gugun-button.active {
  background: #3498db;
  color: white;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); /* 너비 증가 */
  gap: 1rem; /* 간격 증가 */
  padding: 0.5rem; /* 패딩 추가 */
}

.category-button {
  padding: 0.8rem 1rem;
  border: none;
  border-radius: 8px;
  background: #f5f5f5;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
  text-align: center;
  width: 100%; /* 너비 100%로 설정 */
}

/* 카테고리 버튼 활성화 스타일 추가 */
.category-button.active {
  background: #3498db;
  color: white;
  font-weight: 500;
}

.category-button:hover {
  background: #e0e0e0;
}

.category-button.active:hover {
  background: #2980b9;
}

.selected-filters {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.filter-tag {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #e8f4fd;
  color: #3498db;
  border-radius: 20px;
  font-size: 0.9rem;
}

.tag-remove {
  background: none;
  border: none;
  color: #3498db;
  cursor: pointer;
  font-size: 1.2rem;
  padding: 0;
  display: flex;
  align-items: center;
}

.tag-remove:hover {
  color: #2980b9;
}

.category-tabs {
  display: flex;
  justify-content: center;
  gap: 0.8rem;  /* 간격 줄임 */
  flex-wrap: wrap;
  padding: 0 1rem;  /* 좌우 패딩 추가 */
}

.content-wrapper {
  display: flex;
  gap: 2rem;
  flex: 1;
  min-height: 0;
  height: calc(100vh - 200px); /* 상단 여백을 고려한 전체 높이 설정 */
}

.attraction-list {
  width: 400px;
  overflow-y: auto;
  padding-right: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  height: 100%; /* 높이를 100%로 설정 */
}

.attraction-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;  /* 패딩 추가 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
}

.attraction-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.attraction-card.active {
  border: 2px solid #3498db;
}

.attraction-info {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.attraction-info h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
  font-weight: 600;
}

.address {
  color: #666;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.address i {
  color: #e74c3c;
  font-size: 1rem;
}

.overview {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.5;
  margin: 0.5rem 0;
}

.info-footer {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
  flex-wrap: wrap;  /* 태그가 많을 경우 줄바꿈 */
}

.category-tag {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 15px;
  font-size: 0.8rem;
  background-color: #e8f4fd;
  color: #3498db;
}

.sido-tag {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 15px;
  font-size: 0.8rem;
  background-color: #f0f0f0;
  color: #666;
}

.gugun-tag {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 15px;
  font-size: 0.8rem;
  background-color: #f5f5f5;
  color: #888;
}

.map-container {
  flex: 1;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background: #f8f9fa;
  height: 100%; /* 높이를 100%로 설정 */
  min-width: 600px;
}

#map {
  width: 100%;
  height: 100%;
}

/* 스크롤바 스타일링 */
.attraction-list::-webkit-scrollbar {
  width: 8px;
}

.attraction-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.attraction-list::-webkit-scrollbar-thumb {
  background: #c0c0c0;
  border-radius: 4px;
}

.attraction-list::-webkit-scrollbar-thumb:hover {
  background: #a0a0a0;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.loading-state {
  animation: pulse 1.5s infinite;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  margin-top: auto;
  background: white;
  border-top: 1px solid #eee;
}

.page-button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  background: #f5f5f5;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-button:not(:disabled):hover {
  background: #e0e0e0;
}

.page-info {
  font-size: 0.9rem;
  color: #666;
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

/* 커스텀 오버레이 스타일 추가/수정 */
.custom-overlay {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  border: 2px solid #3498db;
  min-width: 300px;
  max-width: 400px;
  overflow: hidden;
}

.overlay-content {
  padding: 1rem;
}

.overlay-header {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.overlay-title {
  flex: 1;
}

.overlay-title h4 {
  margin: 0;
  font-size: 1.1rem;
  color: #333;
  font-weight: 600;
}

.overlay-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.overlay-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.overlay-address {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  font-size: 0.9rem;
  margin: 0.5rem 0 1rem;
}

.overlay-address i {
  color: #e74c3c;
}

.overlay-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
  margin-top: 1rem;
}

.detail-button, .close-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.detail-button {
  background: #3498db;
  color: white;
}

.detail-button:hover {
  background: #2980b9;
}

.close-button {
  background: #f5f5f5;
  color: #666;
}

.close-button:hover {
  background: #e0e0e0;
}
</style>

<template>
  <div class="attraction-detail">
    <div class="back-button-container">
      <div class="back-button-wrapper">
        <button class="back-button" @click="goBack">
          <i class="fas fa-arrow-left"></i> 목록으로 돌아가기
        </button>
      </div>
    </div>
    
    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
      로딩 중...
    </div>
    
    <div v-else-if="attraction" class="detail-container">
      <!-- 헤더 섹션 -->
      <div class="header-section">
        <h1>{{ attraction.title }}</h1>
        <div class="meta-info">
          <span class="tag category">{{ attraction.contentTypeName }}</span>
          <span class="tag area">{{ attraction.areaName }}</span>
          <span class="tag location">{{ attraction.siGunguName }}</span>
        </div>
      </div>

      <!-- 이미지 갤러리 섹션 교체 -->
      <ImageCarousel 
        :images="[
          attraction.firstImage1, 
          attraction.firstImage2
        ].filter(img => img)"
      />

      <!-- AI 팁 섹션 -->
      <div class="ai-tip-section">
        <div class="ai-tip-header">
          <h2><i class="fas fa-lightbulb"></i> AI 추천 팁</h2>
          <p class="ai-tip-description">AI가 이 관광지에 대한 맞춤 팁을 제공해드립니다</p>
        </div>
        
        <!-- AI 팁이 로드되지 않았을 때 -->
        <div v-if="!aiTip.loaded && !aiTip.loading" class="ai-tip-placeholder">
          <button 
            class="get-ai-tip-button" 
            @click="fetchAiTip"
            :disabled="aiTip.loading"
          >
            <i class="fas fa-magic"></i>
            AI 팁 받아보기
          </button>
        </div>

        <!-- AI 팁 로딩 중 -->
        <div v-if="aiTip.loading" class="ai-tip-loading">
          <div class="ai-spinner"></div>
          <p>AI가 맞춤 팁을 생성하고 있습니다...</p>
        </div>

        <!-- AI 팁 내용 -->
        <div v-if="aiTip.loaded && aiTip.content" class="ai-tip-content">
          <div class="tip-card">
            <div class="tip-icon">
              <i class="fas fa-robot"></i>
            </div>
            <div class="tip-text">
              <p>{{ aiTip.content }}</p>
            </div>
            <button 
              class="refresh-tip-button" 
              @click="fetchAiTip"
              :disabled="aiTip.loading"
              title="새로운 팁 받기"
            >
              <i class="fas fa-refresh"></i>
            </button>
          </div>
        </div>

        <!-- AI 팁 로드 실패 -->
        <div v-if="aiTip.loaded && aiTip.error" class="ai-tip-error">
          <div class="error-card">
            <i class="fas fa-exclamation-triangle"></i>
            <p>AI 팁을 가져오는데 실패했습니다.</p>
            <button class="retry-button" @click="fetchAiTip">
              <i class="fas fa-redo"></i>
              다시 시도
            </button>
          </div>
        </div>
      </div>

      <!-- 주요 정보 섹션 -->
      <div class="info-grid">
        <div class="info-card">
          <h3><i class="fas fa-map-marker-alt"></i> 주소</h3>
          <p>{{ attraction.fullAddress }}</p>
        </div>

        <div class="info-card" v-if="attraction.tel">
          <h3><i class="fas fa-phone"></i> 연락처</h3>
          <p>{{ attraction.tel }}</p>
        </div>

        <div class="info-card" v-if="attraction.homepage">
          <h3><i class="fas fa-globe"></i> 홈페이지</h3>
          <a :href="attraction.homepage" target="_blank">바로가기</a>
        </div>
      </div>

      <!-- 지도 섹션 -->
      <div class="map-section">
        <h2><i class="fas fa-map"></i> 위치 정보</h2>
        <div id="kakaoMap" class="map-container">
            <KakaoMap
                :lat = Number(attraction.latitude)
                :lng = Number(attraction.longitude)
                :level="1"
                :draggable="true"
                :zoomable="false"
                style="width: 100%; height: 100%;"
            >
                <KakaoMapMarker 
                    :order = "attraction.title"
                    :key= "attraction.no"
                    :lat = Number(attraction.latitude)
                    :lng = Number(attraction.longitude)
                    :clickable="false"
                />
            </KakaoMap>
        </div>
      </div>

      <!-- 상세 설명 섹션 -->
      <div class="description-section" v-if="attraction.overview">
        <h2>상세 설명</h2>
        <div class="content" v-html="formatDescription(attraction.overview)"></div>
      </div>

    </div>

    <div v-else class="error">
      <i class="fas fa-exclamation-circle"></i>
      관광지 정보를 찾을 수 없습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { KakaoMap, KakaoMapMarker } from 'vue3-kakao-maps'
import api from '@/utils/axios'
import ImageCarousel from '@/components/ImageCarousel.vue'

const route = useRoute()
const router = useRouter()
const attraction = ref(null)
const isLoading = ref(true)

// AI 팁 관련 반응형 상태
const aiTip = reactive({
  content: '',
  loading: false,
  loaded: false,
  error: false
})

const fetchAttractionDetail = async () => {
  try {
    const response = await api.get(`/attractions/${route.params.id}`)
    attraction.value = response.data
  } catch (error) {
    console.error('관광지 상세 정보 로딩 실패:', error)
  } finally {
    isLoading.value = false
  }
}

// AI 팁 가져오기 함수
const fetchAiTip = async () => {
  if (!attraction.value) return
  
  // 상태 초기화
  aiTip.loading = true
  aiTip.error = false
  
  try {
    const response = await api.get(`/ai/tip/${attraction.value.no}`)
    aiTip.content = response.data.tip
    aiTip.loaded = true
  } catch (error) {
    console.error('AI 팁 로딩 실패:', error)
    aiTip.error = true
    aiTip.loaded = true
  } finally {
    aiTip.loading = false
  }
}

const goBack = () => {
  router.back()
}

const formatDescription = (text) => {
  if (!text) return ''
  return text.replace(/\n/g, '<br>')
}

onMounted(() => {
  fetchAttractionDetail()
})
</script>

<style scoped>
.attraction-detail {
  padding: 4rem 2rem;
  max-width: 1400px;
  margin: 0 auto;
  background: #f8f9fa;
  min-height: 100vh;
}

.detail-container {
  display: flex;
  flex-direction: column;
  gap: 4rem;
}

.header-section {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.header-section h1 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #1a1a1a;
}

.meta-info {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

/* AI 팁 섹션 스타일 */
.ai-tip-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  color: white;
}

.ai-tip-header h2 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.ai-tip-description {
  opacity: 0.9;
  margin-bottom: 1.5rem;
}

.ai-tip-placeholder {
  text-align: center;
  padding: 2rem 0;
}

.get-ai-tip-button {
  display: inline-flex;
  align-items: center;
  gap: 0.8rem;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50px;
  color: white;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.get-ai-tip-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
}

.get-ai-tip-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.ai-tip-loading {
  text-align: center;
  padding: 2rem 0;
}

.ai-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

.ai-tip-content .tip-card {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 1.5rem;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  position: relative;
}

.tip-icon {
  font-size: 1.5rem;
  opacity: 0.8;
  flex-shrink: 0;
}

.tip-text {
  flex: 1;
  line-height: 1.6;
}

.refresh-tip-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  backdrop-filter: blur(5px);
}

.refresh-tip-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
  transform: rotate(180deg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.refresh-tip-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.refresh-tip-button i {
  color: white;
  font-size: 14px;
}

.ai-tip-error .error-card {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
}

.retry-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 25px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-button:hover {
  background: rgba(255, 255, 255, 0.3);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.info-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-card h3 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.info-card i {
  color: #3498db;
}

.description-section {
  background: white;
  padding: 3rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.description-section h2 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.content {
  font-size: 1.1rem;
  line-height: 2;
  color: #444;
}

.tag {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.tag.category {
  background: #e8f4fd;
  color: #3498db;
}

.tag.area {
  background: #f0f0f0;
  color: #666;
}

.tag.location {
  background: #e8f6f3;
  color: #16a085;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  color: #e74c3c;
  font-size: 1.2rem;
}

.back-button-container {
  position: sticky;
  top: 0;
  z-index: 100;
  background: linear-gradient(to bottom, rgba(248, 249, 250, 1) 0%, rgba(248, 249, 250, 0.9) 100%);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.back-button-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 0.8rem;
  padding: 0.8rem 1.5rem;
  border: 2px solid #3498db;
  border-radius: 50px;
  background: transparent;
  color: #3498db;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-button:hover {
  background: #3498db;
  color: white;
  transform: translateX(-5px);
}

.back-button i {
  font-size: 1.1rem;
  transition: transform 0.3s ease;
}

.back-button:hover i {
  transform: translateX(-3px);
}

.map-section {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.map-section h2 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.map-section h2 i {
  color: #3498db;
}

.map-container {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
}
</style>
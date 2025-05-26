<template>
  <div class="image-carousel">
    <!-- 이미지가 없는 경우 -->
    <div v-if="!images || images.length === 0" class="no-images">
      <div class="placeholder-image">
        <i class="fas fa-image"></i>
        <p>이미지가 없습니다</p>
      </div>
    </div>

    <!-- 이미지가 있는 경우 -->
    <div v-else class="carousel-wrapper">
      <!-- 메인 캐러셀 -->
      <div class="carousel-container" @click="openLightbox">
        <div class="carousel-track" :style="carouselStyle">
          <div v-for="(image, index) in images" :key="index" class="carousel-slide">
            <img :src="image" :alt="`Image ${index + 1}`" @error="handleImageError">
            <div class="image-overlay">
              <i class="fas fa-search-plus"></i>
              <span>클릭하여 확대</span>
            </div>
          </div>
        </div>

        <!-- 화살표 컨트롤 (이미지가 2개 이상일 때만 표시) -->
        <div v-if="images.length > 1" class="carousel-controls">
          <button @click.stop="prevSlide" class="control-btn prev" :disabled="currentIndex === 0 && !isLoop">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button @click.stop="nextSlide" class="control-btn next" :disabled="currentIndex === images.length - 1 && !isLoop">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>

        <!-- 인디케이터 (이미지가 2개 이상일 때만 표시) -->
        <div v-if="images.length > 1" class="carousel-indicators">
          <button 
            v-for="(_, index) in images" 
            :key="index"
            :class="['indicator', { active: currentIndex === index }]"
            @click.stop="goToSlide(index)"
            :aria-label="`이미지 ${index + 1}로 이동`"
          >
            <span class="sr-only">{{ index + 1 }}</span>
          </button>
        </div>

        <!-- 이미지 카운터 -->
        <div v-if="images.length > 1" class="image-counter">
          {{ currentIndex + 1 }} / {{ images.length }}
        </div>
      </div>

      <!-- 썸네일 (showThumbnails가 true이고 이미지가 2개 이상일 때) -->
      <div v-if="showThumbnails && images.length > 1" class="thumbnail-container">
        <div class="thumbnail-track" ref="thumbnailTrack">
          <button
            v-for="(image, index) in images"
            :key="index"
            :class="['thumbnail', { active: currentIndex === index }]"
            @click="goToSlide(index)"
          >
            <img :src="image" :alt="`썸네일 ${index + 1}`">
          </button>
        </div>
      </div>
    </div>

    <!-- 라이트박스 -->
    <Teleport to="body">
      <div v-if="isLightboxOpen" class="lightbox" @click="closeLightbox">
        <div class="lightbox-header">
          <div class="lightbox-counter">
            {{ currentIndex + 1 }} / {{ images.length }}
          </div>
          <button @click="closeLightbox" class="lightbox-close">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="m18 6-12 12M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
        
        <div class="lightbox-content" @click.stop>
          <img :src="images[currentIndex]" :alt="`Image ${currentIndex + 1}`">
        </div>

        <div v-if="images.length > 1" class="lightbox-controls">
          <button @click.stop="prevSlide" class="lightbox-btn prev">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
              <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button @click.stop="nextSlide" class="lightbox-btn next">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
              <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';

const props = defineProps({
  images: {
    type: Array,
    required: true,
    default: () => []
  },
  showThumbnails: {
    type: Boolean,
    default: false
  },
  autoPlay: {
    type: Boolean,
    default: true
  },
  autoPlayInterval: {
    type: Number,
    default: 5000
  },
  isLoop: {
    type: Boolean,
    default: true
  }
});

const currentIndex = ref(0);
const isLightboxOpen = ref(false);
const autoPlayTimer = ref(null);
const isAutoPlaying = ref(props.autoPlay);
const thumbnailTrack = ref(null);

const carouselStyle = computed(() => ({
  transform: `translateX(-${currentIndex.value * 100}%)`,
  transition: 'transform 0.5s cubic-bezier(0.4, 0, 0.2, 1)'
}));

const nextSlide = () => {
  if (!props.images || props.images.length <= 1) return;
  
  if (props.isLoop) {
    currentIndex.value = (currentIndex.value + 1) % props.images.length;
  } else if (currentIndex.value < props.images.length - 1) {
    currentIndex.value++;
  }
};

const prevSlide = () => {
  if (!props.images || props.images.length <= 1) return;
  
  if (props.isLoop) {
    currentIndex.value = currentIndex.value === 0 
      ? props.images.length - 1 
      : currentIndex.value - 1;
  } else if (currentIndex.value > 0) {
    currentIndex.value--;
  }
};

const goToSlide = (index) => {
  if (index >= 0 && index < props.images.length) {
    currentIndex.value = index;
  }
};

const openLightbox = () => {
  isLightboxOpen.value = true;
  stopAutoPlay();
  document.body.style.overflow = 'hidden';
};

const closeLightbox = () => {
  isLightboxOpen.value = false;
  document.body.style.overflow = '';
  if (isAutoPlaying.value) {
    startAutoPlay();
  }
};

const startAutoPlay = () => {
  if (!props.autoPlay || !props.images || props.images.length <= 1) return;
  
  stopAutoPlay();
  autoPlayTimer.value = setInterval(() => {
    nextSlide();
  }, props.autoPlayInterval);
};

const stopAutoPlay = () => {
  if (autoPlayTimer.value) {
    clearInterval(autoPlayTimer.value);
    autoPlayTimer.value = null;
  }
};

const handleImageError = (event) => {
  event.target.src = '/api/placeholder/800/600';
};

// 썸네일 스크롤
const scrollThumbnails = () => {
  if (!thumbnailTrack.value) return;
  
  nextTick(() => {
    const activeThumb = thumbnailTrack.value.children[currentIndex.value];
    if (activeThumb) {
      activeThumb.scrollIntoView({
        behavior: 'smooth',
        block: 'nearest',
        inline: 'center'
      });
    }
  });
};

// 키보드 이벤트
const handleKeydown = (event) => {
  if (!isLightboxOpen.value) return;
  
  switch (event.key) {
    case 'ArrowLeft':
      prevSlide();
      break;
    case 'ArrowRight':
      nextSlide();
      break;
    case 'Escape':
      closeLightbox();
      break;
  }
};

// 워치
watch(currentIndex, () => {
  if (props.showThumbnails) {
    scrollThumbnails();
  }
});

watch(() => props.images, () => {
  currentIndex.value = 0;
}, { deep: true });

onMounted(() => {
  if (isAutoPlaying.value) {
    startAutoPlay();
  }
  document.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  stopAutoPlay();
  document.removeEventListener('keydown', handleKeydown);
  document.body.style.overflow = '';
});
</script>

<style scoped>
.image-carousel {
  width: 100%;
  position: relative;
}

/* 이미지가 없는 경우 */
.no-images {
  width: 100%;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 16px;
  border: 2px dashed #dee2e6;
}

.placeholder-image {
  text-align: center;
  color: #6c757d;
}

.placeholder-image i {
  font-size: 3rem;
  margin-bottom: 1rem;
}

/* 캐러셀 래퍼 */
.carousel-wrapper {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* 캐러셀 컨테이너 */
.carousel-container {
  width: 100%;
  height: 500px;
  overflow: hidden;
  position: relative;
  border-radius: 20px;
  cursor: pointer;
  background: #f8f9fa;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.carousel-track {
  display: flex;
  height: 100%;
  will-change: transform;
}

.carousel-slide {
  min-width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.carousel-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.carousel-slide:hover .image-overlay {
  opacity: 1;
}

.image-overlay i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

/* 컨트롤 버튼 */
.carousel-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  padding: 0 1rem;
  pointer-events: none;
  z-index: 10;
}

.control-btn {
  pointer-events: all;
  width: 56px;
  height: 56px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.95);
  color: #2d3748;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(8px);
}

.control-btn:hover:not(:disabled) {
  background: white;
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.2);
}

.control-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.control-btn svg {
  transition: transform 0.2s ease;
}

.control-btn:hover:not(:disabled) svg {
  transform: scale(1.1);
}

/* 인디케이터 */
.carousel-indicators {
  position: absolute;
  bottom: 1.5rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 0.75rem;
  z-index: 10;
}

.indicator {
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: scale(1.2);
}

.indicator.active {
  background: white;
  transform: scale(1.3);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

/* 이미지 카운터 */
.image-counter {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  backdrop-filter: blur(8px);
}

/* 썸네일 */
.thumbnail-container {
  width: 100%;
  overflow-x: auto;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e0 transparent;
}

.thumbnail-container::-webkit-scrollbar {
  height: 6px;
}

.thumbnail-container::-webkit-scrollbar-track {
  background: transparent;
}

.thumbnail-container::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.thumbnail-track {
  display: flex;
  gap: 0.75rem;
  padding: 0.5rem 0;
}

.thumbnail {
  flex-shrink: 0;
  width: 80px;
  height: 60px;
  border: 2px solid transparent;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  background: none;
  padding: 0;
}

.thumbnail:hover {
  border-color: #667eea;
  transform: translateY(-2px);
}

.thumbnail.active {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 라이트박스 */
.lightbox {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.95);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(8px);
}

.lightbox-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.8), transparent);
}

.lightbox-counter {
  color: white;
  font-size: 1.1rem;
  font-weight: 500;
}

.lightbox-close {
  width: 48px;
  height: 48px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.lightbox-close:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.lightbox-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6rem 2rem 2rem;
}

.lightbox-content img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.lightbox-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  padding: 0 2rem;
  pointer-events: none;
  z-index: 10;
}

.lightbox-btn {
  pointer-events: all;
  width: 64px;
  height: 64px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
}

.lightbox-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

/* 접근성 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

/* 반응형 */
@media (max-width: 768px) {
  .carousel-container {
    height: 300px;
    border-radius: 16px;
  }
  
  .control-btn {
    width: 44px;
    height: 44px;
  }
  
  .carousel-controls {
    padding: 0 0.5rem;
  }
  
  .image-counter {
    top: 0.5rem;
    right: 0.5rem;
    font-size: 0.8rem;
    padding: 0.25rem 0.75rem;
  }
  
  .thumbnail {
    width: 60px;
    height: 45px;
  }
  
  .lightbox-header {
    padding: 1rem;
  }
  
  .lightbox-content {
    padding: 4rem 1rem 1rem;
  }
  
  .lightbox-controls {
    padding: 0 1rem;
  }
  
  .lightbox-btn {
    width: 48px;
    height: 48px;
  }
}

@media (max-width: 480px) {
  .carousel-container {
    height: 250px;
  }
  
  .control-btn {
    width: 36px;
    height: 36px;
  }
  
  .indicator {
    width: 8px;
    height: 8px;
  }
}
</style>
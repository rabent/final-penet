<!-- views/Home.vue -->
<template>
  <div class="home">
    <section class="hero">
      <div class="hero-content">
        <h1>여행의 즐거움을 발견하세요</h1>
        <p>HamGaja과 함께 새로운 여행지를 탐색하고, 여행 계획을 세우고, 경험을 공유하세요.</p>
        <div class="search-container">
          <input v-model="searchTerm" type="text" placeholder="어디로 떠나고 싶으신가요?" class="search-input" @keyup.enter="handleSearch"/>
          <button class="search-button" @click="handleSearch">검색</button>
        </div>
      </div>
    </section>

    <section class="featured-destinations">
      <h2>인기 여행지</h2>
      <div class="destination-grid">
        <div class="destination-card" v-for="(destination, index) in randomDestinations" :key="index">
          <div class="destination-image" :style="{ backgroundImage: `url(${imageUrls[destination.name]})` }"></div>
          <div class="destination-info">
            <h3>{{ destination.name }}</h3>
            <p>{{ destination.description }}</p>
            <router-link :to="{ path: `/attractions` , query: {location : destination.name}}" class="view-more">
              더 알아보기
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <section class="recent-posts">
      <h2>최근 게시글</h2>
      <div class="post-list">
        <div class="post-card" v-for="(post, index) in recentPosts" :key="index">
          <h3>{{ post.title }}</h3>
          <p class="post-meta">{{ post.user }} · {{ post.createdAt }}</p>
          <p class="post-excerpt">{{ post.excerpt }}</p>
          <router-link :to="`/board/post/${post.id}`" class="read-more">
            자세히 보기
          </router-link>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="cta-content">
        <h2>나만의 여행 계획을 만들어보세요</h2>
        <p>Hamgaja의 여행 계획 도구로 손쉽게 여행 일정을 만들고 관리하세요.</p>
        <router-link to="/trip-plan" class="cta-button">
          여행 계획 시작하기
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/axios'

const loading = ref(true)
const imageUrls = ref({})
const errorMessage = ref('')
const searchTerm=ref('')
const router = useRouter()

const randomDestinations = computed(() => {
  const shuffled = [...featuredDestinations.value].sort(() => 0.5 - Math.random());
  return shuffled.slice(0, 3);
});

const handleSearch = () => {
    const keyword = String(searchTerm.value || '').trim();
            if (keyword) {
                router.push({ path: `/attractions` , query: {keyword : keyword}});
            }
        }

const featuredDestinations = ref([
 {
   id: 'jeju',
   name: '제주도',
   description: '아름다운 해변과 자연 경관을 자랑하는 한국의 대표적인 휴양지',
 },
 {
   id: 'busan',
   name: '부산',
   description: '해운대 해변과 활기찬 시장이 있는 한국 제2의 도시',
 },
 {
   id: 'seoul',
   name: '서울',
   description: '전통과 현대가 공존하는 대한민국의 수도',
 },
 {
   id: 'chungbuk',
   name: '충청북도',
   description: '단양팔경과 속리산으로 유명한 내륙 산간 지역',
 },
 {
   id: 'chungnam',
   name: '충청남도',
   description: '백제의 역사가 깃든 공주와 부여, 아름다운 서해안을 품은 지역',
 },
 {
   id: 'daegu',
   name: '대구',
   description: '약령시와 팔공산으로 유명한 영남 지역의 중심 도시',
 },
 {
   id: 'daejeon',
   name: '대전',
   description: '과학과 교육의 중심지이자 온천으로 유명한 충청권 거점 도시',
 },
 {
   id: 'gangwon',
   name: '강원특별자치도',
   description: '설악산과 동해안의 아름다운 자연경관을 자랑하는 산과 바다의 고장',
 },
 {
   id: 'gwangju',
   name: '광주',
   description: '예향의 도시로 불리며 풍부한 문화 예술과 맛있는 음식으로 유명한 호남 지역의 중심',
 },
 {
   id: 'gyeonggi',
   name: '경기도',
   description: '수원 화성과 한국민속촌 등 다양한 관광지가 있는 서울 근교의 거대한 도시권',
 },
 {
   id: 'gyeongbuk',
   name: '경상북도',
   description: '안동 하회마을과 경주 불국사 등 유네스코 세계문화유산이 풍부한 역사의 보고',
 },
 {
   id: 'gyeongnam',
   name: '경상남도',
   description: '통영의 아름다운 바다와 하동의 차밭이 어우러진 남해안의 관광 명소',
 },
 {
   id: 'incheon',
   name: '인천',
   description: '인천국제공항과 차이나타운, 월미도가 있는 서해안의 관문 도시',
 },
 {
   id: 'jeonbuk',
   name: '전북특별자치도',
   description: '전주 한옥마을과 맛있는 비빔밥으로 유명한 한국 전통문화의 중심지',
 },
 {
   id: 'jeonnam',
   name: '전라남도',
   description: '순천만 습지와 여수 밤바다로 유명한 남해안의 자연 생태 보고',
 },
 {
   id: 'sejong',
   name: '세종특별자치시',
   description: '대한민국의 행정수도로 계획된 신도시이자 정부청사가 위치한 현대적 도시',
 },
 {
   id: 'ulsan',
   name: '울산',
   description: '산업의 중심지이면서 태화강 십리대숲과 간절곶 해돋이로 유명한 동남권 도시',
 }
])

const recentPosts = ref([])

const fetchPosts = async () => {
  try {
      const response = await api.get('/boards/main')
      recentPosts.value = response.data.content;
    } catch (error) {
      console.error('시도 목록 로딩 실패:', error)
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
  fetchImage()
  fetchPosts()
})

</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Hero 섹션 */
.hero {
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('https://via.placeholder.com/1200x600?text=Travel+Background');
  background-size: cover;
  background-position: center;
  color: white;
  text-align: center;
  padding: 100px 20px;
  margin: 20px 0;
  border-radius: 10px;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero h1 {
  font-size: 36px;
  margin-bottom: 20px;
}

.hero p {
  font-size: 18px;
  margin-bottom: 30px;
}

.search-container {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  flex: 1;
  padding: 15px;
  font-size: 16px;
  border: none;
  border-radius: 4px 0 0 4px;
}

.search-button {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 15px 30px;
  font-size: 16px;
  cursor: pointer;
  border-radius: 0 4px 4px 0;
  transition: background-color 0.3s;
}

.search-button:hover {
  background-color: #2980b9;
}

/* 인기 여행지 섹션 */
.featured-destinations {
  margin: 60px 0;
}

h2 {
  font-size: 28px;
  margin-bottom: 30px;
  text-align: center;
  color: #333;
}

.destination-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
}

.destination-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.destination-card:hover {
  transform: translateY(-5px);
}

.destination-image {
  height: 200px;
  background-size: cover;
  background-position: center;
}

.destination-info {
  padding: 20px;
}

.destination-info h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #333;
}

.destination-info p {
  color: #666;
  margin-bottom: 15px;
}

.view-more {
  display: inline-block;
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
}

.view-more:hover {
  text-decoration: underline;
}

/* 최근 게시글 섹션 */
.recent-posts {
  margin: 60px 0;
}

.post-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
}

.post-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
}

.post-card h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #333;
}

.post-meta {
  color: #888;
  font-size: 14px;
  margin-bottom: 12px;
}

.post-excerpt {
  color: #666;
  margin-bottom: 15px;
}

.read-more {
  display: inline-block;
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
}

.read-more:hover {
  text-decoration: underline;
}

/* CTA 섹션 */
.cta-section {
  background-color: #3498db;
  color: white;
  border-radius: 10px;
  padding: 60px 20px;
  margin: 60px 0;
  text-align: center;
}

.cta-content {
  max-width: 800px;
  margin: 0 auto;
}

.cta-content h2 {
  color: white;
  margin-bottom: 15px;
}

.cta-content p {
  margin-bottom: 30px;
  font-size: 18px;
}

.cta-button {
  display: inline-block;
  background-color: white;
  color: #3498db;
  padding: 12px 30px;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  font-size: 16px;
  transition: background-color 0.3s, color 0.3s;
}

.cta-button:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 28px;
  }

  .hero p {
    font-size: 16px;
  }

  .destination-grid, .post-list {
    grid-template-columns: 1fr;
  }
}
</style>

<!-- views/Home.vue -->
<template>
  <div class="home">
    <section class="hero">
      <div class="hero-content">
        <h1>여행의 즐거움을 발견하세요</h1>
        <p>EnjoyTrip과 함께 새로운 여행지를 탐색하고, 여행 계획을 세우고, 경험을 공유하세요.</p>
        <div class="search-container">
          <input type="text" placeholder="어디로 떠나고 싶으신가요?" class="search-input" />
          <button class="search-button">검색</button>
        </div>
      </div>
    </section>

    <section class="featured-destinations">
      <h2>인기 여행지</h2>
      <div class="destination-grid">
        <div class="destination-card" v-for="(destination, index) in featuredDestinations" :key="index">
          <div class="destination-image" :style="{ backgroundImage: `url(${destination.image})` }"></div>
          <div class="destination-info">
            <h3>{{ destination.name }}</h3>
            <p>{{ destination.description }}</p>
            <router-link :to="`/attractions?location=${destination.id}`" class="view-more">
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
          <p class="post-meta">{{ post.author }} · {{ post.date }}</p>
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
        <p>EnjoyTrip의 여행 계획 도구로 손쉽게 여행 일정을 만들고 관리하세요.</p>
        <router-link to="/trip-plan" class="cta-button">
          여행 계획 시작하기
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const featuredDestinations = ref([
  {
    id: 'jeju',
    name: '제주도',
    description: '아름다운 해변과 자연 경관을 자랑하는 한국의 대표적인 휴양지',
    image: 'https://via.placeholder.com/400x300?text=제주도'
  },
  {
    id: 'busan',
    name: '부산',
    description: '해운대 해변과 활기찬 시장이 있는 한국 제2의 도시',
    image: 'https://via.placeholder.com/400x300?text=부산'
  },
  {
    id: 'gyeongju',
    name: '경주',
    description: '신라 시대의 역사적 유적이 풍부한 역사 문화 도시',
    image: 'https://via.placeholder.com/400x300?text=경주'
  },
  {
    id: 'seoul',
    name: '서울',
    description: '전통과 현대가 공존하는 대한민국의 수도',
    image: 'https://via.placeholder.com/400x300?text=서울'
  }
])

const recentPosts = ref([
  {
    id: 1,
    title: '제주도 3박 4일 여행 후기',
    author: '여행자123',
    date: '2025-05-05',
    excerpt: '제주도의 숨겨진 명소들과 현지인 맛집을 소개합니다.'
  },
  {
    id: 2,
    title: '혼자 떠나는 부산 여행 가이드',
    author: '솔로트래블러',
    date: '2025-05-03',
    excerpt: '혼자서도 충분히 즐길 수 있는 부산 여행 코스를 정리했습니다.'
  },
  {
    id: 3,
    title: '가족과 함께하는 경주 여행 계획',
    author: '행복한가족',
    date: '2025-05-01',
    excerpt: '아이들과 함께 역사 탐방을 할 수 있는 경주 여행 계획을 공유합니다.'
  }
])
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
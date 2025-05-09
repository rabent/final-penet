<!-- App.vue (Vue 3 버전) -->
<template>
  <div id="app">
    <Header />
    <main class="main-content">
      <!-- 메인 콘텐츠가 들어갈 위치 -->
      <router-view v-slot="{ Component }">
        <!-- Vue 3에서 추가된 transition 기능 (선택 사항) -->
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script>
// Vue 3에서는 Composition API 사용 가능 (Options API도 여전히 지원)
// 여기서는 Options API를 사용하여 구현
import Header from './components/Header.vue'

export default {
  name: 'App',
  components: {
    Header
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Noto Sans KR', sans-serif;
  color: #333;
  line-height: 1.6;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  margin-top: 70px; /* Header 높이만큼 여백 추가 */
  flex: 1;
  padding: 20px;
}

/* 페이지 전환 애니메이션 (선택 사항) */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
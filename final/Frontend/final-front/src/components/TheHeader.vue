<!-- TheHeader.vue (Vue 3 버전) -->
<template>
  <header class="header">
    <div class="header-container">
      <div class="header-left">
        <h1 class="site-title"><router-link to="/">EnjoyTrip</router-link></h1>
        <nav class="main-nav">
          <ul>
            <li><router-link to="/attractions">관광지 검색</router-link></li>
            <li><router-link to="/board">게시판</router-link></li>
            <li><router-link to="/trip-plan">여행 계획</router-link></li>
          </ul>
        </nav>
      </div>
      <div class="header-right">
        <router-link to="/login" class="login-btn">로그인</router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
defineOptions({
  name: 'TheHeader'
})
</script>

<style scoped>
.header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
}

.header-left {
  display: flex;
  align-items: center;
}

.site-title {
  margin: 0;
  font-size: 24px;
  margin-right: 30px;
}

.site-title a {
  color: #3498db;
  text-decoration: none;
  font-weight: bold;
}

.main-nav ul {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
}

.main-nav li {
  margin-right: 20px;
}

.main-nav a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
}

.main-nav a:hover {
  color: #3498db;
}

.main-nav a:after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  background: #3498db;
  bottom: 0;
  left: 0;
  transition: width 0.3s;
}

.main-nav a:hover:after {
  width: 100%;
}

.login-btn {
  background-color: #3498db;
  color: white;
  padding: 8px 20px;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #2980b9;
}
</style>
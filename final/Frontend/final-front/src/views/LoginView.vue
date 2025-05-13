<template>
  <div class="login-container">
    <div class="login-box">
      <h2>로그인</h2>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="userId">아이디</label>
          <input
            type="text"
            id="userId"
            v-model="loginForm.userId"
            required
            placeholder="아이디를 입력하세요"
          />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            required
            placeholder="비밀번호를 입력하세요"
          />
        </div>
        <div class="form-options">
          <label class="remember-me">
            <input type="checkbox" v-model="loginForm.rememberMe" />
            <span>아이디 저장</span>
          </label>
          <div class="find-links">
            <router-link to="/find-id" class="find-link">아이디 찾기</router-link>
            <span class="divider">|</span>
            <router-link to="/find-password" class="find-link">비밀번호 찾기</router-link>
          </div>
        </div>
        <button type="submit" class="login-button">로그인</button>
      </form>
      <div class="register-link">
        계정이 없으신가요? <router-link to="/register">회원가입</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loginForm = reactive({
  userId: '',
  password: '',
  rememberMe: false
})

const handleLogin = async () => {
  try {
    // TODO: API 호출 구현
    console.log('로그인 시도:', loginForm)
    // 로그인 성공 시 홈으로 이동
    router.push('/')
  } catch (error) {
    console.error('로그인 실패:', error)
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 70px);
  background-color: #f5f6f8;
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-box h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.form-group input:focus {
  border-color: #3498db;
  outline: none;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.find-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.find-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.find-link:hover {
  color: #3498db;
}

.divider {
  color: #ddd;
  font-size: 14px;
}

.login-button {
  width: 100%;
  padding: 12px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #2980b9;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-link a {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
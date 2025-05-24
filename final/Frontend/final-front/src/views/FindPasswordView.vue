<template>
  <div class="find-container">
    <div class="find-box">
      <h2>비밀번호 찾기</h2>
      <form @submit.prevent="handleFindPassword" class="find-form">
        <div class="form-group">
          <label for="email">이메일</label>
          <input
            type="email"
            id="email"
            v-model="findForm.email"
            required
            placeholder="이메일을 입력하세요"
            @input="resetPasswordReset"
          />
        </div>
        <div class="form-group">
          <label for="name">이름</label>
          <input
            type="text"
            id="name"
            v-model="findForm.name"
            required
            placeholder="이름을 입력하세요"
            @input="resetPasswordReset"
          />
        </div>
        <button type="submit" class="find-button" :disabled="!isFormValid">
          임시 비밀번호 발급
        </button>
      </form>
      
      <div v-if="tempPassword" class="result-message">
        <p>임시 비밀번호가 발급되었습니다:</p>
        <div class="temp-password">
          {{ tempPassword }}
        </div>
        <p class="warning">
          * 보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.
        </p>
      </div>

      <div class="login-link">
        <router-link to="/login">로그인 페이지로 돌아가기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import api from '@/utils/axios'

const findForm = reactive({
  email: '',
  name: ''
})

const tempPassword = ref('')

const resetPasswordReset = () => {
  tempPassword.value = ''
}

const isValidEmail = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(findForm.email)
})

const isFormValid = computed(() => {
  return findForm.email && 
         isValidEmail.value && 
         findForm.name
})

const handleFindPassword = async () => {
  try {
    const response = await api.post('/auth/reset-password', findForm)
    if (response.data.tempPassword) {
      tempPassword.value = response.data.tempPassword
    }
  } catch (error) {
    console.error('비밀번호 찾기 실패:', error)
    alert('일치하는 정보를 찾을 수 없습니다.')
  }
}
</script>

<style scoped>
.find-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 70px);
  background-color: #f5f6f8;
  padding: 20px;
}

.find-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.find-box h2 {
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

.find-button {
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

.find-button:hover {
  background-color: #2980b9;
}

.find-button:disabled {
  background-color: #95a5a6;
  cursor: not-allowed;
}

.temp-password {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  margin: 15px 0;
  letter-spacing: 2px;
}

.warning {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 10px;
}

.result-message {
  margin-top: 20px;
  padding: 20px;
  background-color: #e8f4fd;
  border-radius: 4px;
  color: #2c3e50;
  text-align: center;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link a {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
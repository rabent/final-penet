<template>
  <div class="find-container">
    <div class="find-box">
      <h2>아이디 찾기</h2>
      <form @submit.prevent="handleFindId" class="find-form">
        <div class="form-group">
          <label for="name">이름</label>
          <input
            type="text"
            id="name"
            v-model="findForm.name"
            required
            placeholder="이름을 입력하세요"
            @input="resetFoundEmail"
          />
        </div>
        <div class="form-group">
          <label for="number">전화번호</label>
          <input
            type="tel"
            id="number"
            v-model="findForm.number"
            required
            placeholder="전화번호를 입력하세요 (-없이)"
            maxlength="11"
            @input="handlePhoneNumberInput"
          />
          <span class="input-guide" v-if="!isValidPhoneNumber">
            올바른 전화번호 형식이 아닙니다. (예: 01012345678)
          </span>
        </div>
        <button type="submit" class="find-button" :disabled="!isFormValid">아이디 찾기</button>
      </form>
      
      <div v-if="foundEmail" class="result-message">
        찾은 이메일: <span class="email">{{ foundEmail }}</span>
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
  name: '',
  number: ''
})

const foundEmail = ref('')

const getFormattedPhoneNumber = () => {
  const num = findForm.number
  if (num.length === 11) {
    return `${num.slice(0, 3)}-${num.slice(3, 7)}-${num.slice(7)}`
  }
  return num
}

const handleFindId = async () => {
  try {
    const formData = {
      name: findForm.name,
      number: getFormattedPhoneNumber() // 포맷된 전화번호 사용
    }
    const response = await api.post('/auth/find-id', formData)
    if (response.data.email) {
      foundEmail.value = response.data.email
    }
  } catch (error) {
    console.error('아이디 찾기 실패:', error)
    alert('일치하는 정보를 찾을 수 없습니다.')
  }
}

// 결과 초기화 함수 추가
const resetFoundEmail = () => {
  foundEmail.value = ''
}

// 전화번호 입력 핸들러 수정
const handlePhoneNumberInput = (event) => {
  validatePhoneNumber(event)
  resetFoundEmail()
}

// 전화번호 유효성 검사
const validatePhoneNumber = (event) => {
  findForm.number = event.target.value.replace(/[^0-9]/g, '').slice(0, 11)
}

const isValidPhoneNumber = computed(() => {
  const phoneRegex = /^010\d{8}$/
  return !findForm.number || phoneRegex.test(findForm.number)
})

const isFormValid = computed(() => {
  return findForm.name && 
         findForm.number && 
         isValidPhoneNumber.value
})
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

.input-guide {
  display: block;
  margin-top: 5px;
  color: #e74c3c;
  font-size: 14px;
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

.result-message {
  margin-top: 20px;
  padding: 15px;
  background-color: #e8f4fd;
  border-radius: 4px;
  color: #2c3e50;
  text-align: center;
}

.result-message .email {
  color: #3498db;
  font-weight: 600;
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
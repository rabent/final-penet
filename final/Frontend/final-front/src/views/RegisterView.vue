<template>
  <div class="register-container">
    <div class="register-box">
      <h2>회원가입</h2>
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="email">이메일 (아이디)</label>
          <input
            type="email"
            id="email"
            v-model="registerForm.email"
            required
            maxlength="40"
            placeholder="이메일을 입력하세요 (40자 이하)"
          />
          <span class="input-guide" v-if="!isValidEmail">
            올바른 이메일 형식이 아닙니다.
          </span>
        </div>

        <div class="form-group">
          <label for="password">비밀번호</label>
          <input
            type="password"
            id="password"
            v-model="registerForm.password"
            required
            minlength="8"
            maxlength="15"
            placeholder="비밀번호를 입력하세요 (8-15자)"
          />
          <span class="input-guide" v-if="!isValidPassword">
            비밀번호는 8자 이상 15자 이하로 입력해주세요.
          </span>
        </div>

        <div class="form-group">
          <label for="passwordConfirm">비밀번호 확인</label>
          <input
            type="password"
            id="passwordConfirm"
            v-model="passwordConfirm"
            required
            placeholder="비밀번호를 다시 입력하세요"
          />
          <span class="password-match" :class="{ 'not-match': !isPasswordMatch && passwordConfirm }">
            {{ passwordMatchMessage }}
          </span>
        </div>

        <div class="form-group">
          <label for="name">이름</label>
          <input
            type="text"
            id="name"
            v-model="registerForm.name"
            required
            maxlength="15"
            placeholder="이름을 입력하세요 (15자 이하)"
          />
        </div>

        <div class="form-group">
          <label for="address">주소</label>
          <input
            type="text"
            id="address"
            v-model="registerForm.address"
            required
            maxlength="50"
            placeholder="주소를 입력하세요 (50자 이하)"
          />
        </div>

        <div class="form-group">
          <label for="number">전화번호</label>
          <input
            type="tel"
            id="number"
            v-model="registerForm.number"
            required
            maxlength="11"
            placeholder="01012345678"
            @input="validatePhoneNumber"
          />
          <span class="input-guide" v-if="!isValidPhoneNumber">
            올바른 전화번호 형식이 아닙니다. (예: 01012345678)
          </span>
        </div>

        <button type="submit" class="register-button" :disabled="!isFormValid">회원가입</button>
      </form>
      <div class="login-link">
        이미 계정이 있으신가요? <router-link to="/login">로그인</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'  // axios import 추가

const router = useRouter()
const passwordConfirm = ref('')

const registerForm = reactive({
  name: '',
  password: '',
  email: '',
  address: '',
  number: '',
  role: 'USER' // 기본 역할 설정
})

const isPasswordMatch = computed(() => {
  return !passwordConfirm.value || registerForm.password === passwordConfirm.value
})

const passwordMatchMessage = computed(() => {
  if (!passwordConfirm.value) return ''
  return isPasswordMatch.value ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.'
})

const validatePhoneNumber = (event) => {
  // 숫자만 입력 가능하도록
  registerForm.number = event.target.value.replace(/[^0-9]/g, '').slice(0, 11)
}

const isValidPhoneNumber = computed(() => {
  const phoneRegex = /^010\d{8}$/
  return !registerForm.number || phoneRegex.test(registerForm.number)
})

const getFormattedPhoneNumber = () => {
  // 서버 전송을 위한 포맷팅
  const num = registerForm.number
  if (num.length === 11) {
    return `${num.slice(0, 3)}-${num.slice(3, 7)}-${num.slice(7)}`
  }
  return num
}

// 이메일 유효성 검사 추가
const isValidEmail = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return !registerForm.email || emailRegex.test(registerForm.email)
})

// 비밀번호 유효성 검사 추가
const isValidPassword = computed(() => {
  return !registerForm.password ||
    (registerForm.password.length >= 8 && registerForm.password.length <= 15)
})

// isFormValid에 새로운 조건 추가
const isFormValid = computed(() => {
  return registerForm.name &&
         registerForm.name.length <= 15 &&
         registerForm.password &&
         isValidPassword.value &&
         passwordConfirm.value &&
         registerForm.password === passwordConfirm.value &&
         registerForm.email &&
         isValidEmail.value &&
         registerForm.email.length <= 40 &&
         registerForm.address &&
         registerForm.address.length <= 50 &&
         registerForm.number &&
         isValidPhoneNumber.value
})

const handleRegister = async () => {
  try {
    if (registerForm.password !== passwordConfirm.value) {
      alert('비밀번호가 일치하지 않습니다.')
      return
    }

    // 서버로 전송할 데이터 준비
    const formData = {
      ...registerForm,
      number: getFormattedPhoneNumber() // 전화번호 포맷팅
    }

    // API 호출
    const response = await axios.post('http://localhost:8080/api/users', formData)

    if (response.data) {
      alert('회원가입이 완료되었습니다.')
      console.log('회원가입 성공:', response.data) 
      router.push('/login')
    }
  } catch (error) {
    console.error('회원가입 실패:', error)
    if (error.response) {
      // 서버가 응답한 에러
      switch (error.response.status) {
        case 400:
          alert('잘못된 요청입니다. 입력값을 확인해주세요.')
          break
        case 409:
          alert('이미 사용중인 이메일입니다.')
          break
        default:
          alert('회원가입 중 오류가 발생했습니다.')
      }
    } else if (error.request) {
      // 서버에 요청이 도달하지 못한 경우
      alert('서버에 연결할 수 없습니다.')
    } else {
      alert('회원가입 처리 중 오류가 발생했습니다.')
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 70px);
  background-color: #f5f6f8;
  padding: 20px;
}

.register-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

.register-box h2 {
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
  border-color: #2ecc71;
  outline: none;
}

.register-button {
  width: 100%;
  padding: 12px;
  background-color: #2ecc71;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-button:hover {
  background-color: #27ae60;
}

.register-button:disabled {
  background-color: #95a5a6;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link a {
  color: #2ecc71;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}

.password-match {
  display: block;
  margin-top: 5px;
  font-size: 14px;
  color: #27ae60;
}

.password-match.not-match {
  color: #e74c3c;
}

.input-guide {
  display: block;
  margin-top: 5px;
  font-size: 14px;
  color: #e74c3c;
}
</style>

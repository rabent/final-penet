import axios from 'axios'
import router from '@/router'

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
})

// 요청 인터셉터
api.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 응답 인터셉터
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      // 토큰 만료 코드 확인 (서버에서 보내는 특정 코드)
      const errorData = error.response.data
      const isTokenExpired = errorData && errorData.code === 'TOKEN_EXPIRED'
      
      // 토큰이 만료되었거나 유효하지 않은 경우
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('userId')
      router.push('/login')
      
      if (isTokenExpired) {
        alert('로그인 세션이 만료되었습니다. 다시 로그인해주세요.')
      } else {
        alert('로그인이 필요한 서비스입니다.')
      }
    }
    return Promise.reject(error)
  }
)

export default api
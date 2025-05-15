// router/index.js (Vue 3 버전)
import { createRouter, createWebHistory } from 'vue-router'

// 홈 페이지는 즉시 로드
import Home from '../views/HomeView.vue'
import Login from '../views/LoginView.vue'
import Register from '../views/RegisterView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  }
  ,{
    path: '/login',
    name: 'Login',
    component: Login
  }
  ,{
    path: '/register',
    name: 'Register',
    component: Register
  }
  // ,{
  //   path: '/attractions',
  //   name: 'Attractions',
  //   // 지연 로딩 사용 (코드 스플리팅)
  //   component: () => import(/* webpackChunkName: "attractions" */ '../views/Attractions.vue')
  // },
  // {
  //   path: '/board',
  //   name: 'Board',
  //   component: () => import(/* webpackChunkName: "board" */ '../views/Board.vue')
  // },
  // {
  //   path: '/trip-plan',
  //   name: 'TripPlan',
  //   component: () => import(/* webpackChunkName: "trip-plan" */ '../views/TripPlan.vue')
  // }
]

// Vue 3에서는 createRouter 함수 사용
const router = createRouter({
  // Vue 3에서는 createWebHistory 사용 (Vue 2의 mode: 'history'와 동일)
  history: createWebHistory(import.meta.env.BASE_URL), // Vite에서 사용하는 환경 변수
  routes
})

export default router
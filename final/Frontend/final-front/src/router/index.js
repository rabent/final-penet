// router/index.js (Vue 3 버전)
import { createRouter, createWebHistory } from 'vue-router'

// 홈 페이지는 즉시 로드
import Home from '../views/HomeView.vue'
import Login from '../views/LoginView.vue'
import Register from '../views/RegisterView.vue'
import Board from '../views/BoardView.vue'
import BoardWrite from '../views/BoardWriteView.vue'
import BoardDetail from '../views/BoardDetailView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/board',
    name: 'Board',
    component: Board
  },
  {
    path: '/board/write',
    name: 'BoardWrite',
    component: BoardWrite,
    meta: { requiresAuth: true }
  },
  {
    path: '/board/post/:id',
    name: 'BoardDetail',
    component: BoardDetail
  },
  {
    path: '/board/edit/:id',
    name: 'BoardEdit',
    component: () => import('../views/BoardWriteView.vue'), // 수정 페이지는 작성 페이지를 재활용
    meta: { requiresAuth: true }
  }
  // 나중에 필요한 경우 추가 라우트
  // ,{
  //   path: '/attractions',
  //   name: 'Attractions',
  //   component: () => import('../views/Attractions.vue')
  // },
  // {
  //   path: '/trip-plan',
  //   name: 'TripPlan',
  //   component: () => import('../views/TripPlan.vue')
  // }
]

// Vue 3에서는 createRouter 함수 사용
const router = createRouter({
  // Vue 3에서는 createWebHistory 사용 (Vue 2의 mode: 'history'와 동일)
  history: createWebHistory(import.meta.env.BASE_URL), // Vite에서 사용하는 환경 변수
  routes
})

// 로그인이 필요한 페이지에 대한 네비게이션 가드 추가
router.beforeEach((to, from, next) => {
  // 인증이 필요한 페이지인지 확인
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 현재 로그인 상태 확인
    const isLoggedIn = !!sessionStorage.getItem('token')

    if (!isLoggedIn) {
      // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
      alert('로그인이 필요한 페이지입니다.')
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 로그인 후 원래 가려던 페이지로 리다이렉트하기 위한 쿼리
      })
    } else {
      // 로그인된 경우 원래 가려던 페이지로 진행
      next()
    }
  } else {
    // 인증이 필요하지 않은 페이지는 그냥 진행
    next()
  }
})

export default router

// router/index.js (Vue 3 버전) - 마이페이지 라우트 추가
import { createRouter, createWebHistory } from 'vue-router'

// 홈 페이지는 즉시 로드
import Home from '../views/HomeView.vue'
import Login from '../views/LoginView.vue'
import Register from '../views/RegisterView.vue'
import MyPage from '../views/MyPageView.vue'
import Board from '../views/BoardView.vue'
import BoardWrite from '../views/BoardWriteView.vue'
import BoardDetail from '../views/BoardDetailView.vue'
import BoardEditView from '../views/BoardEditView.vue'
import Attractions from '../views/AttractionView.vue'
import AttractionDetailView from '../views/AttractionDetailView.vue'
import TripPlan from '../views/TripPlanView.vue'
import TripPlanDetail from '../views/TripPlanDetailView.vue'
import TripPlanCreate from '../views/TripPlanCreateView.vue'
import TripScheduleCreate from '../views/TripScheduleCreateView.vue'
import TripPlanEdit from '../views/TripPlanEditView.vue'
import TripScheduleEdit from '../views/TripScheduleEditView.vue'
import FindIdView from '../views/FindIdView.vue'
import FindPasswordView from '../views/FindPasswordView.vue'

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
        name: 'boardEdit',
        component: BoardEditView
    },
  {
    path: '/mypage',
    name: 'MyPage',
    component: MyPage,
    meta: {
      requiresAuth: true // 인증이 필요한 페이지로 표시
    }
  }
    ,{
    path: '/attractions',
    name: 'Attractions',
    component: Attractions
  },
  {
    path: '/attractions/:id',
    name: 'AttractionDetail',
    component: AttractionDetailView,
  },
  {
    path: '/trip-plan',
    name: 'TripPlan',
    component: TripPlan,
    meta: { requiresAuth: true }
  },
  {
    path: '/trip-plan/create',
    name: 'TripPlanCreate',
    component: TripPlanCreate,
    meta: { requiresAuth: true }
  },
  {
    path: '/trip-plan/:id',
    name: 'TripPlanDetail',
    component: TripPlanDetail,
    meta: { requiresAuth: true },
    props: true
  },
  {
      path: '/trip-plan/:id/edit',
      name: 'TripPlanEdit',
      component: TripPlanEdit,
      meta: { requiresAuth: true },
      props: true
    },

    // 여행 일정 수정 페이지 라우트
    {
      path: '/trip-plan/:planId/:scheduleId/edit',
      name: 'TripScheduleEdit',
      component: TripScheduleEdit,
      meta: { requiresAuth: true }
    },
  {
    path: '/trip-plan/:planId/create',
    name: 'TripScheduleCreate',
    component: TripScheduleCreate,
    meta: { requiresAuth: true }
  },
  {
    path: '/find-id',
    name: 'FindId',
    component: FindIdView
  },
  {
    path: '/find-password',
    name: 'FindPassword',
    component: FindPasswordView
  }
]

// Vue 3에서는 createRouter 함수 사용
const router = createRouter({
  // Vue 3에서는 createWebHistory 사용 (Vue 2의 mode: 'history'와 동일)
  history: createWebHistory(import.meta.env.BASE_URL), // Vite에서 사용하는 환경 변수
  routes
})

// 네비게이션 가드 추가 - 인증이 필요한 페이지 접근 제어
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!sessionStorage.getItem('token')

  // 인증이 필요한 페이지인지 확인
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 로그인 상태가 아니면 로그인 페이지로 리다이렉트
    if (!isLoggedIn) {
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 로그인 후 원래 가려던 페이지로 리다이렉트하기 위한 쿼리 파라미터
      })
    } else {
      next() // 로그인 상태면 정상적으로 페이지 이동
    }
  } else {
    next() // 인증이 필요없는 페이지는 그냥 통과
  }
})

export default router

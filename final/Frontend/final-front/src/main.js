/// main.js (Vue 3 버전)
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Vue 3에서는 createApp() 함수 사용
createApp(App)
  .use(router)
  .mount('#app')
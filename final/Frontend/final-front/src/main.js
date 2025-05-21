/// main.js (Vue 3 버전)
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { useKakao } from 'vue3-kakao-maps/@utils';

useKakao('27490439fa9e5e360bdbce0f425e5963')
// Vue 3에서는 createApp() 함수 사용
createApp(App)
  .use(router)
  .mount('#app')
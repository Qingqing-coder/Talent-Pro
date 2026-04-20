// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import FaceLogin from '@/views/FaceLogin.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/face-login',
    name: 'FaceLogin',
    component: FaceLogin
  },
  // 其他路由...
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
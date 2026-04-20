import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const isAdminAuthenticated = ref(false) // 管理员是否已认证
  const adminPassword = '123456' // 管理员密码

  // 验证管理员密码
  const verifyAdminPassword = (password: string): boolean => {
    if (password === adminPassword) {
      isAdminAuthenticated.value = true
      localStorage.setItem('adminAuthenticated', 'true')
      return true
    }
    return false
  }

  // 检查管理员是否已认证
  const checkAdminAuth = (): boolean => {
    const saved = localStorage.getItem('adminAuthenticated')
    if (saved === 'true') {
      isAdminAuthenticated.value = true
    }
    return isAdminAuthenticated.value
  }

  // 清除管理员认证
  const clearAdminAuth = () => {
    isAdminAuthenticated.value = false
    localStorage.removeItem('adminAuthenticated')
  }

  // 初始化认证状态
  const initAuth = () => {
    checkAdminAuth()
  }

  return {
    isAdminAuthenticated,
    verifyAdminPassword,
    checkAdminAuth,
    clearAdminAuth,
    initAuth,
  }
})

import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useRoleStore = defineStore('role', () => {
  const currentRole = ref('user') // 默认角色改为用户

  // 设置角色
  const setRole = (role:any) => {
    currentRole.value = role
    // 保存到localStorage
    localStorage.setItem('userRole', role)
  }

  // 获取角色
  const getRole = () => {
    // 优先从localStorage获取，如果没有则使用默认值
    const savedRole = localStorage.getItem('userRole')
    if (savedRole) {
      currentRole.value = savedRole
    } else {
      // 如果没有保存的角色，默认设置为用户
      currentRole.value = 'user'
      localStorage.setItem('userRole', 'user')
    }
    return currentRole.value
  }

  // 初始化角色
  const initRole = () => {
    getRole()
  }

  // 检查权限
  const hasPermission = (requiredRole: string) => {
    const roleHierarchy: Record<string, number> = {
      user: 1,
      company: 2,
      admin: 3,
    }
    return roleHierarchy[currentRole.value] >= roleHierarchy[requiredRole]
  }

  return {
    currentRole,
    setRole,
    getRole,
    initRole,
    hasPermission,
  }
})

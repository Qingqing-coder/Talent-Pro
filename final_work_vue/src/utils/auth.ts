import { ref } from 'vue'

const isAuthenticated = ref(false)
const currentUser = ref('')

export function setAuthStatus(status: boolean, username: string = '') {
  isAuthenticated.value = status
  currentUser.value = username
  if (status) {
    localStorage.setItem('isAuthenticated', 'true')
    localStorage.setItem('username', username)
  } else {
    localStorage.removeItem('isAuthenticated')
    localStorage.removeItem('username')
  }
}

export function getAuthStatus() {
  const storedAuth = localStorage.getItem('isAuthenticated')
  if (storedAuth) {
    isAuthenticated.value = true
    currentUser.value = localStorage.getItem('username') || ''
  }
  return {
    isAuthenticated: isAuthenticated.value,
    username: currentUser.value
  }
}

export function clearAuth() {
  isAuthenticated.value = false
  currentUser.value = ''
  localStorage.removeItem('isAuthenticated')
  localStorage.removeItem('username')
  localStorage.removeItem('companyUsername')
}
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('admin_token') || '')
  const adminInfo = ref(JSON.parse(localStorage.getItem('admin_info') || 'null'))

  // 设置 token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('admin_token', newToken)
  }

  // 设置管理员信息
  const setAdminInfo = (info) => {
    adminInfo.value = info
    localStorage.setItem('admin_info', JSON.stringify(info))
  }

  // 清除用户信息
  const clearUserInfo = () => {
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
  }

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!token.value
  }

  return {
    token,
    adminInfo,
    setToken,
    setAdminInfo,
    clearUserInfo,
    isLoggedIn
  }
})

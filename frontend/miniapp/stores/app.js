/**
 * 应用全局状态管理
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 状态
  const city = ref('北京') // 当前城市
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(uni.getStorageSync('userInfo') || null)

  // 设置城市
  const setCity = (newCity) => {
    city.value = newCity
    uni.setStorageSync('city', newCity)
  }

  // 设置 Token
  const setToken = (newToken) => {
    token.value = newToken
    uni.setStorageSync('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    uni.setStorageSync('userInfo', info)
  }

  // 清除用户信息
  const clearUserInfo = () => {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }

  // 是否登录
  const isLoggedIn = () => {
    return !!token.value
  }

  return {
    city,
    token,
    userInfo,
    setCity,
    setToken,
    setUserInfo,
    clearUserInfo,
    isLoggedIn
  }
})

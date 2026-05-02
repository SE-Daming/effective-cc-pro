/**
 * 用户状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { wxLogin, getUserInfo, getUserStatistics } from '../api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(uni.getStorageSync('token') || '')
  const userInfo = ref(null)
  const statistics = ref(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const hasUserInfo = computed(() => !!userInfo.value)

  // 登录
  const login = async () => {
    try {
      // 获取微信登录 code
      const loginRes = await new Promise((resolve, reject) => {
        uni.login({
          provider: 'weixin',
          success: resolve,
          fail: reject
        })
      })

      if (!loginRes.code) {
        throw new Error('获取登录code失败')
      }

      // 调用后端登录接口
      const data = await wxLogin(loginRes.code)

      // 保存 token
      token.value = data.token
      uni.setStorageSync('token', data.token)

      // 保存用户信息
      if (data.userInfo) {
        userInfo.value = data.userInfo
        uni.setStorageSync('userInfo', JSON.stringify(data.userInfo))
      }

      return data
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const data = await getUserInfo()
      userInfo.value = data
      uni.setStorageSync('userInfo', JSON.stringify(data))
      return data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 获取用户统计
  const fetchStatistics = async () => {
    try {
      const data = await getUserStatistics()
      statistics.value = data
      return data
    } catch (error) {
      console.error('获取统计数据失败:', error)
      throw error
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    statistics.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }

  // 检查登录状态
  const checkLogin = () => {
    if (!token.value) {
      uni.navigateTo({ url: '/pages/login/login' })
      return false
    }
    return true
  }

  // 初始化（从本地存储恢复）
  const init = () => {
    const savedToken = uni.getStorageSync('token')
    const savedUserInfo = uni.getStorageSync('userInfo')

    if (savedToken) {
      token.value = savedToken
    }
    if (savedUserInfo) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
  }

  return {
    // 状态
    token,
    userInfo,
    statistics,
    // 计算属性
    isLoggedIn,
    hasUserInfo,
    // 方法
    login,
    logout,
    fetchUserInfo,
    fetchStatistics,
    checkLogin,
    init
  }
})

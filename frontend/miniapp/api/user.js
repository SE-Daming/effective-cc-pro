/**
 * 用户相关 API
 */
import { get, post, put } from './request'

/**
 * 微信登录
 * @param {string} code 微信登录 code
 */
export const wxLogin = (code) => {
  return post('/auth/wx-login', { code })
}

/**
 * 获取当前用户信息
 */
export const getUserInfo = () => {
  return get('/users/me')
}

/**
 * 更新用户信息
 * @param {Object} data 用户信息
 */
export const updateUserInfo = (data) => {
  return put('/users/profile', data)
}

/**
 * 绑定手机号
 * @param {Object} data 手机号和验证码
 */
export const bindPhone = (data) => {
  return post('/users/bind-phone', data)
}

/**
 * 获取搜索历史
 * @param {Object} params 查询参数
 */
export const getSearchHistory = (params) => {
  return get('/users/search-history', params)
}

/**
 * 清空搜索历史
 * @param {number} type 类型：1电影 2影院
 */
export const clearSearchHistory = (type) => {
  return post('/users/search-history/clear', { type })
}

/**
 * 获取用户统计数据
 */
export const getUserStatistics = () => {
  return get('/users/statistics')
}

export default {
  wxLogin,
  getUserInfo,
  updateUserInfo,
  bindPhone,
  getSearchHistory,
  clearSearchHistory,
  getUserStatistics
}

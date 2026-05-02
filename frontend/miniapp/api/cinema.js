/**
 * 影院相关 API
 */
import { get } from './request'

/**
 * 获取影院列表
 * @param {Object} params 查询参数
 */
export const getCinemaList = (params) => {
  return get('/cinemas', params)
}

/**
 * 获取附近影院
 * @param {Object} params 经纬度和半径
 */
export const getNearbyCinemas = (params) => {
  return get('/cinemas/nearby', params)
}

/**
 * 获取影院详情
 * @param {number} id 影院ID
 */
export const getCinemaDetail = (id) => {
  return get(`/cinemas/${id}`)
}

/**
 * 获取影院影厅列表
 * @param {number} id 影院ID
 */
export const getCinemaHalls = (id) => {
  return get(`/cinemas/${id}/halls`)
}

/**
 * 获取影院排片日期列表
 * @param {number} id 影院ID
 * @param {number} movieId 电影ID（可选）
 */
export const getCinemaScheduleDates = (id, movieId) => {
  return get(`/cinemas/${id}/schedule-dates`, { movieId })
}

/**
 * 获取影院某日排片
 * @param {number} id 影院ID
 * @param {Object} params 日期和电影ID
 */
export const getCinemaSchedules = (id, params) => {
  return get(`/cinemas/${id}/schedules`, params)
}

/**
 * 获取影院品牌列表
 */
export const getCinemaBrands = () => {
  return get('/cinema-brands')
}

export default {
  getCinemaList,
  getNearbyCinemas,
  getCinemaDetail,
  getCinemaHalls,
  getCinemaScheduleDates,
  getCinemaSchedules,
  getCinemaBrands
}

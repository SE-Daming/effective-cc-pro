/**
 * 收藏相关 API
 */
import { get, post, del } from './request'

/**
 * 添加收藏
 * @param {number} targetType 类型：1电影 2影院
 * @param {number} targetId 目标ID
 */
export const addFavorite = (targetType, targetId) => {
  return post('/favorites', { targetType, targetId })
}

/**
 * 取消收藏
 * @param {number} targetType 类型：1电影 2影院
 * @param {number} targetId 目标ID
 */
export const removeFavorite = (targetType, targetId) => {
  return del('/favorites', { targetType, targetId })
}

/**
 * 检查是否已收藏
 * @param {number} targetType 类型：1电影 2影院
 * @param {number} targetId 目标ID
 */
export const checkFavorite = (targetType, targetId) => {
  return get('/favorites/check', { targetType, targetId })
}

/**
 * 获取收藏的电影列表
 * @param {Object} params 分页参数
 */
export const getFavoriteMovies = (params) => {
  return get('/favorites/movies', params)
}

/**
 * 获取收藏的影院列表
 * @param {Object} params 分页参数
 */
export const getFavoriteCinemas = (params) => {
  return get('/favorites/cinemas', params)
}

/**
 * 获取收藏数量统计
 */
export const getFavoriteCount = () => {
  return get('/favorites/count')
}

export default {
  addFavorite,
  removeFavorite,
  checkFavorite,
  getFavoriteMovies,
  getFavoriteCinemas,
  getFavoriteCount
}

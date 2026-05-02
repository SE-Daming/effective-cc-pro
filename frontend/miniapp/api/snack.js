/**
 * 卖品相关 API
 */
import { get, post } from './request'

/**
 * 获取卖品分类列表
 */
export const getSnackCategories = () => {
  return get('/snacks/categories')
}

/**
 * 获取卖品列表
 * @param {Object} params 查询参数
 * @param {number} params.categoryId 分类ID
 * @param {number} params.page 页码
 * @param {number} params.pageSize 每页数量
 */
export const getSnackList = (params) => {
  return get('/snacks', params)
}

/**
 * 获取卖品详情
 * @param {number} id 卖品ID
 */
export const getSnackDetail = (id) => {
  return get(`/snacks/${id}`)
}

/**
 * 创建卖品订单
 * @param {Object} data 订单数据
 * @param {Array} data.items 卖品列表 [{snackId, quantity}]
 * @param {number} data.cinemaId 影院ID（可选）
 */
export const createSnackOrder = (data) => {
  return post('/snack-orders', data)
}

/**
 * 获取卖品订单详情
 * @param {number} id 订单ID
 */
export const getSnackOrderDetail = (id) => {
  return get(`/snack-orders/${id}`)
}

/**
 * 获取卖品订单列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.pageSize 每页数量
 * @param {number} params.status 订单状态（可选）
 */
export const getSnackOrderList = (params) => {
  return get('/snack-orders', params)
}

export default {
  getSnackCategories,
  getSnackList,
  getSnackDetail,
  createSnackOrder,
  getSnackOrderDetail,
  getSnackOrderList
}

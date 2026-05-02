/**
 * 订单相关 API
 */
import { get, post } from './request'

/**
 * 创建订单（电影票）
 * @param {Object} data 订单数据
 */
export const createOrder = (data) => {
  return post('/orders', data)
}

/**
 * 创建组合订单（电影票+卖品）
 * @param {Object} data 订单数据
 */
export const createComboOrder = (data) => {
  return post('/orders/combo', data)
}

/**
 * 获取订单列表
 * @param {Object} params 查询参数
 */
export const getOrderList = (params) => {
  return get('/orders', params)
}

/**
 * 获取订单详情
 * @param {number} id 订单ID
 */
export const getOrderDetail = (id) => {
  return get(`/orders/${id}`)
}

/**
 * 获取支付信息
 * @param {number} id 订单ID
 */
export const getPayInfo = (id) => {
  return get(`/orders/${id}/pay-info`)
}

/**
 * 取消订单
 * @param {number} id 订单ID
 */
export const cancelOrder = (id) => {
  return post(`/orders/${id}/cancel`)
}

/**
 * 获取取票码
 * @param {number} id 订单ID
 */
export const getPickCode = (id) => {
  return get(`/orders/${id}/pick-code`)
}

/**
 * 检查退票条件
 * @param {number} id 订单ID
 */
export const checkRefund = (id) => {
  return get(`/orders/${id}/refund-check`)
}

/**
 * 申请退票
 * @param {number} id 订单ID
 * @param {string} reason 退票原因
 */
export const applyRefund = (id, reason) => {
  return post(`/orders/${id}/refund`, { reason })
}

/**
 * 获取卖品取货码
 * @param {number} id 订单ID
 */
export const getSnackPickCode = (id) => {
  return get(`/orders/${id}/snack-pick-code`)
}

export default {
  createOrder,
  createComboOrder,
  getOrderList,
  getOrderDetail,
  getPayInfo,
  cancelOrder,
  getPickCode,
  checkRefund,
  applyRefund,
  getSnackPickCode
}

/**
 * 排片相关 API
 */
import { get, post, del } from './request'

/**
 * 获取排片详情
 * @param {number} id 排片ID
 */
export const getScheduleDetail = (id) => {
  return get(`/schedules/${id}`)
}

/**
 * 获取场次座位图
 * @param {number} id 排片ID
 */
export const getScheduleSeats = (id) => {
  return get(`/schedules/${id}/seats`)
}

/**
 * 锁定座位
 * @param {number} id 排片ID
 * @param {Array} seatIds 座位ID列表
 */
export const lockSeats = (id, seatIds) => {
  return post(`/schedules/${id}/lock-seats`, { seatIds })
}

/**
 * 释放座位锁
 * @param {string} lockId 锁定ID
 */
export const releaseSeatLock = (lockId) => {
  return del(`/seat-locks/${lockId}`)
}

export default {
  getScheduleDetail,
  getScheduleSeats,
  lockSeats,
  releaseSeatLock
}

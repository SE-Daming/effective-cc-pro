/**
 * 格式化工具函数
 */

/**
 * 格式化日期
 * @param {string|Date} date 日期
 * @param {string} format 格式
 */
export const formatDate = (date, format = 'YYYY-MM-DD') => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化时间（仅显示时分）
 * @param {string} time 时间字符串
 */
export const formatTime = (time) => {
  if (!time) return ''
  // 如果是完整时间字符串，提取时分
  if (time.includes(':')) {
    const parts = time.split(':')
    return `${parts[0]}:${parts[1]}`
  }
  return time
}

/**
 * 格式化价格
 * @param {number} price 价格
 */
export const formatPrice = (price) => {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

/**
 * 格式化距离
 * @param {number} distance 距离（km）
 */
export const formatDistance = (distance) => {
  if (!distance) return ''
  if (distance < 1) {
    return `${Math.round(distance * 1000)}m`
  }
  return `${distance.toFixed(1)}km`
}

/**
 * 获取星期几
 * @param {string|Date} date 日期
 */
export const getWeekday = (date) => {
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const d = new Date(date)
  return weekdays[d.getDay()]
}

/**
 * 判断是否是今天
 * @param {string|Date} date 日期
 */
export const isToday = (date) => {
  const d = new Date(date)
  const today = new Date()
  return d.toDateString() === today.toDateString()
}

/**
 * 判断是否是明天
 * @param {string|Date} date 日期
 */
export const isTomorrow = (date) => {
  const d = new Date(date)
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  return d.toDateString() === tomorrow.toDateString()
}

/**
 * 格式化日期显示（今天/明天/周几）
 * @param {string|Date} date 日期
 */
export const formatDateDisplay = (date) => {
  if (isToday(date)) return '今天'
  if (isTomorrow(date)) return '明天'
  return getWeekday(date)
}

/**
 * 手机号脱敏
 * @param {string} phone 手机号
 */
export const maskPhone = (phone) => {
  if (!phone || phone.length !== 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 订单状态文本
 */
export const orderStatusText = (status) => {
  const statusMap = {
    1: '待支付',
    2: '已支付',
    3: '已完成',
    4: '已取消',
    5: '已退款',
    6: '退款审核中'
  }
  return statusMap[status] || '未知状态'
}

/**
 * 订单状态颜色
 */
export const orderStatusColor = (status) => {
  const colorMap = {
    1: '#ff9900',
    2: '#07c160',
    3: '#999999',
    4: '#999999',
    5: '#999999',
    6: '#ff9900'
  }
  return colorMap[status] || '#999999'
}

export default {
  formatDate,
  formatTime,
  formatPrice,
  formatDistance,
  getWeekday,
  isToday,
  isTomorrow,
  formatDateDisplay,
  maskPhone,
  orderStatusText,
  orderStatusColor
}

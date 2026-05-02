/**
 * 网络请求封装
 * 统一处理请求拦截、响应拦截、错误处理
 */

// API 基础地址
const BASE_URL = 'http://localhost:8080/api'

// 请求超时时间
const TIMEOUT = 30000

/**
 * 请求拦截器
 * @param {Object} config - 请求配置
 */
const requestInterceptor = (config) => {
  // 从本地存储获取 token
  const token = uni.getStorageSync('token')
  if (token) {
    config.header = {
      ...config.header,
      Authorization: `Bearer ${token}`
    }
  }

  // 添加请求 ID (用于调试)
  config.requestId = Date.now()
  console.log(`[Request ${config.requestId}]`, config.method || 'GET', config.url, config.data)

  return config
}

/**
 * 响应拦截器
 * @param {Object} response - 响应数据
 */
const responseInterceptor = (response, requestId) => {
  const { statusCode, data } = response

  console.log(`[Response ${requestId}]`, statusCode, data)

  // HTTP 状态码判断
  if (statusCode === 200) {
    // 业务状态码判断
    if (data.code === 200) {
      return data
    } else {
      // 业务错误
      handleBusinessError(data)
      return Promise.reject(data)
    }
  } else if (statusCode === 401) {
    // 未授权,跳转登录
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    // 延迟跳转到登录页
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    }, 1500)
    return Promise.reject(response)
  } else {
    // 其他 HTTP 错误
    uni.showToast({
      title: `网络错误: ${statusCode}`,
      icon: 'none'
    })
    return Promise.reject(response)
  }
}

/**
 * 处理业务错误
 * @param {Object} error - 错误信息
 */
const handleBusinessError = (error) => {
  const errorMessages = {
    1001: '用户不存在',
    1002: '用户已禁用',
    2001: '电影不存在',
    2002: '电影已下架',
    3001: '影院不存在',
    3002: '影院已关闭',
    4001: '场次不存在',
    4002: '场次已取消',
    4003: '座位已被选',
    4004: '座位锁定失败',
    5001: '订单不存在',
    5002: '订单已支付',
    5003: '订单已取消',
    5004: '订单已过期',
    5005: '退票时间不足',
    6001: '优惠券不存在',
    6002: '优惠券已使用',
    6003: '优惠券已过期',
    6004: '优惠券不满足条件'
  }

  const message = errorMessages[error.code] || error.message || '请求失败'

  uni.showToast({
    title: message,
    icon: 'none',
    duration: 2000
  })
}

/**
 * 统一请求方法
 * @param {Object} options - 请求配置
 * @returns {Promise}
 */
const request = (options) => {
  // 合并配置
  const config = {
    url: BASE_URL + options.url,
    method: options.method || 'GET',
    data: options.data || {},
    header: {
      'Content-Type': 'application/json',
      ...options.header
    },
    timeout: options.timeout || TIMEOUT
  }

  // 请求拦截
  const processedConfig = requestInterceptor(config)

  // 发起请求
  return new Promise((resolve, reject) => {
    uni.request({
      ...processedConfig,
      success: (response) => {
        responseInterceptor(response, processedConfig.requestId)
          .then(resolve)
          .catch(reject)
      },
      fail: (error) => {
        console.error(`[Request Failed ${processedConfig.requestId}]`, error)
        uni.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(error)
      }
    })
  })
}

/**
 * GET 请求
 */
export const get = (url, data, options = {}) => {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  })
}

/**
 * POST 请求
 */
export const post = (url, data, options = {}) => {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

/**
 * PUT 请求
 */
export const put = (url, data, options = {}) => {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

/**
 * DELETE 请求
 */
export const del = (url, data, options = {}) => {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  })
}

export default {
  get,
  post,
  put,
  del,
  request
}

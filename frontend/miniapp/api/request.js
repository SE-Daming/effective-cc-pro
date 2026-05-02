/**
 * HTTP 请求封装
 */
import config from './config'

// 请求拦截器
const requestInterceptor = (options) => {
  // 获取 token
  const token = uni.getStorageSync('token')
  if (token) {
    options.header = {
      ...options.header,
      Authorization: `Bearer ${token}`
    }
  }
  return options
}

// 响应拦截器
const responseInterceptor = (response) => {
  const { statusCode, data } = response

  if (statusCode === 200) {
    // 业务状态码判断
    if (data.code === 200) {
      return data.data
    } else if (data.code === 401) {
      // 未登录或 token 过期
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      // 跳转登录页
      uni.navigateTo({ url: '/pages/login/login' })
      return Promise.reject(new Error('请先登录'))
    } else {
      // 业务错误
      uni.showToast({
        title: data.message || '请求失败',
        icon: 'none'
      })
      return Promise.reject(new Error(data.message))
    }
  } else {
    uni.showToast({
      title: '网络错误',
      icon: 'none'
    })
    return Promise.reject(new Error('网络错误'))
  }
}

/**
 * 通用请求方法
 * @param {Object} options 请求配置
 * @returns {Promise}
 */
const request = (options) => {
  // 合并默认配置
  options = {
    ...options,
    url: config.baseUrl + options.url,
    timeout: config.timeout,
    header: {
      'Content-Type': 'application/json',
      ...options.header
    }
  }

  // 请求拦截
  options = requestInterceptor(options)

  return new Promise((resolve, reject) => {
    uni.request({
      ...options,
      success: (response) => {
        responseInterceptor(response)
          .then(resolve)
          .catch(reject)
      },
      fail: (error) => {
        uni.showToast({
          title: '网络请求失败',
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
export const get = (url, params = {}, options = {}) => {
  return request({
    url,
    method: 'GET',
    data: params,
    ...options
  })
}

/**
 * POST 请求
 */
export const post = (url, data = {}, options = {}) => {
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
export const put = (url, data = {}, options = {}) => {
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
export const del = (url, data = {}, options = {}) => {
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

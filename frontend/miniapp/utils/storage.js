/**
 * 存储工具函数
 */

/**
 * 设置存储
 * @param {string} key 键名
 * @param {any} value 值
 */
export const setStorage = (key, value) => {
  try {
    uni.setStorageSync(key, JSON.stringify(value))
    return true
  } catch (e) {
    console.error('setStorage error:', e)
    return false
  }
}

/**
 * 获取存储
 * @param {string} key 键名
 * @param {any} defaultValue 默认值
 */
export const getStorage = (key, defaultValue = null) => {
  try {
    const value = uni.getStorageSync(key)
    if (value) {
      return JSON.parse(value)
    }
    return defaultValue
  } catch (e) {
    console.error('getStorage error:', e)
    return defaultValue
  }
}

/**
 * 移除存储
 * @param {string} key 键名
 */
export const removeStorage = (key) => {
  try {
    uni.removeStorageSync(key)
    return true
  } catch (e) {
    console.error('removeStorage error:', e)
    return false
  }
}

/**
 * 清空所有存储
 */
export const clearStorage = () => {
  try {
    uni.clearStorageSync()
    return true
  } catch (e) {
    console.error('clearStorage error:', e)
    return false
  }
}

export default {
  setStorage,
  getStorage,
  removeStorage,
  clearStorage
}

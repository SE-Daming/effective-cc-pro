/**
 * API 配置
 */

// 开发环境
const devBaseUrl = 'http://localhost:8080/api'

// 生产环境
const prodBaseUrl = 'https://api.maoyan.com/api'

// 当前环境
const isDev = process.env.NODE_ENV === 'development'

// 导出配置
export const config = {
  baseUrl: isDev ? devBaseUrl : prodBaseUrl,
  appId: 'wx6fc4322ae9fb6004',
  timeout: 30000
}

export default config

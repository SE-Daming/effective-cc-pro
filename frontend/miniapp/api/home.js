/**
 * 首页相关 API
 */
import { get } from '../utils/request.js'

/**
 * 获取 Banner 列表
 */
export const getBanners = () => {
  return get('/home/banners')
}

/**
 * 获取热门推荐电影
 */
export const getHotMovies = (limit = 10) => {
  return get('/home/hot-movies', { limit })
}

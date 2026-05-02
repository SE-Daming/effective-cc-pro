/**
 * 电影相关 API
 */
import { get, post, del } from '../utils/request.js'

/**
 * 获取正在热映电影列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.city - 城市
 */
export const getNowPlaying = (params = {}) => {
  return get('/movies/now-playing', params)
}

/**
 * 获取即将上映电影列表
 * @param {Object} params - 查询参数
 */
export const getComingSoon = (params = {}) => {
  return get('/movies/coming-soon', params)
}

/**
 * 搜索电影
 * @param {string} keyword - 搜索关键词
 * @param {Object} params - 其他查询参数
 */
export const searchMovies = (keyword, params = {}) => {
  return get('/movies/search', { keyword, ...params })
}

/**
 * 获取电影详情
 * @param {number} id - 电影ID
 */
export const getMovieDetail = (id) => {
  return get(`/movies/${id}`)
}

/**
 * 获取电影演员列表
 * @param {number} id - 电影ID
 */
export const getMovieActors = (id) => {
  return get(`/movies/${id}/actors`)
}

/**
 * 获取电影影评列表
 * @param {number} id - 电影ID
 * @param {Object} params - 查询参数
 */
export const getMovieReviews = (id, params = {}) => {
  return get(`/movies/${id}/reviews`, params)
}

/**
 * 发布影评
 * @param {Object} data - 影评数据
 */
export const publishReview = (data) => {
  return post('/reviews', data)
}

/**
 * 删除影评
 * @param {number} id - 影评ID
 */
export const deleteReview = (id) => {
  return del(`/reviews/${id}`)
}

/**
 * 点赞影评
 * @param {number} id - 影评ID
 */
export const likeReview = (id) => {
  return post(`/reviews/${id}/like`)
}

/**
 * 取消点赞
 * @param {number} id - 影评ID
 */
export const unlikeReview = (id) => {
  return del(`/reviews/${id}/like`)
}

/**
 * 获取电影分类列表
 */
export const getMovieCategories = () => {
  return get('/movie-categories')
}

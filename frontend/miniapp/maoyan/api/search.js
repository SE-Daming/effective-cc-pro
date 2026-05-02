/**
 * 搜索相关 API
 */
import { get, del } from './request'
import { searchMovies } from './movie'
import { getCinemaList } from './cinema'

/**
 * 搜索电影（复用 movie.js 的搜索接口）
 * @param {string} keyword - 搜索关键词
 * @param {Object} params - 其他查询参数
 */
export const searchMovie = (keyword, params = {}) => {
  return searchMovies(keyword, params)
}

/**
 * 搜索影院（复用 cinema.js 的列表接口）
 * @param {string} keyword - 搜索关键词
 * @param {Object} params - 其他查询参数
 */
export const searchCinema = (keyword, params = {}) => {
  return getCinemaList({ keyword, ...params })
}

/**
 * 获取搜索历史
 * @param {Object} params - 查询参数
 * @param {number} params.type - 类型：1电影 2影院，不传查全部
 * @param {number} params.limit - 数量限制，默认10
 */
export const getSearchHistory = (params = {}) => {
  return get('/users/search-history', params)
}

/**
 * 清空搜索历史
 * @param {number} type - 类型：1电影 2影院，不传清空全部
 */
export const clearSearchHistory = (type) => {
  return del('/users/search-history', type ? { type } : {})
}

/**
 * 获取热门搜索关键词
 * 这里使用本地数据，实际项目中可以从后端获取
 */
export const getHotKeywords = () => {
  return Promise.resolve({
    data: {
      movie: [
        { keyword: '流浪地球', heat: 100 },
        { keyword: '满江红', heat: 95 },
        { keyword: '消失的她', heat: 90 },
        { keyword: '孤注一掷', heat: 85 },
        { keyword: '封神第一部', heat: 80 },
        { keyword: '热烈', heat: 75 },
        { keyword: '长安三万里', heat: 70 },
        { keyword: '八角笼中', heat: 65 }
      ],
      cinema: [
        { keyword: '万达影城', heat: 100 },
        { keyword: '金逸影城', heat: 95 },
        { keyword: '大地影院', heat: 90 },
        { keyword: '博纳影城', heat: 85 },
        { keyword: 'CGV影城', heat: 80 },
        { keyword: '百老汇', heat: 75 },
        { keyword: '幸福蓝海', heat: 70 },
        { keyword: '中影国际', heat: 65 }
      ]
    }
  })
}

export default {
  searchMovie,
  searchCinema,
  getSearchHistory,
  clearSearchHistory,
  getHotKeywords
}

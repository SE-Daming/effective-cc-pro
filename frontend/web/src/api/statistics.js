import request from '@/utils/request'

// 获取销售概览
export function getOverview(params) {
  return request({
    url: '/admin/statistics/overview',
    method: 'get',
    params
  })
}

// 获取销售趋势
export function getTrend(params) {
  return request({
    url: '/admin/statistics/trend',
    method: 'get',
    params
  })
}

// 获取电影票房排行
export function getMovieRanking(params) {
  return request({
    url: '/admin/statistics/movie-ranking',
    method: 'get',
    params
  })
}

// 获取影院销售排行
export function getCinemaRanking(params) {
  return request({
    url: '/admin/statistics/cinema-ranking',
    method: 'get',
    params
  })
}

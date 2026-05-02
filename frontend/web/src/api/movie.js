import request from '@/utils/request'

// 获取电影列表
export function getMovieList(params) {
  return request({
    url: '/admin/movies',
    method: 'get',
    params
  })
}

// 获取电影详情
export function getMovieDetail(id) {
  return request({
    url: `/admin/movies/${id}`,
    method: 'get'
  })
}

// 新增电影
export function createMovie(data) {
  return request({
    url: '/admin/movies',
    method: 'post',
    data
  })
}

// 更新电影
export function updateMovie(id, data) {
  return request({
    url: `/admin/movies/${id}`,
    method: 'put',
    data
  })
}

// 删除电影
export function deleteMovie(id) {
  return request({
    url: `/admin/movies/${id}`,
    method: 'delete'
  })
}

// 获取电影分类
export function getMovieCategories() {
  return request({
    url: '/admin/movie-categories',
    method: 'get'
  })
}

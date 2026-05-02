import request from '@/utils/request'

// 获取影院列表
export function getCinemaList(params) {
  return request({
    url: '/admin/cinemas',
    method: 'get',
    params
  })
}

// 获取影院详情
export function getCinemaDetail(id) {
  return request({
    url: `/admin/cinemas/${id}`,
    method: 'get'
  })
}

// 新增影院
export function createCinema(data) {
  return request({
    url: '/admin/cinemas',
    method: 'post',
    data
  })
}

// 更新影院
export function updateCinema(id, data) {
  return request({
    url: `/admin/cinemas/${id}`,
    method: 'put',
    data
  })
}

// 删除影院
export function deleteCinema(id) {
  return request({
    url: `/admin/cinemas/${id}`,
    method: 'delete'
  })
}

// 获取影厅列表
export function getHallList(cinemaId) {
  return request({
    url: '/admin/halls',
    method: 'get',
    params: { cinemaId }
  })
}

// 新增影厅
export function createHall(data) {
  return request({
    url: '/admin/halls',
    method: 'post',
    data
  })
}

// 更新影厅
export function updateHall(id, data) {
  return request({
    url: `/admin/halls/${id}`,
    method: 'put',
    data
  })
}

// 删除影厅
export function deleteHall(id) {
  return request({
    url: `/admin/halls/${id}`,
    method: 'delete'
  })
}

// 获取影院品牌
export function getCinemaBrands() {
  return request({
    url: '/admin/brands',
    method: 'get'
  })
}

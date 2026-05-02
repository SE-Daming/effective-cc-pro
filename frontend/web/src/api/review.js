import request from '@/utils/request'

// 获取影评列表
export function getReviewList(params) {
  return request({
    url: '/admin/reviews',
    method: 'get',
    params
  })
}

// 获取影评详情
export function getReviewDetail(id) {
  return request({
    url: `/admin/reviews/${id}`,
    method: 'get'
  })
}

// 审核影评（显示/隐藏）
export function auditReview(id, status) {
  return request({
    url: `/admin/reviews/${id}/audit`,
    method: 'patch',
    data: { status }
  })
}

// 删除影评
export function deleteReview(id) {
  return request({
    url: `/admin/reviews/${id}`,
    method: 'delete'
  })
}

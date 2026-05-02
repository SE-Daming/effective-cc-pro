import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/admin/orders',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/admin/orders/${id}`,
    method: 'get'
  })
}

// 退款审核
export function refundAudit(id, data) {
  return request({
    url: `/admin/orders/${id}/refund-audit`,
    method: 'post',
    data
  })
}

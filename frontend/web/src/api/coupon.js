import request from '@/utils/request'

// 获取优惠券列表
export function getCouponList(params) {
  return request({
    url: '/admin/coupons',
    method: 'get',
    params
  })
}

// 新增优惠券
export function createCoupon(data) {
  return request({
    url: '/admin/coupons',
    method: 'post',
    data
  })
}

// 更新优惠券
export function updateCoupon(id, data) {
  return request({
    url: `/admin/coupons/${id}`,
    method: 'put',
    data
  })
}

// 删除优惠券
export function deleteCoupon(id) {
  return request({
    url: `/admin/coupons/${id}`,
    method: 'delete'
  })
}

// 发放优惠券
export function distributeCoupon(id, data) {
  return request({
    url: `/admin/coupons/${id}/distribute`,
    method: 'post',
    data
  })
}

// 获取优惠券核销记录
export function getCouponRecords(id, params) {
  return request({
    url: `/admin/coupons/${id}/records`,
    method: 'get',
    params
  })
}

import request from '@/utils/request'

// 获取排片列表
export function getScheduleList(params) {
  return request({
    url: '/admin/schedules',
    method: 'get',
    params
  })
}

// 新增排片
export function createSchedule(data) {
  return request({
    url: '/admin/schedules',
    method: 'post',
    data
  })
}

// 批量排片
export function batchCreateSchedule(data) {
  return request({
    url: '/admin/schedules/batch',
    method: 'post',
    data
  })
}

// 删除排片
export function deleteSchedule(id) {
  return request({
    url: `/admin/schedules/${id}`,
    method: 'delete'
  })
}

// 获取排片日历数据
export function getScheduleCalendar(params) {
  return request({
    url: '/admin/schedules/calendar',
    method: 'get',
    params
  })
}

# 管理员模块 API

> 后台管理接口

---

## 1. 管理员认证

### 1.1 管理员登录

**接口**：`POST /api/admin/auth/login`

**请求参数**：
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "Bearer xxx",
    "adminInfo": {
      "id": 1,
      "username": "admin",
      "realName": "超级管理员",
      "roleId": 1,
      "roleName": "超级管理员",
      "permissions": {
        "all": true
      }
    }
  }
}
```

### 1.2 管理员登出

**接口**：`POST /api/admin/auth/logout`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.3 获取当前管理员信息

**接口**：`GET /api/admin/auth/info`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "超级管理员",
    "phone": "138****8888",
    "email": "admin@example.com",
    "roleId": 1,
    "roleName": "超级管理员",
    "permissions": {
      "all": true
    },
    "lastLoginTime": "2025-05-01 10:00:00"
  }
}
```

---

## 2. 电影管理

### 2.1 获取电影列表

**接口**：`GET /api/admin/movies`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| status | int | 否 | 状态 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "流浪地球3",
        "poster": "海报URL",
        "director": "郭帆",
        "duration": 150,
        "categoryNames": "动作,科幻",
        "releaseDate": "2025-02-01",
        "score": 9.2,
        "status": 2,
        "createTime": "2025-01-01 10:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 10
  }
}
```

### 2.2 新增电影

**接口**：`POST /api/admin/movies`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "title": "流浪地球3",
  "englishTitle": "The Wandering Earth 3",
  "poster": "海报URL",
  "director": "郭帆",
  "actors": "吴京,刘德华,李雪健",
  "duration": 150,
  "categoryIds": "1,4",
  "region": "中国大陆",
  "language": "汉语普通话",
  "releaseDate": "2025-02-01",
  "synopsis": "剧情简介...",
  "trailerUrl": "预告片URL",
  "status": 1
}
```

### 2.3 更新电影

**接口**：`PUT /api/admin/movies/{id}`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：同新增

### 2.4 删除电影

**接口**：`DELETE /api/admin/movies/{id}`

**请求头**：`Authorization: Bearer xxx`

---

## 3. 影院管理

### 3.1 获取影院列表

**接口**：`GET /api/admin/cinemas`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| city | string | 否 | 城市 |
| brandId | int | 否 | 品牌ID |
| status | int | 否 | 状态 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

### 3.2 新增影院

**接口**：`POST /api/admin/cinemas`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "name": "万达影城（XX店）",
  "brandId": 1,
  "province": "上海市",
  "city": "上海市",
  "district": "浦东新区",
  "address": "浦东新区XX路XX号",
  "longitude": 121.473701,
  "latitude": 31.230416,
  "phone": "021-12345678",
  "facilities": "IMAX,Dolby,3D",
  "description": "影院介绍...",
  "images": ["图片1", "图片2"]
}
```

### 3.3 更新影院

**接口**：`PUT /api/admin/cinemas/{id}`

### 3.4 删除影院

**接口**：`DELETE /api/admin/cinemas/{id}`

---

## 4. 影厅管理

### 4.1 获取影厅列表

**接口**：`GET /api/admin/halls`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| cinemaId | long | 是 | 影院ID |

### 4.2 新增影厅

**接口**：`POST /api/admin/halls`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "cinemaId": 1,
  "name": "1号厅",
  "type": "IMAX",
  "rows": 10,
  "cols": 20,
  "seatLayout": "{...}"
}
```

**说明**：创建影厅时会自动生成座位数据

---

## 5. 排片管理

### 5.1 获取排片列表

**接口**：`GET /api/admin/schedules`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| cinemaId | long | 否 | 影院ID |
| movieId | long | 否 | 电影ID |
| showDate | date | 否 | 放映日期 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

### 5.2 新增排片

**接口**：`POST /api/admin/schedules`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "movieId": 1,
  "cinemaId": 1,
  "hallId": 1,
  "showDate": "2025-05-02",
  "showTime": "14:30",
  "price": 68.00,
  "cleanDuration": 15
}
```

**说明**：
- 新增前会自动检测排片冲突
- 自动计算 end_time = show_time + duration + clean_duration
- 自动填充 total_seats

### 5.3 批量排片

**接口**：`POST /api/admin/schedules/batch`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "movieId": 1,
  "cinemaId": 1,
  "hallId": 1,
  "showDates": ["2025-05-02", "2025-05-03"],
  "showTimes": ["10:00", "14:30", "19:00"],
  "price": 68.00
}
```

### 5.4 删除排片

**接口**：`DELETE /api/admin/schedules/{id}`

---

## 6. 订单管理

### 6.1 获取订单列表

**接口**：`GET /api/admin/orders`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderNo | string | 否 | 订单号 |
| status | int | 否 | 状态 |
| type | int | 否 | 类型 |
| userId | long | 否 | 用户ID |
| cinemaId | long | 否 | 影院ID |
| startTime | datetime | 否 | 开始时间 |
| endTime | datetime | 否 | 结束时间 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

### 6.2 获取订单详情

**接口**：`GET /api/admin/orders/{id}`

### 6.3 退款审核

**接口**：`POST /api/admin/orders/{id}/refund-audit`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "approve": true,
  "remark": "同意退款"
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "refundAmount": 165.60,
    "refundFee": 18.40
  }
}
```

---

## 7. 优惠券管理

### 7.1 获取优惠券列表

**接口**：`GET /api/admin/coupons`

### 7.2 新增优惠券

**接口**：`POST /api/admin/coupons`

**请求参数**：
```json
{
  "name": "满200减20",
  "type": 1,
  "value": 20.00,
  "minAmount": 200.00,
  "totalCount": 1000,
  "limitPerUser": 1,
  "validDays": 30,
  "validStart": "2025-05-01",
  "validEnd": "2025-06-30",
  "scope": 1
}
```

### 7.3 发放优惠券

**接口**：`POST /api/admin/coupons/{id}/distribute`

**请求参数**：
```json
{
  "userIds": [1, 2, 3],
  "all": false
}
```

---

## 8. 用户管理

### 8.1 获取用户列表

**接口**：`GET /api/admin/users`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词（昵称/手机号） |
| status | int | 否 | 状态 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

### 8.2 获取用户详情

**接口**：`GET /api/admin/users/{id}`

### 8.3 禁用/启用用户

**接口**：`PATCH /api/admin/users/{id}/status`

**请求参数**：
```json
{
  "status": 0
}
```

---

## 9. 数据统计

### 9.1 销售统计概览

**接口**：`GET /api/admin/statistics/overview`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | date | 否 | 开始日期 |
| endDate | date | 否 | 结束日期 |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalOrders": 1000,
    "totalAmount": 50000.00,
    "totalTickets": 2000,
    "totalUsers": 500,
    "newUsers": 50,
    "refundCount": 20,
    "refundAmount": 1000.00
  }
}
```

### 9.2 销售趋势

**接口**：`GET /api/admin/statistics/trend`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | date | 是 | 开始日期 |
| endDate | date | 是 | 结束日期 |
| type | string | 否 | 类型：day/month |

### 9.3 电影票房排行

**接口**：`GET /api/admin/statistics/movie-ranking`

### 9.4 影院销售排行

**接口**：`GET /api/admin/statistics/cinema-ranking`

---

## 10. 管理员管理

### 10.1 获取管理员列表

**接口**：`GET /api/admin/admins`

### 10.2 新增管理员

**接口**：`POST /api/admin/admins`

### 10.3 更新管理员

**接口**：`PUT /api/admin/admins/{id}`

### 10.4 删除管理员

**接口**：`DELETE /api/admin/admins/{id}`

---

## 11. 角色管理

### 11.1 获取角色列表

**接口**：`GET /api/admin/roles`

### 11.2 新增角色

**接口**：`POST /api/admin/roles`

**请求参数**：
```json
{
  "name": "运营管理员",
  "code": "operator",
  "permissions": {
    "movie": true,
    "cinema": true,
    "schedule": true
  }
}
```

---

## 12. 操作日志

### 12.1 获取操作日志列表

**接口**：`GET /api/admin/operation-logs`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| adminId | int | 否 | 管理员ID |
| module | string | 否 | 模块 |
| startTime | datetime | 否 | 开始时间 |
| endTime | datetime | 否 | 结束时间 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

---

*文档版本：v1.0*
*创建时间：2025-05-02*

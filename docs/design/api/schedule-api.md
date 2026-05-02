# 排片模块 API

> 排片查询、座位选择相关接口

---

## 1. 排片查询

### 1.1 获取电影排片影院列表

**接口**：`GET /api/movies/{id}/cinemas`

**描述**：查询某电影在哪些影院有排片

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| city | string | 是 | 城市 |
| date | date | 是 | 放映日期 |
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
        "cinemaId": 1,
        "cinemaName": "万达影城（XX店）",
        "address": "XX区XX路XX号",
        "distance": 1.2,
        "schedules": [
          {
            "id": 1,
            "hallName": "1号IMAX厅",
            "hallType": "IMAX",
            "showTime": "14:30",
            "price": 68.00,
            "soldSeats": 150,
            "totalSeats": 200
          }
        ]
      }
    ],
    "total": 20,
    "page": 1,
    "pageSize": 10
  }
}
```

### 1.2 获取排片详情

**接口**：`GET /api/schedules/{id}`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "movieId": 1,
    "movieTitle": "流浪地球3",
    "moviePoster": "海报URL",
    "cinemaId": 1,
    "cinemaName": "万达影城（XX店）",
    "hallId": 1,
    "hallName": "1号IMAX厅",
    "hallType": "IMAX",
    "showDate": "2025-05-02",
    "showTime": "14:30",
    "endTime": "17:00",
    "price": 68.00,
    "totalSeats": 200,
    "soldSeats": 150,
    "status": 1
  }
}
```

---

## 2. 座位查询

### 2.1 获取场次座位图

**接口**：`GET /api/schedules/{id}/seats`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hallInfo": {
      "id": 1,
      "name": "1号IMAX厅",
      "type": "IMAX",
      "rows": 10,
      "cols": 20
    },
    "movieInfo": {
      "id": 1,
      "title": "流浪地球3",
      "poster": "海报URL",
      "duration": 150
    },
    "scheduleInfo": {
      "id": 1,
      "showDate": "2025-05-02",
      "showTime": "14:30",
      "price": 68.00
    },
    "seats": [
      {
        "id": 1,
        "rowNum": 1,
        "colNum": 1,
        "seatNo": "A01",
        "seatType": 1,
        "status": 1,
        "lockStatus": 0
      },
      {
        "id": 2,
        "rowNum": 1,
        "colNum": 2,
        "seatNo": "A02",
        "seatType": 2,
        "status": 1,
        "lockStatus": 1
      }
    ],
    "maxSelect": 4
  }
}
```

**座位状态说明**：
| lockStatus | 说明 |
|------------|------|
| 0 | 可选 |
| 1 | 已锁定 |
| 2 | 已售 |

---

## 3. 座位锁定

### 3.1 锁定座位

**接口**：`POST /api/schedules/{id}/lock-seats`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "seatIds": [1, 2, 3]
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "lockId": "lock_202505021430001",
    "seats": [
      {
        "id": 1,
        "seatNo": "A01",
        "rowNum": 1,
        "colNum": 1,
        "price": 68.00
      }
    ],
    "totalPrice": 204.00,
    "expireTime": "2025-05-02 14:35:00"
  }
}
```

**说明**：
- 锁定成功后，座位保留 5 分钟
- 5 分钟内未支付，座位自动释放
- 返回的 lockId 用于后续创建订单

### 3.2 释放座位锁

**接口**：`DELETE /api/seat-locks/{lockId}`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 4. 排片冲突检测

### 4.1 检测排片冲突（B端）

**接口**：`POST /api/admin/schedules/check-conflict`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "hallId": 1,
  "showDate": "2025-05-02",
  "showTime": "14:30",
  "duration": 150,
  "cleanDuration": 15,
  "excludeId": null
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasConflict": false,
    "conflictSchedules": []
  }
}
```

或存在冲突时：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasConflict": true,
    "conflictSchedules": [
      {
        "id": 2,
        "movieTitle": "另一部电影",
        "showTime": "16:00",
        "endTime": "18:30"
      }
    ]
  }
}
```

---

*文档版本：v1.0*
*创建时间：2025-05-02*

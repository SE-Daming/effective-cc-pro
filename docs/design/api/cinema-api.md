# 影院模块 API

> 影院列表、详情、影厅相关接口

---

## 1. 影院列表

### 1.1 获取影院列表

**接口**：`GET /api/cinemas`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| city | string | 是 | 城市 |
| district | string | 否 | 区域 |
| brandId | int | 否 | 品牌ID |
| keyword | string | 否 | 搜索关键词 |
| longitude | decimal | 否 | 用户经度（用于计算距离） |
| latitude | decimal | 否 | 用户纬度 |
| orderBy | string | 否 | 排序：distance/score |
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
        "name": "万达影城（XX店）",
        "brandId": 1,
        "brandName": "万达影城",
        "address": "XX区XX路XX号",
        "distance": 1.2,
        "phone": "021-12345678",
        "facilities": "IMAX,Dolby,3D",
        "score": 4.8,
        "status": 1
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 10
  }
}
```

### 1.2 获取附近影院

**接口**：`GET /api/cinemas/nearby`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| longitude | decimal | 是 | 用户经度 |
| latitude | decimal | 是 | 用户纬度 |
| radius | int | 否 | 搜索半径（km），默认5 |
| limit | int | 否 | 数量限制，默认10 |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "万达影城（XX店）",
        "address": "XX区XX路XX号",
        "distance": 1.2,
        "score": 4.8
      }
    ]
  }
}
```

---

## 2. 影院详情

### 2.1 获取影院详情

**接口**：`GET /api/cinemas/{id}`

**路径参数**：
| 参数 | 类型 | 说明 |
|------|------|------|
| id | long | 影院ID |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "万达影城（XX店）",
    "brandId": 1,
    "brandName": "万达影城",
    "province": "上海市",
    "city": "上海市",
    "district": "浦东新区",
    "address": "浦东新区XX路XX号",
    "longitude": 121.473701,
    "latitude": 31.230416,
    "phone": "021-12345678",
    "facilities": "IMAX,Dolby,3D",
    "description": "影院介绍...",
    "images": ["图片1", "图片2"],
    "score": 4.8,
    "status": 1,
    "isFavorite": false
  }
}
```

### 2.2 获取影院影厅列表

**接口**：`GET /api/cinemas/{id}/halls`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "1号厅",
        "type": "IMAX",
        "totalSeats": 200,
        "status": 1
      },
      {
        "id": 2,
        "name": "2号厅",
        "type": "普通",
        "totalSeats": 150,
        "status": 1
      }
    ]
  }
}
```

---

## 3. 影院排片

### 3.1 获取影院排片日期列表

**接口**：`GET /api/cinemas/{id}/schedule-dates`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| movieId | long | 否 | 电影ID |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "date": "2025-05-02",
        "weekday": "周五",
        "isToday": true,
        "isTomorrow": false
      },
      {
        "date": "2025-05-03",
        "weekday": "周六",
        "isToday": false,
        "isTomorrow": true
      }
    ]
  }
}
```

### 3.2 获取影院某日排片

**接口**：`GET /api/cinemas/{id}/schedules`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| date | date | 是 | 放映日期 |
| movieId | long | 否 | 电影ID |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "movieId": 1,
        "movieTitle": "流浪地球3",
        "moviePoster": "海报URL",
        "score": 9.2,
        "duration": 150,
        "schedules": [
          {
            "id": 1,
            "hallId": 1,
            "hallName": "1号IMAX厅",
            "hallType": "IMAX",
            "showTime": "14:30",
            "endTime": "17:00",
            "price": 68.00,
            "totalSeats": 200,
            "soldSeats": 150,
            "status": 1
          }
        ]
      }
    ]
  }
}
```

---

## 4. 影院品牌

### 4.1 获取影院品牌列表

**接口**：`GET /api/cinema-brands`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "万达影城",
        "logo": "Logo URL",
        "sort": 1
      },
      {
        "id": 2,
        "name": "CGV影城",
        "logo": "Logo URL",
        "sort": 2
      }
    ]
  }
}
```

---

*文档版本：v1.0*
*创建时间：2025-05-02*

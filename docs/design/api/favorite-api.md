# 收藏模块 API

> 收藏/取消收藏相关接口

---

## 1. 收藏操作

### 1.1 添加收藏

**接口**：`POST /api/favorites`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "targetType": 1,
  "targetId": 1
}
```

**参数说明**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetType | int | 是 | 类型：1电影 2影院 |
| targetId | long | 是 | 目标ID |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "targetType": 1,
    "targetId": 1,
    "createTime": "2025-05-02 10:00:00"
  }
}
```

### 1.2 取消收藏

**接口**：`DELETE /api/favorites`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "targetType": 1,
  "targetId": 1
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.3 检查是否已收藏

**接口**：`GET /api/favorites/check`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetType | int | 是 | 类型：1电影 2影院 |
| targetId | long | 是 | 目标ID |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "isFavorite": true,
    "favoriteId": 1
  }
}
```

---

## 2. 收藏列表

### 2.1 获取收藏的电影列表

**接口**：`GET /api/favorites/movies`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
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
        "targetId": 1,
        "movieId": 1,
        "movieTitle": "流浪地球3",
        "moviePoster": "海报URL",
        "movieScore": 9.2,
        "movieStatus": 2,
        "createTime": "2025-05-01 10:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10
  }
}
```

### 2.2 获取收藏的影院列表

**接口**：`GET /api/favorites/cinemas`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
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
        "id": 2,
        "targetId": 1,
        "cinemaId": 1,
        "cinemaName": "万达影城（XX店）",
        "cinemaAddress": "XX区XX路XX号",
        "cinemaScore": 4.8,
        "cinemaStatus": 1,
        "createTime": "2025-05-01 10:00:00"
      }
    ],
    "total": 3,
    "page": 1,
    "pageSize": 10
  }
}
```

---

## 3. 收藏统计

### 3.1 获取收藏数量统计

**接口**：`GET /api/favorites/count`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "movieCount": 5,
    "cinemaCount": 3,
    "totalCount": 8
  }
}
```

---

*文档版本：v1.0*
*创建时间：2025-05-02*

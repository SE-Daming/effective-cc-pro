# 电影模块 API

> 电影列表、详情、影评相关接口

---

## 1. 电影列表

### 1.1 正在热映

**接口**：`GET /api/movies/now-playing`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页数量，默认10 |
| city | string | 否 | 城市 |

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
        "score": 9.2,
        "duration": 150,
        "categoryIds": "1,4",
        "categoryNames": "动作,科幻",
        "releaseDate": "2025-02-01",
        "status": 2,
        "wantCount": 10000
      }
    ],
    "total": 20,
    "page": 1,
    "pageSize": 10
  }
}
```

### 1.2 即将上映

**接口**：`GET /api/movies/coming-soon`

**请求参数**：同上

**响应参数**：同上，额外包含 `releaseDate` 字段

### 1.3 电影搜索

**接口**：`GET /api/movies/search`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
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
        "score": 9.2,
        "categoryNames": "动作,科幻",
        "releaseDate": "2025-02-01"
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10
  }
}
```

---

## 2. 电影详情

### 2.1 获取电影详情

**接口**：`GET /api/movies/{id}`

**路径参数**：
| 参数 | 类型 | 说明 |
|------|------|------|
| id | long | 电影ID |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "流浪地球3",
    "englishTitle": "The Wandering Earth 3",
    "poster": "海报URL",
    "director": "郭帆",
    "actors": "吴京,刘德华,李雪健",
    "duration": 150,
    "categoryIds": "1,4",
    "categoryNames": "动作,科幻",
    "region": "中国大陆",
    "language": "汉语普通话",
    "releaseDate": "2025-02-01",
    "synopsis": "剧情简介...",
    "trailerUrl": "预告片URL",
    "score": 9.2,
    "scoreCount": 50000,
    "status": 2,
    "isFavorite": false,
    "wantCount": 10000
  }
}
```

### 2.2 获取电影演员列表

**接口**：`GET /api/movies/{id}/actors`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "吴京",
        "role": "主演",
        "sort": 1
      },
      {
        "id": 2,
        "name": "郭帆",
        "role": "导演",
        "sort": 0
      }
    ]
  }
}
```

### 2.3 获取电影影评列表

**接口**：`GET /api/movies/{id}/reviews`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |
| orderBy | string | 否 | 排序：time/like |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 10,
        "nickname": "用户昵称",
        "avatar": "头像URL",
        "score": 9,
        "content": "影评内容...",
        "likeCount": 100,
        "isLiked": false,
        "createTime": "2025-05-01 10:00:00"
      }
    ],
    "total": 500,
    "page": 1,
    "pageSize": 10
  }
}
```

---

## 3. 电影分类

### 3.1 获取电影分类列表

**接口**：`GET /api/movie-categories`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "动作",
        "sort": 1
      },
      {
        "id": 2,
        "name": "喜剧",
        "sort": 2
      }
    ]
  }
}
```

---

## 4. 影评操作

### 4.1 发布影评

**接口**：`POST /api/reviews`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "movieId": 1,
  "score": 9,
  "content": "影评内容..."
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "movieId": 1,
    "score": 9,
    "content": "影评内容...",
    "createTime": "2025-05-02 10:00:00"
  }
}
```

### 4.2 删除影评

**接口**：`DELETE /api/reviews/{id}`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.3 点赞影评

**接口**：`POST /api/reviews/{id}/like`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "likeCount": 101
  }
}
```

### 4.4 取消点赞

**接口**：`DELETE /api/reviews/{id}/like`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "likeCount": 100
  }
}
```

---

*文档版本：v1.0*
*创建时间：2025-05-02*

# 用户模块 API

> 用户登录、个人信息、搜索历史相关接口

---

## 1. 微信授权登录

### 1.1 小程序登录

**接口**：`POST /api/auth/wx-login`

**请求参数**：
```json
{
  "code": "微信登录code",
  "encryptedData": "加密数据",
  "iv": "加密算法初始向量"
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "Bearer xxx",
    "userInfo": {
      "id": 1,
      "nickname": "用户昵称",
      "avatar": "头像URL",
      "phone": "138****8888"
    },
    "isNewUser": true
  }
}
```

### 1.2 更新用户信息

**接口**：`PUT /api/users/profile`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "nickname": "新昵称",
  "avatar": "新头像URL",
  "gender": 1
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "nickname": "新昵称",
    "avatar": "新头像URL",
    "gender": 1
  }
}
```

---

## 2. 用户信息

### 2.1 获取当前用户信息

**接口**：`GET /api/users/me`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "nickname": "用户昵称",
    "avatar": "头像URL",
    "phone": "138****8888",
    "gender": 1,
    "totalConsumption": 500.00,
    "createTime": "2025-05-01 10:00:00"
  }
}
```

### 2.2 绑定手机号

**接口**：`POST /api/users/bind-phone`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "phone": "13888888888",
  "code": "验证码"
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

---

## 3. 搜索历史

### 3.1 获取搜索历史

**接口**：`GET /api/users/search-history`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | int | 否 | 类型：1电影 2影院，不传查全部 |
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
        "keyword": "流浪地球",
        "type": 1,
        "createTime": "2025-05-02 10:00:00"
      }
    ]
  }
}
```

### 3.2 清空搜索历史

**接口**：`DELETE /api/users/search-history`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "type": 1  // 可选，不传清空全部
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

---

## 4. 用户统计

### 4.1 获取用户统计数据

**接口**：`GET /api/users/statistics`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderCount": 10,
    "totalConsumption": 500.00,
    "couponCount": 3,
    "favoriteCount": 5
  }
}
```

---

*文档版本：v1.0*
*创建时间：2025-05-02*

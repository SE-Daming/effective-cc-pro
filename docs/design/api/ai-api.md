# AI助手模块 API

> 智能客服、智能推荐、Function Call 相关接口

---

## 1. 智能对话

### 1.1 发送对话消息

**接口**：`POST /api/ai/chat`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "message": "帮我推荐一部科幻片",
  "sessionId": "session_123",
  "context": {
    "location": {
      "longitude": 121.473701,
      "latitude": 31.230416
    }
  }
}
```

**参数说明**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| message | string | 是 | 用户消息 |
| sessionId | string | 否 | 会话ID（首次可不传，返回新会话ID） |
| context | object | 否 | 上下文信息 |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "session_123",
    "reply": "好的，为您推荐以下科幻片：\n1. 流浪地球3 - 评分 9.2\n2. 三体 - 评分 8.8\n\n您对哪一部感兴趣？",
    "intent": "movie_recommend",
    "actions": [
      {
        "type": "show_movies",
        "data": [
          {
            "id": 1,
            "title": "流浪地球3",
            "poster": "海报URL",
            "score": 9.2
          }
        ]
      }
    ]
  }
}
```

---

## 2. 意图识别

### 2.1 意图类型说明

| 意图 | 说明 | 示例 |
|------|------|------|
| movie_query | 电影查询 | "最近有什么科幻片" |
| cinema_query | 影院查询 | "附近有什么电影院" |
| schedule_query | 排片查询 | "万达今天有什么电影" |
| movie_recommend | 电影推荐 | "推荐一部喜剧片" |
| cinema_recommend | 影院推荐 | "推荐附近评分高的影院" |
| ticket_book | 购票 | "帮我买一张流浪地球3" |
| order_query | 订单查询 | "查一下我的订单" |
| refund_apply | 退票申请 | "帮我退掉明天的票" |
| coupon_query | 优惠券查询 | "我有哪些优惠券" |
| rule_inquiry | 规则咨询 | "电影票怎么退" |

---

## 3. Function Call 接口

### 3.1 Function Call 流程

```
用户输入 → AI识别意图 → 调用Function → 返回结果 → AI生成回复
```

### 3.2 内部 Function 列表

AI助手可调用的主服务接口：

| Function | 说明 | 调用接口 |
|----------|------|----------|
| searchMovies | 搜索电影 | GET /api/movies/search |
| getMovieDetail | 获取电影详情 | GET /api/movies/{id} |
| getCinemas | 获取影院列表 | GET /api/cinemas |
| getCinemaDetail | 获取影院详情 | GET /api/cinemas/{id} |
| getSchedules | 获取排片列表 | GET /api/cinemas/{id}/schedules |
| getScheduleSeats | 获取场次座位 | GET /api/schedules/{id}/seats |
| lockSeats | 锁定座位 | POST /api/schedules/{id}/lock-seats |
| createOrder | 创建订单 | POST /api/orders |
| getUserOrders | 获取用户订单 | GET /api/orders |
| checkRefund | 检查退票条件 | GET /api/orders/{id}/refund-check |
| applyRefund | 申请退票 | POST /api/orders/{id}/refund |
| getUserCoupons | 获取用户优惠券 | GET /api/user-coupons |
| getAvailableCoupons | 获取可领优惠券 | GET /api/coupons/available |

### 3.3 Function Call 示例

**用户**："帮我买一张流浪地球3的票"

**AI处理流程**：
```json
// 1. AI识别意图
{
  "intent": "ticket_book",
  "params": {
    "movieTitle": "流浪地球3",
    "quantity": 1
  }
}

// 2. AI调用 searchMovies
GET /api/movies/search?keyword=流浪地球3

// 3. AI调用 getCinemas（获取有排片的影院）
GET /api/movies/1/cinemas?city=上海&date=2025-05-02

// 4. AI返回让用户选择影院
{
  "reply": "《流浪地球3》在以下影院有排片：\n1. 万达影城（XX店）- 距离 1.2km\n2. CGV影城（XX店）- 距离 2.5km\n\n请问您选择哪个影院？",
  "actions": [
    {
      "type": "show_cinemas",
      "data": [...]
    }
  ]
}

// 5. 用户选择后，继续获取排片、座位...
```

---

## 4. 智能购票完整流程

### 4.1 购票流程接口调用序列

```
1. POST /api/ai/chat          → 用户发起购票请求
2. GET /api/movies/search     → AI搜索电影
3. GET /api/movies/{id}/cinemas → AI获取有排片的影院
4. POST /api/ai/chat          → 用户选择影院
5. GET /api/cinemas/{id}/schedules → AI获取场次
6. POST /api/ai/chat          → 用户选择场次
7. GET /api/schedules/{id}/seats → AI获取座位图
8. POST /api/ai/chat          → 用户选择座位
9. POST /api/schedules/{id}/lock-seats → AI锁定座位
10. POST /api/orders          → AI创建订单
11. POST /api/ai/chat         → AI返回支付信息
```

---

## 5. 会话管理

### 5.1 创建会话

**接口**：`POST /api/ai/sessions`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "session_123",
    "createTime": "2025-05-02 10:00:00"
  }
}
```

### 5.2 获取会话历史

**接口**：`GET /api/ai/sessions/{sessionId}/history`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "session_123",
    "messages": [
      {
        "role": "user",
        "content": "帮我推荐一部科幻片",
        "createTime": "2025-05-02 10:00:00"
      },
      {
        "role": "assistant",
        "content": "好的，为您推荐...",
        "createTime": "2025-05-02 10:00:05"
      }
    ]
  }
}
```

### 5.3 删除会话

**接口**：`DELETE /api/ai/sessions/{sessionId}`

**请求头**：`Authorization: Bearer xxx`

---

## 6. 智能推荐

### 6.1 获取个性化推荐

**接口**：`GET /api/ai/recommendations`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 是 | 类型：movie/cinema/coupon |
| limit | int | 否 | 数量限制，默认10 |

**响应参数（电影推荐）**：
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
        "reason": "根据您的观影偏好推荐"
      }
    ]
  }
}
```

---

## 7. 错误处理

### 7.1 AI 服务错误码

| 错误码 | 含义 |
|--------|------|
| 8001 | AI 服务不可用 |
| 8002 | 意图识别失败 |
| 8003 | 参数提取失败 |
| 8004 | Function Call 执行失败 |
| 8005 | 会话不存在 |
| 8006 | 会话已过期 |

### 7.2 错误响应示例

```json
{
  "code": 8002,
  "message": "意图识别失败，请重新描述您的需求",
  "data": null
}
```

---

## 8. 服务间调用

### 8.1 AI服务调用主服务

AI服务调用主服务时，需在请求头携带：

| Header | 说明 |
|--------|------|
| X-Internal-Call | 内部调用标识 |
| X-User-Id | 用户ID |

**示例**：
```
GET /api/orders HTTP/1.1
Host: main-service
X-Internal-Call: true
X-User-Id: 1001
```

### 8.2 主服务校验

主服务需校验 `X-Internal-Call` 标识，确认是 AI 服务发起的内部调用。

---

*文档版本：v1.0*
*创建时间：2025-05-02*

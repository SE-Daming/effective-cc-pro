# API 接口设计规范

> 猫眼电影票务平台 - RESTful API 设计规范

---

## 1. 基础约定

### 1.1 URL 规范

- **格式**：小写，连字符分隔，名词复数
- **示例**：
  - ✅ `/api/users`, `/api/order-items`
  - ❌ `/api/User`, `/api/orderItems`

### 1.2 HTTP 方法

| 方法 | 用途 | 示例 |
|------|------|------|
| GET | 查询资源 | `GET /api/movies` 获取电影列表 |
| POST | 创建资源 | `POST /api/orders` 创建订单 |
| PUT | 全量更新 | `PUT /api/movies/1` 更新电影信息 |
| PATCH | 部分更新 | `PATCH /api/orders/1/status` 更新订单状态 |
| DELETE | 删除资源 | `DELETE /api/reviews/1` 删除影评 |

### 1.3 统一返回格式

#### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

#### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [ ... ],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

#### 错误响应

```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

### 1.4 错误码定义

| 错误码 | 含义 | 说明 |
|--------|------|------|
| 200 | 成功 | 请求处理成功 |
| 400 | 参数错误 | 请求参数校验失败 |
| 401 | 未授权 | 未登录或 Token 失效 |
| 403 | 禁止访问 | 无权限访问 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 服务器内部错误 |
| 1001 | 用户不存在 | - |
| 1002 | 用户已禁用 | - |
| 2001 | 电影不存在 | - |
| 2002 | 电影已下架 | - |
| 3001 | 影院不存在 | - |
| 3002 | 影院已关闭 | - |
| 4001 | 场次不存在 | - |
| 4002 | 场次已取消 | - |
| 4003 | 座位已被选 | - |
| 4004 | 座位锁定失败 | - |
| 5001 | 订单不存在 | - |
| 5002 | 订单已支付 | - |
| 5003 | 订单已取消 | - |
| 5004 | 订单已过期 | - |
| 5005 | 退票时间不足 | 开场前2小时内不可退 |
| 6001 | 优惠券不存在 | - |
| 6002 | 优惠券已使用 | - |
| 6003 | 优惠券已过期 | - |
| 6004 | 优惠券不满足条件 | - |

### 1.5 请求头规范

| Header | 说明 | 示例 |
|--------|------|------|
| Authorization | 用户 Token | Bearer xxx |
| Content-Type | 内容类型 | application/json |
| X-User-Id | 用户ID（内部调用） | 1001 |

---

## 2. 接口模块划分

| 模块 | 文档 | 说明 |
|------|------|------|
| 用户模块 | [user-api.md](./user-api.md) | 登录、个人信息、搜索历史 |
| 电影模块 | [movie-api.md](./movie-api.md) | 电影列表、详情、影评 |
| 影院模块 | [cinema-api.md](./cinema-api.md) | 影院列表、详情、影厅 |
| 排片模块 | [schedule-api.md](./schedule-api.md) | 排片查询、场次选择 |
| 订单模块 | [order-api.md](./order-api.md) | 选座、下单、支付、退票 |
| 优惠券模块 | [coupon-api.md](./coupon-api.md) | 领券、使用、查询 |
| 卖品模块 | [snack-api.md](./snack-api.md) | 卖品列表、购买 |
| 收藏模块 | [favorite-api.md](./favorite-api.md) | 收藏/取消收藏 |
| 管理员模块 | [admin-api.md](./admin-api.md) | 后台管理接口 |
| AI助手模块 | [ai-api.md](./ai-api.md) | 智能客服、Function Call |

---

## 3. 接口版本管理

- 当前版本：v1
- URL 格式：`/api/v1/xxx`（暂不使用版本前缀，后续迭代再加）

---

## 4. 接口安全

### 4.1 认证方式

- **C端用户**：微信授权登录，返回 Token，后续请求携带 Token
- **B端管理员**：账号密码登录，返回 Token，后续请求携带 Token
- **AI助手调用主服务**：内部服务调用，携带系统标识

### 4.2 敏感操作

以下操作需要二次确认：

- 下单支付：跳转支付页面
- 退票申请：明确告知手续费，用户确认

---

## 5. 接口文档目录

```
docs/design/api/
├── README.md           # 本文件 - API 规范说明
├── user-api.md         # 用户模块接口
├── movie-api.md        # 电影模块接口
├── cinema-api.md       # 影院模块接口
├── schedule-api.md     # 排片模块接口
├── order-api.md        # 订单模块接口
├── coupon-api.md       # 优惠券模块接口
├── snack-api.md        # 卖品模块接口
├── favorite-api.md     # 收藏模块接口
├── admin-api.md        # 管理员模块接口
└── ai-api.md           # AI助手模块接口
```

---

*文档版本：v1.0*
*创建时间：2025-05-02*
*最后更新：2025-05-02*

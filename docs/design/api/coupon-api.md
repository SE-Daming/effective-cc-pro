# 优惠券模块 API

> 领券、使用、查询相关接口

---

## 1. 领券中心

### 1.1 获取可领取优惠券列表

**接口**：`GET /api/coupons/available`

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
        "name": "新用户专享券",
        "type": 1,
        "value": 20.00,
        "minAmount": 100.00,
        "validDays": 30,
        "validStart": "2025-05-02",
        "validEnd": "2025-06-01",
        "totalCount": 1000,
        "receiveCount": 500,
        "limitPerUser": 1,
        "received": false,
        "scope": 1,
        "scopeName": "全部电影"
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10
  }
}
```

### 1.2 领取优惠券

**接口**：`POST /api/coupons/{id}/receive`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userCouponId": 1,
    "name": "新用户专享券",
    "validStart": "2025-05-02 00:00:00",
    "validEnd": "2025-06-01 23:59:59"
  }
}
```

**错误情况**：
- 已领过：`code: 6002`
- 已领完：`code: 6005`
- 不符合条件：`code: 6006`

---

## 2. 我的优惠券

### 2.1 获取用户优惠券列表

**接口**：`GET /api/user-coupons`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int | 否 | 状态：1未使用 2已使用 3已过期 |
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
        "couponId": 1,
        "name": "新用户专享券",
        "type": 1,
        "typeName": "满减券",
        "value": 20.00,
        "minAmount": 100.00,
        "validStart": "2025-05-02 00:00:00",
        "validEnd": "2025-06-01 23:59:59",
        "status": 1,
        "scope": 1,
        "scopeName": "全部电影"
      }
    ],
    "total": 3,
    "page": 1,
    "pageSize": 10
  }
}
```

### 2.2 获取可用优惠券（下单时）

**接口**：`GET /api/user-coupons/usable`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| amount | decimal | 是 | 订单金额 |
| movieId | long | 否 | 电影ID |
| cinemaId | long | 否 | 影院ID |

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "满200减20",
        "type": 1,
        "value": 20.00,
        "minAmount": 200.00,
        "discountAmount": 20.00,
        "validEnd": "2025-06-01 23:59:59"
      }
    ],
    "bestCoupon": {
      "id": 1,
      "name": "满200减20",
      "discountAmount": 20.00
    }
  }
}
```

---

## 3. 优惠券详情

### 3.1 获取优惠券详情

**接口**：`GET /api/coupons/{id}`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "新用户专享券",
    "type": 1,
    "typeName": "满减券",
    "value": 20.00,
    "minAmount": 100.00,
    "totalCount": 1000,
    "receiveCount": 500,
    "usedCount": 100,
    "limitPerUser": 1,
    "validDays": 30,
    "validStart": "2025-05-02",
    "validEnd": "2025-06-01",
    "scope": 1,
    "scopeName": "全部电影",
    "scopeIds": null,
    "status": 1,
    "description": "满100元可用，全场通用"
  }
}
```

---

## 4. 优惠券类型说明

| type | 名称 | 说明 |
|------|------|------|
| 1 | 满减券 | 满 minAmount 元减 value 元 |
| 2 | 折扣券 | value 为折扣（如 0.8 表示 8 折） |
| 3 | 立减券 | 无门槛，直接减 value 元 |
| 4 | 兑换券 | 免费兑换，value 为兑换金额上限 |

| scope | 名称 | 说明 |
|-------|------|------|
| 1 | 全部 | 全部电影/影院可用 |
| 2 | 指定电影 | scopeIds 为电影ID列表 |
| 3 | 指定影院 | scopeIds 为影院ID列表 |

---

*文档版本：v1.0*
*创建时间：2025-05-02*

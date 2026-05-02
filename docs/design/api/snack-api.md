# 卖品模块 API

> 卖品列表、购买相关接口

---

## 1. 卖品分类

### 1.1 获取卖品分类列表

**接口**：`GET /api/snack-categories`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "爆米花",
        "icon": "图标URL",
        "sort": 1
      },
      {
        "id": 2,
        "name": "饮料",
        "icon": "图标URL",
        "sort": 2
      }
    ]
  }
}
```

---

## 2. 卖品列表

### 2.1 获取卖品列表

**接口**：`GET /api/snacks`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | int | 否 | 分类ID |
| keyword | string | 否 | 搜索关键词 |
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
        "categoryId": 1,
        "categoryName": "爆米花",
        "name": "经典爆米花",
        "image": "图片URL",
        "description": "香甜可口",
        "price": 25.00,
        "specs": [
          {
            "name": "小份",
            "price": 25.00
          },
          {
            "name": "中份",
            "price": 35.00
          },
          {
            "name": "大份",
            "price": 45.00
          }
        ],
        "stock": 100,
        "sales": 500,
        "status": 1
      }
    ],
    "total": 20,
    "page": 1,
    "pageSize": 10
  }
}
```

### 2.2 获取卖品详情

**接口**：`GET /api/snacks/{id}`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "categoryId": 1,
    "categoryName": "爆米花",
    "name": "经典爆米花",
    "image": "图片URL",
    "description": "香甜可口，粒粒饱满",
    "price": 25.00,
    "specs": [
      {
        "name": "小份",
        "price": 25.00,
        "stock": 50
      },
      {
        "name": "中份",
        "price": 35.00,
        "stock": 30
      },
      {
        "name": "大份",
        "price": 45.00,
        "stock": 20
      }
    ],
    "stock": 100,
    "sales": 500,
    "status": 1
  }
}
```

---

## 3. 卖品购买

### 3.1 创建卖品订单

**接口**：`POST /api/snack-orders`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "cinemaId": 1,
  "items": [
    {
      "snackId": 1,
      "spec": "大份",
      "quantity": 2
    },
    {
      "snackId": 5,
      "spec": "中杯",
      "quantity": 2
    }
  ],
  "userCouponId": 1
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": 10,
    "orderNo": "2025050215000001",
    "totalAmount": 140.00,
    "discountAmount": 20.00,
    "payAmount": 120.00,
    "expireTime": "2025-05-02 15:05:00",
    "payInfo": {
      "appId": "wx***",
      "timeStamp": "1714632000",
      "nonceStr": "xxx",
      "package": "prepay_id=xxx",
      "signType": "RSA",
      "paySign": "xxx"
    }
  }
}
```

### 3.2 获取卖品订单详情

**接口**：`GET /api/snack-orders/{id}`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "orderNo": "2025050215000001",
    "type": 2,
    "status": 2,
    "cinemaId": 1,
    "cinemaName": "万达影城（XX店）",
    "cinemaAddress": "XX区XX路XX号",
    "totalAmount": 140.00,
    "discountAmount": 20.00,
    "payAmount": 120.00,
    "payTime": "2025-05-02 15:01:00",
    "createTime": "2025-05-02 15:00:00",
    "items": [
      {
        "id": 1,
        "snackId": 1,
        "snackName": "经典爆米花",
        "spec": "大份",
        "quantity": 2,
        "price": 45.00,
        "totalPrice": 90.00,
        "pickCode": "998877",
        "status": 1
      }
    ],
    "coupon": {
      "id": 1,
      "name": "满100减20",
      "value": 20.00
    }
  }
}
```

---

## 4. 卖品订单列表

### 4.1 获取卖品订单列表

**接口**：`GET /api/snack-orders`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
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
        "id": 10,
        "orderNo": "2025050215000001",
        "status": 2,
        "totalAmount": 140.00,
        "payAmount": 120.00,
        "cinemaName": "万达影城（XX店）",
        "createTime": "2025-05-02 15:00:00",
        "itemCount": 4
      }
    ],
    "total": 5,
    "page": 1,
    "pageSize": 10
  }
}
```

---

*文档版本：v1.0*
*创建时间：2025-05-02*

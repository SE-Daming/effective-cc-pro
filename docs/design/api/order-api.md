# 订单模块 API

> 选座、下单、支付、退票相关接口

---

## 1. 订单创建

### 1.1 创建订单（电影票）

**接口**：`POST /api/orders`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "lockId": "lock_202505021430001",
  "type": 1,
  "userCouponId": 1,
  "remark": "备注"
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": 1,
    "orderNo": "2025050214300001",
    "totalAmount": 204.00,
    "discountAmount": 20.00,
    "payAmount": 184.00,
    "expireTime": "2025-05-02 14:35:00",
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

### 1.2 创建组合订单（电影票+卖品）

**接口**：`POST /api/orders/combo`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "lockId": "lock_202505021430001",
  "type": 3,
  "snacks": [
    {
      "snackId": 1,
      "spec": "大份",
      "quantity": 1
    }
  ],
  "userCouponId": 1
}
```

**响应参数**：同上

---

## 2. 订单支付

### 2.1 获取支付信息

**接口**：`GET /api/orders/{id}/pay-info`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": 1,
    "orderNo": "2025050214300001",
    "payAmount": 184.00,
    "expireTime": "2025-05-02 14:35:00",
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

### 2.2 支付回调（微信回调）

**接口**：`POST /api/orders/pay-notify`

**说明**：微信支付回调接口，由微信服务器调用

### 2.3 取消订单

**接口**：`POST /api/orders/{id}/cancel`

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

## 3. 订单查询

### 3.1 获取订单列表

**接口**：`GET /api/orders`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int | 否 | 订单状态 |
| type | int | 否 | 订单类型 |
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
        "orderNo": "2025050214300001",
        "type": 1,
        "status": 2,
        "totalAmount": 204.00,
        "payAmount": 184.00,
        "createTime": "2025-05-02 14:30:00",
        "tickets": [
          {
            "movieTitle": "流浪地球3",
            "cinemaName": "万达影城（XX店）",
            "hallName": "1号IMAX厅",
            "showDate": "2025-05-02",
            "showTime": "14:30",
            "seatNo": "A01"
          }
        ]
      }
    ],
    "total": 10,
    "page": 1,
    "pageSize": 10
  }
}
```

### 3.2 获取订单详情

**接口**：`GET /api/orders/{id}`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "orderNo": "2025050214300001",
    "type": 1,
    "status": 2,
    "totalAmount": 204.00,
    "discountAmount": 20.00,
    "payAmount": 184.00,
    "payTime": "2025-05-02 14:31:00",
    "payType": "wechat",
    "createTime": "2025-05-02 14:30:00",
    "expireTime": "2025-05-02 14:35:00",
    "tickets": [
      {
        "id": 1,
        "scheduleId": 1,
        "movieTitle": "流浪地球3",
        "moviePoster": "海报URL",
        "cinemaName": "万达影城（XX店）",
        "cinemaAddress": "XX区XX路XX号",
        "hallName": "1号IMAX厅",
        "showDate": "2025-05-02",
        "showTime": "14:30",
        "seatNo": "A01",
        "ticketPrice": 68.00,
        "pickCode": "886655",
        "pickQrcode": "二维码URL",
        "status": 1
      }
    ],
    "snacks": [],
    "coupon": {
      "id": 1,
      "name": "满200减20",
      "value": 20.00
    }
  }
}
```

---

## 4. 取票

### 4.1 获取取票码

**接口**：`GET /api/orders/{id}/pick-code`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "pickCode": "886655",
    "pickQrcode": "二维码URL",
    "tickets": [
      {
        "seatNo": "A01",
        "hallName": "1号IMAX厅"
      }
    ]
  }
}
```

---

## 5. 退票

### 5.1 检查退票条件

**接口**：`GET /api/orders/{id}/refund-check`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "canRefund": true,
    "refundAmount": 184.00,
    "refundFee": 18.40,
    "actualRefund": 165.60,
    "reason": "开场前退票收取10%手续费"
  }
}
```

或不可退票：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "canRefund": false,
    "reason": "开场前2小时内不可退票"
  }
}
```

### 5.2 申请退票

**接口**：`POST /api/orders/{id}/refund`

**请求头**：`Authorization: Bearer xxx`

**请求参数**：
```json
{
  "reason": "临时有事无法观影"
}
```

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "refundId": 1,
    "refundAmount": 165.60,
    "refundFee": 18.40,
    "status": 6,
    "message": "退票申请已提交，等待审核"
  }
}
```

---

## 6. 卖品订单

### 6.1 获取卖品取货码

**接口**：`GET /api/orders/{id}/snack-pick-code`

**请求头**：`Authorization: Bearer xxx`

**响应参数**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "pickCode": "998877",
    "snacks": [
      {
        "name": "双人爆米花套餐",
        "spec": "大份",
        "quantity": 1
      }
    ]
  }
}
```

---

*文档版本：v1.0*
*创建时间：2025-05-02*

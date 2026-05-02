# 订单模块开发日志

> 记录订单模块（C端+B端）的开发过程

---

## 开发信息

| 项目 | 内容 |
|------|------|
| 模块名称 | 订单模块 |
| 开发时间 | 2025-05-02 |
| 开发人员 | Claude |
| 状态 | ✅ 已完成 |

---

## 功能清单

### C端接口（10个）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建订单 | POST | /api/orders | 创建电影票订单 |
| 创建组合订单 | POST | /api/orders/combo | 创建电影票+卖品组合订单 |
| 获取支付信息 | GET | /api/orders/{id}/pay-info | 获取订单支付信息 |
| 取消订单 | POST | /api/orders/{id}/cancel | 取消待支付订单 |
| 获取订单列表 | GET | /api/orders | 获取当前用户订单列表 |
| 获取订单详情 | GET | /api/orders/{id} | 获取订单详细信息 |
| 获取取票码 | GET | /api/orders/{id}/pick-code | 获取电影票取票码 |
| 检查退票条件 | GET | /api/orders/{id}/refund-check | 检查是否可退票 |
| 申请退票 | POST | /api/orders/{id}/refund | 提交退票申请 |
| 卖品取货码 | GET | /api/orders/{id}/snack-pick-code | 获取卖品取货码 |

### B端接口（3个）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 订单列表 | GET | /api/admin/orders | 分页查询订单 |
| 订单详情 | GET | /api/admin/orders/{id} | 获取订单详情 |
| 退款审核 | POST | /api/admin/orders/{id}/refund-audit | 审核退款申请 |

---

## 业务规则实现

### 1. 座位锁定规则
- 锁定时长：5分钟
- 超时自动释放
- 锁定状态：锁定(1)、已售(2)、释放(3)

### 2. 退票规则
- 开场前2小时内不可退票
- 开场前24小时以上：无手续费
- 开场前2-24小时：收取10%手续费
- 优惠券不退回

### 3. 订单状态流转
```
待支付(1) → 已支付(2) → 已出票(3) → 已完成(4)
    ↓           ↓
 已取消(5)   退款中(6) → 已退款(7)
```

---

## 文件结构

```
backend/main-service/src/main/java/com/maoyan/
├── controller/
│   ├── api/order/
│   │   └── OrderController.java          # C端订单控制器
│   └── admin/order/
│       └── AdminOrderController.java     # B端订单管理控制器
├── service/
│   ├── OrderService.java                 # 订单服务接口
│   └── impl/
│       └── OrderServiceImpl.java         # 订单服务实现类
├── dto/order/
│   ├── OrderCreateDTO.java               # 创建订单请求
│   ├── ComboOrderCreateDTO.java          # 创建组合订单请求
│   ├── OrderQueryDTO.java                # 订单查询条件
│   ├── AdminOrderQueryDTO.java           # B端订单查询条件
│   ├── RefundApplyDTO.java               # 退票申请请求
│   └── RefundAuditDTO.java               # 退款审核请求
└── vo/order/
    ├── OrderCreateVO.java                # 创建订单响应
    ├── OrderDetailVO.java                # 订单详情
    ├── OrderListItemVO.java              # 订单列表项
    ├── PayInfoVO.java                    # 支付信息
    ├── PickCodeVO.java                   # 取票码响应
    ├── RefundCheckVO.java                # 退票检查响应
    ├── RefundApplyVO.java                # 退票申请响应
    ├── RefundAuditVO.java                # 退款审核响应
    └── SnackPickCodeVO.java              # 卖品取货码响应
```

---

## 技术要点

### 1. 事务管理
使用 `@Transactional` 确保订单创建、座位锁定、优惠券使用等操作的原子性。

### 2. 优惠券计算
支持四种优惠券类型：
- 满减券：满足金额后减免
- 折扣券：按折扣率计算
- 立减券：无门槛减免
- 兑换券：全额抵扣

### 3. 锁定ID解析
锁定ID格式：`lock_{scheduleId}_{seatId1-seatId2-...}`

### 4. 取票码生成
6位随机数字，确保唯一性。

---

## 自测记录

| 测试项 | 结果 |
|--------|------|
| 编译通过 | ✅ |
| 代码规范 | ✅ |

---

## 备注

1. 用户认证暂时使用请求头 `X-User-Id` 传递，后续需集成 JWT
2. 支付信息目前为模拟数据，后续需对接微信支付
3. 订单超时取消功能需要定时任务实现（待开发）

---

*创建时间：2025-05-02*

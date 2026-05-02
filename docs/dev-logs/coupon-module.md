# 优惠券模块开发日志

> 2025-05-02

---

## 开发内容

### 1. 模块概述

开发优惠券模块，包括 C 端和 B 端接口。

### 2. 技术实现

#### 2.1 Mapper 层

- `CouponMapper` - 优惠券模板 Mapper
- `UserCouponMapper` - 用户优惠券 Mapper

#### 2.2 DTO 层

- `AvailableCouponQueryDTO` - 可领取优惠券查询参数
- `UserCouponQueryDTO` - 用户优惠券查询参数
- `UsableCouponQueryDTO` - 可用优惠券查询参数（下单时）
- `CouponSaveDTO` - 优惠券创建/更新参数
- `AdminCouponQueryDTO` - 优惠券管理查询参数
- `CouponDistributeDTO` - 优惠券发放参数

#### 2.3 VO 层

- `AvailableCouponVO` - 可领取优惠券
- `UserCouponVO` - 用户优惠券
- `UsableCouponVO` - 可用优惠券
- `UsableCouponListVO` - 可用优惠券列表（含最佳优惠券）
- `BestCouponVO` - 最佳优惠券
- `CouponDetailVO` - 优惠券详情
- `AdminCouponVO` - 管理端优惠券列表项
- `ReceiveCouponVO` - 领取优惠券结果

#### 2.4 Service 层

**CouponService / CouponServiceImpl**

C 端接口：
- `getAvailableCoupons` - 获取可领取优惠券列表
- `receiveCoupon` - 领取优惠券
- `getCouponDetail` - 获取优惠券详情
- `getUserCoupons` - 获取用户优惠券列表
- `getUsableCoupons` - 获取可用优惠券（下单时）

B 端接口：
- `getAdminCouponList` - 获取优惠券列表
- `createCoupon` - 创建优惠券
- `updateCoupon` - 更新优惠券
- `deleteCoupon` - 删除优惠券
- `distributeCoupon` - 发放优惠券

#### 2.5 Controller 层

**CouponController（C端）**

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/coupons/available` | GET | 获取可领取优惠券列表 |
| `/api/coupons/{id}/receive` | POST | 领取优惠券 |
| `/api/coupons/{id}` | GET | 获取优惠券详情 |
| `/api/coupons/user-coupons` | GET | 获取用户优惠券列表 |
| `/api/coupons/usable` | GET | 获取可用优惠券 |

**AdminCouponController（B端）**

| 接口 | 方法 | 说明 |
|------|------|------|
| `/admin/coupons` | GET | 获取优惠券列表 |
| `/admin/coupons` | POST | 新增优惠券 |
| `/admin/coupons/{id}` | PUT | 更新优惠券 |
| `/admin/coupons/{id}` | DELETE | 删除优惠券 |
| `/admin/coupons/{id}/distribute` | POST | 发放优惠券 |

### 3. 业务规则实现

#### 3.1 优惠券类型

| 类型 | 编码 | 说明 |
|------|------|------|
| 满减券 | 1 | 满 minAmount 元减 value 元 |
| 折扣券 | 2 | value 为折扣（如 0.8 表示 8 折） |
| 立减券 | 3 | 无门槛，直接减 value 元 |
| 兑换券 | 4 | 免费兑换，value 为兑换金额上限 |

#### 3.2 适用范围

| 范围 | 编码 | 说明 |
|------|------|------|
| 全部 | 1 | 全部电影/影院可用 |
| 指定电影 | 2 | scopeIds 为电影ID列表 |
| 指定影院 | 3 | scopeIds 为影院ID列表 |

#### 3.3 优惠券状态

| 状态 | 编码 | 说明 |
|------|------|------|
| 未使用 | 1 | 可使用 |
| 已使用 | 2 | 已核销 |
| 已过期 | 3 | 超过有效期 |

#### 3.4 核心业务逻辑

1. **领取优惠券**
   - 检查优惠券是否存在、是否启用
   - 检查是否在有效期内
   - 检查库存是否充足
   - 检查用户领取次数是否超限
   - 计算有效期（固定日期 或 领取后N天）
   - 创建用户优惠券记录

2. **可用优惠券查询**
   - 自动更新过期优惠券状态
   - 过滤满足使用条件的优惠券
   - 检查适用范围（全部/指定电影/指定影院）
   - 计算每种优惠券的优惠金额
   - 推荐最佳优惠券

3. **优惠金额计算**
   - 满减券：满额后减免固定金额
   - 折扣券：按折扣计算优惠金额
   - 立减券：直接减免
   - 兑换券：订单金额与上限取小值
   - 优惠金额不超过订单金额

4. **发放优惠券**
   - 支持指定用户列表发放
   - 支持发放给所有用户
   - 自动跳过已发放用户

### 4. 枚举扩展

为 `CouponType` 和 `CouponStatus` 枚举添加了 `getByCode` 静态方法。

---

## 自测记录

**模块**：优惠券模块

**测试项**：
- ✅ 编译通过
- ✅ C端接口定义正确
- ✅ B端接口定义正确
- ✅ 业务规则验证逻辑完整

**结果**：通过

---

## 关键文件

```
backend/main-service/src/main/java/com/maoyan/
├── mapper/
│   ├── CouponMapper.java
│   └── UserCouponMapper.java
├── dto/coupon/
│   ├── AvailableCouponQueryDTO.java
│   ├── UserCouponQueryDTO.java
│   ├── UsableCouponQueryDTO.java
│   ├── CouponSaveDTO.java
│   ├── AdminCouponQueryDTO.java
│   └── CouponDistributeDTO.java
├── vo/coupon/
│   ├── AvailableCouponVO.java
│   ├── UserCouponVO.java
│   ├── UsableCouponVO.java
│   ├── UsableCouponListVO.java
│   ├── BestCouponVO.java
│   ├── CouponDetailVO.java
│   ├── AdminCouponVO.java
│   └── ReceiveCouponVO.java
├── service/
│   ├── CouponService.java
│   └── impl/CouponServiceImpl.java
├── controller/api/
│   └── CouponController.java
├── controller/admin/
│   └── AdminCouponController.java
└── common/enums/
    ├── CouponType.java（扩展）
    └── CouponStatus.java（扩展）
```

---

*开发时间：2025-05-02*

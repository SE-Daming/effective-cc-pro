# 数据库设计文档

> 数据库名称：maoyan-may2

---

## 1. 数据库概述

### 1.1 数据库信息

| 项目 | 值 |
|------|------|
| 数据库名 | maoyan-may2 |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_general_ci |
| 存储引擎 | InnoDB |

### 1.2 表清单

| 模块 | 表名 | 说明 |
|------|------|------|
| **用户模块** | user | 用户表 |
| | member_level | 会员等级配置表 |
| | user_points_log | 用户积分流水表 |
| | search_history | 搜索历史表 |
| **电影模块** | movie | 电影表 |
| | movie_actor | 电影演员表 |
| | movie_category | 电影分类表 |
| | movie_review | 影评表 |
| **影院模块** | cinema_brand | 影院品牌表 |
| | cinema | 影院表 |
| | hall | 影厅表 |
| | seat | 座位表 |
| **排片模块** | schedule | 排片表 |
| **订单模块** | order | 订单表 |
| | order_ticket | 订单票务明细表 |
| | order_snack | 订单卖品明细表 |
| | seat_lock | 座位锁定表 |
| **优惠券模块** | coupon | 优惠券模板表 |
| | user_coupon | 用户优惠券表 |
| **卖品模块** | snack | 卖品表 |
| | snack_category | 卖品分类表 |
| **收藏模块** | user_favorite | 用户收藏表 |
| **消息模块** | message_template | 消息模板表 |
| | message_log | 消息发送记录表 |
| **管理员模块** | admin | 管理员表 |
| | admin_role | 角色表 |
| | admin_operation_log | 操作日志表 |
| **其他** | banner | 轮播图表 |

---

## 2. ER 图

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    user     │     │    movie    │     │   cinema    │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id          │     │ id          │     │ id          │
│ openid      │     │ title       │     │ name        │
│ nickname    │     │ poster      │     │ address     │
│ avatar      │     │ director    │     │ phone       │
│ phone       │     │ duration    │     │ brand_id    │
│ level_id    │──┐  │ status      │  ┌──│ location    │
│ points      │  │  │ release_date│  │  │ facilities  │
│ ...         │  │  │ ...         │  │  │ ...         │
└─────────────┘  │  └─────────────┘  │  └─────────────┘
       │         │         │         │         │
       │         │         │         │         │
       ▼         │         ▼         │         ▼
┌─────────────┐  │  ┌─────────────┐  │  ┌─────────────┐
│member_level │◄─┘  │movie_review │  │  │    hall     │
├─────────────┤     ├─────────────┤  │  ├─────────────┤
│ id          │     │ id          │  │  │ id          │
│ name        │     │ user_id     │  │  │ cinema_id   │◄─┘
│ min_amount  │     │ movie_id    │  │  │ name        │
│ discount    │     │ score       │  │  │ type        │
│ ...         │     │ content     │  │  │ rows        │
└─────────────┘     │ ...         │  │  │ cols        │
                    └─────────────┘  │  │ ...         │
                                     │  └─────────────┘
                                     │         │
                                     │         │
                                     ▼         ▼
                              ┌─────────────────┐
                              │    schedule     │
                              ├─────────────────┤
                              │ id              │
                              │ movie_id        │
                              │ cinema_id       │
                              │ hall_id         │
                              │ show_date       │
                              │ show_time       │
                              │ price           │
                              │ ...             │
                              └─────────────────┘
                                     │
                                     │
                                     ▼
┌─────────────┐     ┌─────────────────────────────────┐
│    order    │────▶│         order_ticket            │
├─────────────┤     ├─────────────────────────────────┤
│ id          │     │ id                              │
│ order_no    │     │ order_id                        │
│ user_id     │     │ schedule_id                     │
│ type        │     │ seat_id                         │
│ total_amount│     │ ticket_price                    │
│ status      │     │ pick_code                       │
│ ...         │     │ ...                             │
└─────────────┘     └─────────────────────────────────┘
       │
       │
       ▼
┌─────────────────┐
│  order_snack    │
├─────────────────┤
│ id              │
│ order_id        │
│ snack_id        │
│ quantity        │
│ price           │
│ ...             │
└─────────────────┘
```

---

## 3. 表结构详细设计

### 3.1 用户模块

#### user - 用户表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| openid | varchar(64) | NO | - | 微信openid |
| union_id | varchar(64) | YES | NULL | 微信union_id |
| nickname | varchar(64) | YES | NULL | 昵称 |
| avatar | varchar(255) | YES | NULL | 头像URL |
| phone | varchar(20) | YES | NULL | 手机号 |
| gender | tinyint | YES | 0 | 性别：0未知 1男 2女 |
| birthday | date | YES | NULL | 生日 |
| level_id | int | YES | 1 | 会员等级ID |
| points | int | YES | 0 | 当前积分 |
| total_points | int | YES | 0 | 累计积分 |
| total_consumption | decimal(10,2) | YES | 0.00 | 累计消费金额 |
| level_expire_time | datetime | YES | NULL | 等级过期时间 |
| last_login_time | datetime | YES | NULL | 最后登录时间 |
| status | tinyint | YES | 1 | 状态：0禁用 1正常 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- PRIMARY KEY (id)
- UNIQUE KEY uk_openid (openid)
- KEY idx_phone (phone)

#### member_level - 会员等级配置表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | int | NO | AUTO_INCREMENT | 主键 |
| name | varchar(32) | NO | - | 等级名称 |
| min_consumption | decimal(10,2) | NO | - | 升级最低消费金额 |
| discount | decimal(3,2) | YES | 1.00 | 折扣（1.00=无折扣） |
| points_rate | decimal(3,2) | YES | 1.00 | 积分倍率 |
| retention_amount | decimal(10,2) | YES | 0.00 | 保级消费金额 |
| benefits | text | YES | NULL | 权益说明JSON |
| icon | varchar(255) | YES | NULL | 等级图标 |
| sort | int | YES | 0 | 排序 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |

#### user_points_log - 用户积分流水表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| user_id | bigint | NO | - | 用户ID |
| type | tinyint | NO | - | 类型：1获取 2消费 3过期 |
| points | int | NO | - | 积分数量（正数） |
| balance | int | YES | - | 变更后余额 |
| source | varchar(32) | YES | NULL | 来源：ORDER/REVIEW/LOGIN等 |
| source_id | bigint | YES | NULL | 关联ID |
| remark | varchar(255) | YES | NULL | 备注 |
| expire_time | datetime | YES | NULL | 过期时间 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- KEY idx_user_id (user_id)
- KEY idx_create_time (create_time)

---

### 3.2 电影模块

#### movie - 电影表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| title | varchar(128) | NO | - | 电影名称 |
| english_title | varchar(128) | YES | NULL | 英文名称 |
| poster | varchar(255) | NO | - | 海报URL |
| director | varchar(64) | YES | NULL | 导演 |
| actors | varchar(500) | YES | NULL | 主演（逗号分隔） |
| duration | int | YES | NULL | 时长（分钟） |
| category_ids | varchar(64) | YES | NULL | 分类ID（逗号分隔） |
| region | varchar(32) | YES | NULL | 地区 |
| language | varchar(32) | YES | NULL | 语言 |
| release_date | date | YES | NULL | 上映日期 |
| off_date | date | YES | NULL | 下映日期 |
| synopsis | text | YES | NULL | 剧情简介 |
| trailer_url | varchar(255) | YES | NULL | 预告片URL |
| score | decimal(2,1) | YES | NULL | 评分（0-10） |
| score_count | int | YES | 0 | 评分人数 |
| status | tinyint | YES | 1 | 状态：1即将上映 2上映中 3下架 |
| sort | int | YES | 0 | 排序 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- KEY idx_status (status)
- KEY idx_release_date (release_date)

#### movie_category - 电影分类表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | int | NO | AUTO_INCREMENT | 主键 |
| name | varchar(32) | NO | - | 分类名称 |
| sort | int | YES | 0 | 排序 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

#### movie_actor - 电影演员表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| movie_id | bigint | NO | - | 电影ID |
| name | varchar(64) | NO | - | 演员姓名 |
| role | varchar(32) | YES | NULL | 角色：主演/配角/导演等 |
| sort | int | YES | 0 | 排序 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- KEY idx_movie_id (movie_id)

#### movie_review - 影评表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| user_id | bigint | NO | - | 用户ID |
| movie_id | bigint | NO | - | 电影ID |
| score | tinyint | NO | - | 评分（1-10） |
| content | text | YES | NULL | 影评内容 |
| like_count | int | YES | 0 | 点赞数 |
| status | tinyint | YES | 1 | 状态：0隐藏 1显示 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- KEY idx_movie_id (movie_id)
- KEY idx_user_id (user_id)

---

### 3.3 影院模块

#### cinema_brand - 影院品牌表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | int | NO | AUTO_INCREMENT | 主键 |
| name | varchar(64) | NO | - | 品牌名称 |
| logo | varchar(255) | YES | NULL | 品牌Logo |
| description | varchar(255) | YES | NULL | 品牌介绍 |
| sort | int | YES | 0 | 排序 |
| status | tinyint | YES | 1 | 状态 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

#### cinema - 影院表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| name | varchar(128) | NO | - | 影院名称 |
| brand_id | int | YES | NULL | 品牌ID |
| province | varchar(32) | YES | NULL | 省 |
| city | varchar(32) | YES | NULL | 市 |
| district | varchar(32) | YES | NULL | 区 |
| address | varchar(255) | YES | NULL | 详细地址 |
| longitude | decimal(10,6) | YES | NULL | 经度 |
| latitude | decimal(10,6) | YES | NULL | 纬度 |
| phone | varchar(20) | YES | NULL | 电话 |
| facilities | varchar(255) | YES | NULL | 设施标签（逗号分隔） |
| description | text | YES | NULL | 影院介绍 |
| images | text | YES | NULL | 图片URL（JSON数组） |
| score | decimal(2,1) | YES | NULL | 评分 |
| status | tinyint | YES | 1 | 状态：0关闭 1营业 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- KEY idx_city (city)
- KEY idx_location (longitude, latitude)

#### hall - 影厅表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| cinema_id | bigint | NO | - | 影院ID |
| name | varchar(32) | NO | - | 影厅名称 |
| type | varchar(32) | YES | NULL | 类型：普通/IMAX/Dolby等 |
| rows | int | NO | - | 行数 |
| cols | int | NO | - | 列数 |
| total_seats | int | NO | - | 总座位数 |
| seat_layout | text | YES | NULL | 座位布局JSON |
| status | tinyint | YES | 1 | 状态：0关闭 1正常 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- KEY idx_cinema_id (cinema_id)

#### seat - 座位表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| hall_id | bigint | NO | - | 影厅ID |
| row_num | int | NO | - | 行号 |
| col_num | int | NO | - | 列号 |
| seat_no | varchar(10) | NO | - | 座位号（如：F08） |
| seat_type | tinyint | YES | 1 | 类型：1普通 2情侣 3VIP |
| status | tinyint | YES | 1 | 状态：0禁用 1正常 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- UNIQUE KEY uk_hall_seat (hall_id, row_num, col_num)

---

### 3.4 排片模块

#### schedule - 排片表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| movie_id | bigint | NO | - | 电影ID |
| cinema_id | bigint | NO | - | 影院ID |
| hall_id | bigint | NO | - | 影厅ID |
| show_date | date | NO | - | 放映日期 |
| show_time | time | NO | - | 放映时间 |
| end_time | time | YES | NULL | 结束时间 |
| clean_duration | int | YES | 15 | 清洁时间（分钟） |
| price | decimal(6,2) | NO | - | 票价 |
| member_price | decimal(6,2) | YES | NULL | 会员价 |
| total_seats | int | NO | - | 总座位数 |
| sold_seats | int | YES | 0 | 已售座位数 |
| status | tinyint | YES | 1 | 状态：0取消 1正常 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- KEY idx_movie_cinema (movie_id, cinema_id)
- KEY idx_show_date (show_date)
- KEY idx_cinema_date (cinema_id, show_date)

---

### 3.5 订单模块

#### order - 订单表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| order_no | varchar(32) | NO | - | 订单编号 |
| user_id | bigint | NO | - | 用户ID |
| type | tinyint | NO | - | 类型：1电影票 2卖品 3组合 |
| cinema_id | bigint | YES | NULL | 影院ID |
| total_amount | decimal(10,2) | NO | - | 订单总额 |
| discount_amount | decimal(10,2) | YES | 0.00 | 优惠金额 |
| points_amount | decimal(10,2) | YES | 0.00 | 积分抵扣金额 |
| pay_amount | decimal(10,2) | NO | - | 实付金额 |
| user_coupon_id | bigint | YES | NULL | 使用的用户优惠券ID |
| use_points | int | YES | 0 | 使用积分数 |
| status | tinyint | NO | 1 | 状态 |
| pay_time | datetime | YES | NULL | 支付时间 |
| pay_type | varchar(20) | YES | NULL | 支付方式 |
| pay_trade_no | varchar(64) | YES | NULL | 第三方交易号 |
| refund_amount | decimal(10,2) | YES | NULL | 退款金额 |
| refund_fee | decimal(10,2) | YES | NULL | 退票手续费 |
| refund_time | datetime | YES | NULL | 退款时间 |
| refund_reason | varchar(255) | YES | NULL | 退款原因 |
| refund_auditor_id | int | YES | NULL | 退款审核人ID |
| refund_audit_time | datetime | YES | NULL | 退款审核时间 |
| refund_audit_remark | varchar(255) | YES | NULL | 退款审核备注 |
| expire_time | datetime | YES | NULL | 过期时间 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**订单状态说明：**
| 值 | 状态 |
|------|------|
| 1 | 待支付 |
| 2 | 已支付 |
| 3 | 已出票 |
| 4 | 已完成 |
| 5 | 已取消 |
| 6 | 退款中 |
| 7 | 已退款 |

**索引：**
- UNIQUE KEY uk_order_no (order_no)
- KEY idx_user_id (user_id)
- KEY idx_status (status)
- KEY idx_create_time (create_time)

#### order_ticket - 订单票务明细表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| order_id | bigint | NO | - | 订单ID |
| schedule_id | bigint | NO | - | 场次ID |
| seat_id | bigint | NO | - | 座位ID |
| seat_no | varchar(10) | NO | - | 座位号 |
| ticket_price | decimal(6,2) | NO | - | 票价 |
| pick_code | varchar(20) | YES | NULL | 取票码 |
| pick_qrcode | varchar(255) | YES | NULL | 取票二维码 |
| status | tinyint | YES | 1 | 状态：1未取票 2已取票 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- KEY idx_order_id (order_id)
- KEY idx_schedule_seat (schedule_id, seat_id)

#### order_snack - 订单卖品明细表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| order_id | bigint | NO | - | 订单ID |
| snack_id | bigint | NO | - | 卖品ID |
| snack_name | varchar(64) | NO | - | 卖品名称 |
| spec | varchar(32) | YES | NULL | 规格 |
| quantity | int | NO | - | 数量 |
| price | decimal(6,2) | NO | - | 单价 |
| total_price | decimal(6,2) | NO | - | 小计 |
| pick_code | varchar(20) | YES | NULL | 取货码 |
| status | tinyint | YES | 1 | 状态：1未取货 2已取货 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- KEY idx_order_id (order_id)

#### seat_lock - 座位锁定表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| schedule_id | bigint | NO | - | 场次ID |
| seat_id | bigint | NO | - | 座位ID |
| seat_no | varchar(10) | NO | - | 座位号 |
| user_id | bigint | NO | - | 用户ID |
| order_id | bigint | YES | NULL | 订单ID |
| status | tinyint | YES | 1 | 状态：1锁定 2已售 3释放 |
| expire_time | datetime | NO | - | 过期时间 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- UNIQUE KEY uk_schedule_seat (schedule_id, seat_id)
- KEY idx_expire_time (expire_time)

---

### 3.6 优惠券模块

#### coupon - 优惠券模板表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| name | varchar(64) | NO | - | 优惠券名称 |
| type | tinyint | NO | - | 类型：1满减 2折扣 3立减 4兑换 |
| value | decimal(6,2) | YES | NULL | 优惠值（金额/折扣） |
| min_amount | decimal(6,2) | YES | 0.00 | 最低消费金额 |
| total_count | int | YES | 0 | 发放总量 |
| used_count | int | YES | 0 | 已使用数量 |
| receive_count | int | YES | 0 | 已领取数量 |
| limit_per_user | int | YES | 1 | 每人限领数量 |
| valid_days | int | YES | NULL | 有效天数 |
| valid_start | date | YES | NULL | 有效期开始 |
| valid_end | date | YES | NULL | 有效期结束 |
| scope | tinyint | YES | 1 | 适用范围：1全部 2指定电影 3指定影院 |
| scope_ids | varchar(255) | YES | NULL | 适用ID列表（逗号分隔） |
| status | tinyint | YES | 1 | 状态：0禁用 1启用 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- KEY idx_status (status)

#### user_coupon - 用户优惠券表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| user_id | bigint | NO | - | 用户ID |
| coupon_id | bigint | NO | - | 优惠券ID |
| coupon_name | varchar(64) | NO | - | 优惠券名称（冗余） |
| type | tinyint | NO | - | 类型 |
| value | decimal(6,2) | YES | NULL | 优惠值 |
| min_amount | decimal(6,2) | YES | 0.00 | 最低消费 |
| valid_start | datetime | NO | - | 有效期开始 |
| valid_end | datetime | NO | - | 有效期结束 |
| status | tinyint | YES | 1 | 状态：1未使用 2已使用 3已过期 |
| use_time | datetime | YES | NULL | 使用时间 |
| order_id | bigint | YES | NULL | 使用的订单ID |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- KEY idx_user_status (user_id, status)
- KEY idx_valid_end (valid_end)

---

### 3.7 卖品模块

#### snack_category - 卖品分类表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | int | NO | AUTO_INCREMENT | 主键 |
| name | varchar(32) | NO | - | 分类名称 |
| icon | varchar(255) | YES | NULL | 图标 |
| sort | int | YES | 0 | 排序 |
| status | tinyint | YES | 1 | 状态 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

#### snack - 卖品表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| category_id | int | NO | - | 分类ID |
| name | varchar(64) | NO | - | 卖品名称 |
| image | varchar(255) | YES | NULL | 图片 |
| description | varchar(255) | YES | NULL | 描述 |
| specs | text | YES | NULL | 规格JSON |
| price | decimal(6,2) | NO | - | 价格 |
| stock | int | YES | 0 | 库存 |
| sales | int | YES | 0 | 销量 |
| status | tinyint | YES | 1 | 状态：0下架 1上架 |
| sort | int | YES | 0 | 排序 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- KEY idx_category_id (category_id)

---

### 3.8 收藏模块

#### user_favorite - 用户收藏表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| user_id | bigint | NO | - | 用户ID |
| target_type | tinyint | NO | - | 类型：1电影 2影院 |
| target_id | bigint | NO | - | 目标ID |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- UNIQUE KEY uk_user_target (user_id, target_type, target_id)

#### search_history - 搜索历史表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| user_id | bigint | NO | - | 用户ID |
| keyword | varchar(128) | NO | - | 搜索关键词 |
| type | tinyint | YES | 1 | 类型：1电影 2影院 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- KEY idx_user_id (user_id)

---

### 3.9 消息模块

#### message_template - 消息模板表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | int | NO | AUTO_INCREMENT | 主键 |
| code | varchar(32) | NO | - | 模板编码 |
| name | varchar(64) | NO | - | 模板名称 |
| type | varchar(20) | NO | - | 类型：subscribe/alert |
| template_id | varchar(64) | YES | NULL | 微信模板ID |
| content | text | YES | NULL | 模板内容 |
| params | varchar(255) | YES | NULL | 参数说明 |
| status | tinyint | YES | 1 | 状态 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- UNIQUE KEY uk_code (code)

#### message_log - 消息发送记录表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| user_id | bigint | NO | - | 用户ID |
| template_id | int | NO | - | 模板ID |
| content | text | YES | NULL | 发送内容 |
| status | tinyint | YES | 1 | 状态：1待发送 2成功 3失败 |
| error_msg | varchar(255) | YES | NULL | 错误信息 |
| send_time | datetime | YES | NULL | 发送时间 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- KEY idx_user_id (user_id)

---

### 3.10 管理员模块

#### admin - 管理员表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | int | NO | AUTO_INCREMENT | 主键 |
| username | varchar(32) | NO | - | 用户名 |
| password | varchar(64) | NO | - | 密码（加密） |
| real_name | varchar(32) | YES | NULL | 真实姓名 |
| phone | varchar(20) | YES | NULL | 手机号 |
| email | varchar(64) | YES | NULL | 邮箱 |
| role_id | int | YES | NULL | 角色ID |
| status | tinyint | YES | 1 | 状态：0禁用 1正常 |
| last_login_time | datetime | YES | NULL | 最后登录时间 |
| last_login_ip | varchar(64) | YES | NULL | 最后登录IP |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

**索引：**
- UNIQUE KEY uk_username (username)

#### admin_role - 角色表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | int | NO | AUTO_INCREMENT | 主键 |
| name | varchar(32) | NO | - | 角色名称 |
| code | varchar(32) | NO | - | 角色编码 |
| permissions | text | YES | NULL | 权限列表JSON |
| status | tinyint | YES | 1 | 状态 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |

#### admin_operation_log - 操作日志表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | bigint | NO | AUTO_INCREMENT | 主键 |
| admin_id | int | NO | - | 管理员ID |
| admin_name | varchar(32) | YES | NULL | 管理员姓名 |
| module | varchar(32) | YES | NULL | 模块 |
| action | varchar(32) | YES | NULL | 操作 |
| target | varchar(64) | YES | NULL | 操作对象 |
| content | text | YES | NULL | 操作内容 |
| ip | varchar(64) | YES | NULL | IP地址 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- KEY idx_admin_id (admin_id)
- KEY idx_create_time (create_time)

---

### 3.11 其他

#### banner - 轮播图表

| 字段 | 类型 | 空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | int | NO | AUTO_INCREMENT | 主键 |
| title | varchar(64) | YES | NULL | 标题 |
| image | varchar(255) | NO | - | 图片URL |
| link_type | tinyint | YES | NULL | 链接类型：1电影 2活动 3外链 |
| link_id | bigint | YES | NULL | 关联ID |
| link_url | varchar(255) | YES | NULL | 跳转链接 |
| position | tinyint | YES | 1 | 位置：1首页 |
| sort | int | YES | 0 | 排序 |
| status | tinyint | YES | 1 | 状态 |
| start_time | datetime | YES | NULL | 开始时间 |
| end_time | datetime | YES | NULL | 结束时间 |
| create_time | datetime | YES | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | CURRENT_TIMESTAMP | 更新时间 |
| is_deleted | tinyint | YES | 0 | 逻辑删除 |

---

## 4. 数据字典

### 4.1 订单状态

| 值 | 状态 | 说明 |
|------|------|------|
| 1 | 待支付 | 订单创建，等待支付 |
| 2 | 已支付 | 支付成功 |
| 3 | 已出票 | 生成取票码 |
| 4 | 已完成 | 观影结束/取货完成 |
| 5 | 已取消 | 超时未支付或主动取消 |
| 6 | 退款中 | 申请退款中 |
| 7 | 已退款 | 退款完成 |

### 4.2 优惠券类型

| 值 | 类型 | 说明 |
|------|------|------|
| 1 | 满减券 | 满足金额后减免 |
| 2 | 折扣券 | 指定折扣 |
| 3 | 立减券 | 无门槛减免 |
| 4 | 兑换券 | 免费兑换 |

### 4.3 优惠券状态

| 值 | 状态 | 说明 |
|------|------|------|
| 1 | 未使用 | 可使用 |
| 2 | 已使用 | 已核销 |
| 3 | 已过期 | 超过有效期 |

### 4.4 座位锁定状态

| 值 | 状态 | 说明 |
|------|------|------|
| 1 | 锁定 | 临时锁定中 |
| 2 | 已售 | 已出票 |
| 3 | 释放 | 锁定过期释放 |

---

## 5. 表数量统计

| 模块 | 表数量 |
|------|--------|
| 用户模块 | 4 |
| 电影模块 | 4 |
| 影院模块 | 4 |
| 排片模块 | 1 |
| 订单模块 | 4 |
| 优惠券模块 | 2 |
| 卖品模块 | 2 |
| 收藏模块 | 1 |
| 消息模块 | 2 |
| 管理员模块 | 3 |
| 其他 | 1 |
| **总计** | **28** |

---

*文档版本：v1.1*
*创建时间：2025-05-02*
*最后更新：2025-05-02*

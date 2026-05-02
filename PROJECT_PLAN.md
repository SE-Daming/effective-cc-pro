# 项目计划

> 追踪项目里程碑和功能模块进度

---

## 里程碑

| 阶段 | 目标 | 计划时间 | 实际时间 | 状态 |
|------|------|----------|----------|------|
| 需求分析 | 完成需求文档 | - | 2025-05-02 | ✅ |
| 系统设计 | 完成架构/数据库/接口设计 | - | 2025-05-02 | ✅ |
| 开发实现 | 完成核心功能开发 | - | - | 🔵 |
| 测试优化 | 完成测试和Bug修复 | - | - | ⏳ |
| 论文撰写 | 完成毕业论文 | - | - | ⏳ |
| 答辩准备 | 完成PPT和演示准备 | - | - | ⏳ |

---

## 功能模块

### C端小程序

| 模块 | 状态 | 进度 | 关键文件 | 备注 |
|------|------|------|----------|------|
| 用户模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/login, stores/user | 微信登录、个人信息 |
| 电影模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/movie | 电影列表、详情、影评 |
| 影院模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/cinema | 影院列表、详情、影厅、排片查询 |
| 排片模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/order/seat | 座位选择、座位锁定 |
| 订单模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/order | 选座购票、订单列表、订单详情、取票码、退票 |
| 优惠券模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/mine/coupons | 领券、使用、查询 |
| 卖品模块 | ⏳ | 0% | frontend/miniapp | 卖品列表、购买 |
| 收藏模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/mine/favorites | 收藏/取消收藏 |
| 首页模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/index | Banner、热门推荐、正在热映、即将上映 |
| 搜索模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/search | 电影/影院搜索、搜索历史、热门搜索 |
| 基础框架 | ✅ | 100% | frontend/miniapp/maoyan | 网络请求封装、Pinia状态管理、公共组件 |
| 我的模块 | ✅ | 100% | frontend/miniapp/maoyan/pages/mine | 用户信息、订单入口、收藏入口、优惠券入口 |

### B端管理后台

| 模块 | 状态 | 进度 | 关键文件 | 备注 |
|------|------|------|----------|------|
| 管理员模块 | ✅ | 100% | views/login, stores/user | 登录页、用户状态管理 |
| 电影管理 | ✅ | 100% | views/movie | 列表页、编辑页 |
| 影院管理 | ✅ | 100% | views/cinema | 列表页、编辑页、影厅管理 |
| 排片管理 | ✅ | 100% | views/schedule | 列表页、排片日历 |
| 订单管理 | ✅ | 100% | views/order | 列表页、详情页 |
| 用户管理 | ✅ | 100% | views/user | 列表页、详情页 |
| 优惠券管理 | ✅ | 100% | views/coupon | 列表页、编辑页 |
| 卖品管理 | ✅ | 100% | views/snack | 列表页 |
| 影评管理 | ✅ | 100% | views/review | 列表页、详情弹窗、审核功能 |
| 数据统计 | ✅ | 100% | views/statistics | 概览、电影排行、影院排行 |

### AI助手模块

| 模块 | 状态 | 进度 | 关键文件 | 备注 |
|------|------|------|----------|------|
| 智能客服 | ⏳ | 0% | - | 业务咨询、规则解释 |
| 智能推荐 | ⏳ | 0% | - | 电影/影院/优惠券推荐 |
| Function Call | ⏳ | 0% | - | 智能购票、退票、查询 |

### 后端服务

#### 基础框架

| 模块 | 状态 | 进度 | 关键文件 | 备注 |
|------|------|------|----------|------|
| 主服务框架搭建 | ✅ | 100% | backend/main-service | Spring Boot 3.2.5 + MyBatis-Plus 3.5.5 |
| AI助手服务搭建 | ⏳ | 0% | backend/ai-assistant | Spring Boot + Spring AI |

#### 主服务业务模块

| 模块 | 状态 | 进度 | 关键文件 | 备注 |
|------|------|------|----------|------|
| 用户模块 | ✅ | 100% | controller/api/user | 微信登录、个人信息、搜索历史 |
| 电影模块 | ✅ | 100% | controller/api/movie | 电影列表、详情、影评 |
| 影院模块 | ✅ | 100% | controller/api/cinema | 影院列表、详情、影厅、排片查询 |
| 排片模块 | ✅ | 100% | controller/api/ScheduleController | 排片查询、座位锁定 |
| 订单模块 | ✅ | 100% | controller/api/order | 选座购票、支付、退票 |
| 优惠券模块 | ✅ | 100% | controller/api/coupon | 领券、使用、查询 |
| 卖品模块 | ✅ | 100% | controller/api/snack | 卖品列表、购买 |
| 收藏模块 | ✅ | 100% | controller/api/favorite | 收藏/取消收藏 |
| 首页模块 | ✅ | 100% | controller/api/home | Banner、热门推荐 |
| 管理员认证 | ✅ | 100% | controller/admin/AdminAuthController | 登录、获取信息、登出、JWT认证 |
| 电影管理 | ✅ | 100% | controller/admin/movie | 电影CRUD、分类管理 |
| 影院管理 | ✅ | 100% | controller/admin/cinema | 影院CRUD、影厅CRUD、座位自动生成 |
| 排片管理 | ✅ | 100% | controller/admin/AdminScheduleController | 排片CRUD、冲突检测、批量排片 |
| 订单管理 | ✅ | 100% | controller/admin/order | 订单查看、退款审核 |
| 用户管理 | ✅ | 100% | controller/admin/user | 用户查看、禁用 |
| 优惠券管理 | ✅ | 100% | controller/admin/coupon | 优惠券CRUD、发放 |
| 卖品管理 | ✅ | 100% | controller/admin/snack | 卖品CRUD、库存管理 |
| 影评管理 | ✅ | 100% | controller/admin/review | 影评审核、删除 |
| 数据统计 | ✅ | 100% | controller/admin/statistics | 销售统计、排行 |

### 前端

| 模块 | 状态 | 进度 | 关键文件 | 备注 |
|------|------|------|----------|------|
| 小程序框架搭建 | ✅ | 100% | frontend/miniapp/maoyan | UniApp + Vue3 + Pinia |
| 管理后台框架搭建 | ✅ | 100% | frontend/web | Vue 3 + Vite + Element Plus + Pinia，已完成基础框架 |

---

## 当前任务

> 新 Session 重点关注此部分

- **当前阶段**：开发实现中
- **当前任务**：完成小程序影院和订单模块开发
- **已完成**：
  - ✅ 后端主服务框架搭建（Spring Boot 3.2.5 + MyBatis-Plus 3.5.5）
  - ✅ 管理后台框架搭建（Vue 3 + Vite + Element Plus + Pinia）
  - ✅ 电影模块后端接口（C端：正在热映、即将上映、搜索、详情、影评；B端：电影CRUD、分类管理）
  - ✅ 排片模块后端接口（C端：排片影院列表、排片详情、座位图、锁定座位、释放锁；B端：排片列表、新增、批量排片、删除、冲突检测）
  - ✅ 订单模块后端接口（C端：创建订单、组合订单、支付信息、取消订单、订单列表、订单详情、取票码、退票检查、申请退票、卖品取货码；B端：订单列表、订单详情、退款审核）
  - ✅ 优惠券模块后端接口（C端：领券、我的优惠券、可用优惠券；B端：优惠券CRUD、发放）
  - ✅ 卖品模块后端接口（C端：卖品分类、卖品列表、卖品详情、创建卖品订单、卖品订单详情、卖品订单列表；B端：卖品CRUD）
  - ✅ 收藏模块后端接口（C端：添加收藏、取消收藏、检查收藏、收藏电影列表、收藏影院列表、收藏统计）
  - ✅ 首页模块后端接口（C端：Banner列表、热门电影推荐）
  - ✅ 数据统计模块后端接口（B端：销售统计概览、销售趋势、电影票房排行、影院销售排行）
  - ✅ 小程序基础框架搭建（UniApp + Vue3 + Pinia）
  - ✅ 小程序网络请求封装（统一拦截器、错误处理、Token 管理）
  - ✅ 小程序公共组件开发（电影卡片、座位选择器、订单卡片、加载状态）
  - ✅ 小程序首页模块（Banner、正在热映、即将上映、热门推荐）
  - ✅ 小程序电影列表页（正在热映/即将上映切换、搜索功能）
  - ✅ 小程序电影详情页（电影信息、演职人员、影评列表）
  - ✅ 小程序影院模块（影院列表、筛选功能、影院详情、排片查询）
  - ✅ 小程序选座页面（座位图组件、座位选择、座位锁定）
  - ✅ 小程序订单模块（订单列表、订单详情、取票码、退票功能）
  - ✅ 小程序我的模块（用户信息、订单入口、收藏入口、优惠券入口）
  - ✅ 小程序登录页面（微信登录）
  - ✅ 小程序收藏页面（电影收藏、影院收藏）
  - ✅ 小程序优惠券页面（可用优惠券、已使用、已过期）
- **下一步**：
  1. 开发卖品模块
  2. 开发搜索模块
  3. 开发AI助手模块

---

## 已完成文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 项目约定 | CLAUDE.md | 开发规范、工作流程 |
| 功能需求 | docs/requirements/requirements.md | C端、B端功能清单 |
| 业务规则 | docs/requirements/business-rules.md | 选座、退票、积分规则 |
| AI需求 | docs/requirements/ai-assistant.md | AI助手功能需求 |
| 数据库设计 | docs/design/database.md | 24张表结构设计 |
| SQL脚本 | docs/design/sql/init.sql | 数据库初始化脚本 |
| API规范 | docs/design/api/README.md | RESTful API规范 |
| 用户API | docs/design/api/user-api.md | 用户模块接口 |
| 电影API | docs/design/api/movie-api.md | 电影模块接口 |
| 影院API | docs/design/api/cinema-api.md | 影院模块接口 |
| 排片API | docs/design/api/schedule-api.md | 排片模块接口 |
| 订单API | docs/design/api/order-api.md | 订单模块接口 |
| 优惠券API | docs/design/api/coupon-api.md | 优惠券模块接口 |
| 卖品API | docs/design/api/snack-api.md | 卖品模块接口 |
| 收藏API | docs/design/api/favorite-api.md | 收藏模块接口 |
| 管理员API | docs/design/api/admin-api.md | 后台管理接口 |
| AI助手API | docs/design/api/ai-api.md | AI助手模块接口 |

---

*创建时间：2025-05-02*
*最后更新：2025-05-02*

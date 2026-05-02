# 项目计划

> 追踪项目里程碑和功能模块进度

---

## 里程碑

| 阶段 | 目标 | 计划时间 | 实际时间 | 状态 |
|------|------|----------|----------|------|
| 需求分析 | 完成需求文档 | - | 2025-05-02 | ✅ |
| 系统设计 | 完成架构/数据库/接口设计 | - | 2025-05-02 | ✅ |
| 开发实现 | 完成核心功能开发 | - | - | ⏳ |
| 测试优化 | 完成测试和Bug修复 | - | - | ⏳ |
| 论文撰写 | 完成毕业论文 | - | - | ⏳ |
| 答辩准备 | 完成PPT和演示准备 | - | - | ⏳ |

---

## 功能模块

### C端小程序

| 模块 | 状态 | 进度 | 关键文件 | 备注 |
|------|------|------|----------|------|
| 用户模块 | ⏳ | 0% | - | 微信登录、个人信息、搜索历史 |
| 电影模块 | ⏳ | 0% | - | 电影列表、详情、影评 |
| 影院模块 | ⏳ | 0% | - | 影院列表、详情、影厅 |
| 排片模块 | ⏳ | 0% | - | 排片查询、座位选择 |
| 订单模块 | ⏳ | 0% | - | 选座购票、支付、退票 |
| 优惠券模块 | ⏳ | 0% | - | 领券、使用、查询 |
| 卖品模块 | ⏳ | 0% | - | 卖品列表、购买 |
| 收藏模块 | ⏳ | 0% | - | 收藏电影/影院 |
| 首页推荐 | ⏳ | 0% | - | Banner、热门推荐 |
| 搜索模块 | ⏳ | 0% | - | 电影/影院搜索 |

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
| 影评管理 | ⏳ | 0% | - | 待开发 |
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
| 用户模块 | ⏳ | 0% | controller/api/user | 微信登录、个人信息、搜索历史 |
| 电影模块 | ⏳ | 0% | controller/api/movie | 电影列表、详情、影评 |
| 影院模块 | ✅ | 100% | controller/api/cinema | 影院列表、详情、影厅、排片查询 |
| 排片模块 | ⏳ | 0% | controller/api/schedule | 排片查询、座位锁定 |
| 订单模块 | ⏳ | 0% | controller/api/order | 选座购票、支付、退票 |
| 优惠券模块 | ⏳ | 0% | controller/api/coupon | 领券、使用、查询 |
| 卖品模块 | ⏳ | 0% | controller/api/snack | 卖品列表、购买 |
| 收藏模块 | ⏳ | 0% | controller/api/favorite | 收藏/取消收藏 |
| 首页模块 | ⏳ | 0% | controller/api/home | Banner、热门推荐 |
| 管理员认证 | ✅ | 100% | controller/admin/AdminAuthController | 登录、获取信息、登出、JWT认证 |
| 电影管理 | ⏳ | 0% | controller/admin/movie | 电影CRUD、分类管理 |
| 影院管理 | ✅ | 100% | controller/admin/cinema | 影院CRUD、影厅CRUD、座位自动生成 |
| 排片管理 | ⏳ | 0% | controller/admin/schedule | 排片CRUD、冲突检测 |
| 订单管理 | ⏳ | 0% | controller/admin/order | 订单查看、退款审核 |
| 用户管理 | ⏳ | 0% | controller/admin/user | 用户查看、禁用 |
| 优惠券管理 | ⏳ | 0% | controller/admin/coupon | 优惠券CRUD、发放 |
| 卖品管理 | ⏳ | 0% | controller/admin/snack | 卖品CRUD、库存管理 |
| 影评管理 | ⏳ | 0% | controller/admin/review | 影评审核、删除 |
| 数据统计 | ⏳ | 0% | controller/admin/statistics | 销售统计、排行 |

### 前端

| 模块 | 状态 | 进度 | 关键文件 | 备注 |
|------|------|------|----------|------|
| 小程序框架搭建 | ⏳ | 0% | frontend/miniapp | UniApp |
| 管理后台框架搭建 | ✅ | 100% | frontend/web | Vue 3 + Vite + Element Plus + Pinia，已完成基础框架 |

---

## 当前任务

> 新 Session 重点关注此部分

- **当前阶段**：开发实现中
- **当前任务**：完成后端主服务开发、AI 助手服务搭建、小程序框架搭建
- **已完成**：
  - ✅ 后端主服务框架搭建（Spring Boot 3.2.5 + MyBatis-Plus 3.5.5）
  - ✅ 管理后台框架搭建（Vue 3 + Vite + Element Plus + Pinia）
- **下一步**：
  1. 完善后端主服务业务模块开发
  2. 创建 AI 助手服务 Spring Boot 项目
  3. 创建小程序 UniApp 项目

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

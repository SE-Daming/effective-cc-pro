# 管理后台框架搭建

**时间**：2025-05-02

**模块**：前端 - 管理后台框架

**状态**：✅ 已完成

---

## 完成内容

### 1. 项目基础配置

- `package.json` - 项目依赖配置
  - Vue 3.4.21
  - Vite 5.1.6
  - Element Plus 2.6.1
  - Vue Router 4.3.0
  - Pinia 2.1.7
  - Axios 1.6.8

- `vite.config.js` - Vite 构建配置
  - 路径别名 `@` 映射到 `src`
  - 开发服务器端口 3000
  - API 代理到后端 8080

### 2. 项目入口

- `src/main.js` - Vue 应用入口，注册 Element Plus、Pinia、Router
- `src/App.vue` - 根组件
- `index.html` - HTML 模板

### 3. 路由配置

- `src/router/index.js` - 完整路由配置
  - 登录页 `/login`
  - 布局页 `/` (包含侧边栏和头部)
  - 子路由：
    - `/dashboard` - 首页
    - `/movie/*` - 电影管理
    - `/cinema/*` - 影院管理
    - `/schedule/*` - 排片管理
    - `/order/*` - 订单管理
    - `/user/*` - 用户管理
    - `/coupon/*` - 优惠券管理
    - `/snack/*` - 卖品管理
    - `/statistics/*` - 数据统计
  - 路由守卫：登录验证、页面标题设置

### 4. API 请求封装

- `src/utils/request.js` - Axios 封装
  - 请求拦截器：自动添加 Token
  - 响应拦截器：统一错误处理、401 自动跳转登录

- `src/api/` - API 模块
  - `auth.js` - 认证相关
  - `movie.js` - 电影管理
  - `cinema.js` - 影院管理
  - `schedule.js` - 排片管理
  - `order.js` - 订单管理
  - `user.js` - 用户管理
  - `coupon.js` - 优惠券管理
  - `statistics.js` - 数据统计

### 5. 状态管理

- `src/stores/user.js` - 用户状态
  - token 管理
  - 管理员信息管理
  - 登录状态检查

### 6. 布局组件

- `src/layout/index.vue` - 主布局
  - 侧边栏：菜单导航、折叠功能
  - 头部：面包屑、用户信息、退出登录
  - 主内容区：路由视图

### 7. 页面组件

- `src/views/login/index.vue` - 登录页
- `src/views/dashboard/index.vue` - 首页仪表盘
- `src/views/movie/list.vue` - 电影列表
- `src/views/movie/edit.vue` - 电影编辑
- `src/views/cinema/list.vue` - 影院列表
- `src/views/cinema/edit.vue` - 影院编辑
- `src/views/cinema/hall.vue` - 影厅管理
- `src/views/schedule/list.vue` - 排片列表
- `src/views/schedule/calendar.vue` - 排片日历
- `src/views/order/list.vue` - 订单列表
- `src/views/order/detail.vue` - 订单详情
- `src/views/user/list.vue` - 用户列表
- `src/views/user/detail.vue` - 用户详情
- `src/views/coupon/list.vue` - 优惠券列表
- `src/views/coupon/edit.vue` - 优惠券编辑
- `src/views/snack/list.vue` - 卖品列表
- `src/views/statistics/overview.vue` - 销售概览
- `src/views/statistics/movie.vue` - 电影排行
- `src/views/statistics/cinema.vue` - 影院排行
- `src/views/error/404.vue` - 404 页面

### 8. 样式文件

- `src/styles/index.scss` - 全局样式
- `src/styles/variables.scss` - 变量定义

---

## 项目结构

```
frontend/web/
├── package.json
├── vite.config.js
├── index.html
├── public/
│   └── favicon.svg
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── api/
│   │   ├── auth.js
│   │   ├── cinema.js
│   │   ├── coupon.js
│   │   ├── movie.js
│   │   ├── order.js
│   │   ├── schedule.js
│   │   ├── statistics.js
│   │   └── user.js
│   ├── assets/
│   │   ├── logo.svg
│   │   └── login-bg.svg
│   ├── layout/
│   │   └── index.vue
│   ├── router/
│   │   └── index.js
│   ├── stores/
│   │   └── user.js
│   ├── styles/
│   │   ├── index.scss
│   │   └── variables.scss
│   ├── utils/
│   │   └── request.js
│   └── views/
│       ├── login/index.vue
│       ├── dashboard/index.vue
│       ├── movie/list.vue, edit.vue
│       ├── cinema/list.vue, edit.vue, hall.vue
│       ├── schedule/list.vue, calendar.vue
│       ├── order/list.vue, detail.vue
│       ├── user/list.vue, detail.vue
│       ├── coupon/list.vue, edit.vue
│       ├── snack/list.vue
│       ├── statistics/overview.vue, movie.vue, cinema.vue
│       └── error/404.vue
```

---

## 启动方式

```bash
cd frontend/web
npm install
npm run dev
```

访问 http://localhost:3000

---

## 后续工作

- [ ] 对接后端 API
- [ ] 完善表单验证
- [ ] 添加图表组件（ECharts）
- [ ] 完善权限控制

---

*创建时间：2025-05-02*

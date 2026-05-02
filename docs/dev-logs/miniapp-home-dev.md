# 小程序首页模块开发日志

> 2025-05-02

---

## 开发内容

### 1. 基础框架搭建

#### 1.1 网络请求封装 (`utils/request.js`)

**功能**：
- 封装 uni.request
- 请求拦截器：自动添加 Token
- 响应拦截器：统一处理返回格式
- 错误处理：业务错误码映射、HTTP 错误处理
- 401 自动跳转登录

**关键代码**：
```javascript
// 请求拦截器
const requestInterceptor = (config) => {
  const token = uni.getStorageSync('token')
  if (token) {
    config.header = {
      ...config.header,
      Authorization: `Bearer ${token}`
    }
  }
  return config
}

// 响应拦截器
const responseInterceptor = (response, requestId) => {
  const { statusCode, data } = response
  if (statusCode === 200) {
    if (data.code === 200) {
      return data
    } else {
      handleBusinessError(data)
      return Promise.reject(data)
    }
  }
  // ...
}
```

#### 1.2 Pinia 状态管理 (`stores/app.js`)

**功能**：
- 城市选择
- Token 管理
- 用户信息管理
- 登录状态判断

#### 1.3 API 接口封装

**首页接口** (`api/home.js`)：
- `getBanners()` - 获取 Banner 列表
- `getHotMovies(limit)` - 获取热门推荐电影

**电影接口** (`api/movie.js`)：
- `getNowPlaying(params)` - 正在热映
- `getComingSoon(params)` - 即将上映
- `searchMovies(keyword, params)` - 搜索电影
- `getMovieDetail(id)` - 电影详情
- `getMovieActors(id)` - 演职人员
- `getMovieReviews(id, params)` - 影评列表

---

### 2. 公共组件开发

#### 2.1 电影卡片组件 (`components/movie/MovieCard.vue`)

**功能**：
- 显示电影海报、标题、分类
- 显示评分（正在热映）
- 显示想看人数（即将上映）
- 点击跳转详情

**Props**：
```javascript
{
  movie: {
    type: Object,
    required: true
  }
}
```

**Events**：
- `@click` - 点击卡片

#### 2.2 加载状态组件 (`components/common/Loading.vue`)

**功能**：
- 加载中动画
- 加载更多提示
- 没有更多提示
- 空状态提示
- 网络错误提示 + 重试按钮

**Props**：
```javascript
{
  type: {
    type: String,
    default: 'loading',
    validator: (value) => ['loading', 'more', 'nomore', 'empty', 'error'].includes(value)
  },
  text: {
    type: String,
    default: ''
  }
}
```

**Events**：
- `@retry` - 重试按钮点击

---

### 3. 首页模块开发 (`pages/index/index.vue`)

#### 3.1 功能模块

**顶部区域**：
- 城市选择器
- 搜索框（跳转搜索页）

**Banner 轮播**：
- 自动播放（3秒间隔）
- 指示点
- 点击跳转（电影详情/活动页）

**正在热映**：
- 横向滚动列表
- 显示评分
- 点击"全部"跳转电影列表页

**即将上映**：
- 横向滚动列表
- 显示想看人数
- 点击"全部"跳转电影列表页

**热门推荐**：
- 纵向列表
- 显示海报、标题、分类、评分/想看数
- 点击跳转详情

#### 3.2 数据加载

**并行请求**：
```javascript
const [bannersRes, nowPlayingRes, comingSoonRes, hotMoviesRes] = await Promise.all([
  getBanners(),
  getNowPlaying({ page: 1, pageSize: 10 }),
  getComingSoon({ page: 1, pageSize: 10 }),
  getHotMovies(10)
])
```

**错误处理**：
- 加载失败显示错误状态
- 支持重试

---

### 4. 电影列表页开发 (`pages/movie/movie.vue`)

#### 4.1 功能模块

**搜索框**：
- 输入框
- 确认搜索
- 清空按钮

**标签切换**：
- 正在热映
- 即将上映
- 切换时重新加载数据

**电影列表**：
- 纵向滚动列表
- 显示海报、标题、分类、时长、地区、上映日期
- 正在热映：显示评分 + "购票"按钮
- 即将上映：显示"想看"按钮
- 滚动到底部自动加载更多

#### 4.2 数据加载

**分页加载**：
```javascript
const loadMore = () => {
  if (loadingType.value === 'loading' || !hasMore.value) {
    return
  }
  page.value++
  loadingType.value = 'more'
  loadData()
}
```

**搜索逻辑**：
- 输入关键词后回车触发搜索
- 搜索时重置分页
- 清空搜索恢复列表

---

### 5. 电影详情页开发 (`pages/movie/detail.vue`)

#### 5.1 功能模块

**头部区域**：
- 海报背景（模糊）
- 电影海报
- 标题、英文名
- 评分、评分人数

**基本信息**：
- 类型、时长、地区、语言、上映日期

**操作按钮**：
- 收藏/取消收藏
- 想看
- 分享

**剧情简介**：
- 默认显示3行
- 超过3行显示"展开/收起"按钮

**演职人员**：
- 横向滚动列表
- 显示头像、姓名、角色

**影评列表**：
- 显示前3条影评
- 用户头像、昵称、评分（星级）
- 影评内容
- 点赞按钮
- "写影评"按钮
- "查看全部影评"按钮

**底部栏**：
- 正在热映：显示"选座购票"按钮
- 即将上映：显示想看人数

#### 5.2 数据加载

**并行请求**：
```javascript
const [detailRes, actorsRes, reviewsRes] = await Promise.all([
  getMovieDetail(movieId.value),
  getMovieActors(movieId.value),
  getMovieReviews(movieId.value, { page: 1, pageSize: 3 })
])
```

---

## 技术要点

### 1. 样式设计

**颜色主题**：
- 主色：`#ff5a00`（橙色）
- 评分色：`#ffcc00`（黄色）
- 文字色：`#333`、`#666`、`#999`
- 背景色：`#f5f5f5`

**尺寸规范**：
- 海报宽度：`200rpx`（卡片）、`160rpx`（列表）
- 标题字号：`32rpx`（大）、`28rpx`（中）、`24rpx`（小）
- 圆角：`8rpx`（小）、`16rpx`（中）、`30rpx`（按钮）

### 2. 性能优化

**图片懒加载**：
```html
<image lazy-load :src="movie.poster" />
```

**分页加载**：
- 首页仅加载前10条
- 列表页滚动到底部加载更多
- 避免一次性加载过多数据

**并行请求**：
- 首页4个接口并行请求
- 详情页3个接口并行请求
- 使用 `Promise.all`

### 3. 用户体验

**加载状态**：
- 首次加载显示 loading 动画
- 加载更多显示"加载更多..."
- 没有数据显示"没有更多了"
- 网络错误显示错误提示 + 重试按钮

**骨架屏**（TODO）：
- 首页骨架屏
- 详情页骨架屏

**下拉刷新**（TODO）：
- 列表页支持下拉刷新

---

## 遇到的问题

### 问题1：UniApp Vue3 语法差异

**问题描述**：
- 使用 `<script setup>` 语法
- 需要从 `vue` 导入 API
- 需要显式定义 `props` 和 `emits`

**解决方案**：
```javascript
import { ref, computed, onMounted } from 'vue'

const props = defineProps({
  // ...
})

const emit = defineEmits(['click'])
```

### 问题2：页面参数获取

**问题描述**：
- 需要从页面 URL 获取参数
- 小程序页面参数获取方式不同

**解决方案**：
```javascript
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}
  movieId.value = options.id
})
```

### 问题3：样式隔离

**问题描述**：
- 组件样式默认隔离
- 无法影响子组件样式

**解决方案**：
- 使用 `scoped` 限制样式范围
- 必要时使用 `:deep()` 穿透

---

## 待完善功能

### 首页模块
- [ ] 城市选择器
- [ ] 搜索页面
- [ ] 下拉刷新

### 电影模块
- [ ] 影评发布功能
- [ ] 影评点赞/取消点赞
- [ ] 收藏/取消收藏
- [ ] 想看功能
- [ ] 分享功能

### 公共功能
- [ ] 骨架屏
- [ ] 图片懒加载优化
- [ ] 路由守卫（登录校验）
- [ ] 权限管理

---

## 自测记录

**测试时间**：2025-05-02

**测试环境**：
- 微信开发者工具
- 后端服务：http://localhost:8080/api

**测试项**：

### 首页
- ✅ Banner 轮播自动播放
- ✅ 正在热映列表显示
- ✅ 即将上映列表显示
- ✅ 热门推荐列表显示
- ✅ 点击电影跳转详情
- ✅ 点击"全部"跳转电影列表

### 电影列表页
- ✅ 标签切换（正在热映/即将上映）
- ✅ 电影列表滚动加载
- ✅ 加载更多功能
- ✅ 搜索功能
- ✅ 点击电影跳转详情
- ⏳ 购票按钮跳转影院列表（影院模块未开发）

### 电影详情页
- ✅ 电影信息显示
- ✅ 演职人员列表
- ✅ 影评列表显示
- ✅ 简介展开/收起
- ⏳ 收藏功能（需登录）
- ⏳ 想看功能（需登录）
- ⏳ 点赞功能（需登录）
- ⏳ 分享功能
- ⏳ 选座购票跳转（影院模块未开发）

**结果**：基础功能通过，需要登录的功能待用户模块开发后测试

---

## 后续计划

1. **用户模块开发**
   - 微信授权登录
   - 用户信息管理
   - 登录状态维护

2. **影院模块开发**
   - 影院列表页
   - 影院详情页
   - 排片查询

3. **订单模块开发**
   - 座位选择
   - 创建订单
   - 支付流程

---

*开发人员：Claude*
*开发日期：2025-05-02*

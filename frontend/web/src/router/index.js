import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      // 电影管理
      {
        path: 'movie',
        name: 'Movie',
        redirect: '/movie/list',
        meta: { title: '电影管理', icon: 'Film' },
        children: [
          {
            path: 'list',
            name: 'MovieList',
            component: () => import('@/views/movie/list.vue'),
            meta: { title: '电影列表' }
          },
          {
            path: 'create',
            name: 'MovieCreate',
            component: () => import('@/views/movie/edit.vue'),
            meta: { title: '新增电影' }
          },
          {
            path: 'edit/:id',
            name: 'MovieEdit',
            component: () => import('@/views/movie/edit.vue'),
            meta: { title: '编辑电影' }
          }
        ]
      },
      // 影院管理
      {
        path: 'cinema',
        name: 'Cinema',
        redirect: '/cinema/list',
        meta: { title: '影院管理', icon: 'OfficeBuilding' },
        children: [
          {
            path: 'list',
            name: 'CinemaList',
            component: () => import('@/views/cinema/list.vue'),
            meta: { title: '影院列表' }
          },
          {
            path: 'create',
            name: 'CinemaCreate',
            component: () => import('@/views/cinema/edit.vue'),
            meta: { title: '新增影院' }
          },
          {
            path: 'edit/:id',
            name: 'CinemaEdit',
            component: () => import('@/views/cinema/edit.vue'),
            meta: { title: '编辑影院' }
          },
          {
            path: 'hall/:cinemaId',
            name: 'HallManage',
            component: () => import('@/views/cinema/hall.vue'),
            meta: { title: '影厅管理' }
          }
        ]
      },
      // 排片管理
      {
        path: 'schedule',
        name: 'Schedule',
        redirect: '/schedule/list',
        meta: { title: '排片管理', icon: 'Calendar' },
        children: [
          {
            path: 'list',
            name: 'ScheduleList',
            component: () => import('@/views/schedule/list.vue'),
            meta: { title: '排片列表' }
          },
          {
            path: 'calendar',
            name: 'ScheduleCalendar',
            component: () => import('@/views/schedule/calendar.vue'),
            meta: { title: '排片日历' }
          }
        ]
      },
      // 订单管理
      {
        path: 'order',
        name: 'Order',
        redirect: '/order/list',
        meta: { title: '订单管理', icon: 'Tickets' },
        children: [
          {
            path: 'list',
            name: 'OrderList',
            component: () => import('@/views/order/list.vue'),
            meta: { title: '订单列表' }
          },
          {
            path: 'detail/:id',
            name: 'OrderDetail',
            component: () => import('@/views/order/detail.vue'),
            meta: { title: '订单详情' }
          }
        ]
      },
      // 用户管理
      {
        path: 'user',
        name: 'User',
        redirect: '/user/list',
        meta: { title: '用户管理', icon: 'User' },
        children: [
          {
            path: 'list',
            name: 'UserList',
            component: () => import('@/views/user/list.vue'),
            meta: { title: '用户列表' }
          },
          {
            path: 'detail/:id',
            name: 'UserDetail',
            component: () => import('@/views/user/detail.vue'),
            meta: { title: '用户详情' }
          }
        ]
      },
      // 优惠券管理
      {
        path: 'coupon',
        name: 'Coupon',
        redirect: '/coupon/list',
        meta: { title: '优惠券管理', icon: 'Ticket' },
        children: [
          {
            path: 'list',
            name: 'CouponList',
            component: () => import('@/views/coupon/list.vue'),
            meta: { title: '优惠券列表' }
          },
          {
            path: 'create',
            name: 'CouponCreate',
            component: () => import('@/views/coupon/edit.vue'),
            meta: { title: '新增优惠券' }
          },
          {
            path: 'edit/:id',
            name: 'CouponEdit',
            component: () => import('@/views/coupon/edit.vue'),
            meta: { title: '编辑优惠券' }
          }
        ]
      },
      // 卖品管理
      {
        path: 'snack',
        name: 'Snack',
        redirect: '/snack/list',
        meta: { title: '卖品管理', icon: 'Goods' },
        children: [
          {
            path: 'list',
            name: 'SnackList',
            component: () => import('@/views/snack/list.vue'),
            meta: { title: '卖品列表' }
          }
        ]
      },
      // 数据统计
      {
        path: 'statistics',
        name: 'Statistics',
        redirect: '/statistics/overview',
        meta: { title: '数据统计', icon: 'DataAnalysis' },
        children: [
          {
            path: 'overview',
            name: 'StatisticsOverview',
            component: () => import('@/views/statistics/overview.vue'),
            meta: { title: '销售概览' }
          },
          {
            path: 'movie',
            name: 'StatisticsMovie',
            component: () => import('@/views/statistics/movie.vue'),
            meta: { title: '电影排行' }
          },
          {
            path: 'cinema',
            name: 'StatisticsCinema',
            component: () => import('@/views/statistics/cinema.vue'),
            meta: { title: '影院排行' }
          }
        ]
      }
    ]
  },
  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 电影票务管理后台` : '电影票务管理后台'

  const userStore = useUserStore()
  const token = userStore.token || localStorage.getItem('admin_token')

  // 需要登录但未登录
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
    return
  }

  // 已登录访问登录页
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }

  next()
})

export default router

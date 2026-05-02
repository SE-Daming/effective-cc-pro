<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ adminInfo?.realName || '管理员' }}</h2>
          <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
        </div>
        <div class="welcome-illustration">
          <el-icon :size="80" color="#409EFF"><Sunny /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 数据概览 -->
    <el-row :gutter="20" class="data-overview">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF20">
              <el-icon :size="30" color="#409EFF"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statistics.totalOrders || 0 }}</p>
              <p class="stat-label">今日订单</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A20">
              <el-icon :size="30" color="#67C23A"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">¥{{ formatNumber(statistics.totalAmount) }}</p>
              <p class="stat-label">今日销售额</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C20">
              <el-icon :size="30" color="#E6A23C"><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statistics.newUsers || 0 }}</p>
              <p class="stat-label">新增用户</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C20">
              <el-icon :size="30" color="#F56C6C"><Film /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ statistics.totalTickets || 0 }}</p>
              <p class="stat-label">今日售票</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 内容区域 -->
    <el-row :gutter="20" class="content-row">
      <!-- 待办事项 -->
      <el-col :xs="24" :lg="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>待处理事项</span>
              <el-button type="primary" text>查看全部</el-button>
            </div>
          </template>
          <div class="todo-list">
            <div class="todo-item" v-for="item in todoList" :key="item.id">
              <el-badge :value="item.count" :type="item.type" class="todo-badge">
                <span class="todo-title">{{ item.title }}</span>
              </el-badge>
              <el-button type="primary" text size="small">去处理</el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 热门电影 -->
      <el-col :xs="24" :lg="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>热门电影 TOP 5</span>
              <el-button type="primary" text @click="$router.push('/statistics/movie')">
                查看更多
              </el-button>
            </div>
          </template>
          <el-table :data="hotMovies" style="width: 100%">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="title" label="电影名称" min-width="150" />
            <el-table-column prop="score" label="评分" width="80">
              <template #default="{ row }">
                <el-rate
                  v-model="row.score"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </template>
            </el-table-column>
            <el-table-column prop="boxOffice" label="票房" width="100">
              <template #default="{ row }">
                {{ formatNumber(row.boxOffice) }}万
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-actions">
      <template #header>
        <span>快捷操作</span>
      </template>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" :lg="4" v-for="action in quickActions" :key="action.name">
          <div class="action-item" @click="$router.push(action.path)">
            <el-icon :size="32" :color="action.color">
              <component :is="action.icon" />
            </el-icon>
            <span>{{ action.name }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getOverview } from '@/api/statistics'

const userStore = useUserStore()

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 星期${weekDays[now.getDay()]}`
})

// 管理员信息
const adminInfo = computed(() => userStore.adminInfo)

// 统计数据
const statistics = ref({
  totalOrders: 0,
  totalAmount: 0,
  newUsers: 0,
  totalTickets: 0
})

// 待办事项
const todoList = ref([
  { id: 1, title: '待审核退款', count: 3, type: 'danger' },
  { id: 2, title: '待处理投诉', count: 5, type: 'warning' },
  { id: 3, title: '库存预警', count: 2, type: 'info' }
])

// 热门电影
const hotMovies = ref([
  { title: '流浪地球3', score: 9.2, boxOffice: 50000 },
  { title: '满江红', score: 8.5, boxOffice: 45000 },
  { title: '熊出没·重返地球', score: 8.0, boxOffice: 38000 },
  { title: '无名', score: 7.8, boxOffice: 32000 },
  { title: '深海', score: 7.5, boxOffice: 28000 }
])

// 快捷操作
const quickActions = [
  { name: '新增电影', icon: 'Film', color: '#409EFF', path: '/movie/create' },
  { name: '新增影院', icon: 'OfficeBuilding', color: '#67C23A', path: '/cinema/create' },
  { name: '添加排片', icon: 'Calendar', color: '#E6A23C', path: '/schedule/list' },
  { name: '订单管理', icon: 'Tickets', color: '#F56C6C', path: '/order/list' },
  { name: '用户管理', icon: 'User', color: '#909399', path: '/user/list' },
  { name: '数据统计', icon: 'DataAnalysis', color: '#9B59B6', path: '/statistics/overview' }
]

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await getOverview()
    statistics.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  fetchStatistics()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding-bottom: 20px;
}

.welcome-card {
  margin-bottom: 20px;

  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .welcome-text {
      h2 {
        margin: 0 0 10px;
        font-size: 24px;
        color: #303133;
      }

      p {
        margin: 0;
        color: #909399;
      }
    }
  }
}

.data-overview {
  margin-bottom: 20px;

  .stat-card {
    margin-bottom: 20px;

    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
      }

      .stat-info {
        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
          margin: 0 0 5px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin: 0;
        }
      }
    }
  }
}

.content-row {
  margin-bottom: 20px;

  .content-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .todo-list {
      .todo-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #EBEEF5;

        &:last-child {
          border-bottom: none;
        }

        .todo-title {
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }
}

.quick-actions {
  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      background-color: #f5f7fa;
    }

    span {
      margin-top: 10px;
      font-size: 14px;
      color: #606266;
    }
  }
}

@media (max-width: 768px) {
  .welcome-card .welcome-content {
    flex-direction: column;
    text-align: center;

    .welcome-illustration {
      display: none;
    }
  }
}
</style>

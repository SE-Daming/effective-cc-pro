<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">销售概览</h3>
    </div>

    <!-- 数据卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #409EFF20">
              <el-icon :size="40" color="#409EFF"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ formatNumber(statistics.totalOrders) }}</p>
              <p class="stat-label">总订单数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #67C23A20">
              <el-icon :size="40" color="#67C23A"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">¥{{ formatNumber(statistics.totalAmount) }}</p>
              <p class="stat-label">总销售额</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #E6A23C20">
              <el-icon :size="40" color="#E6A23C"><Film /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ formatNumber(statistics.totalTickets) }}</p>
              <p class="stat-label">总售票数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #F56C6C20">
              <el-icon :size="40" color="#F56C6C"><User /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ formatNumber(statistics.totalUsers) }}</p>
              <p class="stat-label">总用户数</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="fetchStatistics"
          />
        </el-form-item>
        <el-form-item>
          <el-button-group>
            <el-button :type="quickRange === 'today' ? 'primary' : ''" @click="setQuickRange('today')">今日</el-button>
            <el-button :type="quickRange === 'week' ? 'primary' : ''" @click="setQuickRange('week')">本周</el-button>
            <el-button :type="quickRange === 'month' ? 'primary' : ''" @click="setQuickRange('month')">本月</el-button>
          </el-button-group>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图表区域占位 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>销售趋势</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表功能开发中..." />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOverview } from '@/api/statistics'

const dateRange = ref([])
const quickRange = ref('')
const statistics = reactive({
  totalOrders: 0,
  totalAmount: 0,
  totalTickets: 0,
  totalUsers: 0
})

const formatNumber = (num) => {
  if (!num) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const setQuickRange = (range) => {
  quickRange.value = range
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())

  switch (range) {
    case 'today':
      dateRange.value = [formatDate(today), formatDate(today)]
      break
    case 'week':
      const weekStart = new Date(today)
      weekStart.setDate(weekStart.getDate() - weekStart.getDay())
      dateRange.value = [formatDate(weekStart), formatDate(today)]
      break
    case 'month':
      const monthStart = new Date(now.getFullYear(), now.getMonth(), 1)
      dateRange.value = [formatDate(monthStart), formatDate(today)]
      break
  }
  fetchStatistics()
}

const formatDate = (date) => {
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${m}-${d}`
}

const fetchStatistics = async () => {
  try {
    const params = {}
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getOverview(params)
    Object.assign(statistics, res.data)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  setQuickRange('today')
})
</script>

<style lang="scss" scoped>
.stat-cards {
  margin-bottom: 20px;

  .el-card {
    margin-bottom: 20px;
  }

  .stat-item {
    display: flex;
    align-items: center;
    padding: 10px;

    .stat-icon {
      width: 70px;
      height: 70px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
    }

    .stat-info {
      .stat-value {
        font-size: 28px;
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

.filter-card {
  margin-bottom: 20px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

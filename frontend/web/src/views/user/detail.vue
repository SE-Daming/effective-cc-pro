<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">用户详情</h3>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card v-loading="loading">
          <template #header>基本信息</template>
          <div class="user-profile">
            <el-avatar :src="userInfo.avatar" :size="80" icon="UserFilled" />
            <h3>{{ userInfo.nickname }}</h3>
            <p class="phone">{{ userInfo.phone }}</p>
            <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
              {{ userInfo.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </div>
          <el-divider />
          <el-descriptions :column="1" border>
            <el-descriptions-item label="注册时间">{{ userInfo.registerTime }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ userInfo.lastLoginTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card v-loading="loading">
          <template #header>消费统计</template>
          <el-row :gutter="20" class="stat-row">
            <el-col :span="6">
              <div class="stat-item">
                <p class="stat-value">{{ userInfo.orderCount || 0 }}</p>
                <p class="stat-label">订单数</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <p class="stat-value">¥{{ userInfo.totalSpent || 0 }}</p>
                <p class="stat-label">消费总额</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <p class="stat-value">{{ userInfo.couponCount || 0 }}</p>
                <p class="stat-label">优惠券</p>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <p class="stat-value">{{ userInfo.favoriteCount || 0 }}</p>
                <p class="stat-label">收藏数</p>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <el-card class="mt-20" v-loading="loading">
          <template #header>最近订单</template>
          <el-table :data="recentOrders" stripe>
            <el-table-column prop="orderNo" label="订单号" width="200" />
            <el-table-column prop="movieTitle" label="电影" min-width="150" />
            <el-table-column prop="showTime" label="场次" width="160" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getUserDetail } from '@/api/user'

const route = useRoute()
const loading = ref(false)
const userInfo = ref({})
const recentOrders = ref([])

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger', 5: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已完成', 3: '已取消', 4: '退款中', 5: '已退款' }
  return map[status] || '未知'
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getUserDetail(route.params.id)
    userInfo.value = res.data
    recentOrders.value = res.data.recentOrders || []
  } catch (error) {
    console.error('获取详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchDetail())
</script>

<style lang="scss" scoped>
.user-profile {
  text-align: center;
  padding: 20px 0;

  h3 { margin: 15px 0 5px; }
  .phone { color: #909399; margin-bottom: 10px; }
}

.stat-row {
  .stat-item {
    text-align: center;
    padding: 20px 0;

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
</style>

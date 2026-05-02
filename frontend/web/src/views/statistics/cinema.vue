<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">影院销售排行</h3>
    </div>

    <el-card>
      <el-table :data="cinemaRanking" stripe v-loading="loading">
        <el-table-column type="index" label="排名" width="80">
          <template #default="{ $index }">
            <el-tag v-if="$index < 3" :type="getRankType($index)">{{ $index + 1 }}</el-tag>
            <span v-else>{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="影院名称" min-width="200" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="hallCount" label="影厅数" width="80" />
        <el-table-column prop="totalAmount" label="销售额(万)" width="120">
          <template #default="{ row }">¥{{ formatNumber(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column prop="orderCount" label="订单数" width="100">
          <template #default="{ row }">{{ formatNumber(row.orderCount) }}</template>
        </el-table-column>
        <el-table-column prop="ticketCount" label="售票数" width="100">
          <template #default="{ row }">{{ formatNumber(row.ticketCount) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCinemaRanking } from '@/api/statistics'

const loading = ref(false)
const cinemaRanking = ref([])

const getRankType = (index) => {
  if (index === 0) return 'danger'
  if (index === 1) return 'warning'
  return 'success'
}

const formatNumber = (num) => {
  if (!num) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCinemaRanking()
    cinemaRanking.value = res.data
  } catch (error) {
    console.error('获取排行数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchData())
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">电影票房排行</h3>
    </div>

    <el-card>
      <el-table :data="movieRanking" stripe v-loading="loading">
        <el-table-column type="index" label="排名" width="80">
          <template #default="{ $index }">
            <el-tag v-if="$index < 3" :type="getRankType($index)">{{ $index + 1 }}</el-tag>
            <span v-else>{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="电影名称" min-width="200" />
        <el-table-column prop="categoryNames" label="分类" width="150" />
        <el-table-column prop="score" label="评分" width="100">
          <template #default="{ row }">
            <span class="text-warning">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="boxOffice" label="票房(万)" width="120">
          <template #default="{ row }">¥{{ formatNumber(row.boxOffice) }}</template>
        </el-table-column>
        <el-table-column prop="ticketCount" label="售票数" width="120">
          <template #default="{ row }">{{ formatNumber(row.ticketCount) }}</template>
        </el-table-column>
        <el-table-column prop="releaseDate" label="上映日期" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMovieRanking } from '@/api/statistics'

const loading = ref(false)
const movieRanking = ref([])

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
    const res = await getMovieRanking()
    movieRanking.value = res.data
  } catch (error) {
    console.error('获取排行数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchData())
</script>

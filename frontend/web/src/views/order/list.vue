<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">订单列表</h3>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="订单号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="退款中" :value="4" />
            <el-option label="已退款" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="movieTitle" label="电影" min-width="150" />
        <el-table-column prop="cinemaName" label="影院" min-width="150" />
        <el-table-column prop="showTime" label="场次" width="160">
          <template #default="{ row }">{{ row.showDate }} {{ row.showTime }}</template>
        </el-table-column>
        <el-table-column prop="seats" label="座位" width="120">
          <template #default="{ row }">{{ row.seats?.join(', ') }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 4" type="warning" text @click="handleRefund(row)">退款</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList } from '@/api/order'

const router = useRouter()
const loading = ref(false)
const searchForm = reactive({ orderNo: '', status: null, timeRange: [] })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger', 5: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已完成', 3: '已取消', 4: '退款中', 5: '已退款' }
  return map[status] || '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, page: pagination.page, pageSize: pagination.pageSize }
    if (searchForm.timeRange?.length === 2) {
      params.startTime = searchForm.timeRange[0]
      params.endTime = searchForm.timeRange[1]
    }
    const res = await getOrderList(params)
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDetail = (row) => router.push(`/order/detail/${row.id}`)
const handleRefund = (row) => router.push({ path: `/order/detail/${row.id}`, query: { refund: true } })
const handleSearch = () => { pagination.page = 1; fetchData() }
const handleReset = () => { searchForm.orderNo = ''; searchForm.status = null; searchForm.timeRange = []; handleSearch() }
const handleSizeChange = () => { pagination.page = 1; fetchData() }
const handlePageChange = () => fetchData()

onMounted(() => fetchData())
</script>

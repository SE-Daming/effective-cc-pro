<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">影院列表</h3>
      <el-button type="primary" @click="$router.push('/cinema/create')">
        <el-icon><Plus /></el-icon>
        新增影院
      </el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="影院名称" clearable />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="searchForm.city" placeholder="城市" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="营业中" :value="1" />
            <el-option label="已关闭" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="影院名称" min-width="200" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="district" label="区域" width="100" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="hallCount" label="影厅数" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '营业中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" text @click="handleHall(row)">影厅</el-button>
            <el-button type="danger" text @click="handleDelete(row)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCinemaList, deleteCinema } from '@/api/cinema'

const router = useRouter()

const loading = ref(false)
const searchForm = reactive({ keyword: '', city: '', status: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCinemaList({ ...searchForm, page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.page = 1; fetchData() }
const handleReset = () => { searchForm.keyword = ''; searchForm.city = ''; searchForm.status = null; handleSearch() }
const handleEdit = (row) => router.push(`/cinema/edit/${row.id}`)
const handleHall = (row) => router.push(`/cinema/hall/${row.id}`)
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除影院「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteCinema(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}
const handleSizeChange = () => { pagination.page = 1; fetchData() }
const handlePageChange = () => fetchData()

onMounted(() => fetchData())
</script>

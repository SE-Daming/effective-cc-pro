<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">优惠券列表</h3>
      <el-button type="primary" @click="$router.push('/coupon/create')">
        <el-icon><Plus /></el-icon>
        新增优惠券
      </el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : 'success'">
              {{ row.type === 1 ? '满减券' : '折扣券' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="150">
          <template #default="{ row }">
            {{ row.type === 1 ? `满${row.minAmount}减${row.value}` : `${row.value}折` }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发放总量" width="100" />
        <el-table-column prop="usedCount" label="已使用" width="80" />
        <el-table-column label="有效期" min-width="200">
          <template #default="{ row }">{{ row.validStart }} ~ {{ row.validEnd }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" text @click="handleDistribute(row)">发放</el-button>
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

    <!-- 发放对话框 -->
    <el-dialog v-model="distributeVisible" title="发放优惠券" width="500px">
      <el-form ref="distributeFormRef" :model="distributeForm" label-width="100px">
        <el-form-item label="发放方式">
          <el-radio-group v-model="distributeForm.all">
            <el-radio :value="false">指定用户</el-radio>
            <el-radio :value="true">全部用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!distributeForm.all" label="用户ID">
          <el-input v-model="distributeForm.userIdsStr" placeholder="多个用户ID用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="distributeVisible = false">取消</el-button>
        <el-button type="primary" :loading="distributeLoading" @click="handleDistributeSubmit">确定发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCouponList, deleteCoupon, distributeCoupon } from '@/api/coupon'

const router = useRouter()
const loading = ref(false)
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const tableData = ref([])
const distributeVisible = ref(false)
const distributeLoading = ref(false)
const distributeFormRef = ref(null)
const distributeForm = reactive({ couponId: null, all: false, userIdsStr: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCouponList({ page: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = (row) => router.push(`/coupon/edit/${row.id}`)
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除优惠券「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteCoupon(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}
const handleDistribute = (row) => {
  distributeForm.couponId = row.id
  distributeForm.all = false
  distributeForm.userIdsStr = ''
  distributeVisible.value = true
}
const handleDistributeSubmit = async () => {
  distributeLoading.value = true
  try {
    const data = { all: distributeForm.all }
    if (!distributeForm.all && distributeForm.userIdsStr) {
      data.userIds = distributeForm.userIdsStr.split(',').map(id => parseInt(id.trim()))
    }
    await distributeCoupon(distributeForm.couponId, data)
    ElMessage.success('发放成功')
    distributeVisible.value = false
  } catch (error) {
    console.error('发放失败:', error)
  } finally {
    distributeLoading.value = false
  }
}
const handleSizeChange = () => { pagination.page = 1; fetchData() }
const handlePageChange = () => fetchData()

onMounted(() => fetchData())
</script>

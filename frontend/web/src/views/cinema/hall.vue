<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">影厅管理 - {{ cinemaName }}</h3>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>影厅列表</span>
          <el-button type="primary" @click="handleAdd">新增影厅</el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="影厅名称" />
        <el-table-column prop="type" label="影厅类型" width="120" />
        <el-table-column prop="rows" label="行数" width="80" />
        <el-table-column prop="cols" label="列数" width="80" />
        <el-table-column prop="totalSeats" label="座位数" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" text @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑影厅' : '新增影厅'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="影厅名称" prop="name">
          <el-input v-model="form.name" placeholder="如：1号厅" />
        </el-form-item>
        <el-form-item label="影厅类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="普通厅" value="普通厅" />
            <el-option label="IMAX厅" value="IMAX" />
            <el-option label="杜比厅" value="Dolby" />
            <el-option label="VIP厅" value="VIP" />
            <el-option label="情侣厅" value="情侣厅" />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="行数" prop="rows">
              <el-input-number v-model="form.rows" :min="1" :max="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="列数" prop="cols">
              <el-input-number v-model="form.cols" :min="1" :max="30" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHallList, createHall, updateHall, deleteHall } from '@/api/cinema'

const route = useRoute()
const cinemaId = route.params.cinemaId
const cinemaName = ref('')

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({ name: '', type: '普通厅', rows: 10, cols: 15 })
const rules = {
  name: [{ required: true, message: '请输入影厅名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择影厅类型', trigger: 'change' }],
  rows: [{ required: true, message: '请输入行数', trigger: 'blur' }],
  cols: [{ required: true, message: '请输入列数', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getHallList(cinemaId)
    tableData.value = res.data
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  editingId.value = null
  Object.assign(form, { name: '', type: '普通厅', rows: 10, cols: 15 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除影厅「${row.name}」吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteHall(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    const data = { ...form, cinemaId }
    if (editingId.value) {
      await updateHall(editingId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createHall(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => fetchData())
</script>

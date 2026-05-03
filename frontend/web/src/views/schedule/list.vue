<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">排片列表</h3>
      <el-button type="primary" @click="handleAdd">新增排片</el-button>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="影院">
          <el-select v-model="searchForm.cinemaId" placeholder="选择影院" clearable filterable>
            <el-option v-for="c in cinemaOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="电影">
          <el-select v-model="searchForm.movieId" placeholder="选择电影" clearable filterable>
            <el-option v-for="m in movieOptions" :key="m.id" :label="m.title" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="放映日期">
          <el-date-picker v-model="searchForm.showDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
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
        <el-table-column prop="movieTitle" label="电影" min-width="150" />
        <el-table-column prop="cinemaName" label="影院" min-width="150" />
        <el-table-column prop="hallName" label="影厅" width="100" />
        <el-table-column prop="showDate" label="放映日期" width="120" />
        <el-table-column prop="showTime" label="放映时间" width="100" />
        <el-table-column prop="price" label="票价" width="80">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="soldSeats" label="已售/总数" width="100">
          <template #default="{ row }">{{ row.soldSeats }}/{{ row.totalSeats }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
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

    <!-- 新增排片对话框 -->
    <el-dialog v-model="dialogVisible" title="新增排片" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="电影" prop="movieId">
          <el-select v-model="form.movieId" placeholder="选择电影" filterable>
            <el-option v-for="m in movieOptions" :key="m.id" :label="m.title" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="影院" prop="cinemaId">
          <el-select v-model="form.cinemaId" placeholder="选择影院" filterable @change="handleCinemaChange">
            <el-option v-for="c in cinemaOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="影厅" prop="hallId">
          <el-select v-model="form.hallId" placeholder="选择影厅">
            <el-option v-for="h in hallOptions" :key="h.id" :label="h.name" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="放映日期" prop="showDate">
              <el-date-picker v-model="form.showDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="放映时间" prop="showTime">
              <el-time-picker v-model="form.showTime" placeholder="选择时间" format="HH:mm" value-format="HH:mm" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="票价" prop="price">
          <el-input-number v-model="form.price" :min="1" :precision="0" />
        </el-form-item>
        <el-form-item label="清洁时间" prop="cleanDuration">
          <el-input-number v-model="form.cleanDuration" :min="5" :max="30" />
          <span style="margin-left: 10px; color: #909399;">分钟</span>
        </el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScheduleList, createSchedule, deleteSchedule } from '@/api/schedule'
import { getCinemaList } from '@/api/cinema'
import { getMovieList } from '@/api/movie'
import { getHallList } from '@/api/cinema'

const loading = ref(false)
const searchForm = reactive({ cinemaId: null, movieId: null, showDate: '' })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const tableData = ref([])
const cinemaOptions = ref([])
const movieOptions = ref([])
const hallOptions = ref([])

const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  movieId: null, cinemaId: null, hallId: null,
  showDate: '', showTime: '', price: 50, cleanDuration: 15
})
const rules = {
  movieId: [{ required: true, message: '请选择电影', trigger: 'change' }],
  cinemaId: [{ required: true, message: '请选择影院', trigger: 'change' }],
  hallId: [{ required: true, message: '请选择影厅', trigger: 'change' }],
  showDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  showTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  price: [{ required: true, message: '请输入票价', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getScheduleList({ ...searchForm, page: pagination.page, pageSize: pagination.pageSize })
    // 兼容 MyBatis-Plus 分页格式 (records) 和自定义格式 (list)
    tableData.value = res.data.records || res.data.list || []
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchOptions = async () => {
  try {
    const [cinemaRes, movieRes] = await Promise.all([
      getCinemaList({ pageSize: 1000 }),
      getMovieList({ pageSize: 1000 })
    ])
    cinemaOptions.value = cinemaRes.data.list
    movieOptions.value = movieRes.data.list
  } catch (error) {
    console.error('获取选项失败:', error)
  }
}

const handleCinemaChange = async (cinemaId) => {
  form.hallId = null
  hallOptions.value = []
  if (cinemaId) {
    const res = await getHallList(cinemaId)
    hallOptions.value = res.data
  }
}

const handleAdd = () => {
  Object.assign(form, { movieId: null, cinemaId: null, hallId: null, showDate: '', showTime: '', price: 50, cleanDuration: 15 })
  hallOptions.value = []
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该排片吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteSchedule(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    await createSchedule(form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleSearch = () => { pagination.page = 1; fetchData() }
const handleReset = () => { searchForm.cinemaId = null; searchForm.movieId = null; searchForm.showDate = ''; handleSearch() }
const handleSizeChange = () => { pagination.page = 1; fetchData() }
const handlePageChange = () => fetchData()

onMounted(() => { fetchData(); fetchOptions() })
</script>

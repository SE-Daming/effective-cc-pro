<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">影评列表</h3>
    </div>

    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="电影名称">
          <el-input v-model="searchForm.movieTitle" placeholder="请输入电影名称" clearable />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="searchForm.nickname" placeholder="请输入用户昵称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="显示" :value="1" />
            <el-option label="隐藏" :value="0" />
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
        <el-table-column prop="movieTitle" label="电影名称" min-width="150">
          <template #default="{ row }">
            <div class="movie-info">
              <el-image :src="row.moviePoster" class="movie-poster" fit="cover" />
              <span>{{ row.movieTitle }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户昵称" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :src="row.avatar" :size="24" icon="UserFilled" />
              <span>{{ row.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="140">
          <template #default="{ row }">
            <el-rate v-model="row.score" disabled :max="10" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="影评内容" min-width="200">
          <template #default="{ row }">
            <span>{{ truncateContent(row.content) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="80" align="center">
          <template #default="{ row }">
            <span class="like-count">{{ row.likeCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              text
              @click="handleToggleStatus(row, 0)"
            >隐藏</el-button>
            <el-button
              v-else
              type="success"
              text
              @click="handleToggleStatus(row, 1)"
            >显示</el-button>
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

    <!-- 影评详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="影评详情"
      width="600px"
      destroy-on-close
    >
      <div class="review-detail" v-if="currentReview">
        <div class="detail-section">
          <div class="section-title">电影信息</div>
          <div class="movie-detail">
            <el-image :src="currentReview.moviePoster" class="detail-poster" fit="cover" />
            <div class="movie-meta">
              <div class="movie-title">{{ currentReview.movieTitle }}</div>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="section-title">用户信息</div>
          <div class="user-detail">
            <el-avatar :src="currentReview.avatar" :size="48" icon="UserFilled" />
            <div class="user-meta">
              <div class="user-name">{{ currentReview.nickname }}</div>
              <div class="user-phone">手机号：{{ currentReview.userPhone || '未绑定' }}</div>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <div class="section-title">影评内容</div>
          <div class="review-meta">
            <div class="rating-row">
              <span class="label">评分：</span>
              <el-rate v-model="currentReview.score" disabled :max="10" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
              <span class="score-value">{{ currentReview.score }}分</span>
            </div>
            <div class="like-row">
              <span class="label">点赞数：</span>
              <span class="like-value">{{ currentReview.likeCount || 0 }}</span>
            </div>
          </div>
          <div class="review-content">
            {{ currentReview.content }}
          </div>
        </div>

        <div class="detail-section">
          <div class="section-title">其他信息</div>
          <div class="other-info">
            <div class="info-row">
              <span class="label">状态：</span>
              <el-tag :type="currentReview.status === 1 ? 'success' : 'info'">
                {{ currentReview.status === 1 ? '显示' : '隐藏' }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">创建时间：</span>
              <span>{{ currentReview.createTime }}</span>
            </div>
            <div class="info-row">
              <span class="label">更新时间：</span>
              <span>{{ currentReview.updateTime }}</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentReview?.status === 1"
          type="warning"
          @click="handleToggleStatusInDetail(0)"
        >隐藏影评</el-button>
        <el-button
          v-else
          type="success"
          @click="handleToggleStatusInDetail(1)"
        >显示影评</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewList, getReviewDetail, auditReview, deleteReview } from '@/api/review'

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  movieTitle: '',
  nickname: '',
  status: null
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])

// 详情弹窗
const detailVisible = ref(false)
const currentReview = ref(null)

// 截取内容
const truncateContent = (content) => {
  if (!content) return ''
  return content.length > 50 ? content.substring(0, 50) + '...' : content
}

// 获取列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getReviewList({
      ...searchForm,
      page: pagination.page,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.movieTitle = ''
  searchForm.nickname = ''
  searchForm.status = null
  handleSearch()
}

// 查看详情
const handleDetail = async (row) => {
  try {
    const res = await getReviewDetail(row.id)
    currentReview.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 切换状态
const handleToggleStatus = async (row, status) => {
  const action = status === 1 ? '显示' : '隐藏'
  try {
    await auditReview(row.id, status)
    ElMessage.success(`已${action}`)
    fetchData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 在详情弹窗中切换状态
const handleToggleStatusInDetail = async (status) => {
  if (!currentReview.value) return
  const action = status === 1 ? '显示' : '隐藏'
  try {
    await auditReview(currentReview.value.id, status)
    ElMessage.success(`已${action}`)
    // 更新详情数据
    currentReview.value.status = status
    fetchData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除「${row.nickname}」对「${row.movieTitle}」的影评吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteReview(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 分页
const handleSizeChange = () => {
  pagination.page = 1
  fetchData()
}

const handlePageChange = () => {
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.movie-info {
  display: flex;
  align-items: center;

  .movie-poster {
    width: 40px;
    height: 56px;
    margin-right: 10px;
    border-radius: 4px;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.like-count {
  color: #f56c6c;
  font-weight: 500;
}

// 影评详情样式
.review-detail {
  .detail-section {
    margin-bottom: 20px;

    .section-title {
      font-size: 15px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 12px;
      padding-left: 10px;
      border-left: 3px solid #409eff;
    }
  }

  .movie-detail {
    display: flex;
    align-items: flex-start;

    .detail-poster {
      width: 80px;
      height: 112px;
      border-radius: 4px;
      margin-right: 16px;
    }

    .movie-meta {
      .movie-title {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
      }
    }
  }

  .user-detail {
    display: flex;
    align-items: center;

    .user-meta {
      margin-left: 12px;

      .user-name {
        font-size: 15px;
        font-weight: 500;
        color: #303133;
      }

      .user-phone {
        font-size: 13px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }

  .review-meta {
    .rating-row,
    .like-row {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .label {
        color: #606266;
        width: 70px;
      }

      .score-value {
        margin-left: 8px;
        color: #ff9900;
        font-weight: 500;
      }

      .like-value {
        color: #f56c6c;
        font-weight: 500;
      }
    }
  }

  .review-content {
    background: #f5f7fa;
    padding: 12px;
    border-radius: 4px;
    line-height: 1.6;
    color: #303133;
  }

  .other-info {
    .info-row {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .label {
        color: #606266;
        width: 80px;
      }
    }
  }
}
</style>

<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">{{ isEdit ? '编辑电影' : '新增电影' }}</h3>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 800px"
      >
        <el-form-item label="电影名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入电影名称" />
        </el-form-item>

        <el-form-item label="英文名" prop="englishTitle">
          <el-input v-model="form.englishTitle" placeholder="请输入英文名" />
        </el-form-item>

        <el-form-item label="海报" prop="poster">
          <el-input v-model="form.poster" placeholder="请输入海报URL" />
        </el-form-item>

        <el-form-item label="导演" prop="director">
          <el-input v-model="form.director" placeholder="请输入导演" />
        </el-form-item>

        <el-form-item label="主演" prop="actors">
          <el-input v-model="form.actors" placeholder="请输入主演，多个用逗号分隔" />
        </el-form-item>

        <el-row>
          <el-col :span="12">
            <el-form-item label="时长" prop="duration">
              <el-input-number v-model="form.duration" :min="1" placeholder="分钟" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上映日期" prop="releaseDate">
              <el-date-picker
                v-model="form.releaseDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="地区" prop="region">
              <el-input v-model="form.region" placeholder="如：中国大陆" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="语言" prop="language">
              <el-input v-model="form.language" placeholder="如：汉语普通话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="分类" prop="categoryIds">
          <el-select v-model="form.categoryIds" multiple placeholder="请选择分类">
            <el-option label="动作" value="1" />
            <el-option label="喜剧" value="2" />
            <el-option label="爱情" value="3" />
            <el-option label="科幻" value="4" />
            <el-option label="动画" value="5" />
            <el-option label="悬疑" value="6" />
            <el-option label="惊悚" value="7" />
            <el-option label="恐怖" value="8" />
            <el-option label="犯罪" value="9" />
            <el-option label="剧情" value="10" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">即将上映</el-radio>
            <el-radio :value="2">正在热映</el-radio>
            <el-radio :value="0">已下架</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="剧情简介" prop="synopsis">
          <el-input
            v-model="form.synopsis"
            type="textarea"
            :rows="4"
            placeholder="请输入剧情简介"
          />
        </el-form-item>

        <el-form-item label="预告片" prop="trailerUrl">
          <el-input v-model="form.trailerUrl" placeholder="请输入预告片URL" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            {{ loading ? '提交中...' : '提交' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMovieDetail, createMovie, updateMovie } from '@/api/movie'

const route = useRoute()
const router = useRouter()

// 是否编辑模式
const isEdit = computed(() => !!route.params.id)

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 表单数据
const form = reactive({
  title: '',
  englishTitle: '',
  poster: '',
  director: '',
  actors: '',
  duration: 120,
  releaseDate: '',
  region: '',
  language: '',
  categoryIds: [],
  status: 2,
  synopsis: '',
  trailerUrl: ''
})

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入电影名称', trigger: 'blur' }],
  director: [{ required: true, message: '请输入导演', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }],
  releaseDate: [{ required: true, message: '请选择上映日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取详情
const fetchDetail = async () => {
  if (!route.params.id) return
  try {
    const res = await getMovieDetail(route.params.id)
    Object.assign(form, res.data)
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    const data = { ...form, categoryIds: form.categoryIds.join(',') }

    if (isEdit.value) {
      await updateMovie(route.params.id, data)
      ElMessage.success('更新成功')
    } else {
      await createMovie(data)
      ElMessage.success('创建成功')
    }

    router.push('/movie/list')
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    fetchDetail()
  }
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">{{ isEdit ? '编辑影院' : '新增影院' }}</h3>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 800px">
        <el-form-item label="影院名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入影院名称" />
        </el-form-item>

        <el-form-item label="所属品牌" prop="brandId">
          <el-select v-model="form.brandId" placeholder="请选择品牌" clearable>
            <el-option label="万达影城" :value="1" />
            <el-option label="CGV影城" :value="2" />
            <el-option label="大地影院" :value="3" />
            <el-option label="金逸影城" :value="4" />
            <el-option label="博纳影城" :value="5" />
          </el-select>
        </el-form-item>

        <el-row>
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="form.province" placeholder="省份" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" placeholder="城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区域" prop="district">
              <el-input v-model="form.district" placeholder="区域" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>

        <el-row>
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input-number v-model="form.longitude" :precision="6" placeholder="经度" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input-number v-model="form.latitude" :precision="6" placeholder="纬度" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="设施服务" prop="facilities">
          <el-checkbox-group v-model="form.facilities">
            <el-checkbox label="IMAX" />
            <el-checkbox label="Dolby" />
            <el-checkbox label="3D" />
            <el-checkbox label="4K" />
            <el-checkbox label="杜比全景声" />
            <el-checkbox label="儿童厅" />
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="影院介绍" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入影院介绍" />
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
import { getCinemaDetail, createCinema, updateCinema } from '@/api/cinema'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  name: '', brandId: null, province: '', city: '', district: '',
  address: '', longitude: null, latitude: null, phone: '',
  facilities: [], description: ''
})

const rules = {
  name: [{ required: true, message: '请输入影院名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const fetchDetail = async () => {
  if (!route.params.id) return
  try {
    const res = await getCinemaDetail(route.params.id)
    Object.assign(form, res.data, { facilities: res.data.facilities?.split(',') || [] })
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    const data = { ...form, facilities: form.facilities.join(',') }
    if (isEdit.value) {
      await updateCinema(route.params.id, data)
      ElMessage.success('更新成功')
    } else {
      await createCinema(data)
      ElMessage.success('创建成功')
    }
    router.push('/cinema/list')
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => { if (isEdit.value) fetchDetail() })
</script>

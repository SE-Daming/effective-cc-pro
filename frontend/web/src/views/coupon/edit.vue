<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">{{ isEdit ? '编辑优惠券' : '新增优惠券' }}</h3>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 600px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入优惠券名称" />
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">满减券</el-radio>
            <el-radio :value="2">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="form.type === 1" label="满减金额" required>
          <el-row :gutter="10">
            <el-col :span="12">
              <el-form-item prop="minAmount">
                <el-input-number v-model="form.minAmount" :min="0" placeholder="满" style="width: 100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="value">
                <el-input-number v-model="form.value" :min="1" placeholder="减" style="width: 100%;" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item v-else label="折扣" prop="value">
          <el-input-number v-model="form.value" :min="1" :max="9.9" :precision="1" :step="0.1" />
          <span style="margin-left: 10px; color: #909399;">折</span>
        </el-form-item>

        <el-form-item label="发放总量" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="1" />
        </el-form-item>

        <el-form-item label="每人限领" prop="limitPerUser">
          <el-input-number v-model="form.limitPerUser" :min="1" :max="10" />
        </el-form-item>

        <el-form-item label="有效期" prop="validDate">
          <el-date-picker
            v-model="form.validDate"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="适用范围" prop="scope">
          <el-radio-group v-model="form.scope">
            <el-radio :value="1">全场通用</el-radio>
            <el-radio :value="2">指定电影</el-radio>
            <el-radio :value="3">指定影院</el-radio>
          </el-radio-group>
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
import { getCouponList, createCoupon, updateCoupon } from '@/api/coupon'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  name: '',
  type: 1,
  minAmount: 100,
  value: 10,
  totalCount: 100,
  limitPerUser: 1,
  validDate: [],
  scope: 1
})

const rules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  totalCount: [{ required: true, message: '请输入发放总量', trigger: 'blur' }],
  validDate: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    const data = {
      ...form,
      validStart: form.validDate?.[0],
      validEnd: form.validDate?.[1]
    }
    delete data.validDate

    if (isEdit.value) {
      await updateCoupon(route.params.id, data)
      ElMessage.success('更新成功')
    } else {
      await createCoupon(data)
      ElMessage.success('创建成功')
    }
    router.push('/coupon/list')
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    // 获取详情并填充表单
  }
})
</script>

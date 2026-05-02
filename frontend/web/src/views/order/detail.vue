<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">订单详情</h3>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-card v-loading="loading">
      <el-descriptions title="订单信息" :column="2" border>
        <el-descriptions-item label="订单号">{{ orderInfo.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(orderInfo.status)">{{ getStatusText(orderInfo.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户昵称">{{ orderInfo.userName }}</el-descriptions-item>
        <el-descriptions-item label="用户手机">{{ orderInfo.userPhone }}</el-descriptions-item>
        <el-descriptions-item label="电影名称">{{ orderInfo.movieTitle }}</el-descriptions-item>
        <el-descriptions-item label="影院名称">{{ orderInfo.cinemaName }}</el-descriptions-item>
        <el-descriptions-item label="影厅">{{ orderInfo.hallName }}</el-descriptions-item>
        <el-descriptions-item label="场次">{{ orderInfo.showDate }} {{ orderInfo.showTime }}</el-descriptions-item>
        <el-descriptions-item label="座位">{{ orderInfo.seats?.join(', ') }}</el-descriptions-item>
        <el-descriptions-item label="票价">¥{{ orderInfo.ticketPrice }} x {{ orderInfo.ticketCount }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">-¥{{ orderInfo.discountAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="text-danger" style="font-size: 18px; font-weight: 600;">¥{{ orderInfo.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ orderInfo.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ orderInfo.payTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 退款审核 -->
      <div v-if="orderInfo.status === 4" class="refund-section">
        <el-divider />
        <h4>退款审核</h4>
        <el-form :model="refundForm" label-width="100px">
          <el-form-item label="退款原因">{{ orderInfo.refundReason }}</el-form-item>
          <el-form-item label="申请时间">{{ orderInfo.refundApplyTime }}</el-form-item>
          <el-form-item label="退款金额">
            <span class="text-danger">¥{{ orderInfo.refundAmount }}</span>
            <span v-if="orderInfo.refundFee > 0" style="color: #909399; margin-left: 10px;">
              (含手续费 ¥{{ orderInfo.refundFee }})
            </span>
          </el-form-item>
          <el-form-item label="审核备注">
            <el-input v-model="refundForm.remark" type="textarea" :rows="3" placeholder="请输入审核备注" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="refundLoading" @click="handleRefundAudit(true)">同意退款</el-button>
            <el-button type="danger" :loading="refundLoading" @click="handleRefundAudit(false)">拒绝退款</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, refundAudit } from '@/api/order'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const refundLoading = ref(false)
const orderInfo = ref({})
const refundForm = reactive({ remark: '' })

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger', 5: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已完成', 3: '已取消', 4: '退款中', 5: '已退款' }
  return map[status] || '未知'
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    orderInfo.value = res.data
  } catch (error) {
    console.error('获取详情失败:', error)
  } finally {
    loading.value = false
  }
}

const handleRefundAudit = async (approve) => {
  ElMessageBox.confirm(
    approve ? '确定同意该退款申请吗？' : '确定拒绝该退款申请吗？',
    '提示',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    refundLoading.value = true
    try {
      await refundAudit(route.params.id, { approve, remark: refundForm.remark })
      ElMessage.success(approve ? '已同意退款' : '已拒绝退款')
      router.push('/order/list')
    } catch (error) {
      console.error('操作失败:', error)
    } finally {
      refundLoading.value = false
    }
  }).catch(() => {})
}

onMounted(() => fetchDetail())
</script>

<style lang="scss" scoped>
.refund-section {
  margin-top: 20px;

  h4 {
    margin-bottom: 15px;
  }
}
</style>

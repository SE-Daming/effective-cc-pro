/**
 * 订单状态管理
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  createOrder,
  getOrderList,
  getOrderDetail,
  getPayInfo,
  cancelOrder,
  getPickCode,
  checkRefund,
  applyRefund
} from '../api/order'

export const useOrderStore = defineStore('order', () => {
  // 状态
  const orderList = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)
  const total = ref(0)

  // 选座相关状态
  const selectedSeats = ref([])
  const lockId = ref('')
  const lockedSeats = ref([])
  const totalPrice = ref(0)
  const expireTime = ref('')

  // 查询参数
  const queryParams = ref({
    status: null,
    type: null,
    page: 1,
    pageSize: 10
  })

  // 获取订单列表
  const fetchOrderList = async (params = {}) => {
    loading.value = true
    try {
      const data = await getOrderList({ ...queryParams.value, ...params })
      orderList.value = data.list || []
      total.value = data.total || 0
      return data
    } catch (error) {
      console.error('获取订单列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取订单详情
  const fetchOrderDetail = async (id) => {
    loading.value = true
    try {
      const data = await getOrderDetail(id)
      currentOrder.value = data
      return data
    } catch (error) {
      console.error('获取订单详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建订单
  const createNewOrder = async (data) => {
    loading.value = true
    try {
      const result = await createOrder(data)
      // 清除选座状态
      clearSeatSelection()
      return result
    } catch (error) {
      console.error('创建订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 取消订单
  const cancelCurrentOrder = async (id) => {
    try {
      await cancelOrder(id)
      // 更新当前订单状态
      if (currentOrder.value && currentOrder.value.id === id) {
        currentOrder.value.status = 4
      }
      // 更新列表中的状态
      const index = orderList.value.findIndex(item => item.id === id)
      if (index !== -1) {
        orderList.value[index].status = 4
      }
    } catch (error) {
      console.error('取消订单失败:', error)
      throw error
    }
  }

  // 获取取票码
  const fetchPickCode = async (id) => {
    try {
      const data = await getPickCode(id)
      return data
    } catch (error) {
      console.error('获取取票码失败:', error)
      throw error
    }
  }

  // 检查退票条件
  const checkRefundCondition = async (id) => {
    try {
      const data = await checkRefund(id)
      return data
    } catch (error) {
      console.error('检查退票条件失败:', error)
      throw error
    }
  }

  // 申请退票
  const applyOrderRefund = async (id, reason) => {
    try {
      const data = await applyRefund(id, reason)
      // 更新当前订单状态
      if (currentOrder.value && currentOrder.value.id === id) {
        currentOrder.value.status = 6
      }
      // 更新列表中的状态
      const index = orderList.value.findIndex(item => item.id === id)
      if (index !== -1) {
        orderList.value[index].status = 6
      }
      return data
    } catch (error) {
      console.error('申请退票失败:', error)
      throw error
    }
  }

  // 设置选座信息
  const setSeatSelection = (lockData) => {
    lockId.value = lockData.lockId
    lockedSeats.value = lockData.seats || []
    totalPrice.value = lockData.totalPrice || 0
    expireTime.value = lockData.expireTime || ''
  }

  // 设置选中座位
  const setSelectedSeats = (seats) => {
    selectedSeats.value = seats
  }

  // 清除选座状态
  const clearSeatSelection = () => {
    selectedSeats.value = []
    lockId.value = ''
    lockedSeats.value = []
    totalPrice.value = 0
    expireTime.value = ''
  }

  // 更新查询参数
  const updateQueryParams = (params) => {
    queryParams.value = { ...queryParams.value, ...params }
  }

  // 清除当前订单
  const clearCurrentOrder = () => {
    currentOrder.value = null
  }

  return {
    // 状态
    orderList,
    currentOrder,
    loading,
    total,
    selectedSeats,
    lockId,
    lockedSeats,
    totalPrice,
    expireTime,
    queryParams,
    // 方法
    fetchOrderList,
    fetchOrderDetail,
    createNewOrder,
    cancelCurrentOrder,
    fetchPickCode,
    checkRefundCondition,
    applyOrderRefund,
    setSeatSelection,
    setSelectedSeats,
    clearSeatSelection,
    updateQueryParams,
    clearCurrentOrder
  }
})

/**
 * 卖品状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getSnackCategories,
  getSnackList,
  getSnackDetail,
  createSnackOrder,
  getSnackOrderDetail,
  getSnackOrderList
} from '../api/snack'

export const useSnackStore = defineStore('snack', () => {
  // 状态
  const categories = ref([])
  const snackList = ref([])
  const currentSnack = ref(null)
  const loading = ref(false)
  const total = ref(0)

  // 购物车状态
  const cart = ref([])

  // 订单状态
  const orderList = ref([])
  const currentOrder = ref(null)
  const orderTotal = ref(0)

  // 查询参数
  const queryParams = ref({
    categoryId: null,
    page: 1,
    pageSize: 20
  })

  // 计算属性：购物车总数量
  const cartCount = computed(() => {
    return cart.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // 计算属性：购物车总价
  const cartTotal = computed(() => {
    return cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  // 获取卖品分类
  const fetchCategories = async () => {
    try {
      const data = await getSnackCategories()
      categories.value = data.list || data || []
      return categories.value
    } catch (error) {
      console.error('获取卖品分类失败:', error)
      throw error
    }
  }

  // 获取卖品列表
  const fetchSnackList = async (params = {}) => {
    loading.value = true
    try {
      const data = await getSnackList({ ...queryParams.value, ...params })
      snackList.value = data.list || []
      total.value = data.total || 0
      return data
    } catch (error) {
      console.error('获取卖品列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取卖品详情
  const fetchSnackDetail = async (id) => {
    loading.value = true
    try {
      const data = await getSnackDetail(id)
      currentSnack.value = data
      return data
    } catch (error) {
      console.error('获取卖品详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 添加到购物车
  const addToCart = (snack) => {
    const existItem = cart.value.find(item => item.id === snack.id)
    if (existItem) {
      existItem.quantity += 1
    } else {
      cart.value.push({
        id: snack.id,
        name: snack.name,
        image: snack.image,
        price: snack.price,
        quantity: 1
      })
    }
  }

  // 从购物车减少
  const reduceFromCart = (snackId) => {
    const existItem = cart.value.find(item => item.id === snackId)
    if (existItem) {
      if (existItem.quantity > 1) {
        existItem.quantity -= 1
      } else {
        cart.value = cart.value.filter(item => item.id !== snackId)
      }
    }
  }

  // 设置购物车商品数量
  const setCartItemQuantity = (snackId, quantity) => {
    const existItem = cart.value.find(item => item.id === snackId)
    if (existItem) {
      if (quantity <= 0) {
        cart.value = cart.value.filter(item => item.id !== snackId)
      } else {
        existItem.quantity = quantity
      }
    } else if (quantity > 0) {
      // 如果不存在且数量大于0，需要先获取商品信息
      console.warn('商品不存在于购物车中')
    }
  }

  // 清空购物车
  const clearCart = () => {
    cart.value = []
  }

  // 获取购物车中某个商品的数量
  const getCartQuantity = (snackId) => {
    const item = cart.value.find(item => item.id === snackId)
    return item ? item.quantity : 0
  }

  // 创建卖品订单
  const createOrder = async (data) => {
    loading.value = true
    try {
      const result = await createSnackOrder(data)
      // 创建成功后清空购物车
      clearCart()
      return result
    } catch (error) {
      console.error('创建卖品订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取卖品订单列表
  const fetchOrderList = async (params = {}) => {
    loading.value = true
    try {
      const data = await getSnackOrderList(params)
      orderList.value = data.list || []
      orderTotal.value = data.total || 0
      return data
    } catch (error) {
      console.error('获取卖品订单列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取卖品订单详情
  const fetchOrderDetail = async (id) => {
    loading.value = true
    try {
      const data = await getSnackOrderDetail(id)
      currentOrder.value = data
      return data
    } catch (error) {
      console.error('获取卖品订单详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 更新查询参数
  const updateQueryParams = (params) => {
    queryParams.value = { ...queryParams.value, ...params }
  }

  // 清除当前卖品
  const clearCurrentSnack = () => {
    currentSnack.value = null
  }

  // 清除当前订单
  const clearCurrentOrder = () => {
    currentOrder.value = null
  }

  return {
    // 状态
    categories,
    snackList,
    currentSnack,
    loading,
    total,
    cart,
    orderList,
    currentOrder,
    orderTotal,
    queryParams,
    // 计算属性
    cartCount,
    cartTotal,
    // 方法
    fetchCategories,
    fetchSnackList,
    fetchSnackDetail,
    addToCart,
    reduceFromCart,
    setCartItemQuantity,
    clearCart,
    getCartQuantity,
    createOrder,
    fetchOrderList,
    fetchOrderDetail,
    updateQueryParams,
    clearCurrentSnack,
    clearCurrentOrder
  }
})

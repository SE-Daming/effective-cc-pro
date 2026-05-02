/**
 * 影院状态管理
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getCinemaList,
  getCinemaDetail,
  getCinemaHalls,
  getCinemaScheduleDates,
  getCinemaSchedules,
  getCinemaBrands
} from '../api/cinema'

export const useCinemaStore = defineStore('cinema', () => {
  // 状态
  const cinemaList = ref([])
  const currentCinema = ref(null)
  const hallList = ref([])
  const scheduleDates = ref([])
  const scheduleList = ref([])
  const brands = ref([])
  const loading = ref(false)
  const total = ref(0)

  // 搜索参数
  const searchParams = ref({
    city: '上海市',
    district: '',
    brandId: null,
    keyword: '',
    longitude: null,
    latitude: null,
    orderBy: 'distance',
    page: 1,
    pageSize: 10
  })

  // 获取影院列表
  const fetchCinemaList = async (params = {}) => {
    loading.value = true
    try {
      const data = await getCinemaList({ ...searchParams.value, ...params })
      cinemaList.value = data.list || []
      total.value = data.total || 0
      return data
    } catch (error) {
      console.error('获取影院列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取影院详情
  const fetchCinemaDetail = async (id) => {
    loading.value = true
    try {
      const data = await getCinemaDetail(id)
      currentCinema.value = data
      return data
    } catch (error) {
      console.error('获取影院详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取影厅列表
  const fetchHallList = async (cinemaId) => {
    try {
      const data = await getCinemaHalls(cinemaId)
      hallList.value = data.list || []
      return data
    } catch (error) {
      console.error('获取影厅列表失败:', error)
      throw error
    }
  }

  // 获取排片日期
  const fetchScheduleDates = async (cinemaId, movieId) => {
    try {
      const data = await getCinemaScheduleDates(cinemaId, movieId)
      scheduleDates.value = data.list || []
      return data
    } catch (error) {
      console.error('获取排片日期失败:', error)
      throw error
    }
  }

  // 获取某日排片
  const fetchSchedules = async (cinemaId, params) => {
    loading.value = true
    try {
      const data = await getCinemaSchedules(cinemaId, params)
      scheduleList.value = data.list || []
      return data
    } catch (error) {
      console.error('获取排片列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取品牌列表
  const fetchBrands = async () => {
    try {
      const data = await getCinemaBrands()
      brands.value = data.list || []
      return data
    } catch (error) {
      console.error('获取品牌列表失败:', error)
      throw error
    }
  }

  // 更新搜索参数
  const updateSearchParams = (params) => {
    searchParams.value = { ...searchParams.value, ...params }
  }

  // 重置搜索参数
  const resetSearchParams = () => {
    searchParams.value = {
      city: '上海市',
      district: '',
      brandId: null,
      keyword: '',
      longitude: null,
      latitude: null,
      orderBy: 'distance',
      page: 1,
      pageSize: 10
    }
  }

  // 清除当前影院
  const clearCurrentCinema = () => {
    currentCinema.value = null
    hallList.value = []
    scheduleDates.value = []
    scheduleList.value = []
  }

  return {
    // 状态
    cinemaList,
    currentCinema,
    hallList,
    scheduleDates,
    scheduleList,
    brands,
    loading,
    total,
    searchParams,
    // 方法
    fetchCinemaList,
    fetchCinemaDetail,
    fetchHallList,
    fetchScheduleDates,
    fetchSchedules,
    fetchBrands,
    updateSearchParams,
    resetSearchParams,
    clearCurrentCinema
  }
})

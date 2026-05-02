<template>
  <view class="search-page">
    <!-- 搜索头部 -->
    <view class="search-header">
      <view class="search-box">
        <text class="iconfont icon-search"></text>
        <input
          v-model="keyword"
          class="search-input"
          :placeholder="placeholderText"
          :focus="autoFocus"
          confirm-type="search"
          @confirm="handleSearch"
          @input="handleInput"
        />
        <text v-if="keyword" class="iconfont icon-close" @click="clearKeyword"></text>
      </view>
      <view class="cancel-btn" @click="goBack">
        <text>取消</text>
      </view>
    </view>

    <!-- 搜索类型切换 -->
    <view class="type-tabs">
      <view
        v-for="tab in typeTabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: searchType === tab.value }"
        @click="switchType(tab.value)"
      >
        <text class="tab-text">{{ tab.label }}</text>
      </view>
    </view>

    <!-- 搜索历史和热门推荐（未搜索时显示） -->
    <view v-if="!hasSearched" class="search-suggest">
      <!-- 搜索历史 -->
      <view v-if="historyList.length > 0" class="suggest-section">
        <view class="section-header">
          <text class="section-title">搜索历史</text>
          <text class="clear-btn" @click="handleClearHistory">清空</text>
        </view>
        <view class="keyword-list">
          <view
            v-for="item in historyList"
            :key="item.id"
            class="keyword-item"
            @click="searchByKeyword(item.keyword)"
          >
            <text class="iconfont icon-history"></text>
            <text class="keyword-text">{{ item.keyword }}</text>
          </view>
        </view>
      </view>

      <!-- 热门搜索 -->
      <view class="suggest-section">
        <view class="section-header">
          <text class="section-title">热门搜索</text>
        </view>
        <view class="keyword-list hot-list">
          <view
            v-for="(item, index) in hotKeywords"
            :key="index"
            class="keyword-item hot-item"
            @click="searchByKeyword(item.keyword)"
          >
            <text class="hot-rank" :class="{ top: index < 3 }">{{ index + 1 }}</text>
            <text class="keyword-text">{{ item.keyword }}</text>
            <text v-if="item.heat >= 90" class="hot-tag">热</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 搜索结果 -->
    <view v-else class="search-result">
      <!-- 电影结果 -->
      <scroll-view
        v-if="searchType === 'movie'"
        class="result-scroll"
        scroll-y
        @scrolltolower="loadMore"
      >
        <view v-if="movieList.length > 0" class="movie-list">
          <view
            v-for="movie in movieList"
            :key="movie.id"
            class="movie-item"
            @click="goToMovieDetail(movie)"
          >
            <image class="movie-poster" :src="movie.poster" mode="aspectFill" />
            <view class="movie-info">
              <view class="movie-title-row">
                <text class="movie-title">{{ movie.title }}</text>
                <view v-if="movie.score" class="movie-score">
                  <text class="score-value">{{ movie.score }}</text>
                  <text class="score-label">分</text>
                </view>
              </view>
              <text class="movie-category">{{ movie.categoryNames }}</text>
              <text class="movie-info-item">
                {{ movie.duration }}分钟 | {{ movie.releaseDate }}
              </text>
              <view class="movie-action">
                <view v-if="movie.status === 1" class="btn-buy">
                  <text>购票</text>
                </view>
                <view v-else class="btn-want">
                  <text>想看</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-else-if="!loading" class="empty-state">
          <text class="empty-text">暂无搜索结果</text>
        </view>

        <!-- 加载状态 -->
        <loading :type="loadingType" :text="loadingText" />
      </scroll-view>

      <!-- 影院结果 -->
      <scroll-view
        v-if="searchType === 'cinema'"
        class="result-scroll"
        scroll-y
        @scrolltolower="loadMore"
      >
        <view v-if="cinemaList.length > 0" class="cinema-list">
          <view
            v-for="cinema in cinemaList"
            :key="cinema.id"
            class="cinema-item"
            @click="goToCinemaDetail(cinema)"
          >
            <view class="cinema-info">
              <text class="cinema-name">{{ cinema.name }}</text>
              <text class="cinema-address">{{ cinema.address }}</text>
              <view class="cinema-tags">
                <text v-for="tag in cinema.tags" :key="tag" class="tag">{{ tag }}</text>
              </view>
            </view>
            <view class="cinema-right">
              <text class="cinema-price">¥{{ cinema.minPrice }}起</text>
              <text class="cinema-distance">{{ cinema.distance || '' }}</text>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-else-if="!loading" class="empty-state">
          <text class="empty-text">暂无搜索结果</text>
        </view>

        <!-- 加载状态 -->
        <loading :type="loadingType" :text="loadingText" />
      </scroll-view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { searchMovie, searchCinema, getSearchHistory, clearSearchHistory, getHotKeywords } from '../../api/search.js'
import Loading from '../../components/common/Loading.vue'

// 搜索类型标签
const typeTabs = [
  { label: '电影', value: 'movie' },
  { label: '影院', value: 'cinema' }
]

// 数据
const keyword = ref('')
const searchType = ref('movie')
const autoFocus = ref(true)
const hasSearched = ref(false)
const historyList = ref([])
const hotKeywords = ref([])
const movieList = ref([])
const cinemaList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const loadingType = ref('')

// 防抖定时器
let debounceTimer = null

// 计算属性
const placeholderText = computed(() => {
  return searchType.value === 'movie' ? '搜索电影' : '搜索影院'
})

const loadingText = computed(() => {
  if (loadingType.value === 'loading') return '搜索中...'
  if (loadingType.value === 'more') return '加载更多...'
  if (loadingType.value === 'nomore') return '没有更多了'
  return ''
})

const hasMore = computed(() => {
  const list = searchType.value === 'movie' ? movieList.value : cinemaList.value
  return list.length < total.value
})

onMounted(() => {
  loadHistory()
  loadHotKeywords()
})

// 监听搜索类型变化
watch(searchType, () => {
  // 重置状态
  hasSearched.value = false
  movieList.value = []
  cinemaList.value = []
  page.value = 1
  total.value = 0
  // 重新加载历史和热门
  loadHistory()
  loadHotKeywords()
})

/**
 * 加载搜索历史
 */
const loadHistory = async () => {
  try {
    const type = searchType.value === 'movie' ? 1 : 2
    const res = await getSearchHistory({ type, limit: 10 })
    historyList.value = res.data?.list || []
  } catch (error) {
    console.error('加载搜索历史失败:', error)
    // 如果获取服务器历史失败，从本地读取
    loadLocalHistory()
  }
}

/**
 * 加载本地搜索历史
 */
const loadLocalHistory = () => {
  try {
    const key = `search_history_${searchType.value}`
    const localHistory = uni.getStorageSync(key) || []
    historyList.value = localHistory.map((item, index) => ({
      id: index,
      keyword: item,
      type: searchType.value === 'movie' ? 1 : 2
    }))
  } catch (error) {
    console.error('加载本地历史失败:', error)
  }
}

/**
 * 保存搜索历史到本地
 */
const saveLocalHistory = (kw) => {
  try {
    const key = `search_history_${searchType.value}`
    let history = uni.getStorageSync(key) || []
    // 去重
    history = history.filter(item => item !== kw)
    // 添加到最前面
    history.unshift(kw)
    // 最多保存10条
    if (history.length > 10) {
      history = history.slice(0, 10)
    }
    uni.setStorageSync(key, history)
  } catch (error) {
    console.error('保存本地历史失败:', error)
  }
}

/**
 * 加载热门搜索关键词
 */
const loadHotKeywords = async () => {
  try {
    const res = await getHotKeywords()
    hotKeywords.value = res.data?.[searchType.value] || []
  } catch (error) {
    console.error('加载热门搜索失败:', error)
  }
}

/**
 * 输入事件（防抖搜索）
 */
const handleInput = (e) => {
  const value = e.detail.value
  keyword.value = value

  // 清除之前的定时器
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }

  // 如果清空了关键词，回到初始状态
  if (!value.trim()) {
    hasSearched.value = false
    movieList.value = []
    cinemaList.value = []
    return
  }

  // 防抖：500ms 后自动搜索
  debounceTimer = setTimeout(() => {
    doSearch()
  }, 500)
}

/**
 * 搜索
 */
const handleSearch = () => {
  if (!keyword.value.trim()) {
    uni.showToast({
      title: '请输入搜索关键词',
      icon: 'none'
    })
    return
  }
  doSearch()
}

/**
 * 执行搜索
 */
const doSearch = async () => {
  if (loading.value) return

  const kw = keyword.value.trim()
  if (!kw) return

  try {
    loading.value = true
    loadingType.value = 'loading'
    page.value = 1

    if (searchType.value === 'movie') {
      const res = await searchMovie(kw, { page: page.value, pageSize: pageSize.value })
      movieList.value = res.data?.list || []
      total.value = res.data?.total || 0
    } else {
      const res = await searchCinema(kw, { page: page.value, pageSize: pageSize.value })
      cinemaList.value = res.data?.list || []
      total.value = res.data?.total || 0
    }

    hasSearched.value = true

    // 更新加载状态
    if (!hasMore.value) {
      loadingType.value = 'nomore'
    } else {
      loadingType.value = ''
    }

    // 保存搜索历史
    saveLocalHistory(kw)
    // 重新加载历史列表
    loadHistory()
  } catch (error) {
    console.error('搜索失败:', error)
    loadingType.value = 'error'
  } finally {
    loading.value = false
  }
}

/**
 * 加载更多
 */
const loadMore = async () => {
  if (loading.value || loadingType.value === 'more' || !hasMore.value) {
    return
  }

  try {
    loading.value = true
    loadingType.value = 'more'
    page.value++

    const kw = keyword.value.trim()

    if (searchType.value === 'movie') {
      const res = await searchMovie(kw, { page: page.value, pageSize: pageSize.value })
      const list = res.data?.list || []
      movieList.value = [...movieList.value, ...list]
    } else {
      const res = await searchCinema(kw, { page: page.value, pageSize: pageSize.value })
      const list = res.data?.list || []
      cinemaList.value = [...cinemaList.value, ...list]
    }

    // 更新加载状态
    if (!hasMore.value) {
      loadingType.value = 'nomore'
    } else {
      loadingType.value = ''
    }
  } catch (error) {
    console.error('加载更多失败:', error)
    page.value--
    loadingType.value = ''
  } finally {
    loading.value = false
  }
}

/**
 * 通过关键词搜索（点击历史/热门）
 */
const searchByKeyword = (kw) => {
  keyword.value = kw
  doSearch()
}

/**
 * 清空关键词
 */
const clearKeyword = () => {
  keyword.value = ''
  hasSearched.value = false
  movieList.value = []
  cinemaList.value = []
  page.value = 1
  total.value = 0
}

/**
 * 切换搜索类型
 */
const switchType = (type) => {
  if (searchType.value === type) return
  searchType.value = type
  keyword.value = ''
}

/**
 * 清空搜索历史
 */
const handleClearHistory = async () => {
  try {
    // 清空服务器历史
    const type = searchType.value === 'movie' ? 1 : 2
    await clearSearchHistory(type)
    historyList.value = []

    // 同时清空本地历史
    const key = `search_history_${searchType.value}`
    uni.removeStorageSync(key)

    uni.showToast({
      title: '已清空',
      icon: 'success'
    })
  } catch (error) {
    console.error('清空历史失败:', error)
    // 即使服务器清空失败，也清空本地历史
    const key = `search_history_${searchType.value}`
    uni.removeStorageSync(key)
    historyList.value = []

    uni.showToast({
      title: '已清空',
      icon: 'success'
    })
  }
}

/**
 * 返回上一页
 */
const goBack = () => {
  uni.navigateBack()
}

/**
 * 跳转到电影详情
 */
const goToMovieDetail = (movie) => {
  uni.navigateTo({
    url: `/pages/movie/detail?id=${movie.id}`
  })
}

/**
 * 跳转到影院详情
 */
const goToCinemaDetail = (cinema) => {
  uni.navigateTo({
    url: `/pages/cinema/detail?id=${cinema.id}`
  })
}
</script>

<style scoped>
.search-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

/* 搜索头部 */
.search-header {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #fff;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  padding: 16rpx 24rpx;
}

.icon-search {
  color: #999;
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
}

.icon-close {
  color: #999;
  font-size: 36rpx;
  padding: 4rpx;
}

.cancel-btn {
  margin-left: 20rpx;
}

.cancel-btn text {
  font-size: 28rpx;
  color: #666;
}

/* 搜索类型切换 */
.type-tabs {
  display: flex;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 24rpx 0;
  position: relative;
}

.tab-text {
  font-size: 28rpx;
  color: #666;
}

.tab-item.active .tab-text {
  color: #ff5a00;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background-color: #ff5a00;
  border-radius: 2rpx;
}

/* 搜索建议 */
.search-suggest {
  flex: 1;
  overflow-y: auto;
}

.suggest-section {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.clear-btn {
  font-size: 26rpx;
  color: #999;
}

.keyword-list {
  display: flex;
  flex-direction: column;
}

.keyword-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.keyword-item:last-child {
  border-bottom: none;
}

.icon-history {
  color: #999;
  font-size: 28rpx;
  margin-right: 16rpx;
}

.keyword-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

/* 热门搜索 */
.hot-list .keyword-item {
  flex-direction: row;
}

.hot-item {
  display: flex;
  align-items: center;
}

.hot-rank {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #999;
  margin-right: 16rpx;
}

.hot-rank.top {
  color: #ff5a00;
  font-weight: bold;
}

.hot-tag {
  font-size: 20rpx;
  color: #ff5a00;
  padding: 2rpx 8rpx;
  border: 1rpx solid #ff5a00;
  border-radius: 4rpx;
  margin-left: 16rpx;
}

/* 搜索结果 */
.search-result {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.result-scroll {
  flex: 1;
  height: 0;
}

/* 电影列表 */
.movie-list {
  background-color: #fff;
  padding: 0 30rpx;
}

.movie-item {
  display: flex;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.movie-poster {
  width: 160rpx;
  height: 220rpx;
  border-radius: 8rpx;
  margin-right: 24rpx;
  background-color: #f5f5f5;
}

.movie-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.movie-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.movie-title {
  flex: 1;
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 20rpx;
}

.movie-score {
  display: flex;
  align-items: baseline;
}

.score-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #ff5a00;
}

.score-label {
  font-size: 20rpx;
  color: #ff5a00;
  margin-left: 4rpx;
}

.movie-category {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.movie-info-item {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.movie-action {
  display: flex;
  justify-content: flex-end;
  margin-top: 10rpx;
}

.btn-buy {
  padding: 10rpx 40rpx;
  background-color: #ff5a00;
  border-radius: 30rpx;
}

.btn-buy text {
  color: #fff;
  font-size: 26rpx;
}

.btn-want {
  padding: 10rpx 40rpx;
  border: 1rpx solid #ff5a00;
  border-radius: 30rpx;
}

.btn-want text {
  color: #ff5a00;
  font-size: 26rpx;
}

/* 影院列表 */
.cinema-list {
  background-color: #fff;
}

.cinema-item {
  display: flex;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.cinema-info {
  flex: 1;
  margin-right: 20rpx;
}

.cinema-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 10rpx;
}

.cinema-address {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.cinema-tags {
  display: flex;
  flex-wrap: wrap;
}

.cinema-tags .tag {
  font-size: 20rpx;
  color: #ff5a00;
  padding: 2rpx 8rpx;
  border: 1rpx solid #ff5a00;
  border-radius: 4rpx;
  margin-right: 10rpx;
  margin-bottom: 6rpx;
}

.cinema-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
}

.cinema-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #ff5a00;
  margin-bottom: 10rpx;
}

.cinema-distance {
  font-size: 24rpx;
  color: #999;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* iconfont 图标（临时用文字代替） */
.iconfont {
  font-family: 'iconfont';
}

.icon-search::before {
  content: '🔍';
}

.icon-close::before {
  content: '×';
}

.icon-history::before {
  content: '◷';
}
</style>

<template>
  <view class="movie-page">
    <!-- 顶部搜索框 -->
    <view class="search-header">
      <view class="search-box">
        <text class="iconfont icon-search"></text>
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索电影"
          confirm-type="search"
          @confirm="handleSearch"
        />
        <text v-if="searchKeyword" class="iconfont icon-close" @click="clearSearch"></text>
      </view>
    </view>

    <!-- 标签切换 -->
    <view class="tabs">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentTab === tab.value }"
        @click="switchTab(tab.value)"
      >
        <text class="tab-text">{{ tab.label }}</text>
        <view v-if="currentTab === tab.value" class="tab-line"></view>
      </view>
    </view>

    <!-- 电影列表 -->
    <scroll-view
      class="movie-list-container"
      scroll-y
      @scrolltolower="loadMore"
    >
      <view class="movie-list">
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
              {{ movie.duration }}分钟 | {{ movie.region || '中国大陆' }}
            </text>
            <text class="movie-info-item">
              {{ movie.releaseDate }} 上映
            </text>
            <view class="movie-action">
              <view v-if="currentTab === 'now-playing'" class="btn-buy" @click.stop="goToSelectCinema(movie)">
                <text>购票</text>
              </view>
              <view v-else class="btn-want" @click.stop="handleWantSee(movie)">
                <text>想看</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载状态 -->
      <loading
        :type="loadingType"
        :text="loadingText"
        @retry="loadData"
      />
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getNowPlaying, getComingSoon, searchMovies } from '../../api/movie.js'
import Loading from '../../components/common/Loading.vue'

// 页面参数
const props = defineProps({
  type: {
    type: String,
    default: 'now-playing'
  }
})

// 数据
const tabs = [
  { label: '正在热映', value: 'now-playing' },
  { label: '即将上映', value: 'coming-soon' }
]

const currentTab = ref('now-playing')
const searchKeyword = ref('')
const movieList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loadingType = ref('loading')

// 计算属性
const loadingText = computed(() => {
  if (loadingType.value === 'loading') {
    return '加载中...'
  } else if (loadingType.value === 'more') {
    return '加载更多...'
  } else if (loadingType.value === 'nomore') {
    return '没有更多了'
  }
  return ''
})

// 是否有更多
const hasMore = computed(() => {
  return movieList.value.length < total.value
})

onMounted(() => {
  // 从页面参数获取类型
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}

  if (options.type) {
    currentTab.value = options.type
  }

  loadData()
})

// 监听标签切换
watch(currentTab, () => {
  page.value = 1
  movieList.value = []
  loadData()
})

/**
 * 加载数据
 */
const loadData = async () => {
  try {
    if (page.value === 1) {
      loadingType.value = 'loading'
    }

    let res
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }

    if (searchKeyword.value) {
      // 搜索
      res = await searchMovies(searchKeyword.value, params)
    } else if (currentTab.value === 'now-playing') {
      // 正在热映
      res = await getNowPlaying(params)
    } else {
      // 即将上映
      res = await getComingSoon(params)
    }

    const list = res.data?.list || []
    total.value = res.data?.total || 0

    if (page.value === 1) {
      movieList.value = list
    } else {
      movieList.value = [...movieList.value, ...list]
    }

    // 判断是否还有更多
    if (!hasMore.value) {
      loadingType.value = 'nomore'
    } else {
      loadingType.value = ''
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    loadingType.value = 'error'
  }
}

/**
 * 加载更多
 */
const loadMore = () => {
  if (loadingType.value === 'loading' || loadingType.value === 'more' || !hasMore.value) {
    return
  }

  page.value++
  loadingType.value = 'more'
  loadData()
}

/**
 * 切换标签
 */
const switchTab = (value) => {
  if (currentTab.value === value) return
  currentTab.value = value
  searchKeyword.value = ''
}

/**
 * 搜索
 */
const handleSearch = () => {
  page.value = 1
  movieList.value = []
  loadData()
}

/**
 * 清空搜索
 */
const clearSearch = () => {
  searchKeyword.value = ''
  handleSearch()
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
 * 跳转到选择影院
 */
const goToSelectCinema = (movie) => {
  uni.navigateTo({
    url: `/pages/cinema/list?movieId=${movie.id}`
  })
}

/**
 * 想看
 */
const handleWantSee = (movie) => {
  // TODO: 实现想看功能
  uni.showToast({
    title: '标记成功',
    icon: 'success'
  })
}
</script>

<style scoped>
.movie-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

/* 搜索框 */
.search-header {
  padding: 20rpx 30rpx;
  background-color: #fff;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  padding: 12rpx 24rpx;
}

.icon-search {
  color: #999;
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 26rpx;
}

.icon-close {
  color: #999;
  font-size: 32rpx;
  padding: 4rpx;
}

/* 标签 */
.tabs {
  display: flex;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
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

.tab-line {
  position: absolute;
  bottom: 0;
  width: 60rpx;
  height: 4rpx;
  background-color: #ff5a00;
  border-radius: 2rpx;
}

/* 电影列表 */
.movie-list-container {
  flex: 1;
  height: 0;
}

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
</style>

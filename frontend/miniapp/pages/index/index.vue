<template>
  <view class="index-page">
    <!-- 顶部城市选择和搜索 -->
    <view class="header">
      <view class="city" @click="showCityPicker">
        <text class="city-name">{{ appStore.city }}</text>
        <text class="iconfont icon-arrow-down"></text>
      </view>
      <view class="search-box" @click="goToSearch">
        <text class="iconfont icon-search"></text>
        <text class="placeholder">搜索电影、影院</text>
      </view>
    </view>

    <!-- Banner 轮播图 -->
    <view class="banner-section">
      <swiper
        class="banner-swiper"
        :indicator-dots="true"
        :autoplay="true"
        :interval="3000"
        :duration="500"
        indicator-color="rgba(255, 255, 255, 0.5)"
        indicator-active-color="#ffffff"
      >
        <swiper-item v-for="banner in bannerList" :key="banner.id" @click="handleBannerClick(banner)">
          <image class="banner-image" :src="banner.imageUrl" mode="aspectFill" />
        </swiper-item>
      </swiper>
    </view>

    <!-- 正在热映 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">正在热映</text>
        <view class="section-more" @click="goToMovieList('now-playing')">
          <text>全部</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
      </view>
      <scroll-view class="movie-scroll" scroll-x show-scrollbar="false">
        <view class="movie-list">
          <movie-card
            v-for="movie in nowPlayingList"
            :key="movie.id"
            :movie="movie"
            @click="goToMovieDetail"
          />
        </view>
      </scroll-view>
    </view>

    <!-- 即将上映 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">即将上映</text>
        <view class="section-more" @click="goToMovieList('coming-soon')">
          <text>全部</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
      </view>
      <scroll-view class="movie-scroll" scroll-x show-scrollbar="false">
        <view class="movie-list">
          <movie-card
            v-for="movie in comingSoonList"
            :key="movie.id"
            :movie="movie"
            @click="goToMovieDetail"
          />
        </view>
      </scroll-view>
    </view>

    <!-- 热门推荐 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">热门推荐</text>
      </view>
      <view class="hot-movie-list">
        <view
          v-for="movie in hotMovieList"
          :key="movie.id"
          class="hot-movie-item"
          @click="goToMovieDetail(movie)"
        >
          <image class="hot-movie-poster" :src="movie.poster" mode="aspectFill" />
          <view class="hot-movie-info">
            <text class="hot-movie-title">{{ movie.title }}</text>
            <text class="hot-movie-category">{{ movie.categoryNames }}</text>
            <view class="hot-movie-score">
              <text v-if="movie.score" class="score">{{ movie.score }}分</text>
              <text v-else class="want">{{ movie.wantCount }}人想看</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <loading :type="loadingType" text="加载中..." />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAppStore } from '../../stores/app.js'
import { getBanners, getHotMovies } from '../../api/home.js'
import { getNowPlaying, getComingSoon } from '../../api/movie.js'
import MovieCard from '../../components/movie/MovieCard.vue'
import Loading from '../../components/common/Loading.vue'

const appStore = useAppStore()

// 数据
const bannerList = ref([])
const nowPlayingList = ref([])
const comingSoonList = ref([])
const hotMovieList = ref([])
const loadingType = ref('loading')

onMounted(() => {
  loadData()
})

/**
 * 加载数据
 */
const loadData = async () => {
  try {
    loadingType.value = 'loading'

    // 并行请求所有数据
    const [bannersRes, nowPlayingRes, comingSoonRes, hotMoviesRes] = await Promise.all([
      getBanners(),
      getNowPlaying({ page: 1, pageSize: 10 }),
      getComingSoon({ page: 1, pageSize: 10 }),
      getHotMovies(10)
    ])

    // 设置数据
    bannerList.value = bannersRes.data?.list || []
    nowPlayingList.value = nowPlayingRes.data?.list || []
    comingSoonList.value = comingSoonRes.data?.list || []
    hotMovieList.value = hotMoviesRes.data?.list || []

    loadingType.value = ''
  } catch (error) {
    console.error('加载数据失败:', error)
    loadingType.value = 'error'
  }
}

/**
 * 显示城市选择器
 */
const showCityPicker = () => {
  // TODO: 实现城市选择器
  uni.showToast({
    title: '城市选择功能开发中',
    icon: 'none'
  })
}

/**
 * 跳转到搜索页面
 */
const goToSearch = () => {
  uni.navigateTo({
    url: '/pages/search/search'
  })
}

/**
 * 跳转到电影列表
 */
const goToMovieList = (type) => {
  uni.navigateTo({
    url: `/pages/movie/movie?type=${type}`
  })
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
 * 点击 Banner
 */
const handleBannerClick = (banner) => {
  if (banner.linkType === 1) {
    // 电影详情
    uni.navigateTo({
      url: `/pages/movie/detail?id=${banner.linkId}`
    })
  } else if (banner.linkType === 2) {
    // 活动页面
    // TODO: 实现活动页面跳转
  }
}
</script>

<style scoped>
.index-page {
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 顶部 */
.header {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #ff5a00;
}

.city {
  display: flex;
  align-items: center;
  margin-right: 20rpx;
}

.city-name {
  color: #fff;
  font-size: 28rpx;
  font-weight: 500;
}

.icon-arrow-down {
  color: #fff;
  font-size: 24rpx;
  margin-left: 8rpx;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 30rpx;
  padding: 12rpx 24rpx;
}

.icon-search {
  color: #999;
  font-size: 28rpx;
  margin-right: 12rpx;
}

.placeholder {
  color: #999;
  font-size: 26rpx;
}

/* Banner */
.banner-section {
  width: 100%;
  height: 300rpx;
  background-color: #ff5a00;
}

.banner-swiper {
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
}

/* 区块 */
.section {
  background-color: #fff;
  margin-bottom: 20rpx;
  padding: 30rpx 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.section-more {
  display: flex;
  align-items: center;
}

.section-more text {
  font-size: 24rpx;
  color: #999;
}

.icon-arrow-right {
  font-size: 24rpx;
  color: #999;
  margin-left: 4rpx;
}

/* 电影滚动列表 */
.movie-scroll {
  width: 100%;
  white-space: nowrap;
}

.movie-list {
  display: inline-flex;
  padding: 0 30rpx;
}

/* 热门推荐 */
.hot-movie-list {
  padding: 0 30rpx;
}

.hot-movie-item {
  display: flex;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.hot-movie-item:last-child {
  border-bottom: none;
}

.hot-movie-poster {
  width: 120rpx;
  height: 160rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
  background-color: #f5f5f5;
}

.hot-movie-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.hot-movie-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 10rpx;
}

.hot-movie-category {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.hot-movie-score .score {
  font-size: 24rpx;
  color: #ff5a00;
  font-weight: 500;
}

.hot-movie-score .want {
  font-size: 24rpx;
  color: #999;
}

/* iconfont 图标（临时用文字代替） */
.iconfont {
  font-family: 'iconfont';
}

.icon-arrow-down::before {
  content: '▼';
}

.icon-arrow-right::before {
  content: '›';
}

.icon-search::before {
  content: '🔍';
}
</style>

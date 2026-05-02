<template>
  <view class="movie-detail-page">
    <!-- 电影海报背景 -->
    <view class="movie-bg" :style="{ backgroundImage: `url(${movieDetail.poster})` }">
      <view class="bg-mask"></view>
    </view>

    <!-- 电影信息 -->
    <view class="movie-header">
      <image class="movie-poster" :src="movieDetail.poster" mode="aspectFill" />
      <view class="movie-info">
        <text class="movie-title">{{ movieDetail.title }}</text>
        <text class="movie-english-title">{{ movieDetail.englishTitle }}</text>
        <view class="movie-score">
          <text class="score-value">{{ movieDetail.score }}</text>
          <text class="score-count">{{ movieDetail.scoreCount }}人评分</text>
        </view>
      </view>
    </view>

    <!-- 电影基本信息 -->
    <view class="movie-basic">
      <view class="basic-item">
        <text class="basic-label">类型</text>
        <text class="basic-value">{{ movieDetail.categoryNames }}</text>
      </view>
      <view class="basic-item">
        <text class="basic-label">时长</text>
        <text class="basic-value">{{ movieDetail.duration }}分钟</text>
      </view>
      <view class="basic-item">
        <text class="basic-label">地区</text>
        <text class="basic-value">{{ movieDetail.region }}</text>
      </view>
      <view class="basic-item">
        <text class="basic-label">语言</text>
        <text class="basic-value">{{ movieDetail.language }}</text>
      </view>
      <view class="basic-item">
        <text class="basic-label">上映</text>
        <text class="basic-value">{{ movieDetail.releaseDate }}</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="movie-actions">
      <view class="action-btn" :class="{ active: isFavorite }" @click="handleFavorite">
        <text class="iconfont icon-favorite"></text>
        <text>{{ isFavorite ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="action-btn" @click="handleWantSee">
        <text class="iconfont icon-want"></text>
        <text>想看</text>
      </view>
      <view class="action-btn" @click="handleShare">
        <text class="iconfont icon-share"></text>
        <text>分享</text>
      </view>
    </view>

    <!-- 剧情简介 -->
    <view class="section">
      <view class="section-title">剧情简介</view>
      <view class="synopsis" :class="{ expanded: isSynopsisExpanded }">
        <text>{{ movieDetail.synopsis || '暂无简介' }}</text>
      </view>
      <view v-if="movieDetail.synopsis && movieDetail.synopsis.length > 100" class="expand-btn" @click="toggleSynopsis">
        <text>{{ isSynopsisExpanded ? '收起' : '展开' }}</text>
      </view>
    </view>

    <!-- 演职人员 -->
    <view class="section">
      <view class="section-title">演职人员</view>
      <scroll-view class="actor-scroll" scroll-x>
        <view class="actor-list">
          <view v-for="actor in actorList" :key="actor.id" class="actor-item">
            <image class="actor-avatar" :src="actor.avatar || '/static/images/default-avatar.png'" mode="aspectFill" />
            <text class="actor-name">{{ actor.name }}</text>
            <text class="actor-role">{{ actor.role }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 影评 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">影评</text>
        <view class="write-review-btn" @click="goToWriteReview">
          <text>写影评</text>
        </view>
      </view>

      <view class="review-list">
        <view v-for="review in reviewList" :key="review.id" class="review-item">
          <view class="review-header">
            <image class="review-avatar" :src="review.avatar" mode="aspectFill" />
            <view class="review-user-info">
              <text class="review-nickname">{{ review.nickname }}</text>
              <view class="review-score">
                <text v-for="i in 10" :key="i" class="star" :class="{ active: i <= review.score }">★</text>
              </view>
            </view>
          </view>
          <view class="review-content">
            <text>{{ review.content }}</text>
          </view>
          <view class="review-footer">
            <text class="review-time">{{ review.createTime }}</text>
            <view class="review-actions">
              <view class="like-btn" @click="handleLikeReview(review)">
                <text class="iconfont icon-like" :class="{ active: review.isLiked }"></text>
                <text>{{ review.likeCount }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-if="reviewList.length > 0" class="more-reviews-btn" @click="goToReviewList">
        <text>查看全部影评</text>
      </view>
    </view>

    <!-- 底部购票按钮 -->
    <view class="bottom-bar">
      <view v-if="movieDetail.status === 2" class="buy-btn" @click="goToSelectCinema">
        <text>选座购票</text>
      </view>
      <view v-else class="want-btn">
        <text>{{ movieDetail.wantCount }}人想看</text>
      </view>
    </view>

    <!-- 加载状态 -->
    <loading v-if="loadingType === 'loading'" type="loading" text="加载中..." />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMovieDetail, getMovieActors, getMovieReviews } from '../../api/movie.js'
import Loading from '../../components/common/Loading.vue'

// 数据
const movieId = ref(null)
const movieDetail = ref({})
const actorList = ref([])
const reviewList = ref([])
const isFavorite = ref(false)
const isSynopsisExpanded = ref(false)
const loadingType = ref('loading')

onMounted(() => {
  // 获取电影ID
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}

  movieId.value = options.id

  if (movieId.value) {
    loadMovieDetail()
  }
})

/**
 * 加载电影详情
 */
const loadMovieDetail = async () => {
  try {
    loadingType.value = 'loading'

    // 并行请求
    const [detailRes, actorsRes, reviewsRes] = await Promise.all([
      getMovieDetail(movieId.value),
      getMovieActors(movieId.value),
      getMovieReviews(movieId.value, { page: 1, pageSize: 3 })
    ])

    movieDetail.value = detailRes.data || {}
    actorList.value = actorsRes.data?.list || []
    reviewList.value = reviewsRes.data?.list || []
    isFavorite.value = detailRes.data?.isFavorite || false

    loadingType.value = ''
  } catch (error) {
    console.error('加载电影详情失败:', error)
    loadingType.value = 'error'
  }
}

/**
 * 展开/收起简介
 */
const toggleSynopsis = () => {
  isSynopsisExpanded.value = !isSynopsisExpanded.value
}

/**
 * 收藏/取消收藏
 */
const handleFavorite = () => {
  // TODO: 实现收藏功能
  isFavorite.value = !isFavorite.value
  uni.showToast({
    title: isFavorite.value ? '收藏成功' : '取消收藏',
    icon: 'success'
  })
}

/**
 * 想看
 */
const handleWantSee = () => {
  // TODO: 实现想看功能
  uni.showToast({
    title: '标记成功',
    icon: 'success'
  })
}

/**
 * 分享
 */
const handleShare = () => {
  // TODO: 实现分享功能
  uni.showToast({
    title: '分享功能开发中',
    icon: 'none'
  })
}

/**
 * 点赞影评
 */
const handleLikeReview = (review) => {
  // TODO: 实现点赞功能
  review.isLiked = !review.isLiked
  review.likeCount += review.isLiked ? 1 : -1
}

/**
 * 跳转到选择影院
 */
const goToSelectCinema = () => {
  uni.navigateTo({
    url: `/pages/cinema/list?movieId=${movieId.value}`
  })
}

/**
 * 写影评
 */
const goToWriteReview = () => {
  uni.navigateTo({
    url: `/pages/review/write?movieId=${movieId.value}`
  })
}

/**
 * 查看全部影评
 */
const goToReviewList = () => {
  uni.navigateTo({
    url: `/pages/review/list?movieId=${movieId.value}`
  })
}
</script>

<style scoped>
.movie-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

/* 海报背景 */
.movie-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 500rpx;
  background-size: cover;
  background-position: center;
  filter: blur(20px);
  z-index: 0;
}

.bg-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

/* 电影头部信息 */
.movie-header {
  position: relative;
  z-index: 1;
  display: flex;
  padding: 40rpx 30rpx;
}

.movie-poster {
  width: 200rpx;
  height: 280rpx;
  border-radius: 8rpx;
  margin-right: 30rpx;
}

.movie-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.movie-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 10rpx;
}

.movie-english-title {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 20rpx;
}

.movie-score {
  display: flex;
  align-items: baseline;
}

.score-value {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffcc00;
  margin-right: 10rpx;
}

.score-count {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* 基本信息 */
.movie-basic {
  position: relative;
  z-index: 1;
  background-color: #fff;
  margin: 0 20rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.basic-item {
  display: flex;
  margin-bottom: 16rpx;
}

.basic-item:last-child {
  margin-bottom: 0;
}

.basic-label {
  width: 100rpx;
  font-size: 26rpx;
  color: #999;
}

.basic-value {
  flex: 1;
  font-size: 26rpx;
  color: #333;
}

/* 操作按钮 */
.movie-actions {
  display: flex;
  justify-content: space-around;
  background-color: #fff;
  margin: 0 20rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx 0;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-btn .iconfont {
  font-size: 40rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.action-btn text {
  font-size: 24rpx;
  color: #666;
}

.action-btn.active .iconfont {
  color: #ff5a00;
}

.action-btn.active text {
  color: #ff5a00;
}

/* 区块 */
.section {
  background-color: #fff;
  margin: 0 20rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

/* 简介 */
.synopsis {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.synopsis.expanded {
  -webkit-line-clamp: unset;
}

.synopsis text {
  font-size: 26rpx;
  color: #666;
  line-height: 40rpx;
}

.expand-btn {
  text-align: center;
  margin-top: 20rpx;
}

.expand-btn text {
  font-size: 24rpx;
  color: #ff5a00;
}

/* 演职人员 */
.actor-scroll {
  width: 100%;
  white-space: nowrap;
}

.actor-list {
  display: inline-flex;
}

.actor-item {
  width: 140rpx;
  margin-right: 20rpx;
  text-align: center;
}

.actor-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  margin-bottom: 12rpx;
}

.actor-name {
  display: block;
  font-size: 24rpx;
  color: #333;
  margin-bottom: 4rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.actor-role {
  display: block;
  font-size: 20rpx;
  color: #999;
}

/* 影评 */
.write-review-btn {
  padding: 10rpx 20rpx;
  border: 1rpx solid #ff5a00;
  border-radius: 30rpx;
}

.write-review-btn text {
  font-size: 24rpx;
  color: #ff5a00;
}

.review-list {
  margin-bottom: 20rpx;
}

.review-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  margin-bottom: 16rpx;
}

.review-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  margin-right: 16rpx;
}

.review-user-info {
  flex: 1;
}

.review-nickname {
  display: block;
  font-size: 26rpx;
  color: #333;
  margin-bottom: 8rpx;
}

.review-score .star {
  font-size: 24rpx;
  color: #e0e0e0;
  margin-right: 2rpx;
}

.review-score .star.active {
  color: #ffcc00;
}

.review-content {
  margin-bottom: 16rpx;
}

.review-content text {
  font-size: 26rpx;
  color: #666;
  line-height: 40rpx;
}

.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.review-time {
  font-size: 22rpx;
  color: #999;
}

.review-actions {
  display: flex;
}

.like-btn {
  display: flex;
  align-items: center;
}

.like-btn .iconfont {
  font-size: 32rpx;
  color: #999;
  margin-right: 8rpx;
}

.like-btn text {
  font-size: 24rpx;
  color: #999;
}

.like-btn .iconfont.active {
  color: #ff5a00;
}

.more-reviews-btn {
  text-align: center;
  padding: 20rpx 0;
}

.more-reviews-btn text {
  font-size: 26rpx;
  color: #ff5a00;
}

/* 底部栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 20rpx 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.buy-btn {
  background-color: #ff5a00;
  border-radius: 40rpx;
  padding: 20rpx 0;
  text-align: center;
}

.buy-btn text {
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
}

.want-btn {
  background-color: #f5f5f5;
  border-radius: 40rpx;
  padding: 20rpx 0;
  text-align: center;
}

.want-btn text {
  color: #666;
  font-size: 28rpx;
}

/* iconfont 图标（临时用文字代替） */
.iconfont {
  font-family: 'iconfont';
}

.icon-favorite::before {
  content: '♥';
}

.icon-want::before {
  content: '★';
}

.icon-share::before {
  content: '↗';
}

.icon-like::before {
  content: '👍';
}
</style>

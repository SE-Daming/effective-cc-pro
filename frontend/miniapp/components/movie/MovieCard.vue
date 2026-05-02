<template>
  <view class="movie-card" @click="handleClick">
    <!-- 电影海报 -->
    <view class="poster-wrapper">
      <image
        class="poster"
        :src="movie.poster || '/static/images/default-poster.png'"
        mode="aspectFill"
        lazy-load
      />
      <!-- 评分 -->
      <view v-if="movie.score" class="score">
        <text class="score-text">{{ movie.score }}</text>
      </view>
      <!-- 想看标签 -->
      <view v-if="movie.wantCount" class="want-tag">
        <text>{{ formatWantCount(movie.wantCount) }}人想看</text>
      </view>
    </view>

    <!-- 电影信息 -->
    <view class="info">
      <text class="title">{{ movie.title }}</text>
      <text v-if="movie.categoryNames" class="category">{{ movie.categoryNames }}</text>
      <text v-if="movie.releaseDate" class="release-date">{{ movie.releaseDate }} 上映</text>
    </view>
  </view>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  movie: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click', props.movie)
}

/**
 * 格式化想看人数
 */
const formatWantCount = (count) => {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count
}
</script>

<style scoped>
.movie-card {
  display: inline-block;
  width: 200rpx;
  margin-right: 20rpx;
  vertical-align: top;
}

.poster-wrapper {
  position: relative;
  width: 200rpx;
  height: 280rpx;
  border-radius: 8rpx;
  overflow: hidden;
  background-color: #f5f5f5;
}

.poster {
  width: 100%;
  height: 100%;
}

.score {
  position: absolute;
  right: 0;
  top: 0;
  background: linear-gradient(135deg, #ff9500 0%, #ff5a00 100%);
  padding: 4rpx 12rpx;
  border-radius: 0 8rpx 0 12rpx;
}

.score-text {
  color: #fff;
  font-size: 24rpx;
  font-weight: bold;
}

.want-tag {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  padding: 6rpx 10rpx;
}

.want-tag text {
  color: #fff;
  font-size: 20rpx;
}

.info {
  padding: 12rpx 0;
}

.title {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8rpx;
}

.category {
  display: block;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 4rpx;
}

.release-date {
  display: block;
  font-size: 24rpx;
  color: #ff5a00;
}
</style>

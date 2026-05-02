<template>
  <view class="loading-container">
    <!-- 加载中 -->
    <view v-if="type === 'loading'" class="loading">
      <view class="loading-spinner"></view>
      <text class="loading-text">{{ text }}</text>
    </view>

    <!-- 加载更多 -->
    <view v-else-if="type === 'more'" class="load-more">
      <view class="load-more-line"></view>
      <text class="load-more-text">{{ text }}</text>
      <view class="load-more-line"></view>
    </view>

    <!-- 没有更多 -->
    <view v-else-if="type === 'nomore'" class="no-more">
      <text class="no-more-text">{{ text || '没有更多了' }}</text>
    </view>

    <!-- 空状态 -->
    <view v-else-if="type === 'empty'" class="empty">
      <image class="empty-image" src="/static/images/empty.png" mode="aspectFit" />
      <text class="empty-text">{{ text || '暂无数据' }}</text>
    </view>

    <!-- 网络错误 -->
    <view v-else-if="type === 'error'" class="error">
      <image class="error-image" src="/static/images/error.png" mode="aspectFit" />
      <text class="error-text">{{ text || '网络错误' }}</text>
      <view class="retry-btn" @click="handleRetry">
        <text>重新加载</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'loading',
    validator: (value) => ['loading', 'more', 'nomore', 'empty', 'error'].includes(value)
  },
  text: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['retry'])

const handleRetry = () => {
  emit('retry')
}
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx 0;
}

/* 加载中 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loading-spinner {
  width: 48rpx;
  height: 48rpx;
  border: 4rpx solid #e0e0e0;
  border-top-color: #ff5a00;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 16rpx;
  font-size: 24rpx;
  color: #999;
}

/* 加载更多 */
.load-more {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.load-more-line {
  flex: 1;
  height: 1rpx;
  background-color: #e0e0e0;
}

.load-more-text {
  margin: 0 20rpx;
  font-size: 24rpx;
  color: #999;
}

/* 没有更多 */
.no-more {
  display: flex;
  justify-content: center;
}

.no-more-text {
  font-size: 24rpx;
  color: #999;
}

/* 空状态 */
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

/* 网络错误 */
.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.error-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
}

.error-text {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 30rpx;
}

.retry-btn {
  padding: 16rpx 60rpx;
  background-color: #ff5a00;
  border-radius: 40rpx;
}

.retry-btn text {
  color: #fff;
  font-size: 28rpx;
}
</style>

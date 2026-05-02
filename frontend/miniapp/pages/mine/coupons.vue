<template>
	<view class="coupons-page">
		<!-- 标签栏 -->
		<view class="tabs">
			<view
				class="tab-item"
				:class="{ active: currentTab === 'available' }"
				@click="switchTab('available')"
			>
				可用
			</view>
			<view
				class="tab-item"
				:class="{ active: currentTab === 'used' }"
				@click="switchTab('used')"
			>
				已使用
			</view>
			<view
				class="tab-item"
				:class="{ active: currentTab === 'expired' }"
				@click="switchTab('expired')"
			>
				已过期
			</view>
		</view>

		<!-- 优惠券列表 -->
		<scroll-view
			class="coupon-list"
			scroll-y
			@scrolltolower="loadMore"
			:refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<view v-if="loading && !couponList.length" class="loading-wrap">
				<text>加载中...</text>
			</view>

			<view v-else-if="!couponList.length" class="empty-wrap">
				<text>暂无优惠券</text>
			</view>

			<view v-else>
				<view
					class="coupon-item"
					v-for="coupon in couponList"
					:key="coupon.id"
					:class="{ disabled: currentTab !== 'available' }"
				>
					<view class="coupon-left">
						<view class="coupon-value">
							<text class="unit">￥</text>
							<text class="amount">{{ coupon.value }}</text>
						</view>
						<view class="coupon-condition">
							满{{ coupon.minAmount }}可用
						</view>
					</view>
					<view class="coupon-right">
						<view class="coupon-name">{{ coupon.name }}</view>
						<view class="coupon-time">
							{{ coupon.startTime }} - {{ coupon.endTime }}
						</view>
						<view class="coupon-tip" v-if="coupon.scope === 1">
							全品类可用
						</view>
						<view class="coupon-tip" v-else-if="coupon.scope === 2">
							指定电影可用
						</view>
					</view>
					<view class="use-btn" v-if="currentTab === 'available'" @click="useCoupon(coupon)">
						去使用
					</view>
					<view class="status-tag" v-else-if="currentTab === 'used'">已使用</view>
					<view class="status-tag expired" v-else>已过期</view>
				</view>
			</view>

			<view class="load-more" v-if="hasMore">
				<text>{{ loading ? '加载中...' : '上拉加载更多' }}</text>
			</view>
			<view class="no-more" v-else-if="couponList.length">
				<text>没有更多了</text>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { get } from '@/api/request'

// 状态
const currentTab = ref('available')
const loading = ref(false)
const refreshing = ref(false)
const couponList = ref([])
const total = ref(0)
const page = ref(1)

// 计算属性
const hasMore = computed(() => total.value > couponList.value.length)

// 方法
const fetchCoupons = async (isRefresh = false) => {
	if (loading.value) return

	loading.value = true

	try {
		const params = {
			status: currentTab.value === 'available' ? 1 : (currentTab.value === 'used' ? 2 : 3),
			page: isRefresh ? 1 : page.value,
			pageSize: 10
		}

		const data = await get('/coupons/my', params)

		if (isRefresh) {
			couponList.value = data.list || []
			page.value = 1
		} else {
			couponList.value = [...couponList.value, ...(data.list || [])]
		}
		total.value = data.total || 0
	} finally {
		loading.value = false
		refreshing.value = false
	}
}

const loadMore = () => {
	if (!loading.value && hasMore.value) {
		page.value++
		fetchCoupons()
	}
}

const onRefresh = () => {
	refreshing.value = true
	fetchCoupons(true)
}

const switchTab = (tab) => {
	currentTab.value = tab
	couponList.value = []
	total.value = 0
	page.value = 1
	fetchCoupons(true)
}

const useCoupon = (coupon) => {
	// 跳转到首页使用优惠券
	uni.switchTab({ url: '/pages/index/index' })
}

// 生命周期
onMounted(() => {
	fetchCoupons(true)
})
</script>

<style lang="scss" scoped>
.coupons-page {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.tabs {
	display: flex;
	background-color: #ffffff;
	padding: 0 20rpx;
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 30rpx 0;
	font-size: 28rpx;
	color: #666666;
	position: relative;

	&.active {
		color: #ff5a00;
		font-weight: bold;

		&::after {
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
	}
}

.coupon-list {
	flex: 1;
	padding: 20rpx;
}

.loading-wrap, .empty-wrap {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
	font-size: 28rpx;
	color: #999999;
}

.coupon-item {
	display: flex;
	background-color: #ffffff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
	position: relative;

	&.disabled {
		opacity: 0.6;
	}
}

.coupon-left {
	width: 200rpx;
	padding: 30rpx 20rpx;
	background: linear-gradient(135deg, #ff5a00, #ff8a00);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.coupon-value {
	color: #ffffff;
	margin-bottom: 8rpx;

	.unit {
		font-size: 28rpx;
	}

	.amount {
		font-size: 56rpx;
		font-weight: bold;
	}
}

.coupon-condition {
	font-size: 22rpx;
	color: rgba(255, 255, 255, 0.8);
}

.coupon-right {
	flex: 1;
	padding: 24rpx;
}

.coupon-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 12rpx;
}

.coupon-time {
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 8rpx;
}

.coupon-tip {
	font-size: 22rpx;
	color: #ff5a00;
}

.use-btn {
	position: absolute;
	right: 24rpx;
	bottom: 24rpx;
	padding: 12rpx 32rpx;
	background-color: #ff5a00;
	color: #ffffff;
	font-size: 24rpx;
	border-radius: 24rpx;
}

.status-tag {
	position: absolute;
	right: 24rpx;
	top: 24rpx;
	padding: 8rpx 16rpx;
	background-color: #999999;
	color: #ffffff;
	font-size: 22rpx;
	border-radius: 4rpx;

	&.expired {
		background-color: #cccccc;
	}
}

.load-more, .no-more {
	text-align: center;
	padding: 30rpx 0;
	font-size: 26rpx;
	color: #999999;
}
</style>

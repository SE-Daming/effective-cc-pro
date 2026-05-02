<template>
	<view class="mine-page">
		<!-- 用户信息 -->
		<view class="user-header">
			<view class="user-info" @click="handleUserClick">
				<image
					class="avatar"
					:src="userInfo?.avatar || '/static/images/default-avatar.png'"
					mode="aspectFill"
				/>
				<view class="user-detail">
					<view class="nickname" v-if="isLoggedIn">
						{{ userInfo?.nickname || '微信用户' }}
					</view>
					<view class="login-tip" v-else>
						点击登录
					</view>
					<view class="user-phone" v-if="userInfo?.phone">
						{{ maskPhone(userInfo.phone) }}
					</view>
				</view>
			</view>
			<view class="user-stats" v-if="isLoggedIn">
				<view class="stat-item" @click="goOrders('all')">
					<view class="stat-value">{{ statistics?.orderCount || 0 }}</view>
					<view class="stat-label">订单</view>
				</view>
				<view class="stat-item" @click="goCoupons">
					<view class="stat-value">{{ statistics?.couponCount || 0 }}</view>
					<view class="stat-label">优惠券</view>
				</view>
				<view class="stat-item" @click="goFavorites">
					<view class="stat-value">{{ statistics?.favoriteCount || 0 }}</view>
					<view class="stat-label">收藏</view>
				</view>
			</view>
		</view>

		<!-- 订单入口 -->
		<view class="order-entry">
			<view class="entry-header">
				<text class="entry-title">我的订单</text>
				<view class="entry-more" @click="goOrders('all')">
					<text>全部订单</text>
					<text class="iconfont icon-arrow-right"></text>
				</view>
			</view>
			<view class="entry-list">
				<view class="entry-item" @click="goOrders('unpaid')">
					<view class="entry-icon">
						<text class="iconfont icon-unpaid"></text>
						<view class="badge" v-if="unpaidCount > 0">{{ unpaidCount }}</view>
					</view>
					<text class="entry-text">待支付</text>
				</view>
				<view class="entry-item" @click="goOrders('paid')">
					<view class="entry-icon">
						<text class="iconfont icon-paid"></text>
					</view>
					<text class="entry-text">待观影</text>
				</view>
				<view class="entry-item" @click="goOrders('refunded')">
					<view class="entry-icon">
						<text class="iconfont icon-refund"></text>
					</view>
					<text class="entry-text">退款</text>
				</view>
			</view>
		</view>

		<!-- 功能列表 -->
		<view class="menu-list">
			<view class="menu-item" @click="goCoupons">
				<text class="iconfont icon-coupon"></text>
				<text class="menu-text">我的优惠券</text>
				<text class="iconfont icon-arrow-right"></text>
			</view>
			<view class="menu-item" @click="goFavorites">
				<text class="iconfont icon-favorite"></text>
				<text class="menu-text">我的收藏</text>
				<text class="iconfont icon-arrow-right"></text>
			</view>
			<view class="menu-item" @click="goHistory">
				<text class="iconfont icon-history"></text>
				<text class="menu-text">观影记录</text>
				<text class="iconfont icon-arrow-right"></text>
			</view>
		</view>

		<view class="menu-list">
			<view class="menu-item" @click="goHelp">
				<text class="iconfont icon-help"></text>
				<text class="menu-text">帮助与反馈</text>
				<text class="iconfont icon-arrow-right"></text>
			</view>
			<view class="menu-item" @click="goAbout">
				<text class="iconfont icon-about"></text>
				<text class="menu-text">关于我们</text>
				<text class="iconfont icon-arrow-right"></text>
			</view>
		</view>

		<!-- 退出登录 -->
		<view class="logout-btn" v-if="isLoggedIn" @click="handleLogout">
			退出登录
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { maskPhone } from '@/utils/format'

const userStore = useUserStore()

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const statistics = computed(() => userStore.statistics)

// 状态
const unpaidCount = ref(0)

// 方法
const fetchStatistics = async () => {
	if (userStore.isLoggedIn) {
		try {
			await userStore.fetchStatistics()
		} catch (e) {
			console.error('获取统计数据失败', e)
		}
	}
}

const handleUserClick = () => {
	if (!userStore.isLoggedIn) {
		uni.navigateTo({ url: '/pages/login/login' })
	}
}

const goOrders = (tab) => {
	if (!userStore.isLoggedIn) {
		uni.navigateTo({ url: '/pages/login/login' })
		return
	}
	uni.navigateTo({ url: `/pages/order/list?tab=${tab}` })
}

const goCoupons = () => {
	if (!userStore.isLoggedIn) {
		uni.navigateTo({ url: '/pages/login/login' })
		return
	}
	uni.navigateTo({ url: '/pages/mine/coupons' })
}

const goFavorites = () => {
	if (!userStore.isLoggedIn) {
		uni.navigateTo({ url: '/pages/login/login' })
		return
	}
	uni.navigateTo({ url: '/pages/mine/favorites' })
}

const goHistory = () => {
	uni.showToast({ title: '功能开发中', icon: 'none' })
}

const goHelp = () => {
	uni.showToast({ title: '功能开发中', icon: 'none' })
}

const goAbout = () => {
	uni.showToast({ title: '功能开发中', icon: 'none' })
}

const handleLogout = () => {
	uni.showModal({
		title: '提示',
		content: '确定要退出登录吗？',
		success: (res) => {
			if (res.confirm) {
				userStore.logout()
				uni.showToast({ title: '已退出登录', icon: 'success' })
			}
		}
	})
}

// 生命周期
onShow(() => {
	fetchStatistics()
})
</script>

<style lang="scss" scoped>
.mine-page {
	min-height: 100vh;
	background-color: #f5f5f5;
}

.user-header {
	background: linear-gradient(135deg, #ff5a00, #ff8a00);
	padding: 60rpx 30rpx 40rpx;
}

.user-info {
	display: flex;
	align-items: center;
	margin-bottom: 40rpx;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.5);
	margin-right: 30rpx;
}

.user-detail {
	flex: 1;
}

.nickname {
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 8rpx;
}

.login-tip {
	font-size: 32rpx;
	color: #ffffff;
}

.user-phone {
	font-size: 26rpx;
	color: rgba(255, 255, 255, 0.8);
}

.user-stats {
	display: flex;
	background-color: rgba(255, 255, 255, 0.2);
	border-radius: 16rpx;
	padding: 24rpx 0;
}

.stat-item {
	flex: 1;
	text-align: center;
}

.stat-value {
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 8rpx;
}

.stat-label {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.order-entry {
	background-color: #ffffff;
	margin: 20rpx;
	border-radius: 16rpx;
	padding: 24rpx;
}

.entry-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.entry-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
}

.entry-more {
	display: flex;
	align-items: center;
	font-size: 26rpx;
	color: #999999;

	.iconfont {
		font-size: 24rpx;
		margin-left: 8rpx;
	}
}

.entry-list {
	display: flex;
}

.entry-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.entry-icon {
	position: relative;
	margin-bottom: 12rpx;

	.iconfont {
		font-size: 56rpx;
		color: #ff5a00;
	}

	.badge {
		position: absolute;
		top: -8rpx;
		right: -20rpx;
		min-width: 32rpx;
		height: 32rpx;
		line-height: 32rpx;
		padding: 0 10rpx;
		background-color: #ff5a00;
		color: #ffffff;
		font-size: 20rpx;
		border-radius: 16rpx;
		text-align: center;
	}
}

.entry-text {
	font-size: 26rpx;
	color: #666666;
}

.menu-list {
	background-color: #ffffff;
	margin: 20rpx;
	border-radius: 16rpx;
	overflow: hidden;
}

.menu-item {
	display: flex;
	align-items: center;
	padding: 30rpx 24rpx;
	border-bottom: 1rpx solid #f5f5f5;

	&:last-child {
		border-bottom: none;
	}

	.iconfont:first-child {
		font-size: 40rpx;
		color: #ff5a00;
		margin-right: 24rpx;
	}

	.menu-text {
		flex: 1;
		font-size: 28rpx;
		color: #333333;
	}

	.icon-arrow-right {
		font-size: 28rpx;
		color: #cccccc;
	}
}

.logout-btn {
	margin: 40rpx 20rpx;
	padding: 30rpx 0;
	text-align: center;
	font-size: 30rpx;
	color: #ff5a00;
	background-color: #ffffff;
	border-radius: 16rpx;
}
</style>

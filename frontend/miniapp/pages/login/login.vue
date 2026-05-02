<template>
	<view class="login-page">
		<view class="login-header">
			<image class="logo" src="/static/logo.png" mode="aspectFit" />
			<text class="title">猫眼电影</text>
			<text class="subtitle">看电影，选猫眼</text>
		</view>

		<view class="login-content">
			<view class="login-btn" @click="handleWxLogin">
				<text class="iconfont icon-wechat"></text>
				<text>微信登录</text>
			</view>

			<view class="login-tips">
				登录即代表同意
				<text class="link" @click="goAgreement('user')">《用户协议》</text>
				和
				<text class="link" @click="goAgreement('privacy')">《隐私政策》</text>
			</view>
		</view>

		<view class="login-footer">
			<view class="feature-item">
				<text class="iconfont icon-movie"></text>
				<text>在线选座</text>
			</view>
			<view class="feature-item">
				<text class="iconfont icon-coupon"></text>
				<text>优惠购票</text>
			</view>
			<view class="feature-item">
				<text class="iconfont icon-service"></text>
				<text>贴心服务</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const handleWxLogin = async () => {
	try {
		uni.showLoading({ title: '登录中...' })

		await userStore.login()

		uni.hideLoading()
		uni.showToast({ title: '登录成功', icon: 'success' })

		// 返回上一页
		setTimeout(() => {
			uni.navigateBack()
		}, 1500)
	} catch (e) {
		uni.hideLoading()
		console.error('登录失败', e)
		uni.showToast({ title: '登录失败，请重试', icon: 'none' })
	}
}

const goAgreement = (type) => {
	uni.showToast({ title: '协议页面开发中', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.login-page {
	min-height: 100vh;
	display: flex;
	flex-direction: column;
	background: linear-gradient(180deg, #ff5a00, #ff8a00);
}

.login-header {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 100rpx 0 60rpx;
}

.logo {
	width: 160rpx;
	height: 160rpx;
	margin-bottom: 30rpx;
}

.title {
	font-size: 48rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 16rpx;
}

.subtitle {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.8);
}

.login-content {
	flex: 1;
	padding: 0 60rpx;
}

.login-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #ffffff;
	padding: 28rpx 0;
	border-radius: 44rpx;
	margin-bottom: 30rpx;

	.iconfont {
		font-size: 40rpx;
		color: #07c160;
		margin-right: 16rpx;
	}

	text:last-child {
		font-size: 32rpx;
		color: #333333;
	}
}

.login-tips {
	text-align: center;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);

	.link {
		color: #ffffff;
	}
}

.login-footer {
	display: flex;
	justify-content: center;
	padding: 60rpx 0;
	background-color: rgba(255, 255, 255, 0.1);
}

.feature-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 0 40rpx;

	.iconfont {
		font-size: 48rpx;
		color: #ffffff;
		margin-bottom: 12rpx;
	}

	text:last-child {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.9);
	}
}
</style>

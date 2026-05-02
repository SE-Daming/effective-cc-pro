<template>
	<view class="order-list-page">
		<!-- 标签栏 -->
		<view class="tabs">
			<view
				class="tab-item"
				:class="{ active: currentTab === 'all' }"
				@click="switchTab('all')"
			>
				全部
			</view>
			<view
				class="tab-item"
				:class="{ active: currentTab === 'unpaid' }"
				@click="switchTab('unpaid')"
			>
				待支付
			</view>
			<view
				class="tab-item"
				:class="{ active: currentTab === 'paid' }"
				@click="switchTab('paid')"
			>
				已支付
			</view>
			<view
				class="tab-item"
				:class="{ active: currentTab === 'refunded' }"
				@click="switchTab('refunded')"
			>
				已退款
			</view>
		</view>

		<!-- 订单列表 -->
		<scroll-view
			class="order-list"
			scroll-y
			@scrolltolower="loadMore"
			:refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<view v-if="loading && !orderList.length" class="loading-wrap">
				<text>加载中...</text>
			</view>

			<view v-else-if="!orderList.length" class="empty-wrap">
				<image class="empty-icon" src="/static/images/empty-order.png" mode="aspectFit" />
				<text>暂无订单</text>
				<view class="go-buy" @click="goMovie">去购票</view>
			</view>

			<view v-else>
				<OrderCard
					v-for="order in orderList"
					:key="order.id"
					:order="order"
					@click="goDetail"
					@cancel="cancelOrder"
					@pay="payOrder"
					@pick-code="showPickCode"
					@refund="handleRefund"
				/>

				<view class="load-more" v-if="hasMore">
					<text>{{ loading ? '加载中...' : '上拉加载更多' }}</text>
				</view>
				<view class="no-more" v-else-if="orderList.length">
					<text>没有更多了</text>
				</view>
			</view>
		</scroll-view>

		<!-- 取票码弹窗 -->
		<view class="popup-mask" v-if="showPickCodePopup" @click="showPickCodePopup = false">
			<view class="popup-content" @click.stop>
				<view class="popup-title">取票码</view>
				<view class="pick-code">{{ pickCodeData.pickCode }}</view>
				<image class="qrcode" :src="pickCodeData.pickQrcode" mode="aspectFit" />
				<view class="ticket-list">
					<view class="ticket-item" v-for="ticket in pickCodeData.tickets" :key="ticket.seatNo">
						<text>{{ ticket.hallName }}</text>
						<text>{{ ticket.seatNo }}</text>
					</view>
				</view>
				<view class="popup-tip">请凭取票码到影院自助取票机取票</view>
			</view>
		</view>

		<!-- 退票确认弹窗 -->
		<view class="popup-mask" v-if="showRefundPopup" @click="showRefundPopup = false">
			<view class="popup-content" @click.stop>
				<view class="popup-title">申请退票</view>
				<view class="refund-info" v-if="refundData.canRefund">
					<view class="refund-item">
						<text class="label">退票金额</text>
						<text class="value">￥{{ refundData.refundAmount }}</text>
					</view>
					<view class="refund-item" v-if="refundData.refundFee > 0">
						<text class="label">手续费</text>
						<text class="value">-￥{{ refundData.refundFee }}</text>
					</view>
					<view class="refund-item total">
						<text class="label">实际退款</text>
						<text class="value price">￥{{ refundData.actualRefund }}</text>
					</view>
					<view class="refund-reason">{{ refundData.reason }}</view>
				</view>
				<view class="refund-warning" v-else>
					<text>{{ refundData.reason }}</text>
				</view>
				<view class="popup-actions">
					<view class="popup-btn cancel" @click="showRefundPopup = false">取消</view>
					<view
						class="popup-btn confirm"
						v-if="refundData.canRefund"
						@click="confirmRefund"
					>
						确认退票
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useOrderStore } from '@/stores/order'
import { useUserStore } from '@/stores/user'
import { checkRefund, applyRefund, getPickCode, cancelOrder as cancelOrderApi } from '@/api/order'
import OrderCard from '@/components/OrderCard.vue'

const orderStore = useOrderStore()
const userStore = useUserStore()

// 状态
const loading = ref(false)
const refreshing = ref(false)
const currentTab = ref('all')
const showPickCodePopup = ref(false)
const showRefundPopup = ref(false)
const pickCodeData = ref({})
const refundData = ref({})
const refundingOrderId = ref(null)

// 计算属性
const orderList = computed(() => orderStore.orderList)
const hasMore = computed(() => orderStore.total > orderList.value.length)

// 方法
const fetchOrders = async (isRefresh = false) => {
	if (loading.value) return

	loading.value = true

	try {
		const params = {
			page: isRefresh ? 1 : Math.floor(orderList.value.length / 10) + 1,
			pageSize: 10
		}

		// 根据标签筛选状态
		if (currentTab.value === 'unpaid') {
			params.status = 1
		} else if (currentTab.value === 'paid') {
			params.status = 2
		} else if (currentTab.value === 'refunded') {
			params.status = 5
		}

		await orderStore.fetchOrderList(params)
	} finally {
		loading.value = false
		refreshing.value = false
	}
}

const loadMore = () => {
	if (!loading.value && hasMore.value) {
		fetchOrders()
	}
}

const onRefresh = () => {
	refreshing.value = true
	fetchOrders(true)
}

const switchTab = (tab) => {
	currentTab.value = tab
	orderStore.orderList = []
	orderStore.total = 0
	fetchOrders(true)
}

const goDetail = (order) => {
	uni.navigateTo({
		url: `/pages/order/detail?id=${order.id}`
	})
}

const goMovie = () => {
	uni.switchTab({
		url: '/pages/index/index'
	})
}

const cancelOrder = async (order) => {
	uni.showModal({
		title: '提示',
		content: '确定要取消该订单吗？',
		success: async (res) => {
			if (res.confirm) {
				try {
					await cancelOrderApi(order.id)
					uni.showToast({ title: '订单已取消', icon: 'success' })
					onRefresh()
				} catch (e) {
					console.error('取消订单失败', e)
				}
			}
		}
	})
}

const payOrder = (order) => {
	// TODO: 调用微信支付
	uni.showToast({ title: '支付功能开发中', icon: 'none' })
}

const showPickCode = async (order) => {
	try {
		const data = await getPickCode(order.id)
		pickCodeData.value = data
		showPickCodePopup.value = true
	} catch (e) {
		console.error('获取取票码失败', e)
	}
}

const handleRefund = async (order) => {
	try {
		const data = await checkRefund(order.id)
		refundData.value = data
		refundingOrderId.value = order.id
		showRefundPopup.value = true
	} catch (e) {
		console.error('检查退票条件失败', e)
	}
}

const confirmRefund = async () => {
	if (!refundingOrderId.value) return

	try {
		await applyRefund(refundingOrderId.value, '用户申请退票')
		uni.showToast({ title: '退票申请已提交', icon: 'success' })
		showRefundPopup.value = false
		onRefresh()
	} catch (e) {
		console.error('申请退票失败', e)
	}
}

// 生命周期
onMounted(() => {
	if (!userStore.isLoggedIn) {
		uni.navigateTo({ url: '/pages/login/login' })
		return
	}
	fetchOrders(true)
})
</script>

<style lang="scss" scoped>
.order-list-page {
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

.order-list {
	flex: 1;
	padding: 20rpx;
}

.loading-wrap, .empty-wrap {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
	font-size: 28rpx;
	color: #999999;
}

.empty-icon {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 20rpx;
}

.go-buy {
	margin-top: 30rpx;
	padding: 16rpx 60rpx;
	background-color: #ff5a00;
	color: #ffffff;
	font-size: 28rpx;
	border-radius: 44rpx;
}

.load-more, .no-more {
	text-align: center;
	padding: 30rpx 0;
	font-size: 26rpx;
	color: #999999;
}

.popup-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 100;
}

.popup-content {
	width: 600rpx;
	background-color: #ffffff;
	border-radius: 24rpx;
	padding: 40rpx;
}

.popup-title {
	text-align: center;
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 30rpx;
}

.pick-code {
	text-align: center;
	font-size: 60rpx;
	font-weight: bold;
	color: #ff5a00;
	letter-spacing: 8rpx;
	margin-bottom: 30rpx;
}

.qrcode {
	width: 300rpx;
	height: 300rpx;
	display: block;
	margin: 0 auto 30rpx;
}

.ticket-list {
	padding: 20rpx;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	margin-bottom: 20rpx;
}

.ticket-item {
	display: flex;
	justify-content: space-between;
	padding: 16rpx 0;
	font-size: 26rpx;
	color: #666666;
}

.popup-tip {
	text-align: center;
	font-size: 24rpx;
	color: #999999;
}

.refund-info {
	padding: 20rpx 0;
}

.refund-item {
	display: flex;
	justify-content: space-between;
	padding: 16rpx 0;
	font-size: 28rpx;

	&.total {
		padding-top: 24rpx;
		border-top: 1rpx solid #f5f5f5;

		.value {
			font-size: 36rpx;
		}
	}

	.label {
		color: #666666;
	}

	.value {
		color: #333333;

		&.price {
			color: #ff5a00;
			font-weight: bold;
		}
	}
}

.refund-reason {
	font-size: 24rpx;
	color: #999999;
	text-align: center;
	margin-top: 20rpx;
}

.refund-warning {
	padding: 40rpx;
	text-align: center;
	font-size: 28rpx;
	color: #ff5a00;
}

.popup-actions {
	display: flex;
	margin-top: 40rpx;
}

.popup-btn {
	flex: 1;
	text-align: center;
	padding: 24rpx 0;
	font-size: 30rpx;
	border-radius: 44rpx;

	&.cancel {
		color: #666666;
		background-color: #f5f5f5;
		margin-right: 20rpx;
	}

	&.confirm {
		color: #ffffff;
		background-color: #ff5a00;
	}
}
</style>

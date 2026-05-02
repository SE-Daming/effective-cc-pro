<template>
	<view class="order-detail-page">
		<!-- 订单状态 -->
		<view class="status-header" :style="{ backgroundColor: statusColor }">
			<view class="status-text">{{ statusText }}</view>
			<view class="status-tip" v-if="order.status === 1">
				请于 {{ order.expireTime }} 前完成支付
			</view>
			<view class="status-tip" v-else-if="order.status === 6">
				退票申请审核中，请耐心等待
			</view>
		</view>

		<!-- 电影票信息 -->
		<view class="ticket-section" v-if="order.tickets && order.tickets.length">
			<view class="section-title">电影票</view>
			<view class="ticket-card">
				<view class="movie-info">
					<image class="movie-poster" :src="order.tickets[0].moviePoster" mode="aspectFill" />
					<view class="movie-detail">
						<view class="movie-title">{{ order.tickets[0].movieTitle }}</view>
						<view class="movie-meta">
							<text>{{ order.tickets[0].showDate }}</text>
							<text class="divider">|</text>
							<text>{{ order.tickets[0].showTime }}</text>
						</view>
					</view>
				</view>

				<view class="cinema-info">
					<view class="cinema-name">{{ order.tickets[0].cinemaName }}</view>
					<view class="cinema-address">{{ order.tickets[0].cinemaAddress }}</view>
				</view>

				<view class="seat-list">
					<view class="seat-item" v-for="ticket in order.tickets" :key="ticket.id">
						<view class="seat-info">
							<text class="hall-name">{{ ticket.hallName }}</text>
							<text class="seat-no">{{ ticket.seatNo }}</text>
						</view>
						<view class="seat-price">￥{{ ticket.ticketPrice }}</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 取票码 -->
		<view class="pick-code-section" v-if="order.status === 2">
			<view class="section-title">取票码</view>
			<view class="pick-code-card" @click="showPickCodePopup = true">
				<view class="pick-code">{{ pickCode }}</view>
				<text class="pick-tip">点击查看二维码</text>
			</view>
		</view>

		<!-- 订单信息 -->
		<view class="order-section">
			<view class="section-title">订单信息</view>
			<view class="info-card">
				<view class="info-item">
					<text class="label">订单编号</text>
					<text class="value">{{ order.orderNo }}</text>
				</view>
				<view class="info-item">
					<text class="label">下单时间</text>
					<text class="value">{{ order.createTime }}</text>
				</view>
				<view class="info-item" v-if="order.payTime">
					<text class="label">支付时间</text>
					<text class="value">{{ order.payTime }}</text>
				</view>
				<view class="info-item" v-if="order.payType">
					<text class="label">支付方式</text>
					<text class="value">微信支付</text>
				</view>
			</view>
		</view>

		<!-- 金额信息 -->
		<view class="amount-section">
			<view class="section-title">金额明细</view>
			<view class="info-card">
				<view class="info-item">
					<text class="label">票价总额</text>
					<text class="value">￥{{ order.totalAmount }}</text>
				</view>
				<view class="info-item" v-if="order.discountAmount > 0">
					<text class="label">优惠金额</text>
					<text class="value discount">-￥{{ order.discountAmount }}</text>
				</view>
				<view class="info-item total">
					<text class="label">实付金额</text>
					<text class="value price">￥{{ order.payAmount }}</text>
				</view>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar safe-area-bottom">
			<view class="action-btn cancel" v-if="order.status === 1" @click="handleCancel">
				取消订单
			</view>
			<view class="action-btn pay" v-if="order.status === 1" @click="handlePay">
				立即支付
			</view>
			<view class="action-btn refund" v-if="order.status === 2" @click="handleRefund">
				申请退票
			</view>
		</view>

		<!-- 取票码弹窗 -->
		<view class="popup-mask" v-if="showPickCodePopup" @click="showPickCodePopup = false">
			<view class="popup-content" @click.stop>
				<view class="popup-title">取票码</view>
				<view class="pick-code-big">{{ pickCode }}</view>
				<image class="qrcode" :src="pickQrcode" mode="aspectFit" />
				<view class="popup-tip">请凭取票码到影院自助取票机取票</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useOrderStore } from '@/stores/order'
import { getPickCode, checkRefund, applyRefund, cancelOrder } from '@/api/order'
import { orderStatusText, orderStatusColor } from '@/utils/format'

const orderStore = useOrderStore()

// 状态
const orderId = ref(null)
const order = ref({})
const pickCode = ref('')
const pickQrcode = ref('')
const showPickCodePopup = ref(false)

// 计算属性
const statusText = computed(() => orderStatusText(order.value.status))
const statusColor = computed(() => orderStatusColor(order.value.status))

// 方法
const fetchDetail = async () => {
	try {
		uni.showLoading({ title: '加载中...' })

		const data = await orderStore.fetchOrderDetail(orderId.value)
		order.value = data

		// 如果已支付，获取取票码
		if (data.status === 2) {
			fetchPickCode()
		}

		uni.hideLoading()
	} catch (e) {
		uni.hideLoading()
		console.error('获取订单详情失败', e)
	}
}

const fetchPickCode = async () => {
	try {
		const data = await getPickCode(orderId.value)
		pickCode.value = data.pickCode
		pickQrcode.value = data.pickQrcode
	} catch (e) {
		console.error('获取取票码失败', e)
	}
}

const handleCancel = () => {
	uni.showModal({
		title: '提示',
		content: '确定要取消该订单吗？',
		success: async (res) => {
			if (res.confirm) {
				try {
					await cancelOrder(orderId.value)
					uni.showToast({ title: '订单已取消', icon: 'success' })
					fetchDetail()
				} catch (e) {
					console.error('取消订单失败', e)
				}
			}
		}
	})
}

const handlePay = () => {
	// TODO: 调用微信支付
	uni.showToast({ title: '支付功能开发中', icon: 'none' })
}

const handleRefund = async () => {
	try {
		const data = await checkRefund(orderId.value)

		if (!data.canRefund) {
			uni.showToast({ title: data.reason, icon: 'none' })
			return
		}

		uni.showModal({
			title: '申请退票',
			content: `退票金额：￥${data.actualRefund}（含手续费￥${data.refundFee}），确认退票？`,
			success: async (res) => {
				if (res.confirm) {
					await applyRefund(orderId.value, '用户申请退票')
					uni.showToast({ title: '退票申请已提交', icon: 'success' })
					fetchDetail()
				}
			}
		})
	} catch (e) {
		console.error('检查退票条件失败', e)
	}
}

// 生命周期
onLoad((options) => {
	orderId.value = options.id
	fetchDetail()
})
</script>

<style lang="scss" scoped>
.order-detail-page {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 140rpx;
}

.status-header {
	padding: 40rpx 30rpx;
	background: linear-gradient(135deg, #ff5a00, #ff8a00);
	color: #ffffff;
}

.status-text {
	font-size: 36rpx;
	font-weight: bold;
	margin-bottom: 12rpx;
}

.status-tip {
	font-size: 26rpx;
	opacity: 0.9;
}

.section-title {
	font-size: 28rpx;
	color: #999999;
	padding: 20rpx 30rpx 10rpx;
}

.ticket-section, .pick-code-section, .order-section, .amount-section {
	margin-bottom: 20rpx;
}

.ticket-card, .pick-code-card, .info-card {
	background-color: #ffffff;
	padding: 24rpx 30rpx;
}

.movie-info {
	display: flex;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.movie-poster {
	width: 100rpx;
	height: 130rpx;
	border-radius: 8rpx;
	margin-right: 20rpx;
}

.movie-detail {
	flex: 1;
}

.movie-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 12rpx;
}

.movie-meta {
	font-size: 26rpx;
	color: #666666;

	.divider {
		margin: 0 12rpx;
		color: #dddddd;
	}
}

.cinema-info {
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.cinema-name {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 8rpx;
}

.cinema-address {
	font-size: 24rpx;
	color: #999999;
}

.seat-list {
	padding-top: 20rpx;
}

.seat-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12rpx 0;
}

.seat-info {
	.hall-name {
		font-size: 26rpx;
		color: #666666;
		margin-right: 16rpx;
	}

	.seat-no {
		font-size: 26rpx;
		color: #ff5a00;
		padding: 4rpx 12rpx;
		background-color: rgba(255, 90, 0, 0.1);
		border-radius: 4rpx;
	}
}

.seat-price {
	font-size: 26rpx;
	color: #333333;
}

.pick-code-card {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 40rpx;
}

.pick-code {
	font-size: 48rpx;
	font-weight: bold;
	color: #ff5a00;
	letter-spacing: 8rpx;
}

.pick-tip {
	font-size: 24rpx;
	color: #999999;
	margin-top: 16rpx;
}

.info-item {
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

		&.discount {
			color: #07c160;
		}

		&.price {
			color: #ff5a00;
			font-weight: bold;
		}
	}
}

.bottom-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	display: flex;
	justify-content: flex-end;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	border-top: 1rpx solid #eeeeee;
}

.action-btn {
	padding: 20rpx 50rpx;
	font-size: 28rpx;
	border-radius: 44rpx;
	margin-left: 20rpx;

	&.cancel {
		color: #666666;
		background-color: #f5f5f5;
	}

	&.pay {
		color: #ffffff;
		background-color: #ff5a00;
	}

	&.refund {
		color: #ff5a00;
		border: 1rpx solid #ff5a00;
	}
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

.pick-code-big {
	text-align: center;
	font-size: 72rpx;
	font-weight: bold;
	color: #ff5a00;
	letter-spacing: 12rpx;
	margin-bottom: 30rpx;
}

.qrcode {
	width: 300rpx;
	height: 300rpx;
	display: block;
	margin: 0 auto 30rpx;
}

.popup-tip {
	text-align: center;
	font-size: 24rpx;
	color: #999999;
}
</style>

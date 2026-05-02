<template>
	<view class="order-card" @click="handleClick">
		<!-- 订单状态 -->
		<view class="order-header">
			<view class="order-no">
				<text>订单号：{{ order.orderNo }}</text>
			</view>
			<view class="order-status" :style="{ color: statusColor }">
				{{ statusText }}
			</view>
		</view>

		<!-- 电影票信息 -->
		<view class="ticket-info" v-if="order.tickets && order.tickets.length">
			<image class="movie-poster" :src="order.tickets[0].moviePoster" mode="aspectFill" />
			<view class="ticket-detail">
				<view class="movie-title">{{ order.tickets[0].movieTitle }}</view>
				<view class="cinema-name">{{ order.tickets[0].cinemaName }}</view>
				<view class="show-time">
					{{ order.tickets[0].showDate }} {{ order.tickets[0].showTime }}
				</view>
				<view class="seat-info">
					<text class="hall-name">{{ order.tickets[0].hallName }}</text>
					<text class="seat-no" v-for="ticket in order.tickets" :key="ticket.id">
						{{ ticket.seatNo }}
					</text>
				</view>
			</view>
		</view>

		<!-- 订单金额 -->
		<view class="order-footer">
			<view class="ticket-count">
				共{{ order.tickets?.length || 0 }}张
			</view>
			<view class="order-amount">
				<text class="label">实付</text>
				<text class="amount">￥{{ order.payAmount || order.totalAmount }}</text>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="order-actions" v-if="showActions">
			<view
				class="action-btn cancel"
				v-if="order.status === 1"
				@click.stop="handleCancel"
			>
				取消订单
			</view>
			<view
				class="action-btn pay"
				v-if="order.status === 1"
				@click.stop="handlePay"
			>
				立即支付
			</view>
			<view
				class="action-btn primary"
				v-if="order.status === 2"
				@click.stop="handlePickCode"
			>
				查看取票码
			</view>
			<view
				class="action-btn"
				v-if="order.status === 2"
				@click.stop="handleRefund"
			>
				申请退票
			</view>
		</view>
	</view>
</template>

<script setup>
import { computed } from 'vue'
import { orderStatusText, orderStatusColor } from '@/utils/format'

const props = defineProps({
	order: {
		type: Object,
		required: true
	},
	showActions: {
		type: Boolean,
		default: true
	}
})

const emit = defineEmits(['click', 'cancel', 'pay', 'pick-code', 'refund'])

// 计算属性
const statusText = computed(() => orderStatusText(props.order.status))
const statusColor = computed(() => orderStatusColor(props.order.status))

// 方法
const handleClick = () => {
	emit('click', props.order)
}

const handleCancel = () => {
	emit('cancel', props.order)
}

const handlePay = () => {
	emit('pay', props.order)
}

const handlePickCode = () => {
	emit('pick-code', props.order)
}

const handleRefund = () => {
	emit('refund', props.order)
}
</script>

<style lang="scss" scoped>
.order-card {
	background-color: #ffffff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	padding: 24rpx;
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.order-no {
	font-size: 24rpx;
	color: #999999;
}

.order-status {
	font-size: 26rpx;
	font-weight: bold;
}

.ticket-info {
	display: flex;
	margin-bottom: 20rpx;
}

.movie-poster {
	width: 140rpx;
	height: 180rpx;
	border-radius: 8rpx;
	margin-right: 20rpx;
	flex-shrink: 0;
}

.ticket-detail {
	flex: 1;
	min-width: 0;
}

.movie-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 12rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.cinema-name {
	font-size: 26rpx;
	color: #666666;
	margin-bottom: 8rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.show-time {
	font-size: 26rpx;
	color: #666666;
	margin-bottom: 8rpx;
}

.seat-info {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
}

.hall-name {
	font-size: 24rpx;
	color: #999999;
	margin-right: 16rpx;
}

.seat-no {
	font-size: 24rpx;
	color: #ff5a00;
	margin-right: 12rpx;
	padding: 4rpx 12rpx;
	background-color: rgba(255, 90, 0, 0.1);
	border-radius: 4rpx;
}

.order-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 20rpx;
	border-top: 1rpx solid #f5f5f5;
}

.ticket-count {
	font-size: 26rpx;
	color: #999999;
}

.order-amount {
	.label {
		font-size: 26rpx;
		color: #666666;
		margin-right: 8rpx;
	}

	.amount {
		font-size: 32rpx;
		font-weight: bold;
		color: #ff5a00;
	}
}

.order-actions {
	display: flex;
	justify-content: flex-end;
	padding-top: 20rpx;
	margin-top: 20rpx;
	border-top: 1rpx solid #f5f5f5;
}

.action-btn {
	padding: 16rpx 32rpx;
	font-size: 26rpx;
	color: #666666;
	border: 1rpx solid #dddddd;
	border-radius: 32rpx;
	margin-left: 20rpx;

	&.primary {
		color: #ff5a00;
		border-color: #ff5a00;
	}

	&.pay {
		background-color: #ff5a00;
		color: #ffffff;
		border-color: #ff5a00;
	}
}
</style>

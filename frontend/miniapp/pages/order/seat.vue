<template>
	<view class="seat-page">
		<!-- 电影场次信息 -->
		<view class="movie-info">
			<image class="movie-poster" :src="scheduleInfo.moviePoster" mode="aspectFill" />
			<view class="movie-detail">
				<view class="movie-title">{{ scheduleInfo.movieTitle }}</view>
				<view class="movie-meta">
					<text>{{ scheduleInfo.showDate }}</text>
					<text class="divider">|</text>
					<text>{{ scheduleInfo.showTime }}</text>
				</view>
				<view class="hall-info">
					<text>{{ scheduleInfo.cinemaName }}</text>
					<text class="divider">|</text>
					<text>{{ scheduleInfo.hallName }}</text>
				</view>
			</view>
		</view>

		<!-- 座位选择组件 -->
		<SeatSelector
			ref="seatSelectorRef"
			:seats="seats"
			:hallInfo="hallInfo"
			:maxSelect="maxSelect"
			:schedulePrice="scheduleInfo.price"
			@select="handleSeatSelect"
			@max-exceeded="handleMaxExceeded"
		/>

		<!-- 底部操作栏 -->
		<view class="bottom-bar safe-area-bottom">
			<view class="price-info">
				<view class="total-price" v-if="selectedSeats.length">
					<text class="label">总计：</text>
					<text class="price">￥{{ totalPrice }}</text>
				</view>
				<view class="seat-count" v-else>
					<text>请选择座位（最多{{ maxSelect }}张）</text>
				</view>
			</view>
			<view class="confirm-btn" :class="{ disabled: !selectedSeats.length || locking }" @click="confirmSeats">
				{{ locking ? '锁定中...' : '确认选座' }}
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { getScheduleSeats, lockSeats, releaseSeatLock } from '@/api/schedule'
import { useOrderStore } from '@/stores/order'
import SeatSelector from '@/components/SeatSelector.vue'

const orderStore = useOrderStore()

// 状态
const scheduleId = ref(null)
const scheduleInfo = ref({})
const hallInfo = ref({ rows: 0, cols: 0 })
const seats = ref([])
const selectedSeats = ref([])
const maxSelect = ref(4)
const locking = ref(false)
const currentLockId = ref('')
const seatSelectorRef = ref(null)

// 计算属性
const totalPrice = computed(() => {
	return selectedSeats.value.reduce((sum, seat) => sum + (seat.price || 0), 0).toFixed(2)
})

// 方法
const fetchSeats = async () => {
	try {
		uni.showLoading({ title: '加载中...' })

		const data = await getScheduleSeats(scheduleId.value)

		scheduleInfo.value = {
			movieId: data.movieInfo.id,
			movieTitle: data.movieInfo.title,
			moviePoster: data.movieInfo.poster,
			cinemaName: data.hallInfo.cinemaName || '影院',
			hallName: data.hallInfo.name,
			hallType: data.hallInfo.type,
			showDate: data.scheduleInfo.showDate,
			showTime: data.scheduleInfo.showTime,
			price: data.scheduleInfo.price
		}

		hallInfo.value = {
			rows: data.hallInfo.rows || 10,
			cols: data.hallInfo.cols || 20
		}

		seats.value = data.seats || []
		maxSelect.value = data.maxSelect || 4

		uni.hideLoading()
	} catch (e) {
		uni.hideLoading()
		console.error('获取座位图失败', e)
		uni.showToast({ title: '获取座位图失败', icon: 'none' })
	}
}

const handleSeatSelect = (seats) => {
	selectedSeats.value = seats
}

const handleMaxExceeded = (max) => {
	uni.showToast({
		title: `最多只能选择${max}个座位`,
		icon: 'none'
	})
}

const confirmSeats = async () => {
	if (!selectedSeats.value.length || locking.value) return

	locking.value = true

	try {
		// 如果之前有锁定，先释放
		if (currentLockId.value) {
			await releaseSeatLock(currentLockId.value)
		}

		// 锁定座位
		const seatIds = selectedSeats.value.map(s => s.id)
		const data = await lockSeats(scheduleId.value, seatIds)

		currentLockId.value = data.lockId

		// 保存选座信息到 store
		orderStore.setSeatSelection({
			lockId: data.lockId,
			seats: data.seats,
			totalPrice: data.totalPrice,
			expireTime: data.expireTime
		})

		// 跳转到订单确认页
		uni.redirectTo({
			url: `/pages/order/confirm?lockId=${data.lockId}`
		})
	} catch (e) {
		console.error('锁定座位失败', e)
		uni.showToast({ title: '锁定座位失败，请重试', icon: 'none' })
		// 刷新座位图
		fetchSeats()
		// 清空选择
		seatSelectorRef.value?.clearSelection()
		selectedSeats.value = []
	} finally {
		locking.value = false
	}
}

// 生命周期
onLoad((options) => {
	scheduleId.value = options.id
	fetchSeats()
})

onUnload(() => {
	// 页面退出时释放锁
	if (currentLockId.value && !orderStore.lockId) {
		releaseSeatLock(currentLockId.value)
	}
})
</script>

<style lang="scss" scoped>
.seat-page {
	display: flex;
	flex-direction: column;
	min-height: 100vh;
	background-color: #f5f5f5;
}

.movie-info {
	display: flex;
	padding: 20rpx;
	background-color: #ffffff;
	border-bottom: 1rpx solid #f5f5f5;
}

.movie-poster {
	width: 120rpx;
	height: 160rpx;
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

.movie-meta, .hall-info {
	font-size: 26rpx;
	color: #666666;
	margin-bottom: 8rpx;

	.divider {
		margin: 0 12rpx;
		color: #dddddd;
	}
}

.bottom-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	border-top: 1rpx solid #eeeeee;
}

.price-info {
	flex: 1;

	.total-price {
		.label {
			font-size: 28rpx;
			color: #666666;
		}

		.price {
			font-size: 40rpx;
			font-weight: bold;
			color: #ff5a00;
		}
	}

	.seat-count {
		font-size: 28rpx;
		color: #999999;
	}
}

.confirm-btn {
	padding: 24rpx 60rpx;
	background-color: #ff5a00;
	color: #ffffff;
	font-size: 30rpx;
	border-radius: 44rpx;

	&.disabled {
		background-color: #cccccc;
	}
}
</style>

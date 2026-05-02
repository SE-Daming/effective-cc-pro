<template>
	<view class="seat-component">
		<!-- 银幕 -->
		<view class="screen-wrap">
			<view class="screen"></view>
			<text class="screen-text">银幕中央</text>
		</view>

		<!-- 座位图 -->
		<scroll-view
			class="seat-scroll"
			scroll-x
			scroll-y
			:scroll-left="scrollLeft"
			:scroll-top="scrollTop"
		>
			<view class="seat-container" :style="containerStyle">
				<!-- 行号 -->
				<view class="row-numbers">
					<view
						class="row-number"
						v-for="row in hallInfo.rows"
						:key="row"
						:style="{ height: seatSize + 'px', lineHeight: seatSize + 'px' }"
					>
						{{ String.fromCharCode(64 + row) }}
					</view>
				</view>

				<!-- 座位区域 -->
				<view class="seat-area">
					<view
						class="seat-row"
						v-for="row in seatRows"
						:key="row.rowNum"
					>
						<view
							class="seat-item"
							v-for="seat in row.seats"
							:key="seat.id"
							:class="getSeatClass(seat)"
							:style="{ width: seatSize + 'px', height: seatSize + 'px' }"
							@click="handleSeatClick(seat)"
						>
							<text v-if="seat.seatType === 2" class="couple-icon">情侣</text>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 图例 -->
		<view class="legend">
			<view class="legend-item">
				<view class="legend-seat available"></view>
				<text>可选</text>
			</view>
			<view class="legend-item">
				<view class="legend-seat selected"></view>
				<text>已选</text>
			</view>
			<view class="legend-item">
				<view class="legend-seat sold"></view>
				<text>已售</text>
			</view>
			<view class="legend-item">
				<view class="legend-seat couple"></view>
				<text>情侣座</text>
			</view>
		</view>

		<!-- 已选座位 -->
		<view class="selected-info" v-if="selectedSeats.length">
			<view class="selected-list">
				<view class="selected-item" v-for="seat in selectedSeats" :key="seat.id">
					<text class="seat-name">{{ seat.seatNo }}</text>
					<text class="seat-price">￥{{ seat.price }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
	// 座位列表
	seats: {
		type: Array,
		default: () => []
	},
	// 影厅信息
	hallInfo: {
		type: Object,
		default: () => ({ rows: 0, cols: 0 })
	},
	// 最大可选数量
	maxSelect: {
		type: Number,
		default: 4
	},
	// 座位尺寸
	seatSize: {
		type: Number,
		default: 30
	},
	// 座位间距
	seatGap: {
		type: Number,
		default: 4
	}
})

const emit = defineEmits(['select', 'max-exceeded'])

// 状态
const selectedSeats = ref([])
const scrollLeft = ref(0)
const scrollTop = ref(0)

// 计算属性
const containerStyle = computed(() => {
	const width = props.hallInfo.cols * (props.seatSize + props.seatGap) + 40
	const height = props.hallInfo.rows * (props.seatSize + props.seatGap)
	return {
		width: `${width}px`,
		minHeight: `${height}px`
	}
})

// 按行组织座位
const seatRows = computed(() => {
	const rows = []
	const maxRow = props.hallInfo.rows || 10
	const maxCol = props.hallInfo.cols || 20

	for (let r = 1; r <= maxRow; r++) {
		const rowSeats = props.seats
			.filter(seat => seat.rowNum === r)
			.sort((a, b) => a.colNum - b.colNum)

		// 填充空座位
		const seats = []
		for (let c = 1; c <= maxCol; c++) {
			const seat = rowSeats.find(s => s.colNum === c)
			if (seat) {
				seats.push(seat)
			} else {
				// 空座位（走廊等）
				seats.push({
					id: `empty-${r}-${c}`,
					rowNum: r,
					colNum: c,
					seatNo: '',
					seatType: 0,
					status: 0,
					lockStatus: 0,
					isEmpty: true
				})
			}
		}

		rows.push({
			rowNum: r,
			seats
		})
	}

	return rows
})

// 方法
const getSeatClass = (seat) => {
	if (seat.isEmpty) return 'empty'

	const classes = []

	// 座位类型
	if (seat.seatType === 2) {
		classes.push('couple')
	}

	// 座位状态
	if (isSelected(seat)) {
		classes.push('selected')
	} else if (seat.lockStatus === 2) {
		classes.push('sold')
	} else if (seat.lockStatus === 1) {
		classes.push('locked')
	} else if (seat.status === 0) {
		classes.push('unavailable')
	} else {
		classes.push('available')
	}

	return classes.join(' ')
}

const isSelected = (seat) => {
	return selectedSeats.value.some(s => s.id === seat.id)
}

const handleSeatClick = (seat) => {
	// 空座位不可点击
	if (seat.isEmpty) return

	// 不可选座位
	if (seat.lockStatus !== 0 || seat.status !== 1) {
		return
	}

	// 已选中则取消
	if (isSelected(seat)) {
		selectedSeats.value = selectedSeats.value.filter(s => s.id !== seat.id)
		emit('select', selectedSeats.value)
		return
	}

	// 检查是否超过最大数量
	if (selectedSeats.value.length >= props.maxSelect) {
		emit('max-exceeded', props.maxSelect)
		return
	}

	// 情侣座需要同时选中两个座位
	if (seat.seatType === 2) {
		// 查找相邻的情侣座
		const coupleSeat = props.seats.find(s =>
			s.rowNum === seat.rowNum &&
			s.seatType === 2 &&
			s.id !== seat.id &&
			Math.abs(s.colNum - seat.colNum) === 1
		)

		if (coupleSeat && coupleSeat.lockStatus === 0 && coupleSeat.status === 1) {
			// 检查是否超过最大数量
			if (selectedSeats.value.length + 2 > props.maxSelect) {
				emit('max-exceeded', props.maxSelect)
				return
			}

			selectedSeats.value.push({
				...seat,
				price: props.schedulePrice || 68
			})
			selectedSeats.value.push({
				...coupleSeat,
				price: props.schedulePrice || 68
			})
		}
	} else {
		selectedSeats.value.push({
			...seat,
			price: props.schedulePrice || 68
		})
	}

	emit('select', selectedSeats.value)
}

const clearSelection = () => {
	selectedSeats.value = []
}

// 暴露方法
defineExpose({
	clearSelection,
	selectedSeats
})

// 初始化滚动位置（居中显示）
onMounted(() => {
	setTimeout(() => {
		const containerWidth = props.hallInfo.cols * (props.seatSize + props.seatGap)
		// scrollLeft.value = Math.max(0, (containerWidth - 375) / 2)
	}, 100)
})
</script>

<style lang="scss" scoped>
.seat-component {
	background-color: #ffffff;
}

.screen-wrap {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 0;
	background: linear-gradient(to bottom, #e8e8e8, #ffffff);
}

.screen {
	width: 60%;
	height: 16rpx;
	background: linear-gradient(to bottom, #c0c0c0, #e8e8e8);
	border-radius: 0 0 50% 50%;
}

.screen-text {
	font-size: 24rpx;
	color: #999999;
	margin-top: 16rpx;
}

.seat-scroll {
	height: 400rpx;
	background-color: #f8f8f8;
}

.seat-container {
	display: flex;
	padding: 20rpx;
}

.row-numbers {
	display: flex;
	flex-direction: column;
	margin-right: 10rpx;
}

.row-number {
	font-size: 22rpx;
	color: #999999;
	text-align: center;
}

.seat-area {
	flex: 1;
}

.seat-row {
	display: flex;
	justify-content: flex-start;
	margin-bottom: 4rpx;
}

.seat-item {
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 4rpx;
	border-radius: 4rpx;
	transition: all 0.2s;

	&.empty {
		background-color: transparent;
	}

	&.available {
		background-color: #ffffff;
		border: 2rpx solid #dddddd;

		&:active {
			background-color: #ff5a00;
			border-color: #ff5a00;
		}
	}

	&.selected {
		background-color: #ff5a00;
		border: 2rpx solid #ff5a00;
	}

	&.sold {
		background-color: #cccccc;
		border: 2rpx solid #cccccc;
	}

	&.locked {
		background-color: #e5e5e5;
		border: 2rpx solid #e5e5e5;
	}

	&.unavailable {
		background-color: #f5f5f5;
		border: 2rpx solid #f5f5f5;
	}

	&.couple {
		width: 64rpx !important;

		&.available {
			background-color: #fff0f0;
			border-color: #ffcccc;
		}

		&.selected {
			background-color: #ff5a00;
			border-color: #ff5a00;
		}
	}

	.couple-icon {
		font-size: 18rpx;
		color: #ff5a00;

		.selected & {
			color: #ffffff;
		}
	}
}

.legend {
	display: flex;
	justify-content: center;
	padding: 20rpx;
	background-color: #ffffff;
}

.legend-item {
	display: flex;
	align-items: center;
	margin: 0 20rpx;
	font-size: 22rpx;
	color: #999999;
}

.legend-seat {
	width: 32rpx;
	height: 32rpx;
	border-radius: 4rpx;
	margin-right: 8rpx;

	&.available {
		background-color: #ffffff;
		border: 2rpx solid #dddddd;
	}

	&.selected {
		background-color: #ff5a00;
		border: 2rpx solid #ff5a00;
	}

	&.sold {
		background-color: #cccccc;
		border: 2rpx solid #cccccc;
	}

	&.couple {
		background-color: #fff0f0;
		border: 2rpx solid #ffcccc;
	}
}

.selected-info {
	padding: 20rpx;
	background-color: #ffffff;
	border-top: 1rpx solid #f5f5f5;
}

.selected-list {
	display: flex;
	flex-wrap: wrap;
}

.selected-item {
	display: flex;
	align-items: center;
	padding: 12rpx 20rpx;
	margin-right: 16rpx;
	margin-bottom: 12rpx;
	background-color: #f5f5f5;
	border-radius: 8rpx;
}

.seat-name {
	font-size: 26rpx;
	color: #333333;
	margin-right: 12rpx;
}

.seat-price {
	font-size: 24rpx;
	color: #ff5a00;
}
</style>

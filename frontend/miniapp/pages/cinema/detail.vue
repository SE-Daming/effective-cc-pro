<template>
	<view class="cinema-detail">
		<!-- 影院信息 -->
		<view class="cinema-header">
			<view class="cinema-name">{{ cinema.name }}</view>
			<view class="cinema-address">
				<text class="iconfont icon-location"></text>
				<text>{{ cinema.address }}</text>
			</view>
			<view class="cinema-contact" v-if="cinema.phone">
				<text class="iconfont icon-phone"></text>
				<text>{{ cinema.phone }}</text>
			</view>
			<view class="cinema-facilities" v-if="cinema.facilities">
				<text class="facility-tag" v-for="(item, index) in cinema.facilities.split(',')" :key="index">
					{{ item }}
				</text>
			</view>
		</view>

		<!-- 日期选择 -->
		<scroll-view class="date-scroll" scroll-x>
			<view class="date-list">
				<view
					class="date-item"
					:class="{ active: selectedDate === item.date }"
					v-for="item in dateList"
					:key="item.date"
					@click="selectDate(item.date)"
				>
					<view class="weekday">{{ item.weekday }}</view>
					<view class="date">{{ formatDateText(item.date) }}</view>
				</view>
			</view>
		</scroll-view>

		<!-- 排片列表 -->
		<view class="schedule-list">
			<view v-if="loading" class="loading-wrap">
				<text>加载中...</text>
			</view>

			<view v-else-if="!scheduleList.length" class="empty-wrap">
				<text>暂无排片信息</text>
			</view>

			<view v-else>
				<view class="movie-group" v-for="movie in scheduleList" :key="movie.movieId">
					<!-- 电影信息 -->
					<view class="movie-info" @click="goMovieDetail(movie.movieId)">
						<image class="movie-poster" :src="movie.moviePoster" mode="aspectFill" />
						<view class="movie-detail">
							<view class="movie-title">{{ movie.movieTitle }}</view>
							<view class="movie-meta">
								<text class="movie-score" v-if="movie.score">{{ movie.score }}分</text>
								<text class="movie-duration">{{ movie.duration }}分钟</text>
							</view>
						</view>
						<text class="iconfont icon-arrow-right"></text>
					</view>

					<!-- 场次列表 -->
					<scroll-view class="schedule-scroll" scroll-x>
						<view class="schedule-items">
							<view
								class="schedule-item"
								v-for="schedule in movie.schedules"
								:key="schedule.id"
								@click="goSeat(schedule.id)"
							>
								<view class="schedule-time">{{ schedule.showTime }}</view>
								<view class="schedule-end">{{ schedule.endTime }}散场</view>
								<view class="schedule-hall">{{ schedule.hallName }}</view>
								<view class="schedule-type">{{ schedule.hallType }}</view>
								<view class="schedule-price">￥{{ schedule.price }}</view>
								<view class="schedule-seats">
									{{ schedule.soldSeats }}/{{ schedule.totalSeats }}
								</view>
							</view>
						</view>
					</scroll-view>
				</view>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar safe-area-bottom">
			<view class="action-btn favorite" :class="{ active: isFavorite }" @click="toggleFavorite">
				<text class="iconfont" :class="isFavorite ? 'icon-favorite-fill' : 'icon-favorite'"></text>
				<text>{{ isFavorite ? '已收藏' : '收藏' }}</text>
			</view>
			<view class="action-btn phone" @click="callPhone" v-if="cinema.phone">
				<text class="iconfont icon-phone"></text>
				<text>电话</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useCinemaStore } from '@/stores/cinema'
import { useUserStore } from '@/stores/user'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/favorite'

const cinemaStore = useCinemaStore()
const userStore = useUserStore()

// 状态
const cinemaId = ref(null)
const cinema = ref({})
const dateList = ref([])
const scheduleList = ref([])
const selectedDate = ref('')
const loading = ref(false)
const isFavorite = ref(false)

// 方法
const fetchDetail = async () => {
	try {
		const data = await cinemaStore.fetchCinemaDetail(cinemaId.value)
		cinema.value = data

		// 检查收藏状态
		if (userStore.isLoggedIn) {
			const result = await checkFavorite(2, cinemaId.value)
			isFavorite.value = result.isFavorite
		}
	} catch (e) {
		console.error('获取影院详情失败', e)
	}
}

const fetchScheduleDates = async () => {
	try {
		const data = await cinemaStore.fetchScheduleDates(cinemaId.value)
		dateList.value = data.list || []

		// 默认选择今天
		if (dateList.value.length > 0) {
			selectedDate.value = dateList.value[0].date
			fetchSchedules()
		}
	} catch (e) {
		console.error('获取排片日期失败', e)
	}
}

const fetchSchedules = async () => {
	if (!selectedDate.value) return

	loading.value = true
	try {
		const data = await cinemaStore.fetchSchedules(cinemaId.value, {
			date: selectedDate.value
		})
		scheduleList.value = data.list || []
	} catch (e) {
		console.error('获取排片列表失败', e)
	} finally {
		loading.value = false
	}
}

const selectDate = (date) => {
	selectedDate.value = date
	fetchSchedules()
}

const formatDateText = (date) => {
	const d = new Date(date)
	const today = new Date()
	const tomorrow = new Date(today)
	tomorrow.setDate(tomorrow.getDate() + 1)

	if (d.toDateString() === today.toDateString()) {
		return '今天'
	} else if (d.toDateString() === tomorrow.toDateString()) {
		return '明天'
	}

	return `${d.getMonth() + 1}月${d.getDate()}日`
}

const goMovieDetail = (movieId) => {
	uni.navigateTo({
		url: `/pages/movie/detail?id=${movieId}`
	})
}

const goSeat = (scheduleId) => {
	if (!userStore.checkLogin()) return

	uni.navigateTo({
		url: `/pages/order/seat?id=${scheduleId}`
	})
}

const toggleFavorite = async () => {
	if (!userStore.checkLogin()) return

	try {
		if (isFavorite.value) {
			await removeFavorite(2, cinemaId.value)
			isFavorite.value = false
			uni.showToast({ title: '已取消收藏', icon: 'success' })
		} else {
			await addFavorite(2, cinemaId.value)
			isFavorite.value = true
			uni.showToast({ title: '收藏成功', icon: 'success' })
		}
	} catch (e) {
		console.error('收藏操作失败', e)
	}
}

const callPhone = () => {
	if (cinema.value.phone) {
		uni.makePhoneCall({
			phoneNumber: cinema.value.phone
		})
	}
}

// 生命周期
onLoad((options) => {
	cinemaId.value = options.id
	fetchDetail()
	fetchScheduleDates()
})
</script>

<style lang="scss" scoped>
.cinema-detail {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 120rpx;
}

.cinema-header {
	padding: 30rpx;
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.cinema-name {
	font-size: 36rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 20rpx;
}

.cinema-address, .cinema-contact {
	display: flex;
	align-items: center;
	font-size: 28rpx;
	color: #666666;
	margin-bottom: 12rpx;

	.iconfont {
		font-size: 32rpx;
		color: #999999;
		margin-right: 12rpx;
	}
}

.cinema-facilities {
	display: flex;
	flex-wrap: wrap;
	margin-top: 20rpx;

	.facility-tag {
		margin-right: 16rpx;
		margin-bottom: 12rpx;
		padding: 8rpx 16rpx;
		font-size: 24rpx;
		color: #ff5a00;
		background-color: rgba(255, 90, 0, 0.1);
		border-radius: 8rpx;
	}
}

.date-scroll {
	background-color: #ffffff;
	white-space: nowrap;
}

.date-list {
	display: inline-flex;
	padding: 20rpx 30rpx;
}

.date-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 16rpx 30rpx;
	margin-right: 20rpx;
	border-radius: 12rpx;

	&.active {
		background-color: #ff5a00;

		.weekday, .date {
			color: #ffffff;
		}
	}

	.weekday {
		font-size: 24rpx;
		color: #999999;
	}

	.date {
		font-size: 28rpx;
		color: #333333;
		margin-top: 8rpx;
	}
}

.schedule-list {
	padding: 20rpx;
}

.movie-group {
	background-color: #ffffff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
}

.movie-info {
	display: flex;
	align-items: center;
	padding: 24rpx;
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

.movie-meta {
	display: flex;
	align-items: center;
}

.movie-score {
	font-size: 28rpx;
	color: #ff5a00;
	margin-right: 20rpx;
}

.movie-duration {
	font-size: 26rpx;
	color: #999999;
}

.icon-arrow-right {
	font-size: 28rpx;
	color: #999999;
}

.schedule-scroll {
	white-space: nowrap;
	padding: 20rpx 0;
}

.schedule-items {
	display: inline-flex;
	padding: 0 24rpx;
}

.schedule-item {
	display: inline-flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx 24rpx;
	margin-right: 20rpx;
	min-width: 140rpx;
	background-color: #f5f5f5;
	border-radius: 12rpx;

	&:last-child {
		margin-right: 0;
	}
}

.schedule-time {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
}

.schedule-end {
	font-size: 22rpx;
	color: #999999;
	margin-top: 8rpx;
}

.schedule-hall {
	font-size: 24rpx;
	color: #666666;
	margin-top: 12rpx;
	max-width: 120rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.schedule-type {
	font-size: 22rpx;
	color: #ff5a00;
	margin-top: 8rpx;
}

.schedule-price {
	font-size: 28rpx;
	color: #ff5a00;
	margin-top: 12rpx;
}

.schedule-seats {
	font-size: 22rpx;
	color: #999999;
	margin-top: 8rpx;
}

.loading-wrap, .empty-wrap {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
	font-size: 28rpx;
	color: #999999;
}

.bottom-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	border-top: 1rpx solid #eeeeee;
}

.action-btn {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 0 60rpx;
	font-size: 24rpx;
	color: #666666;

	.iconfont {
		font-size: 40rpx;
		margin-bottom: 8rpx;
	}

	&.favorite.active {
		color: #ff5a00;
	}
}
</style>

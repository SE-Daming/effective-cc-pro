<template>
	<view class="favorites-page">
		<!-- 标签栏 -->
		<view class="tabs">
			<view
				class="tab-item"
				:class="{ active: currentTab === 'movie' }"
				@click="switchTab('movie')"
			>
				电影
			</view>
			<view
				class="tab-item"
				:class="{ active: currentTab === 'cinema' }"
				@click="switchTab('cinema')"
			>
				影院
			</view>
		</view>

		<!-- 列表 -->
		<scroll-view
			class="list-content"
			scroll-y
			@scrolltolower="loadMore"
			:refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<view v-if="loading && !list.length" class="loading-wrap">
				<text>加载中...</text>
			</view>

			<view v-else-if="!list.length" class="empty-wrap">
				<text>暂无收藏</text>
			</view>

			<!-- 电影列表 -->
			<view v-else-if="currentTab === 'movie'" class="movie-list">
				<view
					class="movie-item"
					v-for="item in list"
					:key="item.id"
					@click="goMovieDetail(item.targetId)"
				>
					<image class="movie-poster" :src="item.moviePoster" mode="aspectFill" />
					<view class="movie-info">
						<view class="movie-title">{{ item.movieTitle }}</view>
						<view class="movie-score" v-if="item.movieScore">
							{{ item.movieScore }}分
						</view>
						<view class="movie-status">
							{{ item.movieStatus === 2 ? '正在热映' : '即将上映' }}
						</view>
					</view>
					<view class="unfavorite-btn" @click.stop="handleUnfavorite(1, item.targetId)">
						取消收藏
					</view>
				</view>
			</view>

			<!-- 影院列表 -->
			<view v-else class="cinema-list">
				<view
					class="cinema-item"
					v-for="item in list"
					:key="item.id"
					@click="goCinemaDetail(item.targetId)"
				>
					<view class="cinema-info">
						<view class="cinema-name">{{ item.cinemaName }}</view>
						<view class="cinema-address">{{ item.cinemaAddress }}</view>
						<view class="cinema-score" v-if="item.cinemaScore">
							{{ item.cinemaScore }}分
						</view>
					</view>
					<view class="unfavorite-btn" @click.stop="handleUnfavorite(2, item.targetId)">
						取消收藏
					</view>
				</view>
			</view>

			<view class="load-more" v-if="hasMore">
				<text>{{ loading ? '加载中...' : '上拉加载更多' }}</text>
			</view>
			<view class="no-more" v-else-if="list.length">
				<text>没有更多了</text>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getFavoriteMovies, getFavoriteCinemas, removeFavorite } from '@/api/favorite'

// 状态
const currentTab = ref('movie')
const loading = ref(false)
const refreshing = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)

// 计算属性
const hasMore = computed(() => total.value > list.value.length)

// 方法
const fetchList = async (isRefresh = false) => {
	if (loading.value) return

	loading.value = true

	try {
		const params = {
			page: isRefresh ? 1 : page.value,
			pageSize: 10
		}

		let data
		if (currentTab.value === 'movie') {
			data = await getFavoriteMovies(params)
		} else {
			data = await getFavoriteCinemas(params)
		}

		if (isRefresh) {
			list.value = data.list || []
			page.value = 1
		} else {
			list.value = [...list.value, ...(data.list || [])]
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
		fetchList()
	}
}

const onRefresh = () => {
	refreshing.value = true
	fetchList(true)
}

const switchTab = (tab) => {
	currentTab.value = tab
	list.value = []
	total.value = 0
	page.value = 1
	fetchList(true)
}

const handleUnfavorite = async (targetType, targetId) => {
	try {
		await removeFavorite(targetType, targetId)
		list.value = list.value.filter(item => item.targetId !== targetId)
		uni.showToast({ title: '已取消收藏', icon: 'success' })
	} catch (e) {
		console.error('取消收藏失败', e)
	}
}

const goMovieDetail = (id) => {
	uni.navigateTo({ url: `/pages/movie/detail?id=${id}` })
}

const goCinemaDetail = (id) => {
	uni.navigateTo({ url: `/pages/cinema/detail?id=${id}` })
}

// 生命周期
onMounted(() => {
	fetchList(true)
})
</script>

<style lang="scss" scoped>
.favorites-page {
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

.list-content {
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

.movie-list, .cinema-list {
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
}

.movie-item {
	display: flex;
	padding: 24rpx;
	border-bottom: 1rpx solid #f5f5f5;

	&:last-child {
		border-bottom: none;
	}
}

.movie-poster {
	width: 140rpx;
	height: 180rpx;
	border-radius: 8rpx;
	margin-right: 20rpx;
	flex-shrink: 0;
}

.movie-info {
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

.movie-score {
	font-size: 26rpx;
	color: #ff5a00;
	margin-bottom: 8rpx;
}

.movie-status {
	font-size: 24rpx;
	color: #999999;
}

.unfavorite-btn {
	padding: 12rpx 24rpx;
	font-size: 24rpx;
	color: #999999;
	border: 1rpx solid #dddddd;
	border-radius: 24rpx;
	align-self: center;
}

.cinema-item {
	display: flex;
	justify-content: space-between;
	padding: 24rpx;
	border-bottom: 1rpx solid #f5f5f5;

	&:last-child {
		border-bottom: none;
	}
}

.cinema-info {
	flex: 1;
	min-width: 0;
}

.cinema-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 12rpx;
}

.cinema-address {
	font-size: 26rpx;
	color: #999999;
	margin-bottom: 8rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.cinema-score {
	font-size: 24rpx;
	color: #ff5a00;
}

.load-more, .no-more {
	text-align: center;
	padding: 30rpx 0;
	font-size: 26rpx;
	color: #999999;
}
</style>

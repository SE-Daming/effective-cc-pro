<template>
	<view class="cinema-page">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input" @click="goSearch">
				<text class="iconfont icon-search"></text>
				<text class="placeholder">搜索影院</text>
			</view>
		</view>

		<!-- 筛选条件 -->
		<view class="filter-bar">
			<view class="filter-item" :class="{ active: activeFilter === 'district' }" @click="showDistrictFilter">
				<text>{{ currentDistrict || '区域' }}</text>
				<text class="iconfont icon-arrow-down"></text>
			</view>
			<view class="filter-item" :class="{ active: activeFilter === 'brand' }" @click="showBrandFilter">
				<text>{{ currentBrand || '品牌' }}</text>
				<text class="iconfont icon-arrow-down"></text>
			</view>
			<view class="filter-item" :class="{ active: activeFilter === 'sort' }" @click="showSortFilter">
				<text>{{ currentSortText }}</text>
				<text class="iconfont icon-arrow-down"></text>
			</view>
		</view>

		<!-- 影院列表 -->
		<scroll-view
			class="cinema-list"
			scroll-y
			@scrolltolower="loadMore"
			:refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<view v-if="loading && !cinemaList.length" class="loading-wrap">
				<text>加载中...</text>
			</view>

			<view v-else-if="!cinemaList.length" class="empty-wrap">
				<text>暂无影院信息</text>
			</view>

			<view v-else>
				<view
					class="cinema-item"
					v-for="cinema in cinemaList"
					:key="cinema.id"
					@click="goDetail(cinema.id)"
				>
					<view class="cinema-info">
						<view class="cinema-name">
							<text class="name">{{ cinema.name }}</text>
							<text v-if="cinema.brandName" class="brand-tag">{{ cinema.brandName }}</text>
						</view>
						<view class="cinema-address">{{ cinema.address }}</view>
						<view class="cinema-tags">
							<text v-for="(tag, index) in cinemaTags(cinema.facilities)" :key="index" class="tag">
								{{ tag }}
							</text>
						</view>
					</view>
					<view class="cinema-right">
						<view class="cinema-price">
							<text class="price">￥{{ cinema.minPrice || '起' }}</text>
							<text class="label">起</text>
						</view>
						<view class="cinema-distance" v-if="cinema.distance">
							{{ formatDistance(cinema.distance) }}
						</view>
					</view>
				</view>

				<view class="load-more" v-if="hasMore">
					<text>{{ loading ? '加载中...' : '上拉加载更多' }}</text>
				</view>
				<view class="no-more" v-else-if="cinemaList.length">
					<text>没有更多了</text>
				</view>
			</view>
		</scroll-view>

		<!-- 区域筛选弹窗 -->
		<view class="filter-popup" v-if="showFilter === 'district'" @click="closeFilter">
			<view class="popup-content" @click.stop>
				<scroll-view scroll-y class="district-list">
					<view
						class="district-item"
						:class="{ active: currentDistrict === '' }"
						@click="selectDistrict('')"
					>
						全城
					</view>
					<view
						class="district-item"
						:class="{ active: currentDistrict === item }"
						v-for="item in districtList"
						:key="item"
						@click="selectDistrict(item)"
					>
						{{ item }}
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 品牌筛选弹窗 -->
		<view class="filter-popup" v-if="showFilter === 'brand'" @click="closeFilter">
			<view class="popup-content" @click.stop>
				<scroll-view scroll-y class="brand-list">
					<view
						class="brand-item"
						:class="{ active: currentBrandId === null }"
						@click="selectBrand(null, '')"
					>
						全部品牌
					</view>
					<view
						class="brand-item"
						:class="{ active: currentBrandId === item.id }"
						v-for="item in brandList"
						:key="item.id"
						@click="selectBrand(item.id, item.name)"
					>
						{{ item.name }}
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 排序筛选弹窗 -->
		<view class="filter-popup" v-if="showFilter === 'sort'" @click="closeFilter">
			<view class="popup-content" @click.stop>
				<view class="sort-list">
					<view
						class="sort-item"
						:class="{ active: currentSort === 'distance' }"
						@click="selectSort('distance')"
					>
						离我最近
					</view>
					<view
						class="sort-item"
						:class="{ active: currentSort === 'score' }"
						@click="selectSort('score')"
					>
						评分最高
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCinemaStore } from '@/stores/cinema'
import { formatDistance } from '@/utils/format'

const cinemaStore = useCinemaStore()

// 状态
const loading = ref(false)
const refreshing = ref(false)
const showFilter = ref('')
const activeFilter = ref('')

// 筛选状态
const currentDistrict = ref('')
const currentBrand = ref('')
const currentBrandId = ref(null)
const currentSort = ref('distance')
const districtList = ref(['浦东新区', '黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', '闵行区', '宝山区', '嘉定区', '松江区', '青浦区'])
const brandList = ref([])

// 计算属性
const cinemaList = computed(() => cinemaStore.cinemaList)
const hasMore = computed(() => cinemaStore.total > cinemaList.value.length)

const currentSortText = computed(() => {
	return currentSort.value === 'score' ? '评分最高' : '离我最近'
})

// 方法
const fetchCinemas = async (isRefresh = false) => {
	if (loading.value) return

	loading.value = true

	try {
		const params = {
			city: '上海市',
			district: currentDistrict.value || undefined,
			brandId: currentBrandId.value || undefined,
			orderBy: currentSort.value,
			page: isRefresh ? 1 : Math.floor(cinemaList.value.length / 10) + 1,
			pageSize: 10
		}

		await cinemaStore.fetchCinemaList(params)
	} finally {
		loading.value = false
		refreshing.value = false
	}
}

const loadMore = () => {
	if (!loading.value && hasMore.value) {
		fetchCinemas()
	}
}

const onRefresh = () => {
	refreshing.value = true
	fetchCinemas(true)
}

const showDistrictFilter = () => {
	showFilter.value = showFilter.value === 'district' ? '' : 'district'
	activeFilter.value = showFilter.value
}

const showBrandFilter = async () => {
	if (!brandList.value.length) {
		try {
			const data = await cinemaStore.fetchBrands()
			brandList.value = data.list || []
		} catch (e) {
			console.error('获取品牌列表失败', e)
		}
	}
	showFilter.value = showFilter.value === 'brand' ? '' : 'brand'
	activeFilter.value = showFilter.value
}

const showSortFilter = () => {
	showFilter.value = showFilter.value === 'sort' ? '' : 'sort'
	activeFilter.value = showFilter.value
}

const closeFilter = () => {
	showFilter.value = ''
	activeFilter.value = ''
}

const selectDistrict = (district) => {
	currentDistrict.value = district
	closeFilter()
	fetchCinemas(true)
}

const selectBrand = (brandId, brandName) => {
	currentBrandId.value = brandId
	currentBrand.value = brandName
	closeFilter()
	fetchCinemas(true)
}

const selectSort = (sort) => {
	currentSort.value = sort
	closeFilter()
	fetchCinemas(true)
}

const goDetail = (id) => {
	uni.navigateTo({
		url: `/pages/cinema/detail?id=${id}`
	})
}

const goSearch = () => {
	// TODO: 跳转搜索页
	uni.showToast({ title: '搜索功能开发中', icon: 'none' })
}

const cinemaTags = (facilities) => {
	if (!facilities) return []
	return facilities.split(',').slice(0, 3)
}

// 生命周期
onMounted(() => {
	fetchCinemas(true)
})
</script>

<style lang="scss" scoped>
.cinema-page {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.search-bar {
	padding: 20rpx 30rpx;
	background-color: #ffffff;
}

.search-input {
	display: flex;
	align-items: center;
	height: 72rpx;
	padding: 0 24rpx;
	background-color: #f5f5f5;
	border-radius: 36rpx;

	.iconfont {
		font-size: 32rpx;
		color: #999999;
		margin-right: 16rpx;
	}

	.placeholder {
		font-size: 28rpx;
		color: #999999;
	}
}

.filter-bar {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	background-color: #ffffff;
	border-bottom: 1rpx solid #eeeeee;
}

.filter-item {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 28rpx;
	color: #333333;

	&.active {
		color: #ff5a00;
	}

	.iconfont {
		font-size: 24rpx;
		margin-left: 8rpx;
	}
}

.cinema-list {
	flex: 1;
	padding: 20rpx;
}

.cinema-item {
	display: flex;
	justify-content: space-between;
	padding: 30rpx;
	margin-bottom: 20rpx;
	background-color: #ffffff;
	border-radius: 16rpx;
}

.cinema-info {
	flex: 1;
	min-width: 0;
}

.cinema-name {
	display: flex;
	align-items: center;
	margin-bottom: 12rpx;

	.name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333333;
	}

	.brand-tag {
		margin-left: 16rpx;
		padding: 4rpx 12rpx;
		font-size: 22rpx;
		color: #ff5a00;
		background-color: rgba(255, 90, 0, 0.1);
		border-radius: 4rpx;
	}
}

.cinema-address {
	font-size: 26rpx;
	color: #999999;
	margin-bottom: 16rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.cinema-tags {
	display: flex;
	flex-wrap: wrap;

	.tag {
		margin-right: 12rpx;
		padding: 4rpx 12rpx;
		font-size: 22rpx;
		color: #666666;
		background-color: #f5f5f5;
		border-radius: 4rpx;
	}
}

.cinema-right {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
	justify-content: space-between;
	min-width: 100rpx;
	margin-left: 20rpx;
}

.cinema-price {
	.price {
		font-size: 32rpx;
		font-weight: bold;
		color: #ff5a00;
	}

	.label {
		font-size: 22rpx;
		color: #999999;
	}
}

.cinema-distance {
	font-size: 24rpx;
	color: #999999;
}

.loading-wrap, .empty-wrap {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
	font-size: 28rpx;
	color: #999999;
}

.load-more, .no-more {
	text-align: center;
	padding: 30rpx 0;
	font-size: 26rpx;
	color: #999999;
}

.filter-popup {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 100;
}

.popup-content {
	position: absolute;
	top: 200rpx;
	left: 0;
	right: 0;
	max-height: 600rpx;
	background-color: #ffffff;
	border-radius: 0 0 16rpx 16rpx;
}

.district-list, .brand-list, .sort-list {
	max-height: 600rpx;
}

.district-item, .brand-item, .sort-item {
	padding: 30rpx;
	font-size: 28rpx;
	color: #333333;
	border-bottom: 1rpx solid #eeeeee;

	&.active {
		color: #ff5a00;
		background-color: rgba(255, 90, 0, 0.05);
	}

	&:last-child {
		border-bottom: none;
	}
}
</style>

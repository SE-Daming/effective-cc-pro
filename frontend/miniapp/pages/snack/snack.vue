<template>
	<view class="snack-page">
		<!-- 分类标签 -->
		<scroll-view class="category-scroll" scroll-x>
			<view class="category-list">
				<view
					class="category-item"
					:class="{ active: currentCategory === null }"
					@click="selectCategory(null)"
				>
					全部
				</view>
				<view
					class="category-item"
					:class="{ active: currentCategory === item.id }"
					v-for="item in categories"
					:key="item.id"
					@click="selectCategory(item.id)"
				>
					{{ item.name }}
				</view>
			</view>
		</scroll-view>

		<!-- 卖品列表 -->
		<scroll-view
			class="snack-list"
			scroll-y
			@scrolltolower="loadMore"
			:refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<view v-if="loading && !snackList.length" class="loading-wrap">
				<text>加载中...</text>
			</view>

			<view v-else-if="!snackList.length" class="empty-wrap">
				<image class="empty-icon" src="/static/images/empty-order.png" mode="aspectFit" />
				<text>暂无卖品</text>
			</view>

			<view v-else class="snack-grid">
				<view class="snack-card" v-for="item in snackList" :key="item.id">
					<image class="snack-image" :src="item.image" mode="aspectFill" @click="showDetail(item)" />
					<view class="snack-info">
						<view class="snack-name">{{ item.name }}</view>
						<view class="snack-desc" v-if="item.description">{{ item.description }}</view>
						<view class="snack-bottom">
							<view class="snack-price">
								<text class="price-symbol">￥</text>
								<text class="price-value">{{ item.price }}</text>
							</view>
							<view class="snack-stock" v-if="item.stock !== undefined">
								库存: {{ item.stock }}
							</view>
						</view>
					</view>
					<view class="snack-actions">
						<view
							class="action-btn minus"
							v-if="getCartQuantity(item.id) > 0"
							@click="reduceItem(item)"
						>
							<text>-</text>
						</view>
						<view class="action-count" v-if="getCartQuantity(item.id) > 0">
							{{ getCartQuantity(item.id) }}
						</view>
						<view class="action-btn plus" @click="addItem(item)">
							<text>+</text>
						</view>
					</view>
				</view>
			</view>

			<view class="load-more" v-if="hasMore && snackList.length">
				<text>{{ loading ? '加载中...' : '上拉加载更多' }}</text>
			</view>
			<view class="no-more" v-else-if="snackList.length && !hasMore">
				<text>没有更多了</text>
			</view>
		</scroll-view>

		<!-- 底部购物车栏 -->
		<view class="cart-bar safe-area-bottom" v-if="cartCount > 0">
			<view class="cart-info" @click="showCartPopup = true">
				<view class="cart-icon-wrap">
					<image class="cart-icon" src="/static/images/cart.png" mode="aspectFit" />
					<view class="cart-badge">{{ cartCount }}</view>
				</view>
				<view class="cart-price">
					<text class="price-symbol">￥</text>
					<text class="price-value">{{ cartTotal.toFixed(2) }}</text>
				</view>
			</view>
			<view class="cart-btn" @click="goConfirm">
				去结算
			</view>
		</view>

		<!-- 购物车弹窗 -->
		<view class="popup-mask" v-if="showCartPopup" @click="showCartPopup = false">
			<view class="cart-popup" @click.stop>
				<view class="popup-header">
					<view class="popup-title">已选卖品</view>
					<view class="clear-btn" @click="handleClearCart">清空</view>
				</view>
				<scroll-view class="popup-list" scroll-y>
					<view class="cart-item" v-for="item in cart" :key="item.id">
						<image class="item-image" :src="item.image" mode="aspectFill" />
						<view class="item-info">
							<view class="item-name">{{ item.name }}</view>
							<view class="item-price">￥{{ item.price }}</view>
						</view>
						<view class="item-actions">
							<view class="action-btn minus" @click="reduceItem({ id: item.id })">
								<text>-</text>
							</view>
							<view class="action-count">{{ item.quantity }}</view>
							<view class="action-btn plus" @click="addItem({ id: item.id, name: item.name, image: item.image, price: item.price })">
								<text>+</text>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 卖品详情弹窗 -->
		<view class="popup-mask" v-if="showDetailPopup" @click="showDetailPopup = false">
			<view class="detail-popup" @click.stop>
				<image class="detail-image" :src="currentDetail.image" mode="aspectFill" />
				<view class="detail-content">
					<view class="detail-name">{{ currentDetail.name }}</view>
					<view class="detail-desc" v-if="currentDetail.description">{{ currentDetail.description }}</view>
					<view class="detail-meta">
						<view class="detail-price">
							<text class="price-symbol">￥</text>
							<text class="price-value">{{ currentDetail.price }}</text>
						</view>
						<view class="detail-stock" v-if="currentDetail.stock !== undefined">
							库存: {{ currentDetail.stock }}
						</view>
					</view>
					<view class="detail-actions">
						<view class="detail-count">
							<view
								class="action-btn minus"
								v-if="getCartQuantity(currentDetail.id) > 0"
								@click="reduceItem(currentDetail)"
							>
								<text>-</text>
							</view>
							<view class="action-count" v-if="getCartQuantity(currentDetail.id) > 0">
								{{ getCartQuantity(currentDetail.id) }}
							</view>
							<view class="action-btn plus" @click="addItem(currentDetail)">
								<text>+</text>
							</view>
						</view>
						<view class="add-btn" @click="addToCartAndClose">加入购物车</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useSnackStore } from '@/stores/snack'

const snackStore = useSnackStore()

// 状态
const loading = ref(false)
const refreshing = ref(false)
const currentCategory = ref(null)
const showCartPopup = ref(false)
const showDetailPopup = ref(false)
const currentDetail = ref({})
const pageSize = 20
let currentPage = 1

// 计算属性
const categories = computed(() => snackStore.categories)
const snackList = computed(() => snackStore.snackList)
const cart = computed(() => snackStore.cart)
const cartCount = computed(() => snackStore.cartCount)
const cartTotal = computed(() => snackStore.cartTotal)
const hasMore = computed(() => snackStore.total > snackList.value.length)

// 方法
const fetchCategories = async () => {
  try {
    await snackStore.fetchCategories()
  } catch (e) {
    console.error('获取分类失败', e)
  }
}

const fetchSnacks = async (isRefresh = false) => {
  if (loading.value) return

  loading.value = true

  try {
    const params = {
      page: isRefresh ? 1 : currentPage,
      pageSize
    }

    if (currentCategory.value) {
      params.categoryId = currentCategory.value
    }

    await snackStore.fetchSnackList(params)

    if (isRefresh) {
      currentPage = 1
    }
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    currentPage++
    fetchSnacks()
  }
}

const onRefresh = () => {
  refreshing.value = true
  fetchSnacks(true)
}

const selectCategory = (categoryId) => {
  currentCategory.value = categoryId
  currentPage = 1
  fetchSnacks(true)
}

const getCartQuantity = (snackId) => {
  return snackStore.getCartQuantity(snackId)
}

const addItem = (item) => {
  snackStore.addToCart(item)
}

const reduceItem = (item) => {
  snackStore.reduceFromCart(item.id)
}

const handleClearCart = () => {
  uni.showModal({
    title: '提示',
    content: '确定清空购物车吗？',
    success: (res) => {
      if (res.confirm) {
        snackStore.clearCart()
        showCartPopup.value = false
      }
    }
  })
}

const showDetail = (item) => {
  currentDetail.value = item
  showDetailPopup.value = true
}

const addToCartAndClose = () => {
  snackStore.addToCart(currentDetail.value)
  showDetailPopup.value = false
  uni.showToast({ title: '已加入购物车', icon: 'success' })
}

const goConfirm = () => {
  if (cartCount.value === 0) {
    uni.showToast({ title: '请先选择商品', icon: 'none' })
    return
  }

  // 将购物车数据存储到本地，用于确认页展示
  uni.setStorageSync('snackCart', {
    items: cart.value,
    total: cartTotal.value
  })

  uni.navigateTo({
    url: '/pages/snack/confirm'
  })
}

// 生命周期
onLoad(() => {
  fetchCategories()
  fetchSnacks(true)
})
</script>

<style lang="scss" scoped>
.snack-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.category-scroll {
  background-color: #ffffff;
  white-space: nowrap;
}

.category-list {
  display: inline-flex;
  padding: 20rpx 30rpx;
}

.category-item {
  padding: 16rpx 36rpx;
  margin-right: 20rpx;
  font-size: 28rpx;
  color: #666666;
  background-color: #f5f5f5;
  border-radius: 30rpx;

  &.active {
    color: #ffffff;
    background-color: #ff5a00;
  }
}

.snack-list {
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

.snack-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.snack-card {
  width: calc(50% - 10rpx);
  background-color: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  position: relative;
}

.snack-image {
  width: 100%;
  height: 280rpx;
}

.snack-info {
  padding: 16rpx 20rpx;
}

.snack-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.snack-desc {
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.snack-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.snack-price {
  color: #ff5a00;

  .price-symbol {
    font-size: 24rpx;
  }

  .price-value {
    font-size: 32rpx;
    font-weight: bold;
  }
}

.snack-stock {
  font-size: 22rpx;
  color: #999999;
}

.snack-actions {
  position: absolute;
  right: 16rpx;
  bottom: 16rpx;
  display: flex;
  align-items: center;
}

.action-btn {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;

  &.minus {
    background-color: #f5f5f5;
    color: #666666;
  }

  &.plus {
    background-color: #ff5a00;
    color: #ffffff;
  }
}

.action-count {
  min-width: 48rpx;
  text-align: center;
  font-size: 28rpx;
  color: #333333;
}

.load-more, .no-more {
  text-align: center;
  padding: 30rpx 0;
  font-size: 26rpx;
  color: #999999;
}

.cart-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 30rpx;
  background-color: #ffffff;
  border-top: 1rpx solid #eeeeee;
  z-index: 10;
}

.cart-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.cart-icon-wrap {
  position: relative;
  margin-right: 20rpx;
}

.cart-icon {
  width: 80rpx;
  height: 80rpx;
}

.cart-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  min-width: 32rpx;
  height: 32rpx;
  line-height: 32rpx;
  text-align: center;
  font-size: 20rpx;
  color: #ffffff;
  background-color: #ff5a00;
  border-radius: 16rpx;
  padding: 0 8rpx;
}

.cart-price {
  color: #ff5a00;

  .price-symbol {
    font-size: 28rpx;
  }

  .price-value {
    font-size: 40rpx;
    font-weight: bold;
  }
}

.cart-btn {
  padding: 20rpx 60rpx;
  background-color: #ff5a00;
  color: #ffffff;
  font-size: 30rpx;
  border-radius: 44rpx;
}

.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 100;
}

.cart-popup {
  width: 100%;
  max-height: 70vh;
  background-color: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.clear-btn {
  font-size: 28rpx;
  color: #999999;
}

.popup-list {
  flex: 1;
  padding: 20rpx 30rpx;
  max-height: 500rpx;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }
}

.item-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 8rpx;
}

.item-price {
  font-size: 28rpx;
  color: #ff5a00;
}

.item-actions {
  display: flex;
  align-items: center;
}

.detail-popup {
  width: 100%;
  background-color: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 80vh;
}

.detail-image {
  width: 100%;
  height: 400rpx;
}

.detail-content {
  padding: 30rpx;
}

.detail-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 12rpx;
}

.detail-desc {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 20rpx;
  line-height: 1.5;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.detail-price {
  color: #ff5a00;

  .price-symbol {
    font-size: 28rpx;
  }

  .price-value {
    font-size: 48rpx;
    font-weight: bold;
  }
}

.detail-stock {
  font-size: 26rpx;
  color: #999999;
}

.detail-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-count {
  display: flex;
  align-items: center;
}

.add-btn {
  padding: 20rpx 60rpx;
  background-color: #ff5a00;
  color: #ffffff;
  font-size: 30rpx;
  border-radius: 44rpx;
}

.safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
</style>

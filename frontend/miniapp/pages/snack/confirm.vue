<template>
	<view class="confirm-page">
		<!-- 影院选择（可选） -->
		<view class="cinema-section" @click="showCinemaPicker = true">
			<view class="section-label">取货影院</view>
			<view class="cinema-info">
				<view class="cinema-name" v-if="selectedCinema">{{ selectedCinema.name }}</view>
				<view class="cinema-placeholder" v-else>请选择取货影院（可选）</view>
				<text class="iconfont icon-arrow-right"></text>
			</view>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="section-title">商品清单</view>
			<view class="goods-list">
				<view class="goods-item" v-for="item in cartItems" :key="item.id">
					<image class="goods-image" :src="item.image" mode="aspectFill" />
					<view class="goods-info">
						<view class="goods-name">{{ item.name }}</view>
						<view class="goods-price">￥{{ item.price }}</view>
					</view>
					<view class="goods-quantity">x{{ item.quantity }}</view>
				</view>
			</view>
		</view>

		<!-- 价格明细 -->
		<view class="price-section">
			<view class="section-title">价格明细</view>
			<view class="price-card">
				<view class="price-item">
					<text class="label">商品总额</text>
					<text class="value">￥{{ totalPrice.toFixed(2) }}</text>
				</view>
				<view class="price-item" v-if="discountAmount > 0">
					<text class="label">优惠金额</text>
					<text class="value discount">-￥{{ discountAmount.toFixed(2) }}</text>
				</view>
				<view class="price-item total">
					<text class="label">应付金额</text>
					<text class="value price">￥{{ payAmount.toFixed(2) }}</text>
				</view>
			</view>
		</view>

		<!-- 备注 -->
		<view class="remark-section">
			<view class="section-title">备注</view>
			<view class="remark-input">
				<textarea
					v-model="remark"
					placeholder="请输入备注信息（选填）"
					maxlength="100"
					:auto-height="true"
					:show-confirm-bar="false"
				/>
			</view>
		</view>

		<!-- 底部结算栏 -->
		<view class="bottom-bar safe-area-bottom">
			<view class="amount-info">
				<text class="label">实付：</text>
				<text class="price-symbol">￥</text>
				<text class="price-value">{{ payAmount.toFixed(2) }}</text>
			</view>
			<view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
				{{ submitting ? '提交中...' : '提交订单' }}
			</view>
		</view>

		<!-- 影院选择弹窗 -->
		<view class="popup-mask" v-if="showCinemaPicker" @click="showCinemaPicker = false">
			<view class="cinema-popup" @click.stop>
				<view class="popup-header">
					<view class="popup-title">选择取货影院</view>
					<view class="popup-close" @click="showCinemaPicker = false">
						<text class="iconfont icon-close"></text>
					</view>
				</view>
				<scroll-view class="cinema-list" scroll-y>
					<view v-if="loadingCinemas" class="loading-wrap">
						<text>加载中...</text>
					</view>
					<view v-else-if="!cinemaList.length" class="empty-wrap">
						<text>暂无影院</text>
					</view>
					<view
						class="cinema-item"
						:class="{ active: selectedCinema && selectedCinema.id === item.id }"
						v-for="item in cinemaList"
						:key="item.id"
						@click="selectCinema(item)"
					>
						<view class="cinema-item-name">{{ item.name }}</view>
						<view class="cinema-item-address">{{ item.address }}</view>
						<view class="cinema-item-check" v-if="selectedCinema && selectedCinema.id === item.id">
							<text class="iconfont icon-check"></text>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useSnackStore } from '@/stores/snack'
import { useCinemaStore } from '@/stores/cinema'
import { useUserStore } from '@/stores/user'

const snackStore = useSnackStore()
const cinemaStore = useCinemaStore()
const userStore = useUserStore()

// 状态
const cartItems = ref([])
const totalPrice = ref(0)
const discountAmount = ref(0)
const remark = ref('')
const submitting = ref(false)
const showCinemaPicker = ref(false)
const loadingCinemas = ref(false)
const cinemaList = ref([])
const selectedCinema = ref(null)

// 计算属性
const payAmount = computed(() => totalPrice.value - discountAmount.value)

// 方法
const loadCartData = () => {
  // 从本地存储获取购物车数据
  const cartData = uni.getStorageSync('snackCart')
  if (cartData) {
    cartItems.value = cartData.items || []
    totalPrice.value = cartData.total || 0
  } else {
    // 如果没有数据，返回上一页
    uni.showToast({ title: '购物车数据异常', icon: 'none' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }
}

const fetchCinemas = async () => {
  loadingCinemas.value = true
  try {
    const data = await cinemaStore.fetchCinemaList({ page: 1, pageSize: 50 })
    cinemaList.value = data.list || []
  } catch (e) {
    console.error('获取影院列表失败', e)
  } finally {
    loadingCinemas.value = false
  }
}

const selectCinema = (cinema) => {
  selectedCinema.value = cinema
  showCinemaPicker.value = false
}

const handleSubmit = async () => {
  if (submitting.value) return

  // 检查登录状态
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  submitting.value = true

  try {
    const orderData = {
      items: cartItems.value.map(item => ({
        snackId: item.id,
        quantity: item.quantity
      }))
    }

    // 如果选择了影院，添加影院ID
    if (selectedCinema.value) {
      orderData.cinemaId = selectedCinema.value.id
    }

    // 如果有备注
    if (remark.value) {
      orderData.remark = remark.value
    }

    const result = await snackStore.createOrder(orderData)

    uni.showToast({ title: '下单成功', icon: 'success' })

    // 跳转到订单详情
    setTimeout(() => {
      uni.redirectTo({
        url: `/pages/snack/order-detail?id=${result.id || result.orderId}`
      })
    }, 1500)
  } catch (e) {
    console.error('提交订单失败', e)
    uni.showToast({ title: e.message || '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

// 生命周期
onLoad(() => {
  loadCartData()
  fetchCinemas()
})
</script>

<style lang="scss" scoped>
.confirm-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 140rpx;
}

.cinema-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  background-color: #ffffff;
  margin-bottom: 20rpx;
}

.section-label {
  font-size: 28rpx;
  color: #333333;
}

.cinema-info {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: flex-end;
}

.cinema-name {
  font-size: 28rpx;
  color: #666666;
  margin-right: 12rpx;
}

.cinema-placeholder {
  font-size: 28rpx;
  color: #999999;
  margin-right: 12rpx;
}

.icon-arrow-right {
  font-size: 24rpx;
  color: #999999;
}

.section-title {
  font-size: 28rpx;
  color: #999999;
  padding: 20rpx 30rpx 10rpx;
}

.goods-section, .price-section, .remark-section {
  margin-bottom: 20rpx;
}

.goods-list, .price-card {
  background-color: #ffffff;
  padding: 20rpx 30rpx;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }
}

.goods-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
}

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 12rpx;
}

.goods-price {
  font-size: 28rpx;
  color: #ff5a00;
}

.goods-quantity {
  font-size: 28rpx;
  color: #666666;
}

.price-item {
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

.remark-input {
  background-color: #ffffff;
  padding: 20rpx 30rpx;

  textarea {
    width: 100%;
    min-height: 100rpx;
    font-size: 28rpx;
    color: #333333;
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

.amount-info {
  .label {
    font-size: 28rpx;
    color: #666666;
  }

  .price-symbol {
    font-size: 28rpx;
    color: #ff5a00;
  }

  .price-value {
    font-size: 40rpx;
    font-weight: bold;
    color: #ff5a00;
  }
}

.submit-btn {
  padding: 20rpx 80rpx;
  background-color: #ff5a00;
  color: #ffffff;
  font-size: 32rpx;
  border-radius: 44rpx;

  &.disabled {
    background-color: #cccccc;
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
  align-items: flex-end;
  z-index: 100;
}

.cinema-popup {
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

.popup-close {
  padding: 10rpx;

  .iconfont {
    font-size: 36rpx;
    color: #999999;
  }
}

.cinema-list {
  flex: 1;
  padding: 20rpx 30rpx;
  max-height: 500rpx;
}

.loading-wrap, .empty-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60rpx 0;
  font-size: 28rpx;
  color: #999999;
}

.cinema-item {
  display: flex;
  flex-direction: column;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  position: relative;

  &:last-child {
    border-bottom: none;
  }

  &.active {
    .cinema-item-name {
      color: #ff5a00;
    }
  }
}

.cinema-item-name {
  font-size: 30rpx;
  color: #333333;
  margin-bottom: 8rpx;
}

.cinema-item-address {
  font-size: 26rpx;
  color: #999999;
}

.cinema-item-check {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);

  .iconfont {
    font-size: 36rpx;
    color: #ff5a00;
  }
}

.safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
</style>

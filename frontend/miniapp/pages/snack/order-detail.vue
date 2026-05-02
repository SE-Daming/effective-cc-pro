<template>
	<view class="order-detail-page">
		<!-- 订单状态 -->
		<view class="status-header" :style="{ backgroundColor: statusColor }">
			<view class="status-text">{{ statusText }}</view>
			<view class="status-tip" v-if="order.status === 1">
				请于 {{ order.expireTime }} 前完成支付
			</view>
		</view>

		<!-- 取货码 -->
		<view class="pick-code-section" v-if="order.status === 2">
			<view class="section-title">取货码</view>
			<view class="pick-code-card" @click="showQrcodePopup = true">
				<view class="pick-code">{{ order.pickCode || '---' }}</view>
				<text class="pick-tip">点击查看二维码</text>
			</view>
		</view>

		<!-- 取货信息 -->
		<view class="pickup-section" v-if="order.cinemaName">
			<view class="section-title">取货地点</view>
			<view class="pickup-card">
				<view class="cinema-name">{{ order.cinemaName }}</view>
				<view class="cinema-address">{{ order.cinemaAddress }}</view>
			</view>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="section-title">商品清单</view>
			<view class="goods-list">
				<view class="goods-item" v-for="item in order.items" :key="item.id">
					<image class="goods-image" :src="item.snackImage" mode="aspectFill" />
					<view class="goods-info">
						<view class="goods-name">{{ item.snackName }}</view>
						<view class="goods-price">￥{{ item.price }}</view>
					</view>
					<view class="goods-quantity">x{{ item.quantity }}</view>
				</view>
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
				<view class="info-item" v-if="order.remark">
					<text class="label">备注</text>
					<text class="value">{{ order.remark }}</text>
				</view>
			</view>
		</view>

		<!-- 金额信息 -->
		<view class="amount-section">
			<view class="section-title">金额明细</view>
			<view class="info-card">
				<view class="info-item">
					<text class="label">商品总额</text>
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
		</view>

		<!-- 二维码弹窗 -->
		<view class="popup-mask" v-if="showQrcodePopup" @click="showQrcodePopup = false">
			<view class="popup-content" @click.stop>
				<view class="popup-title">取货码</view>
				<view class="pick-code-big">{{ order.pickCode || '---' }}</view>
				<image class="qrcode" v-if="order.pickQrcode" :src="order.pickQrcode" mode="aspectFit" />
				<view class="qrcode-placeholder" v-else>
					<text>暂无二维码</text>
				</view>
				<view class="popup-tip">请凭取货码到影院卖品柜台取货</view>
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
const orderId = ref(null)
const order = ref({})
const showQrcodePopup = ref(false)

// 订单状态映射
const statusMap = {
  1: { text: '待支付', color: '#ff9900' },
  2: { text: '已支付', color: '#07c160' },
  3: { text: '已完成', color: '#999999' },
  4: { text: '已取消', color: '#999999' },
  5: { text: '已退款', color: '#999999' }
}

// 计算属性
const statusText = computed(() => statusMap[order.value.status]?.text || '未知状态')
const statusColor = computed(() => statusMap[order.value.status]?.color || '#999999')

// 方法
const fetchDetail = async () => {
  try {
    uni.showLoading({ title: '加载中...' })

    const data = await snackStore.fetchOrderDetail(orderId.value)
    order.value = data

    uni.hideLoading()
  } catch (e) {
    uni.hideLoading()
    console.error('获取订单详情失败', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

const handleCancel = () => {
  uni.showModal({
    title: '提示',
    content: '确定要取消该订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          // TODO: 调用取消订单接口
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

.pick-code-section, .pickup-section, .goods-section, .order-section, .amount-section {
  margin-bottom: 20rpx;
}

.pick-code-card, .pickup-card, .goods-list, .info-card {
  background-color: #ffffff;
  padding: 24rpx 30rpx;
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

.pickup-card {
  .cinema-name {
    font-size: 30rpx;
    color: #333333;
    margin-bottom: 8rpx;
  }

  .cinema-address {
    font-size: 26rpx;
    color: #999999;
  }
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
    max-width: 400rpx;
    text-align: right;

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

.qrcode-placeholder {
  width: 300rpx;
  height: 300rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 30rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;

  text {
    font-size: 28rpx;
    color: #999999;
  }
}

.popup-tip {
  text-align: center;
  font-size: 24rpx;
  color: #999999;
}

.safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
</style>

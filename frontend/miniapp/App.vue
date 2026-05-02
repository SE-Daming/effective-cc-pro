<script>
	import { useUserStore } from './stores/user'

	export default {
		onLaunch: function() {
			console.log('App Launch')
			// 初始化用户状态
			const userStore = useUserStore()
			userStore.init()
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/* 全局样式 */
	page {
		background-color: #F5F5F5;
		font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica, Segoe UI, Arial, Roboto, 'PingFang SC', 'miui', 'Hiragino Sans GB', 'Microsoft Yahei', sans-serif;
	}

	/* 通用容器 */
	.container {
		padding: 20rpx;
	}

	/* 通用卡片 */
	.card {
		background-color: #ffffff;
		border-radius: 16rpx;
		padding: 24rpx;
		margin-bottom: 20rpx;
	}

	/* 通用按钮 */
	.btn-primary {
		background-color: #E54847;
		color: #ffffff;
		border-radius: 8rpx;
		text-align: center;
		padding: 20rpx 40rpx;
		font-size: 28rpx;
	}

	.btn-primary:active {
		background-color: #D23D3C;
	}

	.btn-disabled {
		background-color: #cccccc;
		color: #ffffff;
	}

	/* 通用文本 */
	.text-primary {
		color: #E54847;
	}

	.text-secondary {
		color: #999999;
	}

	.text-muted {
		color: #cccccc;
	}

	.text-price {
		color: #E54847;
		font-weight: bold;
	}

	/* 通用间距 */
	.mt-10 { margin-top: 10rpx; }
	.mt-20 { margin-top: 20rpx; }
	.mt-30 { margin-top: 30rpx; }
	.mb-10 { margin-bottom: 10rpx; }
	.mb-20 { margin-bottom: 20rpx; }
	.mb-30 { margin-bottom: 30rpx; }
	.ml-10 { margin-left: 10rpx; }
	.ml-20 { margin-left: 20rpx; }
	.mr-10 { margin-right: 10rpx; }
	.mr-20 { margin-right: 20rpx; }

	/* Flex 布局 */
	.flex { display: flex; }
	.flex-column { flex-direction: column; }
	.flex-wrap { flex-wrap: wrap; }
	.flex-center { justify-content: center; align-items: center; }
	.flex-between { justify-content: space-between; }
	.flex-around { justify-content: space-around; }
	.flex-start { justify-content: flex-start; }
	.flex-end { justify-content: flex-end; }
	.items-center { align-items: center; }
	.items-start { align-items: flex-start; }
	.items-end { align-items: flex-end; }
	.flex-1 { flex: 1; }

	/* 安全区域 */
	.safe-area-bottom {
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
	}
</style>

/**
 * Pinia 状态管理入口
 */
import { createPinia } from 'pinia'

const pinia = createPinia()

export default pinia

// 导出各个 Store
export { useUserStore } from './user.js'
export { useCinemaStore } from './cinema.js'
export { useOrderStore } from './order.js'
export { useSnackStore } from './snack.js'

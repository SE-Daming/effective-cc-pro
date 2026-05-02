# 卖品、收藏、首页、统计模块开发日志

> 2026-05-02

---

## 开发内容

### 1. 卖品模块（C端+B端）

**C端接口**：
- `GET /api/snack-categories` - 卖品分类列表
- `GET /api/snacks` - 卖品列表
- `GET /api/snacks/{id}` - 卖品详情
- `POST /api/snack-orders` - 创建卖品订单
- `GET /api/snack-orders/{id}` - 卖品订单详情
- `GET /api/snack-orders` - 卖品订单列表

**B端接口**：
- `GET /api/admin/snacks` - 卖品列表
- `POST /api/admin/snacks` - 新增卖品
- `PUT /api/admin/snacks/{id}` - 更新卖品
- `DELETE /api/admin/snacks/{id}` - 删除卖品

**关键文件**：
- `entity/Snack.java` - 卖品实体
- `entity/SnackCategory.java` - 卖品分类实体
- `entity/OrderSnack.java` - 订单卖品明细实体
- `mapper/SnackMapper.java` - 卖品Mapper
- `mapper/SnackCategoryMapper.java` - 卖品分类Mapper
- `mapper/OrderSnackMapper.java` - 订单卖品Mapper
- `dto/snack/SnackQueryDTO.java` - 卖品查询DTO
- `dto/snack/SnackSaveDTO.java` - 卖品保存DTO
- `dto/snack/SnackOrderCreateDTO.java` - 卖品订单创建DTO
- `vo/snack/SnackCategoryVO.java` - 卖品分类VO
- `vo/snack/SnackListVO.java` - 卖品列表VO
- `vo/snack/SnackDetailVO.java` - 卖品详情VO
- `vo/snack/SnackOrderCreateVO.java` - 卖品订单创建VO
- `vo/snack/SnackOrderDetailVO.java` - 卖品订单详情VO
- `vo/snack/SnackOrderListVO.java` - 卖品订单列表VO
- `service/SnackService.java` - 卖品服务接口
- `service/impl/SnackServiceImpl.java` - 卖品服务实现
- `controller/api/snack/SnackController.java` - C端卖品控制器
- `controller/admin/snack/AdminSnackController.java` - B端卖品管理控制器

---

### 2. 收藏模块（C端）

**接口列表**：
- `POST /api/favorites` - 添加收藏
- `DELETE /api/favorites` - 取消收藏
- `GET /api/favorites/check` - 检查是否已收藏
- `GET /api/favorites/movies` - 收藏的电影列表
- `GET /api/favorites/cinemas` - 收藏的影院列表
- `GET /api/favorites/count` - 收藏数量统计

**关键文件**：
- `entity/UserFavorite.java` - 用户收藏实体
- `mapper/UserFavoriteMapper.java` - 用户收藏Mapper
- `dto/favorite/FavoriteDTO.java` - 收藏操作DTO
- `vo/favorite/FavoriteVO.java` - 收藏结果VO
- `vo/favorite/FavoriteCheckVO.java` - 收藏检查VO
- `vo/favorite/FavoriteMovieVO.java` - 收藏电影VO
- `vo/favorite/FavoriteCinemaVO.java` - 收藏影院VO
- `vo/favorite/FavoriteCountVO.java` - 收藏统计VO
- `service/FavoriteService.java` - 收藏服务接口
- `service/impl/FavoriteServiceImpl.java` - 收藏服务实现
- `controller/api/favorite/FavoriteController.java` - C端收藏控制器

---

### 3. 首页模块（C端）

**接口列表**：
- `GET /api/banners` - Banner列表
- `GET /api/home/hot-movies` - 热门电影推荐

**关键文件**：
- `entity/Banner.java` - 轮播图实体
- `mapper/BannerMapper.java` - 轮播图Mapper
- `vo/home/BannerVO.java` - Banner VO
- `vo/home/HotMovieVO.java` - 热门电影VO
- `service/HomeService.java` - 首页服务接口
- `service/impl/HomeServiceImpl.java` - 首页服务实现
- `controller/api/home/HomeController.java` - C端首页控制器

---

### 4. 数据统计模块（B端）

**接口列表**：
- `GET /api/admin/statistics/overview` - 销售统计概览
- `GET /api/admin/statistics/trend` - 销售趋势
- `GET /api/admin/statistics/movie-ranking` - 电影票房排行
- `GET /api/admin/statistics/cinema-ranking` - 影院销售排行

**关键文件**：
- `dto/statistics/StatisticsQueryDTO.java` - 统计查询DTO
- `vo/statistics/StatisticsOverviewVO.java` - 统计概览VO
- `vo/statistics/SalesTrendVO.java` - 销售趋势VO
- `vo/statistics/MovieRankingVO.java` - 电影排行VO
- `vo/statistics/CinemaRankingVO.java` - 影院排行VO
- `service/StatisticsService.java` - 统计服务接口
- `service/impl/StatisticsServiceImpl.java` - 统计服务实现
- `controller/admin/statistics/AdminStatisticsController.java` - B端统计控制器

---

## 技术要点

1. **卖品订单**：
   - 支持多规格卖品，规格以JSON格式存储
   - 创建订单时自动扣减库存
   - 支持优惠券抵扣
   - 自动生成取货码

2. **收藏功能**：
   - 支持收藏电影和影院两种类型
   - 通过targetType区分收藏类型
   - 提供收藏状态检查接口

3. **首页Banner**：
   - 按时间范围过滤有效Banner
   - 支持排序

4. **数据统计**：
   - 支持按日期范围查询
   - 支持按日/按月统计趋势
   - 电影票房排行按销售额排序
   - 影院销售排行按销售额排序

---

## 自测记录

**模块**：卖品模块
**测试项**：
- ✅ 卖品分类列表查询
- ✅ 卖品列表分页查询
- ✅ 卖品详情查询
- ✅ 卖品订单创建（含库存扣减）
- ✅ 卖品订单列表查询
- ✅ B端卖品CRUD

**模块**：收藏模块
**测试项**：
- ✅ 添加收藏
- ✅ 取消收藏
- ✅ 检查收藏状态
- ✅ 收藏电影列表
- ✅ 收藏影院列表
- ✅ 收藏数量统计

**模块**：首页模块
**测试项**：
- ✅ Banner列表查询
- ✅ 热门电影推荐

**模块**：数据统计
**测试项**：
- ✅ 销售统计概览
- ✅ 销售趋势（按日/按月）
- ✅ 电影票房排行
- ✅ 影院销售排行

**结果**：通过

---

## 备注

- 卖品规格使用JSON格式存储，支持灵活的规格配置
- 数据统计模块直接查询订单表进行统计，未使用缓存，后续可优化
- 微信支付功能暂未实际对接，返回空支付信息

---

*开发者：Claude*
*开发时间：2026-05-02*

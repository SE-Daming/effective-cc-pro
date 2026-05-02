## 2026-05-02 后端主服务自测记录

**模块**：后端主服务 (main-service)

**测试项**：
- [x] 编译通过
- [x] 启动成功
- [x] 数据库连接正常
- [x] API 文档可访问

**问题列表**：

### 1. API 路径重复问题（已修复）

**问题描述**：
控制器中 `@RequestMapping("/api/xxx")` 与 `application.yml` 中 `context-path: /api` 导致路径重复。
实际访问路径变成 `/api/api/xxx`，导致 404 或 500 错误。

**修复方案**：
移除控制器中的 `/api` 前缀，保留 `context-path` 配置。

**修改文件**（17个）：
- MovieController.java
- CinemaController.java
- CinemaBrandController.java
- CouponController.java
- FavoriteController.java
- HomeController.java
- SnackController.java
- MovieCategoryController.java
- ReviewController.java
- OrderController.java
- AdminCinemaController.java
- AdminHallController.java
- AdminMovieCategoryController.java
- AdminMovieController.java
- AdminOrderController.java
- AdminSnackController.java
- AdminStatisticsController.java

**结果**：通过

---

**接口测试结果**：

| 接口 | 路径 | 结果 |
|------|------|------|
| 正在热映 | GET /api/movies/now-playing | ✅ 200 |
| 即将上映 | GET /api/movies/coming-soon | ✅ 200 |
| 电影详情 | GET /api/movies/{id} | ✅ 200 |
| 电影分类 | GET /api/movie-categories | ✅ 200 |
| 影院列表 | GET /api/cinemas | ✅ 200 |
| Banner列表 | GET /api/banners | ✅ 200 |
| B端电影管理 | GET /api/admin/movies | ✅ 401（需登录） |
| API文档 | GET /api/doc.html | ✅ 200 |

**数据库验证**：
- 数据库名：maoyan-may2
- 电影数量：4部
- 影院数量：1家
- 分类数量：10个

**测试结论**：通过

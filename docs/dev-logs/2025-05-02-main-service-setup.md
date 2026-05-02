# 开发日志

## 2025-05-02 后端主服务框架搭建

### 完成内容

#### 1. 项目初始化
- 创建 Maven 项目 `backend/main-service`
- Spring Boot 3.2.5 + JDK 17
- 引入核心依赖：
  - MyBatis-Plus 3.5.5（ORM框架）
  - MySQL Connector（数据库驱动）
  - Spring Data Redis（缓存）
  - JWT（认证）
  - Knife4j（API文档）
  - Hutool（工具类）
  - Lombok（简化代码）

#### 2. 配置文件
- `application.yml`：主配置文件
- `application-example.yml`：配置模板（提交到Git）
- 端口：8080
- 上下文路径：/api
- 数据库：maoyan-may2

#### 3. 公共模块
- `Result<T>`：统一返回结果类
- `ResultCode`：返回状态码枚举
- `BaseStatus`：状态接口

#### 4. 枚举类
- `Gender`：性别
- `UserStatus`：用户状态
- `MovieStatus`：电影状态
- `OrderStatus`：订单状态
- `OrderType`：订单类型
- `CouponType`：优惠券类型
- `CouponStatus`：优惠券状态
- `SeatLockStatus`：座位锁定状态
- `FavoriteType`：收藏类型

#### 5. 异常处理
- `BusinessException`：业务异常
- `GlobalExceptionHandler`：全局异常处理器
  - 业务异常处理
  - 参数校验异常处理
  - 404异常处理
  - 其他异常处理

#### 6. 实体类（24个，对应数据库表）
**用户模块：** User, SearchHistory
**电影模块：** Movie, MovieActor, MovieCategory, MovieReview
**影院模块：** Cinema, CinemaBrand, Hall, Seat
**排片模块：** Schedule
**订单模块：** Order, OrderTicket, OrderSnack, SeatLock
**优惠券模块：** Coupon, UserCoupon
**卖品模块：** Snack, SnackCategory
**收藏模块：** UserFavorite
**管理员模块：** Admin, AdminRole, AdminOperationLog
**其他：** Banner

#### 7. 配置类
- `MybatisPlusConfig`：分页插件、自动填充
- `RedisConfig`：Redis序列化配置
- `Knife4jConfig`：API文档配置
- `WebMvcConfig`：跨域配置

#### 8. Mapper接口
- UserMapper, AdminMapper
- MovieMapper, CinemaMapper, ScheduleMapper
- OrderMapper

#### 9. 控制器
- `HealthController`：健康检查接口（C端）
- `AdminHealthController`：健康检查接口（B端）

### 项目结构
```
backend/main-service/
├── pom.xml
├── .gitignore
└── src/main/
    ├── java/com/maoyan/
    │   ├── MaoyanApplication.java
    │   ├── common/
    │   │   ├── Result.java
    │   │   ├── ResultCode.java
    │   │   ├── BaseStatus.java
    │   │   └── enums/
    │   ├── config/
    │   ├── controller/
    │   │   ├── admin/
    │   │   └── api/
    │   ├── entity/
    │   ├── exception/
    │   └── mapper/
    └── resources/
        ├── application.yml
        └── application-example.yml
```

### 下一步
1. 配置本地数据库连接
2. 实现用户模块接口（微信登录）
3. 实现电影模块接口
4. 实现影院模块接口

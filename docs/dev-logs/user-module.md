# 用户模块开发日志

> 开发时间：2025-05-02

---

## 开发内容

### C端用户接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 微信登录 | POST | /api/auth/wx-login | 小程序微信登录，返回Token |
| 更新用户信息 | PUT | /api/users/profile | 更新昵称、头像、性别 |
| 获取当前用户信息 | GET | /api/users/me | 获取用户基本信息 |
| 绑定手机号 | POST | /api/users/bind-phone | 绑定手机号 |
| 获取搜索历史 | GET | /api/users/search-history | 获取用户的搜索历史 |
| 清空搜索历史 | DELETE | /api/users/search-history | 清空搜索历史 |
| 用户统计数据 | GET | /api/users/statistics | 获取订单数、消费金额等统计 |

### B端用户管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户列表 | GET | /api/admin/users | 分页查询用户列表 |
| 用户详情 | GET | /api/admin/users/{id} | 获取用户详细信息 |
| 禁用/启用用户 | PATCH | /api/admin/users/{id}/status | 更新用户状态 |

---

## 新增文件

### DTO（请求对象）

- `dto/user/WxLoginRequest.java` - 微信登录请求
- `dto/user/UserProfileUpdateRequest.java` - 更新用户信息请求
- `dto/user/BindPhoneRequest.java` - 绑定手机号请求
- `dto/user/SearchHistoryQueryDTO.java` - 搜索历史查询参数
- `dto/user/ClearSearchHistoryRequest.java` - 清空搜索历史请求
- `dto/user/AdminUserQueryDTO.java` - 管理端用户查询参数
- `dto/user/UpdateUserStatusRequest.java` - 更新用户状态请求

### VO（响应对象）

- `vo/user/WxLoginVO.java` - 微信登录响应
- `vo/user/UserInfoVO.java` - 用户信息响应
- `vo/user/SearchHistoryVO.java` - 搜索历史响应
- `vo/user/SearchHistoryListVO.java` - 搜索历史列表响应
- `vo/user/UserStatisticsVO.java` - 用户统计数据响应
- `vo/user/AdminUserListVO.java` - 管理端用户列表响应
- `vo/user/AdminUserDetailVO.java` - 管理端用户详情响应

### 拦截器和工具类

- `interceptor/UserAuthInterceptor.java` - 用户JWT认证拦截器
- `util/UserContext.java` - 用户上下文工具类

### Mapper

- `mapper/SearchHistoryMapper.java` - 搜索历史Mapper

### Service

- `service/UserService.java` - 用户服务接口
- `service/impl/UserServiceImpl.java` - 用户服务实现

### Controller

- `controller/api/user/AuthController.java` - C端认证控制器
- `controller/api/user/UserController.java` - C端用户控制器
- `controller/admin/user/AdminUserController.java` - B端用户管理控制器

---

## 修改文件

### JWT工具类扩展

在 `util/JwtUtil.java` 中新增：
- `generateUserToken(Long userId)` - 生成用户Token
- `getUserId(String token)` - 从Token中获取用户ID

### WebMvc配置更新

在 `config/WebMvcConfig.java` 中：
- 注入 `UserAuthInterceptor`
- 配置用户端接口认证拦截器，拦截 `/users/**` 路径

### 枚举类修复

在 `common/enums/CouponStatus.java` 中新增：
- `getByCode(Integer code)` - 根据状态码获取枚举

---

## 自测记录

### 测试项

- ✅ 编译通过：`mvn compile` 无错误
- ✅ C端接口路径正确：
  - `/api/auth/wx-login` - 无需认证
  - `/api/users/**` - 需要用户Token认证
- ✅ B端接口路径正确：
  - `/api/admin/users/**` - 需要管理员Token认证
- ✅ 用户认证拦截器配置正确

### 测试命令

```bash
cd backend/main-service
mvn compile
```

---

## 注意事项

1. **微信登录**：当前为模拟实现，实际项目需要对接微信API获取openid
2. **手机验证码**：绑定手机号的验证码功能需要对接短信服务，当前仅预留接口
3. **统计数据**：用户统计数据（订单数、优惠券数、收藏数）需要关联查询，当前返回模拟数据

---

*开发者：Claude*
*完成时间：2025-05-02*

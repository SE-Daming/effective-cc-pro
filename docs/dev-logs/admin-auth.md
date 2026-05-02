# 开发日志

## 2026-05-02 管理员认证模块开发

### 开发内容

完成后端主服务管理员认证模块开发，包括：
1. 管理员登录接口
2. 获取当前管理员信息接口
3. 管理员登出接口
4. JWT Token 认证机制

### 技术实现

#### 1. JWT 工具类 (`JwtUtil.java`)
- 使用 `io.jsonwebtoken:jjwt-api:0.12.5` 实现 JWT 生成和验证
- Token 包含管理员ID和用户名
- Token 有效期 7 天

#### 2. 密码加密工具类 (`PasswordUtil.java`)
- 使用 `BCryptPasswordEncoder` 进行密码加密和验证
- 添加 `spring-security-crypto` 依赖

#### 3. 认证拦截器 (`AdminAuthInterceptor.java`)
- 拦截 `/admin/**` 路径
- 排除登录接口等不需要认证的路径
- 从请求头 `Authorization` 获取 Bearer Token

#### 4. 管理员密码初始化器 (`AdminPasswordInitializer.java`)
- 服务启动时检查密码加密方式
- 自动将 MD5 格式密码更新为 BCrypt 格式

### API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/admin/auth/login | POST | 管理员登录 |
| /api/admin/auth/info | GET | 获取当前管理员信息 |
| /api/admin/auth/logout | POST | 管理员登出 |

### 自测记录

**测试项**：
- ✅ 正常登录（用户名：admin，密码：123456）
- ✅ 错误密码登录（返回密码错误）
- ✅ 未登录访问需要认证的接口（返回401未授权）
- ✅ 获取当前管理员信息
- ✅ 登出

**测试命令**：
```bash
# 登录
curl -X POST "http://localhost:8080/api/admin/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 获取管理员信息
curl -X GET "http://localhost:8080/api/admin/auth/info" \
  -H "Authorization: Bearer <token>"

# 登出
curl -X POST "http://localhost:8080/api/admin/auth/logout" \
  -H "Authorization: Bearer <token>"
```

**结果**：通过

### 新增文件

```
backend/main-service/src/main/java/com/maoyan/
├── config/
│   └── AdminPasswordInitializer.java    # 密码初始化器
├── controller/admin/
│   └── AdminAuthController.java         # 认证控制器
├── dto/admin/
│   ├── AdminInfoResponse.java           # 管理员信息响应
│   ├── AdminLoginRequest.java           # 登录请求
│   └── AdminLoginResponse.java          # 登录响应
├── interceptor/
│   └── AdminAuthInterceptor.java        # JWT认证拦截器
├── mapper/
│   └── AdminRoleMapper.java             # 角色Mapper
├── service/
│   └── AdminAuthService.java            # 认证服务接口
├── service/impl/
│   └── AdminAuthServiceImpl.java        # 认证服务实现
└── util/
    ├── AdminContext.java                # 管理员上下文工具
    ├── JwtUtil.java                     # JWT工具类
    └── PasswordUtil.java                # 密码工具类
```

### 修改文件

```
backend/main-service/
├── pom.xml                              # 添加spring-security-crypto依赖
└── src/main/java/com/maoyan/config/
    └── WebMvcConfig.java                # 配置拦截器
```

---

*创建时间：2026-05-02*

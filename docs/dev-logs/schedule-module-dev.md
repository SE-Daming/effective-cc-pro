# 排片模块开发日志

## 2026-05-02 排片模块开发

### 开发内容

完成后端主服务排片模块开发，包括 C 端和 B 端接口。

### C 端接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 电影排片影院列表 | GET | /api/movies/{id}/cinemas | 查询某电影在哪些影院有排片 |
| 排片详情 | GET | /api/schedules/{id} | 获取场次详细信息 |
| 场次座位图 | GET | /api/schedules/{id}/seats | 获取场次的座位布局和状态 |
| 锁定座位 | POST | /api/schedules/{id}/lock-seats | 选座后锁定座位5分钟 |
| 释放座位锁 | DELETE | /api/seat-locks/{lockId} | 取消选座时释放锁定的座位 |

### B 端接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 排片列表 | GET | /api/admin/schedules | 分页查询排片列表 |
| 新增排片 | POST | /api/admin/schedules | 创建单场排片，自动检测冲突 |
| 批量排片 | POST | /api/admin/schedules/batch | 批量创建多天多场次的排片 |
| 删除排片 | DELETE | /api/admin/schedules/{id} | 删除场次 |
| 排片冲突检测 | POST | /api/admin/schedules/check-conflict | 检测指定时间是否与现有排片冲突 |

### 业务规则实现

1. **排片时间冲突检测**
   - 同一影厅场次间隔需 > 电影时长 + 清洁时间(15分钟)
   - 自动计算 end_time = show_time + duration + clean_duration

2. **座位锁定机制**
   - 锁定时长：5分钟
   - 使用数据库记录锁定状态
   - 可选 Redis 缓存锁定ID（Redis 不可用时降级为纯数据库方案）

### 关键文件

- **Mapper**: `SeatMapper.java`, `SeatLockMapper.java`, `HallMapper.java`
- **DTO**: `LockSeatsRequest.java`, `ScheduleCreateRequest.java`, `ScheduleBatchCreateRequest.java`, `ScheduleConflictCheckRequest.java`
- **VO**: `ScheduleDetailVO.java`, `ScheduleSeatVO.java`, `LockSeatsVO.java`, `MovieCinemaScheduleVO.java`, `ScheduleListItemVO.java`, `ScheduleConflictCheckVO.java`
- **Service**: `ScheduleService.java`, `ScheduleServiceImpl.java`
- **Controller**: `ScheduleController.java`, `AdminScheduleController.java`

### Bug 修复

1. **Hall 实体类字段问题**
   - 问题：`rows` 是 MySQL 保留字，导致 SQL 语法错误
   - 解决：使用 `@TableField("`rows`")` 注解转义

2. **空指针异常**
   - 问题：`LocalDate.parse(showDate)` 当参数为 null 时报错
   - 解决：添加空值判断

3. **Redis 依赖问题**
   - 问题：应用启动失败，缺少 StringRedisTemplate
   - 解决：将 Redis 配置改为条件加载，Redis 依赖改为可选注入

### 自测结果

**测试项**：
- ✅ 排片列表查询
- ✅ 排片冲突检测
- ✅ 新增排片
- ✅ 排片详情查询
- ✅ 场次座位图查询
- ✅ 锁定座位
- ✅ 释放座位锁
- ✅ 电影排片影院列表

**结果**：通过

---

*开发时间：2026-05-02*

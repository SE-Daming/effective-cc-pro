# Bug 追踪

> 记录项目中的 Bug 及修复状态

---

## Bug 列表

| ID | 描述 | 优先级 | 状态 | 发现时间 | 修复时间 | 验证时间 |
|----|------|--------|------|----------|----------|----------|
| B001 | 电影管理-新增电影接口返回 500 错误 | 高 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B002 | 电影管理-分类列表接口返回 500 错误 | 高 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B003 | 电影管理-搜索接口中文参数返回 400 错误 | 中 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B004 | 影院管理-新增影院接口返回 500 错误 | 高 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B005 | 影院管理-编辑影院接口返回 500 错误 | 高 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B006 | 影院管理-影厅列表接口返回 500 错误 | 高 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B007 | 影院管理-座位自动生成接口返回 500 错误 | 中 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B008 | 订单管理-订单状态筛选接口返回 500 错误 | 中 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B009 | 订单管理-退款审核接口返回 500 错误 | 中 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B010 | 用户管理-禁用用户接口返回 500 错误 | 低 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B011 | 优惠券管理-新增优惠券接口返回 500 错误 | 中 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B012 | 电影编辑页面-加载时显示"服务器内部错误" | 高 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B013 | 排片列表页面-显示"服务器内部错误"和"请求参数错误" | 中 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B014 | 排片列表接口-showDate参数为空字符串时返回500错误 | 中 | ✅ | 2026-05-03 | 2026-05-03 | 2026-05-03 |
| B015 | 排片管理页面-API请求pageSize=1000返回400错误 | 中 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |
| B016 | 排片日历接口-接口不存在返回500错误 | 中 | ✅ | 2026-05-02 | 2026-05-02 | 2026-05-02 |

---

## 回归测试结论

**2026-05-02 完整回归测试结果**: 所有 16 个 Bug 已验证修复。

- 后端 API 测试: 46/46 通过 (100%)
- 前端 UI 测试: 18/18 通过 (100%)

### 测试发现的接口路径差异

测试过程中发现测试计划文档与实际实现的差异（非Bug）：

| 测试计划 | 实际实现 | 类型 |
|----------|----------|------|
| PUT /admin/reviews/{id}/audit | PATCH /admin/reviews/{id}/audit | HTTP方法差异 |
| PUT /admin/users/{id}/status | PATCH /admin/users/{id}/status | HTTP方法差异 |
| PUT /admin/orders/{id}/refund | POST /admin/orders/{id}/refund-audit | 路径+方法差异 |
| POST /admin/coupons/{id}/issue | POST /admin/coupons/{id}/distribute | 路径差异 |
| GET /admin/movies/categories | GET /admin/movie-categories | 路径差异 |
| GET /admin/cinemas/{id}/halls | GET /admin/halls?cinemaId=xx | 路径结构差异 |
| POST /admin/cinemas/{id}/halls | POST /admin/halls | 路径结构差异 |

建议更新测试计划文档使路径与实际实现一致。

---

### B014 详细信息

**问题描述**: 排片列表接口在 `showDate` 参数为空字符串时返回 500 错误

**根本原因**: `ScheduleServiceImpl.getScheduleList()` 方法中，条件表达式的第三个参数无论如何都会被求值。当 `showDate` 为空字符串时，`LocalDate.parse(showDate)` 被调用导致异常。

**修复方案**: 将条件判断从 `showDate != null` 改为 `StrUtil.isNotBlank(showDate)`，确保空字符串情况下不执行日期解析。

**修复代码**:
```java
// 修复前
.eq(StrUtil.isNotBlank(showDate), Schedule::getShowDate, showDate != null ? LocalDate.parse(showDate) : null)

// 修复后
.eq(StrUtil.isNotBlank(showDate), Schedule::getShowDate, StrUtil.isNotBlank(showDate) ? LocalDate.parse(showDate) : null)
```

---

### B015 详细信息

**问题描述**: 排片管理页面加载时调用 `/api/admin/movies?pageSize=1000` 返回 400 错误

**根本原因**: 后端 `PageDTO` 有验证规则 `@Max(value = 100)`，限制 `pageSize` 最大为 100，前端传递 `pageSize=1000` 超出限制。

**修复方案**: 修改前端 `schedule/list.vue` 中的 `fetchOptions` 函数，将 `pageSize` 从 1000 改为 100。

**修复代码**:
```javascript
// 修复前
getCinemaList({ pageSize: 1000 }),
getMovieList({ pageSize: 1000 })

// 修复后
getCinemaList({ page: 1, pageSize: 100 }),
getMovieList({ page: 1, pageSize: 100 })
```

---

### B016 详细信息

**问题描述**: 排片日历接口 `/api/admin/schedules/calendar` 不存在，返回 500 错误

**根本原因**: 后端未实现排片日历接口。

**修复方案**: 
1. 在 `ScheduleService` 接口添加 `getScheduleCalendar` 方法
2. 在 `ScheduleServiceImpl` 实现该方法，返回按日期-时间分组的排片数据
3. 在 `AdminScheduleController` 添加 `/calendar` 接口

**新增代码**:
```java
// ScheduleService.java
Map<String, List<ScheduleListItemVO>> getScheduleCalendar(String startDate, String endDate);

// AdminScheduleController.java
@GetMapping("/calendar")
public Result<Map<String, List<ScheduleListItemVO>>> getScheduleCalendar(
        @RequestParam String startDate,
        @RequestParam String endDate) {
    return Result.success(scheduleService.getScheduleCalendar(startDate, endDate));
}
```

---

## 状态说明

| 标识 | 含义 |
|------|------|
| ⏳ | 待修复 |
| 🔵 | 修复中 |
| ✅ | 已修复 |
| ❌ | 无法复现/暂不修复 |

---

*创建时间：2025-05-02*
*最后更新：2026-05-02*

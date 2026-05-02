# 影院模块开发日志

> 开发时间：2026-05-02

---

## 开发内容

### C端接口

1. **影院列表**：GET /api/cinemas
   - 支持城市、区域、品牌、关键词筛选
   - 支持按距离或评分排序
   - 计算用户与影院的距离

2. **附近影院**：GET /api/cinemas/nearby
   - 基于经纬度计算距离
   - 支持搜索半径和数量限制

3. **影院详情**：GET /api/cinemas/{id}
   - 返回影院完整信息
   - 包含品牌名称、图片列表

4. **影院影厅列表**：GET /api/cinemas/{id}/halls
   - 返回影院下所有影厅

5. **影院排片日期**：GET /api/cinemas/{id}/schedule-dates
   - 返回未来7天有排片的日期列表
   - 标识今天、明天

6. **影院某日排片**：GET /api/cinemas/{id}/schedules
   - 按电影分组返回场次信息
   - 包含影厅名称、票价、余票等

7. **影院品牌列表**：GET /api/cinema-brands
   - 返回所有影院品牌

### B端接口

1. **影院列表**：GET /api/admin/cinemas
   - 支持关键词、城市、品牌、状态筛选
   - 分页查询

2. **新增影院**：POST /api/admin/cinemas
   - 支持图片列表上传

3. **更新影院**：PUT /api/admin/cinemas/{id}

4. **删除影院**：DELETE /api/admin/cinemas/{id}
   - 有影厅的影院无法删除

5. **影厅列表**：GET /api/admin/halls
   - 按影院ID查询

6. **新增影厅**：POST /api/admin/halls
   - 自动生成座位数据
   - 座位号格式：A01, A02, ... B01, B02...

7. **更新影厅**：PUT /api/admin/halls/{id}
   - 有排片的影厅无法修改座位布局

8. **删除影厅**：DELETE /api/admin/halls/{id}
   - 自动删除关联座位

---

## 关键文件

### DTO（请求参数）
- `dto/CinemaQueryDTO.java` - 影院查询参数
- `dto/NearbyCinemaQueryDTO.java` - 附近影院查询参数
- `dto/AdminCinemaQueryDTO.java` - 管理端影院查询参数
- `dto/CinemaCreateDTO.java` - 影院创建/更新参数
- `dto/HallCreateDTO.java` - 影厅创建/更新参数

### VO（响应数据）
- `vo/CinemaListVO.java` - 影院列表VO
- `vo/CinemaDetailVO.java` - 影院详情VO
- `vo/HallListVO.java` - 影厅列表VO
- `vo/CinemaBrandVO.java` - 影院品牌VO
- `vo/ScheduleDateVO.java` - 排片日期VO
- `vo/CinemaScheduleVO.java` - 影院排片VO
- `vo/ScheduleItemVO.java` - 场次项VO

### Service
- `service/CinemaService.java` - 影院服务接口
- `service/impl/CinemaServiceImpl.java` - 影院服务实现
- `service/HallService.java` - 影厅服务接口
- `service/impl/HallServiceImpl.java` - 影厅服务实现
- `service/CinemaBrandService.java` - 影院品牌服务接口
- `service/impl/CinemaBrandServiceImpl.java` - 影院品牌服务实现

### Controller
- `controller/api/cinema/CinemaController.java` - C端影院接口
- `controller/api/cinema/CinemaBrandController.java` - C端影院品牌接口
- `controller/admin/cinema/AdminCinemaController.java` - B端影院管理接口
- `controller/admin/cinema/AdminHallController.java` - B端影厅管理接口

---

## 技术要点

### 距离计算

使用 Haversine 公式计算两点间距离：

```java
private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
    double earthRadius = 6371; // 地球半径，单位km
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthRadius * c;
}
```

### 座位自动生成

创建影厅时自动生成座位：

```java
private String generateSeatNo(int row, int col) {
    char rowChar = (char) ('A' + row - 1);
    return String.format("%c%02d", rowChar, col);
}
```

座位号格式：A01, A02, ... B01, B02...

---

## 自测结果

- ✅ 编译通过
- ⏳ 接口测试（服务启动需要 Redis）

---

*创建时间：2026-05-02*

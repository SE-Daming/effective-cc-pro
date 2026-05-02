package com.maoyan.controller.admin.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.Result;
import com.maoyan.dto.order.AdminOrderQueryDTO;
import com.maoyan.dto.order.RefundAuditDTO;
import com.maoyan.service.OrderService;
import com.maoyan.util.AdminContext;
import com.maoyan.vo.order.OrderDetailVO;
import com.maoyan.vo.order.OrderListItemVO;
import com.maoyan.vo.order.RefundAuditVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * B端订单管理控制器
 *
 * @author maoyan
 */
@Tag(name = "B端-订单管理", description = "订单查看、退款审核相关接口")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * 获取订单列表
     */
    @Operation(summary = "获取订单列表", description = "分页查询订单列表")
    @GetMapping
    public Result<Page<OrderListItemVO>> getOrderList(
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "订单类型") @RequestParam(required = false) Integer type,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "影院ID") @RequestParam(required = false) Long cinemaId,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {

        AdminOrderQueryDTO dto = new AdminOrderQueryDTO();
        dto.setOrderNo(orderNo);
        dto.setStatus(status);
        dto.setType(type);
        dto.setUserId(userId);
        dto.setCinemaId(cinemaId);
        dto.setPage(page);
        dto.setPageSize(pageSize);

        // 解析时间参数
        if (startTime != null && !startTime.isEmpty()) {
            dto.setStartTime(java.time.LocalDateTime.parse(startTime.replace(" ", "T")));
        }
        if (endTime != null && !endTime.isEmpty()) {
            dto.setEndTime(java.time.LocalDateTime.parse(endTime.replace(" ", "T")));
        }

        return Result.success(orderService.getAdminOrderList(dto));
    }

    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情", description = "获取订单详细信息")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        return Result.success(orderService.getAdminOrderDetail(id));
    }

    /**
     * 退款审核
     */
    @Operation(summary = "退款审核", description = "审核用户的退款申请")
    @PostMapping("/{id}/refund-audit")
    public Result<RefundAuditVO> auditRefund(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Valid @RequestBody RefundAuditDTO dto) {
        // 获取当前管理员ID
        Integer adminId = AdminContext.getAdminId();
        if (adminId == null) {
            adminId = 1; // 临时测试用
        }
        return Result.success(orderService.auditRefund(id, dto, adminId));
    }

}

package com.maoyan.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 优惠券创建/更新参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "优惠券创建/更新参数")
public class CouponSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "优惠券名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "优惠券名称不能为空")
    private String name;

    @Schema(description = "类型：1满减 2折扣 3立减 4兑换", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "优惠券类型不能为空")
    private Integer type;

    @Schema(description = "优惠值（金额/折扣）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "优惠值不能为空")
    @Min(value = 0, message = "优惠值不能为负数")
    private BigDecimal value;

    @Schema(description = "最低消费金额")
    private BigDecimal minAmount;

    @Schema(description = "发放总量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "发放总量不能为空")
    @Min(value = 1, message = "发放总量最小为1")
    private Integer totalCount;

    @Schema(description = "每人限领数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "每人限领数量不能为空")
    @Min(value = 1, message = "每人限领数量最小为1")
    private Integer limitPerUser;

    @Schema(description = "有效天数（从领取开始计算）")
    private Integer validDays;

    @Schema(description = "有效期开始")
    private LocalDate validStart;

    @Schema(description = "有效期结束")
    private LocalDate validEnd;

    @Schema(description = "适用范围：1全部 2指定电影 3指定影院", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "适用范围不能为空")
    private Integer scope;

    @Schema(description = "适用ID列表（逗号分隔）")
    private String scopeIds;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

}

package com.maoyan.vo.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 优惠券详情VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "优惠券详情")
public class CouponDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "优惠券ID")
    private Long id;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "类型：1满减 2折扣 3立减 4兑换")
    private Integer type;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "优惠值")
    private BigDecimal value;

    @Schema(description = "最低消费金额")
    private BigDecimal minAmount;

    @Schema(description = "发放总量")
    private Integer totalCount;

    @Schema(description = "已领取数量")
    private Integer receiveCount;

    @Schema(description = "已使用数量")
    private Integer usedCount;

    @Schema(description = "每人限领数量")
    private Integer limitPerUser;

    @Schema(description = "有效天数")
    private Integer validDays;

    @Schema(description = "有效期开始")
    private LocalDate validStart;

    @Schema(description = "有效期结束")
    private LocalDate validEnd;

    @Schema(description = "适用范围：1全部 2指定电影 3指定影院")
    private Integer scope;

    @Schema(description = "适用范围名称")
    private String scopeName;

    @Schema(description = "适用ID列表（逗号分隔）")
    private String scopeIds;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

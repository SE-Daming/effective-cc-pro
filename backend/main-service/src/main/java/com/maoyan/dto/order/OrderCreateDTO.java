package com.maoyan.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建订单请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "创建订单请求")
public class OrderCreateDTO {

    @Schema(description = "锁定ID", required = true)
    private String lockId;

    @Schema(description = "订单类型：1电影票 2卖品 3组合", required = true)
    private Integer type;

    @Schema(description = "用户优惠券ID")
    private Long userCouponId;

    @Schema(description = "备注")
    private String remark;

}

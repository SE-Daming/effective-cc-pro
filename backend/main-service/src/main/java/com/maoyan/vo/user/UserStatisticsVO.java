package com.maoyan.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户统计数据响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "用户统计数据响应")
public class UserStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单数量")
    private Integer orderCount;

    @Schema(description = "累计消费金额")
    private BigDecimal totalConsumption;

    @Schema(description = "优惠券数量")
    private Integer couponCount;

    @Schema(description = "收藏数量")
    private Integer favoriteCount;

}

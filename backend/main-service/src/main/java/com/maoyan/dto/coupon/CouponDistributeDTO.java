package com.maoyan.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券发放参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "优惠券发放参数")
public class CouponDistributeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID列表")
    private List<Long> userIds;

    @Schema(description = "是否发放给所有用户")
    private Boolean all;

}

package com.maoyan.dto.coupon;

import com.maoyan.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户优惠券查询参数
 *
 * @author maoyan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户优惠券查询参数")
public class UserCouponQueryDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态：1未使用 2已使用 3已过期")
    private Integer status;

}

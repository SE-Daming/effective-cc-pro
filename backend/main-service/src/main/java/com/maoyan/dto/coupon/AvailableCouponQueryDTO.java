package com.maoyan.dto.coupon;

import com.maoyan.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 可领取优惠券查询参数
 *
 * @author maoyan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "可领取优惠券查询参数")
public class AvailableCouponQueryDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

}

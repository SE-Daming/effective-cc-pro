package com.maoyan.dto.coupon;

import com.maoyan.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 优惠券管理查询参数
 *
 * @author maoyan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "优惠券管理查询参数")
public class AdminCouponQueryDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "类型：1满减 2折扣 3立减 4兑换")
    private Integer type;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;

}

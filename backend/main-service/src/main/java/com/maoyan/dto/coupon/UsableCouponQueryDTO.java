package com.maoyan.dto.coupon;

import com.maoyan.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 可用优惠券查询参数
 *
 * @author maoyan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "可用优惠券查询参数")
public class UsableCouponQueryDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单金额不能为空")
    private BigDecimal amount;

    @Schema(description = "电影ID")
    private Long movieId;

    @Schema(description = "影院ID")
    private Long cinemaId;

}

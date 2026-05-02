package com.maoyan.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端用户详情响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "管理端用户详情响应")
public class AdminUserDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "微信openid")
    private String openid;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    @Schema(description = "累计消费金额")
    private BigDecimal totalConsumption;

    @Schema(description = "状态：0禁用 1正常")
    private Integer status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // 统计数据
    @Schema(description = "订单数量")
    private Integer orderCount;

    @Schema(description = "优惠券数量")
    private Integer couponCount;

    @Schema(description = "收藏数量")
    private Integer favoriteCount;

}

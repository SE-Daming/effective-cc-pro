package com.maoyan.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "用户信息响应")
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "手机号（脱敏）")
    private String phone;

    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    @Schema(description = "累计消费金额")
    private BigDecimal totalConsumption;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

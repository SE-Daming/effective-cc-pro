package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 锁定座位响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "锁定座位响应")
public class LockSeatsVO {

    @Schema(description = "锁定ID")
    private String lockId;

    @Schema(description = "锁定的座位列表")
    private List<LockedSeat> seats;

    @Schema(description = "总价")
    private BigDecimal totalPrice;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    /**
     * 锁定的座位信息
     */
    @Data
    @Schema(description = "锁定的座位信息")
    public static class LockedSeat {
        @Schema(description = "座位ID")
        private Long id;

        @Schema(description = "座位号")
        private String seatNo;

        @Schema(description = "行号")
        private Integer rowNum;

        @Schema(description = "列号")
        private Integer colNum;

        @Schema(description = "票价")
        private BigDecimal price;
    }
}

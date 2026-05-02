package com.maoyan.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表项
 *
 * @author maoyan
 */
@Data
@Schema(description = "订单列表项")
public class OrderListItemVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单类型：1电影票 2卖品 3组合")
    private Integer type;

    @Schema(description = "订单状态：1待支付 2已支付 3已出票 4已完成 5已取消 6退款中 7已退款")
    private Integer status;

    @Schema(description = "订单总额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "票务信息列表")
    private List<TicketInfo> tickets;

    /**
     * 票务信息
     */
    @Data
    @Schema(description = "票务信息")
    public static class TicketInfo {
        @Schema(description = "电影名称")
        private String movieTitle;

        @Schema(description = "影院名称")
        private String cinemaName;

        @Schema(description = "影厅名称")
        private String hallName;

        @Schema(description = "放映日期")
        private String showDate;

        @Schema(description = "放映时间")
        private String showTime;

        @Schema(description = "座位号")
        private String seatNo;
    }

}

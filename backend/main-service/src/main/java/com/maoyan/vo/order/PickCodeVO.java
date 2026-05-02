package com.maoyan.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 取票码响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "取票码响应")
public class PickCodeVO {

    @Schema(description = "取票码")
    private String pickCode;

    @Schema(description = "取票二维码URL")
    private String pickQrcode;

    @Schema(description = "票务列表")
    private List<TicketItem> tickets;

    /**
     * 票务项
     */
    @Data
    @Schema(description = "票务项")
    public static class TicketItem {
        @Schema(description = "座位号")
        private String seatNo;

        @Schema(description = "影厅名称")
        private String hallName;
    }

}

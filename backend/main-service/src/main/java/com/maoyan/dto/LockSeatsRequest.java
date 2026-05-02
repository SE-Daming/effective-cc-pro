package com.maoyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 锁定座位请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "锁定座位请求")
public class LockSeatsRequest {

    @NotEmpty(message = "座位ID不能为空")
    @Schema(description = "座位ID列表", required = true)
    private List<Long> seatIds;

}

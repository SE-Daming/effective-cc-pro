package com.maoyan.controller.api.cinema;

import com.maoyan.common.Result;
import com.maoyan.service.CinemaBrandService;
import com.maoyan.vo.CinemaBrandVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * C端影院品牌接口
 *
 * @author maoyan
 */
@Tag(name = "C端-影院品牌", description = "影院品牌相关接口")
@RestController
@RequestMapping("/api/cinema-brands")
@RequiredArgsConstructor
public class CinemaBrandController {

    private final CinemaBrandService cinemaBrandService;

    @Operation(summary = "影院品牌列表")
    @GetMapping
    public Result<Map<String, Object>> getBrandList() {
        List<CinemaBrandVO> list = cinemaBrandService.getAllBrands();
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        return Result.success(data);
    }

}

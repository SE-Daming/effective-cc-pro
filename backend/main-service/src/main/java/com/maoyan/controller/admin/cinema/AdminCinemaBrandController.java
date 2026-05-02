package com.maoyan.controller.admin.cinema;

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
 * B端影院品牌管理接口
 *
 * @author maoyan
 */
@Tag(name = "B端-影院品牌管理", description = "影院品牌管理接口")
@RestController
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
public class AdminCinemaBrandController {

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

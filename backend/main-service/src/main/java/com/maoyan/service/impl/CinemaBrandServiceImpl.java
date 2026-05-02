package com.maoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maoyan.entity.CinemaBrand;
import com.maoyan.mapper.CinemaBrandMapper;
import com.maoyan.service.CinemaBrandService;
import com.maoyan.vo.CinemaBrandVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 影院品牌服务实现类
 *
 * @author maoyan
 */
@Service
@RequiredArgsConstructor
public class CinemaBrandServiceImpl extends ServiceImpl<CinemaBrandMapper, CinemaBrand> implements CinemaBrandService {

    @Override
    public List<CinemaBrandVO> getAllBrands() {
        LambdaQueryWrapper<CinemaBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CinemaBrand::getStatus, 1)
                .orderByAsc(CinemaBrand::getSort);

        List<CinemaBrand> brands = baseMapper.selectList(wrapper);

        return brands.stream().map(brand -> {
            CinemaBrandVO vo = new CinemaBrandVO();
            vo.setId(brand.getId());
            vo.setName(brand.getName());
            vo.setLogo(brand.getLogo());
            vo.setSort(brand.getSort());
            return vo;
        }).collect(Collectors.toList());
    }

}

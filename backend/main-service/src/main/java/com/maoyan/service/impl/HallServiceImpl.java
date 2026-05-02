package com.maoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.maoyan.common.ResultCode;
import com.maoyan.dto.HallCreateDTO;
import com.maoyan.entity.Cinema;
import com.maoyan.entity.Hall;
import com.maoyan.entity.Seat;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.CinemaMapper;
import com.maoyan.mapper.HallMapper;
import com.maoyan.mapper.SeatMapper;
import com.maoyan.service.HallService;
import com.maoyan.vo.HallListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 影厅服务实现类
 *
 * @author maoyan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HallServiceImpl extends ServiceImpl<HallMapper, Hall> implements HallService {

    private final CinemaMapper cinemaMapper;
    private final SeatMapper seatMapper;

    @Override
    public List<HallListVO> getHallsByCinemaId(Long cinemaId) {
        // 检查影院是否存在
        Cinema cinema = cinemaMapper.selectById(cinemaId);
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        LambdaQueryWrapper<Hall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hall::getCinemaId, cinemaId)
                .orderByAsc(Hall::getId);

        List<Hall> halls = baseMapper.selectList(wrapper);

        return halls.stream().map(hall -> {
            HallListVO vo = new HallListVO();
            vo.setId(hall.getId());
            vo.setName(hall.getName());
            vo.setType(hall.getType());
            vo.setTotalSeats(hall.getTotalSeats());
            vo.setStatus(hall.getStatus());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHall(HallCreateDTO createDTO) {
        // 检查影院是否存在
        Cinema cinema = cinemaMapper.selectById(createDTO.getCinemaId());
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        // 检查影厅名称是否重复
        LambdaQueryWrapper<Hall> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(Hall::getCinemaId, createDTO.getCinemaId())
                .eq(Hall::getName, createDTO.getName());
        if (baseMapper.selectCount(existWrapper) > 0) {
            throw new BusinessException("影厅名称已存在");
        }

        Hall hall = new Hall();
        hall.setCinemaId(createDTO.getCinemaId());
        hall.setName(createDTO.getName());
        hall.setType(createDTO.getType());
        hall.setRows(createDTO.getRows());
        hall.setCols(createDTO.getCols());
        hall.setTotalSeats(createDTO.getRows() * createDTO.getCols());
        hall.setSeatLayout(createDTO.getSeatLayout());
        hall.setStatus(1);

        baseMapper.insert(hall);

        // 自动生成座位
        generateSeats(hall.getId(), createDTO.getRows(), createDTO.getCols());

        return hall.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHall(Long id, HallCreateDTO createDTO) {
        Hall hall = baseMapper.selectById(id);
        if (hall == null) {
            throw new BusinessException(ResultCode.HALL_NOT_FOUND);
        }

        // 检查影厅名称是否重复（排除自己）
        LambdaQueryWrapper<Hall> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(Hall::getCinemaId, hall.getCinemaId())
                .eq(Hall::getName, createDTO.getName())
                .ne(Hall::getId, id);
        if (baseMapper.selectCount(existWrapper) > 0) {
            throw new BusinessException("影厅名称已存在");
        }

        // 检查是否修改了行数或列数
        boolean sizeChanged = !hall.getRows().equals(createDTO.getRows()) || !hall.getCols().equals(createDTO.getCols());
        if (sizeChanged) {
            // 检查是否已有排片
            // 这里简化处理，如果有排片则不允许修改大小
            // 实际业务可能需要更复杂的逻辑
            throw new BusinessException("该影厅已有排片，无法修改座位布局");
        }

        hall.setName(createDTO.getName());
        hall.setType(createDTO.getType());
        hall.setSeatLayout(createDTO.getSeatLayout());

        baseMapper.updateById(hall);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHall(Long id) {
        Hall hall = baseMapper.selectById(id);
        if (hall == null) {
            throw new BusinessException(ResultCode.HALL_NOT_FOUND);
        }

        // 检查是否已有排片
        // 这里简化处理，实际需要检查schedule表
        // 暂时允许删除

        // 删除座位
        LambdaQueryWrapper<Seat> seatWrapper = new LambdaQueryWrapper<>();
        seatWrapper.eq(Seat::getHallId, id);
        seatMapper.delete(seatWrapper);

        // 删除影厅
        baseMapper.deleteById(id);
    }

    @Override
    public Hall getById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 自动生成座位
     */
    private void generateSeats(Long hallId, int rows, int cols) {
        List<Seat> seats = new ArrayList<>();

        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                Seat seat = new Seat();
                seat.setHallId(hallId);
                seat.setRowNum(row);
                seat.setColNum(col);
                seat.setSeatNo(generateSeatNo(row, col));
                seat.setSeatType(1); // 默认普通座位
                seat.setStatus(1); // 默认正常
                seats.add(seat);
            }
        }

        // 批量插入
        for (Seat seat : seats) {
            seatMapper.insert(seat);
        }

        log.info("影厅 {} 自动生成 {} 个座位", hallId, seats.size());
    }

    /**
     * 生成座位号
     * 例如：第1行第8列 -> A08, 第2行第12列 -> B12
     */
    private String generateSeatNo(int row, int col) {
        char rowChar = (char) ('A' + row - 1);
        return String.format("%c%02d", rowChar, col);
    }

}

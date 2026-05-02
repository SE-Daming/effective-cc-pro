package com.maoyan.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.enums.SeatLockStatus;
import com.maoyan.dto.LockSeatsRequest;
import com.maoyan.dto.ScheduleBatchCreateRequest;
import com.maoyan.dto.ScheduleConflictCheckRequest;
import com.maoyan.dto.ScheduleCreateRequest;
import com.maoyan.entity.*;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.*;
import com.maoyan.service.ScheduleService;
import com.maoyan.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 排片服务实现
 *
 * @author maoyan
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final MovieMapper movieMapper;
    private final CinemaMapper cinemaMapper;
    private final HallMapper hallMapper;
    private final SeatMapper seatMapper;
    private final SeatLockMapper seatLockMapper;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 座位锁定时长（分钟）
     */
    private static final int LOCK_DURATION_MINUTES = 5;

    /**
     * 默认清洁时间（分钟）
     */
    private static final int DEFAULT_CLEAN_DURATION = 15;

    public ScheduleServiceImpl(ScheduleMapper scheduleMapper, MovieMapper movieMapper,
                               CinemaMapper cinemaMapper, HallMapper hallMapper,
                               SeatMapper seatMapper, SeatLockMapper seatLockMapper) {
        this.scheduleMapper = scheduleMapper;
        this.movieMapper = movieMapper;
        this.cinemaMapper = cinemaMapper;
        this.hallMapper = hallMapper;
        this.seatMapper = seatMapper;
        this.seatLockMapper = seatLockMapper;
    }

    @Override
    public Page<MovieCinemaScheduleVO> getMovieCinemaSchedules(Long movieId, String city, String date, Integer page, Integer pageSize) {
        // 查询电影
        Movie movie = movieMapper.selectById(movieId);
        if (movie == null) {
            throw new BusinessException("电影不存在");
        }

        // 解析日期
        LocalDate showDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);

        // 查询该电影在该日期的所有排片
        LambdaQueryWrapper<Schedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.eq(Schedule::getMovieId, movieId)
                .eq(Schedule::getShowDate, showDate)
                .eq(Schedule::getStatus, 1);
        List<Schedule> schedules = scheduleMapper.selectList(scheduleWrapper);

        if (schedules.isEmpty()) {
            return new Page<>(page, pageSize, 0);
        }

        // 按影院分组
        Map<Long, List<Schedule>> cinemaScheduleMap = schedules.stream()
                .collect(Collectors.groupingBy(Schedule::getCinemaId));

        // 查询影院信息
        List<Long> cinemaIds = new ArrayList<>(cinemaScheduleMap.keySet());
        LambdaQueryWrapper<Cinema> cinemaWrapper = new LambdaQueryWrapper<>();
        cinemaWrapper.in(Cinema::getId, cinemaIds)
                .eq(Cinema::getStatus, 1);
        if (StrUtil.isNotBlank(city)) {
            cinemaWrapper.eq(Cinema::getCity, city);
        }
        List<Cinema> cinemas = cinemaMapper.selectList(cinemaWrapper);

        // 构建结果
        List<MovieCinemaScheduleVO> list = new ArrayList<>();
        for (Cinema cinema : cinemas) {
            MovieCinemaScheduleVO vo = new MovieCinemaScheduleVO();
            vo.setCinemaId(cinema.getId());
            vo.setCinemaName(cinema.getName());
            vo.setAddress(cinema.getAddress());
            vo.setDistance(null); // 距离需要前端传经纬度计算，这里暂不处理

            // 获取该影院的场次
            List<Schedule> cinemaSchedules = cinemaScheduleMap.get(cinema.getId());
            if (cinemaSchedules != null && !cinemaSchedules.isEmpty()) {
                // 查询影厅信息
                Set<Long> hallIds = cinemaSchedules.stream()
                        .map(Schedule::getHallId)
                        .collect(Collectors.toSet());
                Map<Long, Hall> hallMap = hallMapper.selectBatchIds(hallIds).stream()
                        .collect(Collectors.toMap(Hall::getId, h -> h));

                List<MovieCinemaScheduleVO.ScheduleSimpleVO> scheduleVOs = cinemaSchedules.stream()
                        .sorted(Comparator.comparing(Schedule::getShowTime))
                        .map(schedule -> {
                            MovieCinemaScheduleVO.ScheduleSimpleVO scheduleVO = new MovieCinemaScheduleVO.ScheduleSimpleVO();
                            scheduleVO.setId(schedule.getId());
                            Hall hall = hallMap.get(schedule.getHallId());
                            if (hall != null) {
                                scheduleVO.setHallName(hall.getName());
                                scheduleVO.setHallType(hall.getType());
                            }
                            scheduleVO.setShowTime(schedule.getShowTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                            scheduleVO.setPrice(schedule.getPrice());
                            scheduleVO.setSoldSeats(schedule.getSoldSeats());
                            scheduleVO.setTotalSeats(schedule.getTotalSeats());
                            return scheduleVO;
                        })
                        .collect(Collectors.toList());
                vo.setSchedules(scheduleVOs);
            }
            list.add(vo);
        }

        // 分页处理
        Page<MovieCinemaScheduleVO> result = new Page<>(page, pageSize, list.size());
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        result.setRecords(fromIndex < list.size() ? list.subList(fromIndex, toIndex) : new ArrayList<>());
        return result;
    }

    @Override
    public ScheduleDetailVO getScheduleDetail(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("场次不存在");
        }

        // 查询电影
        Movie movie = movieMapper.selectById(schedule.getMovieId());
        // 查询影院
        Cinema cinema = cinemaMapper.selectById(schedule.getCinemaId());
        // 查询影厅
        Hall hall = hallMapper.selectById(schedule.getHallId());

        ScheduleDetailVO vo = new ScheduleDetailVO();
        vo.setId(schedule.getId());
        vo.setMovieId(schedule.getMovieId());
        if (movie != null) {
            vo.setMovieTitle(movie.getTitle());
            vo.setMoviePoster(movie.getPoster());
            vo.setMovieDuration(movie.getDuration());
        }
        vo.setCinemaId(schedule.getCinemaId());
        if (cinema != null) {
            vo.setCinemaName(cinema.getName());
        }
        vo.setHallId(schedule.getHallId());
        if (hall != null) {
            vo.setHallName(hall.getName());
            vo.setHallType(hall.getType());
        }
        vo.setShowDate(schedule.getShowDate());
        vo.setShowTime(schedule.getShowTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        if (schedule.getEndTime() != null) {
            vo.setEndTime(schedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        vo.setPrice(schedule.getPrice());
        vo.setTotalSeats(schedule.getTotalSeats());
        vo.setSoldSeats(schedule.getSoldSeats() != null ? schedule.getSoldSeats() : 0);
        vo.setStatus(schedule.getStatus());

        return vo;
    }

    @Override
    public ScheduleSeatVO getScheduleSeats(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("场次不存在");
        }

        Hall hall = hallMapper.selectById(schedule.getHallId());
        if (hall == null) {
            throw new BusinessException("影厅不存在");
        }

        Movie movie = movieMapper.selectById(schedule.getMovieId());

        // 查询影厅所有座位
        LambdaQueryWrapper<Seat> seatWrapper = new LambdaQueryWrapper<>();
        seatWrapper.eq(Seat::getHallId, hall.getId())
                .orderByAsc(Seat::getRowNum)
                .orderByAsc(Seat::getColNum);
        List<Seat> seats = seatMapper.selectList(seatWrapper);

        // 查询该场次已锁定/已售的座位
        LambdaQueryWrapper<SeatLock> lockWrapper = new LambdaQueryWrapper<>();
        lockWrapper.eq(SeatLock::getScheduleId, scheduleId)
                .in(SeatLock::getStatus, Arrays.asList(SeatLockStatus.LOCKED.getCode(), SeatLockStatus.SOLD.getCode()));
        List<SeatLock> seatLocks = seatLockMapper.selectList(lockWrapper);

        // 座位锁定状态映射
        Map<Long, Integer> seatLockStatusMap = seatLocks.stream()
                .collect(Collectors.toMap(SeatLock::getSeatId, SeatLock::getStatus));

        // 构建座位信息
        List<ScheduleSeatVO.SeatInfo> seatInfos = seats.stream().map(seat -> {
            ScheduleSeatVO.SeatInfo seatInfo = new ScheduleSeatVO.SeatInfo();
            seatInfo.setId(seat.getId());
            seatInfo.setRowNum(seat.getRowNum());
            seatInfo.setColNum(seat.getColNum());
            seatInfo.setSeatNo(seat.getSeatNo());
            seatInfo.setSeatType(seat.getSeatType());
            seatInfo.setStatus(seat.getStatus());
            // 锁定状态：0可选 1已锁定 2已售
            Integer lockStatus = seatLockStatusMap.get(seat.getId());
            seatInfo.setLockStatus(lockStatus != null ? lockStatus : 0);
            return seatInfo;
        }).collect(Collectors.toList());

        // 构建返回结果
        ScheduleSeatVO vo = new ScheduleSeatVO();

        // 影厅信息
        ScheduleSeatVO.HallInfo hallInfo = new ScheduleSeatVO.HallInfo();
        hallInfo.setId(hall.getId());
        hallInfo.setName(hall.getName());
        hallInfo.setType(hall.getType());
        hallInfo.setRows(hall.getRows());
        hallInfo.setCols(hall.getCols());
        vo.setHallInfo(hallInfo);

        // 电影信息
        if (movie != null) {
            ScheduleSeatVO.MovieInfo movieInfo = new ScheduleSeatVO.MovieInfo();
            movieInfo.setId(movie.getId());
            movieInfo.setTitle(movie.getTitle());
            movieInfo.setPoster(movie.getPoster());
            movieInfo.setDuration(movie.getDuration());
            vo.setMovieInfo(movieInfo);
        }

        // 场次信息
        ScheduleSeatVO.ScheduleInfo scheduleInfo = new ScheduleSeatVO.ScheduleInfo();
        scheduleInfo.setId(schedule.getId());
        scheduleInfo.setShowDate(schedule.getShowDate());
        scheduleInfo.setShowTime(schedule.getShowTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        scheduleInfo.setPrice(schedule.getPrice());
        vo.setScheduleInfo(scheduleInfo);

        vo.setSeats(seatInfos);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LockSeatsVO lockSeats(Long scheduleId, LockSeatsRequest request, Long userId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("场次不存在");
        }
        if (schedule.getStatus() != 1) {
            throw new BusinessException("场次已取消");
        }

        List<Long> seatIds = request.getSeatIds();
        if (seatIds == null || seatIds.isEmpty()) {
            throw new BusinessException("请选择座位");
        }
        if (seatIds.size() > 4) {
            throw new BusinessException("最多选择4个座位");
        }

        // 查询座位
        List<Seat> seats = seatMapper.selectBatchIds(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new BusinessException("部分座位不存在");
        }

        // 检查座位是否属于该场次的影厅
        Hall hall = hallMapper.selectById(schedule.getHallId());
        for (Seat seat : seats) {
            if (!seat.getHallId().equals(schedule.getHallId())) {
                throw new BusinessException("座位" + seat.getSeatNo() + "不属于该场次影厅");
            }
            if (seat.getStatus() != 1) {
                throw new BusinessException("座位" + seat.getSeatNo() + "不可用");
            }
        }

        // 检查座位是否已被锁定或售出
        LambdaQueryWrapper<SeatLock> lockWrapper = new LambdaQueryWrapper<>();
        lockWrapper.eq(SeatLock::getScheduleId, scheduleId)
                .in(SeatLock::getSeatId, seatIds)
                .in(SeatLock::getStatus, Arrays.asList(SeatLockStatus.LOCKED.getCode(), SeatLockStatus.SOLD.getCode()));
        List<SeatLock> existingLocks = seatLockMapper.selectList(lockWrapper);

        if (!existingLocks.isEmpty()) {
            String lockedSeats = existingLocks.stream()
                    .map(SeatLock::getSeatNo)
                    .collect(Collectors.joining(", "));
            throw new BusinessException("座位 " + lockedSeats + " 已被选择");
        }

        // 生成锁定ID
        String lockId = IdUtil.fastSimpleUUID();

        // 计算过期时间
        LocalDateTime expireTime = LocalDateTime.now().plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES);

        // 锁定座位
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<LockSeatsVO.LockedSeat> lockedSeats = new ArrayList<>();

        for (Seat seat : seats) {
            SeatLock seatLock = new SeatLock();
            seatLock.setScheduleId(scheduleId);
            seatLock.setSeatId(seat.getId());
            seatLock.setSeatNo(seat.getSeatNo());
            seatLock.setUserId(userId);
            seatLock.setStatus(SeatLockStatus.LOCKED.getCode());
            seatLock.setExpireTime(expireTime);
            seatLockMapper.insert(seatLock);

            LockSeatsVO.LockedSeat lockedSeat = new LockSeatsVO.LockedSeat();
            lockedSeat.setId(seat.getId());
            lockedSeat.setSeatNo(seat.getSeatNo());
            lockedSeat.setRowNum(seat.getRowNum());
            lockedSeat.setColNum(seat.getColNum());
            lockedSeat.setPrice(schedule.getPrice());
            lockedSeats.add(lockedSeat);

            totalPrice = totalPrice.add(schedule.getPrice());
        }

        // 将锁定ID存入Redis，设置过期时间（如果Redis可用）
        if (stringRedisTemplate != null) {
            String redisKey = "seat_lock:" + lockId;
            String lockSeatIds = seatIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            stringRedisTemplate.opsForValue().set(redisKey, lockSeatIds, LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        }

        // 构建返回结果
        LockSeatsVO vo = new LockSeatsVO();
        vo.setLockId(lockId);
        vo.setSeats(lockedSeats);
        vo.setTotalPrice(totalPrice);
        vo.setExpireTime(expireTime);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseSeatLock(String lockId, Long userId) {
        List<Long> seatIds = new ArrayList<>();

        // 如果Redis可用，从Redis获取锁定的座位ID
        if (stringRedisTemplate != null) {
            String redisKey = "seat_lock:" + lockId;
            String lockSeatIds = stringRedisTemplate.opsForValue().get(redisKey);
            if (lockSeatIds != null) {
                seatIds = Arrays.stream(lockSeatIds.split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
                stringRedisTemplate.delete(redisKey);
            }
        }

        // 如果Redis中没有，从数据库查询（通过lockId匹配，这里lockId暂用UUID，需要另外处理）
        // 简化处理：直接通过用户ID和锁定状态查询
        if (seatIds.isEmpty()) {
            // 直接通过用户ID和锁定状态释放座位锁
            LambdaQueryWrapper<SeatLock> lockWrapper = new LambdaQueryWrapper<>();
            lockWrapper.eq(SeatLock::getUserId, userId)
                    .eq(SeatLock::getStatus, SeatLockStatus.LOCKED.getCode())
                    .gt(SeatLock::getExpireTime, LocalDateTime.now());
            List<SeatLock> seatLocks = seatLockMapper.selectList(lockWrapper);
            seatIds = seatLocks.stream().map(SeatLock::getSeatId).collect(Collectors.toList());
        }

        // 更新锁定状态为释放
        for (Long seatId : seatIds) {
            LambdaQueryWrapper<SeatLock> lockWrapper = new LambdaQueryWrapper<>();
            lockWrapper.eq(SeatLock::getSeatId, seatId)
                    .eq(SeatLock::getUserId, userId)
                    .eq(SeatLock::getStatus, SeatLockStatus.LOCKED.getCode());
            SeatLock seatLock = seatLockMapper.selectOne(lockWrapper);
            if (seatLock != null) {
                seatLock.setStatus(SeatLockStatus.RELEASED.getCode());
                seatLockMapper.updateById(seatLock);
            }
        }
    }

    @Override
    public Page<ScheduleListItemVO> getScheduleList(Long cinemaId, Long movieId, String showDate, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(cinemaId != null, Schedule::getCinemaId, cinemaId)
                .eq(movieId != null, Schedule::getMovieId, movieId)
                .eq(StrUtil.isNotBlank(showDate), Schedule::getShowDate, showDate != null ? LocalDate.parse(showDate) : null)
                .orderByDesc(Schedule::getShowDate)
                .orderByAsc(Schedule::getShowTime);

        Page<Schedule> schedulePage = scheduleMapper.selectPage(new Page<>(page, pageSize), wrapper);

        // 转换为VO
        Page<ScheduleListItemVO> result = new Page<>(page, pageSize, schedulePage.getTotal());
        List<ScheduleListItemVO> voList = schedulePage.getRecords().stream()
                .map(this::convertToListItemVO)
                .collect(Collectors.toList());
        result.setRecords(voList);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSchedule(ScheduleCreateRequest request) {
        // 验证电影
        Movie movie = movieMapper.selectById(request.getMovieId());
        if (movie == null) {
            throw new BusinessException("电影不存在");
        }

        // 验证影院
        Cinema cinema = cinemaMapper.selectById(request.getCinemaId());
        if (cinema == null) {
            throw new BusinessException("影院不存在");
        }

        // 验证影厅
        Hall hall = hallMapper.selectById(request.getHallId());
        if (hall == null) {
            throw new BusinessException("影厅不存在");
        }
        if (!hall.getCinemaId().equals(cinema.getId())) {
            throw new BusinessException("影厅不属于该影院");
        }

        // 解析放映时间
        LocalTime showTime = LocalTime.parse(request.getShowTime(), DateTimeFormatter.ofPattern("HH:mm"));

        // 计算结束时间
        int duration = movie.getDuration() != null ? movie.getDuration() : 120;
        int cleanDuration = request.getCleanDuration() != null ? request.getCleanDuration() : DEFAULT_CLEAN_DURATION;
        LocalTime endTime = showTime.plusMinutes(duration + cleanDuration);

        // 检测冲突
        ScheduleConflictCheckRequest conflictRequest = new ScheduleConflictCheckRequest();
        conflictRequest.setHallId(request.getHallId());
        conflictRequest.setShowDate(request.getShowDate());
        conflictRequest.setShowTime(request.getShowTime());
        conflictRequest.setDuration(duration);
        conflictRequest.setCleanDuration(cleanDuration);

        ScheduleConflictCheckVO conflictResult = checkConflict(conflictRequest);
        if (conflictResult.getHasConflict()) {
            throw new BusinessException("排片时间冲突，请选择其他时间");
        }

        // 创建排片
        Schedule schedule = new Schedule();
        schedule.setMovieId(request.getMovieId());
        schedule.setCinemaId(request.getCinemaId());
        schedule.setHallId(request.getHallId());
        schedule.setShowDate(request.getShowDate());
        schedule.setShowTime(showTime);
        schedule.setEndTime(endTime);
        schedule.setCleanDuration(cleanDuration);
        schedule.setPrice(request.getPrice());
        schedule.setTotalSeats(hall.getTotalSeats());
        schedule.setSoldSeats(0);
        schedule.setStatus(1);

        scheduleMapper.insert(schedule);
        return schedule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchCreateSchedule(ScheduleBatchCreateRequest request) {
        // 验证电影
        Movie movie = movieMapper.selectById(request.getMovieId());
        if (movie == null) {
            throw new BusinessException("电影不存在");
        }

        // 验证影院
        Cinema cinema = cinemaMapper.selectById(request.getCinemaId());
        if (cinema == null) {
            throw new BusinessException("影院不存在");
        }

        // 验证影厅
        Hall hall = hallMapper.selectById(request.getHallId());
        if (hall == null) {
            throw new BusinessException("影厅不存在");
        }
        if (!hall.getCinemaId().equals(cinema.getId())) {
            throw new BusinessException("影厅不属于该影院");
        }

        int duration = movie.getDuration() != null ? movie.getDuration() : 120;
        int cleanDuration = request.getCleanDuration() != null ? request.getCleanDuration() : DEFAULT_CLEAN_DURATION;

        int count = 0;
        List<String> conflictMessages = new ArrayList<>();

        for (LocalDate showDate : request.getShowDates()) {
            for (String showTimeStr : request.getShowTimes()) {
                // 检测冲突
                ScheduleConflictCheckRequest conflictRequest = new ScheduleConflictCheckRequest();
                conflictRequest.setHallId(request.getHallId());
                conflictRequest.setShowDate(showDate);
                conflictRequest.setShowTime(showTimeStr);
                conflictRequest.setDuration(duration);
                conflictRequest.setCleanDuration(cleanDuration);

                ScheduleConflictCheckVO conflictResult = checkConflict(conflictRequest);
                if (conflictResult.getHasConflict()) {
                    conflictMessages.add(showDate + " " + showTimeStr);
                    continue;
                }

                // 创建排片
                LocalTime showTime = LocalTime.parse(showTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime endTime = showTime.plusMinutes(duration + cleanDuration);

                Schedule schedule = new Schedule();
                schedule.setMovieId(request.getMovieId());
                schedule.setCinemaId(request.getCinemaId());
                schedule.setHallId(request.getHallId());
                schedule.setShowDate(showDate);
                schedule.setShowTime(showTime);
                schedule.setEndTime(endTime);
                schedule.setCleanDuration(cleanDuration);
                schedule.setPrice(request.getPrice());
                schedule.setTotalSeats(hall.getTotalSeats());
                schedule.setSoldSeats(0);
                schedule.setStatus(1);

                scheduleMapper.insert(schedule);
                count++;
            }
        }

        if (!conflictMessages.isEmpty()) {
            log.warn("批量排片部分冲突: {}", conflictMessages);
        }

        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("场次不存在");
        }

        // 检查是否有已售座位
        if (schedule.getSoldSeats() != null && schedule.getSoldSeats() > 0) {
            throw new BusinessException("该场次已有售票，无法删除");
        }

        // 检查是否有未过期的座位锁
        LambdaQueryWrapper<SeatLock> lockWrapper = new LambdaQueryWrapper<>();
        lockWrapper.eq(SeatLock::getScheduleId, id)
                .eq(SeatLock::getStatus, SeatLockStatus.LOCKED.getCode())
                .gt(SeatLock::getExpireTime, LocalDateTime.now());
        Long lockCount = seatLockMapper.selectCount(lockWrapper);
        if (lockCount > 0) {
            throw new BusinessException("该场次有正在进行的订单，无法删除");
        }

        scheduleMapper.deleteById(id);
    }

    @Override
    public ScheduleConflictCheckVO checkConflict(ScheduleConflictCheckRequest request) {
        Hall hall = hallMapper.selectById(request.getHallId());
        if (hall == null) {
            throw new BusinessException("影厅不存在");
        }

        LocalTime newShowTime = LocalTime.parse(request.getShowTime(), DateTimeFormatter.ofPattern("HH:mm"));
        int duration = request.getDuration() != null ? request.getDuration() : 120;
        int cleanDuration = request.getCleanDuration() != null ? request.getCleanDuration() : DEFAULT_CLEAN_DURATION;
        LocalTime newEndTime = newShowTime.plusMinutes(duration + cleanDuration);

        // 查询该影厅该日期的所有排片
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getHallId, request.getHallId())
                .eq(Schedule::getShowDate, request.getShowDate())
                .eq(Schedule::getStatus, 1);

        if (request.getExcludeId() != null) {
            wrapper.ne(Schedule::getId, request.getExcludeId());
        }

        List<Schedule> existingSchedules = scheduleMapper.selectList(wrapper);

        List<ScheduleConflictCheckVO.ConflictSchedule> conflictSchedules = new ArrayList<>();

        for (Schedule existing : existingSchedules) {
            LocalTime existShowTime = existing.getShowTime();
            LocalTime existEndTime = existing.getEndTime();

            // 检测时间重叠
            // 新场次的开始时间必须在已有场次的结束时间之后，或者新场次的结束时间必须在已有场次的开始时间之前
            // 如果不满足以上两个条件，则存在冲突
            if (!(newShowTime.isAfter(existEndTime) || newShowTime.equals(existEndTime)) &&
                    !(newEndTime.isBefore(existShowTime) || newEndTime.equals(existShowTime))) {
                // 存在冲突
                Movie movie = movieMapper.selectById(existing.getMovieId());

                ScheduleConflictCheckVO.ConflictSchedule conflict = new ScheduleConflictCheckVO.ConflictSchedule();
                conflict.setId(existing.getId());
                conflict.setMovieTitle(movie != null ? movie.getTitle() : "未知电影");
                conflict.setShowTime(existShowTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                conflict.setEndTime(existEndTime != null ? existEndTime.format(DateTimeFormatter.ofPattern("HH:mm")) : null);
                conflictSchedules.add(conflict);
            }
        }

        ScheduleConflictCheckVO vo = new ScheduleConflictCheckVO();
        vo.setHasConflict(!conflictSchedules.isEmpty());
        vo.setConflictSchedules(conflictSchedules);

        return vo;
    }

    /**
     * 转换为列表项VO
     */
    private ScheduleListItemVO convertToListItemVO(Schedule schedule) {
        ScheduleListItemVO vo = new ScheduleListItemVO();
        vo.setId(schedule.getId());
        vo.setMovieId(schedule.getMovieId());
        vo.setCinemaId(schedule.getCinemaId());
        vo.setHallId(schedule.getHallId());
        vo.setShowDate(schedule.getShowDate());
        vo.setShowTime(schedule.getShowTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        vo.setEndTime(schedule.getEndTime() != null ? schedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) : null);
        vo.setPrice(schedule.getPrice());
        vo.setTotalSeats(schedule.getTotalSeats());
        vo.setSoldSeats(schedule.getSoldSeats() != null ? schedule.getSoldSeats() : 0);
        vo.setRemainSeats(schedule.getTotalSeats() - (schedule.getSoldSeats() != null ? schedule.getSoldSeats() : 0));
        vo.setStatus(schedule.getStatus());

        Movie movie = movieMapper.selectById(schedule.getMovieId());
        if (movie != null) {
            vo.setMovieTitle(movie.getTitle());
            vo.setMoviePoster(movie.getPoster());
        }

        Cinema cinema = cinemaMapper.selectById(schedule.getCinemaId());
        if (cinema != null) {
            vo.setCinemaName(cinema.getName());
        }

        Hall hall = hallMapper.selectById(schedule.getHallId());
        if (hall != null) {
            vo.setHallName(hall.getName());
            vo.setHallType(hall.getType());
        }

        return vo;
    }
}

package com.maoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maoyan.common.ResultCode;
import com.maoyan.dto.*;
import com.maoyan.entity.*;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.*;
import com.maoyan.service.CinemaService;
import com.maoyan.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 影院服务实现类
 *
 * @author maoyan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CinemaServiceImpl extends ServiceImpl<CinemaMapper, Cinema> implements CinemaService {

    private final CinemaBrandMapper cinemaBrandMapper;
    private final HallMapper hallMapper;
    private final SeatMapper seatMapper;
    private final ScheduleMapper scheduleMapper;
    private final MovieMapper movieMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<CinemaListVO> getCinemaList(CinemaQueryDTO queryDTO) {
        Page<Cinema> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());

        LambdaQueryWrapper<Cinema> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cinema::getStatus, 1); // 只查询营业中的影院

        // 城市
        if (StringUtils.hasText(queryDTO.getCity())) {
            wrapper.eq(Cinema::getCity, queryDTO.getCity());
        }
        // 区域
        if (StringUtils.hasText(queryDTO.getDistrict())) {
            wrapper.eq(Cinema::getDistrict, queryDTO.getDistrict());
        }
        // 品牌
        if (queryDTO.getBrandId() != null) {
            wrapper.eq(Cinema::getBrandId, queryDTO.getBrandId());
        }
        // 关键词搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(Cinema::getName, queryDTO.getKeyword());
        }

        // 排序
        if ("score".equals(queryDTO.getOrderBy())) {
            wrapper.orderByDesc(Cinema::getScore);
        } else {
            wrapper.orderByAsc(Cinema::getId);
        }

        Page<Cinema> cinemaPage = baseMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<CinemaListVO> voPage = new Page<>(cinemaPage.getCurrent(), cinemaPage.getSize(), cinemaPage.getTotal());
        List<CinemaListVO> voList = cinemaPage.getRecords().stream()
                .map(cinema -> convertToListVO(cinema, queryDTO.getLongitude(), queryDTO.getLatitude()))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<CinemaListVO> getNearbyCinemas(NearbyCinemaQueryDTO queryDTO) {
        // 计算边界
        double radius = queryDTO.getRadius();
        double lng = queryDTO.getLongitude().doubleValue();
        double lat = queryDTO.getLatitude().doubleValue();

        // 简单的经纬度范围计算（适用于小范围）
        double latRange = radius / 111.0; // 1度纬度约111km
        double lngRange = radius / (111.0 * Math.cos(Math.toRadians(lat)));

        BigDecimal minLat = BigDecimal.valueOf(lat - latRange);
        BigDecimal maxLat = BigDecimal.valueOf(lat + latRange);
        BigDecimal minLng = BigDecimal.valueOf(lng - lngRange);
        BigDecimal maxLng = BigDecimal.valueOf(lng + lngRange);

        LambdaQueryWrapper<Cinema> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cinema::getStatus, 1)
                .ge(Cinema::getLatitude, minLat)
                .le(Cinema::getLatitude, maxLat)
                .ge(Cinema::getLongitude, minLng)
                .le(Cinema::getLongitude, maxLng);

        List<Cinema> cinemas = baseMapper.selectList(wrapper);

        // 计算实际距离并过滤
        return cinemas.stream()
                .map(cinema -> convertToListVO(cinema, queryDTO.getLongitude(), queryDTO.getLatitude()))
                .filter(vo -> vo.getDistance() != null && vo.getDistance().doubleValue() <= radius)
                .sorted(Comparator.comparing(CinemaListVO::getDistance))
                .limit(queryDTO.getLimit())
                .collect(Collectors.toList());
    }

    @Override
    public CinemaDetailVO getCinemaDetail(Long id) {
        Cinema cinema = baseMapper.selectById(id);
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        CinemaDetailVO vo = new CinemaDetailVO();
        vo.setId(cinema.getId());
        vo.setName(cinema.getName());
        vo.setBrandId(cinema.getBrandId());
        vo.setProvince(cinema.getProvince());
        vo.setCity(cinema.getCity());
        vo.setDistrict(cinema.getDistrict());
        vo.setAddress(cinema.getAddress());
        vo.setLongitude(cinema.getLongitude());
        vo.setLatitude(cinema.getLatitude());
        vo.setPhone(cinema.getPhone());
        vo.setFacilities(cinema.getFacilities());
        vo.setDescription(cinema.getDescription());
        vo.setScore(cinema.getScore());
        vo.setStatus(cinema.getStatus());

        // 品牌名称
        if (cinema.getBrandId() != null) {
            CinemaBrand brand = cinemaBrandMapper.selectById(cinema.getBrandId());
            if (brand != null) {
                vo.setBrandName(brand.getName());
            }
        }

        // 图片列表
        if (StringUtils.hasText(cinema.getImages())) {
            try {
                List<String> images = objectMapper.readValue(cinema.getImages(), new TypeReference<List<String>>() {});
                vo.setImages(images);
            } catch (JsonProcessingException e) {
                log.warn("解析影院图片失败: {}", e.getMessage());
            }
        }

        // TODO: 是否收藏（需要用户登录信息）
        vo.setIsFavorite(false);

        return vo;
    }

    @Override
    public List<HallListVO> getCinemaHalls(Long cinemaId) {
        // 检查影院是否存在
        Cinema cinema = baseMapper.selectById(cinemaId);
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        LambdaQueryWrapper<Hall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hall::getCinemaId, cinemaId)
                .eq(Hall::getStatus, 1)
                .orderByAsc(Hall::getId);

        List<Hall> halls = hallMapper.selectList(wrapper);

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
    public List<ScheduleDateVO> getScheduleDates(Long cinemaId, Long movieId) {
        // 检查影院是否存在
        Cinema cinema = baseMapper.selectById(cinemaId);
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(6); // 查询7天内的排片

        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getCinemaId, cinemaId)
                .eq(Schedule::getStatus, 1)
                .ge(Schedule::getShowDate, today)
                .le(Schedule::getShowDate, endDate);

        if (movieId != null) {
            wrapper.eq(Schedule::getMovieId, movieId);
        }

        wrapper.select(Schedule::getShowDate).groupBy(Schedule::getShowDate);

        List<Schedule> schedules = scheduleMapper.selectList(wrapper);

        return schedules.stream().map(schedule -> {
            ScheduleDateVO vo = new ScheduleDateVO();
            LocalDate date = schedule.getShowDate();
            vo.setDate(date.toString());
            vo.setWeekday(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.CHINESE));
            vo.setIsToday(date.equals(today));
            vo.setIsTomorrow(date.equals(today.plusDays(1)));
            return vo;
        }).sorted(Comparator.comparing(ScheduleDateVO::getDate)).collect(Collectors.toList());
    }

    @Override
    public List<CinemaScheduleVO> getCinemaSchedules(Long cinemaId, String date, Long movieId) {
        // 检查影院是否存在
        Cinema cinema = baseMapper.selectById(cinemaId);
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        LocalDate showDate = LocalDate.parse(date);

        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getCinemaId, cinemaId)
                .eq(Schedule::getShowDate, showDate)
                .eq(Schedule::getStatus, 1);

        if (movieId != null) {
            wrapper.eq(Schedule::getMovieId, movieId);
        }

        wrapper.orderByAsc(Schedule::getShowTime);

        List<Schedule> schedules = scheduleMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(schedules)) {
            return Collections.emptyList();
        }

        // 获取电影信息
        Set<Long> movieIds = schedules.stream().map(Schedule::getMovieId).collect(Collectors.toSet());
        Map<Long, Movie> movieMap = new HashMap<>();
        if (!movieIds.isEmpty()) {
            List<Movie> movies = movieMapper.selectBatchIds(movieIds);
            movieMap = movies.stream().collect(Collectors.toMap(Movie::getId, m -> m));
        }

        // 获取影厅信息
        Set<Long> hallIds = schedules.stream().map(Schedule::getHallId).collect(Collectors.toSet());
        Map<Long, Hall> hallMapTemp;
        if (!hallIds.isEmpty()) {
            List<Hall> halls = hallMapper.selectBatchIds(hallIds);
            hallMapTemp = halls.stream().collect(Collectors.toMap(Hall::getId, h -> h));
        } else {
            hallMapTemp = new HashMap<>();
        }
        final Map<Long, Hall> hallMap = hallMapTemp;

        // 按电影分组
        Map<Long, Movie> finalMovieMap = movieMap;
        Map<Long, List<Schedule>> groupedSchedules = schedules.stream()
                .collect(Collectors.groupingBy(Schedule::getMovieId));

        return groupedSchedules.entrySet().stream().map(entry -> {
            Long movieIdKey = entry.getKey();
            List<Schedule> scheduleList = entry.getValue();

            Movie movie = finalMovieMap.get(movieIdKey);
            CinemaScheduleVO vo = new CinemaScheduleVO();
            vo.setMovieId(movieIdKey);
            if (movie != null) {
                vo.setMovieTitle(movie.getTitle());
                vo.setMoviePoster(movie.getPoster());
                vo.setScore(movie.getScore());
                vo.setDuration(movie.getDuration());
            }

            List<ScheduleItemVO> scheduleItems = scheduleList.stream().map(schedule -> {
                ScheduleItemVO itemVO = new ScheduleItemVO();
                itemVO.setId(schedule.getId());
                itemVO.setHallId(schedule.getHallId());
                itemVO.setShowTime(schedule.getShowTime().toString());
                itemVO.setEndTime(schedule.getEndTime() != null ? schedule.getEndTime().toString() : null);
                itemVO.setPrice(schedule.getPrice());
                itemVO.setTotalSeats(schedule.getTotalSeats());
                itemVO.setSoldSeats(schedule.getSoldSeats());
                itemVO.setStatus(schedule.getStatus());

                Hall hall = hallMap.get(schedule.getHallId());
                if (hall != null) {
                    itemVO.setHallName(hall.getName());
                    itemVO.setHallType(hall.getType());
                }

                return itemVO;
            }).collect(Collectors.toList());

            vo.setSchedules(scheduleItems);
            return vo;
        }).sorted(Comparator.comparing(vo -> {
            if (!CollectionUtils.isEmpty(vo.getSchedules())) {
                return vo.getSchedules().get(0).getShowTime();
            }
            return "";
        })).collect(Collectors.toList());
    }

    // ========== 管理端方法 ==========

    @Override
    public Page<CinemaListVO> getAdminCinemaList(AdminCinemaQueryDTO queryDTO) {
        Page<Cinema> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());

        LambdaQueryWrapper<Cinema> wrapper = new LambdaQueryWrapper<>();

        // 城市
        if (StringUtils.hasText(queryDTO.getCity())) {
            wrapper.eq(Cinema::getCity, queryDTO.getCity());
        }
        // 品牌
        if (queryDTO.getBrandId() != null) {
            wrapper.eq(Cinema::getBrandId, queryDTO.getBrandId());
        }
        // 状态
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Cinema::getStatus, queryDTO.getStatus());
        }
        // 关键词搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(Cinema::getName, queryDTO.getKeyword());
        }

        wrapper.orderByDesc(Cinema::getId);

        Page<Cinema> cinemaPage = baseMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<CinemaListVO> voPage = new Page<>(cinemaPage.getCurrent(), cinemaPage.getSize(), cinemaPage.getTotal());
        List<CinemaListVO> voList = cinemaPage.getRecords().stream()
                .map(cinema -> convertToListVO(cinema, null, null))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCinema(CinemaCreateDTO createDTO) {
        Cinema cinema = new Cinema();
        cinema.setName(createDTO.getName());
        cinema.setBrandId(createDTO.getBrandId());
        cinema.setProvince(createDTO.getProvince());
        cinema.setCity(createDTO.getCity());
        cinema.setDistrict(createDTO.getDistrict());
        cinema.setAddress(createDTO.getAddress());
        cinema.setLongitude(createDTO.getLongitude());
        cinema.setLatitude(createDTO.getLatitude());
        cinema.setPhone(createDTO.getPhone());
        cinema.setFacilities(createDTO.getFacilities());
        cinema.setDescription(createDTO.getDescription());
        cinema.setStatus(1); // 默认营业
        cinema.setScore(BigDecimal.valueOf(4.8)); // 默认评分

        // 处理图片
        if (!CollectionUtils.isEmpty(createDTO.getImages())) {
            try {
                cinema.setImages(objectMapper.writeValueAsString(createDTO.getImages()));
            } catch (JsonProcessingException e) {
                log.warn("序列化影院图片失败: {}", e.getMessage());
            }
        }

        baseMapper.insert(cinema);
        return cinema.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCinema(Long id, CinemaCreateDTO createDTO) {
        Cinema cinema = baseMapper.selectById(id);
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        cinema.setName(createDTO.getName());
        cinema.setBrandId(createDTO.getBrandId());
        cinema.setProvince(createDTO.getProvince());
        cinema.setCity(createDTO.getCity());
        cinema.setDistrict(createDTO.getDistrict());
        cinema.setAddress(createDTO.getAddress());
        cinema.setLongitude(createDTO.getLongitude());
        cinema.setLatitude(createDTO.getLatitude());
        cinema.setPhone(createDTO.getPhone());
        cinema.setFacilities(createDTO.getFacilities());
        cinema.setDescription(createDTO.getDescription());

        // 处理图片
        if (!CollectionUtils.isEmpty(createDTO.getImages())) {
            try {
                cinema.setImages(objectMapper.writeValueAsString(createDTO.getImages()));
            } catch (JsonProcessingException e) {
                log.warn("序列化影院图片失败: {}", e.getMessage());
            }
        } else {
            cinema.setImages(null);
        }

        baseMapper.updateById(cinema);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCinema(Long id) {
        Cinema cinema = baseMapper.selectById(id);
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        // 检查是否有影厅
        LambdaQueryWrapper<Hall> hallWrapper = new LambdaQueryWrapper<>();
        hallWrapper.eq(Hall::getCinemaId, id);
        long hallCount = hallMapper.selectCount(hallWrapper);
        if (hallCount > 0) {
            throw new BusinessException("该影院下存在影厅，无法删除");
        }

        baseMapper.deleteById(id);
    }

    @Override
    public Cinema getById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 转换为列表VO
     */
    private CinemaListVO convertToListVO(Cinema cinema, BigDecimal userLng, BigDecimal userLat) {
        CinemaListVO vo = new CinemaListVO();
        vo.setId(cinema.getId());
        vo.setName(cinema.getName());
        vo.setBrandId(cinema.getBrandId());
        vo.setAddress(cinema.getAddress());
        vo.setPhone(cinema.getPhone());
        vo.setFacilities(cinema.getFacilities());
        vo.setScore(cinema.getScore());
        vo.setStatus(cinema.getStatus());

        // 品牌名称
        if (cinema.getBrandId() != null) {
            CinemaBrand brand = cinemaBrandMapper.selectById(cinema.getBrandId());
            if (brand != null) {
                vo.setBrandName(brand.getName());
            }
        }

        // 计算距离
        if (userLng != null && userLat != null && cinema.getLongitude() != null && cinema.getLatitude() != null) {
            double distance = calculateDistance(
                    userLat.doubleValue(), userLng.doubleValue(),
                    cinema.getLatitude().doubleValue(), cinema.getLongitude().doubleValue()
            );
            vo.setDistance(BigDecimal.valueOf(distance).setScale(1, RoundingMode.HALF_UP));
        }

        return vo;
    }

    /**
     * 计算两点间距离（Haversine公式）
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // 地球半径，单位km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

}

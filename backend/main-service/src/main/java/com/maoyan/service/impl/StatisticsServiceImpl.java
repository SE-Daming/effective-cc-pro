package com.maoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.maoyan.dto.statistics.StatisticsQueryDTO;
import com.maoyan.entity.Cinema;
import com.maoyan.entity.Movie;
import com.maoyan.entity.Order;
import com.maoyan.entity.OrderTicket;
import com.maoyan.entity.Schedule;
import com.maoyan.mapper.*;
import com.maoyan.service.StatisticsService;
import com.maoyan.vo.statistics.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现
 *
 * @author maoyan
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderMapper orderMapper;
    private final OrderTicketMapper orderTicketMapper;
    private final MovieMapper movieMapper;
    private final CinemaMapper cinemaMapper;
    private final ScheduleMapper scheduleMapper;
    private final UserMapper userMapper;

    @Override
    public StatisticsOverviewVO getOverview(StatisticsQueryDTO queryDTO) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        if (queryDTO.getStartDate() != null) {
            startTime = queryDTO.getStartDate().atStartOfDay();
        }
        if (queryDTO.getEndDate() != null) {
            endTime = queryDTO.getEndDate().atTime(LocalTime.MAX);
        }

        // 构建查询条件
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Order::getStatus, 2, 3, 4); // 已支付、已出票、已完成
        if (startTime != null) {
            wrapper.ge(Order::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(Order::getCreateTime, endTime);
        }

        List<Order> orders = orderMapper.selectList(wrapper);

        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        vo.setTotalOrders((long) orders.size());

        BigDecimal totalAmount = orders.stream()
                .map(Order::getPayAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalAmount(totalAmount);

        // 统计票数
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        long totalTickets = 0;
        if (!orderIds.isEmpty()) {
            LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
            ticketWrapper.in(OrderTicket::getOrderId, orderIds);
            totalTickets = orderTicketMapper.selectCount(ticketWrapper);
        }
        vo.setTotalTickets(totalTickets);

        // 统计购票用户数
        long totalUsers = orders.stream()
                .map(Order::getUserId)
                .distinct()
                .count();
        vo.setTotalUsers(totalUsers);

        // 新增用户数
        LambdaQueryWrapper<com.maoyan.entity.User> userWrapper = new LambdaQueryWrapper<>();
        if (startTime != null) {
            userWrapper.ge(com.maoyan.entity.User::getCreateTime, startTime);
        }
        if (endTime != null) {
            userWrapper.le(com.maoyan.entity.User::getCreateTime, endTime);
        }
        vo.setNewUsers(userMapper.selectCount(userWrapper));

        // 退款统计
        LambdaQueryWrapper<Order> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.in(Order::getStatus, 6, 7); // 退款中、已退款
        if (startTime != null) {
            refundWrapper.ge(Order::getCreateTime, startTime);
        }
        if (endTime != null) {
            refundWrapper.le(Order::getCreateTime, endTime);
        }
        List<Order> refundOrders = orderMapper.selectList(refundWrapper);
        vo.setRefundCount((long) refundOrders.size());

        BigDecimal refundAmount = refundOrders.stream()
                .map(Order::getRefundAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setRefundAmount(refundAmount);

        return vo;
    }

    @Override
    public List<SalesTrendVO> getSalesTrend(StatisticsQueryDTO queryDTO) {
        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<SalesTrendVO> result = new ArrayList<>();

        if ("month".equals(queryDTO.getType())) {
            // 按月统计
            result = getMonthlyTrend(startDate, endDate);
        } else {
            // 按日统计
            result = getDailyTrend(startDate, endDate);
        }

        return result;
    }

    @Override
    public List<MovieRankingVO> getMovieRanking(StatisticsQueryDTO queryDTO) {
        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        // 查询已支付订单
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(Order::getStatus, 2, 3, 4)
                .ge(Order::getCreateTime, startTime)
                .le(Order::getCreateTime, endTime)
                .eq(Order::getType, 1); // 电影票订单
        List<Order> orders = orderMapper.selectList(orderWrapper);

        if (orders.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取订单项统计
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.in(OrderTicket::getOrderId, orderIds);
        List<OrderTicket> tickets = orderTicketMapper.selectList(ticketWrapper);

        // 按电影分组统计
        Map<Long, BigDecimal> movieBoxOffice = new HashMap<>();
        Map<Long, Long> movieTicketCount = new HashMap<>();

        // 统计票房和票数
        // 先获取场次信息，通过场次获取电影ID
        Map<Long, Schedule> scheduleMap = new HashMap<>();
        if (!tickets.isEmpty()) {
            List<Long> scheduleIds = tickets.stream()
                    .map(OrderTicket::getScheduleId)
                    .distinct()
                    .collect(Collectors.toList());
            List<Schedule> schedules = scheduleMapper.selectBatchIds(scheduleIds);
            scheduleMap = schedules.stream()
                    .collect(Collectors.toMap(Schedule::getId, s -> s));
        }

        for (OrderTicket ticket : tickets) {
            Schedule schedule = scheduleMap.get(ticket.getScheduleId());
            if (schedule == null) continue;
            Long movieId = schedule.getMovieId();
            BigDecimal ticketPrice = ticket.getTicketPrice();
            movieBoxOffice.merge(movieId, ticketPrice != null ? ticketPrice : BigDecimal.ZERO, BigDecimal::add);
            movieTicketCount.merge(movieId, 1L, Long::sum);
        }

        // 获取电影信息
        List<Movie> movies = movieMapper.selectBatchIds(movieBoxOffice.keySet());
        Map<Long, Movie> movieMap = movies.stream()
                .collect(Collectors.toMap(Movie::getId, m -> m));

        // 构建排行列表
        List<MovieRankingVO> rankingList = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<Long, BigDecimal> entry : movieBoxOffice.entrySet()) {
            Long movieId = entry.getKey();
            Movie movie = movieMap.get(movieId);
            if (movie == null) continue;

            MovieRankingVO vo = new MovieRankingVO();
            vo.setRank(rank++);
            vo.setMovieId(movieId);
            vo.setMovieTitle(movie.getTitle());
            vo.setMoviePoster(movie.getPoster());
            vo.setBoxOffice(entry.getValue());
            vo.setTicketCount(movieTicketCount.getOrDefault(movieId, 0L));

            rankingList.add(vo);

            if (rankingList.size() >= queryDTO.getLimit()) {
                break;
            }
        }

        // 按票房排序
        rankingList.sort((a, b) -> b.getBoxOffice().compareTo(a.getBoxOffice()));
        for (int i = 0; i < rankingList.size(); i++) {
            rankingList.get(i).setRank(i + 1);
        }

        return rankingList;
    }

    @Override
    public List<CinemaRankingVO> getCinemaRanking(StatisticsQueryDTO queryDTO) {
        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        // 查询已支付订单
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(Order::getStatus, 2, 3, 4)
                .ge(Order::getCreateTime, startTime)
                .le(Order::getCreateTime, endTime);
        List<Order> orders = orderMapper.selectList(orderWrapper);

        if (orders.isEmpty()) {
            return new ArrayList<>();
        }

        // 按影院分组统计
        Map<Long, BigDecimal> cinemaSales = new HashMap<>();
        Map<Long, Long> cinemaOrderCount = new HashMap<>();

        for (Order order : orders) {
            Long cinemaId = order.getCinemaId();
            if (cinemaId == null) continue;
            cinemaSales.merge(cinemaId, order.getPayAmount(), BigDecimal::add);
            cinemaOrderCount.merge(cinemaId, 1L, Long::sum);
        }

        // 统计票数
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.in(OrderTicket::getOrderId, orderIds);
        List<OrderTicket> tickets = orderTicketMapper.selectList(ticketWrapper);

        Map<Long, Long> cinemaTicketCount = tickets.stream()
                .collect(Collectors.groupingBy(t -> {
                    Order order = orders.stream()
                            .filter(o -> o.getId().equals(t.getOrderId()))
                            .findFirst()
                            .orElse(null);
                    return order != null ? order.getCinemaId() : null;
                }))
                .entrySet().stream()
                .filter(e -> e.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));

        // 获取影院信息
        List<Cinema> cinemas = cinemaMapper.selectBatchIds(cinemaSales.keySet());
        Map<Long, Cinema> cinemaMap = cinemas.stream()
                .collect(Collectors.toMap(Cinema::getId, c -> c));

        // 构建排行列表
        List<CinemaRankingVO> rankingList = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : cinemaSales.entrySet()) {
            Long cinemaId = entry.getKey();
            Cinema cinema = cinemaMap.get(cinemaId);
            if (cinema == null) continue;

            CinemaRankingVO vo = new CinemaRankingVO();
            vo.setCinemaId(cinemaId);
            vo.setCinemaName(cinema.getName());
            vo.setCinemaAddress(cinema.getAddress());
            vo.setSalesAmount(entry.getValue());
            vo.setOrderCount(cinemaOrderCount.getOrDefault(cinemaId, 0L));
            vo.setTicketCount(cinemaTicketCount.getOrDefault(cinemaId, 0L));

            rankingList.add(vo);
        }

        // 按销售额排序
        rankingList.sort((a, b) -> b.getSalesAmount().compareTo(a.getSalesAmount()));
        for (int i = 0; i < rankingList.size(); i++) {
            rankingList.get(i).setRank(i + 1);
        }

        // 限制数量
        if (rankingList.size() > queryDTO.getLimit()) {
            rankingList = rankingList.subList(0, queryDTO.getLimit());
        }

        return rankingList;
    }

    // ===== 私有方法 =====

    private List<SalesTrendVO> getDailyTrend(LocalDate startDate, LocalDate endDate) {
        List<SalesTrendVO> result = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.atTime(LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Order::getStatus, 2, 3, 4)
                    .ge(Order::getCreateTime, dayStart)
                    .le(Order::getCreateTime, dayEnd);
            List<Order> orders = orderMapper.selectList(wrapper);

            SalesTrendVO vo = new SalesTrendVO();
            vo.setDate(current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            vo.setOrderCount((long) orders.size());

            BigDecimal amount = orders.stream()
                    .map(Order::getPayAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setAmount(amount);

            // 统计票数
            List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
            if (!orderIds.isEmpty()) {
                LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
                ticketWrapper.in(OrderTicket::getOrderId, orderIds);
                vo.setTicketCount(orderTicketMapper.selectCount(ticketWrapper));
            } else {
                vo.setTicketCount(0L);
            }

            result.add(vo);
            current = current.plusDays(1);
        }

        return result;
    }

    private List<SalesTrendVO> getMonthlyTrend(LocalDate startDate, LocalDate endDate) {
        List<SalesTrendVO> result = new ArrayList<>();

        // 调整到月初
        LocalDate current = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);

        while (!current.isAfter(endMonth)) {
            LocalDateTime monthStart = current.atStartOfDay();
            LocalDateTime monthEnd = current.plusMonths(1).minusDays(1).atTime(LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Order::getStatus, 2, 3, 4)
                    .ge(Order::getCreateTime, monthStart)
                    .le(Order::getCreateTime, monthEnd);
            List<Order> orders = orderMapper.selectList(wrapper);

            SalesTrendVO vo = new SalesTrendVO();
            vo.setDate(current.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            vo.setOrderCount((long) orders.size());

            BigDecimal amount = orders.stream()
                    .map(Order::getPayAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setAmount(amount);

            // 统计票数
            List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
            if (!orderIds.isEmpty()) {
                LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
                ticketWrapper.in(OrderTicket::getOrderId, orderIds);
                vo.setTicketCount(orderTicketMapper.selectCount(ticketWrapper));
            } else {
                vo.setTicketCount(0L);
            }

            result.add(vo);
            current = current.plusMonths(1);
        }

        return result;
    }
}

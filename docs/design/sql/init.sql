-- ============================================
-- 猫眼电影票务平台 - 数据库初始化脚本
-- 数据库名：maoyan-may2
-- 表数量：24
-- 创建时间：2025-05-02
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `maoyan-may2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `maoyan-may2`;

-- ============================================
-- 1. 用户模块
-- ============================================

-- 用户表
CREATE TABLE `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `openid` varchar(64) NOT NULL COMMENT '微信openid',
    `union_id` varchar(64) DEFAULT NULL COMMENT '微信union_id',
    `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `gender` tinyint DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `total_consumption` decimal(10,2) DEFAULT 0.00 COMMENT '累计消费金额',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `status` tinyint DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 搜索历史表
CREATE TABLE `search_history` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `keyword` varchar(64) NOT NULL COMMENT '搜索关键词',
    `type` tinyint DEFAULT 1 COMMENT '类型：1电影 2影院',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- ============================================
-- 2. 电影模块
-- ============================================

-- 电影表
CREATE TABLE `movie` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` varchar(128) NOT NULL COMMENT '电影名称',
    `english_title` varchar(128) DEFAULT NULL COMMENT '英文名称',
    `poster` varchar(255) NOT NULL COMMENT '海报URL',
    `director` varchar(64) DEFAULT NULL COMMENT '导演',
    `actors` varchar(500) DEFAULT NULL COMMENT '主演（逗号分隔）',
    `duration` int DEFAULT NULL COMMENT '时长（分钟）',
    `category_ids` varchar(64) DEFAULT NULL COMMENT '分类ID（逗号分隔）',
    `region` varchar(32) DEFAULT NULL COMMENT '地区',
    `language` varchar(32) DEFAULT NULL COMMENT '语言',
    `release_date` date DEFAULT NULL COMMENT '上映日期',
    `off_date` date DEFAULT NULL COMMENT '下映日期',
    `synopsis` text COMMENT '剧情简介',
    `trailer_url` varchar(255) DEFAULT NULL COMMENT '预告片URL',
    `score` decimal(2,1) DEFAULT NULL COMMENT '评分（0-10）',
    `score_count` int DEFAULT 0 COMMENT '评分人数',
    `status` tinyint DEFAULT 1 COMMENT '状态：1即将上映 2上映中 3下架',
    `sort` int DEFAULT 0 COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_release_date` (`release_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影表';

-- 电影分类表
CREATE TABLE `movie_category` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(32) NOT NULL COMMENT '分类名称',
    `sort` int DEFAULT 0 COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影分类表';

-- 电影演员表
CREATE TABLE `movie_actor` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `movie_id` bigint NOT NULL COMMENT '电影ID',
    `name` varchar(64) NOT NULL COMMENT '演员姓名',
    `role` varchar(32) DEFAULT NULL COMMENT '角色：主演/配角/导演等',
    `sort` int DEFAULT 0 COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_movie_id` (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电影演员表';

-- 影评表
CREATE TABLE `movie_review` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `movie_id` bigint NOT NULL COMMENT '电影ID',
    `score` tinyint NOT NULL COMMENT '评分（1-10）',
    `content` text COMMENT '影评内容',
    `like_count` int DEFAULT 0 COMMENT '点赞数',
    `status` tinyint DEFAULT 1 COMMENT '状态：0隐藏 1显示',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_movie_id` (`movie_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影评表';

-- ============================================
-- 3. 影院模块
-- ============================================

-- 影院品牌表
CREATE TABLE `cinema_brand` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(64) NOT NULL COMMENT '品牌名称',
    `logo` varchar(255) DEFAULT NULL COMMENT '品牌Logo',
    `description` varchar(255) DEFAULT NULL COMMENT '品牌介绍',
    `sort` int DEFAULT 0 COMMENT '排序',
    `status` tinyint DEFAULT 1 COMMENT '状态',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影院品牌表';

-- 影院表
CREATE TABLE `cinema` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(128) NOT NULL COMMENT '影院名称',
    `brand_id` int DEFAULT NULL COMMENT '品牌ID',
    `province` varchar(32) DEFAULT NULL COMMENT '省',
    `city` varchar(32) DEFAULT NULL COMMENT '市',
    `district` varchar(32) DEFAULT NULL COMMENT '区',
    `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
    `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
    `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
    `phone` varchar(20) DEFAULT NULL COMMENT '电话',
    `facilities` varchar(255) DEFAULT NULL COMMENT '设施标签（逗号分隔）',
    `description` text COMMENT '影院介绍',
    `images` text COMMENT '图片URL（JSON数组）',
    `score` decimal(2,1) DEFAULT NULL COMMENT '评分',
    `status` tinyint DEFAULT 1 COMMENT '状态：0关闭 1营业',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_city` (`city`),
    KEY `idx_location` (`longitude`, `latitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影院表';

-- 影厅表
CREATE TABLE `hall` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cinema_id` bigint NOT NULL COMMENT '影院ID',
    `name` varchar(32) NOT NULL COMMENT '影厅名称',
    `type` varchar(32) DEFAULT NULL COMMENT '类型：普通/IMAX/Dolby等',
    `rows` int NOT NULL COMMENT '行数',
    `cols` int NOT NULL COMMENT '列数',
    `total_seats` int NOT NULL COMMENT '总座位数',
    `seat_layout` text COMMENT '座位布局JSON',
    `status` tinyint DEFAULT 1 COMMENT '状态：0关闭 1正常',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_cinema_id` (`cinema_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影厅表';

-- 座位表
CREATE TABLE `seat` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `hall_id` bigint NOT NULL COMMENT '影厅ID',
    `row_num` int NOT NULL COMMENT '行号',
    `col_num` int NOT NULL COMMENT '列号',
    `seat_no` varchar(10) NOT NULL COMMENT '座位号（如：F08）',
    `seat_type` tinyint DEFAULT 1 COMMENT '类型：1普通 2情侣 3VIP',
    `status` tinyint DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_hall_seat` (`hall_id`, `row_num`, `col_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

-- ============================================
-- 4. 排片模块
-- ============================================

-- 排片表
CREATE TABLE `schedule` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `movie_id` bigint NOT NULL COMMENT '电影ID',
    `cinema_id` bigint NOT NULL COMMENT '影院ID',
    `hall_id` bigint NOT NULL COMMENT '影厅ID',
    `show_date` date NOT NULL COMMENT '放映日期',
    `show_time` time NOT NULL COMMENT '放映时间',
    `end_time` time DEFAULT NULL COMMENT '结束时间',
    `clean_duration` int DEFAULT 15 COMMENT '清洁时间（分钟）',
    `price` decimal(6,2) NOT NULL COMMENT '票价',
    `total_seats` int NOT NULL COMMENT '总座位数',
    `sold_seats` int DEFAULT 0 COMMENT '已售座位数',
    `status` tinyint DEFAULT 1 COMMENT '状态：0取消 1正常',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_movie_cinema` (`movie_id`, `cinema_id`),
    KEY `idx_show_date` (`show_date`),
    KEY `idx_cinema_date` (`cinema_id`, `show_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排片表';

-- ============================================
-- 5. 订单模块
-- ============================================

-- 订单表
CREATE TABLE `order` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no` varchar(32) NOT NULL COMMENT '订单编号',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `type` tinyint NOT NULL COMMENT '类型：1电影票 2卖品 3组合',
    `cinema_id` bigint DEFAULT NULL COMMENT '影院ID',
    `total_amount` decimal(10,2) NOT NULL COMMENT '订单总额',
    `discount_amount` decimal(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
    `user_coupon_id` bigint DEFAULT NULL COMMENT '使用的用户优惠券ID',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1待支付 2已支付 3已出票 4已完成 5已取消 6退款中 7已退款',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式',
    `pay_trade_no` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
    `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
    `refund_fee` decimal(10,2) DEFAULT NULL COMMENT '退票手续费',
    `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
    `refund_reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
    `refund_auditor_id` int DEFAULT NULL COMMENT '退款审核人ID',
    `refund_audit_time` datetime DEFAULT NULL COMMENT '退款审核时间',
    `refund_audit_remark` varchar(255) DEFAULT NULL COMMENT '退款审核备注',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单票务明细表
CREATE TABLE `order_ticket` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `schedule_id` bigint NOT NULL COMMENT '场次ID',
    `seat_id` bigint NOT NULL COMMENT '座位ID',
    `seat_no` varchar(10) NOT NULL COMMENT '座位号',
    `ticket_price` decimal(6,2) NOT NULL COMMENT '票价',
    `pick_code` varchar(20) DEFAULT NULL COMMENT '取票码',
    `pick_qrcode` varchar(255) DEFAULT NULL COMMENT '取票二维码',
    `status` tinyint DEFAULT 1 COMMENT '状态：1未取票 2已取票',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_schedule_seat` (`schedule_id`, `seat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单票务明细表';

-- 订单卖品明细表
CREATE TABLE `order_snack` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `snack_id` bigint NOT NULL COMMENT '卖品ID',
    `snack_name` varchar(64) NOT NULL COMMENT '卖品名称',
    `spec` varchar(32) DEFAULT NULL COMMENT '规格',
    `quantity` int NOT NULL COMMENT '数量',
    `price` decimal(6,2) NOT NULL COMMENT '单价',
    `total_price` decimal(6,2) NOT NULL COMMENT '小计',
    `pick_code` varchar(20) DEFAULT NULL COMMENT '取货码',
    `status` tinyint DEFAULT 1 COMMENT '状态：1未取货 2已取货',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单卖品明细表';

-- 座位锁定表
CREATE TABLE `seat_lock` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `schedule_id` bigint NOT NULL COMMENT '场次ID',
    `seat_id` bigint NOT NULL COMMENT '座位ID',
    `seat_no` varchar(10) NOT NULL COMMENT '座位号',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `order_id` bigint DEFAULT NULL COMMENT '订单ID',
    `status` tinyint DEFAULT 1 COMMENT '状态：1锁定 2已售 3释放',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_schedule_seat` (`schedule_id`, `seat_id`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位锁定表';

-- ============================================
-- 6. 优惠券模块
-- ============================================

-- 优惠券模板表
CREATE TABLE `coupon` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(64) NOT NULL COMMENT '优惠券名称',
    `type` tinyint NOT NULL COMMENT '类型：1满减 2折扣 3立减 4兑换',
    `value` decimal(6,2) DEFAULT NULL COMMENT '优惠值（金额/折扣）',
    `min_amount` decimal(6,2) DEFAULT 0.00 COMMENT '最低消费金额',
    `total_count` int DEFAULT 0 COMMENT '发放总量',
    `used_count` int DEFAULT 0 COMMENT '已使用数量',
    `receive_count` int DEFAULT 0 COMMENT '已领取数量',
    `limit_per_user` int DEFAULT 1 COMMENT '每人限领数量',
    `valid_days` int DEFAULT NULL COMMENT '有效天数',
    `valid_start` date DEFAULT NULL COMMENT '有效期开始',
    `valid_end` date DEFAULT NULL COMMENT '有效期结束',
    `scope` tinyint DEFAULT 1 COMMENT '适用范围：1全部 2指定电影 3指定影院',
    `scope_ids` varchar(255) DEFAULT NULL COMMENT '适用ID列表（逗号分隔）',
    `status` tinyint DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- 用户优惠券表
CREATE TABLE `user_coupon` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
    `coupon_name` varchar(64) NOT NULL COMMENT '优惠券名称（冗余）',
    `type` tinyint NOT NULL COMMENT '类型',
    `value` decimal(6,2) DEFAULT NULL COMMENT '优惠值',
    `min_amount` decimal(6,2) DEFAULT 0.00 COMMENT '最低消费',
    `valid_start` datetime NOT NULL COMMENT '有效期开始',
    `valid_end` datetime NOT NULL COMMENT '有效期结束',
    `status` tinyint DEFAULT 1 COMMENT '状态：1未使用 2已使用 3已过期',
    `use_time` datetime DEFAULT NULL COMMENT '使用时间',
    `order_id` bigint DEFAULT NULL COMMENT '使用的订单ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_valid_end` (`valid_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ============================================
-- 7. 卖品模块
-- ============================================

-- 卖品分类表
CREATE TABLE `snack_category` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(32) NOT NULL COMMENT '分类名称',
    `icon` varchar(255) DEFAULT NULL COMMENT '图标',
    `sort` int DEFAULT 0 COMMENT '排序',
    `status` tinyint DEFAULT 1 COMMENT '状态',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卖品分类表';

-- 卖品表
CREATE TABLE `snack` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` int NOT NULL COMMENT '分类ID',
    `name` varchar(64) NOT NULL COMMENT '卖品名称',
    `image` varchar(255) DEFAULT NULL COMMENT '图片',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `specs` text COMMENT '规格JSON',
    `price` decimal(6,2) NOT NULL COMMENT '价格',
    `stock` int DEFAULT 0 COMMENT '库存',
    `sales` int DEFAULT 0 COMMENT '销量',
    `status` tinyint DEFAULT 1 COMMENT '状态：0下架 1上架',
    `sort` int DEFAULT 0 COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卖品表';

-- ============================================
-- 8. 收藏模块
-- ============================================

-- 用户收藏表
CREATE TABLE `user_favorite` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `target_type` tinyint NOT NULL COMMENT '类型：1电影 2影院',
    `target_id` bigint NOT NULL COMMENT '目标ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ============================================
-- 9. 管理员模块
-- ============================================

-- 管理员表
CREATE TABLE `admin` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` varchar(32) NOT NULL COMMENT '用户名',
    `password` varchar(64) NOT NULL COMMENT '密码（加密）',
    `real_name` varchar(32) DEFAULT NULL COMMENT '真实姓名',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
    `role_id` int DEFAULT NULL COMMENT '角色ID',
    `status` tinyint DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 角色表
CREATE TABLE `admin_role` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(32) NOT NULL COMMENT '角色名称',
    `code` varchar(32) NOT NULL COMMENT '角色编码',
    `permissions` text COMMENT '权限列表JSON',
    `status` tinyint DEFAULT 1 COMMENT '状态',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 操作日志表
CREATE TABLE `admin_operation_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `admin_id` int NOT NULL COMMENT '管理员ID',
    `admin_name` varchar(32) DEFAULT NULL COMMENT '管理员姓名',
    `module` varchar(32) DEFAULT NULL COMMENT '模块',
    `action` varchar(32) DEFAULT NULL COMMENT '操作',
    `target` varchar(64) DEFAULT NULL COMMENT '操作对象',
    `content` text COMMENT '操作内容',
    `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================
-- 10. 其他
-- ============================================

-- 轮播图表
CREATE TABLE `banner` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` varchar(64) DEFAULT NULL COMMENT '标题',
    `image` varchar(255) NOT NULL COMMENT '图片URL',
    `link_type` tinyint DEFAULT NULL COMMENT '链接类型：1电影 2活动 3外链',
    `link_id` bigint DEFAULT NULL COMMENT '关联ID',
    `link_url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
    `position` tinyint DEFAULT 1 COMMENT '位置：1首页',
    `sort` int DEFAULT 0 COMMENT '排序',
    `status` tinyint DEFAULT 1 COMMENT '状态',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ============================================
-- 初始化数据
-- ============================================

-- 初始化电影分类
INSERT INTO `movie_category` (`name`, `sort`) VALUES
('动作', 1),
('喜剧', 2),
('爱情', 3),
('科幻', 4),
('悬疑', 5),
('恐怖', 6),
('动画', 7),
('纪录片', 8),
('战争', 9),
('犯罪', 10);

-- 初始化影院品牌
INSERT INTO `cinema_brand` (`name`, `sort`) VALUES
('万达影城', 1),
('CGV影城', 2),
('大地影院', 3),
('金逸影城', 4),
('博纳影城', 5);

-- 初始化卖品分类
INSERT INTO `snack_category` (`name`, `sort`) VALUES
('爆米花', 1),
('饮料', 2),
('套餐', 3),
('小食', 4);

-- 初始化管理员角色
INSERT INTO `admin_role` (`name`, `code`, `permissions`) VALUES
('超级管理员', 'super_admin', '{"all": true}'),
('运营管理员', 'operator', '{"movie": true, "cinema": true, "schedule": true}'),
('客服', 'service', '{"order": true, "refund": true}');

-- 初始化管理员（密码：123456，实际使用时需加密）
INSERT INTO `admin` (`username`, `password`, `real_name`, `role_id`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '超级管理员', 1);

-- ============================================
-- 完成
-- ============================================

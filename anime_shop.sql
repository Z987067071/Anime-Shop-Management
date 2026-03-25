/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80019
Source Host           : localhost:3306
Source Database       : anime_shop

Target Server Type    : MYSQL
Target Server Version : 80019
File Encoding         : 65001

Date: 2026-03-25 16:36:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for buyer_info
-- ----------------------------
DROP TABLE IF EXISTS `buyer_info`;
CREATE TABLE `buyer_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID（关联user表id）',
  `name` varchar(50) NOT NULL COMMENT '购票人真实姓名',
  `id_card` varchar(18) NOT NULL COMMENT '购票人身份证号',
  `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态：0-待审核 1-审核通过 2-审核驳回',
  `audit_remark` varchar(200) DEFAULT NULL COMMENT '审核驳回理由（审核通过时为空）',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_id_card` (`id_card`) COMMENT '身份证号唯一',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_audit_status` (`audit_status`) COMMENT '审核状态索引'
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购票人身份信息表';

-- ----------------------------
-- Table structure for b_banner
-- ----------------------------
DROP TABLE IF EXISTS `b_banner`;
CREATE TABLE `b_banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图id',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `img_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图url',
  `link_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '跳转链接',
  `sort` int DEFAULT '0' COMMENT '排序值 小靠前',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 1显示 0隐藏',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for comic_con
-- ----------------------------
DROP TABLE IF EXISTS `comic_con`;
CREATE TABLE `comic_con` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '漫展ID（主键）',
  `product_id` bigint NOT NULL COMMENT '关联商品表ID（p_product.id），漫展作为虚拟商品上架',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '漫展名称',
  `start_time` datetime NOT NULL COMMENT '漫展开始时间',
  `end_time` datetime NOT NULL COMMENT '漫展结束时间',
  `venue` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '漫展场馆地址',
  `description` text COMMENT '漫展简介（富文本，可选）',
  `banner_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '漫展封面图',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '漫展状态：1=可售，0=停售，2=已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`) COMMENT '一个漫展对应一个虚拟商品',
  KEY `idx_status` (`status`) COMMENT '按状态筛选漫展',
  CONSTRAINT `fk_comic_con_product` FOREIGN KEY (`product_id`) REFERENCES `p_product` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漫展主表';

-- ----------------------------
-- Table structure for comic_con_ticket
-- ----------------------------
DROP TABLE IF EXISTS `comic_con_ticket`;
CREATE TABLE `comic_con_ticket` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '票种ID（主键）',
  `product_id` bigint NOT NULL COMMENT '关联商品ID p_product.id',
  `comic_con_id` bigint NOT NULL COMMENT '关联漫展ID（comic_con.id）',
  `ticket_name` varchar(100) NOT NULL COMMENT '票种名称（如：周六VIP票/周日普通票/夜场票）',
  `price` decimal(10,2) NOT NULL COMMENT '票种售价',
  `sku_id` bigint DEFAULT NULL COMMENT '关联product_sku.id',
  `stock` int NOT NULL DEFAULT '0' COMMENT '该票种剩余库存（票数）',
  `total_stock` int NOT NULL DEFAULT '0' COMMENT '该票种总库存（初始票数，统计用）',
  `need_real_name` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否需要实名：1=是，0=否（默认实名）',
  `max_buy` int NOT NULL DEFAULT '5' COMMENT '单人限购张数（如5张）',
  `sale_start` datetime NOT NULL COMMENT '该票种开售时间',
  `sale_end` datetime NOT NULL COMMENT '该票种停售时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '票种状态：1=正常销售，0=下架，2=售罄',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_comic_con_id` (`comic_con_id`) COMMENT '按漫展查票种',
  KEY `idx_status` (`status`) COMMENT '按状态筛选票种',
  CONSTRAINT `fk_ticket_comic_con` FOREIGN KEY (`comic_con_id`) REFERENCES `comic_con` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漫展票种/场次表';

-- ----------------------------
-- Table structure for f_feedback
-- ----------------------------
DROP TABLE IF EXISTS `f_feedback`;
CREATE TABLE `f_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单ID（主键）',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联u_user.id）',
  `feedback_content` text NOT NULL COMMENT '反馈内容（用户输入的文字）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '工单状态：0=待审核，1=审核中，2=已解决，3=已驳回，4=已关闭',
  `reply_content` text COMMENT '后台回复内容（审核后填写）',
  `creator` varchar(32) DEFAULT '' COMMENT '创建人（冗余：用户昵称）',
  `auditor` varchar(32) DEFAULT '' COMMENT '审核人（后台管理员昵称）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0=未删，1=已删',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '按用户ID查询工单',
  KEY `idx_status` (`status`) COMMENT '按工单状态筛选',
  KEY `idx_create_time` (`create_time`) COMMENT '按创建时间排序'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='移动端工单反馈主表';

-- ----------------------------
-- Table structure for f_feedback_image
-- ----------------------------
DROP TABLE IF EXISTS `f_feedback_image`;
CREATE TABLE `f_feedback_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID（主键）',
  `feedback_id` bigint NOT NULL COMMENT '关联工单ID（f_feedback.id）',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL（存储OSS/服务器路径）',
  `sort` int NOT NULL DEFAULT '1' COMMENT '图片排序（值越大越靠前）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_feedback_id` (`feedback_id`) COMMENT '按工单ID查询图片',
  KEY `idx_image_url` (`image_url`) COMMENT '图片URL索引（防重复）'
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单反馈图片表（支持多图上传）';

-- ----------------------------
-- Table structure for f_feedback_reply
-- ----------------------------
DROP TABLE IF EXISTS `f_feedback_reply`;
CREATE TABLE `f_feedback_reply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `feedback_id` bigint NOT NULL COMMENT '关联工单ID',
  `user_id` bigint NOT NULL COMMENT '回复人ID',
  `content` varchar(1000) NOT NULL COMMENT '回复内容',
  `is_admin` tinyint(1) NOT NULL COMMENT '是否管理员：0-用户 1-管理员',
  `replyer` varchar(50) NOT NULL COMMENT '回复人昵称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删 1-已删',
  PRIMARY KEY (`id`),
  KEY `idx_feedback_id` (`feedback_id`) COMMENT '工单ID索引'
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单回复表';

-- ----------------------------
-- Table structure for product_sku
-- ----------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` bigint NOT NULL COMMENT '关联商品ID',
  `spec_id` bigint NOT NULL COMMENT '关联规格ID（对应product_spec.id）',
  `spec_value` varchar(255) DEFAULT NULL COMMENT 'SKU规格值/票种名称',
  `price` decimal(10,2) NOT NULL COMMENT '该票种的价格（如早鸟票88.00）',
  `stock` int DEFAULT '0' COMMENT '库存（可选，票务可设为-1表示无库存限制）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_spec_id` (`spec_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品SKU表（票种价格）';

-- ----------------------------
-- Table structure for product_spec
-- ----------------------------
DROP TABLE IF EXISTS `product_spec`;
CREATE TABLE `product_spec` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `spec_name` varchar(50) NOT NULL COMMENT '规格名称（固定为“票种”）',
  `spec_value` varchar(50) NOT NULL COMMENT '规格值（早鸟票/VIP票/普通票）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品规格表（票种）';

-- ----------------------------
-- Table structure for p_category
-- ----------------------------
DROP TABLE IF EXISTS `p_category`;
CREATE TABLE `p_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID（主键）',
  `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父分类ID：0表示一级分类，非0表示子分类',
  `category_name` varchar(50) NOT NULL COMMENT '分类名称（适配二次元：如手办、COS服、吧唧）',
  `category_level` tinyint NOT NULL COMMENT '分类层级：1=一级分类，2=二级分类，3=三级分类',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值：数值越大越靠前，用于分类展示排序',
  `icon` varchar(255) DEFAULT '' COMMENT '分类图标URL（毕设加分项：比如手办分类配手办图标）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间（自动更新）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_parent_name` (`parent_id`,`category_name`),
  KEY `idx_parent_id` (`parent_id`) COMMENT '父分类索引：优化子分类查询',
  KEY `idx_status_delete` (`status`) COMMENT '状态+删除索引：优化有效分类查询'
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='二次元电商商品分类表';

-- ----------------------------
-- Table structure for p_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `p_comment_like`;
CREATE TABLE `p_comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`,`user_id`) COMMENT '用户对评论仅能点赞一次',
  KEY `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论点赞表';

-- ----------------------------
-- Table structure for p_community_comment
-- ----------------------------
DROP TABLE IF EXISTS `p_community_comment`;
CREATE TABLE `p_community_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID（0为根评论）',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `image_urls` varchar(2000) DEFAULT '' COMMENT '评论图片（仅根评论可传）',
  `user_name` varchar(50) DEFAULT NULL COMMENT '评论人昵称',
  `user_avatar` varchar(255) DEFAULT NULL COMMENT '评论人头像URL',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `is_delete` tinyint DEFAULT '0' COMMENT '是否删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post` (`post_id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区帖子评论表';

-- ----------------------------
-- Table structure for p_community_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `p_community_comment_like`;
CREATE TABLE `p_community_comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `community_comment_id` bigint NOT NULL COMMENT '社区评论ID（关联p_community_comment的id）',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`community_comment_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区评论点赞表';

-- ----------------------------
-- Table structure for p_community_post
-- ----------------------------
DROP TABLE IF EXISTS `p_community_post`;
CREATE TABLE `p_community_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '发帖用户ID',
  `user_avatar` varchar(500) DEFAULT '' COMMENT '发布人头像URL',
  `user_name` varchar(100) DEFAULT '' COMMENT '发布人用户名',
  `title` varchar(200) NOT NULL COMMENT '帖子标题',
  `content` text NOT NULL COMMENT '帖子内容',
  `image_urls` varchar(2000) DEFAULT '' COMMENT '帖子图片（逗号分隔）',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `view_count` int DEFAULT '0' COMMENT '浏览数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0=删除,1=正常,2=审核中,3=封禁',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区帖子表';

-- ----------------------------
-- Table structure for p_community_post_like
-- ----------------------------
DROP TABLE IF EXISTS `p_community_post_like`;
CREATE TABLE `p_community_post_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID（关联p_community_post.id）',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `is_cancel` tinyint DEFAULT '0' COMMENT '是否取消点赞：0=未取消，1=已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`) COMMENT '一个用户只能给一个帖子点赞一次',
  KEY `idx_post_id` (`post_id`) COMMENT '优化帖子点赞数查询'
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区帖子点赞关联表';

-- ----------------------------
-- Table structure for p_order
-- ----------------------------
DROP TABLE IF EXISTS `p_order`;
CREATE TABLE `p_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号（唯一，如：20260122100000001）',
  `user_id` bigint NOT NULL COMMENT '下单用户ID（关联用户表）',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实付金额（扣除优惠/运费）',
  `freight_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '运费',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `pay_type` tinyint DEFAULT '0' COMMENT '支付方式：0=未支付，1=微信，2=支付宝，3=银行卡',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0=未支付，1=已支付，2=退款中，3=已退款, 4=已取消',
  `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态：\r\n    0: ''待付款'',\r\n    1: ''待发货'',\r\n    2: ''待收货'',\r\n    3: ''已完成'',\r\n    4: ''已取消'',\r\n    5: ''售后中''',
  `consignee` varchar(32) NOT NULL DEFAULT '' COMMENT '收货人姓名',
  `consignee_phone` varchar(11) NOT NULL DEFAULT '' COMMENT '收货人手机号',
  `consignee_address` varchar(255) NOT NULL DEFAULT '' COMMENT '收货人地址',
  `delivery_company` varchar(32) DEFAULT '' COMMENT '快递公司（如：顺丰、圆通）',
  `delivery_sn` varchar(64) DEFAULT '' COMMENT '物流单号',
  `remark` varchar(500) DEFAULT '' COMMENT '订单备注',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0=未删，1=已删',
  `is_ticket` tinyint(1) DEFAULT '0' COMMENT '是否票务订单 0=普通商品 1=漫展票务',
  `order_type` tinyint NOT NULL DEFAULT '0' COMMENT '订单类型：0=普通商品订单，1=漫展票务订单',
  `buyer_ids` varchar(500) DEFAULT NULL COMMENT '关联购票人ID列表（逗号分隔，如：1,2,3）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_order_type` (`order_type`),
  KEY `idx_buyer_ids` (`buyer_ids`(10))
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';

-- ----------------------------
-- Table structure for p_order_item
-- ----------------------------
DROP TABLE IF EXISTS `p_order_item`;
CREATE TABLE `p_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint NOT NULL COMMENT '订单ID（关联p_order.id）',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号（冗余，便于查询）',
  `product_id` bigint NOT NULL COMMENT '商品ID（关联p_product.id）',
  `sku_id` bigint DEFAULT NULL COMMENT '关联product_sku.id（票种SKU）',
  `ticket_type` varchar(50) DEFAULT NULL COMMENT '票种名称（早鸟票/VIP票）',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称（冗余，避免商品修改后订单名称变化）',
  `product_img` varchar(255) DEFAULT '' COMMENT '商品图片（冗余，取商品封面图/第一张图）',
  `product_price` decimal(10,2) NOT NULL COMMENT '商品单价（下单时的价格，冗余）',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  `total_price` decimal(10,2) NOT NULL COMMENT '该商品总价（product_price * quantity）',
  `buyer_id` bigint DEFAULT NULL COMMENT '关联购票人ID（票务订单必填）',
  `buyer_name` varchar(50) DEFAULT NULL COMMENT '购票人姓名（冗余存储）',
  `buyer_id_card` varchar(18) DEFAULT NULL COMMENT '购票人身份证号（脱敏，如：110********1234）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0=未删，1=已删',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_buyer_id` (`buyer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单项表（订单商品明细）';

-- ----------------------------
-- Table structure for p_product
-- ----------------------------
DROP TABLE IF EXISTS `p_product`;
CREATE TABLE `p_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `category_id` bigint NOT NULL COMMENT '商品所属二级分类ID（关联p_category.id）',
  `first_category_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '一级分类ID（关联p_category的id，0表示未分类）',
  `cover_img` varchar(255) DEFAULT '' COMMENT '商品封面图URL',
  `price` decimal(10,2) NOT NULL COMMENT '商品售价',
  `original_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品原价',
  `remain_stock` int NOT NULL DEFAULT '0' COMMENT '商品剩余库存',
  `total_stock` int NOT NULL DEFAULT '0' COMMENT '商品总库存',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '商品状态：1=上架，0=下架',
  `sort` int NOT NULL DEFAULT '1' COMMENT '排序（值越大越靠前）',
  `tag` varchar(50) DEFAULT '' COMMENT '商品标签（多个用逗号分隔：新品,热销）',
  `product_type` tinyint NOT NULL DEFAULT '0' COMMENT '商品类型：0=普通商品，1=漫展虚拟商品',
  `is_ticket` tinyint(1) DEFAULT '0' COMMENT '是否为漫展票务 0=否 1=是',
  `comic_con_id` bigint DEFAULT NULL COMMENT '关联漫展ID（comic_con.id）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `sales` int DEFAULT '0' COMMENT '销量/已售数量',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`) COMMENT '按分类查询索引',
  KEY `idx_status_category` (`status`,`category_id`) COMMENT '移动端商品查询索引',
  KEY `idx_product_name` (`product_name`) COMMENT '商品名称模糊查询索引',
  KEY `idx_first_category_id` (`first_category_id`) COMMENT '一级分类ID索引：优化按一级分类查询商品',
  KEY `idx_product_type` (`product_type`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品基础表';

-- ----------------------------
-- Table structure for p_product_collect
-- ----------------------------
DROP TABLE IF EXISTS `p_product_collect`;
CREATE TABLE `p_product_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联用户表）',
  `product_id` bigint NOT NULL COMMENT '商品ID（关联p_product表）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '是否删除（0=未删，1=已删）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`,`is_delete`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品收藏表';

-- ----------------------------
-- Table structure for p_product_comment
-- ----------------------------
DROP TABLE IF EXISTS `p_product_comment`;
CREATE TABLE `p_product_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID（0为根评论，回复时填对应评论ID）',
  `content` varchar(500) NOT NULL COMMENT '评论内容（根评论500字，回复300字）',
  `image_urls` varchar(2000) DEFAULT '' COMMENT '评论图片URL（逗号分隔，仅根评论可传）',
  `like_count` int DEFAULT '0' COMMENT '评论点赞数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '是否删除（0=否，1=是）',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评论表';

-- ----------------------------
-- Table structure for p_product_detail
-- ----------------------------
DROP TABLE IF EXISTS `p_product_detail`;
CREATE TABLE `p_product_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `product_id` bigint NOT NULL COMMENT '关联商品ID',
  `detail_content` text COMMENT '商品详情富文本（图文）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`) COMMENT '商品ID唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品详情表';

-- ----------------------------
-- Table structure for p_product_image
-- ----------------------------
DROP TABLE IF EXISTS `p_product_image`;
CREATE TABLE `p_product_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `product_id` bigint NOT NULL COMMENT '关联商品ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '图片类型：1=轮播图，2=详情图',
  `sort` int NOT NULL DEFAULT '1' COMMENT '图片排序',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`) COMMENT '按商品ID查询图片索引'
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品图片表';

-- ----------------------------
-- Table structure for p_product_like
-- ----------------------------
DROP TABLE IF EXISTS `p_product_like`;
CREATE TABLE `p_product_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_user` (`product_id`,`user_id`) COMMENT '用户对商品仅能点赞一次'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品点赞表';

-- ----------------------------
-- Table structure for p_report
-- ----------------------------
DROP TABLE IF EXISTS `p_report`;
CREATE TABLE `p_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `target_type` tinyint NOT NULL COMMENT '举报目标类型：1=商品，2=商品评论，3=社区帖子，4=社区帖子评论',
  `target_id` bigint NOT NULL COMMENT '举报目标ID（商品ID/评论ID/帖子ID/帖子评论ID）',
  `user_id` bigint NOT NULL COMMENT '举报用户ID',
  `report_reason` tinyint NOT NULL COMMENT '举报理由枚举：1=违反法律规定,2=色情低俗,3=赌博诈骗,4=人身攻击,5=侵犯隐私,6=垃圾广告,7=引战,8=刷屏/抢楼,9=青少年不良,10=谣言造谣,99=其他',
  `custom_reason` varchar(150) DEFAULT '' COMMENT '自定义举报理由（仅report_reason=99时填写）',
  `status` tinyint DEFAULT '0' COMMENT '举报处理状态：0=待处理,1=已受理,2=已驳回,3=已处理',
  `handler_id` bigint DEFAULT '0' COMMENT '处理人ID（管理员ID）',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_note` varchar(500) DEFAULT '' COMMENT '处理备注（管理员填写）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通用举报表（兼容商品/社区）';

-- ----------------------------
-- Table structure for u_cart
-- ----------------------------
DROP TABLE IF EXISTS `u_cart`;
CREATE TABLE `u_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `goods_id` varchar(64) NOT NULL COMMENT '商品ID',
  `goods_name` varchar(255) NOT NULL COMMENT '商品名称',
  `goods_image` varchar(512) DEFAULT NULL COMMENT '商品图片地址',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价（元）',
  `num` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_goods` (`user_id`,`goods_id`) COMMENT '用户+商品唯一索引，避免重复加入'
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户购物车表';

-- ----------------------------
-- Table structure for u_user
-- ----------------------------
DROP TABLE IF EXISTS `u_user`;
CREATE TABLE `u_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(30) NOT NULL COMMENT '登录账号',
  `password` varchar(100) NOT NULL COMMENT '登录密码（BCrypt加密）',
  `nickname` varchar(30) NOT NULL DEFAULT '' COMMENT '用户昵称',
  `tel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '',
  `role` enum('admin','manager','leader','member','consumer') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'member' COMMENT '权限',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '账户状态：1-启用，0-禁用',
  `avatar` varchar(200) DEFAULT '' COMMENT '头像URL',
  `email` varchar(50) DEFAULT '' COMMENT '邮箱（可选）',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `idx_u_user_username` (`username`),
  CONSTRAINT `chk_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=2034895749309763587 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Table structure for u_user_address
-- ----------------------------
DROP TABLE IF EXISTS `u_user_address`;
CREATE TABLE `u_user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联u_user.id）',
  `consignee` varchar(32) NOT NULL COMMENT '收货人姓名',
  `phone` varchar(16) NOT NULL COMMENT '收货人手机号',
  `province` varchar(32) NOT NULL COMMENT '省',
  `city` varchar(32) NOT NULL COMMENT '市',
  `district` varchar(32) NOT NULL COMMENT '区/县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址（街道/门牌号）',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认地址：1=是，0=否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0=未删，1=已删',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '按用户ID查询地址',
  KEY `idx_is_default` (`is_default`) COMMENT '查询默认地址'
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收货地址表（个人中心）';

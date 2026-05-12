/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80019
Source Host           : localhost:3306
Source Database       : anime_shop

Target Server Type    : MYSQL
Target Server Version : 80019
File Encoding         : 65001

Date: 2026-05-12 11:21:12
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
-- Records of buyer_info
-- ----------------------------
INSERT INTO `buyer_info` VALUES ('2', '1999834598331097089', '张科', '441622200208082376', '1', '', '2026-03-15 17:06:08', '2026-03-15 16:55:29', '2026-03-15 16:55:29');
INSERT INTO `buyer_info` VALUES ('3', '1999834598331097089', '胡歌', '441622198605236645', '1', '', '2026-03-15 17:06:17', '2026-03-15 16:57:57', '2026-03-15 16:57:57');
INSERT INTO `buyer_info` VALUES ('4', '1999834598331097089', '真彭于晏', '442534198906052356', '1', '', '2026-03-16 10:25:58', '2026-03-16 10:21:05', '2026-03-16 10:21:05');
INSERT INTO `buyer_info` VALUES ('5', '1999834598331097089', '纳兹', '442516200012251648', '1', '', '2026-03-20 14:27:24', '2026-03-20 14:23:21', '2026-03-20 14:23:21');
INSERT INTO `buyer_info` VALUES ('6', '2034895749309763586', 'zktest', '442568198805123564', '1', '', '2026-03-20 15:48:21', '2026-03-20 15:46:59', '2026-03-20 15:46:59');
INSERT INTO `buyer_info` VALUES ('7', '1999834598331097089', '八云紫', '432622199811263286', '0', null, null, '2026-03-20 16:51:57', '2026-03-20 16:51:57');

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
-- Records of b_banner
-- ----------------------------
INSERT INTO `b_banner` VALUES ('1', '伊雷娜手办', 'https://s41.ax1x.com/2026/03/16/peVWOv4.png', '/search?keyword=%E4%BC%8A%E8%95%BE%E5%A8%9C&pageNum=1&pageSize=10&sort=default', '1', '1');
INSERT INTO `b_banner` VALUES ('2', '初音手办预售', 'https://s41.ax1x.com/2026/01/26/pZRiOPS.jpg', '/search?keyword=%E5%88%9D%E9%9F%B3%E6%9C%AA%E6%9D%A5&pageNum=1&pageSize=10&sort=default', '2', '1');

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漫展主表';

-- ----------------------------
-- Records of comic_con
-- ----------------------------
INSERT INTO `comic_con` VALUES ('2', '4', 'cp32', '2026-05-01 00:00:00', '2026-05-04 00:00:00', '上海博览会啊啊啊', '啊啊啊啊啊', '', '1', '2026-02-14 14:11:56', '2026-02-14 14:11:56');
INSERT INTO `comic_con` VALUES ('3', '6', '2026 广州 萤火虫 5.1', '2026-05-01 09:00:00', '2026-05-04 18:00:00', '广州琶洲国际中心', '萤火虫xxxxxxx', '', '1', '2026-03-20 11:03:36', '2026-03-20 11:03:36');
INSERT INTO `comic_con` VALUES ('5', '9', 'ces', '2026-03-10 00:00:00', '2026-04-10 00:00:00', 'aa', '', '', '1', '2026-03-23 12:16:01', '2026-03-23 14:14:44');
INSERT INTO `comic_con` VALUES ('6', '10', '测试3333', '2026-03-23 14:13:53', '2026-04-02 00:00:00', '啊啊啊', '', '', '1', '2026-03-23 14:13:59', '2026-03-23 14:13:59');
INSERT INTO `comic_con` VALUES ('7', '12', '深圳会展中心漫展', '2026-03-26 00:00:00', '2026-04-09 00:00:00', '会展中心', '啊啊啊啊', '', '1', '2026-03-24 15:05:08', '2026-03-24 15:05:08');
INSERT INTO `comic_con` VALUES ('8', '14', '深圳会展中心漫展2test', '2026-05-14 00:00:00', '2026-05-30 00:00:00', '会展中心', '啊啊啊啊啊', '', '1', '2026-05-09 10:41:03', '2026-05-09 10:41:03');

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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漫展票种/场次表';

-- ----------------------------
-- Records of comic_con_ticket
-- ----------------------------
INSERT INTO `comic_con_ticket` VALUES ('2', '4', '2', '5.1早鸟票', '75.00', '5', '1995', '2000', '1', '5', '2026-02-15 00:00:00', '2026-03-01 00:00:00', '1', '2026-02-14 15:01:14', '2026-05-12 11:08:45');
INSERT INTO `comic_con_ticket` VALUES ('3', '4', '2', '5.1VIP票', '175.00', '6', '996', '1000', '1', '5', '2026-02-15 00:00:00', '2026-03-01 00:00:00', '1', '2026-02-14 15:01:56', '2026-05-12 10:15:16');
INSERT INTO `comic_con_ticket` VALUES ('4', '6', '3', '早鸟票', '75.00', null, '5000', '5000', '1', '5', '2026-04-15 12:00:00', '2026-04-25 12:00:00', '1', '2026-03-20 11:34:08', '2026-03-24 14:18:26');
INSERT INTO `comic_con_ticket` VALUES ('5', '6', '3', '普通票', '85.00', null, '20000', '20000', '1', '5', '2026-04-15 12:00:00', '2026-05-04 12:00:00', '1', '2026-03-20 11:45:13', '2026-03-24 14:18:27');
INSERT INTO `comic_con_ticket` VALUES ('6', '6', '3', 'VIP票', '150.00', null, '5000', '5000', '1', '2', '2026-04-15 12:00:00', '2026-05-04 12:00:00', '1', '2026-03-20 11:46:05', '2026-03-24 14:18:38');
INSERT INTO `comic_con_ticket` VALUES ('10', '10', '6', 'SVIP票', '200.00', '12', '99', '101', '1', '5', '2026-03-23 14:16:30', '2026-04-22 00:00:00', '1', '2026-03-23 14:16:34', '2026-05-12 09:57:25');
INSERT INTO `comic_con_ticket` VALUES ('12', '12', '7', 'VIP票', '150.00', '14', '1996', '2000', '1', '2', '2026-03-24 15:06:57', '2026-03-31 00:00:00', '1', '2026-03-24 15:07:04', '2026-05-12 09:56:59');
INSERT INTO `comic_con_ticket` VALUES ('13', '12', '7', '普通票', '85.00', '15', '2996', '3000', '1', '5', '2026-03-24 15:07:24', '2026-03-31 00:00:00', '1', '2026-03-24 15:07:45', '2026-03-24 15:51:21');
INSERT INTO `comic_con_ticket` VALUES ('14', '12', '7', '早鸟票', '75.00', '16', '2000', '2000', '1', '5', '2026-03-24 16:06:05', '2026-03-31 00:00:00', '1', '2026-03-24 16:06:08', '2026-03-24 16:06:08');
INSERT INTO `comic_con_ticket` VALUES ('15', '14', '8', '早鸟票', '75.00', '17', '9998', '10000', '1', '5', '2026-05-09 00:00:00', '2026-05-14 00:00:00', '2', '2026-05-09 10:41:41', '2026-05-09 11:09:47');
INSERT INTO `comic_con_ticket` VALUES ('16', '14', '8', 'VIP票', '150.00', '18', '5000', '5000', '1', '2', '2026-05-09 00:00:00', '2026-05-30 00:00:00', '2', '2026-05-09 10:42:11', '2026-05-09 11:09:27');
INSERT INTO `comic_con_ticket` VALUES ('17', '14', '8', '普通票', '85.00', '19', '25000', '25000', '1', '5', '2026-05-14 10:42:37', '2026-05-30 00:00:00', '2', '2026-05-09 10:42:47', '2026-05-09 11:09:26');
INSERT INTO `comic_con_ticket` VALUES ('19', '14', '8', '特殊票', '120.00', '21', '10000', '10000', '1', '5', '2026-05-09 00:00:00', '2026-05-30 00:00:00', '2', '2026-05-09 10:43:56', '2026-05-09 11:09:24');

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='移动端工单反馈主表';

-- ----------------------------
-- Records of f_feedback
-- ----------------------------
INSERT INTO `f_feedback` VALUES ('1', '2012032783522684930', '测试工单哈哈哈哈啊啊啊啊', '4', '错误', 'zzz1', '管理员', '2026-02-12 19:08:51', '2026-02-13 15:04:01', '2026-02-13 15:04:01', '0');
INSERT INTO `f_feedback` VALUES ('2', '1999847752930926594', '11111111111', '3', '你不行', '12345', '管理员', '2026-02-13 09:19:13', '2026-02-13 15:03:49', '2026-02-13 15:03:49', '0');
INSERT INTO `f_feedback` VALUES ('3', '2012032935754948609', '测试工单啊啊啊啊啊啊33333333', '2', '111', 'zczcz', '管理员', '2026-02-13 13:44:46', '2026-02-13 15:03:17', '2026-02-13 15:03:17', '0');
INSERT INTO `f_feedback` VALUES ('4', '1999847752930926594', '测试水水水水水水水水水水水水', '2', 'aaa', '雾雨魔理沙', '管理员', '2026-03-17 15:14:05', '2026-03-19 14:55:16', '2026-03-19 14:55:16', '0');
INSERT INTO `f_feedback` VALUES ('5', '1999847752930926594', 'cessssssssssssssssssssssssss', '2', '111', '雾雨魔理沙', '管理员', '2026-03-17 15:24:43', '2026-03-20 14:37:04', '2026-03-20 14:37:04', '0');
INSERT INTO `f_feedback` VALUES ('6', '1999834598331097089', '我想增加妖精的尾巴IP', '1', null, 'admin', '', '2026-03-20 14:30:18', null, '2026-03-20 14:38:37', '0');
INSERT INTO `f_feedback` VALUES ('7', '2034895749309763586', '这是一个工单!!!!', '2', '收到', 'zktest', '管理员', '2026-03-20 16:41:25', '2026-03-20 16:43:19', '2026-03-20 16:43:19', '0');
INSERT INTO `f_feedback` VALUES ('8', '1999834598331097089', 'hhahahaaaaaa', '2', '111', '管理员1', '管理员', '2026-03-24 18:14:24', '2026-03-24 18:18:21', '2026-03-24 18:18:21', '0');
INSERT INTO `f_feedback` VALUES ('9', '1999834598331097089', 'aaaaaaaaaaaaa', '0', null, '管理员1', '', '2026-03-26 10:45:59', null, '2026-03-26 10:45:59', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单反馈图片表（支持多图上传）';

-- ----------------------------
-- Records of f_feedback_image
-- ----------------------------
INSERT INTO `f_feedback_image` VALUES ('1', '1', 'http://localhost:8080/feedback/56fc41f2fbbf4586ba5e5822dd2e6633.jpg', '1', '2026-02-12 19:08:51');
INSERT INTO `f_feedback_image` VALUES ('2', '1', 'http://localhost:8080/feedback/f2da9cc6c9d14ed882bdcad4a89e775f.jpg', '2', '2026-02-12 19:08:51');
INSERT INTO `f_feedback_image` VALUES ('3', '2', 'http://localhost:8080/feedback/005d665ef4e14f21931b832ef47e952c.jpg', '1', '2026-02-13 09:19:13');
INSERT INTO `f_feedback_image` VALUES ('4', '3', 'http://localhost:8080/feedback/9033091ff5cd4045b2e89e4bf0e0b14e.jpg', '1', '2026-02-13 13:44:46');
INSERT INTO `f_feedback_image` VALUES ('5', '4', 'http://localhost:8080/feedback/0394ca24fcf54e42b3c6a1f0a24d208d.jpg', '1', '2026-03-17 15:14:05');
INSERT INTO `f_feedback_image` VALUES ('6', '4', 'http://localhost:8080/feedback/c06edb3fbc9f46a3b86bac308d5f0221.jpg', '2', '2026-03-17 15:14:05');
INSERT INTO `f_feedback_image` VALUES ('7', '4', 'http://localhost:8080/feedback/1fd07bef2ad14aee8e4de595c46c609d.jpg', '3', '2026-03-17 15:14:05');
INSERT INTO `f_feedback_image` VALUES ('8', '5', 'http://localhost:8080/feedback/faa117d1b60b4f1f8a79bc2baf1fb82b.jpg', '1', '2026-03-17 15:24:43');
INSERT INTO `f_feedback_image` VALUES ('9', '5', 'http://localhost:8080/feedback/0348f5725d3645e6b9f9dcddeb449a31.jpg', '2', '2026-03-17 15:24:43');
INSERT INTO `f_feedback_image` VALUES ('10', '6', 'http://localhost:8080/feedback/be7b8edbe48c43a3a555cb7aed6d5fdb.gif', '1', '2026-03-20 14:30:18');
INSERT INTO `f_feedback_image` VALUES ('11', '7', 'http://localhost:8080/feedback/88d5257f17d44bbb83416a73feae137f.jpg', '1', '2026-03-20 16:41:25');
INSERT INTO `f_feedback_image` VALUES ('12', '8', 'http://localhost:8080/feedback/4609cdc3f2454fd0b336f75200f3108d.gif', '1', '2026-03-24 18:14:24');
INSERT INTO `f_feedback_image` VALUES ('13', '9', 'http://localhost:8080/feedback/0161caac2d0049e1bece1e6a15b24e8e.gif', '1', '2026-03-26 10:45:59');

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单回复表';

-- ----------------------------
-- Records of f_feedback_reply
-- ----------------------------
INSERT INTO `f_feedback_reply` VALUES ('1', '1', '2012032783522684930', 'AAAAAAAA', '0', 'zzz1', '2026-02-13 10:25:28', '0');
INSERT INTO `f_feedback_reply` VALUES ('2', '1', '2012032783522684930', 't听得到吗', '0', 'zzz1', '2026-02-13 10:27:15', '0');
INSERT INTO `f_feedback_reply` VALUES ('3', '1', '2012032783522684930', 'oioioi', '0', 'zzz1', '2026-02-13 10:27:19', '0');
INSERT INTO `f_feedback_reply` VALUES ('4', '3', '2012032935754948609', '听到没管理员处理工单', '0', 'zczcz', '2026-02-13 13:44:59', '0');
INSERT INTO `f_feedback_reply` VALUES ('5', '3', '1999834598331097089', '听到了?没?', '1', '管理员', '2026-02-13 14:13:39', '0');
INSERT INTO `f_feedback_reply` VALUES ('6', '2', '1999834598331097089', '啊啊啊啊', '1', '管理员', '2026-02-13 14:35:59', '0');
INSERT INTO `f_feedback_reply` VALUES ('7', '3', '1999834598331097089', '可以了你过关', '1', '管理员', '2026-02-13 14:51:51', '0');
INSERT INTO `f_feedback_reply` VALUES ('8', '3', '2012032935754948609', 'okok', '0', 'zczcz', '2026-02-13 14:53:14', '0');
INSERT INTO `f_feedback_reply` VALUES ('9', '2', '1999834598331097089', '你不行下一个', '1', '管理员', '2026-02-13 15:03:41', '0');
INSERT INTO `f_feedback_reply` VALUES ('10', '6', '1999834598331097089', '快说话', '0', 'admin', '2026-03-20 14:31:42', '0');
INSERT INTO `f_feedback_reply` VALUES ('11', '6', '1999834598331097089', '111', '1', '管理员', '2026-03-20 14:38:37', '0');
INSERT INTO `f_feedback_reply` VALUES ('12', '7', '1999834598331097089', '111', '1', '管理员', '2026-03-20 16:43:11', '0');
INSERT INTO `f_feedback_reply` VALUES ('13', '8', '1999834598331097089', 'nihao', '0', '管理员1', '2026-03-24 18:14:30', '0');
INSERT INTO `f_feedback_reply` VALUES ('14', '8', '1999834598331097089', '你好', '1', '管理员', '2026-03-24 18:18:15', '0');
INSERT INTO `f_feedback_reply` VALUES ('15', '9', '1999834598331097089', '瞅你咋地', '0', '管理员1', '2026-03-26 10:46:07', '0');

-- ----------------------------
-- Table structure for order_ticket_relation
-- ----------------------------
DROP TABLE IF EXISTS `order_ticket_relation`;
CREATE TABLE `order_ticket_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联p_order.id',
  `order_item_id` bigint DEFAULT NULL COMMENT '关联p_order_item.id',
  `user_id` bigint NOT NULL COMMENT '购票用户ID',
  `comic_con_ticket_id` bigint DEFAULT NULL COMMENT '关联漫展票种ID',
  `real_name` varchar(50) DEFAULT NULL COMMENT '购票人真实姓名',
  `id_card` varchar(18) DEFAULT NULL COMMENT '购票人身份证号',
  `verify_code` varchar(20) DEFAULT NULL COMMENT '唯一核销码',
  `qr_code` text COMMENT '核销二维码',
  `verify_status` tinyint NOT NULL DEFAULT '0' COMMENT '核销状态：0=未核销 1=已核销 2=已退票',
  `verify_time` datetime DEFAULT NULL COMMENT '核销时间',
  `verify_staff` varchar(50) DEFAULT NULL COMMENT '核销员',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_verify_code` (`verify_code`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漫展票务核销关联表';

-- ----------------------------
-- Records of order_ticket_relation
-- ----------------------------
INSERT INTO `order_ticket_relation` VALUES ('1', '25', '31', '1999834598331097089', '5', '胡歌', '441622********6645', '204895-01', null, '0', null, null, '0', '2026-03-15 18:14:19', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('2', '27', '33', '1999834598331097089', '6', '胡歌', '441622********6645', '629934-01', null, '0', null, null, '0', '2026-03-16 09:44:16', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('3', '31', '34', '1999834598331097089', '5', '胡歌', '441622********6645', '735181-01', null, '0', null, null, '0', '2026-03-16 10:07:39', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('4', '31', '35', '1999834598331097089', '5', '张科', '441622********2376', '735181-02', null, '0', null, null, '0', '2026-03-16 10:07:39', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('5', '35', '39', '1999834598331097089', '6', '真彭于晏', '442534********2356', '296543-01', null, '0', null, null, '0', '2026-03-20 13:30:43', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('6', '35', '40', '1999834598331097089', '6', '胡歌', '441622********6645', '296543-02', null, '0', null, null, '0', '2026-03-20 13:30:43', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('7', '37', '42', '2034895749309763586', '5', 'zktest', '442568********3564', '231692-01', null, '0', null, null, '0', '2026-03-20 15:49:54', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('8', '40', '48', '1999834598331097089', '14', '纳兹', '442516********1648', '397856-01', null, '0', null, null, '0', '2026-03-24 15:21:42', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('9', '40', '49', '1999834598331097089', '14', '真彭于晏', '442534********2356', '397856-02', null, '0', null, null, '0', '2026-03-24 15:21:42', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('10', '41', '50', '1999834598331097089', '15', '纳兹', '442516********1648', '510740-01', null, '0', null, null, '0', '2026-03-24 15:23:28', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('11', '41', '51', '1999834598331097089', '15', '真彭于晏', '442534********2356', '510740-02', null, '0', null, null, '0', '2026-03-24 15:23:28', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('12', '41', '52', '1999834598331097089', '15', '胡歌', '441622********6645', '510740-03', null, '0', null, null, '0', '2026-03-24 15:23:28', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('13', '41', '53', '1999834598331097089', '15', '张科', '441622********2376', '510740-04', null, '0', null, null, '0', '2026-03-24 15:23:28', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('14', '47', '63', '1999834598331097089', '5', '纳兹', '442516********1648', '762171-01', null, '0', null, null, '0', '2026-03-27 17:32:14', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('15', '49', '65', '1999834598331097089', '17', '纳兹', '442516********1648', '103528-01', null, '0', null, null, '0', '2026-05-09 11:08:45', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('16', '50', '66', '1999834598331097089', '17', '纳兹', '442516********1648', '388450-01', null, '0', null, null, '0', '2026-05-09 11:09:48', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('17', '52', '68', '1999834598331097089', '5', '纳兹', '442516********1648', '736820-01', null, '0', null, null, '0', '2026-05-09 12:01:56', '2026-05-12 10:31:21');
INSERT INTO `order_ticket_relation` VALUES ('18', '53', '69', '1999834598331097089', '5', '真彭于晏', '442534********2356', '944235-01', null, '0', null, null, '0', '2026-05-12 09:50:54', '2026-05-12 10:13:09');
INSERT INTO `order_ticket_relation` VALUES ('19', '54', '70', '1999834598331097089', '6', '张科', '441622********2376', '248268-01', null, '0', null, null, '0', '2026-05-12 09:51:14', '2026-05-12 10:13:09');
INSERT INTO `order_ticket_relation` VALUES ('20', '54', '71', '1999834598331097089', '6', '胡歌', '441622********6645', '248268-02', null, '0', null, null, '0', '2026-05-12 09:51:14', '2026-05-12 10:13:09');
INSERT INTO `order_ticket_relation` VALUES ('21', '55', '72', '1999834598331097089', '14', '胡歌', '441622********6645', '910574-01', null, '1', '2026-05-12 10:24:57', 'zk', '0', '2026-05-12 09:56:59', '2026-05-12 10:24:56');
INSERT INTO `order_ticket_relation` VALUES ('22', '55', '73', '1999834598331097089', '14', '纳兹', '442516********1648', '910574-02', null, '1', '2026-05-12 10:28:07', 'zk', '0', '2026-05-12 09:56:59', '2026-05-12 10:28:07');
INSERT INTO `order_ticket_relation` VALUES ('23', '56', '74', '1999834598331097089', '12', '张科', '441622********2376', '278869-01', null, '0', null, null, '0', '2026-05-12 09:57:26', '2026-05-12 10:13:09');
INSERT INTO `order_ticket_relation` VALUES ('24', '57', '75', '1999834598331097089', '7', '纳兹', '442516********1648', '633141-01', null, '0', null, null, '0', '2026-05-12 10:15:01', '2026-05-12 10:15:01');
INSERT INTO `order_ticket_relation` VALUES ('25', '58', '76', '1999834598331097089', '6', '胡歌', '441622********6645', '727751-01', null, '1', '2026-05-12 10:21:15', 'zk', '0', '2026-05-12 10:15:16', '2026-05-12 10:21:14');
INSERT INTO `order_ticket_relation` VALUES ('26', '58', '77', '1999834598331097089', '6', '张科', '441622********2376', '727751-02', null, '1', '2026-05-12 10:26:38', 'ZK', '0', '2026-05-12 10:15:16', '2026-05-12 10:26:37');
INSERT INTO `order_ticket_relation` VALUES ('27', '59', '78', '1999834598331097089', '5', '真彭于晏', '442534********2356', '957771-01', null, '0', null, null, '0', '2026-05-12 10:33:44', '2026-05-12 10:33:44');
INSERT INTO `order_ticket_relation` VALUES ('28', '60', '79', '1999834598331097089', '5', '胡歌', '441622********6645', '918425-01', null, '1', '2026-05-12 10:47:00', 'sdd', '0', '2026-05-12 10:38:56', '2026-05-12 10:46:59');
INSERT INTO `order_ticket_relation` VALUES ('29', '61', '80', '1999834598331097089', '5', '胡歌', '441622********6645', '937858-01', null, '0', null, null, '0', '2026-05-12 11:08:45', '2026-05-12 11:08:45');

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
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品SKU表（票种价格）';

-- ----------------------------
-- Records of product_sku
-- ----------------------------
INSERT INTO `product_sku` VALUES ('5', '4', '0', '早鸟票', '85.00', '5000', '2026-03-01 13:02:32', '2026-03-01 13:02:32');
INSERT INTO `product_sku` VALUES ('6', '4', '0', 'VIP票', '158.00', '1000', '2026-03-01 13:02:32', '2026-03-01 13:02:32');
INSERT INTO `product_sku` VALUES ('7', '4', '0', '普通票', '95.00', '9998', '2026-03-01 13:02:32', '2026-03-01 13:02:32');
INSERT INTO `product_sku` VALUES ('8', '6', '0', '早鸟票', '75.00', '5000', '2026-03-20 13:22:59', '2026-03-20 13:22:59');
INSERT INTO `product_sku` VALUES ('9', '8', '3', '普通票', '85.00', '5000', '2026-03-23 11:22:11', '2026-03-23 11:35:52');
INSERT INTO `product_sku` VALUES ('10', '8', '2', 'VIP票', '125.00', '5000', '2026-03-23 11:37:27', '2026-03-23 11:38:20');
INSERT INTO `product_sku` VALUES ('11', '8', '4', '测试票', '75.00', '50', '2026-03-23 11:58:31', '2026-03-23 11:58:31');
INSERT INTO `product_sku` VALUES ('12', '10', '5', 'SVIP票', '200.00', '100', '2026-03-23 14:16:34', '2026-03-24 12:04:26');
INSERT INTO `product_sku` VALUES ('14', '12', '2', 'VIP票', '150.00', '2000', '2026-03-24 15:07:04', '2026-03-24 15:07:04');
INSERT INTO `product_sku` VALUES ('15', '12', '3', '普通票', '85.00', '2996', '2026-03-24 15:07:45', '2026-03-24 15:51:21');
INSERT INTO `product_sku` VALUES ('16', '12', '1', '早鸟票', '75.00', '2000', '2026-03-24 16:06:08', '2026-03-24 16:06:08');
INSERT INTO `product_sku` VALUES ('17', '14', '1', '早鸟票', '75.00', '10000', '2026-05-09 10:41:41', '2026-05-09 10:41:41');
INSERT INTO `product_sku` VALUES ('18', '14', '2', 'VIP票', '150.00', '5000', '2026-05-09 10:42:11', '2026-05-09 10:42:11');
INSERT INTO `product_sku` VALUES ('19', '14', '3', '普通票', '85.00', '25000', '2026-05-09 10:42:47', '2026-05-09 10:43:08');
INSERT INTO `product_sku` VALUES ('21', '14', '7', '特殊票', '120.00', '10000', '2026-05-09 10:43:56', '2026-05-09 10:43:56');

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品规格表（票种）';

-- ----------------------------
-- Records of product_spec
-- ----------------------------
INSERT INTO `product_spec` VALUES ('1', '票种', '早鸟票', '2026-02-14 16:30:34', '2026-02-14 16:30:34');
INSERT INTO `product_spec` VALUES ('2', '票种', 'VIP票', '2026-02-14 16:30:34', '2026-02-14 16:30:34');
INSERT INTO `product_spec` VALUES ('3', '票种', '普通票', '2026-02-14 16:30:34', '2026-02-14 16:30:34');
INSERT INTO `product_spec` VALUES ('4', '票种', '测试票', '2026-03-23 11:58:30', '2026-03-23 11:58:30');
INSERT INTO `product_spec` VALUES ('5', '票种', 'SVIP票', '2026-03-23 14:16:33', '2026-03-23 14:16:33');
INSERT INTO `product_spec` VALUES ('6', '票种', 'aaa', '2026-05-09 10:43:19', '2026-05-09 10:43:19');
INSERT INTO `product_spec` VALUES ('7', '票种', '特殊票', '2026-05-09 10:43:56', '2026-05-09 10:43:56');

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
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '分类图标URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间（自动更新）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_parent_name` (`parent_id`,`category_name`),
  KEY `idx_parent_id` (`parent_id`) COMMENT '父分类索引：优化子分类查询',
  KEY `idx_status_delete` (`status`) COMMENT '状态+删除索引：优化有效分类查询'
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='二次元电商商品分类表';

-- ----------------------------
-- Records of p_category
-- ----------------------------
INSERT INTO `p_category` VALUES ('1', '0', '手办', '1', '10', '', '1', '2026-01-16 19:11:24', '2026-01-26 16:57:08');
INSERT INTO `p_category` VALUES ('2', '0', '周边', '1', '9', '', '1', '2026-01-16 19:11:24', '2026-01-26 16:57:10');
INSERT INTO `p_category` VALUES ('3', '0', '图书漫画', '1', '8', '', '1', '2026-01-16 19:11:24', '2026-01-26 16:57:12');
INSERT INTO `p_category` VALUES ('4', '0', '漫展演出', '1', '7', '', '1', '2026-01-16 19:11:24', '2026-01-26 16:57:14');
INSERT INTO `p_category` VALUES ('5', '0', 'IP', '1', '6', '', '1', '2026-01-16 19:11:24', '2026-01-26 16:57:17');
INSERT INTO `p_category` VALUES ('6', '0', '品牌', '1', '5', '', '1', '2026-01-16 19:11:24', '2026-01-26 16:57:19');
INSERT INTO `p_category` VALUES ('27', '1', '可动手办', '2', '10', 'https://s41.ax1x.com/2026/01/26/pZRFkPU.jpg', '1', '2026-01-16 19:12:02', '2026-01-26 16:53:20');
INSERT INTO `p_category` VALUES ('28', '1', '景品手办', '2', '9', 'https://s41.ax1x.com/2026/01/26/pZRAQ3D.png', '1', '2026-01-16 19:12:02', '2026-01-26 17:22:44');
INSERT INTO `p_category` VALUES ('29', '1', '比例手办', '2', '8', 'https://s41.ax1x.com/2026/01/26/pZRiOPS.jpg', '1', '2026-01-16 19:12:02', '2026-01-26 16:50:06');
INSERT INTO `p_category` VALUES ('30', '1', 'Q版手办', '2', '7', 'https://s41.ax1x.com/2026/01/26/pZRAlge.png', '1', '2026-01-16 19:12:02', '2026-01-26 17:22:30');
INSERT INTO `p_category` VALUES ('31', '2', '谷子', '2', '10', 'https://s41.ax1x.com/2026/01/26/pZRA1jH.jpg', '1', '2026-01-16 19:12:35', '2026-01-26 17:22:55');
INSERT INTO `p_category` VALUES ('32', '2', '日用品', '2', '9', 'https://s41.ax1x.com/2026/01/26/pZRAtEt.jpg', '1', '2026-01-16 19:12:35', '2026-01-26 17:23:07');
INSERT INTO `p_category` VALUES ('33', '2', '服饰鞋包', '2', '8', 'https://s41.ax1x.com/2026/01/26/pZRA8ud.jpg', '1', '2026-01-16 19:12:35', '2026-01-26 17:23:19');
INSERT INTO `p_category` VALUES ('34', '2', '鼠标键盘', '2', '7', 'https://www.helloimg.com/i/2026/01/26/6977338b57ce0.jpeg', '1', '2026-01-16 19:12:35', '2026-01-26 17:26:27');
INSERT INTO `p_category` VALUES ('35', '2', '痛包', '2', '6', 'https://s41.ax1x.com/2026/01/26/pZRANUP.jpg', '1', '2026-01-16 19:12:35', '2026-01-26 17:23:36');
INSERT INTO `p_category` VALUES ('36', '3', '漫画', '2', '10', 'https://www.helloimg.com/i/2026/01/26/6977338edcc9c.jpeg', '1', '2026-01-16 19:12:41', '2026-01-26 17:27:33');
INSERT INTO `p_category` VALUES ('37', '3', '画集', '2', '9', 'https://www.helloimg.com/i/2026/01/26/69773468a7ae9.jpg', '1', '2026-01-16 19:12:41', '2026-01-26 17:30:00');
INSERT INTO `p_category` VALUES ('38', '3', '轻小说', '2', '8', 'https://www.helloimg.com/i/2026/01/26/69773385bd787.jpeg', '1', '2026-01-16 19:12:41', '2026-01-26 17:26:44');
INSERT INTO `p_category` VALUES ('39', '3', '其他出版物', '2', '7', 'https://www.helloimg.com/i/2026/01/26/69773390c05c8.jpg', '1', '2026-01-16 19:12:41', '2026-01-26 17:33:43');
INSERT INTO `p_category` VALUES ('40', '4', '漫展', '2', '10', 'https://www.helloimg.com/i/2026/01/26/69773388d805d.jpg', '1', '2026-01-16 19:12:46', '2026-01-26 17:33:27');
INSERT INTO `p_category` VALUES ('41', '4', '演出', '2', '9', 'https://www.helloimg.com/i/2026/01/26/697733813a567.jpg', '1', '2026-01-16 19:12:46', '2026-01-26 17:27:13');
INSERT INTO `p_category` VALUES ('42', '5', 'FATE系列', '2', '10', 'https://storage.moegirl.org.cn/moegirl/commons/d/d5/FSN%E5%9B%BE%E6%A0%87v2.png!/fw/99?v=20210808153443', '1', '2026-01-16 19:12:52', '2026-01-26 17:35:10');
INSERT INTO `p_category` VALUES ('43', '5', 'VOCALOID', '2', '9', 'https://storage.moegirl.org.cn/moegirl/commons/e/e7/VOCALOID.gif!/fw/280?v=20121231072013', '1', '2026-01-16 19:12:52', '2026-01-26 17:35:56');
INSERT INTO `p_category` VALUES ('44', '5', '海贼王', '2', '8', 'https://storage.moegirl.org.cn/moegirl/commons/5/51/OP-SN.png!/fw/99?v=20220906012444', '1', '2026-01-16 19:12:52', '2026-01-26 17:36:25');
INSERT INTO `p_category` VALUES ('45', '5', '明日方舟', '2', '7', 'https://storage.moegirl.org.cn/moegirl/commons/7/79/%E6%98%8E%E6%97%A5%E6%96%B9%E8%88%9Ficon.png!/fw/70?v=20200819024520', '1', '2026-01-16 19:12:52', '2026-01-26 17:36:45');
INSERT INTO `p_category` VALUES ('47', '6', '万代', '2', '10', 'https://storage.moegirl.org.cn/moegirl/commons/4/44/Bandai_Namco_Holdings_Logo_%282022-%29_Type2.svg!/fw/99?v=20220403054958', '1', '2026-01-16 19:12:58', '2026-01-26 17:37:03');
INSERT INTO `p_category` VALUES ('48', '6', 'ANIPLEX ONLINE', '2', '9', 'https://storage.moegirl.org.cn/moegirl/commons/3/37/Aniplex_logo.svg!/fw/280?v=20170911041745', '1', '2026-01-16 19:12:58', '2026-01-26 17:37:33');
INSERT INTO `p_category` VALUES ('49', '6', '角川', '2', '8', 'https://storage.moegirl.org.cn/moegirl/commons/2/2d/%E5%A4%A9%E9%97%BB%E8%A7%92%E5%B7%9D_Logo.svg!/fw/280?v=20200423114928', '1', '2026-01-16 19:12:58', '2026-01-26 17:37:57');
INSERT INTO `p_category` VALUES ('50', '6', 'GSC', '2', '7', 'https://storage.moegirl.org.cn/moegirl/commons/f/fd/Good_Smile_Company_Logo.svg!/fw/99?v=20240205034049', '1', '2026-01-16 19:12:58', '2026-01-26 17:38:14');
INSERT INTO `p_category` VALUES ('51', '6', 'ALTER', '2', '6', 'https://storage.moegirl.org.cn/moegirl/commons/8/87/Alter_logo.png!/fw/99?v=20220729203917', '1', '2026-01-16 19:12:58', '2026-01-26 17:38:32');
INSERT INTO `p_category` VALUES ('52', '6', '哔哩哔哩', '2', '5', 'https://storage.moegirl.org.cn/moegirl/commons/1/10/Bilitv.png!/fw/50?v=20210625073607', '1', '2026-01-16 19:12:58', '2026-01-26 17:38:50');
INSERT INTO `p_category` VALUES ('53', '6', 'Phat!', '2', '5', 'https://rfx.hpoi.net/gk/cover/n/2019/03/53d3eb51451f4c7b931d9a1b80b68c7b.png?date=1733974099091', '1', '2026-01-17 14:50:41', '2026-01-26 17:40:02');
INSERT INTO `p_category` VALUES ('54', '5', '刀剑神域', '2', '9', 'https://storage.moegirl.org.cn/moegirl/commons/b/bc/SAO_anime_logo.png!/fw/99?v=20190331022823', '1', '2026-01-17 14:51:21', '2026-01-26 17:42:12');
INSERT INTO `p_category` VALUES ('63', '5', '鬼灭之刃', '2', '1', 'https://storage.moegirl.org.cn/moegirl/commons/b/b3/Kimetsu_Logo.png!/fw/70?v=20190608095333', '1', '2026-01-17 15:45:56', '2026-01-26 17:42:14');

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
-- Records of p_comment_like
-- ----------------------------
INSERT INTO `p_comment_like` VALUES ('6', '9', '1999834598331097089', '2026-03-17 11:39:27');
INSERT INTO `p_comment_like` VALUES ('7', '9', '1999847752930926594', '2026-03-17 11:39:50');
INSERT INTO `p_comment_like` VALUES ('8', '17', '1999834598331097089', '2026-03-18 15:50:07');
INSERT INTO `p_comment_like` VALUES ('9', '16', '1999834598331097089', '2026-03-18 17:45:32');
INSERT INTO `p_comment_like` VALUES ('22', '3', '1999834598331097089', '2026-03-19 11:45:12');
INSERT INTO `p_comment_like` VALUES ('23', '2', '1999834598331097089', '2026-03-19 11:45:14');
INSERT INTO `p_comment_like` VALUES ('24', '1', '1999834598331097089', '2026-03-19 11:45:15');
INSERT INTO `p_comment_like` VALUES ('25', '6', '1999834598331097089', '2026-03-19 11:52:07');
INSERT INTO `p_comment_like` VALUES ('26', '21', '1999834598331097089', '2026-03-19 12:20:03');

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
-- Records of p_community_comment
-- ----------------------------
INSERT INTO `p_community_comment` VALUES ('1', '4', '1999834598331097089', '0', '我来测试', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '1', '0', '2026-03-18 16:19:53', '2026-03-19 13:44:28');
INSERT INTO `p_community_comment` VALUES ('2', '4', '1999834598331097089', '0', '测试来来来来来', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '1', '0', '2026-03-18 16:33:29', '2026-03-19 13:44:30');
INSERT INTO `p_community_comment` VALUES ('3', '4', '1999834598331097089', '0', '测试111', 'http://localhost:8080/communityPostComment/0044cc6ef1c946abb09ca656b4503e0c.jpg', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '1', '0', '2026-03-18 17:32:45', '2026-03-18 17:32:45');
INSERT INTO `p_community_comment` VALUES ('6', '4', '1999834598331097089', '0', '1111111111', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '3', '0', '2026-03-18 18:16:30', '2026-03-19 14:04:55');
INSERT INTO `p_community_comment` VALUES ('7', '4', '1999834598331097089', '6', '11111', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '0', '2026-03-18 18:16:37', '2026-03-18 18:16:37');
INSERT INTO `p_community_comment` VALUES ('8', '4', '1999834598331097089', '6', '2222', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '0', '2026-03-19 11:10:02', '2026-03-19 11:10:02');
INSERT INTO `p_community_comment` VALUES ('9', '4', '1999834598331097089', '6', 'aaaaa', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '0', '2026-03-19 11:37:10', '2026-03-19 11:37:10');
INSERT INTO `p_community_comment` VALUES ('10', '4', '1999834598331097089', '0', 'zzzzz', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '1', '0', '2026-03-19 14:03:18', '2026-03-19 14:04:51');
INSERT INTO `p_community_comment` VALUES ('11', '4', '1999847752930926594', '0', '出问题了', '', '雾雨魔理沙', 'http://localhost:8080/avatar/4ed318398cec47c297b423f1b84f4a42.jpg', '1', '0', '2026-03-19 14:04:19', '2026-03-19 14:04:46');
INSERT INTO `p_community_comment` VALUES ('12', '4', '1999834598331097089', '0', 'xianzai重复吗', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '0', '2026-03-19 14:11:44', '2026-03-19 14:11:44');
INSERT INTO `p_community_comment` VALUES ('13', '5', '1999834598331097089', '0', '测试一下啊啊啊', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '2', '0', '2026-03-19 14:17:16', '2026-03-19 14:31:47');
INSERT INTO `p_community_comment` VALUES ('14', '5', '1999847752930926594', '0', '我也来\n啊啊啊啊', '', '雾雨魔理沙', 'http://localhost:8080/avatar/4ed318398cec47c297b423f1b84f4a42.jpg', '1', '1', '2026-03-19 14:17:51', '2026-03-19 14:18:53');
INSERT INTO `p_community_comment` VALUES ('15', '5', '1999847752930926594', '14', '你好呀', '', '雾雨魔理沙', 'http://localhost:8080/avatar/4ed318398cec47c297b423f1b84f4a42.jpg', '0', '0', '2026-03-19 14:18:00', '2026-03-19 14:18:00');
INSERT INTO `p_community_comment` VALUES ('16', '5', '1999847752930926594', '13', '你也好', '', '雾雨魔理沙', 'http://localhost:8080/avatar/4ed318398cec47c297b423f1b84f4a42.jpg', '0', '0', '2026-03-19 14:18:06', '2026-03-19 14:18:06');
INSERT INTO `p_community_comment` VALUES ('17', '5', '1999847752930926594', '0', '????', '', '雾雨魔理沙', 'http://localhost:8080/avatar/4ed318398cec47c297b423f1b84f4a42.jpg', '3', '0', '2026-03-19 14:19:15', '2026-03-19 14:31:46');
INSERT INTO `p_community_comment` VALUES ('18', '5', '1999834598331097089', '0', '你是谁?', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '1', '0', '2026-03-19 14:31:12', '2026-03-19 14:31:45');
INSERT INTO `p_community_comment` VALUES ('19', '5', '2000219241932894209', '0', '帅哥来了', 'http://localhost:8080/communityPostComment/8111c0cab791428fa9ab4845e1d07ea2.jpg', 'gggg', 'http://localhost:8080/avatar/34facd0d70434469ae9386b380e276fe.jpg', '0', '0', '2026-03-19 14:31:59', '2026-03-19 14:37:55');
INSERT INTO `p_community_comment` VALUES ('20', '5', '2000219241932894209', '0', 'aaaa', '', 'gggg', 'http://localhost:8080/avatar/34facd0d70434469ae9386b380e276fe.jpg', '0', '0', '2026-03-19 14:37:44', '2026-03-19 14:37:44');
INSERT INTO `p_community_comment` VALUES ('21', '6', '2000219241932894209', '0', 'aaaaa', '', 'gggg', 'http://localhost:8080/avatar/34facd0d70434469ae9386b380e276fe.jpg', '1', '0', '2026-03-19 14:38:32', '2026-03-19 14:38:40');
INSERT INTO `p_community_comment` VALUES ('22', '6', '2000219241932894209', '0', '我说ssss', '', 'gggg', 'http://localhost:8080/avatar/34facd0d70434469ae9386b380e276fe.jpg', '1', '1', '2026-03-19 14:38:37', '2026-03-19 14:38:46');
INSERT INTO `p_community_comment` VALUES ('23', '6', '2000219241932894209', '21', '阿哲', '', 'gggg', 'http://localhost:8080/avatar/34facd0d70434469ae9386b380e276fe.jpg', '0', '0', '2026-03-19 14:49:16', '2026-03-19 14:49:16');
INSERT INTO `p_community_comment` VALUES ('24', '6', '2000219241932894209', '21', '兄弟可以啊', '', 'gggg', 'http://localhost:8080/avatar/34facd0d70434469ae9386b380e276fe.jpg', '0', '0', '2026-03-19 14:52:15', '2026-03-19 14:52:15');
INSERT INTO `p_community_comment` VALUES ('25', '7', '1999847752930926594', '0', '你这家伙', 'http://localhost:8080/communityPostComment/3a26528ab33a4489843c6808dfc478d4.gif', '雾雨魔理沙', 'http://localhost:8080/avatar/4ed318398cec47c297b423f1b84f4a42.jpg', '1', '0', '2026-03-20 14:06:15', '2026-03-23 09:47:19');
INSERT INTO `p_community_comment` VALUES ('26', '7', '1999834598331097089', '0', '212112', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '1', '0', '2026-03-23 09:41:35', '2026-03-23 09:47:20');
INSERT INTO `p_community_comment` VALUES ('27', '7', '1999834598331097089', '26', '1111', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '0', '2026-03-23 09:47:12', '2026-03-23 09:47:12');
INSERT INTO `p_community_comment` VALUES ('28', '7', '1999834598331097089', '25', '111', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '0', '2026-03-23 09:47:18', '2026-03-23 09:47:18');
INSERT INTO `p_community_comment` VALUES ('29', '7', '1999834598331097089', '0', 'xixi', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '0', '2026-03-23 10:07:15', '2026-03-23 10:07:15');
INSERT INTO `p_community_comment` VALUES ('30', '7', '1999834598331097089', '0', 'ahha', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '1', '2026-03-23 10:07:34', '2026-03-23 10:27:00');
INSERT INTO `p_community_comment` VALUES ('31', '7', '1999834598331097089', '30', '啊啊啊啊啊', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '1', '2026-03-23 10:09:48', '2026-03-23 10:27:00');
INSERT INTO `p_community_comment` VALUES ('32', '7', '1999834598331097089', '30', '哈?', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '1', '2026-03-23 10:10:12', '2026-03-23 10:27:00');
INSERT INTO `p_community_comment` VALUES ('33', '7', '1999834598331097089', '0', '啊啊啊啊', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '1', '2026-03-23 10:13:06', '2026-03-23 10:13:38');
INSERT INTO `p_community_comment` VALUES ('34', '7', '1999834598331097089', '33', '测试一下啊', '', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '0', '0', '2026-03-23 10:13:17', '2026-03-23 10:13:17');
INSERT INTO `p_community_comment` VALUES ('35', '8', '1999834598331097089', '0', 'aaaaa', 'http://localhost:8080/communityPostComment/5b3cef83404d49749e4dd04299b2f36f.jpg', '管理员1', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '1', '0', '2026-03-24 18:12:27', '2026-03-24 18:12:31');

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
-- Records of p_community_comment_like
-- ----------------------------
INSERT INTO `p_community_comment_like` VALUES ('6', '6', '1999834598331097089', '2026-03-19 14:03:06', '2026-03-19 14:03:06');
INSERT INTO `p_community_comment_like` VALUES ('7', '11', '1999847752930926594', '2026-03-19 14:04:47', '2026-03-19 14:04:47');
INSERT INTO `p_community_comment_like` VALUES ('8', '10', '1999847752930926594', '2026-03-19 14:04:52', '2026-03-19 14:04:52');
INSERT INTO `p_community_comment_like` VALUES ('9', '6', '1999847752930926594', '2026-03-19 14:04:56', '2026-03-19 14:04:56');
INSERT INTO `p_community_comment_like` VALUES ('10', '13', '1999834598331097089', '2026-03-19 14:17:19', '2026-03-19 14:17:19');
INSERT INTO `p_community_comment_like` VALUES ('11', '14', '1999847752930926594', '2026-03-19 14:18:13', '2026-03-19 14:18:13');
INSERT INTO `p_community_comment_like` VALUES ('12', '17', '1999847752930926594', '2026-03-19 14:21:06', '2026-03-19 14:21:06');
INSERT INTO `p_community_comment_like` VALUES ('13', '17', '1999834598331097089', '2026-03-19 14:31:05', '2026-03-19 14:31:05');
INSERT INTO `p_community_comment_like` VALUES ('15', '18', '2000219241932894209', '2026-03-19 14:31:45', '2026-03-19 14:31:45');
INSERT INTO `p_community_comment_like` VALUES ('16', '17', '2000219241932894209', '2026-03-19 14:31:46', '2026-03-19 14:31:46');
INSERT INTO `p_community_comment_like` VALUES ('17', '13', '2000219241932894209', '2026-03-19 14:31:47', '2026-03-19 14:31:47');
INSERT INTO `p_community_comment_like` VALUES ('18', '22', '2000219241932894209', '2026-03-19 14:38:40', '2026-03-19 14:38:40');
INSERT INTO `p_community_comment_like` VALUES ('19', '21', '2000219241932894209', '2026-03-19 14:38:41', '2026-03-19 14:38:41');
INSERT INTO `p_community_comment_like` VALUES ('20', '25', '1999834598331097089', '2026-03-23 09:47:19', '2026-03-23 09:47:19');
INSERT INTO `p_community_comment_like` VALUES ('21', '26', '1999834598331097089', '2026-03-23 09:47:20', '2026-03-23 09:47:20');
INSERT INTO `p_community_comment_like` VALUES ('22', '35', '1999834598331097089', '2026-03-24 18:12:31', '2026-03-24 18:12:31');

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
-- Records of p_community_post
-- ----------------------------
INSERT INTO `p_community_post` VALUES ('1', '1999834598331097089', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '管理员1', 'c测试帖子', '12312312313213', 'http://localhost:8080/communityPost/0d4bd1b73fef4918b10fd6813448b949.jpg', '0', '0', '1', '1', '2026-03-18 10:49:10', '2026-03-18 18:06:23');
INSERT INTO `p_community_post` VALUES ('2', '1999834598331097089', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '管理员1', '⑨神', 'BAKABKABAKBAKA', 'http://localhost:8080/communityPost/098a5f1bf60542bbaf618a0bd0ce31ec.jpg,http://localhost:8080/communityPost/6255ec2d5547414a81d5ebfb523bc60d.jpg', '0', '0', '2', '1', '2026-03-18 11:12:28', '2026-03-18 14:51:30');
INSERT INTO `p_community_post` VALUES ('3', '1999834598331097089', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '管理员1', 'aaaaaa22222', 'test2222', 'http://localhost:8080/communityPost/fdd56e6cbf3c441a8109e5136e384802.jpg', '0', '0', '14', '1', '2026-03-18 11:37:10', '2026-03-19 14:03:58');
INSERT INTO `p_community_post` VALUES ('4', '1999847752930926594', 'http://localhost:8080/avatar/4ed318398cec47c297b423f1b84f4a42.jpg', '雾雨魔理沙', '魔理沙来咯', 'Master⭐Spark!', 'http://localhost:8080/communityPost/f5a15585945f41b39ae69d0be61c9b1f.png', '2', '10', '141', '1', '2026-03-18 11:49:14', '2026-03-23 10:03:33');
INSERT INTO `p_community_post` VALUES ('5', '1999834598331097089', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '管理员1', '测试新帖子', '12313123', 'http://localhost:8080/communityPost/1867ef141e0948e6a4d70cde06b5ba2f.jpg,http://localhost:8080/communityPost/6c2347e4273448d28b3b2a6b2adb7223.jpg', '2', '7', '17', '1', '2026-03-19 14:16:58', '2026-03-23 10:03:33');
INSERT INTO `p_community_post` VALUES ('6', '2000219241932894209', 'http://localhost:8080/avatar/34facd0d70434469ae9386b380e276fe.jpg', 'gggg', 'ceshi2222', '1321313', 'http://localhost:8080/communityPost/90112bfde33341a89a58b7da43b06658.jpg', '1', '3', '4', '1', '2026-03-19 14:38:25', '2026-03-23 10:03:33');
INSERT INTO `p_community_post` VALUES ('7', '1999834598331097089', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', 'admin', '我是apex高手', '啊啊啊啊啊啊啊啊啊啊再快点再快点', 'http://localhost:8080/communityPost/e95974aee199406eb7acb96a603a0378.gif', '2', '5', '36', '1', '2026-03-20 13:59:13', '2026-03-23 10:26:59');
INSERT INTO `p_community_post` VALUES ('8', '1999834598331097089', 'http://localhost:8080/avatar/b3d235174dfd43daa5e6a5e0552a071c.png', '管理员1', 'hahahha', '11111', 'http://localhost:8080/communityPost/228ad4c2467844f4bf408db5af147dad.jpg', '1', '1', '4', '1', '2026-03-24 18:12:15', '2026-03-26 11:18:57');

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
-- Records of p_community_post_like
-- ----------------------------
INSERT INTO `p_community_post_like` VALUES ('10', '4', '1999834598331097089', '0', '2026-03-18 15:07:19', '2026-03-18 15:07:19');
INSERT INTO `p_community_post_like` VALUES ('11', '4', '1999847752930926594', '0', '2026-03-19 14:04:13', '2026-03-19 14:04:13');
INSERT INTO `p_community_post_like` VALUES ('12', '5', '1999834598331097089', '0', '2026-03-19 14:17:20', '2026-03-19 14:17:20');
INSERT INTO `p_community_post_like` VALUES ('13', '5', '1999847752930926594', '0', '2026-03-19 14:17:44', '2026-03-19 14:17:44');
INSERT INTO `p_community_post_like` VALUES ('14', '6', '2000219241932894209', '0', '2026-03-19 14:38:42', '2026-03-19 14:38:42');
INSERT INTO `p_community_post_like` VALUES ('15', '7', '1999847752930926594', '0', '2026-03-20 14:06:16', '2026-03-20 14:06:16');
INSERT INTO `p_community_post_like` VALUES ('16', '7', '1999834598331097089', '0', '2026-03-23 09:47:22', '2026-03-23 09:47:22');
INSERT INTO `p_community_post_like` VALUES ('17', '8', '1999834598331097089', '0', '2026-03-24 18:12:20', '2026-03-24 18:12:20');

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
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';

-- ----------------------------
-- Records of p_order
-- ----------------------------
INSERT INTO `p_order` VALUES ('1', '20260211151959101873', '1999834598331097089', '2275.00', '2275.00', '0.00', '0.00', '1', '1', '3', '张科', '13544164650', '广东省深圳市福田区一号线', '顺丰速运', 'SF1232313313', '', '2026-02-11 15:20:02', '2026-02-11 08:07:39', '2026-02-11 08:07:57', null, '2026-02-11 15:19:59', '2026-02-11 08:07:57', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('2', '20260211152022797197', '1999834598331097089', '1125.00', '1125.00', '0.00', '0.00', '0', '0', '3', '张科', '13544164650', '广东省深圳市福田区一号线', '', '', '', null, null, '2026-02-11 07:43:37', '2026-02-11 15:21:04', '2026-02-11 15:20:23', '2026-02-11 08:07:03', '1', '0', '0', null);
INSERT INTO `p_order` VALUES ('3', '20260211153834905134', '1999834598331097089', '2225.00', '2225.00', '0.00', '0.00', '0', '4', '4', '张科', '13544164650', '广东省深圳市福田区一号线', '', '', '', null, null, null, '2026-02-11 15:38:47', '2026-02-11 15:38:35', '2026-02-11 07:43:32', '1', '0', '0', null);
INSERT INTO `p_order` VALUES ('4', '20260211153920588925', '1999834598331097089', '2250.00', '2250.00', '0.00', '0.00', '0', '4', '4', '张科', '13544164650', '广东省深圳市福田区一号线', '', '', '', null, null, null, '2026-02-11 15:39:44', '2026-02-11 15:39:21', '2026-02-11 07:43:30', '1', '0', '0', null);
INSERT INTO `p_order` VALUES ('5', '20260211154228824264', '1999834598331097089', '1125.00', '1125.00', '0.00', '0.00', '0', '4', '4', '张科', '13544164650', '广东省深圳市福田区一号线', '', '', '', null, null, null, '2026-02-11 15:42:41', '2026-02-11 15:42:28', '2026-02-11 07:43:29', '1', '0', '0', null);
INSERT INTO `p_order` VALUES ('6', '20260211154305756029', '1999834598331097089', '2275.00', '2275.00', '0.00', '0.00', '0', '4', '4', '张科', '13544164650', '广东省深圳市福田区一号线', '', '', '', null, null, null, '2026-02-11 15:43:17', '2026-02-11 15:43:05', '2026-02-11 07:43:27', '1', '0', '0', null);
INSERT INTO `p_order` VALUES ('7', '20260211160818383484', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '1', '1', '1', '张科', '13544164650', '广东省深圳市福田区一号线', '', '', '', '2026-02-11 16:08:37', null, null, null, '2026-02-11 16:08:18', '2026-02-11 16:08:37', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('8', '20260211161952193540', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市福田区一号线', '', '', '', '2026-02-11 16:19:56', null, null, null, '2026-02-11 16:19:53', '2026-02-11 16:19:56', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('9', '20260211162426577802', '1999834598331097089', '50.00', '50.00', '0.00', '0.00', '1', '1', '2', '张科', '13544164650', '广东省深圳市福田区一号线', '韵达快递', 'YD1231323132', '', '2026-02-11 16:24:30', '2026-02-11 08:28:14', null, null, '2026-02-11 16:24:27', '2026-02-11 08:28:14', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('10', '20260212145036228165', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-02-12 14:50:41', null, null, null, '2026-02-12 14:50:37', '2026-02-12 14:50:41', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('11', '20260213171338205476', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-02-13 17:13:41', null, null, null, '2026-02-13 17:13:38', '2026-02-13 17:13:41', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('12', '20260213171402610869', '1999834598331097089', '50.00', '50.00', '0.00', '0.00', '0', '0', '4', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', null, null, null, '2026-02-13 09:14:23', '2026-02-13 17:14:02', '2026-02-13 09:14:23', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('13', '20260214203530192363', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '1', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-02-14 20:35:33', null, null, null, '2026-02-14 20:35:30', '2026-02-14 20:35:33', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('14', '20260214204541427105', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '1', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-02-14 20:45:47', null, null, null, '2026-02-14 20:45:41', '2026-02-14 20:45:47', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('15', '20260301130458310499', '1999834598331097089', '316.00', '316.00', '0.00', '0.00', '1', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-01 13:05:02', null, null, null, '2026-03-01 13:04:58', '2026-03-01 13:05:02', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('16', '20260301135202143217', '1999834598331097089', '255.00', '255.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-01 13:52:06', null, null, null, '2026-03-01 13:52:03', '2026-03-01 13:52:06', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('17', '20260301141112219036', '1999834598331097089', '158.00', '158.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-01 14:11:16', null, null, null, '2026-03-01 14:11:13', '2026-03-01 14:11:16', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('18', '20260301141308202131', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-01 14:13:12', null, null, null, '2026-03-01 14:13:09', '2026-03-01 14:13:12', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('19', '20260315111451762838', '1999834598331097089', '190.00', '190.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-15 11:14:55', null, null, null, '2026-03-15 11:14:52', '2026-03-15 11:14:55', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('20', '20260315113311335424', '1999834598331097089', '158.00', '158.00', '0.00', '0.00', '1', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-15 11:33:15', null, null, null, '2026-03-15 11:33:12', '2026-03-15 11:33:15', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('21', '20260315113501224266', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '0', '4', '4', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', null, null, null, '2026-03-15 11:50:18', '2026-03-15 11:35:02', '2026-05-11 18:13:15', '1', '1', '1', null);
INSERT INTO `p_order` VALUES ('22', '20260315122732278546', '1999834598331097089', '158.00', '158.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-15 12:27:55', null, null, null, '2026-03-15 12:27:32', '2026-05-11 18:13:15', '0', '1', '1', null);
INSERT INTO `p_order` VALUES ('23', '20260315124410827614', '1999834598331097089', '95.00', '95.00', '0.00', '0.00', '1', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-15 12:44:14', null, null, null, '2026-03-15 12:44:11', '2026-05-11 18:13:15', '0', '1', '1', null);
INSERT INTO `p_order` VALUES ('24', '20260315124523200777', '1999834598331097089', '158.00', '158.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-15 12:45:27', null, null, null, '2026-03-15 12:45:24', '2026-05-11 18:13:15', '0', '1', '1', null);
INSERT INTO `p_order` VALUES ('25', '20260315181419204895', '1999834598331097089', '170.00', '170.00', '0.00', '0.00', '2', '1', '1', '', '', '', '', '', '', '2026-03-15 18:14:22', null, null, null, '2026-03-15 18:14:19', '2026-05-11 18:13:15', '0', '1', '1', null);
INSERT INTO `p_order` VALUES ('26', '20260315184929540124', '1999834598331097089', '2200.00', '2200.00', '0.00', '0.00', '2', '1', '1', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-03-15 18:49:43', null, null, null, '2026-03-15 18:49:30', '2026-03-15 18:49:43', '0', '0', '0', null);
INSERT INTO `p_order` VALUES ('27', '20260316094416629934', '1999834598331097089', '316.00', '316.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-03-16 09:44:19', null, null, null, '2026-03-16 09:44:16', '2026-05-11 18:13:15', '0', '1', '1', '3,2');
INSERT INTO `p_order` VALUES ('31', '20260316100739735181', '1999834598331097089', '170.00', '170.00', '0.00', '0.00', '2', '1', '1', '', '', '', '', '', '', '2026-03-16 10:07:42', null, null, null, '2026-03-16 10:07:39', '2026-05-11 18:13:15', '0', '1', '1', '3,2');
INSERT INTO `p_order` VALUES ('32', '20260320120221577836', '1999834598331097089', '1399.00', '1399.00', '0.00', '0.00', '2', '1', '1', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-03-20 12:02:49', null, null, null, '2026-03-20 12:02:21', '2026-03-20 12:02:49', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('33', '20260320120604666943', '1999834598331097089', '1399.00', '1399.00', '0.00', '0.00', '2', '1', '1', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '', '', '', '2026-03-20 12:06:10', null, null, null, '2026-03-20 12:06:05', '2026-03-20 12:06:10', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('34', '20260320120621509275', '1999834598331097089', '1399.00', '1399.00', '0.00', '0.00', '1', '1', '3', '张科', '13544164650', '广东省深圳市龙岗区布吉街道', '顺丰', 'SF12312312314', '', '2026-03-20 12:06:25', '2026-03-20 04:14:30', '2026-03-20 04:19:27', null, '2026-03-20 12:06:22', '2026-03-20 04:19:27', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('35', '20260320133043296543', '1999834598331097089', '316.00', '316.00', '0.00', '0.00', '1', '1', '4', '', '', '', '', '', '', '2026-03-20 13:30:46', null, null, '2026-03-24 03:33:06', '2026-03-20 13:30:43', '2026-05-11 18:13:15', '0', '1', '1', '4,3');
INSERT INTO `p_order` VALUES ('36', '20260320154101462072', '2034895749309763586', '1100.00', '1100.00', '0.00', '0.00', '1', '1', '3', 'zktest', '13544164359', '北京市北京市朝阳区bj', '顺丰快递', 'SF12312312314', '', '2026-03-20 15:41:04', '2026-03-20 07:42:59', '2026-03-20 07:46:00', null, '2026-03-20 15:41:01', '2026-03-20 07:46:00', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('37', '20260320154953231692', '2034895749309763586', '85.00', '85.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-03-20 15:49:56', null, null, null, '2026-03-20 15:49:54', '2026-05-11 18:13:15', '0', '1', '1', '6');
INSERT INTO `p_order` VALUES ('38', '20260324112329709787', '1999834598331097089', '5596.00', '5596.00', '0.00', '0.00', '2', '3', '4', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-03-24 11:23:32', null, null, '2026-03-24 03:58:55', '2026-03-24 11:23:30', '2026-03-24 03:58:55', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('39', '20260324112539126913', '1999834598331097089', '1399.00', '1399.00', '0.00', '0.00', '1', '1', '4', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-03-24 11:25:42', null, null, '2026-03-24 03:26:30', '2026-03-24 11:25:40', '2026-03-24 03:26:30', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('40', '20260324152142397856', '1999834598331097089', '300.00', '300.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-03-24 15:21:45', null, null, null, '2026-03-24 15:21:42', '2026-05-11 18:13:15', '0', '1', '1', '5,4');
INSERT INTO `p_order` VALUES ('41', '20260324152327510740', '1999834598331097089', '340.00', '340.00', '0.00', '0.00', '2', '1', '1', '', '', '', '', '', '', '2026-03-24 15:23:30', null, null, null, '2026-03-24 15:23:28', '2026-05-11 18:13:15', '0', '1', '1', '5,4,3,2');
INSERT INTO `p_order` VALUES ('42', '20260324172511318942', '1999834598331097089', '495.00', '495.00', '0.00', '0.00', '1', '1', '1', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-03-24 17:25:14', null, null, null, '2026-03-24 17:25:11', '2026-03-24 17:25:14', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('43', '20260324181148328037', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '2', '1', '2', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '顺丰快递', 'SF144324324234', '', '2026-03-24 18:11:51', '2026-03-24 10:17:32', null, null, '2026-03-24 18:11:49', '2026-03-24 10:17:32', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('44', '20260324181329761415', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '1', '3', '4', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-03-24 18:13:32', null, null, '2026-03-24 10:17:25', '2026-03-24 18:13:30', '2026-03-24 10:17:25', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('45', '20260327165218624185', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '1', '1', '1', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-03-27 16:52:29', null, null, null, '2026-03-27 16:52:19', '2026-03-27 16:52:29', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('46', '20260327170435941759', '1999834598331097089', '100.00', '100.00', '0.00', '0.00', '2', '1', '1', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-03-27 17:04:38', null, null, null, '2026-03-27 17:04:35', '2026-03-27 17:04:38', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('47', '20260327173213762171', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '2', '1', '1', '', '', '', '', '', '', '2026-03-27 17:32:17', null, null, null, '2026-03-27 17:32:14', '2026-05-11 18:13:15', '0', '1', '1', '5');
INSERT INTO `p_order` VALUES ('48', '20260509105950802017', '1999834598331097089', '1999.00', '1999.00', '0.00', '0.00', '2', '1', '1', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-05-09 10:59:53', null, null, null, '2026-05-09 10:59:51', '2026-05-09 10:59:53', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('49', '20260509110844103528', '1999834598331097089', '75.00', '75.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-05-09 11:08:47', null, null, null, '2026-05-09 11:08:45', '2026-05-11 18:13:15', '0', '1', '1', '5');
INSERT INTO `p_order` VALUES ('50', '20260509110947388450', '1999834598331097089', '75.00', '75.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-05-09 11:09:50', null, null, null, '2026-05-09 11:09:48', '2026-05-11 18:13:15', '0', '1', '1', '5');
INSERT INTO `p_order` VALUES ('51', '20260509120137789180', '1999834598331097089', '1100.00', '1100.00', '0.00', '0.00', '2', '1', '1', '彭于晏', '13534077008', '广东省深圳市罗湖区万象城', '', '', '', '2026-05-09 12:01:43', null, null, null, '2026-05-09 12:01:38', '2026-05-09 12:01:43', '0', '0', '0', '');
INSERT INTO `p_order` VALUES ('52', '20260509120156736820', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '1', '3', '4', '', '', '', '', '', '', '2026-05-09 12:02:05', null, null, '2026-05-11 10:19:01', '2026-05-09 12:01:56', '2026-05-11 10:19:01', '0', '1', '1', '5');
INSERT INTO `p_order` VALUES ('53', '20260512095054944235', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '2', '1', '1', '', '', '', '', '', '', '2026-05-12 09:50:57', null, null, null, '2026-05-12 09:50:54', '2026-05-12 09:50:57', '0', '1', '1', '4');
INSERT INTO `p_order` VALUES ('54', '20260512095113248268', '1999834598331097089', '316.00', '316.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-05-12 09:51:17', null, null, null, '2026-05-12 09:51:13', '2026-05-12 09:51:17', '0', '1', '1', '2,3');
INSERT INTO `p_order` VALUES ('55', '20260512095659910574', '1999834598331097089', '300.00', '300.00', '0.00', '0.00', '2', '1', '3', '', '', '', '', '', '', '2026-05-12 09:57:02', null, '2026-05-12 02:28:07', null, '2026-05-12 09:56:59', '2026-05-12 02:28:07', '0', '1', '1', '3,5');
INSERT INTO `p_order` VALUES ('56', '20260512095725278869', '1999834598331097089', '200.00', '200.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-05-12 09:57:28', null, null, null, '2026-05-12 09:57:26', '2026-05-12 09:57:28', '0', '1', '1', '2');
INSERT INTO `p_order` VALUES ('57', '20260512101500633141', '1999834598331097089', '95.00', '95.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-05-12 10:15:04', null, null, null, '2026-05-12 10:15:01', '2026-05-12 10:15:04', '0', '1', '1', '5');
INSERT INTO `p_order` VALUES ('58', '20260512101516727751', '1999834598331097089', '316.00', '316.00', '0.00', '0.00', '1', '1', '3', '', '', '', '', '', '', '2026-05-12 10:15:19', null, '2026-05-12 02:26:38', null, '2026-05-12 10:15:16', '2026-05-12 02:26:38', '0', '1', '1', '3,2');
INSERT INTO `p_order` VALUES ('59', '20260512103344957771', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '1', '1', '1', '', '', '', '', '', '', '2026-05-12 10:33:47', null, null, null, '2026-05-12 10:33:44', '2026-05-12 10:33:47', '0', '1', '1', '4');
INSERT INTO `p_order` VALUES ('60', '20260512103856918425', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '2', '1', '3', '', '', '', '', '', '', '2026-05-12 10:38:59', null, '2026-05-12 02:47:00', null, '2026-05-12 10:38:56', '2026-05-12 02:47:00', '0', '1', '1', '3');
INSERT INTO `p_order` VALUES ('61', '20260512110845937858', '1999834598331097089', '85.00', '85.00', '0.00', '0.00', '1', '1', '1', '', '13411568489', '', '', '', '', '2026-05-12 11:08:48', null, null, null, '2026-05-12 11:08:45', '2026-05-12 11:08:48', '0', '1', '1', '3');

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
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单项表（订单商品明细）';

-- ----------------------------
-- Records of p_order_item
-- ----------------------------
INSERT INTO `p_order_item` VALUES ('1', '1', '20260211151959101873', '2', null, null, '魔女之旅 周边吧唧', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '25.00', '3', '75.00', null, null, null, '2026-02-11 15:19:59', '2026-02-11 15:19:59', '0');
INSERT INTO `p_order_item` VALUES ('2', '1', '20260211151959101873', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '2', '2200.00', null, null, null, '2026-02-11 15:19:59', '2026-02-11 15:19:59', '0');
INSERT INTO `p_order_item` VALUES ('3', '2', '20260211152022797197', '2', null, null, '魔女之旅 周边吧唧', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '25.00', '1', '25.00', null, null, null, '2026-02-11 15:20:23', '2026-02-11 08:07:03', '1');
INSERT INTO `p_order_item` VALUES ('4', '2', '20260211152022797197', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-02-11 15:20:23', '2026-02-11 08:07:03', '1');
INSERT INTO `p_order_item` VALUES ('5', '3', '20260211153834905134', '2', null, null, '魔女之旅 周边吧唧', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '25.00', '1', '25.00', null, null, null, '2026-02-11 15:38:35', '2026-02-11 07:43:32', '1');
INSERT INTO `p_order_item` VALUES ('6', '3', '20260211153834905134', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '2', '2200.00', null, null, null, '2026-02-11 15:38:35', '2026-02-11 07:43:32', '1');
INSERT INTO `p_order_item` VALUES ('7', '4', '20260211153920588925', '2', null, null, '魔女之旅 周边吧唧', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '25.00', '2', '50.00', null, null, null, '2026-02-11 15:39:21', '2026-02-11 07:43:30', '1');
INSERT INTO `p_order_item` VALUES ('8', '4', '20260211153920588925', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '2', '2200.00', null, null, null, '2026-02-11 15:39:21', '2026-02-11 07:43:30', '1');
INSERT INTO `p_order_item` VALUES ('9', '5', '20260211154228824264', '2', null, null, '魔女之旅 周边吧唧', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '25.00', '1', '25.00', null, null, null, '2026-02-11 15:42:28', '2026-02-11 07:43:29', '1');
INSERT INTO `p_order_item` VALUES ('10', '5', '20260211154228824264', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-02-11 15:42:28', '2026-02-11 07:43:29', '1');
INSERT INTO `p_order_item` VALUES ('11', '6', '20260211154305756029', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '2', '2200.00', null, null, null, '2026-02-11 15:43:05', '2026-02-11 07:43:27', '1');
INSERT INTO `p_order_item` VALUES ('12', '6', '20260211154305756029', '2', null, null, '魔女之旅 周边吧唧', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '25.00', '3', '75.00', null, null, null, '2026-02-11 15:43:05', '2026-02-11 07:43:27', '1');
INSERT INTO `p_order_item` VALUES ('13', '7', '20260211160818383484', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-02-11 16:08:18', '2026-02-11 16:08:18', '0');
INSERT INTO `p_order_item` VALUES ('14', '8', '20260211161952193540', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-02-11 16:19:53', '2026-02-11 16:19:53', '0');
INSERT INTO `p_order_item` VALUES ('15', '9', '20260211162426577802', '2', null, null, '魔女之旅 周边吧唧', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '25.00', '2', '50.00', null, null, null, '2026-02-11 16:24:27', '2026-02-11 16:24:27', '0');
INSERT INTO `p_order_item` VALUES ('16', '10', '20260212145036228165', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-02-12 14:50:37', '2026-02-12 14:50:37', '0');
INSERT INTO `p_order_item` VALUES ('17', '11', '20260213171338205476', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-02-13 17:13:38', '2026-02-13 17:13:38', '0');
INSERT INTO `p_order_item` VALUES ('18', '12', '20260213171402610869', '2', null, null, '魔女之旅 周边吧唧', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '25.00', '2', '50.00', null, null, null, '2026-02-13 17:14:02', '2026-02-13 17:14:02', '0');
INSERT INTO `p_order_item` VALUES ('19', '13', '20260214203530192363', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', null, null, null, '2026-02-14 20:35:30', '2026-02-14 20:35:30', '0');
INSERT INTO `p_order_item` VALUES ('20', '14', '20260214204541427105', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', null, null, null, '2026-02-14 20:45:41', '2026-02-14 20:45:41', '0');
INSERT INTO `p_order_item` VALUES ('21', '15', '20260301130458310499', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '2', '316.00', null, null, null, '2026-03-01 13:04:59', '2026-03-01 13:04:59', '0');
INSERT INTO `p_order_item` VALUES ('22', '16', '20260301135202143217', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '3', '255.00', null, null, null, '2026-03-01 13:52:03', '2026-03-01 13:52:03', '0');
INSERT INTO `p_order_item` VALUES ('23', '17', '20260301141112219036', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '158.00', null, null, null, '2026-03-01 14:11:13', '2026-03-01 14:11:13', '0');
INSERT INTO `p_order_item` VALUES ('24', '18', '20260301141308202131', '1', null, null, '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-03-01 14:13:09', '2026-03-01 14:13:09', '0');
INSERT INTO `p_order_item` VALUES ('25', '19', '20260315111451762838', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '95.00', '2', '190.00', null, null, null, '2026-03-15 11:14:52', '2026-03-15 11:14:52', '0');
INSERT INTO `p_order_item` VALUES ('26', '20', '20260315113311335424', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '158.00', null, null, null, '2026-03-15 11:33:12', '2026-03-15 11:33:12', '0');
INSERT INTO `p_order_item` VALUES ('27', '21', '20260315113501224266', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', null, null, null, '2026-03-15 11:35:02', '2026-03-15 04:55:12', '1');
INSERT INTO `p_order_item` VALUES ('28', '22', '20260315122732278546', '4', null, null, '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '158.00', null, null, null, '2026-03-15 12:27:32', '2026-03-15 12:27:32', '0');
INSERT INTO `p_order_item` VALUES ('29', '23', '20260315124410827614', '4', '7', '普通票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '95.00', '1', '95.00', null, null, null, '2026-03-15 12:44:11', '2026-03-15 12:44:11', '0');
INSERT INTO `p_order_item` VALUES ('30', '24', '20260315124523200777', '4', '6', 'VIP票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '158.00', null, null, null, '2026-03-15 12:45:24', '2026-03-15 12:45:24', '0');
INSERT INTO `p_order_item` VALUES ('31', '25', '20260315181419204895', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '2', '170.00', '3', '胡歌', '441622********6645', '2026-03-15 18:14:19', '2026-03-15 18:14:19', '0');
INSERT INTO `p_order_item` VALUES ('32', '26', '20260315184929540124', '1', null, '', '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '2', '2200.00', null, null, null, '2026-03-15 18:49:30', '2026-03-15 18:49:30', '0');
INSERT INTO `p_order_item` VALUES ('33', '27', '20260316094416629934', '4', '6', 'VIP票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '2', '316.00', '3', '胡歌', '441622********6645', '2026-03-16 09:44:16', '2026-03-16 09:44:16', '0');
INSERT INTO `p_order_item` VALUES ('34', '31', '20260316100739735181', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '170.00', '3', '胡歌', '441622********6645', '2026-03-16 10:07:39', '2026-03-16 10:07:39', '0');
INSERT INTO `p_order_item` VALUES ('35', '31', '20260316100739735181', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '170.00', '2', '张科', '441622********2376', '2026-03-16 10:07:39', '2026-03-16 10:07:39', '0');
INSERT INTO `p_order_item` VALUES ('36', '32', '20260320120221577836', '5', null, '', '七龙珠 卡卡罗特 手办', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1399.00', '1', '1399.00', null, null, null, '2026-03-20 12:02:21', '2026-03-20 12:02:21', '0');
INSERT INTO `p_order_item` VALUES ('37', '33', '20260320120604666943', '5', null, '', '七龙珠 卡卡罗特 手办', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1399.00', '1', '1399.00', null, null, null, '2026-03-20 12:06:05', '2026-03-20 12:06:05', '0');
INSERT INTO `p_order_item` VALUES ('38', '34', '20260320120621509275', '5', null, '', '七龙珠 卡卡罗特 手办', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1399.00', '1', '1399.00', null, null, null, '2026-03-20 12:06:22', '2026-03-20 12:06:22', '0');
INSERT INTO `p_order_item` VALUES ('39', '35', '20260320133043296543', '4', '6', 'VIP票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '316.00', '4', '真彭于晏', '442534********2356', '2026-03-20 13:30:43', '2026-03-20 13:30:43', '0');
INSERT INTO `p_order_item` VALUES ('40', '35', '20260320133043296543', '4', '6', 'VIP票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '316.00', '3', '胡歌', '441622********6645', '2026-03-20 13:30:43', '2026-03-20 13:30:43', '0');
INSERT INTO `p_order_item` VALUES ('41', '36', '20260320154101462072', '1', null, '', '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-03-20 15:41:01', '2026-03-20 15:41:01', '0');
INSERT INTO `p_order_item` VALUES ('42', '37', '20260320154953231692', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', '6', 'zktest', '442568********3564', '2026-03-20 15:49:54', '2026-03-20 15:49:54', '0');
INSERT INTO `p_order_item` VALUES ('43', '38', '20260324112329709787', '5', null, '', '七龙珠 卡卡罗特 手办', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1399.00', '1', '5596.00', null, null, null, '2026-03-24 11:23:30', '2026-03-24 11:23:30', '0');
INSERT INTO `p_order_item` VALUES ('44', '38', '20260324112329709787', '5', null, '', '七龙珠 卡卡罗特 手办', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1399.00', '1', '5596.00', null, null, null, '2026-03-24 11:23:30', '2026-03-24 11:23:30', '0');
INSERT INTO `p_order_item` VALUES ('45', '38', '20260324112329709787', '5', null, '', '七龙珠 卡卡罗特 手办', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1399.00', '1', '5596.00', null, null, null, '2026-03-24 11:23:30', '2026-03-24 11:23:30', '0');
INSERT INTO `p_order_item` VALUES ('46', '38', '20260324112329709787', '5', null, '', '七龙珠 卡卡罗特 手办', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1399.00', '1', '5596.00', null, null, null, '2026-03-24 11:23:30', '2026-03-24 11:23:30', '0');
INSERT INTO `p_order_item` VALUES ('47', '39', '20260324112539126913', '5', null, '', '七龙珠 卡卡罗特 手办', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1399.00', '1', '1399.00', null, null, null, '2026-03-24 11:25:40', '2026-03-24 11:25:40', '0');
INSERT INTO `p_order_item` VALUES ('48', '40', '20260324152142397856', '12', '14', 'VIP票', '深圳会展中心 漫展', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '150.00', '1', '300.00', '5', '纳兹', '442516********1648', '2026-03-24 15:21:42', '2026-03-24 15:21:42', '0');
INSERT INTO `p_order_item` VALUES ('49', '40', '20260324152142397856', '12', '14', 'VIP票', '深圳会展中心 漫展', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '150.00', '1', '300.00', '4', '真彭于晏', '442534********2356', '2026-03-24 15:21:42', '2026-03-24 15:21:42', '0');
INSERT INTO `p_order_item` VALUES ('50', '41', '20260324152327510740', '12', '15', '普通票', '深圳会展中心 漫展', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '85.00', '1', '340.00', '5', '纳兹', '442516********1648', '2026-03-24 15:23:28', '2026-03-24 15:23:28', '0');
INSERT INTO `p_order_item` VALUES ('51', '41', '20260324152327510740', '12', '15', '普通票', '深圳会展中心 漫展', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '85.00', '1', '340.00', '4', '真彭于晏', '442534********2356', '2026-03-24 15:23:28', '2026-03-24 15:23:28', '0');
INSERT INTO `p_order_item` VALUES ('52', '41', '20260324152327510740', '12', '15', '普通票', '深圳会展中心 漫展', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '85.00', '1', '340.00', '3', '胡歌', '441622********6645', '2026-03-24 15:23:28', '2026-03-24 15:23:28', '0');
INSERT INTO `p_order_item` VALUES ('53', '41', '20260324152327510740', '12', '15', '普通票', '深圳会展中心 漫展', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '85.00', '1', '340.00', '2', '张科', '441622********2376', '2026-03-24 15:23:28', '2026-03-24 15:23:28', '0');
INSERT INTO `p_order_item` VALUES ('54', '42', '20260324172511318942', '7', null, '', '游戏人生  白 手办', 'http://localhost:8080/upload/57c9c9a9-b1ce-48f6-9ab9-3a7dd8894783.jpg', '99.00', '1', '495.00', null, null, null, '2026-03-24 17:25:11', '2026-03-24 17:25:11', '0');
INSERT INTO `p_order_item` VALUES ('55', '42', '20260324172511318942', '7', null, '', '游戏人生  白 手办', 'http://localhost:8080/upload/57c9c9a9-b1ce-48f6-9ab9-3a7dd8894783.jpg', '99.00', '1', '495.00', null, null, null, '2026-03-24 17:25:11', '2026-03-24 17:25:11', '0');
INSERT INTO `p_order_item` VALUES ('56', '42', '20260324172511318942', '7', null, '', '游戏人生  白 手办', 'http://localhost:8080/upload/57c9c9a9-b1ce-48f6-9ab9-3a7dd8894783.jpg', '99.00', '1', '495.00', null, null, null, '2026-03-24 17:25:11', '2026-03-24 17:25:11', '0');
INSERT INTO `p_order_item` VALUES ('57', '42', '20260324172511318942', '7', null, '', '游戏人生  白 手办', 'http://localhost:8080/upload/57c9c9a9-b1ce-48f6-9ab9-3a7dd8894783.jpg', '99.00', '1', '495.00', null, null, null, '2026-03-24 17:25:11', '2026-03-24 17:25:11', '0');
INSERT INTO `p_order_item` VALUES ('58', '42', '20260324172511318942', '7', null, '', '游戏人生  白 手办', 'http://localhost:8080/upload/57c9c9a9-b1ce-48f6-9ab9-3a7dd8894783.jpg', '99.00', '1', '495.00', null, null, null, '2026-03-24 17:25:11', '2026-03-24 17:25:11', '0');
INSERT INTO `p_order_item` VALUES ('59', '43', '20260324181148328037', '1', null, '', '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-03-24 18:11:49', '2026-03-24 18:11:49', '0');
INSERT INTO `p_order_item` VALUES ('60', '44', '20260324181329761415', '1', null, '', '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-03-24 18:13:30', '2026-03-24 18:13:30', '0');
INSERT INTO `p_order_item` VALUES ('61', '45', '20260327165218624185', '1', null, '', '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-03-27 16:52:19', '2026-03-27 16:52:19', '0');
INSERT INTO `p_order_item` VALUES ('62', '46', '20260327170435941759', '13', null, '', '哈哈哈哈测试', 'http://localhost:8080/upload/47239fa0-0f6a-476f-a712-a1a21ffe1fcb.jpg', '100.00', '1', '100.00', null, null, null, '2026-03-27 17:04:35', '2026-03-27 17:04:35', '0');
INSERT INTO `p_order_item` VALUES ('63', '47', '20260327173213762171', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', '5', '纳兹', '442516********1648', '2026-03-27 17:32:14', '2026-03-27 17:32:14', '0');
INSERT INTO `p_order_item` VALUES ('64', '48', '20260509105950802017', '15', null, '', '伊蕾娜新手办', 'http://localhost:8080/upload/020f574a-3e61-4f23-b03c-9caec4b7dfce.jpg', '1999.00', '1', '1999.00', null, null, null, '2026-05-09 10:59:51', '2026-05-09 10:59:51', '0');
INSERT INTO `p_order_item` VALUES ('65', '49', '20260509110844103528', '14', '17', '早鸟票', '深圳会展中心漫展', 'http://localhost:8080/upload/a4fef504-f7ba-4c5c-98cd-c207f7666077.jpg', '75.00', '1', '75.00', '5', '纳兹', '442516********1648', '2026-05-09 11:08:45', '2026-05-09 11:08:45', '0');
INSERT INTO `p_order_item` VALUES ('66', '50', '20260509110947388450', '14', '17', '早鸟票', '深圳会展中心漫展', 'http://localhost:8080/upload/a4fef504-f7ba-4c5c-98cd-c207f7666077.jpg', '75.00', '1', '75.00', '5', '纳兹', '442516********1648', '2026-05-09 11:09:48', '2026-05-09 11:09:48', '0');
INSERT INTO `p_order_item` VALUES ('67', '51', '20260509120137789180', '1', null, '', '海贼王 路飞 五档尼卡', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1100.00', '1', '1100.00', null, null, null, '2026-05-09 12:01:38', '2026-05-09 12:01:38', '0');
INSERT INTO `p_order_item` VALUES ('68', '52', '20260509120156736820', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', '5', '纳兹', '442516********1648', '2026-05-09 12:01:56', '2026-05-09 12:01:56', '0');
INSERT INTO `p_order_item` VALUES ('69', '53', '20260512095054944235', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', '4', '真彭于晏', '442534********2356', '2026-05-12 09:50:54', '2026-05-12 09:50:54', '0');
INSERT INTO `p_order_item` VALUES ('70', '54', '20260512095113248268', '4', '6', 'VIP票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '316.00', '2', '张科', '441622********2376', '2026-05-12 09:51:14', '2026-05-12 09:51:14', '0');
INSERT INTO `p_order_item` VALUES ('71', '54', '20260512095113248268', '4', '6', 'VIP票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '316.00', '3', '胡歌', '441622********6645', '2026-05-12 09:51:14', '2026-05-12 09:51:14', '0');
INSERT INTO `p_order_item` VALUES ('72', '55', '20260512095659910574', '12', '14', 'VIP票', '深圳会展中心 漫展', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '150.00', '1', '300.00', '3', '胡歌', '441622********6645', '2026-05-12 09:56:59', '2026-05-12 09:56:59', '0');
INSERT INTO `p_order_item` VALUES ('73', '55', '20260512095659910574', '12', '14', 'VIP票', '深圳会展中心 漫展', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '150.00', '1', '300.00', '5', '纳兹', '442516********1648', '2026-05-12 09:56:59', '2026-05-12 09:56:59', '0');
INSERT INTO `p_order_item` VALUES ('74', '56', '20260512095725278869', '10', '12', 'SVIP票', '测试3', 'http://localhost:8080/upload/9c6530ca-9142-4e9d-84b9-fe4544bd7854.jpg', '200.00', '1', '200.00', '2', '张科', '441622********2376', '2026-05-12 09:57:26', '2026-05-12 09:57:26', '0');
INSERT INTO `p_order_item` VALUES ('75', '57', '20260512101500633141', '4', '7', '普通票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '95.00', '1', '95.00', '5', '纳兹', '442516********1648', '2026-05-12 10:15:01', '2026-05-12 10:15:01', '0');
INSERT INTO `p_order_item` VALUES ('76', '58', '20260512101516727751', '4', '6', 'VIP票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '316.00', '3', '胡歌', '441622********6645', '2026-05-12 10:15:16', '2026-05-12 10:15:16', '0');
INSERT INTO `p_order_item` VALUES ('77', '58', '20260512101516727751', '4', '6', 'VIP票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '158.00', '1', '316.00', '2', '张科', '441622********2376', '2026-05-12 10:15:16', '2026-05-12 10:15:16', '0');
INSERT INTO `p_order_item` VALUES ('78', '59', '20260512103344957771', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', '4', '真彭于晏', '442534********2356', '2026-05-12 10:33:44', '2026-05-12 10:33:44', '0');
INSERT INTO `p_order_item` VALUES ('79', '60', '20260512103856918425', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', '3', '胡歌', '441622********6645', '2026-05-12 10:38:56', '2026-05-12 10:38:56', '0');
INSERT INTO `p_order_item` VALUES ('80', '61', '20260512110845937858', '4', '5', '早鸟票', '上海·2026CP32', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '85.00', '1', '85.00', '3', '胡歌', '441622********6645', '2026-05-12 11:08:45', '2026-05-12 11:08:45', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品基础表';

-- ----------------------------
-- Records of p_product
-- ----------------------------
INSERT INTO `p_product` VALUES ('1', '海贼王 路飞 五档尼卡', '29', '1', '', '1100.00', '1399.00', '76', '100', '1', '1', '海贼王,路飞,热门', '0', '0', '1', '2026-01-23 15:49:52', '2026-05-09 12:01:38', '13');
INSERT INTO `p_product` VALUES ('2', '魔女之旅 周边吧唧', '31', '2', '', '25.00', '30.00', '179', '200', '1', '1', '伊蕾娜,魔女之旅,热门,新品', '0', '0', null, '2026-01-27 18:33:15', '2026-03-24 16:48:42', '5');
INSERT INTO `p_product` VALUES ('4', '上海·2026CP32', '40', '4', '', '85.00', '95.00', '9967', '10000', '1', '1', '漫展,CP,上海,热门', '1', '1', '2', '2026-02-14 12:48:10', '2026-05-12 11:08:48', '33');
INSERT INTO `p_product` VALUES ('5', '七龙珠 卡卡罗特 手办', '29', '1', '', '1399.00', '1599.00', '9996', '10000', '1', '1', '七龙珠,好评如潮!', '0', '0', null, '2026-03-20 10:50:16', '2026-05-09 11:44:53', '4');
INSERT INTO `p_product` VALUES ('6', '广州 萤火虫 5.1', '40', '4', '', '75.00', '80.00', '30000', '30000', '1', '1', '漫展,萤火虫,动漫,新品', '1', '1', '3', '2026-03-20 10:59:18', '2026-05-09 11:44:54', '0');
INSERT INTO `p_product` VALUES ('7', '游戏人生  白 手办', '30', '1', '', '99.00', '109.00', '995', '1000', '1', '1', '游戏人生,好评如潮!', '0', '0', null, '2026-03-20 16:47:15', '2026-05-09 11:44:56', '5');
INSERT INTO `p_product` VALUES ('8', '测试 漫展票务', '40', '4', '', '75.00', '85.00', '10000', '10000', '0', '1', '漫展,二次元', '1', '1', '4', '2026-03-23 11:20:47', '2026-05-09 11:44:58', '0');
INSERT INTO `p_product` VALUES ('9', '测试漫展2', '40', '4', '', '75.00', '85.00', '10000', '10000', '1', '1', '漫展', '1', '1', '5', '2026-03-23 12:11:10', '2026-05-09 11:45:00', '0');
INSERT INTO `p_product` VALUES ('10', '测试3', '40', '4', '', '80.00', '85.00', '999', '1000', '1', '1', '漫展', '1', '1', '6', '2026-03-23 14:13:05', '2026-05-12 09:57:28', '1');
INSERT INTO `p_product` VALUES ('11', 'test4', '40', '4', '', '200.00', '210.00', '100', '100', '1', '1', '漫展', '1', '1', null, '2026-03-23 14:43:06', '2026-05-09 11:45:03', '0');
INSERT INTO `p_product` VALUES ('12', '深圳会展中心 漫展', '40', '4', '', '75.00', '85.00', '9992', '10000', '1', '1', '漫展,会展中心,二次元', '1', '1', '7', '2026-03-24 15:04:16', '2026-05-12 09:57:02', '8');
INSERT INTO `p_product` VALUES ('13', '哈哈哈哈测试', '31', '2', '', '100.00', '120.00', '9', '10', '1', '1', '测试,谷子,ssss', '0', '0', null, '2026-03-24 18:16:51', '2026-05-09 11:47:01', '1');
INSERT INTO `p_product` VALUES ('14', '深圳会展中心漫展', '40', '4', '', '80.00', '90.00', '49998', '50000', '1', '1', '漫展,二次元,会展中心', '1', '1', '8', '2026-05-09 10:40:26', '2026-05-09 11:45:11', '2');
INSERT INTO `p_product` VALUES ('15', '伊蕾娜新手办', '28', '1', '', '1999.00', '2199.00', '9', '10', '1', '1', '伊蕾娜,魔女之旅,动漫', '0', '0', null, '2026-05-09 10:49:22', '2026-05-09 11:45:35', '1');
INSERT INTO `p_product` VALUES ('16', '雷蛇鼠标 联名鸣潮', '34', '2', '', '499.00', '599.00', '1000', '1000', '1', '1', '雷蛇,鸣潮,鼠标', '0', '0', null, '2026-05-09 11:49:01', '2026-05-09 11:51:18', '0');
INSERT INTO `p_product` VALUES ('17', '宝可梦 皮卡丘 马克杯', '32', '2', '', '29.00', '39.00', '100', '100', '1', '1', '宝可梦,皮卡丘,马克杯,联名', '0', '0', null, '2026-05-09 11:54:28', '2026-05-09 11:54:28', '0');

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
-- Records of p_product_collect
-- ----------------------------
INSERT INTO `p_product_collect` VALUES ('1', '1999834598331097089', '2', '2026-03-16 15:05:10', '2026-03-16 15:47:23', '0');
INSERT INTO `p_product_collect` VALUES ('4', '1999834598331097089', '1', '2026-03-16 15:47:05', '2026-03-24 10:31:51', '0');
INSERT INTO `p_product_collect` VALUES ('5', '1999847752930926594', '1', '2026-03-16 17:18:32', '2026-03-16 17:18:32', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评论表';

-- ----------------------------
-- Records of p_product_comment
-- ----------------------------
INSERT INTO `p_product_comment` VALUES ('1', '1', '1999834598331097089', '0', '不错的', '', '1', '2026-03-17 10:35:23', '2026-03-17 10:41:29', '0');
INSERT INTO `p_product_comment` VALUES ('2', '1', '1999834598331097089', '0', '啊啊啊啊啊啊', '', '1', '2026-03-17 10:36:33', '2026-03-17 10:59:51', '0');
INSERT INTO `p_product_comment` VALUES ('3', '1', '1999834598331097089', '0', '哈哈哈哈', '', '1', '2026-03-17 10:44:30', '2026-03-17 10:45:07', '0');
INSERT INTO `p_product_comment` VALUES ('4', '1', '1999834598331097089', '3', '啊啊啊', '', '0', '2026-03-17 10:44:59', '2026-03-17 10:44:59', '0');
INSERT INTO `p_product_comment` VALUES ('5', '1', '1999834598331097089', '3', '啊啊啊啊啊啊', '', '0', '2026-03-17 10:53:42', '2026-03-17 10:53:42', '0');
INSERT INTO `p_product_comment` VALUES ('6', '1', '1999834598331097089', '0', '吼吼吼22', '', '0', '2026-03-17 10:53:58', '2026-03-17 10:59:38', '1');
INSERT INTO `p_product_comment` VALUES ('7', '1', '1999834598331097089', '6', '你好', '', '0', '2026-03-17 10:59:32', '2026-03-17 10:59:32', '0');
INSERT INTO `p_product_comment` VALUES ('8', '1', '1999834598331097089', '1', '没错', '', '0', '2026-03-17 10:59:43', '2026-03-17 10:59:43', '0');
INSERT INTO `p_product_comment` VALUES ('9', '1', '1999834598331097089', '0', '幻想乡最强⑨神!', 'http://localhost:8080/comment/5103f192d6e34fc29cb883f6518f8887.jpg,http://localhost:8080/comment/9652b5984c8548eea587434c0d652e03.jpg,http://localhost:8080/comment/aca08e1d452f4c03a37b66bffa865880.jpg', '2', '2026-03-17 11:25:19', '2026-03-17 11:39:50', '0');
INSERT INTO `p_product_comment` VALUES ('10', '1', '1999834598331097089', '9', '很帅', '', '0', '2026-03-17 11:39:32', '2026-03-17 11:39:32', '0');
INSERT INTO `p_product_comment` VALUES ('11', '1', '1999847752930926594', '9', '哟西⑨桑', '', '0', '2026-03-17 11:39:56', '2026-03-17 11:39:56', '0');
INSERT INTO `p_product_comment` VALUES ('12', '1', '1999847752930926594', '0', '我是雾雨魔理沙', 'http://localhost:8080/comment/ea76cf0e9bd4493997810accac033e06.png', '0', '2026-03-17 14:32:43', '2026-03-17 14:56:31', '0');
INSERT INTO `p_product_comment` VALUES ('13', '1', '1999834598331097089', '0', '我不信我来', 'http://localhost:8080/comment/8d657d51a1de458cb1262dffe298a652.jpg', '0', '2026-03-17 14:43:48', '2026-03-17 14:43:48', '0');
INSERT INTO `p_product_comment` VALUES ('14', '1', '1999834598331097089', '0', 'az', 'http://localhost:8080/comment/af7d546ea43a40b2b5332ee39824b45c.jpg', '0', '2026-03-17 14:59:16', '2026-03-17 14:59:16', '0');
INSERT INTO `p_product_comment` VALUES ('15', '1', '1999847752930926594', '0', 'aaaaaaaaaa啊啊啊啊', 'http://localhost:8080/comment/18b07c1dcd2e44cfbf0365b5617eb314.jpg', '0', '2026-03-17 15:07:57', '2026-03-17 15:07:57', '0');
INSERT INTO `p_product_comment` VALUES ('16', '1', '1999847752930926594', '0', 'kyl', 'http://localhost:8080/comment/c0fa03ecc8b94055bc224999d6ccb196.jpg', '1', '2026-03-17 15:25:04', '2026-03-18 17:45:32', '0');
INSERT INTO `p_product_comment` VALUES ('17', '2', '1999834598331097089', '0', '来测试的', 'http://localhost:8080/comment/f11a82c180804a018f1dfa547094720a.jpg', '1', '2026-03-18 15:50:05', '2026-03-18 15:50:27', '1');
INSERT INTO `p_product_comment` VALUES ('18', '2', '1999834598331097089', '17', '啊啊啊啊', '', '0', '2026-03-18 15:50:11', '2026-03-18 15:50:11', '0');
INSERT INTO `p_product_comment` VALUES ('19', '2', '1999834598331097089', '17', '嘻嘻', '', '0', '2026-03-18 15:50:24', '2026-03-18 15:50:24', '0');
INSERT INTO `p_product_comment` VALUES ('20', '1', '1999834598331097089', '16', 'xx', '', '0', '2026-03-19 11:10:28', '2026-03-19 11:10:28', '0');
INSERT INTO `p_product_comment` VALUES ('21', '2', '1999834598331097089', '0', '111', 'http://localhost:8080/comment/4ca251bd486f4fefa8418ff466fb67eb.jpg', '1', '2026-03-19 12:19:55', '2026-03-23 17:45:15', '1');
INSERT INTO `p_product_comment` VALUES ('22', '2', '1999834598331097089', '21', '2222', '', '0', '2026-03-19 12:19:58', '2026-03-19 12:19:58', '0');
INSERT INTO `p_product_comment` VALUES ('23', '5', '1999834598331097089', '0', '很喜欢卡卡罗特', 'http://localhost:8080/comment/7eccf4693df343c28ba7f8e3516b3987.gif', '0', '2026-03-20 13:57:35', '2026-03-20 13:57:35', '0');
INSERT INTO `p_product_comment` VALUES ('24', '5', '1999834598331097089', '23', 'aaaaa', '', '0', '2026-03-20 14:00:38', '2026-03-20 14:00:38', '0');
INSERT INTO `p_product_comment` VALUES ('25', '5', '1999847752930926594', '0', 'aaaaaaaaaa', '', '0', '2026-03-20 14:04:39', '2026-03-20 14:04:39', '0');
INSERT INTO `p_product_comment` VALUES ('26', '7', '1999834598331097089', '0', '你是凑?你是凑?你是谁啊?', '', '0', '2026-03-20 16:56:34', '2026-03-20 16:56:34', '0');
INSERT INTO `p_product_comment` VALUES ('27', '7', '1999834598331097089', '26', '我是你爹', '', '0', '2026-03-20 16:56:42', '2026-03-20 16:56:42', '0');
INSERT INTO `p_product_comment` VALUES ('28', '7', '2034895749309763586', '0', 'hahha', '', '0', '2026-03-20 16:58:49', '2026-03-20 16:58:49', '0');
INSERT INTO `p_product_comment` VALUES ('29', '1', '1999834598331097089', '0', 'aaaa', 'http://localhost:8080/comment/faeb258e178443e3b6d1b71c5b172ce2.jpg', '0', '2026-03-27 16:31:14', '2026-03-27 16:31:14', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品详情表';

-- ----------------------------
-- Records of p_product_detail
-- ----------------------------
INSERT INTO `p_product_detail` VALUES ('12', '8', '测试i  是 是 ', '2026-03-23 11:21:04');
INSERT INTO `p_product_detail` VALUES ('21', '10', '啊啊啊', '2026-03-23 17:27:44');
INSERT INTO `p_product_detail` VALUES ('22', '9', '嘻嘻漫展测试', '2026-03-23 17:27:51');
INSERT INTO `p_product_detail` VALUES ('23', '1', '海贼王我当定了', '2026-03-23 17:32:51');
INSERT INTO `p_product_detail` VALUES ('24', '4', '啊啊啊啊啊啊CP32', '2026-03-23 17:33:04');
INSERT INTO `p_product_detail` VALUES ('27', '6', '广州萤火虫', '2026-03-23 17:36:26');
INSERT INTO `p_product_detail` VALUES ('28', '2', '这位戴着彰显魔女身份的胸针，披着一头灰色秀发，其美貌与才能散发的光芒，连太阳见了都会不由眯起眼睛的美女，究竟是谁呢？没错，就是我.', '2026-03-24 16:48:42');
INSERT INTO `p_product_detail` VALUES ('29', '7', 'NO GAME NO LIFE!', '2026-03-24 17:24:31');
INSERT INTO `p_product_detail` VALUES ('30', '5', '集齐七颗龙珠许愿', '2026-03-24 17:24:44');
INSERT INTO `p_product_detail` VALUES ('31', '13', '1231231', '2026-03-24 18:16:51');
INSERT INTO `p_product_detail` VALUES ('34', '15', '啊啊啊', '2026-05-09 10:49:22');
INSERT INTO `p_product_detail` VALUES ('35', '14', '会展中心', '2026-05-09 11:03:34');
INSERT INTO `p_product_detail` VALUES ('36', '16', '啊啊啊', '2026-05-09 11:49:01');
INSERT INTO `p_product_detail` VALUES ('37', '17', '皮卡丘~', '2026-05-09 11:54:28');

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
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品图片表';

-- ----------------------------
-- Records of p_product_image
-- ----------------------------
INSERT INTO `p_product_image` VALUES ('12', '11', 'http://localhost:8080/upload/a86c3814-4a39-4284-8ea4-762bdd99b415.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('13', '10', 'http://localhost:8080/upload/9c6530ca-9142-4e9d-84b9-fe4544bd7854.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('14', '9', 'http://localhost:8080/upload/0143e337-f0ec-4ada-8da6-dc911e6fab9a.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('15', '1', 'http://localhost:8080/upload/e58c3aba-912d-45f4-9aef-5b506cdd8802.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('16', '4', 'http://localhost:8080/upload/f2a9f334-c427-42e0-8aed-3b95e81f411d.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('20', '6', 'http://localhost:8080/upload/458d9659-ec9d-4e0c-835f-a81885082393.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('22', '12', 'http://localhost:8080/upload/e7f81761-a342-4617-a279-a470d79811fc.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('23', '2', 'http://localhost:8080/upload/7be4c4e2-6b2d-45ce-9cd3-9a575d3780fe.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('24', '2', 'http://localhost:8080/upload/4ce2289f-120c-43dc-be91-5e4031d3f996.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('25', '7', 'http://localhost:8080/upload/57c9c9a9-b1ce-48f6-9ab9-3a7dd8894783.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('26', '5', 'http://localhost:8080/upload/f1230b17-78d5-44df-9247-fb1ee8f63d63.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('27', '13', 'http://localhost:8080/upload/47239fa0-0f6a-476f-a712-a1a21ffe1fcb.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('30', '15', 'http://localhost:8080/upload/020f574a-3e61-4f23-b03c-9caec4b7dfce.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('31', '14', 'http://localhost:8080/upload/a4fef504-f7ba-4c5c-98cd-c207f7666077.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('32', '16', 'http://localhost:8080/upload/118dce71-d0a7-4740-8b81-ce79264f48cb.jpg', '1', '1');
INSERT INTO `p_product_image` VALUES ('33', '17', 'http://localhost:8080/upload/fed38abd-55d2-443f-95be-53c9c1c04c7c.jpg', '1', '1');

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品点赞表';

-- ----------------------------
-- Records of p_product_like
-- ----------------------------
INSERT INTO `p_product_like` VALUES ('6', '1', '1999847752930926594', '2026-03-16 17:18:30');
INSERT INTO `p_product_like` VALUES ('7', '4', '1999847752930926594', '2026-03-16 17:18:36');
INSERT INTO `p_product_like` VALUES ('12', '1', '1999834598331097089', '2026-03-27 17:03:34');
INSERT INTO `p_product_like` VALUES ('13', '2', '1999834598331097089', '2026-03-27 17:06:48');
INSERT INTO `p_product_like` VALUES ('14', '5', '1999834598331097089', '2026-05-09 09:59:26');
INSERT INTO `p_product_like` VALUES ('15', '14', '1999834598331097089', '2026-05-09 11:18:18');

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
-- Records of p_report
-- ----------------------------
INSERT INTO `p_report` VALUES ('1', '2', '11', '1999834598331097089', '4', '', '0', '0', null, '', '2026-03-17 14:23:52', '2026-03-17 14:23:52');
INSERT INTO `p_report` VALUES ('2', '2', '9', '1999847752930926594', '99', '有BAKA', '0', '0', null, '', '2026-03-17 14:24:57', '2026-03-17 14:24:57');
INSERT INTO `p_report` VALUES ('3', '2', '3', '1999847752930926594', '7', '', '1', '1999834598331097089', '2026-03-17 17:04:26', '【处置建议：建议删除违规内容】欠踢了', '2026-03-17 14:25:23', '2026-03-17 14:25:23');
INSERT INTO `p_report` VALUES ('4', '2', '2', '1999847752930926594', '99', '测试', '2', '1999834598331097089', '2026-03-17 16:49:07', '没问题', '2026-03-17 14:25:39', '2026-03-17 14:25:39');
INSERT INTO `p_report` VALUES ('5', '2', '8', '1999847752930926594', '99', '啊啊啊啊啊', '3', '1999834598331097089', '2026-03-17 16:48:35', '已经处理完成', '2026-03-17 14:31:58', '2026-03-17 14:31:58');
INSERT INTO `p_report` VALUES ('6', '4', '23', '2000219241932894209', '4', '', '0', '0', null, '', '2026-03-19 14:52:20', '2026-03-19 14:52:20');
INSERT INTO `p_report` VALUES ('7', '4', '21', '2000219241932894209', '99', '阿萨飒飒飒飒', '0', '0', null, '', '2026-03-19 14:52:44', '2026-03-19 14:52:44');
INSERT INTO `p_report` VALUES ('8', '3', '6', '2000219241932894209', '9', '', '0', '0', null, '', '2026-03-19 14:52:52', '2026-03-19 14:52:52');
INSERT INTO `p_report` VALUES ('9', '2', '23', '1999847752930926594', '1', '', '3', '1999834598331097089', '2026-03-24 18:17:56', '【处置建议：建议警告发布者】111', '2026-03-20 14:03:58', '2026-03-20 14:03:58');
INSERT INTO `p_report` VALUES ('10', '2', '27', '2034895749309763586', '4', '', '1', '1999834598331097089', '2026-03-24 18:17:49', '【处置建议：建议警告发布者】警告', '2026-03-20 16:59:03', '2026-03-20 16:59:03');
INSERT INTO `p_report` VALUES ('11', '2', '26', '2034895749309763586', '4', '', '1', '1999834598331097089', '2026-03-20 17:01:05', '【处置建议：建议警告发布者】建议删除该评论否则进行处理', '2026-03-20 16:59:29', '2026-03-20 16:59:29');

-- ----------------------------
-- Table structure for ticket_verify_log
-- ----------------------------
DROP TABLE IF EXISTS `ticket_verify_log`;
CREATE TABLE `ticket_verify_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `ticket_relation_id` bigint DEFAULT NULL COMMENT '关联order_ticket_relation.id',
  `verify_code` varchar(50) NOT NULL COMMENT '核销码',
  `verify_staff` varchar(50) DEFAULT NULL COMMENT '核销员',
  `verify_ip` varchar(50) DEFAULT NULL COMMENT '核销IP',
  `verify_result` tinyint NOT NULL DEFAULT '0' COMMENT '核销结果：0=失败 1=成功',
  `fail_reason` varchar(200) DEFAULT NULL COMMENT '失败原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '核销时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_verify_code` (`verify_code`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='票务核销日志表';

-- ----------------------------
-- Records of ticket_verify_log
-- ----------------------------
INSERT INTO `ticket_verify_log` VALUES ('1', '58', '25', '727751-01', 'zk', '127.0.0.1', '1', null, '2026-05-12 10:21:15');
INSERT INTO `ticket_verify_log` VALUES ('2', '55', '21', '910574-01', 'zk', '127.0.0.1', '1', null, '2026-05-12 10:24:57');
INSERT INTO `ticket_verify_log` VALUES ('3', '58', '26', '727751-02', 'ZK', '127.0.0.1', '1', null, '2026-05-12 10:26:38');
INSERT INTO `ticket_verify_log` VALUES ('4', '55', '22', '910574-02', 'zk', '127.0.0.1', '1', null, '2026-05-12 10:28:07');
INSERT INTO `ticket_verify_log` VALUES ('5', '60', '28', '918425-01', 'sdd', '127.0.0.1', '1', null, '2026-05-12 10:47:00');

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
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户购物车表';

-- ----------------------------
-- Records of u_cart
-- ----------------------------

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
  `role` enum('admin','manager','leader','member','consumer') DEFAULT 'consumer' COMMENT '权限',
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
) ENGINE=InnoDB AUTO_INCREMENT=2053772824372289538 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of u_user
-- ----------------------------
INSERT INTO `u_user` VALUES ('1999834598331097089', 'admin', '$2a$10$NgZf6vrlJ.jB4VPSmPTp8OEZOq1Huh8rCM8NSuiMGqUjM9JcQH.zO', '管理员1', '13544164655', 'admin', '1', 'http://localhost:8080/avatar/e947ea950c22415fa86455c158574440.jpg', '987067071@qq.com', '441622200209142394', '2025-12-14 19:49:14', '2026-03-27 16:37:26');
INSERT INTO `u_user` VALUES ('1999847752930926594', 'zk', '$2a$10$6UWLKD4MQd5wVlYZRx57luTetiDrkro3cR.X1hDQW2KPMPVO85qzu', '雾雨魔理沙', '13544164509', 'manager', '1', 'http://localhost:8080/avatar/4ed318398cec47c297b423f1b84f4a42.jpg', '', '', '2025-12-14 19:49:21', '2026-03-20 14:46:02');
INSERT INTO `u_user` VALUES ('2000107848118272001', 'lzk', '$2a$10$TW.taCupEB5ImVTBQQoXDeA8zsbgdnHK.xk8W5zZjKQhGoVlMjFO6', 'lzkss', '13544154444', 'leader', '1', '', '', '', '2025-12-14 19:49:24', '2026-03-19 18:00:23');
INSERT INTO `u_user` VALUES ('2000219241932894209', 'gyh', '$2a$10$9MybqofJXJ3zpi8jHjn5bOTiHtIADddDjOakHEh06Q0ZQiGfAuHB6', 'gggg', '13442321123', 'consumer', '1', 'http://localhost:8080/avatar/34facd0d70434469ae9386b380e276fe.jpg', '', '', '2025-12-14 19:49:27', '2026-03-30 11:28:05');
INSERT INTO `u_user` VALUES ('2002614823964082177', 'test_manager01', '$2a$10$WOKcCzTVYzmjA4dHOvdw/e9kBEalwwJAWG6fJ5o3reTj7P.87e/N6', '测试经理01', '13800138000', 'manager', '1', '', 'test_manager01@example.com', '440101199001011234', '2025-12-21 13:39:15', '2026-03-20 14:46:43');
INSERT INTO `u_user` VALUES ('2012032783522684930', 'test1', '$2a$10$d8VjK49yA9aTjP6baNj78es3ZGgjspRq0MGgB85P07vjsK/q6I4j.', 'zzz1', '', 'manager', '1', '', '', '', '2026-01-16 13:22:52', '2026-02-12 11:49:06');
INSERT INTO `u_user` VALUES ('2012032878855020546', 'test2', '$2a$10$eXfo8d7fxv5tlqg2.UXPCuW1t8q5sRd6ozexymzomyVkUps0Ifwny', 'zzzzz', '', 'leader', '1', '', '', '', '2026-01-16 13:23:15', '2026-01-16 13:23:15');
INSERT INTO `u_user` VALUES ('2012032935754948609', 'test3', '$2a$10$mo0bLEKRJ5W3ifT37SgNl.vI.m27LHJCgeLhA3uc81JB6HtBn0OhG', 'zczcz', '', 'member', '1', '', '', '', '2026-01-16 13:23:28', '2026-01-16 13:23:28');
INSERT INTO `u_user` VALUES ('2012032986090790914', 'test4', '$2a$10$L3BLoCcEtrzcEDwNVD8sQuxQAtvW1ToF1f2Ar6qq4aOecI.X5WS3.', '13213123', '', 'manager', '1', '', '', '', '2026-01-16 13:23:40', '2026-01-16 13:24:24');
INSERT INTO `u_user` VALUES ('2012033068638887938', 'test6', '$2a$10$ewqlgUXBIRjZ1yU0W7jqzOGpU0rkchL0Dbtiuh2VL0l/MmUCDpHNq', 'asdasdas', '', 'consumer', '0', '', '', '', '2026-01-16 13:24:00', '2026-03-30 11:19:46');
INSERT INTO `u_user` VALUES ('2034883864543858689', 'ceshi1', '$2a$10$cIcotdS2ukCUV3dIzXiuYe357Z6hwcZWI6vZHC3TQj7/rWCK3UsKW', '测试112331', '', 'leader', '1', '', '', '', '2026-03-20 14:44:54', '2026-03-20 14:44:54');
INSERT INTO `u_user` VALUES ('2034895749309763586', 'zktest', '$2a$10$S2C3ODOR8Y3I8B3EUlZpi.3Cu6BbtTr1toK3nJGUAIAlEHnFPAYAG', 'zktest', '', 'member', '1', 'http://localhost:8080/avatar/cb987fec8bb8414db4256dcc01585ba0.jpg', '', '', null, '2026-03-20 15:39:08');

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

-- ----------------------------
-- Records of u_user_address
-- ----------------------------
INSERT INTO `u_user_address` VALUES ('1', '1999834598331097089', '张科', '13544164650', '广东省', '深圳市', '福田区', '市民中心', '0', '2026-01-28 14:28:56', '2026-01-28 15:02:22', '1');
INSERT INTO `u_user_address` VALUES ('2', '1999834598331097089', '彭于晏', '13534077008', '广东省', '深圳市', '罗湖区', '万象城', '1', '2026-01-28 14:34:47', '2026-03-15 13:19:16', '0');
INSERT INTO `u_user_address` VALUES ('3', '1999834598331097089', '张科', '13544164650', '广东省', '深圳市', '龙岗区', '布吉街道', '0', '2026-01-28 15:03:05', '2026-03-15 13:19:16', '0');
INSERT INTO `u_user_address` VALUES ('4', '1999834598331097089', '胡歌', '13544153345', '浙江省', '杭州市', '江干区', '斤斤计较经济界', '0', '2026-02-11 19:38:05', '2026-02-11 19:38:05', '0');
INSERT INTO `u_user_address` VALUES ('5', '1999834598331097089', '张国荣', '13544164359', '上海市', '上海市', '虹口区', '虹桥机场', '0', '2026-02-11 19:47:27', '2026-02-11 19:47:27', '0');
INSERT INTO `u_user_address` VALUES ('6', '2034895749309763586', 'zktest', '13544164359', '北京市', '北京市', '朝阳区', 'bj', '1', '2026-03-20 15:40:22', '2026-03-20 15:40:22', '0');

/*
 Navicat Premium Dump SQL

 Source Server         : db(locallhost)
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : jsyl_ms

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 17/02/2026 23:38:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for campus_info
-- ----------------------------
DROP TABLE IF EXISTS `campus_info`;
CREATE TABLE `campus_info`  (
  `id` tinyint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '校区ID',
  `campus_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '校区名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '校区信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of campus_info
-- ----------------------------
INSERT INTO `campus_info` VALUES (1, '默认主校区');

-- ----------------------------
-- Table structure for comment_info
-- ----------------------------
DROP TABLE IF EXISTS `comment_info`;
CREATE TABLE `comment_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint UNSIGNED NOT NULL COMMENT '所属帖子ID',
  `parent_id` bigint UNSIGNED NULL DEFAULT 0 COMMENT '父评论ID(0表示顶级评论)',
  `user_id` int UNSIGNED NOT NULL COMMENT '评论者用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comment_post`(`post_id` ASC) USING BTREE,
  INDEX `idx_comment_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 131 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment_info
-- ----------------------------
INSERT INTO `comment_info` VALUES (1, 1, 0, 3, '用洛必达法则试试？', '2026-02-13 12:43:42');
INSERT INTO `comment_info` VALUES (2, 1, 1, 2, '好的，我去看看！', '2026-02-11 10:36:42');
INSERT INTO `comment_info` VALUES (3, 1, 0, 4, '我也不会，同问。', '2026-02-15 00:16:42');
INSERT INTO `comment_info` VALUES (4, 1, 3, 5, '老师上课讲过类似的题，你可以看看笔记。', '2026-02-11 02:52:42');
INSERT INTO `comment_info` VALUES (5, 1, 0, 6, '这题确实很难，我也想知道怎么做。', '2026-02-08 22:56:42');
INSERT INTO `comment_info` VALUES (6, 2, 0, 4, '自行车多少钱？', '2026-02-09 19:29:42');
INSERT INTO `comment_info` VALUES (7, 2, 6, 3, '价格可以商量，你加我微信详谈。', '2026-02-13 14:02:42');
INSERT INTO `comment_info` VALUES (8, 2, 0, 5, '自行车还在吗？', '2026-02-08 21:05:42');
INSERT INTO `comment_info` VALUES (9, 2, 0, 6, '有图片吗？想看看车况。', '2026-02-09 00:42:42');
INSERT INTO `comment_info` VALUES (10, 2, 9, 3, '有的，我私发给你。', '2026-02-09 21:37:42');
INSERT INTO `comment_info` VALUES (11, 3, 0, 6, '我推荐学校门口的那家黄焖鸡米饭，超好吃！', '2026-02-13 19:25:42');
INSERT INTO `comment_info` VALUES (12, 3, 0, 7, '南区二食堂的麻辣香锅也不错。', '2026-02-09 17:37:42');
INSERT INTO `comment_info` VALUES (13, 3, 0, 8, '我也想知道，最近吃什么都没胃口。', '2026-02-12 15:12:42');
INSERT INTO `comment_info` VALUES (14, 3, 12, 5, '那家我也吃过，确实不错！', '2026-02-11 08:31:42');
INSERT INTO `comment_info` VALUES (15, 3, 0, 9, '北区三食堂的兰州拉面推荐！', '2026-02-10 06:26:42');
INSERT INTO `comment_info` VALUES (16, 4, 0, 7, '占座确实很烦人，但是也没办法。', '2026-02-08 16:00:42');
INSERT INTO `comment_info` VALUES (17, 4, 0, 8, '我觉得图书馆应该加强管理。', '2026-02-10 22:05:42');
INSERT INTO `comment_info` VALUES (18, 4, 0, 9, '早点去就有位置了。', '2026-02-12 23:48:42');
INSERT INTO `comment_info` VALUES (19, 4, 17, 6, '说的容易，大家都有课啊。', '2026-02-09 14:11:42');
INSERT INTO `comment_info` VALUES (20, 4, 0, 10, '我觉得可以实行预约制。', '2026-02-14 08:52:42');
INSERT INTO `comment_info` VALUES (21, 5, 0, 8, '我会编程，但是我数学不太好。', '2026-02-13 11:11:42');
INSERT INTO `comment_info` VALUES (22, 5, 20, 6, '没关系，我们可以互补！', '2026-02-08 14:44:42');
INSERT INTO `comment_info` VALUES (23, 5, 0, 9, '比赛什么时候开始？', '2026-02-15 01:31:42');
INSERT INTO `comment_info` VALUES (24, 5, 0, 10, '我也想参加，但是我什么都不会。', '2026-02-12 20:44:42');
INSERT INTO `comment_info` VALUES (25, 5, 23, 6, '没关系，我们可以一起学习！', '2026-02-10 12:30:42');
INSERT INTO `comment_info` VALUES (26, 6, 0, 9, '有高数教材吗？', '2026-02-12 09:44:42');
INSERT INTO `comment_info` VALUES (27, 6, 25, 7, '有的，你要大几的？', '2026-02-14 20:35:42');
INSERT INTO `comment_info` VALUES (28, 6, 0, 10, '价格大概多少？', '2026-02-14 11:03:42');
INSERT INTO `comment_info` VALUES (29, 6, 0, 11, '有英语教材吗？', '2026-02-12 02:53:42');
INSERT INTO `comment_info` VALUES (30, 6, 28, 7, '有的，都有。', '2026-02-08 14:42:42');
INSERT INTO `comment_info` VALUES (31, 7, 0, 10, '我知道学校附近有一家，还不错。', '2026-02-12 02:14:42');
INSERT INTO `comment_info` VALUES (32, 7, 0, 11, '具体在哪里？', '2026-02-12 00:29:42');
INSERT INTO `comment_info` VALUES (33, 7, 31, 10, '就在学校门口那条街，你可以地图搜一下。', '2026-02-15 05:03:42');
INSERT INTO `comment_info` VALUES (34, 7, 0, 12, '我的电脑也坏了，同问。', '2026-02-10 09:15:42');
INSERT INTO `comment_info` VALUES (35, 7, 0, 13, '可以去售后看看，就是有点贵。', '2026-02-11 16:26:42');
INSERT INTO `comment_info` VALUES (36, 8, 0, 11, '是哪家店？求地址！', '2026-02-11 15:50:42');
INSERT INTO `comment_info` VALUES (37, 8, 35, 9, '就在学校附近，我私发给你。', '2026-02-14 15:17:42');
INSERT INTO `comment_info` VALUES (38, 8, 0, 12, '看起来很好吃的样子！', '2026-02-15 14:17:42');
INSERT INTO `comment_info` VALUES (39, 8, 0, 13, '有时间去试试！', '2026-02-11 10:58:42');
INSERT INTO `comment_info` VALUES (40, 8, 0, 14, '我也去过那家，确实不错！', '2026-02-08 21:22:42');
INSERT INTO `comment_info` VALUES (41, 9, 0, 12, '我也想选一些轻松的课，同问。', '2026-02-15 11:19:42');
INSERT INTO `comment_info` VALUES (42, 9, 0, 13, '推荐音乐鉴赏，老师很好，给分也高。', '2026-02-14 01:53:42');
INSERT INTO `comment_info` VALUES (43, 9, 41, 10, '真的吗？我也想选这个。', '2026-02-15 08:52:42');
INSERT INTO `comment_info` VALUES (44, 9, 0, 14, '还有什么其他的推荐吗？', '2026-02-12 00:04:42');
INSERT INTO `comment_info` VALUES (45, 9, 0, 15, '同求推荐！', '2026-02-12 07:09:42');
INSERT INTO `comment_info` VALUES (46, 10, 0, 13, '你想租什么样的房子？', '2026-02-09 20:56:42');
INSERT INTO `comment_info` VALUES (47, 10, 45, 11, '单间就行，价格不要太贵。', '2026-02-10 20:36:42');
INSERT INTO `comment_info` VALUES (48, 10, 0, 14, '我也想租房子，同求。', '2026-02-09 01:37:42');
INSERT INTO `comment_info` VALUES (49, 10, 0, 15, '学校附近的房子都挺贵的。', '2026-02-11 03:39:42');
INSERT INTO `comment_info` VALUES (50, 10, 0, 16, '可以找几个人一起合租，这样便宜点。', '2026-02-12 22:47:42');
INSERT INTO `comment_info` VALUES (51, 11, 0, 14, '代取快递多少钱？', '2026-02-08 16:23:42');
INSERT INTO `comment_info` VALUES (52, 11, 0, 15, '可以送到宿舍楼下吗？', '2026-02-09 22:58:42');
INSERT INTO `comment_info` VALUES (53, 12, 0, 15, '同求四六级经验！', '2026-02-15 03:05:42');
INSERT INTO `comment_info` VALUES (54, 12, 0, 16, '多做真题，背单词就可以了。', '2026-02-09 03:51:42');
INSERT INTO `comment_info` VALUES (55, 13, 0, 16, '吉他多少钱？', '2026-02-12 19:27:42');
INSERT INTO `comment_info` VALUES (56, 13, 0, 17, '是民谣吉他吗？', '2026-02-13 23:06:42');
INSERT INTO `comment_info` VALUES (57, 14, 0, 17, '最近有社团活动，可以关注一下。', '2026-02-08 18:33:42');
INSERT INTO `comment_info` VALUES (58, 14, 0, 18, '同问有什么活动。', '2026-02-14 08:48:42');
INSERT INTO `comment_info` VALUES (59, 15, 0, 18, '同求考研经验！', '2026-02-08 21:46:42');
INSERT INTO `comment_info` VALUES (60, 15, 0, 19, '早点开始复习，坚持最重要。', '2026-02-13 19:50:42');
INSERT INTO `comment_info` VALUES (61, 16, 0, 19, '自行车多少钱？', '2026-02-12 19:16:42');
INSERT INTO `comment_info` VALUES (62, 16, 0, 20, '有图片吗？', '2026-02-13 22:14:42');
INSERT INTO `comment_info` VALUES (63, 17, 0, 20, '学校附近有健身房，就是有点贵。', '2026-02-08 14:45:42');
INSERT INTO `comment_info` VALUES (64, 17, 0, 21, '可以办学生卡，会便宜点。', '2026-02-13 16:27:42');
INSERT INTO `comment_info` VALUES (65, 18, 0, 21, '感谢分享！', '2026-02-12 23:23:42');
INSERT INTO `comment_info` VALUES (66, 18, 0, 22, '很有用的分享！', '2026-02-15 04:58:42');
INSERT INTO `comment_info` VALUES (67, 19, 0, 22, '我也想参加，加我一个！', '2026-02-14 12:06:42');
INSERT INTO `comment_info` VALUES (68, 19, 0, 23, '我篮球打得不好，可以吗？', '2026-02-11 06:58:42');
INSERT INTO `comment_info` VALUES (69, 20, 0, 23, '电脑配置怎么样？', '2026-02-11 07:56:42');
INSERT INTO `comment_info` VALUES (70, 20, 0, 24, '价格大概多少？', '2026-02-14 04:10:42');
INSERT INTO `comment_info` VALUES (71, 21, 0, 24, '学校里面就有打印店，很方便。', '2026-02-14 06:23:42');
INSERT INTO `comment_info` VALUES (72, 21, 0, 25, '图书馆也有打印的地方。', '2026-02-13 04:49:42');
INSERT INTO `comment_info` VALUES (73, 22, 0, 25, '我也想认识新朋友！', '2026-02-14 14:22:42');
INSERT INTO `comment_info` VALUES (74, 22, 0, 26, '大家可以建个群一起交流。', '2026-02-10 18:43:42');
INSERT INTO `comment_info` VALUES (75, 23, 0, 26, '同求实习经验！', '2026-02-15 11:55:42');
INSERT INTO `comment_info` VALUES (76, 23, 0, 27, '可以多投简历，多面试。', '2026-02-15 12:49:42');
INSERT INTO `comment_info` VALUES (77, 24, 0, 27, '有哪些书？', '2026-02-15 13:42:42');
INSERT INTO `comment_info` VALUES (78, 24, 0, 28, '价格大概多少？', '2026-02-08 15:26:42');
INSERT INTO `comment_info` VALUES (79, 25, 0, 28, '学校附近有几家理发店，都还可以。', '2026-02-08 21:30:42');
INSERT INTO `comment_info` VALUES (80, 25, 0, 29, '可以美团上看看评价。', '2026-02-09 22:35:42');
INSERT INTO `comment_info` VALUES (81, 26, 0, 29, '是哪里？求推荐！', '2026-02-14 09:50:42');
INSERT INTO `comment_info` VALUES (82, 26, 0, 30, '看起来很好玩的样子！', '2026-02-12 14:48:42');
INSERT INTO `comment_info` VALUES (83, 27, 0, 30, '我也想参加，加我一个！', '2026-02-11 05:54:42');
INSERT INTO `comment_info` VALUES (84, 27, 0, 31, '我英语不好，可以吗？', '2026-02-09 18:30:42');
INSERT INTO `comment_info` VALUES (85, 28, 0, 31, '手机是什么型号的？', '2026-02-13 12:11:42');
INSERT INTO `comment_info` VALUES (86, 28, 0, 32, '价格大概多少？', '2026-02-08 14:50:42');
INSERT INTO `comment_info` VALUES (87, 29, 0, 32, '学校里面就有超市，很方便。', '2026-02-14 22:58:42');
INSERT INTO `comment_info` VALUES (88, 29, 0, 33, '学校门口也有几家大超市。', '2026-02-12 07:43:42');
INSERT INTO `comment_info` VALUES (89, 30, 0, 33, '感谢分享！我也去听听这些歌。', '2026-02-15 03:07:42');
INSERT INTO `comment_info` VALUES (90, 30, 0, 34, '我也来分享一些我喜欢的歌。', '2026-02-09 01:48:42');
INSERT INTO `comment_info` VALUES (91, 31, 0, 34, '同求公务员备考经验！', '2026-02-12 09:02:42');
INSERT INTO `comment_info` VALUES (92, 31, 0, 35, '多刷题，多总结。', '2026-02-12 01:09:42');
INSERT INTO `comment_info` VALUES (93, 32, 0, 35, '平板是什么牌子的？', '2026-02-14 12:04:42');
INSERT INTO `comment_info` VALUES (94, 32, 0, 36, '价格大概多少？', '2026-02-13 18:17:42');
INSERT INTO `comment_info` VALUES (95, 33, 0, 36, '学校附近就有药店，很方便。', '2026-02-09 16:35:42');
INSERT INTO `comment_info` VALUES (96, 33, 0, 37, '如果严重的话，还是去医院看看。', '2026-02-12 13:28:42');
INSERT INTO `comment_info` VALUES (97, 34, 0, 37, '是哪部电影？求推荐！', '2026-02-11 02:59:42');
INSERT INTO `comment_info` VALUES (98, 34, 0, 38, '看起来很好看的样子！', '2026-02-09 07:54:42');
INSERT INTO `comment_info` VALUES (99, 35, 0, 38, '我也想参加，加我一个！', '2026-02-11 15:55:42');
INSERT INTO `comment_info` VALUES (100, 35, 0, 39, '我辩论不太好，可以吗？', '2026-02-14 17:20:42');
INSERT INTO `comment_info` VALUES (101, 36, 0, 39, '相机是什么型号的？', '2026-02-09 00:15:42');
INSERT INTO `comment_info` VALUES (102, 36, 0, 40, '价格大概多少？', '2026-02-13 06:42:42');
INSERT INTO `comment_info` VALUES (103, 37, 0, 40, '学校附近就有银行，很方便。', '2026-02-09 18:06:42');
INSERT INTO `comment_info` VALUES (104, 37, 0, 41, '具体是什么银行？', '2026-02-14 07:50:42');
INSERT INTO `comment_info` VALUES (105, 38, 0, 41, '是哪个窗口？求推荐！', '2026-02-12 18:16:42');
INSERT INTO `comment_info` VALUES (106, 38, 0, 42, '看起来很好吃的样子！', '2026-02-12 05:10:42');
INSERT INTO `comment_info` VALUES (107, 39, 0, 42, '同求教师资格证备考经验！', '2026-02-14 04:29:42');
INSERT INTO `comment_info` VALUES (108, 39, 0, 43, '多背知识点，多做题。', '2026-02-11 16:17:42');
INSERT INTO `comment_info` VALUES (109, 40, 0, 43, '耳机是什么牌子的？', '2026-02-14 05:21:42');
INSERT INTO `comment_info` VALUES (110, 40, 0, 44, '价格大概多少？', '2026-02-13 11:17:42');
INSERT INTO `comment_info` VALUES (111, 41, 0, 44, '学校附近有几家网吧，都还可以。', '2026-02-09 01:48:42');
INSERT INTO `comment_info` VALUES (112, 41, 0, 45, '可以美团上看看评价。', '2026-02-10 08:30:42');
INSERT INTO `comment_info` VALUES (113, 42, 0, 45, '我也喜欢运动！', '2026-02-08 22:32:42');
INSERT INTO `comment_info` VALUES (114, 42, 0, 46, '我平时喜欢跑步。', '2026-02-12 00:32:42');
INSERT INTO `comment_info` VALUES (115, 43, 0, 46, '我也想参加，加我一个！', '2026-02-10 16:29:42');
INSERT INTO `comment_info` VALUES (116, 43, 0, 47, '我羽毛球打得不好，可以吗？', '2026-02-08 18:14:42');
INSERT INTO `comment_info` VALUES (117, 44, 0, 47, '键盘是什么牌子的？', '2026-02-10 03:04:42');
INSERT INTO `comment_info` VALUES (118, 44, 0, 48, '价格大概多少？', '2026-02-08 18:00:42');
INSERT INTO `comment_info` VALUES (119, 45, 0, 48, '学校附近就有水果店，很方便。', '2026-02-11 18:13:42');
INSERT INTO `comment_info` VALUES (120, 45, 0, 49, '可以美团上看看评价。', '2026-02-09 22:30:42');
INSERT INTO `comment_info` VALUES (121, 46, 0, 49, '是哪本书？求推荐！', '2026-02-12 19:15:42');
INSERT INTO `comment_info` VALUES (122, 46, 0, 2, '看起来很好看的样子！', '2026-02-11 14:06:42');
INSERT INTO `comment_info` VALUES (123, 47, 0, 2, '同求雅思备考经验！', '2026-02-10 22:09:42');
INSERT INTO `comment_info` VALUES (124, 47, 0, 3, '多听多说多写多读。', '2026-02-11 05:50:42');
INSERT INTO `comment_info` VALUES (125, 48, 0, 3, '鼠标是什么牌子的？', '2026-02-14 20:08:42');
INSERT INTO `comment_info` VALUES (126, 48, 0, 4, '价格大概多少？', '2026-02-10 20:34:42');
INSERT INTO `comment_info` VALUES (127, 49, 0, 4, '学校附近就有蛋糕店，很方便。', '2026-02-15 03:47:42');
INSERT INTO `comment_info` VALUES (128, 49, 0, 5, '可以美团上看看评价。', '2026-02-13 14:36:42');
INSERT INTO `comment_info` VALUES (129, 50, 0, 5, '同求推荐！', '2026-02-13 23:03:42');
INSERT INTO `comment_info` VALUES (130, 50, 0, 6, '学校附近有几家蛋糕店，都还可以。', '2026-02-13 08:52:42');

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '当前用户ID (发起方/视角方)',
  `target_user_id` int UNSIGNED NOT NULL COMMENT '对方用户ID',
  `last_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一条消息预览',
  `unread_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '未读消息数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会话创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后消息时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_conversation_user`(`user_id` ASC, `target_user_id` ASC) USING BTREE,
  INDEX `idx_conv_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_conv_target`(`target_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_conv_target` FOREIGN KEY (`target_user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_conv_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of conversation
-- ----------------------------

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '接收用户ID',
  `type` tinyint UNSIGNED NOT NULL COMMENT '通知类型：1-系统 2-评论 3-订单',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `related_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联ID（如订单ID/帖子ID）',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-否 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_notif_user`(`user_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------

-- ----------------------------
-- Table structure for order_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `order_evaluation`;
CREATE TABLE `order_evaluation`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint UNSIGNED NOT NULL COMMENT '订单ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '评价人ID',
  `target_user_id` int UNSIGNED NOT NULL COMMENT '被评价人ID',
  `rating` tinyint UNSIGNED NOT NULL COMMENT '评分：1-5星',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_eval_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_eval_target`(`target_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_eval_order` FOREIGN KEY (`order_id`) REFERENCES `order_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_evaluation
-- ----------------------------

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单主键ID（自增）',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号（唯一，如ORD20260204001）',
  `user_id` int UNSIGNED NOT NULL COMMENT '下单用户ID（关联user_info表的id）',
  `acceptor_id` int UNSIGNED NULL DEFAULT NULL COMMENT '接单用户ID（关联user_info表，为空表示未接单）',
  `service_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务/收货地址',
  `requirement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '具体需求描述',
  `contact_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `order_amount` decimal(10, 2) NOT NULL COMMENT '订单金额（精确到分，避免浮点误差）',
  `order_status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0-待接单 1-已接单(服务中) 2-完成订单',
  `type_id` tinyint UNSIGNED NOT NULL COMMENT '订单类型ID（关联order_type表的id）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间（默认当前时间）',
  `require_time` datetime NULL DEFAULT NULL COMMENT '订单要求时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '订单完成时间',
  `deleted_at` datetime NULL DEFAULT NULL COMMENT '删除时间（NULL表示未删除）',
  `campus_id` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '校区ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `fk_order_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_order_type`(`type_id` ASC) USING BTREE,
  INDEX `idx_acceptor_id`(`acceptor_id` ASC) USING BTREE,
  INDEX `idx_order_status`(`order_status` ASC) USING BTREE,
  CONSTRAINT `fk_order_acceptor` FOREIGN KEY (`acceptor_id`) REFERENCES `user_info` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_type` FOREIGN KEY (`type_id`) REFERENCES `order_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ck_order_amount` CHECK (`order_amount` >= 0)
) ENGINE = InnoDB AUTO_INCREMENT = 151 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES (1, 'ORD20260215001', 2, NULL, '北区食堂门口', '帮我带一份黄焖鸡米饭，微辣', '13800138001', 18.50, 0, 5, '2026-02-14 20:55:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (2, 'ORD20260215002', 3, NULL, '南区快递站', '取一个顺丰快递，送到南区3栋', '13800138002', 5.00, 0, 6, '2026-02-15 09:17:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (3, 'ORD20260215003', 4, NULL, '图书馆二楼', '打印50份复习资料，双面黑白', '13800138003', 15.00, 0, 3, '2026-02-14 17:02:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (4, 'ORD20260215004', 5, NULL, '东区超市', '买两瓶矿泉水和一包纸巾，送到东区5栋', '13800138004', 12.00, 0, 4, '2026-02-14 18:46:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (5, 'ORD20260215005', 6, NULL, '西区校门', '帮我把这份文件送到行政楼教务处', '13800138005', 8.00, 0, 1, '2026-02-15 04:05:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (6, 'ORD20260215006', 7, NULL, '北区1栋楼下', '取一个圆通快递，送到北区7栋', '13800138006', 3.00, 0, 2, '2026-02-14 21:31:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (7, 'ORD20260215007', 8, NULL, '南区二食堂', '帮我带一份糖醋排骨饭，不要香菜', '13800138007', 16.80, 0, 5, '2026-02-15 08:45:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (8, 'ORD20260215008', 9, NULL, '菜鸟驿站', '取两个快递，都是中通的，送到北区9栋', '13800138008', 6.00, 0, 6, '2026-02-15 12:35:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (9, 'ORD20260215009', 10, NULL, '教学楼A座', '打印100份PPT，彩色单面', '13800138009', 50.00, 0, 3, '2026-02-14 22:05:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (10, 'ORD20260215010', 11, NULL, '校园超市', '买一箱牛奶和一些零食，送到南区11栋', '13800138010', 65.00, 0, 4, '2026-02-15 10:02:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (11, 'ORD20260215011', 12, NULL, '北区三食堂', '帮我带一份兰州拉面，加蛋', '13800138011', 14.00, 0, 5, '2026-02-14 17:17:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (12, 'ORD20260215012', 13, NULL, '顺丰快递点', '取一个大件快递，送到东区13栋', '13800138012', 10.00, 0, 6, '2026-02-14 17:45:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (13, 'ORD20260215013', 14, NULL, '图书馆打印室', '打印20份论文，装订成册', '13800138013', 30.00, 0, 3, '2026-02-14 22:18:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (14, 'ORD20260215014', 15, NULL, '西区水果店', '买一些苹果和香蕉，送到西区15栋', '13800138014', 25.00, 0, 4, '2026-02-14 19:37:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (15, 'ORD20260215015', 16, NULL, '南区一食堂', '帮我带一份麻辣香锅，微辣', '13800138015', 22.00, 0, 5, '2026-02-14 16:35:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (16, 'ORD20260215016', 17, NULL, '韵达快递站', '取一个快递，送到北区17栋', '13800138016', 4.00, 0, 2, '2026-02-15 09:27:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (17, 'ORD20260215017', 18, NULL, '教学楼B座', '打印30份作业，双面打印', '13800138017', 9.00, 0, 3, '2026-02-15 06:53:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (18, 'ORD20260215018', 19, NULL, '校园便利店', '买一些日用品，送到南区19栋', '13800138018', 45.00, 0, 4, '2026-02-14 15:27:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (19, 'ORD20260215019', 20, NULL, '北区二食堂', '帮我带一份盖浇饭，土豆牛肉', '13800138019', 15.00, 0, 5, '2026-02-14 18:02:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (20, 'ORD20260215020', 21, NULL, '申通快递点', '取两个快递，送到东区21栋', '13800138020', 7.00, 0, 6, '2026-02-15 05:09:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (21, 'ORD20260215021', 22, NULL, '图书馆', '帮我占一个座位，靠窗的位置', '13800138021', 5.00, 0, 1, '2026-02-15 05:06:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (22, 'ORD20260215022', 23, NULL, '南区三食堂', '帮我带一份麻辣烫，不要麻不要辣', '13800138022', 18.00, 0, 5, '2026-02-14 19:24:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (23, 'ORD20260215023', 24, NULL, '京东快递站', '取一个快递，送到西区23栋', '13800138023', 4.00, 0, 2, '2026-02-14 19:04:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (24, 'ORD20260215024', 25, NULL, '打印店', '打印一张海报，A3彩色', '13800138024', 20.00, 0, 3, '2026-02-14 22:34:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (25, 'ORD20260215025', 26, NULL, '校园超市', '买一些饮料和零食，送到北区25栋', '13800138025', 35.00, 0, 4, '2026-02-14 17:01:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (26, 'ORD20260215026', 27, NULL, '北区一食堂', '帮我带一份包子和豆浆', '13800138026', 8.00, 0, 5, '2026-02-15 02:48:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (27, 'ORD20260215027', 28, NULL, '圆通快递站', '取一个快递，送到南区27栋', '13800138027', 3.00, 0, 6, '2026-02-14 20:21:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (28, 'ORD20260215028', 29, NULL, '教学楼C座', '打印50份试卷，单面黑白', '13800138028', 25.00, 0, 3, '2026-02-15 06:43:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (29, 'ORD20260215029', 30, NULL, '水果店', '买一个西瓜，送到东区29栋', '13800138029', 30.00, 0, 4, '2026-02-15 05:57:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (30, 'ORD20260215030', 31, NULL, '南区二食堂', '帮我带一份炒饭，加火腿肠', '13800138030', 12.00, 0, 5, '2026-02-14 18:58:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (31, 'ORD20260215031', 32, NULL, '中通快递站', '取一个快递，送到西区31栋', '13800138031', 4.00, 0, 2, '2026-02-15 14:24:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (32, 'ORD20260215032', 33, NULL, '图书馆打印室', '打印10份简历，彩色', '13800138032', 15.00, 0, 3, '2026-02-15 00:27:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (33, 'ORD20260215033', 34, NULL, '校园超市', '买一些学习用品，送到北区33栋', '13800138033', 28.00, 0, 4, '2026-02-14 16:27:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (34, 'ORD20260215034', 35, NULL, '北区三食堂', '帮我带一份水饺，猪肉馅的', '13800138034', 16.00, 0, 5, '2026-02-14 18:19:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (35, 'ORD20260215035', 36, NULL, '韵达快递站', '取两个快递，送到南区35栋', '13800138035', 6.00, 0, 6, '2026-02-15 03:36:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (36, 'ORD20260215036', 37, NULL, '教学楼A座', '打印20份课件，双面打印', '13800138036', 8.00, 0, 3, '2026-02-14 20:28:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (37, 'ORD20260215037', 38, NULL, '便利店', '买一些零食和饮料，送到东区37栋', '13800138037', 40.00, 0, 4, '2026-02-15 04:54:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (38, 'ORD20260215038', 39, NULL, '南区一食堂', '帮我带一份套餐，一荤一素', '13800138038', 14.00, 0, 5, '2026-02-14 20:31:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (39, 'ORD20260215039', 40, NULL, '顺丰快递点', '取一个快递，送到西区39栋', '13800138039', 5.00, 0, 2, '2026-02-15 01:16:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (40, 'ORD20260215040', 41, NULL, '打印店', '打印一张证件照，一寸', '13800138040', 15.00, 0, 3, '2026-02-15 02:08:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (41, 'ORD20260215041', 42, NULL, '校园超市', '买一些水果和牛奶，送到北区41栋', '13800138041', 55.00, 0, 4, '2026-02-14 16:18:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (42, 'ORD20260215042', 43, NULL, '北区二食堂', '帮我带一份面条，加煎蛋', '13800138042', 13.00, 0, 5, '2026-02-15 12:31:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (43, 'ORD20260215043', 44, NULL, '申通快递站', '取一个快递，送到南区43栋', '13800138043', 4.00, 0, 6, '2026-02-14 23:02:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (44, 'ORD20260215044', 45, NULL, '教学楼B座', '打印40份资料，单面打印', '13800138044', 12.00, 0, 3, '2026-02-14 15:02:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (45, 'ORD20260215045', 46, NULL, '水果店', '买一些草莓，送到东区45栋', '13800138045', 35.00, 0, 4, '2026-02-14 15:29:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (46, 'ORD20260215046', 47, NULL, '南区三食堂', '帮我带一份快餐，两荤一素', '13800138046', 18.00, 0, 5, '2026-02-14 17:40:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (47, 'ORD20260215047', 48, NULL, '京东快递站', '取一个快递，送到西区47栋', '13800138047', 5.00, 0, 2, '2026-02-15 03:17:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (48, 'ORD20260215048', 49, NULL, '图书馆打印室', '打印15份论文，装订', '13800138048', 22.00, 0, 3, '2026-02-14 20:49:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (49, 'ORD20260215049', 50, NULL, '校园超市', '买一些日用品和零食，送到北区49栋', '13800138049', 50.00, 0, 4, '2026-02-15 07:35:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (50, 'ORD20260215050', 2, NULL, '北区一食堂', '帮我带一份早餐，包子和粥', '13800138001', 10.00, 0, 5, '2026-02-15 08:54:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (51, 'ORD20260215051', 3, 10, '南区快递站', '取一个顺丰快递，送到南区3栋', '13800138002', 5.00, 1, 6, '2026-02-15 07:09:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (52, 'ORD20260215052', 4, 11, '图书馆二楼', '打印50份复习资料，双面黑白', '13800138003', 15.00, 1, 3, '2026-02-14 18:27:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (53, 'ORD20260215053', 5, 12, '东区超市', '买两瓶矿泉水和一包纸巾，送到东区5栋', '13800138004', 12.00, 1, 4, '2026-02-15 08:12:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (54, 'ORD20260215054', 6, 13, '西区校门', '帮我把这份文件送到行政楼教务处', '13800138005', 8.00, 1, 1, '2026-02-14 19:03:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (55, 'ORD20260215055', 7, 14, '北区1栋楼下', '取一个圆通快递，送到北区7栋', '13800138006', 3.00, 1, 2, '2026-02-15 07:59:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (56, 'ORD20260215056', 8, 15, '南区二食堂', '帮我带一份糖醋排骨饭，不要香菜', '13800138007', 16.80, 1, 5, '2026-02-14 16:13:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (57, 'ORD20260215057', 9, 16, '菜鸟驿站', '取两个快递，都是中通的，送到北区9栋', '13800138008', 6.00, 1, 6, '2026-02-14 18:29:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (58, 'ORD20260215058', 10, 17, '教学楼A座', '打印100份PPT，彩色单面', '13800138009', 50.00, 1, 3, '2026-02-15 05:11:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (59, 'ORD20260215059', 11, 18, '校园超市', '买一箱牛奶和一些零食，送到南区11栋', '13800138010', 65.00, 1, 4, '2026-02-15 03:50:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (60, 'ORD20260215060', 12, 19, '北区三食堂', '帮我带一份兰州拉面，加蛋', '13800138011', 14.00, 1, 5, '2026-02-15 13:03:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (61, 'ORD20260215061', 13, 20, '顺丰快递点', '取一个大件快递，送到东区13栋', '13800138012', 10.00, 1, 6, '2026-02-14 15:07:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (62, 'ORD20260215062', 14, 21, '图书馆打印室', '打印20份论文，装订成册', '13800138013', 30.00, 1, 3, '2026-02-14 21:50:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (63, 'ORD20260215063', 15, 22, '西区水果店', '买一些苹果和香蕉，送到西区15栋', '13800138014', 25.00, 1, 4, '2026-02-15 01:12:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (64, 'ORD20260215064', 16, 23, '南区一食堂', '帮我带一份麻辣香锅，微辣', '13800138015', 22.00, 1, 5, '2026-02-14 21:56:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (65, 'ORD20260215065', 17, 24, '韵达快递站', '取一个快递，送到北区17栋', '13800138016', 4.00, 1, 2, '2026-02-14 19:25:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (66, 'ORD20260215066', 18, 25, '教学楼B座', '打印30份作业，双面打印', '13800138017', 9.00, 1, 3, '2026-02-14 16:39:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (67, 'ORD20260215067', 19, 26, '校园便利店', '买一些日用品，送到南区19栋', '13800138018', 45.00, 1, 4, '2026-02-15 10:26:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (68, 'ORD20260215068', 20, 27, '北区二食堂', '帮我带一份盖浇饭，土豆牛肉', '13800138019', 15.00, 1, 5, '2026-02-15 11:36:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (69, 'ORD20260215069', 21, 28, '申通快递点', '取两个快递，送到东区21栋', '13800138020', 7.00, 1, 6, '2026-02-15 12:05:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (70, 'ORD20260215070', 22, 29, '图书馆', '帮我占一个座位，靠窗的位置', '13800138021', 5.00, 1, 1, '2026-02-15 11:01:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (71, 'ORD20260215071', 23, 30, '南区三食堂', '帮我带一份麻辣烫，不要麻不要辣', '13800138022', 18.00, 1, 5, '2026-02-15 04:14:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (72, 'ORD20260215072', 24, 31, '京东快递站', '取一个快递，送到西区23栋', '13800138023', 4.00, 1, 2, '2026-02-14 21:30:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (73, 'ORD20260215073', 25, 32, '打印店', '打印一张海报，A3彩色', '13800138024', 20.00, 1, 3, '2026-02-15 08:14:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (74, 'ORD20260215074', 26, 33, '校园超市', '买一些饮料和零食，送到北区25栋', '13800138025', 35.00, 1, 4, '2026-02-15 10:00:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (75, 'ORD20260215075', 27, 34, '北区一食堂', '帮我带一份包子和豆浆', '13800138026', 8.00, 1, 5, '2026-02-15 10:44:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (76, 'ORD20260215076', 28, 35, '圆通快递站', '取一个快递，送到南区27栋', '13800138027', 3.00, 1, 6, '2026-02-15 09:04:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (77, 'ORD20260215077', 29, 36, '教学楼C座', '打印50份试卷，单面黑白', '13800138028', 25.00, 1, 3, '2026-02-14 22:28:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (78, 'ORD20260215078', 30, 37, '水果店', '买一个西瓜，送到东区29栋', '13800138029', 30.00, 1, 4, '2026-02-14 22:35:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (79, 'ORD20260215079', 31, 38, '南区二食堂', '帮我带一份炒饭，加火腿肠', '13800138030', 12.00, 1, 5, '2026-02-15 06:55:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (80, 'ORD20260215080', 32, 39, '中通快递站', '取一个快递，送到西区31栋', '13800138031', 4.00, 1, 2, '2026-02-15 00:11:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (81, 'ORD20260215081', 33, 40, '图书馆打印室', '打印10份简历，彩色', '13800138032', 15.00, 1, 3, '2026-02-15 13:34:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (82, 'ORD20260215082', 34, 41, '校园超市', '买一些学习用品，送到北区33栋', '13800138033', 28.00, 1, 4, '2026-02-15 04:42:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (83, 'ORD20260215083', 35, 42, '北区三食堂', '帮我带一份水饺，猪肉馅的', '13800138034', 16.00, 1, 5, '2026-02-14 16:12:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (84, 'ORD20260215084', 36, 43, '韵达快递站', '取两个快递，送到南区35栋', '13800138035', 6.00, 1, 6, '2026-02-15 04:17:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (85, 'ORD20260215085', 37, 44, '教学楼A座', '打印20份课件，双面打印', '13800138036', 8.00, 1, 3, '2026-02-15 06:13:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (86, 'ORD20260215086', 38, 45, '便利店', '买一些零食和饮料，送到东区37栋', '13800138037', 40.00, 1, 4, '2026-02-15 03:38:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (87, 'ORD20260215087', 39, 46, '南区一食堂', '帮我带一份套餐，一荤一素', '13800138038', 14.00, 1, 5, '2026-02-15 08:56:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (88, 'ORD20260215088', 40, 47, '顺丰快递点', '取一个快递，送到西区39栋', '13800138039', 5.00, 1, 2, '2026-02-14 19:06:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (89, 'ORD20260215089', 41, 48, '打印店', '打印一张证件照，一寸', '13800138040', 15.00, 1, 3, '2026-02-15 06:07:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (90, 'ORD20260215090', 42, 49, '校园超市', '买一些水果和牛奶，送到北区41栋', '13800138041', 55.00, 1, 4, '2026-02-15 06:41:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (91, 'ORD20260215091', 43, 2, '北区二食堂', '帮我带一份面条，加煎蛋', '13800138042', 13.00, 1, 5, '2026-02-15 00:29:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (92, 'ORD20260215092', 44, 3, '申通快递站', '取一个快递，送到南区43栋', '13800138043', 4.00, 1, 6, '2026-02-14 15:42:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (93, 'ORD20260215093', 45, 4, '教学楼B座', '打印40份资料，单面打印', '13800138044', 12.00, 1, 3, '2026-02-15 14:29:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (94, 'ORD20260215094', 46, 5, '水果店', '买一些草莓，送到东区45栋', '13800138045', 35.00, 1, 4, '2026-02-15 10:42:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (95, 'ORD20260215095', 47, 6, '南区三食堂', '帮我带一份快餐，两荤一素', '13800138046', 18.00, 1, 5, '2026-02-14 19:26:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (96, 'ORD20260215096', 48, 7, '京东快递站', '取一个快递，送到西区47栋', '13800138047', 5.00, 1, 2, '2026-02-15 02:28:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (97, 'ORD20260215097', 49, 8, '图书馆打印室', '打印15份论文，装订', '13800138048', 22.00, 1, 3, '2026-02-15 11:24:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (98, 'ORD20260215098', 50, 9, '校园超市', '买一些日用品和零食，送到北区49栋', '13800138049', 50.00, 1, 4, '2026-02-15 11:02:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (99, 'ORD20260215099', 2, 10, '北区一食堂', '帮我带一份早餐，包子和粥', '13800138001', 10.00, 1, 5, '2026-02-15 06:22:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (100, 'ORD20260215100', 3, 11, '南区快递站', '取一个顺丰快递，送到南区3栋', '13800138002', 5.00, 1, 6, '2026-02-15 08:05:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (101, 'ORD20260215101', 4, 12, '图书馆二楼', '打印50份复习资料，双面黑白', '13800138003', 15.00, 2, 3, '2026-02-15 06:45:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (102, 'ORD20260215102', 5, 13, '东区超市', '买两瓶矿泉水和一包纸巾，送到东区5栋', '13800138004', 12.00, 2, 4, '2026-02-14 18:54:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (103, 'ORD20260215103', 6, 14, '西区校门', '帮我把这份文件送到行政楼教务处', '13800138005', 8.00, 2, 1, '2026-02-15 11:35:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (104, 'ORD20260215104', 7, 15, '北区1栋楼下', '取一个圆通快递，送到北区7栋', '13800138006', 3.00, 2, 2, '2026-02-15 10:40:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (105, 'ORD20260215105', 8, 16, '南区二食堂', '帮我带一份糖醋排骨饭，不要香菜', '13800138007', 16.80, 2, 5, '2026-02-15 03:56:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (106, 'ORD20260215106', 9, 17, '菜鸟驿站', '取两个快递，都是中通的，送到北区9栋', '13800138008', 6.00, 2, 6, '2026-02-14 21:03:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (107, 'ORD20260215107', 10, 18, '教学楼A座', '打印100份PPT，彩色单面', '13800138009', 50.00, 2, 3, '2026-02-15 06:51:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (108, 'ORD20260215108', 11, 19, '校园超市', '买一箱牛奶和一些零食，送到南区11栋', '13800138010', 65.00, 2, 4, '2026-02-15 04:29:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (109, 'ORD20260215109', 12, 20, '北区三食堂', '帮我带一份兰州拉面，加蛋', '13800138011', 14.00, 2, 5, '2026-02-15 11:15:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (110, 'ORD20260215110', 13, 21, '顺丰快递点', '取一个大件快递，送到东区13栋', '13800138012', 10.00, 2, 6, '2026-02-15 04:11:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (111, 'ORD20260215111', 14, 22, '图书馆打印室', '打印20份论文，装订成册', '13800138013', 30.00, 2, 3, '2026-02-14 20:36:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (112, 'ORD20260215112', 15, 23, '西区水果店', '买一些苹果和香蕉，送到西区15栋', '13800138014', 25.00, 2, 4, '2026-02-15 03:51:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (113, 'ORD20260215113', 16, 24, '南区一食堂', '帮我带一份麻辣香锅，微辣', '13800138015', 22.00, 2, 5, '2026-02-14 14:51:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (114, 'ORD20260215114', 17, 25, '韵达快递站', '取一个快递，送到北区17栋', '13800138016', 4.00, 2, 2, '2026-02-15 00:06:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (115, 'ORD20260215115', 18, 26, '教学楼B座', '打印30份作业，双面打印', '13800138017', 9.00, 2, 3, '2026-02-15 13:20:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (116, 'ORD20260215116', 19, 27, '校园便利店', '买一些日用品，送到南区19栋', '13800138018', 45.00, 2, 4, '2026-02-15 03:45:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (117, 'ORD20260215117', 20, 28, '北区二食堂', '帮我带一份盖浇饭，土豆牛肉', '13800138019', 15.00, 2, 5, '2026-02-15 12:08:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (118, 'ORD20260215118', 21, 29, '申通快递点', '取两个快递，送到东区21栋', '13800138020', 7.00, 2, 6, '2026-02-15 10:49:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (119, 'ORD20260215119', 22, 30, '图书馆', '帮我占一个座位，靠窗的位置', '13800138021', 5.00, 2, 1, '2026-02-15 03:04:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (120, 'ORD20260215120', 23, 31, '南区三食堂', '帮我带一份麻辣烫，不要麻不要辣', '13800138022', 18.00, 2, 5, '2026-02-14 16:15:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (121, 'ORD20260215121', 24, 32, '京东快递站', '取一个快递，送到西区23栋', '13800138023', 4.00, 2, 2, '2026-02-15 09:28:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (122, 'ORD20260215122', 25, 33, '打印店', '打印一张海报，A3彩色', '13800138024', 20.00, 2, 3, '2026-02-15 07:58:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (123, 'ORD20260215123', 26, 34, '校园超市', '买一些饮料和零食，送到北区25栋', '13800138025', 35.00, 2, 4, '2026-02-14 20:48:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (124, 'ORD20260215124', 27, 35, '北区一食堂', '帮我带一份包子和豆浆', '13800138026', 8.00, 2, 5, '2026-02-14 17:31:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (125, 'ORD20260215125', 28, 36, '圆通快递站', '取一个快递，送到南区27栋', '13800138027', 3.00, 2, 6, '2026-02-15 10:34:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (126, 'ORD20260215126', 29, 37, '教学楼C座', '打印50份试卷，单面黑白', '13800138028', 25.00, 2, 3, '2026-02-15 09:39:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (127, 'ORD20260215127', 30, 38, '水果店', '买一个西瓜，送到东区29栋', '13800138029', 30.00, 2, 4, '2026-02-15 01:57:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (128, 'ORD20260215128', 31, 39, '南区二食堂', '帮我带一份炒饭，加火腿肠', '13800138030', 12.00, 2, 5, '2026-02-15 14:11:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (129, 'ORD20260215129', 32, 40, '中通快递站', '取一个快递，送到西区31栋', '13800138031', 4.00, 2, 2, '2026-02-15 02:27:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (130, 'ORD20260215130', 33, 41, '图书馆打印室', '打印10份简历，彩色', '13800138032', 15.00, 2, 3, '2026-02-15 03:06:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (131, 'ORD20260215131', 34, 42, '校园超市', '买一些学习用品，送到北区33栋', '13800138033', 28.00, 2, 4, '2026-02-14 17:32:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (132, 'ORD20260215132', 35, 43, '北区三食堂', '帮我带一份水饺，猪肉馅的', '13800138034', 16.00, 2, 5, '2026-02-14 15:47:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (133, 'ORD20260215133', 36, 44, '韵达快递站', '取两个快递，送到南区35栋', '13800138035', 6.00, 2, 6, '2026-02-15 11:40:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (134, 'ORD20260215134', 37, 45, '教学楼A座', '打印20份课件，双面打印', '13800138036', 8.00, 2, 3, '2026-02-14 20:24:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (135, 'ORD20260215135', 38, 46, '便利店', '买一些零食和饮料，送到东区37栋', '13800138037', 40.00, 2, 4, '2026-02-15 04:23:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (136, 'ORD20260215136', 39, 47, '南区一食堂', '帮我带一份套餐，一荤一素', '13800138038', 14.00, 2, 5, '2026-02-14 18:05:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (137, 'ORD20260215137', 40, 48, '顺丰快递点', '取一个快递，送到西区39栋', '13800138039', 5.00, 2, 2, '2026-02-14 14:43:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (138, 'ORD20260215138', 41, 49, '打印店', '打印一张证件照，一寸', '13800138040', 15.00, 2, 3, '2026-02-15 04:40:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (139, 'ORD20260215139', 42, 2, '校园超市', '买一些水果和牛奶，送到北区41栋', '13800138041', 55.00, 2, 4, '2026-02-15 12:35:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (140, 'ORD20260215140', 43, 3, '北区二食堂', '帮我带一份面条，加煎蛋', '13800138042', 13.00, 2, 5, '2026-02-15 10:21:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (141, 'ORD20260215141', 44, 4, '申通快递站', '取一个快递，送到南区43栋', '13800138043', 4.00, 2, 6, '2026-02-14 23:22:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (142, 'ORD20260215142', 45, 5, '教学楼B座', '打印40份资料，单面打印', '13800138044', 12.00, 2, 3, '2026-02-14 23:12:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (143, 'ORD20260215143', 46, 6, '水果店', '买一些草莓，送到东区45栋', '13800138045', 35.00, 2, 4, '2026-02-15 07:15:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (144, 'ORD20260215144', 47, 7, '南区三食堂', '帮我带一份快餐，两荤一素', '13800138046', 18.00, 2, 5, '2026-02-15 00:02:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (145, 'ORD20260215145', 48, 8, '京东快递站', '取一个快递，送到西区47栋', '13800138047', 5.00, 2, 2, '2026-02-15 11:49:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (146, 'ORD20260215146', 49, 9, '图书馆打印室', '打印15份论文，装订', '13800138048', 22.00, 2, 3, '2026-02-14 20:21:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (147, 'ORD20260215147', 50, 10, '校园超市', '买一些日用品和零食，送到北区49栋', '13800138049', 50.00, 2, 4, '2026-02-15 03:44:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (148, 'ORD20260215148', 2, 11, '北区一食堂', '帮我带一份早餐，包子和粥', '13800138001', 10.00, 2, 5, '2026-02-14 14:58:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (149, 'ORD20260215149', 3, 12, '南区快递站', '取一个顺丰快递，送到南区3栋', '13800138002', 5.00, 2, 6, '2026-02-15 01:02:42', NULL, NULL, NULL, 1);
INSERT INTO `order_info` VALUES (150, 'ORD20260215150', 4, 13, '图书馆二楼', '打印50份复习资料，双面黑白', '13800138003', 15.00, 2, 3, '2026-02-14 17:41:42', NULL, NULL, NULL, 1);

-- ----------------------------
-- Table structure for order_type
-- ----------------------------
DROP TABLE IF EXISTS `order_type`;
CREATE TABLE `order_type`  (
  `id` tinyint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单类型ID（主键）',
  `type_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型名称（如送货、取货等）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_name`(`type_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单类型字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_type
-- ----------------------------
INSERT INTO `order_type` VALUES (3, '帮助打印');
INSERT INTO `order_type` VALUES (5, '帮取外卖');
INSERT INTO `order_type` VALUES (4, '帮忙购物');
INSERT INTO `order_type` VALUES (6, '帮拿快递');
INSERT INTO `order_type` VALUES (2, '替课');
INSERT INTO `order_type` VALUES (1, '送货');

-- ----------------------------
-- Table structure for post_image
-- ----------------------------
DROP TABLE IF EXISTS `post_image`;
CREATE TABLE `post_image`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `post_id` bigint UNSIGNED NOT NULL COMMENT '帖子ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `sort_order` int UNSIGNED NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_img_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_img_post` FOREIGN KEY (`post_id`) REFERENCES `post_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_image
-- ----------------------------

-- ----------------------------
-- Table structure for post_info
-- ----------------------------
DROP TABLE IF EXISTS `post_info`;
CREATE TABLE `post_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '发布者用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `view_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览量',
  `reply_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '回复/评论数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `type_id` tinyint UNSIGNED NOT NULL DEFAULT 2 COMMENT '帖子类型ID（关联post_type表的id）',
  `deleted_at` datetime NULL DEFAULT NULL COMMENT '删除时间（NULL表示未删除）',
  `campus_id` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '校区ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_post_type`(`type_id` ASC) USING BTREE,
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_post_type` FOREIGN KEY (`type_id`) REFERENCES `post_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_info
-- ----------------------------
INSERT INTO `post_info` VALUES (1, 2, '求问高数作业最后一题怎么做？', '如题，卡在最后一步了，求大神解答！', 156, 5, '2026-02-10 23:12:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (2, 3, '出二手自行车一辆', '9成新，价格面议，有需要的联系。', 234, 8, '2026-02-10 16:59:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (3, 4, '求推荐学校附近好吃的外卖', '最近不知道吃什么了，大家有什么推荐吗？', 421, 15, '2026-02-12 00:44:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (4, 5, '图书馆占座问题讨论', '今天去图书馆又没位置了，大家怎么看占座这件事？', 567, 23, '2026-02-12 10:07:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (5, 6, '求组队参加数学建模比赛', '我们队现在有两个人，还差一个会编程的队友，有兴趣的联系我。', 189, 6, '2026-02-10 09:44:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (6, 7, '出售二手教材', '大一大二的教材都有，价格便宜，需要的同学联系。', 278, 10, '2026-02-13 03:45:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (7, 8, '求问学校附近哪里有修电脑的', '电脑坏了，想找个靠谱的地方修一下。', 145, 4, '2026-02-11 22:54:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (8, 9, '校园美食分享', '发现学校附近一家超好吃的店，推荐给大家！', 389, 12, '2026-02-11 16:38:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (9, 10, '求问选课建议', '下学期想选一些轻松点的课，大家有什么推荐吗？', 456, 18, '2026-02-13 23:53:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (10, 11, '求租校园附近的房子', '想在学校附近租个房子，有房源的联系我。', 234, 7, '2026-02-12 06:55:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (11, 12, '校园快递代取服务', '提供校园快递代取服务，价格优惠，需要的联系。', 312, 9, '2026-02-10 20:18:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (12, 13, '求问四六级备考经验', '马上要考四六级了，大家有什么备考经验吗？', 521, 21, '2026-02-08 18:09:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (13, 14, '出售二手吉他', '有一把二手吉他想出售，价格面议，有兴趣的联系。', 178, 5, '2026-02-09 15:15:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (14, 15, '校园活动分享', '最近学校有什么有趣的活动吗？大家分享一下。', 298, 11, '2026-02-13 07:13:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (15, 16, '求问考研备考经验', '准备考研，大家有什么备考经验和建议吗？', 612, 25, '2026-02-14 23:41:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (16, 17, '出售二手自行车', '有一辆二手自行车想出售，价格便宜，需要的联系。', 245, 8, '2026-02-12 10:09:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (17, 18, '求问学校附近哪里有健身房', '想健身，学校附近有什么好的健身房推荐吗？', 189, 6, '2026-02-15 13:05:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (18, 19, '校园学习分享', '分享一些学习方法和技巧，希望对大家有帮助。', 356, 14, '2026-02-10 20:24:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (19, 20, '求组队参加篮球赛', '想组队参加学校的篮球赛，有兴趣的同学联系我。', 223, 7, '2026-02-13 00:09:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (20, 21, '出售二手电脑', '有一台二手笔记本电脑想出售，配置不错，价格面议。', 412, 13, '2026-02-09 20:56:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (21, 22, '求问学校附近哪里有打印店', '想打印一些资料，学校附近有什么好的打印店推荐吗？', 156, 5, '2026-02-08 17:36:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (22, 23, '校园交友', '想认识一些新朋友，大家可以一起学习一起玩。', 489, 19, '2026-02-12 10:36:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (23, 24, '求问实习经验', '想找实习，大家有什么实习经验和建议吗？', 378, 15, '2026-02-13 09:36:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (24, 25, '出售二手书籍', '有一些二手书籍想出售，价格便宜，需要的联系。', 267, 9, '2026-02-14 01:35:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (25, 26, '求问学校附近哪里有理发店', '想理发，学校附近有什么好的理发店推荐吗？', 198, 6, '2026-02-14 12:32:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (26, 27, '校园旅游分享', '最近去了一个好玩的地方，分享给大家。', 334, 12, '2026-02-14 19:16:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (27, 28, '求组队参加英语演讲比赛', '想参加英语演讲比赛，有没有想一起组队的同学？', 189, 6, '2026-02-14 20:10:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (28, 29, '出售二手手机', '有一部二手手机想出售，价格面议，有兴趣的联系。', 398, 14, '2026-02-14 04:25:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (29, 30, '求问学校附近哪里有超市', '想买些东西，学校附近有什么大的超市吗？', 167, 5, '2026-02-10 19:00:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (30, 31, '校园音乐分享', '分享一些我喜欢的音乐，大家有什么好听的歌也推荐一下。', 423, 16, '2026-02-09 19:05:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (31, 32, '求问公务员备考经验', '准备考公务员，大家有什么备考经验和建议吗？', 512, 20, '2026-02-14 23:52:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (32, 33, '出售二手平板', '有一台二手平板想出售，价格便宜，需要的联系。', 289, 10, '2026-02-08 23:25:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (33, 34, '求问学校附近哪里有药店', '身体不舒服，想买点药，学校附近有药店吗？', 145, 4, '2026-02-12 06:54:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (34, 35, '校园电影分享', '最近看了一部好看的电影，推荐给大家。', 367, 13, '2026-02-11 21:39:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (35, 36, '求组队参加辩论赛', '想参加学校的辩论赛，有没有想一起组队的同学？', 212, 7, '2026-02-14 00:54:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (36, 37, '出售二手相机', '有一台二手相机想出售，价格面议，有兴趣的联系。', 445, 15, '2026-02-11 21:00:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (37, 38, '求问学校附近哪里有银行', '需要去银行办业务，学校附近有什么银行？', 178, 6, '2026-02-08 15:40:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (38, 39, '校园美食推荐', '发现学校食堂一家超好吃的窗口，推荐给大家！', 498, 18, '2026-02-13 00:44:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (39, 40, '求问教师资格证备考经验', '准备考教师资格证，大家有什么备考经验和建议吗？', 534, 22, '2026-02-09 14:02:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (40, 41, '出售二手耳机', '有一副二手耳机想出售，价格便宜，需要的联系。', 256, 9, '2026-02-14 05:22:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (41, 42, '求问学校附近哪里有网吧', '想上网，学校附近有什么好的网吧推荐吗？', 189, 6, '2026-02-12 18:08:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (42, 43, '校园运动分享', '最近喜欢上了运动，大家平时都喜欢做什么运动？', 345, 12, '2026-02-12 11:56:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (43, 44, '求组队参加羽毛球赛', '想组队参加学校的羽毛球赛，有兴趣的同学联系我。', 234, 8, '2026-02-08 14:42:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (44, 45, '出售二手键盘', '有一个二手键盘想出售，价格便宜，需要的联系。', 212, 7, '2026-02-10 23:03:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (45, 46, '求问学校附近哪里有水果店', '想买水果，学校附近有什么好的水果店推荐吗？', 167, 5, '2026-02-13 08:35:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (46, 47, '校园读书分享', '最近读了一本好书，分享给大家。', 389, 14, '2026-02-11 07:07:42', '2026-02-17 22:17:08', 2, NULL, 1);
INSERT INTO `post_info` VALUES (47, 48, '求问雅思备考经验', '准备考雅思，大家有什么备考经验和建议吗？', 567, 23, '2026-02-14 19:14:42', '2026-02-17 22:17:08', 1, NULL, 1);
INSERT INTO `post_info` VALUES (48, 49, '出售二手鼠标', '有一个二手鼠标想出售，价格便宜，需要的联系。', 198, 7, '2026-02-10 12:13:42', '2026-02-17 22:17:08', 3, NULL, 1);
INSERT INTO `post_info` VALUES (49, 50, '求问学校附近哪里有蛋糕店', '想买蛋糕，学校附近有什么好的蛋糕店推荐吗？', 145, 5, '2026-02-13 12:48:42', '2026-02-17 22:17:08', 1, NULL, 1);

-- ----------------------------
-- Table structure for post_type
-- ----------------------------
DROP TABLE IF EXISTS `post_type`;
CREATE TABLE `post_type`  (
  `id` tinyint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '帖子类型ID（主键）',
  `type_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子类型名称（如求助、闲聊、交易）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_name`(`type_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子类型字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_type
-- ----------------------------
INSERT INTO `post_type` VALUES (3, '交易');
INSERT INTO `post_type` VALUES (1, '求助');
INSERT INTO `post_type` VALUES (2, '闲聊');

-- ----------------------------
-- Table structure for private_message
-- ----------------------------
DROP TABLE IF EXISTS `private_message`;
CREATE TABLE `private_message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `conversation_id` bigint UNSIGNED NOT NULL COMMENT '所属会话ID',
  `sender_id` int UNSIGNED NOT NULL COMMENT '发送者ID',
  `receiver_id` int UNSIGNED NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `msg_type` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '消息类型：1-文本 2-图片',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-否 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_msg_conv`(`conversation_id` ASC) USING BTREE,
  INDEX `idx_msg_sender`(`sender_id` ASC) USING BTREE,
  INDEX `idx_msg_receiver`(`receiver_id` ASC) USING BTREE,
  CONSTRAINT `fk_msg_conv` FOREIGN KEY (`conversation_id`) REFERENCES `conversation` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_msg_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_msg_sender` FOREIGN KEY (`sender_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '私聊消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of private_message
-- ----------------------------

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `contact_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `address_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_addr_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_addr_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户手机号(11位)',
  `permission` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户权限：0-普通用户 1-管理员',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户收货地址',
  `deleted_at` datetime NULL DEFAULT NULL COMMENT '删除时间（NULL表示未删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account`(`account` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  CONSTRAINT `ck_account_format` CHECK (regexp_like(`account`,_utf8mb4'^[a-zA-Z0-9_-]+$'))
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, 'admin', '系统管理员', 'admin123456', '13800000000', 1, '行政楼', NULL);
INSERT INTO `user_info` VALUES (2, 'student001', '张同学', 'pass123456', '13800138001', 0, '北区1栋101', NULL);
INSERT INTO `user_info` VALUES (3, 'student002', '李同学', 'pass123456', '13800138002', 0, '北区2栋202', NULL);
INSERT INTO `user_info` VALUES (4, 'student003', '王同学', 'pass123456', '13800138003', 0, '南区3栋303', NULL);
INSERT INTO `user_info` VALUES (5, 'student004', '刘同学', 'pass123456', '13800138004', 0, '南区4栋404', NULL);
INSERT INTO `user_info` VALUES (6, 'student005', '陈同学', 'pass123456', '13800138005', 0, '东区5栋505', NULL);
INSERT INTO `user_info` VALUES (7, 'student006', '杨同学', 'pass123456', '13800138006', 0, '东区6栋606', NULL);
INSERT INTO `user_info` VALUES (8, 'student007', '赵同学', 'pass123456', '13800138007', 0, '西区7栋707', NULL);
INSERT INTO `user_info` VALUES (9, 'student008', '周同学', 'pass123456', '13800138008', 0, '西区8栋808', NULL);
INSERT INTO `user_info` VALUES (10, 'student009', '吴同学', 'pass123456', '13800138009', 0, '北区9栋909', NULL);
INSERT INTO `user_info` VALUES (11, 'student010', '郑同学', 'pass123456', '13800138010', 0, '北区10栋1010', NULL);
INSERT INTO `user_info` VALUES (12, 'student011', '孙同学', 'pass123456', '13800138011', 0, '南区11栋1111', NULL);
INSERT INTO `user_info` VALUES (13, 'student012', '马同学', 'pass123456', '13800138012', 0, '南区12栋1212', NULL);
INSERT INTO `user_info` VALUES (14, 'student013', '朱同学', 'pass123456', '13800138013', 0, '东区13栋1313', NULL);
INSERT INTO `user_info` VALUES (15, 'student014', '胡同学', 'pass123456', '13800138014', 0, '东区14栋1414', NULL);
INSERT INTO `user_info` VALUES (16, 'student015', '林同学', 'pass123456', '13800138015', 0, '西区15栋1515', NULL);
INSERT INTO `user_info` VALUES (17, 'student016', '何同学', 'pass123456', '13800138016', 0, '西区16栋1616', NULL);
INSERT INTO `user_info` VALUES (18, 'student017', '高同学', 'pass123456', '13800138017', 0, '北区17栋1717', NULL);
INSERT INTO `user_info` VALUES (19, 'student018', '罗同学', 'pass123456', '13800138018', 0, '北区18栋1818', NULL);
INSERT INTO `user_info` VALUES (20, 'student019', '谢同学', 'pass123456', '13800138019', 0, '南区19栋1919', NULL);
INSERT INTO `user_info` VALUES (21, 'student020', '宋同学', 'pass123456', '13800138020', 0, '南区20栋2020', NULL);
INSERT INTO `user_info` VALUES (22, 'student021', '唐同学', 'pass123456', '13800138021', 0, '东区21栋2121', NULL);
INSERT INTO `user_info` VALUES (23, 'student022', '许同学', 'pass123456', '13800138022', 0, '东区22栋2222', NULL);
INSERT INTO `user_info` VALUES (24, 'student023', '韩同学', 'pass123456', '13800138023', 0, '西区23栋2323', NULL);
INSERT INTO `user_info` VALUES (25, 'student024', '冯同学', 'pass123456', '13800138024', 0, '西区24栋2424', NULL);
INSERT INTO `user_info` VALUES (26, 'student025', '邓同学', 'pass123456', '13800138025', 0, '北区25栋2525', NULL);
INSERT INTO `user_info` VALUES (27, 'student026', '曹同学', 'pass123456', '13800138026', 0, '北区26栋2626', NULL);
INSERT INTO `user_info` VALUES (28, 'student027', '彭同学', 'pass123456', '13800138027', 0, '南区27栋2727', NULL);
INSERT INTO `user_info` VALUES (29, 'student028', '曾同学', 'pass123456', '13800138028', 0, '南区28栋2828', NULL);
INSERT INTO `user_info` VALUES (30, 'student029', '萧同学', 'pass123456', '13800138029', 0, '东区29栋2929', NULL);
INSERT INTO `user_info` VALUES (31, 'student030', '田同学', 'pass123456', '13800138030', 0, '东区30栋3030', NULL);
INSERT INTO `user_info` VALUES (32, 'student031', '董同学', 'pass123456', '13800138031', 0, '西区31栋3131', NULL);
INSERT INTO `user_info` VALUES (33, 'student032', '袁同学', 'pass123456', '13800138032', 0, '西区32栋3232', NULL);
INSERT INTO `user_info` VALUES (34, 'student033', '潘同学', 'pass123456', '13800138033', 0, '北区33栋3333', NULL);
INSERT INTO `user_info` VALUES (35, 'student034', '于同学', 'pass123456', '13800138034', 0, '北区34栋3434', NULL);
INSERT INTO `user_info` VALUES (36, 'student035', '蒋同学', 'pass123456', '13800138035', 0, '南区35栋3535', NULL);
INSERT INTO `user_info` VALUES (37, 'student036', '蔡同学', 'pass123456', '13800138036', 0, '南区36栋3636', NULL);
INSERT INTO `user_info` VALUES (38, 'student037', '余同学', 'pass123456', '13800138037', 0, '东区37栋3737', NULL);
INSERT INTO `user_info` VALUES (39, 'student038', '杜同学', 'pass123456', '13800138038', 0, '东区38栋3838', NULL);
INSERT INTO `user_info` VALUES (40, 'student039', '叶同学', 'pass123456', '13800138039', 0, '西区39栋3939', NULL);
INSERT INTO `user_info` VALUES (41, 'student040', '程同学', 'pass123456', '13800138040', 0, '西区40栋4040', NULL);
INSERT INTO `user_info` VALUES (42, 'student041', '苏同学', 'pass123456', '13800138041', 0, '北区41栋4141', NULL);
INSERT INTO `user_info` VALUES (43, 'student042', '魏同学', 'pass123456', '13800138042', 0, '北区42栋4242', NULL);
INSERT INTO `user_info` VALUES (44, 'student043', '吕同学', 'pass123456', '13800138043', 0, '南区43栋4343', NULL);
INSERT INTO `user_info` VALUES (45, 'student044', '丁同学', 'pass123456', '13800138044', 0, '南区44栋4444', NULL);
INSERT INTO `user_info` VALUES (46, 'student045', '任同学', 'pass123456', '13800138045', 0, '东区45栋4545', NULL);
INSERT INTO `user_info` VALUES (47, 'student046', '沈同学', 'pass123456', '13800138046', 0, '东区46栋4646', NULL);
INSERT INTO `user_info` VALUES (48, 'student047', '姚同学', 'pass123456', '13800138047', 0, '西区47栋4747', NULL);
INSERT INTO `user_info` VALUES (49, 'student048', '卢同学', 'pass123456', '13800138048', 0, '西区48栋4848', NULL);
INSERT INTO `user_info` VALUES (50, 'student049', '姜同学', 'pass123456', '13800138049', 0, '北区49栋4949', NULL);
INSERT INTO `user_info` VALUES (51, 'wo258108048', 'wo258108048', '$2a$10$pL0Mgmj4UXFQjZBZzZnd3O4/iGeLmqMl1s7LUe3Miizj0htdJwKNa', '18104417838', 0, NULL, NULL);

-- ----------------------------
-- Table structure for user_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_stats`;
CREATE TABLE `user_stats`  (
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `credit_score` int NOT NULL DEFAULT 100 COMMENT '信誉分',
  `order_count` int UNSIGNED NULL DEFAULT 0 COMMENT '完成订单数',
  `avg_rating` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '平均评分',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE,
  CONSTRAINT `fk_stats_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_stats
-- ----------------------------
INSERT INTO `user_stats` VALUES (1, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (2, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (3, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (4, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (5, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (6, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (7, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (8, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (9, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (10, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (11, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (12, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (13, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (14, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (15, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (16, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (17, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (18, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (19, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (20, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (21, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (22, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (23, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (24, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (25, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (26, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (27, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (28, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (29, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (30, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (31, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (32, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (33, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (34, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (35, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (36, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (37, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (38, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (39, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (40, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (41, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (42, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (43, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (44, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (45, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (46, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (47, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (48, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (49, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (50, 100, 0, 5.00, '2026-02-17 22:22:06');
INSERT INTO `user_stats` VALUES (51, 100, 0, 5.00, '2026-02-17 22:22:06');

-- ----------------------------
-- Procedure structure for AddColumnIfNotExists
-- ----------------------------
DROP PROCEDURE IF EXISTS `AddColumnIfNotExists`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `AddColumnIfNotExists`(
    IN tableName VARCHAR(64),
    IN columnName VARCHAR(64),
    IN columnDef VARCHAR(1024)
)
BEGIN
    DECLARE columnExists INT DEFAULT 0;
    -- 检查字段是否存在
    SELECT COUNT(*) INTO columnExists
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tableName
      AND COLUMN_NAME = columnName;
    
    -- 字段不存在则执行添加
    IF columnExists = 0 THEN
        SET @addSql = CONCAT('ALTER TABLE `', tableName, '` ADD COLUMN `', columnName, '` ', columnDef);
        PREPARE stmt FROM @addSql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for AddForeignKeyIfNotExists
-- ----------------------------
DROP PROCEDURE IF EXISTS `AddForeignKeyIfNotExists`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `AddForeignKeyIfNotExists`(
    IN tableName VARCHAR(64),
    IN fkName VARCHAR(64),
    IN fkDef VARCHAR(1024)
)
BEGIN
    DECLARE fkExists INT DEFAULT 0;
    -- 检查外键是否存在
    SELECT COUNT(*) INTO fkExists
    FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = tableName
      AND CONSTRAINT_NAME = fkName;
    
    -- 外键不存在则执行添加
    IF fkExists = 0 THEN
        SET @addSql = CONCAT('ALTER TABLE `', tableName, '` ADD CONSTRAINT `', fkName, '` ', fkDef);
        PREPARE stmt FROM @addSql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for AddIndexIfNotExists
-- ----------------------------
DROP PROCEDURE IF EXISTS `AddIndexIfNotExists`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `AddIndexIfNotExists`(
    IN tableName VARCHAR(64),
    IN indexName VARCHAR(64),
    IN indexDef VARCHAR(1024)
)
BEGIN
    DECLARE indexExists INT DEFAULT 0;
    -- 检查索引是否存在
    SELECT COUNT(*) INTO indexExists
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tableName
      AND INDEX_NAME = indexName;
    
    -- 索引不存在则执行添加
    IF indexExists = 0 THEN
        SET @addSql = CONCAT('ALTER TABLE `', tableName, '` ADD INDEX `', indexName, '` ', indexDef);
        PREPARE stmt FROM @addSql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;

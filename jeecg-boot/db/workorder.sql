/*
 Navicat Premium Data Transfer

 Source Server         : jeecgboot
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost:3306
 Source Schema         : workorder

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 04/09/2019 09:53:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for repair_info
-- ----------------------------
DROP TABLE IF EXISTS `repair_info`;
CREATE TABLE `repair_info`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'id',
  `adress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报修地址',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报修标题',
  `cont` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报修内容',
  `pic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
  `tel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报修人电话',
  `state_tag` tinyint(1) NULL DEFAULT 0 COMMENT '0：报修（报修）\r\n1：接收\r\n2：派工\r\n3：维修中\r\n4：维修完毕',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报修人id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报修人姓名',
  `vist_bz` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `acc_time` datetime(0) NULL DEFAULT NULL COMMENT '修理工接单时间',
  `send_time` datetime(0) NULL DEFAULT NULL COMMENT '报修提交时间',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始维修时间',
  `staff_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修理工id',
  `staff_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修理工姓名',
  `over_time` datetime(0) NULL DEFAULT NULL COMMENT '维修完成时间',
  `eval_degree` int(2) NULL DEFAULT NULL COMMENT '满意度（满分10）',
  `eval_cont` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论内容',
  `vist_time` datetime(0) NULL DEFAULT NULL COMMENT '备注时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '报修单创建时间',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报修单创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近更新时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最近一次更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of repair_info
-- ----------------------------
BEGIN;
INSERT INTO `repair_info` VALUES ('016dd996639950306cb040c7572fca86', 'fs', '阿凡达发', '放大的方式', 'files/20190831/9e62050a8b9c05fb796f10661afca2b1_1567212706961.jpg', NULL, 1, 'a75d45a015c44384a04449ee80dc3503', NULL, '发发发', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-08-31 08:52:03', 'admin', '2019-08-31 18:07:30', 'admin'), ('549e23d6765c839bbbf33d5e2eece1d9', '北京ant Desing 走马灯点击图片显示对应下标的图片 - caoya..._CSDN博客', 'ant Desing 走马灯点击图片显示对应下标的图片 - caoya..._CSDN博客', '马桶坏了', '1', NULL, 4, 'a75d45a015c44384a04449ee80dc3503', 'admin', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', NULL, '2019-08-30 14:32:09', 'admin', '2019-09-01 16:03:50', 'admin'), ('59cf07253282ccef753ba0781cb543f7', NULL, NULL, NULL, NULL, NULL, 0, 'a75d45a015c44384a04449ee80dc3503', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-09-04 01:00:25', 'admin', NULL, NULL), ('76eb4417cc299b0569cb314f4708a473', 'fs', '阿凡达发', '放大的方式', 'files/20190903/9e62050a8b9c05fb796f10661afca2b1_1567504566504.jpg', NULL, 0, 'a75d45a015c44384a04449ee80dc3503', NULL, '发发发', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-09-03 17:44:44', 'jeecg', '2019-09-03 17:56:09', 'admin'), ('7d86105499a51adb40f9ea4f8ef25d35', 'xinde', 'hehe', 'huaile', 'files/20190903/6a0a19addbc9969a990f15332db834da_1567503337231.jpg', '121', 0, 'a75d45a015c44384a04449ee80dc3503', NULL, 'kuailai', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-09-03 17:35:40', 'jeecg', NULL, NULL), ('9cb79690e3d735d30777fd404efa100c', 'sdfss', ';5656+', NULL, 'files/20190904/9d7d7a046267af68c899e29777e2d659_1567533658121.jpg', NULL, 1, 'a75d45a015c44384a04449ee80dc3503', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-09-04 02:01:08', 'jeecg', '2019-09-04 08:01:15', 'jeecg'), ('c8796513eddbf3787416dd5338bd22d1', 'fs', '阿凡达发', '放大的方式', 'files/20190903/76b4e910dc76cde85bc2112aba7ce0a3_1567503722050.jpg', NULL, 1, 'a75d45a015c44384a04449ee80dc3503', NULL, '发发发', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-09-03 17:42:04', 'jeecg', '2019-09-03 18:02:37', 'admin'), ('dd79d4425955360a36a3d6480f5346fb', '北京海淀ant Desing 走马灯点击图片显示对应下标的图片 - caoya..._CSDN博客', 'ant Desing 走马灯点击图片显示对应下标的图片 - caoya..._CSDN博客', '放大反抗拉萨发', NULL, NULL, 1, 'a75d45a015c44384a04449ee80dc3503', 'jeecg', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-08-30 14:34:38', 'admin', '2019-09-02 01:04:33', 'admin'), ('fa5ca0134f110b2f4e400a655db69438', '萧山', NULL, '发达的安抚放到发的', NULL, NULL, 1, 'e9ca23d68d884d4ebb19d07889727dae', 'admin', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2019-08-30 17:06:32', 'admin', '2019-09-02 01:04:33', 'admin');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

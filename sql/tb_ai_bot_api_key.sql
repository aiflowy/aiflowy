/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80400
 Source Host           : localhost:3306
 Source Schema         : aiflowy_copy

 Target Server Type    : MySQL
 Target Server Version : 80400
 File Encoding         : 65001

 Date: 22/04/2025 11:57:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_ai_bot_api_key
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_api_key`;
CREATE TABLE `tb_ai_bot_api_key`  (
  `id` bigint(0) NOT NULL COMMENT 'id',
  `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'apiKey',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '状态1启用 2失效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

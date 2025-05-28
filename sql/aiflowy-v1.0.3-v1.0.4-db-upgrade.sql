SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_ai_plugin_categories
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin_categories`;
CREATE TABLE `tb_ai_plugin_categories`  (
                                            `id` int(0) NOT NULL AUTO_INCREMENT,
                                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for tb_ai_plugin_category_relation
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin_category_relation`;
CREATE TABLE `tb_ai_plugin_category_relation`  (
                                                   `category_id` int(0) NOT NULL,
                                                   `plugin_id` bigint(0) NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for tb_sys_token
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_token`;
CREATE TABLE `tb_sys_token`  (
                                 `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '生成的 token 值',
                                 `user_id` bigint(0) NOT NULL COMMENT '关联用户ID',
                                 `expire_time` datetime(0) NOT NULL COMMENT '过期时间',
                                 `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                 `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Token 描述（可选）',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `uk_token`(`token`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'iframe 嵌入用 Token 表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 修改 quartz 表前缀 开始
-- ----------------------------
RENAME TABLE qrtz_blob_triggers TO tb_qrtz_blob_triggers;
RENAME TABLE qrtz_calendars TO tb_qrtz_calendars;
RENAME TABLE qrtz_cron_triggers TO tb_qrtz_cron_triggers;
RENAME TABLE qrtz_fired_triggers TO tb_qrtz_fired_triggers;
RENAME TABLE qrtz_job_details TO tb_qrtz_job_details;
RENAME TABLE qrtz_locks TO tb_qrtz_locks;
RENAME TABLE qrtz_paused_trigger_grps TO tb_qrtz_paused_trigger_grps;
RENAME TABLE qrtz_scheduler_state TO tb_qrtz_scheduler_state;
RENAME TABLE qrtz_simple_triggers TO tb_qrtz_simple_triggers;
RENAME TABLE qrtz_simprop_triggers TO tb_qrtz_simprop_triggers;
RENAME TABLE qrtz_triggers TO tb_qrtz_triggers;
-- ----------------------------
-- 修改 quartz 表前缀 结束
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `aiflowy-copy`.`tb_sys_menu`(`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (284467996239060992, 259168688849412096, 0, 'token', 'sys/sysToken', '', 'BarChartOutlined', 1, '', 31, 0, '2025-05-26 12:35:15', 1, '2025-05-28 09:23:15', 1, '', 0);


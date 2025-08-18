SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for TB_QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `TB_QRTZ_BLOB_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `BLOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `SCHED_NAME`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `TB_QRTZ_BLOB_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `TB_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_CALENDARS`;
CREATE TABLE `TB_QRTZ_CALENDARS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_CRON_TRIGGERS`;
CREATE TABLE `TB_QRTZ_CRON_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `TB_QRTZ_CRON_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `TB_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `TB_QRTZ_FIRED_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `ENTRY_ID` varchar(95) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `FIRED_TIME` bigint(0) NOT NULL,
  `SCHED_TIME` bigint(0) NOT NULL,
  `PRIORITY` int(0) NOT NULL,
  `STATE` varchar(16) CHARACTER SET utf8mb4 NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TRIG_INST_NAME`(`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE,
  INDEX `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY`(`SCHED_NAME`, `INSTANCE_NAME`, `REQUESTS_RECOVERY`) USING BTREE,
  INDEX `IDX_QRTZ_FT_J_G`(`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_JG`(`SCHED_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_T_G`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TG`(`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_JOB_DETAILS`;
CREATE TABLE `TB_QRTZ_JOB_DETAILS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8mb4 NOT NULL,
  `IS_DURABLE` varchar(1) CHARACTER SET utf8mb4 NOT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 NOT NULL,
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8mb4 NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 NOT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_J_REQ_RECOVERY`(`SCHED_NAME`, `REQUESTS_RECOVERY`) USING BTREE,
  INDEX `IDX_QRTZ_J_GRP`(`SCHED_NAME`, `JOB_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_LOCKS`;
CREATE TABLE `TB_QRTZ_LOCKS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `LOCK_NAME` varchar(40) CHARACTER SET utf8mb4 NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `TB_QRTZ_PAUSED_TRIGGER_GRPS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_SCHEDULER_STATE`;
CREATE TABLE `TB_QRTZ_SCHEDULER_STATE`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `LAST_CHECKIN_TIME` bigint(0) NOT NULL,
  `CHECKIN_INTERVAL` bigint(0) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `TB_QRTZ_SIMPLE_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `REPEAT_COUNT` bigint(0) NOT NULL,
  `REPEAT_INTERVAL` bigint(0) NOT NULL,
  `TIMES_TRIGGERED` bigint(0) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `TB_QRTZ_SIMPLE_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `TB_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `TB_QRTZ_SIMPROP_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `STR_PROP_1` varchar(512) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `STR_PROP_2` varchar(512) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `STR_PROP_3` varchar(512) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `INT_PROP_1` int(0) NULL DEFAULT NULL,
  `INT_PROP_2` int(0) NULL DEFAULT NULL,
  `LONG_PROP_1` bigint(0) NULL DEFAULT NULL,
  `LONG_PROP_2` bigint(0) NULL DEFAULT NULL,
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `TB_QRTZ_SIMPROP_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `TB_QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TB_QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `TB_QRTZ_TRIGGERS`;
CREATE TABLE `TB_QRTZ_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(0) NULL DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(0) NULL DEFAULT NULL,
  `PRIORITY` int(0) NULL DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8mb4 NOT NULL,
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8mb4 NOT NULL,
  `START_TIME` bigint(0) NOT NULL,
  `END_TIME` bigint(0) NULL DEFAULT NULL,
  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `MISFIRE_INSTR` smallint(0) NULL DEFAULT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_J`(`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_JG`(`SCHED_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_C`(`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE,
  INDEX `IDX_QRTZ_T_G`(`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_STATE`(`SCHED_NAME`, `TRIGGER_STATE`) USING BTREE,
  INDEX `IDX_QRTZ_T_N_STATE`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`, `TRIGGER_STATE`) USING BTREE,
  INDEX `IDX_QRTZ_T_N_G_STATE`(`SCHED_NAME`, `TRIGGER_GROUP`, `TRIGGER_STATE`) USING BTREE,
  INDEX `IDX_QRTZ_T_NEXT_FIRE_TIME`(`SCHED_NAME`, `NEXT_FIRE_TIME`) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST`(`SCHED_NAME`, `TRIGGER_STATE`, `NEXT_FIRE_TIME`) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_MISFIRE`(`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE`(`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`, `TRIGGER_STATE`) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP`(`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`, `TRIGGER_GROUP`, `TRIGGER_STATE`) USING BTREE,
  CONSTRAINT `TB_QRTZ_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `TB_QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot`;
CREATE TABLE `tb_ai_bot`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键ID',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '别名',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `llm_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'LLM ID',
  `llm_options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'LLM选项',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选项',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
  `modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '修改者ID',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tb_ai_bot_alias_uindex`(`alias`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_bot_conversation_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_conversation_message`;
CREATE TABLE `tb_ai_bot_conversation_message`  (
  `session_id` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '会话id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话标题',
  `bot_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT 'botid',
  `account_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `created` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`session_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_knowledge`;
CREATE TABLE `tb_ai_bot_knowledge`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `bot_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `knowledge_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_llm
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_llm`;
CREATE TABLE `tb_ai_bot_llm`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `bot_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `llm_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_message`;
CREATE TABLE `tb_ai_bot_message`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `bot_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT 'Bot ID',
  `account_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '关联的账户ID',
  `session_id` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '会话ID',
  `role` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 NULL,
  `image` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `prompt_tokens` int(0) NULL DEFAULT NULL,
  `completion_tokens` int(0) NULL DEFAULT NULL,
  `total_tokens` int(0) NULL DEFAULT NULL,
  `functions` text CHARACTER SET utf8mb4 NULL COMMENT '方法定义',
  `options` text CHARACTER SET utf8mb4 NULL,
  `created` datetime(0) NULL DEFAULT NULL,
  `modified` datetime(0) NULL DEFAULT NULL,
  `is_external_msg` int(0) NULL DEFAULT NULL COMMENT '1是external 消息，0: bot页面消息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `bot_id`(`bot_id`) USING BTREE,
  INDEX `account_id`(`account_id`) USING BTREE,
  INDEX `session_id`(`session_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = 'Bot 消息记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_plugins
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_plugins`;
CREATE TABLE `tb_ai_bot_plugins`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `bot_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `plugin_tool_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_workflow
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_workflow`;
CREATE TABLE `tb_ai_bot_workflow`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `bot_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `workflow_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_chat_message`;
CREATE TABLE `tb_ai_chat_message`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `topic_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `role` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 NULL,
  `image` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `prompt_tokens` int(0) NULL DEFAULT NULL,
  `completion_tokens` int(0) NULL DEFAULT NULL,
  `total_tokens` int(0) NULL DEFAULT NULL,
  `tools` text CHARACTER SET utf8mb4 NULL,
  `options` text CHARACTER SET utf8mb4 NULL,
  `created` datetime(0) NULL DEFAULT NULL,
  `modified` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `topic_id`(`topic_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = 'AI 消息记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_chat_topic
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_chat_topic`;
CREATE TABLE `tb_ai_chat_topic`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `account_id` bigint(0) UNSIGNED NULL DEFAULT NULL,
  `title` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL,
  `created` datetime(0) NULL DEFAULT NULL,
  `modified` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `account_id`(`account_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = 'AI 话题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_document
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document`;
CREATE TABLE `tb_ai_document`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `knowledge_id` bigint(0) UNSIGNED NOT NULL COMMENT '知识库ID',
  `document_type` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '文档类型 pdf/word/aieditor 等',
  `document_path` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '文档路径',
  `title` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 NULL COMMENT '内容',
  `content_type` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '内容类型',
  `slug` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT 'URL 别名',
  `order_no` int(0) NULL DEFAULT NULL COMMENT '排序序号',
  `options` text CHARACTER SET utf8mb4 NULL COMMENT '其他配置项',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint(0) NULL DEFAULT NULL COMMENT '创建人ID',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '最后的修改时间',
  `modified_by` bigint(0) NULL DEFAULT NULL COMMENT '最后的修改人的ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `knowledge_id`(`knowledge_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '文档' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_document_chunk
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document_chunk`;
CREATE TABLE `tb_ai_document_chunk`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `document_id` bigint(0) UNSIGNED NOT NULL COMMENT '文档ID',
  `knowledge_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '知识库ID',
  `content` text CHARACTER SET utf8mb4 NULL COMMENT '分块内容',
  `sorting` int(0) UNSIGNED NULL DEFAULT NULL COMMENT '分割顺序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_document_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document_history`;
CREATE TABLE `tb_ai_document_history`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `document_id` bigint(0) NULL DEFAULT NULL COMMENT '修改的文档ID',
  `old_title` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '旧标题',
  `new_title` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '新标题',
  `old_content` text CHARACTER SET utf8mb4 NULL COMMENT '旧内容',
  `new_content` text CHARACTER SET utf8mb4 NULL COMMENT '新内容',
  `old_document_type` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '旧的文档类型',
  `new_document_type` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '新的额文档类型',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint(0) NULL DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_knowledge`;
CREATE TABLE `tb_ai_knowledge`  (
  `id` bigint UNSIGNED NOT NULL,
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL COMMENT '别名',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICON',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `slug` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'URL 别名',
  `vector_store_enable` tinyint(1) NULL DEFAULT NULL COMMENT '是否启用向量存储',
  `vector_store_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量数据库类型',
  `vector_store_collection` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量数据库集合',
  `vector_store_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '向量数据库配置',
  `vector_embed_llm_id` bigint NULL DEFAULT NULL COMMENT 'Embedding 模型ID',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建用户ID',
  `modified` datetime NULL DEFAULT NULL COMMENT '最后一次修改时间',
  `modified_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '最后一次修改用户ID',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他配置',
  `rerank_llm_id` bigint NULL DEFAULT NULL COMMENT '重排模型id',
  `search_engine_enable` tinyint(1) NULL DEFAULT NULL COMMENT '是否启用搜索引擎',
  `english_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tb_ai_knowledge_alias_uindex`(`alias`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_llm
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_llm`;
CREATE TABLE `tb_ai_llm`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `dept_id` bigint(0) UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint(0) UNSIGNED NOT NULL COMMENT '租户ID',
  `title` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '标题或名称',
  `brand` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '品牌',
  `icon` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT 'ICON',
  `description` varchar(512) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '描述',
  `support_chat` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持对话',
  `support_function_calling` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持方法调用',
  `support_embed` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持向量化',
  `support_reranker` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持重排',
  `support_text_to_image` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持文字生成图片',
  `support_image_to_image` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持图片生成图片',
  `support_text_to_audio` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持文字生成语音',
  `support_audio_to_audio` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持语音生成语音',
  `support_text_to_video` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持文字生成视频',
  `support_image_to_video` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持图片生成视频',
  `llm_endpoint` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '大模型请求地址',
  `llm_model` varchar(64) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '大模型名称',
  `llm_api_key` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '大模型 API KEY',
  `llm_extra_config` text CHARACTER SET utf8mb4 NULL COMMENT '大模型其他属性配置',
  `options` text CHARACTER SET utf8mb4 NULL COMMENT '其他配置内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_plugin
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin`;
CREATE TABLE `tb_ai_plugin`  (
  `id` bigint NOT NULL COMMENT '插件id',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL  COMMENT '别名',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `type` int NULL DEFAULT NULL COMMENT '类型',
  `base_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '基础URL',
  `auth_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '认证方式  【apiKey/none】',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标地址',
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '认证参数位置 【headers, query】',
  `headers` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求头',
  `token_key` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'token键',
  `token_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'token值',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门id',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tb_ai_plugin_alias_uindex`(`alias`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_plugin_categories
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin_categories`;
CREATE TABLE `tb_ai_plugin_categories`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
  `created_at` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_plugin_category_relation
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin_category_relation`;
CREATE TABLE `tb_ai_plugin_category_relation`  (
  `category_id` int(0) NOT NULL,
  `plugin_id` bigint(0) NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_plugin_tool
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin_tool`;
CREATE TABLE `tb_ai_plugin_tool`  (
  `id` bigint(0) NOT NULL COMMENT '插件工具id',
  `plugin_id` bigint(0) NOT NULL COMMENT '插件id',
  `name` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '描述',
  `base_path` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '基础路径',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `status` int(0) NULL DEFAULT NULL COMMENT '是否启用',
  `input_data` text CHARACTER SET utf8mb4 NULL COMMENT '输入参数',
  `output_data` text CHARACTER SET utf8mb4 NULL COMMENT '输出参数',
  `request_method` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '请求方式【Post, Get, Put, Delete】',
  `service_status` int(0) NULL DEFAULT NULL COMMENT '服务状态[0 下线 1 上线]',
  `debug_status` int(0) NULL DEFAULT NULL COMMENT '调试状态【0失败 1成功】',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_plugins
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugins`;
CREATE TABLE `tb_ai_plugins`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint(0) UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint(0) UNSIGNED NOT NULL COMMENT '租户ID',
  `plugin_type` tinyint(0) NOT NULL DEFAULT 1 COMMENT '插件类型',
  `plugin_name` varchar(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '插件名称',
  `plugin_desc` varchar(300) CHARACTER SET utf8mb4 NOT NULL COMMENT '插件描述',
  `options` text CHARACTER SET utf8mb4 NULL COMMENT '插件配置',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime(0) NOT NULL COMMENT '创建时间',
  `created_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime(0) NOT NULL COMMENT '修改时间',
  `modified_by` bigint(0) UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标识',
  `icon` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '图标',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '插件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_workflow
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_workflow`;
CREATE TABLE `tb_ai_workflow`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'ID 主键',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL  COMMENT '别名',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICON',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '工作流设计的 JSON 内容',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `modified` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  `modified_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '最后修改的人',
  `english_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tb_ai_workflow_alias_uindex`(`alias`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account`;
CREATE TABLE `tb_sys_account`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint(0) UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint(0) UNSIGNED NOT NULL COMMENT '租户ID',
  `login_name` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '登录账号',
  `password` varchar(128) CHARACTER SET utf8mb4 NOT NULL COMMENT '密码',
  `account_type` tinyint(0) NOT NULL DEFAULT 0 COMMENT '账户类型',
  `nickname` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '昵称',
  `mobile` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '手机电话',
  `email` varchar(64) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '邮件',
  `avatar` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '账户头像',
  `data_scope` int(0) NULL DEFAULT 1 COMMENT '数据权限类型',
  `dept_id_list` json NULL COMMENT '自定义部门权限',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime(0) NOT NULL COMMENT '创建时间',
  `created_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime(0) NOT NULL COMMENT '修改时间',
  `modified_by` bigint(0) UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_login_name`(`login_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_account_position
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account_position`;
CREATE TABLE `tb_sys_account_position`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `account_id` bigint(0) UNSIGNED NOT NULL COMMENT '用户ID',
  `position_id` bigint(0) UNSIGNED NOT NULL COMMENT '职位ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '用户-职位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_account_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account_role`;
CREATE TABLE `tb_sys_account_role`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `account_id` bigint(0) UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` bigint(0) UNSIGNED NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '用户-角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_api_key
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_api_key`;
CREATE TABLE `tb_sys_api_key`  (
  `id` bigint(0) NOT NULL COMMENT 'id',
  `api_key` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT 'apiKey',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '状态1启用 2失效',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '部门id',
  `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT '租户id',
  `expired_at` datetime(0) NULL DEFAULT NULL COMMENT '失效时间',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dept`;
CREATE TABLE `tb_sys_dept`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `tenant_id` bigint(0) UNSIGNED NOT NULL COMMENT '租户ID',
  `parent_id` bigint(0) UNSIGNED NOT NULL COMMENT '父级ID',
  `ancestors` varchar(500) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '父级部门ID集合',
  `dept_name` varchar(128) CHARACTER SET utf8mb4 NOT NULL COMMENT '部门名称',
  `dept_code` varchar(64) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '部门编码',
  `sort_no` int(0) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime(0) NOT NULL COMMENT '创建时间',
  `created_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime(0) NOT NULL COMMENT '修改时间',
  `modified_by` bigint(0) UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dict`;
CREATE TABLE `tb_sys_dict`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '数据字典名称',
  `code` varchar(32) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '字典编码',
  `description` varchar(512) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '字典描述或备注',
  `dict_type` tinyint(0) NULL DEFAULT NULL COMMENT '字典类型 1 自定义字典、2 数据表字典、 3 枚举类字典、 4 系统字典（自定义 DictLoader）',
  `sort_no` int(0) NULL DEFAULT NULL COMMENT '排序编号',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '是否启用',
  `options` text CHARACTER SET utf8mb4 NULL COMMENT '扩展字典  存放 json',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `key`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '系统字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dict_item`;
CREATE TABLE `tb_sys_dict_item`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `dict_id` bigint(0) UNSIGNED NOT NULL COMMENT '归属哪个字典',
  `text` varchar(32) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '名称或内容',
  `value` varchar(32) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '值',
  `description` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '描述',
  `sort_no` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `css_content` text CHARACTER SET utf8mb4 NULL COMMENT 'css样式内容',
  `css_class` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT 'css样式类名',
  `remark` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '状态',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modified` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '数据字典内容' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_job
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_job`;
CREATE TABLE `tb_sys_job`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint(0) UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint(0) UNSIGNED NOT NULL COMMENT '租户ID',
  `job_name` varchar(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '任务名称',
  `job_type` int(0) NOT NULL COMMENT '任务类型',
  `job_params` text CHARACTER SET utf8mb4 NULL COMMENT '任务参数',
  `cron_expression` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT 'cron表达式',
  `allow_concurrent` int(0) NOT NULL DEFAULT 0 COMMENT '是否并发执行',
  `misfire_policy` int(0) NOT NULL DEFAULT 3 COMMENT '错过策略',
  `options` text CHARACTER SET utf8mb4 NULL COMMENT '其他配置',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime(0) NOT NULL COMMENT '创建时间',
  `created_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime(0) NOT NULL COMMENT '修改时间',
  `modified_by` bigint(0) UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '系统任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_job_log`;
CREATE TABLE `tb_sys_job_log`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `job_id` bigint(0) UNSIGNED NOT NULL COMMENT '任务ID',
  `job_name` varchar(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '任务名称',
  `job_params` text CHARACTER SET utf8mb4 NULL COMMENT '任务参数',
  `job_result` text CHARACTER SET utf8mb4 NULL COMMENT '执行结果',
  `error_info` text CHARACTER SET utf8mb4 NULL COMMENT '错误信息',
  `status` int(0) NOT NULL COMMENT '执行状态',
  `start_time` datetime(0) NOT NULL COMMENT '开始时间',
  `end_time` datetime(0) NOT NULL COMMENT '结束时间',
  `created` datetime(0) NOT NULL COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '系统任务日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_log`;
CREATE TABLE `tb_sys_log`  (
  `id` bigint(0) UNSIGNED NOT NULL,
  `account_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '操作人',
  `action_name` varchar(256) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '操作名称',
  `action_type` varchar(32) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '操作的类型',
  `action_class` varchar(100) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '操作涉及的类',
  `action_method` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '操作涉及的方法',
  `action_url` varchar(1024) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '操作涉及的 URL 地址',
  `action_ip` varchar(128) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '操作涉及的用户 IP 地址',
  `action_params` text CHARACTER SET utf8mb4 NULL COMMENT '操作请求参数',
  `action_body` text CHARACTER SET utf8mb4 NULL COMMENT '操作请求body',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '操作状态 1 成功 9 失败',
  `created` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_menu`;
CREATE TABLE `tb_sys_menu`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `parent_id` bigint(0) UNSIGNED NOT NULL COMMENT '父菜单id',
  `menu_type` int(0) NOT NULL COMMENT '菜单类型',
  `menu_title` varchar(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '菜单标题',
  `menu_url` varchar(200) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '菜单url',
  `component` varchar(200) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '组件路径',
  `menu_icon` varchar(200) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '图标/图片地址',
  `is_show` int(0) NOT NULL DEFAULT 1 COMMENT '是否显示',
  `permission_tag` varchar(50) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '权限标识',
  `sort_no` int(0) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime(0) NOT NULL COMMENT '创建时间',
  `created_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime(0) NOT NULL COMMENT '修改时间',
  `modified_by` bigint(0) UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_option
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_option`;
CREATE TABLE `tb_sys_option`  (
  `tenant_id` bigint(0) UNSIGNED NOT NULL COMMENT '租户ID',
  `key` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '配置KEY',
  `value` text CHARACTER SET utf8mb4 NULL COMMENT '配置内容',
  INDEX `uni_key`(`tenant_id`, `key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '系统配置信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_position
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_position`;
CREATE TABLE `tb_sys_position`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `tenant_id` bigint(0) UNSIGNED NOT NULL COMMENT '租户ID',
  `dept_id` bigint(0) UNSIGNED NOT NULL COMMENT '部门ID',
  `position_name` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '岗位名称',
  `position_code` varchar(64) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '岗位编码',
  `sort_no` int(0) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime(0) NOT NULL COMMENT '创建时间',
  `created_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime(0) NOT NULL COMMENT '修改时间',
  `modified_by` bigint(0) UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '职位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role`;
CREATE TABLE `tb_sys_role`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `tenant_id` bigint(0) UNSIGNED NOT NULL COMMENT '租户ID',
  `role_name` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '角色名称',
  `role_key` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '角色标识',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime(0) NOT NULL COMMENT '创建时间',
  `created_by` bigint(0) UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime(0) NOT NULL COMMENT '修改时间',
  `modified_by` bigint(0) UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_tenant_role`(`tenant_id`, `role_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '系统角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role_menu`;
CREATE TABLE `tb_sys_role_menu`  (
  `id` bigint(0) UNSIGNED NOT NULL COMMENT '主键',
  `role_id` bigint(0) UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` bigint(0) UNSIGNED NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '角色-菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_token
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_token`;
CREATE TABLE `tb_sys_token`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `token` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '生成的 token 值',
  `user_id` bigint(0) NOT NULL COMMENT '关联用户ID',
  `expire_time` datetime(0) NOT NULL COMMENT '过期时间',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `description` varchar(255) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT 'Token 描述（可选）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_token`(`token`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COMMENT = 'iframe 嵌入用 Token 表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_resource. since v1.1.0
-- ----------------------------
CREATE TABLE `tb_ai_resource` (
  `id` bigint unsigned NOT NULL COMMENT '主键',
  `dept_id` bigint unsigned NOT NULL COMMENT '部门ID',
  `tenant_id` bigint unsigned NOT NULL COMMENT '租户ID',
  `resource_type` int NOT NULL COMMENT '素材类型',
  `resource_name` varchar(100) NOT NULL COMMENT '素材名称',
  `suffix` varchar(10) NOT NULL COMMENT '后缀',
  `resource_url` varchar(200) NOT NULL COMMENT '素材地址',
  `origin` int NOT NULL DEFAULT '0' COMMENT '素材来源',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint unsigned NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint unsigned NOT NULL COMMENT '修改者',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '扩展项',
  `is_deleted` tinyint DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='素材库';

-- v1.1.2 ddl start --
-- ----------------------------
-- Table structure for tb_ai_bot_api_key since v1.1.2
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_api_key`;
CREATE TABLE `tb_ai_bot_api_key`  (
  `id` bigint NOT NULL COMMENT 'id',
  `api_key` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'apiKey，请勿手动修改！',
  `bot_id` bigint NOT NULL COMMENT 'botId',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '加密botId，生成apiKey的盐',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '预留拓展配置的字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'bot apiKey 表' ROW_FORMAT = Dynamic;
-- v1.1.2 ddl end --

-- v1.1.3 ddl start  -----------

-- ----------------------------
-- Table structure for tb_datacenter_table since v1.1.3
-- ----------------------------
DROP TABLE IF EXISTS `tb_datacenter_table`;
CREATE TABLE `tb_datacenter_table`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `table_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数据表名',
  `table_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '数据表描述',
  `actual_table` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物理表名',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展项',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据中枢表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_datacenter_table_fields since v1.1.3
-- ----------------------------
DROP TABLE IF EXISTS `tb_datacenter_table_fields`;
CREATE TABLE `tb_datacenter_table_fields`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `table_id` bigint UNSIGNED NOT NULL COMMENT '数据表ID',
  `field_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字段名称',
  `field_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字段描述',
  `field_type` int NOT NULL COMMENT '字段类型',
  `required` int NOT NULL DEFAULT 0 COMMENT '是否必填',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展项',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据中枢字段表' ROW_FORMAT = Dynamic;

-- v1.1.3 ddl end --------------

SET FOREIGN_KEY_CHECKS = 1;

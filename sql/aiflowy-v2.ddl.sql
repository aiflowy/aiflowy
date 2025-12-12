SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_ai_bot
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot`;
CREATE TABLE `tb_ai_bot`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键ID',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '别名',
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
-- Table structure for tb_ai_bot_api_key
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_api_key`;
CREATE TABLE `tb_ai_bot_api_key`  (
  `id` bigint NOT NULL COMMENT 'id',
  `api_key` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'apiKey，请勿手动修改！',
  `bot_id` bigint NOT NULL COMMENT 'botId',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '加密botId，生成apiKey的盐',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '预留拓展配置的字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'bot apiKey 表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_bot_conversation_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_conversation_message`;
CREATE TABLE `tb_ai_bot_conversation_message`  (
  `session_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话标题',
  `bot_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'botid',
  `account_id` bigint UNSIGNED NULL DEFAULT NULL,
  `created` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`session_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_bot_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_knowledge`;
CREATE TABLE `tb_ai_bot_knowledge`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `bot_id` bigint UNSIGNED NULL DEFAULT NULL,
  `knowledge_id` bigint UNSIGNED NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_bot_llm
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_llm`;
CREATE TABLE `tb_ai_bot_llm`  (
  `id` bigint UNSIGNED NOT NULL,
  `bot_id` bigint UNSIGNED NULL DEFAULT NULL,
  `llm_id` bigint UNSIGNED NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_bot_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_message`;
CREATE TABLE `tb_ai_bot_message`  (
  `id` bigint UNSIGNED NOT NULL,
  `bot_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'Bot ID',
  `account_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联的账户ID',
  `session_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话ID',
  `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created` datetime NULL DEFAULT NULL,
  `modified` datetime NULL DEFAULT NULL,
  `is_external_msg` int NULL DEFAULT NULL COMMENT '1是external 消息，0: bot页面消息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `bot_id`(`bot_id`) USING BTREE,
  INDEX `account_id`(`account_id`) USING BTREE,
  INDEX `session_id`(`session_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Bot 消息记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_bot_plugins
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_plugins`;
CREATE TABLE `tb_ai_bot_plugins`  (
  `id` bigint UNSIGNED NOT NULL,
  `bot_id` bigint UNSIGNED NULL DEFAULT NULL,
  `plugin_tool_id` bigint UNSIGNED NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_bot_workflow
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_workflow`;
CREATE TABLE `tb_ai_bot_workflow`  (
  `id` bigint UNSIGNED NOT NULL,
  `bot_id` bigint UNSIGNED NULL DEFAULT NULL,
  `workflow_id` bigint UNSIGNED NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_chat_message`;
CREATE TABLE `tb_ai_chat_message`  (
  `id` bigint UNSIGNED NOT NULL,
  `topic_id` bigint UNSIGNED NULL DEFAULT NULL,
  `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `prompt_tokens` int NULL DEFAULT NULL,
  `completion_tokens` int NULL DEFAULT NULL,
  `total_tokens` int NULL DEFAULT NULL,
  `tools` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created` datetime NULL DEFAULT NULL,
  `modified` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `topic_id`(`topic_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 消息记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_chat_topic
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_chat_topic`;
CREATE TABLE `tb_ai_chat_topic`  (
  `id` bigint UNSIGNED NOT NULL,
  `account_id` bigint UNSIGNED NULL DEFAULT NULL,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created` datetime NULL DEFAULT NULL,
  `modified` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `account_id`(`account_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 话题表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_document
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document`;
CREATE TABLE `tb_ai_document`  (
  `id` bigint UNSIGNED NOT NULL,
  `knowledge_id` bigint UNSIGNED NOT NULL COMMENT '知识库ID',
  `document_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档类型 pdf/word/aieditor 等',
  `document_path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档路径',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容类型',
  `slug` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'URL 别名',
  `order_no` int NULL DEFAULT NULL COMMENT '排序序号',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他配置项',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `modified` datetime NULL DEFAULT NULL COMMENT '最后的修改时间',
  `modified_by` bigint NULL DEFAULT NULL COMMENT '最后的修改人的ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `knowledge_id`(`knowledge_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文档' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_document_chunk
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document_chunk`;
CREATE TABLE `tb_ai_document_chunk`  (
  `id` bigint UNSIGNED NOT NULL,
  `document_id` bigint UNSIGNED NOT NULL COMMENT '文档ID',
  `knowledge_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '知识库ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分块内容',
  `sorting` int UNSIGNED NULL DEFAULT NULL COMMENT '分割顺序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_document_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document_history`;
CREATE TABLE `tb_ai_document_history`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `document_id` bigint NULL DEFAULT NULL COMMENT '修改的文档ID',
  `old_title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '旧标题',
  `new_title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新标题',
  `old_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '旧内容',
  `new_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '新内容',
  `old_document_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '旧的文档类型',
  `new_document_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新的额文档类型',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_knowledge`;
CREATE TABLE `tb_ai_knowledge`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'Id',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '别名',
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
  `id` bigint UNSIGNED NOT NULL COMMENT 'ID',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题或名称',
  `brand` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICON',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
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
  `llm_endpoint` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '大模型请求地址',
  `llm_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '大模型名称',
  `llm_api_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '大模型 API KEY',
  `llm_extra_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '大模型其他属性配置',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他配置内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '大模型管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_plugin
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin`;
CREATE TABLE `tb_ai_plugin`  (
  `id` bigint NOT NULL COMMENT '插件id',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '别名',
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '插件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_plugin_categories
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin_categories`;
CREATE TABLE `tb_ai_plugin_categories`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_plugin_category_relation
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin_category_relation`;
CREATE TABLE `tb_ai_plugin_category_relation`  (
  `category_id` int NOT NULL,
  `plugin_id` bigint NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_plugin_tool
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugin_tool`;
CREATE TABLE `tb_ai_plugin_tool`  (
  `id` bigint NOT NULL COMMENT '插件工具id',
  `plugin_id` bigint NOT NULL COMMENT '插件id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `base_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '基础路径',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `status` int NULL DEFAULT NULL COMMENT '是否启用',
  `input_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '输入参数',
  `output_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '输出参数',
  `request_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方式【Post, Get, Put, Delete】',
  `service_status` int NULL DEFAULT NULL COMMENT '服务状态[0 下线 1 上线]',
  `debug_status` int NULL DEFAULT NULL COMMENT '调试状态【0失败 1成功】',
  `english_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '插件工具表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_resource
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_resource`;
CREATE TABLE `tb_ai_resource`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `resource_type` int NOT NULL COMMENT '素材类型',
  `resource_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '素材名称',
  `suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '后缀',
  `resource_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '素材地址',
  `origin` int NOT NULL DEFAULT 0 COMMENT '素材来源',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展项',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  `file_size` bigint UNSIGNED NULL DEFAULT NULL COMMENT '文件大小',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '素材库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_workflow
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_workflow`;
CREATE TABLE `tb_ai_workflow`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'ID 主键',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '别名',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICON',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '工作流设计的 JSON 内容',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `modified` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  `modified_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '最后修改的人',
  `english_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名称',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '分类ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tb_ai_workflow_alias_uindex`(`alias`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工作流' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_ai_workflow_category
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_workflow_category`;
CREATE TABLE `tb_ai_workflow_category`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort_no` int NULL DEFAULT 0 COMMENT '排序',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `status` int NOT NULL DEFAULT 0 COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工作流分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_datacenter_table
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据中枢表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_datacenter_table_fields
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据中枢字段表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_dynamic_orders_350826308360716288
-- ----------------------------
DROP TABLE IF EXISTS `tb_dynamic_orders_350826308360716288`;
CREATE TABLE `tb_dynamic_orders_350826308360716288`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `order_no` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `good_name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `order_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '订单备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_dynamic_shop_user_306167410386075648
-- ----------------------------
DROP TABLE IF EXISTS `tb_dynamic_shop_user_306167410386075648`;
CREATE TABLE `tb_dynamic_shop_user_306167410386075648`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `age` int NOT NULL COMMENT '年龄',
  `entry_time` datetime NOT NULL COMMENT '入职时间',
  `salary` decimal(20, 6) NOT NULL COMMENT '薪资',
  `is_full_time` int NOT NULL COMMENT '是否全职',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '店员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_blob_triggers`;
CREATE TABLE `tb_qrtz_blob_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `BLOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `SCHED_NAME`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `TB_QRTZ_BLOB_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `tb_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_calendars`;
CREATE TABLE `tb_qrtz_calendars`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_cron_triggers`;
CREATE TABLE `tb_qrtz_cron_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `TB_QRTZ_CRON_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `tb_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_fired_triggers`;
CREATE TABLE `tb_qrtz_fired_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `ENTRY_ID` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `FIRED_TIME` bigint NOT NULL,
  `SCHED_TIME` bigint NOT NULL,
  `PRIORITY` int NOT NULL,
  `STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TRIG_INST_NAME`(`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE,
  INDEX `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY`(`SCHED_NAME`, `INSTANCE_NAME`, `REQUESTS_RECOVERY`) USING BTREE,
  INDEX `IDX_QRTZ_FT_J_G`(`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_JG`(`SCHED_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_T_G`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TG`(`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_job_details`;
CREATE TABLE `tb_qrtz_job_details`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `IS_DURABLE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_J_REQ_RECOVERY`(`SCHED_NAME`, `REQUESTS_RECOVERY`) USING BTREE,
  INDEX `IDX_QRTZ_J_GRP`(`SCHED_NAME`, `JOB_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_locks`;
CREATE TABLE `tb_qrtz_locks`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `LOCK_NAME` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_paused_trigger_grps`;
CREATE TABLE `tb_qrtz_paused_trigger_grps`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_scheduler_state`;
CREATE TABLE `tb_qrtz_scheduler_state`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint NOT NULL,
  `CHECKIN_INTERVAL` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_simple_triggers`;
CREATE TABLE `tb_qrtz_simple_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `REPEAT_COUNT` bigint NOT NULL,
  `REPEAT_INTERVAL` bigint NOT NULL,
  `TIMES_TRIGGERED` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `TB_QRTZ_SIMPLE_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `tb_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_simprop_triggers`;
CREATE TABLE `tb_qrtz_simprop_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `STR_PROP_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `STR_PROP_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `STR_PROP_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `INT_PROP_1` int NULL DEFAULT NULL,
  `INT_PROP_2` int NULL DEFAULT NULL,
  `LONG_PROP_1` bigint NULL DEFAULT NULL,
  `LONG_PROP_2` bigint NULL DEFAULT NULL,
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `TB_QRTZ_SIMPROP_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `tb_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `tb_qrtz_triggers`;
CREATE TABLE `tb_qrtz_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint NULL DEFAULT NULL,
  `PREV_FIRE_TIME` bigint NULL DEFAULT NULL,
  `PRIORITY` int NULL DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `START_TIME` bigint NOT NULL,
  `END_TIME` bigint NULL DEFAULT NULL,
  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `MISFIRE_INSTR` smallint NULL DEFAULT NULL,
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
  CONSTRAINT `TB_QRTZ_TRIGGERS_IBFK_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `tb_qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account`;
CREATE TABLE `tb_sys_account`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `login_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `account_type` tinyint NOT NULL DEFAULT 0 COMMENT '账户类型',
  `nickname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '昵称',
  `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机电话',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '邮件',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '账户头像',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_login_name`(`login_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_account_position
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account_position`;
CREATE TABLE `tb_sys_account_position`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `account_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `position_id` bigint UNSIGNED NOT NULL COMMENT '职位ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-职位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_account_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account_role`;
CREATE TABLE `tb_sys_account_role`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `account_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_api_key
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_api_key`;
CREATE TABLE `tb_sys_api_key`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'id',
  `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'apiKey',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态1启用 2失效',
  `dept_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '部门id',
  `tenant_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '租户id',
  `expired_at` datetime NULL DEFAULT NULL COMMENT '失效时间',
  `created_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'apikey表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_api_key_resource_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_api_key_resource_permission`;
CREATE TABLE `tb_sys_api_key_resource_permission`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'id',
  `request_interface` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求接口',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请求接口表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_api_key_resource_permission_relationship
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_api_key_resource_permission_relationship`;
CREATE TABLE `tb_sys_api_key_resource_permission_relationship`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'id',
  `api_key_id` bigint UNSIGNED NOT NULL COMMENT 'api_key_id',
  `api_key_resource_id` bigint UNSIGNED NOT NULL COMMENT '请求接口资源访问id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'apikey-请求接口表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dept`;
CREATE TABLE `tb_sys_dept`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `parent_id` bigint UNSIGNED NOT NULL COMMENT '父级ID',
  `ancestors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '父级部门ID集合',
  `dept_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `dept_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门编码',
  `sort_no` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dict`;
CREATE TABLE `tb_sys_dict`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据字典名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '字典编码',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '字典描述或备注',
  `dict_type` tinyint NULL DEFAULT NULL COMMENT '字典类型 1 自定义字典、2 数据表字典、 3 枚举类字典、 4 系统字典（自定义 DictLoader）',
  `sort_no` int NULL DEFAULT NULL COMMENT '排序编号',
  `status` tinyint NULL DEFAULT NULL COMMENT '是否启用',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展字典  存放 json',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `key`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dict_item`;
CREATE TABLE `tb_sys_dict_item`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `dict_id` bigint UNSIGNED NOT NULL COMMENT '归属哪个字典',
  `text` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '名称或内容',
  `value` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '值',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `sort_no` int NOT NULL DEFAULT 0 COMMENT '排序',
  `css_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'css样式内容',
  `css_class` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'css样式类名',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态',
  `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据字典内容' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_job
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_job`;
CREATE TABLE `tb_sys_job`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_type` int NOT NULL COMMENT '任务类型',
  `job_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务参数',
  `cron_expression` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'cron表达式',
  `allow_concurrent` int NOT NULL DEFAULT 0 COMMENT '是否并发执行',
  `misfire_policy` int NOT NULL DEFAULT 3 COMMENT '错过策略',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他配置',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_job_log`;
CREATE TABLE `tb_sys_job_log`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `job_id` bigint UNSIGNED NOT NULL COMMENT '任务ID',
  `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务参数',
  `job_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行结果',
  `error_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `status` int NOT NULL COMMENT '执行状态',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `created` datetime NOT NULL COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统任务日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_log`;
CREATE TABLE `tb_sys_log`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'ID',
  `account_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '操作人',
  `action_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作名称',
  `action_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作的类型',
  `action_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作涉及的类',
  `action_method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作涉及的方法',
  `action_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作涉及的 URL 地址',
  `action_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作涉及的用户 IP 地址',
  `action_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作请求参数',
  `action_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作请求body',
  `status` tinyint NULL DEFAULT NULL COMMENT '操作状态 1 成功 9 失败',
  `created` datetime NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_menu`;
CREATE TABLE `tb_sys_menu`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `parent_id` bigint UNSIGNED NOT NULL COMMENT '父菜单id',
  `menu_type` int NOT NULL COMMENT '菜单类型',
  `menu_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单标题',
  `menu_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单url',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '组件路径',
  `menu_icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图标/图片地址',
  `is_show` int NOT NULL DEFAULT 1 COMMENT '是否显示',
  `permission_tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '权限标识',
  `sort_no` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_option
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_option`;
CREATE TABLE `tb_sys_option`  (
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置KEY',
  `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置内容',
  INDEX `uni_key`(`tenant_id`, `key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_position
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_position`;
CREATE TABLE `tb_sys_position`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `position_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `position_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '岗位编码',
  `sort_no` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '职位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role`;
CREATE TABLE `tb_sys_role`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色标识',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
  `data_scope` int NULL DEFAULT 1 COMMENT '数据权限(EnumDataScope)',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_tenant_role`(`tenant_id`, `role_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统角色' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role_dept`;
CREATE TABLE `tb_sys_role_dept`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色-部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role_menu`;
CREATE TABLE `tb_sys_role_menu`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` bigint UNSIGNED NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色-菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_token
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_token`;
CREATE TABLE `tb_sys_token`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '生成的 token 值',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Token 描述（可选）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_token`(`token`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'iframe 嵌入用 Token 表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;

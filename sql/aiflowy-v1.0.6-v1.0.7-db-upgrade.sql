SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------

ALTER TABLE `tb_ai_knowledge` 
ADD COLUMN `rerank_llm_id` bigint(0) NULL DEFAULT NULL COMMENT '重排模型id' AFTER `options`,
ADD COLUMN `search_engines_enable` tinyint(1) NULL COMMENT '是否启用搜索引擎' AFTER `rerank_llm_id`;

-- ----------------------------
SET FOREIGN_KEY_CHECKS = 1;
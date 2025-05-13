ALTER TABLE tb_ai_knowledge
    ADD COLUMN can_update_embedding TINYINT(1) NULL DEFAULT NULL COMMENT '是否能修改向量模型'
    AFTER options;

ALTER TABLE `tb_ai_bot_plugins`
    CHANGE COLUMN   `plugin_id` `plugin_tool_id` bigint(0) UNSIGNED NULL DEFAULT NULL AFTER `bot_id`;

package tech.aiflowy.ai.entity;

import com.agentsflex.core.message.*;
import com.agentsflex.core.model.chat.tool.Tool;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiBotMessageBase;
import tech.aiflowy.common.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Bot 消息记录表 实体类。
 *
 * @author michael
 * @since 2024-11-04
 */

@Table(value = "tb_ai_bot_message", comment = "Bot 消息记录表")
public class AiBotMessage extends AiBotMessageBase {

    @Column(ignore = true)
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

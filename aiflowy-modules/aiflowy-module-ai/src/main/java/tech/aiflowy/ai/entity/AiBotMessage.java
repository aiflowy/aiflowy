package tech.aiflowy.ai.entity;

import com.agentsflex.core.message.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiBotMessageBase;


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

    public void setContentAndRole(Message msg) {
        String jsonMessage = JSON.toJSONString(msg, SerializerFeature.WriteClassName);
        super.setContent(jsonMessage);

        if (msg instanceof AiMessage) {
            super.setRole("assistant");
        } else if (msg instanceof UserMessage) {
            super.setRole("user");
        } else if (msg instanceof SystemMessage) {
            super.setRole("system");
        } else if (msg instanceof ToolMessage) {
            super.setRole("function");
        }
    }

    public Message getContentAsMessage() {
        String role = getRole();
        if ("assistant".equals(role)) {
            return parseMessage(AiMessage.class);
        } else if ("user".equals(role)) {
            return parseMessage(UserMessage.class);
        } else if ("system".equals(role)) {
            return parseMessage(SystemMessage.class);
        } else if ("function".equals(role)) {
            return parseMessage(ToolMessage.class);
        }
        return null;
    }

    private <T extends Message> T parseMessage(Class<T> clazz) {
        return JSON.parseObject(
                getContent(),
                clazz,
                Feature.SupportClassForName,
                Feature.SupportAutoType);
    }


}

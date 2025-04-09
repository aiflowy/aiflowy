package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiChatMessageBase;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.Message;
import com.agentsflex.core.message.SystemMessage;
import com.mybatisflex.annotation.Table;

/**
 * AI 消息记录表 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table(value = "tb_ai_chat_message", comment = "AI 消息记录表")
public class AiChatMessage extends AiChatMessageBase {
    public Message toMessage() {
        String role = getRole();
        if ("user".equalsIgnoreCase(role)) {
            HumanMessage humanMessage = new HumanMessage();
            humanMessage.setContent(getContent());
            return humanMessage;
        } else if ("assistant".equalsIgnoreCase(role)) {
            AiMessage aiMessage = new AiMessage();
//            aiMessage.setContent(getContent());
            aiMessage.setFullContent(getContent());
            aiMessage.setTotalTokens(getTotalTokens());
            return aiMessage;
        } else if ("system".equalsIgnoreCase(role)) {
            SystemMessage systemMessage = new SystemMessage();
            systemMessage.setContent(getContent());
            return systemMessage;
        }
        return null;

    }
}

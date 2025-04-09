package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiChatTopicBase;
import com.mybatisflex.annotation.Table;

/**
 * AI 话题表 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table(value = "tb_ai_chat_topic", comment = "AI 话题表")
public class AiChatTopic extends AiChatTopicBase {
}

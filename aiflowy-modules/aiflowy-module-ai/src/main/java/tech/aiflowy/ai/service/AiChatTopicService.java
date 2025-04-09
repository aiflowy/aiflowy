package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.AiChatTopic;
import com.mybatisflex.core.service.IService;

import java.math.BigInteger;

/**
 * AI 话题表 服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiChatTopicService extends IService<AiChatTopic> {

    AiChatTopic getOrCreateDefaultTopic(BigInteger topicId, BigInteger loginAccountId);
}

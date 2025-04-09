package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.AiChatTopic;
import tech.aiflowy.ai.mapper.AiChatTopicMapper;
import tech.aiflowy.ai.service.AiChatTopicService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;

/**
 * AI 话题表 服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiChatTopicServiceImpl extends ServiceImpl<AiChatTopicMapper, AiChatTopic> implements AiChatTopicService {

    @Override
    public AiChatTopic getOrCreateDefaultTopic(BigInteger topicId, BigInteger loginAccountId) {
        if (loginAccountId == null) {
            return null;
        }
        AiChatTopic topic = null;
        if (topicId != null) {
            topic = queryChain()
                    .eq(AiChatTopic::getId, topicId)
                    .eq(AiChatTopic::getAccountId, loginAccountId)
                    .one();
        }

        if (topic == null) {
            topic = queryChain().where(AiChatTopic::getAccountId).eq(loginAccountId)
                    .orderBy(AiChatTopic::getCreated, false)
                    .one();
        }
        if (topic != null) {
            return topic;
        }

        topic = new AiChatTopic();
        topic.setTitle("未命名");
        topic.setCreated(new Date());
        topic.setAccountId(loginAccountId);

        save(topic);

        return topic;
    }
}

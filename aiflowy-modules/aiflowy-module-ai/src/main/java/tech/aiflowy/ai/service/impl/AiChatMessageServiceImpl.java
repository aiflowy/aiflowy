package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.AiChatMessage;
import tech.aiflowy.ai.mapper.AiChatMessageMapper;
import tech.aiflowy.ai.service.AiChatMessageService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * AI 消息记录表 服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessage> implements AiChatMessageService {

}

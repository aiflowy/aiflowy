package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.AiBotMessage;
import tech.aiflowy.ai.mapper.AiBotMessageMapper;
import tech.aiflowy.ai.service.AiBotMessageService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Bot 消息记录表 服务层实现。
 *
 * @author michael
 * @since 2024-11-04
 */
@Service
public class AiBotMessageServiceImpl extends ServiceImpl<AiBotMessageMapper, AiBotMessage> implements AiBotMessageService {

}

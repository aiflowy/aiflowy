package tech.aiflowy.ai.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.mapper.AiBotConversationMessageMapper;
import tech.aiflowy.ai.service.AiBotConversationMessageService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-04-15
 */
@Service
public class AiBotConversationMessageServiceImpl extends ServiceImpl<AiBotConversationMessageMapper, AiBotConversationMessage>  implements AiBotConversationMessageService{

}

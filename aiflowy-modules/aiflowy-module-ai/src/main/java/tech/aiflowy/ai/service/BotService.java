package tech.aiflowy.ai.service;

import com.agentsflex.core.message.Message;
import com.agentsflex.core.message.UserMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.prompt.MemoryPrompt;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.Bot;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.service.impl.BotServiceImpl;
import tech.aiflowy.core.chat.protocol.sse.ChatSseEmitter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface BotService extends IService<Bot> {

    Bot getDetail(String id);

    void updateBotLlmId(Bot aiBot);

    Bot getByAlias(String alias);

    SseEmitter checkChatBeforeStart(BigInteger botId, String prompt, String conversationId, BotServiceImpl.ChatCheckResult chatCheckResult);

    SseEmitter startChat(BigInteger botId, String prompt,  BigInteger conversationId, List<Map<String, String>> messages, BotServiceImpl.ChatCheckResult chatCheckResult);

    SseEmitter startPublicChat(BigInteger botId, String prompt,  List<Message> messages, BotServiceImpl.ChatCheckResult chatCheckResult);

}

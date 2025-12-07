package tech.aiflowy.ai.service;

import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.prompt.MemoryPrompt;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.AiBot;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.common.domain.Result;

import java.math.BigInteger;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiBotService extends IService<AiBot> {

    AiBot getDetail(String id);

    void updateBotLlmId(AiBot aiBot);

    AiBot getByAlias(String alias);

    SseEmitter startChat(BigInteger botId, ChatModel chatModel, String prompt, MemoryPrompt memoryPrompt, ChatOptions chatOptions, String sessionId);
}

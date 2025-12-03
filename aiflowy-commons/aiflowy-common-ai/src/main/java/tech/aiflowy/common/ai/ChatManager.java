package tech.aiflowy.common.ai;

import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.UserMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.model.chat.response.AiMessageResponse;
import com.agentsflex.core.model.client.StreamContext;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.agentsflex.core.prompt.SimplePrompt;
import tech.aiflowy.common.ai.util.LLMUtil;
import tech.aiflowy.common.options.SysOptions;
import tech.aiflowy.common.util.StringUtil;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.prompt.Prompt;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatManager {

    private static final Logger logger = LoggerFactory.getLogger(ChatManager.class);
    private static final ChatManager manager = new ChatManager();

    public static ChatManager getInstance() {
        return manager;
    }

    private ExecutorService sseExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public ChatModel getChatLlm() {
        String modelOfChat = SysOptions.get("model_of_chat");
        return LLMUtil.getLlmByType(modelOfChat);
    }

    public String chat(String prompt) {
        return chat(new SimplePrompt(prompt));
    }

    public String chat(SimplePrompt prompt) {
        ChatModel llm = getChatLlm();
        if (llm == null) {
            return null;
        }
        AiMessageResponse messageResponse = llm.chat(prompt);
        return messageResponse != null && messageResponse.getMessage() != null ?
                messageResponse.getMessage().getContent() : null;

    }


    public SseEmitter sseEmitter(String prompt) {
        return sseEmitter(new SimplePrompt(prompt));
    }

    public SseEmitter sseEmitter(String prompt, ChatMemory memory) {
        MemoryPrompt memoryPrompt = new MemoryPrompt(memory);
        memoryPrompt.addMessage(new UserMessage(prompt));
        return sseEmitter(memoryPrompt);
    }

    public SseEmitter sseEmitter(Prompt prompt) {
        MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        sseExecutor.execute(() -> {
            RequestContextHolder.setRequestAttributes(sra, true);
            try {
                ChatModel llm = getChatLlm();
                if (llm == null) {
                    logger.error("sseEmitter error:llm is null");
                    AiMessage aiMessage = buildErrorMessage("AI 大模型未配置正确：大模型为 null");
                    String message = JSON.toJSONString(aiMessage);
                    emitter.sendAndComplete(message);
                    return;
                }
                llm.chatStream(prompt, new StreamResponseListener() {
                    @Override
                    public void onMessage(StreamContext streamContext, AiMessageResponse aiMessageResponse) {
                        String content = aiMessageResponse.getMessage().getContent();
                        Object messageContent = aiMessageResponse.getMessage();
                        if (StringUtil.hasText(content)) {
                            String jsonResult = JSON.toJSONString(messageContent);
                            emitter.send(jsonResult);
                        }
                    }

                    @Override
                    public void onStop(StreamContext context) {
                        emitter.complete();
                    }
                });
            } catch (Exception e) {
                logger.error("sseEmitter error", e);
                AiMessage aiMessage = buildErrorMessage("服务器内部错误，请查看日志，或检查大模型配置！");
                String message = JSON.toJSONString(aiMessage);
                emitter.sendAndComplete(message);
            }


        });
        return emitter;
    }

    private AiMessage buildErrorMessage(String content) {
        AiMessage aiMessage = new AiMessage();
        aiMessage.setToolCalls(new ArrayList<>());
        aiMessage.setContent(content);
        aiMessage.setFullContent(content);
        return aiMessage;
    }

    public SseEmitter sseEmitterForContent(String content) {
        MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));
        emitter.sendAndComplete(content);
        return emitter;
    }

    public ExecutorService getSseExecutor() {
        return sseExecutor;
    }

    public void setSseExecutor(ExecutorService sseExecutor) {
        this.sseExecutor = sseExecutor;
    }
}

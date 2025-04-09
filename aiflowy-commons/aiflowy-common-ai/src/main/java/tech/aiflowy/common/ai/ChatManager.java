package tech.aiflowy.common.ai;

import tech.aiflowy.common.ai.util.LLMUtil;
import tech.aiflowy.common.options.SysOptions;
import tech.aiflowy.common.util.StringUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.agentsflex.core.prompt.Prompt;
import com.agentsflex.core.prompt.TextPrompt;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatManager {

    private static final Logger logger = LoggerFactory.getLogger(ChatManager.class);
    private static final ChatManager manager = new ChatManager();

    public static ChatManager getInstance() {
        return manager;
    }

    private ExecutorService sseExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Llm getChatLlm() {
        String modelOfChat = SysOptions.get("model_of_chat");
        return LLMUtil.getLlmByType(modelOfChat);
    }

    public String chat(String prompt) {
        return chat(new TextPrompt(prompt));
    }

    public String chat(TextPrompt prompt) {
        Llm llm = getChatLlm();
        if (llm == null) {
            return null;
        }
        AiMessageResponse messageResponse = llm.chat(prompt);
        return messageResponse != null && messageResponse.getMessage() != null ?
                messageResponse.getMessage().getContent() : null;
    }


    public SseEmitter sseEmitter(String prompt) {
        return sseEmitter(new TextPrompt(prompt));
    }

    public SseEmitter sseEmitter(String prompt, ChatMemory memory) {
        HistoriesPrompt historiesPrompt = new HistoriesPrompt(memory);
        historiesPrompt.addMessage(new HumanMessage(prompt));
        return sseEmitter(historiesPrompt);
    }

    public SseEmitter sseEmitter(Prompt prompt) {
        MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        sseExecutor.execute(() -> {
            RequestContextHolder.setRequestAttributes(sra, true);
            Llm llm = getChatLlm();
            if (llm == null) {
                emitter.sendAndComplete("AI 大模型未配置正确");
                return;
            }
            llm.chatStream(prompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext chatContext, AiMessageResponse aiMessageResponse) {
                    String content = aiMessageResponse.getMessage().getContent();
                    Object messageContent = aiMessageResponse.getMessage();
                    if (StringUtil.hasText(content)) {
                        String jsonResult =  JSON.toJSONString(messageContent);
                        emitter.send(jsonResult);
                    }
//                    String content = aiMessageResponse.getMessage().getContent();
                    System.out.println(">>>>response: " + content);
                }
                @Override
                public void onStop(ChatContext context) {
                    emitter.complete();
                }
            });

        });
        return emitter;
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

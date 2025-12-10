package tech.aiflowy.ai.service;

import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.ToolMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.model.chat.response.AiMessageResponse;
import com.agentsflex.core.model.client.StreamContext;
import com.agentsflex.core.prompt.MemoryPrompt;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.common.util.StringUtil;

import java.util.List;

public class ChatStreamListener implements StreamResponseListener {

    private ChatModel chatModel;
    private MemoryPrompt memoryPrompt;
    private SseEmitter sseEmitter;


    public ChatStreamListener(ChatModel chatModel, MemoryPrompt memoryPrompt, SseEmitter sseEmitter) {
        this.chatModel = chatModel;
        this.memoryPrompt = memoryPrompt;
        this.sseEmitter = sseEmitter;
    }

    @Override
    public void onStart(StreamContext context) {
        StreamResponseListener.super.onStart(context);
    }

    @Override
    public void onMessage(StreamContext context, AiMessageResponse aiMessageResponse) {
        try {
            AiMessage aiMessage = aiMessageResponse.getMessage();
            if (aiMessage == null) {
                return;
            }
            if ((aiMessage.getFinished() != null) && StringUtil.hasText(aiMessage.getFullReasoningContent()) && aiMessage.isFinalDelta()){
                System.out.println("最终结果触发");
                aiMessage.setContent(aiMessage.getFullReasoningContent());
                memoryPrompt.addMessage(aiMessage);
            }
            if (aiMessage.isFinalDelta() && aiMessageResponse.hasToolCalls()) {
                System.out.println("有工具调用");
                List<ToolMessage> toolMessages = aiMessageResponse.executeToolCallsAndGetToolMessages();
                for (ToolMessage toolMessage : toolMessages) {
                    memoryPrompt.addMessage(toolMessage);
                }

                chatModel.chatStream(memoryPrompt,this);


            } else {

                    String delta = aiMessageResponse.getMessage().getContent();
                    if (StringUtil.hasText(delta)) {
                        sseEmitter.send(delta);
                    }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop(StreamContext context) {
        System.out.println("onStop");
        StreamResponseListener.super.onStop(context);
    }

    @Override
    public void onFailure(StreamContext context, Throwable throwable) {
        StreamResponseListener.super.onFailure(context, throwable);
    }
}

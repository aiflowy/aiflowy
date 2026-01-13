package tech.aiflowy.ai.agentsflex.listener;

import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.ToolMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.model.chat.response.AiMessageResponse;
import com.agentsflex.core.model.client.StreamContext;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.alibaba.fastjson.JSON;
import org.apache.catalina.connector.ClientAbortException;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.core.chat.protocol.ChatDomain;
import tech.aiflowy.core.chat.protocol.ChatEnvelope;
import tech.aiflowy.core.chat.protocol.ChatType;
import tech.aiflowy.core.chat.protocol.MessageRole;
import tech.aiflowy.core.chat.protocol.sse.ChatSseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatStreamListener implements StreamResponseListener {

    private final ChatModel chatModel;
    private final MemoryPrompt memoryPrompt;
    private final ChatSseEmitter sseEmitter;
    private final ChatOptions chatOptions;
    // 核心标记：是否允许执行onStop业务逻辑（仅最后一次无后续工具调用时为true）
    private boolean canStop = true;
    // 辅助标记：是否进入过工具调用（避免重复递归判断）
    private boolean hasToolCall = false;

    public ChatStreamListener(ChatModel chatModel, MemoryPrompt memoryPrompt, ChatSseEmitter sseEmitter, ChatOptions chatOptions) {
        this.chatModel = chatModel;
        this.memoryPrompt = memoryPrompt;
        this.sseEmitter = sseEmitter;
        this.chatOptions = chatOptions;
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
            if (aiMessage.isFinalDelta() && aiMessageResponse.hasToolCalls()) {
                this.canStop = false; // 工具调用期间，禁止执行onStop
                this.hasToolCall = true; // 标记已进入过工具调用
                aiMessage.setContent(null);
                memoryPrompt.addMessage(aiMessage);
                List<ToolMessage> toolMessages = aiMessageResponse.executeToolCallsAndGetToolMessages();
                for (ToolMessage toolMessage : toolMessages) {
                    memoryPrompt.addMessage(toolMessage);
                }
                chatModel.chatStream(memoryPrompt, this, chatOptions);
            } else {
                if (this.hasToolCall) {
                    this.canStop = true;
                }
                String delta = aiMessageResponse.getMessage().getContent();
                if (delta != null) {
                    ChatEnvelope<Map<String, String>> chatEnvelope = new ChatEnvelope<>();
                    chatEnvelope.setDomain(ChatDomain.LLM);
                    chatEnvelope.setType(ChatType.MESSAGE);
                    Map<String, String> deletaMap = new HashMap<>();
                    deletaMap.put("delta", delta);
                    deletaMap.put("role", MessageRole.ASSISTANT.getValue());
                    chatEnvelope.setPayload(deletaMap);
                    sseEmitter.send(chatEnvelope);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop(StreamContext context) {
        // 仅当canStop为true（最后一次无后续工具调用的响应）时，执行业务逻辑
        if (this.canStop) {
            System.out.println("onStop");
            memoryPrompt.addMessage(context.getAiMessage());
            ChatEnvelope<Map<String, String>> chatEnvelope = new ChatEnvelope<>();
            chatEnvelope.setDomain(ChatDomain.SYSTEM);
            sseEmitter.sendDone(chatEnvelope);
            StreamResponseListener.super.onStop(context);
        }

    }

    @Override
    public void onFailure(StreamContext context, Throwable throwable) {
        if (throwable != null && throwable.getCause() instanceof ClientAbortException) {
            sseEmitter.complete();
        }
    }
}

package tech.aiflowy.ai.agentsflex.memory;

import com.agentsflex.core.memory.DefaultChatMemory;
import com.agentsflex.core.message.Message;
import tech.aiflowy.core.chat.protocol.sse.ChatSseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PublicBotMessageMemory extends DefaultChatMemory {
    private final ChatSseEmitter sseEmitter;
    private List<Message> messages = new ArrayList<>();

    public PublicBotMessageMemory(ChatSseEmitter sseEmitter, List<Message> messages ) {
        this.messages = messages;
        this.sseEmitter = sseEmitter;
    }

    @Override
    public List<Message> getMessages(int count) {
        return messages.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addMessage(Message message) {
        this.messages.add(message);
    }
}

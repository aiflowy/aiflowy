package tech.aiflowy.common.controller;

import tech.aiflowy.ai.entity.AiChatMessage;
import tech.aiflowy.ai.service.AiChatMessageService;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.Message;
import com.agentsflex.core.message.SystemMessage;
import com.mybatisflex.core.query.QueryWrapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AiTopicMemory implements ChatMemory {
    private final BigInteger topicId;
    private final AiChatMessageService messageService;

    public AiTopicMemory(BigInteger topicId, AiChatMessageService messageService) {
        this.topicId = topicId;
        this.messageService = messageService;
    }

    @Override
    public List<Message> getMessages() {
        List<AiChatMessage> sysAiMessages = messageService.list(QueryWrapper.create()
                .eq(AiChatMessage::getTopicId, topicId)
                .orderBy(AiChatMessage::getCreated, true)
        );

        if (sysAiMessages == null || sysAiMessages.isEmpty()) {
            return null;
        }

        List<Message> messages = new ArrayList<>(sysAiMessages.size());
        for (AiChatMessage sysAiMessage : sysAiMessages) {
            Message message = sysAiMessage.toMessage();
            if (message != null) messages.add(message);
        }
        return messages;
    }

    @Override
    public void addMessage(Message message) {
        AiChatMessage aiMessage = new AiChatMessage();
        aiMessage.setCreated(new Date());
        aiMessage.setTopicId(topicId);
        if (message instanceof AiMessage) {
            aiMessage.setContent(((AiMessage) message).getFullContent());
            aiMessage.setRole("assistant");
            aiMessage.setTotalTokens(((AiMessage) message).getTotalTokens());
            aiMessage.setPromptTokens(((AiMessage) message).getPromptTokens());
            aiMessage.setCompletionTokens(((AiMessage) message).getCompletionTokens());
        } else if (message instanceof HumanMessage) {
            aiMessage.setContent(((HumanMessage) message).getContent());
            aiMessage.setRole("user");
        } else if (message instanceof SystemMessage) {
            aiMessage.setRole("system");
            aiMessage.setContent(((SystemMessage) message).getContent());
        }
        messageService.save(aiMessage);
    }

    @Override
    public Object id() {
        return topicId;
    }
}

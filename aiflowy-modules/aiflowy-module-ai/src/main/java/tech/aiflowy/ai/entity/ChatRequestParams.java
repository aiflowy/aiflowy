package tech.aiflowy.ai.entity;

import com.agentsflex.core.message.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ChatRequestParams implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigInteger botId;
    private String conversationId;
    private List<Message> messages;

    public ChatRequestParams() {
    }

    @JsonProperty("messages")
    public void setMessagesFromJson(List<Object> rawMessages) {
        if (rawMessages == null) {
            this.messages = null;
            return;
        }

        this.messages = new ArrayList<>();
        for (Object raw : rawMessages) {
            Message message = convertToMessage(raw);
            if (message != null) {
                this.messages.add(message);
            }
        }
    }

    private Message convertToMessage(Object raw) {
        JSONObject jsonObj = JSONObject.from(raw);
        if (jsonObj == null) {
            return null;
        }

        String role = jsonObj.getString("role");
        String content = jsonObj.getString("content");
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        if ("user".equals(role)) {
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("User message cannot be empty");
            }
        }

        return switch (role) {
            case "user" -> jsonObj.toJavaObject(UserMessage.class);
            case "system" -> jsonObj.toJavaObject(SystemMessage.class);
            case "assistant" -> jsonObj.toJavaObject(AiMessage.class);
            case "tool" -> jsonObj.toJavaObject(ToolMessage.class);
            default -> {
                UserMessage defaultMsg = new UserMessage();
                defaultMsg.setContent(content);
                yield defaultMsg;
            }
        };
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public BigInteger getBotId() {
        return botId;
    }

    public void setBotId(BigInteger botId) {
        this.botId = botId;
    }

    public String getConversationId() {return conversationId;}

    public void setConversationId(String conversationId) {this.conversationId = conversationId;}
}
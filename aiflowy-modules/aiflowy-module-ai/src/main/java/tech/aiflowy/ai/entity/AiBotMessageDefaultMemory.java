package tech.aiflowy.ai.entity;

import com.agentsflex.core.memory.DefaultChatMemory;
import com.agentsflex.core.message.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AiBotMessageDefaultMemory extends DefaultChatMemory {

    private final SseEmitter sseEmitter;

    private final List<Map<String, String>> messages;

    public AiBotMessageDefaultMemory(String sessionId, SseEmitter sseEmitter, List<Map<String, String>> messages) {
        super(sessionId);
        this.sseEmitter = sseEmitter;
        this.messages = messages;
    }

    @Override
    public List<Message> getMessages(int count) {
        List<Message> list = new ArrayList<>(messages.size());
        for (Map<String, String> msg : messages) {
            AiBotMessage aiBotMessage = new AiBotMessage();
            aiBotMessage.setRole(msg.get("role"));
            aiBotMessage.setContent(msg.get("content"));
            Message message = aiBotMessage.getContentAsMessage();
            list.add(message);
        }
        return list;
    }

    @Override
    public void addMessage(Message message) {
        try {
            AiBotMessage dbMessage = new AiBotMessage();
            String jsonMessage = JSON.toJSONString(message, SerializerFeature.WriteClassName);
            if (message instanceof AiMessage) {
                dbMessage.setRole("assistant");
            } else if (message instanceof UserMessage) {
                dbMessage.setRole("user");
            }else if (message instanceof SystemMessage) {
                dbMessage.setRole("system");
            } else if (message instanceof ToolMessage) {
                dbMessage.setRole("function");
            }
            Map<String, String> res = new HashMap<>();
            res.put("role", dbMessage.getRole());
            res.put("content", jsonMessage);
            sseEmitter.send(SseEmitter.event().name("needSaveMessage").data(JSON.toJSONString(res)
            ));
            if (messages.size() == 1 ) {
                messages.clear();
                messages.add(res);
            }
            if ("user".equals(dbMessage.getRole())){
                messages.remove(messages.size()-1);
                messages.add(res);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

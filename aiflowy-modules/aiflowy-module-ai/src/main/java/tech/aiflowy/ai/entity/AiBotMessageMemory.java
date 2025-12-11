package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.tool.Tool;
import com.alibaba.fastjson.parser.Feature;
import tech.aiflowy.ai.enums.BotMessageTypeEnum;
import tech.aiflowy.ai.mapper.AiBotConversationMessageMapper;
import tech.aiflowy.ai.service.AiBotConversationMessageService;
import tech.aiflowy.ai.service.AiBotMessageService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mybatisflex.core.query.QueryWrapper;

import java.math.BigInteger;
import java.util.*;

public class AiBotMessageMemory implements ChatMemory {
    private final BigInteger botId;
    private final BigInteger accountId;
    private final String sessionId;
    private final AiBotMessageService messageService;
    public AiBotMessageMemory(BigInteger botId, BigInteger accountId, String sessionId,
                              AiBotMessageService messageService ) {
        this.botId = botId;
        this.accountId = accountId;
        this.sessionId = sessionId;
        this.messageService = messageService;
    }

    @Override
    public List<Message> getMessages(int i) {
        List<AiBotMessage> sysAiMessages = messageService.list(QueryWrapper.create()
                .eq(AiBotMessage::getBotId, botId, true)
                .eq(AiBotMessage::getAccountId, accountId, true)
                .eq(AiBotMessage::getSessionId, sessionId, true)
                .orderBy(AiBotMessage::getCreated, true)
        );

        if (sysAiMessages == null || sysAiMessages.isEmpty()) {
            return null;
        }

        List<Message> messages = new ArrayList<>(sysAiMessages.size());
        for (AiBotMessage aiBotMessage : sysAiMessages) {
            Message message = parseByRole(aiBotMessage);
            if (message != null) messages.add(message);
        }
        return messages;
    }


    @Override
    public void addMessage(Message message) {

        AiBotMessage dbMessage = new AiBotMessage();
        dbMessage.setCreated(new Date());
        dbMessage.setBotId(botId);
        dbMessage.setAccountId(accountId);
        dbMessage.setSessionId(sessionId);
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
        dbMessage.setContent(jsonMessage);
        messageService.save(dbMessage);

    }

    @Override
    public void clear() {

    }

    @Override
    public Object id() {
        return botId;
    }

    public static Message parseByRole(AiBotMessage aiBotMessage) {
        try {
            String role = aiBotMessage.getRole();
            if ("assistant".equals(role)) {
                return JSON.parseObject(
                        aiBotMessage.getContent(),
                        AiMessage.class,
                        Feature.SupportClassForName,
                        Feature.SupportAutoType
                );
            } else if ("user".equals(role)) {
                return JSON.parseObject(
                        aiBotMessage.getContent(),
                        UserMessage.class,
                        Feature.SupportClassForName,
                        Feature.SupportAutoType
                );
            } else if ("system".equals(role)) {
                return JSON.parseObject(
                        aiBotMessage.getContent(),
                        SystemMessage.class,
                        Feature.SupportClassForName,
                        Feature.SupportAutoType
                );
            } else if ("function".equals(role)) {
                return JSON.parseObject(
                        aiBotMessage.getContent(),
                        ToolMessage.class,
                        Feature.SupportClassForName,
                        Feature.SupportAutoType
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

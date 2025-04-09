package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.service.AiBotMessageService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mybatisflex.core.query.QueryWrapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AiBotMessageMemory implements ChatMemory {
    private final BigInteger botId;
    private final BigInteger accountId;
    private final String sessionId;
    private final AiBotMessageService messageService;

    public AiBotMessageMemory(BigInteger botId, BigInteger accountId, String sessionId, AiBotMessageService messageService) {
        this.botId = botId;
        this.accountId = accountId;
        this.sessionId = sessionId;
        this.messageService = messageService;
    }

    @Override
    public List<Message> getMessages() {
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
            Message message = aiBotMessage.toMessage();
            if (message != null) messages.add(message);
        }
        return messages;
    }

    @Override
    public void addMessage(Message message) {
        AiBotMessage aiMessage = new AiBotMessage();
        aiMessage.setCreated(new Date());
        aiMessage.setBotId(botId);
        aiMessage.setAccountId(accountId);
        aiMessage.setSessionId(sessionId);

        if (message instanceof AiMessage) {
            AiMessage m = (AiMessage) message;
            aiMessage.setContent(m.getFullContent());
            aiMessage.setRole("assistant");
            aiMessage.setTotalTokens(m.getTotalTokens());
            aiMessage.setPromptTokens(m.getPromptTokens());
            aiMessage.setCompletionTokens(m.getCompletionTokens());
            List<FunctionCall> calls = m.getCalls();
            if (CollectionUtil.isNotEmpty(calls)) {
                return;
            }
        } else if (message instanceof HumanMessage) {
            HumanMessage hm = (HumanMessage) message;
            aiMessage.setContent(hm.getContent());
            List<Function> functions = hm.getFunctions();
            aiMessage.setFunctions(JSON.toJSONString(functions, SerializerFeature.WriteClassName));
            aiMessage.setRole("user");
        } else if (message instanceof SystemMessage) {
            aiMessage.setRole("system");
            aiMessage.setContent(((SystemMessage) message).getContent());
        }
        if (StrUtil.isNotEmpty(aiMessage.getContent())) {
            messageService.save(aiMessage);
        }
    }

    @Override
    public Object id() {
        return botId;
    }
}

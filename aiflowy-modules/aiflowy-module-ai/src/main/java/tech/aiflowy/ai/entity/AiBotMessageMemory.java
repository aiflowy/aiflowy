package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.tool.Tool;
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
    private final int isExternalMsg;
    private final AiBotMessageService messageService;
    private final AiBotConversationMessageMapper aiBotConversationMessageMapper;
    private final AiBotConversationMessageService aiBotConversationService;
    public AiBotMessageMemory(BigInteger botId, BigInteger accountId, String sessionId, int isExternalMsg,
                              AiBotMessageService messageService,
                              AiBotConversationMessageMapper aiBotConversationMessageMapper,
                              AiBotConversationMessageService aiBotConversationService ) {
        this.botId = botId;
        this.accountId = accountId;
        this.sessionId = sessionId;
        this.isExternalMsg = isExternalMsg;
        this.messageService = messageService;
        this.aiBotConversationMessageMapper = aiBotConversationMessageMapper;
        this.aiBotConversationService = aiBotConversationService;
    }

    @Override
    public List<Message> getMessages(int i) {
        List<AiBotMessage> sysAiMessages = messageService.list(QueryWrapper.create()
                .eq(AiBotMessage::getBotId, botId, true)
                .eq(AiBotMessage::getAccountId, accountId, true)
                .eq(AiBotMessage::getSessionId, sessionId, true)
                .eq(AiBotMessage::getIsExternalMsg, isExternalMsg, true)
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
        aiMessage.setIsExternalMsg(isExternalMsg);
        if (message instanceof AiMessage) {
            AiMessage m = (AiMessage) message;
            aiMessage.setContent(m.getFullContent());
            aiMessage.setRole("assistant");
            aiMessage.setTotalTokens(m.getTotalTokens());
            aiMessage.setPromptTokens(m.getPromptTokens());
            aiMessage.setCompletionTokens(m.getCompletionTokens());
            Map<String, Object> metadataMap = m.getMetadataMap();
            aiMessage.setOptions(metadataMap);
            List<ToolCall> toolCalls = m.getToolCalls();
            if (CollectionUtil.isNotEmpty(toolCalls)) {
                return;
            }

        } else if (message instanceof UserMessage) {

            UserMessage hm = (UserMessage) message;
            aiMessage.setContent(hm.getContent());
            List<Tool> functions = hm.getTools();
            aiMessage.setFunctions(JSON.toJSONString(functions, SerializerFeature.WriteClassName));
            aiMessage.setRole("user");
            Map<String, Object> metadataMap = hm.getMetadataMap();
            if (metadataMap == null){
                metadataMap = new HashMap<>();
            }

            Object type = metadataMap.get("type");
            if (type != null) {
                String t = type.toString();
                if (!t.equals(String.valueOf(BotMessageTypeEnum.REACT_THINKING.getValue()))){
                    metadataMap.put("type", BotMessageTypeEnum.TOOL_RESULT.getValue());
                }
            } else {
                metadataMap.put("type", BotMessageTypeEnum.NORMAL.getValue());
            }

            aiMessage.setOptions(metadataMap);

        }else if (message instanceof SystemMessage) {

            aiMessage.setRole("system");
            aiMessage.setContent(((SystemMessage) message).getContent());

        }
        if (StrUtil.isNotEmpty(aiMessage.getContent())) {
            messageService.save(aiMessage);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public Object id() {
        return botId;
    }
}

package tech.aiflowy.ai.entity;

import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.Message;
import com.mybatisflex.core.query.QueryWrapper;
import tech.aiflowy.ai.service.AiBotMessageService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AiBotMessageMemory implements ChatMemory {
    private final BigInteger botId;
    private final BigInteger accountId;
    private final String sessionId;
    private final AiBotMessageService messageService;
    
    public AiBotMessageMemory(BigInteger botId, BigInteger accountId, String sessionId,
                              AiBotMessageService messageService) {
        this.botId = botId;
        this.accountId = accountId;
        this.sessionId = sessionId;
        this.messageService = messageService;
    }

    @Override
    public List<Message> getMessages(int count) {
        List<AiBotMessage> sysAiMessages = messageService.list(QueryWrapper.create()
                .eq(AiBotMessage::getBotId, botId, true)
                .eq(AiBotMessage::getAccountId, accountId, true)
                .eq(AiBotMessage::getSessionId, sessionId, true)
                .orderBy(AiBotMessage::getCreated, true)
                .limit(count)
        );

        if (sysAiMessages == null || sysAiMessages.isEmpty()) {
            return null;
        }

        List<Message> messages = new ArrayList<>(sysAiMessages.size());
        for (AiBotMessage aiBotMessage : sysAiMessages) {
            Message message = aiBotMessage.getContentAsMessage();
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
        dbMessage.setContentAndRole(message);
        messageService.save(dbMessage);
    }

    @Override
    public void clear() {

    }

    @Override
    public Object id() {
        return botId;
    }

}

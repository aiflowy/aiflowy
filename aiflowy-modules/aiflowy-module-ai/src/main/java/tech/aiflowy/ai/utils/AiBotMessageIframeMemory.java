//package tech.aiflowy.ai.utils;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.StrUtil;
//import com.agentsflex.core.llm.functions.Function;
//import com.agentsflex.core.memory.ChatMemory;
//import com.agentsflex.core.message.*;
//import com.agentsflex.core.prompt.ToolPrompt;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.alicp.jetcache.Cache;
//import tech.aiflowy.ai.entity.AiBotConversationMessage;
//import tech.aiflowy.ai.entity.AiBotMessage;
//import tech.aiflowy.ai.service.AiBotConversationMessageService;
//import tech.aiflowy.common.satoken.util.SaTokenUtil;
//
//import java.math.BigInteger;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class AiBotMessageIframeMemory implements ChatMemory {
//    private final BigInteger botId;
//    private final String tempUserId;
//    private final String sessionId;
//    private final String sessionTitle;
//
//    private final AiBotConversationMessageService aiBotConversationMessageService;
//
//    private Cache<String, Object> cache;
//
//    public AiBotMessageIframeMemory(
//            BigInteger botId,
//            String tempUserId,
//            String sessionId,
//            Cache<String, Object> cache,
//            AiBotConversationMessageService aiBotConversationMessageService,
//            String sessionTitle
//    ) {
//        this.botId = botId;
//        this.tempUserId = tempUserId;
//        this.sessionId = sessionId;
//        this.cache = cache;
//        this.aiBotConversationMessageService = aiBotConversationMessageService;
//        this.sessionTitle = sessionTitle;
//    }
//
//    @Override
//    public List<Message> getMessages() {
//        List<AiBotConversationMessage> result = (List<AiBotConversationMessage>) cache.get(tempUserId + ":" + botId);
//        if (result == null || result.isEmpty()) {
//            return null;
//        }
//
//        AiBotConversationMessage aiBotConversationMessage = result.stream().filter(messages -> messages.getSessionId().equals(sessionId)).findFirst().orElse(null);
//        if (aiBotConversationMessage == null) {
//            return null;
//        }
//
//        List<AiBotMessage> aiBotMessageList = aiBotConversationMessage.getAiBotMessageList();
//
//        if (aiBotMessageList == null || aiBotMessageList.isEmpty()) {
//            return null;
//        }
//
//        List<Message> messages = new ArrayList<>();
//        for (AiBotMessage aiBotMessage : aiBotMessageList) {
//            Message message1 = aiBotMessage.toMessage();
//            if (message1 != null) {
//                messages.add(message1);
//            }
//        }
//
//        return messages;
//    }
//
//    @Override
//    public void addMessage(Message message) {
//
//        AiBotMessage aiMessage = new AiBotMessage();
//        aiMessage.setCreated(new Date());
//        aiMessage.setBotId(botId);
//        aiMessage.setSessionId(sessionId);
//        aiMessage.setIsExternalMsg(1);
//
//        if (message instanceof AiMessage) {
//            AiMessage m = (AiMessage) message;
//            aiMessage.setContent(m.getFullContent());
//            aiMessage.setRole("assistant");
//            aiMessage.setTotalTokens(m.getTotalTokens());
//            aiMessage.setPromptTokens(m.getPromptTokens());
//            aiMessage.setCompletionTokens(m.getCompletionTokens());
//            Map<String, Object> metadataMap = m.getMetadataMap();
//            aiMessage.setOptions(metadataMap);
//
//            List<FunctionCall> calls = m.getCalls();
//            if (CollectionUtil.isNotEmpty(calls)) {
//                return;
//            }
//        } else if (message instanceof HumanMessage) {
//            HumanMessage hm = (HumanMessage) message;
//            aiMessage.setContent(hm.getContent());
//            List<Function> functions = hm.getFunctions();
//            aiMessage.setFunctions(JSON.toJSONString(functions, SerializerFeature.WriteClassName));
//            aiMessage.setRole("user");
//            Map<String, Object> metadataMap = hm.getMetadataMap();
//
//            if (metadataMap == null){
//                metadataMap = new HashMap<>();
//            }
//
//            Object type = metadataMap.get("type");
//            if (type != null) {
//                String t = type.toString();
//                if (!t.equals("1")){
//                    metadataMap.put("type",2);
//                }
//            }else {
//                metadataMap.put("type",0);
//            }
//
//            aiMessage.setOptions(metadataMap);
//
//        } else if (message instanceof SystemMessage) {
//            aiMessage.setRole("system");
//            aiMessage.setContent(((SystemMessage) message).getContent());
//        }
//        if (StrUtil.isNotEmpty(aiMessage.getContent())) {
//
//            List<AiBotConversationMessage> aiBotConversationMessages = (List<AiBotConversationMessage>) cache.get(tempUserId + ":" + botId);
//            if (aiBotConversationMessages == null || aiBotConversationMessages.isEmpty()) {
//                aiBotConversationMessages = new ArrayList<>();
//            }
//            AiBotConversationMessage aiBotConversationMessage = aiBotConversationMessages.stream().filter(conversation -> conversation.getSessionId().equals(sessionId)).findFirst().orElse(null);
//            if (aiBotConversationMessage == null) {
//                aiBotConversationMessage = new AiBotConversationMessage();
//                aiBotConversationMessage.setSessionId(sessionId);
//                aiBotConversationMessage.setTitle(sessionTitle);
//                aiBotConversationMessage.setCreated(new Date());
//                aiBotConversationMessages.add(aiBotConversationMessage);
//            }
//
//            List<AiBotMessage> aiBotMessageList = aiBotConversationMessage.getAiBotMessageList();
//            if (aiBotMessageList == null || aiBotMessageList.isEmpty()) {
//                aiBotMessageList = new ArrayList<>();
//            }
//            aiBotMessageList.add(aiMessage);
//            aiBotConversationMessage.setAiBotMessageList(aiBotMessageList);
//
//            cache.put(tempUserId + ":" + botId, aiBotConversationMessages);
//        }
//    }
//
//    @Override
//    public Object id() {
//        return null;
//    }
//}

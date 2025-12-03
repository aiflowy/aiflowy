//
//package tech.aiflowy.ai.message.thirdPart;
//import com.agentsflex.core.llm.functions.Function;
//import com.agentsflex.core.memory.ChatMemory;
//import com.agentsflex.core.message.FunctionCall;
//import com.agentsflex.core.message.Message;
//
//import java.util.List;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.alicp.jetcache.Cache;
//import java.math.BigInteger;
//
//import com.mybatisflex.core.util.CollectionUtil;
//import tech.aiflowy.ai.entity.AiBotMessage;
//import java.sql.Date;
//import java.time.Instant;
//import java.util.Map;
//
//import com.agentsflex.core.message.AiMessage;
//import com.agentsflex.core.message.HumanMessage;
//import com.agentsflex.core.message.SystemMessage;
//import java.util.HashMap;
//import java.util.ArrayList;
//
//public class ThirdPartChatMemory implements ChatMemory {
//
//    private final Cache<String,Object> cache;
//
//    private final BigInteger botId;
//
//    private final String sessionId;
//
//    private final String platform;
//
//
//    public ThirdPartChatMemory(Cache<String,Object> cache,BigInteger botId,String sessionId,String platform) {
//        this.cache = cache;
//        this.botId = botId;
//        this.sessionId = sessionId;
//        this.platform = platform;
//    }
//
//    @Override
//    public List<Message> getMessages() {
//
//        String cacheKey = getCacheKey();
//        List<AiBotMessage> aiBotMessageList = (List<AiBotMessage>) cache.get(cacheKey);
//        List<Message> messageList = new ArrayList<>();
//
//        if (aiBotMessageList != null && !aiBotMessageList.isEmpty()){
//            aiBotMessageList.forEach(aiBotMessage -> {
//                Message message = aiBotMessage.toMessage();
//                messageList.add(message);
//            });
//        }
//
//
//        return messageList;
//    }
//
//    @Override
//    public void addMessage(Message message) {
//
//        AiBotMessage aiBotMessage = new AiBotMessage();
//        aiBotMessage.setBotId(botId);
//        aiBotMessage.setSessionId(sessionId);
//        aiBotMessage.setCreated(Date.from(Instant.now()));
//
//        if (message instanceof AiMessage){
//
//            AiMessage m = (AiMessage) message;
//            aiBotMessage.setContent(m.getFullContent());
//            aiBotMessage.setRole("assistant");
//            aiBotMessage.setTotalTokens(m.getTotalTokens());
//            aiBotMessage.setPromptTokens(m.getPromptTokens());
//            aiBotMessage.setCompletionTokens(m.getCompletionTokens());
//            Map<String, Object> metadataMap = m.getMetadataMap();
//
//            if (metadataMap == null){
//                metadataMap = new HashMap<>();
//            }
//            metadataMap.put("platform",platform);
//
//            aiBotMessage.setOptions(metadataMap);
//
//            List<FunctionCall> calls = m.getCalls();
//            if (CollectionUtil.isNotEmpty(calls)) {
//                return;
//            }
//
//        } else if (message instanceof HumanMessage) {
//
//            HumanMessage hm = (HumanMessage) message;
//            aiBotMessage.setContent(hm.getContent());
//            List<Function> functions = hm.getFunctions();
//            aiBotMessage.setFunctions(JSON.toJSONString(functions, SerializerFeature.WriteClassName));
//            aiBotMessage.setRole("user");
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
//            metadataMap.put("platform",platform);
//
//            aiBotMessage.setOptions(metadataMap);
//
//
//        } else if (message instanceof SystemMessage) {
//            aiBotMessage.setRole("system");
//            aiBotMessage.setContent(((SystemMessage) message).getContent());
//            Map<String, Object> metadataMap = message.getMetadataMap();
//
//            if (metadataMap == null){
//                metadataMap = new HashMap<>();
//            }
//
//            metadataMap.put("platform",platform);
//            aiBotMessage.setOptions(metadataMap);
//        }
//
//        String cacheKey = getCacheKey();
//        List<AiBotMessage> messageList = (List<AiBotMessage>) cache.get(cacheKey);
//
//        if (messageList == null){
//            messageList = new ArrayList<>();
//        }
//
//        messageList.add(aiBotMessage);
//        cache.put(cacheKey,messageList);
//
//    }
//
//
//    private String getCacheKey(){
//        return this.platform + ":" + this.botId + ":" + this.sessionId;
//    }
//
//    @Override
//    public Object id() {
//        return null;
//    }
//}

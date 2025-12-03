//package tech.aiflowy.admin.controller.ai;
//
//import cn.hutool.core.util.IdUtil;
//import com.agentsflex.core.llm.ChatContext;
//import com.agentsflex.core.llm.ChatOptions;
//import com.agentsflex.core.llm.Llm;
//import com.agentsflex.core.llm.StreamResponseListener;
//import com.agentsflex.core.llm.client.OkHttpClientUtil;
//import com.agentsflex.core.llm.response.AiMessageResponse;
//import com.agentsflex.core.message.AiMessage;
//import com.agentsflex.core.message.HumanMessage;
//import com.agentsflex.core.message.SystemMessage;
//import com.agentsflex.core.prompt.HistoriesPrompt;
//import com.agentsflex.llm.ollama.OllamaLlm;
//import com.agentsflex.llm.ollama.OllamaLlmConfig;
//import com.agentsflex.llm.openai.OpenAILlm;
//import com.agentsflex.llm.openai.OpenAILlmConfig;
//import com.alibaba.fastjson.JSON;
//import com.alicp.jetcache.Cache;
//import okhttp3.OkHttpClient;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//import tech.aiflowy.ai.service.AiBotConversationMessageService;
//import tech.aiflowy.ai.utils.AiBotMessageIframeMemory;
//import tech.aiflowy.common.ai.MySseEmitter;
//import tech.aiflowy.common.domain.Result;
//import tech.aiflowy.common.util.Maps;
//import tech.aiflowy.common.web.exceptions.BusinessException;
//import tech.aiflowy.common.web.jsonbody.JsonBody;
//import tech.aiflowy.system.options.DefaultOptionStore;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.math.BigInteger;
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/v1/system/bot")
//public class SystemBotController {
//
//    @Resource
//    private DefaultOptionStore defaultOptionStore;
//
//    private static final String MODEL_OF_CHAT = "model_of_chat";
//
//    private static final String CHATGPT_API_KEY = "chatgpt_api_key";
//
//    private static final String CHATGPT_MODEL_NAME  = "chatgpt_model_name";
//
//    private static final String CHATGPT_ENDPOINT = "chatgpt_endpoint";
//
//    private static final String CHATGPT_CHAT_PATH = "chatgpt_chatPath";
//
//
//    private String tempUserId;
//
//    private BigInteger botId;
//
//    private String sessionId;
//
//    @Resource
//    private Cache<String,Object> cache;
//
//    @Resource
//    private AiBotConversationMessageService  aiBotConversationMessageService;
//
//    @PostConstruct
//    public void init(){
//        this.tempUserId = UUID.randomUUID().toString();
//        this.botId = BigInteger.valueOf(IdUtil.getSnowflake(1, 1).nextId());
//        this.sessionId = UUID.randomUUID().toString();
//    }
//
//
//    @PostMapping("/chat")
//    public SseEmitter chat(@JsonBody(value = "prompt",required = true) String prompt, HttpServletResponse response){
//
//
//        String brand = defaultOptionStore.get(MODEL_OF_CHAT);
//        String apiKey = defaultOptionStore.get(CHATGPT_API_KEY);
//        String modelName = defaultOptionStore.get(CHATGPT_MODEL_NAME);
//        String endpoint = defaultOptionStore.get(CHATGPT_ENDPOINT);
//        String chatPath = defaultOptionStore.get(CHATGPT_CHAT_PATH);
//
//        Llm llm = getLlm(brand, apiKey, modelName, endpoint, chatPath);
//        HistoriesPrompt historiesPrompt = new HistoriesPrompt();
//        AiBotMessageIframeMemory memory = new AiBotMessageIframeMemory(botId, tempUserId, sessionId, cache,
//                aiBotConversationMessageService, prompt);
//        historiesPrompt.setMemory(memory);
//
//        historiesPrompt.setSystemMessage(SystemMessage.of("你是一个AI助手，请根据用户的问题给出清晰、准确的回答。"));
//        ChatOptions chatOptions = getDefaultChatOptions();
//        historiesPrompt.setMaxAttachedMessageCount(10);
//
//        HumanMessage humanMessage = new HumanMessage(prompt);
//        historiesPrompt.addMessage(humanMessage);
//
//        final OkHttpClient.Builder[] builder = {new OkHttpClient.Builder()};
//        builder[0].connectTimeout(Duration.ofSeconds(30));
//        builder[0].readTimeout(Duration.ofMinutes(20));
//        OkHttpClientUtil.setOkHttpClientBuilder(builder[0]);
//
//        MySseEmitter mySseEmitter = new MySseEmitter(1000 * 60 * 300L);
//
//        AiMessage thinkingMessage = new AiMessage();
//        Map<String, Object> thinkingIdMap = new HashMap<>();
//        final boolean[] thinkingMessageAdded = {false};
//
//        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        llm.chatStream(historiesPrompt, new StreamResponseListener() {
//            @Override
//            public void onMessage(ChatContext chatContext, AiMessageResponse aiMessageResponse) {
//                RequestContextHolder.setRequestAttributes(sra, true);
//                AiMessage message = aiMessageResponse.getMessage();
//                String reasoningContent = message.getReasoningContent();
//                String content = message.getContent();
//                if (StringUtils.hasLength(reasoningContent)){
//                    if (thinkingIdMap.get("id") == null) {
//                        thinkingIdMap.put("id", IdUtil.getSnowflake(1, 1).nextId());
//                    }
//                    thinkingIdMap.put("chainTitle", "🧠 思考");
//
//                    thinkingMessage.setContent(reasoningContent);
//                    thinkingMessage.setMetadataMap(thinkingIdMap);
//
//                    try {
//                        mySseEmitter.send(SseEmitter.event().name("thinking").data(JSON.toJSONString(thinkingMessage)));
//                    } catch (IOException e) {
//                        throw new BusinessException("发送思考事件报错");
//                    }
//                }
//
//                if (StringUtils.hasLength(content)){
//
//                    if (StringUtils.hasLength(thinkingMessage.getContent()) && !thinkingMessageAdded[0]) {
//                        historiesPrompt.addMessage(thinkingMessage);
//                        thinkingMessageAdded[0] = true;
//                    }
//
//                    AiMessage aiMessage = new AiMessage();
//                    aiMessage.setContent(content);
//                    mySseEmitter.send(JSON.toJSONString(aiMessage));
//                }
//            }
//
//            @Override
//            public void onStop(ChatContext context) {
//                mySseEmitter.complete();
//            }
//
//            @Override
//            public void onFailure(ChatContext context, Throwable throwable) {
//                StreamResponseListener.logger.error("sse报错：{}",throwable.getMessage());
//                AiMessage aiMessage = new AiMessage();
//                aiMessage.setContent("大模型调用出错，请检查配置");
//                mySseEmitter.send(JSON.toJSONString(aiMessage));
//                mySseEmitter.completeWithError(throwable);
//            }
//        },chatOptions);
//
//        return mySseEmitter;
//    }
//
//    @GetMapping("/clearMessage")
//    public Result<Void> clearMessage(){
//        cache.remove(tempUserId + ":" + botId);
//        return Result.ok();
//    }
//
//    private Llm getLlm(String brand,String apiKey,String modelName,String endpoint,String chatPath){
//
//        if ("ollama".equalsIgnoreCase(brand)) {
//            OllamaLlmConfig ollamaLlmConfig = new OllamaLlmConfig();
//            ollamaLlmConfig.setApiKey(apiKey);
//            ollamaLlmConfig.setEndpoint(endpoint);
//            ollamaLlmConfig.setModel(modelName);
//
//            return new OllamaLlm(ollamaLlmConfig);
//        }
//
//        OpenAILlmConfig openAILlmConfig = new OpenAILlmConfig();
//        openAILlmConfig.setApiKey(apiKey);
//        openAILlmConfig.setEndpoint(endpoint);
//        openAILlmConfig.setModel(modelName);
//        openAILlmConfig.setChatPath(chatPath);
//
//        return new OpenAILlm(openAILlmConfig);
//    }
//
//
//    private ChatOptions getDefaultChatOptions() {
//
//        ChatOptions chatOptions = new ChatOptions();
//        chatOptions.setTemperature(0.7f);
//        chatOptions.setTopK(4);
//        chatOptions.setMaxTokens(2048);
//        chatOptions.setTopP(0.7f);
//
//        return chatOptions;
//    }
//
//}

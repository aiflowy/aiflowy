//package tech.aiflowy.ai.message.thirdPart;
//
//
//import com.agentsflex.core.llm.ChatOptions;
//import com.agentsflex.core.prompt.HistoriesPrompt;
//import com.mybatisflex.core.query.QueryWrapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import tech.aiflowy.ai.entity.AiBot;
//import tech.aiflowy.ai.entity.AiBotKnowledge;
//import tech.aiflowy.ai.entity.AiBotPlugins;
//import tech.aiflowy.ai.entity.AiBotWorkflow;
//import tech.aiflowy.ai.entity.AiLlm;
//import tech.aiflowy.ai.entity.AiPluginTool;
//import tech.aiflowy.common.web.exceptions.BusinessException;
//import javax.annotation.Resource;
//import tech.aiflowy.ai.service.AiBotService;
//import tech.aiflowy.ai.service.AiBotApiKeyService;
//import tech.aiflowy.ai.service.AiBotPluginsService;
//import tech.aiflowy.ai.service.AiPluginToolService;
//import tech.aiflowy.ai.service.AiBotKnowledgeService;
//import tech.aiflowy.ai.service.AiKnowledgeService;
//import tech.aiflowy.ai.service.AiBotWorkflowService;
//import tech.aiflowy.ai.service.AiWorkflowService;
//import tech.aiflowy.common.util.StringUtil;
//import tech.aiflowy.common.util.Maps;
//import tech.aiflowy.ai.entity.AiBotLlm;
//import tech.aiflowy.ai.service.AiBotLlmService;
//import tech.aiflowy.ai.service.AiLlmService;
//import com.agentsflex.core.llm.Llm;
//
//@Service
//public class MessageHandlerService {
//
//    private final Map<String, MessageHandler> handlerMap;
//
//    private static final Logger log = LoggerFactory.getLogger(MessageHandlerService.class);
//
//    @Autowired
//    public MessageHandlerService(List<MessageHandler> handlers) {
//        log.info("注入 messageHandlers");
//        this.handlerMap = handlers.stream()
//        .collect(Collectors.toMap(
//            MessageHandler::getPlatformType,
//            Function.identity()
//        ));
//    }
//
//    @Resource
//    private AiBotService aiBotService;
//
//    @Resource
//    private AiBotApiKeyService aiBotApiKeyService;
//
//    @Resource
//    private AiBotPluginsService aiBotPluginsService;
//
//    @Resource
//    private AiPluginToolService pluginToolService;
//
//    @Resource
//    private AiBotKnowledgeService aiBotKnowledgeService;
//
//    @Resource
//    private AiKnowledgeService aiKnowledgeService;
//
//    @Resource
//    private AiBotWorkflowService aiBotWorkflowService;
//
//    @Resource
//    private AiWorkflowService aiWorkflowService;
//
//    @Resource
//    private AiBotApiKeyService apiKeyService;
//
//    @Resource
//    private AiBotLlmService aiBotLlmService;
//
//    @Resource
//    private AiLlmService llmService;
//
//    /**
//     * 根据平台类型获取对应的消息处理器
//     */
//    public MessageHandler getHandler(String platformType) {
//        MessageHandler handler = handlerMap.get(platformType);
//        if (handler == null) {
//            log.error("不支持的平台类型: {}",platformType);
//            throw new BusinessException("不支持的平台类型: " + platformType);
//        }
//        return handler;
//    }
//
//    /**
//     * 处理消息
//     */
//    public Object handleMessage(String platformType, Object messageData, Map<String, Object> contextData) {
//        MessageHandler handler = getHandler(platformType);
//
//        String apiKey = (String)contextData.get("apiKey");
//        if (!StringUtil.hasLength(apiKey)){
//            log.error("apiKey 为空");
//            throw new BusinessException("apiKey 为空");
//        }
//
//        Map<String,Object> agentParams = buildReActAgentParams(apiKey);
//        if (agentParams == null || agentParams.isEmpty()){
//            log.error("构建 agent 失败，agent 为 null");
//            throw new BusinessException("构建 agent 失败");
//        }
//
//
//        return handler.handleMessage(messageData, contextData,agentParams);
//    }
//
//    private  Map<String,Object> buildReActAgentParams(String apiKey) {
//
//        BigInteger botId = apiKeyService.decryptApiKey(apiKey);
//
//        if (botId == null) {
//            log.error("botId 为空");
//            return null;
//        }
//
//        AiBot aiBot = aiBotService.getById(botId);
//
//        if (aiBot == null){
//            log.error("bot 不存在");
//            return null;
//        }
//
//        Map<String, Object> botOptions = aiBot.getOptions();
//
//        BigInteger llmId = aiBot.getLlmId();
//        if (llmId == null) {
//            log.error("此 bot 没有绑定大模型");
//            return null;
//        }
//
//        AiLlm aiLlm = llmService.getById(llmId);
//        if (aiLlm == null){
//            log.error("大模型为空");
//            return null;
//        }
//
//        Map<String, Object> llmOptions = aiBot.getLlmOptions();
//        Llm llm = aiLlm.toLlm();
//
//        final HistoriesPrompt historiesPrompt = new HistoriesPrompt();
//
//        if (llmOptions != null && llmOptions.get("maxMessageCount") != null) {
//            Object maxMessageCount = llmOptions.get("maxMessageCount");
//            historiesPrompt.setMaxAttachedMessageCount(Integer.parseInt(String.valueOf(maxMessageCount)));
//        }
//
//        List<com.agentsflex.core.llm.functions.Function > functions = buildFunctionList(Maps.of("botId", botId).set("needEnglishName", true));
//
//        ChatOptions chatOptions = getChatOptions(llmOptions);
//        return Maps.of("llm",llm)
//        .set("functions",functions)
//        .set("historiesPrompt",historiesPrompt)
//        .set("chatOptions",chatOptions)
//        .set("botId",botId)
//        .setIfNotEmpty("botOptions",botOptions)
//        ;
//
//    }
//
//
//    private List<com.agentsflex.core.llm.functions.Function > buildFunctionList(Map<String, Object> buildParams) {
//
//        if (buildParams == null || buildParams.isEmpty()) {
//            throw new IllegalArgumentException("buildParams is empty");
//        }
//
//        List<com.agentsflex.core.llm.functions.Function > functionList = new ArrayList<>();
//
//        BigInteger botId = (BigInteger) buildParams.get("botId");
//        if (botId == null) {
//            throw new IllegalArgumentException("botId is empty");
//        }
//        Boolean needEnglishName = (Boolean) buildParams.get("needEnglishName");
//        if (needEnglishName == null) {
//            needEnglishName = false;
//        }
//
//        QueryWrapper queryWrapper = QueryWrapper.create();
//
//        // 工作流 function 集合
//        queryWrapper.eq(AiBotWorkflow::getBotId, botId);
//        List<AiBotWorkflow> aiBotWorkflows = aiBotWorkflowService.getMapper()
//            .selectListWithRelationsByQuery(queryWrapper);
//        if (aiBotWorkflows != null && !aiBotWorkflows.isEmpty()) {
//            for (AiBotWorkflow aiBotWorkflow : aiBotWorkflows) {
//                com.agentsflex.core.llm.functions.Function function = aiBotWorkflow.getWorkflow().toFunction(needEnglishName);
//                functionList.add(function);
//            }
//        }
//
//        // 知识库 function 集合
//        queryWrapper = QueryWrapper.create();
//        queryWrapper.eq(AiBotKnowledge::getBotId, botId);
//        List<AiBotKnowledge> aiBotKnowledges = aiBotKnowledgeService.getMapper()
//            .selectListWithRelationsByQuery(queryWrapper);
//        if (aiBotKnowledges != null && !aiBotKnowledges.isEmpty()) {
//            for (AiBotKnowledge aiBotKnowledge : aiBotKnowledges) {
//                com.agentsflex.core.llm.functions.Function  function = aiBotKnowledge.getKnowledge().toFunction(needEnglishName);
//                functionList.add(function);
//            }
//        }
//
//        // 插件 function 集合
//        queryWrapper = QueryWrapper.create();
//        queryWrapper.select("plugin_tool_id").eq(AiBotPlugins::getBotId, botId);
//        List<BigInteger> pluginToolIds = aiBotPluginsService.getMapper()
//            .selectListWithRelationsByQueryAs(queryWrapper, BigInteger.class);
//
//        if (pluginToolIds == null || pluginToolIds.isEmpty()) {
//            return functionList;
//        }
//
//        QueryWrapper queryTool = QueryWrapper.create().select("*").from("tb_ai_plugin_tool").in("id", pluginToolIds);
//        List<AiPluginTool> aiPluginTools = pluginToolService.getMapper().selectListWithRelationsByQuery(queryTool);
//        if (aiPluginTools != null && !aiPluginTools.isEmpty()) {
//            for (AiPluginTool aiPluginTool : aiPluginTools) {
//                functionList.add(aiPluginTool.toFunction());
//            }
//        }
//
//        return functionList;
//    }
//
//
//    private ChatOptions getChatOptions(Map<String, Object> llmOptions) {
//        ChatOptions defaultOptions = new ChatOptions();
//        if (llmOptions != null) {
//            Object topK = llmOptions.get("topK");
//            Object maxReplyLength = llmOptions.get("maxReplyLength");
//            Object temperature = llmOptions.get("temperature");
//            Object topP = llmOptions.get("topP");
//            if (topK != null) {
//                defaultOptions.setTopK(Integer.parseInt(String.valueOf(topK)));
//            }
//            if (maxReplyLength != null) {
//                defaultOptions.setMaxTokens(Integer.parseInt(String.valueOf(maxReplyLength)));
//            }
//            if (temperature != null) {
//                defaultOptions.setTemperature(Float.parseFloat(String.valueOf(temperature)));
//            }
//            if (topP != null) {
//                defaultOptions.setTopP(Float.parseFloat(String.valueOf(topP)));
//            }
//        }
//        return defaultOptions;
//    }
//
//}
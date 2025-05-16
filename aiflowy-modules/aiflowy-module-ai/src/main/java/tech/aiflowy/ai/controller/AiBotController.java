package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.llm.response.FunctionCaller;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.agentsflex.core.util.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.*;
import tech.aiflowy.ai.mapper.AiBotConversationMessageMapper;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.common.ai.ChatManager;
import tech.aiflowy.common.ai.MySseEmitter;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.system.mapper.SysApiKeyMapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiBot")
public class AiBotController extends BaseCurdController<AiBotService, AiBot> {

    private final AiLlmService aiLlmService;
    private final AiBotWorkflowService aiBotWorkflowService;
    private final AiBotKnowledgeService aiBotKnowledgeService;
    private final AiBotMessageService aiBotMessageService;
    @Resource
    private SysApiKeyMapper aiBotApiKeyMapper;
    @Resource
    private AiBotConversationMessageService aiBotConversationMessageService;
    @Resource
    private AiBotConversationMessageMapper aiBotConversationMessageMapper;

    public AiBotController(AiBotService service, AiLlmService aiLlmService, AiBotWorkflowService aiBotWorkflowService, AiBotKnowledgeService aiBotKnowledgeService, AiBotMessageService aiBotMessageService) {
        super(service);
        this.aiLlmService = aiLlmService;
        this.aiBotWorkflowService = aiBotWorkflowService;
        this.aiBotKnowledgeService = aiBotKnowledgeService;
        this.aiBotMessageService = aiBotMessageService;
    }

    @Resource
    private AiPluginsService aiPluginsService;
    @Resource
    private AiBotPluginsService aiBotPluginsService;
    @Resource
    private AiPluginToolService aiPluginToolService;

    @PostMapping("updateOptions")
    public Result updateOptions(@JsonBody("id") BigInteger id, @JsonBody("options") Map<String, Object> options) {
        AiBot aiBot = service.getById(id);
        Map<String, Object> existOptions = aiBot.getOptions();
        if (existOptions == null) {
            existOptions = new HashMap<>();
        }
        if (options != null) {
            existOptions.putAll(options);
        }
        aiBot.setOptions(existOptions);
        service.updateById(aiBot);
        return Result.success();
    }


    @PostMapping("updateLlmOptions")
    public Result updateLlmOptions(@JsonBody("id") BigInteger id, @JsonBody("llmOptions") Map<String, Object> llmOptions) {
        AiBot aiBot = service.getById(id);
        Map<String, Object> existLlmOptions = aiBot.getLlmOptions();
        if (existLlmOptions == null) {
            existLlmOptions = new HashMap<>();
        }
        if (llmOptions != null) {
            existLlmOptions.putAll(llmOptions);
        }
        aiBot.setLlmOptions(existLlmOptions);
        service.updateById(aiBot);
        return Result.success();
    }

    /**
     * 当前系统用户调用对话
     *
     * @param prompt
     * @param botId
     * @param sessionId
     * @param isExternalMsg
     * @param response
     * @return
     */
    @PostMapping("chat")
    public SseEmitter chat(@JsonBody(value = "prompt", required = true) String prompt,
                           @JsonBody(value = "botId", required = true) BigInteger botId,
                           @JsonBody(value = "sessionId", required = true) String sessionId,
                           @JsonBody(value = "isExternalMsg") int isExternalMsg,
                           HttpServletResponse response) {
        response.setContentType("text/event-stream");
        AiBot aiBot = service.getById(botId);
        if (aiBot == null) {
            return ChatManager.getInstance().sseEmitterForContent("机器人不存在");
        }

        Map<String, Object> llmOptions = aiBot.getLlmOptions();
        String systemPrompt = llmOptions != null ? (String) llmOptions.get("systemPrompt") : null;
        AiLlm aiLlm = aiLlmService.getById(aiBot.getLlmId());

        if (aiLlm == null) {
            return ChatManager.getInstance().sseEmitterForContent("LLM不存在");
        }

        Llm llm = aiLlm.toLlm();

        if (llm == null) {
            return ChatManager.getInstance().sseEmitterForContent("LLM获取为空");
        }

        AiBotMessageMemory memory = new AiBotMessageMemory(botId, SaTokenUtil.getLoginAccount().getId(),
                sessionId, isExternalMsg, aiBotMessageService, aiBotConversationMessageMapper,
                aiBotConversationMessageService);

        final HistoriesPrompt historiesPrompt = new HistoriesPrompt();
        if (systemPrompt != null) {
            historiesPrompt.setSystemMessage(SystemMessage.of(systemPrompt));
        }

        historiesPrompt.setMemory(memory);

        HumanMessage humanMessage = new HumanMessage(prompt);

        // 添加插件相关的function calling
        appendPluginToolFunction(botId, humanMessage);

        //添加工作流相关的 Function Calling
        appendWorkflowFunctions(botId, humanMessage);

        //添加知识库相关的 Function Calling
        appendKnowledgeFunctions(botId, humanMessage);

        historiesPrompt.addMessage(humanMessage);

        MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));

        final Boolean[] needClose = {true};
        if (humanMessage.getFunctions() != null && !humanMessage.getFunctions().isEmpty()) {
            try {
                AiMessageResponse aiMessageResponse = llm.chat(historiesPrompt);
                function_call(aiMessageResponse, emitter, needClose, historiesPrompt, llm, prompt, false);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }

            if (needClose[0]) {
                System.out.println("function chat complete");
                emitter.complete();
            }
        } else {

            llm.chatStream(historiesPrompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext context, AiMessageResponse response) {
                    try {

                        function_call(response, emitter, needClose, historiesPrompt, llm, prompt, false);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onStop(ChatContext context) {
                    if (needClose[0]) {
                        System.out.println("normal chat complete");
                        emitter.complete();
                    }
                }

                @Override
                public void onFailure(ChatContext context, Throwable throwable) {
                    emitter.completeWithError(throwable);
                }
            });
        }

        return emitter;
    }

    /**
     * 外部用户调用智能体进行对话
     * 需要用户传 apiKey 对用户进行身份验证
     *
     * @param stream [true: 返回sse false： 返回json
     * @return
     */
    @SaIgnore
    @PostMapping("externalChat")
    public Object externalChat(
            @JsonBody(value = "messages", required = true) List<AiBotMessage> messages,
            @JsonBody(value = "botId", required = true) BigInteger botId,
            @JsonBody(value = "stream", required = false) boolean stream,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        // 设置响应类型
        if (stream) {
            response.setContentType("text/event-stream");
        } else {
            response.setContentType("application/json");
        }

        // 获取 API Key 和 Bot 信息
        String apiKey = request.getHeader("Authorization");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("api_key", "status", "expired_at")
                .from("tb_sys_api_key")
                .where("api_key = ? ", apiKey);
        SysApiKey aiBotApiKey = aiBotApiKeyMapper.selectOneByQuery(queryWrapper);
        if (aiBotApiKey == null) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(1, "该apiKey不存在")));
        }
        if (aiBotApiKey.getStatus() == 0) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(2, "该apiKey未启用")));
        }

        if (aiBotApiKey.getExpiredAt().getTime() < new Date().getTime()) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(3, "该apiKey已失效")));

        }

        AiBot aiBot = service.getById(botId);
        if (aiBot == null) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(4, "机器人不存在")));
        }

        Map<String, Object> llmOptions = aiBot.getLlmOptions();
        String systemPrompt = llmOptions != null ? (String) llmOptions.get("systemPrompt") : null;

        AiLlm aiLlm = aiLlmService.getById(aiBot.getLlmId());
        if (aiLlm == null) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(5, "LLM不存在")));
        }

        Llm llm = aiLlm.toLlm();
        AiBotExternalMessageMemory messageMemory = new AiBotExternalMessageMemory(messages);
        HistoriesPrompt historiesPrompt = new HistoriesPrompt();
        if (systemPrompt != null) {
            historiesPrompt.setSystemMessage(SystemMessage.of(systemPrompt));
        }
        historiesPrompt.setMemory(messageMemory);

        String prompt = messages.get(messages.size() - 1).getContent();
        HumanMessage humanMessage = new HumanMessage();

        // 添加插件、工作流、知识库相关的 Function Calling
        appendPluginToolFunction(botId, humanMessage);
        appendWorkflowFunctions(botId, humanMessage);
        appendKnowledgeFunctions(botId, humanMessage);

        historiesPrompt.addMessage(humanMessage);

        // 根据 responseType 返回不同的响应
        if (stream) {
            MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));
            final Boolean[] needClose = {true};

            if (humanMessage.getFunctions() != null && !humanMessage.getFunctions().isEmpty()) {
                try {
                    AiMessageResponse aiMessageResponse = llm.chat(historiesPrompt);
                    function_call(aiMessageResponse, emitter, needClose, historiesPrompt, llm, prompt, true);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }

                if (needClose[0]) {
                    System.out.println("function chat complete");
                    emitter.complete();
                }
            } else {
                llm.chatStream(historiesPrompt, new StreamResponseListener() {
                    @Override
                    public void onMessage(ChatContext context, AiMessageResponse response) {
                        try {
                            function_call(response, emitter, needClose, historiesPrompt, llm, prompt, true);
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onStop(ChatContext context) {

                        if (needClose[0]) {
                            System.out.println("normal chat complete");
                            emitter.complete();
                        }
                    }

                    @Override
                    public void onFailure(ChatContext context, Throwable throwable) {
                        emitter.completeWithError(throwable);
                    }
                });
            }

            return emitter;
        } else {
            AiMessageResponse resultFunctionCall;
            if (humanMessage.getFunctions() != null && !humanMessage.getFunctions().isEmpty()) {
                try {
                    AiMessageResponse aiMessageResponse = llm.chat(historiesPrompt);
                    resultFunctionCall = jsonResultJsonFunctionCall(aiMessageResponse, historiesPrompt, llm, prompt);
                    return JSON.toJSONString(resultFunctionCall.getMessage(), new SerializeConfig());
                } catch (Exception e) {
                    return createErrorResponse(e);
                }
            } else {
                AiMessageResponse messageResponse = llm.chat(historiesPrompt);
                resultFunctionCall = jsonResultJsonFunctionCall(messageResponse, historiesPrompt, llm, prompt);
                AiBotExternalMsgJsonResult result = handleMessageResult(resultFunctionCall.getMessage());
                return JSON.toJSONString(result, new SerializeConfig());
            }
        }
    }

    private AiBotExternalMsgJsonResult handleMessageResult(AiMessage aiMessage) {
        AiBotExternalMsgJsonResult messageResult = new AiBotExternalMsgJsonResult();
        messageResult.setCreated(new Date().getTime());
        AiBotExternalMsgJsonResult.Usage usage = new AiBotExternalMsgJsonResult.Usage();
        usage.setTotalTokens(aiMessage.getTotalTokens());
        usage.setCompletionTokens(aiMessage.getCompletionTokens());
        usage.setPromptTokens(aiMessage.getPromptTokens());
        messageResult.setUsage(usage);
        AiBotExternalMsgJsonResult.Choice choice = new AiBotExternalMsgJsonResult.Choice();
        AiBotExternalMsgJsonResult.Message message = new AiBotExternalMsgJsonResult.Message();
        message.setContent(aiMessage.getContent());
        message.setRole("assistant");
        choice.setMessage(message);
        messageResult.setChoices(choice);
        messageResult.setStatus(aiMessage.getStatus().name());
        return messageResult;
    }

    // 辅助方法：创建响应
    private Object createResponse(boolean stream, String content) {
        if (stream) {
            MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));
            emitter.send(content);
            emitter.complete();
            return emitter;
        } else {
            return ResponseEntity.ok(content);
        }
    }

    // 辅助方法：创建错误响应
    private Object createErrorResponse(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    /**
     * @param aiMessageResponse 大模型返回的消息
     * @param emitter
     * @param needClose         是否需要关闭流
     * @param historiesPrompt   消息历史记录
     * @param llm               大模型
     * @param prompt            提示词
     * @param isExternalChatApi true 外部系统调用bot  false 内部系统调用bot
     */
    private String function_call(AiMessageResponse aiMessageResponse, MySseEmitter emitter, Boolean[] needClose, HistoriesPrompt historiesPrompt, Llm llm, String prompt, boolean isExternalChatApi) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);
        String content = aiMessageResponse.getMessage().getContent();
        Object messageContent = aiMessageResponse.getMessage();
        if (StringUtil.hasText(content)) {
            // 如果是外部系统调用chat
            if (isExternalChatApi) {
                AiBotExternalMsgJsonResult result = handleMessageStreamJsonResult(aiMessageResponse.getMessage());
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println(JSON.toJSONString(result, new SerializeConfig()));
                System.out.println("未完测试");

                emitter.send(JSON.toJSONString(result, new SerializeConfig()));
            } else {
                emitter.send(JSON.toJSONString(messageContent));
            }

        }
        List<FunctionCaller> functionCallers = aiMessageResponse.getFunctionCallers();
        if (CollectionUtil.hasItems(functionCallers)) {
            needClose[0] = false;
            for (FunctionCaller functionCaller : functionCallers) {
                Object result = functionCaller.call();
                if (ObjectUtil.isNotEmpty(result)) {

                    String newPrompt = "请根据以下内容回答用户，内容是:\n" + result + "\n 用户的问题是：" + prompt;
                    historiesPrompt.addMessageTemporary(new HumanMessage(newPrompt));

                    llm.chatStream(historiesPrompt, new StreamResponseListener() {
                        @Override
                        public void onMessage(ChatContext context, AiMessageResponse response) {
                            needClose[0] = true;
                            String content = response.getMessage().getContent();
                            Object messageContent = response.getMessage();
                            if (StringUtil.hasText(content)) {
                                String jsonResult = JSON.toJSONString(messageContent);
                                emitter.send(jsonResult);
                            }
                        }

                        @Override
                        public void onStop(ChatContext context) {
                            if (needClose[0]) {
                                System.out.println("function chat complete");
                                emitter.complete();
                            }
                            historiesPrompt.clearTemporaryMessages();
                        }

                        @Override
                        public void onFailure(ChatContext context, Throwable throwable) {
                            emitter.completeWithError(throwable);
                        }
                    });
                }
            }
        }
        return JSON.toJSONString(messageContent);
    }

    private Map<String, Object> errorRespnseMsg(int errorCode, String message) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("error", errorCode);
        result.put("message", message);
        return result;
    }

    private AiBotExternalMsgJsonResult handleMessageStreamJsonResult(AiMessage message) {
        AiBotExternalMsgJsonResult result = new AiBotExternalMsgJsonResult();
        AiBotExternalMsgJsonResult.Choice choice = new AiBotExternalMsgJsonResult.Choice();
        AiBotExternalMsgJsonResult.Delta delta = new AiBotExternalMsgJsonResult.Delta();
        delta.setRole("assistant");
        delta.setContent(message.getContent());
        choice.setDelta(delta);
        result.setCreated(new Date().getTime());
        result.setChoices(choice);
        result.setStatus(message.getStatus().name());

        return result;
    }

    private AiMessageResponse jsonResultJsonFunctionCall(AiMessageResponse aiMessageResponse, HistoriesPrompt historiesPrompt, Llm llm, String prompt) {
        List<FunctionCaller> functionCallers = aiMessageResponse.getFunctionCallers();
        if (CollectionUtil.hasItems(functionCallers)) {
            for (FunctionCaller functionCaller : functionCallers) {
                Object result = functionCaller.call();
                if (ObjectUtil.isNotEmpty(result)) {
                    String newPrompt = "请根据以下内容回答用户，内容是:\n" + result + "\n 用户的问题是：" + prompt;
                    historiesPrompt.addMessageTemporary(new HumanMessage(newPrompt));
                    return llm.chat(historiesPrompt);
                }
            }
        }
        return aiMessageResponse;
    }

    private void appendWorkflowFunctions(BigInteger botId, HumanMessage humanMessage) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(AiBotWorkflow::getBotId, botId);
        List<AiBotWorkflow> aiBotWorkflows = aiBotWorkflowService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        if (aiBotWorkflows != null) {
            for (AiBotWorkflow aiBotWorkflow : aiBotWorkflows) {
                Function function = aiBotWorkflow.getWorkflow().toFunction();
                humanMessage.addFunction(function);
            }
        }
    }

    private void appendKnowledgeFunctions(BigInteger botId, HumanMessage humanMessage) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(AiBotKnowledge::getBotId, botId);
        List<AiBotKnowledge> aiBotKnowledges = aiBotKnowledgeService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        if (aiBotKnowledges != null) {
            for (AiBotKnowledge aiBotKnowledge : aiBotKnowledges) {
                Function function = aiBotKnowledge.getKnowledge().toFunction();
                humanMessage.addFunction(function);
            }
        }
    }

    //    private void appendPluginFunctions(BigInteger botId, HumanMessage humanMessage) {
//        QueryWrapper queryWrapper = QueryWrapper.create().eq(AiBotPlugins::getBotId, botId);
//        List<AiBotPlugins> aiBotPlugins = aiBotPluginsService.getMapper().selectListWithRelationsByQuery(queryWrapper);
//        if (cn.hutool.core.collection.CollectionUtil.isNotEmpty(aiBotPlugins)) {
//            for (AiBotPlugins aiBotPlugin : aiBotPlugins) {
//                Function function = aiBotPlugin.getAiPlugins().toFunction();
//                humanMessage.addFunction(function);
//            }
//        }
//    }
    private void appendPluginToolFunction(BigInteger botId, HumanMessage humanMessage) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_tool_id").eq(AiBotPlugins::getBotId, botId);
        List<BigInteger> pluginToolIds = aiBotPluginsService.getMapper().selectListWithRelationsByQueryAs(queryWrapper, BigInteger.class);

        if (pluginToolIds == null || pluginToolIds.isEmpty()) {
            return;
        }

        QueryWrapper queryTool = QueryWrapper.create()
                .select("*")
                .from("tb_ai_plugin_tool")
                .in("id", pluginToolIds);
        List<AiPluginTool> aiPluginTools = aiPluginToolService.getMapper().selectListWithRelationsByQuery(queryTool);
        for (AiPluginTool item : aiPluginTools) {
            humanMessage.addFunction(item.toFunction());
        }


    }
}

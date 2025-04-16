package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.*;
import tech.aiflowy.ai.mapper.AiBotConversationMessageMapper;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.common.ai.ChatManager;
import tech.aiflowy.common.ai.MySseEmitter;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import cn.hutool.core.util.ObjectUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.llm.response.FunctionCaller;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.agentsflex.core.util.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
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

        AiLlm aiLlm = aiLlmService.getById(aiBot.getLlmId());
        if (aiLlm == null) {
            return ChatManager.getInstance().sseEmitterForContent("LLM不存在");
        }

        Llm llm = aiLlm.toLlm();

        AiBotMessageMemory memory = new AiBotMessageMemory(botId, SaTokenUtil.getLoginAccount().getId(),
                sessionId, isExternalMsg, aiBotMessageService, aiBotConversationMessageMapper,
                aiBotConversationMessageService);

        final HistoriesPrompt historiesPrompt = new HistoriesPrompt();
        historiesPrompt.setSystemMessage(SystemMessage.of((String) llmOptions.get("systemPrompt")));
        historiesPrompt.setMemory(memory);

        HumanMessage humanMessage = new HumanMessage(prompt);

        // 添加插件相关的function calling
        appendPluginFunctions(botId, humanMessage);

        //添加工作流相关的 Function Calling
        appendWorkflowFunctions(botId, humanMessage);

        //添加知识库相关的 Function Calling
        appendKnowledgeFunctions(botId, humanMessage);

        historiesPrompt.addMessage(humanMessage);

        MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));

        final Boolean[] needClose = {true};

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        llm.chatStream(historiesPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {
                try {
                    RequestContextHolder.setRequestAttributes(sra, true);
                    String content = response.getMessage().getContent();
                    Object messageContent = response.getMessage();
                    if (StringUtil.hasText(content)) {
                        String jsonResult = JSON.toJSONString(messageContent);
                        emitter.send(jsonResult);
                    }
                    List<FunctionCaller> functionCallers = response.getFunctionCallers();
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

        return emitter;
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

    private void appendPluginFunctions(BigInteger botId, HumanMessage humanMessage) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(AiBotPlugins::getBotId, botId);
        List<AiBotPlugins> aiBotPlugins = aiBotPluginsService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        if (cn.hutool.core.collection.CollectionUtil.isNotEmpty(aiBotPlugins)) {
            for (AiBotPlugins aiBotPlugin : aiBotPlugins) {
                Function function = aiBotPlugin.getAiPlugins().toFunction();
                humanMessage.addFunction(function);
            }
        }
    }
}
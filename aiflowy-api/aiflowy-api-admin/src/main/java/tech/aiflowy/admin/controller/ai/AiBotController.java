
package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.message.UserMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.model.chat.tool.Tool;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.Cache;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.*;
import tech.aiflowy.ai.mapper.AiBotConversationMessageMapper;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.common.ai.ChatManager;
import tech.aiflowy.common.audio.core.AudioServiceManager;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.util.Maps;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.mapper.SysApiKeyMapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

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
    @Resource
    private AiBotService aiBotService;
    @Autowired
    @Qualifier("defaultCache") // 指定 Bean 名称
    private Cache<String, Object> cache;
    @Resource
    private AudioServiceManager audioServiceManager;

    private static final Logger logger = LoggerFactory.getLogger(AiBotController.class);

    public AiBotController(AiBotService service, AiLlmService aiLlmService, AiBotWorkflowService aiBotWorkflowService,
                           AiBotKnowledgeService aiBotKnowledgeService, AiBotMessageService aiBotMessageService) {
        super(service);
        this.aiLlmService = aiLlmService;
        this.aiBotWorkflowService = aiBotWorkflowService;
        this.aiBotKnowledgeService = aiBotKnowledgeService;
        this.aiBotMessageService = aiBotMessageService;
    }

    @Resource
    private AiBotPluginsService aiBotPluginsService;
    @Resource
    private AiPluginToolService aiPluginToolService;

    @PostMapping("updateOptions")
    @SaCheckPermission("/api/v1/aiBot/save")
    public Result<Void> updateOptions(@JsonBody("id")
                                BigInteger id, @JsonBody("options")
                                Map<String, Object> options) {
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
        return Result.ok();
    }

    @PostMapping("updateLlmOptions")
    @SaCheckPermission("/api/v1/aiBot/save")
    public Result<Void> updateLlmOptions(@JsonBody("id")
                                   BigInteger id, @JsonBody("llmOptions")
                                   Map<String, Object> llmOptions) {
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
        return Result.ok();
    }

    @PostMapping("voiceInput")
    @SaIgnore
    public Result<String> voiceInput(@RequestParam("audio")
                             MultipartFile audioFile) {

        String recognize = null;
        try {
            recognize = audioServiceManager.audioToText(audioFile.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Result.ok("", recognize);
    }

    /**
     * 当前系统用户调用对话
     *
     * @param prompt
     * @param botId
     * @param sessionId
     * @param response
     * @return
     */
    @PostMapping("chat")
    @SaIgnore
    public SseEmitter chat(@JsonBody(value = "prompt", required = true)
                           String prompt, @JsonBody(value = "botId", required = true)
                           BigInteger botId, @JsonBody(value = "sessionId", required = true)
                           String sessionId, @JsonBody(value = "isExternalMsg")
                           HttpServletResponse response) {

        response.setContentType("text/event-stream");
        if (!StringUtils.hasLength(prompt)) {
            throw new BusinessException("提示词不能为空！");
        }

        AiBot aiBot = service.getById(botId);
        if (aiBot == null) {
            return ChatManager.getInstance().sseEmitterForContent(JSON.toJSONString(Maps.of("content", "机器人不存在")));
        }

        boolean login = StpUtil.isLogin();
        if (!login && !aiBot.isAnonymousEnabled()) {
            return ChatManager.getInstance().sseEmitterForContent(JSON.toJSONString(Maps.of("content", "此bot不支持匿名访问")));

        }

        Map<String, Object> llmOptions = aiBot.getLlmOptions();
        String systemPrompt = llmOptions != null ?
                (String) llmOptions.get("systemPrompt") == null || !StringUtils.hasLength((String) llmOptions.get("systemPrompt")) ? "你是一个AI助手，请根据用户的问题给出清晰、准确的回答。" : (String) llmOptions.get("systemPrompt")
                : null;

        AiLlm aiLlm = aiLlmService.getById(aiBot.getLlmId());
        if (aiLlm == null) {
            return ChatManager.getInstance().sseEmitterForContent(JSON.toJSONString(Maps.of("content", "LLM不存在")));
        }

        ChatModel chatModel = aiLlm.toChatModel();
        if (chatModel == null) {
            return ChatManager.getInstance().sseEmitterForContent(JSON.toJSONString(Maps.of("content", "LLM获取为空")));
        }
        final MemoryPrompt memoryPrompt = new MemoryPrompt();
        if (llmOptions != null && llmOptions.get("maxMessageCount") != null) {
            Object maxMessageCount = llmOptions.get("maxMessageCount");
            memoryPrompt.setMaxAttachedMessageCount(Integer.parseInt(String.valueOf(maxMessageCount)));
        }

        if (StringUtils.hasLength(systemPrompt)) {
            memoryPrompt.setSystemMessage(SystemMessage.of(systemPrompt));
        }

        if (StpUtil.isLogin()) {
            AiBotMessageMemory memory = new AiBotMessageMemory(botId, SaTokenUtil.getLoginAccount().getId(), sessionId,
                    0, aiBotMessageService);
            memoryPrompt.setMemory(memory);
        }
        UserMessage userMessage = new UserMessage(prompt);
        userMessage.addTools(buildFunctionList(Maps.of("botId", botId).set("needEnglishName", false)));
        memoryPrompt.addMessage(userMessage);
        ChatOptions chatOptions = getChatOptions(llmOptions);
        return aiBotService.startChat(botId, chatModel, prompt, memoryPrompt, chatOptions, sessionId);

    }

    @PostMapping("updateLlmId")
    @SaCheckPermission("/api/v1/aiBot/save")
    public Result<Void> updateBotLlmId(@RequestBody
                                 AiBot aiBot) {
        service.updateBotLlmId(aiBot);
        return Result.ok();
    }

    @GetMapping("getDetail")
    @SaIgnore
    public Result<AiBot> getDetail(String id) {
        return Result.ok(aiBotService.getDetail(id));
    }

    @Override
    @SaIgnore
    public Result<AiBot> detail(String id) {
        AiBot data = aiBotService.getDetail(id);
        if (data == null) {
            return Result.ok(data);
        }

        Map<String, Object> llmOptions = data.getLlmOptions();
        if (llmOptions == null) {
            llmOptions = new HashMap<>();
        }

        if (data.getLlmId() == null) {
            return Result.ok(data);
        }

        BigInteger llmId = data.getLlmId();
        AiLlm llm = aiLlmService.getById(llmId);

        if (llm == null) {
            data.setLlmId(null);
            return Result.ok(data);
        }

        Map<String, Object> options = llm.getOptions();

        if (options != null && !options.isEmpty()) {

            // 获取是否多模态
            Boolean multimodal = (Boolean) options.get("multimodal");
            llmOptions.put("multimodal", multimodal != null && multimodal);

        }

        return Result.ok(data);
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(AiBot entity, boolean isSave) {

        String alias = entity.getAlias();

        if (StringUtils.hasLength(alias)) {
            AiBot aiBot = service.getByAlias(alias);


            if (aiBot != null && isSave) {
                throw new BusinessException("别名已存在！");
            }

            if (aiBot != null && aiBot.getId().compareTo(entity.getId()) != 0) {
                throw new BusinessException("别名已存在！");
            }

        }


        if (isSave) {
            // 设置默认值
            entity.setLlmOptions(getDefaultLlmOptions());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    private ChatOptions getChatOptions(Map<String, Object> llmOptions) {
        ChatOptions defaultOptions = new ChatOptions();
        if (llmOptions != null) {
            Object topK = llmOptions.get("topK");
            Object maxReplyLength = llmOptions.get("maxReplyLength");
            Object temperature = llmOptions.get("temperature");
            Object topP = llmOptions.get("topP");
            Object thinkingEnabled = llmOptions.get("thinkingEnabled");

            if (topK != null) {
                defaultOptions.setTopK(Integer.parseInt(String.valueOf(topK)));
            }
            if (maxReplyLength != null) {
                defaultOptions.setMaxTokens(Integer.parseInt(String.valueOf(maxReplyLength)));
            }
            if (temperature != null) {
                defaultOptions.setTemperature(Float.parseFloat(String.valueOf(temperature)));
            }
            if (topP != null) {
                defaultOptions.setTopP(Float.parseFloat(String.valueOf(topP)));
            }
            if (thinkingEnabled != null) {
                defaultOptions.setThinkingEnabled(Boolean.parseBoolean(String.valueOf(thinkingEnabled)));
            }

        }
        return defaultOptions;
    }

    private Map<String, Object> getDefaultLlmOptions() {
        Map<String, Object> defaultLlmOptions = new HashMap<>();
        defaultLlmOptions.put("temperature", 0.7);
        defaultLlmOptions.put("topK", 4);
        defaultLlmOptions.put("maxReplyLength", 2048);
        defaultLlmOptions.put("topP", 0.7);
        defaultLlmOptions.put("maxMessageCount", 10);
        return defaultLlmOptions;
    }

    private Map<String, Object> errorRespnseMsg(int errorCode, String message) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("error", errorCode);
        result.put("message", message);
        return result;
    }

    @Override
    protected Result<?> onRemoveBefore(Collection<Serializable> ids) {
        QueryWrapper queryWrapperKnowledge = QueryWrapper.create().in(AiBotKnowledge::getBotId, ids);
        aiBotKnowledgeService.remove(queryWrapperKnowledge);
        QueryWrapper queryWrapperBotWorkflow = QueryWrapper.create().in(AiBotWorkflow::getBotId, ids);
        aiBotWorkflowService.remove(queryWrapperBotWorkflow);
        QueryWrapper queryWrapperBotPlugins = QueryWrapper.create().in(AiBotPlugins::getBotId, ids);
        aiBotPluginsService.remove(queryWrapperBotPlugins);
        return super.onRemoveBefore(ids);
    }

    private List<Tool> buildFunctionList(Map<String, Object> buildParams) {

        if (buildParams == null || buildParams.isEmpty()) {
            throw new IllegalArgumentException("buildParams is empty");
        }

        List<Tool> functionList = new ArrayList<>();

        BigInteger botId = (BigInteger) buildParams.get("botId");
        if (botId == null) {
            throw new IllegalArgumentException("botId is empty");
        }
        Boolean needEnglishName = (Boolean) buildParams.get("needEnglishName");
        if (needEnglishName == null) {
            needEnglishName = false;
        }

        QueryWrapper queryWrapper = QueryWrapper.create();

        // 工作流 function 集合
        queryWrapper.eq(AiBotWorkflow::getBotId, botId);
        List<AiBotWorkflow> aiBotWorkflows = aiBotWorkflowService.getMapper()
                .selectListWithRelationsByQuery(queryWrapper);
//        if (aiBotWorkflows != null && !aiBotWorkflows.isEmpty()) {
//            for (AiBotWorkflow aiBotWorkflow : aiBotWorkflows) {
//                Tool function = aiBotWorkflow.getWorkflow().toFunction(needEnglishName);
//                functionList.add(function);
//            }
//        }

        // 知识库 function 集合
        queryWrapper = QueryWrapper.create();
        queryWrapper.eq(AiBotKnowledge::getBotId, botId);
        List<AiBotKnowledge> aiBotKnowledges = aiBotKnowledgeService.getMapper()
                .selectListWithRelationsByQuery(queryWrapper);
        if (aiBotKnowledges != null && !aiBotKnowledges.isEmpty()) {
            for (AiBotKnowledge aiBotKnowledge : aiBotKnowledges) {
                Tool function = aiBotKnowledge.getKnowledge().toFunction(needEnglishName);
                functionList.add(function);
            }
        }

        // 插件 function 集合
        queryWrapper = QueryWrapper.create();
        queryWrapper.select("plugin_tool_id").eq(AiBotPlugins::getBotId, botId);
        List<BigInteger> pluginToolIds = aiBotPluginsService.getMapper()
                .selectListWithRelationsByQueryAs(queryWrapper, BigInteger.class);
        if (pluginToolIds != null && !pluginToolIds.isEmpty()) {
            QueryWrapper queryTool = QueryWrapper.create()
                    .select("*")
                    .from("tb_ai_plugin_tool")
                    .in("id", pluginToolIds);
            List<AiPluginTool> aiPluginTools = aiPluginToolService.getMapper().selectListWithRelationsByQuery(queryTool);
            if (aiPluginTools != null && !aiPluginTools.isEmpty()) {
                for (AiPluginTool aiPluginTool : aiPluginTools) {
                    functionList.add(aiPluginTool.toFunction());
                }
            }
        }


        return functionList;
    }
}

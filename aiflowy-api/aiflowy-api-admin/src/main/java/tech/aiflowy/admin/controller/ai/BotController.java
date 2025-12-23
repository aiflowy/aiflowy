
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
import tech.aiflowy.ai.mapper.BotConversationMapper;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.common.audio.core.AudioServiceManager;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.util.MapUtil;
import tech.aiflowy.common.util.Maps;
import tech.aiflowy.common.util.SSEUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.mapper.SysApiKeyMapper;

import javax.annotation.Resource;
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
public class BotController extends BaseCurdController<BotService, Bot> {

    private final ModelService modelService;
    private final BotWorkflowService botWorkflowService;
    private final BotDocumentCollectionService botDocumentCollectionService;
    private final BotMessageService botMessageService;
    @Resource
    private SysApiKeyMapper aiBotApiKeyMapper;
    @Resource
    private BotConversationService botConversationService;
    @Resource
    private BotConversationMapper botConversationMapper;
    @Resource
    private BotService botService;
    @Autowired
    @Qualifier("defaultCache") // 指定 Bean 名称
    private Cache<String, Object> cache;
    @Resource
    private AudioServiceManager audioServiceManager;

    private static final Logger logger = LoggerFactory.getLogger(BotController.class);

    public BotController(BotService service, ModelService modelService, BotWorkflowService botWorkflowService,
                         BotDocumentCollectionService botDocumentCollectionService, BotMessageService botMessageService) {
        super(service);
        this.modelService = modelService;
        this.botWorkflowService = botWorkflowService;
        this.botDocumentCollectionService = botDocumentCollectionService;
        this.botMessageService = botMessageService;
    }

    @Resource
    private BotPluginService botPluginService;
    @Resource
    private PluginItemService pluginItemService;

    @PostMapping("updateOptions")
    @SaCheckPermission("/api/v1/aiBot/save")
    public Result<Void> updateOptions(@JsonBody("id") BigInteger id,
                                      @JsonBody("options") Map<String, Object> options) {
        Bot aiBot = service.getById(id);
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
        Bot aiBot = service.getById(id);
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
     * 处理聊天请求的接口方法
     *
     * @param prompt    用户输入的聊天内容，必须提供
     * @param botId     聊天机器人的唯一标识符，必须提供
     * @param sessionId 会话ID，用于标识当前对话会话，必须提供
     * @param messages  历史消息，用于提供上下文，可选
     * @return 返回SseEmitter对象，用于服务器向客户端推送聊天响应数据
     */
    @PostMapping("chat")
    @SaIgnore
    public SseEmitter chat(
            @JsonBody(value = "prompt", required = true) String prompt,
            @JsonBody(value = "botId", required = true) BigInteger botId,
            @JsonBody(value = "sessionId", required = true) String sessionId,
            @JsonBody(value = "messages") List<Map<String, String>>  messages

    ) {

        if (!StringUtils.hasLength(prompt)) {
            throw new BusinessException("提示词不能为空！");
        }

        Bot aiBot = service.getById(botId);
        if (aiBot == null) {
            return SSEUtil.sseEmitterForContent( "机器人不存在");
        }

        boolean login = StpUtil.isLogin();
        if (!login && !aiBot.isAnonymousEnabled()) {
            return SSEUtil.sseEmitterForContent( "此bot不支持匿名访问");

        }

        Map<String, Object> llmOptions = aiBot.getLlmOptions();
        String systemPrompt = MapUtil.getString(llmOptions, "systemPrompt");

        Model model = modelService.getLlmInstance(aiBot.getLlmId());
        if (model == null) {
            return SSEUtil.sseEmitterForContent( "LLM不存在");
        }


        ChatModel chatModel = model.toChatModel();
        if (chatModel == null) {
            return SSEUtil.sseEmitterForContent( "LLM获取为空");
        }
        final MemoryPrompt memoryPrompt = new MemoryPrompt();
        Integer maxMessageCount = MapUtil.getInteger(llmOptions, "maxMessageCount");
        if (maxMessageCount != null) {
            memoryPrompt.setMaxAttachedMessageCount(maxMessageCount);
        }

        if (StringUtils.hasLength(systemPrompt)) {
            memoryPrompt.setSystemMessage(SystemMessage.of(systemPrompt));
        }

        if (StpUtil.isLogin()) {
            BotMessageMemory memory = new BotMessageMemory(botId, SaTokenUtil.getLoginAccount().getId(), sessionId,
                    botMessageService);
            memoryPrompt.setMemory(memory);
        }
        UserMessage userMessage = new UserMessage(prompt);
        userMessage.addTools(buildFunctionList(Maps.of("botId", botId).set("needEnglishName", false)));
        memoryPrompt.addMessage(userMessage);
        ChatOptions chatOptions = getChatOptions(llmOptions);
        return botService.startChat(botId, chatModel, prompt, memoryPrompt, chatOptions, sessionId, messages);

    }

    @PostMapping("updateLlmId")
    @SaCheckPermission("/api/v1/aiBot/save")
    public Result<Void> updateBotLlmId(@RequestBody
                                       Bot aiBot) {
        service.updateBotLlmId(aiBot);
        return Result.ok();
    }

    @GetMapping("getDetail")
    @SaIgnore
    public Result<Bot> getDetail(String id) {
        return Result.ok(botService.getDetail(id));
    }

    @Override
    @SaIgnore
    public Result<Bot> detail(String id) {
        Bot data = botService.getDetail(id);
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
        Model llm = modelService.getById(llmId);

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
    protected Result<?> onSaveOrUpdateBefore(Bot entity, boolean isSave) {

        String alias = entity.getAlias();

        if (StringUtils.hasLength(alias)) {
            Bot aiBot = service.getByAlias(alias);


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
        QueryWrapper queryWrapperKnowledge = QueryWrapper.create().in(BotDocumentCollection::getBotId, ids);
        botDocumentCollectionService.remove(queryWrapperKnowledge);
        QueryWrapper queryWrapperBotWorkflow = QueryWrapper.create().in(BotWorkflow::getBotId, ids);
        botWorkflowService.remove(queryWrapperBotWorkflow);
        QueryWrapper queryWrapperBotPlugins = QueryWrapper.create().in(BotPlugin::getBotId, ids);
        botPluginService.remove(queryWrapperBotPlugins);
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
        queryWrapper.eq(BotWorkflow::getBotId, botId);
        List<BotWorkflow> botWorkflows = botWorkflowService.getMapper()
                .selectListWithRelationsByQuery(queryWrapper);
        if (botWorkflows != null && !botWorkflows.isEmpty()) {
            for (BotWorkflow botWorkflow : botWorkflows) {
                Tool function = botWorkflow.getWorkflow().toFunction(needEnglishName);
                functionList.add(function);
            }
        }

        // 知识库 function 集合
        queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BotDocumentCollection::getBotId, botId);
        List<BotDocumentCollection> botDocumentCollections = botDocumentCollectionService.getMapper()
                .selectListWithRelationsByQuery(queryWrapper);
        if (botDocumentCollections != null && !botDocumentCollections.isEmpty()) {
            for (BotDocumentCollection botDocumentCollection : botDocumentCollections) {
                Tool function = botDocumentCollection.getKnowledge().toFunction(needEnglishName);
                functionList.add(function);
            }
        }

        // 插件 function 集合
        queryWrapper = QueryWrapper.create();
        queryWrapper.select("plugin_tool_id").eq(BotPlugin::getBotId, botId);
        List<BigInteger> pluginToolIds = botPluginService.getMapper()
                .selectListWithRelationsByQueryAs(queryWrapper, BigInteger.class);
        if (pluginToolIds != null && !pluginToolIds.isEmpty()) {
            QueryWrapper queryTool = QueryWrapper.create()
                    .select("*")
                    .from("tb_plugin_item")
                    .in("id", pluginToolIds);
            List<PluginItem> pluginItems = pluginItemService.getMapper().selectListWithRelationsByQuery(queryTool);
            if (pluginItems != null && !pluginItems.isEmpty()) {
                for (PluginItem pluginItem : pluginItems) {
                    functionList.add(pluginItem.toFunction());
                }
            }
        }


        return functionList;
    }
}

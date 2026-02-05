
package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.alicp.jetcache.Cache;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.agentsflex.listener.PromptChoreChatStreamListener;
import tech.aiflowy.ai.entity.*;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.ai.service.impl.BotServiceImpl;
import tech.aiflowy.common.audio.core.AudioServiceManager;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.core.chat.protocol.sse.ChatSseEmitter;
import tech.aiflowy.core.chat.protocol.sse.ChatSseUtil;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
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
@RequestMapping("/api/v1/bot")
public class BotController extends BaseCurdController<BotService, Bot> {

    private final ModelService modelService;
    private final BotWorkflowService botWorkflowService;
    private final BotDocumentCollectionService botDocumentCollectionService;
    private final BotMessageService botMessageService;
    @Resource
    private BotService botService;
    @Autowired
    @Qualifier("defaultCache") // 指定 Bean 名称
    private Cache<String, Object> cache;
    @Resource
    private AudioServiceManager audioServiceManager;

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

    @GetMapping("/generateConversationId")
    public Result<Long> generateConversationId() {
        long nextId = new SnowFlakeIDKeyGenerator().nextId();
        return Result.ok(nextId);
    }

    @PostMapping("updateOptions")
    @SaCheckPermission("/api/v1/bot/save")
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
    @SaCheckPermission("/api/v1/bot/save")
    public Result<Void> updateLlmOptions(@JsonBody("id")
                                         BigInteger id, @JsonBody("llmOptions")
                                         Map<String, Object> llmOptions) {
        Bot aiBot = service.getById(id);
        Map<String, Object> existLlmOptions = aiBot.getModelOptions();
        if (existLlmOptions == null) {
            existLlmOptions = new HashMap<>();
        }
        if (llmOptions != null) {
            existLlmOptions.putAll(llmOptions);
        }
        aiBot.setModelOptions(existLlmOptions);
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
     * @param conversationId 会话ID，用于标识当前对话会话，必须提供
     * @param messages  历史消息，用于提供上下文，可选
     * @return 返回SseEmitter对象，用于服务器向客户端推送聊天响应数据
     */
    @PostMapping("chat")
    @SaIgnore
    public SseEmitter chat(
            @JsonBody(value = "prompt", required = true) String prompt,
            @JsonBody(value = "botId", required = true) BigInteger botId,
            @JsonBody(value = "conversationId", required = true) BigInteger conversationId,
            @JsonBody(value = "messages") List<Map<String, String>>  messages

    ) {
        BotServiceImpl.ChatCheckResult chatCheckResult = new BotServiceImpl.ChatCheckResult();

        // 前置校验：失败则直接返回错误SseEmitter
        SseEmitter errorEmitter = botService.checkChatBeforeStart(botId, prompt, conversationId.toString(), chatCheckResult);
        if (errorEmitter != null) {
            return errorEmitter;
        }
        return botService.startChat(botId, prompt, conversationId, messages, chatCheckResult);
    }

    @PostMapping("updateLlmId")
    @SaCheckPermission("/api/v1/bot/save")
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

        Map<String, Object> llmOptions = data.getModelOptions();
        if (llmOptions == null) {
            llmOptions = new HashMap<>();
        }

        if (data.getModelId() == null) {
            return Result.ok(data);
        }

        BigInteger llmId = data.getModelId();
        Model llm = modelService.getById(llmId);

        if (llm == null) {
            data.setModelId(null);
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
            entity.setModelOptions(getDefaultLlmOptions());
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

    /**
     * 系统提示词优化
     *
     * @param prompt
     * @return
     */
    @PostMapping("prompt/chore/chat")
    @SaIgnore
    public SseEmitter chat(
            @JsonBody(value = "prompt", required = true) String prompt,
            @JsonBody(value = "botId", required = true) BigInteger botId
    ){
        if (!StringUtils.hasLength(prompt)) {
            throw new BusinessException("提示词不能为空！");
        }
        String promptChore = "# 角色与目标\n" +
                "\n" +
                "你是一位专业的提示词工程师（Prompt Engineer）。你的任务是，分析我提供的“用户原始提示词”，并将其优化成一个结构清晰、指令明确、效果最优的“系统提示词（System Prompt）”。\n" +
                "\n" +
                "这个优化后的系统提示词将直接用于引导一个AI助手，使其能够精准、高效地完成用户的请求。\n" +
                "\n" +
                "# 优化指南 (请严格遵循)\n" +
                "\n" +
                "在优化过程中，请遵循以下原则，以确保最终提示词的质量：\n" +
                "\n" +
                "1.  **角色定义 (Role Definition)**：\n" +
                "    *   为AI助手明确一个具体、专业的角色。这个角色应该与任务高度相关。\n" +
                "    *   例如：“你是一位资深的软件架构师”、“你是一位经验丰富的产品经理”。\n" +
                "\n" +
                "2.  **任务与目标 (Task & Goal)**：\n" +
                "    *   清晰、具体地描述AI需要完成的任务。\n" +
                "    *   明确指出期望的最终输出是什么，以及输出的目标和用途。\n" +
                "    *   避免模糊不清的指令。\n" +
                "\n" +
                "3.  **输入输出格式 (Input/Output Format)**：\n" +
                "    *   如果任务涉及到处理特定格式的数据，请明确说明输入数据的格式。\n" +
                "    *   **至关重要**：请为AI的输出指定一个清晰、结构化的格式。这能极大地提升结果的可用性。\n" +
                "    *   例如：“请以Markdown表格的形式输出”、“请分点列出，每点不超过20字”。\n" +
                "\n" +
                "4.  **背景与上下文 (Context & Background)**：\n" +
                "    *   提供完成任务所必需的背景信息。\n" +
                "    *   例如：项目的阶段、目标用户、使用的技术栈、相关的约束条件等。\n" +
                "\n" +
                "5.  **语气与风格 (Tone & Style)**：\n" +
                "    *   指定AI回答时应采用的语气和风格。\n" +
                "    *   例如：“专业且简洁”、“通俗易懂，避免使用专业术语”。\n" +
                "\n" +
                "6.  **约束与规则 (Constraints & Rules)**：\n" +
                "    *   设定AI在回答时必须遵守的规则和限制。\n" +
                "    *   例如：“回答必须基于提供的文档”、“禁止猜测用户未提及的信息”。\n" +
                "\n" +
                "7.  **思考过程 (Chain-of-Thought)**：\n" +
                "    *   对于复杂的推理任务，可以引导AI展示其思考过程。\n" +
                "    *   例如：“请先分析问题的关键点，然后给出解决方案，并解释你的推理步骤。”\n" +
                "\n" +
                "# 输出要求\n" +
                "\n" +
                "请你直接输出优化后的完整系统提示词。不要包含任何额外的解释或说明。\n" +
                "\n" +
                "---\n" +
                "\n" +
                "## 用户原始提示词\n" +
                "[" + prompt + "]\n";
        Bot aiBot = service.getById(botId);
        if (aiBot == null) {
            return ChatSseUtil.sendSystemError(null, "聊天助手不存在");
        }
        ChatSseEmitter sseEmitter = new ChatSseEmitter();
        Model model = modelService.getModelInstance(aiBot.getModelId());
        if (model == null) {
            return ChatSseUtil.sendSystemError(null, "模型不存在");
        }
        ChatModel chatModel = model.toChatModel();
        if (chatModel == null) {
            return ChatSseUtil.sendSystemError(null, "模型不存在");
        }
        PromptChoreChatStreamListener promptChoreChatStreamListener = new PromptChoreChatStreamListener(sseEmitter);
        chatModel.chatStream(promptChore, promptChoreChatStreamListener);
        return sseEmitter.getEmitter();
    }
}

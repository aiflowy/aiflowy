
package tech.aiflowy.usercenter.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.alicp.jetcache.Cache;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.*;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.ai.service.impl.BotServiceImpl;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.audio.core.AudioServiceManager;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;

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
@RequestMapping("/userCenter/bot")
@UsePermission(moduleName = "/api/v1/bot")
public class UcBotController extends BaseCurdController<BotService, Bot> {

    private final ModelService modelService;
    private final BotWorkflowService botWorkflowService;
    private final BotDocumentCollectionService botDocumentCollectionService;
    @Resource
    private BotService botService;
    @Autowired
    @Qualifier("defaultCache") // 指定 Bean 名称
    private Cache<String, Object> cache;
    @Resource
    private AudioServiceManager audioServiceManager;

    public UcBotController(BotService service, ModelService modelService, BotWorkflowService botWorkflowService,
                           BotDocumentCollectionService botDocumentCollectionService) {
        super(service);
        this.modelService = modelService;
        this.botWorkflowService = botWorkflowService;
        this.botDocumentCollectionService = botDocumentCollectionService;
    }

    @Resource
    private BotPluginService botPluginService;
    @Resource
    private BotConversationService conversationMessageService;

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
            @JsonBody(value = "messages") List<Map<String, String>> messages

    ) {
        BotServiceImpl.ChatCheckResult chatCheckResult = new BotServiceImpl.ChatCheckResult();

        // 前置校验：失败则直接返回错误SseEmitter
        SseEmitter errorEmitter = botService.checkChatBeforeStart(botId, prompt, conversationId.toString(), chatCheckResult);
        if (errorEmitter != null) {
            return errorEmitter;
        }
        BotConversation conversation = conversationMessageService.getById(conversationId);
        if (conversation == null) {
            conversation = new BotConversation();
            conversation.setId(conversationId);
            if (prompt.length() > 200) {
                conversation.setTitle(prompt.substring(0, 200));
            } else {
                conversation.setTitle(prompt);
            }
            conversation.setBotId(botId);
            conversation.setAccountId(SaTokenUtil.getLoginAccount().getId());
            commonFiled(conversation, SaTokenUtil.getLoginAccount().getId(), SaTokenUtil.getLoginAccount().getTenantId(), SaTokenUtil.getLoginAccount().getDeptId());
            conversationMessageService.save(conversation);
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

}

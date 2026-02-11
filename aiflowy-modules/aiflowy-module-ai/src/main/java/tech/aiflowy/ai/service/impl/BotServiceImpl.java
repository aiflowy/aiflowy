package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.file2text.File2TextService;
import com.agentsflex.core.file2text.source.HttpDocumentSource;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.Message;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.message.UserMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.model.chat.tool.Tool;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.agentsflex.listener.ChatStreamListener;
import tech.aiflowy.ai.agentsflex.memory.BotMessageMemory;
import tech.aiflowy.ai.agentsflex.memory.DefaultBotMessageMemory;
import tech.aiflowy.ai.agentsflex.memory.PublicBotMessageMemory;
import tech.aiflowy.ai.entity.*;
import tech.aiflowy.ai.mapper.BotMapper;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.ai.utils.CustomBeanUtils;
import tech.aiflowy.ai.utils.RegexUtils;
import tech.aiflowy.common.filestorage.FileStorageService;
import tech.aiflowy.common.filestorage.utils.PathGeneratorUtil;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.util.MapUtil;
import tech.aiflowy.common.util.Maps;
import tech.aiflowy.common.util.UrlEncoderUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.core.chat.protocol.sse.ChatSseEmitter;
import tech.aiflowy.core.chat.protocol.sse.ChatSseUtil;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static tech.aiflowy.ai.entity.table.BotPluginTableDef.BOT_PLUGIN;
import static tech.aiflowy.ai.entity.table.PluginItemTableDef.PLUGIN_ITEM;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class BotServiceImpl extends ServiceImpl<BotMapper, Bot> implements BotService {

    private static final Logger log = LoggerFactory.getLogger(BotServiceImpl.class);

    public static class ChatCheckResult {
        private Bot aiBot;
        private Map<String, Object> modelOptions;
        private ChatModel chatModel;
        private String conversationIdStr;

        public Bot getAiBot() {return aiBot;}

        public void setAiBot(Bot aiBot) {this.aiBot = aiBot;}

        public Map<String, Object> getModelOptions() {return modelOptions;}

        public void setModelOptions(Map<String, Object> modelOptions) {this.modelOptions = modelOptions;}

        public ChatModel getChatModel() {return chatModel;}

        public void setChatModel(ChatModel chatModel) {this.chatModel = chatModel;}

        public String getConversationIdStr() {return conversationIdStr;}

        public void setConversationIdStr(String conversationIdStr) {this.conversationIdStr = conversationIdStr;}
    }

    @Resource
    private BotMessageService botMessageService;

    @Resource(name = "sseThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private ModelService modelService;
    @Resource
    private BotWorkflowService botWorkflowService;
    @Resource
    private BotDocumentCollectionService botDocumentCollectionService;
    @Resource
    private BotPluginService botPluginService;
    @Resource
    private PluginItemService pluginItemService;
    @Resource
    private BotMcpService botMcpService;
    @Resource
    private McpService mcpService;
    @Resource(name = "default")
    FileStorageService storageService;

    @Override
    public Bot getDetail(String id) {
        Bot aiBot = null;

        if (id.matches(RegexUtils.ALL_NUMBER)) {
            aiBot = getById(id);

            if (aiBot == null) {
                aiBot = getByAlias(id);
            }

        } else {
            aiBot = getByAlias(id);
        }

        return aiBot;
    }

    @Override
    public Bot getByAlias(String alias) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(Bot::getAlias, alias);
        return getOne(queryWrapper);
    }

    public SseEmitter checkChatBeforeStart(BigInteger botId, String prompt, String conversationId, ChatCheckResult chatCheckResult) {
        if (!StringUtils.hasLength(prompt)) {
            return ChatSseUtil.sendSystemError(conversationId, "提示词不能为空");
        }
        if (!StringUtils.hasLength(conversationId)) {
            return ChatSseUtil.sendSystemError(conversationId, "conversationId不能为空");
        }
        Bot aiBot = this.getById(botId);
        if (aiBot == null) {
            return ChatSseUtil.sendSystemError(conversationId, "聊天助手不存在");
        }
        if (aiBot.getModelId() == null) {
            return ChatSseUtil.sendSystemError(conversationId, "请配置大模型!");
        }
        boolean login = StpUtil.isLogin();
        if (!login && !aiBot.isAnonymousEnabled()) {
            return ChatSseUtil.sendSystemError(conversationId, "此聊天助手不支持匿名访问");
        }
        Map<String, Object> modelOptions = aiBot.getModelOptions();
        Model model = modelService.getModelInstance(aiBot.getModelId());
        if (model == null) {
            return ChatSseUtil.sendSystemError(conversationId, "模型不存在，请检查配置");
        }
        ChatModel chatModel = model.toChatModel();
        if (chatModel == null) {
            return ChatSseUtil.sendSystemError(conversationId, "对话模型获取失败，请检查配置");
        }

        chatCheckResult.setAiBot(aiBot);
        chatCheckResult.setModelOptions(modelOptions);
        chatCheckResult.setChatModel(chatModel);
        chatCheckResult.setConversationIdStr(conversationId);
        return null;
    }

    @Override
    public SseEmitter startChat(BigInteger botId, String prompt, BigInteger conversationId, List<Map<String, String>> messages,
                                BotServiceImpl.ChatCheckResult chatCheckResult, List<String> attachments) {
        Map<String, Object> modelOptions = chatCheckResult.getModelOptions();
        ChatModel chatModel = chatCheckResult.getChatModel();
        final MemoryPrompt memoryPrompt = new MemoryPrompt();
        String systemPrompt = MapUtil.getString(modelOptions, Bot.KEY_SYSTEM_PROMPT);
        Integer maxMessageCount = MapUtil.getInteger(modelOptions, Bot.KEY_MAX_MESSAGE_COUNT);
        if (maxMessageCount != null) {
            memoryPrompt.setMaxAttachedMessageCount(maxMessageCount);
        }
        if (StringUtils.hasLength(systemPrompt)) {
            memoryPrompt.setSystemMessage(SystemMessage.of(systemPrompt));
        }
        String attachmentsToString = attachmentsToString(attachments);
        if (StringUtils.hasLength(attachmentsToString)) {
            prompt = "【用户问题】：\n" + prompt + "\n\n请基于用户上传的附件内容回答用户问题： \n" +  "【用户上传的附件内容】：\n" + attachmentsToString ;
        }
        UserMessage userMessage = new UserMessage(prompt);
        userMessage.addTools(buildFunctionList(Maps.of("botId", botId).set("needEnglishName", false)));
        ChatOptions chatOptions = getChatOptions(modelOptions);
        Boolean enableDeepThinking = MapUtil.getBoolean(modelOptions, Bot.KEY_ENABLE_DEEP_THINKING, false);
        chatOptions.setThinkingEnabled(enableDeepThinking);
        ChatSseEmitter chatSseEmitter = new ChatSseEmitter();
        SseEmitter emitter = chatSseEmitter.getEmitter();
        if (messages != null && !messages.isEmpty()) {
            ChatMemory defaultChatMemory = new DefaultBotMessageMemory(conversationId, chatSseEmitter, messages);
            memoryPrompt.setMemory(defaultChatMemory);
        } else {
            BotMessageMemory memory = new BotMessageMemory(botId, SaTokenUtil.getLoginAccount().getId(), conversationId, botMessageService);
            memoryPrompt.setMemory(memory);
        }
        memoryPrompt.addMessage(userMessage);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        threadPoolTaskExecutor.execute(() -> {
            ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
            RequestContextHolder.setRequestAttributes(sra, true);
            StreamResponseListener streamResponseListener = new ChatStreamListener(conversationId.toString(), chatModel, memoryPrompt, chatSseEmitter, chatOptions);
            chatModel.chatStream(memoryPrompt, streamResponseListener, chatOptions);
        });

        return emitter;
    }

    /**
     * 第三方使用Apikey访问聊天
     * @param botId
     * @return
     */
    @Override
    public SseEmitter startPublicChat(BigInteger botId, String prompt,  List<Message> messages, BotServiceImpl.ChatCheckResult chatCheckResult) {
        Map<String, Object> modelOptions = chatCheckResult.getModelOptions();
        ChatOptions chatOptions = getChatOptions(modelOptions);
        ChatModel chatModel = chatCheckResult.getChatModel();
        UserMessage userMessage = new UserMessage(prompt);
        userMessage.addTools(buildFunctionList(Maps.of("botId", botId)
                .set("needEnglishName", false)
                .set("needAccountId", false)
        ));
        ChatSseEmitter chatSseEmitter = new ChatSseEmitter();
        SseEmitter emitter = chatSseEmitter.getEmitter();
        ChatMemory defaultChatMemory = new PublicBotMessageMemory(chatSseEmitter, messages);
        final MemoryPrompt memoryPrompt = new MemoryPrompt();
        memoryPrompt.setMemory(defaultChatMemory);
        memoryPrompt.addMessage(userMessage);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
        threadPoolTaskExecutor.execute(() -> {
            RequestContextHolder.setRequestAttributes(sra, true);
            StreamResponseListener streamResponseListener = new ChatStreamListener(chatCheckResult.getConversationIdStr(), chatModel, memoryPrompt, chatSseEmitter, chatOptions);
            chatModel.chatStream(memoryPrompt, streamResponseListener, chatOptions);
        });

        return emitter;
    }

    @Override
    public void updateBotLlmId(Bot aiBot) {
        Bot bot = getById(aiBot.getId());

        if (bot == null) {
            log.error("修改bot的llmId失败，bot不存在！");
            throw new BusinessException("bot不存在！");
        }

        bot.setModelId(aiBot.getModelId());

        updateById(bot, false);

    }


    @Override
    public boolean updateById(Bot entity) {
        Bot aiBot = getById(entity.getId());
        if (aiBot == null) {
            throw new BusinessException("bot 不存在");
        }

        CustomBeanUtils.copyPropertiesIgnoreNull(entity, aiBot);

        if ("".equals(aiBot.getAlias())) {
            aiBot.setAlias(null);
        }


        return super.updateById(aiBot, false);
    }

    public static ChatOptions getChatOptions(Map<String, Object> llmOptions) {
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
        queryWrapper.select(BOT_PLUGIN.PLUGIN_ITEM_ID).eq(BotPlugin::getBotId, botId);
        List<BigInteger> pluginToolIds = botPluginService.getMapper()
                .selectListWithRelationsByQueryAs(queryWrapper, BigInteger.class);
        if (pluginToolIds != null && !pluginToolIds.isEmpty()) {
            QueryWrapper queryTool = QueryWrapper.create()
                    .select(PLUGIN_ITEM.ALL_COLUMNS)
                    .from(PLUGIN_ITEM)
                    .where(PLUGIN_ITEM.ID.in(pluginToolIds));
            List<PluginItem> pluginItems = pluginItemService.getMapper().selectListWithRelationsByQuery(queryTool);
            if (pluginItems != null && !pluginItems.isEmpty()) {
                for (PluginItem pluginItem : pluginItems) {
                    functionList.add(pluginItem.toFunction());
                }
            }
        }

        // MCP function 集合
        queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BotMcp::getBotId, botId);
        List<BotMcp> botMcpList = botMcpService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        botMcpList.forEach(botMcp -> {
            Tool tool = mcpService.toFunction(botMcp);
            functionList.add(tool);
        });

        return functionList;
    }

    public String attachmentsToString(List<String> fileList) {
        StringBuilder messageBuilder = new StringBuilder();
        if (fileList != null && !fileList.isEmpty()) {
            File2TextService fileTextService = new File2TextService();
            for (int i = 0; i < fileList.size(); i++) {
                String fileUrl = fileList.get(i);
                String encodedUrl = UrlEncoderUtil.getEncodedUrl(fileUrl);
                String result = fileTextService.extractTextFromSource(new HttpDocumentSource(encodedUrl));
                if (result != null) {
                    if (i > 0) {
                        messageBuilder.append("\n\n");
                    }
                    messageBuilder.append("附件").append(i + 1).append("，文件名为：").append(PathGeneratorUtil.getPureFileName(fileUrl)).append("，内容为：  \n").append(result);
                }
                storageService.delete(fileUrl);
            }
        }
        return messageBuilder.toString();
    }

}

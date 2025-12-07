package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.ToolCall;
import com.agentsflex.core.message.ToolMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.model.chat.response.AiMessageResponse;
import com.agentsflex.core.model.chat.tool.GlobalToolInterceptors;
import com.agentsflex.core.model.client.StreamContext;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.agentsflex.core.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.AiBot;
import tech.aiflowy.ai.mapper.AiBotMapper;
import tech.aiflowy.ai.service.AiBotService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.utils.CustomBeanUtils;
import tech.aiflowy.common.ai.ChatSseEmitter;
import tech.aiflowy.common.ai.inteceptor.ToolLoggingInterceptor;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.ai.utils.RegexUtils;
import com.mybatisflex.core.query.QueryWrapper;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiBotServiceImpl extends ServiceImpl<AiBotMapper, AiBot> implements AiBotService {

    private static final Logger log = LoggerFactory.getLogger(AiBotServiceImpl.class);

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 30秒发一次心跳
    private static final long HEARTBEAT_INTERVAL = 30 * 1000L;
    // 心跳消息
    private static final String HEARTBEAT_MESSAGE = "ping";
    @Override
    public AiBot getDetail(String id) {
        AiBot aiBot = null;

        if (id.matches(RegexUtils.ALL_NUMBER)){
            aiBot = getById(id);

            if (aiBot == null) {
               aiBot = getByAlias(id);
            }

        }else{
            aiBot = getByAlias(id);
        }

        return aiBot;
    }

    @Override
    public AiBot getByAlias(String alias){
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("alias",alias);
        return getOne(queryWrapper);
    }

    @Override
    public SseEmitter startChat(BigInteger botId, ChatModel chatModel, String prompt, MemoryPrompt memoryPrompt, ChatOptions chatOptions, String sessionId) {

        GlobalToolInterceptors.addInterceptor(new ToolLoggingInterceptor());

        SseEmitter emitter = ChatSseEmitter.create();
        System.out.println("emitters大小" + emitters.size()) ;
        String currentUserId = StpUtil.getLoginIdAsString();
        emitters.put(currentUserId, emitter);
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final boolean[] hasFinished = {true};
        chatModel.chatStream(memoryPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(StreamContext streamContext, AiMessageResponse aiMessageResponse) {
                try {
                    RequestContextHolder.setRequestAttributes(sra, true);
                    if (currentUserId != null) {
                        SseEmitter emitter = emitters.get(currentUserId);
                        if (emitter != null) {
                            String fullText = aiMessageResponse.getMessage().getFullContent();
                            String delta = aiMessageResponse.getMessage().getContent();
                            if (StringUtil.hasText(delta)) {
                                emitter.send(delta);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onStart(StreamContext context) {
                StreamResponseListener.super.onStart(context);
            }

            @Override
            public void onStop(StreamContext context) {
                RequestContextHolder.setRequestAttributes(sra, true);
                AiMessage aiMessage = context.getAiMessage();
                String finishReason = aiMessage.getFinishReason();
                if (!StringUtil.hasText(finishReason)){
                    return;
                }
                // 检查是否有工具调用请求
                if (aiMessage.getFinished() && "stop".equals(aiMessage.getFinishReason()) && CollectionUtil.hasItems(aiMessage.getToolCalls())) {
                    hasFinished[0] = false;
                    AiMessageResponse aiMessageResponse = new AiMessageResponse(context.getChatContext(), prompt, aiMessage);
                    List<ToolMessage> toolMessages = aiMessageResponse.executeToolCallsAndGetToolMessages();
                    String newPrompt = "请根据以下内容回答用户，内容是:\n" + toolMessages + "\n 用户的问题是：" + prompt;

                    chatFunctionCallStream(newPrompt, chatModel, emitter, chatOptions);
                }
//                else {
//                        if (hasFinished[0]) {
//                            return;
//                        }
//                        SseEmitter emitter = emitters.get(currentUserId);
//                        if (emitter != null && "stop".equals(aiMessage.getFinishReason()) && !CollectionUtil.hasItems(aiMessage.getToolCalls()) ) {
//                            emitter.complete();
//                            emitters.remove(currentUserId);
//                        }
//
//                }

            }

            @Override
            public void onFailure(StreamContext context, Throwable throwable) {
                StreamResponseListener.super.onFailure(context, throwable);
            }
        });
        return emitter;
    }

    public void chatFunctionCallStream (String newPrompt, ChatModel chatModel, SseEmitter emitter, ChatOptions chatOptions){
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        chatModel.chatStream(newPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(StreamContext streamContext, AiMessageResponse aiMessageResponse) {
                try {
                    RequestContextHolder.setRequestAttributes(sra, true);
                    if (emitter != null) {
                        String fullText = aiMessageResponse.getMessage().getFullContent();
                        String delta = aiMessageResponse.getMessage().getContent();
                        if (StringUtil.hasText(delta)) {
                            emitter.send(delta);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }, chatOptions);

    }

    // 定时任务：发送心跳 + 清理无效连接
//    @Scheduled(fixedRate = HEARTBEAT_INTERVAL)
//    public void heartbeatTask() {
//        if (emitters.isEmpty()) {
//            log.debug("无活跃SSE连接，跳过心跳检测");
//            return;
//        }
//
//        log.debug("开始心跳检测，当前活跃连接数：{}", emitters.size());
//        // 遍历所有emitter，发送心跳
//        Iterator<Map.Entry<String, SseEmitter>> iterator = emitters.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, SseEmitter> entry = iterator.next();
//            String userId = entry.getKey();
//            SseEmitter emitter = entry.getValue();
//
//            try {
//                // 发送心跳消息（SSE的comment类型，前端不会触发message事件，仅用于保活）
//                emitter.send(SseEmitter.event().comment(HEARTBEAT_MESSAGE));
//                log.debug("向用户[{}]发送心跳成功", userId);
//            } catch (Exception e) {
//                // 其他异常（如emitter已超时）
//                log.error("处理用户[{}]心跳时异常", userId, e);
//                iterator.remove();
//            }
//        }
//        log.debug("心跳检测完成，剩余活跃连接数：{}", emitters.size());
//    }


    @Override
    public void updateBotLlmId(AiBot aiBot) {
        AiBot byId = getById(aiBot.getId());

        if (byId == null) {
            log.error("修改bot的llmId失败，bot不存在！");
            throw new BusinessException("bot不存在！");
        }

        byId.setLlmId(aiBot.getLlmId());

        updateById(byId,false);

    }


    @Override
    public boolean updateById(AiBot entity) {
        AiBot aiBot = getById(entity.getId());
        if (aiBot == null) {
            throw new BusinessException("bot 不存在");
        }

        CustomBeanUtils.copyPropertiesIgnoreNull(entity,aiBot);

        if ("".equals(aiBot.getAlias())){
            aiBot.setAlias(null);
        }


        return super.updateById(aiBot,false);
    }
}

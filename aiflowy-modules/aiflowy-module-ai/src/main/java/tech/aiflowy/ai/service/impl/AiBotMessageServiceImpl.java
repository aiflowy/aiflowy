package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.util.Maps;
import com.alicp.jetcache.Cache;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.entity.AiBotMessage;
import tech.aiflowy.ai.mapper.AiBotMessageMapper;
import tech.aiflowy.ai.service.AiBotMessageService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bot 消息记录表 服务层实现。
 *
 * @author michael
 * @since 2024-11-04
 */
@Service
public class AiBotMessageServiceImpl extends ServiceImpl<AiBotMessageMapper, AiBotMessage> implements AiBotMessageService {

    @Resource
    private AiBotMessageMapper aiBotMessageMapper;

    @Autowired
    @Qualifier("defaultCache") // 指定 Bean 名称
    private Cache<String, Object> cache;

    /**
     * 根据 botId 和 sessionId 查询当前对应的消息记录
     * @param botId
     * @param sessionId
     * @return
     */
    @Override
    public Result messageList(String botId, String sessionId, int isExternalMsg, String tempUserId, String tempUserSessionId) {
        boolean login = StpUtil.isLogin();
        if (login) {
            QueryWrapper queryConversation = QueryWrapper.create()
                    .select("id","bot_id","account_id","session_id","content","role","created","options")
                    .from("tb_ai_bot_message")
                    .where("bot_id = ? ", botId)
                    .where("session_id = ? ", sessionId)
                    .where("is_external_msg = ? ", isExternalMsg)
                    .where("account_id = ? ", SaTokenUtil.getLoginAccount().getId());
            List<AiBotMessage> messages = aiBotMessageMapper.selectListByQueryAs(queryConversation, AiBotMessage.class);
            List<Maps> finalMessages = new ArrayList<>();
            for (AiBotMessage message : messages){
                Map<String, Object> options = message.getOptions();
                if (options != null && (Integer) options.get("type") == 2) {
                    continue;
                }

                if (options != null && (Integer) options.get("type") == 1){
                    message.setContent((String) options.get("user_input"));
                }

                Maps maps = Maps.of("id", message.getId())
                        .set("content", message.getContent())
                        .set("role", message.getRole())
                        .set("options", message.getOptions())
                        .set("created", message.getCreated().getTime())
                        .set("updateAt", message.getCreated().getTime());

                if (options != null && options.get("fileList") != null){
                    maps.set("files", options.get("fileList"));
                }

                finalMessages.add(maps);
            }



            return Result.success(finalMessages);
        } else {
            AtomicReference<List<Maps>> messages = new AtomicReference<>(new ArrayList<>());
            List<AiBotConversationMessage> result = (List<AiBotConversationMessage>)cache.get(tempUserId + ":" + botId);
            if (result == null || result.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            result.forEach(conversationMessage -> {
                if (conversationMessage.getSessionId().equals(sessionId)) {
                    List<AiBotMessage> aiBotMessageList = conversationMessage.getAiBotMessageList();
                    List<Maps> finalMessageList = new ArrayList<>();
                    for (AiBotMessage aiBotMessage : aiBotMessageList) {
                        Map<String, Object> options = aiBotMessage.getOptions();
                        if (options != null && (Integer) options.get("type") == 2) {
                            continue;
                        }

                        if (options != null && (Integer) options.get("type") == 1){
                            aiBotMessage.setContent((String) options.get("user_input"));
                        }

                        Maps maps = Maps.of("id", aiBotMessage.getId())
                                .set("content", aiBotMessage.getContent())
                                .set("role", aiBotMessage.getRole())
                                .set("options", aiBotMessage.getOptions())
                                .set("created", aiBotMessage.getCreated().getTime())
                                .set("updateAt", aiBotMessage.getCreated().getTime());

                        if (options != null && options.get("fileList") != null){
                            maps.set("files", options.get("fileList"));
                        }

                        finalMessageList.add(maps);
                    }
                    messages.set(finalMessageList);
                }
            });
            return Result.success(messages);
        }
    }

    @Override
    public Result removeMsg(String botId, String sessionId, int isExternalMsg) {
        QueryWrapper queryWrapper =  QueryWrapper.create()
                 .select("*")
                 .from("tb_ai_bot_message")
                 .where("bot_id = ? ", botId)
                 .where("session_id = ? ", sessionId)
                 .where("is_external_msg = ? ", isExternalMsg);

        aiBotMessageMapper.deleteByQuery(queryWrapper);
        return Result.success();
    }


}

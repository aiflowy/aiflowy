package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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
                    .select("id","bot_id","account_id","session_id","content","role","created")
                    .from("tb_ai_bot_message")
                    .where("bot_id = ? ", botId)
                    .where("session_id = ? ", sessionId)
                    .where("is_external_msg = ? ", isExternalMsg)
                    .where("account_id = ? ", SaTokenUtil.getLoginAccount().getId());
            List<AiBotMessage> messages = aiBotMessageMapper.selectListByQueryAs(queryConversation, AiBotMessage.class);
            return Result.success(messages);
        } else {
            AtomicReference<List<AiBotMessage>> messages = new AtomicReference<>(new ArrayList<>());
            List<AiBotConversationMessage> result = (List<AiBotConversationMessage>)cache.get(tempUserId + ":" + botId);
            if (result == null || result.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            result.forEach(conversationMessage -> {
                if (conversationMessage.getSessionId().equals(sessionId)) {
                    messages.set(conversationMessage.getAiBotMessageList());
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

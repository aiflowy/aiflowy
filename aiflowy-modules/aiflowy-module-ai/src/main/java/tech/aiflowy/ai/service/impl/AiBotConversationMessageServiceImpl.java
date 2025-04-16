package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.entity.AiBotMessage;
import tech.aiflowy.ai.mapper.AiBotConversationMessageMapper;
import tech.aiflowy.ai.mapper.AiBotMessageMapper;
import tech.aiflowy.ai.service.AiBotConversationMessageService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-04-15
 */
@Service
public class AiBotConversationMessageServiceImpl extends ServiceImpl<AiBotConversationMessageMapper, AiBotConversationMessage>  implements AiBotConversationMessageService{

    @Resource
    private AiBotConversationMessageMapper aiBotConversationMessageMapper;

    @Resource
    private AiBotMessageMapper aiBotMessageMapper;
    /**
     * 删除指定会话
     * @param botId
     * @param sessionId
     * @return
     */
    @Override
    public Result deleteConversation(String botId, String sessionId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("bot_id = ?", botId)
                .where("session_id = ?", sessionId)
                .where("account_id = ?", SaTokenUtil.getLoginAccount().getId());
        int res = aiBotConversationMessageMapper.deleteByQuery(queryWrapper);
        if (res <= 0){
            return Result.fail();
        }
        // 删除消息记录中的数据
        QueryWrapper msgQuery = QueryWrapper.create()
                .where("bot_id = ?", botId)
                .where("session_id = ?", sessionId)
                .where("account_id = ?", SaTokenUtil.getLoginAccount().getId());
        int r = aiBotMessageMapper.deleteByQuery(msgQuery);
        if (r <= 0){
            return Result.fail();
        }
        return Result.success();
    }

    @Override
    public Result updateConversation(String botId, String sessionId, String title) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("bot_id = ? ", botId)
                .where("session_id = ? ", sessionId)
                .where("account_id = ?", SaTokenUtil.getLoginAccount().getId());
        AiBotConversationMessage conversationMessage = new AiBotConversationMessage();
        conversationMessage.setTitle(title);
        int res = aiBotConversationMessageMapper.updateByQuery(conversationMessage, queryWrapper);
        if (res <= 0){
            return Result.fail();
        }
        return Result.success();
    }

    @Override
    public Result externalList(BigInteger botId) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        BigInteger accountId = loginUser.getId();
        QueryWrapper query = QueryWrapper.create()
                .select("session_id") // 选择字段
                .from("tb_ai_bot_message")
                .where("bot_id = ?", botId)
                .where("is_external_msg = ?", 1)
                .where("account_id = ? ", accountId);
        AiBotMessage aiBotMessage = aiBotMessageMapper.selectOneByQuery(query);
        if (aiBotMessage == null){
            return Result.fail();
        }
        QueryWrapper queryConversation = QueryWrapper.create()
                .select("session_id","title", "bot_id") // 选择字段
                .from("tb_ai_bot_conversation_message")
                .where("bot_id = ?", botId)
                .where("account_id = ? ", accountId)
                .orderBy("created", false);
        List<AiBotConversationMessage> cons = aiBotMessageMapper.selectListByQueryAs(queryConversation, AiBotConversationMessage.class);
        Map<String, Object> result = new HashMap<>();
        result.put("cons", cons);
        return Result.success(result);
    }
}

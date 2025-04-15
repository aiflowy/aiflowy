package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.entity.AiBotMessage;
import tech.aiflowy.ai.mapper.AiBotMessageMapper;
import tech.aiflowy.ai.service.AiBotMessageService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
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
 * Bot 消息记录表 服务层实现。
 *
 * @author michael
 * @since 2024-11-04
 */
@Service
public class AiBotMessageServiceImpl extends ServiceImpl<AiBotMessageMapper, AiBotMessage> implements AiBotMessageService {

    @Resource
    private AiBotMessageMapper aiBotMessageMapper;

    @Override
    public Result externalList(BigInteger botId) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        BigInteger accountId = loginUser.getId();
        QueryWrapper query = QueryWrapper.create()
                .select("session_id") // 选择字段
                .from("tb_ai_bot_message")
                .where("bot_id = ?", botId)
                .where("account_id = ? ", accountId);
        AiBotMessage aiBotMessage = aiBotMessageMapper.selectOneByQuery(query);
        if (aiBotMessage == null){
            return Result.fail();
        }
        QueryWrapper queryConversation = QueryWrapper.create()
                .select("session_id","title", "bot_id") // 选择字段
                .from("tb_ai_bot_conversation_message")
                .where("bot_id = ?", botId)
                .where("account_id = ? ", accountId);
        List<AiBotConversationMessage> cons = aiBotMessageMapper.selectListByQueryAs(queryConversation, AiBotConversationMessage.class);
        Map<String, Object> result = new HashMap<>();
        result.put("cons", cons);
        return Result.success(result);
    }
}

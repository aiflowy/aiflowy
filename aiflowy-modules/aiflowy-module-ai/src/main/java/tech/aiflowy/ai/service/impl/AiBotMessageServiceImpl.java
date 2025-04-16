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



    /**
     * 根据 botId 和 sessionId 查询当前对应的消息记录
     * @param botId
     * @param sessionId
     * @return
     */
    @Override
    public Result messageList(String botId, String sessionId, int isExternalMsg) {
        QueryWrapper queryConversation = QueryWrapper.create()
                .select("id","bot_id","account_id","session_id","content","role","created")
                .from("tb_ai_bot_message")
                .where("bot_id = ? ", botId)
                .where("session_id = ? ", sessionId)
                .where("is_external_msg = ? ", isExternalMsg)
                .where("account_id = ? ", SaTokenUtil.getLoginAccount().getId());
        List<AiBotMessage> messages = aiBotMessageMapper.selectListByQueryAs(queryConversation, AiBotMessage.class);
        return Result.success(messages);
    }


}

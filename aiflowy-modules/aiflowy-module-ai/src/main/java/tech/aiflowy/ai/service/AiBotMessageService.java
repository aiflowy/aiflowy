package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.AiBotMessage;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.common.domain.Result;

import java.math.BigInteger;

/**
 * Bot 消息记录表 服务层。
 *
 * @author michael
 * @since 2024-11-04
 */
public interface AiBotMessageService extends IService<AiBotMessage> {


    Result messageList(String botId, String sessionId, int isExternalMsg);

}

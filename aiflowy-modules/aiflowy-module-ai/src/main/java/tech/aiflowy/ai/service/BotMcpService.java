package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.BotMcp;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author wangGangQiang
 * @since 2026-01-05
 */
public interface BotMcpService extends IService<BotMcp> {

    void updateBotMcpToolIds(BigInteger botId, List<Map<String, List<List<String>>>> mcpSelectedData);
}

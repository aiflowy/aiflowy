package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import tech.aiflowy.ai.entity.BotMcp;
import tech.aiflowy.ai.mapper.BotMcpMapper;
import tech.aiflowy.ai.service.BotMcpService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *  服务层实现。
 *
 * @author wangGangQiang
 * @since 2026-01-05
 */
@Service
public class BotMcpServiceImpl extends ServiceImpl<BotMcpMapper, BotMcp>  implements BotMcpService{

    @Override
    @Transactional
    public void updateBotMcpToolIds(BigInteger botId, List<Map<String, List<List<String>>>> mcpSelectedData) {
        // 删除原来绑定的mcp
        this.remove(QueryWrapper.create().eq(BotMcp::getBotId, botId));
        for (Map<String, List<List<String>>> mcpItem : mcpSelectedData) {
            for (Map.Entry<String, List<List<String>>> entry : mcpItem.entrySet()) {
                String mcpId = entry.getKey(); // 上一级id
                List<List<String>> toolList = entry.getValue(); // 包含name和description的二维数组

                // 遍历每个工具的[name, description]
                for (List<String> toolInfo : toolList) {
                    String toolName = toolInfo.get(0); // 工具名称
                    String toolDesc = toolInfo.get(1); // 工具描述
                    System.out.println("工具名称：" + toolName + "，描述：" + toolDesc);
                    BotMcp botMcp = new BotMcp();
                    botMcp.setBotId(botId);
                    botMcp.setMcpId(new BigInteger(mcpId));
                    botMcp.setMcpToolName(toolName);
                    botMcp.setMcpToolDescription(toolDesc);
                    this.save(botMcp);
                }
            }
        }
    }
}

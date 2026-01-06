package tech.aiflowy.ai.service;

import com.agentsflex.core.model.chat.tool.Tool;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.BotMcp;
import tech.aiflowy.ai.entity.Mcp;
import tech.aiflowy.common.domain.Result;

import java.io.Serializable;

/**
 *  服务层。
 *
 * @author wangGangQiang
 * @since 2026-01-04
 */
public interface McpService extends IService<Mcp> {

    void saveMcp(Mcp entity);

    Result<Page<Mcp>> pageMcpTools(Result<Page<Mcp>> page);

    void updateMcp(Mcp entity);

    void removeMcp(Serializable id);

    Tool toFunction(BotMcp botMcp);
}

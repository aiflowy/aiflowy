package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.common.domain.Result;

/**
 *  服务层。
 *
 * @author WangGangqiang
 * @since 2025-04-27
 */
public interface AiPluginToolService extends IService<AiPluginTool> {

    Result savePluginTool(AiPluginTool aiPluginTool);

    Result searchPlugin(String aiPluginToolId);
}

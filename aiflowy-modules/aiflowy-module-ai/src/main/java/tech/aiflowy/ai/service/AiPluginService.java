package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.common.domain.Result;

/**
 *  服务层。
 *
 * @author WangGangqiang
 * @since 2025-04-25
 */
public interface AiPluginService extends IService<AiPlugin> {

    Result savePlugin(AiPlugin aiPlugin);

    Result removePlugin(String id);

    Result updatePlugin(AiPlugin aiPlugin);

    Result getList();

    void pageByCategory(Long pageNumber, Long pageSize, int category);
}

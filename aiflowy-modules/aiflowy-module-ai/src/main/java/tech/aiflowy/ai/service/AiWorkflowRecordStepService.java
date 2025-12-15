package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.AiWorkflowRecordStep;

/**
 * 执行记录步骤 服务层。
 *
 * @author ArkLight
 * @since 2025-05-28
 */
public interface AiWorkflowRecordStepService extends IService<AiWorkflowRecordStep> {

    // 根据 execKey 获取记录
    AiWorkflowRecordStep getByExecKey(String execKey);
}

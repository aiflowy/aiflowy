package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.entity.AiWorkflowRecordStep;
import tech.aiflowy.ai.mapper.AiWorkflowRecordStepMapper;
import tech.aiflowy.ai.service.AiWorkflowRecordStepService;

/**
 * 执行记录步骤 服务层实现。
 *
 * @author ArkLight
 * @since 2025-05-28
 */
@Service
public class AiWorkflowRecordStepServiceImpl extends ServiceImpl<AiWorkflowRecordStepMapper, AiWorkflowRecordStep>  implements AiWorkflowRecordStepService{

    @Override
    public AiWorkflowRecordStep getByExecKey(String execKey) {
        QueryWrapper w = QueryWrapper.create();
        w.eq(AiWorkflowRecordStep::getExecKey, execKey);
        return getOne(w);
    }
}

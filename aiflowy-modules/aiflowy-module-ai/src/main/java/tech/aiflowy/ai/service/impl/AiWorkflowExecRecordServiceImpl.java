package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.entity.AiWorkflowExecRecord;
import tech.aiflowy.ai.mapper.AiWorkflowExecRecordMapper;
import tech.aiflowy.ai.service.AiWorkflowExecRecordService;

/**
 * 工作流执行记录 服务层实现。
 *
 * @author ArkLight
 * @since 2025-05-28
 */
@Service
public class AiWorkflowExecRecordServiceImpl extends ServiceImpl<AiWorkflowExecRecordMapper, AiWorkflowExecRecord>  implements AiWorkflowExecRecordService{

    @Override
    public AiWorkflowExecRecord getByExecKey(String execKey) {
        QueryWrapper w = QueryWrapper.create();
        w.eq(AiWorkflowExecRecord::getExecKey, execKey);
        return getOne(w);
    }
}

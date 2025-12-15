package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiWorkflowExecRecordBase;


/**
 * 工作流执行记录 实体类。
 *
 * @author ArkLight
 * @since 2025-05-28
 */
@Table(value = "tb_ai_workflow_exec_record", comment = "工作流执行记录")
public class AiWorkflowExecRecord extends AiWorkflowExecRecordBase {

    public Long getExecTime() {
        if (getEndTime() == null) {
            return null;
        }
        return getEndTime().getTime() - getStartTime().getTime();
    }
}

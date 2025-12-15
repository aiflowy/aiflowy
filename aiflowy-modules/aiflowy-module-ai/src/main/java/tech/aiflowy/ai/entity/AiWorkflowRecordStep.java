package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiWorkflowRecordStepBase;


/**
 * 执行记录步骤 实体类。
 *
 * @author ArkLight
 * @since 2025-05-28
 */
@Table(value = "tb_ai_workflow_record_step", comment = "执行记录步骤")
public class AiWorkflowRecordStep extends AiWorkflowRecordStepBase {

    /**
     * 节点类型，agentsflex里没有这个属性
     */
    @Column(ignore = true)
    private String nodeType;

    public Long getExecTime() {
        if (getEndTime() == null) {
            return null;
        }
        return getEndTime().getTime() - getStartTime().getTime();
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}

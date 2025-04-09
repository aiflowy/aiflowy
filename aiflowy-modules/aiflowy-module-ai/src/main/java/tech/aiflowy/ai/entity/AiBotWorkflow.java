package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiBotWorkflowBase;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;

/**
 *  实体类。
 *
 * @author michael
 * @since 2024-08-28
 */

@Table("tb_ai_bot_workflow")
public class AiBotWorkflow extends AiBotWorkflowBase {

    @RelationOneToOne(selfField = "workflowId", targetField = "id")
    private AiWorkflow workflow;

    public AiWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(AiWorkflow workflow) {
        this.workflow = workflow;
    }
}

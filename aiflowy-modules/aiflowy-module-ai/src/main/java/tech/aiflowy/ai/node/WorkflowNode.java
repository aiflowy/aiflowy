package tech.aiflowy.ai.node;

import dev.tinyflow.core.chain.Chain;
import dev.tinyflow.core.node.BaseNode;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.common.util.SpringContextUtil;

import java.util.HashMap;
import java.util.Map;

public class WorkflowNode extends BaseNode {

    private String workflowId;

    public WorkflowNode() {
    }

    public WorkflowNode(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public Map<String, Object> execute(Chain chain) {

        Map<String, Object> params = chain.getState().resolveParameters(this);
        AiWorkflowService service = SpringContextUtil.getBean(AiWorkflowService.class);
        AiWorkflow workflow = service.getById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在：" + workflowId);
        }
        //Chain subChain = workflow.toTinyflow().toChain();

        return new HashMap<>();
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}

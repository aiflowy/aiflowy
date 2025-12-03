package tech.aiflowy.ai.node;

import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.node.BaseNode;
import dev.tinyflow.core.parser.BaseNodeParser;

public class WorkflowNodeParser extends BaseNodeParser {

    @Override
    protected BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        String workflowId = data.getString("workflowId");
        if (workflowId == null) {
            throw new RuntimeException("请选择工作流");
        }
        return new WorkflowNode(workflowId);
    }

    public String getNodeName() {
        return "workflow-node";
    }
}

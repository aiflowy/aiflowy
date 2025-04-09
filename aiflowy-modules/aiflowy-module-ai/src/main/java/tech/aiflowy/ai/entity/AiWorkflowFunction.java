package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.common.util.SpringContextUtil;
import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.Parameter;
import com.agentsflex.core.llm.functions.BaseFunction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AiWorkflowFunction extends BaseFunction {

    private BigInteger workflowId;

    public AiWorkflowFunction() {
    }

    public AiWorkflowFunction(AiWorkflow aiWorkflow) {
        this.workflowId = aiWorkflow.getId();
        this.name = aiWorkflow.getTitle();
        this.description = aiWorkflow.getDescription();
        this.parameters = toParameters(aiWorkflow);
    }


    static com.agentsflex.core.llm.functions.Parameter[] toParameters(AiWorkflow aiWorkflow) {
        List<Parameter> parameterDefs = aiWorkflow.toTinyflow().toChain().getParameters();
        if (parameterDefs == null || parameterDefs.isEmpty()) {
            return new com.agentsflex.core.llm.functions.Parameter[0];
        }

        com.agentsflex.core.llm.functions.Parameter[] parameters = new com.agentsflex.core.llm.functions.Parameter[parameterDefs.size()];
        for (int i = 0; i < parameterDefs.size(); i++) {
            Parameter parameterDef = parameterDefs.get(i);
            com.agentsflex.core.llm.functions.Parameter parameter = new  com.agentsflex.core.llm.functions.Parameter();
            parameter.setName(parameterDef.getName());
            parameter.setDescription(parameterDef.getDescription());
            parameter.setType(parameterDef.getDataType().toString());
            parameter.setRequired(parameterDef.isRequired());
            parameters[i] = parameter;
        }
        return parameters;
    }

    public BigInteger getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(BigInteger workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public Object invoke(Map<String, Object> argsMap) {
        AiWorkflowService service = SpringContextUtil.getBean(AiWorkflowService.class);
        AiWorkflow workflow = service.getById(this.workflowId);
        if (workflow != null) {
            Chain chain = workflow.toTinyflow().toChain();
            return chain.executeForResult(argsMap);
        } else {
            throw new RuntimeException("can not find the workflow by id: " + this.workflowId);
        }
    }

    @Override
    public String toString() {
        return "AiWorkflowFunction{" +
                "workflowId=" + workflowId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}

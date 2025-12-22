package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.tool.BaseTool;
import com.agentsflex.core.model.chat.tool.Parameter;
import dev.tinyflow.core.chain.ChainDefinition;
import dev.tinyflow.core.chain.DataType;
import dev.tinyflow.core.chain.runtime.ChainExecutor;
import tech.aiflowy.common.util.SpringContextUtil;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AiWorkflowFunction extends BaseTool {

    private BigInteger workflowId;

    public AiWorkflowFunction() {
    }

    public AiWorkflowFunction(AiWorkflow aiWorkflow, boolean needEnglishName) {
        this.workflowId = aiWorkflow.getId();
        if (needEnglishName) {
            this.name = aiWorkflow.getEnglishName();
        } else {
            this.name = aiWorkflow.getTitle();
        }
        this.description = aiWorkflow.getDescription();
        this.parameters = toParameters(aiWorkflow);
    }


    static Parameter[] toParameters(AiWorkflow aiWorkflow) {
        ChainExecutor executor = SpringContextUtil.getBean(ChainExecutor.class);
        ChainDefinition definition = executor.getDefinitionRepository().getChainDefinitionById(aiWorkflow.getId().toString());
        List<dev.tinyflow.core.chain.Parameter> parameterDefs = definition.getStartParameters();
        if (parameterDefs == null || parameterDefs.isEmpty()) {
            return new Parameter[0];
        }

        Parameter[] parameters = new Parameter[parameterDefs.size()];
        for (int i = 0; i < parameterDefs.size(); i++) {
            dev.tinyflow.core.chain.Parameter parameterDef = parameterDefs.get(i);
            Parameter parameter = new Parameter();
            parameter.setName(parameterDef.getName());
            parameter.setDescription(parameterDef.getDescription());
            DataType dataType = parameterDef.getDataType();
            if (dataType == null) dataType = DataType.String;
            parameter.setType(dataType.toString());
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
        ChainExecutor executor = SpringContextUtil.getBean(ChainExecutor.class);
        return executor.execute(workflowId.toString(), argsMap);
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

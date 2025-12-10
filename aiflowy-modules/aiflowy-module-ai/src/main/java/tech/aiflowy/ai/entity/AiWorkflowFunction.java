//package tech.aiflowy.ai.entity;
//
//import com.agentsflex.core.model.chat.tool.BaseTool;
//import com.agentsflex.core.model.chat.tool.Parameter;
//import dev.tinyflow.core.Tinyflow;
//import dev.tinyflow.core.chain.Chain;
//import tech.aiflowy.ai.service.AiWorkflowService;
//import tech.aiflowy.ai.utils.TinyFlowConfigService;
//import tech.aiflowy.common.util.SpringContextUtil;
//
//import java.math.BigInteger;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//public class AiWorkflowFunction extends BaseTool {
//
//    private BigInteger workflowId;
//
//    public AiWorkflowFunction() {
//    }
//
//    public AiWorkflowFunction(AiWorkflow aiWorkflow, boolean needEnglishName) {
//        this.workflowId = aiWorkflow.getId();
//        if (needEnglishName){
//            this.name = aiWorkflow.getEnglishName();
//        } else {
//            this.name = aiWorkflow.getTitle();
//        }
//        this.description = aiWorkflow.getDescription();
//        this.parameters = toParameters(aiWorkflow);
//    }
//
//
//    static Parameter[] toParameters(AiWorkflow aiWorkflow) {
//        List<Parameter> parameterDefs = aiWorkflow.toTinyflow().toChain().getParameters();
//        if (parameterDefs == null || parameterDefs.isEmpty()) {
//            return new Parameter[0];
//        }
//
//        Parameter[] parameters = new Parameter[parameterDefs.size()];
//        for (int i = 0; i < parameterDefs.size(); i++) {
//            Parameter parameterDef = parameterDefs.get(i);
//            Parameter parameter = new Parameter();
//            parameter.setName(parameterDef.getName());
//            parameter.setDescription(parameterDef.getDescription());
//            parameter.setType(parameterDef.getDataType().toString());
//            parameter.setRequired(parameterDef.isRequired());
//            parameters[i] = parameter;
//        }
//        return parameters;
//    }
//
//    public BigInteger getWorkflowId() {
//        return workflowId;
//    }
//
//    public void setWorkflowId(BigInteger workflowId) {
//        this.workflowId = workflowId;
//    }
//
//    @Override
//    public Object invoke(Map<String, Object> argsMap) {
//        AiWorkflowService service = SpringContextUtil.getBean(AiWorkflowService.class);
//        AiWorkflow workflow = service.getById(this.workflowId);
//        if (workflow != null) {
//            Tinyflow tinyflow = workflow.toTinyflow();
//            Chain chain = tinyflow.toChain();
//            return chain.executeForResult(argsMap);
//        } else {
//            throw new RuntimeException("can not find the workflow by id: " + this.workflowId);
//        }
//    }
//
//
//    @Override
//    public String toString() {
//        return "AiWorkflowFunction{" +
//                "workflowId=" + workflowId +
//                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", parameters=" + Arrays.toString(parameters) +
//                '}';
//    }
//}

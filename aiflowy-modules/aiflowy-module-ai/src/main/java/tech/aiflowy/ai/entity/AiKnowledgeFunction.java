//package tech.aiflowy.ai.entity;
//
//import com.agentsflex.core.document.Document;
//import tech.aiflowy.ai.service.AiKnowledgeService;
//import tech.aiflowy.common.domain.Result;
//import tech.aiflowy.common.util.SpringContextUtil;
//import com.agentsflex.core.llm.functions.BaseFunction;
//import com.agentsflex.core.llm.functions.Parameter;
//
//import java.math.BigInteger;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//public class AiKnowledgeFunction extends BaseFunction {
//
//    private BigInteger knowledgeId;
//
//    public AiKnowledgeFunction() {
//    }
//
//
//    public AiKnowledgeFunction(AiKnowledge aiKnowledge, boolean needEnglishName) {
//        this.knowledgeId = aiKnowledge.getId();
//        if (needEnglishName){
//            this.name = aiKnowledge.getEnglishName();
//        } else {
//            this.name = aiKnowledge.getTitle();
//        }
//        this.description = aiKnowledge.getDescription();
//        this.parameters = getDefaultParameters();
//    }
//
//
//    public Parameter[] getDefaultParameters() {
//        Parameter parameter = new Parameter();
//        parameter.setName("input");
//        parameter.setDescription("要查询的相关知识");
//        parameter.setType("string");
//        parameter.setRequired(true);
//        return new Parameter[]{parameter};
//    }
//
//    public BigInteger getKnowledgeId() {
//        return knowledgeId;
//    }
//
//    public void setKnowledgeId(BigInteger knowledgeId) {
//        this.knowledgeId = knowledgeId;
//    }
//
//    @Override
//    public Object invoke(Map<String, Object> argsMap) {
//
//        AiKnowledgeService knowledgeService = SpringContextUtil.getBean(AiKnowledgeService.class);
//        List<Document> documents = knowledgeService.search(this.knowledgeId, (String) argsMap.get("input"));
//
//        StringBuilder sb = new StringBuilder();
//        if (documents != null) {
//            for (Document document : documents) {
//                sb.append(document.getContent());
//            }
//        }
//        return sb.toString();
//    }
//
//    @Override
//    public String toString() {
//        return "AiKnowledgeFunction{" +
//                "knowledgeId=" + knowledgeId +
//                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", parameters=" + Arrays.toString(parameters) +
//                '}';
//    }
//}

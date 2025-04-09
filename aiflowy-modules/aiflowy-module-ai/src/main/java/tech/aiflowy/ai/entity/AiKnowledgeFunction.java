package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.util.SpringContextUtil;
import com.agentsflex.core.llm.functions.BaseFunction;
import com.agentsflex.core.llm.functions.Parameter;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AiKnowledgeFunction extends BaseFunction {

    private BigInteger knowledgeId;

    public AiKnowledgeFunction() {
    }

    public AiKnowledgeFunction(AiKnowledge aiKnowledge) {
        this.knowledgeId = aiKnowledge.getId();
        this.name = aiKnowledge.getTitle();
        this.description = aiKnowledge.getDescription();
        this.parameters = getDefaultParameters();
    }


    public Parameter[] getDefaultParameters() {
        Parameter parameter = new Parameter();
        parameter.setName("input");
        parameter.setDescription("要查询的相关知识");
        parameter.setType("String");
        parameter.setRequired(true);
        return new Parameter[]{parameter};
    }

    public BigInteger getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(BigInteger knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    @Override
    public Object invoke(Map<String, Object> argsMap) {

        AiKnowledgeService knowledgeService = SpringContextUtil.getBean(AiKnowledgeService.class);
        Result result = knowledgeService.search(this.knowledgeId, (String) argsMap.get("input"));

        if (result.isSuccess()) {
            StringBuilder sb = new StringBuilder();
            //noinspection unchecked
            List<AiDocumentChunk> chunks = (List<AiDocumentChunk>) result.data();
            if (chunks != null) {
                for (AiDocumentChunk chunk : chunks) {
                    sb.append(chunk.getContent());
                }
            }
            return sb.toString();
        }


        return result.failMessage();
    }

    @Override
    public String toString() {
        return "AiKnowledgeFunction{" +
                "knowledgeId=" + knowledgeId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}

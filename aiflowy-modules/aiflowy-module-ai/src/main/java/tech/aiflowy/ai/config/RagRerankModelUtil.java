package tech.aiflowy.ai.config;

import com.agentsflex.rerank.DefaultRerankModel;
import com.agentsflex.rerank.DefaultRerankModelConfig;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.common.util.PropertiesUtil;

import java.util.Properties;
import java.util.Map;

public class RagRerankModelUtil {

    public static DefaultRerankModel getRerankModel(AiLlm aiLlmRerank) {
        if (aiLlmRerank == null){
            return null;
        }
        DefaultRerankModelConfig defaultRerankModelConfig = new DefaultRerankModelConfig();
        if (aiLlmRerank.getLlmModel() != null && !aiLlmRerank.getLlmModel().isEmpty()){
            defaultRerankModelConfig.setModel(aiLlmRerank.getLlmModel());
        }
        if (aiLlmRerank.getLlmApiKey() != null && !aiLlmRerank.getLlmApiKey().isEmpty()){
            defaultRerankModelConfig.setApiKey(aiLlmRerank.getLlmApiKey());
        }
        if (aiLlmRerank.getLlmEndpoint() != null && !aiLlmRerank.getLlmEndpoint().isEmpty()){
            defaultRerankModelConfig.setEndpoint(aiLlmRerank.getLlmEndpoint());
        }
        String llmExtraConfig = aiLlmRerank.getLlmExtraConfig();
        if (llmExtraConfig != null && !llmExtraConfig.isEmpty()){
            Properties prop = PropertiesUtil.textToProperties(llmExtraConfig);
            String basePath = prop.getProperty("basePath");
            if (basePath != null && !basePath.isEmpty()) {
                defaultRerankModelConfig.setBasePath(basePath);
            }
        }

        return new DefaultRerankModel(defaultRerankModelConfig);
    }
}

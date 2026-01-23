
package tech.aiflowy.ai.entity;

import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.embedding.EmbeddingModel;
import com.agentsflex.core.model.rerank.RerankModel;
import com.agentsflex.core.store.VectorData;
import com.agentsflex.embedding.ollama.OllamaEmbeddingConfig;
import com.agentsflex.embedding.ollama.OllamaEmbeddingModel;
import com.agentsflex.embedding.openai.OpenAIEmbeddingConfig;
import com.agentsflex.embedding.openai.OpenAIEmbeddingModel;
import com.agentsflex.llm.deepseek.DeepseekChatModel;
import com.agentsflex.llm.deepseek.DeepseekConfig;
import com.agentsflex.llm.ollama.OllamaChatConfig;
import com.agentsflex.llm.ollama.OllamaChatModel;
import com.agentsflex.llm.openai.OpenAIChatConfig;
import com.agentsflex.llm.openai.OpenAIChatModel;
import com.agentsflex.rerank.DefaultRerankModel;
import com.agentsflex.rerank.DefaultRerankModelConfig;
import com.agentsflex.rerank.gitee.GiteeRerankModel;
import com.agentsflex.rerank.gitee.GiteeRerankModelConfig;
import com.mybatisflex.annotation.RelationManyToOne;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.ModelBase;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_model")
public class Model extends ModelBase {

    @RelationManyToOne(selfField = "providerId", targetField = "id")
    private ModelProvider modelProvider;

    /**
     * 模型类型
     */
    public final static String[] MODEL_TYPES = {"chatModel", "embeddingModel", "rerankModel"};


    public ModelProvider getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public ChatModel toChatModel() {
        String providerType = modelProvider.getProviderType();
        if (StringUtil.noText(providerType)) {
            return null;
        }
        switch (providerType.toLowerCase()) {
            case "ollama":
                OllamaChatConfig ollamaChatConfig = new OllamaChatConfig();
                ollamaChatConfig.setEndpoint(checkAndGetEndpoint());
                if(StringUtil.hasText(getApiKey())){
                    ollamaChatConfig.setApiKey(checkAndGetApiKey());
                }
                ollamaChatConfig.setModel(checkAndGetModelName());
                ollamaChatConfig.setProvider(getModelProvider().getProviderName());
                return new OllamaChatModel(ollamaChatConfig);
            case "deepseek":
                DeepseekConfig deepseekConfig = new DeepseekConfig();
                deepseekConfig.setProvider(getModelProvider().getProviderName());
                deepseekConfig.setEndpoint(checkAndGetEndpoint());
                deepseekConfig.setApiKey(checkAndGetApiKey());
                deepseekConfig.setModel(checkAndGetModelName());
                deepseekConfig.setRequestPath(checkAndGetRequestPath());
                return new DeepseekChatModel(deepseekConfig);
            default:
                OpenAIChatConfig openAIChatConfig = new OpenAIChatConfig();
                openAIChatConfig.setProvider(getModelProvider().getProviderName());
                openAIChatConfig.setEndpoint(checkAndGetEndpoint());
                openAIChatConfig.setApiKey(checkAndGetApiKey());
                openAIChatConfig.setModel(checkAndGetModelName());
                openAIChatConfig.setRequestPath(checkAndGetRequestPath());
                if (getSupportToolMessage() != null) {
                    openAIChatConfig.setSupportToolMessage(getSupportToolMessage());
                }
                return new OpenAIChatModel(openAIChatConfig);
        }
    }

    public RerankModel toRerankModel() {
        switch (modelProvider.getProviderType().toLowerCase()) {
            case "gitee":
                GiteeRerankModelConfig giteeRerankModelConfig = new GiteeRerankModelConfig();
                giteeRerankModelConfig.setProvider(getModelProvider().getProviderName());
                giteeRerankModelConfig.setApiKey(checkAndGetApiKey());
                giteeRerankModelConfig.setEndpoint(checkAndGetEndpoint());
                giteeRerankModelConfig.setModel(checkAndGetModelName());
                giteeRerankModelConfig.setRequestPath(checkAndGetRequestPath());
                return new GiteeRerankModel(giteeRerankModelConfig);
            default:
                DefaultRerankModelConfig defaultRerankModelConfig = new DefaultRerankModelConfig();
                defaultRerankModelConfig.setProvider(getModelProvider().getProviderName());
                defaultRerankModelConfig.setApiKey(checkAndGetApiKey());
                defaultRerankModelConfig.setEndpoint(checkAndGetEndpoint());
                defaultRerankModelConfig.setRequestPath(checkAndGetRequestPath());
                defaultRerankModelConfig.setModel(checkAndGetModelName());
                return new DefaultRerankModel(defaultRerankModelConfig);
        }
    }

    public EmbeddingModel toEmbeddingModel() {
        String providerType = modelProvider.getProviderType();
        if (StringUtil.noText(providerType)) {
            return null;
        }
        switch (providerType.toLowerCase()) {
            case "ollama":
                OllamaEmbeddingConfig ollamaEmbeddingConfig = new OllamaEmbeddingConfig();
                ollamaEmbeddingConfig.setProvider(getModelProvider().getProviderName());
                ollamaEmbeddingConfig.setEndpoint(checkAndGetEndpoint());
                ollamaEmbeddingConfig.setApiKey(getApiKey());
                ollamaEmbeddingConfig.setModel(checkAndGetModelName());
                ollamaEmbeddingConfig.setRequestPath(getRequestPath());
                return new OllamaEmbeddingModel(ollamaEmbeddingConfig);
            default:
                OpenAIEmbeddingConfig openAIEmbeddingConfig = new OpenAIEmbeddingConfig();
                openAIEmbeddingConfig.setProvider(getModelProvider().getProviderName());
                openAIEmbeddingConfig.setEndpoint(checkAndGetEndpoint());
                openAIEmbeddingConfig.setApiKey(checkAndGetApiKey());
                openAIEmbeddingConfig.setModel(checkAndGetModelName());
                openAIEmbeddingConfig.setRequestPath(checkAndGetRequestPath());
                return new OpenAIEmbeddingModel(openAIEmbeddingConfig);
        }
    }

    /**
     * 获取模型向量的维度
     * @return
     */
    public static int getEmbeddingDimension(EmbeddingModel embeddingModel) {
        if (embeddingModel == null) {
            throw new BusinessException("embeddingModel不能为空");
        }
        VectorData vectorData = embeddingModel.embed("测试向量维度");
        return vectorData.getVector().length;
    }

    public String checkAndGetRequestPath() {
        if (StrUtil.isEmpty(getRequestPath())){
            throw new BusinessException("请求地址不能为空");
        }
        return getRequestPath();
    }

    public String checkAndGetApiKey() {
        if (StrUtil.isEmpty(getApiKey())) {
            throw new BusinessException("API 密钥不能为空");
        }
        return getApiKey();
    }

    public String checkAndGetEndpoint() {
        if (StrUtil.isEmpty(getEndpoint())){
            throw new BusinessException("API 地址不能为空");
        }
        return getEndpoint();
    }

    public String checkAndGetModelName() {
        if (StrUtil.isEmpty(getModelName())){
            throw new BusinessException("模型名称不能为空");
        }
        return getModelName();
    }

}

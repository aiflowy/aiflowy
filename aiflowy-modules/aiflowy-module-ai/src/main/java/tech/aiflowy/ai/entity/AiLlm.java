
package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.embedding.EmbeddingModel;
import com.agentsflex.core.model.rerank.RerankModel;
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
import com.agentsflex.rerank.gitee.GiteeRerankModel;
import com.agentsflex.rerank.gitee.GiteeRerankModelConfig;
import com.mybatisflex.annotation.RelationManyToOne;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;
import dev.tinyflow.core.llm.Llm;
import org.springframework.util.StringUtils;
import tech.aiflowy.ai.entity.base.AiLlmBase;
import tech.aiflowy.common.util.PropertiesUtil;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.util.Map;
import java.util.Properties;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_ai_llm")
public class AiLlm extends AiLlmBase {

    @RelationManyToOne(selfField = "providerId", targetField = "id")
    private AiLlmProvider aiLlmProvider;

    public final static String Llm_Endpoint = "llmEndpoint";
    public final static String Chat_path = "chatPath";
    public final static String Embed_path = "embedPath";
    public final static String Rerank_path = "rerankPath";

    public AiLlmProvider getAiLlmProvider() {
        return aiLlmProvider;
    }

    public void setAiLlmProvider(AiLlmProvider aiLlmProvider) {
        this.aiLlmProvider = aiLlmProvider;
    }

    public ChatModel toChatModel() {
        String provider = getAiLlmProvider().getProviderName();
        if (StringUtil.noText(provider)) {
            return null;
        }
        switch (provider.toLowerCase()) {
            case "ollama":
                return ollamaLlm();
            case "deepseek":
                return deepSeekLLm();
            default:
                return openaiLLm();
        }
    }

    private ChatModel ollamaLlm() {
        OllamaChatConfig ollamaChatConfig = new OllamaChatConfig();
        ollamaChatConfig.setEndpoint(getPath(Llm_Endpoint));
        ollamaChatConfig.setApiKey(getLlmApiKey());
        ollamaChatConfig.setModel(getLlmModel());
        return new OllamaChatModel(ollamaChatConfig);
    }

    private ChatModel deepSeekLLm() {
        DeepseekConfig deepseekConfig = new DeepseekConfig();
        deepseekConfig.setEndpoint(getPath(Llm_Endpoint));
        deepseekConfig.setApiKey(getLlmApiKey());
        deepseekConfig.setModel(getLlmModel());
        deepseekConfig.setRequestPath(getPath(Chat_path));
        return new DeepseekChatModel(deepseekConfig);
    }

    private ChatModel openaiLLm() {
        OpenAIChatConfig openAIChatConfig = new OpenAIChatConfig();
        openAIChatConfig.setEndpoint(getPath(Llm_Endpoint));
        openAIChatConfig.setApiKey(getLlmApiKey());
        openAIChatConfig.setModel(getLlmModel());
        openAIChatConfig.setLogEnabled(true);
        openAIChatConfig.setRequestPath(getPath(Chat_path));
        return new OpenAIChatModel(openAIChatConfig);
    }

    public RerankModel toRerankModel() {
        String rerankPath = getPath(Rerank_path);
        String endpoint = getPath(Llm_Endpoint);
        String apiKey = getLlmApiKey();
        switch (getAiLlmProvider().getProviderName().toLowerCase()) {
            case "gitee":
                GiteeRerankModelConfig giteeRerankModelConfig = new GiteeRerankModelConfig();
                giteeRerankModelConfig.setApiKey(apiKey);
                giteeRerankModelConfig.setEndpoint(endpoint);
                giteeRerankModelConfig.setRequestPath(rerankPath);
                return new GiteeRerankModel(giteeRerankModelConfig);
            default:
                return null;
        }
    }

    public EmbeddingModel toEmbeddingModel() {
        String embedPath = getPath(Embed_path);;
        String endpoint = getPath(Llm_Endpoint);
        String providerName = getAiLlmProvider().getProviderName();
        if (StringUtil.noText(providerName)) {
            return null;
        }
        switch (providerName.toLowerCase()) {
            case "ollama":
                OllamaEmbeddingConfig ollamaEmbeddingConfig = new OllamaEmbeddingConfig();
                ollamaEmbeddingConfig.setEndpoint(endpoint);
                ollamaEmbeddingConfig.setApiKey(getLlmApiKey());
                ollamaEmbeddingConfig.setModel(getLlmModel());
                if (StringUtils.hasLength(embedPath)) {
                    ollamaEmbeddingConfig.setRequestPath(embedPath);
                }
                return new OllamaEmbeddingModel(ollamaEmbeddingConfig);
            default:
                OpenAIEmbeddingConfig openAIEmbeddingConfig = new OpenAIEmbeddingConfig();
                openAIEmbeddingConfig.setEndpoint(endpoint);
                openAIEmbeddingConfig.setApiKey(getLlmApiKey());
                openAIEmbeddingConfig.setModel(getLlmModel());
                if (StringUtils.hasLength(embedPath)) {
                    openAIEmbeddingConfig.setRequestPath(embedPath);
                }
                return new OpenAIEmbeddingModel(openAIEmbeddingConfig);
        }
    }

    /**
     * 获取模型路径
     * @param key
     * @return
     */
    public String getPath (String key) {
        Map<String, Object> options = getOptions();
        String path = "";
        if (Llm_Endpoint.equals(key)) {
            path = getLlmEndpoint();
        }

        if (options != null) {
            String pathFromOptions = (String) options.get(key);
            if (path == null && StringUtils.hasLength(pathFromOptions)) {
                path = pathFromOptions;
            } else {
                if (Llm_Endpoint.equals(key)) {
                    path = this.getAiLlmProvider().getEndPoint();
                } else if (Chat_path.equals(key)){
                    path = this.getAiLlmProvider().getChatPath();
                } else if (Embed_path.equals(key)){
                    path = this.getAiLlmProvider().getEmbedPath();
                } else if (Rerank_path.equals(key)){
                    path = this.getAiLlmProvider().getRerankPath();
                }
            }
        }
        if (StringUtil.noText(path)) {
            throw new BusinessException("请设置模型" + key);
        }
        return path;
    }
}

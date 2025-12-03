
package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.llm.ollama.OllamaChatConfig;
import com.agentsflex.llm.ollama.OllamaChatModel;
import com.agentsflex.llm.openai.OpenAIChatConfig;
import com.agentsflex.llm.openai.OpenAIChatModel;
import com.mybatisflex.annotation.Table;
import org.springframework.util.StringUtils;
import tech.aiflowy.ai.entity.base.AiLlmBase;
import tech.aiflowy.common.util.PropertiesUtil;
import tech.aiflowy.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
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

    public List<String> getSupportFeatures() {
        List<String> features = new ArrayList<>();
        if (getSupportChat() != null && getSupportChat()) {
            features.add("对话");
        }

        if (getSupportFunctionCalling() != null && getSupportFunctionCalling()) {
            features.add("方法调用");
        }

        if (getSupportEmbed() != null && getSupportEmbed()) {
            features.add("Embedding");
        }

        if (getSupportReranker() != null && getSupportReranker()) {
            features.add("重排");
        }

        if (getSupportTextToImage() != null && getSupportTextToImage()) {
            features.add("文生图");
        }

        if (getSupportImageToImage() != null && getSupportImageToImage()) {
            features.add("图生图");
        }

        if (getSupportTextToAudio() != null && getSupportTextToAudio()) {
            features.add("文生音频");
        }

        if (getSupportAudioToAudio() != null && getSupportAudioToAudio()) {
            features.add("音频转音频");
        }

        if (getSupportTextToVideo() != null && getSupportTextToVideo()) {
            features.add("文生视频");
        }

        if (getSupportImageToVideo() != null && getSupportImageToVideo()) {
            features.add("图生视频");
        }

        if (getOptions() != null && !getOptions().isEmpty()) {
            Boolean multimodal = (Boolean) getOptions().get("multimodal");
            if (multimodal != null && multimodal) {
                features.add("多模态");
            }
        }

        return features;
    }

    public ChatModel toChatModel() {
        String brand = getBrand();
        if (StringUtil.noText(brand)) {
            return null;
        }
        switch (brand.toLowerCase()) {
            case "ollama":
                return ollamaLlm();
            default:
                return openaiLLm();
        }
    }
    private ChatModel ollamaLlm() {
        OllamaChatConfig ollamaChatConfig = new OllamaChatConfig();
        ollamaChatConfig.setEndpoint(getLlmEndpoint());
        ollamaChatConfig.setApiKey(getLlmApiKey());
        ollamaChatConfig.setModel(getLlmModel());
        return new OllamaChatModel(ollamaChatConfig);
    }
    private ChatModel openaiLLm() {
        OpenAIChatConfig openAIChatConfig = new OpenAIChatConfig();
        openAIChatConfig.setEndpoint(getLlmEndpoint());
        openAIChatConfig.setApiKey(getLlmApiKey());
        openAIChatConfig.setModel(getLlmModel());
        Properties properties = PropertiesUtil.textToProperties(getLlmExtraConfig() == null ? "" : getLlmExtraConfig());
        String chatPath = properties.getProperty("chatPath");
        Map<String, Object> options = getOptions();

        if (StringUtils.hasLength(chatPath)) {
            openAIChatConfig.setRequestPath(chatPath);
        } else {
            if (options != null) {
                String chatPathFromOptions = (String) options.get("chatPath");
                if (StringUtils.hasLength(chatPathFromOptions)) {
                    chatPath = chatPathFromOptions;
                    openAIChatConfig.setRequestPath(chatPath);
                }
                ;
            }

        }
        return new OpenAIChatModel(openAIChatConfig);
    }
}

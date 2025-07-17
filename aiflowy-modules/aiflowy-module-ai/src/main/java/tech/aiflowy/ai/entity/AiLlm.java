
package tech.aiflowy.ai.entity;

import com.agentsflex.llm.deepseek.DeepseekConfig;
import com.agentsflex.llm.deepseek.DeepseekLlm;
import tech.aiflowy.ai.entity.base.AiLlmBase;
import tech.aiflowy.common.util.StringUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.llm.gitee.GiteeAiLlm;
import com.agentsflex.llm.gitee.GiteeAiLlmConfig;
import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.agentsflex.llm.openai.OpenAILlm;
import com.agentsflex.llm.openai.OpenAILlmConfig;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import com.agentsflex.llm.spark.SparkLlm;
import com.agentsflex.llm.spark.SparkLlmConfig;
import com.mybatisflex.annotation.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.springframework.util.StringUtils;
import java.util.Map;
import tech.aiflowy.common.util.PropertiesUtil;
import com.alibaba.fastjson2.JSON;

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

    public Llm toLlm() {
        String brand = getBrand();
        if (StringUtil.noText(brand)) {
            return null;
        }
        switch (brand.toLowerCase()) {
            case "spark":
                return sparkLlm();
            case "ollama":
                return ollamaLlm();
            default:
                return openaiLLm();
        }
    }


    private Llm ollamaLlm() {
        OllamaLlmConfig ollamaLlmConfig = new OllamaLlmConfig();
        ollamaLlmConfig.setEndpoint(getLlmEndpoint());
        ollamaLlmConfig.setApiKey(getLlmApiKey());
        ollamaLlmConfig.setModel(getLlmModel());
       //        ollamaLlmConfig.setDebug(true);
        return new OllamaLlm(ollamaLlmConfig);
    }

    private Llm openaiLLm() {
        OpenAILlmConfig openAiLlmConfig = new OpenAILlmConfig();
        openAiLlmConfig.setEndpoint(getLlmEndpoint());
        openAiLlmConfig.setApiKey(getLlmApiKey());
        openAiLlmConfig.setModel(getLlmModel());
        openAiLlmConfig.setDefaultEmbeddingModel(getLlmModel());
        openAiLlmConfig.setDebug(true);
        Properties properties = PropertiesUtil.textToProperties(getLlmExtraConfig() == null ? "" : getLlmExtraConfig());
        String chatPath = properties.getProperty("chatPath");
        String embedPath = properties.getProperty("embedPath");

        Map<String, Object> options = getOptions();

        if (StringUtils.hasLength(chatPath)){
            openAiLlmConfig.setChatPath(chatPath);
        }else {
            if (options != null){
                String chatPathFromOptions = (String)options.get("chatPath");
                if (StringUtils.hasLength(chatPathFromOptions)){
                    chatPath = chatPathFromOptions;
                    openAiLlmConfig.setChatPath(chatPath);
                };
            }

        }

        if (StringUtils.hasLength(embedPath)){
            openAiLlmConfig.setEmbedPath(embedPath);
        }else {
            if (options != null) {
                String embedPathFromOptions = (String)options.get("embedPath");
                if (StringUtils.hasLength(embedPathFromOptions)){
                    embedPath = embedPathFromOptions;
                    openAiLlmConfig.setEmbedPath(embedPath);
                }
            }

        }

        return new OpenAILlm(openAiLlmConfig);
    }

    private Llm sparkLlm() {

        SparkLlmConfig sparkLlmConfig = new SparkLlmConfig();

        Properties properties = PropertiesUtil.textToProperties(getLlmExtraConfig() == null ? "" : getLlmExtraConfig());
        String version = properties.getProperty("version");
        String appId = properties.getProperty("appId");
        String apiSecret = properties.getProperty("apiSecret");

        Map<String,Object> options = getOptions();

        if (!StringUtils.hasLength(version) && options != null){
            String versionFromOptions = (String)options.get("version");
            if (StringUtils.hasLength(versionFromOptions)){
                version = versionFromOptions;
            }
        }

        if (!StringUtils.hasLength(appId) && options != null){
            String appIdFromOptions = (String)options.get("appId");
            if (StringUtils.hasLength(appIdFromOptions)){
                appId = appIdFromOptions;
            }
        }


        if (!StringUtils.hasLength(apiSecret) && options != null){
            String apiSecretFromOptions = (String)options.get("apiSecret");
            if (StringUtils.hasLength(apiSecretFromOptions)){
                apiSecret = apiSecretFromOptions;
            }
        }

        sparkLlmConfig.setApiSecret(apiSecret);
        sparkLlmConfig.setVersion(version);
        sparkLlmConfig.setAppId(appId);
        sparkLlmConfig.setApiKey(getLlmApiKey());

        return new SparkLlm(sparkLlmConfig);
    }

}

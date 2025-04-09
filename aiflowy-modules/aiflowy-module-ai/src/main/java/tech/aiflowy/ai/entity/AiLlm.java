package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiLlmBase;
import tech.aiflowy.common.util.PropertiesUtil;
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
        return features;
    }

    public Llm toLlm() {
        String brand = getBrand();
        if (StringUtil.noText(brand)) {
            return null;
        }
        switch (brand.toLowerCase()) {
            case "openai":
                return openaiLLm();
            case "spark":
                return sparkLlm();
            case "ollama":
                return ollamaLlm();
            case "qwen":
            case "aliyun":
                return qwenLlm();
            case "gitee":
                return giteeLlm();
            default:
                return null;
        }
    }

    private Llm giteeLlm() {
        GiteeAiLlmConfig giteeAiLlmConfig = new GiteeAiLlmConfig();
        giteeAiLlmConfig.setEndpoint(getLlmEndpoint());
        giteeAiLlmConfig.setApiKey(getLlmApiKey());
        giteeAiLlmConfig.setModel(getLlmModel());
        return new GiteeAiLlm(giteeAiLlmConfig);
    }

    private Llm qwenLlm() {
        QwenLlmConfig qwenLlmConfig = new QwenLlmConfig();
        qwenLlmConfig.setEndpoint(getLlmEndpoint());
        qwenLlmConfig.setApiKey(getLlmApiKey());
        qwenLlmConfig.setModel(getLlmModel());
        return new QwenLlm(qwenLlmConfig);
    }

    private Llm ollamaLlm() {
        OllamaLlmConfig ollamaLlmConfig = new OllamaLlmConfig();
        ollamaLlmConfig.setEndpoint(getLlmEndpoint());
        ollamaLlmConfig.setApiKey(getLlmApiKey());
        ollamaLlmConfig.setModel(getLlmModel());
        return new OllamaLlm(ollamaLlmConfig);
    }

    private Llm openaiLLm() {
        OpenAILlmConfig openAiLlmConfig = new OpenAILlmConfig();
        openAiLlmConfig.setEndpoint(getLlmEndpoint());
        openAiLlmConfig.setApiKey(getLlmApiKey());
        openAiLlmConfig.setModel(getLlmModel());
        return new OpenAILlm(openAiLlmConfig);
    }


    private Llm sparkLlm() {
        SparkLlmConfig sparkLlmConfig = PropertiesUtil.propertiesTextToEntity(getLlmExtraConfig(), SparkLlmConfig.class);
        sparkLlmConfig.setApiKey(getLlmApiKey());
        return new SparkLlm(sparkLlmConfig);
    }
}

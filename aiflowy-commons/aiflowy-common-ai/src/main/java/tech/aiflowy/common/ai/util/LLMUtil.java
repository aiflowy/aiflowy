package tech.aiflowy.common.ai.util;

import tech.aiflowy.common.options.SysOptions;
import tech.aiflowy.common.util.StringUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.llm.chatglm.ChatglmLlm;
import com.agentsflex.llm.chatglm.ChatglmLlmConfig;
import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.agentsflex.llm.openai.OpenAILlm;
import com.agentsflex.llm.openai.OpenAILlmConfig;
import com.agentsflex.llm.spark.SparkLlm;
import com.agentsflex.llm.spark.SparkLlmConfig;

public class LLMUtil {

    public static Llm getLlmByType(String type) {
        if ("chatGPT".equalsIgnoreCase(type)) {
            OpenAILlmConfig openAiLlmConfig = new OpenAILlmConfig();
            openAiLlmConfig.setApiKey(SysOptions.get("chatgpt_api_key"));
            openAiLlmConfig.setEndpoint(SysOptions.get("chatgpt_endpoint"));
            openAiLlmConfig.setModel(SysOptions.get("chatgpt_model_name"));
            return new OpenAILlm(openAiLlmConfig);
        } else if ("chatglm".equalsIgnoreCase(type)) {
            ChatglmLlmConfig config = new ChatglmLlmConfig();
            String chatglmApiKey = SysOptions.get("chatglm_api_key");
            if (StringUtil.hasText(chatglmApiKey)) {
                config.setApiKey(chatglmApiKey);
                return new ChatglmLlm(config);
            }
        } else if ("ollama".equalsIgnoreCase(type)) {
            OllamaLlmConfig ollamaLlmConfig = new OllamaLlmConfig();
            ollamaLlmConfig.setModel(SysOptions.get("ollama_model_name"));
            ollamaLlmConfig.setEndpoint(SysOptions.get("ollama_endpoint"));
            return new OllamaLlm(ollamaLlmConfig);
        }
        //星火大模型
        else if ("spark".equalsIgnoreCase(type)) {
            SparkLlmConfig config = new SparkLlmConfig();
            config.setAppId(SysOptions.get("spark_ai_app_id"));
            config.setApiKey(SysOptions.get("spark_ai_api_key"));
            config.setApiSecret(SysOptions.get("spark_ai_app_secret"));
            config.setVersion(SysOptions.get("spark_ai_version"));
            config.setDebug(true);
            return new SparkLlm(config);
        }
        return null;
    }
}

package tech.aiflowy.common.ai.util;

import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.llm.ollama.OllamaChatConfig;
import com.agentsflex.llm.ollama.OllamaChatModel;
import com.agentsflex.llm.openai.OpenAIChatConfig;
import com.agentsflex.llm.openai.OpenAIChatModel;
import tech.aiflowy.common.options.SysOptions;

public class LLMUtil {

    public static ChatModel getLlmByType(String type) {
        if ("openai".equalsIgnoreCase(type)) {
            OpenAIChatConfig openAiChatConfig = new OpenAIChatConfig();
            openAiChatConfig.setApiKey(SysOptions.get("chatgpt_api_key"));
            openAiChatConfig.setEndpoint(SysOptions.get("chatgpt_endpoint"));
            openAiChatConfig.setModel(SysOptions.get("chatgpt_model_name"));
            return new OpenAIChatModel(openAiChatConfig);
        }
        else if ("ollama".equalsIgnoreCase(type)) {
            OllamaChatConfig ollamaChatConfig = new OllamaChatConfig();
            ollamaChatConfig.setModel(SysOptions.get("ollama_model_name"));
            ollamaChatConfig.setEndpoint(SysOptions.get("ollama_endpoint"));
            return new OllamaChatModel(ollamaChatConfig);
        }
        return null;
    }
}

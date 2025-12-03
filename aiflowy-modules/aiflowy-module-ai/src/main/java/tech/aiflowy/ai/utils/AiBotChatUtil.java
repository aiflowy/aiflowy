
package tech.aiflowy.ai.utils;

import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.llm.deepseek.DeepseekChatModel;

public class AiBotChatUtil {

    public static final String LLM_BRAND_KEY = "aiLlmBrand";

    /**
     * 判断当前大模型的方法名称是否需要传非中文的 tool 名称
     *
     * @param chatModel
     * @return
     */
    public static boolean needEnglishName(ChatModel chatModel) {
        return chatModel instanceof DeepseekChatModel;
    }

}

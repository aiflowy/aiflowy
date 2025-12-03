package tech.aiflowy.ai.tinyflow.llm;

import dev.tinyflow.agentsflex.provider.AgentsFlexLlm;
import dev.tinyflow.core.llm.Llm;
import dev.tinyflow.core.llm.LlmProvider;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.service.AiLlmService;

import javax.annotation.Resource;

@Component
public class LlmProviderImpl implements LlmProvider {

    @Resource
    private AiLlmService aiLlmService;

    @Override
    public Llm getChatModel(Object modelId) {
        // TODO AiLlM 转成 ChatModel
        AgentsFlexLlm llm = new AgentsFlexLlm();
        llm.setChatModel(null);
        return llm;
    }
}

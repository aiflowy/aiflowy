package tech.aiflowy.ai.tinyflow.config;

import dev.tinyflow.core.chain.repository.ChainDefinitionRepository;
import dev.tinyflow.core.chain.repository.ChainStateRepository;
import dev.tinyflow.core.chain.repository.NodeStateRepository;
import dev.tinyflow.core.chain.runtime.ChainExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.aiflowy.ai.tinyflow.listener.ChainErrorListenerForSave;
import tech.aiflowy.ai.tinyflow.listener.ChainEventListenerForSave;
import tech.aiflowy.ai.tinyflow.listener.NodeErrorListenerForSave;

import javax.annotation.Resource;

@Configuration
public class ChainExecutorConfig {

    @Resource
    private ChainDefinitionRepository chainDefinitionRepository;
    @Resource
    private ChainStateRepository chainStateRepository;
    @Resource
    private NodeStateRepository nodeStateRepository;
    @Resource
    private ChainEventListenerForSave chainEventListenerForSave;

    @Bean(name = "chainExecutor")
    public ChainExecutor chainExecutor() {

        ChainExecutor chainExecutor = new ChainExecutor(chainDefinitionRepository,
                chainStateRepository,
                nodeStateRepository);

        saveStepsListeners(chainExecutor);

        return chainExecutor;
    }

    /**
     * 步骤保存监听器 - 自行实现
     */
    private void saveStepsListeners(ChainExecutor chainExecutor) {
        chainExecutor.addEventListener(chainEventListenerForSave);
        chainExecutor.addErrorListener(new ChainErrorListenerForSave());
        chainExecutor.addNodeErrorListener(new NodeErrorListenerForSave());
    }
}

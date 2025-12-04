package tech.aiflowy.ai.tinyflow;

import com.alicp.jetcache.Cache;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.chain.ChainDefinition;
import dev.tinyflow.core.chain.ChainState;
import dev.tinyflow.core.chain.NodeState;
import dev.tinyflow.core.chain.repository.*;
import dev.tinyflow.core.chain.runtime.ChainExecutor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.ai.tinyflow.listener.ChainErrorListenerForFront;
import tech.aiflowy.ai.tinyflow.listener.ChainEventListenerForFront;
import tech.aiflowy.ai.tinyflow.listener.NodeErrorListenerForFront;
import tech.aiflowy.ai.utils.TinyFlowConfigService;

import javax.annotation.Resource;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@Configuration
public class ChainConfig {

    public static final String CHAIN_CACHE_KEY = "chainState:";
    public static final String NODE_CACHE_KEY = "nodeState:";

    @Resource(name = "defaultCache")
    private Cache<String, Object> cache;
    @Resource
    private AiWorkflowService aiWorkflowService;
    @Resource
    private TinyFlowConfigService tinyFlowConfigService;

    @Bean(name = "chainExecutor")
    public ChainExecutor chainExecutor() {

        ChainExecutor chainExecutor = new ChainExecutor(new ChainDefinitionRepository() {

            @Override
            public ChainDefinition getChainDefinitionById(String id) {
                AiWorkflow workflow = aiWorkflowService.getById(id);
                String json = workflow.getContent();
                Tinyflow tinyflow = new Tinyflow(json);
                ChainDefinition chainDefinition = tinyflow.toChain();
                chainDefinition.setId(workflow.getId().toString());
                chainDefinition.setName(workflow.getEnglishName());
                chainDefinition.setDescription(workflow.getDescription());
                return chainDefinition;
            }

        }, new ChainStateRepository() {

            @Override
            public ChainState load(String instanceId) {
                ChainState chainState = (ChainState) cache.get(CHAIN_CACHE_KEY + instanceId);
                return chainState == null ? new ChainState() : chainState;
            }

            @Override
            public boolean tryUpdate(ChainState newState, EnumSet<ChainStateField> fields) {
                String key = CHAIN_CACHE_KEY + newState.getInstanceId();
                putCache(key, newState);
                return true;
            }
        }, new NodeStateRepository() {

            @Override
            public NodeState load(String instanceId, String nodeId) {
                String key = NODE_CACHE_KEY + instanceId + ":" + nodeId;
                NodeState nodeState = (NodeState) cache.get(key);
                if (nodeState == null) {
                    nodeState = new NodeState();
                    nodeState.setChainInstanceId(instanceId);
                    nodeState.setNodeId(nodeId);
                }
                return nodeState;
            }

            @Override
            public boolean tryUpdate(NodeState newState, EnumSet<NodeStateField> fields, long chainStateVersion) {
                String key = NODE_CACHE_KEY + newState.getChainInstanceId() + ":" + newState.getNodeId();
                putCache(key, newState);
                return true;
            }
        });

        addStateListeners(chainExecutor);
        saveStepsListeners(chainExecutor);

        return chainExecutor;
    }

    private void putCache(String key, Object value) {
        cache.put(key, value, 3, TimeUnit.DAYS);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initTinyFlow() {
        System.out.println("初始化 tinyflow 参数");
        tinyFlowConfigService.initProvidersAndNodeParsers();
    }

    /**
     * 执行状态监听器
     */
    private void addStateListeners(ChainExecutor chainExecutor) {
        chainExecutor.addEventListener(new ChainEventListenerForFront());
        chainExecutor.addErrorListener(new ChainErrorListenerForFront());
        chainExecutor.addNodeErrorListener(new NodeErrorListenerForFront());
    }

    /**
     * 步骤保存监听器
     */
    private void saveStepsListeners(ChainExecutor chainExecutor) {

    }
}

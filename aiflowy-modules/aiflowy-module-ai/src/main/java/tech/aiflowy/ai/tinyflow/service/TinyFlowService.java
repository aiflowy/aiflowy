package tech.aiflowy.ai.tinyflow.service;

import dev.tinyflow.core.chain.ChainState;
import dev.tinyflow.core.chain.ExceptionSummary;
import dev.tinyflow.core.chain.NodeState;
import dev.tinyflow.core.chain.repository.ChainStateRepository;
import dev.tinyflow.core.chain.repository.NodeStateRepository;
import dev.tinyflow.core.chain.runtime.ChainExecutor;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.tinyflow.entity.ChainInfo;
import tech.aiflowy.ai.tinyflow.entity.NodeInfo;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class TinyFlowService {

    @Resource
    private ChainExecutor chainExecutor;

    /**
     * 获取执行状态
     */
    public ChainInfo getChainStatus(String executeId, List<NodeInfo> nodes) {

        ChainStateRepository chainStateRepository = chainExecutor.getChainStateRepository();
        NodeStateRepository nodeStateRepository = chainExecutor.getNodeStateRepository();

        ChainState chainState = chainStateRepository.load(executeId);
        ChainInfo res = getChainInfo(executeId, chainState);

        for (NodeInfo node : nodes) {
            processNodeState(executeId, node, chainStateRepository, nodeStateRepository);
            res.getNodes().put(node.getNodeId(), node);
        }
        return res;
    }

    /**
     * 处理节点状态
     */
    private void processNodeState(String currentExecuteId,
                                  NodeInfo node,
                                  ChainStateRepository chainStateRepository,
                                  NodeStateRepository nodeStateRepository) {

        // 加载当前层的状态
        ChainState currentChainState = chainStateRepository.load(currentExecuteId);
        NodeState currentNodeState = nodeStateRepository.load(currentExecuteId, node.getNodeId());

        setNodeStatus(node, currentNodeState, currentChainState);
    }

    private static ChainInfo getChainInfo(String executeId, ChainState chainState) {
        ChainInfo res = new ChainInfo();
        res.setExecuteId(executeId);
        res.setStatus(chainState.getStatus().getValue());
        ExceptionSummary chainError = chainState.getError();
        if (chainError != null) {
            res.setMessage(chainError.getRootCauseClass() + " --> " + chainError.getRootCauseMessage());
        }
        Map<String, Object> executeResult = chainState.getExecuteResult();
        if (executeResult != null && !executeResult.isEmpty()) {
            res.setResult(executeResult);
        }
        return res;
    }

    private void setNodeStatus(NodeInfo node, NodeState nodeState, ChainState chainState) {
        String nodeId = node.getNodeId();
        // 如果状态为空或不存在，可能不需要覆盖，这里视具体业务逻辑而定，目前保持原逻辑
        node.setStatus(nodeState.getStatus().getValue());

        ExceptionSummary error = nodeState.getError();
        if (error != null) {
            node.setMessage(error.getRootCauseClass() + " --> " + error.getRootCauseMessage());
        }

        Map<String, Object> nodeExecuteResult = chainState.getNodeExecuteResult(nodeId);
        if (nodeExecuteResult != null && !nodeExecuteResult.isEmpty()) {
            node.setResult(nodeExecuteResult);
        }

        // 只有当参数不为空时才覆盖
        if (chainState.getSuspendForParameters() != null) {
            node.setSuspendForParameters(chainState.getSuspendForParameters());
        }
    }
}

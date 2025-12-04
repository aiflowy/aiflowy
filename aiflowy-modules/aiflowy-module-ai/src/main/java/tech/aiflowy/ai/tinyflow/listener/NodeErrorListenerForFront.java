package tech.aiflowy.ai.tinyflow.listener;

import dev.tinyflow.core.chain.Chain;
import dev.tinyflow.core.chain.Node;
import dev.tinyflow.core.chain.listener.NodeErrorListener;

import java.util.Map;

public class NodeErrorListenerForFront implements NodeErrorListener {
    @Override
    public void onError(Throwable error, Node node, Map<String, Object> nodeResult, Chain chain) {
        System.out.println("node error: " + error.getMessage() + ": " + node.getName());
    }
}

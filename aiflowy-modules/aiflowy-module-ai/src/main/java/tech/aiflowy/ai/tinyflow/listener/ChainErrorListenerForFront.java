package tech.aiflowy.ai.tinyflow.listener;

import dev.tinyflow.core.chain.Chain;
import dev.tinyflow.core.chain.listener.ChainErrorListener;

public class ChainErrorListenerForFront implements ChainErrorListener {
    @Override
    public void onError(Throwable error, Chain chain) {
        System.out.println("ChainErrorListenerForFront: " + error.getMessage());
    }
}

package tech.aiflowy.ai.tinyflow.listener;

import dev.tinyflow.core.chain.Chain;
import dev.tinyflow.core.chain.Event;
import dev.tinyflow.core.chain.listener.ChainEventListener;
import org.springframework.stereotype.Component;

@Component
public class ChainEventListenerForFront implements ChainEventListener {

    @Override
    public void onEvent(Event event, Chain chain) {
        System.out.println(event);
    }
}

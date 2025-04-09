package tech.aiflowy.common.ai;

import tech.aiflowy.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public class MySseEmitter extends SseEmitter {
    private static final Logger logger = LoggerFactory.getLogger(MySseEmitter.class);

    public MySseEmitter() {
    }

    public MySseEmitter(Long timeout) {
        super(timeout);
    }

    public void send(String content) {
        try {
            if (StringUtil.hasText(content)) {
                SseEventBuilder event = SseEmitter.event().data(content);
                super.send(event);
            }
        } catch (IOException e) {
            super.completeWithError(e);
        }
    }

    public void sendAndComplete(String content) {
        try {
            super.send(SseEmitter.event().data(content));
            super.complete();
        } catch (IOException e) {
            logger.error(e.toString(), e);
            super.completeWithError(e);
        }
    }
}

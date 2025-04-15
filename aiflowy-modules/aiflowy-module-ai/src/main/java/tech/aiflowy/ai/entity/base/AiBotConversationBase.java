package tech.aiflowy.ai.entity.base;

import java.io.Serializable;
import java.math.BigInteger;

public class AiBotConversationBase implements Serializable {

    private BigInteger sessionId;

    private String title;

    public BigInteger getSessionId() {
        return sessionId;
    }

    public void setSessionId(BigInteger sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

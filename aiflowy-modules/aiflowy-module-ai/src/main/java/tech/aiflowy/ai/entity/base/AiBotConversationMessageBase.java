package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class AiBotConversationMessageBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话id
     */
    @Id(comment = "会话id")
    private String sessionId;

    /**
     * 会话标题
     */
    @Column(comment = "会话标题")
    private String title;

    /**
     * BotId
     */
    @Column(comment = "BotId")
    private BigInteger BotId;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 用户id
     */
    @Column(comment = "用户id")
    private BigInteger accountId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigInteger getBotId() {
        return BotId;
    }

    public void setBotId(BigInteger botId) {
        BotId = botId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public BigInteger getAccountId() {
        return accountId;
    }

    public void setAccountId(BigInteger accountId) {
        this.accountId = accountId;
    }
}

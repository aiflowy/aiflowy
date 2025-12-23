package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class BotConversationBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "会话id")
    private BigInteger id;

    /**
     * 会话标题
     */
    @Column(comment = "会话标题")
    private String title;

    /**
     * botid
     */
    @Column(comment = "botid")
    private BigInteger botId;

    /**
     * 账户 id
     */
    @Column(comment = "账户 id")
    private BigInteger accountId;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigInteger getBotId() {
        return botId;
    }

    public void setBotId(BigInteger botId) {
        this.botId = botId;
    }

    public BigInteger getAccountId() {
        return accountId;
    }

    public void setAccountId(BigInteger accountId) {
        this.accountId = accountId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}

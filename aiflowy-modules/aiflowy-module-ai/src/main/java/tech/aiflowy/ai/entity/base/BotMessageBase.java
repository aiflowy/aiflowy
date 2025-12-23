package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import tech.aiflowy.common.entity.DateEntity;


public class BotMessageBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "ID")
    private BigInteger id;

    /**
     * botId
     */
    @Column(comment = "botId")
    private BigInteger botId;

    /**
     * 关联的账户ID
     */
    @Column(comment = "关联的账户ID")
    private BigInteger accountId;

    /**
     * 会话ID
     */
    @Column(comment = "会话ID")
    private BigInteger conversationId;

    /**
     * 角色[user|assistant]
     */
    @Column(comment = "角色[user|assistant]")
    private String role;

    /**
     * 内容
     */
    @Column(comment = "内容")
    private String content;

    /**
     * 图片
     */
    @Column(comment = "图片")
    private String image;

    /**
     * 选项
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "选项")
    private Map<String, Object> options;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 更新时间
     */
    @Column(comment = "更新时间")
    private Date modified;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public BigInteger getConversationId() {
        return conversationId;
    }

    public void setConversationId(BigInteger conversationId) {
        this.conversationId = conversationId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}

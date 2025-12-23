package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import tech.aiflowy.common.entity.DateEntity;


public class ModelProviderBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "id")
    private BigInteger id;

    /**
     * 供应商名称
     */
    @Column(comment = "供应商名称")
    private String providerName;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建者
     */
    @Column(comment = "创建者")
    private BigInteger createdBy;

    /**
     * 修改时间
     */
    @Column(comment = "修改时间")
    private Date modified;

    /**
     * 修改者
     */
    @Column(comment = "修改者")
    private BigInteger modifiedBy;

    /**
     * 图标
     */
    @Column(comment = "图标")
    private String icon;

    /**
     * apiKey
     */
    @Column(comment = "apiKey")
    private String apiKey;

    /**
     * endPoint
     */
    @Column(comment = "endPoint")
    private String endpoint;

    /**
     * 对话地址
     */
    @Column(comment = "对话地址")
    private String chatPath;

    /**
     * 向量地址
     */
    @Column(comment = "向量地址")
    private String embedPath;

    /**
     * 重排路径
     */
    @Column(comment = "重排路径")
    private String rerankPath;

    /**
     * 供应商
     */
    @Column(comment = "供应商")
    private String provider;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public BigInteger getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public BigInteger getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(BigInteger modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getChatPath() {
        return chatPath;
    }

    public void setChatPath(String chatPath) {
        this.chatPath = chatPath;
    }

    public String getEmbedPath() {
        return embedPath;
    }

    public void setEmbedPath(String embedPath) {
        this.embedPath = embedPath;
    }

    public String getRerankPath() {
        return rerankPath;
    }

    public void setRerankPath(String rerankPath) {
        this.rerankPath = rerankPath;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

}

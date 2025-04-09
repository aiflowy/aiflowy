package tech.aiflowy.ai.entity.base;

import tech.aiflowy.common.entity.DateTreeEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;


public class AiDocumentBase extends DateTreeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private BigInteger id;

    /**
     * 知识库ID
     */
    @Column(comment = "知识库ID")
    private BigInteger knowledgeId;

    /**
     * 文档类型 pdf/word/aieditor 等
     */
    @Column(comment = "文档类型 pdf/word/aieditor 等")
    private String documentType;

    /**
     * 文档路径
     */
    @Column(comment = "文档路径")
    private String documentPath;

    /**
     * 标题
     */
    @Column(comment = "标题")
    private String title;

    /**
     * 内容
     */
    @Column(comment = "内容")
    private String content;

    /**
     * 内容类型
     */
    @Column(comment = "内容类型")
    private String contentType;

    /**
     * URL 别名
     */
    @Column(comment = "URL 别名")
    private String slug;

    /**
     * 排序序号
     */
    @Column(comment = "排序序号")
    private Integer orderNo;

    /**
     * 其他配置项
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "其他配置项")
    private Map<String, Object> options;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建人ID
     */
    @Column(comment = "创建人ID")
    private BigInteger createdBy;

    /**
     * 最后的修改时间
     */
    @Column(comment = "最后的修改时间")
    private Date modified;

    /**
     * 最后的修改人的ID
     */
    @Column(comment = "最后的修改人的ID")
    private BigInteger modifiedBy;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(BigInteger knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
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

}

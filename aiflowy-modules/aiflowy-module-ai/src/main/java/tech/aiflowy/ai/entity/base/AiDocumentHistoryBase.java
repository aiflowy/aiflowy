package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class AiDocumentHistoryBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto, value = "snowFlakeId")
    private BigInteger id;

    /**
     * 修改的文档ID
     */
    @Column(comment = "修改的文档ID")
    private Long documentId;

    /**
     * 旧标题
     */
    @Column(comment = "旧标题")
    private String oldTitle;

    /**
     * 新标题
     */
    @Column(comment = "新标题")
    private String newTitle;

    /**
     * 旧内容
     */
    @Column(comment = "旧内容")
    private String oldContent;

    /**
     * 新内容
     */
    @Column(comment = "新内容")
    private String newContent;

    /**
     * 旧的文档类型
     */
    @Column(comment = "旧的文档类型")
    private String oldDocumentType;

    /**
     * 新的额文档类型
     */
    @Column(comment = "新的额文档类型")
    private String newDocumentType;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建人ID
     */
    @Column(comment = "创建人ID")
    private Long createdBy;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getOldTitle() {
        return oldTitle;
    }

    public void setOldTitle(String oldTitle) {
        this.oldTitle = oldTitle;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public String getOldContent() {
        return oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public String getOldDocumentType() {
        return oldDocumentType;
    }

    public void setOldDocumentType(String oldDocumentType) {
        this.oldDocumentType = oldDocumentType;
    }

    public String getNewDocumentType() {
        return newDocumentType;
    }

    public void setNewDocumentType(String newDocumentType) {
        this.newDocumentType = newDocumentType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

}

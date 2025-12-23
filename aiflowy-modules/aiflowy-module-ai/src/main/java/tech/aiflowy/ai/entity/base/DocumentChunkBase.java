package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;


public class DocumentChunkBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private BigInteger id;

    /**
     * 文档ID
     */
    @Column(comment = "文档ID")
    private BigInteger documentId;

    /**
     * 知识库ID
     */
    @Column(comment = "知识库ID")
    private BigInteger documentCollectionId;

    /**
     * 分块内容
     */
    @Column(comment = "分块内容")
    private String content;

    /**
     * 分割顺序
     */
    @Column(comment = "分割顺序")
    private Long sorting;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getDocumentId() {
        return documentId;
    }

    public void setDocumentId(BigInteger documentId) {
        this.documentId = documentId;
    }

    public BigInteger getDocumentCollectionId() {
        return documentCollectionId;
    }

    public void setDocumentCollectionId(BigInteger documentCollectionId) {
        this.documentCollectionId = documentCollectionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSorting() {
        return sorting;
    }

    public void setSorting(Long sorting) {
        this.sorting = sorting;
    }

}

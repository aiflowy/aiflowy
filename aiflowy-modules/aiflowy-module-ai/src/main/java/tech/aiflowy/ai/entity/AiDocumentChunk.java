package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiDocumentChunkBase;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_ai_document_chunk")
public class AiDocumentChunk extends AiDocumentChunkBase {

    @RelationOneToOne(selfField = "documentId",
            targetTable = "tb_ai_document",
            targetField = "id",
            valueField = "title")
    @Column(ignore = true)
    private String title;

    /**
     * 相似度
     */
    @Column(ignore = true)
    private Double similarityScore;

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

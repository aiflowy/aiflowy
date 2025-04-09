package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiDocumentBase;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;

import java.math.BigInteger;

/**
 *  实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_ai_document")
public class AiDocument extends AiDocumentBase {

    // 每条document对应的有多少条documentChunk分段
    @Column(ignore = true)
   private BigInteger chunkCount;

    /**
     * 分块最大长度
     */
    @Column(ignore = true)
    private int chunkSize;

    /**
     *  分块之间的重叠长度
     */
    @Column(ignore = true)
    private int overlapSize;

    public BigInteger getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(BigInteger chunkCount) {
        this.chunkCount = chunkCount;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getOverlapSize() {
        return overlapSize;
    }

    public void setOverlapSize(int overlapSize) {
        this.overlapSize = overlapSize;
    }
}

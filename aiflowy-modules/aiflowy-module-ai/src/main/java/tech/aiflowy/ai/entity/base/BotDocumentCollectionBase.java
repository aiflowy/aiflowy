package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;


public class BotDocumentCollectionBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto, value = "snowFlakeId")
    private BigInteger id;

    private BigInteger botId;

    private BigInteger documentCollectionId;

    @Column(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> options;

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

    public BigInteger getDocumentCollectionId() {
        return documentCollectionId;
    }

    public void setDocumentCollectionId(BigInteger documentCollectionId) {
        this.documentCollectionId = documentCollectionId;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

}

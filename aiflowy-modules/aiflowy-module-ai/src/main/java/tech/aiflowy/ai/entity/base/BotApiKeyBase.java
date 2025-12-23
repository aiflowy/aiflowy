package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;
import java.io.Serializable;
import java.util.Map;


public class BotApiKeyBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "id")
    private Long id;

    /**
     * apiKey，请勿手动修改！
     */
    @Column(comment = "apiKey，请勿手动修改！")
    private String apiKey;

    /**
     * botId
     */
    @Column(comment = "botId")
    private Long botId;

    /**
     * 加密botId，生成apiKey的盐
     */
    @Column(comment = "加密botId，生成apiKey的盐")
    private String salt;

    /**
     * 预留拓展配置的字段
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "预留拓展配置的字段")
    private Map<String, Object> options;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getBotId() {
        return botId;
    }

    public void setBotId(Long botId) {
        this.botId = botId;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

}

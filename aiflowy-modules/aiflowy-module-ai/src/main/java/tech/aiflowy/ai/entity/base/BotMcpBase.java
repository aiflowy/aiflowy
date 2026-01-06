package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;


public class BotMcpBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "id")
    private BigInteger id;

    /**
     * botId
     */
    @Column(comment = "botId")
    private BigInteger botId;

    /**
     * mcpId
     */
    @Column(comment = "mcpId")
    private BigInteger mcpId;

    /**
     * mcp工具名称
     */
    @Column(comment = "mcp工具名称")
    private String mcpToolName;

    /**
     * mcp工具描述
     */
    @Column(comment = "mcp工具描述")
    private String mcpToolDescription;

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

    public BigInteger getMcpId() {
        return mcpId;
    }

    public void setMcpId(BigInteger mcpId) {this.mcpId = mcpId;}

    public String getMcpToolName() {return mcpToolName;}

    public void setMcpToolName(String mcpToolName) {this.mcpToolName = mcpToolName;}

    public String getMcpToolDescription() {return mcpToolDescription;}

    public void setMcpToolDescription(String mcpToolDescription) {this.mcpToolDescription = mcpToolDescription;}
}

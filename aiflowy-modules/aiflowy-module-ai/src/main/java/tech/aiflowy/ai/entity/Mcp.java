package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.modelcontextprotocol.spec.McpSchema;
import tech.aiflowy.ai.entity.base.McpBase;

import java.util.List;


/**
 * 实体类。
 *
 * @author wangGangQiang
 * @since 2026-01-04
 */
@Table("tb_mcp")
public class Mcp extends McpBase {

    @Column(ignore = true)
    private List<McpSchema.Tool> tools;

    /**
     * 该MCP服务是否存活
     */
    @Column(ignore = true)
    private boolean alive;

    /**
     * 客户端健康状态
     */
    @Column(ignore = true)
    private Boolean clientOnline;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public List<McpSchema.Tool> getTools() {
        return tools;
    }

    public void setTools(List<McpSchema.Tool> tools) {
        this.tools = tools;
    }

    public Boolean getClientOnline() {
        return clientOnline;
    }

    public void setClientOnline(Boolean clientOnline) {
        this.clientOnline = clientOnline;
    }
}

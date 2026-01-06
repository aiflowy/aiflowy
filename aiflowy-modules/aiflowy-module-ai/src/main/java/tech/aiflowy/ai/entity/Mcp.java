package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.modelcontextprotocol.spec.McpSchema;
import tech.aiflowy.ai.entity.base.McpBase;

import java.util.List;


/**
 *  实体类。
 *
 * @author wangGangQiang
 * @since 2026-01-04
 */
@Table("tb_mcp")
public class Mcp extends McpBase {

    @Column(ignore = true)
    List<McpSchema.Tool> tools;

    public List<McpSchema.Tool> getTools() {
        return tools;
    }

    public void setTools(List<McpSchema.Tool> tools) {
        this.tools = tools;
    }
}

package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiPluginToolBase;


/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-04-27
 */
@Table("tb_ai_plugin_tool")
public class AiPluginTool extends AiPluginToolBase {

    @Column(ignore = true)
    private boolean joinBot;

    public boolean isJoinBot() {
        return joinBot;
    }

    public void setJoinBot(boolean joinBot) {
        this.joinBot = joinBot;
    }
}

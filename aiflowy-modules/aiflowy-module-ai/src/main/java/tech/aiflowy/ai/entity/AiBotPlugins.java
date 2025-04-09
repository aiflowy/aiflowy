package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiBotPluginsBase;


/**
 *  实体类。
 *
 * @author michael
 * @since 2025-04-07
 */
@Table("tb_ai_bot_plugins")
public class AiBotPlugins extends AiBotPluginsBase {

    @RelationOneToOne(selfField = "pluginId", targetField = "id")
    private AiPlugins aiPlugins;

    public AiPlugins getAiPlugins() {
        return aiPlugins;
    }

    public void setAiPlugins(AiPlugins aiPlugins) {
        this.aiPlugins = aiPlugins;
    }
}

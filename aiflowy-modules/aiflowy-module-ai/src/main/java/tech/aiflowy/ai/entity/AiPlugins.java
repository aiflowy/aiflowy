package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiPluginsBase;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;


/**
 * 插件 实体类。
 *
 * @author ArkLight
 * @since 2025-04-01
 */
@Table(value = "tb_ai_plugins", comment = "插件")
public class AiPlugins extends AiPluginsBase {

    @Column(ignore = true)
    private String title;

    public String getTitle() {
        return this.getPluginName();
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

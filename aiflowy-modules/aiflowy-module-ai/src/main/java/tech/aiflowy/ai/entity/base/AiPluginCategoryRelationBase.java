package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Id;
import java.io.Serializable;


public class AiPluginCategoryRelationBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long pluginId;

    @Id
    private Integer categoryId;

    public Long getPluginId() {
        return pluginId;
    }

    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

}

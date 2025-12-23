package tech.aiflowy.ai.entity.base;

import java.io.Serializable;
import java.math.BigInteger;


public class PluginCategoryMappingBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigInteger categoryId;

    private BigInteger pluginId;

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public BigInteger getPluginId() {
        return pluginId;
    }

    public void setPluginId(BigInteger pluginId) {
        this.pluginId = pluginId;
    }

}

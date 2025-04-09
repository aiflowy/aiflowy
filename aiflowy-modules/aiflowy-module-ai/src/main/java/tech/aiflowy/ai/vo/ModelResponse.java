package tech.aiflowy.ai.vo;

import java.util.List;

/**
 *  实体类。
 *
 * @author wangGangQiang
 * @since 2025-03-13
 */
public class ModelResponse {
    private List<OllamaModel> models;

    public void setModels(List<OllamaModel> models) {
        this.models = models;
    }

    public List<OllamaModel> getModels() {
        return models;
    }


}

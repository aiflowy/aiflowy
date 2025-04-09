package tech.aiflowy.ai.service;

import tech.aiflowy.common.domain.Result;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiOllamaService {
    public Result getLargeModels(String apiUrl,Integer current,Integer pageSize);

    Result getOllamaModels();

    SseEmitter installModel(String modelApiUrl, String modelName);

    boolean deleteModel(String modelApiUrl, String modelName);
}

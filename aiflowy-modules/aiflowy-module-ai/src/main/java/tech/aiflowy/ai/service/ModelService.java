package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.Model;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.common.domain.Result;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface ModelService extends IService<Model> {

    boolean addAiLlm(Model entity);

    Map<String, Object> verifyModelConfig(Model llm);

    Map<String, Map<String, List<Model>>> getList(Model entity);

    void removeByEntity(Model entity);

    Model getModelInstance(BigInteger modelId);

    void updateByEntity(Model entity);
}

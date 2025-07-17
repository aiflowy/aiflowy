package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.common.domain.Result;
import com.mybatisflex.core.service.IService;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiLlmService extends IService<AiLlm> {

    Result addAiLlm(AiLlm entity);

    void verifyLlmConfig(AiLlm llm);
}

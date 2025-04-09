package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.common.domain.Result;
import com.mybatisflex.core.service.IService;

import java.math.BigInteger;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiKnowledgeService extends IService<AiKnowledge> {

    Result search(BigInteger id, String keyword);

}

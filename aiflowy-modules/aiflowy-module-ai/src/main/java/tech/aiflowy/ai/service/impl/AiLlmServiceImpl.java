package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.mapper.AiLlmMapper;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.domain.Result;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiLlmServiceImpl extends ServiceImpl<AiLlmMapper, AiLlm> implements AiLlmService {

    @Autowired
    AiLlmMapper aiLlmMapper;

    @Override
    public Result addAiLlm(AiLlm entity) {
        int insert = aiLlmMapper.insert(entity);
        if (insert <= 0){
            return Result.fail();
        }
        return Result.success();
    }
}

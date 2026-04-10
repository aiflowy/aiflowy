package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.VectorDatabase;
import tech.aiflowy.common.domain.Result;

import java.math.BigInteger;

/**
 * 向量数据库表 服务层。
 *
 * @author 12076
 * @since 2026-02-12
 */
public interface VectorDatabaseService extends IService<VectorDatabase> {

    Result<?> saveConfig(VectorDatabase entity);

    Result<?> verifyVectorConfig(BigInteger vectorEmbedModelId, VectorDatabase entity, String collectionName, Integer dimensions);
}

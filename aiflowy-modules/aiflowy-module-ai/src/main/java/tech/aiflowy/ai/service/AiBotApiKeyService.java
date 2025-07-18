package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.AiBotApiKey;

import java.math.BigInteger;

/**
 * bot apiKey 表 服务层。
 *
 * @author ArkLight
 * @since 2025-07-18
 */
public interface AiBotApiKeyService extends IService<AiBotApiKey> {

    /**
     * 根据 botId 生成 apiKey 并返回
     */
    String generateApiKeyByBotId(BigInteger botId);


    /**
     * 解密 apiKey
     */
     BigInteger decryptApiKey(String apiKey);
}

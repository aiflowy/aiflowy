package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.AiPlugins;
import tech.aiflowy.ai.mapper.AiPluginsMapper;
import tech.aiflowy.ai.service.AiPluginsService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 插件 服务层实现。
 *
 * @author ArkLight
 * @since 2025-04-01
 */
@Service
public class AiPluginsServiceImpl extends ServiceImpl<AiPluginsMapper, AiPlugins>  implements AiPluginsService {

}

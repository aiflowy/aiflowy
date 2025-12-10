package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.AiBotKnowledge;
import tech.aiflowy.ai.entity.AiBotPluginTool;
import tech.aiflowy.ai.entity.AiBotPlugins;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.mapper.AiBotPluginsMapper;
import tech.aiflowy.ai.mapper.AiPluginMapper;
import tech.aiflowy.ai.service.AiBotPluginsService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2025-04-07
 */
@Service
public class AiBotPluginsServiceImpl extends ServiceImpl<AiBotPluginsMapper, AiBotPlugins>  implements AiBotPluginsService{

    @Resource
    private AiBotPluginsMapper aiBotPluginsMapper;

    @Resource
    private AiPluginMapper aiPluginMapper;

    @Override
    public List<AiPlugin> getList(String botId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_tool_id").where("bot_id = ?", botId);
        List<BigInteger> pluginIds = aiBotPluginsMapper.selectListByQueryAs(queryWrapper, BigInteger.class);
        List<AiPlugin> aiPlugins = aiPluginMapper.selectListByIds(pluginIds);
        return aiPlugins;
    }

    @Override
    public boolean doRemove(String botId, String pluginToolId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("id")
                .from("tb_ai_bot_plugins")
                .where("bot_id = ?", botId)
                .where("plugin_tool_id = ?", pluginToolId);
        BigInteger id = aiBotPluginsMapper.selectOneByQueryAs(queryWrapper, BigInteger.class);
        int delete = aiBotPluginsMapper.deleteById(id);
        return delete > 0;
    }

    @Override
    public List<BigInteger> getBotPluginToolIds(String botId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_tool_id").where("bot_id = ?", botId);
        return aiBotPluginsMapper.selectListByQueryAs(queryWrapper, BigInteger.class);
    }

    @Override
    public void saveBotAndPluginTool(BigInteger botId, BigInteger[] pluginToolIds) {
        this.remove(QueryWrapper.create().eq(AiBotPlugins::getBotId, botId));
        List<AiBotPlugins> list = new ArrayList<>(pluginToolIds.length);
        for (BigInteger pluginToolId : pluginToolIds) {
            AiBotPlugins aiBotPluginTool = new AiBotPlugins();
            aiBotPluginTool.setBotId(botId);
            aiBotPluginTool.setPluginToolId(pluginToolId);
            list.add(aiBotPluginTool);
        }
        this.saveBatch(list);
    }
}

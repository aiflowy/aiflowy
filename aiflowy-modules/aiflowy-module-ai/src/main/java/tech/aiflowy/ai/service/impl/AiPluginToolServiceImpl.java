package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.ai.mapper.AiPluginMapper;
import tech.aiflowy.ai.mapper.AiPluginToolMapper;
import tech.aiflowy.ai.service.AiPluginToolService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  服务层实现。
 *
 * @author WangGangqiang
 * @since 2025-04-27
 */
@Service
public class AiPluginToolServiceImpl extends ServiceImpl<AiPluginToolMapper, AiPluginTool>  implements AiPluginToolService{

    @Resource
    private AiPluginToolMapper aiPluginToolMapper;

    @Resource
    private AiPluginMapper aiPluginMapper;

    @Override
    public Result savePluginTool(AiPluginTool aiPluginTool) {
        aiPluginTool.setCreated(new Date());
        aiPluginTool.setRequestMethod("Post");
        int insert = aiPluginToolMapper.insert(aiPluginTool);
        if (insert <= 0){
            return Result.fail(1, "插入失败");
        }
        return Result.success();
    }

    @Override
    public Result searchPlugin(String aiPluginToolId) {
        //查询当前插件工具
        QueryWrapper queryAiPluginToolWrapper = QueryWrapper.create()
                .select("*")
                .from("tb_ai_plugin_tool")
                .where("id = ? ", aiPluginToolId);
        AiPluginTool aiPluginTool = aiPluginToolMapper.selectOneByQuery(queryAiPluginToolWrapper);
        // 查询当前的插件信息
        QueryWrapper queryAiPluginWrapper = QueryWrapper.create()
                .select("*")
                .from("tb_ai_plugin");
        AiPlugin aiPlugin = aiPluginMapper.selectOneByQuery(queryAiPluginWrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("data", aiPluginTool);
        result.put("aiPlugin", aiPlugin);
        return Result.success(result);
    }
}

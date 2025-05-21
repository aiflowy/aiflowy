package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.mapper.AiPluginMapper;
import tech.aiflowy.ai.service.AiPluginService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *  服务层实现。
 *
 * @author WangGangqiang
 * @since 2025-04-25
 */
@Service
public class AiPluginServiceImpl extends ServiceImpl<AiPluginMapper, AiPlugin>  implements AiPluginService {

    @Resource
    AiPluginMapper aiPluginMapper;

    @Override
    public Result savePlugin(AiPlugin aiPlugin) {
        System.out.println("aaa");
        aiPlugin.setCreated(new Date());
        int insert = aiPluginMapper.insert(aiPlugin);
        if (insert <= 0){
            return Result.fail(1, "保存失败");
        }
        return Result.success();
    }

    @Override
    public Result removePlugin(String id) {
        int remove =  aiPluginMapper.deleteById(id);
        if (remove <= 0){
            return Result.fail(1, "删除失败");

        }
        return Result.success();

    }

    @Override
    public Result updatePlugin(AiPlugin aiPlugin) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("id")
                .from("tb_ai_plugin")
                .where("id = ?", aiPlugin.getId());
        int update = aiPluginMapper.updateByQuery(aiPlugin, queryWrapper);
        if (update <= 0){
            return Result.fail(1, "修改失败");

        }
        return Result.success();
    }

    @Override
    public Result getList() {
        QueryWrapper queryWrapper = QueryWrapper.create().select("id, name, description, icon")
                .from("tb_ai_plugin");
        List<AiPlugin> aiPlugins = aiPluginMapper.selectListByQueryAs(queryWrapper, AiPlugin.class);
        return Result.success(aiPlugins);
    }

    @Override
    public void pageByCategory(Long pageNumber, Long pageSize, int category) {
    }


}

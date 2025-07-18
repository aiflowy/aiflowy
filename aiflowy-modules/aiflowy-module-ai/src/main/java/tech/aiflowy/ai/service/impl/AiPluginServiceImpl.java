package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.entity.AiPluginCategoryRelation;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.ai.mapper.AiPluginCategoryRelationMapper;
import tech.aiflowy.ai.mapper.AiPluginMapper;
import tech.aiflowy.ai.service.AiBotPluginsService;
import tech.aiflowy.ai.service.AiPluginService;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.service.AiPluginToolService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务层实现。
 *
 * @author WangGangqiang
 * @since 2025-04-25
 */
@Service
public class AiPluginServiceImpl extends ServiceImpl<AiPluginMapper, AiPlugin> implements AiPluginService {

    private static final Logger log = LoggerFactory.getLogger(AiPluginServiceImpl.class);

    @Resource
    AiPluginMapper aiPluginMapper;

    @Resource
    AiPluginCategoryRelationMapper aiPluginCategoryRelationMapper;

    @Resource
    private AiBotPluginsService aiBotPluginsService;

    @Resource
    private AiPluginToolService aiPluginToolService;

    @Override
    public Result savePlugin(AiPlugin aiPlugin) {
        aiPlugin.setCreated(new Date());
        int insert = aiPluginMapper.insert(aiPlugin);
        if (insert <= 0) {
            return Result.fail(1, "保存失败");
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result removePlugin(String id) {

        List<AiPluginTool> aiPluginTools = aiPluginToolService.getByPluginId(id);
        List<BigInteger> pluginToolIds = new ArrayList<>();

        if (aiPluginTools != null && !aiPluginTools.isEmpty()) {

            pluginToolIds = aiPluginTools.stream().map(AiPluginTool::getId).collect(Collectors.toList());

            QueryWrapper queryWrapper = QueryWrapper.create();
            queryWrapper.in("plugin_tool_id", pluginToolIds);

            boolean exists = aiBotPluginsService.exists(queryWrapper);
            if (exists){
                return Result.fail(1, "插件中有工具还关联着bot，请先取消关联！");
            }

        }

        if ( !pluginToolIds.isEmpty()) {
           boolean result =  aiPluginToolService.removeByIds(pluginToolIds);
           if (!result){
               log.error("删除插件工具表结果为0");
               throw new BusinessException("删除失败，请稍后重试！");
           }
        }


        int remove = aiPluginMapper.deleteById(id);
        if (remove <= 0) {
            log.error("删除插件结果为0");
            throw new BusinessException("删除失败，请稍后重试！");
        }

        return Result.success();

    }

    @Override
    public Result updatePlugin(AiPlugin aiPlugin) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("id")
                .from("tb_ai_plugin")
                .where("id = ?", aiPlugin.getId());
        int update = aiPluginMapper.updateByQuery(aiPlugin, queryWrapper);
        if (update <= 0) {
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
    public Result pageByCategory(Long pageNumber, Long pageSize, int category) {
        // 通过分类查询插件
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_id")
                .from("tb_ai_plugin_category_relation")
                .where("category_id = ? ", category);
        // 分页查询该分类中的插件
        Page<BigInteger> pagePluginIds = aiPluginCategoryRelationMapper.paginateAs(new Page<>(pageNumber, pageSize), queryWrapper, BigInteger.class);
        Page<AiPluginCategoryRelation> paginateCategories = aiPluginCategoryRelationMapper.paginate(pageNumber, pageSize, queryWrapper);
        List<AiPlugin> aiPlugins = Collections.emptyList();
        if (paginateCategories.getRecords().isEmpty()) {
            return Result.success(new Page<>(aiPlugins, pageNumber, pageSize, paginateCategories.getTotalRow()));
        }
        List<BigInteger> pluginIds = pagePluginIds.getRecords();
        // 查询对应的插件信息
        QueryWrapper queryPluginWrapper = QueryWrapper.create().select("*")
                .from("tb_ai_plugin")
                .in("id", pluginIds);
        aiPlugins = aiPluginMapper.selectListByQuery(queryPluginWrapper);
        Page<AiPlugin> aiPluginPage = new Page<>(aiPlugins, pageNumber, pageSize, paginateCategories.getTotalRow());
        return Result.success(aiPluginPage);
    }


}

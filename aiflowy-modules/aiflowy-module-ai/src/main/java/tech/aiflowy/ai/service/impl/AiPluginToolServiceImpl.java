package tech.aiflowy.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.eclipse.jetty.util.ajax.JSON;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.ai.mapper.AiPluginMapper;
import tech.aiflowy.ai.mapper.AiPluginToolMapper;
import tech.aiflowy.ai.service.AiPluginToolService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.util.StringUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Result updatePlugin(AiPluginTool aiPluginTool) {
        String inputData = null;
        String outputData = null;
        if (!StrUtil.isEmpty(aiPluginTool.getInputData())){
            inputData = switchParams(aiPluginTool.getInputData());
        }
        if (!StrUtil.isEmpty(inputData)){
            aiPluginTool.setInputData(inputData);
        }
        if (!StrUtil.isEmpty(aiPluginTool.getOutputData())){
            outputData = switchParams(aiPluginTool.getOutputData());
        }
        if (!StrUtil.isEmpty(outputData)){
            aiPluginTool.setOutputData(outputData);
        }

        int update = aiPluginToolMapper.update(aiPluginTool);
        if (update <= 0){
            return Result.fail(1,"修改失败");
        }
        return Result.success();
    }

    public static String switchParams(String paramString){
        ObjectMapper mapper = new ObjectMapper();
        // 1. 将JSON解析为Map<String, Parameter>
        Map<String, Map<String, Object>> map = null;
        try {
            map = mapper.readValue(
                    paramString,
                    new TypeReference<Map<String, Map<String, Object>>>(){}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 2. 转换为目标结构
        List<Map<String, Object>> result = map.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> param = entry.getValue();
                    param.put("key", entry.getKey()); // 添加key字段
                    return param;
                })
                .collect(Collectors.toList());
        return JSON.toString(result);
    }
}

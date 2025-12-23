package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.PluginItem;
import tech.aiflowy.ai.service.BotPluginService;
import tech.aiflowy.ai.service.PluginItemService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 *  控制层。
 *
 * @author WangGangqiang
 * @since 2025-04-27
 */
@RestController
@RequestMapping("/api/v1/aiPluginTool")
@UsePermission(moduleName = "/api/v1/aiPlugin")
public class PluginItemController extends BaseCurdController<PluginItemService, PluginItem> {
    public PluginItemController(PluginItemService service) {
        super(service);
    }

    @Resource
    private PluginItemService pluginItemService;

    @Resource
    private BotPluginService botPluginService;

    @PostMapping("/tool/save")
    @SaCheckPermission("/api/v1/aiPlugin/save")
    public Result<Boolean> savePlugin(@JsonBody PluginItem pluginItem){

        return Result.ok(pluginItemService.savePluginTool(pluginItem));
    }

    // 插件工具修改页面查询
    @PostMapping("/tool/search")
    @SaCheckPermission("/api/v1/aiPlugin/query")
    public Result<?> searchPlugin(@JsonBody(value = "aiPluginToolId", required = true) BigInteger aiPluginToolId){
        return pluginItemService.searchPlugin(aiPluginToolId);
    }

    @PostMapping("/toolsList")
    @SaCheckPermission("/api/v1/aiPlugin/query")
    public Result<List<PluginItem>> searchPluginToolByPluginId(@JsonBody(value = "pluginId", required = true) BigInteger pluginId,
                                                               @JsonBody(value = "botId", required = false) BigInteger botId){
        return Result.ok(pluginItemService.searchPluginToolByPluginId(pluginId, botId));
    }

    @PostMapping("/tool/update")
    @SaCheckPermission("/api/v1/aiPlugin/save")
    public Result<Boolean> updatePlugin(@JsonBody PluginItem pluginItem){
        return Result.ok(pluginItemService.updatePlugin(pluginItem));
    }

    @PostMapping("/tool/list")
    @SaCheckPermission("/api/v1/aiPlugin/query")
    public Result<List<PluginItem>> getPluginToolList(@JsonBody(value = "botId", required = true) BigInteger botId){
        return Result.ok(pluginItemService.getPluginToolList(botId));
    }

    @GetMapping("/getTinyFlowData")
    @SaCheckPermission("/api/v1/aiPlugin/query")
    public Result<?> getTinyFlowData(BigInteger id) {
        JSONObject nodeData = new JSONObject();
        PluginItem record = pluginItemService.getById(id);
        if (record == null) {
            return Result.ok(nodeData);
        }
        nodeData.put("pluginId", record.getId().toString());
        nodeData.put("pluginName", record.getName());

        JSONArray parameters = new JSONArray();
        JSONArray outputDefs = new JSONArray();
        String inputData = record.getInputData();
        if (StrUtil.isNotEmpty(inputData)) {
            JSONArray array = JSON.parseArray(inputData);
            handleArray(array);
            parameters = array;
        }
        String outputData = record.getOutputData();
        if (StrUtil.isNotEmpty(outputData)) {
            JSONArray array = JSON.parseArray(outputData);
            handleArray(array);
            outputDefs = array;
        }
        nodeData.put("parameters", parameters);
        nodeData.put("outputDefs", outputDefs);
        return Result.ok(nodeData);
    }

    /**
     * 插件试运行接口
     * @return
     */
    @PostMapping("/test")
    public Result<?> pluginToolTest(@JsonBody(value = "inputData", required = true) String inputData,
                                 @JsonBody(value = "pluginToolId", required = true) BigInteger pluginToolId){
        return pluginItemService.pluginToolTest(inputData, pluginToolId);
    }

    private void handleArray(JSONArray array) {
        for (Object o : array) {
            JSONObject obj = (JSONObject) o;
            obj.put("id", IdUtil.simpleUUID());
            obj.put("nameDisabled", true);
            obj.put("dataTypeDisabled", true);
            obj.put("deleteDisabled", true);
            obj.put("addChildDisabled", true);
            JSONArray children = obj.getJSONArray("children");
            if (children != null) {
                handleArray(children);
            }
        }
    }

    @Override
    protected Result<?> onRemoveBefore(Collection<Serializable> ids) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in("plugin_tool_id", ids);

        boolean exists = botPluginService.exists(queryWrapper);
        if (exists){
            return Result.fail(1, "此工具还关联着bot，请先取消关联！");
        }

        return null;
    }
}

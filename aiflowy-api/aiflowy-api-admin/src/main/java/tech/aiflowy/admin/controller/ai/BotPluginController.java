package tech.aiflowy.admin.controller.ai;

import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.ai.entity.Plugin;
import tech.aiflowy.ai.entity.BotPlugin;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.ai.service.BotPluginService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

/**
 *  控制层。
 *
 * @author michael
 * @since 2025-04-07
 */
@RestController
@RequestMapping("/api/v1/aiBotPlugins")
@UsePermission(moduleName = "/api/v1/aiBot")
public class BotPluginController extends BaseCurdController<BotPluginService, BotPlugin> {

    public BotPluginController(BotPluginService service) {
        super(service);
    }

    @Resource
    private BotPluginService botPluginService;

    @GetMapping("list")
    public Result<List<BotPlugin>> list(BotPlugin entity, Boolean asTree, String sortKey, String sortType){

        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));

        List<BotPlugin> botPlugins = service.getMapper().selectListWithRelationsByQuery(queryWrapper);

        List<BotPlugin> list = Tree.tryToTree(botPlugins, asTree);

         return Result.ok(list);
    }

    @PostMapping("/getList")
    public Result<List<Plugin>> getList(@JsonBody(value = "botId", required = true) String botId){
        return Result.ok(botPluginService.getList(botId));
    }

    @PostMapping("/getBotPluginToolIds")
    public Result<List<BigInteger>> getBotPluginToolIds(@JsonBody(value = "botId", required = true) String botId){
        return Result.ok(botPluginService.getBotPluginToolIds(botId));
    }

    @PostMapping("/doRemove")
    public Result<Boolean> doRemove(@JsonBody(value = "botId", required = true) String botId,
                           @JsonBody(value = "pluginToolId", required = true) String pluginToolId){
        return Result.ok(botPluginService.doRemove(botId, pluginToolId));
    }

    @PostMapping("updateBotPluginToolIds")
    public Result<?> save(@JsonBody("botId") BigInteger botId, @JsonBody("pluginToolIds") BigInteger [] pluginToolIds) {
        service.saveBotAndPluginTool(botId, pluginToolIds);
        return Result.ok();
    }

}

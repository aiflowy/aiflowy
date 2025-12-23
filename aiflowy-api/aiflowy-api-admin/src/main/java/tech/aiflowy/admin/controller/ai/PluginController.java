package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.ai.entity.Plugin;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.ai.service.PluginService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *  控制层。
 *
 * @author Administrator
 * @since 2025-04-25
 */
@RestController
@RequestMapping("/api/v1/aiPlugin")
public class PluginController extends BaseCurdController<PluginService, Plugin> {
    public PluginController(PluginService service) {
        super(service);
    }

    @Resource
    PluginService pluginService;

    @Override
    protected Result<?> onSaveOrUpdateBefore(Plugin entity, boolean isSave) {
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @PostMapping("/plugin/save")
    @SaCheckPermission("/api/v1/aiPlugin/save")
    public Result<Boolean> savePlugin(@JsonBody Plugin plugin){

        return Result.ok(pluginService.savePlugin(plugin));
    }

    @PostMapping("/plugin/remove")
    @SaCheckPermission("/api/v1/aiPlugin/remove")
    public Result<Boolean> removePlugin(@JsonBody(value = "id", required = true) String id){

        return Result.ok(pluginService.removePlugin(id));
    }

    @PostMapping("/plugin/update")
    @SaCheckPermission("/api/v1/aiPlugin/save")
    public Result<Boolean> updatePlugin(@JsonBody Plugin plugin){

        return Result.ok(pluginService.updatePlugin(plugin));
    }

    @PostMapping("/getList")
    @SaCheckPermission("/api/v1/aiPlugin/query")
    public Result<List<Plugin>> getList(){
        return Result.ok(pluginService.getList());
    }

    @GetMapping("/pageByCategory")
    @SaCheckPermission("/api/v1/aiPlugin/query")
    public Result<Page<Plugin>> pageByCategory(HttpServletRequest request, String sortKey, String sortType, Long pageNumber, Long pageSize, int category) {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10L;
        }
        if (category == 0){
            QueryWrapper queryWrapper = buildQueryWrapper(request);
            queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
            return Result.ok(queryPage(new Page<>(pageNumber, pageSize), queryWrapper));
        } else {
            return pluginService.pageByCategory(pageNumber, pageSize, category);
        }
    }

    @Override
    protected Page<Plugin> queryPage(Page<Plugin> page, QueryWrapper queryWrapper) {
        return service.getMapper().paginateWithRelations(page, queryWrapper);
    }
}

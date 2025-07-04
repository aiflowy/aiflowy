package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.service.AiPluginService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *  控制层。
 *
 * @author Administrator
 * @since 2025-04-25
 */
@RestController
@RequestMapping("/api/v1/aiPlugin")
public class AiPluginController extends BaseCurdController<AiPluginService, AiPlugin> {
    public AiPluginController(AiPluginService service) {
        super(service);
    }

    @Resource
    AiPluginService aiPluginService;

    @Override
    protected Result onSaveOrUpdateBefore(AiPlugin entity, boolean isSave) {
//        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
//        commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
//
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @PostMapping("/plugin/save")
    @SaCheckPermission("/api/v1/aiPlugin/save")
    public Result savePlugin(@JsonBody AiPlugin aiPlugin){

        return aiPluginService.savePlugin(aiPlugin);
    }

    @PostMapping("/plugin/remove")
    @SaCheckPermission("/api/v1/aiPlugin/remove")
    public Result removePlugin(@JsonBody(value = "id", required = true) String id){

        return aiPluginService.removePlugin(id);
    }

    @PostMapping("/plugin/update")
    @SaCheckPermission("/api/v1/aiPlugin/save")
    public Result updatePlugin(@JsonBody AiPlugin aiPlugin){

        return aiPluginService.updatePlugin(aiPlugin);
    }

    @PostMapping("/getList")
    @SaCheckPermission("/api/v1/aiPlugin/query")
    public Result getList(){
        return aiPluginService.getList();
    }

    @GetMapping("/pageByCategory")
    @SaCheckPermission("/api/v1/aiPlugin/query")
    public Result pageByCategory(HttpServletRequest request, String sortKey, String sortType, Long pageNumber, Long pageSize, int category) {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10L;
        }
        if (category == 0){
            QueryWrapper queryWrapper = buildQueryWrapper(request);
            queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
            return Result.success(queryPage(new Page<>(pageNumber, pageSize), queryWrapper));
        } else {
            return aiPluginService.pageByCategory(pageNumber, pageSize, category);
        }
    }

    @Override
    protected Page<AiPlugin> queryPage(Page<AiPlugin> page, QueryWrapper queryWrapper) {
        return service.getMapper().paginateWithRelations(page, queryWrapper);
    }
}

package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiPluginCategories;
import tech.aiflowy.ai.service.AiPluginCategoriesService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;

import javax.annotation.Resource;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2025-05-21
 */
@RestController
@RequestMapping("/api/v1/aiPluginCategories")
@UsePermission(moduleName = "/api/v1/aiPlugin")
public class AiPluginCategoriesController extends BaseCurdController<AiPluginCategoriesService, AiPluginCategories> {
    public AiPluginCategoriesController(AiPluginCategoriesService service) {
        super(service);
    }

    @Resource
    private AiPluginCategoriesService aiPluginCategoriesService;

    @GetMapping("/doRemoveCategory")
    @SaCheckPermission("/api/v1/aiPlugin/remove")
    public Result doRemoveCategory(@RequestParam("id") Integer id){

        return aiPluginCategoriesService.doRemoveCategory(id);
    }
}
package tech.aiflowy.ai.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiPluginCategories;
import tech.aiflowy.ai.service.AiPluginCategoriesService;
import tech.aiflowy.common.web.controller.BaseCurdController;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2025-05-21
 */
@RestController
@RequestMapping("/api/v1/aiPluginCategories")
public class AiPluginCategoriesController extends BaseCurdController<AiPluginCategoriesService, AiPluginCategories> {
    public AiPluginCategoriesController(AiPluginCategoriesService service) {
        super(service);
    }

}
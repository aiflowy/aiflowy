package tech.aiflowy.ai.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiPluginCategoryRelation;
import tech.aiflowy.ai.service.AiPluginCategoryRelationService;
import tech.aiflowy.common.web.controller.BaseCurdController;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2025-05-21
 */
@RestController
@RequestMapping("/api/v1/aiPluginCategoryRelation")
public class AiPluginCategoryRelationController extends BaseCurdController<AiPluginCategoryRelationService, AiPluginCategoryRelation> {
    public AiPluginCategoryRelationController(AiPluginCategoryRelationService service) {
        super(service);
    }

}
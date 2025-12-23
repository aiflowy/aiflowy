package tech.aiflowy.admin.controller.ai;

import tech.aiflowy.ai.entity.ModelProvider;
import tech.aiflowy.ai.service.ModelProviderService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  控制层。
 *
 * @author 12076
 * @since 2025-12-16
 */
@RestController
@RequestMapping("/api/v1/aiLlmProvider")
@UsePermission(moduleName = "/api/v1/aiLlm")
public class ModelProviderController extends BaseCurdController<ModelProviderService, ModelProvider> {
    public ModelProviderController(ModelProviderService service) {
        super(service);
    }
}
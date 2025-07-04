package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import tech.aiflowy.ai.entity.AiLlmBrand;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiLlmBrand")
@UsePermission(moduleName = "/api/v1/aiLlm")
public class AiLlmBrandController extends BaseController {

    @RequestMapping("list")
    @SaCheckPermission("/api/v1/aiLlm/query")
    public Result list(){
        return Result.success(AiLlmBrand.fromJsonConfig());
    }
}
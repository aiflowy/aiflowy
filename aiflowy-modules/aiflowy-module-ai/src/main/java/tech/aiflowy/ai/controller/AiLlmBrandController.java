package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiLlmBrand;
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
public class AiLlmBrandController extends BaseController {

    @RequestMapping("list")
    public Result list(){
        return Result.success(AiLlmBrand.fromJsonConfig());
    }
}
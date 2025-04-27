package tech.aiflowy.ai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.ai.service.AiPluginToolService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;

/**
 *  控制层。
 *
 * @author WangGangqiang
 * @since 2025-04-27
 */
@RestController
@RequestMapping("/api/v1/aiPluginTool")
public class AiPluginToolController extends BaseCurdController<AiPluginToolService, AiPluginTool> {
    public AiPluginToolController(AiPluginToolService service) {
        super(service);
    }

    @Resource
    private AiPluginToolService aiPluginToolService;

    @PostMapping("/tool/save")
    public Result savePlugin(@JsonBody AiPluginTool aiPluginTool){

        return aiPluginToolService.savePluginTool(aiPluginTool);
    }

    // 插件工具修改页面查询
    @PostMapping("/tool/search")
    public Result searchPlugin(@JsonBody(value = "aiPluginToolId", required = true) String aiPluginToolId){
        return aiPluginToolService.searchPlugin(aiPluginToolId);
    }

}
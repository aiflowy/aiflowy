package tech.aiflowy.ai.controller;

import cn.hutool.core.io.IoUtil;
import dev.tinyflow.core.Tinyflow;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.ai.utils.TinyFlowConfigService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import com.agentsflex.core.chain.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiWorkflow")
public class AiWorkflowController extends BaseCurdController<AiWorkflowService, AiWorkflow> {
    private final AiLlmService aiLlmService;

    @Resource
    private AiKnowledgeService aiKnowledgeService;
    @Resource
    private TinyFlowConfigService tinyFlowConfigService;

    public AiWorkflowController(AiWorkflowService service, AiLlmService aiLlmService) {
        super(service);
        this.aiLlmService = aiLlmService;
    }

    @PostMapping("/importWorkFlow")
    public Result importWorkFlow(AiWorkflow workflow, MultipartFile jsonFile) throws Exception {
        InputStream is = jsonFile.getInputStream();
        String content = IoUtil.read(is, StandardCharsets.UTF_8);
        workflow.setContent(content);
        save(workflow);
        return Result.success();
    }

    @GetMapping("getRunningParameters")
    public Result getRunningParameters(@RequestParam BigInteger id) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "can not find the workflow by id: " + id);
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        if (tinyflow == null) {
            return Result.fail(2, "workflow content is empty! ");
        }

        Chain chain = tinyflow.toChain();
        if (chain == null) {
            return Result.fail(2, "节点配置错误，请检查! ");
        }
        List<Parameter> chainParameters = chain.getParameters();
        return Result.success("parameters", chainParameters)
                .set("title",  workflow.getTitle())
                .set("description",  workflow.getDescription())
                .set("icon", workflow.getIcon());
    }

    @PostMapping("tryRunning")
    public Result tryRunning(@JsonBody(value = "id", required = true) BigInteger id, @JsonBody("variables") Map<String, Object> variables) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "can not find the workflow by id: " + id);
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        Chain chain = tinyflow.toChain();
        chain.addEventListener(new ChainEventListener() {
            @Override
            public void onEvent(ChainEvent event, Chain chain) {
                //System.out.println("onEvent : " + event);
            }
        });

        chain.addOutputListener(new ChainOutputListener() {
            @Override
            public void onOutput(Chain chain, ChainNode node, Object outputMessage) {
                //System.out.println("output : " + node.getId() + " : " + outputMessage);
            }
        });

        Map<String, Object> result = chain.executeForResult(variables);

        return Result.success("result", result).set("message", chain.getMessage());
    }
}
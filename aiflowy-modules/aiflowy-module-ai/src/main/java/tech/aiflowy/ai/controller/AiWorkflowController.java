package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import com.agentsflex.core.chain.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
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
    public AiWorkflowController(AiWorkflowService service) {
        super(service);
    }


    @GetMapping("getRunningParameters")
    public Result getRunningParameters(@RequestParam BigInteger id) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "can not find the workflow by id: " + id);
        }

        Chain chain = workflow.toTinyflow().toChain();
        List<Parameter> chainParameters = chain.getParameters();
        return Result.success("parameters", chainParameters);
    }

    @PostMapping("tryRunning")
    public Result tryRunning(@JsonBody(value = "id", required = true) BigInteger id, @JsonBody("variables") Map<String, Object> variables) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "can not find the workflow by id: " + id);
        }

        Chain chain = workflow.toTinyflow().toChain();
        chain.addEventListener(new ChainEventListener() {
            @Override
            public void onEvent(ChainEvent event, Chain chain) {
                System.out.println("onEvent : " + event);
            }
        });

        chain.addOutputListener(new ChainOutputListener() {
            @Override
            public void onOutput(Chain chain, ChainNode node, Object outputMessage) {
                System.out.println("output : " + node.getId() + " : " + outputMessage);
            }
        });

        Map<String, Object> result = chain.executeForResult(variables);

        return Result.success("result", result).set("message", chain.getMessage());
    }
}
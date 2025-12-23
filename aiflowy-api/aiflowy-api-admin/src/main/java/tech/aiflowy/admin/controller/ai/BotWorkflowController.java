package tech.aiflowy.admin.controller.ai;

import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.ai.entity.BotWorkflow;
import tech.aiflowy.ai.service.BotWorkflowService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.web.controller.BaseCurdController;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.math.BigInteger;
import java.util.List;

/**
 *  控制层。
 *
 * @author michael
 * @since 2024-08-28
 */
@RestController
@RequestMapping("/api/v1/aiBotWorkflow")
@UsePermission(moduleName = "/api/v1/aiBot")
public class BotWorkflowController extends BaseCurdController<BotWorkflowService, BotWorkflow> {
    public BotWorkflowController(BotWorkflowService service) {
        super(service);
    }

    @GetMapping("list")
    @Override
    public Result<List<BotWorkflow>> list(BotWorkflow entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<BotWorkflow> botWorkflows = service.getMapper().selectListWithRelationsByQuery(queryWrapper);
        List<BotWorkflow> list = Tree.tryToTree(botWorkflows, asTree);
        return Result.ok(list);
    }

    @PostMapping("updateBotWorkflowIds")
    public Result<?> save(@JsonBody("botId") BigInteger botId, @JsonBody("workflowIds") BigInteger [] workflowIds) {
        service.saveBotAndWorkflowTool(botId, workflowIds);
        return Result.ok();
    }
}
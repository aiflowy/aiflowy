package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiBotWorkflow;
import tech.aiflowy.ai.service.AiBotWorkflowService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.web.controller.BaseCurdController;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  控制层。
 *
 * @author michael
 * @since 2024-08-28
 */
@RestController
@RequestMapping("/api/v1/aiBotWorkflow")
public class AiBotWorkflowController extends BaseCurdController<AiBotWorkflowService, AiBotWorkflow> {
    public AiBotWorkflowController(AiBotWorkflowService service) {
        super(service);
    }

    @GetMapping("list")
    @Override
    public Result list(AiBotWorkflow entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<AiBotWorkflow> aiBotWorkflows = service.getMapper().selectListWithRelationsByQuery(queryWrapper);
        List<AiBotWorkflow> list = Tree.tryToTree(aiBotWorkflows, asTree);
        return Result.success(list);
    }
}
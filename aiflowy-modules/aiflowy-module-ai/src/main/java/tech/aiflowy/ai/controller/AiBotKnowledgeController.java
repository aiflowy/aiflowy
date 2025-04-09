package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiBotKnowledge;
import tech.aiflowy.ai.service.AiBotKnowledgeService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-28
 */
@RestController
@RequestMapping("/api/v1/aiBotKnowledge")
public class AiBotKnowledgeController extends BaseCurdController<AiBotKnowledgeService, AiBotKnowledge> {
    public AiBotKnowledgeController(AiBotKnowledgeService service) {
        super(service);
    }

    @GetMapping("list")
    @Override
    public Result list(AiBotKnowledge entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<AiBotKnowledge> aiBotKnowledges = service.getMapper().selectListWithRelationsByQuery(queryWrapper);
        return Result.success(aiBotKnowledges);
    }
}
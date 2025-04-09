package tech.aiflowy.ai.controller;

import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.ai.entity.AiBotPlugins;
import tech.aiflowy.ai.service.AiBotPluginsService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  控制层。
 *
 * @author michael
 * @since 2025-04-07
 */
@RestController
@RequestMapping("/api/v1/aiBotPlugins")
public class AiBotPluginsController extends BaseCurdController<AiBotPluginsService, AiBotPlugins> {

    public AiBotPluginsController(AiBotPluginsService service) {
        super(service);
    }

    @GetMapping("list")
    public Result list(AiBotPlugins entity, Boolean asTree, String sortKey, String sortType){

        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));

        List<AiBotPlugins> aiBotPlugins = service.getMapper().selectListWithRelationsByQuery(queryWrapper);

        List<AiBotPlugins> list = Tree.tryToTree(aiBotPlugins, asTree);

        return Result.success(list);
    }

}
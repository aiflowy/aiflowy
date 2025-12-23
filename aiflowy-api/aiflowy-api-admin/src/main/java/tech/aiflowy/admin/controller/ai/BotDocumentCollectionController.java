package tech.aiflowy.admin.controller.ai;

import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.ai.entity.BotDocumentCollection;
import tech.aiflowy.ai.service.BotDocumentCollectionService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.math.BigInteger;
import java.util.List;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-28
 */
@RestController
@RequestMapping("/api/v1/aiBotKnowledge")
@UsePermission(moduleName = "/api/v1/aiBot")
public class BotDocumentCollectionController extends BaseCurdController<BotDocumentCollectionService, BotDocumentCollection> {
    public BotDocumentCollectionController(BotDocumentCollectionService service) {
        super(service);
    }

    @GetMapping("list")
    @Override
    public Result<List<BotDocumentCollection>> list(BotDocumentCollection entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<BotDocumentCollection> botDocumentCollections = service.getMapper().selectListWithRelationsByQuery(queryWrapper);
        return Result.ok(botDocumentCollections);
    }

    @PostMapping("updateBotKnowledgeIds")
    public Result<?> save(@JsonBody("botId") BigInteger botId, @JsonBody("knowledgeIds") BigInteger [] knowledgeIds) {
         service.saveBotAndKnowledge(botId, knowledgeIds);
        return Result.ok();
    }
}
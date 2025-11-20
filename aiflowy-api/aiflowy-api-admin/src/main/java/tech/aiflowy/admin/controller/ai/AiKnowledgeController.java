package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.agentsflex.core.document.Document;
import com.mybatisflex.core.query.QueryWrapper;
import dev.tinyflow.core.knowledge.Knowledge;
import org.springframework.util.StringUtils;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.service.AiBotKnowledgeService;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiKnowledge")
public class AiKnowledgeController extends BaseCurdController<AiKnowledgeService, AiKnowledge> {

    private final AiDocumentChunkService chunkService;
    private final AiLlmService llmService;

    @Resource
    private AiBotKnowledgeService aiBotKnowledgeService;

    public AiKnowledgeController(AiKnowledgeService service, AiDocumentChunkService chunkService, AiLlmService llmService) {
        super(service);
        this.chunkService = chunkService;
        this.llmService = llmService;
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(AiKnowledge entity, boolean isSave) {

        String alias = entity.getAlias();

        if (StringUtils.hasLength(alias)){
            AiKnowledge knowledge = service.getByAlias(alias);


            if (knowledge != null && isSave){
                throw new BusinessException("别名已存在！");
            }

            if (knowledge != null && knowledge.getId().compareTo(entity.getId()) != 0){
                throw new BusinessException("别名已存在！");
            }

        }


        if (isSave){
            Map<String, Object> options =  new HashMap<>();
            if (entity.getSearchEngineEnable() == null){
                entity.setSearchEngineEnable(false);
            }
            options.put("canUpdateEmbedding", true);
            entity.setOptions(options);
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @GetMapping("search")
    @SaCheckPermission("/api/v1/aiKnowledge/query")
    public Result<List<Document>> search(@RequestParam BigInteger id, @RequestParam String keyword) {
        return Result.ok(service.search(id, keyword));
    }


    @Override
    protected Result<Void> onRemoveBefore(Collection<Serializable> ids) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in("knowledge_id", ids);

        boolean exists = aiBotKnowledgeService.exists(queryWrapper);
        if (exists){
            throw new BusinessException("此知识库还关联着bot，请先取消关联！");
        }

        return null;
    }

    @Override
    public Result<AiKnowledge> detail(String id) {
        return Result.ok(service.getDetail(id));
    }
}

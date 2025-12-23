package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.agentsflex.core.document.Document;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.util.StringUtils;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.service.BotDocumentCollectionService;
import tech.aiflowy.ai.service.DocumentChunkService;
import tech.aiflowy.ai.service.DocumentCollectionService;
import tech.aiflowy.ai.service.ModelService;
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
public class DocumentCollectionController extends BaseCurdController<DocumentCollectionService, DocumentCollection> {

    private final DocumentChunkService chunkService;
    private final ModelService llmService;

    @Resource
    private BotDocumentCollectionService botDocumentCollectionService;

    public DocumentCollectionController(DocumentCollectionService service, DocumentChunkService chunkService, ModelService llmService) {
        super(service);
        this.chunkService = chunkService;
        this.llmService = llmService;
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(DocumentCollection entity, boolean isSave) {

        String alias = entity.getAlias();

        if (StringUtils.hasLength(alias)){
            DocumentCollection knowledge = service.getByAlias(alias);


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
    public Result<List<Document>> search(@RequestParam BigInteger knowledgeId, @RequestParam String keyword) {
        return Result.ok(service.search(knowledgeId, keyword));
    }


    @Override
    protected Result<Void> onRemoveBefore(Collection<Serializable> ids) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in("knowledge_id", ids);

        boolean exists = botDocumentCollectionService.exists(queryWrapper);
        if (exists){
            throw new BusinessException("此知识库还关联着bot，请先取消关联！");
        }

        return null;
    }

    @Override
    public Result<DocumentCollection> detail(String id) {
        return Result.ok(service.getDetail(id));
    }
}

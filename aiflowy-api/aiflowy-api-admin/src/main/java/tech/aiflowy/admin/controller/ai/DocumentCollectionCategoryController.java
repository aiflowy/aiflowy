package tech.aiflowy.admin.controller.ai;

import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.entity.DocumentCollectionCategory;
import tech.aiflowy.ai.entity.WorkflowCategory;
import tech.aiflowy.ai.mapper.DocumentCollectionMapper;
import tech.aiflowy.ai.service.DocumentCollectionCategoryService;
import tech.aiflowy.ai.service.DocumentCollectionService;
import tech.aiflowy.ai.service.WorkflowCategoryService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2026-01-23
 */
@RestController
@RequestMapping("/api/v1/documentCollectionCategory")
@UsePermission(moduleName = "/api/v1/documentCollection")
public class DocumentCollectionCategoryController extends BaseCurdController<DocumentCollectionCategoryService, DocumentCollectionCategory> {

    @Resource
    private DocumentCollectionMapper documentCollectionMapper;

    public DocumentCollectionCategoryController(DocumentCollectionCategoryService service) {
        super(service);
    }

    @Override
    protected Result<?> onRemoveBefore(Collection<Serializable> ids) {
        ids.forEach(id -> {
            QueryWrapper queryWrapper = QueryWrapper.create().eq(DocumentCollection::getCategoryId, id);
            List<DocumentCollection> documentCollections = documentCollectionMapper.selectListByQuery(queryWrapper);
            if (!documentCollections.isEmpty()) {
                throw new BusinessException("请先删除该分类下的所有知识库");
            }
        });

        return super.onRemoveBefore(ids);
    }
}
package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.VectorDatabase;
import tech.aiflowy.ai.service.VectorDatabaseService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.math.BigInteger;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2026-02-12
 */
@RestController
@RequestMapping("/api/v1/vectorDatabase")
public class VectorDatabaseController extends BaseCurdController<VectorDatabaseService, VectorDatabase> {
    public VectorDatabaseController(VectorDatabaseService service) {
        super(service);
    }

    @PostMapping("saveConfig")
    @SaCheckPermission("/api/v1/vectorDatabase/save")
    public Result<?> save(@JsonBody VectorDatabase entity) {
        return service.saveConfig(entity);
    }

    @PostMapping("verifyVectorConfig")
    public Result<?> save(@JsonBody(value = "vectorEmbedModelId", required = true) BigInteger vectorEmbedModelId,
                          @JsonBody(value = "collectionName", required = true) String collectionName,
                          @JsonBody(value = "dimensions", required = true) Integer dimensions,
                          @JsonBody(value = "vectorDatabase", required = true) VectorDatabase entity) {
        return service.verifyVectorConfig(vectorEmbedModelId, entity, collectionName, dimensions);
    }
}
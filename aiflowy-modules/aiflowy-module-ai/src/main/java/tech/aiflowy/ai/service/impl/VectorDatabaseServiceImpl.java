package tech.aiflowy.ai.service.impl;

import com.agentsflex.core.document.Document;
import com.agentsflex.core.model.embedding.EmbeddingModel;
import com.agentsflex.core.model.embedding.EmbeddingOptions;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.store.StoreResult;
import com.mybatisflex.core.keygen.impl.FlexIDKeyGenerator;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.Model;
import tech.aiflowy.ai.entity.VectorDatabase;
import tech.aiflowy.ai.mapper.VectorDatabaseMapper;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.ai.service.VectorDatabaseService;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.utils.CommonFiledUtil;
import tech.aiflowy.common.constant.enums.EnumVectorDatabaseType;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 向量数据库表 服务层实现。
 *
 * @author 12076
 * @since 2026-02-24
 */
@Service
public class VectorDatabaseServiceImpl extends ServiceImpl<VectorDatabaseMapper, VectorDatabase>  implements VectorDatabaseService{

    @Resource
    private ModelService modelService;
    @Override
    public Result<?> saveConfig(VectorDatabase entity) {
        String vectorDatabaseType = entity.getType();
        Map<String, Object> configOptions = entity.getConfigOptions();
        // redis || elasticSearch
        if (vectorDatabaseType.equals(EnumVectorDatabaseType.REDIS.getCode())
                || vectorDatabaseType.equals(EnumVectorDatabaseType.ELASTICSEARCH.getCode())) {
            getConfigOptions(configOptions, VectorDatabase.HOST);
            getConfigOptions(configOptions, VectorDatabase.PORT);
        }
        // aliyun
        if (vectorDatabaseType.equals(EnumVectorDatabaseType.ALIYUN.getCode())) {
            getConfigOptions(configOptions, VectorDatabase.ENDPOINT);
            getConfigOptions(configOptions, VectorDatabase.APIKEY);
        }
        // TENCENT
        if (vectorDatabaseType.equals(EnumVectorDatabaseType.TENCENT.getCode())) {
            getConfigOptions(configOptions, VectorDatabase.APIKEY);
            getConfigOptions(configOptions, VectorDatabase.HOST);
        }
        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
        CommonFiledUtil.commonFiled(entity, loginAccount.getId(), loginAccount.getTenantId(), loginAccount.getDeptId());
        this.save(entity);
        return Result.ok();
    }

    @Override
    public Result<?> verifyVectorConfig(BigInteger vectorEmbedModelId, VectorDatabase entity, String collectionName, Integer dimensions) {
        try {
            DocumentStore documentStore = entity.toDocumentStore(null);
            List<Document> documents = new ArrayList<>();
            Document document = Document.of("测试向量数据库");
            FlexIDKeyGenerator flexIDKeyGenerator = new FlexIDKeyGenerator();
            String bigIntegerId = String.valueOf(flexIDKeyGenerator.generate(document, null));
            document.setId(bigIntegerId);
            documents.add(document);
            Model model = modelService.getModelInstance(vectorEmbedModelId);
            EmbeddingModel embeddingModel = model.toEmbeddingModel();
            StoreOptions options = StoreOptions.ofCollectionName(collectionName);
            EmbeddingOptions embeddingOptions = new EmbeddingOptions();
            embeddingOptions.setModel(model.getModelName());
            embeddingOptions.setDimensions(dimensions);
            options.setEmbeddingOptions(embeddingOptions);
            options.setIndexName(options.getCollectionName());
            documentStore.setEmbeddingModel(embeddingModel);
            StoreResult storeResult = documentStore.store(documents, options);
            if (storeResult.isSuccess()) {
                List<String> ids = new ArrayList<>();
                ids.add(bigIntegerId);
                documentStore.delete(ids, options);
                return Result.ok("验证成功");
            }
            return Result.fail(1, "向量数据库验证失败,失败原因：" + storeResult.getMessage());
        } catch (Exception e) {
            return Result.fail(2, e.getMessage());
        }
    }

    public String getConfigOptions(Map<String, Object> options, String key) {
        if (options == null) {
            throw new BusinessException("参数错误");
        }
        if (options.containsKey(key)) {
            return (String)options.get(key);
        } else {
            if (key.equals(VectorDatabase.USERNAME)) {
                throw new BusinessException("用户名不能为空");
            }
            if (key.equals(VectorDatabase.PASSWORD)) {
                throw new BusinessException("密码不能为空");
            }
            if (key.equals(VectorDatabase.HOST)) {
                throw new BusinessException("主机不能为空");
            }
            if (key.equals(VectorDatabase.PORT)) {
                throw new BusinessException("端口不能为空");
            }
            if (key.equals(VectorDatabase.DATABASE)) {
                throw new BusinessException("数据库不能为空");
            }
        }
        return null;
    }
}

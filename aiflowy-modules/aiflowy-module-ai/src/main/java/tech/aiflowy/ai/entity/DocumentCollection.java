package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.tool.Tool;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.store.aliyun.AliyunVectorStore;
import com.agentsflex.store.aliyun.AliyunVectorStoreConfig;
import com.agentsflex.store.elasticsearch.ElasticSearchVectorStore;
import com.agentsflex.store.elasticsearch.ElasticSearchVectorStoreConfig;
import com.agentsflex.store.opensearch.OpenSearchVectorStore;
import com.agentsflex.store.opensearch.OpenSearchVectorStoreConfig;
import com.agentsflex.store.qcloud.QCloudVectorStore;
import com.agentsflex.store.qcloud.QCloudVectorStoreConfig;
import com.agentsflex.store.redis.RedisVectorStore;
import com.agentsflex.store.redis.RedisVectorStoreConfig;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.agentsflex.tool.DocumentCollectionTool;
import tech.aiflowy.ai.entity.base.DocumentCollectionBase;
import tech.aiflowy.common.util.PropertiesUtil;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_document_collection")
public class DocumentCollection extends DocumentCollectionBase {

    /**
     * 知识库id
     */
    public static final String KEY_DOCUMENT_ID = "documentCollectionId";

    /**
     * 向量相似度权重
     */
    public static final String KEY_VECTOR_WEIGHT = "vectorWeight";

    /**
     * 混合相似度最小值
     */
    public static final String KEY_MIXED_SIMILARITY_THRESHOLD = "mixedSimThreshold";

    /**
     * 是否允许更新向量模型
     */
    public static final String KEY_CAN_UPDATE_EMBEDDING_MODEL = "canUpdateEmbeddingModel";

    /**
     * 搜索引擎权重
     */
    public static final String KEY_SEARCHER_WEIGHT = "searcherWeight";

    /**
     * 知识召回最大条数
     */
    public static final String KEY_DOC_RECALL_MAX_NUM = "docRecallMaxNum";

    /**
     * 相似度最小值
     */
    public static final String KEY_SIMILARITY_THRESHOLD = "simThreshold";

    /**
     * 搜索引擎类型
     */
    public static final String KEY_SEARCH_ENGINE_TYPE = "searchEngineType";

    public static final String KEY_COLLECTION_NAME = "collectionName";

    public boolean isSearchEngineEnabled() {
        return this.getSearchEngineEnable() != null && this.getSearchEngineEnable();
    }

    // 获取向量数据库的其他数据配置
    public Map<String, Object> getVectorOtherConfig  (){
        Map<String, Object> config = new HashMap<>();
        config.put(KEY_COLLECTION_NAME, this.getVectorStoreCollection());
        return config;
    }

    public Tool toFunction(boolean needEnglishName) {
        return new DocumentCollectionTool(this, needEnglishName);
    }

    public Object getOptionsByKey(String key) {
        Map<String, Object> options = this.getOptions();
        if (options == null) {
            return null;
        }
        if (KEY_DOC_RECALL_MAX_NUM.equals(key) && !options.containsKey(KEY_DOC_RECALL_MAX_NUM)) {
            return 5;
        }
        if (KEY_SIMILARITY_THRESHOLD.equals(key)) {
            if (!options.containsKey(KEY_SIMILARITY_THRESHOLD)) {
                return 0.6f;
            } else {
                BigDecimal score = (BigDecimal) options.get(key);
                return (float) score.doubleValue();
            }
        }
        if (KEY_SEARCH_ENGINE_TYPE.equals(key)) {
            if (!options.containsKey(KEY_SEARCH_ENGINE_TYPE)) {
                return "lucene";
            }
        }
        if (KEY_MIXED_SIMILARITY_THRESHOLD.equals(key)) {
            if (!options.containsKey(KEY_MIXED_SIMILARITY_THRESHOLD)) {
                return 0.2f;
            } else {
                BigDecimal score = (BigDecimal) options.get(key);
                return (float) score.doubleValue();
            }
        }
        if (KEY_VECTOR_WEIGHT.equals(key)) {
            if (!options.containsKey(KEY_VECTOR_WEIGHT)) {
                return 0.7;
            } else {
                Object obj = options.get(key);
                BigDecimal score = (obj == null) ? BigDecimal.ZERO : new BigDecimal(obj.toString());
                return score.doubleValue();
            }
        }
        if (KEY_SEARCHER_WEIGHT.equals(key)) {
            if (!options.containsKey(KEY_SEARCHER_WEIGHT)) {
                return 0.3;
            } else {
                Object obj = options.get(key);
                BigDecimal score = (obj == null) ? BigDecimal.ZERO : new BigDecimal(obj.toString());
                return score.doubleValue();
            }
        }
        return options.get(key);
    }
}

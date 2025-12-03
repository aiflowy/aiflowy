package tech.aiflowy.ai.entity;

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
import tech.aiflowy.ai.entity.base.AiKnowledgeBase;
import tech.aiflowy.common.util.PropertiesUtil;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_ai_knowledge")
public class AiKnowledge extends AiKnowledgeBase {

    public DocumentStore toDocumentStore() {
        String storeType = this.getVectorStoreType();
        if (storeType == null) {
            return null;
        }
        switch (storeType.toLowerCase()) {
            case "redis":
                return redisStore();
//            case "milvus":
//                return milvusStore();
            case "opensearch":
                return openSearchStore();
            case "elasticsearch":
                return elasticSearchStore();
            case "aliyun":
                return aliyunStore();
            case "qcloud":
                return qcloudStore();
        }
        return null;
    }

    public boolean isVectorStoreEnabled() {
        return this.getVectorStoreEnable() != null && this.getVectorStoreEnable();
    }

    public boolean isSearchEngineEnabled() {
        return this.getSearchEngineEnable() != null && this.getSearchEngineEnable();
    }


    private DocumentStore redisStore() {
        RedisVectorStoreConfig redisVectorStoreConfig = getStoreConfig(RedisVectorStoreConfig.class);
        return new RedisVectorStore(redisVectorStoreConfig);
    }

//    private DocumentStore milvusStore() {
//        MilvusVectorStoreConfig milvusVectorStoreConfig = getStoreConfig(MilvusVectorStoreConfig.class);
//        return new MilvusVectorStore(milvusVectorStoreConfig);
//    }

    private DocumentStore openSearchStore() {
        OpenSearchVectorStoreConfig openSearchVectorStoreConfig = getStoreConfig(OpenSearchVectorStoreConfig.class);
        return new OpenSearchVectorStore(openSearchVectorStoreConfig);
    }

    private DocumentStore elasticSearchStore() {
        ElasticSearchVectorStoreConfig elasticSearchVectorStoreConfig = getStoreConfig(ElasticSearchVectorStoreConfig.class);
        return new ElasticSearchVectorStore(elasticSearchVectorStoreConfig);
    }

    private DocumentStore aliyunStore() {
        AliyunVectorStoreConfig aliyunVectorStoreConfig = getStoreConfig(AliyunVectorStoreConfig.class);
        return new AliyunVectorStore(aliyunVectorStoreConfig);
    }

    private DocumentStore qcloudStore() {
        QCloudVectorStoreConfig qCloudVectorStoreConfig = getStoreConfig(QCloudVectorStoreConfig.class);
        return new QCloudVectorStore(qCloudVectorStoreConfig);
    }

    private <T> T getStoreConfig(Class<T> clazz) {
        return PropertiesUtil.propertiesTextToEntity(this.getVectorStoreConfig(), clazz);
    }
}

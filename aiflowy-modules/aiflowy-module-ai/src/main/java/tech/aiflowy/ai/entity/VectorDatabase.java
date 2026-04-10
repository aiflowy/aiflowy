package tech.aiflowy.ai.entity;

import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.store.aliyun.AliyunVectorStore;
import com.agentsflex.store.aliyun.AliyunVectorStoreConfig;
import com.agentsflex.store.elasticsearch.ElasticSearchVectorStore;
import com.agentsflex.store.elasticsearch.ElasticSearchVectorStoreConfig;
import com.agentsflex.store.milvus.MilvusVectorStore;
import com.agentsflex.store.milvus.MilvusVectorStoreConfig;
import com.agentsflex.store.opensearch.OpenSearchVectorStore;
import com.agentsflex.store.opensearch.OpenSearchVectorStoreConfig;
import com.agentsflex.store.qcloud.QCloudVectorStore;
import com.agentsflex.store.qcloud.QCloudVectorStoreConfig;
import com.agentsflex.store.redis.RedisVectorStore;
import com.agentsflex.store.redis.RedisVectorStoreConfig;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.VectorDatabaseBase;
import tech.aiflowy.common.util.MapUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.util.Map;


/**
 * 向量数据库表 实体类。
 *
 * @author 12076
 * @since 2026-02-12
 */
@Table(value = "tb_vector_database", comment = "向量数据库表")
public class VectorDatabase extends VectorDatabaseBase {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String HOST = "host";
    public static final String AGREEMENT = "agreement";
    public static final String PORT = "port";
    public static final String DATABASE = "database";
    public static final String APIKEY = "apiKey";
    public static final String ENDPOINT = "endpoint";

    public DocumentStore toDocumentStore(Map<String, Object> otherConfigOptions) {
        Map<String, Object> vectorDatabaseConfigOptions = this.getConfigOptions();
        if (vectorDatabaseConfigOptions == null) {
            return null;
        }
        switch (this.getType().toLowerCase()) {
            case "redis":
                return redisStore(vectorDatabaseConfigOptions, otherConfigOptions);
            case "milvus":
                return milvusStore(vectorDatabaseConfigOptions, otherConfigOptions);
            case "opensearch":
                return openSearchStore(vectorDatabaseConfigOptions, otherConfigOptions);
            case "elasticsearch":
                return elasticSearchStore(vectorDatabaseConfigOptions, otherConfigOptions);
            case "aliyun":
                return aliyunStore(vectorDatabaseConfigOptions, otherConfigOptions);
            case "qcloud":
                return qcloudStore(vectorDatabaseConfigOptions, otherConfigOptions);
        }
        return null;
    }

    private DocumentStore milvusStore(Map<String, Object> vectorDatabaseConfigOptions, Map<String, Object> otherConfigOptions) {
        MilvusVectorStoreConfig milvusVectorStoreConfig = new MilvusVectorStoreConfig();
        String agreement = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.AGREEMENT);
        String host = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.HOST);
        if (host == null || host.isEmpty()) {
            throw new BusinessException("未配置Milvus服务器地址");
        }
        Integer port = MapUtil.getInteger(vectorDatabaseConfigOptions, VectorDatabase.PORT);
        if (port == null) {
            throw new BusinessException("未配置Milvus服务器端口");
        }
        String username = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.USERNAME);
        String password = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.PASSWORD);
        String url = agreement + host + ":" + port;
        milvusVectorStoreConfig.setEndpoint(url);
        String token = username + ":" + password;
        milvusVectorStoreConfig.setToken(token);
        if (otherConfigOptions != null && otherConfigOptions.containsKey(DocumentCollection.KEY_COLLECTION_NAME)) {
            milvusVectorStoreConfig.setDefaultCollectionName(MapUtil.getString(otherConfigOptions, DocumentCollection.KEY_COLLECTION_NAME));
        }
        return MilvusVectorStore.create(milvusVectorStoreConfig);
    }

    private DocumentStore redisStore(Map<String, Object> vectorDatabaseConfigOptions, Map<String, Object> otherConfigOptions) {
        RedisVectorStoreConfig redisVectorStoreConfig = new RedisVectorStoreConfig();
        String username = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.USERNAME);
        String password = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.PASSWORD);
        String host = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.HOST);
        int port = MapUtil.getInteger(vectorDatabaseConfigOptions, VectorDatabase.PORT);
        String uri = VectorDatabase.buildRedisUri(username, password, host, port, 0);
        redisVectorStoreConfig.setUri(uri);
        if (otherConfigOptions != null && otherConfigOptions.containsKey(DocumentCollection.KEY_COLLECTION_NAME)) {
            redisVectorStoreConfig.setDefaultCollectionName(MapUtil.getString(otherConfigOptions, DocumentCollection.KEY_COLLECTION_NAME));
        }
        return new RedisVectorStore(redisVectorStoreConfig);
    }

//    private DocumentStore milvusStore() {
//        MilvusVectorStoreConfig milvusVectorStoreConfig = getStoreConfig(MilvusVectorStoreConfig.class);
//        return new MilvusVectorStore(milvusVectorStoreConfig);
//    }

    private DocumentStore openSearchStore(Map<String, Object> vectorDatabaseConfigOptions, Map<String, Object> otherConfigOptions) {
        String username = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.USERNAME);
        String password = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.PASSWORD);
        String agreement = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.AGREEMENT);
        String host = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.HOST);
        int port = MapUtil.getInteger(vectorDatabaseConfigOptions, VectorDatabase.PORT);
        OpenSearchVectorStoreConfig openSearchVectorStoreConfig = new OpenSearchVectorStoreConfig();
        String url = agreement + host + ":" + port;
        openSearchVectorStoreConfig.setServerUrl(url);
        openSearchVectorStoreConfig.setUsername(username);
        openSearchVectorStoreConfig.setPassword(password);
        if (otherConfigOptions != null && otherConfigOptions.containsKey(DocumentCollection.KEY_COLLECTION_NAME)) {
            openSearchVectorStoreConfig.setDefaultIndexName(MapUtil.getString(otherConfigOptions, DocumentCollection.KEY_COLLECTION_NAME));
        }
        return new OpenSearchVectorStore(openSearchVectorStoreConfig);
    }

    private DocumentStore elasticSearchStore(Map<String, Object> vectorDatabaseConfigOptions, Map<String, Object> otherConfigOptions) {
        ElasticSearchVectorStoreConfig elasticSearchVectorStoreConfig = new ElasticSearchVectorStoreConfig();
        String agreement = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.AGREEMENT);
        String host = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.HOST);
        if (host == null || host.isEmpty()) {
            throw new BusinessException("未配置ElasticSearch服务器地址");
        }
        Integer port = MapUtil.getInteger(vectorDatabaseConfigOptions, VectorDatabase.PORT);
        if (port == null) {
            throw new BusinessException("未配置ElasticSearch服务器端口");
        }
        String username = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.USERNAME);
        String password = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.PASSWORD);
        String url = agreement + host + ":" + port;
        elasticSearchVectorStoreConfig.setServerUrl(url);
        elasticSearchVectorStoreConfig.setUsername(username);
        elasticSearchVectorStoreConfig.setPassword(password);
        if (otherConfigOptions != null && otherConfigOptions.containsKey(DocumentCollection.KEY_COLLECTION_NAME)) {
            elasticSearchVectorStoreConfig.setDefaultIndexName(MapUtil.getString(otherConfigOptions, DocumentCollection.KEY_COLLECTION_NAME));
        }
        return new ElasticSearchVectorStore(elasticSearchVectorStoreConfig);
    }

    private DocumentStore aliyunStore(Map<String, Object> vectorDatabaseConfigOptions, Map<String, Object> otherConfigOptions) {
        AliyunVectorStoreConfig aliyunVectorStoreConfig = new AliyunVectorStoreConfig();
        String apiKey = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.APIKEY);
        String endpoint = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.ENDPOINT);
        if (apiKey == null || apiKey.isEmpty()) {
            throw new BusinessException("阿里云向量数据库配置apiKey不能为空");
        }
        if (endpoint == null || endpoint.isEmpty() ) {
            throw new BusinessException("阿里云向量数据库配置端点endpoint不能为空");
        }
        aliyunVectorStoreConfig.setApiKey(apiKey);
        aliyunVectorStoreConfig.setEndpoint(endpoint);
        if (otherConfigOptions != null && otherConfigOptions.containsKey(DocumentCollection.KEY_COLLECTION_NAME)) {
            aliyunVectorStoreConfig.setDefaultCollectionName(MapUtil.getString(otherConfigOptions, DocumentCollection.KEY_COLLECTION_NAME));
        }
        return new AliyunVectorStore(aliyunVectorStoreConfig);
    }

    private DocumentStore qcloudStore(Map<String, Object> vectorDatabaseConfigOptions, Map<String, Object> otherConfigOptions) {
        QCloudVectorStoreConfig qCloudVectorStoreConfig = new QCloudVectorStoreConfig();
        String username = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.USERNAME);
        String apiKey = MapUtil.getString(vectorDatabaseConfigOptions, VectorDatabase.APIKEY);
        qCloudVectorStoreConfig.setAccount(username);
        qCloudVectorStoreConfig.setApiKey(apiKey);
        if (otherConfigOptions != null && otherConfigOptions.containsKey(DocumentCollection.KEY_COLLECTION_NAME)) {
            qCloudVectorStoreConfig.setDefaultCollectionName(MapUtil.getString(otherConfigOptions, DocumentCollection.KEY_COLLECTION_NAME));
        }
        return new QCloudVectorStore(qCloudVectorStoreConfig);
    }

    /**
     * 拼接 Redis URI 的工具方法
     * @param username Redis用户名（可为null/空，低版本Redis无需填写）
     * @param password Redis密码（可为null/空）
     * @param host     Redis服务器IP/域名（必填）
     * @param port     Redis端口（必填，通常填6379）
     * @param database Redis数据库编号（可选，默认0）
     * @return 标准的Redis URI字符串
     */
    public static String buildRedisUri(String username, String password, String host, int port, Integer database) {
        // 1. 拼接认证部分（核心修复：无用户名但有密码时，开头加:）
        StringBuilder authPart = new StringBuilder();
        if (password != null && !password.isEmpty()) {
            // 无用户名时，密码前必须加:
            if (username == null || username.isEmpty()) {
                authPart.append(":"); // 关键修复点
            } else {
                authPart.append(username).append(":"); // 有用户名时，拼接 用户名:
            }
            authPart.append(password).append("@");
        } else if (username != null && !username.isEmpty()) {
            // 有用户名、无密码（极少场景）
            authPart.append(username).append("@");
        }

        // 2. 拼接核心地址+数据库
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append("redis://")
                .append(authPart)
                .append(host)
                .append(":").append(port);

        // 3. 拼接数据库编号（可选）
        if (database != null && database >= 0) {
            uriBuilder.append("/").append(database);
        }

        return uriBuilder.toString();
    }

}

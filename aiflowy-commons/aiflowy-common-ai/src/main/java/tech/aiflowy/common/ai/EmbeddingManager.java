package tech.aiflowy.common.ai;

import tech.aiflowy.common.ai.util.LLMUtil;
import tech.aiflowy.common.options.SysOptions;
import tech.aiflowy.common.util.StringUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.prompt.TextPrompt;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.store.aliyun.AliyunVectorStore;
import com.agentsflex.store.aliyun.AliyunVectorStoreConfig;
import com.agentsflex.store.milvus.MilvusVectorStore;
import com.agentsflex.store.milvus.MilvusVectorStoreConfig;
import com.agentsflex.store.qcloud.QCloudVectorStore;
import com.agentsflex.store.qcloud.QCloudVectorStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmbeddingManager {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingManager.class);
    private static final EmbeddingManager manager = new EmbeddingManager();

    public static EmbeddingManager getInstance() {
        return manager;
    }

    private ExecutorService sseExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Llm getEmbeddingModel() {
        String modelOfEmbedding = SysOptions.get("model_of_embedding");
        return LLMUtil.getLlmByType(modelOfEmbedding);
    }

    public DocumentStore getVectorStore() {
        String vectorStoreType = SysOptions.get("vectorstore_type");
        if (StringUtil.noText(vectorStoreType)) {
            return null;
        }

        DocumentStore store = null;

        if ("aliyun".equalsIgnoreCase(vectorStoreType)) {
            AliyunVectorStoreConfig storeConfig = new AliyunVectorStoreConfig();
            storeConfig.setDefaultCollectionName(SysOptions.get("vectorstore_default_collection"));
            storeConfig.setApiKey(SysOptions.get("aliyun_vdb_api_key"));
            storeConfig.setEndpoint(SysOptions.get("aliyun_vdb_endpoint"));
            storeConfig.setDatabase(SysOptions.get("aliyun_vdb_database"));
            store = new AliyunVectorStore(storeConfig);
        }
        //腾讯云
        else if ("qcloud".equalsIgnoreCase(vectorStoreType)) {
            QCloudVectorStoreConfig storeConfig = new QCloudVectorStoreConfig();
            storeConfig.setDefaultCollectionName(SysOptions.get("vectorstore_default_collection"));
            storeConfig.setHost(SysOptions.get("qcloud_vdb_endpoint"));
            storeConfig.setApiKey(SysOptions.get("qcloud_vdb_api_key"));
            storeConfig.setAccount(SysOptions.get("qcloud_vdb_username"));
            storeConfig.setDatabase(SysOptions.get("qcloud_vdb_database"));
            store = new QCloudVectorStore(storeConfig);
        }
        //Milvus 数据库
        else if ("milvus".equalsIgnoreCase(vectorStoreType)) {
            MilvusVectorStoreConfig storeConfig = new MilvusVectorStoreConfig();
            storeConfig.setDatabaseName(SysOptions.get("milvus_database"));
            storeConfig.setDefaultCollectionName(SysOptions.get("vectorstore_default_collection"));
            storeConfig.setUri(SysOptions.get("milvus_endpoint"));
            storeConfig.setToken(SysOptions.get("milvus_apikey"));
            store = new MilvusVectorStore(storeConfig);
        }

        Llm embeddingModel = getEmbeddingModel();
        if (store != null && embeddingModel != null) {
            store.setEmbeddingModel(embeddingModel);
        }

        return store;
    }


    public SseEmitter sseEmitter(String prompt) {
        return sseEmitter(new TextPrompt(prompt));
    }

    public SseEmitter sseEmitter(TextPrompt prompt) {
        SseEmitter emitter = new SseEmitter((long) (1000 * 60 * 2));
        sseExecutor.execute(() -> {
            Llm llm = getEmbeddingModel();
            if (llm == null) {
                try {
                    emitter.send(SseEmitter.event().data("AI 大模型未配置正确"));
                    emitter.complete();
                } catch (IOException e) {
                    logger.error(e.toString(), e);
                    emitter.completeWithError(e);
                }
                return;
            }
            llm.chatStream(prompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext chatContext, AiMessageResponse aiMessageResponse) {
                    try {
                        String content = aiMessageResponse.getMessage().getContent();
                        emitter.send(SseEmitter.event().data(content));
                    } catch (IOException e) {
                        logger.error(e.toString(), e);
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onStop(ChatContext context) {
                    emitter.complete();
                }
            });
        });
        return emitter;
    }

    public ExecutorService getSseExecutor() {
        return sseExecutor;
    }

    public void setSseExecutor(ExecutorService sseExecutor) {
        this.sseExecutor = sseExecutor;
    }
}

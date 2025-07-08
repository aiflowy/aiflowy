package tech.aiflowy.ai.utils;

import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.SearchWrapper;
import com.agentsflex.core.store.StoreOptions;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.file.FileStorage;
import dev.tinyflow.core.knowledge.Knowledge;
import dev.tinyflow.core.node.KnowledgeNode;
import dev.tinyflow.core.parser.ChainParser;
import dev.tinyflow.core.provider.KnowledgeProvider;
import dev.tinyflow.core.provider.LlmProvider;
import dev.tinyflow.core.provider.SearchEngineProvider;
import dev.tinyflow.core.searchengine.SearchEngine;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.config.BochaaiProps;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.node.*;
import tech.aiflowy.ai.provider.BochaaiSearchEngine;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.filestorage.FileStorageService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Component
public class TinyFlowConfigService {

    @Resource
    private ReaderManager readerManager;
    @Resource(name = "default")
    private FileStorageService storageService;
    @Resource
    private AiLlmService aiLlmService;
    @Resource
    private BochaaiProps bochaaiProps;
    @Resource
    private AiKnowledgeService aiKnowledgeService;

    public void initProvidersAndNodeParsers(Tinyflow tinyflow) {
        setExtraNodeParser(tinyflow);
        setSearchEngineProvider(tinyflow);
        setLlmProvider(tinyflow);
        setKnowledgeProvider(tinyflow);
        setFileStorage(tinyflow);
    }

    private void setFileStorage(Tinyflow tinyflow) {
        tinyflow.setFileStorage(new FileStorage() {
            @Override
            public String saveFile(InputStream stream, Map<String, String> headers) {
                return storageService.save(new InputStreamFile(stream, headers));
            }
        });
    }

    public void setExtraNodeParser(Tinyflow tinyflow) {

        ReadDocService readService = readerManager.getReader();
        // 文档解析
        DocNodeParser docNodeParser = new DocNodeParser(readService);
        // 文件生成
        MakeFileNodeParser makeFileNodeParser = new MakeFileNodeParser(storageService);
        // 插件
        PluginToolNodeParser pluginToolNodeParser = new PluginToolNodeParser();
        // SQL查询
        SqlNodeParser sqlNodeParser = new SqlNodeParser();
        // 下载文件节点
        DownloadNodeParser downloadNodeParser = new DownloadNodeParser(storageService);

        ChainParser chainParser = tinyflow.getChainParser();
        chainParser.addNodeParser(docNodeParser.getNodeName(), docNodeParser);
        chainParser.addNodeParser(makeFileNodeParser.getNodeName(), makeFileNodeParser);
        chainParser.addNodeParser(pluginToolNodeParser.getNodeName(), pluginToolNodeParser);
        chainParser.addNodeParser(sqlNodeParser.getNodeName(), sqlNodeParser);
        chainParser.addNodeParser(downloadNodeParser.getNodeName(), downloadNodeParser);
    }

    public void setSearchEngineProvider(Tinyflow tinyflow) {
        tinyflow.setSearchEngineProvider(new SearchEngineProvider() {
            @Override
            public SearchEngine getSearchEngine(Object id) {
                BochaaiSearchEngine searchEngine = new BochaaiSearchEngine();
                searchEngine.setApiKey(bochaaiProps.getApiKey());
                return searchEngine;
            }
        });
    }

    public void setLlmProvider(Tinyflow tinyflow) {
        tinyflow.setLlmProvider(new LlmProvider() {
            @Override
            public Llm getLlm(Object id) {
                AiLlm aiLlm = aiLlmService.getById(new BigInteger(id.toString()));
                return aiLlm.toLlm();
            }
        });
    }

    public void setKnowledgeProvider(Tinyflow tinyflow) {
        tinyflow.setKnowledgeProvider(new KnowledgeProvider() {
            @Override
            public Knowledge getKnowledge(Object id) {
                AiKnowledge aiKnowledge = aiKnowledgeService.getById(new BigInteger(id.toString()));
                return new Knowledge() {
                    @Override
                    public List<Document> search(String keyword, int limit, KnowledgeNode knowledgeNode, Chain chain) {
                        DocumentStore documentStore = aiKnowledge.toDocumentStore();
                        if (documentStore == null) {
                            return null;
                        }
                        AiLlm aiLlm = aiLlmService.getById(aiKnowledge.getVectorEmbedLlmId());
                        if (aiLlm == null) {
                            return null;
                        }
                        documentStore.setEmbeddingModel(aiLlm.toLlm());
                        SearchWrapper wrapper = new SearchWrapper();
                        wrapper.setMaxResults(Integer.valueOf(limit));
                        wrapper.setText(keyword);
                        StoreOptions options = StoreOptions.ofCollectionName(aiKnowledge.getVectorStoreCollection());

                        List<Document> results = documentStore.search(wrapper, options);
                        return results;
                    }
                };
            }
        });
    }
}

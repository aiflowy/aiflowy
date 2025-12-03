package tech.aiflowy.ai.utils;

import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.filestoreage.FileStorageManager;
import dev.tinyflow.core.filestoreage.FileStorageProvider;
import dev.tinyflow.core.knowledge.KnowledgeManager;
import dev.tinyflow.core.knowledge.KnowledgeProvider;
import dev.tinyflow.core.llm.LlmManager;
import dev.tinyflow.core.llm.LlmProvider;
import dev.tinyflow.core.parser.ChainParser;
import dev.tinyflow.core.searchengine.SearchEngine;
import dev.tinyflow.core.searchengine.SearchEngineManager;
import dev.tinyflow.core.searchengine.SearchEngineProvider;
import dev.tinyflow.core.searchengine.impl.BochaaiSearchEngineImpl;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.config.BochaaiProps;
import tech.aiflowy.ai.node.*;

import javax.annotation.Resource;

@Component
public class TinyFlowConfigService {

    @Resource
    private LlmProvider llmProvider;
    @Resource
    private FileStorageProvider fileStorageProvider;
    @Resource
    private BochaaiProps bochaaiProps;
    @Resource
    private KnowledgeProvider knowledgeProvider;

    public void initProvidersAndNodeParsers(Tinyflow tinyflow) {
        setExtraNodeParser(tinyflow);
        setLlmProvider();
        setFileStorage();
        setKnowledgeProvider();
        setSearchEngineProvider();
    }

    private void setFileStorage() {
        FileStorageManager.getInstance().registerProvider(fileStorageProvider);
    }

    public void setExtraNodeParser(Tinyflow tinyflow) {

        // 文档解析
        DocNodeParser docNodeParser = new DocNodeParser();
        // 文件生成
        MakeFileNodeParser makeFileNodeParser = new MakeFileNodeParser();
        // 插件
        PluginToolNodeParser pluginToolNodeParser = new PluginToolNodeParser();
        // SQL查询
        SqlNodeParser sqlNodeParser = new SqlNodeParser();
        // 下载文件节点
        DownloadNodeParser downloadNodeParser = new DownloadNodeParser();
        // 保存数据节点
        SaveToDatacenterNodeParser saveDaveParser = new SaveToDatacenterNodeParser();
        // 查询数据节点
        SearchDatacenterNodeParser searchDatacenterNodeParser = new SearchDatacenterNodeParser();
        // 工作流节点
        WorkflowNodeParser workflowNodeParser = new WorkflowNodeParser();

        ChainParser chainParser = tinyflow.getChainParser();
        chainParser.addNodeParser(docNodeParser.getNodeName(), docNodeParser);
        chainParser.addNodeParser(makeFileNodeParser.getNodeName(), makeFileNodeParser);
        chainParser.addNodeParser(pluginToolNodeParser.getNodeName(), pluginToolNodeParser);
        chainParser.addNodeParser(sqlNodeParser.getNodeName(), sqlNodeParser);
        chainParser.addNodeParser(downloadNodeParser.getNodeName(), downloadNodeParser);
        chainParser.addNodeParser(saveDaveParser.getNodeName(), saveDaveParser);
        chainParser.addNodeParser(searchDatacenterNodeParser.getNodeName(), searchDatacenterNodeParser);
        chainParser.addNodeParser(workflowNodeParser.getNodeName(), workflowNodeParser);
    }

    public void setSearchEngineProvider() {
        BochaaiSearchEngineImpl engine = new BochaaiSearchEngineImpl();
        engine.setApiKey(bochaaiProps.getApiKey());
        SearchEngineManager.getInstance().registerProvider(new SearchEngineProvider() {
            @Override
            public SearchEngine getSearchEngine(Object id) {
                return id.equals("bocha-search") ? engine : null;
            }
        });
    }

    public void setLlmProvider() {
        LlmManager.getInstance().registerProvider(llmProvider);
    }

    public void setKnowledgeProvider() {
        KnowledgeManager.getInstance().registerProvider(knowledgeProvider);
    }
}

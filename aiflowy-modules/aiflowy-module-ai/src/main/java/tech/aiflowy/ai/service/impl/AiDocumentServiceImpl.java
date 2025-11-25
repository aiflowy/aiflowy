package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.document.DocumentParser;
import com.agentsflex.core.document.DocumentSplitter;
import com.agentsflex.core.document.splitter.RegexDocumentSplitter;
import com.agentsflex.core.document.splitter.SimpleDocumentSplitter;
import com.agentsflex.core.document.splitter.SimpleTokenizeSplitter;
import com.agentsflex.core.llm.embedding.EmbeddingOptions;
import com.agentsflex.search.engine.service.DocumentSearcher;
import com.mybatisflex.core.keygen.impl.FlexIDKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.AiDocument;
import tech.aiflowy.ai.entity.AiDocumentChunk;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.mapper.AiDocumentChunkMapper;
import tech.aiflowy.ai.mapper.AiDocumentMapper;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import tech.aiflowy.ai.service.AiDocumentService;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.ai.DocumentParserFactory;
import tech.aiflowy.common.ai.ExcelDocumentSplitter;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.store.StoreResult;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.filestorage.FileStorageService;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.core.utils.JudgeFileTypeUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service("AiService")
public class AiDocumentServiceImpl extends ServiceImpl<AiDocumentMapper, AiDocument> implements AiDocumentService {
    protected Logger Log = LoggerFactory.getLogger(AiDocumentServiceImpl.class);

    @Resource
    private AiDocumentMapper aiDocumentMapper;

    @Resource
    private AiDocumentChunkMapper aiDocumentChunkMapper;

    @Resource
    private AiKnowledgeService knowledgeService;

    @Resource
    private AiLlmService aiLlmService;

    @Resource(name = "default")
    FileStorageService storageService;

    @Resource
    private AiDocumentChunkService documentChunkService;

    @Autowired
    private SearcherFactory searcherFactory;

    @Override
    public Page<AiDocument> getDocumentList(String knowledgeId, int pageSize, int pageNum, String fileName) {
        // 构建 QueryWrapper
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("dt.*", "COUNT(ck.document_id) AS chunk_count") // 选择字段
                .from("tb_ai_document")
                .as("dt")// 主表
                .leftJoin("tb_ai_document_chunk")
                .as("ck").on("dt.id = ck.document_id") // 左连接
                .where("dt.knowledge_id = ?", knowledgeId); // 条件 1
        // 动态添加 fileName 条件
        if (fileName != null && !fileName.trim().isEmpty()) {
            queryWrapper.and("dt.title LIKE CONCAT('%', ?, '%')", fileName); // 条件 2
        }
        // 分组
        queryWrapper.groupBy("dt.id");
        Page<AiDocument> documentVoPage = aiDocumentMapper.paginateAs(pageNum, pageSize, queryWrapper, AiDocument.class);
        return documentVoPage;
    }

    /**
     * 根据文档id删除文件
     *
     * @param id 文档id
     * @return
     */
    @Override
    public boolean removeDoc(String id) {
        // 查询该文档对应哪些分割的字段，先删除
        QueryWrapper where = QueryWrapper.create().where("document_id = ? ", id);
        QueryWrapper aiDocumentWapper = QueryWrapper.create().where("id = ? ", id);
        AiDocument oneByQuery = aiDocumentMapper.selectOneByQuery(aiDocumentWapper);
        AiKnowledge knowledge = knowledgeService.getById(oneByQuery.getKnowledgeId());
        if (knowledge == null) {
            return false;
        }

        // 存储到知识库
        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null) {
            return false;
        }

        AiLlm aiLlm = aiLlmService.getById(knowledge.getVectorEmbedLlmId());
        if (aiLlm == null) {
            return false;
        }
        // 设置向量模型
        Llm embeddingModel = aiLlm.toLlm();
        documentStore.setEmbeddingModel(embeddingModel);
        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        EmbeddingOptions embeddingOptions = new EmbeddingOptions();
        embeddingOptions.setModel(aiLlm.getLlmModel());
        options.setEmbeddingOptions(embeddingOptions);
        options.setCollectionName(knowledge.getVectorStoreCollection());
        // 查询文本分割表tb_ai_document_chunk中对应的有哪些数据，找出来删除
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("id").from("tb_ai_document_chunk").where("document_id = ?", id);
        List<BigInteger> chunkIds = aiDocumentChunkMapper.selectListByQueryAs(queryWrapper, BigInteger.class);
        documentStore.delete(chunkIds, options);
        // 删除搜索引擎中的数据
        if (searcherFactory.getSearcher() != null) {
            DocumentSearcher searcher = searcherFactory.getSearcher();
            chunkIds.forEach(searcher::deleteDocument);
        }
        int ck = aiDocumentChunkMapper.deleteByQuery(where);
        if (ck < 0) {
            return false;
        }
        // 再删除指定路径下的文件
        QueryWrapper wrapper = QueryWrapper.create().where("id = ?", id);
        AiDocument aiDocument = aiDocumentMapper.selectOneByQuery(wrapper);
        storageService.delete(aiDocument.getDocumentPath());
        return true;
    }


    @Override
    @Transactional
    public Result<?> textSplit(Integer pageNumber, Integer pageSize, String operation, BigInteger knowledgeId, String filePath, String originalFilename, String splitterName, Integer chunkSize, Integer overlapSize, String regex, Integer rowsPerChunk) {
        try {
            InputStream inputStream = storageService.readStream(filePath);
            if (getFileExtension(filePath) == null){
                Log.error("获取文件后缀失败");
                throw new BusinessException("获取文件后缀失败");
            }
            DocumentParser documentParser = DocumentParserFactory.getDocumentParser(filePath);
            AiDocument aiDocument = new AiDocument();
            List<AiDocumentChunk> previewList = new ArrayList<>();
            DocumentSplitter documentSplitter = getDocumentSplitter(splitterName, chunkSize, overlapSize, regex, rowsPerChunk);
            Document document = null;
            if (documentParser != null) {
                document = documentParser.parse(inputStream);
            }
            inputStream.close();
            List<Document> documents = documentSplitter.split(document);
            FlexIDKeyGenerator flexIDKeyGenerator = new FlexIDKeyGenerator();
            int sort = 1;
            for (Document value : documents) {
                AiDocumentChunk chunk = new AiDocumentChunk();
                chunk.setId(new BigInteger(String.valueOf(flexIDKeyGenerator.generate(chunk, null))));
                chunk.setContent(value.getContent());
                chunk.setSorting(sort);
                sort++;
                previewList.add(chunk);
            }
            String fileTypeByExtension = JudgeFileTypeUtil.getFileTypeByExtension(filePath);
            aiDocument.setDocumentType(fileTypeByExtension);
            aiDocument.setKnowledgeId(knowledgeId);
            aiDocument.setDocumentPath(filePath);
            aiDocument.setCreated(new Date());
            aiDocument.setModifiedBy(BigInteger.valueOf(StpUtil.getLoginIdAsLong()));
            aiDocument.setModified(new Date());
            if (document != null) {
                aiDocument.setContent(document.getContent());
            }
            aiDocument.setChunkSize(chunkSize);
            aiDocument.setOverlapSize(overlapSize);
            aiDocument.setTitle(originalFilename);
            Map<String, Object> res = new HashMap<>();

            List<AiDocumentChunk> aiDocumentChunks = null;
            // 如果是预览拆分，则返回指定页的数据
            if ("textSplit".equals(operation)){
                int startIndex = (pageNumber - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, previewList.size());

                if (startIndex >= previewList.size()) {
                    aiDocumentChunks = new ArrayList<>();
                } else {
                    aiDocumentChunks = new ArrayList<>(previewList.subList(startIndex, endIndex));
                }

                res.put("total", previewList.size());
                // 保存文件到知识库
            } else if ("saveText".equals(operation)){
                aiDocumentChunks = previewList;
                return this.saveTextResult(aiDocumentChunks, aiDocument);
            }

            res.put("previewData", aiDocumentChunks);
            res.put("aiDocumentData", aiDocument);
            // 返回分割效果给用户
            return Result.ok(res);
        } catch (IOException e) {
            Log.error(e.toString(), e);
        }

        return Result.fail("操作失败");
    }

    @Override
    public Result<?> saveTextResult(List<AiDocumentChunk> aiDocumentChunks, AiDocument aiDocument) {
        Boolean result = storeDocument(aiDocument, aiDocumentChunks);
        if (result) {
            this.getMapper().insert(aiDocument);
            AtomicInteger sort = new AtomicInteger(1);
            aiDocumentChunks.forEach(item -> {
                item.setKnowledgeId(aiDocument.getKnowledgeId());
                item.setSorting(sort.get());
                item.setDocumentId(aiDocument.getId());
                sort.getAndIncrement();
                documentChunkService.save(item);
            });
            return Result.ok();
        }
        return Result.fail(1, "保存失败");
    }

    protected Boolean storeDocument(AiDocument entity, List<AiDocumentChunk> aiDocumentChunks) {
        AiKnowledge knowledge = knowledgeService.getById(entity.getKnowledgeId());
        if (knowledge == null) {
            throw new BusinessException("知识库不存在");
        }
        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null) {
            throw new BusinessException("向量数据库类型未设置");
        }
        // 设置向量模型
        AiLlm aiLlm = aiLlmService.getById(knowledge.getVectorEmbedLlmId());
        if (aiLlm == null) {
            throw new BusinessException("该知识库未配置大模型");
        }
        // 设置向量模型
        Llm embeddingModel = aiLlm.toLlm();
        documentStore.setEmbeddingModel(embeddingModel);

        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        EmbeddingOptions embeddingOptions = new EmbeddingOptions();
        embeddingOptions.setModel(aiLlm.getLlmModel());
        options.setEmbeddingOptions(embeddingOptions);
        options.setIndexName(options.getCollectionName());
        List<Document> documents = new ArrayList<>();
        aiDocumentChunks.forEach(item -> {
                    Document document = new Document();
                    document.setId(item.getId());
                    document.setContent(item.getContent());
                    documents.add(document);
                }
        );
        StoreResult result = documentStore.store(documents, options);
        if (!result.isSuccess()) {
            Log.error("DocumentStore.store failed: " + result);
            throw new BusinessException("DocumentStore.store failed");
        }

        if (knowledge.isSearchEngineEnabled()) {
            // 获取搜索引擎
            DocumentSearcher searcher = searcherFactory.getSearcher();
            // 添加到搜索引擎
            documents.forEach(searcher::addDocument);
        }

        AiKnowledge aiKnowledge = new AiKnowledge();
        aiKnowledge.setId(entity.getKnowledgeId());
        // CanUpdateEmbedLlm false: 不能修改知识库的大模型 true: 可以修改
        AiKnowledge knowledge1 = knowledgeService.getById(entity.getKnowledgeId());
        Map<String, Object> knowledgeoptions = new HashMap<>();
        if (knowledge1.getOptions() == null) {
            knowledgeoptions.put("canUpdateEmbedding", false);
        } else {
            knowledgeoptions = knowledge.getOptions();
            knowledgeoptions.put("canUpdateEmbedding", false);
        }
        aiKnowledge.setOptions(knowledgeoptions);
        knowledgeService.updateById(aiKnowledge);
        return true;
    }

    public DocumentSplitter getDocumentSplitter(String splitterName, int chunkSize, int overlapSize, String regex, int excelRows) {

        if (StringUtil.noText(splitterName)) {
            return null;
        }
        switch (splitterName) {
            case "SimpleDocumentSplitter":
                return new SimpleDocumentSplitter(chunkSize, overlapSize);
            case "RegexDocumentSplitter":
                return new RegexDocumentSplitter(regex);
            case "SimpleTokenizeSplitter":
                if (overlapSize == 0) {
                    return new SimpleTokenizeSplitter(chunkSize);
                } else {
                    return new SimpleTokenizeSplitter(chunkSize, overlapSize);
                }
            case "ExcelDocumentSplitter":
                return new ExcelDocumentSplitter(excelRows);
            default:
                return null;
        }

    }

    public static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return filePath.substring(lastDotIndex + 1);
        }
        return null;
    }

    public static String getFileName(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return filePath.substring(lastDotIndex + 1);
        }
        return null;
    }
}

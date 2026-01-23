package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.document.DocumentSplitter;
import com.agentsflex.core.document.splitter.RegexDocumentSplitter;
import com.agentsflex.core.document.splitter.SimpleDocumentSplitter;
import com.agentsflex.core.document.splitter.SimpleTokenizeSplitter;
import com.agentsflex.core.file2text.File2TextUtil;
import com.agentsflex.core.model.embedding.EmbeddingModel;
import com.agentsflex.core.model.embedding.EmbeddingOptions;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.store.StoreResult;
import com.agentsflex.search.engine.service.DocumentSearcher;
import com.mybatisflex.core.keygen.impl.FlexIDKeyGenerator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.Document;
import tech.aiflowy.ai.entity.DocumentChunk;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.entity.Model;

import static tech.aiflowy.ai.entity.DocumentCollection.KEY_CAN_UPDATE_EMBEDDING_MODEL;
import static tech.aiflowy.ai.entity.DocumentCollection.KEY_SEARCH_ENGINE_TYPE;
import static tech.aiflowy.ai.entity.table.DocumentChunkTableDef.DOCUMENT_CHUNK;
import static tech.aiflowy.ai.entity.table.DocumentTableDef.DOCUMENT;
import tech.aiflowy.ai.mapper.DocumentChunkMapper;
import tech.aiflowy.ai.mapper.DocumentMapper;
import tech.aiflowy.ai.service.DocumentChunkService;
import tech.aiflowy.ai.service.DocumentService;
import tech.aiflowy.ai.service.DocumentCollectionService;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.common.ai.rag.ExcelDocumentSplitter;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.filestorage.FileStorageService;
import tech.aiflowy.common.util.FileUtil;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

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
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {
    protected Logger Log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Resource
    private DocumentMapper documentMapper;

    @Resource
    private DocumentChunkMapper documentChunkMapper;

    @Resource
    private DocumentCollectionService knowledgeService;

    @Resource
    private ModelService modelService;

    @Resource(name = "default")
    FileStorageService storageService;

    @Resource
    private DocumentChunkService documentChunkService;

    @Autowired
    private SearcherFactory searcherFactory;

    @Override
    public Page<Document> getDocumentList(String knowledgeId, int pageSize, int pageNum, String fileName) {
        QueryWrapper queryWrapper=QueryWrapper.create()
                .select(
                        DOCUMENT.ALL_COLUMNS,
                        QueryMethods.count(DOCUMENT_CHUNK.DOCUMENT_ID).as("chunk_count")

                )
                .from(Document.class)
                .leftJoin(DocumentChunk.class).on(DOCUMENT.ID.eq(DOCUMENT_CHUNK.DOCUMENT_ID))
                .where(DOCUMENT.COLLECTION_ID.eq(knowledgeId))
                .orderBy(DOCUMENT.ID, false)
                ;
        if (fileName != null && !fileName.trim().isEmpty()) {
            queryWrapper.and(DOCUMENT.TITLE.like(fileName));
        }
        // 分组
        queryWrapper.groupBy(DOCUMENT.ID);
        Page<Document> documentVoPage = documentMapper.paginateAs(pageNum, pageSize, queryWrapper, Document.class);
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
        QueryWrapper queryWrapperDocument = QueryWrapper.create().eq(Document::getId, id);
        Document oneByQuery = documentMapper.selectOneByQuery(queryWrapperDocument);
        DocumentCollection knowledge = knowledgeService.getById(oneByQuery.getCollectionId());
        if (knowledge == null) {
            return false;
        }

        // 存储到知识库
        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null) {
            return false;
        }

        Model model = modelService.getById(knowledge.getVectorEmbedModelId());
        if (model == null) {
            return false;
        }
        // 设置向量模型
        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        EmbeddingOptions embeddingOptions = new EmbeddingOptions();
        embeddingOptions.setModel(model.getModelName());
        options.setEmbeddingOptions(embeddingOptions);
        options.setCollectionName(knowledge.getVectorStoreCollection());
        // 查询文本分割表tb_document_chunk中对应的有哪些数据，找出来删除
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(DOCUMENT_CHUNK.ID).eq(DocumentChunk::getDocumentId, id);
        List<BigInteger> chunkIds = documentChunkMapper.selectListByQueryAs(queryWrapper, BigInteger.class);
        documentStore.delete(chunkIds, options);
        // 删除搜索引擎中的数据
        if (searcherFactory.getSearcher((String) knowledge.getOptionsByKey(KEY_SEARCH_ENGINE_TYPE)) != null) {
            DocumentSearcher searcher = searcherFactory.getSearcher((String) knowledge.getOptionsByKey(KEY_SEARCH_ENGINE_TYPE));
            chunkIds.forEach(searcher::deleteDocument);
        }
        int ck = documentChunkMapper.deleteByQuery(QueryWrapper.create().eq(DocumentChunk::getDocumentId, id));
        if (ck < 0) {
            return false;
        }
        // 再删除指定路径下的文件
        Document document = documentMapper.selectOneByQuery(queryWrapperDocument);
        storageService.delete(document.getDocumentPath());
        return true;
    }


    @Override
    @Transactional
    public Result<?> textSplit(Integer pageNumber, Integer pageSize, String operation, BigInteger knowledgeId, String filePath, String originalFilename, String splitterName, Integer chunkSize, Integer overlapSize, String regex, Integer rowsPerChunk) {
        try {
            InputStream inputStream = storageService.readStream(filePath);
            Document aiDocument = new Document();
            List<DocumentChunk> previewList = new ArrayList<>();
            DocumentSplitter documentSplitter = getDocumentSplitter(splitterName, chunkSize, overlapSize, regex, rowsPerChunk);
            String content = File2TextUtil.readFromStream(inputStream, originalFilename, null);
            com.agentsflex.core.document.Document document = new com.agentsflex.core.document.Document(content);;
            inputStream.close();
            List<com.agentsflex.core.document.Document> documents = documentSplitter.split(document);
            FlexIDKeyGenerator flexIDKeyGenerator = new FlexIDKeyGenerator();
            int sort = 1;
            for (com.agentsflex.core.document.Document value : documents) {
                DocumentChunk chunk = new DocumentChunk();
                chunk.setId(new BigInteger(String.valueOf(flexIDKeyGenerator.generate(chunk, null))));
                chunk.setContent(value.getContent());
                chunk.setSorting(sort);
                sort++;
                previewList.add(chunk);
            }
            String fileTypeByExtension = FileUtil.getFileTypeByExtension(filePath);
            aiDocument.setDocumentType(fileTypeByExtension);
            aiDocument.setCollectionId(knowledgeId);
            aiDocument.setDocumentPath(filePath);
            aiDocument.setCreated(new Date());
            aiDocument.setModifiedBy(BigInteger.valueOf(StpUtil.getLoginIdAsLong()));
            aiDocument.setModified(new Date());
            aiDocument.setContent(document.getContent());
            aiDocument.setChunkSize(chunkSize);
            aiDocument.setOverlapSize(overlapSize);
            aiDocument.setTitle(originalFilename);
            Map<String, Object> res = new HashMap<>();

            List<DocumentChunk> documentChunks = null;
            // 如果是预览拆分，则返回指定页的数据
            if ("textSplit".equals(operation)){
                int startIndex = (pageNumber - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, previewList.size());

                if (startIndex >= previewList.size()) {
                    documentChunks = new ArrayList<>();
                } else {
                    documentChunks = new ArrayList<>(previewList.subList(startIndex, endIndex));
                }

                res.put("total", previewList.size());
                // 保存文件到知识库
            } else if ("saveText".equals(operation)){
                documentChunks = previewList;
                return this.saveTextResult(documentChunks, aiDocument);
            }

            res.put("previewData", documentChunks);
            res.put("aiDocumentData", aiDocument);
            // 返回分割效果给用户
            return Result.ok(res);
        } catch (IOException e) {
            Log.error(e.toString(), e);
        }

        return Result.fail("操作失败");
    }

    @Override
    public Result<?> saveTextResult(List<DocumentChunk> documentChunks, Document document) {
        Boolean result = storeDocument(document, documentChunks);
        if (result) {
            this.getMapper().insert(document);
            AtomicInteger sort = new AtomicInteger(1);
            documentChunks.forEach(item -> {
                item.setDocumentCollectionId(document.getCollectionId());
                item.setSorting(sort.get());
                item.setDocumentId(document.getId());
                sort.getAndIncrement();
                documentChunkService.save(item);
            });
            return Result.ok();
        }
        return Result.fail(1, "保存失败");
    }

    protected Boolean storeDocument(Document entity, List<DocumentChunk> documentChunks) {
        DocumentCollection knowledge = knowledgeService.getById(entity.getCollectionId());
        if (knowledge == null) {
            throw new BusinessException("知识库不存在");
        }
        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null) {
            throw new BusinessException("向量数据库类型未设置");
        }
        // 设置向量模型
        Model model = modelService.getModelInstance(knowledge.getVectorEmbedModelId());
        if (model == null) {
            throw new BusinessException("该知识库未配置大模型");
        }
        // 设置向量模型
        EmbeddingModel embeddingModel = model.toEmbeddingModel();
        documentStore.setEmbeddingModel(embeddingModel);

        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        EmbeddingOptions embeddingOptions = new EmbeddingOptions();
        embeddingOptions.setModel(model.getModelName());
        embeddingOptions.setDimensions(knowledge.getDimensionOfVectorModel());
        options.setEmbeddingOptions(embeddingOptions);
        options.setIndexName(options.getCollectionName());
        List<com.agentsflex.core.document.Document> documents = new ArrayList<>();
        documentChunks.forEach(item -> {
                    com.agentsflex.core.document.Document document = new com.agentsflex.core.document.Document();
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
            DocumentSearcher searcher = searcherFactory.getSearcher((String) knowledge.getOptionsByKey(KEY_SEARCH_ENGINE_TYPE));
            // 添加到搜索引擎
            documents.forEach(searcher::addDocument);
        }

        DocumentCollection documentCollection = new DocumentCollection();
        documentCollection.setId(entity.getCollectionId());
        Map<String, Object> knowledgeOptions = knowledge.getOptions();
        knowledgeOptions.put(KEY_CAN_UPDATE_EMBEDDING_MODEL, false);
        documentCollection.setOptions(knowledgeOptions);
        knowledgeService.updateById(documentCollection);
        if (knowledge.getDimensionOfVectorModel() == null) {
            int dimension = Model.getEmbeddingDimension(embeddingModel);
            knowledge.setDimensionOfVectorModel(dimension);
            knowledgeService.updateById(knowledge);
        }
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

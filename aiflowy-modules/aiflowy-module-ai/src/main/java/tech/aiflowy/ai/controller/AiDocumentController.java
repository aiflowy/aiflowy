package tech.aiflowy.ai.controller;

import com.agentsflex.core.document.DocumentSplitter;
import com.agentsflex.core.document.splitter.RegexDocumentSplitter;
import com.agentsflex.core.document.splitter.SimpleTokenizeSplitter;
import org.springframework.core.io.ClassPathResource;
import tech.aiflowy.ai.entity.AiDocument;
import tech.aiflowy.ai.entity.AiDocumentChunk;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.ai.service.impl.AiDocumentServiceImpl;
import tech.aiflowy.common.ai.DocumentParserFactory;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.core.utils.JudgeFileTypeUtil;
import tech.aiflowy.common.filestorage.FileStorageService;
import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.document.DocumentParser;
import com.agentsflex.core.document.splitter.SimpleDocumentSplitter;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.store.StoreResult;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiDocument")
public class AiDocumentController extends BaseCurdController<AiDocumentService, AiDocument> {

    private final AiKnowledgeService knowledgeService;
    private final AiDocumentChunkService documentChunkService;
    private final AiLlmService aiLlmService;

    @Autowired
    private AiDocumentService aiDocumentService;

    @Resource(name = "default")
    FileStorageService storageService;

    @Value("${aiflowy.storage.local.root}")
    private  String fileUploadPath;

    public AiDocumentController(AiDocumentService service,
                                AiKnowledgeService knowledgeService,
                                AiDocumentChunkService documentChunkService, AiLlmService aiLlmService) {
        super(service);
        this.knowledgeService = knowledgeService;
        this.documentChunkService = documentChunkService;
        this.aiLlmService = aiLlmService;
    }
    @PostMapping("removeDoc")
    @Transactional
    public Result remove(@JsonBody(value = "id", required = true) String id) {
        List<Serializable> ids = Collections.singletonList(id);
        Result result = onRemoveBefore(ids);
        if (result != null) return result;
        boolean isSuccess = aiDocumentService.removeDoc(id);
        if (!isSuccess){
            return Result.fail(1,"删除失败");
        }
        boolean success = service.removeById(id);
        onRemoveAfter(ids);
        return Result.create(success);
    }

    /**
     *
     * @param documentId 文档id
     * @return
     * @throws IOException
     */
    @PostMapping("docPreview")
    public Result previewFile(@JsonBody(value = "documentId", required = true) String documentId) throws IOException {

        return Result.success(aiDocumentService.previewFile(documentId));
    }


    /**
     * 查询所有所有数据
     *
     * @param entity
     * @param asTree
     * @param sortKey
     * @param sortType
     * @return 所有数据
     */
    @GetMapping("list")
    @Override
    public Result list(AiDocument entity, Boolean asTree, String sortKey, String sortType) {
        String kbSlug = RequestUtil.getParamAsString("id");
        if (StringUtil.noText(kbSlug)) {
            return Result.fail(1, "知识库id不能为空");
        }

        AiKnowledge knowledge = StringUtil.isNumeric(kbSlug)
                ? knowledgeService.getById(kbSlug)
                : knowledgeService.getOne(QueryWrapper.create().eq(AiKnowledge::getSlug, kbSlug));

        if (knowledge == null) {
            return Result.fail(2, "知识库不存在");
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(AiDocument::getKnowledgeId, knowledge.getId());
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<AiDocument> aiDocuments = service.list(queryWrapper);
        List<AiDocument> list = Tree.tryToTree(aiDocuments, asTree);
        return Result.success(list);
    }

    @GetMapping("documentList")
    public Result documentList(@RequestParam(name="fileName", required = false) String fileName, @RequestParam(name="pageSize") int pageSize, @RequestParam(name = "current") int current) {
        String kbSlug = RequestUtil.getParamAsString("id");
        if (StringUtil.noText(kbSlug)) {
            return Result.fail(1, "知识库id不能为空");
        }
        Page<AiDocument> documentList = aiDocumentService.getDocumentList(kbSlug, pageSize, current,fileName);
        return Result.success(documentList);
    }


    @Override
    protected String getDefaultOrderBy() {
        return "order_no asc";
    }


    @PostMapping("update")
    @Override
    public Result update(@JsonBody AiDocument entity) {
        super.update(entity);
        return updatePosition(entity);
    }

    /**
     *
     * @param file 上传的文件
     * @param knowledgeId 知识库id
     * @param chunkSize 分段大小
     * @param overlapSize 分段重叠长度
     * @param userWillSave 用户的操作是否要保存当前上传的文件 true 保存  false 不保存， 用户只预览上传文件后分割的效果
     * @return
     * @throws IOException
     */
    @Transactional
    @PostMapping(value = "upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result upload(@RequestParam("file") MultipartFile file, @RequestParam("knowledgeId") BigInteger knowledgeId,
                         @RequestParam(name="splitterName", required = false) String splitterName,
                         @RequestParam(name="chunkSize", required = false) Integer chunkSize,
                         @RequestParam(name="overlapSize", required = false) Integer overlapSize,
                         @RequestParam(name="regex", required = false) String regex,
                         @RequestParam(name="userWillSave") boolean userWillSave
    ) throws IOException {

        String fileTypeByExtension = JudgeFileTypeUtil.getFileTypeByExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(fileTypeByExtension)){
            return Result.fail(1,"不支持的文档类型");
        }
        DocumentParser documentParser = DocumentParserFactory.getDocumentParser(file.getOriginalFilename());
        if (documentParser == null) {
            return Result.fail(2, "can not support the file type: " + file.getOriginalFilename());
        }
        String path = storageService.save(file);
        AiDocument aiDocument = new AiDocument();
        try (InputStream stream = storageService.readStream(path);) {
            Document document = documentParser.parse(stream);
            aiDocument.setContent(document.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //如果用户是预览分割效果
        if (!userWillSave){
            List<AiDocumentChunk> previewList = new ArrayList<>();
            // 设置分割器 todo 未来可以通过参数来指定分割器，不同的文档使用不同的分割器效果更好
            DocumentSplitter documentSplitter = getDocumentSplitter(splitterName, chunkSize, overlapSize, regex);
            Document document = Document.of(aiDocument.getContent());
            List<Document> documents = documentSplitter.split(document);
            int sort = 1;
            for (Document value : documents) {
                AiDocumentChunk chunk = new AiDocumentChunk();
                chunk.setContent(value.getContent());
                chunk.setSorting(sort);
                sort++;
                previewList.add(chunk);
            }
            // 删除本地文件
            AiDocumentServiceImpl.deleteFile(getRootPath() + path);
            Map res = new HashMap();
            res.put("data", previewList);
            res.put("userWillSave", false);
            // 返回分割效果给用户
            return Result.success(res);
        }


        aiDocument.setDocumentType(fileTypeByExtension);
        aiDocument.setKnowledgeId(knowledgeId);
        aiDocument.setDocumentPath(path);
        aiDocument.setCreated(new Date());
        aiDocument.setModifiedBy(BigInteger.valueOf(StpUtil.getLoginIdAsLong()));
        aiDocument.setModified(new Date());

        if (chunkSize != null && chunkSize != 0){
            aiDocument.setChunkSize(chunkSize);
        } else {
            aiDocument.setChunkSize(200);
        }
        if (overlapSize != null && overlapSize != 0){
            aiDocument.setOverlapSize(overlapSize);
        } else {
            aiDocument.setOverlapSize(100);
        }
        aiDocument.setTitle(StringUtil.removeFileExtension(file.getOriginalFilename()));

        super.save(aiDocument);
        return storeDocument(aiDocument, splitterName, chunkSize, overlapSize, regex);
    }


    /**
     * 更新 entity
     *
     * @param entity
     * @return Result
     */
    private Result updatePosition(AiDocument entity) {
        Integer orderNo = entity.getOrderNo();
        if (orderNo != null) {
            if (orderNo <= 0) orderNo = 0;
            BigInteger knowledgeId = service.getById(entity.getId()).getKnowledgeId();
            List<AiDocument> list = service.list(QueryWrapper.create()
                    .eq(AiDocument::getKnowledgeId, knowledgeId)
                    .orderBy(getDefaultOrderBy())
            );

            list.removeIf(item -> item.getId().equals(entity.getId()));
            if (orderNo >= list.size()) {
                list.add(entity);
            } else {
                list.add(orderNo, entity);
            }

            List<AiDocument> updateList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                AiDocument updateItem = new AiDocument();
                updateItem.setId(list.get(i).getId());
                updateItem.setOrderNo(i);
                updateList.add(updateItem);
            }

            service.updateBatch(updateList);
        }

        return Result.success();
    }

    /**
     * 文档存储到向量数据库
     *
     * @param entity 将要分割的文档
     * @param splitterName 分割器名称
     * @param chunkSize 分割器名称
     * @param overlapSize 分段大小
     * @param overlapSize 分段重叠大小
     * @param regex 正则表达式
     */
    protected Result storeDocument(AiDocument entity, String splitterName, int chunkSize, int overlapSize, String regex) {
        entity = service.getById(entity.getId());
        AiKnowledge knowledge = knowledgeService.getById(entity.getKnowledgeId());
        if (knowledge == null) {
            return Result.fail(1, "知识库不存在");
        }
        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null){
            return Result.fail(2, "向量数据库类型未设置");
        }
        // 设置向量模型
        AiLlm aiLlm = aiLlmService.getById(knowledge.getVectorEmbedLlmId());
        if (aiLlm == null) {
            return Result.fail(3, "该知识库未配置大模型");

        }
        // 设置向量模型
        Llm embeddingModel = aiLlm.toLlm();
        documentStore.setEmbeddingModel(embeddingModel);

        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());

        if (entity.getId() != null) {
            List<AiDocumentChunk> documentChunks = documentChunkService.list(QueryWrapper.create()
                    .eq(AiDocumentChunk::getDocumentId, entity.getId()));

            if (documentChunks != null && !documentChunks.isEmpty()) {
                List<BigInteger> chunkIds = documentChunks.stream()
                        .map(AiDocumentChunk::getId)
                        .collect(Collectors.toList());

                //移除所有的文档分段内容
                documentChunkService.removeByIds(chunkIds);

                //移除向量数据库的所有内容
                documentStore.delete(chunkIds);
            }
        }

        // 设置分割器 todo 未来可以通过参数来指定分割器，不同的文档使用不同的分割器效果更好
        documentStore.setDocumentSplitter(getDocumentSplitter(splitterName, chunkSize, overlapSize, regex));

        AiDocument finalEntity = entity;
        AtomicInteger sort  = new AtomicInteger(1);
        // 设置文档ID生成器
        documentStore.setDocumentIdGenerator(document -> {
            AiDocumentChunk chunk = new AiDocumentChunk();
            chunk.setContent(document.getContent());
            chunk.setDocumentId(finalEntity.getId());
            chunk.setKnowledgeId(finalEntity.getKnowledgeId());
            chunk.setSorting(sort.get());
            boolean success = documentChunkService.save(chunk);
           sort.getAndIncrement();

            if (success) {
                return chunk.getId();
            } else {
                throw new IllegalStateException("Can not save document chunk");
            }
        });

        Document document = Document.of(entity.getContent());

        StoreResult result = documentStore.store(document, options);
        if (!result.isSuccess()) {
            LoggerFactory.getLogger(AiDocumentController.class).error("DocumentStore.store failed: " + result);
        }
        AiKnowledge aiKnowledge = new AiKnowledge();
        aiKnowledge.setId(entity.getKnowledgeId());
        // CanUpdateEmbedLlm false: 不能修改知识库的大模型 true: 可以修改
        aiKnowledge.setCanUpdateEmbedLlm(false);
        knowledgeService.updateById(aiKnowledge);
        return Result.success();
    }

    public String getRootPath() {
        if (StringUtil.hasText(this.fileUploadPath)) {
            return this.fileUploadPath;
        }
        ClassPathResource fileResource = new ClassPathResource("/");
        try {
            return new File(fileResource.getFile(), "/public").getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DocumentSplitter getDocumentSplitter (String splitterName, int chunkSize, int overlapSize, String regex){

        if (StringUtil.noText(splitterName)) {
            return null;
        }
        switch (splitterName) {
            case "SimpleDocumentSplitter":
                return new SimpleDocumentSplitter(chunkSize, overlapSize);
            case "RegexDocumentSplitter":
                return new RegexDocumentSplitter(regex);
            case "SimpleTokenizeSplitter":
                if (overlapSize == 0){
                    return new SimpleTokenizeSplitter(chunkSize);
                } else {
                    return new SimpleTokenizeSplitter(chunkSize, overlapSize);
                }
            default:
                return null;
        }

    }

}
package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.document.DocumentParser;
import com.agentsflex.core.document.DocumentSplitter;
import com.agentsflex.core.document.splitter.RegexDocumentSplitter;
import com.agentsflex.core.document.splitter.SimpleDocumentSplitter;
import com.agentsflex.core.document.splitter.SimpleTokenizeSplitter;
import com.agentsflex.core.llm.embedding.EmbeddingOptions;
import com.alibaba.fastjson.JSON;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.ai.controller.AiDocumentController;
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
import tech.aiflowy.common.ai.MarkdownParser;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.store.StoreResult;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.filestorage.FileStorageService;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.core.utils.JudgeFileTypeUtil;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service("AiService")
public class AiDocumentServiceImpl extends ServiceImpl<AiDocumentMapper, AiDocument> implements AiDocumentService {

    @javax.annotation.Resource
    private AiDocumentMapper aiDocumentMapper;

    @javax.annotation.Resource
    private AiDocumentChunkMapper aiDocumentChunkMapper;


    @Value("${aiflowy.storage.local.root}")
    private  String fileUploadPath;

    @Resource
    private  AiKnowledgeService knowledgeService;

    @Resource
    private  AiLlmService aiLlmService;

    @Resource(name = "default")
    FileStorageService storageService;

    @Resource
    private  AiDocumentChunkService documentChunkService;


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
     *  根据文档id删除文件
     * @param id 文档id
     * @return
     */
    @Override
    public boolean removeDoc(String id) {
        // 查询该文档对应哪些分割的字段，先删除
        QueryWrapper where = QueryWrapper.create().where("document_id = ? ", id);
        QueryWrapper aiDocumentWapper =  QueryWrapper.create().where("id = ? ", id);
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
        // 查询文本分割表tb_ai_document_chunk中对应的有哪些数据，找出来删除
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("id").from("tb_ai_document_chunk").where("document_id = ?", id);
        List<BigInteger> chunkIds =  aiDocumentChunkMapper.selectListByQueryAs(queryWrapper, BigInteger.class);
        StoreResult deleteResult =  documentStore.delete(chunkIds, options);
//        if (!deleteResult.isSuccess()){
//            return false;
//        }
        int ck = aiDocumentChunkMapper.deleteByQuery(where);
        if (ck < 0){
            return false;
        }
        // 再删除指定路径下的文件
        QueryWrapper wrapper = QueryWrapper.create().where("id = ?", id);
        AiDocument aiDocument = aiDocumentMapper.selectOneByQuery(wrapper);
        // 删除 s3 下的文件
        if (aiDocument.getDocumentPath().startsWith("http")){
            storageService.delete(aiDocument.getDocumentPath());
        } else {
            String filePath = getRootPath() + aiDocument.getDocumentPath();
            deleteFile(filePath);
        }
        return true;
    }

    /**
     * 文件预览
     * @param documentId
     * @return
     */
    @Override
    public ResponseEntity<?> previewFile(String documentId) throws IOException {
        QueryWrapper wrapper = QueryWrapper.create().where("id = ?", documentId);
        AiDocument aiDocument = aiDocumentMapper.selectOneByQuery(wrapper);
        String FILE_DIRECTORY = getRootPath() + aiDocument.getDocumentPath();
        // 构建文件路径
        Path filePath = Paths.get(FILE_DIRECTORY);
//        Path filePath = Paths.get(FILE_DIRECTORY).resolve(fileName).normalize();
        org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());

        // 检查文件是否存在
        if (!resource.exists()) {
            return ResponseEntity.status(404).body("文件不存在或无法读取！");
        }

        // 获取文件扩展名
        String fileExtension = aiDocument.getDocumentType();

        // 根据文件类型处理
        switch (fileExtension.toLowerCase()) {
            case "txt":
                // 文本文件：直接返回内容
                String textContent = new String(Files.readAllBytes(filePath));
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(textContent);
            case "xlsx":
                // 文本文件：直接返回内容
                String excelToMarkdownContent = aiDocument.getContent();
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(excelToMarkdownContent);

            case "jpg":
            case "jpeg":
            case "png":
                // 图片文件：返回 Base64 编码
                byte[] imageBytes = Files.readAllBytes(filePath);
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                String mimeType = Files.probeContentType(filePath);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        .body(base64Image);

            case "pdf":
                // PDF 文件：返回 Base64 编码
//                byte[] pdfBytes = Files.readAllBytes(filePath);
//                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(aiDocument.getDocumentPath());

            case "docx":
                // DOCX 文件：提取文本内容
                try (FileInputStream fis = new FileInputStream(filePath.toFile());
                     XWPFDocument document = new XWPFDocument(fis)) {

                    StringBuilder content = new StringBuilder();

                    // 读取段落
                    for (XWPFParagraph paragraph : document.getParagraphs()) {
                        content.append(paragraph.getText()).append("\n");
                    }

                    // 读取表格
                    for (XWPFTable table : document.getTables()) {
                        for (XWPFTableRow row : table.getRows()) {
                            for (XWPFTableCell cell : row.getTableCells()) {
                                content.append(cell.getText()).append("\t");
                            }
                            content.append("\n");
                        }
                    }

                    return ResponseEntity.ok()
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(content.toString());
                }
            case "md":
                // Markdown 文件：解析为纯文本
                try (InputStream inputStream = Files.newInputStream(filePath)) {
                    MarkdownParser markdownParser = new MarkdownParser();
                    String plainText = markdownParser.parseToPlainText(inputStream);
                    return ResponseEntity.ok()
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(plainText);
                }
            default:
                // 其他文件类型：提示用户下载
                return ResponseEntity.status(400).body("不支持预览的文件类型，请下载查看！");
        }
    }

    @Override
    public Result textSplit(BigInteger knowledgeId, MultipartFile file, String splitterName, Integer chunkSize, Integer overlapSize, String regex, Integer rowsPerChunk) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DocumentParser documentParser = DocumentParserFactory.getDocumentParser(file.getOriginalFilename());
        AiDocument aiDocument = new AiDocument();
        List<AiDocumentChunk> previewList = new ArrayList<>();
        DocumentSplitter documentSplitter = getDocumentSplitter(splitterName, chunkSize, overlapSize, regex, rowsPerChunk);
        com.agentsflex.core.document.Document document = null;
        if (documentParser != null) {
            document = documentParser.parse(inputStream);
        }
        List<com.agentsflex.core.document.Document> documents = documentSplitter.split(document);
        int sort = 1;
        for (Document value : documents) {
            AiDocumentChunk chunk = new AiDocumentChunk();
            chunk.setContent(value.getContent());
            chunk.setSorting(sort);
            sort++;
            previewList.add(chunk);
        }
        String fileTypeByExtension = JudgeFileTypeUtil.getFileTypeByExtension(file.getOriginalFilename());
        String filePath = storageService.save(file);
        aiDocument.setDocumentType(fileTypeByExtension);
        aiDocument.setKnowledgeId(knowledgeId);
        aiDocument.setDocumentPath(filePath);
        aiDocument.setCreated(new Date());
        aiDocument.setModifiedBy(BigInteger.valueOf(StpUtil.getLoginIdAsLong()));
        aiDocument.setModified(new Date());
        aiDocument.setContent(document.getContent());
        aiDocument.setChunkSize(chunkSize);
        aiDocument.setOverlapSize(overlapSize);
        aiDocument.setTitle(StringUtil.removeFileExtension(file.getOriginalFilename()));
        Map<String, Object> res = new HashMap<>();
        res.put("previewData", previewList);
        res.put("aiDocumentData", aiDocument);
        // 返回分割效果给用户
        return Result.success(res);
    }

    @Override
    @Transactional
    public Result saveTextResult(BigInteger knowledgeId, String previewListStr, String aiDocumentStr) {
        AiDocument aiDocument = JSON.parseObject(aiDocumentStr, AiDocument.class);
        List<AiDocumentChunk> aiDocumentChunks = JSON.parseArray(previewListStr, AiDocumentChunk.class);
        this.getMapper().insert(aiDocument);
        AtomicInteger sort = new AtomicInteger(1);
        aiDocumentChunks.forEach(item ->{
            item.setKnowledgeId(aiDocument.getKnowledgeId());
            item.setSorting(sort.get());
            item.setDocumentId(aiDocument.getId());
            sort.getAndIncrement();
            documentChunkService.save(item);
        });
        return storeDocument(aiDocument, aiDocumentChunks);
    }
    protected Result storeDocument(AiDocument entity, List<AiDocumentChunk> aiDocumentChunks) {
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
        EmbeddingOptions embeddingOptions = new EmbeddingOptions();
        embeddingOptions.setModel(aiLlm.getLlmModel());
        options.setEmbeddingOptions(embeddingOptions);
        List<Document> documents = new ArrayList<>();
        aiDocumentChunks.forEach(item ->{
                    Document document = new Document();
                    document.setId(item.getId());
                    document.setContent(item.getContent());
                    documents.add(document);
                }
        );
        StoreResult result = documentStore.store(documents, options);
        if (!result.isSuccess()) {
            LoggerFactory.getLogger(AiDocumentController.class).error("DocumentStore.store failed: " + result);
        }
        AiKnowledge aiKnowledge = new AiKnowledge();
        aiKnowledge.setId(entity.getKnowledgeId());
        // CanUpdateEmbedLlm false: 不能修改知识库的大模型 true: 可以修改
        AiKnowledge knowledge1 = knowledgeService.getById(entity.getKnowledgeId());
        Map<String, Object> knowledgeoptions =  new HashMap<>();
        if (knowledge1.getOptions() == null){
            knowledgeoptions.put("canUpdateEmbedding", false);
        } else {
            knowledgeoptions = knowledge.getOptions();
            knowledgeoptions.put("canUpdateEmbedding", false);
        }
        aiKnowledge.setOptions(knowledgeoptions);
        knowledgeService.updateById(aiKnowledge);
        return Result.success();
    }

    public DocumentSplitter getDocumentSplitter (String splitterName, int chunkSize, int overlapSize, String regex, int excelRows){

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
            case "ExcelDocumentSplitter":
                return new ExcelDocumentSplitter(excelRows);
            default:
                return null;
        }

    }

    public static boolean deleteFile(String filePath){
        // 判断当前文件存储的地方是本地，还是s3
        Path path = Paths.get(filePath);
        try {
            // 删除文件
            Files.delete(path);
            return true;
        } catch (NoSuchFileException e) {
            System.out.println("文件不存在：" + e.getMessage());
        } catch (DirectoryNotEmptyException e) {
            System.out.println("目录不为空，无法删除：" + e.getMessage());
        } catch (IOException e) {
            System.out.println("发生 I/O 错误：" + e.getMessage());
        } finally {
            return false;
        }
    }

    private String getRootPath() {
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


}

package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.amazonaws.util.IOUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.ai.entity.AiDocument;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import tech.aiflowy.ai.service.AiDocumentService;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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


    @Autowired
    private AiDocumentService aiDocumentService;


    @Value("${aiflowy.storage.local.root}")
    private  String fileUploadPath;


    public AiDocumentController(AiDocumentService service,
                                AiKnowledgeService knowledgeService,
                                AiDocumentChunkService documentChunkService, AiLlmService aiLlmService) {
        super(service);
        this.knowledgeService = knowledgeService;
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
     * 文本拆分
     * @param file 文件
     * @param splitterName 拆分器名称
     * @param chunkSize 分段大小
     * @param overlapSize 重叠大小
     * @param regex 正则表达式
     * @param rowsPerChunk excel 分割段数
     */
    @PostMapping(value = "textSplit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result textSplit(
                              @RequestParam("file") MultipartFile file,
                              @RequestParam(name="knowledgeId") BigInteger knowledgeId,
                              @RequestParam(name="splitterName", required = false) String splitterName,
                              @RequestParam(name="chunkSize", required = false) Integer chunkSize,
                              @RequestParam(name="overlapSize", required = false) Integer overlapSize,
                              @RequestParam(name="regex", required = false) String regex,
                              @RequestParam(name="rowsPerChunk", required = false) Integer rowsPerChunk
                              ) {
        if (chunkSize == null){
            chunkSize = 100;
        }
        if (overlapSize == null){
            overlapSize = 200;
        }
        if (rowsPerChunk == null){
            rowsPerChunk = 1;
        }
        return aiDocumentService.textSplit(knowledgeId, file, splitterName, chunkSize, overlapSize, regex, rowsPerChunk);

    }

    /**
     *
     * @param knowledgeId 知识库id
     * @param previewList 文本分割list
     * @param aiDocument 文档信息
     * @return
     * @throws IOException
     */
    @PostMapping(value = "saveText")
    public Result saveTextResult(
                         @RequestParam("knowledgeId") BigInteger knowledgeId,
                         @RequestParam(name="previewData") String previewList,
                         @RequestParam(name="aiDocumentData") String aiDocument
    )  {
        return aiDocumentService.saveTextResult(knowledgeId, previewList, aiDocument);
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


    @GetMapping("/download")
    @SaIgnore
    public void downloadDocument(@RequestParam String documentId, HttpServletResponse response) throws IOException {
        // 1. 从数据库获取文件信息
        QueryWrapper queryWrapper = QueryWrapper.create().select("*").where("id = ?", documentId);
        AiDocument aiDocument = service.getOne(queryWrapper);

        if (aiDocument == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }

        // 2. 获取文件路径
        String filePath = getRootPath() + aiDocument.getDocumentPath();
        File file = new File(filePath);

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }

        // 3. 构造文件名并设置响应头
        String originalFilename = aiDocument.getTitle() + "." + aiDocument.getDocumentType();
        String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.name())
                .replaceAll("\\+", "%20");

        response.setContentType("application/octet-stream");
        response.setContentLengthLong(file.length());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

        // 缓存控制
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 4. 输出文件流
        try (FileInputStream fis = new FileInputStream(file)) {
            IOUtils.copy(fis, response.getOutputStream());
        }
    }


}

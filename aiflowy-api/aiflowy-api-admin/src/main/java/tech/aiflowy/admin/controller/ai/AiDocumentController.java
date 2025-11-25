package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
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
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
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
@UsePermission(moduleName = "/api/v1/aiKnowledge")
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
    @SaCheckPermission("/api/v1/aiKnowledge/remove")
    public Result<?> remove(@JsonBody(value = "id", required = true) String id) {
        List<Serializable> ids = Collections.singletonList(id);
        Result<?> result = onRemoveBefore(ids);
        if (result != null) return result;
        boolean isSuccess = aiDocumentService.removeDoc(id);
        if (!isSuccess){
            return Result.ok(false);
        }
        boolean success = service.removeById(id);
        onRemoveAfter(ids);
        return Result.ok(success);
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
    @SaCheckPermission("/api/v1/aiKnowledge/query")
    public Result<List<AiDocument>> list(AiDocument entity, Boolean asTree, String sortKey, String sortType) {
        String kbSlug = RequestUtil.getParamAsString("id");
        if (StringUtil.noText(kbSlug)) {
            throw new BusinessException("知识库id不能为空");
        }

        AiKnowledge knowledge = StringUtil.isNumeric(kbSlug)
                ? knowledgeService.getById(kbSlug)
                : knowledgeService.getOne(QueryWrapper.create().eq(AiKnowledge::getSlug, kbSlug));

        if (knowledge == null) {
            throw new BusinessException("知识库不存在");
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(AiDocument::getKnowledgeId, knowledge.getId());
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<AiDocument> aiDocuments = service.list(queryWrapper);
        List<AiDocument> list = Tree.tryToTree(aiDocuments, asTree);
        return Result.ok(list);
    }

    @GetMapping("documentList")
    @SaCheckPermission("/api/v1/aiKnowledge/query")
    public Result<Page<AiDocument>> documentList(@RequestParam(name="title", required = false) String fileName, @RequestParam(name="pageSize") int pageSize, @RequestParam(name = "pageNumber") int pageNumber) {
        String kbSlug = RequestUtil.getParamAsString("id");
        if (StringUtil.noText(kbSlug)) {
            throw new BusinessException("知识库id不能为空");
        }
        Page<AiDocument> documentList = aiDocumentService.getDocumentList(kbSlug, pageSize, pageNumber,fileName);
        return Result.ok(documentList);
    }


    @Override
    protected String getDefaultOrderBy() {
        return "order_no asc";
    }


    @PostMapping("update")
    @Override
    @SaCheckPermission("/api/v1/aiKnowledge/save")
    public Result<Boolean> update(@JsonBody AiDocument entity) {
        super.update(entity);
        return Result.ok(updatePosition(entity));
    }

    /**
     * 文本拆分
     * @param filePath 文件路径
     * @param operation textSplit 拆分预览/  saveText 保存
     * @param splitterName 拆分器名称
     * @param chunkSize 分段大小
     * @param overlapSize 重叠大小
     * @param regex 正则表达式
     * @param rowsPerChunk excel 分割段数
     */
    @PostMapping(value = {"textSplit", "/saveText"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @SaCheckPermission("/api/v1/aiKnowledge/save")
    public Result<?> textSplit(
                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                              @RequestParam("operation") String operation,
                              @RequestParam("filePath") String filePath,
                              @RequestParam("fileOriginName") String fileOriginName,
                              @RequestParam(name="knowledgeId") BigInteger knowledgeId,
                              @RequestParam(name="splitterName", required = false) String splitterName,
                              @RequestParam(name="chunkSize", required = false) Integer chunkSize,
                              @RequestParam(name="overlapSize", required = false) Integer overlapSize,
                              @RequestParam(name="regex", required = false) String regex,
                              @RequestParam(name="rowsPerChunk", required = false) Integer rowsPerChunk
                              ) {
        if (pageNumber == null || pageNumber == 0){
            pageNumber = 1;
        }
        if (pageSize == null || pageSize == 0){
            pageSize = 10;
        }
        if (chunkSize == null){
            chunkSize = 512;
        }
        if (overlapSize == null){
            overlapSize = 128;
        }
        if (rowsPerChunk == null){
            rowsPerChunk = 1;
        }
        return aiDocumentService.textSplit(pageNumber, pageSize, operation, knowledgeId, filePath, fileOriginName,  splitterName, chunkSize, overlapSize, regex, rowsPerChunk);

    }

    /**
     * 更新 entity
     *
     * @param entity
     * @return Result
     */
    private boolean updatePosition(AiDocument entity) {
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

        return true;
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

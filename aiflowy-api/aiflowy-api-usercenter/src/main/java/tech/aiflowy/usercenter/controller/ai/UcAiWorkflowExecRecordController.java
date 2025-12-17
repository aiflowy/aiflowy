package tech.aiflowy.usercenter.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiWorkflowExecRecord;
import tech.aiflowy.ai.entity.AiWorkflowRecordStep;
import tech.aiflowy.ai.service.AiWorkflowExecRecordService;
import tech.aiflowy.ai.service.AiWorkflowRecordStepService;
import tech.aiflowy.ai.utils.WorkFlowUtil;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

/**
 * 工作流执行记录
 */
@RestController
@RequestMapping("/userCenter/aiWorkflowExecRecord")
@UsePermission(moduleName = "/api/v1/aiWorkflow")
public class UcAiWorkflowExecRecordController extends BaseCurdController<AiWorkflowExecRecordService, AiWorkflowExecRecord> {

    @Resource
    private AiWorkflowRecordStepService recordStepService;

    public UcAiWorkflowExecRecordController(AiWorkflowExecRecordService service) {
        super(service);
    }

    /**
     * 删除
     */
    @GetMapping("/del")
    @Transactional(rollbackFor = Exception.class)
    @SaCheckPermission("/api/v1/aiWorkflow/remove")
    public Result<Void> del(BigInteger id) {
        LoginAccount account = SaTokenUtil.getLoginAccount();
        AiWorkflowExecRecord record = service.getById(id);
        if (!account.getId().toString().equals(record.getCreatedBy())) {
            return Result.fail(1, "非法请求");
        }
        service.removeById(id);
        QueryWrapper w = QueryWrapper.create();
        w.eq(AiWorkflowRecordStep::getRecordId, id);
        recordStepService.remove(w);
        return Result.ok();
    }

    @GetMapping("getPage")
    public Result<Page<AiWorkflowExecRecord>> getPage(HttpServletRequest request,
                                                   String sortKey,
                                                   String sortType,
                                                   Long pageNumber,
                                                   Long pageSize,
                                                   String queryBegin,
                                                   String queryEnd) {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10L;
        }

        QueryWrapper queryWrapper = buildQueryWrapper(request);
        if (StrUtil.isNotEmpty(queryBegin) && StrUtil.isNotEmpty(queryEnd)) {
            queryWrapper.between(AiWorkflowExecRecord::getStartTime, queryBegin, queryEnd);
        }
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        return Result.ok(queryPage(new Page<>(pageNumber, pageSize), queryWrapper));
    }

    @Override
    protected Page<AiWorkflowExecRecord> queryPage(Page<AiWorkflowExecRecord> page, QueryWrapper queryWrapper) {
        queryWrapper.eq(AiWorkflowExecRecord::getCreatedBy, SaTokenUtil.getLoginAccount().getId().toString());
        Page<AiWorkflowExecRecord> res = super.queryPage(page, queryWrapper);
        for (AiWorkflowExecRecord record : res.getRecords()) {
            record.setWorkflowJson(WorkFlowUtil.removeSensitiveInfo(record.getWorkflowJson()));
        }
        return res;
    }
}
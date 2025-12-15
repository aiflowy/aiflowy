package tech.aiflowy.admin.controller.ai;

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
import java.math.BigInteger;

/**
 * 工作流执行记录
 */
@RestController
@RequestMapping("/api/v1/aiWorkflowExecRecord")
@UsePermission(moduleName = "/api/v1/aiWorkflow")
public class AiWorkflowExecRecordController extends BaseCurdController<AiWorkflowExecRecordService, AiWorkflowExecRecord> {

    @Resource
    private AiWorkflowRecordStepService recordStepService;

    public AiWorkflowExecRecordController(AiWorkflowExecRecordService service) {
        super(service);
    }

    @GetMapping("/del")
    @Transactional(rollbackFor = Exception.class)
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
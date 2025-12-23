package tech.aiflowy.admin.controller.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.WorkflowExecResult;
import tech.aiflowy.ai.entity.WorkflowExecStep;
import tech.aiflowy.ai.service.WorkflowExecResultService;
import tech.aiflowy.ai.service.WorkflowExecStepService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行记录步骤
 */
@RestController
@RequestMapping("/api/v1/aiWorkflowRecordStep")
@UsePermission(moduleName = "/api/v1/aiWorkflow")
public class WorkflowExecStepController extends BaseCurdController<WorkflowExecStepService, WorkflowExecStep> {

    @Resource
    private WorkflowExecResultService execRecordService;

    public WorkflowExecStepController(WorkflowExecStepService service) {
        super(service);
    }

    @GetMapping("/getListByRecordId")
    public Result<List<WorkflowExecStep>> getListByRecordId(BigInteger recordId) {
        if (recordId == null) {
            throw new BusinessException("recordId不能为空！");
        }
        WorkflowExecResult record = execRecordService.getById(recordId);
        String workflowJson = record.getWorkflowJson();
        JSONObject workflow = JSON.parseObject(workflowJson);
        Map<String, String> idTypeMap = new HashMap<>();
        JSONArray nodes = workflow.getJSONArray("nodes");
        for (Object node : nodes) {
            JSONObject nodeObj = (JSONObject) node;
            idTypeMap.put(nodeObj.getString("id"), nodeObj.getString("type"));
        }
        QueryWrapper w = QueryWrapper.create();
        w.eq(WorkflowExecStep::getRecordId, recordId);
        List<WorkflowExecStep> list = service.list(w);
        for (WorkflowExecStep step : list) {
            step.setNodeData(null);
            step.setNodeType(idTypeMap.get(step.getNodeId()));
        }
        return Result.ok(list);
    }
}
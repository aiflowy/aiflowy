package tech.aiflowy.admin.controller.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiWorkflowExecRecord;
import tech.aiflowy.ai.entity.AiWorkflowRecordStep;
import tech.aiflowy.ai.service.AiWorkflowExecRecordService;
import tech.aiflowy.ai.service.AiWorkflowRecordStepService;
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
public class AiWorkflowRecordStepController extends BaseCurdController<AiWorkflowRecordStepService, AiWorkflowRecordStep> {

    @Resource
    private AiWorkflowExecRecordService execRecordService;

    public AiWorkflowRecordStepController(AiWorkflowRecordStepService service) {
        super(service);
    }

    @GetMapping("/getListByRecordId")
    public Result<List<AiWorkflowRecordStep>> getListByRecordId(BigInteger recordId) {
        if (recordId == null) {
            throw new BusinessException("recordId不能为空！");
        }
        AiWorkflowExecRecord record = execRecordService.getById(recordId);
        String workflowJson = record.getWorkflowJson();
        JSONObject workflow = JSON.parseObject(workflowJson);
        Map<String, String> idTypeMap = new HashMap<>();
        JSONArray nodes = workflow.getJSONArray("nodes");
        for (Object node : nodes) {
            JSONObject nodeObj = (JSONObject) node;
            idTypeMap.put(nodeObj.getString("id"), nodeObj.getString("type"));
        }
        QueryWrapper w = QueryWrapper.create();
        w.eq(AiWorkflowRecordStep::getRecordId, recordId);
        List<AiWorkflowRecordStep> list = service.list(w);
        for (AiWorkflowRecordStep step : list) {
            step.setNodeData(null);
            step.setNodeType(idTypeMap.get(step.getNodeId()));
        }
        return Result.ok(list);
    }
}
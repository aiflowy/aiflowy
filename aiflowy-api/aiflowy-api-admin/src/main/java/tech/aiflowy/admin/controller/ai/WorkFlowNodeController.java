//package tech.aiflowy.admin.controller.ai;
//
//import cn.hutool.core.util.IdUtil;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import dev.tinyflow.core.Tinyflow;
//import dev.tinyflow.core.node.ConfirmNode;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import tech.aiflowy.ai.entity.AiWorkflow;
//import tech.aiflowy.ai.service.AiWorkflowService;
//import tech.aiflowy.common.domain.Result;
//import tech.aiflowy.common.web.exceptions.BusinessException;
//
//import javax.annotation.Resource;
//import java.util.List;
//
//@RequestMapping("/api/v1/workflowNode")
//@RestController
//public class WorkFlowNodeController {
//
//    @Resource
//    private AiWorkflowService aiWorkflowService;
//
//    @GetMapping("/getChainParams")
//    public Result<?> getChainParams(String currentId, String workflowId) {
//        if (workflowId.equals(currentId)) {
//            throw new BusinessException("工作流不能作为自身子节点");
//        }
//        JSONObject nodeData = new JSONObject();
//        AiWorkflow workflow = aiWorkflowService.getById(workflowId);
//        if (workflow == null) {
//            throw new BusinessException("工作流不存在: " + workflowId);
//        }
//        nodeData.put("workflowId", workflow.getId());
//        nodeData.put("workflowName", workflow.getTitle());
//        Tinyflow tinyflow = workflow.toTinyflow();
//        Chain chain = tinyflow.toChain();
//        List<ChainNode> nodes = chain.getNodes();
//        JSONArray inputs = new JSONArray();
//        JSONArray outputs = new JSONArray();
//        for (ChainNode node : nodes) {
//            if (node instanceof StartNode) {
//                inputs = JSON.parseArray(JSON.toJSONString(node.getParameters()));
//                handleArray(inputs);
//            }
//            if (node instanceof EndNode) {
//                outputs = JSON.parseArray(JSON.toJSONString(((EndNode) node).getOutputDefs()));
//                handleArray(outputs);
//            }
//            if (node instanceof ConfirmNode) {
//                throw new BusinessException("工作流存在【确认节点】，暂不支持作为子节点");
//            }
//        }
//        nodeData.put("parameters", inputs);
//        nodeData.put("outputDefs", outputs);
//        return Result.ok(nodeData);
//    }
//
//    private void handleArray(JSONArray array) {
//        if (array != null) {
//            for (Object o : array) {
//                JSONObject obj = (JSONObject) o;
//                obj.put("id", IdUtil.simpleUUID());
//                obj.put("nameDisabled", true);
//                obj.put("dataTypeDisabled", true);
//                obj.put("deleteDisabled", true);
//                obj.put("addChildDisabled", true);
//                obj.put("refType", "ref");
//                JSONArray children = obj.getJSONArray("children");
//                if (children != null) {
//                    handleArray(children);
//                }
//            }
//        }
//    }
//}

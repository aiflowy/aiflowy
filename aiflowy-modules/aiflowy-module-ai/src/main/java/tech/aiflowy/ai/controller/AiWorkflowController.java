package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.chain.*;
import com.agentsflex.core.chain.event.ChainStatusChangeEvent;
import com.agentsflex.core.chain.event.NodeEndEvent;
import com.agentsflex.core.chain.event.NodeStartEvent;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.parser.NodeParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.common.ai.MySseEmitter;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.service.SysApiKeyService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiWorkflow")
public class AiWorkflowController extends BaseCurdController<AiWorkflowService, AiWorkflow> {
    private final AiLlmService aiLlmService;

    @Resource
    private SysApiKeyService apiKeyService;

    public AiWorkflowController(AiWorkflowService service, AiLlmService aiLlmService) {
        super(service);
        this.aiLlmService = aiLlmService;
    }

    @PostMapping("/importWorkFlow")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result importWorkFlow(AiWorkflow workflow, MultipartFile jsonFile) throws Exception {
        InputStream is = jsonFile.getInputStream();
        String content = IoUtil.read(is, StandardCharsets.UTF_8);
        workflow.setContent(content);
        save(workflow);
        return Result.success();
    }

    @GetMapping("/exportWorkFlow")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result exportWorkFlow(BigInteger id) {
        AiWorkflow workflow = service.getById(id);
        return Result.success("content", workflow.getContent());
    }

    @GetMapping("getRunningParameters")
    @SaCheckPermission("/api/v1/aiWorkflow/query")
    public Result getRunningParameters(@RequestParam BigInteger id) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "can not find the workflow by id: " + id);
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        if (tinyflow == null) {
            return Result.fail(2, "workflow content is empty! ");
        }

        Chain chain = tinyflow.toChain();
        if (chain == null) {
            return Result.fail(2, "节点配置错误，请检查! ");
        }
        List<Parameter> chainParameters = chain.getParameters();
        return Result.success("parameters", chainParameters)
                .set("title", workflow.getTitle())
                .set("description", workflow.getDescription())
                .set("icon", workflow.getIcon());
    }

    @PostMapping("tryRunning")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result tryRunning(@JsonBody(value = "id", required = true) BigInteger id, @JsonBody("variables") Map<String, Object> variables) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "工作流不存在");
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        Chain chain = tinyflow.toChain();

        if (StpUtil.isLogin()) {
            chain.getMemory().put(Constants.LOGIN_USER_KEY, SaTokenUtil.getLoginAccount());
        }

        Map<String, Object> result = chain.executeForResult(variables);

        return Result.success("result", result).set("message", chain.getMessage());
    }

    @PostMapping("tryRunningStream")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public SseEmitter tryRunningStream(
            @JsonBody(value = "id", required = true) BigInteger id,
            @JsonBody("variables") Map<String, Object> variables,
            HttpServletResponse response) {

        response.setContentType("text/event-stream");

        MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 10));

        AiWorkflow workflow = service.getById(id);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        Chain chain = tinyflow.toChain();

        if (StpUtil.isLogin()) {
            chain.getMemory().put(Constants.LOGIN_USER_KEY, SaTokenUtil.getLoginAccount());
        }

        JSONObject json = new JSONObject();

        chain.addEventListener(new ChainEventListener() {
            @Override
            public void onEvent(ChainEvent event, Chain chain) {
                if (event instanceof NodeStartEvent) {
                    JSONObject content = new JSONObject();
                    ChainNode node = ((NodeStartEvent) event).getNode();
                    content.put("nodeId", node.getId());
                    content.put("status", "start");
                    json.put("content", content);
                    emitter.send(json.toJSONString());
                }
                if (event instanceof NodeEndEvent) {
                    ChainNode node = ((NodeEndEvent) event).getNode();
                    Map<String, Object> result = ((NodeEndEvent) event).getResult();
                    JSONObject content = new JSONObject();
                    content.put("nodeId", node.getId());
                    content.put("status", "end");
                    content.put("res", result);
                    json.put("content", content);
                    emitter.send(json.toJSONString());
                }
                if (event instanceof ChainStatusChangeEvent) {
                    ChainStatus status = ((ChainStatusChangeEvent) event).getStatus();
                    if (ChainStatus.FINISHED_ABNORMAL.equals(status)) {
                        String message = chain.getMessage();
                        JSONObject content = new JSONObject();
                        content.put("status", "error");
                        content.put("errorMsg", message);
                        json.put("content", content);
                        emitter.sendAndComplete(json.toJSONString());
                    }
                }
            }
        });

        chain.addNodeErrorListener(new NodeErrorListener() {
            @Override
            public void onError(Throwable e, ChainNode node, Map<String, Object> map, Chain chain) {
                String message = ExceptionUtil.getRootCauseMessage(e);
                JSONObject content = new JSONObject();
                content.put("nodeId", node.getId());
                content.put("status", "nodeError");
                content.put("errorMsg", message);
                json.put("content", content);
                emitter.sendAndComplete(json.toJSONString());
            }
        });

        ThreadUtil.execAsync(() -> {
            Map<String, Object> result = chain.executeForResult(variables);
            JSONObject content = new JSONObject();
            content.put("execResult", result);
            json.put("content", content);
            emitter.sendAndComplete(json.toJSONString());
        });

        return emitter;
    }

    @SaIgnore
    @GetMapping(value = "/external/getRunningParams", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result externalGetRunningParameters(HttpServletRequest request,
                                               @RequestParam BigInteger id) {
        String apiKey = request.getHeader("Authorization");
        apiKeyService.checkApiKey(apiKey);
        return getRunningParameters(id);
    }

    @SaIgnore
    @PostMapping(value = "/external/run", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result externalRun(HttpServletRequest request,
                              @JsonBody(value = "id", required = true) BigInteger id,
                              @JsonBody("variables") Map<String, Object> variables) {
        String apiKey = request.getHeader("Authorization");
        apiKeyService.checkApiKey(apiKey);
        return tryRunning(id, variables);
    }

    @PostMapping("/singleRun")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result singleRun(
            @JsonBody(value = "id", required = true) BigInteger id,
            @JsonBody(value = "node", required = true) Map<String, Object> node,
            @JsonBody("variables") Map<String, Object> variables) {

        AiWorkflow workflow = service.getById(id);
        if (workflow == null) {
            return Result.fail(1, "工作流不存在");
        }
        List<ChainNode> nodes = new ArrayList<>();
        Tinyflow tinyflow = workflow.toTinyflow();
        Chain fullChain = tinyflow.toChain();
        if (fullChain != null) {
            nodes = fullChain.getNodes();
        }
        Map<String, NodeParser> map = tinyflow.getChainParser().getNodeParserMap();
        NodeParser parser = map.get(node.get("type").toString());
        if (parser == null) {
            return Result.fail(1, "节点类型不存在");
        }
        ChainNode currentNode = parser.parse(new JSONObject(node), tinyflow);
        if (currentNode == null) {
            return Result.fail(1, "节点不存在");
        }
        currentNode.setInwardEdges(null);
        currentNode.setOutwardEdges(null);
        fixParamType(nodes, currentNode);
        Chain chain = new Chain();

        if (StpUtil.isLogin()) {
            chain.getMemory().put(Constants.LOGIN_USER_KEY, SaTokenUtil.getLoginAccount());
        }

        chain.addNode(currentNode);
        Map<String, Object> res = chain.executeForResult(variables);
        return Result.success(res);
    }

    /**
     * 修正引用类的值类型
     */
    private void fixParamType(List<ChainNode> allNodes, ChainNode currentNode) {
        List<Parameter> currentParams = currentNode.getParameters();
        if (CollectionUtil.isEmpty(currentParams)) {
            return;
        }

        Map<String, DataType> refToDataTypeMap = new HashMap<>();

        for (ChainNode node : allNodes) {
            if (node == null || CollectionUtil.isEmpty(node.getParameters())) {
                continue;
            }

            String nodeId = node.getId();
            for (Parameter param : node.getParameters()) {
                if (param != null && param.getName() != null) {
                    refToDataTypeMap.put(nodeId + "." + param.getName(), param.getDataType());
                }
            }
        }

        for (Parameter parameter : currentParams) {
            if (parameter == null || !RefType.REF.equals(parameter.getRefType())) {
                continue;
            }

            parameter.setRefType(RefType.INPUT);
            String ref = parameter.getRef();

            if (StrUtil.isNotEmpty(ref) && refToDataTypeMap.containsKey(ref)) {
                parameter.setDataType(refToDataTypeMap.get(ref));
            }
        }
    }
}
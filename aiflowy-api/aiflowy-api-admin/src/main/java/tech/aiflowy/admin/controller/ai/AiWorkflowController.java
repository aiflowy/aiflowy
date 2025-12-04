package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson.JSONObject;
import com.alicp.jetcache.Cache;
import com.mybatisflex.core.query.QueryWrapper;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.chain.*;
import dev.tinyflow.core.chain.event.ChainStatusChangeEvent;
import dev.tinyflow.core.chain.event.NodeEndEvent;
import dev.tinyflow.core.chain.event.NodeStartEvent;
import dev.tinyflow.core.chain.listener.ChainEventListener;
import dev.tinyflow.core.chain.listener.NodeErrorListener;
import dev.tinyflow.core.chain.runtime.ChainExecutor;
import dev.tinyflow.core.node.ConfirmNode;
import dev.tinyflow.core.parser.NodeParser;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiBotWorkflowService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.common.ai.MySseEmitter;
import tech.aiflowy.common.annotation.NeedApiKeyAccess;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.constant.RedisKey;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.service.SysApiKeyService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private AiBotWorkflowService aiBotWorkflowService;
    @Resource(name = "defaultCache")
    private Cache<String, Object> defaultCache;
    @Resource
    private ChainExecutor chainExecutor;

    public AiWorkflowController(AiWorkflowService service, AiLlmService aiLlmService) {
        super(service);
        this.aiLlmService = aiLlmService;
    }

    @GetMapping("/test")
    @SaIgnore
    public Result<Map<String, Object>> test() {
        String id = "351890611582607360";
        Map<String, Object> variables = new HashMap<>();
        variables.put("single", "a");
        variables.put("resource", "b");
        variables.put("vvv", "c");
        Map<String, Object> execute = chainExecutor.execute(id, variables);
        return Result.ok(execute);
    }

    @PostMapping("/importWorkFlow")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result<Void> importWorkFlow(AiWorkflow workflow, MultipartFile jsonFile) throws Exception {
        InputStream is = jsonFile.getInputStream();
        String content = IoUtil.read(is, StandardCharsets.UTF_8);
        workflow.setContent(content);
        // if (!StringUtils.hasLength(workflow.getAlias())){
        //     workflow.setAlias(UUID.randomUUID().toString().replaceAll("-",""));
        // }
        save(workflow);
        return Result.ok();
    }

    @GetMapping("/exportWorkFlow")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result<String> exportWorkFlow(BigInteger id) {
        AiWorkflow workflow = service.getById(id);
        return Result.ok("", workflow.getContent());
    }

    @GetMapping("getRunningParameters")
    @SaCheckPermission("/api/v1/aiWorkflow/query")
    public Result<?> getRunningParameters(@RequestParam BigInteger id) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "can not find the workflow by id: " + id);
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        if (tinyflow == null) {
            return Result.fail(2, "workflow content is empty! ");
        }

        ChainDefinition definition = tinyflow.toChain();
        if (definition == null) {
            return Result.fail(2, "节点配置错误，请检查! ");
        }
        List<Parameter> chainParameters = definition.getStartParameters();
        Map<String, Object> res = new HashMap<>();
        res.put("parameters", chainParameters);
        res.put("title", workflow.getTitle());
        res.put("description", workflow.getDescription());
        res.put("icon", workflow.getIcon());
        return Result.ok(res);
    }

    @PostMapping("tryRunning")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result<?> tryRunning(@JsonBody(value = "id", required = true) BigInteger id, @JsonBody("variables") Map<String, Object> variables) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "工作流不存在");
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        ChainDefinition definition = tinyflow.toChain();

        if (StpUtil.isLogin()) {
            variables.put(Constants.LOGIN_USER_KEY, SaTokenUtil.getLoginAccount());
        }

        Map<String, Object> result = chainExecutor.execute(definition.getId() ,variables);
        HashMap<String, Object> res = new HashMap<>();
        res.put("result", result);
        return Result.ok(res);
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
        ChainDefinition definition = tinyflow.toChain();

        if (StpUtil.isLogin()) {
            variables.put(Constants.LOGIN_USER_KEY, SaTokenUtil.getLoginAccount());
        }

        String chainExecId = IdUtil.fastSimpleUUID();
        variables.put("chainExecId", chainExecId);

        chainExecutor.executeAsync(definition.getId(), variables);

//        ThreadUtil.execAsync(() -> {
//            Map<String, Object> result = chain.executeForResult(variables);
//            JSONObject content = new JSONObject();
//            content.put("execResult", result);
//            json.put("content", content);
//            chainExecutor.addEventListener(, );
//            emitter.sendAndComplete(json.toJSONString());
//        });

        return emitter;
    }

//    @PostMapping("/resumeChain")
//    public SseEmitter resumeChain(
//            @JsonBody(value = "chainId", required = true) String chainId,
//            @JsonBody("confirmParams") Map<String, Object> confirmParams,
//            HttpServletResponse response) {
//        response.setContentType("text/event-stream");
//        JSONObject json = new JSONObject();
//        MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 10));
//        Object obj = defaultCache.get(RedisKey.CHAIN_SUSPEND_KEY + chainId);
//        if (obj == null) {
//            JSONObject content = new JSONObject();
//            content.put("status", "execOnce");
//            json.put("content", content);
//            emitter.sendAndComplete(json.toJSONString());
//            return emitter;
//        }
//        String chainJson = obj.toString();
//        defaultCache.remove(RedisKey.CHAIN_SUSPEND_KEY + chainId);
//        Chain chain = Chain.fromJSON(chainJson);
//
//        if (StpUtil.isLogin()) {
//            chain.getMemory().put(Constants.LOGIN_USER_KEY, SaTokenUtil.getLoginAccount());
//        }
//
//        addChainEvent(chain, json, emitter, chainId);
//
//        ThreadUtil.execAsync(() -> {
//            chain.resume(confirmParams);
//            Map<String, Object> result = chain.getExecuteResult();
//            JSONObject content = new JSONObject();
//            content.put("execResult", result);
//            json.put("content", content);
//            emitter.sendAndComplete(json.toJSONString());
//        });
//
//        return emitter;
//    }


    @Override
    public Result<AiWorkflow> detail(String id) {
        AiWorkflow workflow = service.getDetail(id);
        return Result.ok(workflow);
    }

//    private void addChainEvent(Chain chain, JSONObject json, MySseEmitter emitter, String chainId) {
//        chain.addEventListener(new ChainEventListener() {
//            @Override
//            public void onEvent(ChainEvent event, Chain chain) {
//                if (event instanceof NodeStartEvent) {
//                    JSONObject content = new JSONObject();
//                    ChainNode node = ((NodeStartEvent) event).getNode();
//                    if ((node instanceof ConfirmNode)) {
//                        chain.getMemory().put("confirmNodeId", node.getId());
//                        //System.out.println("确认节点开始 ---> " + node.getId());
//                    }
//                    content.put("nodeId", node.getId());
//                    content.put("status", "start");
//                    json.put("content", content);
//                    emitter.send(json.toJSONString());
//                }
//                if (event instanceof NodeEndEvent) {
//                    ChainNode node = ((NodeEndEvent) event).getNode();
//                    if ((node instanceof ConfirmNode)) {
//                        chain.getMemory().put("confirmNodeId", node.getId());
//                        //System.out.println("确认节点结束 ---> " + node.getId());
//                    }
//                    Object errorInfo = node.getMemory().get(node.getId() + "|" + "errorInfo");
//                    if (errorInfo != null) {
//                        JSONObject content = new JSONObject();
//                        content.put("nodeId", node.getId());
//                        content.put("status", "nodeError");
//                        content.put("errorMsg", errorInfo);
//                        json.put("content", content);
//                        emitter.sendAndComplete(json.toJSONString());
//                    } else {
//                        Map<String, Object> result = ((NodeEndEvent) event).getResult();
//                        JSONObject content = new JSONObject();
//                        content.put("nodeId", node.getId());
//                        content.put("status", "end");
//                        content.put("res", result);
//                        json.put("content", content);
//                        emitter.send(json.toJSONString());
//                    }
//                }
//                if (event instanceof ChainStatusChangeEvent) {
//                    ChainStatus status = ((ChainStatusChangeEvent) event).getStatus();
//                    if (ChainStatus.FINISHED_ABNORMAL.equals(status)) {
//                        String message = chain.getMessage();
//                        JSONObject content = new JSONObject();
//                        content.put("status", "error");
//                        content.put("errorMsg", message);
//                        json.put("content", content);
//                        emitter.sendAndComplete(json.toJSONString());
//                    }
//                }
//            }
//        });
//
//        chain.addNodeErrorListener(new NodeErrorListener() {
//            @Override
//            public void onError(Throwable e, ChainNode node, Map<String, Object> map, Chain chain) {
//                if (!(e instanceof ChainSuspendException)) {
//                    String message = ExceptionUtil.getMessage(e);
//                    node.getMemory().put(node.getId() + "|" + "errorInfo",message);
//                }
//            }
//        });
//
//        chain.addSuspendListener(new ChainSuspendListener() {
//            @Override
//            public void onSuspend(Chain chain) {
//                Object confirmNodeId = chain.getMemory().get("confirmNodeId");
//                //System.out.println("流程挂起 ---> " + confirmNodeId);
//                String message = chain.getMessage();
//                List<Parameter> suspendForParameters = chain.getSuspendForParameters();
//                JSONObject content = new JSONObject();
//                content.put("chainMessage", message);
//                content.put("nodeId", confirmNodeId);
//                content.put("status", "confirm");
//                content.put("suspendForParameters", suspendForParameters);
//                //String chainId = IdUtil.fastSimpleUUID();
//                String chainJson = chain.toJSON();
//                content.put("chainId", chainId);
//                defaultCache.put(RedisKey.CHAIN_SUSPEND_KEY + chainId, chainJson, 1, TimeUnit.HOURS);
//                json.put("content", content);
//                emitter.sendAndComplete(json.toJSONString());
//            }
//        });
//    }

    @SaIgnore
    @GetMapping(value = "/external/getRunningParams", produces = MediaType.APPLICATION_JSON_VALUE)
    @NeedApiKeyAccess(value = "/api/v1/aiWorkflow/external/getRunningParams")
    @ResponseBody
    public Result<?> externalGetRunningParameters(@RequestParam BigInteger id) {
        return getRunningParameters(id);
    }

    @SaIgnore
    @PostMapping(value = "/external/run", produces = MediaType.APPLICATION_JSON_VALUE)
    @NeedApiKeyAccess(value = "/api/v1/aiWorkflow/external/run")
    @ResponseBody
    public Result<?> externalRun(HttpServletRequest request,
                              @JsonBody(value = "id", required = true) BigInteger id,
                              @JsonBody("variables") Map<String, Object> variables) {
        return tryRunning(id, variables);
    }

//    @PostMapping("/singleRun")
//    @SaCheckPermission("/api/v1/aiWorkflow/save")
//    public Result<?> singleRun(
//            @JsonBody(value = "id", required = true) BigInteger id,
//            @JsonBody(value = "node", required = true) Map<String, Object> node,
//            @JsonBody("variables") Map<String, Object> variables) {
//
//        AiWorkflow workflow = service.getById(id);
//        if (workflow == null) {
//            return Result.fail(1, "工作流不存在");
//        }
//        List<Node> nodes = new ArrayList<>();
//        Tinyflow tinyflow = workflow.toTinyflow();
//        ChainDefinition definition = tinyflow.toChain();
//        if (definition != null) {
//            nodes = definition.getNodes();
//        }
//        Map<String, NodeParser<?>> map = tinyflow.getChainParser().getNodeParserMap();
//        NodeParser parser = map.get(node.get("type").toString());
//        if (parser == null) {
//            return Result.fail(1, "节点类型不存在");
//        }
//        Node currentNode = parser.parse(new JSONObject(node), tinyflow);
//        if (currentNode == null) {
//            return Result.fail(1, "节点不存在");
//        }
//        currentNode.setInwardEdges(null);
//        currentNode.setOutwardEdges(null);
//        fixParamType(nodes, currentNode);
//        Chain chain = new Chain();
//
//        if (StpUtil.isLogin()) {
//            variables.put(Constants.LOGIN_USER_KEY, SaTokenUtil.getLoginAccount());
//        }
//
//        chain.addNode(currentNode);
//        Map<String, Object> res = chain.executeForResult(variables);
//        return Result.ok(res);
//    }

    @GetMapping("/copy")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result<Void> copy(BigInteger id) {
        LoginAccount account = SaTokenUtil.getLoginAccount();
        AiWorkflow workflow = service.getById(id);
        workflow.setId(null);
        workflow.setAlias(IdUtil.fastSimpleUUID());
        commonFiled(workflow, account.getId(), account.getTenantId(), account.getDeptId());
        service.save(workflow);
        return Result.ok();
    }

    /**
     * 修正引用类的值类型
     */
    private void fixParamType(List<Node> allNodes, Node currentNode) {
        List<Parameter> currentParams = currentNode.getParameters();
        if (CollectionUtil.isEmpty(currentParams)) {
            return;
        }

        Map<String, DataType> refToDataTypeMap = new HashMap<>();

        for (Node node : allNodes) {
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


    @Override
    protected Result onSaveOrUpdateBefore(AiWorkflow entity, boolean isSave) {


        String alias = entity.getAlias();

        if (StringUtils.hasLength(alias)){
            AiWorkflow workflow = service.getByAlias(alias);

            if (workflow == null){
                return null;
            }

            if (isSave){
                throw new BusinessException("别名已存在！");
            }

            BigInteger id = entity.getId();

            if (id.compareTo(workflow.getId()) != 0){
                throw new BusinessException("别名已存在！");
            }


        }

        return null;
    }


    @Override
    protected Result onRemoveBefore(Collection<Serializable> ids) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in("workflow_id", ids);
        boolean exists = aiBotWorkflowService.exists(queryWrapper);
        if (exists) {
            return Result.fail(1, "此工作流还关联有bot，请先取消关联后再删除！");
        }

        return null;
    }
}
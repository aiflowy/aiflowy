package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.mybatisflex.core.query.QueryWrapper;
import dev.tinyflow.core.chain.*;
import dev.tinyflow.core.chain.repository.ChainStateRepository;
import dev.tinyflow.core.chain.repository.NodeStateRepository;
import dev.tinyflow.core.chain.runtime.ChainExecutor;
import dev.tinyflow.core.parser.ChainParser;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiBotWorkflowService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.ai.tinyflow.entity.ChainInfo;
import tech.aiflowy.ai.tinyflow.entity.NodeInfo;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.service.SysApiKeyService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    @Resource
    private ChainExecutor chainExecutor;
    @Resource
    private ChainParser chainParser;

    public AiWorkflowController(AiWorkflowService service, AiLlmService aiLlmService) {
        super(service);
        this.aiLlmService = aiLlmService;
    }

    /**
     * 节点单独运行
     */
    @PostMapping("/singleRun")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result<?> singleRun(
            @JsonBody(value = "workflowId", required = true) BigInteger workflowId,
            @JsonBody(value = "nodeId", required = true) String nodeId,
            @JsonBody("variables") Map<String, Object> variables) {

        AiWorkflow workflow = service.getById(workflowId);
        if (workflow == null) {
            return Result.fail(1, "工作流不存在");
        }
        Map<String, Object> res = chainExecutor.executeNode(workflowId.toString(), nodeId, variables);
        return Result.ok(res);
    }

    /**
     * 运行工作流 - v2
     */
    @PostMapping("/runAsync")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result<String> runAsync(@JsonBody(value = "id", required = true) BigInteger id,
                                   @JsonBody("variables") Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        AiWorkflow workflow = service.getById(id);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }
        if (StpUtil.isLogin()) {
            variables.put(Constants.LOGIN_USER_KEY, SaTokenUtil.getLoginAccount());
        }
        String executeId = chainExecutor.executeAsync(id.toString(), variables);
        return Result.ok(executeId);
    }

    /**
     * 获取工作流运行状态 - v2
     */
    @PostMapping("/getChainStatus")
    public Result<ChainInfo> getChainStatus(@JsonBody(value = "executeId") String executeId,
                                            @JsonBody("nodes") List<NodeInfo> nodes) {
        ChainStateRepository chainStateRepository = chainExecutor.getChainStateRepository();
        NodeStateRepository nodeStateRepository = chainExecutor.getNodeStateRepository();

        ChainState chainState = chainStateRepository.load(executeId);

        ChainInfo res = new ChainInfo();
        res.setExecuteId(executeId);
        res.setStatus(chainState.getStatus().getValue());
        ExceptionSummary chainError = chainState.getError();
        if (chainError != null) {
            res.setMessage(chainError.getRootCauseClass() + " --> " + chainError.getRootCauseMessage());
        }
        Map<String, Object> executeResult = chainState.getExecuteResult();
        if (executeResult != null && !executeResult.isEmpty()) {
            res.setResult(executeResult);
        }

        Set<String> childStateIds = chainState.getChildStateIds();

        for (NodeInfo node : nodes) {
            String nodeId = node.getNodeId();
            NodeState nodeState = nodeStateRepository.load(executeId, nodeId);
            if (NodeStatus.READY.equals(nodeState.getStatus())) {
                if (childStateIds != null && !childStateIds.isEmpty()) {
                    for (String childStateId : childStateIds) {
                        ChainState childChainState = chainStateRepository.load(childStateId);
                        NodeState nodeStateInChild = nodeStateRepository.load(childStateId, nodeId);
                        setNodeStatus(node, nodeStateInChild, childChainState);
                    }
                }
            } else {
                setNodeStatus(node, nodeState, chainState);
            }
            res.getNodes().put(nodeId, node);
        }
        return Result.ok(res);
    }

    private void setNodeStatus(NodeInfo node, NodeState nodeState, ChainState chainState) {
        String nodeId = node.getNodeId();
        node.setStatus(nodeState.getStatus().getValue());
        ExceptionSummary error = nodeState.getError();
        if (error != null) {
            node.setMessage(error.getRootCauseClass() + " --> " + error.getRootCauseMessage());
        }
        Map<String, Object> nodeExecuteResult = chainState.getNodeExecuteResult(nodeId);
        if (nodeExecuteResult != null && !nodeExecuteResult.isEmpty()) {
            node.setResult(nodeExecuteResult);
        }
        node.setSuspendForParameters(chainState.getSuspendForParameters());
    }

    /**
     * 恢复工作流运行 - v2
     */
    @PostMapping("/resume")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result<Void> resume(@JsonBody(value = "executeId", required = true) String executeId,
                               @JsonBody("confirmParams") Map<String, Object> confirmParams) {
        chainExecutor.resumeAsync(executeId, confirmParams);
        return Result.ok();
    }

    @PostMapping("/importWorkFlow")
    @SaCheckPermission("/api/v1/aiWorkflow/save")
    public Result<Void> importWorkFlow(AiWorkflow workflow, MultipartFile jsonFile) throws Exception {
        InputStream is = jsonFile.getInputStream();
        String content = IoUtil.read(is, StandardCharsets.UTF_8);
        workflow.setContent(content);
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

        ChainDefinition definition = chainParser.parse(workflow.getContent());
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

    @Override
    public Result<AiWorkflow> detail(String id) {
        AiWorkflow workflow = service.getDetail(id);
        return Result.ok(workflow);
    }

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

    @Override
    protected Result onSaveOrUpdateBefore(AiWorkflow entity, boolean isSave) {

        String alias = entity.getAlias();
        if (StringUtils.hasLength(alias)) {
            AiWorkflow workflow = service.getByAlias(alias);
            if (workflow == null) {
                return null;
            }
            if (isSave) {
                throw new BusinessException("别名已存在！");
            }
            BigInteger id = entity.getId();
            if (id.compareTo(workflow.getId()) != 0) {
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
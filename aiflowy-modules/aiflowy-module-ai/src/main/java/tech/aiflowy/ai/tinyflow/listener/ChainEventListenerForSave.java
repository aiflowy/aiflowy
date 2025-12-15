package tech.aiflowy.ai.tinyflow.listener;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import dev.tinyflow.core.chain.*;
import dev.tinyflow.core.chain.event.*;
import dev.tinyflow.core.chain.listener.ChainEventListener;
import dev.tinyflow.core.chain.repository.NodeStateField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.entity.AiWorkflowExecRecord;
import tech.aiflowy.ai.entity.AiWorkflowRecordStep;
import tech.aiflowy.ai.service.AiWorkflowExecRecordService;
import tech.aiflowy.ai.service.AiWorkflowRecordStepService;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.ai.utils.WorkFlowUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.EnumSet;

@Component
public class ChainEventListenerForSave implements ChainEventListener {


    private static final Logger log = LoggerFactory.getLogger(ChainEventListenerForSave.class);

    @Resource
    private AiWorkflowService aiWorkflowService;
    @Resource
    private AiWorkflowExecRecordService aiWorkflowExecRecordService;
    @Resource
    private AiWorkflowRecordStepService aiWorkflowRecordStepService;

    @Override
    public void onEvent(Event event, Chain chain) {
        if (event instanceof ChainStartEvent) {
            handleChainStartEvent((ChainStartEvent) event, chain);
        }
        if (event instanceof ChainEndEvent) {
            handleChainEndEvent((ChainEndEvent) event, chain);
        }
        if (event instanceof NodeStartEvent) {
            handleNodeStartEvent((NodeStartEvent) event, chain);
        }
        if (event instanceof NodeEndEvent) {
            handleNodeEndEvent((NodeEndEvent) event, chain);
        }
        if (event instanceof ChainStatusChangeEvent) {
            handleChainStatusChangeEvent((ChainStatusChangeEvent) event, chain);
        }
        if (event instanceof ChainResumeEvent) {
            handleChainResumeEvent((ChainResumeEvent) event, chain);
        }
    }

    private void handleChainStartEvent(ChainStartEvent event, Chain chain) {
        log.info("ChainStartEvent: {}", event);
        ChainDefinition definition = chain.getDefinition();
        ChainState state = chain.getState();
        AiWorkflow workflow = aiWorkflowService.getById(definition.getId());
        String instanceId = state.getInstanceId();
        AiWorkflowExecRecord record = new AiWorkflowExecRecord();
        record.setExecKey(instanceId);
        record.setWorkflowId(workflow.getId());
        record.setTitle(workflow.getTitle());
        record.setDescription(workflow.getDescription());
        record.setInput(JSON.toJSONString(event.getVariables()));
        record.setWorkflowJson(workflow.getContent());
        record.setStartTime(new Date());
        record.setStatus(state.getStatus().getValue());
        record.setCreatedKey(WorkFlowUtil.USER_KEY);
        record.setCreatedBy(WorkFlowUtil.getOperator(chain).getId().toString());
        aiWorkflowExecRecordService.save(record);
    }

    private void handleChainEndEvent(ChainEndEvent event, Chain chain) {
        log.info("ChainEndEvent: {}", event);
        ChainState state = chain.getState();
        String instanceId = state.getInstanceId();
        AiWorkflowExecRecord record = aiWorkflowExecRecordService.getByExecKey(instanceId);
        if (record == null) {
            log.error("ChainEndEvent: record not found: {}", instanceId);
        } else {
            record.setEndTime(new Date());
            record.setStatus(state.getStatus().getValue());
            record.setOutput(JSON.toJSONString(state.getExecuteResult()));
            ExceptionSummary error = state.getError();
            if (error != null) {
                record.setErrorInfo(error.getRootCauseClass() + " --> " + error.getRootCauseMessage());
            }
            aiWorkflowExecRecordService.updateById(record);
        }
    }

    private void handleNodeStartEvent(NodeStartEvent event, Chain chain) {
        log.info("NodeStartEvent: {}", event);
        Node node = event.getNode();
        ChainState ancestorState = findAncestorState(chain.getState(), chain);

        String instanceId = ancestorState.getInstanceId();
        NodeState nodeState = chain.getNodeState(node.getId());

        String execKey = IdUtil.fastSimpleUUID();
        chain.updateNodeStateSafely(node.getId(), state -> {
            state.getMemory().put("executeId", execKey);
            return EnumSet.of(NodeStateField.MEMORY);
        });

        AiWorkflowExecRecord record = aiWorkflowExecRecordService.getByExecKey(instanceId);
        if (record == null) {
            log.error("NodeStartEvent: record not found: {}", instanceId);
        } else {
            AiWorkflowRecordStep step = new AiWorkflowRecordStep();
            step.setRecordId(record.getId());
            step.setExecKey(execKey);
            step.setNodeId(node.getId());
            step.setNodeName(node.getName());
            step.setInput(JSON.toJSONString(ancestorState.resolveParameters(node)));
            step.setNodeData(JSON.toJSONString(node));
            step.setStartTime(new Date());
            step.setStatus(nodeState.getStatus().getValue());
            aiWorkflowRecordStepService.save(step);
        }
    }

    private void handleNodeEndEvent(NodeEndEvent event, Chain chain) {
        log.info("NodeEndEvent: {}", event);
        Node node = event.getNode();
        NodeState nodeState = chain.getNodeState(node.getId());
        String execKey = nodeState.getMemory().get("executeId").toString();
        AiWorkflowRecordStep step = aiWorkflowRecordStepService.getByExecKey(execKey);
        if (step == null) {
            log.error("NodeEndEvent: step not found: {}", execKey);
        } else {
            step.setOutput(JSON.toJSONString(event.getResult()));
            step.setEndTime(new Date());
            step.setStatus(nodeState.getStatus().getValue());
            ExceptionSummary error = nodeState.getError();
            if (error != null) {
                step.setErrorInfo(error.getRootCauseClass() + " --> " + error.getRootCauseMessage());
            }
            aiWorkflowRecordStepService.updateById(step);
        }
    }

    private void handleChainStatusChangeEvent(ChainStatusChangeEvent event, Chain chain) {
        log.info("ChainStatusChangeEvent: {}", event);
    }

    private void handleChainResumeEvent(ChainResumeEvent event, Chain chain) {
        log.info("ChainResumeEvent: {}", event);
    }

    /**
     * 递归查找顶级状态
     */
    private ChainState findAncestorState(ChainState state, Chain chain) {
        String parentInstanceId = state.getParentInstanceId();
        if (StrUtil.isEmpty(parentInstanceId)) {
            return state;
        }
        ChainState chainState = chain.getChainStateRepository().load(parentInstanceId);
        return findAncestorState(chainState, chain);
    }
}

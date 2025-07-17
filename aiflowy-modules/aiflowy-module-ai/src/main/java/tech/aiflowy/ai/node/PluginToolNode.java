package tech.aiflowy.ai.node;

import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.node.BaseNode;
import com.agentsflex.core.llm.functions.Function;
import com.alibaba.fastjson.JSON;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.ai.service.AiPluginToolService;
import tech.aiflowy.common.util.SpringContextUtil;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

public class PluginToolNode extends BaseNode {

    private BigInteger pluginId;

    public PluginToolNode() {
    }

    public PluginToolNode(BigInteger pluginId) {
        this.pluginId = pluginId;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Object> execute(Chain chain) {
        Map<String, Object> map = chain.getParameterValues(this);
        AiPluginToolService bean = SpringContextUtil.getBean(AiPluginToolService.class);
        AiPluginTool tool = bean.getById(pluginId);
        if (tool == null) {
            return Collections.emptyMap();
        }

        Function function = tool.toFunction();
        if (function == null) {
            return Collections.emptyMap();
        }

        Object result = function.invoke(map);
        if (result == null) {
            return Collections.emptyMap();
        }

        if (result instanceof Map) {
            return (Map<String, Object>) result;
        }

        return JSON.parseObject(JSON.toJSONString(result), Map.class);
    }

    public BigInteger getPluginId() {
        return pluginId;
    }

    public void setPluginId(BigInteger pluginId) {
        this.pluginId = pluginId;
    }
}

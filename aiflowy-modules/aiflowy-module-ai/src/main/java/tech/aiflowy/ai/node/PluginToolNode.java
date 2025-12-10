package tech.aiflowy.ai.node;

import com.alibaba.fastjson.JSON;
import dev.tinyflow.core.chain.Chain;
import dev.tinyflow.core.node.BaseNode;
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
    public Map<String, Object> execute(Chain chain) {
        Map<String, Object> map = chain.getState().resolveParameters(this);
        AiPluginToolService bean = SpringContextUtil.getBean(AiPluginToolService.class);
        AiPluginTool tool = bean.getById(pluginId);
        if (tool == null) {
            return Collections.emptyMap();
        }

//        Function function = tool.toFunction();
//        if (function == null) {
//            return Collections.emptyMap();
//        }
//
//        Object result = function.invoke(map);
//        if (result == null) {
//            return Collections.emptyMap();
//        }
//
//        if (result instanceof Map) {
//            return (Map<String, Object>) result;
//        }

//        return JSON.parseObject(JSON.toJSONString(result), Map.class);
        return Collections.emptyMap();
    }

    public BigInteger getPluginId() {
        return pluginId;
    }

    public void setPluginId(BigInteger pluginId) {
        this.pluginId = pluginId;
    }
}

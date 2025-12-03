package tech.aiflowy.ai.node;

import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.node.BaseNode;
import dev.tinyflow.core.parser.BaseNodeParser;

import java.math.BigInteger;

public class PluginToolNodeParser extends BaseNodeParser {

    @Override
    public BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        BigInteger pluginId = data.getBigInteger("pluginId");
        return new PluginToolNode(pluginId);
    }

    public String getNodeName() {
        return "plugin-node";
    }
}

package tech.aiflowy.ai.node;

import com.agentsflex.core.chain.node.BaseNode;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.parser.BaseNodeParser;

public class DocNodeParser extends BaseNodeParser {

    @Override
    public BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        return new DocNode();
    }

    public String getNodeName() {
        return "document-node";
    }
}

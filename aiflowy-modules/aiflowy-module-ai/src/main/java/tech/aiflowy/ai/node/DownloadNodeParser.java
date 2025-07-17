package tech.aiflowy.ai.node;

import com.agentsflex.core.chain.node.BaseNode;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.parser.BaseNodeParser;
import tech.aiflowy.common.constant.enums.EnumResourceType;
import tech.aiflowy.common.filestorage.FileStorageService;

public class DownloadNodeParser extends BaseNodeParser {

    @Override
    protected BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        Integer resourceType = data.getInteger("resourceType");
        if (resourceType == null) {
            resourceType = EnumResourceType.OTHER.getCode();
        }
        return new DownloadNode(resourceType);
    }

    public String getNodeName() {
        return "download-node";
    }
}

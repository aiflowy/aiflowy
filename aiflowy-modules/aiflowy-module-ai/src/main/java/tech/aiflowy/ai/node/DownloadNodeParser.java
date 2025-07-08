package tech.aiflowy.ai.node;

import com.agentsflex.core.chain.node.BaseNode;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.parser.BaseNodeParser;
import tech.aiflowy.common.constant.enums.EnumResourceType;
import tech.aiflowy.common.filestorage.FileStorageService;

public class DownloadNodeParser extends BaseNodeParser {

    private final FileStorageService fileStorageService;

    public DownloadNodeParser(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    protected BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        Integer resourceType = data.getInteger("resourceType");
        if (resourceType == null) {
            resourceType = EnumResourceType.OTHER.getCode();
        }
        return new DownloadNode(fileStorageService,resourceType);
    }

    public String getNodeName() {
        return "download-node";
    }
}

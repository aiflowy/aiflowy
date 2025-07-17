package tech.aiflowy.ai.node;

import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.chain.ChainNode;
import com.agentsflex.core.chain.node.BaseNode;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.parser.BaseNodeParser;
import tech.aiflowy.common.filestorage.FileStorageService;

public class MakeFileNodeParser extends BaseNodeParser {

    @Override
    public BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        String suffix = data.getString("suffix");
        if (StrUtil.isEmpty(suffix)) {
            suffix = "docx";
        }
        return new MakeFileNode(suffix);
    }

    public String getNodeName() {
        return "make-file";
    }
}

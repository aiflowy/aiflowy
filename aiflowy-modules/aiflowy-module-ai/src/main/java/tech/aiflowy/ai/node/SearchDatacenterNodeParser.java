package tech.aiflowy.ai.node;

import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.node.BaseNode;
import dev.tinyflow.core.parser.BaseNodeParser;

import java.math.BigInteger;

public class SearchDatacenterNodeParser extends BaseNodeParser {

    @Override
    protected BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        BigInteger tableId = data.getBigInteger("tableId");
        String where = data.getString("where");
        Long limit = data.getLong("limit");
        if (tableId == null) {
            throw new RuntimeException("请选择数据表");
        }
        return new SearchDatacenterNode(tableId,where,limit);
    }

    public String getNodeName() {
        return "search-datacenter-node";
    }
}

package tech.aiflowy.ai.node;

import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.node.BaseNode;
import dev.tinyflow.core.parser.BaseNodeParser;

/**
 * Sql查询节点解析
 *
 * @author tao
 * @date 2025-05-21
 */
public class SqlNodeParser extends BaseNodeParser {


    @Override
    public BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        String sql = data.getString("sql");
        return new SqlNode(sql);
    }

    public String getNodeName() {
        return "sql-node";
    }
}

package tech.aiflowy.ai.node;

import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.Parameter;
import com.agentsflex.core.chain.node.BaseNode;
import com.agentsflex.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.ai.utils.DocUtil;
import tech.aiflowy.common.util.SpringContextUtil;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocNode extends BaseNode {

    private static final Logger log = LoggerFactory.getLogger(DocNode.class);

    @Override
    protected Map<String, Object> execute(Chain chain) {
        Map<String, Object> map = chain.getParameterValues(this);
        Map<String, Object> res = new HashMap<>();
        String url = map.get("fileUrl").toString();
        byte[] bytes = DocUtil.downloadFile(url);
        ReaderManager manager = SpringContextUtil.getBean(ReaderManager.class);
        String docContent = manager.getReader().read(DocUtil.getFileNameByUrl(url), new ByteArrayInputStream(bytes));

        String key = "content";
        List<Parameter> outputDefs = getOutputDefs();
        if (outputDefs != null && !outputDefs.isEmpty()) {
            String defName = outputDefs.get(0).getName();
            if (StringUtil.hasText(defName)) key = defName;
        }
        res.put(key, docContent);
        return res;
    }
}

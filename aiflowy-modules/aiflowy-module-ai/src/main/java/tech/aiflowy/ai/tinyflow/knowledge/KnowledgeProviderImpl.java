package tech.aiflowy.ai.tinyflow.knowledge;

import com.agentsflex.core.document.Document;
import com.alibaba.fastjson2.JSONObject;
import dev.tinyflow.core.chain.Chain;
import dev.tinyflow.core.knowledge.Knowledge;
import dev.tinyflow.core.knowledge.KnowledgeProvider;
import dev.tinyflow.core.node.KnowledgeNode;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.service.AiKnowledgeService;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class KnowledgeProviderImpl implements KnowledgeProvider {

    @Resource
    private AiKnowledgeService aiKnowledgeService;

    /**
     * 获取知识库
     * @param id 知识库id
     */
    @Override
    public Knowledge getKnowledge(Object id) {
        return new Knowledge() {
            @Override
            public List<Map<String, Object>> search(String keyword, int limit, KnowledgeNode knowledgeNode, Chain chain) {
                List<Document> documents = aiKnowledgeService.search(new BigInteger(id.toString()), keyword);
                List<Map<String, Object>> res = new ArrayList<>();
                for (Document document : documents) {
                    res.add(JSONObject.from(document));
                }
                return res;
            }
        };
    }
}

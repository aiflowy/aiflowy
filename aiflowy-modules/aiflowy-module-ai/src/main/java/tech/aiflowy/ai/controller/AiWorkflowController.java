package tech.aiflowy.ai.controller;

import com.agentsflex.core.document.Document;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.SearchWrapper;
import com.agentsflex.core.store.StoreOptions;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.knowledge.Knowledge;
import dev.tinyflow.core.node.KnowledgeNode;
import dev.tinyflow.core.provider.KnowledgeProvider;
import dev.tinyflow.core.provider.LlmProvider;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import com.agentsflex.core.chain.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiWorkflow")
public class AiWorkflowController extends BaseCurdController<AiWorkflowService, AiWorkflow> {
    private final AiLlmService aiLlmService;

    @Resource
    private AiKnowledgeService aiKnowledgeService;

    public AiWorkflowController(AiWorkflowService service, AiLlmService aiLlmService) {
        super(service);
        this.aiLlmService = aiLlmService;
    }


    @GetMapping("getRunningParameters")
    public Result getRunningParameters(@RequestParam BigInteger id) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "can not find the workflow by id: " + id);
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        if (tinyflow == null) {
            return Result.fail(2, "workflow content is empty! ");
        }

        Chain chain = tinyflow.toChain();
        List<Parameter> chainParameters = chain.getParameters();
        return Result.success("parameters", chainParameters);
    }

    @PostMapping("tryRunning")
    public Result tryRunning(@JsonBody(value = "id", required = true) BigInteger id, @JsonBody("variables") Map<String, Object> variables) {
        AiWorkflow workflow = service.getById(id);

        if (workflow == null) {
            return Result.fail(1, "can not find the workflow by id: " + id);
        }

        Tinyflow tinyflow = workflow.toTinyflow();
        tinyflow.setLlmProvider(new LlmProvider() {
            @Override
            public Llm getLlm(Object id) {
                AiLlm aiLlm = aiLlmService.getById(new BigInteger(id.toString()));
                return aiLlm.toLlm();
            }
        });

        tinyflow.setKnowledgeProvider(new KnowledgeProvider() {
            @Override
            public Knowledge getKnowledge(Object o) {
                AiKnowledge aiKnowledge = aiKnowledgeService.getById(new BigInteger(o.toString()));
                return new Knowledge() {
                    @Override
                    public List<Document> search(String keyword, int limit, KnowledgeNode knowledgeNode, Chain chain) {
                        DocumentStore documentStore = aiKnowledge.toDocumentStore();
                        if (documentStore == null) {
                            return null;
                        }
                        AiLlm aiLlm = aiLlmService.getById(aiKnowledge.getVectorEmbedLlmId());
                        if (aiLlm == null) {
                            return null;
                        }
                        documentStore.setEmbeddingModel(aiLlm.toLlm());
                        SearchWrapper wrapper = new SearchWrapper();
                        wrapper.setMaxResults(Integer.valueOf(limit));
                        wrapper.setText(keyword);
                        StoreOptions options = StoreOptions.ofCollectionName(aiKnowledge.getVectorStoreCollection());

                        List<Document> results = documentStore.search(wrapper, options);
                        return results;
                    }
                };
            }
        });


        Chain chain = tinyflow.toChain();
        chain.addEventListener(new ChainEventListener() {
            @Override
            public void onEvent(ChainEvent event, Chain chain) {
                System.out.println("onEvent : " + event);
            }
        });

        chain.addOutputListener(new ChainOutputListener() {
            @Override
            public void onOutput(Chain chain, ChainNode node, Object outputMessage) {
                System.out.println("output : " + node.getId() + " : " + outputMessage);
            }
        });

        Map<String, Object> result = chain.executeForResult(variables);

        return Result.success("result", result).set("message", chain.getMessage());
    }
}
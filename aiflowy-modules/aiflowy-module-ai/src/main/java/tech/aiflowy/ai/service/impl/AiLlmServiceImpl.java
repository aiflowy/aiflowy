
package tech.aiflowy.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.embedding.EmbeddingModel;
import com.agentsflex.core.model.rerank.RerankModel;
import com.agentsflex.core.store.VectorData;
import com.alicp.jetcache.Cache;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.entity.AiLlmProvider;
import tech.aiflowy.ai.mapper.AiLlmMapper;
import tech.aiflowy.ai.service.AiLlmProviderService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiLlmServiceImpl extends ServiceImpl<AiLlmMapper, AiLlm> implements AiLlmService {

    @Autowired
    AiLlmMapper aiLlmMapper;

    @Autowired
    AiLlmProviderService llmProviderService;

    @Resource
    private Cache<String, Object> cache;


    @Override
    public boolean addAiLlm(AiLlm entity) {
        int insert = aiLlmMapper.insert(entity);
        if (insert <= 0) {
            return false;
        }
        return true;
    }

    private static final Logger log = LoggerFactory.getLogger(AiLlmServiceImpl.class);

    @Override
    public void verifyLlmConfig(AiLlm llm) {
        String modelType = llm.getModelType();
        if ("chatModel".equals(modelType)) {
            // 走聊天验证逻辑
            verifyChatLlm(llm);
            return;
        }

        if ("embeddingModel".equals(modelType)) {
            // 走向量化验证逻辑
            verifyEmbedLlm(llm);
            return;

        }

        if ("rerankModel".equals(modelType)) {
            // 走重排验证逻辑
            verifyRerankLlm(llm);
            return;

        }

        // 以上不满足，视为验证失败
        throw new BusinessException("校验失败！");

    }

    @Override
    public Map<String, Map<String, List<AiLlm>>> getList(AiLlm entity) {
        String[] llmModelTypes = {"chatModel", "embeddingModel", "rerankModel"};
        Map<String, Map<String, List<AiLlm>>> result = new HashMap<>();

        QueryWrapper queryWrapper = new QueryWrapper()
                .eq(AiLlm::getProviderId, entity.getProviderId());
        queryWrapper.eq(AiLlm::getAdded, entity.getAdded());
        List<AiLlm> totalList = aiLlmMapper.selectListWithRelationsByQuery(queryWrapper);
        for (String modelType : llmModelTypes) {
            Map<String, List<AiLlm>> groupMap = groupLlmByGroupName(totalList, modelType);
            if (!CollectionUtils.isEmpty(groupMap)) {
                result.put(modelType, groupMap);
            }
        }

        return result;
    }

    private Map<String, List<AiLlm>> groupLlmByGroupName(List<AiLlm> totalList, String targetModelType) {
        if (CollectionUtils.isEmpty(totalList)) {
            return Collections.emptyMap();
        }

        return totalList.stream()
                .filter(aiLlm -> targetModelType.equals(aiLlm.getModelType())
                        && aiLlm.getGroupName() != null)
                .collect(Collectors.groupingBy(AiLlm::getGroupName));
    }


    private void verifyRerankLlm(AiLlm llm) {
        RerankModel rerankModel = llm.toRerankModel();
        List<Document> documents = new ArrayList<>();
        documents.add(Document.of("Paris is the capital of France."));
        documents.add(Document.of("London is the capital of England."));
        documents.add(Document.of("Tokyo is the capital of Japan."));
        documents.add(Document.of("Beijing is the capital of China."));
        documents.add(Document.of("Washington, D.C. is the capital of the United States."));
        documents.add(Document.of("Moscow is the capital of Russia."));
        try {
            List<Document> rerank = rerankModel.rerank("What is the capital of France?", documents);
            if (rerank == null || rerank.isEmpty()) {
                throw new BusinessException("校验未通过，请前往后端日志查看详情！");
            }
        } catch (Exception e) {
            log.error("校验失败：{}", e.getMessage());
            throw new BusinessException("校验未通过，请前往后端日志查看详情！");
        }
    }

    private void verifyEmbedLlm(AiLlm llm) {
        try {
            EmbeddingModel transLlm = llm.toEmbeddingModel();
            VectorData vectorData = transLlm.embed("这是一条校验模型配置的文本");
            if (vectorData.getVector() == null) {
                throw new BusinessException("校验未通过，请前往后端日志查看详情！");
            }
            log.info("取到向量数据，校验结果通过");
        } catch (Exception e) {
            log.error("模型配置校验失败:{}", e.getMessage());
            throw new BusinessException("校验未通过，请前往后端日志查看详情！");
        }
    }

    private void verifyChatLlm(AiLlm llm) {

        ChatModel chatModel = llm.toChatModel();
        if (chatModel == null) {
            throw new BusinessException("chatModel为空");
        }
        try {
            String response = chatModel.chat("我在对模型配置进行校验，你收到这条消息无需做任何思考，直接回复一个“你好”即可!");
            if (response == null) {
                throw new BusinessException("校验未通过，请前往后端日志查看详情！");
            }
            log.info("校验结果：{}", response);
        } catch (Exception e) {
            log.error("校验失败：{}", e.getMessage());
            throw new BusinessException("校验未通过，请前往后端日志查看详情！");
        }


        Map<String, Object> options = llm.getOptions();
//        if (options != null && options.get("multimodal") != null && (boolean) options.get("multimodal")) {
//
//            textPrompt = new ImagePrompt("我在对模型配置进行校验，你无需描述图片，只需回答“看到了图片”即可",
//                "http://115.190.9.61:7900/aiflowy-pro/public/aibot/files/40b64e32b081942bd7ab30f8a369f2a34fc7fafc04f45c50cd96d8a102fd7afa.jpg");
//
//        } else {
//            textPrompt = new TextPrompt("我在对模型配置进行校验，你收到这条消息无需做任何思考，直接回复一个“你好”即可!");
//        }


    }

    @Override
    public void removeByEntity(AiLlm entity) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(AiLlm::getProviderId, entity.getProviderId()).eq(AiLlm::getGroupName, entity.getGroupName());
        aiLlmMapper.deleteByQuery(queryWrapper);
    }

    @Override
    public AiLlm getLlmInstance(BigInteger llmId) {
        AiLlm aillm = getById(llmId);
        if (aillm == null) {
            return null;
        }
        AiLlmProvider provider = llmProviderService.getById(aillm.getProviderId());
        if (provider == null) {
            return aillm;
        }
        aillm.setAiLlmProvider(provider);
        if (StrUtil.isBlank(aillm.getLlmApiKey())) {
            aillm.setLlmApiKey(provider.getApiKey());
        }
        if (StrUtil.isBlank(aillm.getLlmEndpoint())) {
            aillm.setLlmEndpoint(provider.getEndPoint());
        }

        Map<String, Object> options = aillm.getOptions();
        if (options == null) {
            options = new HashMap<>();
            aillm.setOptions(options);
        }

        String chatPath = (String) options.get("chatPath");
        if (StrUtil.isBlank(chatPath)) {
            options.put("chatPath", provider.getChatPath());
        }

        String embedPath = (String) options.get("embedPath");
        if (StrUtil.isBlank(embedPath)) {
            options.put("embedPath", provider.getEmbedPath());
        }

        String rerankPath = (String) options.get("rerankPath");
        if (StrUtil.isBlank(rerankPath)) {
            options.put("rerankPath", provider.getRerankPath());
        }

        String llmEndpoint = (String) options.get("llmEndpoint");
        if (StrUtil.isBlank(llmEndpoint)) {
            options.put("llmEndpoint", provider.getEndPoint());
        }

        return aillm;
    }


}


package tech.aiflowy.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
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
import tech.aiflowy.ai.entity.Model;
import tech.aiflowy.ai.entity.ModelProvider;
import tech.aiflowy.ai.mapper.ModelMapper;
import tech.aiflowy.ai.service.ModelProviderService;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.common.domain.Result;
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
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ModelProviderService modelProviderService;

    @Resource
    private Cache<String, Object> cache;


    @Override
    public boolean addAiLlm(Model entity) {
        int insert = modelMapper.insert(entity);
        if (insert <= 0) {
            return false;
        }
        return true;
    }

    private static final Logger log = LoggerFactory.getLogger(ModelServiceImpl.class);

    @Override
    public Map<String, Object> verifyModelConfig(Model model) {
        String modelType = model.getModelType();
        Map<String, Object> resMap = new HashMap<>();
        // 走聊天验证逻辑
        if (Model.MODEL_TYPES[0].equals(modelType)) {
            verifyChatLlm(model);
            return null;
        }
        // 走向量化验证逻辑
        if (Model.MODEL_TYPES[1].equals(modelType)) {
            int dimension = verifyEmbedLlm(model);
            resMap.put("dimension", dimension);
            return resMap;
        }
        // 走重排验证逻辑
        if (Model.MODEL_TYPES[2].equals(modelType)) {
            verifyRerankLlm(model);
            return null;

        }

        // 以上不满足，视为验证失败
        throw new BusinessException("校验失败！");

    }

    @Override
    public Map<String, Map<String, List<Model>>> getList(Model entity) {
        Map<String, Map<String, List<Model>>> result = new HashMap<>();

        QueryWrapper queryWrapper = new QueryWrapper()
                .eq(Model::getProviderId, entity.getProviderId());
        queryWrapper.eq(Model::getWithUsed, entity.getWithUsed());
        List<Model> totalList = modelMapper.selectListWithRelationsByQuery(queryWrapper);
        for (String modelType : Model.MODEL_TYPES) {
            Map<String, List<Model>> groupMap = groupLlmByGroupName(totalList, modelType);
            if (!CollectionUtils.isEmpty(groupMap)) {
                result.put(modelType, groupMap);
            }
        }

        return result;
    }

    private Map<String, List<Model>> groupLlmByGroupName(List<Model> totalList, String targetModelType) {
        if (CollectionUtils.isEmpty(totalList)) {
            return Collections.emptyMap();
        }

        return totalList.stream()
                .filter(aiLlm -> targetModelType.equals(aiLlm.getModelType())
                        && aiLlm.getGroupName() != null)
                .collect(Collectors.groupingBy(Model::getGroupName));
    }


    private void verifyRerankLlm(Model model) {
        RerankModel rerankModel = model.toRerankModel();
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

    private int verifyEmbedLlm(Model model) {
        try {
            EmbeddingModel embeddingModel = model.toEmbeddingModel();
            VectorData vectorData = embeddingModel.embed("这是一条校验模型配置的文本");
            if (vectorData.getVector() == null) {
                throw new BusinessException("校验未通过，请前往后端日志查看详情！");
            }
            log.info("取到向量数据，校验结果通过");
            return vectorData.getVector().length;
        } catch (Exception e) {
            log.error("模型配置校验失败:{}", e.getMessage());
            throw new BusinessException("校验未通过，请前往后端日志查看详情！");
        }
    }

    private void verifyChatLlm(Model llm) {

        ChatModel chatModel = llm.toChatModel();
        if (chatModel == null) {
            throw new BusinessException("chatModel为空");
        }
        try {
            ChatOptions options=new ChatOptions();
            options.setThinkingEnabled(false);
            String response = chatModel.chat("我在对模型配置进行校验，你收到这条消息无需做任何思考，直接回复一个“你好”即可!",options);
            if (response == null) {
                throw new BusinessException("校验未通过，请前往后端日志查看详情！");
            }
            log.info("校验结果：{}", response);
        } catch (Exception e) {
            log.error("校验失败：{}", e.getMessage());
            throw new BusinessException("校验未通过，请前往后端日志查看详情！");
        }

    }

    @Override
    public void removeByEntity(Model entity) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(Model::getProviderId, entity.getProviderId()).eq(Model::getGroupName, entity.getGroupName());
        modelMapper.deleteByQuery(queryWrapper);
    }

    @Override
    public Model getModelInstance(BigInteger modelId) {
        Model model = modelMapper.selectOneWithRelationsById(modelId);
        ModelProvider modelProvider = model.getModelProvider();
        model.setModelProvider(modelProvider);
        if (StrUtil.isBlank(model.getApiKey())) {
            model.setApiKey(modelProvider.getApiKey());
        }
        if (StrUtil.isBlank(model.getEndpoint())) {
            model.setEndpoint(modelProvider.getEndpoint());
        }

        // 请求路径为空，从modelProvider中获取
        if (StrUtil.isBlank(model.getRequestPath())) {
            // 模型类型为chatModel
            if (model.getModelType().equals(Model.MODEL_TYPES[0])) {
                model.setRequestPath(modelProvider.getChatPath());
                // 模型类型为embeddingModel
            } else if (model.getModelType().equals(Model.MODEL_TYPES[1])) {
                model.setRequestPath(modelProvider.getEmbedPath());
                // 模型类型为rerankModel
            } else if (model.getModelType().equals(Model.MODEL_TYPES[2])) {
                model.setRequestPath(modelProvider.getRerankPath());
            }
        }

        return model;
    }

    @Override
    public void updateByEntity(Model entity) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(Model::getProviderId, entity.getProviderId())
                .eq(Model::getGroupName, entity.getGroupName());
        Model model = new Model();
        model.setWithUsed(entity.getWithUsed());
        modelMapper.updateByQuery(model, queryWrapper);
    }
}


package tech.aiflowy.ai.service.impl;

import com.agentsflex.core.model.chat.response.AiMessageResponse;
import com.agentsflex.core.store.VectorData;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alicp.jetcache.Cache;
import dev.tinyflow.core.llm.Llm;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.mapper.AiLlmMapper;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.domain.Result;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.agentsflex.rerank.DefaultRerankModel;
import com.agentsflex.rerank.DefaultRerankModelConfig;
import java.util.ArrayList;
import com.agentsflex.core.document.Document;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import tech.aiflowy.ai.entity.AiLlmBrand;
import java.util.HashMap;
import tech.aiflowy.common.util.Maps;
import cn.dev33.satoken.stp.StpUtil;
import tech.aiflowy.common.satoken.util.SaTokenUtil;

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

        Boolean supportChat = llm.getSupportChat();

        if (supportChat != null && supportChat) {
            // 走聊天验证逻辑
            verifyChatLlm(llm);
            return;
        }

        Boolean supportEmbed = llm.getSupportEmbed();
        if (supportEmbed != null && supportEmbed) {

            // 走向量化验证逻辑
            verifyEmbedLlm(llm);
            return;

        }

        Boolean supportReranker = llm.getSupportReranker();
        if (supportReranker != null && supportReranker) {

            // 走重排验证逻辑
            verifyRerankLlm(llm);
            return;

        }

        // 以上不满足，视为验证失败
        throw new BusinessException("校验失败！");

    }

    @Override
    public void quickAdd(String brand, String apiKey) {

//        Object o = cache.get(LLM_BRAND_KEY);
//
//        List<AiLlmBrand> brandList = null;
//
//        if (o != null) {
//            brandList = (List<AiLlmBrand>) o;
//        } else {
//            brandList = AiLlmBrand.fromJsonConfig();
//            if (brandList == null || brandList.isEmpty()) {
//                throw new BusinessException("获取解析供应商列表结果为空，请检查配置文件！");
//            }
//            cache.put(LLM_BRAND_KEY, brandList);
//        }
//
//        AiLlmBrand aiLlmBrand = brandList.stream().filter(f -> f.getKey().equals(brand)).findFirst().orElse(null);
//        Map<String, Object> options = aiLlmBrand.getOptions();
//
//        // 接入点
//        String llmEndpoint = (String) options.get("llmEndpoint");
//
//        // 对话路径
//        String chatPath = (String) options.get("chatPath");
//
//        // 向量化路径
//        String embedPath = (String) options.get("embedPath");
//
//        // 重排路径
//        String rerankPath = (String) options.get("rerankPath");
//
//        // 模型列表
//        List<Map<String, Object>> llmList = (List<Map<String, Object>>) options.get("modelList");
//
//
//        if (llmList == null || llmList.isEmpty()) {
//            log.error("该供应商下的大模型列表为空");
//            return;
//        }
//
//
//        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
//
//        ArrayList<AiLlm> aiLlmList = new ArrayList<>();
//
//        llmList.forEach(llm -> {
//            AiLlm aiLlm = new AiLlm();
//
//            aiLlm.setBrand(brand);
//            aiLlm.setLlmApiKey(apiKey);
//            aiLlm.setLlmEndpoint(llmEndpoint.trim());
//
//            String llmModel = (String) llm.get("llmModel");
//            aiLlm.setLlmModel(llmModel);
//
//            String description = (String) llm.get("description");
//            aiLlm.setDescription(description);
//
//            String title = (String) llm.get("title");
//            aiLlm.setTitle(title);
//
//            Boolean multimodal = (Boolean) llm.get("multimodal");
//
//
//            Maps llmOptions = Maps.of()
//                .setIfNotEmpty("chatPath", chatPath)
//                .setIfNotEmpty("embedPath", embedPath)
//                .setIfNotEmpty("rerankPath", rerankPath)
//                .setIfNotEmpty("multimodal", multimodal);
//
//            aiLlm.setOptions(llmOptions);
//            Boolean supportChat = (Boolean) llm.get("supportChat");
//            Boolean supportFunctionCalling = (Boolean) llm.get("supportFunctionCalling");
//            Boolean supportEmbed = (Boolean) llm.get("supportEmbed");
//            Boolean supportReranker = (Boolean) llm.get("supportReranker");
//            Boolean supportTextToImage = (Boolean) llm.get("supportTextToImage");
//            Boolean supportImageToImage = (Boolean) llm.get("supportImageToImage");
//            Boolean supportTextToAudio = (Boolean) llm.get("supportTextToAudio");
//            Boolean supportAudioToAudio = (Boolean) llm.get("supportAudioToAudio");
//            Boolean supportTextToVideo = (Boolean) llm.get("supportTextToVideo");
//            Boolean supportImageToVideo = (Boolean) llm.get("supportImageToVideo");
//
//            aiLlm.setSupportChat(supportChat);
//            aiLlm.setSupportChat(supportChat);
//            aiLlm.setSupportFunctionCalling(supportFunctionCalling);
//            aiLlm.setSupportEmbed(supportEmbed);
//            aiLlm.setSupportReranker(supportReranker);
//            aiLlm.setSupportTextToImage(supportTextToImage);
//            aiLlm.setSupportImageToImage(supportImageToImage);
//            aiLlm.setSupportTextToAudio(supportTextToAudio);
//            aiLlm.setSupportAudioToAudio(supportAudioToAudio);
//            aiLlm.setSupportTextToVideo(supportTextToVideo);
//            aiLlm.setSupportImageToVideo(supportImageToVideo);
//
//
//
//            aiLlm.setTenantId(loginAccount.getTenantId());
//            aiLlm.setDeptId(loginAccount.getDeptId());
//
//
//            aiLlmList.add(aiLlm);
//        });
//
//        saveBatch(aiLlmList);
    }

    private void verifyRerankLlm(AiLlm llm) {
//        try {
//            DefaultRerankModelConfig rerankModelConfig = new DefaultRerankModelConfig();
//
//            rerankModelConfig.setModel(llm.getLlmModel());
//            rerankModelConfig.setEndpoint(llm.getLlmEndpoint());
//            rerankModelConfig.setApiKey(llm.getLlmApiKey());
//            rerankModelConfig.setDebug(true);
//
//            Map<String, Object> options = llm.getOptions();
//            if (options == null) {
//                throw new BusinessException("options为空");
//            }
//
//            if (options.get("rerankPath") == null || !StringUtils.hasLength((String) options.get("rerankPath"))) {
//                throw new BusinessException("rerankPath未配置");
//            }
//
//            String reankPath = (String) options.get("rerankPath");
//            rerankModelConfig.setBasePath(reankPath);
//
//            DefaultRerankModel rerankModel = new DefaultRerankModel(rerankModelConfig);
//
//            String query = "我和吴彦祖谁帅？";
//            ArrayList<Document> documentList = new ArrayList<>();
//            documentList.add(new Document("你比陈冠希帅"));
//            documentList.add(new Document("你比吴彦祖帅"));
//            documentList.add(new Document("你比谢霆锋帅"));
//            List<Document> rerank = rerankModel.rerank(query, documentList);
//            log.info("校验结果：{}", rerank);
//        } catch (Exception e) {
//            log.error("校验失败：{}", e.getMessage());
//            throw new BusinessException("校验未通过，请前往后端日志查看详情！");
//
//        }
    }

    private void verifyEmbedLlm(AiLlm llm) {
//        try {
//            Llm transLlm = llm.toLlm();
//            VectorData vectorData = transLlm.embed("这是一条校验模型配置的文本");
//            if (vectorData.getVector() == null) {
//                throw new BusinessException("校验未通过，请前往后端日志查看详情！");
//            }
//            log.info("取到向量数据，校验结果通过");
//        } catch (Exception e) {
//            log.error("模型配置校验失败:{}", e.getMessage());
//            throw new BusinessException("校验未通过，请前往后端日志查看详情！");
//        }
    }

    private void verifyChatLlm(AiLlm llm) {

//        Llm transLlm = llm.toLlm();
//
//        TextPrompt textPrompt = null;
//
//        Map<String, Object> options = llm.getOptions();
//        if (options != null && options.get("multimodal") != null && (boolean) options.get("multimodal")) {
//
//            textPrompt = new ImagePrompt("我在对模型配置进行校验，你无需描述图片，只需回答“看到了图片”即可",
//                "http://115.190.9.61:7900/aiflowy-pro/public/aibot/files/40b64e32b081942bd7ab30f8a369f2a34fc7fafc04f45c50cd96d8a102fd7afa.jpg");
//
//        } else {
//            textPrompt = new TextPrompt("我在对模型配置进行校验，你收到这条消息无需做任何思考，直接回复一个“你好”即可!");
//        }
//
//        try {
//            AiMessageResponse chatResponse = transLlm.chat(textPrompt);
//            String content = chatResponse.getMessage().getContent();
//            String fullContent = chatResponse.getMessage().getFullContent();
//            if (!StringUtils.hasLength(content) && !StringUtils.hasLength(fullContent)) {
//                throw new BusinessException("校验未通过，请前往后端日志查看详情！");
//            }
//            log.info("校验结果：response:{},aiMessage:{}", chatResponse.getResponse(), chatResponse.getMessage());
//        } catch (Exception e) {
//            log.error("模型配置校验失败:{}", e.getMessage());
//            throw new BusinessException("校验未通过，请前往后端日志查看详情！");
//        }
    }
}

package tech.aiflowy.ai.service.impl;

import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.mapper.AiLlmMapper;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.domain.Result;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.util.Map;
import com.agentsflex.core.prompt.ImagePrompt;
import com.agentsflex.core.prompt.TextPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiLlmServiceImpl extends ServiceImpl<AiLlmMapper, AiLlm> implements AiLlmService {

    @Autowired
    AiLlmMapper aiLlmMapper;

    @Override
    public Result addAiLlm(AiLlm entity) {
        int insert = aiLlmMapper.insert(entity);
        if (insert <= 0){
            return Result.fail();
        }
        return Result.success();
    }

    private static final Logger log = LoggerFactory.getLogger(AiLlmServiceImpl.class);

    @Override
    public void verifyLlmConfig(AiLlm llm) {

        Boolean supportChat = llm.getSupportChat();

        if (supportChat != null && supportChat){
            // 走聊天验证逻辑
            verifyChatLlm(llm);
            return;
        }


        Boolean supportEmbed = llm.getSupportEmbed();
        if (supportEmbed != null && supportEmbed){

            // 走向量化验证逻辑
            verifyEmbedLlm(llm);
            return;

        }

        Boolean supportReranker = llm.getSupportReranker();
        if (supportReranker != null && supportReranker){

            // 走重排验证逻辑
            verifyRerankLlm(llm);
            return;

        }

        // 以上不满足，视为验证失败
        throw new BusinessException("校验失败！");


    }

    private void verifyRerankLlm(AiLlm llm) {
    }

    private void verifyEmbedLlm(AiLlm llm) {
    }

    private void verifyChatLlm(AiLlm llm) {

        Llm transLlm = llm.toLlm();

        TextPrompt textPrompt = null;

        Map<String, Object> options = llm.getOptions();
        if (options != null && options.get("multimodal") != null && (boolean) options.get("multimodal") ){

            textPrompt = new ImagePrompt("描述一下这张图片", "http://115.190.9.61:7900/aiflowy-pro/public/aibot/files/40b64e32b081942bd7ab30f8a369f2a34fc7fafc04f45c50cd96d8a102fd7afa.jpg");

        }else {
            textPrompt = new TextPrompt("你好!");
        }

        try{
            AiMessageResponse chatResponse = transLlm.chat(textPrompt);
            JSONObject jsonObject = JSON.parseObject(chatResponse.getResponse());
            log.info(jsonObject + "");
        } catch (Exception e){
            throw new BusinessException("校验失败！");
        }

    }
}

package tech.aiflowy.ai.entity;

import com.agentsflex.core.message.*;
import com.agentsflex.core.model.chat.tool.Tool;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiBotMessageBase;
import tech.aiflowy.common.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Bot 消息记录表 实体类。
 *
 * @author michael
 * @since 2024-11-04
 */

@Table(value = "tb_ai_bot_message", comment = "Bot 消息记录表")
public class AiBotMessage extends AiBotMessageBase {

    public Message toMessage() {
        String role = getRole();
        if ("user".equalsIgnoreCase(role)) {

            Map<String, Object> options = getOptions();
            if (options != null && options.get("type") != null && Objects.equals(options.get("type"), 1)) {

                List<String> fileList = (List<String>) options.get("fileList");

                if (fileList != null && !fileList.isEmpty()){
//                    MultimodalMessage multimodalMessage = new MultimodalMessage();
//                    multimodalMessage.setText(getContent());
//                    multimodalMessage.setImageUrls(fileList);
//                    return  multimodalMessage;
                }
            }

            UserMessage humanMessage = new UserMessage();
            humanMessage.setContent(getContent());
            String functionsJson = getFunctions();
            if (StringUtil.hasText(functionsJson)) {
                List<Tool> tools = JSON.parseArray(functionsJson, Tool.class, JSONReader.Feature.SupportAutoType);
                if (tools != null && !tools.isEmpty()) {
                    humanMessage.addTools(tools);
                }
            }
            return humanMessage;
        } else if ("assistant".equalsIgnoreCase(role)) {
            AiMessage aiMessage = new AiMessage();
            aiMessage.setFullContent(getContent());
            aiMessage.setTotalTokens(getTotalTokens());
            return aiMessage;
        } else if ("system".equalsIgnoreCase(role)) {
            SystemMessage systemMessage = new SystemMessage();
            systemMessage.setContent(getContent());
            return systemMessage;
        } else if ("function".equalsIgnoreCase(role)) {
            ToolMessage toolMessage = new ToolMessage();
            toolMessage.setContent(getContent());
            return toolMessage;
        }
        return null;

    }
}

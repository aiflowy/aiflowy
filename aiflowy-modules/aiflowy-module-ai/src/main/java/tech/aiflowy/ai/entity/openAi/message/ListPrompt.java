package tech.aiflowy.ai.entity.openAi.message;

import com.agentsflex.core.message.*;
import com.agentsflex.core.prompt.Prompt;
import tech.aiflowy.ai.entity.openAi.request.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListPrompt extends Prompt {

    private List<ChatMessage> messageList;


    @Override
    public List<Message> toMessages() {

        ArrayList<Message> messages = new ArrayList<>();

        messageList.forEach((message) -> {
            String role = message.getRole();
            Object content = message.getContent();
            List<FunctionCall> functionCalls = new ArrayList<>();
            message.getToolCalls().forEach(toolCall -> {
                FunctionCall flexFunctionCall = toolCall.toAgentsFlexFunctionCall();
                functionCalls.add(flexFunctionCall);

            });

            switch (role) {
                case "system":{
                    messages.add(new SystemMessage(message.getContent().toString()));
                    break;
                }
                case "user":{

                    // 需要对 content 进行判断是否为多模态消息


                    break;
                }
                case "assistant":{
                    AiMessage aiMessage = new AiMessage();
                    aiMessage.setContent(message.getContent().toString());
                    aiMessage.setCalls(functionCalls);
                    messages.add(aiMessage);
                    break;
                }
                case "tool":{
                    break;
                }
            }


        });



        return messages;
    }
}

//
//package tech.aiflowy.ai.message;
//import com.agentsflex.core.message.HumanMessage;
//import com.agentsflex.core.react.ReActMessageBuilder;
//import com.agentsflex.core.llm.functions.Function;
//import com.agentsflex.core.message.Message;
//import java.util.List;
//
//public class NormalMessageBuilder extends ReActMessageBuilder {
//
//    @Override
//    public Message buildStartMessage(String prompt, List<Function> functions, String userQuery) {
//        HumanMessage message = new HumanMessage(prompt);
//        message.addMetadata("tools", functions);
//        message.addMetadata("user_input", userQuery);
//        message.addMetadata("type", 1);
//        return message;
//    }
//
//}

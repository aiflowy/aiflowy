//package tech.aiflowy.ai.entity;
//
//import com.agentsflex.core.memory.ChatMemory;
//import com.agentsflex.core.message.Message;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AiBotExternalMessageMemory  implements ChatMemory {
//
//    List<AiBotMessage> historyMessages;
//
//    public AiBotExternalMessageMemory( List<AiBotMessage> historyMessage) {
//        this.historyMessages = historyMessage;
//    }
//
//    @Override
//    public List<Message> getMessages() {
//        List<Message> messages = new ArrayList<>();
//        for (AiBotMessage aiBotMessage : historyMessages) {
//            Message message = aiBotMessage.toMessage();
//            if (message != null) messages.add(message);
//        }
//        return messages;
//    }
//
//    @Override
//    public void addMessage(Message message) {
//
//    }
//
//    @Override
//    public Object id() {
//        return null;
//    }
//}

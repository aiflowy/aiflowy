//package tech.aiflowy.ai.message;
//
//import com.agentsflex.core.message.HumanMessage;
//import tech.aiflowy.common.util.Maps;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MultimodalMessage extends HumanMessage {
//
//
//    private String text;
//
////    private List<Map<String,Object>> fileList;
//
//    private List<String> imageUrls;
//
//    @Override
//    public Object getMessageContent() {
//
//        List<Map<String, Object>> messageContent = new ArrayList<>();
//
//        messageContent.add(
//                Maps.of("type","text")
//                        .set("text",text)
//        );
//
//        if (imageUrls != null && !imageUrls.isEmpty()) {
//            imageUrls.forEach(item -> {
//
//                messageContent.add(
//                        Maps.of("type","image_url")
//                                .set("image_url",Maps.of("url",item))
//                );
//            });
//        }
//
//        return messageContent;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public List<String> getImageUrls() {
//        return imageUrls;
//    }
//
//    public void setImageUrls(List<String> imageUrls) {
//        this.imageUrls = imageUrls;
//    }
//}

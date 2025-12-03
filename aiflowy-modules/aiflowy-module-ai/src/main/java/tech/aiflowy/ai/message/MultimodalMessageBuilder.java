//package tech.aiflowy.ai.message;
//
//import com.agentsflex.core.llm.functions.Function;
//import com.agentsflex.core.message.HumanMessage;
//import com.agentsflex.core.message.Message;
//import com.agentsflex.core.react.ReActMessageBuilder;
//import com.agentsflex.core.react.ReActStep;
//import com.alibaba.fastjson2.JSON;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import tech.aiflowy.common.util.Maps;
//
//import java.util.List;
//import java.util.Map;
//
//public class MultimodalMessageBuilder extends ReActMessageBuilder {
//
//    private static final Logger logger = LoggerFactory.getLogger(MultimodalMessageBuilder.class);
//
//    private List<String> fileList;
//
//
//    public void setFileList(List<String> fileList){
//        this.fileList = fileList;
//    }
//
//    public List<String> getFileList(){
//        return this.getFileList();
//    }
//
//
//    @Override
//    public Message buildStartMessage(String prompt, List<Function> functions, String userQuery) {
//
//        HumanMessage humanMessage = new HumanMessage(prompt);
//
//
//        humanMessage.setMetadataMap(
//                Maps.of("type",1)
//                        .set("fileList",fileList)
//                        .set("user_input",userQuery)
//        );
//
//        return humanMessage;
//    }
//
//
//    @Override
//    public Message buildActionErrorMessage(ReActStep step, Exception e) {
//        return super.buildActionErrorMessage(step, e);
//    }
//
//
//    @Override
//    public Message buildJsonParserErrorMessage(Exception e, ReActStep step) {
//        return super.buildJsonParserErrorMessage(e, step);
//    }
//
//
//    @Override
//    public Message buildObservationMessage(ReActStep step, Object result) {
//        return super.buildObservationMessage(step, result);
//    }
//}

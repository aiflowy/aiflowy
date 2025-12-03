//package tech.aiflowy.ai.message.thirdPart;
//
//
//import java.util.Map;
//import com.agentsflex.core.react.ReActAgent;
//
//public interface MessageHandler {
//    /**
//    * 处理接收到的消息
//    *
//    * @param messageData 消息数据，包含消息内容和相关信息
//    * @param contextData 上下文数据，包含请求相关信息
//    * @return 回复消息数据
//    */
//    Object handleMessage(Object messageData, Map<String, Object> contextData,Map<String,Object> agentParams);
//
//    /**
//    * 获取支持的平台类型
//    *
//    * @return 平台类型标识
//    */
//    String getPlatformType();
//
//    /**
//    * 检查是否支持该消息类型
//    *
//    * @param messageType 消息类型
//    * @return 是否支持
//    */
//    boolean supportMessageType(String messageType);
//
//
//}
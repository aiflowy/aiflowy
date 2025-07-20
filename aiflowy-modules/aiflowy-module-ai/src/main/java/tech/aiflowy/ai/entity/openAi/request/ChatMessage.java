package tech.aiflowy.ai.entity.openAi.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * 聊天消息类
 * 表示对话中的一条消息，可以是用户消息、助手回复或系统消息
 */
public class ChatMessage {

    /** 消息发送者角色: "system"(系统), "user"(用户), "assistant"(助手), "tool"(工具) */
    private String role;

    /** 消息内容，可以是纯文本字符串或包含多媒体的ContentItem列表 */
    private Object content;

    /** 消息发送者的名称标识，可选字段 */
    private String name;

    /** 工具调用列表，当助手需要调用外部工具时使用 */
    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    /** 工具调用的ID，用于标识特定的工具调用结果 */
    @JsonProperty("tool_call_id")
    private String toolCallId;

    public ChatMessage() {}

    public ChatMessage(String role, Object content) {
        this.role = role;
        this.content = content;
    }

    // ContentItem 内部类 - 表示消息内容项
    public static class ContentItem {
        /** 内容类型: "text"(文本) | "image_url"(图片链接) */
        private String type;

        /** 文本内容，当type为"text"时使用 */
        private String text;

        /** 图片URL信息，当type为"image_url"时使用 */
        @JsonProperty("image_url")
        private ImageUrl imageUrl;

        public ContentItem() {}

        public ContentItem(String type, String text) {
            this.type = type;
            this.text = text;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public ImageUrl getImageUrl() { return imageUrl; }
        public void setImageUrl(ImageUrl imageUrl) { this.imageUrl = imageUrl; }

        @Override
        public String toString() {
            return "ContentItem{" +
                    "type='" + type + '\'' +
                    ", text='" + text + '\'' +
                    ", imageUrl=" + imageUrl +
                    '}';
        }

        // ImageUrl 内部类 - 表示图片URL信息
        public static class ImageUrl {
            /** 图片的URL地址 */
            private String url;

            /** 图片处理详细度: "low"(低), "high"(高), "auto"(自动) */
            private String detail;

            public ImageUrl() {}

            public ImageUrl(String url, String detail) {
                this.url = url;
                this.detail = detail;
            }

            public String getUrl() { return url; }
            public void setUrl(String url) { this.url = url; }
            public String getDetail() { return detail; }
            public void setDetail(String detail) { this.detail = detail; }
        }
    }

    // ToolCall 内部类 - 表示工具调用
    public static class ToolCall {
        /** 工具调用的唯一标识符 */
        private String id;

        /** 工具类型，通常为"function" */
        private String type;

        /** 函数调用详细信息 */
        private FunctionCall function;

        public ToolCall() {}

        public ToolCall(String id, String type, FunctionCall function) {
            this.id = id;
            this.type = type;
            this.function = function;
        }

        /**
         * 转换为 AgentsFlex 的 FunctionCall 对象
         */
        public com.agentsflex.core.message.FunctionCall toAgentsFlexFunctionCall() {
            if (this.function == null) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> args = null;

            try {
                if (this.function.arguments != null && !this.function.arguments.trim().isEmpty()) {
                    args = mapper.readValue(this.function.arguments, Map.class);
                }
            } catch (Exception e) {
                // 处理 JSON 解析异常，可以记录日志或抛出运行时异常
                throw new RuntimeException("Failed to parse function arguments: " + this.function.arguments, e);
            }

            return new com.agentsflex.core.message.FunctionCall(this.id, this.function.name, args);
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public FunctionCall getFunction() { return function; }
        public void setFunction(FunctionCall function) { this.function = function; }

        @Override
        public String toString() {
            return "ToolCall{" +
                    "id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", function=" + function +
                    '}';
        }

        // FunctionCall 内部类 - 表示函数调用
        public static class FunctionCall {
            /** 要调用的函数名称 */
            private String name;

            /** 函数调用的参数，JSON字符串格式 */
            private String arguments;

            public FunctionCall() {}

            public FunctionCall(String name, String arguments) {
                this.name = name;
                this.arguments = arguments;
            }

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getArguments() { return arguments; }
            public void setArguments(String arguments) { this.arguments = arguments; }

            @Override
            public String toString() {
                return "FunctionCall{" +
                        "name='" + name + '\'' +
                        ", arguments='" + arguments + '\'' +
                        '}';
            }
        }
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Object getContent() { return content; }
    public void setContent(Object content) { this.content = content; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ToolCall> getToolCalls() { return toolCalls; }
    public void setToolCalls(List<ToolCall> toolCalls) { this.toolCalls = toolCalls; }
    public String getToolCallId() { return toolCallId; }
    public void setToolCallId(String toolCallId) { this.toolCallId = toolCallId; }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "role='" + role + '\'' +
                ", content=" + content +
                ", name='" + name + '\'' +
                ", toolCalls=" + toolCalls +
                ", toolCallId='" + toolCallId + '\'' +
                '}';
    }
}
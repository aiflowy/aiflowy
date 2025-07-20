package tech.aiflowy.ai.entity.openAi.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * OpenAI Chat API 请求参数类
 * 用于接收客户端发送的聊天请求参数
 */
public class OpenAiChatRequest {

    /** 使用的模型名称，如 "gpt-3.5-turbo", "gpt-4" 等 */
    private String model;

    /** 聊天消息列表，包含用户消息、助手回复等 */
    private List<ChatMessage> messages;

    /** 生成回复时使用的最大token数量限制 */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /** 控制回复随机性的温度参数，范围0-1，值越高越随机 */
    private Float temperature;

    /** 核采样参数，控制回复的多样性，范围0-1 */
    @JsonProperty("top_p")
    private Float topP;

    /** 值：1-10 */
    @JsonProperty("top_k")
    private Integer topK;

    /** 是否启用流式传输，true表示实时返回生成的内容 */
    private Boolean stream = true;

    /** 停止生成的标记列表，遇到这些字符串时停止生成 */
    private List<String> stop;


    /** 可用的工具/函数列表，支持函数调用功能 */
    private List<Tool> tools;

    /** 工具选择策略，可以是字符串("auto","none","required")或ToolChoice对象 */
    @JsonProperty("tool_choice")
    private Object toolChoice;

    @JsonProperty("enable_thinking")
    private Boolean enableThinking;


    /** 随机种子，用于确保结果的可重现性 */
    private Long seed;

    // 构造函数
    public OpenAiChatRequest() {}

    public OpenAiChatRequest(String model, List<ChatMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getTopP() {
        return topP;
    }

    public void setTopP(Float topP) {
        this.topP = topP;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public List<String> getStop() {
        return stop;
    }

    public void setStop(List<String> stop) {
        this.stop = stop;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public Object getToolChoice() {
        return toolChoice;
    }

    public void setToolChoice(Object toolChoice) {
        this.toolChoice = toolChoice;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }


    public Boolean getEnableThinking() {
        return enableThinking;
    }

    public void setEnableThinking(Boolean enableThinking) {
        this.enableThinking = enableThinking;
    }


    @Override
    public String toString() {
        return "OpenAiChatRequest{" +
                "model='" + model + '\'' +
                ", messages=" + messages +
                ", maxTokens=" + maxTokens +
                ", temperature=" + temperature +
                ", topP=" + topP +
                ", topK=" + topK +
                ", stream=" + stream +
                ", stop=" + stop +
                ", tools=" + tools +
                ", toolChoice=" + toolChoice +
                ", enableThinking=" + enableThinking +
                ", seed=" + seed +
                '}';
    }
}
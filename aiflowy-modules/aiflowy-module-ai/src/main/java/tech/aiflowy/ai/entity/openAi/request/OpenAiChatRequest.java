
package tech.aiflowy.ai.entity.openAi.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import com.agentsflex.core.llm.ChatOptions;
import tech.aiflowy.ai.entity.AiLlm;

import java.util.HashMap;

/**
 * OpenAI Chat API 请求参数类
 * 用于接收客户端发送的聊天请求参数
 */
public class OpenAiChatRequest {

    /** 使用的模型名称，如 "gpt-3.5-turbo", "gpt-4" 等 */
    private String model;

    /** 聊天消息列表，包含用户消息、助手回复等 */
    private List<Object> messages;

    /** 生成回复时使用的最大token数量限制 */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /** 控制回复随机性的温度参数，范围0-2，值越高越随机 */
    private Float temperature;

    /** 核采样参数，控制回复的多样性，范围0-1 */
    @JsonProperty("top_p")
    private Float topP;

    /** 值：1-10 */
    @JsonProperty("top_k")
    private Integer topK;

    /** 是否启用流式传输，true表示实时返回生成的内容 */
    private Boolean stream = false;

    /** 停止生成的标记列表，遇到这些字符串时停止生成 */
    private List<String> stop;

    /** 可用的工具/函数列表，支持函数调用功能 */
    private List<Object> tools;

    /** 工具选择策略，可以是字符串("auto","none","required")或ToolChoice对象 */
    @JsonProperty("tool_choice")
    private Object toolChoice;

    /** 是否启用思考功能（特定模型支持） */
    @JsonProperty("enable_thinking")
    private Boolean enableThinking;

    /** 随机种子，用于确保结果的可重现性 */
    private Long seed;

    // ========== 以下是补全的参数 ==========

    /** 要生成的聊天完成选择数，默认为1 */
    private Integer n;

    /** 频率惩罚，范围-2.0到2.0，降低重复词汇的概率 */
    @JsonProperty("frequency_penalty")
    private Float frequencyPenalty;

    /** 存在惩罚，范围-2.0到2.0，鼓励模型谈论新话题 */
    @JsonProperty("presence_penalty")
    private Float presencePenalty;

    /** 修改指定token出现概率的映射，token_id -> bias_value (-100到100) */
    @JsonProperty("logit_bias")
    private Map<String, Integer> logitBias;

    /** 用户标识符，用于监控和检测滥用 */
    private String user;

    /** 响应格式，用于指定返回JSON等特定格式 */
    @JsonProperty("response_format")
    private Object responseFormat;

    /** 是否返回输出token的对数概率 */
    private Boolean logprobs;

    /** 在每个token位置返回最可能的token数量，范围0-5 */
    @JsonProperty("top_logprobs")
    private Integer topLogprobs;

    /** 是否并行执行工具调用，默认为true */
    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls;

    /** 服务层级，用于延迟控制 ("auto", "default", "flex") */
    @JsonProperty("service_tier")
    private String serviceTier;

    /** 系统指纹，用于跟踪系统变更 */
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

    /** 推理努力程度，适用于推理模型 ("low", "medium", "high") */
    @JsonProperty("reasoning_effort")
    private String reasoningEffort;

    /** 是否存储数据用于未来改进，默认true */
    @JsonProperty("store")
    private Boolean store;

    /** 元数据，键值对形式的额外信息 */
    private Map<String, String> metadata;

    // 构造函数
    public OpenAiChatRequest() {
    }

    public OpenAiChatRequest(String model, List<Object> messages) {
        this.model = model;
        this.messages = messages;
    }

    // 现有的getter和setter方法
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Object> getMessages() {
        return messages;
    }

    public void setMessages(List<Object> messages) {
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

    public List<Object> getTools() {
        return tools;
    }

    public void setTools(List<Object> tools) {
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

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Float getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Float frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Float getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Float presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public Map<String, Integer> getLogitBias() {
        return logitBias;
    }

    public void setLogitBias(Map<String, Integer> logitBias) {
        this.logitBias = logitBias;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Object getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(Object responseFormat) {
        this.responseFormat = responseFormat;
    }

    public Boolean getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Boolean logprobs) {
        this.logprobs = logprobs;
    }

    public Integer getTopLogprobs() {
        return topLogprobs;
    }

    public void setTopLogprobs(Integer topLogprobs) {
        this.topLogprobs = topLogprobs;
    }

    public Boolean getParallelToolCalls() {
        return parallelToolCalls;
    }

    public void setParallelToolCalls(Boolean parallelToolCalls) {
        this.parallelToolCalls = parallelToolCalls;
    }

    public String getServiceTier() {
        return serviceTier;
    }

    public void setServiceTier(String serviceTier) {
        this.serviceTier = serviceTier;
    }

    public String getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(String systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    public String getReasoningEffort() {
        return reasoningEffort;
    }

    public void setReasoningEffort(String reasoningEffort) {
        this.reasoningEffort = reasoningEffort;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
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
            ", n=" + n +
            ", frequencyPenalty=" + frequencyPenalty +
            ", presencePenalty=" + presencePenalty +
            ", logitBias=" + logitBias +
            ", user='" + user + '\'' +
            ", responseFormat=" + responseFormat +
            ", logprobs=" + logprobs +
            ", topLogprobs=" + topLogprobs +
            ", parallelToolCalls=" + parallelToolCalls +
            ", serviceTier='" + serviceTier + '\'' +
            ", systemFingerprint='" + systemFingerprint + '\'' +
            ", reasoningEffort='" + reasoningEffort + '\'' +
            ", store=" + store +
            ", metadata=" + metadata +
            '}';
    }

    public ChatOptions buildChatOptions(AiLlm aiLlm) {
        ChatOptions chatOptions = new ChatOptions();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.putIfAbsent("messages", this.getMessages());
        hashMap.put("maxTokens", this.getMaxTokens());
        hashMap.put("temperature", this.getTemperature());
        hashMap.put("topP", this.getTopP());
        hashMap.put("topK", this.getTopK());
        hashMap.put("stream", this.getStream() == null ? true : this.getStream());
        hashMap.put("stop", this.getStop());
        hashMap.put("tools", this.getTools());
        hashMap.put("toolChoice", this.getToolChoice());
        hashMap.put("enableThinking", this.getEnableThinking());
        hashMap.put("seed", this.getSeed());
        hashMap.put("n", this.getN());
        hashMap.put("frequencyPenalty", this.getFrequencyPenalty());
        hashMap.put("presencePenalty", this.getPresencePenalty());
        hashMap.put("logitBias", this.getLogitBias());
        hashMap.put("user", this.getUser());
        hashMap.put("responseFormat", this.getResponseFormat());
        hashMap.put("topLogprobs", this.getTopLogprobs());
        hashMap.put("parallelToolCalls", this.getParallelToolCalls());
        hashMap.put("serviceTier", this.getServiceTier());
        hashMap.put("systemFingerprint", this.getSystemFingerprint());
        hashMap.put("reasoningEffort", this.getReasoningEffort());
        hashMap.put("store", this.getStore());

        hashMap.put("metadata", this.getMetadata());

        chatOptions.setExtra(hashMap);
        return chatOptions;
    }

}
package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;


public class AiLlmBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private BigInteger id;

    /**
     * 部门ID
     */
    @Column(comment = "部门ID")
    private BigInteger deptId;

    /**
     * 租户ID
     */
    @Column(comment = "租户ID", tenantId = true)
    private BigInteger tenantId;

    /**
     * 标题或名称
     */
    @Column(comment = "标题或名称")
    private String title;

    /**
     * 品牌
     */
    @Column(comment = "品牌")
    private String brand;

    /**
     * ICON
     */
    @Column(comment = "ICON")
    private String icon;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;

    /**
     * 是否支持对话
     */
    @Column(comment = "是否支持对话")
    private Boolean supportChat;

    /**
     * 是否支持方法调用
     */
    @Column(comment = "是否支持方法调用")
    private Boolean supportFunctionCalling;

    /**
     * 是否支持向量化
     */
    @Column(comment = "是否支持向量化")
    private Boolean supportEmbed;

    /**
     * 是否支持重排
     */
    @Column(comment = "是否支持重排")
    private Boolean supportReranker;

    /**
     * 是否支持文字生成图片
     */
    @Column(comment = "是否支持文字生成图片")
    private Boolean supportTextToImage;

    /**
     * 是否支持图片生成图片
     */
    @Column(comment = "是否支持图片生成图片")
    private Boolean supportImageToImage;

    /**
     * 是否支持文字生成语音
     */
    @Column(comment = "是否支持文字生成语音")
    private Boolean supportTextToAudio;

    /**
     * 是否支持语音生成语音
     */
    @Column(comment = "是否支持语音生成语音")
    private Boolean supportAudioToAudio;

    /**
     * 是否支持文字生成视频
     */
    @Column(comment = "是否支持文字生成视频")
    private Boolean supportTextToVideo;

    /**
     * 是否支持图片生成视频
     */
    @Column(comment = "是否支持图片生成视频")
    private Boolean supportImageToVideo;

    /**
     * 大模型请求地址
     */
    @Column(comment = "大模型请求地址")
    private String llmEndpoint;

    /**
     * 大模型名称
     */
    @Column(comment = "大模型名称")
    private String llmModel;

    /**
     * 大模型 API KEY
     */
    @Column(comment = "大模型 API KEY")
    private String llmApiKey;

    /**
     * 大模型其他属性配置
     */
    private String llmExtraConfig;

    /**
     * 其他配置内容
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "其他配置内容")
    private Map<String, Object> options;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getDeptId() {
        return deptId;
    }

    public void setDeptId(BigInteger deptId) {
        this.deptId = deptId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSupportChat() {
        return supportChat;
    }

    public void setSupportChat(Boolean supportChat) {
        this.supportChat = supportChat;
    }

    public Boolean getSupportFunctionCalling() {
        return supportFunctionCalling;
    }

    public void setSupportFunctionCalling(Boolean supportFunctionCalling) {
        this.supportFunctionCalling = supportFunctionCalling;
    }

    public Boolean getSupportEmbed() {
        return supportEmbed;
    }

    public void setSupportEmbed(Boolean supportEmbed) {
        this.supportEmbed = supportEmbed;
    }

    public Boolean getSupportReranker() {
        return supportReranker;
    }

    public void setSupportReranker(Boolean supportReranker) {
        this.supportReranker = supportReranker;
    }

    public Boolean getSupportTextToImage() {
        return supportTextToImage;
    }

    public void setSupportTextToImage(Boolean supportTextToImage) {
        this.supportTextToImage = supportTextToImage;
    }

    public Boolean getSupportImageToImage() {
        return supportImageToImage;
    }

    public void setSupportImageToImage(Boolean supportImageToImage) {
        this.supportImageToImage = supportImageToImage;
    }

    public Boolean getSupportTextToAudio() {
        return supportTextToAudio;
    }

    public void setSupportTextToAudio(Boolean supportTextToAudio) {
        this.supportTextToAudio = supportTextToAudio;
    }

    public Boolean getSupportAudioToAudio() {
        return supportAudioToAudio;
    }

    public void setSupportAudioToAudio(Boolean supportAudioToAudio) {
        this.supportAudioToAudio = supportAudioToAudio;
    }

    public Boolean getSupportTextToVideo() {
        return supportTextToVideo;
    }

    public void setSupportTextToVideo(Boolean supportTextToVideo) {
        this.supportTextToVideo = supportTextToVideo;
    }

    public Boolean getSupportImageToVideo() {
        return supportImageToVideo;
    }

    public void setSupportImageToVideo(Boolean supportImageToVideo) {
        this.supportImageToVideo = supportImageToVideo;
    }

    public String getLlmEndpoint() {
        return llmEndpoint;
    }

    public void setLlmEndpoint(String llmEndpoint) {
        this.llmEndpoint = llmEndpoint;
    }

    public String getLlmModel() {
        return llmModel;
    }

    public void setLlmModel(String llmModel) {
        this.llmModel = llmModel;
    }

    public String getLlmApiKey() {
        return llmApiKey;
    }

    public void setLlmApiKey(String llmApiKey) {
        this.llmApiKey = llmApiKey;
    }

    public String getLlmExtraConfig() {
        return llmExtraConfig;
    }

    public void setLlmExtraConfig(String  llmExtraConfig) {
        this.llmExtraConfig = llmExtraConfig;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

}

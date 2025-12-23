package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class PluginItemBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 插件工具id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "插件工具id")
    private BigInteger id;

    /**
     * 插件id
     */
    @Column(comment = "插件id")
    private BigInteger pluginId;

    /**
     * 名称
     */
    @Column(comment = "名称")
    private String name;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;

    /**
     * 基础路径
     */
    @Column(comment = "基础路径")
    private String basePath;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 是否启用
     */
    @Column(comment = "是否启用")
    private Integer status;

    /**
     * 输入参数
     */
    @Column(comment = "输入参数")
    private String inputData;

    /**
     * 输出参数
     */
    @Column(comment = "输出参数")
    private String outputData;

    /**
     * 请求方式【Post, Get, Put, Delete】
     */
    @Column(comment = "请求方式【Post, Get, Put, Delete】")
    private String requestMethod;

    /**
     * 服务状态[0 下线 1 上线]
     */
    @Column(comment = "服务状态[0 下线 1 上线]")
    private Integer serviceStatus;

    /**
     * 调试状态【0失败 1成功】
     */
    @Column(comment = "调试状态【0失败 1成功】")
    private Integer debugStatus;

    /**
     * 英文名称
     */
    @Column(comment = "英文名称")
    private String englishName;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getPluginId() {
        return pluginId;
    }

    public void setPluginId(BigInteger pluginId) {
        this.pluginId = pluginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Integer getDebugStatus() {
        return debugStatus;
    }

    public void setDebugStatus(Integer debugStatus) {
        this.debugStatus = debugStatus;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

}

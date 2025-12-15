package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class AiWorkflowExecRecordBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 执行标识
     */
    @Column(comment = "执行标识")
    private String execKey;

    /**
     * 工作流ID
     */
    @Column(comment = "工作流ID")
    private BigInteger workflowId;

    /**
     * 标题
     */
    @Column(comment = "标题")
    private String title;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;

    /**
     * 输入
     */
    @Column(comment = "输入")
    private String input;

    /**
     * 输出
     */
    @Column(comment = "输出")
    private String output;

    /**
     * 工作流执行时的配置
     */
    @Column(comment = "工作流执行时的配置")
    private String workflowJson;

    /**
     * 开始时间
     */
    @Column(comment = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Column(comment = "结束时间")
    private Date endTime;

    /**
     * 消耗总token
     */
    @Column(comment = "消耗总token")
    private BigInteger tokens;

    /**
     * 数据状态
     */
    @Column(comment = "数据状态")
    private Integer status;

    /**
     * 执行人标识[有可能是用户|外部|定时任务等情况]
     */
    @Column(comment = "执行人标识[有可能是用户|外部|定时任务等情况]")
    private String createdKey;

    /**
     * 执行人
     */
    @Column(comment = "执行人")
    private String createdBy;

    /**
     * 错误信息
     */
    @Column(comment = "错误信息")
    private String errorInfo;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getExecKey() {
        return execKey;
    }

    public void setExecKey(String execKey) {
        this.execKey = execKey;
    }

    public BigInteger getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(BigInteger workflowId) {
        this.workflowId = workflowId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getWorkflowJson() {
        return workflowJson;
    }

    public void setWorkflowJson(String workflowJson) {
        this.workflowJson = workflowJson;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigInteger getTokens() {
        return tokens;
    }

    public void setTokens(BigInteger tokens) {
        this.tokens = tokens;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedKey() {
        return createdKey;
    }

    public void setCreatedKey(String createdKey) {
        this.createdKey = createdKey;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

}

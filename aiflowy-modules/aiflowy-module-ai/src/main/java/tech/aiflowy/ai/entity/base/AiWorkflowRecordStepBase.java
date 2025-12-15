package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class AiWorkflowRecordStepBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 执行记录ID
     */
    @Column(comment = "执行记录ID")
    private BigInteger recordId;

    /**
     * 执行标识
     */
    @Column(comment = "执行标识")
    private String execKey;

    /**
     * 节点ID
     */
    @Column(comment = "节点ID")
    private String nodeId;

    /**
     * 节点名称
     */
    @Column(comment = "节点名称")
    private String nodeName;

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
     * 节点信息
     */
    @Column(comment = "节点信息")
    private String nodeData;

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

    public BigInteger getRecordId() {
        return recordId;
    }

    public void setRecordId(BigInteger recordId) {
        this.recordId = recordId;
    }

    public String getExecKey() {
        return execKey;
    }

    public void setExecKey(String execKey) {
        this.execKey = execKey;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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

    public String getNodeData() {
        return nodeData;
    }

    public void setNodeData(String nodeData) {
        this.nodeData = nodeData;
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

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

}

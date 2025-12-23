package tech.aiflowy.job.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;


public class SysJobLogBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 任务ID
     */
    @Column(comment = "任务ID")
    private BigInteger jobId;

    /**
     * 任务名称
     */
    @Column(comment = "任务名称")
    private String jobName;

    /**
     * 任务参数
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "任务参数")
    private Map<String, Object> jobParams;

    /**
     * 执行结果
     */
    @Column(comment = "执行结果")
    private String jobResult;

    /**
     * 错误信息
     */
    @Column(comment = "错误信息")
    private String errorInfo;

    /**
     * 执行状态
     */
    @Column(comment = "执行状态")
    private Integer status;

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
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 备注
     */
    @Column(comment = "备注")
    private String remark;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getJobId() {
        return jobId;
    }

    public void setJobId(BigInteger jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Map<String, Object> getJobParams() {
        return jobParams;
    }

    public void setJobParams(Map<String, Object> jobParams) {
        this.jobParams = jobParams;
    }

    public String getJobResult() {
        return jobResult;
    }

    public void setJobResult(String jobResult) {
        this.jobResult = jobResult;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}

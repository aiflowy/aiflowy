package tech.aiflowy.job.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import tech.aiflowy.common.entity.DateEntity;


public class SysJobBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 部门ID
     */
    @Column(comment = "部门ID")
    private BigInteger deptId;

    /**
     * 租户ID
     */
    @Column(tenantId = true, comment = "租户ID")
    private BigInteger tenantId;

    /**
     * 任务名称
     */
    @Column(comment = "任务名称")
    private String jobName;

    /**
     * 任务类型
     */
    @Column(comment = "任务类型")
    private Integer jobType;

    /**
     * 任务参数
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "任务参数")
    private Map<String, Object> jobParams;

    /**
     * cron表达式
     */
    @Column(comment = "cron表达式")
    private String cronExpression;

    /**
     * 是否并发执行
     */
    @Column(comment = "是否并发执行")
    private Integer allowConcurrent;

    /**
     * 错过策略
     */
    @Column(comment = "错过策略")
    private Integer misfirePolicy;

    /**
     * 其他配置
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "其他配置")
    private Map<String, Object> options;

    /**
     * 数据状态
     */
    @Column(comment = "数据状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建者
     */
    @Column(comment = "创建者")
    private BigInteger createdBy;

    /**
     * 修改时间
     */
    @Column(comment = "修改时间")
    private Date modified;

    /**
     * 修改者
     */
    @Column(comment = "修改者")
    private BigInteger modifiedBy;

    /**
     * 备注
     */
    @Column(comment = "备注")
    private String remark;

    /**
     * 删除标识
     */
    @Column(isLogicDelete = true, comment = "删除标识")
    private Integer isDeleted;

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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public Map<String, Object> getJobParams() {
        return jobParams;
    }

    public void setJobParams(Map<String, Object> jobParams) {
        this.jobParams = jobParams;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Integer getAllowConcurrent() {
        return allowConcurrent;
    }

    public void setAllowConcurrent(Integer allowConcurrent) {
        this.allowConcurrent = allowConcurrent;
    }

    public Integer getMisfirePolicy() {
        return misfirePolicy;
    }

    public void setMisfirePolicy(Integer misfirePolicy) {
        this.misfirePolicy = misfirePolicy;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public BigInteger getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public BigInteger getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(BigInteger modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

}

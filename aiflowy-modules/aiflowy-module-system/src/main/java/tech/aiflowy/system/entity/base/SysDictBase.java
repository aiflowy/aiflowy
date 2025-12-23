package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import tech.aiflowy.common.entity.DateEntity;


public class SysDictBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 数据字典名称
     */
    @Column(comment = "数据字典名称")
    private String name;

    /**
     * 字典编码
     */
    @Column(comment = "字典编码")
    private String code;

    /**
     * 字典描述或备注
     */
    @Column(comment = "字典描述或备注")
    private String description;

    /**
     * 字典类型 1 自定义字典、2 数据表字典、 3 枚举类字典、 4 系统字典（自定义 DictLoader）
     */
    @Column(comment = "字典类型 1 自定义字典、2 数据表字典、 3 枚举类字典、 4 系统字典（自定义 DictLoader）")
    private Integer dictType;

    /**
     * 排序编号
     */
    @Column(comment = "排序编号")
    private Integer sortNo;

    /**
     * 是否启用
     */
    @Column(comment = "是否启用")
    private Integer status;

    /**
     * 扩展字典  存放 json
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "扩展字典  存放 json")
    private Map<String, Object> options;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 修改时间
     */
    @Column(comment = "修改时间")
    private Date modified;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDictType() {
        return dictType;
    }

    public void setDictType(Integer dictType) {
        this.dictType = dictType;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}

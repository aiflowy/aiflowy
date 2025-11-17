package tech.aiflowy.datacenter.entity.vo;

import java.math.BigInteger;

public class HeaderVo {

    private String key;
    private String dataIndex;
    private String title;
    private Integer fieldType;
    private Integer required;
    private BigInteger fieldId;
    private BigInteger tableId;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public BigInteger getFieldId() {
        return fieldId;
    }

    public void setFieldId(BigInteger fieldId) {
        this.fieldId = fieldId;
    }

    public BigInteger getTableId() {
        return tableId;
    }

    public void setTableId(BigInteger tableId) {
        this.tableId = tableId;
    }
}

package tech.aiflowy.core.dict;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 在 DictItem 的属性中，key value 是同一个值，而 label 和 title 是同一个值
 * 进行这么设计的原因，是为了适配不同的前段组件，不需要对数据进行字段转换
 */
public class DictItem implements Serializable {

    /**
     * 值
     */
    private Object value;
    /**
     * key
     */
    private Object key;
    /**
     * 标签
     */
    private String label;
    /**
     * 标题
     */
    private String title;
    /**
     * 禁用
     */
    private Boolean disabled;
    private Integer layerNo;
    private List<DictItem> children;

    public DictItem() {
    }

    public DictItem(Object value, String label) {
        this.setValue(value);
        this.setLabel(label);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        this.key = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        this.title = label;
    }

    public List<DictItem> getChildren() {
        return children;
    }

    public void setChildren(List<DictItem> children) {
        this.children = children;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getLayerNo() {
        return layerNo;
    }

    public void setLayerNo(Integer layerNo) {
        this.layerNo = layerNo;
    }

    public void addChild(DictItem childDictItem) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(childDictItem);
    }
}

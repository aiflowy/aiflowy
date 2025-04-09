package tech.aiflowy.core.dict;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Dict implements Serializable {

    private String name;
    private String code;
    private String description;

    private List<DictItem> items;

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

    public List<DictItem> getItems() {
        return items;
    }

    public void setItems(List<DictItem> items) {
        this.items = items;
    }

    public void addItem(DictItem item){
        if (this.items == null){
            this.items = new ArrayList<>();
        }
        items.add(item);
    }
}

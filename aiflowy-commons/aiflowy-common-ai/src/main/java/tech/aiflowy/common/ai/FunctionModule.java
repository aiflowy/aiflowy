package tech.aiflowy.common.ai;

import java.io.Serializable;

public class FunctionModule implements Serializable {
    private String name;
    private String title;
    private String description;

    private Object functionsObject;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Object getFunctionsObject() {
        return functionsObject;
    }

    public void setFunctionsObject(Object functionsObject) {
        this.functionsObject = functionsObject;
    }
}

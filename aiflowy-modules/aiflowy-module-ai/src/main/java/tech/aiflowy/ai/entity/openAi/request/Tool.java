package tech.aiflowy.ai.entity.openAi.request;

import java.util.Map;

/**
 * 工具定义类
 * 用于定义AI可以调用的外部工具或函数
 */
public class Tool {

    /**
     * 工具类型，目前主要支持"function"
     */
    private String type;

    /**
     * 函数定义详细信息
     */
    private Function function;

    public Tool() {
    }

    public Tool(String type, Function function) {
        this.type = type;
        this.function = function;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    @Override
    public String toString() {
        return "Tool{" +
                "type='" + type + '\'' +
                ", function=" + function +
                '}';
    }

    // Function 内部类 - 表示函数定义
    public static class Function {
        /**
         * 函数名称，必须是有效的标识符
         */
        private String name;

        /**
         * 函数功能描述，帮助AI理解何时调用此函数
         */
        private String description;

        /**
         * 函数参数定义，使用JSON Schema格式
         */
        private Map<String, Object> parameters;


        public Function() {
        }

        public Function(String name, String description, Map<String, Object> parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
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

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            return "Function{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", parameters=" + parameters +
                    '}';
        }
    }
}
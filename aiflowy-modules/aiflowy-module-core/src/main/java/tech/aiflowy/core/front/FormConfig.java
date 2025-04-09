package tech.aiflowy.core.front;

import java.util.List;
import java.util.Map;

public class FormConfig {
    //组件的类型
    private String type;

    //是否包裹 col 组件，如果包裹，由 DynamicFormItem 自行包裹
    private Boolean wrapCol;

    //组件的其他属性
    private Map<String,Object> attrs;

    //验证规则
    private List<RuleConfig> rules;
}

package tech.aiflowy.core.front;

public class RuleConfig {

    //类型，常见有 string |number |boolean |url | email。更多请参考此处:https://github.com/yiminghe/async-validator#type
    private String type;

    //string 类型时为字符串长度；number 类型时为确定数字； array 类型时为数组长度
    private int len;

    //必须设置 type：string 类型为字符串最大长度；number 类型时为最大值；array 类型时为数组最大长度
    private int max;

    //必须设置 type：string 类型为字符串最小长度；number 类型时为最小值；array 类型时为数组最小长度
    private int min;

    //正则表达式匹配
    private String pattern;

    //是否为必选字段
    private Boolean required;

    //仅警告，不阻塞表单提交
    private Boolean warningOnly;

    //错误信息，不设置时会通过模板自动生成
    private String message;
}

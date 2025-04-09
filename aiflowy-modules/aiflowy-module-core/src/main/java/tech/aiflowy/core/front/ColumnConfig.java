package tech.aiflowy.core.front;

public class ColumnConfig {

    //编辑类型
    private FormConfig form;

    //数据字典，配置后，自动去请求后台的数据字典 url 来填充数据
    private DictConfig dict;

    //占位长度，默认值为 20
    private int colSpan;

    //offset 默认值为 2
    private int colOffset;

    //分组的 key
    private String groupKey;

    //占位字符
    private String placeholder;

    //是否支持搜索
    private boolean supportSearch;
}

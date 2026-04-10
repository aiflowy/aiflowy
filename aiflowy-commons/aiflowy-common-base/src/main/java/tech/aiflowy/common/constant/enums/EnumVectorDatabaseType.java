package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "向量数据库类型", code = "vectorDatabaseType", keyField = "code", labelField = "text")
public enum EnumVectorDatabaseType {

    REDIS("redis","Redis"),
    MILVUS("milvus","Milvus"),
    ELASTICSEARCH("elasticsearch","Elasticsearch"),
    OPENSEARCH("opensearch","OpenSearch"),
    ALIYUN("aliyun","阿里云"),
    TENCENT("tencent","腾讯云"),
    ;

    private final String code;
    private final String text;

    EnumVectorDatabaseType(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}

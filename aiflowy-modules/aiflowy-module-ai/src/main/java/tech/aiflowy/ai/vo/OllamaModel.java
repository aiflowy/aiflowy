package tech.aiflowy.ai.vo;

import tech.aiflowy.core.utils.CommonUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 *  实体类。
 *
 * @author wangGangQiang
 * @since 2025-03-13
 */
public class OllamaModel {
    private String name;
    private Long size;

    private String apiUrl;

    private String modified_at;

    //1用户已经加入了大模型 0没有加入大模型
    private int hasJoinModel;

    public void setHasJoinModel(int hasJoinModel) {
        this.hasJoinModel = hasJoinModel;
    }

    public int getHasJoinModel() {
        return hasJoinModel;
    }

    public String getModified_at() {
        return getModifyTime(this.modified_at);
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getSizeString() {
        return CommonUtils.calcByte(this.size);
    }

    public OllamaModel(String name, Long size, String apiUrl, String modified_at) {
        this.name = name;
        this.size = size;
        this.apiUrl = apiUrl;
        this.modified_at = modified_at;
    }



    /**
     * 日期转yyyy/mm/dd
     * @param timeString 日期字符串
     * @return
     */
    private String getModifyTime(String timeString){
        // 解析时间字符串
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(timeString);

        // 定义格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 格式化输出
        String tm = offsetDateTime.format(formatter);
        return offsetDateTime.format(formatter);
    }
}

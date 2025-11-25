package tech.aiflowy.datacenter.excel;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public class ReadResVo {

    /**
     * 成功数
     */
    private int successCount = 0;
    /**
     * 失败数
     */
    private int errorCount = 0;
    /**
     * 总数
     */
    private int totalCount = 0;

    /**
     * 错误行
     */
    private List<JSONObject> errorRows;

    public ReadResVo() {
    }

    public ReadResVo(int successCount, int errorCount, int totalCount, List<JSONObject> errorRows) {
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.totalCount = totalCount;
        this.errorRows = errorRows;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<JSONObject> getErrorRows() {
        return errorRows;
    }

    public void setErrorRows(List<JSONObject> errorRows) {
        this.errorRows = errorRows;
    }
}

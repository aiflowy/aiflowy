package tech.aiflowy.datacenter.excel;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.read.listener.ReadListener;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;
import tech.aiflowy.datacenter.service.DatacenterTableService;

import java.math.BigInteger;
import java.util.*;

public class ReadDataListener implements ReadListener<LinkedHashMap<Integer, Object>> {

    private static final Logger log = LoggerFactory.getLogger(ReadDataListener.class);

    private BigInteger tableId;

    private List<DatacenterTableFields> fields;

    private LoginAccount loginAccount;

    private final Map<String, Integer> headFieldIndex = new HashMap<>();

    private int successCount = 0;
    private int errorCount = 0;
    private int totalCount = 0;

    private final List<JSONObject> errorRows = new ArrayList<>();

    public ReadDataListener() {
    }

    public ReadDataListener(BigInteger tableId, List<DatacenterTableFields> fields, LoginAccount loginAccount) {
        this.tableId = tableId;
        this.fields = fields;
        this.loginAccount = loginAccount;
    }

    @Override
    public void invoke(LinkedHashMap<Integer, Object> o, AnalysisContext analysisContext) {
        DatacenterTableService service = SpringContextUtil.getBean(DatacenterTableService.class);
        JSONObject obj = new JSONObject();
        for (DatacenterTableFields field : fields) {
            String fieldName = field.getFieldName();
            Integer i = headFieldIndex.get(fieldName);
            if (i != null) {
                obj.put(fieldName, o.get(i));
            }
        }
        try {
            service.saveValue(tableId, obj, loginAccount);
            successCount++;
        } catch (Exception e) {
            errorCount++;
            log.error("导入数据到数据中枢失败，具体值：{}", obj, e);
            errorRows.add(obj);
        }
        totalCount++;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Set<Map.Entry<Integer, ReadCellData<?>>> entries = headMap.entrySet();
        for (Map.Entry<Integer, ReadCellData<?>> entry : entries) {
            Integer key = entry.getKey();
            String field = entry.getValue().getStringValue();
            headFieldIndex.put(field, key);
        }
        if (headFieldIndex.size() != fields.size()) {
            throw new RuntimeException("表头字段数量与表结构对应不上！");
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<DatacenterTableFields> getFields() {
        return fields;
    }

    public void setFields(List<DatacenterTableFields> fields) {
        this.fields = fields;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<JSONObject> getErrorRows() {
        return errorRows;
    }
}

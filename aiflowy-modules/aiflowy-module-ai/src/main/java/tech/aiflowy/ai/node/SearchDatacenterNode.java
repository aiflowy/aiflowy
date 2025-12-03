package tech.aiflowy.ai.node;

import com.agentsflex.core.util.StringUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.tenant.TenantManager;
import dev.tinyflow.core.chain.Chain;
import dev.tinyflow.core.chain.Parameter;
import dev.tinyflow.core.node.BaseNode;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.common.entity.DatacenterQuery;
import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;
import tech.aiflowy.datacenter.service.DatacenterTableService;
import tech.aiflowy.datacenter.utils.WhereConditionSecurityChecker;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchDatacenterNode extends BaseNode {

    private static final Logger log = LoggerFactory.getLogger(SearchDatacenterNode.class);
    private BigInteger tableId;
    private String where;
    private Long limit;

    public SearchDatacenterNode() {
    }

    public SearchDatacenterNode(BigInteger tableId, String where, Long limit) {
        this.tableId = tableId;
        this.where = where;
        this.limit = limit;
    }

    @Override
    protected Map<String, Object> execute(Chain chain) {

        Map<String, Object> map = chain.getState().resolveParameters(this);
        Map<String, Object> res = new HashMap<>();
        long limitNum = 10;
        if (limit != null) {
            limitNum = Long.parseLong(limit.toString());
        }

        DatacenterTableService service = SpringContextUtil.getBean(DatacenterTableService.class);

        DatacenterQuery condition = new DatacenterQuery();
        condition.setTableId(tableId);
        condition.setPageNumber(1L);
        condition.setPageSize(limitNum);
        // 组合查询条件
        if (where != null) {
            setCondition(where, condition, map);
        }
        try {
            TenantManager.ignoreTenantCondition();
            Page<Row> pageData = service.getPageData(condition);

            String key = "rows";
            List<Parameter> outputDefs = getOutputDefs();
            if (outputDefs != null && !outputDefs.isEmpty()) {
                String defName = outputDefs.get(0).getName();
                if (StringUtil.hasText(defName)) key = defName;
            }
            res.put(key, pageData.getRecords());
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return res;
    }

    public BigInteger getTableId() {
        return tableId;
    }

    public void setTableId(BigInteger tableId) {
        this.tableId = tableId;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    private void setCondition(String where, DatacenterQuery condition, Map<String, Object> params) {
        // 条件封装
        Pattern pattern = Pattern.compile("\\{\\{(.+?)\\}\\}");
        Matcher matcher = pattern.matcher(where);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = params.get(key);
            if (value == null) {
                throw new RuntimeException("参数" + key + "不存在");
            }
            String replacement = value.toString();
            matcher.appendReplacement(result, "'" + replacement + "'");
        }
        matcher.appendTail(result);

        try {
            Expression expression = CCJSqlParserUtil.parseCondExpression(result.toString());
            if (expression != null) {
                WhereConditionSecurityChecker checker = new WhereConditionSecurityChecker();
                DatacenterTableService service = SpringContextUtil.getBean(DatacenterTableService.class);
                List<DatacenterTableFields> fields = service.getFields(tableId);
                Set<String> columns = fields.stream().map(DatacenterTableFields::getFieldName).collect(Collectors.toSet());
                columns.add("id");
                columns.add("created");
                columns.add("modified");
                columns.add("created_by");
                columns.add("modified_by");
                checker.checkConditionSafety(expression, columns);
                condition.setWhere(expression.toString());
            }
        } catch (Exception e) {
            log.error("WHERE SQL解析错误：", e);
            throw new RuntimeException(e);
        }
    }
}

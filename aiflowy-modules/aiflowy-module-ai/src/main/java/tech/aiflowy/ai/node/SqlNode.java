package tech.aiflowy.ai.node;
import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.Parameter;
import com.agentsflex.core.chain.node.BaseNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.template.stat.ast.For;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import tech.aiflowy.common.util.Maps;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * SQL查询节点
 *
 * @author tao
 * @date 2025-05-21
 */
public class SqlNode extends BaseNode {

    private String sql;

    private static final Logger logger = LoggerFactory.getLogger(SqlNode.class);

    public SqlNode() {
    }

    public SqlNode(String sql) {
        this.sql = sql;
    }

    @Override
    protected Map<String, Object> execute(Chain chain) {

        Map<String, Object> map = chain.getParameterValues(this);
        Map<String, Object> res = new HashMap<>();


        Map<String, Object> formatSqlMap = formatSql(sql,map);
        String formatSql = (String)formatSqlMap.get("replacedSql");

        Statement statement = null;
        try {
            statement  = CCJSqlParserUtil.parse(formatSql);

        } catch (JSQLParserException e) {
            logger.error("sql 解析报错：",e);
            throw new BusinessException("SQL解析失败，请确认SQL语法无误");
        }

        if (!(statement instanceof Select)) {
            logger.error("sql 解析报错：statement instanceof Select 结果为false");
            throw new BusinessException("仅支持查询语句！");
        }

        List<String> paramNames = (List<String>) formatSqlMap.get("paramNames");

        List<Object> paramValues = new ArrayList<>();
        paramNames.forEach(paramName -> {
            Object o = map.get(paramName);
            paramValues.add(o);
        });

        List<Row> rows = Db.selectListBySql(formatSql, paramValues.toArray());

        if (rows == null || rows.isEmpty()) {
            return Collections.emptyMap();
        }

        res.put("queryData",rows);
        return res;
    }

    private Map<String, Object> formatSql(String rawSql, Map<String, Object> paramMap) {

        if (!StringUtils.hasLength(rawSql)) {
            logger.error("sql解析报错：sql为空");
            throw new BusinessException("sql 不能为空！");
        }

        // 匹配 {{?...}} 表示可用占位符的参数
        Pattern paramPattern = Pattern.compile("\\{\\{\\?([^}]+)}}");

        // 匹配 {{...}} 表示直接替换的参数（非占位符）
        Pattern directPattern = Pattern.compile("\\{\\{([^}?][^}]*)}}");

        List<String> paramNames = new ArrayList<>();
        StringBuffer sqlBuffer = new StringBuffer();

        // 替换 {{?...}}  ->  ?
        Matcher paramMatcher = paramPattern.matcher(rawSql);
        while (paramMatcher.find()) {
            String paramName = paramMatcher.group(1).trim();
            paramNames.add(paramName);
            paramMatcher.appendReplacement(sqlBuffer, "?");
        }
        paramMatcher.appendTail(sqlBuffer);
        String intermediateSql = sqlBuffer.toString();

        // 替换 {{...}}  -> 实际值（用于表名/列名等）
        sqlBuffer = new StringBuffer(); // 清空 buffer 重新处理
        Matcher directMatcher = directPattern.matcher(intermediateSql);
        while (directMatcher.find()) {
            String key = directMatcher.group(1).trim();
            Object value = paramMap.get(key);
            if (value == null) {
                logger.error("未找到参数：" + key);
                throw new BusinessException("sql解析失败，请确保sql语法正确！");
            }

            String safeValue = value.toString();

            directMatcher.appendReplacement(sqlBuffer, Matcher.quoteReplacement(safeValue));
        }
        directMatcher.appendTail(sqlBuffer);

        String finalSql = sqlBuffer.toString().trim();

        // 清理末尾分号与中文引号
        if (finalSql.endsWith(";") || finalSql.endsWith("；")) {
            finalSql = finalSql.substring(0, finalSql.length() - 1);
        }
        finalSql = finalSql.replace("“", "\"").replace("”", "\"");

        logger.info("Final SQL: {}", finalSql);
        logger.info("Param names: {}", paramNames);

        Map<String, Object> result = new HashMap<>();
        result.put("replacedSql", finalSql);
        result.put("paramNames", paramNames);
        return result;
    }



    @Override
    public String toString() {
        return "SqlNode{" +
                "sql='" + sql + '\'' +
                ", outputDefs=" + outputDefs +
                ", parameters=" + parameters +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", async=" + async +
                ", inwardEdges=" + inwardEdges +
                ", outwardEdges=" + outwardEdges +
                ", condition=" + condition +
                ", memory=" + memory +
                ", nodeStatus=" + nodeStatus +
                '}';
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}

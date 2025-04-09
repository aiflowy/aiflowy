package tech.aiflowy.core.dict.loader;

import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.core.dict.Dict;
import tech.aiflowy.core.dict.DictItem;
import tech.aiflowy.core.dict.DictLoader;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatabaseDictLoader implements DictLoader {
    private static final Object[] EMPTY_PARAMS = new Object[0];
    private String code;
    private String tableName;
    private String keyColumn;
    private String labelColumn;
    private String parentColumn;
    private String orderBy;

    public DatabaseDictLoader(String code, String tableName, String keyColumn, String labelColumn) {
        this.code = code;
        this.tableName = tableName;
        this.keyColumn = keyColumn;
        this.labelColumn = labelColumn;
    }

    public DatabaseDictLoader(String code, String tableName, String keyColumn, String labelColumn, String parentColumn) {
        this.code = code;
        this.tableName = tableName;
        this.keyColumn = keyColumn;
        this.labelColumn = labelColumn;
        this.parentColumn = parentColumn;
    }

    public DatabaseDictLoader(String code, String tableName, String keyColumn, String labelColumn, String parentColumn, String orderBy) {
        this.code = code;
        this.tableName = tableName;
        this.keyColumn = keyColumn;
        this.labelColumn = labelColumn;
        this.parentColumn = parentColumn;
        this.orderBy = orderBy;
    }

    @Override
    public String code() {
        return code;
    }


    @Override
    public Dict load(String keyword, Map<String, String[]> parameters) {
        String sql = "SELECT " + keyColumn + ", " + labelColumn;
        if (StringUtils.hasText(parentColumn)) {
            sql += ", " + parentColumn;
        }
        sql += " FROM " + tableName;
        if (StringUtils.hasText(keyword)) {
            sql += "WHERE " + labelColumn + " = ?";
        }
        if (StringUtils.hasText(orderBy)) {
            sql += " ORDER BY " + orderBy;
        }

        List<Row> rows = Db.selectListBySql(sql, StringUtils.hasText(keyword) ? new Object[]{keyword.trim()} : EMPTY_PARAMS);
        if (rows == null || rows.isEmpty()) {
            return null;
        }

        List<DictItem> items = new ArrayList<>(rows.size());

        Boolean asTree = RequestUtil.getParamAsBoolean(parameters,"asTree");

        //有树形结构
        if (StringUtils.hasText(parentColumn)) {
            List<Row> topLayerRows = findTopLayerRows(rows);
            //以树形结构输出
            if (asTree != null && asTree) {
                makeTree(topLayerRows, items, rows);
            }
            //以平级结构输出
            else {
                makeLayer(0, topLayerRows, items, rows);
            }
        }
        //无树形结构数据
        else {
            for (Row row : rows) {
                DictItem dictItem = new DictItem();
                dictItem.setValue(row.get(keyColumn));
                dictItem.setLabel(String.valueOf(row.get(labelColumn)));
                items.add(dictItem);
            }
        }

        Dict dict = new Dict();
        dict.setCode(code);
        dict.setItems(items);
        return dict;
    }

    private void makeTree(List<Row> parentRows, List<DictItem> parentItems, List<Row> allRows) {
        for (Row parentRow : parentRows) {
            DictItem parentItem = row2DictItem(0, parentRow);
            parentItems.add(parentItem);

            List<Row> children = new ArrayList<>();
            for (Row maybeChild : allRows) {
                if (Objects.equals(maybeChild.get(parentColumn), parentRow.get(keyColumn))) {
                    children.add(maybeChild);
                }
            }
            if (!children.isEmpty()) {
                List<DictItem> childrenItems = new ArrayList<>(children.size());
                parentItem.setChildren(childrenItems);
                makeTree(children, childrenItems, allRows);
            }
        }
    }

    private void makeLayer(int layerNo, List<Row> parentRows, List<DictItem> parentItems, List<Row> allRows) {
        for (Row parentRow : parentRows) {
            parentItems.add(row2DictItem(layerNo, parentRow));

            List<Row> children = new ArrayList<>();
            for (Row maybeChild : allRows) {
                if (Objects.equals(maybeChild.get(parentColumn), parentRow.get(keyColumn))) {
                    children.add(maybeChild);
                }
            }
            if (!children.isEmpty()) {
                makeLayer(layerNo + 1, children, parentItems, allRows);
            }
        }
    }


    private DictItem row2DictItem(int layerNo, Row row) {
        DictItem dictItem = new DictItem();
        dictItem.setValue(row.get(keyColumn));
        dictItem.setLabel(Tree.getPrefix(layerNo) + row.get(labelColumn));
        dictItem.setLayerNo(layerNo);
        return dictItem;
    }


    private List<Row> findTopLayerRows(List<Row> rows) {
        List<Row> topLayerRows = new ArrayList<>();
        for (Row row : rows) {
            boolean foundParent = false;
            for (Row row1 : rows) {
                if (Objects.equals(row1.get(keyColumn), row.get(parentColumn))) {
                    foundParent = true;
                    break;
                }
            }
            if (!foundParent) {
                topLayerRows.add(row);
            }
        }
        return topLayerRows;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public String getLabelColumn() {
        return labelColumn;
    }

    public void setLabelColumn(String labelColumn) {
        this.labelColumn = labelColumn;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}

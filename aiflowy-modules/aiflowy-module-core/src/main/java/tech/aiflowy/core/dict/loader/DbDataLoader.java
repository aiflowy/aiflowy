package tech.aiflowy.core.dict.loader;

import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.core.dict.Dict;
import tech.aiflowy.core.dict.DictItem;
import tech.aiflowy.core.dict.DictLoader;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DbDataLoader<T> implements DictLoader {

    private final String code;
    private final BaseMapper<T> mapper;
    // 下划线命名
    private final String keyColumn;
    private final String labelColumn;
    private final String parentColumn;
    // 驼峰命名
    private final String keyColumnCamelCase;
    private final String labelColumnCamelCase;
    private final String parentColumnCamelCase;

    private final String orderBy;
    private boolean queryStatus = false;

    public DbDataLoader(String code,
                        BaseMapper<T> mapper,
                        String keyColumn,
                        String labelColumn,
                        String parentColumn,
                        String orderBy,
                        boolean queryStatus) {
        this.code = code;
        this.mapper = mapper;
        this.keyColumn = keyColumn;
        this.labelColumn = labelColumn;
        this.parentColumn = parentColumn;

        this.keyColumnCamelCase = StrUtil.toCamelCase(this.keyColumn);
        this.labelColumnCamelCase = StrUtil.toCamelCase(labelColumn);
        this.parentColumnCamelCase = StrUtil.toCamelCase(parentColumn);

        this.orderBy = orderBy;
        this.queryStatus = queryStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public Dict load(String keyword, Map<String, String[]> parameters) {

        QueryWrapper where = QueryWrapper.create();
        if (StrUtil.isNotEmpty(keyword)) {
            where.eq(labelColumn, keyword);
        }
        if (queryStatus) {
            where.eq("status", 1);
        }
        if (StrUtil.isNotEmpty(orderBy)) {
            where.orderBy(orderBy);
        }

        List<T> records = mapper.selectListByQuery(where);
        List<JSONObject> rows = new ArrayList<>();
        for (T record : records) {
            rows.add(JSON.parseObject(JSON.toJSONString(record)));
        }
        Boolean asTree = RequestUtil.getParamAsBoolean(parameters, "asTree");

        List<DictItem> items = new ArrayList<>(rows.size());

        //有树形结构
        if (StringUtils.hasText(parentColumn)) {
            List<JSONObject> topLayerRows = findTopLayerRows(rows);
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
            for (JSONObject row : rows) {
                DictItem dictItem = new DictItem();
                dictItem.setValue(row.get(keyColumnCamelCase));
                dictItem.setLabel(String.valueOf(row.get(labelColumnCamelCase)));
                items.add(dictItem);
            }
        }

        Dict dict = new Dict();
        dict.setCode(code);
        dict.setItems(items);
        return dict;
    }

    private void makeTree(List<JSONObject> parentRows, List<DictItem> parentItems, List<JSONObject> allRows) {
        for (JSONObject parentRow : parentRows) {
            DictItem parentItem = row2DictItem(0, parentRow);
            parentItems.add(parentItem);

            List<JSONObject> children = new ArrayList<>();
            for (JSONObject maybeChild : allRows) {
                if (Objects.equals(maybeChild.get(parentColumnCamelCase), parentRow.get(keyColumnCamelCase))) {
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

    private void makeLayer(int layerNo, List<JSONObject> parentRows, List<DictItem> parentItems, List<JSONObject> allRows) {
        for (JSONObject parentRow : parentRows) {
            parentItems.add(row2DictItem(layerNo, parentRow));

            List<JSONObject> children = new ArrayList<>();
            for (JSONObject maybeChild : allRows) {
                if (Objects.equals(maybeChild.get(parentColumnCamelCase), parentRow.get(keyColumnCamelCase))) {
                    children.add(maybeChild);
                }
            }
            if (!children.isEmpty()) {
                makeLayer(layerNo + 1, children, parentItems, allRows);
            }
        }
    }


    private DictItem row2DictItem(int layerNo, JSONObject row) {
        DictItem dictItem = new DictItem();
        dictItem.setValue(row.get(keyColumnCamelCase));
        dictItem.setLabel(Tree.getPrefix(layerNo) + row.get(labelColumnCamelCase));
        dictItem.setLayerNo(layerNo);
        return dictItem;
    }


    private List<JSONObject> findTopLayerRows(List<JSONObject> rows) {
        List<JSONObject> topLayerRows = new ArrayList<>();
        for (JSONObject row : rows) {
            boolean foundParent = false;
            for (JSONObject row1 : rows) {
                if (Objects.equals(row1.get(keyColumnCamelCase), row.get(parentColumnCamelCase))) {
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

    public BaseMapper<T> getMapper() {
        return mapper;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public String getLabelColumn() {
        return labelColumn;
    }

    public String getParentColumn() {
        return parentColumn;
    }

    public String getKeyColumnCamelCase() {
        return keyColumnCamelCase;
    }

    public String getLabelColumnCamelCase() {
        return labelColumnCamelCase;
    }

    public String getParentColumnCamelCase() {
        return parentColumnCamelCase;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public boolean isQueryStatus() {
        return queryStatus;
    }
}

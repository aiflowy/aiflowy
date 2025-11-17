package tech.aiflowy.datacenter.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.aiflowy.common.entity.DatacenterQuery;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.datacenter.adapter.DbHandleManager;
import tech.aiflowy.datacenter.adapter.DbHandleService;
import tech.aiflowy.datacenter.entity.DatacenterTable;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;
import tech.aiflowy.datacenter.entity.vo.HeaderVo;
import tech.aiflowy.datacenter.mapper.DatacenterTableFieldsMapper;
import tech.aiflowy.datacenter.mapper.DatacenterTableMapper;
import tech.aiflowy.datacenter.service.DatacenterTableService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatacenterTableServiceImpl extends ServiceImpl<DatacenterTableMapper, DatacenterTable> implements DatacenterTableService {

    @Resource
    private DbHandleManager dbHandleManager;
    @Resource
    private DatacenterTableFieldsMapper fieldsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTable(DatacenterTable entity, LoginAccount loginUser) {

        DbHandleService dbHandler = dbHandleManager.getDbHandler();

        List<DatacenterTableFields> fields = entity.getFields();

        BigInteger tableId = entity.getId();

        if (tableId == null) {
            long snowId = new SnowFlakeIDKeyGenerator().nextId();
            entity.setId(new BigInteger(String.valueOf(snowId)));

            String actualTable = getActualTableName(entity);
            entity.setActualTable(actualTable);
            // 先 DDL 操作，DDL会默认提交事务，不然报错了事务不会回滚。
            dbHandler.createTable(entity);
            // 保存主表和字段表
            save(entity);
            for (DatacenterTableFields field : fields) {
                // 插入
                field.setCreated(new Date());
                field.setCreatedBy(loginUser.getId());
                field.setModified(new Date());
                field.setModifiedBy(loginUser.getId());
                field.setTableId(entity.getId());
                fieldsMapper.insert(field);
            }
        } else {
            // actualTable 前端不可见，所以要设置
            DatacenterTable tableRecord = getById(tableId);
            entity.setActualTable(tableRecord.getActualTable());
            dbHandler.updateTable(entity, tableRecord);
            updateById(entity);
            // 查询所有字段
            QueryWrapper w = QueryWrapper.create();
            w.eq(DatacenterTableFields::getTableId, entity.getId());
            List<DatacenterTableFields> fieldRecords = fieldsMapper.selectListByQuery(w);

            Map<BigInteger, DatacenterTableFields> fieldsMap = fieldRecords.stream()
                    .collect(Collectors.toMap(DatacenterTableFields::getId, field -> field));

            for (DatacenterTableFields field : fields) {
                BigInteger id = field.getId();
                if (id == null) {
                    // 新增字段到物理表
                    dbHandler.addField(entity, field);
                    // 插入
                    field.setCreated(new Date());
                    field.setCreatedBy(loginUser.getId());
                    field.setModified(new Date());
                    field.setModifiedBy(loginUser.getId());
                    field.setTableId(entity.getId());
                    fieldsMapper.insert(field);
                } else {
                    // 删除的字段
                    if (field.isHandleDelete()) {
                        // 删除物理表中的字段
                        dbHandler.deleteField(entity, field);
                        // 删除字段
                        fieldsMapper.deleteById(id);
                    } else {
                        // 修改物理表中的字段
                        DatacenterTableFields fieldRecord = fieldsMap.get(id);
                        dbHandler.updateField(entity, fieldRecord, field);
                        // 更新字段，字段类型不允许修改
                        field.setFieldType(field.getFieldType());
                        field.setModified(new Date());
                        field.setModifiedBy(loginUser.getId());
                        fieldsMapper.update(field);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTable(BigInteger tableId) {
        DatacenterTable record = getById(tableId);
        dbHandleManager.getDbHandler().deleteTable(record);
        removeById(tableId);
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq(DatacenterTableFields::getTableId, tableId);
        fieldsMapper.deleteByQuery(wrapper);
    }

    @Override
    public Long getCount(DatacenterQuery where) {
        String actualTable = getActualTable(where.getTableId());
        QueryWrapper wrapper = QueryWrapper.create();
        buildCondition(wrapper, where);
        return Db.selectCountByQuery(actualTable, wrapper);
    }

    @Override
    public List<Row> getListData(DatacenterQuery where) {
        String actualTable = getActualTable(where.getTableId());
        QueryWrapper wrapper = QueryWrapper.create();
        buildCondition(wrapper, where);
        List<Row> rows = Db.selectListByQuery(actualTable, wrapper);
        handleBigNumber(rows);
        return rows;
    }

    @Override
    public Page<Row> getPageData(DatacenterQuery where) {
        Long pageNumber = where.getPageNumber();
        Long pageSize = where.getPageSize();

        Long count = getCount(where);
        if (count == 0) {
            return new Page<>(new ArrayList<>(), pageNumber, pageSize, count);
        }

        String actualTable = getActualTable(where.getTableId());
        QueryWrapper wrapper = QueryWrapper.create();
        buildCondition(wrapper, where);

        Page<Row> page = new Page<>(pageNumber, pageSize, count);
        Page<Row> paginate = Db.paginate(actualTable, page, wrapper);
        handleBigNumber(paginate.getRecords());
        return paginate;
    }

    private void handleBigNumber(List<Row> records) {
        for (Row record : records) {
            Map<String, Object> newMap = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                Object value = entry.getValue();
                if ((value instanceof BigInteger ||
                        value instanceof BigDecimal ||
                        value instanceof Long)) {
                    newMap.put(entry.getKey(), value.toString());
                } else {
                    newMap.put(entry.getKey(), value);
                }
            }
            record.clear();
            record.putAll(newMap);
        }
    }

    @Override
    public List<HeaderVo> getHeaders(BigInteger tableId) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("table_id", tableId);
        wrapper.orderBy("id");
        List<DatacenterTableFields> fields = fieldsMapper.selectListByQuery(wrapper);
        List<HeaderVo> headers = new ArrayList<>();
        for (DatacenterTableFields field : fields) {
            HeaderVo header = new HeaderVo();
            header.setKey(field.getFieldName());
            header.setDataIndex(field.getFieldName());
            header.setTitle(field.getFieldDesc());
            header.setFieldType(field.getFieldType());
            header.setRequired(field.getRequired());
            header.setFieldId(field.getId());
            header.setTableId(field.getTableId());
            headers.add(header);
        }
        return headers;
    }

    @Override
    public void saveValue(BigInteger tableId, JSONObject object, LoginAccount account) {

        DatacenterTable table = getById(tableId);

        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("table_id", tableId);
        List<DatacenterTableFields> fields = fieldsMapper.selectListByQuery(wrapper);

        if (CollectionUtil.isEmpty(fields)) {
            throw new BusinessException("请先添加字段");
        }
        table.setFields(fields);
        Object valueId = object.get("id");
        if (valueId == null) {
            dbHandleManager.getDbHandler().saveValue(table, object, account);
        } else {
            dbHandleManager.getDbHandler().updateValue(table, object, account);
        }
    }

    @Override
    public void removeValue(BigInteger tableId, BigInteger id, LoginAccount account) {
        DatacenterTable record = getById(tableId);
        dbHandleManager.getDbHandler().removeValue(record, id, account);
    }

    @Override
    public List<DatacenterTableFields> getFields(BigInteger tableId) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("table_id", tableId);
        return fieldsMapper.selectListByQuery(wrapper);
    }

    private String getActualTable(BigInteger tableId) {
        DatacenterTable record = getById(tableId);
        return record.getActualTable();
    }

    private String getActualTableName(DatacenterTable table) {
        String tableName = table.getTableName();
        BigInteger id = table.getId();
        return "tb_dynamic_" + tableName + "_" + id;
    }

    /**
     * 构建查询条件
     */
    private void buildCondition(QueryWrapper wrapper, DatacenterQuery where) {
        // 构建查询条件
        String condition = where.getWhere();
        if (StrUtil.isNotEmpty(condition)) {
            wrapper.where(condition);
        }
    }
}

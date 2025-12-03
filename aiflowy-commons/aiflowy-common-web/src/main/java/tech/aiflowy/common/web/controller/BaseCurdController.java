package tech.aiflowy.common.web.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.SqlOperators;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.util.SqlOperatorsUtil;
import tech.aiflowy.common.util.SqlUtil;
import tech.aiflowy.common.web.exceptions.ProgramException;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.*;

public class BaseCurdController<S extends IService<M>, M> extends BaseController {


    protected final S service;

    public BaseCurdController(S service) {
        this.service = service;
    }

    /**
     * 添加（保存）数据
     *
     * @param entity 表生成内容配置
     * @return {@code Result.errorCode == 0} 添加成功，否则添加失败
     */
    @PostMapping("save")
    public Result<?> save(@JsonBody M entity) {
        Result<?> result = onSaveOrUpdateBefore(entity, true);
        if (result != null) return result;

        if (entity == null) {
            throw new NullPointerException("entity is null");
        }
        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
        commonFiled(entity, loginAccount.getId(), loginAccount.getTenantId(), loginAccount.getDeptId());
        service.save(entity);
        onSaveOrUpdateAfter(entity, true);
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Object[] pkArgs = tableInfo.buildPkSqlArgs(entity);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", pkArgs);
        return Result.ok(resultMap);
    }

    /**
     * 根据主键删除数据，id 需通过 json 传入，例如：
     * <pre>
     * {
     *   "id":123
     * }
     * <pre/>
     *
     *
     * @param id 主键
     * @return {@code Result.errorCode == 0} 删除成功，否则删除失败
     */
    @PostMapping("remove")
    @Transactional
    public Result<?> remove(@JsonBody(value = "id", required = true) Serializable id) {
        List<Serializable> ids = Collections.singletonList(id);
        Result<?> result = onRemoveBefore(ids);
        if (result != null) return result;
        boolean success = service.removeById(id);
        onRemoveAfter(ids);
        return Result.ok(success);
    }


    /**
     * 根据多个主键删数据内容，id 需通过 json 传入，例如：
     * <pre>
     * {
     *   "ids":[123, 234, 222]
     * }
     * <pre/>
     *
     * @param ids 主键
     * @return {@code Result.errorCode == 0} 删除成功，否则删除失败
     */
    @PostMapping("removeBatch")
    @Transactional
    public Result<?> removeBatch(@JsonBody(value = "ids", required = true) Collection<Serializable> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.fail("id不能为空");
        }
        Result<?> result = onRemoveBefore(ids);
        if (result != null) return result;
        boolean success = service.removeByIds(ids);
        onRemoveAfter(ids);
        return Result.ok(success);
    }

    /**
     * 根据主键更新内容
     *
     * @param entity 实体类数据
     * @return {@code Result.errorCode == 0} 更新成功，否则更新失败
     */
    @PostMapping("update")
    public Result<?> update(@JsonBody M entity) {
        Result<?> result = onSaveOrUpdateBefore(entity, false);
        if (result != null) return result;
        service.updateById(entity);
        onSaveOrUpdateAfter(entity, false);
        return Result.ok();
    }

    /**
     * 查询所有所有数据
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Result<?> list(M entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<M> list = Tree.tryToTree(service.list(queryWrapper), asTree);
        return Result.ok(list);
    }


    /**
     * 根据表主键查询数据详情。
     *
     * @param id 主键值
     * @return 内容详情
     */
    @GetMapping("detail")
    public Result<?> detail(String id) {
        if (tech.aiflowy.common.util.StringUtil.noText(id)) {
            return Result.fail(0, "id is null");
        }
        return Result.ok(service.getById(id));
    }


    /**
     * 分页查询数据列表
     *
     * @param request    查询数据
     * @param sortKey    排序字段
     * @param sortType   排序方式 asc | desc
     * @param pageNumber 当前页码
     * @param pageSize   每页的数据量
     * @return 查询的结果集
     */
    @GetMapping("page")
    public Result<?> page(HttpServletRequest request, String sortKey, String sortType, Long pageNumber, Long pageSize) {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10L;
        }

        QueryWrapper queryWrapper = buildQueryWrapper(request);
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        return Result.ok(queryPage(new Page<>(pageNumber, pageSize), queryWrapper));
    }


    protected QueryWrapper buildQueryWrapper(HttpServletRequest request) {

        QueryWrapper queryWrapper = new QueryWrapper();

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            return queryWrapper;
        }

        String[] isQueryOrs = parameterMap.get("isQueryOr");
        boolean isQueryOrBool = false;

        if (isQueryOrs != null && isQueryOrs.length > 0) {
            String isQueryOr = isQueryOrs[0];
            isQueryOrBool = "true".equals(isQueryOr);
        }

        Map<String, String> propertyColumnMapping = TableInfoFactory.ofEntityClass(getEntityClass())
                .getPropertyColumnMapping();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramKey = entry.getKey();
            if (StringUtil.hasText(paramKey) && !paramKey.endsWith(OperatorBuilder.operatorSuffix) && propertyColumnMapping.containsKey(paramKey)) {
                String columnName = propertyColumnMapping.get(paramKey);
                String[] values = entry.getValue();
                if (values != null && values.length > 0 && StringUtil.hasText(values[0])) {
                    String op = request.getParameter(paramKey + OperatorBuilder.operatorSuffix);
                    if (StringUtil.hasText(op)) {
                        OperatorBuilder.buildOperator(queryWrapper, columnName, op.trim(), values);
                    } else {
                        if (values.length == 2) {
                            queryWrapper.between(columnName, values[0], values[1]);
                        } else {
                            String value = values[0];
                            if (StringUtil.isNumeric(value)) {
                                queryWrapper.eq(columnName, value);
                            } else {
                                if (isQueryOrBool) {
                                    queryWrapper.or(columnName + " like " + "'%" + value + "%' ");
                                } else {
                                    queryWrapper.like(columnName, value);
                                }
                            }
                        }
                    }
                }
            }
        }

        return queryWrapper;
    }

    protected Class<?> getEntityClass() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return (Class<M>) ((ParameterizedType) type).getActualTypeArguments()[1];
        }

        return null;
    }


    /**
     * 方便子类复写，构建自己的 SqlOperators
     *
     * @param entity 实体类
     * @return SqlOperators
     */
    protected SqlOperators buildOperators(M entity) {
        return entity == null ? SqlOperators.empty() : SqlOperatorsUtil.build(entity.getClass());
    }

    protected String getDefaultOrderBy() {
        return "id desc";
    }

    /**
     * 方便子类复写，构建自己的 orderBy
     *
     * @param sortKey        排序字段
     * @param sortType       排序类型
     * @param defaultOrderBy 默认方式内容
     * @return orderBy 的内容，返回 null 或者 空字符串，表示不参与排序
     */
    protected String buildOrderBy(String sortKey, String sortType, String defaultOrderBy) {
        sortKey = StringUtil.camelToUnderline(sortKey);
        return SqlUtil.buildOrderBy(sortKey, sortType, defaultOrderBy);
    }

    protected Page<M> queryPage(Page<M> page, QueryWrapper queryWrapper) {
        return service.page(page, queryWrapper);
    }

    protected Result<?> onSaveOrUpdateBefore(M entity, boolean isSave) {
        return null;
    }

    protected void onSaveOrUpdateAfter(M entity, boolean isSave) {
        //void
    }

    protected Result<?> onRemoveBefore(Collection<Serializable> ids) {
        return null;
    }

    protected void onRemoveAfter(Collection<Serializable> ids) {
        //void
    }

    protected void commonFiled(Object t, BigInteger userId, BigInteger tenantId, BigInteger deptId) {
        Method[] methods = t.getClass().getMethods();
        try {
            for (Method m : methods) {
                String name = m.getName();
                if ("setDeptId".equals(name)) {
                    m.invoke(t, deptId);
                }
                if ("setTenantId".equals(name)) {
                    m.invoke(t, tenantId);
                }
                if ("setCreatedBy".equals(name)) {
                    m.invoke(t, userId);
                }
                if ("setModifiedBy".equals(name)) {
                    m.invoke(t, userId);
                }
                if ("setCreated".equals(name)) {
                    m.invoke(t, new Date());
                }
                if ("setModified".equals(name)) {
                    m.invoke(t, new Date());
                }
            }
        } catch (Exception e) {
            throw new ProgramException("commonFiled反射出错：" + e.getMessage());
        }
    }
}

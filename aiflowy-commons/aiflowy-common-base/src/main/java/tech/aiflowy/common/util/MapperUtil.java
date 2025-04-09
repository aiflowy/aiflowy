package tech.aiflowy.common.util;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.table.IdInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.FieldWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperUtil {


    /**
     * 同步 List 到数据库
     *
     * @param newModels         新的 Models
     * @param mapper            Mapper 查询
     * @param existQueryWrapper 查询旧的 Wrapper
     * @param getter            根据什么字段来对比进行同步
     * @param <T>               Entity 类
     */
    public static <T> void syncList(List<T> newModels, BaseMapper<T> mapper, QueryWrapper existQueryWrapper,
                                    Function<T, Object> getter) {
        syncList(newModels, mapper, existQueryWrapper, getter, null);
    }


    /**
     * 同步 List 到数据库
     *
     * @param newModels         新的 Models
     * @param mapper            Mapper 查询
     * @param existQueryWrapper 查询旧的 Wrapper
     * @param getter            根据什么字段来对比进行同步
     * @param onSyncBefore      在同步到数据库之前，可能需要做的前置操作
     * @param <T>               Entity 类
     */
    public static <T> void syncList(List<T> newModels, BaseMapper<T> mapper, QueryWrapper existQueryWrapper,
                                    Function<T, Object> getter,
                                    Consumer<T> onSyncBefore) {

        List<T> existModels = mapper.selectListByQuery(existQueryWrapper);

        List<T> needDeletes = new ArrayList<>();
        List<T> saveOrUpdates = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(newModels)) {
            if (CollectionUtil.isEmpty(existModels)) {
                saveOrUpdates.addAll(newModels);
            } else {
                for (T existModel : existModels) {
                    boolean removed = true;
                    for (T newModel : newModels) {
                        if (Objects.equals(getter.apply(existModel), getter.apply(newModel))) {
                            removed = false;
                            break;
                        }
                    }
                    if (removed) {
                        needDeletes.add(existModel);
                    }
                }

                TableInfo tableInfo = TableInfoFactory.ofEntityClass(newModels.get(0).getClass());
                List<IdInfo> primaryKeyList = tableInfo.getPrimaryKeyList();
                List<FieldWrapper> fieldWrappers = primaryKeyList.stream().map(idInfo -> FieldWrapper.of(tableInfo.getEntityClass(), idInfo.getProperty()))
                        .collect(Collectors.toList());


                for (T newModel : newModels) {
                    for (T existModel : existModels) {
                        if (Objects.equals(getter.apply(existModel), getter.apply(newModel))) {

                            //复制旧数据库的 ID 到新 model
                            for (FieldWrapper fieldWrapper : fieldWrappers) {
                                fieldWrapper.set(fieldWrapper.get(existModel), newModel);
                            }

                            break;
                        }
                    }
                    saveOrUpdates.add(newModel);
                }
            }
        } else if (CollectionUtil.isNotEmpty(existModels)) {
            needDeletes.addAll(existModels);
        }

        Db.tx(() -> {
            for (T needDelete : needDeletes) {
                mapper.delete(needDelete);
            }

            for (T saveOrUpdate : saveOrUpdates) {
                if (onSyncBefore != null) {
                    onSyncBefore.accept(saveOrUpdate);
                }
                mapper.insertOrUpdate(saveOrUpdate);
            }
            return true;
        });
    }
}

package tech.aiflowy.system.entity;

import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.core.dict.Dict;
import tech.aiflowy.core.dict.DictItem;
import tech.aiflowy.core.dict.DictLoader;
import tech.aiflowy.core.dict.DictType;
import tech.aiflowy.core.dict.loader.DatabaseDictLoader;
import tech.aiflowy.core.dict.loader.EnumDictLoader;
import tech.aiflowy.system.entity.base.SysDictBase;
import tech.aiflowy.system.service.SysDictItemService;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;


/**
 * 系统配置表 实体类。
 *
 * @author michael
 * @since 2024-03-04
 */

@Table(value = "tb_sys_dict",comment = "系统字典表")
public class SysDict extends SysDictBase {

    public DictLoader buildLoader() {
        Integer dictType = getDictType();
        if (dictType == null) {
            return null;
        }

        if (dictType == DictType.TABLE.getValue()) {
            String tableName = (String) this.getOptions().get("tableName");
            String keyColumn = (String) this.getOptions().get("keyColumn");
            String labelColumn = (String) this.getOptions().get("labelColumn");
            String parentColumn = (String) this.getOptions().get("parentColumn");
            return new DatabaseDictLoader(this.getCode(), tableName, keyColumn, labelColumn, parentColumn, null);
        } else if (dictType == DictType.ENUM.getValue()) {
            String enumClassString = (String) this.getOptions().get("enumClass");
            String keyField = (String) this.getOptions().get("keyField");
            String labelField = (String) this.getOptions().get("labelField");

            //noinspection rawtypes
            Class enumClass = null;
            try {
                enumClass = Class.forName(enumClassString);
            } catch (ClassNotFoundException e) {
                //ignore
                return null;
            }
            return new EnumDictLoader<>(this.getName(), this.getCode(), enumClass, keyField, labelField);
        } else if (dictType == DictType.CUSTOM.getValue()) {
            return new DictItemsLoader(this.getCode(), this.getId());
        }

        return null;
    }

    public static class DictItemsLoader implements DictLoader {
        private final String code;
        private final BigInteger dictId;
        private final SysDictItemService itemService;

        public DictItemsLoader(String code, BigInteger dictId) {
            this.code = code;
            this.dictId = dictId;
            this.itemService = SpringContextUtil.getBean(SysDictItemService.class);
        }

        @Override
        public String code() {
            return code;
        }

        @Override
        public Dict load(String keyword, Map<String, String[]> parameters) {
            QueryWrapper qw = QueryWrapper.create()
                    .eq(SysDictItem::getDictId, this.dictId)
                    .eq(SysDictItem::getStatus, 0)
                    .like(SysDictItem::getText, keyword);
            List<SysDictItem> sysDictItems = itemService.list(qw);

            Dict dict = new Dict();
            if (sysDictItems != null) {
                for (SysDictItem sysDictItem : sysDictItems) {
                    dict.addItem(new DictItem(sysDictItem.getValue(), sysDictItem.getText()));
                }
            }
            return dict;
        }
    }
}

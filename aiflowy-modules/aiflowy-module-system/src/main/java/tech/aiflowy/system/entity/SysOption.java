package tech.aiflowy.system.entity;

import tech.aiflowy.system.entity.base.SysOptionBase;
import com.mybatisflex.annotation.Table;

/**
 * 系统配置信息表。 实体类。
 *
 * @author michael
 * @since 2024-03-13
 */

@Table(value = "tb_sys_option", comment = "系统配置信息表")
public class SysOption extends SysOptionBase {

    public SysOption() {
    }

    public SysOption(String key, String value) {
        setKey(key);
        setValue(value);
    }

}

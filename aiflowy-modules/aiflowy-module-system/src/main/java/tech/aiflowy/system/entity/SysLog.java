package tech.aiflowy.system.entity;

import tech.aiflowy.system.entity.base.SysLogBase;
import com.mybatisflex.annotation.RelationManyToOne;
import com.mybatisflex.annotation.Table;

/**
 * 操作日志表 实体类。
 *
 * @author michael
 * @since 2024-03-04
 */

@Table(value = "tb_sys_log", comment = "操作日志表")
public class SysLog extends SysLogBase {

    @RelationManyToOne(selfField = "accountId")
    private SysAccount account;

    public SysAccount getAccount() {
        return account;
    }

    public void setAccount(SysAccount account) {
        this.account = account;
    }
}

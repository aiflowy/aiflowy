package tech.aiflowy.system.entity;

import cn.hutool.core.bean.BeanUtil;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.system.entity.base.SysAccountBase;
import com.alibaba.fastjson.annotation.JSONField;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;

import java.math.BigInteger;
import java.util.List;

/**
 * 用户表 实体类。
 *
 * @author ArkLight
 * @since 2025-03-14
 */

@Table(value = "tb_sys_account", comment = "用户表")
public class SysAccount extends SysAccountBase {

    @RelationManyToMany(joinTable = "tb_sys_account_role"
            , joinSelfColumn = "account_id"
            , joinTargetColumn = "role_id"
            , targetTable = "tb_sys_role"
            , targetField = "id"
            , valueField = "id"
    )
    private List<BigInteger> roleIds;

    @RelationManyToMany(joinTable = "tb_sys_account_position"
            , joinSelfColumn = "account_id"
            , joinTargetColumn = "position_id"
            , targetTable = "tb_sys_position"
            , targetField = "id"
            , valueField = "id"
    )
    private List<BigInteger> positionIds;

    public List<BigInteger> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<BigInteger> roleIds) {
        this.roleIds = roleIds;
    }

    public List<BigInteger> getPositionIds() {
        return positionIds;
    }

    public void setPositionIds(List<BigInteger> positionIds) {
        this.positionIds = positionIds;
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return super.getPassword();
    }

    /**
     * 转为登录用户
     */
    public LoginAccount toLoginAccount() {
        LoginAccount loginAccount = new LoginAccount();
        BeanUtil.copyProperties(this, loginAccount);
        return loginAccount;
    }
}

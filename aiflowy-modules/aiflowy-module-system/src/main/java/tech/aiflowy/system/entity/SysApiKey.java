package tech.aiflowy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.system.entity.base.SysApiKeyBase;

import java.math.BigInteger;
import java.util.List;


/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-04-18
 */
@Table("tb_sys_api_key")
public class SysApiKey extends SysApiKeyBase {

    public static final String KEY_Apikey = "Apikey";

    @Column(ignore = true)
    List<BigInteger> permissionIds;

    @RelationOneToMany(selfField = "id", targetField = "apiKeyId", targetTable = "tb_sys_api_key_resource_mapping")
    private List<SysApiKeyResourceMapping> resourcePermissions;

    public List<SysApiKeyResourceMapping> getResourcePermissions() {
        return resourcePermissions;
    }

    public void setResourcePermissions(List<SysApiKeyResourceMapping> resourcePermissions) {
        this.resourcePermissions = resourcePermissions;
    }

    public List<BigInteger> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<BigInteger> permissionIds) {
        this.permissionIds = permissionIds;
    }
}

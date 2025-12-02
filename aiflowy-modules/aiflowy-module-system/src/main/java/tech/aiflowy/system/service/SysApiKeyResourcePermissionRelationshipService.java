package tech.aiflowy.system.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.system.entity.SysApiKeyResourcePermissionRelationship;

/**
 * apikey-请求接口表 服务层。
 *
 * @author 12076
 * @since 2025-12-01
 */
public interface SysApiKeyResourcePermissionRelationshipService extends IService<SysApiKeyResourcePermissionRelationship> {

    void authInterface(SysApiKey entity);
}

package tech.aiflowy.system.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.system.entity.SysAccount;

/**
 * 用户表 服务层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
public interface SysAccountService extends IService<SysAccount> {

    void syncRelations(SysAccount entity);

    SysAccount getByUsername(String userKey);
}

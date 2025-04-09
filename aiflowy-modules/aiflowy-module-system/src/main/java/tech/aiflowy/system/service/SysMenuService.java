package tech.aiflowy.system.service;

import tech.aiflowy.system.entity.SysMenu;
import com.mybatisflex.core.service.IService;

import java.math.BigInteger;
import java.util.List;

/**
 * 菜单表 服务层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getMenusByAccountId(SysMenu entity, BigInteger accountId);
}

package tech.aiflowy.system.config;

import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.core.dict.DictManager;
import tech.aiflowy.core.dict.loader.DbDataLoader;
import tech.aiflowy.system.entity.SysDict;
import tech.aiflowy.system.mapper.*;
import tech.aiflowy.system.service.SysDictService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class SysDictAutoConfig {

    private SysDictService service;

    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysPositionMapper sysPositionMapper;
    @Resource
    private SysAccountMapper sysAccountMapper;

    public SysDictAutoConfig(SysDictService service) {
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartup() {

        DictManager dictManager = SpringContextUtil.getBean(DictManager.class);
        // 菜单表字典
        dictManager.putLoader(new DbDataLoader<>("sysMenu", sysMenuMapper, "id", "menu_title", "parent_id", "sort_no asc", false));
        // 部门表字典
        dictManager.putLoader(new DbDataLoader<>("sysDept", sysDeptMapper, "id", "dept_name", "parent_id", "sort_no asc", false));
        // 角色表字典
        dictManager.putLoader(new DbDataLoader<>("sysRole", sysRoleMapper, "id", "role_name", null, null, true));
        // 职位字典
        dictManager.putLoader(new DbDataLoader<>("sysPosition", sysPositionMapper, "id", "position_name", null, null, true));
        // 用户字典
        dictManager.putLoader(new DbDataLoader<>("sysAccount", sysAccountMapper, "id", "login_name", null, null, true));

        List<SysDict> sysDicts = service.list();
        if (sysDicts != null) {
            sysDicts.forEach(sysDict -> dictManager.putLoader(sysDict.buildLoader()));
        }
    }
}

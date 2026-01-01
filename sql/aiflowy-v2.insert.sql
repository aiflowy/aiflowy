-- ----------------------------
-- 岗位管理菜单及按钮 SQL
-- 生成时间: 2026-01-01
-- ----------------------------

-- 1. 插入岗位管理菜单 (父级: 系统管理)
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`) 
VALUES (183724390000000001, 258052082618335232, 0, 'menus.sysPosition.title', '/sys/sysPosition', '/system/sysPosition/SysPositionList', 'Setting', 1, 100, 1, NOW(), 0, NOW(), 0, '岗位管理菜单');

-- 2. 插入岗位管理按钮权限 (查询、保存、删除)
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_title`, `menu_url`, `permission_tag`, `menu_type`, `sort_no`, `is_show`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`) 
VALUES 
(183724390000000002, 183724390000000001, '查询', '', '/api/v1/sysPosition/query', 1, 100, 0, 1, NOW(), 0, NOW(), 0, '岗位管理-查询'),
(183724390000000003, 183724390000000001, '保存', '', '/api/v1/sysPosition/save', 1, 101, 0, 1, NOW(), 0, NOW(), 0, '岗位管理-保存'),
(183724390000000004, 183724390000000001, '删除', '', '/api/v1/sysPosition/remove', 1, 102, 0, 1, NOW(), 0, NOW(), 0, '岗位管理-删除');

-- 3. 为超级管理员 (role_id=1) 分配上述菜单权限
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) 
VALUES 
(183724390000000005, 1, 183724390000000001),
(183724390000000006, 1, 183724390000000002),
(183724390000000007, 1, 183724390000000003),
(183724390000000008, 1, 183724390000000004);

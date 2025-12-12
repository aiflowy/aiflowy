
-- ----------------------------
-- Records of tb_sys_account
-- ----------------------------
INSERT INTO `tb_sys_account` VALUES (1, 1, 1000000, 'admin', '$2a$10$mni2UdHMUwomVzvZtdECAOwYJevZ2z48ApO.JSVhyEaQ/AOKr4VP2', 99, '超级管理员', '15555555555', 'bbb@qq.com', '', 1, '2025-06-06 11:32:21', 1, '2025-12-12 15:11:54', 1, '', 0);

-- ----------------------------
-- Records of tb_sys_account_role
-- ----------------------------
INSERT INTO `tb_sys_account_role` VALUES (302654483522224128, 1, 1);

-- ----------------------------
-- Records of tb_sys_dept
-- ----------------------------
INSERT INTO `tb_sys_dept` VALUES (1, 1000000, 0, '0', '总公司', 'root_dept', 0, 1, '2025-03-17 09:09:57', 1, '2025-03-17 09:10:00', 1, '', 0);

-- ----------------------------
-- Records of tb_sys_menu
-- ----------------------------
INSERT INTO `tb_sys_menu` VALUES (258052082618335232, 0, 0, 'menus.system.title', '/sys', '', 'ant-design:appstore-outlined', 1, '', 200, 0, '2025-03-14 15:07:51', 1, '2025-12-02 13:39:37', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (258052774330368000, 258052082618335232, 0, 'menus.system.sysAccount', '/sys/sysAccount', '/system/sysAccount/SysAccountList', 'ant-design:user-outlined', 1, '', 0, 0, '2025-03-14 15:10:36', 1, '2025-12-12 16:31:45', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (258075705244676096, 258052082618335232, 0, 'menus.system.sysRole', '/sys/sysRole', '/system/sysRole/SysRoleList', 'ant-design:smile-outlined', 1, '', 11, 0, '2025-03-14 16:41:43', 1, '2025-11-19 14:32:45', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (258075850434703360, 258052082618335232, 0, 'menus.system.sysMenu', '/sys/sysMenu', '/system/sysMenu/SysMenuList', 'ant-design:control-outlined', 1, '', 21, 0, '2025-03-14 16:42:18', 1, '2025-11-19 14:32:56', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259048038847483904, 258052082618335232, 0, 'menus.system.sysDept', '/sys/sysDept', '/system/sysDept/SysDeptList', 'ant-design:unordered-list-outlined', 1, '', 31, 0, '2025-03-17 09:05:25', 1, '2025-11-19 14:33:09', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259168688849412096, 0, 0, 'menus.settings.title', '/config', '', 'ant-design:setting-twotone', 1, '', 300, 0, '2025-03-17 17:04:51', 1, '2025-12-02 15:23:20', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259168916721754112, 259168688849412096, 0, 'menus.settings.settingsConfig', '/sys/settings', '/config/settings/Settings', 'ant-design:control-outlined', 1, '', 12, 0, '2025-03-17 17:05:45', 1, '2025-12-12 16:32:17', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169318720626688, 258052082618335232, 0, 'menus.system.sysLog', '/sys/logs', '/system/sysLog/SysLogList', 'ant-design:file-search-outlined', 1, '', 61, 0, '2025-03-17 17:07:21', 1, '2025-11-19 14:34:40', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169540360232960, 0, 0, 'menus.ai.title', '/ai', '', 'ant-design:robot-outlined', 1, '', 100, 0, '2025-03-17 17:08:14', 1, '2025-12-10 14:46:07', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169837824466944, 259169540360232960, 0, 'menus.ai.bots', '/ai/bots', '/ai/bots/index', 'ant-design:android-outlined', 1, '', 11, 0, '2025-03-17 17:09:24', 1, '2025-12-12 16:57:50', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169982154661888, 259169540360232960, 0, 'menus.ai.plugin', '/ai/plugin', '/ai/plugin/Plugin', 'ant-design:api-outlined', 1, '', 21, 0, '2025-03-17 17:09:59', 1, '2025-12-02 15:20:24', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259170117110587392, 259169540360232960, 0, 'menus.ai.workflow', '/ai/workflow', '/ai/workflow/AiWorkflowList', 'ant-design:apartment-outlined', 1, '', 31, 0, '2025-03-17 17:10:31', 1, '2025-11-26 10:26:28', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259170422338478080, 259169540360232960, 0, 'menus.ai.knowledge', '/ai/knowledge', '/ai/knowledge/Knowledge', 'ant-design:book-outlined', 1, '', 51, 0, '2025-03-17 17:11:44', 1, '2025-12-02 15:20:43', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259170538264846336, 259169540360232960, 0, 'menus.ai.llm', '/ai/llms', '/ai/llm/Llm', 'ant-design:deployment-unit-outlined', 1, '', 61, 0, '2025-03-17 17:12:11', 1, '2025-12-02 15:20:54', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (270761213536096256, 259168688849412096, 0, 'menus.settings.apiKey', '/sys/sysApiKey', '/config/apikey/SysApiKey', 'ant-design:key-outlined', 1, '', 22, 0, '2025-04-18 16:49:24', 1, '2025-12-04 14:32:23', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (282254669269082112, 258052082618335232, 0, 'menus.system.sysJob', '/sys/sysJob', '/system/sysJob/SysJobList', 'ant-design:field-time-outlined', 1, '', 53, 0, '2025-05-20 10:00:17', 1, '2025-11-19 14:34:25', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (298243919118950400, 258052774330368000, 1, '查询', '', '', '', 0, '/api/v1/sysAccount/query', 1, 0, '2025-07-03 12:55:51', 1, '2025-07-03 12:55:51', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243919727124480, 258052774330368000, 1, '保存', '', '', '', 0, '/api/v1/sysAccount/save', 1, 0, '2025-07-03 12:55:51', 1, '2025-07-03 12:55:51', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243920205275136, 258052774330368000, 1, '删除', '', '', '', 0, '/api/v1/sysAccount/remove', 1, 0, '2025-07-03 12:55:51', 1, '2025-07-03 12:55:51', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243920679231488, 258075705244676096, 1, '查询', '', '', '', 0, '/api/v1/sysRole/query', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243921161576448, 258075705244676096, 1, '保存', '', '', '', 0, '/api/v1/sysRole/save', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243921639727104, 258075705244676096, 1, '删除', '', '', '', 0, '/api/v1/sysRole/remove', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243922155626496, 258075850434703360, 1, '查询', '', '', '', 0, '/api/v1/sysMenu/query', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243922637971456, 258075850434703360, 1, '保存', '', '', '', 0, '/api/v1/sysMenu/save', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243923116122112, 258075850434703360, 1, '删除', '', '', '', 0, '/api/v1/sysMenu/remove', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243923627827200, 259048038847483904, 1, '查询', '', '', '', 0, '/api/v1/sysDept/query', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243924105977856, 259048038847483904, 1, '保存', '', '', '', 0, '/api/v1/sysDept/save', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243924571545600, 259048038847483904, 1, '删除', '', '', '', 0, '/api/v1/sysDept/remove', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243926450593792, 259168916721754112, 1, '查询', '', '', '', 0, '/api/v1/sysOption/query', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243926920355840, 259168916721754112, 1, '保存', '', '', '', 0, '/api/v1/sysOption/save', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243927385923584, 259168916721754112, 1, '删除', '', '', '', 0, '/api/v1/sysOption/remove', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243929294331904, 259169318720626688, 1, '查询', '', '', '', 0, '/api/v1/sysLog/query', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243929755705344, 259169318720626688, 1, '保存', '', '', '', 0, '/api/v1/sysLog/save', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243930225467392, 259169318720626688, 1, '删除', '', '', '', 0, '/api/v1/sysLog/remove', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243932104515584, 259169837824466944, 1, '查询', '', '', '', 0, '/api/v1/aiBot/query', 1, 0, '2025-07-03 12:55:55', 1, '2025-11-20 16:10:04', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243932607832064, 259169837824466944, 1, '保存', '', '', '', 0, '/api/v1/aiBot/save', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243933077594112, 259169837824466944, 1, '删除', '', '', '', 0, '/api/v1/aiBot/remove', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243933547356160, 259169982154661888, 1, '查询', '', '', '', 0, '/api/v1/aiPlugin/query', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243934025506816, 259169982154661888, 1, '保存', '', '', '', 0, '/api/v1/aiPlugin/save', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243934495268864, 259169982154661888, 1, '删除', '', '', '', 0, '/api/v1/aiPlugin/remove', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243934960836608, 259170117110587392, 1, '查询', '', '', '', 0, '/api/v1/aiWorkflow/query', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243935430598656, 259170117110587392, 1, '保存', '', '', '', 0, '/api/v1/aiWorkflow/save', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243935904555008, 259170117110587392, 1, '删除', '', '', '', 0, '/api/v1/aiWorkflow/remove', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243936374317056, 259170422338478080, 1, '查询', '', '', '', 0, '/api/v1/aiKnowledge/query', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243936848273408, 259170422338478080, 1, '保存', '', '', '', 0, '/api/v1/aiKnowledge/save', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243937313841152, 259170422338478080, 1, '删除', '', '', '', 0, '/api/v1/aiKnowledge/remove', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243937783603200, 259170538264846336, 1, '查询', '', '', '', 0, '/api/v1/aiLlm/query', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243938240782336, 259170538264846336, 1, '保存', '', '', '', 0, '/api/v1/aiLlm/save', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243938706350080, 259170538264846336, 1, '删除', '', '', '', 0, '/api/v1/aiLlm/remove', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243939180306432, 270761213536096256, 1, '查询', '', '', '', 0, '/api/v1/sysApiKey/query', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243939650068480, 270761213536096256, 1, '保存', '', '', '', 0, '/api/v1/sysApiKey/save', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243940115636224, 270761213536096256, 1, '删除', '', '', '', 0, '/api/v1/sysApiKey/remove', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243940614758400, 282254669269082112, 1, '查询', '', '', '', 0, '/api/v1/sysJob/query', 1, 0, '2025-07-03 12:55:57', 1, '2025-07-03 12:55:57', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243941080326144, 282254669269082112, 1, '保存', '', '', '', 0, '/api/v1/sysJob/save', 1, 0, '2025-07-03 12:55:57', 1, '2025-07-03 12:55:57', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (298243941545893888, 282254669269082112, 1, '删除', '', '', '', 0, '/api/v1/sysJob/remove', 1, 0, '2025-07-03 12:55:57', 1, '2025-07-03 12:55:57', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` VALUES (300008008381800448, 259169540360232960, 0, 'menus.ai.resources', '/ai/aiResource', '/ai/resource/AiResourceList', 'ant-design:folder-open-outlined', 1, '', 52, 0, '2025-07-08 09:45:43', 1, '2025-11-24 11:42:25', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (300008359986110464, 300008008381800448, 1, '查询', '', '', '', 0, '/api/v1/aiResource/query', 0, 0, '2025-07-08 09:47:07', 1, '2025-07-08 09:47:07', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (300012644643815424, 300008008381800448, 1, '保存', '', '', '', 0, '/api/v1/aiResource/save', 0, 0, '2025-07-08 10:04:08', 1, '2025-07-08 10:04:08', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (300013092268326912, 300008008381800448, 1, '删除', '', '', '', 1, '/api/v1/aiResource/remove', 0, 0, '2025-07-08 10:05:55', 1, '2025-07-08 10:05:55', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (300817858217091072, 259169540360232960, 0, 'menus.ai.datacenter', '/datacenter/table', '/datacenter/DatacenterTableList', 'ant-design:database-outlined', 1, '', 53, 0, '2025-07-10 15:23:46', 1, '2025-11-25 09:19:00', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (300818298270883840, 300817858217091072, 1, '查询', '', '', '', 1, '/api/v1/datacenterTable/query', 0, 0, '2025-07-10 15:25:31', 1, '2025-07-10 15:25:31', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (300818387710222336, 300817858217091072, 1, '保存', '', '', '', 1, '/api/v1/datacenterTable/save', 0, 0, '2025-07-10 15:25:53', 1, '2025-07-10 15:25:53', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (300818488214134784, 300817858217091072, 1, '删除', '', '', '', 1, '/api/v1/datacenterTable/remove', 0, 0, '2025-07-10 15:26:17', 1, '2025-07-10 15:26:17', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (356254301289807872, 0, 0, 'Bots', '/bots', '/ai/bots/index', 'ant-design:account-book-filled', 1, '', 0, 0, '2025-12-10 14:48:24', 1, '2025-12-10 14:48:24', 1, '', 1);

-- ----------------------------
-- Records of tb_sys_role
-- ----------------------------
INSERT INTO `tb_sys_role` VALUES (1, 1000000, '超级管理员', 'super_admin', 1, '2025-03-14 14:52:37', 1, '2025-03-14 14:52:37', 1, '', 0, 1, 0, 0);

-- ----------------------------
-- Records of tb_sys_role_menu
-- ----------------------------
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250817, 1, 258052774330368000);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250818, 1, 258075705244676096);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250819, 1, 258075850434703360);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250822, 1, 259048038847483904);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250825, 1, 258052082618335232);
INSERT INTO `tb_sys_role_menu` VALUES (259168688966852608, 1, 259168688849412096);
INSERT INTO `tb_sys_role_menu` VALUES (259168916826611712, 1, 259168916721754112);
INSERT INTO `tb_sys_role_menu` VALUES (259169318829678592, 1, 259169318720626688);
INSERT INTO `tb_sys_role_menu` VALUES (259169540477673472, 1, 259169540360232960);
INSERT INTO `tb_sys_role_menu` VALUES (259169837941907456, 1, 259169837824466944);
INSERT INTO `tb_sys_role_menu` VALUES (259169982280491008, 1, 259169982154661888);
INSERT INTO `tb_sys_role_menu` VALUES (259170117223833600, 1, 259170117110587392);
INSERT INTO `tb_sys_role_menu` VALUES (259170422447529984, 1, 259170422338478080);
INSERT INTO `tb_sys_role_menu` VALUES (259170538378092544, 1, 259170538264846336);
INSERT INTO `tb_sys_role_menu` VALUES (270761213603205120, 1, 270761213536096256);
INSERT INTO `tb_sys_role_menu` VALUES (282254669390716928, 1, 282254669269082112);
INSERT INTO `tb_sys_role_menu` VALUES (298243919488049152, 1, 298243919118950400);
INSERT INTO `tb_sys_role_menu` VALUES (298243919966199808, 1, 298243919727124480);
INSERT INTO `tb_sys_role_menu` VALUES (298243920444350464, 1, 298243920205275136);
INSERT INTO `tb_sys_role_menu` VALUES (298243920926695424, 1, 298243920679231488);
INSERT INTO `tb_sys_role_menu` VALUES (298243921404846080, 1, 298243921161576448);
INSERT INTO `tb_sys_role_menu` VALUES (298243921908162560, 1, 298243921639727104);
INSERT INTO `tb_sys_role_menu` VALUES (298243922398896128, 1, 298243922155626496);
INSERT INTO `tb_sys_role_menu` VALUES (298243922877046784, 1, 298243922637971456);
INSERT INTO `tb_sys_role_menu` VALUES (298243923367780352, 1, 298243923116122112);
INSERT INTO `tb_sys_role_menu` VALUES (298243923866902528, 1, 298243923627827200);
INSERT INTO `tb_sys_role_menu` VALUES (298243924336664576, 1, 298243924105977856);
INSERT INTO `tb_sys_role_menu` VALUES (298243924810620928, 1, 298243924571545600);
INSERT INTO `tb_sys_role_menu` VALUES (298243926689669120, 1, 298243926450593792);
INSERT INTO `tb_sys_role_menu` VALUES (298243927155236864, 1, 298243926920355840);
INSERT INTO `tb_sys_role_menu` VALUES (298243927620804608, 1, 298243927385923584);
INSERT INTO `tb_sys_role_menu` VALUES (298243929525018624, 1, 298243929294331904);
INSERT INTO `tb_sys_role_menu` VALUES (298243929990586368, 1, 298243929755705344);
INSERT INTO `tb_sys_role_menu` VALUES (298243930460348416, 1, 298243930225467392);
INSERT INTO `tb_sys_role_menu` VALUES (298243932368756736, 1, 298243932104515584);
INSERT INTO `tb_sys_role_menu` VALUES (298243932846907392, 1, 298243932607832064);
INSERT INTO `tb_sys_role_menu` VALUES (298243933312475136, 1, 298243933077594112);
INSERT INTO `tb_sys_role_menu` VALUES (298243933786431488, 1, 298243933547356160);
INSERT INTO `tb_sys_role_menu` VALUES (298243934260387840, 1, 298243934025506816);
INSERT INTO `tb_sys_role_menu` VALUES (298243934730149888, 1, 298243934495268864);
INSERT INTO `tb_sys_role_menu` VALUES (298243935199911936, 1, 298243934960836608);
INSERT INTO `tb_sys_role_menu` VALUES (298243935669673984, 1, 298243935430598656);
INSERT INTO `tb_sys_role_menu` VALUES (298243936139436032, 1, 298243935904555008);
INSERT INTO `tb_sys_role_menu` VALUES (298243936621780992, 1, 298243936374317056);
INSERT INTO `tb_sys_role_menu` VALUES (298243937083154432, 1, 298243936848273408);
INSERT INTO `tb_sys_role_menu` VALUES (298243937552916480, 1, 298243937313841152);
INSERT INTO `tb_sys_role_menu` VALUES (298243938010095616, 1, 298243937783603200);
INSERT INTO `tb_sys_role_menu` VALUES (298243938475663360, 1, 298243938240782336);
INSERT INTO `tb_sys_role_menu` VALUES (298243938949619712, 1, 298243938706350080);
INSERT INTO `tb_sys_role_menu` VALUES (298243939415187456, 1, 298243939180306432);
INSERT INTO `tb_sys_role_menu` VALUES (298243939884949504, 1, 298243939650068480);
INSERT INTO `tb_sys_role_menu` VALUES (298243940358905856, 1, 298243940115636224);
INSERT INTO `tb_sys_role_menu` VALUES (298243940845445120, 1, 298243940614758400);
INSERT INTO `tb_sys_role_menu` VALUES (298243941315207168, 1, 298243941080326144);
INSERT INTO `tb_sys_role_menu` VALUES (298243941780774912, 1, 298243941545893888);
INSERT INTO `tb_sys_role_menu` VALUES (300008008490852352, 1, 300008008381800448);
INSERT INTO `tb_sys_role_menu` VALUES (300008360078385152, 1, 300008359986110464);
INSERT INTO `tb_sys_role_menu` VALUES (300012644702535680, 1, 300012644643815424);
INSERT INTO `tb_sys_role_menu` VALUES (300013092310269952, 1, 300013092268326912);
INSERT INTO `tb_sys_role_menu` VALUES (300817858284199936, 1, 300817858217091072);
INSERT INTO `tb_sys_role_menu` VALUES (300818298325409792, 1, 300818298270883840);
INSERT INTO `tb_sys_role_menu` VALUES (300818387789914112, 1, 300818387710222336);
INSERT INTO `tb_sys_role_menu` VALUES (300818488344158208, 1, 300818488214134784);

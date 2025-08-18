SET NAMES utf8mb4;

INSERT INTO `tb_ai_bot` VALUES (274724831961026560,'bot_274724831961026560', 1, 1000000, '天气查询', '天气查询', NULL, 273418062806761472, '{"systemPrompt":"你是天气查询助手"}', NULL, '2025-04-29 15:19:24', 1, '2025-04-29 15:19:24', 1,1);


INSERT INTO `tb_ai_bot_knowledge` VALUES (6, 267746165268017152, 267751447398232064, NULL);
INSERT INTO `tb_ai_bot_knowledge` VALUES (10, 269226938047168512, 273274084509630464, NULL);
INSERT INTO `tb_ai_bot_knowledge` VALUES (11, 267778325987205120, 273416394929192960, NULL);






INSERT INTO `tb_ai_bot_plugins` VALUES (267770208130498560, 267769906283216896, 267769494146711552, NULL);
INSERT INTO `tb_ai_bot_plugins` VALUES (274649868264542208, 269226938047168512, 267769494146711552, NULL);
INSERT INTO `tb_ai_bot_plugins` VALUES (274727326384918528, 274724831961026560, 273726420990324736, NULL);
INSERT INTO `tb_ai_bot_plugins` VALUES (274728813039194112, 267848016181075968, 267769494146711552, NULL);



INSERT INTO `tb_ai_chat_topic` VALUES (269265123934486528, 1, '未命名', '2025-04-14 13:44:28', NULL);


INSERT INTO `tb_ai_llm` VALUES (273418062806761472, 1, 1000000, '星火大模型', 'spark', NULL, NULL, 1, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'wss://spark-api.xf-yun.com/v3.5/chat', 'wss://spark-api.xf-yun.com/v3.5/chat', '', '', NULL);


INSERT INTO `tb_ai_plugin` VALUES (273726420990324736,'plugin_273726420990324736', '和风天气', '每日天气预报API，提供全球城市未来3-30天天气预报，包括：日出日落、月升月落、最高最低温度、天气白天和夜间状况、风力、风速、风向、相对湿度、大气压强、降水量、露点温度、紫外线强度、能见度等。', NULL, 'https://jw54e3j6ha.re.qweatherapi.com', 'apiKey', '2025-04-26 21:12:04', '/attachment/2025/04-26/cfa688d9-9bc5-4d3f-a50c-2501604e8bed.jpg', 'query', '[{\"label\":\"X-QW-Api-Key\",\"value\":\"2ad5de1c5dddd44e589bece44fdad4258\"}]', 'q', '', NULL, NULL, NULL);


INSERT INTO `tb_ai_plugin_tool` VALUES (273931608955039744, 273726420990324736, 'get_weather', '查询某个城市地方未来三天的天气', '/v7/weather/3d', '2025-04-27 10:47:25', NULL, '[{\"name\":\"location\",\"description\":\"城市 LocationID（如 101010100 代表北京），可通过城市列表查询，当用户问及例如北京的天气的时候，大模型需要查询北京转换为对应的城市id\",\"type\":\"String\",\"method\":\"Query\",\"required\":true,\"defaultValue\":\"101010100\",\"enabled\":true,\"key\":\"1745905802390\"}]', NULL, 'Get', 0, 0);
INSERT INTO `tb_ai_plugin_tool` VALUES (275035209345548290, 273726420990324736, 'get_cityId', '城市搜索API提供全球地理位位置、全球城市搜索服务，支持经纬度坐标反查、多语言、模糊搜索等功能。', '/geo/v2/city/lookup', '2025-04-30 11:52:44', NULL, '[{\"name\":\"location\",\"description\":\"需要查询地区的名称，支持文字、以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）、LocationID或Adcode（仅限中国城市）。例如 location=北京 或 location=116.41,39.92\",\"type\":\"String\",\"method\":\"Query\",\"required\":true,\"defaultValue\":\"\",\"enabled\":true,\"key\":\"1745985221077\"}]', NULL, 'Get', 0, 0);



INSERT INTO `tb_ai_plugins` VALUES (267769494146711552, 1, 1000000, 1, 'get_ip_info', '根据ip获取区域信息', '{\"method\":\"GET\",\"url\":\"https://qifu-api.baidubce.com/ip/geo/v1/district\",\"headers\":[],\"params\":[{\"key\":\"ip\",\"value\":\"\",\"desc\":\"IP地址\"}],\"body\":{}}', 1, '2025-04-10 10:41:22', 1, '2025-04-29 10:21:07', 1, '', 0, NULL);
INSERT INTO `tb_ai_plugins` VALUES (269229432299077632, 1, 1000000, 1, 'test', 'test', NULL, 1, '2025-04-14 11:22:39', 1, '2025-04-14 11:22:39', 1, '', 0, '/attachment/2025/04-14/e2f3e5a5-a4fa-4abe-8fe6-49d10c3d8dbe.jpeg');




INSERT INTO `tb_sys_account` VALUES (1, 1, 1000000, 'admin', '$2a$10$S8HVnrS8m7iygQBS7r1dYuOstEUl5q/W1yhgFcS1uyL6o2/23yUYO', 99, '超级管理员', '15555555555', 'aaa@qq.com', '/attachment/2025/04-10/59866709-5bc5-4e9f-9445-ecb603ff2d82.jpg', 1, NULL, 1, '2025-04-10 16:33:48', 1, '2025-04-10 17:56:17', 1, '', 0);


INSERT INTO `tb_sys_account_position` VALUES (267858187553452032, 1, 259067270360543232);


INSERT INTO `tb_sys_account_role` VALUES (267858187456983040, 1, 1);



INSERT INTO `tb_sys_api_key` VALUES (275081657634275328, '8e8ca1ff71d1430d9ad2a074f5901ed8', '2025-04-30 14:57:18', 1, 1, 1000000, '2025-05-30 00:00:00', 1);



INSERT INTO `tb_sys_dept` VALUES (1, 1000000, 0, '0', '总公司', 'root_dept', 0, 1, '2025-03-17 09:09:57', 1, '2025-03-17 09:10:00', 1, '', 0);



INSERT INTO `tb_sys_dict` VALUES (268213400717598720, 'test', 'test', 'test', 1, NULL, NULL, '{}', '2025-04-11 16:05:18', '2025-04-11 16:05:18');





INSERT INTO `tb_sys_menu` VALUES (258052082618335232, 0, 0, '系统管理', '', '', 'ControlOutlined', 1, '', 999, 0, '2025-03-14 15:07:51', 1, '2025-04-08 11:12:00', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (258052774330368000, 258052082618335232, 0, '用户管理', '/sys/sysAccount', '', 'CustomUserIcon', 1, '', 0, 0, '2025-03-14 15:10:36', 1, '2025-03-14 15:10:36', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (258075705244676096, 258052082618335232, 0, '角色管理', '/sys/sysRole', '', 'CustomRoleIcon', 1, '', 11, 0, '2025-03-14 16:41:43', 1, '2025-03-14 16:41:43', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (258075850434703360, 258052082618335232, 0, '菜单管理', '/sys/sysMenu', '', 'CustomMenuIcon', 1, '', 21, 0, '2025-03-14 16:42:18', 1, '2025-03-14 16:42:18', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (258077347000119296, 258052774330368000, 1, '用户查询', '', '', NULL, 0, 'sysUser:list', 1, 0, '2025-03-14 16:48:14', 1, '2025-03-14 16:48:14', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (258079445137780736, 258052774330368000, 1, '用户保存', '', '', NULL, 1, 'sysUser:save', 2, 0, '2025-03-14 16:56:35', 1, '2025-03-14 16:56:35', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259048038847483904, 258052082618335232, 0, '部门管理', '/sys/sysDept', '', 'CustomDepartIcon', 1, '', 31, 0, '2025-03-17 09:05:25', 1, '2025-03-17 09:05:25', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259065916854448128, 258052082618335232, 0, '岗位管理', '/sys/sysPosition', '', 'CustomPostIcon', 1, '', 32, 0, '2025-03-17 10:16:28', 1, '2025-03-17 10:16:28', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259166589327630336, 0, 0, '欢迎回来', '/index', '', 'BorderlessTableOutlined', 1, '', 0, 0, '2025-03-17 16:56:30', 1, '2025-03-17 16:56:30', 1, '', 1);
INSERT INTO `tb_sys_menu` VALUES (259168688849412096, 0, 0, '系统配置', '', '', NULL, 1, '', 99999, 0, '2025-03-17 17:04:51', 1, '2025-03-17 17:04:51', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259168810450673664, 259168688849412096, 0, 'AI大模型', '/config/model', '', 'DeploymentUnitOutlined', 1, '', 0, 0, '2025-03-17 17:05:20', 1, '2025-03-17 17:05:20', 1, '', 1);
INSERT INTO `tb_sys_menu` VALUES (259168916721754112, 259168688849412096, 0, '系统设置', 'sys/settings', '', 'CustomSettingsIcon', 1, '', 12, 0, '2025-03-17 17:05:45', 1, '2025-04-17 15:54:39', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169177682960384, 258052082618335232, 0, '数据字典', '/sys/dicts', '', 'CustomDictionaryIcon', 1, '', 51, 0, '2025-03-17 17:06:47', 1, '2025-04-14 13:35:41', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169318720626688, 258052082618335232, 0, '日志管理', '/sys/logs', '', 'CustomLogsIcon', 1, '', 61, 0, '2025-03-17 17:07:21', 1, '2025-04-14 13:35:34', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169540360232960, 0, 0, 'AI能力', '', '', NULL, 1, '', 21, 0, '2025-03-17 17:08:14', 1, '2025-03-17 17:08:14', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169689438380032, 259169540360232960, 0, '本地模型', '/ai/ollama', '', 'CustomOllamaStoreIcon', 1, '', 1111, 0, '2025-03-17 17:08:49', 1, '2025-04-08 12:24:03', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169837824466944, 259169540360232960, 0, 'Bots', '/ai/bots', '', 'CustomBotsIcon', 1, '', 11, 0, '2025-03-17 17:09:24', 1, '2025-03-17 17:09:24', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259169982154661888, 259169540360232960, 0, '插件', '/ai/plugin', '', 'CustomPluginIcon', 1, '', 21, 0, '2025-03-17 17:09:59', 1, '2025-04-25 11:23:15', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259170117110587392, 259169540360232960, 0, '工作流', '/ai/workflow', '', 'CustomWorkFlowIcon', 1, '', 31, 0, '2025-03-17 17:10:31', 1, '2025-03-17 17:10:31', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259170422338478080, 259169540360232960, 0, '知识库', '/ai/knowledge', '', 'CustomKnowledgeIcon', 1, '', 51, 0, '2025-03-17 17:11:44', 1, '2025-03-17 17:11:44', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (259170538264846336, 259169540360232960, 0, '模型管理', '/ai/llms', '', 'CustomLlmIcon', 1, '', 61, 0, '2025-03-17 17:12:11', 1, '2025-04-14 10:49:05', 1, '', 0);
INSERT INTO `tb_sys_menu` VALUES (269220820365377536, 259170422338478080, 0, '知识库', '/ai/knowledge/document', '', 'CustomKnowledgeIcon', 1, '', 1, 0, '2025-04-14 10:48:25', 1, '2025-04-14 10:50:19', 1, '', 1);
INSERT INTO `tb_sys_menu` VALUES (269221948243083264, 259170422338478080, 1, '知识库', '/ai/knowledge/Document', '', 'BookFilled', 1, '', 1, 0, '2025-04-14 10:52:54', 1, '2025-04-14 10:52:54', 1, '', 1);
INSERT INTO `tb_sys_menu` VALUES (270761213536096256, 259168688849412096, 0, '访问令牌', '/sys/sysApiKey', '', 'CustomEyeIcon', 1, '', 22, 0, '2025-04-18 16:49:24', 1, '2025-05-28 09:22:33', 1, '', 0);


-- v1.0.3 data start --
INSERT INTO `tb_sys_menu` VALUES (282254669269082112, 258052082618335232, 0, '定时任务', '/sys/sysJob', '', 'CustomTasksIcon', 1, '', 53, 0, '2025-05-20 10:00:17', 1, '2025-05-20 10:00:17', 1, '', 0);
INSERT INTO `tb_sys_role_menu` VALUES (282254669390716928, 1, 282254669269082112);
-- v1.0.3 data end --



INSERT INTO `tb_sys_position` VALUES (259067270360543232, 1000000, 1, '总部CTO', '', 0, 1, '2025-03-17 10:21:50', 1, '2025-03-17 10:21:50', 1, '', 0);



INSERT INTO `tb_sys_role` VALUES (1, 1000000, '超级管理员', 'super_admin', 1, '2025-03-14 14:52:37', 1, '2025-03-14 14:52:37', 1, '', 0);



INSERT INTO `tb_sys_role_menu` VALUES (259111372649250817, 1, 258052774330368000);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250818, 1, 258075705244676096);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250819, 1, 258075850434703360);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250820, 1, 258077347000119296);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250821, 1, 258079445137780736);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250822, 1, 259048038847483904);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250824, 1, 259065916854448128);
INSERT INTO `tb_sys_role_menu` VALUES (259111372649250825, 1, 258052082618335232);
INSERT INTO `tb_sys_role_menu` VALUES (259168688966852608, 1, 259168688849412096);
INSERT INTO `tb_sys_role_menu` VALUES (259168916826611712, 1, 259168916721754112);
INSERT INTO `tb_sys_role_menu` VALUES (259169177817178112, 1, 259169177682960384);
INSERT INTO `tb_sys_role_menu` VALUES (259169318829678592, 1, 259169318720626688);
INSERT INTO `tb_sys_role_menu` VALUES (259169540477673472, 1, 259169540360232960);
INSERT INTO `tb_sys_role_menu` VALUES (259169689576792064, 1, 259169689438380032);
INSERT INTO `tb_sys_role_menu` VALUES (259169837941907456, 1, 259169837824466944);
INSERT INTO `tb_sys_role_menu` VALUES (259169982280491008, 1, 259169982154661888);
INSERT INTO `tb_sys_role_menu` VALUES (259170117223833600, 1, 259170117110587392);
INSERT INTO `tb_sys_role_menu` VALUES (259170422447529984, 1, 259170422338478080);
INSERT INTO `tb_sys_role_menu` VALUES (259170538378092544, 1, 259170538264846336);
INSERT INTO `tb_sys_role_menu` VALUES (270761213603205120, 1, 270761213536096256);

-- v1.1.0 data start --
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243919118950400, 258052774330368000, 1, '查询', '', '', '', 0, '/api/v1/sysAccount/query', 1, 0, '2025-07-03 12:55:51', 1, '2025-07-03 12:55:51', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243919727124480, 258052774330368000, 1, '保存', '', '', '', 0, '/api/v1/sysAccount/save', 1, 0, '2025-07-03 12:55:51', 1, '2025-07-03 12:55:51', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243920205275136, 258052774330368000, 1, '删除', '', '', '', 0, '/api/v1/sysAccount/remove', 1, 0, '2025-07-03 12:55:51', 1, '2025-07-03 12:55:51', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243920679231488, 258075705244676096, 1, '查询', '', '', '', 0, '/api/v1/sysRole/query', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243921161576448, 258075705244676096, 1, '保存', '', '', '', 0, '/api/v1/sysRole/save', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243921639727104, 258075705244676096, 1, '删除', '', '', '', 0, '/api/v1/sysRole/remove', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243922155626496, 258075850434703360, 1, '查询', '', '', '', 0, '/api/v1/sysMenu/query', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243922637971456, 258075850434703360, 1, '保存', '', '', '', 0, '/api/v1/sysMenu/save', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243923116122112, 258075850434703360, 1, '删除', '', '', '', 0, '/api/v1/sysMenu/remove', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243923627827200, 259048038847483904, 1, '查询', '', '', '', 0, '/api/v1/sysDept/query', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243924105977856, 259048038847483904, 1, '保存', '', '', '', 0, '/api/v1/sysDept/save', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243924571545600, 259048038847483904, 1, '删除', '', '', '', 0, '/api/v1/sysDept/remove', 1, 0, '2025-07-03 12:55:52', 1, '2025-07-03 12:55:52', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243925045501952, 259065916854448128, 1, '查询', '', '', '', 0, '/api/v1/sysPosition/query', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243925515264000, 259065916854448128, 1, '保存', '', '', '', 0, '/api/v1/sysPosition/save', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243925980831744, 259065916854448128, 1, '删除', '', '', '', 0, '/api/v1/sysPosition/remove', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243926450593792, 259168916721754112, 1, '查询', '', '', '', 0, '/api/v1/sysOption/query', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243926920355840, 259168916721754112, 1, '保存', '', '', '', 0, '/api/v1/sysOption/save', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243927385923584, 259168916721754112, 1, '删除', '', '', '', 0, '/api/v1/sysOption/remove', 1, 0, '2025-07-03 12:55:53', 1, '2025-07-03 12:55:53', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243927872462848, 259169177682960384, 1, '查询', '', '', '', 0, '/api/v1/sysDict/query', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243928350613504, 259169177682960384, 1, '保存', '', '', '', 0, '/api/v1/sysDict/save', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243928824569856, 259169177682960384, 1, '删除', '', '', '', 0, '/api/v1/sysDict/remove', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243929294331904, 259169318720626688, 1, '查询', '', '', '', 0, '/api/v1/sysLog/query', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243929755705344, 259169318720626688, 1, '保存', '', '', '', 0, '/api/v1/sysLog/save', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243930225467392, 259169318720626688, 1, '删除', '', '', '', 0, '/api/v1/sysLog/remove', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243930691035136, 259169689438380032, 1, '查询', '', '', '', 0, '/api/v1/ollama/query', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243931164991488, 259169689438380032, 1, '保存', '', '', '', 0, '/api/v1/ollama/save', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243931626364928, 259169689438380032, 1, '删除', '', '', '', 0, '/api/v1/ollama/remove', 1, 0, '2025-07-03 12:55:54', 1, '2025-07-03 12:55:54', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243932104515584, 259169837824466944, 1, '查询', '', '', '', 0, '/api/v1/aiBot/query', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243932607832064, 259169837824466944, 1, '保存', '', '', '', 0, '/api/v1/aiBot/save', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243933077594112, 259169837824466944, 1, '删除', '', '', '', 0, '/api/v1/aiBot/remove', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243933547356160, 259169982154661888, 1, '查询', '', '', '', 0, '/api/v1/aiPlugin/query', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243934025506816, 259169982154661888, 1, '保存', '', '', '', 0, '/api/v1/aiPlugin/save', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243934495268864, 259169982154661888, 1, '删除', '', '', '', 0, '/api/v1/aiPlugin/remove', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243934960836608, 259170117110587392, 1, '查询', '', '', '', 0, '/api/v1/aiWorkflow/query', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243935430598656, 259170117110587392, 1, '保存', '', '', '', 0, '/api/v1/aiWorkflow/save', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243935904555008, 259170117110587392, 1, '删除', '', '', '', 0, '/api/v1/aiWorkflow/remove', 1, 0, '2025-07-03 12:55:55', 1, '2025-07-03 12:55:55', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243936374317056, 259170422338478080, 1, '查询', '', '', '', 0, '/api/v1/aiKnowledge/query', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243936848273408, 259170422338478080, 1, '保存', '', '', '', 0, '/api/v1/aiKnowledge/save', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243937313841152, 259170422338478080, 1, '删除', '', '', '', 0, '/api/v1/aiKnowledge/remove', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243937783603200, 259170538264846336, 1, '查询', '', '', '', 0, '/api/v1/aiLlm/query', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243938240782336, 259170538264846336, 1, '保存', '', '', '', 0, '/api/v1/aiLlm/save', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243938706350080, 259170538264846336, 1, '删除', '', '', '', 0, '/api/v1/aiLlm/remove', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243939180306432, 270761213536096256, 1, '查询', '', '', '', 0, '/api/v1/sysApiKey/query', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243939650068480, 270761213536096256, 1, '保存', '', '', '', 0, '/api/v1/sysApiKey/save', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243940115636224, 270761213536096256, 1, '删除', '', '', '', 0, '/api/v1/sysApiKey/remove', 1, 0, '2025-07-03 12:55:56', 1, '2025-07-03 12:55:56', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243940614758400, 282254669269082112, 1, '查询', '', '', '', 0, '/api/v1/sysJob/query', 1, 0, '2025-07-03 12:55:57', 1, '2025-07-03 12:55:57', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243941080326144, 282254669269082112, 1, '保存', '', '', '', 0, '/api/v1/sysJob/save', 1, 0, '2025-07-03 12:55:57', 1, '2025-07-03 12:55:57', 1, 'gen', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (298243941545893888, 282254669269082112, 1, '删除', '', '', '', 0, '/api/v1/sysJob/remove', 1, 0, '2025-07-03 12:55:57', 1, '2025-07-03 12:55:57', 1, 'gen', 0);

INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243919488049152, 1, 298243919118950400);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243919966199808, 1, 298243919727124480);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243920444350464, 1, 298243920205275136);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243920926695424, 1, 298243920679231488);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243921404846080, 1, 298243921161576448);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243921908162560, 1, 298243921639727104);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243922398896128, 1, 298243922155626496);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243922877046784, 1, 298243922637971456);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243923367780352, 1, 298243923116122112);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243923866902528, 1, 298243923627827200);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243924336664576, 1, 298243924105977856);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243924810620928, 1, 298243924571545600);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243925280382976, 1, 298243925045501952);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243925750145024, 1, 298243925515264000);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243926219907072, 1, 298243925980831744);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243926689669120, 1, 298243926450593792);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243927155236864, 1, 298243926920355840);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243927620804608, 1, 298243927385923584);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243928111538176, 1, 298243927872462848);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243928585494528, 1, 298243928350613504);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243929063645184, 1, 298243928824569856);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243929525018624, 1, 298243929294331904);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243929990586368, 1, 298243929755705344);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243930460348416, 1, 298243930225467392);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243930921721856, 1, 298243930691035136);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243931399872512, 1, 298243931164991488);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243931869634560, 1, 298243931626364928);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243932368756736, 1, 298243932104515584);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243932846907392, 1, 298243932607832064);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243933312475136, 1, 298243933077594112);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243933786431488, 1, 298243933547356160);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243934260387840, 1, 298243934025506816);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243934730149888, 1, 298243934495268864);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243935199911936, 1, 298243934960836608);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243935669673984, 1, 298243935430598656);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243936139436032, 1, 298243935904555008);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243936621780992, 1, 298243936374317056);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243937083154432, 1, 298243936848273408);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243937552916480, 1, 298243937313841152);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243938010095616, 1, 298243937783603200);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243938475663360, 1, 298243938240782336);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243938949619712, 1, 298243938706350080);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243939415187456, 1, 298243939180306432);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243939884949504, 1, 298243939650068480);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243940358905856, 1, 298243940115636224);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243940845445120, 1, 298243940614758400);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243941315207168, 1, 298243941080326144);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (298243941780774912, 1, 298243941545893888);

DELETE FROM `tb_sys_menu` WHERE `id` = 258077347000119296;
DELETE FROM `tb_sys_role_menu` WHERE `menu_id` = 258077347000119296;
DELETE FROM `tb_sys_menu` WHERE `id` = 258079445137780736;
DELETE FROM `tb_sys_role_menu` WHERE `menu_id` = 258079445137780736;
DELETE FROM `tb_sys_menu` WHERE `id` = 259166589327630336;
DELETE FROM `tb_sys_role_menu` WHERE `menu_id` = 259166589327630336;
DELETE FROM `tb_sys_menu` WHERE `id` = 259168810450673664;
DELETE FROM `tb_sys_role_menu` WHERE `menu_id` = 259168810450673664;
DELETE FROM `tb_sys_menu` WHERE `id` = 269220820365377536;
DELETE FROM `tb_sys_role_menu` WHERE `menu_id` = 269220820365377536;
DELETE FROM `tb_sys_menu` WHERE `id` = 269221948243083264;
DELETE FROM `tb_sys_role_menu` WHERE `menu_id` = 269221948243083264;

-- 素材库相关菜单 start --
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300008008381800448, 259169540360232960, 0, '素材库', '/ai/aiResource', '', 'CustomSourceMaterialIcon', 1, '', 52, 0, '2025-07-08 09:45:43', 1, '2025-07-08 09:45:43', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300008359986110464, 300008008381800448, 1, '查询', '', '', '', 0, '/api/v1/aiResource/query', 0, 0, '2025-07-08 09:47:07', 1, '2025-07-08 09:47:07', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300012644643815424, 300008008381800448, 1, '保存', '', '', '', 0, '/api/v1/aiResource/save', 0, 0, '2025-07-08 10:04:08', 1, '2025-07-08 10:04:08', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300013092268326912, 300008008381800448, 1, '删除', '', '', '', 1, '/api/v1/aiResource/remove', 0, 0, '2025-07-08 10:05:55', 1, '2025-07-08 10:05:55', 1, '', 0);

INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300008008490852352, 1, 300008008381800448);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300008360078385152, 1, 300008359986110464);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300012644702535680, 1, 300012644643815424);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300013092310269952, 1, 300013092268326912);
-- 素材库相关菜单 end -

-- v1.1.0 data end --

-- v1.1.3 data start --

-- 数据中枢菜单 --
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300817858217091072, 259169540360232960, 0, '数据中枢', '/datacenter/table', '', 'BookFilled', 1, '', 53, 0, '2025-07-10 15:23:46', 1, '2025-07-10 15:24:36', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300818298270883840, 300817858217091072, 1, '查询', '', '', '', 1, '/api/v1/datacenterTable/query', 0, 0, '2025-07-10 15:25:31', 1, '2025-07-10 15:25:31', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300818387710222336, 300817858217091072, 1, '保存', '', '', '', 1, '/api/v1/datacenterTable/save', 0, 0, '2025-07-10 15:25:53', 1, '2025-07-10 15:25:53', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300818488214134784, 300817858217091072, 1, '删除', '', '', '', 1, '/api/v1/datacenterTable/remove', 0, 0, '2025-07-10 15:26:17', 1, '2025-07-10 15:26:17', 1, '', 0);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300817858284199936, 1, 300817858217091072);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300818298325409792, 1, 300818298270883840);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300818387789914112, 1, 300818387710222336);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300818488344158208, 1, 300818488214134784);

-- v1.1.3 data end --
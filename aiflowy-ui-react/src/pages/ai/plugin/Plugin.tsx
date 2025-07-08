import React, { useEffect, useState } from 'react';
import {
	EditOutlined,
	EllipsisOutlined,
	MinusCircleOutlined,
	PlusOutlined,
	QuestionCircleOutlined,
} from '@ant-design/icons';
import {
	Avatar,
	Button,
	Card,
	Col,
	Dropdown, Empty,
	Form,
	FormProps,
	Input,
	message,
	Modal,
	Pagination,
	Radio,
	Row,
	Select, SelectProps,
	Space,
	Spin,
	Tooltip, Typography,
} from 'antd';
import {useGetManual, usePostManual} from '../../../hooks/useApis.ts';
import SearchForm from '../../../components/AntdCrud/SearchForm.tsx';
import { ColumnsConfig } from '../../../components/AntdCrud';
import ImageUploader from '../../../components/ImageUploader';
import TextArea from 'antd/es/input/TextArea';
import {CheckboxGroupProps} from "antd/es/checkbox";
import {useNavigate} from "react-router-dom";
import './less/plugin.less'
import CustomDeleteIcon from "../../../components/CustomIcon/CustomDeleteIcon.tsx";
import "../../../components/CardPage/card_page.less"
import {useCheckPermission} from "../../../hooks/usePermissions.tsx";
import {useBreadcrumbRightEl} from "../../../hooks/useBreadcrumbRightEl.tsx";
import Sider from "antd/es/layout/Sider";
import Layout from "antd/es/layout/layout";
import CustomToolIcon from "../../../components/CustomIcon/CustomToolIcon.tsx";
import CustomClassifyIcon from "../../../components/CustomIcon/CustomClassifyIcon.tsx";

interface Category {
	id: number;
	name: string;
}
const Plugin: React.FC = () => {
	const navigate = useNavigate();

	// 插件分页信息
	const [pagination, setPagination] = useState({
		current: 1,
		pageSize: 10,
		total: 0,
	});

	// 表格列配置
	const columnsConfig: ColumnsConfig<any> = [
		{
			hidden: true,
			form: { type: 'hidden' },
			dataIndex: 'id',
			key: 'id',
		},
		{
			title: 'Icon',
			dataIndex: 'icon',
			key: 'icon',
			form: { type: 'image' },
		},
		{
			form: { type: 'input', rules: [{ required: true, message: '请输入插件名称' }] },
			dataIndex: 'pluginName',
			title: '插件名称',
			key: 'pluginName',
			supportSearch: true,
			placeholder: '请输入插件名称',
		},
		{
			form: { type: 'TextArea', rules: [{ required: true, message: '请输入插件描述' }], attrs: { rows: 3 } },
			dataIndex: 'pluginDesc',
			title: '插件描述',
			key: 'pluginDesc',
		},
		{
			hidden: true,
			form: { type: 'hidden' },
			dataIndex: 'options',
			title: '插件配置',
			key: 'options',
		},
		{
			form: { type: 'select' },
			dataIndex: 'status',
			title: '数据状态',
			key: 'status',
			dict: { name: 'dataStatus' },
		},
	];

	type FieldType = {
		id?: string;
		icon?: string;
		name?: string;
		description?: string;
		baseUrl?: string;
		headers?: string;
		authData?: string;
		authType?: string;
		position?: string;
		tokenKey?: string;
		tokenValue?: string;
	};

	// 处理表单提交
	const onFinish: FormProps<FieldType>['onFinish'] = (values) => {
		if (isSaveOrUpdate) {
			doPostPluginSave({
				data: {
					icon: values.icon,
					name: values.name,
					description: values.description,
					baseUrl: values.baseUrl,
					headers: JSON.stringify(values.headers),
					authData: values.authData,
					authType: values.authType,
					position: values.position,
					tokenKey: values.tokenKey,
					tokenValue: values.tokenValue,
				},
			}).then((r) => {
				if (r.data.errorCode === 0) {
					message.success('插件保存成功！');
					form.resetFields();
					setAddPluginIsOpen(false);
					doSearchPlugins();
				}
			});
		} else {
			doPostPluginUpdate({
				data: {
					id: values.id,
					icon: values.icon,
					name: values.name,
					description: values.description,
					baseUrl: values.baseUrl,
					headers: JSON.stringify(values.headers),
					authData: values.authData,
					authType: values.authType,
					position: values.position,
					tokenKey: values.tokenKey,
					tokenValue: values.tokenValue,
				},
			}).then((r) => {
				if (r.data.errorCode === 0) {
					message.success('修改成功！');
					doSearchPlugins();
					setAddPluginIsOpen(false);
				} else {
					message.error(r.data.message);
				}
			});
		}
	};

	const options: CheckboxGroupProps<string>['options'] = [
		{ label: 'headers', value: 'headers' },
		{ label: 'query', value: 'query' },
	];

	const onFinishFailed: FormProps<FieldType>['onFinishFailed'] = (errorInfo) => {
		console.log('Failed:', errorInfo);
	};

	// 控制新增插件模态框的显示与隐藏
	const [addPluginIsOpen, setAddPluginIsOpen] = useState(false);

	// 认证类型
	const [authType, setAuthType] = useState('none');

	// 定义是新增还是修改 【true: 新增 false: 修改】
	const [isSaveOrUpdate, setIsSaveOrUpdate] = useState(true);

	// 图标路径
	const [iconPath, setIconPath] = useState('');

	// 认证参数位置
	const [positionValue, setPositionValue] = useState('headers');

	// 插件列表数据
	const [plugins, setPlugins] = useState<any[]>([]);
	const [loading, setLoading] = useState<boolean>(false);

	// 分类相关状态
	const [categories, setCategories] = useState<{ id: number; name: string }[]>([]);
	const [selectedCategoryId, setSelectedCategoryId] = useState<number>(0);
	const [selectedCategoryIdToEdit, setSelectedCategoryIdToEdit] = useState<number | null>(null);
	const [isEditCategory, setIsEditCategory] = useState(false);
	const [categoryModalVisible, setCategoryModalVisible] = useState(false);
	const [newCategoryName, setNewCategoryName] = useState('');
	// 归类相关状态
	const [classifyModalVisible, setClassifyModalVisible] = useState(false);
	const [selectedPluginId, setSelectedPluginId] = useState<number | null>(null);
	// 回显选中的分类
	const [selectedCategoryForClassify, setSelectedCategoryForClassify] = useState< SelectProps['options']>([]);

	// 更新插件分类的方法
	const { doPost: doUpdatePluginCategory } = usePostManual('/api/v1/aiPluginCategoryRelation/updateRelation');
	// 获取插件数据
	// const { doGet: doGetPage } = usePage('aiPlugin', {}, { manual: true });
	const {doGet: doGetPage} = useGetManual('/api/v1/aiPlugin/pageByCategory')

	// 保存插件
	const { doPost: doPostPluginSave } = usePostManual('/api/v1/aiPlugin/plugin/save');
	// 修改插件
	const { doPost: doPostPluginUpdate } = usePostManual('/api/v1/aiPlugin/plugin/update');
	const { doPost: doRemove } = usePostManual('/api/v1/aiPlugin/plugin/remove');

	// 获取分类数据（假设有一个分类接口）
	const { doGet: doGetCategories,loading: loadingCategories } = useGetManual('/api/v1/aiPluginCategories/list');

	const { doPost: doSaveCategories } =usePostManual('/api/v1/aiPluginCategories/save');

	const { doPost: doUpdateCategories } =usePostManual('/api/v1/aiPluginCategories/update');

	const { doGet: doRemoveCategory } =useGetManual('/api/v1/aiPluginCategories/doRemoveCategory');

	const {doGet: doGetPluginCategory} = useGetManual('/api/v1/aiPluginCategoryRelation/getPluginCategories')

	const hasRemovePermission = useCheckPermission('/api/v1/aiPlugin/remove')
	const hasSavePermission = useCheckPermission('/api/v1/aiPlugin/save')

	const [collapsed] = useState(false);

	// 设置面包屑右侧按钮
	useBreadcrumbRightEl(
		<>
			{hasSavePermission && <Button type={'primary'} onClick={() => {
				setAddPluginIsOpen(true);
				setIsSaveOrUpdate(true);
			}}>
				<PlusOutlined /> 新增插件
			</Button>}
		</>, [hasSavePermission]
	);

	// 初始化加载插件和分类数据
	useEffect(() => {
		doSearchPlugins();
		fetchCategories();
	}, []);

	const fetchCategories = (isDeleteCategory: boolean = false) => {
		doGetCategories().then((res) => {
			if (res.data.errorCode === 0) {
				setCategories([{id: 0, name: '全部'},  ...(res.data.data)] || []);
			}
			if (isDeleteCategory){
				doSearchPlugins()
				setSelectedCategoryId(0)
			}
		});
	};

	const doSearchPlugins = (params: any = {}) => {
		setLoading(true);
		doGetPage({
			params: {
				pageNumber: pagination.current,
				pageSize: pagination.pageSize,
				category: params.categoryId | 0,
				...params,
			},
		}).then((r) => {
			if (r.data.errorCode === 0) {
				setPlugins(r.data.data.records);
				setPagination({
					current: r.data.data.pageNumber,
					pageSize: r.data.data.pageSize,
					total: r.data.data.totalRow,
				});
			}
			setLoading(false);
		});
	};

	const handleSearch = (values: any) => {
		doSearchPlugins({ name: values.pluginName });
	};

	const getIconPath = (path: string) => {
		form.setFieldsValue({ icon: path });
	};

	// 创建表单实例
	const [form] = Form.useForm();

	const handleAddPluginCancel = () => {
		form.resetFields();
		setPositionValue('headers');
		setAuthType('none');
		setAddPluginIsOpen(false);
	};

	const handleAddPluginOk = () => {};

	// 点击分类
	const handleSelectCategory = (categoryId: number) => {
		console.log('点击分类', categoryId);
		setSelectedCategoryId(categoryId);
		doSearchPlugins({ categoryId });
	};

	// 打开新增分类模态框
	const openAddCategoryModal = () => {
		setCategoryModalVisible(true);
	};

	// 提交新增分类
	const addNewCategory = () => {
		if (!newCategoryName.trim()) {
			message.warning('请输入分类名称');
			return;
		}

		if (isEditCategory){
			doUpdateCategories({data:{id: selectedCategoryIdToEdit, name: newCategoryName}}).then((res)=>{
				if (res.data.errorCode === 0){
					message.success('修改成功');
					setNewCategoryName('')
					setCategoryModalVisible(false)
					fetchCategories();
				}

			})
		} else {
			// 模拟添加本地分类
			const newCategory = {
				id: Date.now(),
				name: newCategoryName,
			};
			setCategories([...categories, newCategory]);
			setNewCategoryName('');
			setCategoryModalVisible(false);
			doSaveCategories({data:{name:newCategoryName}}).then((res)=>{
				if (res.data.errorCode === 0){
					message.success('分类添加成功');
					fetchCategories();
				}

			})
		}


	};
	const handleClassifySubmit = () => {
		// if (!selectedPluginId || selectedCategoriesForClassify.length === 0) {
		// 	message.warning('请至少选择一个分类');
		// 	return;
		// }
		doUpdatePluginCategory({
			data: {
				pluginId: selectedPluginId,
				categoryIds: selectedCategoryForClassify // 注意这里是数组
			}
		}).then(res => {
			if (res.data.errorCode === 0) {
				message.success('分类更新成功');
				doSearchPlugins({categoryId: selectedCategoryId}); // 刷新插件列表
			} else {
				message.error(res.data.message || '分类更新失败');
			}
		});

		setClassifyModalVisible(false);
	};

	// 编辑分类
	const handleEditCategory = (category: any) => {
		setNewCategoryName(category.name);
		setSelectedCategoryIdToEdit(category.id); // 用于标识正在编辑的分类 ID
		setIsEditCategory(true)
		setCategoryModalVisible(true);
	};
	// 删除分类
	const handleDeleteCategory = (categoryId: any) => {
		Modal.confirm({
			title: '确定要删除该分类吗？',
			content: '此操作不可逆，会删除该分类下所有的插件，请谨慎操作。',
			onOk() {
				// 假设你有删除分类的接口
				doRemoveCategory({ params: { id: categoryId } }).then((res) => {
					if (res.data.errorCode === 0) {
						message.success('分类删除成功');
						fetchCategories(true); // 刷新分类列表
					} else {
						message.error(res.data.message || '删除失败');
					}
				});
			},
		});
	};
	return (
		<Layout
			style={{background: "#f5f5f5", height: "100%", width: '100%'}}
		>

			<div style={{  width: '100%', display: 'flex', flexDirection: 'row'}}>
			{/* 左侧分类导航 */}

			<Sider style={{background: "white",  borderRadius: '8px 8px 8px 8px', height: "100%"}} width={220}  className="header-container"
					   collapsed={collapsed}>
					<Card title='插件'
						  bordered={false}
						  style={{
							  height: "100%",
							  borderRadius: '8px 8px 8px 8px'
						  }}
						  styles={{
							  body: {
							  }
						  }}
						  extra={<Space>
							  <div style={{width: "100%", display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "center", gap: "10px"}}>
								  {!collapsed &&
									  <>
                                 		 <Button size={"small"} onClick={openAddCategoryModal}>
											 新增分类
										 </Button>

									  </>

								  }

								  {/*{*/}
									{/*  collapsed ?*/}
									{/*	  (*/}
									{/*		  <span onClick={() => setCollapsed(!collapsed)} style={{cursor: "pointer"}}>*/}
                                  {/*       <DoubleRightOutlined />*/}
                                  {/*    </span>*/}
									{/*	  )*/}
									{/*	  :*/}
									{/*	  (*/}
									{/*		  <span onClick={() => setCollapsed(!collapsed)} style={{cursor: "pointer"}}>*/}
                                  {/*             <DoubleLeftOutlined />*/}
                                  {/*        </span>*/}
									{/*	  )*/}

								  {/*}*/}

							  </div>
						  </Space>}
					>
					<div style={{ backgroundColor: "white", height: '100%'}}>

						{loadingCategories? (

							<div style={{   width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
								<Spin spinning={loadingCategories} />
							</div>
						):(

							<div>
								{categories.map((cat) => (
									<div
										key={cat.id}
										className={`category-item ${selectedCategoryId === cat.id ? 'selected' : ''}`}
										style={{
											position: 'relative',
											// 新增样式：收起时限制宽度
											width: collapsed ? '80px' : '100%',
											whiteSpace: 'nowrap',
											overflow: 'hidden',
											textOverflow: collapsed ? 'ellipsis' : 'clip',
										}}
										onClick={() => handleSelectCategory(cat.id)}
									>
									<span style={{ cursor: 'pointer', flex: 1 }}>
  									  {cat.name}
  									</span>
									{!collapsed && cat.id !== 0 && (
									<span style={{ position: 'absolute', right: '10px' }}>
  									    <Dropdown menu={{ /* ... */ }}>
  									      <EllipsisOutlined style={{ fontSize: '18px', cursor: 'pointer' }} />
  									    </Dropdown>
  									  </span>
										)}
										{ !collapsed && cat.id != 0 ? <span style={{position: 'absolute', right: '10px'}}>
										<Dropdown
											menu={{
												items: [
													{
														key: 'edit',
														icon: <EditOutlined />,
														label: '编辑',
														onClick: (e) => {
															e.domEvent.stopPropagation(); // 阻止事件冒泡
															handleEditCategory(cat)
														}
													},
													{
														key: 'delete',
														icon: <CustomDeleteIcon />,
														label: '删除',
														danger: true,
														onClick: (e) => {
															e.domEvent.stopPropagation(); // 阻止事件冒泡
															handleDeleteCategory(cat.id)
														},
													},
												],
											}}
											trigger={['hover']}
										>
									<EllipsisOutlined
										style={{
											fontSize: '18px',
											cursor: 'pointer',
											marginLeft: '8px',
										}}
									/>
								</Dropdown>
								</span>
												: ''
										}


									</div>
								))}
							</div>)}


					</div>
					</Card>
				</Sider>

			{/* 右侧插件内容 */}
			<div style={{ flex: 1, maxWidth: 'calc(100% - 230px)'}}>
				<SearchForm columns={columnsConfig} colSpan={6} onSearch={handleSearch} />
				<Spin spinning={loading} >
					<Row className={"card-row"} gutter={[16, 16]}>

						{loading ? (
							<div style={{ display: 'flex', flexDirection: 'row',  justifyContent: 'center',
								alignItems: 'center',  height: '100%', width: '100%' }}>
								<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} className={"empty-container"}/>
							</div>

						) : plugins.length > 0 ? (

								plugins.map((item: any) => (
									<Col span={6} key={item.id}
										 xs={24}
										 sm={12}
										 md={8}
										 lg={6}
									>
										<Card
											style={{
												height: '100%',
												display: 'flex',
												flexDirection: 'column',
												cursor: 'pointer',
											}}
											className={"card-hover"}
											actions={[
												(hasSavePermission &&
													<Space  onClick={() => {
														navigate('/ai/pluginTool', {
															state: {
																id: item.id,
																pluginTitle: item.name,
															}
														});
													}}>
														<CustomToolIcon/>
														<span>工具</span>
													</Space>
												)

												,
												(
													hasSavePermission &&
													<Space onClick={() => {
														setIsSaveOrUpdate(false);
														form.setFieldsValue({
															id: item.id,
															icon: item.icon,
															name: item.name,
															description: item.description,
															baseUrl: item.baseUrl,
															headers: item.headers ? JSON.parse(item.headers) : [],
															authData: item.authData,
															authType: item.authType,
															position: item.position,
															tokenKey: item.tokenKey,
															tokenValue: item.tokenValue,
														});
														setIconPath(item.icon);
														setAuthType(item.authType);
														setAddPluginIsOpen(true);
													}} >
														<EditOutlined key="edit" />
														<span>编辑</span>
													</Space>
												),
												(hasSavePermission &&
													<Space  onClick={() => {
														setSelectedPluginId(item.id);
														// 查询当前插件的分类
														doGetPluginCategory({
															params: {
																pluginId: item.id
															}
														}).then((res) => {
															if (res.data.errorCode === 0) {
																const options = res.data.data.map((item: Category) => ({
																	value: item.id,
																	label: item.name
																}));
																setSelectedCategoryForClassify(options)
																// setSelectedCategoryForClassify(item.categoryIds || [])
																setClassifyModalVisible(true);
															}
														});

													}}>
														<CustomClassifyIcon/>
														<span>归类</span>
													</Space>
												)
												,
												<Dropdown menu={{
													items: [
														...(hasRemovePermission ? [	{
															key: 'delete',
															label: '删除',
															icon: <CustomDeleteIcon />,
															danger: true,
															onClick: () => {
																Modal.confirm({
																	title: '确定要删除吗?',
																	content: '此操作不可逆，请谨慎操作。',
																	onOk() {
																		doRemove({ data: { id: item.id } }).then((r) => {
																			if (r.data.errorCode === 0) {
																				message.success("删除成功！");
																				doSearchPlugins({categoryId: 0});
																			} else {
																				message.error(r.data.message);
																			}
																		});
																	},
																});
															},
														},] : []),

													],
												}}>

													<EllipsisOutlined key="ellipsis" title="更多操作" />
												</Dropdown>,
											]}
										>
											<Card.Meta
												avatar={<Avatar src={item.icon || "/src/assets/pluginIcon.png"} style={{width: '48px', height: '48px'}} />}
												title={item.name}
												description={
													<Tooltip title={item.description}>
														<div style={{
															display: '-webkit-box',
															WebkitLineClamp: 1,
															WebkitBoxOrient: 'vertical',
															overflow: 'hidden',
															textOverflow: 'ellipsis',
														}}>{item.description}</div>
													</Tooltip>
												}
											/>
										</Card>
									</Col>
								))
							)
							:
							(<>
								<Empty
									image={Empty.PRESENTED_IMAGE_SIMPLE}
									className={"empty-container"}
									description={
										<Typography.Text style={{color: '#969799'}}>
											{"暂无数据"}
										</Typography.Text>
									}
								>

									{hasSavePermission && (
										<Button  style={{borderColor: '#0066FF', color: '#0066FF', width: '195px', height: '48px'}}
												 onClick={() => {
													 setAddPluginIsOpen(true)
												 }}>
											{"创建插件"}
										</Button>
									)}

								</Empty>

							</>)
						}
					</Row>

					<Pagination
						total={pagination.total}
						align="center"
						onChange={(page, pageSize) => {
							setPagination({ ...pagination, current: page, pageSize });
							doSearchPlugins({ pageNumber: page, pageSize });
						}}
						pageSizeOptions={["10", "20", "50", "100"]}
						showSizeChanger={true}
						hideOnSinglePage={true}
						current={pagination.current}
						defaultCurrent={1}
						defaultPageSize={10}
					/>
				</Spin>
			</div>

			{/* 新增插件模态框 */}
			<Modal
				title="新增插件"
				open={addPluginIsOpen}
				onOk={handleAddPluginOk}
				onCancel={handleAddPluginCancel}
				footer={null}
			>
				<Form
					form={form}
					layout="vertical"
					name="basic"
					style={{ width: '100%' }}
					initialValues={{ authType: 'none', position: 'headers' }}
					onFinish={onFinish}
					onFinishFailed={onFinishFailed}
					autoComplete="off"
				>
					{/* 表单项... 和原文件一致，省略以保持简洁 */}
					{/* 此处可以复用你原来的表单结构 */}
					<Form.Item<FieldType> name="id" hidden></Form.Item>
					<Form.Item<FieldType> name="icon" style={{ textAlign: 'center' }}>
						<div style={{ display: 'flex', justifyContent: 'center' }}>
							<Input hidden />
							<ImageUploader onChange={getIconPath} value={iconPath} />
						</div>
					</Form.Item>
					<Form.Item<FieldType>
						label="插件名称"
						name="name"
						rules={[{ required: true, message: '请输入插件名称!' }]}
					>
						<Input maxLength={30} showCount placeholder={'请输入插件名称'} />
					</Form.Item>
					<Form.Item<FieldType>
						label="插件描述"
						name="description"
						rules={[{ required: true, message: '请输入插件描述!' }]}
					>
						<TextArea showCount maxLength={500} placeholder="请输入插件描述" style={{ height: 80, resize: 'none' }} />
					</Form.Item>
					<Form.Item<FieldType>
						name="baseUrl"
						label={'插件 URL'}
						rules={[{ required: true, message: '请输入插件URL' }]}
					>
						<Input placeholder="请输入插件 URL" />
					</Form.Item>
					<Form.Item<FieldType> name="headers" label={'Headers'}>
						<Form.List name="headers">
							{(fields, { add, remove }) => (
								<>
									{fields.map(({ key, name, ...restField }) => (
										<Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
											<Form.Item {...restField} name={[name, 'label']} rules={[{ required: true, message: 'Missing label' }]}>
												<Input placeholder="Headers Name" />
											</Form.Item>
											<Form.Item {...restField} name={[name, 'value']} rules={[{ required: true, message: 'Missing value' }]}>
												<Input placeholder="Headers value" />
											</Form.Item>
											<MinusCircleOutlined onClick={() => remove(name)} />
										</Space>
									))}
									<Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
										添加 headers
									</Button>
								</>
							)}
						</Form.List>
					</Form.Item>
					<Form.Item<FieldType>
						label={
							<span>
                认证方式
                <Tooltip title="选择插件使用的授权或验证方式。目前支持如下两种类型：\n1. 无需认证\n2. Service token / API key">
                  <QuestionCircleOutlined style={{ marginLeft: 8, color: 'rgba(0,0,0,.45)' }} />
                </Tooltip>
              </span>
						}
						name="authType"
						rules={[{ required: true, message: '请选择认证方式!' }]}
					>
						<Select
							onChange={(value) => {
								setAuthType(value);
							}}
							options={[
								{ value: 'none', label: '无需认证' },
								{ value: 'apiKey', label: 'Service token / API key' },
							]}
						/>
					</Form.Item>
					{authType === 'apiKey' && (
						<>
							<Form.Item<FieldType>
								name="position"
								label={'参数位置'}
								rules={[{ required: true, message: '请输入认证数据' }]}
							>
								<Radio.Group options={options} value={positionValue} onChange={(e) => setPositionValue(e.target.value)} />
							</Form.Item>
							<Form.Item<FieldType>
								label="tokenKey"
								name="tokenKey"
								rules={[{ required: true, message: '请输入tokenKey!' }]}
							>
								<Input maxLength={500} showCount />
							</Form.Item>
							<Form.Item<FieldType>
								label="tokenValue"
								name="tokenValue"
								rules={[{ required: true, message: '请输入tokenValue' }]}
							>
								<Input maxLength={2000} showCount />
							</Form.Item>
						</>
					)}
					<Form.Item label={null}>
						<Space style={{ display: 'flex', justifyContent: 'flex-end' }}>
							<Button onClick={handleAddPluginCancel}>取消</Button>
							<Button type="primary" htmlType="submit" style={{ marginRight: 8 }}>
								确定
							</Button>
						</Space>
					</Form.Item>
				</Form>
			</Modal>

			{/* 新增分类模态框 */}
			<Modal
				title="新增/修改分类"
				open={categoryModalVisible}
				onOk={addNewCategory}
				onCancel={() => setCategoryModalVisible(false)}
			>

				<Input
					placeholder="请输入分类名称"
					value={newCategoryName}
					onChange={(e) => setNewCategoryName(e.target.value)}
				/>
			</Modal>

			{/* 插件归类模态框 */}
			<Modal
				title="选择分类"
				open={classifyModalVisible}
				onOk={handleClassifySubmit}
				onCancel={() => setClassifyModalVisible(false)}
				destroyOnClose
			>
				<Form layout="vertical">
					<Form.Item label="请选择分类">
						<Select
							mode="tags"
							value={selectedCategoryForClassify}
							onChange={(values) => setSelectedCategoryForClassify(values)}
							placeholder="请选择分类"
							style={{ width: '100%' }}
						>
							{categories
								.filter((cat) => cat.name !== '全部') // 过滤掉 name 为 '全部' 的分类
								.map((cat) => (
									<Select.Option key={cat.id} value={cat.id}>
										{cat.name}
									</Select.Option>
								))}
						</Select>
					</Form.Item>
				</Form>
			</Modal>
		</div>

		</Layout>
	);
};

export default {
	path: "/ai/plugin",
	element: Plugin
};

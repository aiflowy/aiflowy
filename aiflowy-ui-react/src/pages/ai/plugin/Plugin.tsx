import React, {useEffect, useState} from 'react';
import {
	DeleteOutlined,
	EditOutlined,
	EllipsisOutlined,
	MenuUnfoldOutlined,
	MinusCircleOutlined,
	PlusOutlined, QuestionCircleOutlined
} from '@ant-design/icons';
import {
	Avatar,
	Button,
	Card,
	Col,
	Dropdown,
	Form,
	FormProps,
	Input,
	message,
	Modal, Pagination,
	Radio,
	Row,
	Select,
	Space, Spin, Tooltip
} from 'antd';
import {usePage, usePostManual} from "../../../hooks/useApis.ts";
import SearchForm from "../../../components/AntdCrud/SearchForm.tsx";
import {ColumnsConfig} from "../../../components/AntdCrud";
import {useBreadcrumbRightEl} from "../../../hooks/useBreadcrumbRightEl.tsx";
import ImageUploader from "../../../components/ImageUploader";
import TextArea from "antd/es/input/TextArea";
import {CheckboxGroupProps} from "antd/es/checkbox";
import {useNavigate} from "react-router-dom";


const Plugin: React.FC = () => {
	const navigate = useNavigate();
	const [pagination, setPagination] = useState({
		current: 1, // 当前页码
		pageSize: 10, // 每页显示条数
		total: 0, // 总记录数
	});
	const columnsConfig: ColumnsConfig<any> = [
		{
			hidden: true,
			form: {
				type: "hidden"
			},
			dataIndex: "id",
			key: "id"
		},
		{
			title: 'Icon',
			dataIndex: 'icon',
			key: 'icon',
			form: {
				type: "image"
			}
		},
		{
			form: {
				type: "input",
				rules: [{required: true, message: '请输入插件名称'}]
			},
			dataIndex: "pluginName",
			title: "插件名称",
			key: "pluginName",
			supportSearch: true,
			placeholder: "请输入插件名称",

		},

		{
			form: {
				type: "TextArea",
				rules: [{required: true, message: '请输入插件描述'}],
				attrs: {
					rows: 3
				}
			},
			dataIndex: "pluginDesc",
			title: "插件描述",
			key: "pluginDesc"
		},

		{
			hidden: true,
			form: {
				type: "hidden"
			},
			dataIndex: "options",
			title: "插件配置",
			key: "options"
		},

		{
			form: {
				type: "select"
			},
			dataIndex: "status",
			title: "数据状态",
			key: "status",
			dict: {
				name: "dataStatus"
			}
		},
	];
	type FieldType = {
		id?: string;
		icon?: string;
		name?: string;
		description	?: string;
		baseUrl?: string; // 基础地址
		headers?: string;
		authData?: string;
		authType?: string;
		position?: string;
		tokenKey?: string;
		tokenValue?: string;

	};

	const onFinish: FormProps<FieldType>['onFinish'] = (values) => {
		// 如果是新增
		if (isSaveOrUpdate){
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
					tokenValue: values.tokenValue
				}
			}).then(r =>{
				if (r.data.errorCode == 0){
					message.success("插件保存成功！")
					form.resetFields()
					setAddPluginIsOpen(false)
					doGetPage({
						params: {
							pageNumber: 1,
							pageSize: 10,
						}
					})
				}
			})
		} else {
			// 如果是修改
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
				}
			}).then(r =>{
				if (r.data.errorCode == 0){
					message.success("修改成功！")
					doGetPage({
						params: {
							pageNumber: 1,
							pageSize: 10,
						}
					})
					setAddPluginIsOpen(false)

				} else if (r.data.errorCode >= 1){
					message.error(r.data.message)
				}
			})
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
	const [addPluginIsOpen, setAddPluginIsOpen] = useState(false)
	// 认证类型
	const [authType, setAuthType] = useState('none')
	// 定义是新增还是修改 【true: 新增 false: 修改】
	const [isSaveOrUpdate, setIsSaveOrUpdate] = useState(true)
	const [iconPath, setIconPath] = useState('')
	// 认证参数位置
	const [positionValue, setPositionValue] = useState('headers')
	const {
		loading,
		result,
		doGet: doGetPage
	} = usePage('aiPlugin', {}, {manual: true})
	// 保存插件
	const {doPost: doPostPluginSave} = usePostManual('/api/v1/aiPlugin/plugin/save')
	// 修改插件
	const {doPost: doPostPluginUpdate} = usePostManual('/api/v1/aiPlugin/plugin/update')
	const {doPost: doRemove} = usePostManual('/api/v1/aiPlugin/plugin/remove')
	useBreadcrumbRightEl(<Button type={"primary"} onClick={() => {
		setAddPluginIsOpen(true)
		// 设置modal 打开方式为新增
		setIsSaveOrUpdate(true)
	}}>
		<PlusOutlined/>新增插件</Button>)
	useEffect(() => {
		doGetPage({
			params: {
				pageNumber: 1,
				pageSize: 10,
			}
		}).then(r => {
			if (r.data.errorCode == 0){
				setPagination({
					current: r.data.data.pageNumber,
					pageSize: r.data.data.pageSize,
					total: r.data.data.totalRow
				})
			}
		})
	}, [])

	useEffect(() => {
		setPagination({
			current: result?.data.pageNumber,
			pageSize: result?.data.pageSize,
			total: result?.data.totalRow
		})
	}, [result])

	const getIconPath = (path: string) => {
		form.setFieldsValue({
			icon: path
		})
	}
	// 创建表单实例
	const [form] = Form.useForm();
	const handleaddPluginCancle = () => {
		form.resetFields()
		setPositionValue('headers')
		setAuthType('none')
		setAddPluginIsOpen(false);
	};

	const handleaddPluginOk = () => {
		// setAddPluginIsOpen(false);
	};
	return loading ?
		<div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
		<Spin />
		</div>
		:
		(
		<div style={{height: 'calc(100vh - 68px)',overflowY: 'auto'}}>
			<SearchForm columns={columnsConfig} colSpan={6}
						onSearch={(values: any) => {
							doGetPage({
								params: {
									name:  values.pluginName
								}
							})
						}}
			/>
			<Row className={"card-row"} gutter={[16, 16]} >
				{result?.data?.records?.length > 0 ? result?.data?.records?.map((item: any) => (
					<Col span={6} key={item.id}>
						<Card  actions={
							[
								<MenuUnfoldOutlined title="工具列表" onClick={() => {
									navigate('/ai/pluginTool', {
										state: {
											id: item.id,
											pluginTitle: item.name
										}
									})
								}}/>,
								<EditOutlined key="edit" onClick={() =>{
									// 设置modal 打开方式为修改
									setIsSaveOrUpdate(false)
									// 赋值模态框数据
									form.setFieldsValue({
										id: item.id,
										icon: item.icon,
										name: item.name,
										description: item.description,
										baseUrl: item.baseUrl,
										headers: item.headers?JSON.parse(item.headers):[],
										authData: item.authData,
										authType: item.authType,
										position: item.position,
										tokenKey: item.tokenKey,
										tokenValue: item.tokenValue
									})
									setIconPath(item.icon)
									setAuthType(item.authType)
									setAddPluginIsOpen(true)
								}} />,
								<Dropdown menu={{
									items: [
										{
											key: 'delete',
											label: '删除',
											icon: <DeleteOutlined/>,
											danger: true,
											onClick: () => {
												Modal.confirm({
													title: '确定要删除吗?',
													content: '此操作不可逆，请谨慎操作。',
													onOk() {
														doRemove({
															data: {
																id: item.id
															}
														}).then(r =>{
															if (r.data.errorCode == 0){
																message.success("删除成功！")
																setAddPluginIsOpen(false)
																doGetPage({
																	data: {
																		pageNumber: 1,
																		pageSize: 10,
																	}
																})
															} else {
																message.error(r.data.message)
															}
														})
													},
													onCancel() {
													},
												});
											},
										}
									],
								}}>
									<EllipsisOutlined key="ellipsis" title="更多操作"/>
								</Dropdown>
							]

						} style={{padding: 8}} >
							<Card.Meta
								avatar={<Avatar src={item.icon || "/favicon.png"} />}
								title={item.name}
								description={
									<Tooltip title={item.description}>
										<div style={{
											display: '-webkit-box',
											WebkitLineClamp: 1,      // 限制显示行数
											WebkitBoxOrient: 'vertical',
											overflow: 'hidden',
											textOverflow: 'ellipsis',
										}}>
											{item.description}
										</div>
									</Tooltip>
								}
							/>
						</Card>
					</Col>)

				) : (<div></div>)}

			</Row>
			<Pagination  total={pagination.total} align="end" showTotal={(total) => `共 ${total} 条数据`}
			onChange={(page, pageSize) => {
				doGetPage({
					params: {
						pageNumber: page,
						pageSize: pageSize,
					}
				})
			}}
			showSizeChanger={true}
			pageSizeOptions={[10, 20, 30, 40, 50]}
			defaultCurrent={1}
			defaultPageSize={10}

			/>
			<Modal title="新增插件" open={addPluginIsOpen} onOk={handleaddPluginOk}
				   onCancel={handleaddPluginCancle}
				   styles={{
					   body: { maxHeight: '500px', overflowY: 'auto' }, // 使用 styles 替代 bodyStyle
				   }}
				   footer={null}
			>
				<Form
					form={form}
					layout="vertical"
					name="basic"
					style={{ width: '100%'}}
					initialValues={{ authType: 'none', position: 'headers' }}
					onFinish={onFinish}
					onFinishFailed={onFinishFailed}
					autoComplete="off"
				>
					<Form.Item<FieldType>
						name="id"
						hidden
					>
					</Form.Item>
					<Form.Item<FieldType>
						name="icon"
						style={{ textAlign: 'center'}}
					>
						<div style={{ display: 'flex', justifyContent: 'center' }}>
							<Input  hidden/>
							{/* 使用 flex 布局确保 ImageUploader 居中 */}
							<ImageUploader onChange={getIconPath} value={iconPath}/>
						</div>
					</Form.Item>

					<Form.Item<FieldType>
						label="插件名称"
						name="name"
						rules={[{ required: true, message: '请输入插件名称!' }]}
					>
						<Input maxLength={30} showCount/>
					</Form.Item>
					<Form.Item<FieldType>
						label="插件描述"
						name="description"
						rules={[{ required: true, message: '请输入插件描述!' }]}
					>
						<TextArea
							showCount
							maxLength={500}
							placeholder="disable resize"
							style={{ height: 80, resize: 'none' }}
						/>
					</Form.Item>

					<Form.Item<FieldType>
						name="baseUrl"
						label={'插件URL'}
					  	rules={[{ required: true, message: '请输入插件URL' }]}
					>
						<Input />
					</Form.Item>
					<Form.Item<FieldType>
						name="headers"
						label={'Headers'}
					>
					<Form.List name="headers">
						{(fields, { add, remove }) => (
							<>
								{fields.map(({ key, name, ...restField }) => (
									<Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
										<Form.Item
											{...restField}
											name={[name, 'label']}
											rules={[{ required: true, message: 'Missing label ' }]}
										>
											<Input placeholder="Headers Name" />
										</Form.Item>
										<Form.Item
											{...restField}
											name={[name, 'value']}
											rules={[{ required: true, message: 'Missing  value' }]}
										>
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
							<span >
      									认证方式
      						<Tooltip style={{width: 350}}
									 styles={{root: {maxWidth: 350 }}}
									 title={
								<div>
									<p style={{ marginBottom: 8, width: 300 }}>选择插件使用的授权或验证方式。目前支持如下两种类型：</p>
									<p>1.  无需认证</p>
									<p style={{ fontSize: 12}}>不需要认证，直接访问插件即可。</p>
									<p>2.  Service token / API key</p>
									<p style={{ fontSize: 12}}>Token（令牌）是一种身份验证方式，用户或系统需提供唯一的密钥（Token）来访问受保护的资源。插件校验 Token 合法后，允许操作。</p>
								</div>
							}
										 >
      						  <QuestionCircleOutlined style={{ marginLeft: 8, color: 'rgba(0,0,0,.45)' }} />
      						</Tooltip>
    						</span>
						}
						name="authType"
						rules={[{ required: true, message: '请输入插件名称!' }]}
					>
						<Select
							onChange={(value) => {
								setAuthType(value)
							}}
							options={[{ value: 'none', label: '无需认证' }, { value: 'apiKey', label: 'Service token / API key' }]}
						/>
					</Form.Item>
					{authType === 'apiKey' ?
						<>
						<Form.Item<FieldType>
						name="position"
						label={'参数位置'}
						rules={[{ required: true, message: '请输入认证数据' }]}
						>
						<Radio.Group options={options} value={positionValue} onChange={(e) =>{
							setPositionValue(e.target.value)
						}} />

						</Form.Item>
						<Form.Item<FieldType>
							label="tokenKey"
							name="tokenKey"
							rules={[{ required: true, message: '请输入tokenKey!' }]}
						>
							<Input maxLength={500} showCount/>
						</Form.Item>
						<Form.Item<FieldType>
						label="tokenValue"
						name="tokenValue"
						rules={[{ required: true, message: '请输入tokenValue' }]}>
					<Input maxLength={2000} showCount/>
					</Form.Item>
						</>
						: <></> }


					<Form.Item label={null}>
						<Space style={{ display: 'flex', justifyContent: 'flex-end' }} >
							{/* 取消按钮 */}
							<Button onClick={handleaddPluginCancle}>取消</Button>
							{/* 确定按钮 */}
							<Button type="primary" htmlType="submit" style={{ marginRight: 8 }}>
								确定
							</Button>
						</Space>
					</Form.Item>
				</Form>
			</Modal>
		</div>
	);
};

export default {
	path: "/ai/plugin",
	element: Plugin
};

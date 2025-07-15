import React, {useEffect, useState} from 'react';
import {Button, Form, FormProps, Input, message, Modal, Space, Table, TableProps, Tooltip} from 'antd';
import {useLocation, useNavigate} from "react-router-dom";
import {useLayout} from "../../../hooks/useLayout.tsx";
import {EditOutlined, LeftOutlined, PlusOutlined} from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";
import {usePage, usePostManual} from "../../../hooks/useApis.ts";
import {convertDatetimeUtil} from "../../../libs/changeDatetimeUtil.tsx";
import {ColumnsConfig} from "../../../components/AntdCrud";
import KeywordSearchForm from "../../../components/AntdCrud/KeywordSearchForm.tsx";
import CustomDeleteIcon from "../../../components/CustomIcon/CustomDeleteIcon.tsx";

interface DataType {
    id: string;
    key: string;
    name: string;
    age: number;
    address: string;
    tags: string[];
}

const PluginTool: React.FC = () =>{
    const [pagination, setPagination] = useState({
        current: 1, // 当前页码
        pageSize: 10, // 每页显示条数
        total: 0, // 总记录数
    });
    type FieldType = {
        name?: string;
        description?: string;
    };
    const location = useLocation();
    // 获取路由参数 插件id
    const { id, pluginTitle } = location.state || {};
    // 创建表单实例
    const [form] = Form.useForm();
    const {setOptions} = useLayout();
    const navigate = useNavigate();

    // 控制创建工具模态框的显示与隐藏
    const [isAddPluginToolModalOpen, setAddPluginToolIsOpen] = React.useState(false);
    // useBreadcrumbRightEl(
    //     <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 16}}>
    //         <div>
    //             <Button onClick={() => navigate(-1)}  icon={<ArrowLeftOutlined />}>返回</Button>
    //             {/* 其他内容 */}
    //         </div>
    //         <div>
    //             <Button type={"primary"} onClick={() => {
    //                 setAddPluginToolIsOpen(true);
    //             }}>
    //                 <PlusOutlined/>创建工具</Button>
    //         </div>
    //     </div>
    // )

    const {
        loading,
        result,
        doGet: doGetPage
    } = usePage('aiPluginTool', {}, {manual: true})
    const {doPost: doPostSavePluginTool} = usePostManual('/api/v1/aiPluginTool/tool/save')
    const {doPost: doPostRemovePluginTool} = usePostManual('/api/v1/aiPluginTool/remove')
    const doGetPagePluginTool = () =>{
        doGetPage({
            params: {
                pageNumber: 1,
                pageSize: 10,
                pluginId: id
            }
        })
    }
    useEffect(() => {
        setOptions({
            showBreadcrumb: true,
            breadcrumbs: [
                {title: '首页'},
                {title: '插件', href: `/ai/plugin`},
                {title: pluginTitle},
            ],
        })
        doGetPagePluginTool()
        return () => {
            setOptions({
                showBreadcrumb: true,
                breadcrumbs: []
            })
        }
    }, [])

    useEffect(() => {
        if (result?.errorCode == 0){
            setPagination({
                current: result?.data.pageNumber,
                pageSize: result?.data.pageSize,
                total: result?.data.totalRow
            })
        }
    }, [result])
    const columns: TableProps<DataType>['columns'] = [
        {
            title: 'id',
            dataIndex: 'id',
            key: 'id',
            hidden: true
        },
        {
            title: '工具名称',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: '工具描述',
            dataIndex: 'description',
            key: 'description',
            ellipsis: true,  // 超出显示省略号
            render: (text: string) => (
                <Tooltip title={text}>
                    <span>{text}</span>
                </Tooltip>
            ),
        },

        {
            title: '创建时间',
            dataIndex: 'created',
            key: 'created',
            render:convertDatetimeUtil
        },
        {
            title: '操作',
            key: 'action',
            render: (_:any, record:DataType) => (
                <Space size="middle">
                    <a onClick={() =>{
                        navigate('/ai/pluginToolEdit', {
                            state: {
                                id: record.id,
                                pluginId: id,
                                // 插件名称
                                pluginTitle:pluginTitle,
                                // 插件工具名称
                                pluginToolTitle: record.name,
                                title: record.name
                            }
                        })
                    }}> <EditOutlined /> 修改 </a>

                    <a style={{color: 'red'}} onClick={() =>{
                        Modal.confirm({
                            title: '确认删除？',
                            content: '确认删除？',
                            okText: '确认',
                            cancelText: '取消',
                            onOk: () => {
                                doPostRemovePluginTool({
                                    data: {
                                        id: record.id
                                    }
                                }).then(r =>{
                                    if  (r.data.errorCode == 0){
                                        message.success("删除成功！")
                                        doGetPagePluginTool()
                                    } else if (r.data.errorCode >= 1){
                                        message.error("删除失败！")
                                    }
                                })
                            },
                        });
                    }}> <CustomDeleteIcon /> 删除 </a>

                </Space>
            ),
        },
    ];
    const columnsSearchConfig: ColumnsConfig<any> = [
        {
            form: {
                type: "input"
            },
            dataIndex: "name",
            title: "工具名称",
            key: "name",
            supportSearch: true
        }
    ];

    const onFinish: FormProps<FieldType>['onFinish'] = (values) => {
        doPostSavePluginTool({
            data: {
                pluginId: id,
                name: values.name,
                description: values.description
            }
        }).then(r =>{
            if (r.data.errorCode == 0){
                message.success("创建成功！")
                form.resetFields()
                setAddPluginToolIsOpen(false)
                doGetPagePluginTool()
            } else if (r.data.errorCode >= 1){
                message.error("创建失败！")
            }

        })
    };

    const onFinishFailed: FormProps<FieldType>['onFinishFailed'] = (errorInfo) => {
        console.log('Failed:', errorInfo);
    };
    const handleAddPluginToolOk = () => {
        // setAddPluginToolIsOpen(false);
    };

    const handleAddPluginToolCancel = () => {
        setAddPluginToolIsOpen(false);
    };
    // 分页变化时触发
    const handleTableChange = (newPagination:any) => {
        const { current, pageSize } = newPagination;
        doGetPage({
            params: {
                pageNumber: current,
                pageSize: pageSize,
                pluginId: id
            }
        }).then(res => {
            if (res) {
                setPagination({
                    current: newPagination.current, // 当前页码
                    pageSize: newPagination.pageSize, // 每页显示条数
                    total: res.data.totalRow, // 总记录数
                })
            }

        })
    };
    return (
        <div style={{padding: "24px"}}>
             <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 16}}>
                 <div style={{paddingBottom: 24}}>
                     <Button onClick={() => navigate(-1)}  icon={<LeftOutlined/>}>返回</Button>
                 </div>
                 {/*<div>*/}
                 {/*    <Button type={"primary"} onClick={() => {*/}
                 {/*        setAddPluginToolIsOpen(true);*/}
                 {/*    }}>*/}
                 {/*        <PlusOutlined/>创建工具</Button>*/}
                 {/*   </div>*/}
             </div>
            {/*<SearchForm columns={columnsSearchConfig} colSpan={6}*/}
            {/*            onSearch={(values: any) => {*/}
            {/*              doGetPage({*/}
            {/*                  params: {*/}
            {/*                      ...values,*/}
            {/*                      pluginId: id*/}
            {/*                  }*/}
            {/*              })*/}
            {/*            }}*/}
            {/*/>*/}
            <KeywordSearchForm tableAlias={"aiPluginTool"} columns={columnsSearchConfig} onSearch={(searchParams: any)=>{
                doGetPage({
                    params: {
                        ...searchParams,
                        pluginId: id
                    }
                })
            }} hiddenAddButton={true} customHandleButton=
                {  <div>
                <Button type={"primary"} onClick={() => {
                    setAddPluginToolIsOpen(true);
                }}>
                    <PlusOutlined/>创建工具</Button>
            </div>}/>
            <Table<DataType> columns={columns} dataSource={result?.data?.records} rowKey="id"
                             loading={loading}
                             pagination={{
                                 position: ['bottomCenter'],
                                 hideOnSinglePage: true,
                                 current: pagination.current,
                                 pageSize: pagination.pageSize,
                                 total: pagination.total,
                                 showSizeChanger: true, // 显示每页条数切换
                                 showTotal: (total) => `共 ${total} 条数据`
                             }}
                             onChange={handleTableChange}
            />
            <Modal title="创建工具" open={isAddPluginToolModalOpen} onOk={handleAddPluginToolOk}
                   onCancel={handleAddPluginToolCancel}
                   footer={null}
            >
                <Form
                    form={form}
                    name="basic"
                    layout="vertical"
                    style={{ maxWidth: 600 }}
                    initialValues={{ remember: true }}
                    onFinish={onFinish}
                    onFinishFailed={onFinishFailed}
                    autoComplete="off"
                >
                    <Form.Item<FieldType>
                        label="工具名称"
                        name="name"
                        rules={[{ required: true, pattern: /^[a-zA-Z0-9_-]+$/, message: '仅包含字母、数字、下划线或连字符的字符串' }]}
                    >

                        <Input maxLength={40} showCount placeholder={'请输入工具名称'}/>
                    </Form.Item>
                    <Form.Item<FieldType>
                        label="描述"
                        name="description"
                        rules={[{ required: true, message: '请输入工具描述' }]}
                    >
                        <TextArea
                            showCount
                            maxLength={500}
                            placeholder="请输入工具描述, 便于大模型更好理解并调用"
                            style={{ height: 80, resize: 'none' }}
                        />
                    </Form.Item>
                    <Form.Item label={null}>
                        <Space style={{ display: 'flex', justifyContent: 'flex-end' }} >
                            {/* 取消按钮 */}
                            <Button onClick={handleAddPluginToolCancel}>取消</Button>
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
    path: "/ai/pluginTool",
    element: PluginTool
};

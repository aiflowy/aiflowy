import React, {useEffect} from 'react';
import {Button, Form, FormProps, Input, message, Modal, Space, Table, TableProps, Tag} from 'antd';
import {useLocation, useNavigate} from "react-router-dom";
import {useLayout} from "../../../hooks/useLayout.tsx";
import {useBreadcrumbRightEl} from "../../../hooks/useBreadcrumbRightEl.tsx";
import {DeleteOutlined, EditOutlined, PlusOutlined} from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";
import {usePage, usePostManual} from "../../../hooks/useApis.ts";
import {convertDatetimeUtil} from "../../../libs/changeDatetimeUtil.tsx";

interface DataType {
    id: string;
    key: string;
    name: string;
    age: number;
    address: string;
    tags: string[];
}

const PluginTool: React.FC = () =>{
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
    useBreadcrumbRightEl(<Button type={"primary"} onClick={() => {
        setAddPluginToolIsOpen(true);
    }}>
        <PlusOutlined/>创建工具</Button>)
    const {
        loading,
        result,
        doGet: doGetPage
    } = usePage('aiPluginTool', {}, {manual: true})
    const {doPost: doPostSavePluginTool} = usePostManual('/api/v1/aiPluginTool/tool/save')
    useEffect(() => {

        setOptions({
            showBreadcrumb: true,
            breadcrumbs: [
                {title: '首页'},
                {title: '插件', href: `/ai/plugin`},
                {title: pluginTitle},
            ],
        })
        doGetPage({
            params: {
                pageNumber: 1,
                pageSize: 10,
            }
        })
        return () => {
            setOptions({
                showBreadcrumb: true,
                breadcrumbs: []
            })
        }
    }, [])


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
            key: 'name'
        },
        {
            title: '输入参数',
            dataIndex: 'inputParams',
            key: 'inputParams',
        },
        {
            title: '调试状态',
            dataIndex: 'debugStatus',
            key: 'debugStatus',
            render: (item: number) =>{
                if (item === 0){
                    return  <Tag color="error">失败</Tag>
                }  else if(item === 1){
                    return  <Tag color="success">成功</Tag>
                }

            }
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
                    <EditOutlined onClick={() =>{
                        navigate('/ai/pluginToolEdit', {
                            state: {
                                id: record.id,
                                // 插件名称
                                pluginTitle:pluginTitle,
                                // 插件工具名称
                                pluginToolTitle: record.name,
                                title: record.name
                            }
                        })
                    }}/>
                    <DeleteOutlined onClick={() =>{

                    }}/>
                </Space>
            ),
        },
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
                doGetPage({
                    params: {
                        pageNumber: 1,
                        pageSize: 10,
                    }
                })
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
    return (
        <div>
            <Table<DataType> columns={columns} dataSource={result?.data?.records} />
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
                        rules={[{ required: true, message: '请输入工具名称' }]}
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
                            placeholder="请输入工具描述"
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

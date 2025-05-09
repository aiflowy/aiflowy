import React, {useEffect, useState} from 'react';
import {useLayout} from "../../../hooks/useLayout.tsx";
import {useLocation, useNavigate} from "react-router-dom";
import {Button, Collapse, Form, Input, message, Select, Space, Spin, Switch, Table, Tooltip} from "antd";
import {usePost, usePostManual} from "../../../hooks/useApis.ts";
import './less/pluginToolEdit.less'
import {
    ArrowLeftOutlined,
    DeleteOutlined,
    EditOutlined,
    PlayCircleOutlined,
    PlusOutlined,
    QuestionCircleOutlined
} from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";
import {useBreadcrumbRightEl} from "../../../hooks/useBreadcrumbRightEl.tsx";

interface inputDataParameter {
    key: string;
    name: string;
    description: string;
    type: string;
    method: string;
    required: boolean;
    defaultValue: string;
    enabled: boolean;
}
interface outputDataParameter {
    key: string;
    name: string;
    description: string;
    type: string;
    required: boolean;
    enabled: boolean;
}


const PluginToolEdit: React.FC = () => {
    const navigate = useNavigate();

    useBreadcrumbRightEl(
        <div>
            <div>
                <button onClick={() => navigate(-1)}>返回</button>
                {/* 其他内容 */}
            </div>
        </div>
    )
    const { setOptions } = useLayout();
    const location = useLocation();
    const { id, pluginTitle, pluginToolTitle } = location.state || {};
    const { result: pluginToolInfo, doPost: doPostSearch } = usePost('/api/v1/aiPluginTool/tool/search');
    const {  doPost: doPostUpdate } = usePostManual('/api/v1/aiPluginTool/tool/update')
    const [showLoading, setShowLoading] = useState(true);
    const [isEditInput, setIsEditInput] = useState(false);
    const [isEditOutput, setIsEditOutput] = useState(false);
    const [formInput] = Form.useForm();
    const [formOutput] = Form.useForm();
    useBreadcrumbRightEl(
        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 16}}>
            <div>
                <Button onClick={() => navigate(-1)}  icon={<ArrowLeftOutlined />}>返回</Button>
                {/* 其他内容 */}
            </div>
            <Button
                type="primary"
                icon={<PlayCircleOutlined />}
            >
                试运行
            </Button>
        </div>
    )
    const [editStates, setEditStates] = useState({
        '1': false,
        '2': false,
        '3': false
    });
    const [formBasicInfo] = Form.useForm();

    useEffect(() => {
        setOptions({
            showBreadcrumb: true,
            breadcrumbs: [
                { title: '首页' },
                { title: '插件', href: `/ai/plugin` },
                { title: pluginTitle},
                { title: pluginToolTitle},
                { title: '修改' },
            ],
        });

        doPostSearch({
            data: {
                aiPluginToolId: id
            }
        }).then(() => {
            setTimeout(() => {
                setShowLoading(false);
            }, 1000);
        });

        return () => {
            setOptions({
                showBreadcrumb: true,
                breadcrumbs: []
            });
        };
    }, []);

    const toggleEdit = (index: string) => (event: React.MouseEvent) => {
        event.stopPropagation();
        setEditStates(prev => ({
            ...prev,
            [index]: !prev[index as keyof typeof prev]
        }));

    };

    const handleAdd = () => {
            const newData: inputDataParameter = {
                key: Date.now().toString(),
                name: '',
                description: '',
                type: 'String',
                method: 'Query',
                required: true,
                defaultValue: '',
                enabled: true,
            };
            setInputData([...inputData, newData]);

    };

    const handleAddOutputData = () => {
            const newData: outputDataParameter = {
                key: Date.now().toString(),
                name: '',
                description: '',
                type: 'String',
                required: true,
                enabled: true,
            };
            setOutputData([...outputData, newData]);
    };

    const handleDeleteInputData = (key: string) => {
        setInputData(inputData.filter((item) => item.key !== key));
    };
    const handleDeleteOutputData = (key: string) => {
        setOutputData(outputData.filter((item) => item.key !== key));
    };
    const [inputData, setInputData] = useState<inputDataParameter[]>([
    ]);
    const [outputData, setOutputData] = useState<outputDataParameter[]>([
    ]);
    useEffect(() => {
        if (pluginToolInfo?.data?.data?.inputData){
            setInputData(JSON.parse(pluginToolInfo?.data?.data?.inputData));
        }
        if(pluginToolInfo?.data?.data?.outputData){
            setOutputData(JSON.parse(pluginToolInfo?.data?.data?.outputData));
        }
    }, [pluginToolInfo]);

    const getColumnsOutput =()=>{
        const columnsOutput = [
            {
                title: (
                    <div style={{display: 'flex'}}>
                    <span>参数名称<span style={{ color: 'red', marginLeft: 4 }}>*</span></span>
                    <div  style={{marginLeft: 5}}>
                        <Tooltip title="当前工具返回的参数">
                            <QuestionCircleOutlined/>
                        </Tooltip>
                    </div>
                    </div>
                ),
                width: 150,
                dataIndex: 'name',
                key: 'name',
                render: (text: string, record: outputDataParameter) => {
                    return isEditOutput ? (
                        <Form.Item
                            className="tool-edit-item"
                            name={[record.key, 'name']}
                            initialValue={text}
                            rules={[{ required: true, message: '请输入参数名称' }]}
                        >
                            <Input placeholder="参数名称" />
                        </Form.Item>
                    ) : (
                        text
                    );
                },
            },
            {
                title: (
                    <div style={{display: 'flex'}}>
                        <span>参数描述<span style={{ color: 'red', marginLeft: 4 }}>*</span></span>
                        <div  style={{marginLeft: 5}}>
                            <Tooltip title="当前工具返回的参数描述">
                                <QuestionCircleOutlined/>
                            </Tooltip>
                        </div>
                    </div>
                ),
                dataIndex: 'description',
                key: 'description',
                render: (text: string, record: outputDataParameter) => {
                    return isEditOutput ? (
                        <Form.Item
                            className="tool-edit-item"
                            name={[record.key, 'description']}
                            initialValue={text}
                            rules={[{ required: true, message: '请输入参数描述' }]}
                        >
                            <Input placeholder="参数描述" />
                        </Form.Item>
                    ) : (
                        <Tooltip title={text}>
                            <div style={{
                                display: '-webkit-box',
                                WebkitLineClamp: 1,      // 限制显示行数
                                WebkitBoxOrient: 'vertical',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                            }}>
                                {text}
                            </div>
                        </Tooltip>
                    );
                },
            },
            {
                title: (
                    <span>参数类型<span style={{ color: 'red', marginLeft: 4 }}>*</span></span>
                ),
                width: 130,
                dataIndex: 'type',
                key: 'type',
                render: (text: string, record: outputDataParameter) => {
                    return isEditOutput ? (
                        <Form.Item name={[record.key, 'type']} initialValue={text} className="tool-edit-item">
                            <Select style={{ width: 120 }}>
                                <Select.Option value="String">String</Select.Option>
                                <Select.Option value="Number">Number</Select.Option>
                                <Select.Option value="Boolean">Boolean</Select.Option>
                                <Select.Option value="Object">Object</Select.Option>
                                <Select.Option value="Array">Array</Select.Option>
                            </Select>
                        </Form.Item>
                    ) : (
                        text
                    );
                },
            },
            {
                title: '开启',
                width: 80,
                dataIndex: 'enabled',
                key: 'enabled',
                render: (text: boolean, record: outputDataParameter) => {
                    return isEditOutput ? (
                        <Form.Item  name={[record.key, 'enabled']} initialValue={text} valuePropName="checked" className="tool-edit-item">
                            <Switch />
                        </Form.Item>
                    ) : (
                        <Switch checked={text} disabled />
                    );
                },
            },
            {
                title: '操作',
                key: 'operation',
                render: (_: any, record: outputDataParameter) => (
                    <Form.Item className="tool-edit-item">
                        <Button
                            type="text"
                            icon={<DeleteOutlined />}
                            onClick={() => handleDeleteOutputData(record.key)}
                        />
                    </Form.Item>

                ),
            },
        ];
        return columnsOutput.filter(col => col.key !== 'operation' || isEditOutput);
    }
    const getColumnsInput = ()=>{
        const columnsInput = [
            {
                title: (
                    <div style={{display: 'flex'}}>
                        <span>参数名称<span style={{ color: 'red', marginLeft: 4 }}>*</span></span>
                        <div  style={{marginLeft: 5}}>
                            <Tooltip title="当前工具请求的参数名称">
                                <QuestionCircleOutlined/>
                            </Tooltip>
                        </div>
                    </div>
                ),
                width: 150,
                dataIndex: 'name',
                key: 'name',
                render: (text: string, record: inputDataParameter) => {
                    return isEditInput ? (
                        <Form.Item
                            className="tool-edit-item"
                            name={[record.key, 'name']}
                            initialValue={text}
                            rules={[{ required: true, message: '请输入参数名称' }]}
                        >
                            <Input placeholder="参数名称" />
                        </Form.Item>
                    ) : (
                        text
                    );
                },
            },
            {
                title: (
                    <div style={{display: 'flex'}}>
                        <span>参数描述<span style={{ color: 'red', marginLeft: 4 }}>*</span></span>
                        <div  style={{marginLeft: 5}}>
                            <Tooltip title="请描述参数的功能，帮助用户/大模型更好的理解。">
                                <QuestionCircleOutlined/>
                            </Tooltip>
                        </div>
                    </div>
                ),
                dataIndex: 'description',
                key: 'description',
                render: (text: string, record: inputDataParameter) => {
                    return isEditInput ? (
                        <Form.Item
                            className="tool-edit-item"
                            name={[record.key, 'description']}
                            initialValue={text}
                            rules={[{ required: true, message: '请输入参数描述' }]}
                        >
                            <Input placeholder="参数描述" />
                        </Form.Item>
                    ) : (
                        <Tooltip title={text}>
                            <div style={{
                                display: '-webkit-box',
                                WebkitLineClamp: 1,      // 限制显示行数
                                WebkitBoxOrient: 'vertical',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                            }}>
                                {text}
                            </div>
                        </Tooltip>
                    );
                },
            },
            {
                title: (
                    <span>参数类型<span style={{ color: 'red', marginLeft: 4 }}>*</span></span>
                ),
                width: 130,
                dataIndex: 'type',
                key: 'type',
                render: (text: string, record: inputDataParameter) => {
                    return isEditInput ? (
                        <Form.Item name={[record.key, 'type']} initialValue={text} className="tool-edit-item">
                            <Select style={{ width: 120 }}>
                                <Select.Option value="String">String</Select.Option>
                                <Select.Option value="Number">Number</Select.Option>
                                <Select.Option value="Boolean">Boolean</Select.Option>
                                <Select.Option value="Object">Object</Select.Option>
                                <Select.Option value="Array">Array</Select.Option>
                            </Select>
                        </Form.Item>
                    ) : (
                        text
                    );
                },
            },
            {
                title: (
                    <span>传入方法<span style={{ color: 'red', marginLeft: 4 }}>*</span></span>
                ),
                width: 130,
                dataIndex: 'method',
                key: 'method',
                render: (text: string, record: inputDataParameter) => {
                    return isEditInput ? (
                        <Form.Item name={[record.key, 'method']} initialValue={text} className="tool-edit-item">
                            <Select style={{ width: 120 }}>
                                <Select.Option value="Query">Query</Select.Option>
                                <Select.Option value="Body">Body</Select.Option>
                                <Select.Option value="Header">Header</Select.Option>
                                <Select.Option value="Path">Path</Select.Option>
                            </Select>
                        </Form.Item>
                    ) : (
                        text
                    );
                },
            },
            {
                title: '是否必须',
                dataIndex: 'required',
                width: 90,
                key: 'required',
                render: (text: boolean, record: inputDataParameter) => {
                    return isEditInput ? (
                        <Form.Item name={[record.key, 'required']} initialValue={text} valuePropName="checked" className="tool-edit-item">
                            <Switch />
                        </Form.Item>
                    ) : (
                        <Switch checked={text} disabled />
                    );
                },
            },
            {
                title: '默认值',
                width: 100,
                dataIndex: 'defaultValue',
                key: 'defaultValue',
                render: (text: string, record: inputDataParameter) => {
                    return isEditInput ? (
                        <Form.Item name={[record.key, 'defaultValue']} initialValue={text} className="tool-edit-item">
                            <Input placeholder="默认值" size="small" />
                        </Form.Item>
                    ) : (
                        text
                    );
                },
            },
            {
                title: '开启',
                width: 80,
                dataIndex: 'enabled',
                key: 'enabled',
                render: (text: boolean, record: inputDataParameter) => {
                    return isEditInput ? (
                        <Form.Item name={[record.key, 'enabled']} initialValue={text} valuePropName="checked" className="tool-edit-item">
                            <Switch />
                        </Form.Item>
                    ) : (
                        <Switch checked={text} disabled />
                    );
                },
            },
            {
                title: '操作',
                key: 'operation',
                render: (_: any, record: inputDataParameter) => (
                    <Button
                        type="text"
                        icon={<DeleteOutlined />}
                        onClick={() => handleDeleteInputData(record.key)}
                    />
                ),
            },
        ];

        return columnsInput.filter(col => col.key !== 'operation' || isEditInput);
    }
    const editPluginTool = (index: string) => {
        // 可以在函数顶部添加条件判断
        if (!index) {
            return null; // 或者返回其他占位元素
        }

        const isEditInput = editStates[index as keyof typeof editStates];

        return (
            <div
                style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
                onClick={toggleEdit(index)}
            >
            <span>
                {isEditInput ? (
                    <div style={{ display: 'flex', gap: '8px' }}>
                        <Button type="primary" size="small" onClick={() => {
                            if (index === '2') {
                                setIsEditInput(false);
                            } else if (index === '3') {
                                setIsEditOutput(false);
                            }
                        }}>取消</Button>
                        <Button
                            type="primary"
                            size="small"
                            onClick={(e) => {
                                e.stopPropagation();
                                formBasicInfo.validateFields().then((values) => {
                                    if (index === '1') {
                                        doPostUpdate({
                                            data: {
                                                id: values.id,
                                                name: values.name,
                                                description: values.description,
                                                basePath: values.basePath,
                                                requestMethod: values.requestMethod
                                            }
                                        }).then((r) => {
                                            if (r?.data?.errorCode === 0) {
                                                message.success('修改成功');
                                                setEditStates(prev => ({...prev, '1': false})); // 添加这行
                                                doPostSearch({
                                                    data: {
                                                        aiPluginToolId: id
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else if (index === '2') {
                                        formInput.validateFields().then(() => {
                                            const formData = formInput.getFieldsValue();
                                            doPostUpdate({
                                                data: {
                                                    id: id,
                                                    inputData: JSON.stringify(formData),
                                                }
                                            }).then((r) => {
                                                if (r?.data?.errorCode === 0) {
                                                    setIsEditInput(false);
                                                    setEditStates(prev => ({...prev, '2': false})); // 添加这行
                                                    message.success('修改成功');
                                                    doPostSearch({
                                                        data: {
                                                            aiPluginToolId: id
                                                        }
                                                    });
                                                }
                                            })
                                        });

                                    }
                                    // 保存输出参数
                                    else if (index === '3') {
                                        formOutput.validateFields().then(() => {
                                            const formData = formOutput.getFieldsValue();
                                            doPostUpdate({
                                                data: {
                                                    id: id,
                                                    outputData: JSON.stringify(formData),
                                                }
                                            }).then((r) => {
                                                if (r?.data?.errorCode === 0) {
                                                    setIsEditOutput(false);
                                                    setEditStates(prev => ({...prev, '3': false})); // 添加这行
                                                    message.success('修改成功');
                                                    doPostSearch({
                                                        data: {
                                                            aiPluginToolId: id
                                                        }
                                                    });
                                                }
                                            })
                                        });
                                    }
                                });
                            }}
                        >
                            保存
                        </Button>
                    </div>
                ) : (
                    <div style={{ display: 'flex', gap: '8px' }} onClick={()=>{

                        if (index === '1' && !editStates['1']) {
                            formBasicInfo.setFieldsValue({
                                id: pluginToolInfo?.data?.data?.id,
                                name: pluginToolInfo?.data?.data?.name,
                                description: pluginToolInfo?.data?.data?.description,
                                basePath: pluginToolInfo?.data?.data?.basePath ? pluginToolInfo?.data?.data?.basePath : `/${pluginToolInfo?.data?.data?.name}`,
                                baseUrl: pluginToolInfo?.data?.aiPlugin?.baseUrl,
                                requestMethod: pluginToolInfo?.data?.data?.requestMethod,
                            });
                        }else if (index === '2') {
                            setIsEditInput(true);
                        } else if (index === '3'){
                            setIsEditOutput(true);
                        }
                    }}>
                        <EditOutlined />
                        <span>编辑</span>
                    </div>
                )}
            </span>
            </div>
        );
    };

    const collapseItems = [
        {
            key: '1',
            label: '基本信息',
            children: editStates['1'] ? (
                <Form
                    form={formBasicInfo}
                    variant="filled"
                    layout="vertical"
                    className="basic-info-update"
                >
                    <Form.Item label="id" name="id" hidden rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item label="工具名称" name="name" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item label="工具描述" name="description" rules={[{ required: true }]}>
                        <TextArea style={{ height: 80, resize: 'none' }} />
                    </Form.Item>
                    <Form.Item label="工具路径" name="basePath" rules={[{ required: true }]}>
                        <Input addonBefore={formBasicInfo.getFieldValue('baseUrl')} />
                    </Form.Item>
                    <Form.Item label="请求方法" name="requestMethod" rules={[{ required: true }]}>
                        <Select
                            // onChange={(value) => {
                            //     setAuthType(value)
                            // }}
                            options={[
                                { value: 'Post', label: 'Post' }
                                , { value: 'Get', label: 'Get' }
                                , { value: 'Delete', label: 'Delete' }
                                , { value: 'Put', label: 'Put' }
                                , { value: 'Patch', label: 'Patch' }]}/>
                    </Form.Item>
                </Form>
            ) : (
                <div className="compact-view">
                    <div><strong>工具名称:</strong> {pluginToolInfo?.data?.data?.name || '--'}</div>
                    <div><strong>工具描述:</strong>{pluginToolInfo?.data?.data?.description || '--'}</div>
                    <div>
                        <strong>工具路径:</strong>
                        {pluginToolInfo?.data?.aiPlugin?.baseUrl
                            && pluginToolInfo?.data?.data?.basePath ?
                            pluginToolInfo?.data?.aiPlugin?.baseUrl+pluginToolInfo?.data?.data?.basePath
                            :
                            pluginToolInfo?.data?.aiPlugin?.baseUrl+'/'+pluginToolInfo?.data?.data?.name
                        }
                    </div>
                    <div><strong>请求方法:</strong> {pluginToolInfo?.data?.data?.requestMethod || '--'}</div>
                </div>
            ),
            extra: editPluginTool('1')
        },
        {
            key: '2',
            label: '配置输入参数',
            children:  (
                <div>
                    <Form form={formInput} component={false} >
                        <Table
                            className="custom-table"
                            bordered
                            dataSource={inputData || []}
                            columns={getColumnsInput()}
                            pagination={false}
                            rowKey="key"
                        />
                    </Form>
                    <Space style={{ marginTop: 16 }}>
                        {
                            isEditInput ? ( <Button
                                    type="dashed"
                                    icon={<PlusOutlined />}
                                    onClick={handleAdd}
                                >
                                    新增参数
                                </Button>
                            ) : (<></>
                            )
                        }

                    </Space>
                </div>
            ),
            extra: editPluginTool('2')
        },
        {
            key: '3',
            label: '配置输出参数',
            children: (
                <div>
                    <Form form={formOutput} component={false} >
                        <Table
                            className="custom-table"
                            bordered
                            dataSource={outputData || []}
                            columns={getColumnsOutput()}
                            pagination={false}
                            rowKey="key"
                        />
                    </Form>
                    <Space style={{ marginTop: 16 }}>
                        {
                            isEditOutput ? ( <Button
                                    type="dashed"
                                    icon={<PlusOutlined />}
                                    onClick={handleAddOutputData}
                                >
                                    新增参数
                                </Button>
                            ) : (<></>
                            )
                        }

                    </Space>
                </div>
            ),
            extra: editPluginTool('3')
        },
    ];

    if (showLoading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Spin />
            </div>
        );
    }

    return (
        <div style={{ backgroundColor: '#F5F5F5',height: '100vh', overflow: 'auto' }}>
            <Collapse
                bordered={false}
                defaultActiveKey={['1', '2', '3']}
                items={collapseItems.map(item => ({
                    ...item,
                    style: {
                        header: { backgroundColor: '#F7F7FA' },
                        body: { backgroundColor: '#F5F5F5' },
                    },
                }))}
            />
        </div>
    );
};

export default {
    path: "/ai/pluginToolEdit",
    element: PluginToolEdit
};
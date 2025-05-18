import React, {useEffect, useRef, useState} from 'react';
import {useLayout} from "../../../hooks/useLayout.tsx";
import {useLocation, useNavigate} from "react-router-dom";
import {Button, Collapse, Form, Input, message, Select, Spin} from "antd";
import {usePost, usePostManual} from "../../../hooks/useApis.ts";
import './less/pluginToolEdit.less'
import {
    ArrowLeftOutlined,
    EditOutlined,
} from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";
import {useBreadcrumbRightEl} from "../../../hooks/useBreadcrumbRightEl.tsx";
import PluginInputAndOutputData, {TreeTableNode} from "./PluginInputAndOutputData.tsx";


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
    const pluginRef = useRef(null);
    const [showLoading, setShowLoading] = useState(true);
    const [isEditInput, setIsEditInput] = useState(false);
    const [isEditOutput, setIsEditOutput] = useState(false);
    useBreadcrumbRightEl(
        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 16}}>
            <div>
                <Button onClick={() => navigate(-1)}  icon={<ArrowLeftOutlined />}>返回</Button>
                {/* 其他内容 */}
            </div>
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

    const [inputDataTree, setInputDataTree] = useState<TreeTableNode[]>([
    ]);
    const [outputDataTree, setOutputDataTree] = useState<TreeTableNode[]>([
    ]);
    useEffect(() => {
        if (pluginToolInfo?.data?.data?.inputData){
            setInputDataTree(JSON.parse(pluginToolInfo?.data?.data?.inputData))
        }
        if(pluginToolInfo?.data?.data?.outputData){
            setOutputDataTree(JSON.parse(pluginToolInfo?.data?.data?.outputData));
        }
    }, [pluginToolInfo]);

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
                            doPostSearch({
                                data: {
                                    aiPluginToolId: id
                                }
                            }).then(res => {
                                setInputDataTree(JSON.parse(res?.data?.data?.data?.inputData))
                                setOutputDataTree(JSON.parse(res?.data?.data?.data?.outputData));
                            })
                        }}>取消</Button>
                        <Button
                            type="primary"
                            size="small"
                            onClick={(e) => {
                                e.stopPropagation();

                                    if (index === '1') {
                                        formBasicInfo.validateFields().then((values) => {
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
                                    });
                                    }
                                    else if (index === '2' || index === '3') {
                                        //@ts-ignore
                                        if (pluginRef.current && pluginRef.current.handleSubmitParams) {
                                            //@ts-ignore
                                            pluginRef.current.handleSubmitParams(); // 主动触发子组件提交
                                        }
                                    }

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
    const handleSubmit = (submittedParams: TreeTableNode[]) => {
        console.log('isEditInput')
        console.log(isEditInput)
        console.log('isEditOutput')
        console.log(isEditOutput)
        console.log('父组件收到的参数:', submittedParams);
        if (isEditInput){
            setIsEditInput(false)
                doPostUpdate({
                    data: {
                        id: id,
                        inputData: JSON.stringify(submittedParams),
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
        } else {
            setIsEditOutput(false)
                    doPostUpdate({
                        data: {
                            id: id,
                            outputData: JSON.stringify(submittedParams),
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
        }

        // 这里可以执行提交到API等操作
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
                        <PluginInputAndOutputData
                        value={inputDataTree}
                        editable={isEditInput}
                        ref={pluginRef}
                        onSubmit={handleSubmit}
                        />
            ),
            extra: editPluginTool('2')
        },
        {
            key: '3',
            label: '配置输出参数',
            children: (
                <PluginInputAndOutputData
                    value={outputDataTree}
                    editable={isEditOutput}
                    ref={pluginRef}
                    onSubmit={handleSubmit}
                    isEditOutput={true}
                />
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

import React, {useState} from 'react';
import {Button, Form, Input, Modal,  Space, Table, App} from 'antd';
import {useGet, usePostManual} from '../../hooks/useApis'; // 确保路径正确
import ModelInstallPage from '../ai/ModelInstallPage'
import {RestOutlined} from "@ant-design/icons";
import {isSupportChat, isSupportEmbed, isSupportFunctionCalling} from "../../libs/modelUtils";

type LayoutType = Parameters<typeof Form>[0]['layout'];
type ParaType = {
    modelName?: string;
};
const Ollama: React.FC = () => {
    const {message} = App.useApp();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isFinish, setIsFinish] = useState<boolean>(false); // 是否正在安装

    const [pagination, setPagination] = useState({
        current: 1, // 当前页码
        pageSize: 10, // 每页显示条数
        total: 0, // 总记录数
    });
    const {loading, result, doGet} = useGet('/api/v1/ollama/list', {
        current:pagination.current,
        pageSize:pagination.pageSize
    });

    const {doPost: doAddAiLlm} = usePostManual("/api/v1/aiLlm/addAiLlm")
    // 处理行操作按钮点击事件
    const handleRowAction = (record:any) => {
        doAddAiLlm({
            params:{
                title: 'ollama_'+record.name,
                llmEndpoint: record.apiUrl,
                llmModel: record.name,
                supportEmbed: isSupportEmbed(record.name),
                supportChat: isSupportChat(record.name),
                supportFunctionCalling: isSupportFunctionCalling(record.name),
                brand: 'ollama'
            }
        }).then(resp => {
            if (resp.data.errorCode == 0) {
                doGet()
            }
        })
    };
    //删除大模型操作
    function handleDeleteAction(record: any) {
        Modal.confirm({
            title: '确认删除?',
            content: '该操作会删除本地大模型',
            okText: '确定',
            cancelText: '取消',
            onOk: () => {
                doGet({
                    url: '/api/v1/ollama/deleteModel',
                    params: {
                        modelName: record.name
                    }
                }).then(res => {
                    if  (res.data.errorCode === 0){
                        message.error('删除成功')
                        doGet({
                            params: pagination
                        })
                    }

                })
            },
        });

    }

    const columns = [
        {
            title: '模型名称',
            dataIndex: 'name',
            key: 'name'
        },
        {
            title: '大小',
            dataIndex: 'sizeString',
            key: 'sizeString',
        },
        {
            title: '创建时间',
            dataIndex: 'modified_at',
            key: 'modified_at',
        },
        {
            title: '操作',
            dataIndex: 'operationType',
            key: 'operationType',
            render: (_:any, record:any) => (
                <Space>
                    <Button
                        type={"link"} // 根据状态设置按钮类型
                        onClick={() => handleRowAction(record)} // 点击事件
                        disabled={record.hasJoinModel === 1} // 如果已加入，禁用按钮
                        size={'small'}
                    >
                        {record.hasJoinModel === 1 ? "已加入模型管理" : "加入模型管理"} {/* 动态显示文本 */}
                    </Button>
                        <a onClick={() => handleDeleteAction(record)}> <RestOutlined/> 删除 </a>
                </Space>
            ),
        },
    ];
    // 定义状态
    const [inputValue, setInputValue] = useState('');


    // 分页变化时触发
    const handleTableChange = (newPagination:any) => {
        const { current, pageSize } = newPagination;
        doGet().then(res => {
            setPagination({
                current: current, // 当前页码
                pageSize: pageSize, // 每页显示条数
                total: res.data.data.totalRow, // 总记录数
            })
            if(result){
                result.records = res.data.records
            }

        })
    };
    const [form] = Form.useForm();
    const [formLayout, setFormLayout] = useState<LayoutType>('horizontal');
    const [isVisable, setIsVisable] = useState(false);


    const [para, setPara] = useState<ParaType>();
    // 定义状态
    const [messageFromChild] = useState<boolean>(true);

    // 点击下载模型Ollama 按钮弹框
    const showModal = () => {
        setIsModalOpen(true);
    };
    // 点击下载模型Ollama 确定
    const handleOk = () => {
        // 如果选择的操作是安装
        setIsFinish(false)
        form.submit()
    };
    // 点击下载模型Ollama 取消
    const handleCancel = () => {
        setIsModalOpen(false);
        // setIsVisable(false)
        form.resetFields()
    };
// 处理输入框变化
    const handleChange = (e: any) => {
        setInputValue(e.target.value); // 更新状态
    };


    const onFormLayoutChange = ({ layout }: { layout: LayoutType }) => {
        setFormLayout(layout);
    };
    // 父组件的方法，用于接收子组件传递的参数
    const handleMessageFromChild = (message: boolean) => {
        if (message === true  && !isFinish) {
            setIsFinish(prevIsFinish => {
                if (!prevIsFinish) { // 只有在 prevIsFinish 为 false 时才执行
                    setTimeout(() => {
                        // 三秒后安装进度条消失
                        setIsVisable(false);
                        doGet({
                            params: pagination
                        });
                    }, 3000);

                    return true; // 更新 isFinish 为 true
                }
                return prevIsFinish; // 如果已经是 true，则保持不变
            });
        }
    };
    const onFinish = () =>{
        let value = JSON.parse(JSON.stringify(form.getFieldsValue()));
        setIsVisable(true)
        setPara(value)
        setIsModalOpen(false)
        form.resetFields()
        if (value.operation == 'install'){
            setIsVisable(true)
            setPara(value)
            setIsModalOpen(false)
            form.resetFields()
        } else if (value.operation == 'remove')
        {
            doGet({
                url: '/api/v1/ollama/deleteModel',
                params: {
                    modelApiUrl: value.modelApiUrl,
                    modelName: value.modelName
                }
            }).then(() => {
                setIsModalOpen(false);
                form.resetFields()
            })


        }
    }
    return <div style={{padding: '24px', width: '100%'}}>
        <div style={{width: '100%', marginBottom: '24px', display: 'flex', alignItems: 'center',gap: '20px', height: '40px'}}>
            <Space.Compact >
                <div style={{display: 'flex', alignItems: 'center' }}>
                    <p style={{width: '50px', margin: 0 }}>名称: &nbsp;</p> <Input placeholder="请输入模型名称" value={inputValue} onChange={handleChange}/>
                </div>

            </Space.Compact>
            <div style={{width: '100px', display: 'flex', alignItems: 'center'}}>
                <div>
                    <Button type="primary" shape='default' size='middle' onClick={() => {
                        doGet({
                            params: {
                                'apiUrl': inputValue
                            }
                        }).then(() => {
                        })
                    }
                    }>
                        搜索
                    </Button>
                </div>
                <div style={{marginLeft: '10px'}}>
                    <Button type="primary" size='middle' onClick={showModal}>
                        下载模型
                    </Button>
                </div>

            </div>
        </div>
        {isVisable  && messageFromChild && <ModelInstallPage  modelName={para?.modelName!} onSendMessage={handleMessageFromChild}/>}
        <Table columns={columns} dataSource={Array.isArray(result?.data?.records) ? result.data.records : []} loading={loading} rowKey="name"
               pagination={{
                   current: pagination.current,
                   pageSize: pagination.pageSize,
                   total: pagination.total,
                   showSizeChanger: true, // 显示每页条数切换
                   showTotal: (total) => `共 ${total} 条数据`
               }}
               onChange={handleTableChange}
        />
        <Modal title="模型安装" width="30%" open={isModalOpen} onOk={handleOk} onCancel={handleCancel} maskClosable={false}
               okText='安装'>
            <Form
                layout={'horizontal'}
                form={form}
                size = {'middle'}
                initialValues={{
                    modelApiUrl: '', // 默认 Ollama 地址为空
                    modelName: '',  // 默认模型名称为空
                    operation: 'install', // 默认操作为 "install"
                }}
                onValuesChange={onFormLayoutChange}
                onFinish={onFinish}
                style={{ maxWidth: formLayout === 'inline' ? 'none' : 600 }}
            >
                <Form.Item label="模型名称&nbsp;&nbsp;&nbsp;&nbsp;" name="modelName" rules={[{ required: true, message: '请输入模型名称!' }]}>
                    <Input  />
                </Form.Item>
            </Form>
        </Modal>
    </div>;
};

export default {
    path: "/ai/ollama",
    element: Ollama
};

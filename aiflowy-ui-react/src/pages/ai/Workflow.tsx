import React, {useRef, useState} from 'react';
import {
    DownloadOutlined,
    NodeIndexOutlined, PlayCircleOutlined, UploadOutlined,
} from "@ant-design/icons";
import CardPage from "../../components/CardPage";
import {ColumnsConfig} from "../../components/AntdCrud";
import {Button, Form, Input, MenuProps, message, Modal, Space, Spin, Upload} from "antd";
import TextArea from "antd/es/input/TextArea";
import {useGetManual, usePostFile} from "../../hooks/useApis.ts";
import {useCheckPermission} from "../../hooks/usePermissions.tsx";

const columnsColumns: ColumnsConfig<any> = [
    {
        key: 'id',
        hidden: true,
        form: {
            type: "hidden"
        }
    },
    {
        title: 'Icon',
        dataIndex: 'icon',
        key: 'icon',
        form: {
            type: "image",
        }
    },
    {
        title: '名称',
        dataIndex: 'title',
        key: 'title',
        placeholder: "请输入工作流名称",
        supportSearch: true,
        form: {
            rules: [{required: true, message: '请输入工作流名称'}]
        }
    },
    {
        title: '英文名称',
        dataIndex: 'englishName',
        key: 'englishName',
        placeholder: "请输入英文名称, 不允许含中文",
        supportSearch: true,
        form: {
            rules: [{required: true, message: '必须由 a-z、A-Z、0-9 字符组成，或包含下划线和连字符，最大长度为 64 个字符', pattern: /^[a-zA-Z0-9_-]{1,64}$/}]
        }
    },
    {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        width: "50%",
        form: {
            type: "TextArea",
            attrs: {
                rows: 3,
                placeholder: "请输入工作流描述, 便于大模型更好理解并调用"
            },
            rules: [{required: true, message: '请输入工作流描述'}]
        }
    },

];


const Workflow: React.FC<{ paramsToUrl: boolean }> = () => {

    const cardPageRef = useRef<any>(null);
    const [importForm] = Form.useForm();
    const [isModalOpen, setIsModalOpen] = useState(false)
    const {loading: confirmLoading,doPost: postFile} = usePostFile("/api/v1/aiWorkflow/importWorkFlow")
    const {loading: exportLoading,doGet: getContent} = useGetManual('/api/v1/aiWorkflow/exportWorkFlow')
    const handleOk = () => {
        importForm.validateFields().then((values:any) => {
            const formData = new FormData();
            formData.append('jsonFile', values.jsonFile.file);
            formData.append("title", values.title)
            formData.append("description", values.description??"")
            postFile({
                data:  formData
            }).then((res) => {
                if (res.data.errorCode === 0) {
                    handleCancel()
                    message.success(`导入成功`);
                    if (cardPageRef.current) {
                        cardPageRef.current.refresh();
                    }
                } else {
                    message.error(`导入失败`);
                }
            })
        }).catch(err => {
            console.log(err)
        })
    }
    const handleCancel = () => {
        importForm.resetFields()
        setIsModalOpen(false)
    }

    const beforeUpload = () => {
        // 返回 false 阻止自动上传
        return false;
    };

    function exportWorkflow(item: any) {
        const filename = item.title + ".json";
        getContent({
            params: {
                id: item.id
            }
        }).then(res => {
            const text = res.data.content;
            const element = document.createElement('a');
            element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
            element.setAttribute('download', filename);
            element.style.display = 'none';
            document.body.appendChild(element);
            element.click();
            document.body.removeChild(element);
            message.success(`导出成功，请等待下载`);
        })
    }

    const canSave = useCheckPermission("/api/v1/aiWorkflow/save")

    const getCustomActions = (item: any) => {
        const arr = []
        if (canSave) {
            arr.push(<Space onClick={() => {
                window.open(`/ai/workflow/design/${item.id}`, "_blank")
            }}>
                <NodeIndexOutlined title="设计工作流"/>
                <span>设计</span>
            </Space>)
            arr.push(<Space onClick={() => {
                exportWorkflow(item)
            }}>
                <DownloadOutlined title="导出工作流"  />
                <span>导出</span>
            </Space>)
        }
        return arr;
    }

    const menuItems: MenuProps['items'] = [
        ...(canSave ? [{
            key: 'import-workflow',
            label: (
                <Space onClick={() => setIsModalOpen(true)}>
                    <UploadOutlined />
                    <span>导入工作流</span>
                </Space>
            ),
        }] : [])
    ];

    return (
        <>
            <Modal title="导入工作流"
                   open={isModalOpen}
                   onOk={handleOk}
                   onCancel={handleCancel}
                   confirmLoading={confirmLoading}
            >
                <Form
                    form={importForm}
                    name="basic"
                    labelCol={{ span: 5 }}
                    wrapperCol={{ span: 16 }}
                    autoComplete="off"
                >
                    <Form.Item
                        label="名称"
                        name="title"
                        rules={[{ required: true, message: '请输入工作流名称' }]}
                    >
                        <Input />
                    </Form.Item>
                    <Form.Item
                        label="描述"
                        name="description"
                    >
                        <TextArea rows={4} />
                    </Form.Item>
                    <Form.Item
                        label="文件"
                        name="jsonFile"
                        rules={[{ required: true, message: '请选择工作流文件' }]}
                    >
                        <Upload
                            name="jsonFile"
                            beforeUpload={beforeUpload}
                            maxCount={1}
                            accept=".json" // 可以指定接受的文件类型
                        >
                            <Button icon={<UploadOutlined />}>选择文件</Button>
                        </Upload>
                    </Form.Item>
                </Form>
            </Modal>
            <Spin spinning={exportLoading}>
                <CardPage ref={cardPageRef}
                          tableAlias={"aiWorkflow"}
                          editModalTitle={"新增/编辑工作流"}
                          columnsConfig={columnsColumns}
                          addButtonText={"新增工作流"}
                          defaultAvatarSrc={"/src/assets/workflowIcon.png"}
                          editLayout={{labelWidth: 140}}
                          optionsText={{
                              addCardTitle: "创建工作流",
                              noDataText: "你还没有工作流，快来创建你的工作流吧!",
                              noDataAddButtonText: "创建工作流"
                          }}
                          customMenuItems={menuItems}
                          customActions={(item, existNodes) => {
                              return [
                                  ...getCustomActions(item),
                                  <Space onClick={() => {
                                      window.open(window.location.href.substring(0, window.location.href.indexOf('/ai')) + '/ai/workflow/external/' + item.id, "_blank")
                                  }}>
                                      <PlayCircleOutlined title="外部地址"/>
                                      <span>运行</span>
                                  </Space>
                                 ,
                                  ...existNodes
                              ]
                          }}
                          // customHandleButton={() => {
                          //     return [
                          //         ...getCustomHandleButton()
                          //     ]
                          // }}
                />
            </Spin>
        </>
    )
};

export default {
    path: "/ai/workflow",
    element: Workflow
};

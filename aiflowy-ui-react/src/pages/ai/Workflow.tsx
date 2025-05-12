import React, {useRef, useState} from 'react';
import {
    DownloadOutlined,
    NodeIndexOutlined, SendOutlined, UploadOutlined,
} from "@ant-design/icons";
import CardPage from "../../components/CardPage";
import {ColumnsConfig} from "../../components/AntdCrud";
import {Button, Form, Input, message, Modal, Upload} from "antd";
import TextArea from "antd/es/input/TextArea";
import {usePostFile} from "../../hooks/useApis.ts";

const columnsColumns: ColumnsConfig<any> = [
    {
        key: 'id',
        hidden: true,
        form: {
            type: "hidden"
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
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        width: "50%",
        form: {
            type: "TextArea",
            attrs: {
                rows: 3
            }
        }
    },

];


const Workflow: React.FC<{ paramsToUrl: boolean }> = () => {

    const cardPageRef = useRef<any>(null);
    function exportWorkflow(item: any) {
        const filename = item.title + ".json";
        const text= item.content;
        const element = document.createElement('a');
        element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
        element.setAttribute('download', filename);
        element.style.display = 'none';
        document.body.appendChild(element);
        element.click();
        document.body.removeChild(element);
        message.success(`导出成功，请等待下载`);
    }
    const [importForm] = Form.useForm();
    const [isModalOpen, setIsModalOpen] = useState(false)
    const {loading: confirmLoading,doPost: postFile} = usePostFile("/api/v1/aiWorkflow/importWorkFlow")
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
            <CardPage ref={cardPageRef}
                      tableAlias={"aiWorkflow"}
                      editModalTitle={"新增/编辑工作流"}
                      columnsConfig={columnsColumns}
                      addButtonText={"新增工作流"}
                      defaultAvatarSrc={"/favicon.png"}
                      editLayout={{labelWidth: 140}}
                      customActions={(item, existNodes) => {
                          return [
                              <NodeIndexOutlined title="设计工作流" onClick={() => {
                                  window.open(`/ai/workflow/design/${item.id}`, "_blank")
                              }}/>,
                              <DownloadOutlined title="导出工作流" onClick={() => {
                                  exportWorkflow(item)
                              }} />,
                              <SendOutlined title="外部地址" onClick={() => {
                                  window.open(window.location.href.substring(0, window.location.href.indexOf('/ai')) + '/ai/workflow/external/' + item.id, "_blank")
                              }}/>,
                              ...existNodes
                          ]
                      }}
                      customHandleButton={() => {
                          return [
                              <Button type={"primary"} onClick={() => {setIsModalOpen(true)}}><UploadOutlined />导入工作流</Button>,
                          ]
                      }}
            />
        </>
    )
};

export default {
    path: "/ai/workflow",
    element: Workflow
};

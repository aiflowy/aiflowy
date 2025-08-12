import React, {useEffect, useState} from "react"
import {Button, Form, Input, Radio, Checkbox, message} from "antd";
import {SendOutlined, UploadOutlined} from "@ant-design/icons";
import {Uploader} from "../../../../components/Uploader";

export type WorkflowFormProps = {
    workflowInfo: any,
    submitLoading: boolean,
    onExecute?: (values: any) => void
}

export const WorkflowForm: React.FC<WorkflowFormProps> = (props) => {

    const {workflowInfo, submitLoading, onExecute} = props
    const [workflowParams, setWorkflowParams] = useState([])

    useEffect(() => {
        setWorkflowParams(workflowInfo?.parameters)
    }, [workflowInfo])

    const onFinish = (values: any) => {
        onExecute && onExecute(values)
    };

    const onFinishFailed = (errorInfo: any) => {
        if (errorInfo.errorFields) {
            message.warning("请完善表单内容")
        } else {
            message.error("失败：" + errorInfo)
        }
    };

    const [paramForm] = Form.useForm();

    const formComponent = (item: any) => {

        const contentType = item.contentType ? item.contentType : "text"

        let checkBoxOptions = []
        if (item.formType === "checkbox") {
            checkBoxOptions = item.enums.map((option: any) => {
                return {
                    label: option,
                    value: option
                }
            })
        }

        return (
            <>
                {contentType === "text" &&
                    <>
                        {item.formType === "input" &&
                            <Input/>
                        }
                        {item.formType === "textarea" &&
                            <Input.TextArea rows={4}/>
                        }
                        {item.formType === "radio" &&
                            <Radio.Group>
                                {item.enums.map((option: any, index: number) => (
                                    <Radio value={option} key={index}>{option}</Radio>
                                ))}
                            </Radio.Group>
                        }
                        {item.formType === "checkbox" &&
                            <Checkbox.Group
                                options={checkBoxOptions}
                            />
                        }
                        {!item.formType &&
                            <Input/>
                        }
                    </>
                }
                {contentType === "other" &&
                    <Input/>
                }
                {
                    (
                        contentType === "image" ||
                        contentType === "video" ||
                        contentType === "audio" ||
                        contentType === "file"
                    ) &&
                    <Uploader
                        maxCount={1}
                        action={'/api/v1/commons/uploadAntd'}
                        children={<Button icon={<UploadOutlined/>}>上传</Button>}
                        onChange={({file}) => {
                            if (file.status === 'done') {
                                let url = file.response?.response.url;
                                if (url.indexOf('http') < 0) {
                                    url = window.location.origin + url;
                                }
                                paramForm.setFieldsValue({
                                    [item.name]: url,
                                });
                            }
                        }}
                    />
                }
            </>
        )
    }

    return (
        <div>
            <Form
                form={paramForm}
                name="basic"
                onFinish={onFinish}
                onFinishFailed={onFinishFailed}
                autoComplete="off"
                layout={"vertical"}
            >
                {workflowParams?.map((item: any, index) => {
                    // console.log(item.contentType + " --> ", item.formType)
                    // formType：radio checkbox input textarea
                    // contentType
                    return (
                        <Form.Item
                            key={index}
                            label={item.formLabel || item.name}
                            name={item.name}
                            rules={[{required: item.required, message: `该项为必填项`}]}
                            extra={item.formDescription}
                        >
                            {formComponent(item)}
                        </Form.Item>
                    )
                })}
                <Form.Item
                    style={{
                        display: "flex",
                        justifyContent: "flex-end",
                    }}
                >
                    <Button disabled={submitLoading} loading={submitLoading} type="primary" htmlType="submit">
                        <SendOutlined/> 开始运行
                    </Button>
                </Form.Item>
            </Form>
        </div>
    )
}
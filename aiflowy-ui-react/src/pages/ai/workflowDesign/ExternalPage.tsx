import {useParams} from "react-router-dom";
import {useGet, usePostManual} from "../../../hooks/useApis.ts";
import {Uploader} from "../../../components/Uploader";
import {Avatar, Button, Col, Form, Input, message, Row, List, Divider, Empty} from "antd";
import {SendOutlined, UploadOutlined} from "@ant-design/icons";
import {useState} from "react";
import JsonView from "react18-json-view";
import 'react18-json-view/src/style.css'

export const ExternalPage = () => {
    const params = useParams();

    const {result: res} = useGet("/api/v1/aiWorkflow/getRunningParameters",{
        id: params.id
    });

    const {doPost: tryRunning} = usePostManual("/api/v1/aiWorkflow/tryRunning");

    const [form] = Form.useForm();
    const [submitLoading, setSubmitLoading] = useState(false);

    const [executeResult, setExecuteResult] = useState<any>()

    const onFinish = (values: any) => {
        //console.log('submit', values)
        setSubmitLoading(true)
        tryRunning({
            data: {
                id: params.id,
                variables: values
            }
        }).then((resp) => {
            if (resp.data.errorCode === 0) {
                message.success("成功")
            }
            setSubmitLoading(false)
            setExecuteResult(resp.data)
        })
    };

    const onFinishFailed = (errorInfo: any) => {
        setSubmitLoading(false)
        if (errorInfo.errorFields) {
            message.warning("请完善表单内容")
        } else {
            message.error("失败：" + errorInfo)
        }
        console.log('Failed:', errorInfo);
    };

    return (
        <>
            <Row gutter={16}>
                <Col style={{padding: "50px"}} className="gutter-row" span={12}>
                    <List
                        itemLayout="horizontal"
                        dataSource={[{...res}]}
                        renderItem={(item, index) => (
                            <List.Item key={index}>
                                <List.Item.Meta
                                    avatar={<Avatar src={item?.icon || '/favicon.png'}/>}
                                    title={<span>{item?.title}</span>}
                                    description={item?.description}
                                />
                            </List.Item>
                        )}
                    />
                    <Divider />
                    <Form
                        form={form}
                        layout={'vertical'}
                        name="basic"
                        labelCol={{span: 6}}
                        wrapperCol={{span: 24}}
                        onFinish={onFinish}
                        onFinishFailed={onFinishFailed}
                        autoComplete="off"
                    >
                        {res && res.parameters?.map((item: any) => {
                            let inputComponent;
                            switch (item.dataType) {
                                case "File":
                                    inputComponent = (
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
                                                    form.setFieldsValue({
                                                        [item.name]: url,
                                                    });
                                                }
                                            }}
                                        />);
                                    break;
                                default:
                                    inputComponent = <Input/>;
                            }

                            return (
                                <Form.Item
                                    key={item.name}
                                    label={item.description}
                                    name={item.name}
                                    rules={[{required: item.required}]}
                                    //extra={item.description}
                                >
                                    {inputComponent}
                                </Form.Item>
                            );
                        })}

                        <Form.Item wrapperCol={{offset: 19,span: 4}}>
                            <Button disabled={submitLoading} loading={submitLoading} type="primary" htmlType="submit">
                                <SendOutlined/> 开始运行
                            </Button>
                        </Form.Item>
                    </Form>
                </Col>
                <Col style={{padding: "50px", backgroundColor: "#fafafa"}}  className="gutter-row" span={12}>
                    {executeResult?
                        <JsonView src={executeResult} />:<Empty description={'暂无内容'} image={Empty.PRESENTED_IMAGE_SIMPLE} />
                    }
                </Col>
            </Row>
        </>
    )
}

export default {
    path: "/ai/workflow/external/:id",
    element: <ExternalPage/>,
    frontEnable: true,
};
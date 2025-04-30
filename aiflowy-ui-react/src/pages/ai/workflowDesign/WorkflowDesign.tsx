import {useEffect, useRef, useState} from 'react';


import {useLayout} from '../../../hooks/useLayout.tsx';
import {App, Button, Drawer, Form, Input} from "antd";
import {useParams} from "react-router-dom";
import {useDetail, useGet, useGetManual, usePostManual, useUpdate} from "../../../hooks/useApis.ts";
import {FormOutlined, SendOutlined, UploadOutlined} from "@ant-design/icons";
import {Tinyflow, TinyflowHandle} from '@tinyflow-ai/react';
import '@tinyflow-ai/react/dist/index.css'
import {Uploader} from "../../../components/Uploader";
import customNode from './customNode/index.ts'

export const WorkflowDesign = () => {

    const {message} = App.useApp()
    const params = useParams();

    const {result: workflow, doGet: reGetWorkflow} = useDetail("aiWorkflow", params.id);
    const {doUpdate} = useUpdate("aiWorkflow");
    const {result: llms} = useGet('/api/v1/aiLlm/list')
    const {result: knowledge} = useGet('/api/v1/aiKnowledge/list')
    const [parameters, setParameters] = useState<any[]>()
    const getOptions = (options: { id: any; title: any }[]): { value: any; label: any }[] => {
        if (options) {
            return options.map((item) => ({
                value: item.id,
                label: item.title,
            }));
        }
        return [];
    };

    const [executeResult, setExecuteResult] = useState<string>('')

    const provider = {
        llm: () => getOptions(llms?.data),
        knowledge: (): any => getOptions(knowledge?.data)
    }
    const {setOptions} = useLayout();
    useEffect(() => {
        setOptions({leftMenuCollapsed: true, showBreadcrumb: false})
        return () => setOptions({leftMenuCollapsed: false, showBreadcrumb: true})
    }, []);

    const tinyflowRef = useRef<TinyflowHandle>(null);

    const saveHandler = () => {
        console.log("data: ", tinyflowRef.current!.getData())
        doUpdate({
            data: {
                id: params.id,
                content: tinyflowRef.current!.getData()
            }
        }).then(reGetWorkflow)
            .then(() => {
                message.success('保存成功')
            })
    }

    const onKeydown = (event: KeyboardEvent) => {
        // 检查是否按下的是 Ctrl + S 或 Command + S
        if ((event.ctrlKey || event.metaKey) && event.key === 's'
            || (event.ctrlKey || event.metaKey) && event.keyCode === 83) {

            // 阻止浏览器默认行为（打开保存对话框）
            event.preventDefault();

            // 保存数据
            saveHandler()
        }
    }

    useEffect(() => {
        document.addEventListener('keydown', onKeydown)
        return () => {
            document.removeEventListener('keydown', onKeydown)
        }
    }, []);


    const {doGet: getRunningParameters} = useGetManual("/api/v1/aiWorkflow/getRunningParameters");
    const {doPost: tryRunning} = usePostManual("/api/v1/aiWorkflow/tryRunning");

    const showRunningParameters = () => {
        getRunningParameters({
            params: {
                id: params.id,
            }
        }).then((resp) => {
            console.log(resp)
            setParameters(resp.data.parameters)
            showDrawer()
        })
    }

    const [open, setOpen] = useState(false);

    const showDrawer = () => {
        setOpen(true);
    };

    const onClose = () => {
        setOpen(false);
    };

    const onFinish = (values: any) => {
        //console.log('submit', values)
        setSubmitLoading(true)
        tryRunning({
            data: {
                id: params.id,
                variables: values
            }
        }).then((resp) => {
            message.success("成功")
            setSubmitLoading(false)
            setExecuteResult(JSON.stringify(resp.data))
        })
    };

    const onFinishFailed = (errorInfo: any) => {
        setSubmitLoading(false)
        message.error("失败：" + errorInfo)
        console.log('Failed:', errorInfo);
    };

    const customNodes: any = {
        ...customNode
    };

    const [form] = Form.useForm();

    const [submitLoading, setSubmitLoading] = useState(false);

    return (
        <>
            <Drawer
                width={640}
                title="请输入参数"
                placement="right"
                closable={false}
                onClose={onClose}
                open={open}
            >

                <Form
                    form={form}
                    name="basic"
                    labelCol={{span: 6}}
                    wrapperCol={{span: 18}}
                    onFinish={onFinish}
                    onFinishFailed={onFinishFailed}
                    autoComplete="off"
                >

                    {parameters && parameters.map((item) => {
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
                                                    url  = window.location.origin + url;
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
                                label={item.name}
                                name={item.name}
                                rules={[{required: item.required}]}
                                extra={item.description}
                            >
                                {inputComponent}
                            </Form.Item>
                        );
                    })}


                    <Form.Item wrapperCol={{offset: 4, span: 18}}>
                        <Button disabled={submitLoading} loading={submitLoading} type="primary" htmlType="submit">
                            <SendOutlined/> 开始运行
                        </Button>
                    </Form.Item>
                </Form>

                <div>
                    <div>执行结果：</div>
                    <div style={{
                        background: "#efefef",
                        padding: "10px",
                        height: "300px",
                        marginTop: "10px",
                        borderRadius: "7px",
                    }}>
                        <pre style={{whiteSpace: "pre-wrap",wordBreak: "break-all"}}>{executeResult || '暂无输出'}</pre>
                    </div>
                </div>

            </Drawer>

            <div style={{height: 'calc(100vh - 50px)', display: "flex"}} className={"agentsflow"}>
                <div style={{flexGrow: 1}}>
                    <div style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        height: "60px",
                        padding: "0 20px",
                        borderBottom: "1px solid #ddd"
                    }}>
                        <div style={{
                            fontSize: "16px",
                            fontWeight: "bold",
                            display: "flex",
                            alignItems: "center",
                            gap: "10px"
                        }}>
                            <span>{workflow?.data?.title || "工作流设计"}</span>
                            <FormOutlined/>
                        </div>
                        <div style={{display: "flex", gap: "10px"}}>
                            <Button type={"default"} onClick={showRunningParameters}> <SendOutlined/> 试运行</Button>
                            <Button type={"primary"} onClick={saveHandler}>保存 (Ctrl + s)</Button>
                        </div>
                    </div>
                    <Tinyflow ref={tinyflowRef} data={JSON.parse(workflow?.data?.content || '{}')}
                              provider={provider}
                              // onChange={(data: any) => {
                              //     console.log(data)
                              //     setWorkflow({
                              //         ...workflow,
                              //         data: {
                              //             ...workflow?.data,
                              //             content: JSON.stringify(data)
                              //         }
                              //     })
                              // }}
                              style={{height: 'calc(100vh - 110px)'}} customNodes={customNodes}/>
                </div>
            </div>
        </>
    );
}

export default {
    path: "/ai/workflow/design/:id",
    element: <WorkflowDesign/>
};

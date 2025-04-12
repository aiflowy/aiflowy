import  {useEffect, useState, useRef} from 'react';


import {useLayout} from '../../../hooks/useLayout.tsx';
import {App, Button, Drawer, Form, Input} from "antd";
import {useParams} from "react-router-dom";
import {useDetail, useGet, useGetManual, usePostManual, useUpdate} from "../../../hooks/useApis.ts";
import {FormOutlined, SendOutlined} from "@ant-design/icons";
import { Tinyflow, TinyflowHandle } from '@tinyflow-ai/react';
import '@tinyflow-ai/react/dist/index.css'


export const WorkflowDesign = () => {

    const {message} = App.useApp()
    const params = useParams();

    const {result: workflow} = useDetail("aiWorkflow", params.id);
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
        llm: ()  => getOptions(llms?.data),
        knowledge: () : any => getOptions(knowledge?.data)
    }
    const {setOptions} = useLayout();
    useEffect(() => {
        setOptions({leftMenuCollapsed: true, showBreadcrumb: false})
        return () => setOptions({leftMenuCollapsed: false, showBreadcrumb: true})
    }, []);

    const tinyflowRef = useRef<TinyflowHandle>(null);


    const saveHandler = () => {
        doUpdate({
            data: {
                id: params.id,
                content: tinyflowRef.current!.getData()
            }
        }).then(() => {
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
        tryRunning({
            data: {
                id: params.id,
                variables: values
            }
        }).then((resp) => {
            message.success("提交成功")
            setExecuteResult(JSON.stringify(resp.data))
        })
    };

    const onFinishFailed = (errorInfo: any) => {
        console.log('Failed:', errorInfo);
    };


    return (
        <>
            <Drawer
                title="请输入参数"
                placement="right"
                closable={false}
                onClose={onClose}
                open={open}
            >

                <Form
                    name="basic"
                    labelCol={{span: 4}}
                    wrapperCol={{span: 18}}
                    onFinish={onFinish}
                    onFinishFailed={onFinishFailed}
                    autoComplete="off"
                >

                    {parameters && parameters.map((item) => (
                        <Form.Item
                            key={item.name}
                            label={item.name}
                            name={item.name}
                            rules={[{required: item.required}]}
                            extra={item.description}
                        >
                            <Input/>
                        </Form.Item>
                    ))}


                    <Form.Item wrapperCol={{offset: 4, span: 18}}>
                        <Button type="primary" htmlType="submit">
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
                        overflowY: "scroll"
                    }}>
                        {executeResult || '暂无输出'}
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
                    <Tinyflow ref={tinyflowRef} data={JSON.parse(workflow?.data?.content||'{}')}
                              provider={provider}
                    style={{height: 'calc(100vh - 110px)'}}/>
                </div>
            </div>
        </>
    );
}

export default {
    path: "/ai/workflow/design/:id",
    element: <WorkflowDesign/>
};

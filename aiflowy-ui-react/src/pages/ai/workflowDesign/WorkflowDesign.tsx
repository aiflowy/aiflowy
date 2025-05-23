// @ts-ignore
import React, {useEffect, useRef, useState} from 'react';
import {useLayout} from '../../../hooks/useLayout.tsx';
import {App, Button, Collapse, Drawer, Empty, Form, Input, Skeleton, Spin} from "antd";
import {useParams} from "react-router-dom";
import {useDetail, useGet, useGetManual, useUpdate} from "../../../hooks/useApis.ts";
import {
    CheckCircleOutlined,
    CloseCircleOutlined, ExclamationCircleOutlined,
    FormOutlined,
    LoadingOutlined,
    SendOutlined,
    UploadOutlined
} from "@ant-design/icons";
import {Tinyflow, TinyflowHandle} from '@tinyflow-ai/react';
import '@tinyflow-ai/react/dist/index.css'
import {Uploader} from "../../../components/Uploader";
import customNode from './customNode/index.ts'
import {PluginNode} from './customNode/pluginNode.ts'
import {PluginTools} from "../botDesign/PluginTools.tsx";
import {SingleRun} from "./SingleRun.tsx";
import JsonView from "react18-json-view";
import {useSse} from "../../../hooks/useSse.ts";
import {sortNodes} from "../../../libs/workflowUtil";

export const WorkflowDesign = () => {

    const {message} = App.useApp()
    const params = useParams();

    const {result: workflow} = useDetail("aiWorkflow", params.id);
    const {doUpdate} = useUpdate("aiWorkflow");
    const {result: llms} = useGet('/api/v1/aiLlm/list')
    const {result: knowledge} = useGet('/api/v1/aiKnowledge/list')
    const [parameters, setParameters] = useState<any[]>()
    const [workflowData, setWorkflowData] = useState<any>({})
    const [saveLoading, setSaveLoading] = useState(false);
    const [runLoading, setRunLoading] = useState(false);
    const [collapseItems, setCollapseItems] = useState<any[]>([])

    const {doGet: getWorkflowInfo} = useGetManual("/api/v1/aiWorkflow/detail")
    const getNodesInfo = (workflowId: any) => {
        setCollapseItems([])
        setRunLoading(true)
        getWorkflowInfo({
            params: {
                id: workflowId,
            }
        }).then(res => {
            setRunLoading(false)
            const nodeJson = JSON.parse(res.data?.data.content);
            setCollapseItems(sortNodes(nodeJson))
        })
    }

    // 添加 useEffect 监听 workflow 变化
    useEffect(() => {
        if (workflow?.data?.content) {
            try {
                const content = JSON.parse(workflow.data.content);
                setWorkflowData(content)
            } catch (e) {
                setWorkflowData({})
            }
        }
    }, [workflow])

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
        knowledge: (): any => getOptions(knowledge?.data),
        searchEngine: (): any => [{
            value: 'bocha',
            label: '博查搜索',
        }]
    }
    const [showTinyflow, setShowTinyflow] = useState(false);
    useEffect(() => {
        if (llms && knowledge) {
            setShowTinyflow(true)
        }
    }, [llms, knowledge]);

    const {setOptions} = useLayout();
    useEffect(() => {
        setOptions({leftMenuCollapsed: true, showBreadcrumb: false})
        return () => setOptions({leftMenuCollapsed: false, showBreadcrumb: true})
    }, []);

    const tinyflowRef = useRef<TinyflowHandle>(null);

    const saveHandler = () => {
        console.log("data: ", tinyflowRef.current!.getData())
        setSaveLoading(true)
        doUpdate({
            data: {
                id: params.id,
                content: tinyflowRef.current!.getData()
            }
        }).then(() => {
            setSaveLoading(false)
            // console.log("res: ", res)
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
    //const {doPost: tryRunning} = usePostManual("/api/v1/aiWorkflow/tryRunning");
    const [selectedItem, setSelectedItem] = useState<any>([]);
    const showRunningParameters = async () => {
        setRunLoading(true)
        await doUpdate({
            data: {
                id: params.id,
                content: tinyflowRef.current!.getData()
            }
        })
        getRunningParameters({
            params: {
                id: params.id,
            }
        }).then((resp) => {
            if (resp.data.errorCode === 0) {
                showDrawer()
                setParameters(resp.data.parameters)
            }
            setRunLoading(false)
        })
        getNodesInfo(params.id)
    }

    const [open, setOpen] = useState(false);
    const [singleRunOpen, setSingleRunOpen] = useState(false);

    const singleRunRef = useRef<any>(null)
    const [currentNode, setCurrentNode] = useState<any>(null)

    const showDrawer = () => {
        setOpen(true);
    };

    const onClose = () => {
        setOpen(false);
    };

    const onSingleRunClose = () => {
        setSingleRunOpen(false);
        singleRunRef.current.resetForm()
    };

    const {start: runWithStream} = useSse("/api/v1/aiWorkflow/tryRunningStream");

    const onFinish = (values: any) => {
        //console.log('submit', values)
        setSubmitLoading(true)
        // tryRunning({
        //     data: {
        //         id: params.id,
        //         variables: values
        //     }
        // }).then((resp) => {
        //     if (resp.data.errorCode === 0) {
        //         message.success("成功")
        //     }
        //     setSubmitLoading(false)
        //     setExecuteResult(JSON.stringify(resp.data))
        // })
        collapseItems.map((item: any) => {
            item.extra = ""
            item.children = ""
        })
        setCollapseItems([...collapseItems])
        runWithStream({
            data: {
                id: params.id,
                variables: values
            },
            onMessage: (msg: any) => {
                //console.log(msg)
                if (msg.execResult) {
                    setExecuteResult(msg.execResult)
                }
                if (msg.status === 'error') {
                    setExecuteResult(msg)
                    collapseItems.map((item: any) => {
                        item.extra = <Spin indicator={<ExclamationCircleOutlined style={{color: "#EABB00"}} />} />
                    })
                    setCollapseItems([...collapseItems])
                }
                if (msg.nodeId && msg.status) {
                    collapseItems.map((item: any) => {
                        if (item.key == msg.nodeId) {
                            if (msg.status === 'start') {
                                item.extra = <Spin indicator={<LoadingOutlined/>}/>
                                item.children = ""
                            }
                            if (msg.status === 'end') {
                                item.extra = <Spin indicator={<CheckCircleOutlined style={{color: 'green'}} />} />
                                item.children = msg.res ?
                                    <div style={{wordWrap: "break-word",}}>
                                        <JsonView src={msg.res}/>
                                    </div> : ""
                            }
                            if (msg.status === 'nodeError') {
                                item.extra = <Spin indicator={<CloseCircleOutlined style={{color: 'red'}} />} />
                                item.children = <JsonView src={msg.errorMsg}/>
                            }
                        }
                    })
                    setCollapseItems([...collapseItems])
                }
            },
            onFinished: () => {
                setSubmitLoading(false)
            },
        })
    };

    const onFinishFailed = (errorInfo: any) => {
        setSubmitLoading(false)
        //message.error("失败：" + errorInfo)
        console.log('Failed:', errorInfo);
    };

    const [changeNodeData, setChangeNodeData] = useState<any>()

    const handleChosen = (updateNodeData: any, value: any) => {
        if (value) {
            setSelectedItem([value])
        }
        setChangeNodeData(() => updateNodeData)
        setPluginOpen(true)
    }
    const customNodes: any = {
        ...customNode,
        'plugin-node': PluginNode({
            onChosen: handleChosen
        })
    };

    const [form] = Form.useForm();

    const [submitLoading, setSubmitLoading] = useState(false);
    const [pluginOpen, setPluginOpen] = useState(false)
    const [pageLoading, setPageLoading] = useState(false)
    const {doGet: getTinyFlowData} = useGetManual("/api/v1/aiPluginTool/getTinyFlowData")


    return (
        <>
            <PluginTools
                selectedItem={selectedItem}
                goToPage="/ai/plugin"
                open={pluginOpen} onClose={() => setPluginOpen(false)}
                onCancel={() => setPluginOpen(false)}
                onSelectedItem={item => {
                    setPluginOpen(false)
                    setPageLoading(true)
                    // 调用保存的 updateNodeData 函数
                    if (changeNodeData) {
                        getTinyFlowData({
                            params: {
                                id: item.id
                            }
                        }).then(res => {
                            setPageLoading(false)
                            changeNodeData(res.data.data)
                        })
                    }
                    setSelectedItem([item.id])
                }}
                onRemoveItem={() => {
                    setPluginOpen(false)
                    // 调用保存的 updateNodeData 函数
                    if (changeNodeData) {
                        changeNodeData({
                            pluginId: '',
                            pluginName: '',
                            parameters: [],
                            outputDefs: []
                        })
                        setSelectedItem([])
                    }
                }}
            />
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
                    <div style={{marginBottom: "10px"}}>执行结果：</div>
                    <div style={{
                        padding: "20px",
                        backgroundColor: "#fafafa",
                        textAlign: "center",
                        wordWrap: "break-word",
                    }}
                    >
                        {executeResult ?
                            <JsonView src={executeResult}/> :
                            <Empty description={'暂无内容'} image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                        }
                    </div>
                </div>
                <div style={{marginTop: "10px"}}>
                    <div>执行步骤：</div>
                    <div style={{marginTop: "10px"}}>
                        <Collapse items={collapseItems}/>
                    </div>
                </div>
            </Drawer>

            <Drawer
                width={640}
                title="请输入参数"
                placement="right"
                closable={false}
                onClose={onSingleRunClose}
                open={singleRunOpen}
            >
            <SingleRun ref={singleRunRef} workflowId={params.id} node={currentNode} />
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
                            <Button type={"default"} loading={runLoading} onClick={showRunningParameters}> <SendOutlined/> 试运行</Button>
                            <Button type={"primary"} loading={saveLoading} onClick={saveHandler}>保存 (Ctrl + s)</Button>
                        </div>
                    </div>
                    {showTinyflow ?
                        <Spin spinning={pageLoading} >
                            <Tinyflow ref={tinyflowRef} data={workflowData}
                                      provider={provider}
                                      onNodeExecute={async (node) => {
                                          if ("loopNode" === node.type) {
                                              message.warning("暂不支持")
                                              return
                                          }
                                          setCurrentNode(node)
                                          setPageLoading(true)
                                          await doUpdate({
                                              data: {
                                                  id: params.id,
                                                  content: tinyflowRef.current!.getData()
                                              }
                                          })
                                          setPageLoading(false)
                                          setSingleRunOpen(true)
                                      }}
                                      style={{height: 'calc(100vh - 10px)'}} customNodes={customNodes}/>
                        </Spin>
                        : <div style={{padding: '20px'}}><Skeleton active/></div>
                    }

                </div>
            </div>
        </>
    );
}

export default {
    path: "/ai/workflow/design/:id",
    element: <WorkflowDesign/>
};

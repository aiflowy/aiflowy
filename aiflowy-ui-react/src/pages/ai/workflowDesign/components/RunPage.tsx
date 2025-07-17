import {useParams} from "react-router-dom";
import {Button, Col, Collapse, Empty, Form, Input, message, Row, Space, Spin} from "antd";
import css from './runpage.module.css'
import {useGet, useGetManual} from "../../../../hooks/useApis.ts";
import {Uploader} from "../../../../components/Uploader";
import {
    CheckCircleOutlined, CloseCircleOutlined,
    ExclamationCircleOutlined,
    LoadingOutlined,
    SendOutlined,
    UploadOutlined
} from "@ant-design/icons";
import React, {useEffect, useRef, useState} from "react";
import workflowIcon from "../../../../assets/wf-ex-icon.png"
import {sortNodes} from "../../../../libs/workflowUtil.tsx";
import {useSse} from "../../../../hooks/useSse.ts";
import JsonView from "react18-json-view";
import confirmIcon from "../../../../assets/confirm-icon.png";
import {ConfirmItem} from "./ConfirmItem.tsx";
import {ConfirmItemMulti} from "./ConfirmItemMulti.tsx";

export const RunPage: React.FC = () => {
    const params = useParams();

    const {result: res} = useGet("/api/v1/aiWorkflow/getRunningParameters",{
        id: params.id
    });

    const [form] = Form.useForm();

    const [submitLoading, setSubmitLoading] = useState(false);

    const {start: runWithStream} = useSse("/api/v1/aiWorkflow/tryRunningStream");

    const onFinish = (values: any) => {
        collapseItems.map((item: any) => {
            item.extra = ""
            item.children = ""
        })
        setCollapseItems([...collapseItems])
        setSubmitLoading(true)
        runWithStream({
            data: {
                id: params.id,
                variables: values
            },
            onMessage: (msg: any) => {
                handleStreamMsg(msg)
            },
            onFinished: () => {
                setSubmitLoading(false)
            },
        })
    };

    const [executeResult, setExecuteResult] = useState<string>('')

    const [confirmForm] = Form.useForm();

    const confirmBtnRef = useRef<any>(null)

    const handleStreamMsg = (msg: any) => {
        //console.log(msg)
        if (msg.status === 'execOnce') {
            message.warning("流程已执行完毕，请重新发起。")
        }
        if (msg.execResult) {
            message.success('执行完毕')
            setExecuteResult(msg.execResult)
        }
        if (msg.status === 'error') {
            message.error('执行错误')
            setExecuteResult(msg)
            collapseItems.map((item: any) => {
                item.extra = <Spin indicator={<ExclamationCircleOutlined style={{color: "#EABB00"}}/>}/>
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
                        item.extra = <Spin indicator={<CheckCircleOutlined style={{color: 'green'}}/>}/>
                        item.children = msg.res ?
                            <div style={{wordWrap: "break-word",}}>
                                <JsonView src={msg.res}/>
                            </div> : ""
                    }
                    if (msg.status === 'nodeError') {
                        item.extra = <Spin indicator={<CloseCircleOutlined style={{color: 'red'}}/>}/>
                        item.children = <JsonView src={msg.errorMsg}/>
                    }
                    if (msg.status === 'confirm') {
                        message.success("有待确认的内容，请先确认！")
                        setActiveCol(msg.nodeId)
                        handleConfirmStep(msg, item)
                    }
                }
            })
            setCollapseItems([...collapseItems])
        }
    }

    const handleConfirmStep = (msg: any, item: any) => {
        //console.log(msg)
        const confirmKey = msg.suspendForParameters[0].name;

        item.children = <>
            <div style={{
                backgroundColor: "#F7F7F7",
                borderRadius: "8px",
                padding: "12px 21px",
            }}>
                <div style={{
                    marginBottom: "16px",
                    wordBreak: "break-all",
                }}>
                    <div style={{fontWeight: "bold",display: "flex",alignItems: "center"}}>
                        <div style={{marginRight: "9px"}}>
                            <img alt={""} src={confirmIcon} style={{width: "21px", height: "21px"}} />
                        </div>
                        <div>
                            {msg.chainMessage}
                        </div>
                    </div>
                </div>

                <Form
                    form={confirmForm}
                    style={{
                        borderRadius: "8px",
                    }}
                >
                    {msg.suspendForParameters.map((ops: any, i: number) => {
                        // formLabel formDescription
                        if (ops.selectionMode === 'confirm') {
                            return null
                        }
                        const selectKey = ops.name;
                        const selectionDataType = ops.selectionDataType ? ops.selectionDataType : "text"
                        const selectionMode = ops.selectionMode ? ops.selectionMode : "single"
                        const selectionData = ops.selectionData

                        return (
                            <div
                                key={i}
                                style={{
                                    backgroundColor: "#fff",
                                    padding: "16px",
                                    borderRadius: "8px",
                                    marginBottom: "16px",
                                }}>
                                <div
                                    style={{
                                        fontWeight: "bold",
                                        display: "flex",
                                        alignItems: "center",
                                        wordBreak: "break-all",
                                    }}>
                                    <div style={{
                                        display: "inline-block",
                                        width: "2px",
                                        borderRadius: "1px",
                                        height: "16px",
                                        marginLeft: "-16px",
                                        marginRight: "16px",
                                        backgroundColor: "#0066FF"
                                    }}>
                                        &nbsp;
                                    </div>
                                    {ops.formLabel}
                                </div>
                                <div style={{
                                    marginBottom: "16px",
                                    color: "#969799",
                                    wordBreak: "break-all",
                                }}>
                                    {ops.formDescription}
                                </div>
                                <Form.Item
                                    style={{
                                        wordBreak: "break-all",
                                    }}
                                    name={selectKey}
                                    rules={[{required: true, message: '请选择内容'}]}
                                >
                                    {selectionMode === 'single' ?
                                        <ConfirmItem selectionDataType={selectionDataType}
                                                     selectionData={selectionData}/> :
                                        <ConfirmItemMulti selectionDataType={selectionDataType}
                                                          selectionData={selectionData}/>
                                    }
                                </Form.Item>
                            </div>
                        )
                    })}
                </Form>
                <div style={{
                    marginTop: "20px",
                    display: "flex",
                    justifyContent: "flex-end",
                }}>
                    <Space>
                        <Button
                            ref={confirmBtnRef}
                            type="primary"
                            disabled={item.confirmBtnDisabled}
                            onClick={() => {
                                confirmForm.validateFields().then((values) => {
                                    const value = {
                                        chainId: msg.chainId,
                                        confirmParams: {
                                            [confirmKey]: 'yes',
                                            ...values
                                        }
                                    }
                                    handleConfirmSubmit(value)
                                }).catch(() => {
                                    message.warning("请选择内容")
                                })
                            }}
                        >
                            确认
                        </Button>
                    </Space>
                </div>
            </div>
        </>
    }

    const {start: runResume} = useSse("/api/v1/aiWorkflow/resumeChain");

    const handleConfirmSubmit = (values: any) => {

        setSubmitLoading(true)
        if (confirmBtnRef.current) {
            confirmBtnRef.current.disabled = true;
        }
        runResume({
            data: {
                ...values
            },
            onMessage: (msg: any) => {
                handleStreamMsg(msg)
            },
            onFinished: () => {
                setSubmitLoading(false)
            }
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

    const [activeCol, setActiveCol] = useState('')
    const [collapseItems, setCollapseItems] = useState<any[]>([])

    const {doGet: getWorkflowInfo} = useGetManual("/api/v1/aiWorkflow/detail")
    const getNodesInfo = (workflowId: any) => {
        setCollapseItems([])
        getWorkflowInfo({
            params: {
                id: workflowId,
            }
        }).then(res => {
            const nodeJson = JSON.parse(res.data?.data.content);
            setCollapseItems(sortNodes(nodeJson))
        })
    }

    useEffect(() => {
        getNodesInfo(params.id)
    },[])

    return (
        <div className={css.runPageContainer}>
            <Row gutter={8}>
                <img alt="AIFlowy" src="/favicon.svg" style={{width: "100px", height: "22px"}}/>
                <Col span={24}>
                    <div className={css.runPageTileDiv}>
                        <img alt={""} src={res?.icon || workflowIcon} style={{width: "70px", height: "70px"}} />
                        <div style={{alignItems: "center", marginLeft: "27px"}}>
                            <div className={css.workflowTitle}>{res?.title}</div>
                            <div className={css.workflowDescription}>{res?.description}</div>
                        </div>
                    </div>
                </Col>
                <Col span={10}>
                    <Col span={24}>
                        <div className={css.paramDiv}>
                            <div className={css.paramTitle}>
                                参数
                            </div>
                            <Form
                                form={form}
                                name="basic"
                                labelCol={{span: 4}}
                                wrapperCol={{span: 20}}
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
                                            label={item.description || item.name}
                                            name={item.name}
                                            rules={[{required: item.required}]}
                                            //extra={item.description}
                                        >
                                            {inputComponent}
                                        </Form.Item>
                                    );
                                })}

                                <Form.Item
                                    style={{display: "flex",
                                        justifyContent: "flex-end",}}
                                >
                                    <Button disabled={submitLoading} loading={submitLoading} type="primary" htmlType="submit">
                                        <SendOutlined/> 开始运行
                                    </Button>
                                </Form.Item>
                            </Form>
                        </div>
                    </Col>
                    <Col span={24}>
                        <div className={css.resultDiv}>
                            <div className={css.resultTitle}>
                                运行结果
                            </div>
                            <div className={css.resultContainer}>
                                {executeResult ?
                                    <JsonView src={executeResult}/> :
                                    <Empty style={{lineHeight: "280px"}} image={Empty.PRESENTED_IMAGE_SIMPLE} />
                                }
                            </div>
                        </div>
                    </Col>
                </Col>
                <Col span={14}>
                    <div className={css.stepDiv}>
                        <div className={css.resultTitle}>
                            执行步骤
                        </div>
                        <Collapse
                            style={{borderColor: "#F0F0F0"}}
                            activeKey={activeCol}
                            items={collapseItems}
                            onChange={(k: any) => {
                            setActiveCol(k)
                        }}/>
                    </div>
                </Col>
            </Row>
        </div>
    )
}

export default {
    path: "/ai/workflow/external/:id",
    element: <RunPage/>,
    frontEnable: true,
};
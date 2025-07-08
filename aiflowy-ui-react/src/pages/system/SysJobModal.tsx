import React, {forwardRef, useImperativeHandle, useState} from 'react'
import {ModalProps} from "antd/es/modal/interface";
import {Form, Input, message, Modal, Spin, theme} from "antd";
import {DictSelect} from "../../components/DictSelect";
import {useGetManual, usePostManual} from "../../hooks/useApis.ts";
import {DynamicItem} from '../../libs/workflowUtil'

export type SysJobModalProps = {
    ref?: any,
    onModalOk: () => void,
    onModalCancel: () => void,
} & ModalProps
export const SysJobModal: React.FC<SysJobModalProps> = forwardRef((props, ref) => {

    const [formData, setFormData] = useState<any>(null)
    const {doGet: getJobInfo} = useGetManual("/api/v1/sysJob/detail")
    const {doPost: saveJob} = usePostManual("/api/v1/sysJob/save")
    const {doPost: updateJob} = usePostManual("/api/v1/sysJob/update")
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false)
    useImperativeHandle(ref, () => ({
        setData: (data: any) => {
            if (data) {
                setLoading(true)
                getJobInfo({
                    params: {
                        id: data.id
                    }
                }).then(res => {
                    setLoading(false)
                    setFormData(res.data.data)
                    form.setFieldsValue(res.data.data)
                    if (res.data.data.jobParams?.workflowId) {
                        getWorkflowParamsList(res.data.data.jobParams.workflowId)
                    }
                })
            }
        }
    }));

    const {doGet: getWorkflowParams} = useGetManual("/api/v1/aiWorkflow/getRunningParameters")

    const [workflowParams, setWorkflowParams] = useState<any>([])

    const onFinish = (values: any) => {
        console.log('Success:', values);
    };
    const onFinishFailed = (errorInfo: any) => {
        console.log('Failed:', errorInfo);
    };

    const {token} = theme.useToken();

    const formStyle = {
        maxWidth: 'none',
        background: token.colorFillAlter,
        borderRadius: token.borderRadiusLG,
        padding: 24,
        marginBottom: '20px'
    };

    const selectChange = (value: any, propName: string) => {
        setFormData({
            ...formData,
            [propName]: value
        })
    }

    const onOK = () => {
        form.validateFields()
            .then(value => {
                setLoading(true)
                if (value.id) {
                    updateJob({
                        data: value
                    }).then(res => {
                        setLoading(false)
                        if (res.data.errorCode == 0) {
                            initData()
                            message.success('成功')
                            props.onModalOk()
                        } else {
                            message.error(res.data.message)
                        }
                    })
                } else {
                    saveJob({
                        data: value
                    }).then(res => {
                        setLoading(false)
                        if (res.data.errorCode == 0) {
                            initData()
                            message.success('成功')
                            props.onModalOk()
                        } else {
                            message.error(res.data.message)
                        }
                    })
                }
            })
            .catch(() => {
            })
    }

    const onCancel = () => {
        initData()
        props.onModalCancel()
    }

    const initData = () => {
        form.resetFields()
        setFormData(null)
        setWorkflowParams([])
    }

    const getWorkflowParamsList = (v: any) => {
        setLoading(true)
        getWorkflowParams({
            params: {
                id: v
            }
        }).then((res) => {
            setLoading(false)
            setWorkflowParams(res.data.parameters)
        })
    }

    return (
        <>
            <Modal
                title={formData?.id ? "修改" : "新增"}
                confirmLoading={loading}
                onOk={onOK}
                onCancel={onCancel}
                {...props}
            >
                <Spin spinning={loading}>

                    <Form
                        form={form}
                        name="basic"
                        labelCol={{span: 8}}
                        wrapperCol={{span: 16}}
                        style={formStyle}
                        //initialValues={formData}
                        onFinish={onFinish}
                        onFinishFailed={onFinishFailed}
                        autoComplete="off"
                    >
                        <Form.Item
                            hidden={true}
                            label="id"
                            name="id"
                        >
                            <Input/>
                        </Form.Item>
                        <Form.Item
                            label="任务名称"
                            name="jobName"
                            rules={[{required: true, message: '请输入任务名称'}]}
                        >
                            <Input/>
                        </Form.Item>

                        <Form.Item
                            label="任务类型"
                            name="jobType"
                            rules={[{required: true, message: '请选择任务类型'}]}
                        >
                            <DictSelect
                                style={{width: '100%'}}
                                code={"jobType"}
                                initvalue={formData?.jobType}
                                onChange={(v) => {
                                    setWorkflowParams([])
                                    selectChange(v, "jobType")
                                }}
                            />
                        </Form.Item>

                        <Form.Item
                            label="cron表达式"
                            name="cronExpression"
                            rules={[{required: true, message: '请输入cron表达式'}]}
                        >
                            <Input/>
                        </Form.Item>

                        <Form.Item
                            label="并发执行"
                            name="allowConcurrent"
                        >
                            <DictSelect
                                style={{width: '100%'}}
                                code={"yesOrNo"}
                                initvalue={formData?.allowConcurrent}
                                onChange={(v) => selectChange(v, "allowConcurrent")}
                            />
                        </Form.Item>

                        <Form.Item
                            label="错过策略"
                            name="misfirePolicy"
                        >
                            <DictSelect
                                style={{width: '100%'}}
                                code={"misfirePolicy"}
                                initvalue={formData?.misfirePolicy}
                                onChange={(v) => selectChange(v, "misfirePolicy")}
                            />
                        </Form.Item>

                        {formData?.jobType == 2 && <Form.Item
                            extra={"示例：sysJobServiceImpl.testParam(\"bean\",false,299) " +
                                "参数支持：String，Boolean，Long[L结尾]，Double[D结尾]，Float[F结尾]，Integer。"}
                            label="bean方法"
                            name={["jobParams", "beanMethod"]}
                            rules={[{required: true, message: '请输入bean方法'}]}
                        >
                            <Input />
                        </Form.Item>}

                        {formData?.jobType == 3 && <Form.Item
                            extra={"示例：tech.aiflowy.job.util.JobUtil.execTest(\"test\",1,0.52D,100L)"}
                            label="java方法"
                            name={["jobParams", "javaMethod"]}
                            rules={[{required: true, message: '请输入java方法'}]}
                        >
                            <Input />
                        </Form.Item>}

                        {formData?.jobType == 1 && <Form.Item
                            label="工作流"
                            name={["jobParams", "workflowId"]}
                        >
                            <DictSelect
                                style={{width: '100%'}}
                                code={"aiWorkFlow"}
                                initvalue={formData?.jobParams?.workflowId}
                                onChange={(v) => {
                                    setFormData({
                                        ...formData,
                                        jobParams: {
                                            workflowId: v
                                        }
                                    })
                                    getWorkflowParamsList(v)
                                }}
                            />
                        </Form.Item>}

                        {formData?.jobType == 1 && workflowParams?.map((item: any) => {
                            return (
                                <Form.Item
                                    key={item.id}
                                    label={item.description ? item.description : item.name}
                                    name={["jobParams", "workflowParams", item.name]}
                                    rules={item.required ? [{required: true, message: '请完善此项'}] : []}
                                >
                                    {DynamicItem(item, form, "jobParams.workflowParams." + item.name)}
                                </Form.Item>
                            )
                        })}

                        <Form.Item
                            extra={"某些场景需要对任务结果进行数据归属"}
                            label="归属者"
                            name={["jobParams", "accountId"]}
                        >
                            <DictSelect
                                style={{width: '100%'}}
                                code={"sysAccount"}
                                initvalue={formData?.jobParams?.accountId}
                                onChange={(v) => {
                                    setFormData({
                                        ...formData,
                                        jobParams: {
                                            ...formData.jobParams,
                                            accountId: v
                                        }
                                    })
                                }}
                            />
                        </Form.Item>
                    </Form>
                </Spin>
            </Modal>
        </>
    )
})
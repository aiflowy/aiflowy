import {Fragment, JSX, useEffect, useMemo, useState} from 'react';
import {
    Button,
    Col,
    Divider,
    Form, FormInstance, Modal,
    Row, Space,
    theme,
} from 'antd';
import {Actions, ColumnConfig, ColumnGroup, ColumnsConfig} from "./index";
import DynamicFormItem from "./DynamicFormItem";
import EditFormContainer from "./EditFormContainer.tsx";
import {isHidden} from "../../libs/utils.ts";
import TextArea from "antd/es/input/TextArea";
import {usePost} from "../../hooks/useApis.ts";
import {convertRowDataToDayjs} from './dayjsUtil.ts';

export type EditLayout = {
    openType?: "modal" | "drawer",
    columnsCount?: number,
    labelLayout?: "horizontal" | "vertical",
    labelWidth?: number,
    customButton?: () => JSX.Element | null
}

export type EditFormProps<T> = {
    columns: ColumnsConfig<any>
    onSubmit: (values: T) => void,
    onCancel: () => void,
    actions: Actions<T>,
    onActionsInvokeAfter?: () => void,
    formRenderFactory?: (position: "edit" | "search", columnConfig: ColumnConfig, form: FormInstance) => JSX.Element | null
    row?: T,
    open: boolean
    title: string,
    formReadOnly?: boolean,
    groups?: ColumnGroup[],
    layout?: EditLayout
    intelligentFilling?: {
        url: string,
    },
}

const EditForm = <T, >({
                           columns,
                           onSubmit,
                           onCancel,
                           actions,
                           onActionsInvokeAfter,
                           formRenderFactory,
                           row,
                           open,
                           title,
                           formReadOnly = false,
                           groups,
                           layout,
                           intelligentFilling,
                       }: EditFormProps<T>) => {

    const {token} = theme.useToken();
    const [form] = Form.useForm();

    const [confirmLoading, setConfirmLoading] = useState(false);

    const formStyle = {
        maxWidth: 'none',
        background: token.colorFillAlter,
        borderRadius: token.borderRadiusLG,
        padding: 24,
        marginBottom: '20px'
    };

    // const formItemLayout = {labelCol: {span: 6}, wrapperCol: {span: 14}};

    useEffect(() => {
        if (row && actions.onFetchDetail) {
            const newRow = convertRowDataToDayjs(actions.onFetchDetail(row), columns)
            form.setFieldsValue(newRow);
        } else if (row) {
            const newRow = convertRowDataToDayjs(row, columns)
            form.setFieldsValue(newRow)
        }
    }, [row])

    const onFinish = (values: T) => {
        try {
            setConfirmLoading(true)
            form.resetFields();
            if (row) {
                actions.onUpdate?.(values);
                onActionsInvokeAfter?.()
            } else {
                actions.onCreate?.(values);
                onActionsInvokeAfter?.()
            }
            onSubmit(values);
        } finally {
            setConfirmLoading(false)
        }
    }

    const onCancelClick = () => {
        form.resetFields();
        onCancel();
    };

    const onValuesChange = (changedValues: any, allValues: any) => {
        for (const columnConfig of columns) {
            columnConfig.onValuesChange?.(changedValues, allValues)
        }
    }

    const [openIntelligentFilling, setOpenIntelligentFilling] = useState(false)


    const [textAreaValue, setTextAreaValue] = useState<string>()
    const {loading, doPost: intelligentFillingStart} = usePost(intelligentFilling?.url!);

    const startFilling = () => {
        intelligentFillingStart({
            data: {
                content: textAreaValue,
            }
        }).then((resp) => {
            form.setFieldsValue((resp.data as any)?.result);
            setOpenIntelligentFilling(false)
        })
    }

    const colSpan = useMemo(() => {
        if (!(layout?.columnsCount) || layout?.columnsCount === 1) {
            return 20
        } else {
            return 24 / layout.columnsCount;
        }
    }, [layout?.columnsCount])


    const colOffset = useMemo(() => {
        if (!(layout?.columnsCount) || layout?.columnsCount === 1) {
            return 2
        } else {
            return 0;
        }
    }, [layout?.columnsCount])

    return (
        <>
            <Modal title="智能填充" open={openIntelligentFilling} onOk={startFilling} confirmLoading={loading}
                   onCancel={() => {
                       setOpenIntelligentFilling(false)
                   }}>
                <TextArea placeholder="点击这里，开始输入内容" value={textAreaValue}
                          autoSize={{minRows: 6, maxRows: 10}}
                          onChange={(e) => {
                              setTextAreaValue(e.target.value)
                          }}/>
            </Modal>

            <EditFormContainer
                type={layout?.openType || "modal"}
                title={title}
                open={open}
                centered={true}
                placement="right"
                onOk={form.submit}
                onClose={onCancelClick}
                onCancel={onCancelClick}
                confirmLoading={confirmLoading}
                footer={<Space>
                    {layout?.customButton?.()}
                    {intelligentFilling && <Button onClick={() => {
                        setOpenIntelligentFilling(true)
                    }}>
                        智能填充
                    </Button>}
                    <Button onClick={onCancelClick}>
                        取消
                    </Button>
                    <Button type="primary" onClick={() => {
                        form.submit();
                    }}>
                        确定
                    </Button>
                </Space>
                }
                width={"40%"}>
                <div style={{maxHeight: "80vh", overflowY: "auto"}}>
                    <Form form={form} style={formStyle} onFinish={onFinish} labelAlign={"right"}
                          onValuesChange={onValuesChange}
                          layout={layout?.labelLayout || "horizontal"}
                          labelCol={layout?.labelLayout !== "vertical" ? {
                              flex: `0 0 ${layout?.labelWidth || 100}px`
                          } : {}}
                    >
                        <Row gutter={24}>
                            {groups && groups.map((group) => {
                                return (
                                    <Fragment key={group.key}>
                                        <Divider orientation="left">{group.title}</Divider>
                                        {columns.filter((columnConfig) => columnConfig.groupKey == group.key)
                                            .map((columnConfig) => {
                                                const wrapCol = columnConfig.form?.wrapCol;
                                                return (
                                                    isHidden(columnConfig) || wrapCol === false ?
                                                        (<DynamicFormItem columnConfig={columnConfig}
                                                                          readOnly={formReadOnly}
                                                                          form={form}
                                                                          key={columnConfig.key}
                                                                          position="edit"
                                                                          data={row}
                                                                          formRenderFactory={formRenderFactory}/>
                                                        ) :
                                                        (<Col span={colSpan}
                                                              offset={colOffset}
                                                              key={columnConfig.key}>
                                                            <DynamicFormItem columnConfig={columnConfig}
                                                                             readOnly={formReadOnly}
                                                                             form={form}
                                                                             position="edit" data={row}
                                                                             formRenderFactory={formRenderFactory}/>
                                                        </Col>)
                                                )
                                            })}
                                    </Fragment>
                                )
                            })}

                            {columns.filter((columnConfig) => !columnConfig.groupKey)
                                .map((columnConfig) => {
                                    const wrapCol = columnConfig.form?.wrapCol;
                                    return (
                                        isHidden(columnConfig) || wrapCol === false ?
                                            (<DynamicFormItem columnConfig={columnConfig}
                                                              readOnly={formReadOnly}
                                                              position="edit"
                                                              key={columnConfig.key}
                                                              form={form}
                                                              data={row}
                                                              formRenderFactory={formRenderFactory}/>
                                            ) :
                                            (<Col span={colSpan}
                                                  offset={colOffset}
                                                  key={columnConfig.key}>
                                                <DynamicFormItem columnConfig={columnConfig}
                                                                 readOnly={formReadOnly}
                                                                 position="edit"
                                                                 form={form}
                                                                 data={row}
                                                                 formRenderFactory={formRenderFactory}/>
                                            </Col>)
                                    )
                                })}

                        </Row>
                    </Form>
                </div>

            </EditFormContainer>
        </>
    );
};

export default EditForm
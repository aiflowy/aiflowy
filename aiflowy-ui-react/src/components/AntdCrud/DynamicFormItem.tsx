import React, {JSX, useState} from 'react';
import {
    Cascader,
    Checkbox, ColorPicker,
    DatePicker,
    Form, FormInstance,
    Input, InputNumber, Radio, Rate, Select, Slider, Switch, TimePicker, TreeSelect,
} from 'antd';
import {ColumnConfig, DictConfig} from "./index";
import TextArea from "antd/es/input/TextArea";
import ImageUploader from "../ImageUploader";
import AntdIconSelect from "../AntdIconSelect";
import {disabledDictItemAndChildren, isHidden} from "../../libs/utils.ts";
import AiEditorWrapper from "../AiEditorWrapper";
import FileUploader from "../FileUploader";
import {useDict} from "../../hooks/useDict.ts";

const {RangePicker} = DatePicker;

const DynamicFormItem: React.FC<{
    position: "edit" | "search",
    columnConfig: ColumnConfig<any>,
    onValueInit?: (key: string) => any,
    formRenderFactory?: (position: "edit" | "search", columnConfig: ColumnConfig, form: FormInstance) => JSX.Element | null
    readOnly?: boolean,
    data?: any,
    form: FormInstance,
}> = ({
          position,
          columnConfig,
          onValueInit,
          formRenderFactory = (_position: any, _column: ColumnConfig, _form: any) => null,
          readOnly = false,
          data,
          form,
      }) => {


    const hidden = !!(columnConfig.editCondition && !columnConfig.editCondition(data));
    const [show, setShow] = useState(!hidden)

    columnConfig.onValuesChange = (_: any) => {
        if (!columnConfig.editCondition) {
            setShow(true)
        } else if (columnConfig.editCondition(form.getFieldsValue(true))) {
            setShow(true)
        } else {
            setShow(false)
        }
    }


    const {queryDict, loading, data: dictData, doGet} = useDict({
        dictInfo: columnConfig.dict,
        asTreeIfNecessary: true,
        paramGetter: (key) => form.getFieldValue(key),
        manual: false,
        useCache: false
    })


    if (queryDict && (columnConfig.dict as DictConfig)?.paramKeys) {
        const onValuesChangeBefore = columnConfig.onValuesChange;
        columnConfig.onValuesChange = (changedValues, allValues) => {
            onValuesChangeBefore?.(changedValues, allValues)
            for (let formItemName of (columnConfig.dict as DictConfig).paramKeys!) {
                if (Object.keys(changedValues).indexOf(formItemName) >= 0) {
                    doGet?.()
                    break;
                }
            }
        }
    }

    if (queryDict && !loading && dictData && position === "edit" && (columnConfig.dict as DictConfig)?.editCondition) {
        dictData.data = (dictData.data as any).filter((columnConfig.dict as DictConfig).editCondition)
    }

    if (queryDict && !loading && dictData?.data && position === "edit" && (columnConfig.dict as DictConfig)?.editExtraData) {
        const editExtraData = (columnConfig.dict as DictConfig)?.editExtraData;

        // 在 react 严格模型下，当前方法会执行两次，从而会导致添加两次 editExtraData
        // 因此，此处需要添加一个 withEditExtraData 来对内容进行添加标识
        if (!(dictData as any)["withEditExtraData"]) {
            dictData.data = editExtraData!.concat(dictData.data);
            (dictData as any)["withEditExtraData"] = true;
        }
    }

    // 在有上下级结构的场景下，设置当前的选项以及子选项为禁用状态
    // 修改时：一个父级菜单不能选择自己、或者自己的子集作为自己的父级
    if (queryDict && !loading && dictData && position === "edit" && data && (columnConfig.dict as DictConfig)?.disabledItemAndChildrenKey) {
        const key = (columnConfig.dict as DictConfig)?.disabledItemAndChildrenKey;
        disabledDictItemAndChildren(dictData.data, data[key!])
    }
    if (queryDict && !loading && dictData?.data) {
        dictData.data = dictData.data.map((item: { [x: string]: any; options: any; }) => {
            const {options, ...rest} = item; // 移除options属性
            return rest;
        });
    }

    function renderInput(column: ColumnConfig) {

        if (queryDict && loading) {
            return (<span>Loading...</span>)
        }

        if (formRenderFactory) {
            const element = formRenderFactory(position, column, form);
            if (element) return element;
        }
        switch (column.form?.type?.toLowerCase()) {
            case "hidden":
                return (
                    <Input type={"hidden"} {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "radio":
                return (
                    <Radio {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "checkbox":
                return (
                    <Checkbox {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "checkboxgroup":
            case "checkbox.group":
            case "checkbox-group":
                return (
                    <Checkbox.Group  {...column.form?.attrs} readOnly={readOnly} options={dictData?.data}/>
                )
            case "rate":
                return (
                    <Rate {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "switch":
                return (
                    <Switch {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "datepicker":
                return (
                    <DatePicker placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "timepicker":
                return (
                    <TimePicker placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "datetimepicker":
                return (
                    <DatePicker showTime placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "rangepicker":
                return (
                    <>
                        {position === "edit" &&
                            <DatePicker placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>}
                        {position === "search" &&
                            <RangePicker placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>}
                    </>
                )
            case "colorpicker":
                return (
                    <ColorPicker placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "cascader":
                return (
                    <Cascader placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "inputnumber":
                return (
                    <InputNumber placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "select":
                return (
                    <Select allowClear showSearch
                            filterOption={(input, option) => {
                                return ((option?.label ?? '') as string).toLowerCase().includes(input.toLowerCase())
                            }}
                            placeholder={column.placeholder} {...column.form?.attrs}
                            options={dictData?.data || column.form?.attrs?.options}
                    />
                )
            case "treeselect":
                return (
                    <TreeSelect allowClear showSearch treeDefaultExpandAll
                                filterTreeNode={(input, treeNode) => {
                                    return ((treeNode?.label ?? '') as string).toLowerCase().includes(input.toLowerCase())
                                }}
                                placeholder={column.placeholder} treeData={dictData?.data}
                                {...column.form?.attrs} />
                )
            case "slider":
                return (
                    <Slider {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "file":
            case "fileuploader":
            case "uploader":
                return (
                    <FileUploader {...column.form?.attrs} readOnly={readOnly}/>
                )
            case "image":
                return (
                    <ImageUploader  {...column.form?.attrs} />
                )
            case "icon":
                return (
                    <AntdIconSelect  {...column.form?.attrs} />
                )
            case "textarea":
                if (position === "search") {
                    return <Input placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}
                                  style={{...column.form?.attrs?.style, cursor: readOnly ? "not-allowed" : ""}}/>
                } else {
                    return (
                        <TextArea placeholder={column.placeholder}  {...column.form?.attrs} readOnly={readOnly}/>
                    )
                }
            case "editor":
                return <AiEditorWrapper {...column.form?.attrs} />
            case "password":
                return <Input.Password placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
            case "opt":
                return <Input.OTP placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
            case "search":
                return <Input.Search placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}/>
            default:
                return (
                    <Input placeholder={column.placeholder} {...column.form?.attrs} readOnly={readOnly}
                           style={{...column.form?.attrs?.style, cursor: readOnly ? "not-allowed" : ""}}/>
                )
        }
    }

    return (
        <Form.Item name={columnConfig.key! as string} label={columnConfig.title! as string}
                   style={{display: isHidden(columnConfig) || !show ? "none" : ""}}
                   rules={position == 'search' ? [] : columnConfig.form?.rules}
                   extra={columnConfig.form?.extra}
                   tooltip={columnConfig.form?.tooltip}
                   initialValue={onValueInit?.(columnConfig.key! as string)}
                   className={`aiadmin-form-item aiadmin-form-${columnConfig.form?.type || 'input'}`}
        >
            {renderInput(columnConfig)}
        </Form.Item>
    );
};


export default DynamicFormItem

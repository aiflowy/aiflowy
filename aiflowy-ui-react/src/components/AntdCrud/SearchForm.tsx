import React, {JSX, useState} from 'react';
import {
    Button,
    Col,
    Form, FormInstance,
    Row,
} from 'antd';
import {ColumnConfig, ColumnsConfig} from "./index";
import {CaretDownOutlined, CaretUpOutlined} from "@ant-design/icons";
import DynamicFormItem from "./DynamicFormItem";
import {convertDayjsToString} from "./dayjsUtil.ts";

const removeEmpty = (obj: any) => {
    const newObj: any = Array.isArray(obj) ? [] : {};
    Object.keys(obj).forEach((key) => {
        if (obj[key] === Object(obj[key])) newObj[key] = removeEmpty(obj[key]);
        else if (obj[key] !== undefined && obj[key] !== null && obj[key] !== '') newObj[key] = obj[key].toString().trim();
    });
    return newObj;
};

const SearchForm: React.FC<{
    onSearch: (values: any) => void,
    onSearchValueInit?: (key: string) => any,
    formRenderFactory?: (position: "edit" | "search", columnConfig: ColumnConfig,form:FormInstance) => JSX.Element | null
    columns: ColumnsConfig<any>
    colSpan: number
}> = ({
          onSearch,
          onSearchValueInit,
          formRenderFactory,
          columns, colSpan
      }) => {

    const [form] = Form.useForm();
    const [showAll, setShowAll] = useState(false);

    const formStyle = {
        maxWidth: 'none',
        paddingTop: 24,
        paddingRight: 8,
    };

    const onFinish = (values: any) => {
        values = convertDayjsToString(values);
        onSearch(removeEmpty(values));
    };

    //支持搜索的数量
    let supportSearchCount = 0;
    columns.map((column) => {
        if (column.supportSearch) supportSearchCount++;
    });

    //第一行显示的表单数据
    const firstLineCount = (24 / colSpan) - 1;
    let firstLineCountVar = firstLineCount;

    //是否显示 "更多" 按钮
    const showMoreButton = supportSearchCount > firstLineCount;
    const justifyContent = supportSearchCount < (firstLineCount) ? "start" : "flex-end";

    return (
        <Form name="form_search" style={formStyle} onFinish={onFinish} form={form}>
            <Row gutter={24}>
                {columns.map((columnConfig) => {
                    return showMoreButton && !showAll
                        ? columnConfig.supportSearch && (firstLineCountVar-- > 0) &&
                        <Col span={colSpan} key={columnConfig.key}>
                            <DynamicFormItem columnConfig={columnConfig} onValueInit={onSearchValueInit}
                                             position="search"
                                             form={form}
                                             formRenderFactory={formRenderFactory}/>
                        </Col>
                        : columnConfig.supportSearch && (
                        <Col span={colSpan} key={columnConfig.key}>
                            <DynamicFormItem columnConfig={columnConfig} onValueInit={onSearchValueInit}
                                             position="search"
                                             form={form}
                                             formRenderFactory={formRenderFactory}/>
                        </Col>
                    )
                })}


                <Col span={colSpan} style={{display: "flex", justifyContent: justifyContent}}
                     offset={!showAll ? (18 - firstLineCount * colSpan) : (24 - ((supportSearchCount + 1) * colSpan) % 24)}>
                    <Button type="primary" htmlType="submit">
                        搜索
                    </Button>
                    <Button style={{margin: '0 5px'}} onClick={() => {
                        form.resetFields();
                        const emptyValues: Record<string, any> = Object.keys(form.getFieldsValue()).reduce(
                            (acc: Record<string, any>, key: string) => {
                                acc[key] = undefined;
                                return acc;
                            },
                            {}
                        );
                        form.setFieldsValue(emptyValues);
                        onSearch(emptyValues);
                    }}>
                        重置
                    </Button>
                    {showMoreButton && (<a onClick={() => {
                        setShowAll(!showAll)
                    }} style={{height: "33px", lineHeight: "33px"}}>
                        {!showAll ? (<><CaretDownOutlined/> 更多</>) : (<><CaretUpOutlined/> 收起</>)}
                    </a>)}
                </Col>
            </Row>
        </Form>
    );
};


export default SearchForm

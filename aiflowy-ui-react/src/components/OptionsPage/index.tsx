import React, {useEffect} from 'react';
import {App, Button, Form,} from "antd";
import {useGetManual, usePostManual} from "../../hooks/useApis.ts";

type Props = {
    children?: React.ReactNode
    style?: React.CSSProperties
}
const OptionsPage: React.FC<Props> = ({children, style}) => {

    const [form] = Form.useForm();
    const {message} = App.useApp();


    const {doGet: loadOptions} = useGetManual("/api/v1/sysOption/list");
    const {loading, doPost: doSave} = usePostManual("/api/v1/sysOption/save");


    useEffect(() => {
        const keys = Object.keys(form.getFieldsValue());
        loadOptions({params: {keys}}).then((resp) => {
            form.setFieldsValue(resp?.data?.data)
        })
    }, []);


    const onFinish = (values: any) => {
        doSave({data: values}).then((res) => {
            if (res.data.errorCode === 0) {
                message.success("数据保存成功")
            }
        })
    };


    return (
        <div style={{padding: "30px 143px 30px 143px", ...style}}>
            <Form
                layout="horizontal"
                form={form}
                labelCol={{span: 2}}
                wrapperCol={{span: 22}}
                onFinish={onFinish}
            >
                {children}
                <div style={{display: "flex", justifyContent: "flex-end", marginTop: "20px"}}>
                    <Form.Item>
                        <Button type="primary" htmlType="submit" loading={loading}>保存</Button>
                    </Form.Item>
                </div>

            </Form>
        </div>
    );
};

export default OptionsPage;

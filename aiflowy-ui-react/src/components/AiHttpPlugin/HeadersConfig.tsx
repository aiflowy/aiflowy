import {Button, Input, Space, Form} from "antd";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import React, {useEffect, useState} from "react";

// 类型定义
export type Header = {
    key: string;
    value: string;
    desc: string;
};
export type HeaderProps = {
    initialHeaders: Header[];
    setHeaderArray: (headers: Header[]) => void;
};
const HeadersConfig: React.FC<HeaderProps> = (props) => {

    const {initialHeaders, setHeaderArray} = props;

    const [headers, setHeaders] = useState<Header[]>(initialHeaders);

    useEffect(() => {
        setHeaders(initialHeaders)
    },[initialHeaders])

    // Headers 操作
    const handleHeaderChange = (index: number, field: keyof Header, value: string) => {
        const newHeaders = [...headers];
        newHeaders[index][field] = value;
        setHeaderArray(newHeaders);
    };

    const addHeader = () => {
        setHeaderArray([...headers, {key: '', value: '', desc: ''}]);
    };

    const removeHeader = (index: number) => {
        const newHeaders = [...headers];
        newHeaders.splice(index, 1);
        setHeaderArray(newHeaders);
    };

    return (
        <>
            <Form.Item label={'请求头'}>
                {initialHeaders.map((header, index) => (
                    <Space key={index} style={{display: 'flex', marginBottom: 8}} align="baseline">
                        <Input
                            placeholder="键"
                            value={header.key}
                            onChange={(e) => handleHeaderChange(index, 'key', e.target.value)}
                            style={{width: '120px'}}
                        />
                        <Input
                            placeholder="值"
                            value={header.value}
                            onChange={(e) => handleHeaderChange(index, 'value', e.target.value)}
                        />
                        <Input
                            placeholder="描述"
                            value={header.desc}
                            onChange={(e) => handleHeaderChange(index, 'desc', e.target.value)}
                        />
                        <MinusCircleOutlined onClick={() => removeHeader(index)}/>
                    </Space>
                ))}
                <Button type="dashed" onClick={addHeader} icon={<PlusOutlined/>}>
                    新增请求头
                </Button>
            </Form.Item>
        </>
    );
};

export default HeadersConfig;
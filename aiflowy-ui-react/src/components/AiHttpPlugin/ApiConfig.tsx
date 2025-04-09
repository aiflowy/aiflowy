import React, {useEffect, useState} from 'react'
import {Form, Input, Select, Space} from "antd";


export type ApiConfigProps = {
    method?: string;
    url?: string;
    setMethod: (method: string) => void;
    setUrl: (url: string) => void;
}
const ApiConfig: React.FC<ApiConfigProps> = (props) => {

    const { method, url, setMethod, setUrl} = props;
    const [methodName, setMethodName] = useState<any>(method)
    const [urlName, setUrlName] = useState<any>(url)

    useEffect(() => {
        setMethodName(method)
        setUrlName(url)
    },[method, url])

    const methodChange = (value: string) => {
        //setMethodName(value)
        setMethod(value)
    }

    const urlChange = (value: string) => {
        //setUrlName(value)
        setUrl(value)
    }

    return (
        <>
            <Space>
                <Form.Item label="请求方式">
                    <Select
                        value={methodName}
                        onChange={(value) => methodChange(value)}
                        style={{ width: 120 }}
                    >
                        <Select.Option value="GET">GET</Select.Option>
                        <Select.Option value="POST">POST</Select.Option>
                        <Select.Option value="PUT">PUT</Select.Option>
                        <Select.Option value="DELETE">DELETE</Select.Option>
                        <Select.Option value="PATCH">PATCH</Select.Option>
                    </Select>
                </Form.Item>
                <Form.Item label="请求地址" style={{ flex: 1 }}>
                    <Input
                        placeholder="请输入请求地址"
                        style={{ width: '500px' }}
                        value={urlName}
                        onChange={(e) => urlChange(e.target.value)}
                    />
                </Form.Item>
            </Space>
        </>
    )
};

export default ApiConfig;
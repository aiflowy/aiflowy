import React, {useEffect, useState} from 'react';
import {Button, Form, Input, Space} from "antd";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";

export type Param = {
    key: string;
    value: string;
    desc: string;
};

export type ParamsConfigProps = {
    initialParams: Param[];
    setParamArray: (params: Param[]) => void;
};

const ParamsConfig: React.FC<ParamsConfigProps> = (props) => {

    const {initialParams, setParamArray} = props;

    const [params, setParams] = useState<Param[]>(initialParams);

    useEffect(() => {
        setParams(initialParams)
    },[initialParams])

    // Params 操作
    const handleParamChange = (index: number, field: keyof Param, value: string) => {
        const newParams = [...params];
        newParams[index][field] = value;
        setParamArray(newParams);
    };

    const addParam = () => {
        setParamArray([...params, { key: '', value: '', desc: '' }]);
    };

    const removeParam = (index: number) => {
        const newParams = [...params];
        newParams.splice(index, 1);
        setParamArray(newParams);
    };

    return(
        <>
            <Form.Item label={'请求参数'}>
                {params.map((param, index) => (
                    <Space key={index} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                        <Input
                            placeholder="键"
                            value={param.key}
                            onChange={(e) => handleParamChange(index, 'key', e.target.value)}
                            style={{ width: '120px' }}
                        />
                        <Input
                            placeholder="值"
                            value={param.value}
                            onChange={(e) => handleParamChange(index, 'value', e.target.value)}
                        />
                        <Input
                            placeholder="描述"
                            value={param.desc}
                            onChange={(e) => handleParamChange(index, 'desc', e.target.value)}
                        />
                        <MinusCircleOutlined onClick={() => removeParam(index)} />
                    </Space>
                ))}
                <Button type="dashed" onClick={addParam} icon={<PlusOutlined />}>
                    新增参数
                </Button>
            </Form.Item>
        </>
    )
}

export default ParamsConfig;
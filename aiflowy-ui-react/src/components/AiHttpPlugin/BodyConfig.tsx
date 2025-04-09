import React, {useEffect, useState} from 'react';
import {Button, Form, Input, Radio, Select, Space} from "antd";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";

export type FormDataItem = {
    key: string;
    type: 'text' | 'file';
    value: string;
    file?: File;
};

export type UrlEncodedItem = {
    key: string;
    value: string;
};

export type BodyContent = {
    'none': null;
    'form-data': FormDataItem[];
    'x-www-form-urlencoded': UrlEncodedItem[];
    'JSON': string;
    'raw': string;
    // 'binary': File | null;
};

export type BodyConfigProps = {
    type: BodyType;
    content: null| FormDataItem[] | UrlEncodedItem[] | string;
    setBodyConfig: (type: BodyType, content: null| FormDataItem[] | UrlEncodedItem[] | string) => void;
};

export type BodyType = 'none' | 'form-data' | 'x-www-form-urlencoded' | 'JSON' | 'raw';
const bodyTypes: { label: string; value: BodyType }[] = [
    { label: 'none', value: 'none' },
    { label: 'form-data', value: 'form-data' },
    { label: 'x-www-form-urlencoded', value: 'x-www-form-urlencoded' },
    { label: 'JSON', value: 'JSON' },
    { label: 'raw', value: 'raw' },
    // { label: 'binary', value: 'binary' },
];

const BodyConfig: React.FC<BodyConfigProps> = (props) => {

    const {type, content, setBodyConfig} = props;

    const initData = {
        'none': null,
        'form-data': [],
        'x-www-form-urlencoded': [],
        'JSON': '',
        'raw': ''
    }

    useEffect(() => {
        setBodyType(type)
        setBodyContent({
            ...bodyContent,
            [type]: content
        })
    },[type,content])

    const [bodyContent, setBodyContent] = useState<BodyContent>(initData);
    const [bodyType, setBodyType] = useState<BodyType>(type);

    // Body 内容操作
    const handleBodyContentChange = <K extends keyof BodyContent>(
        type: K,
        value: BodyContent[K]
    ) => {
        setBodyContent(prev => ({
            ...prev,
            [type]: value
        }));
        setBodyConfig(type, value);
    };

    // 监听 bodyType 变化
    useEffect(() => {
        setBodyConfig(bodyType, bodyContent[bodyType])
    }, [bodyType]);

    // Form-data 操作
    const addFormDataField = () => {
        handleBodyContentChange('form-data', [
            ...bodyContent['form-data'],
            { key: '', type: 'text', value: '' }
        ]);
    };

    const removeFormDataField = (index: number) => {
        const newData = [...bodyContent['form-data']];
        newData.splice(index, 1);
        handleBodyContentChange('form-data', newData);
    };

    const handleFormDataChange = (index: number, field: keyof FormDataItem, value: string | 'text' | 'file') => {
        const newData = [...bodyContent['form-data']];
        // @ts-ignore
        newData[index][field] = value;
        handleBodyContentChange('form-data', newData);
    };

    // const handleFileUpload = (index: number, file: File) => {
    //     const newData = [...bodyContent['form-data']];
    //     newData[index].file = file;
    //     newData[index].value = file.name;
    //     handleBodyContentChange('form-data', newData);
    // };

    // x-www-form-urlencoded 操作
    const addUrlEncodedField = () => {
        handleBodyContentChange('x-www-form-urlencoded', [
            ...bodyContent['x-www-form-urlencoded'],
            { key: '', value: '' }
        ]);
    };

    const removeUrlEncodedField = (index: number) => {
        const newData = [...bodyContent['x-www-form-urlencoded']];
        newData.splice(index, 1);
        handleBodyContentChange('x-www-form-urlencoded', newData);
    };

    const handleUrlEncodedChange = (index: number, field: keyof UrlEncodedItem, value: string) => {
        const newData = [...bodyContent['x-www-form-urlencoded']];
        newData[index][field] = value;
        handleBodyContentChange('x-www-form-urlencoded', newData);
    };

    const renderBodySection = () => {
        switch (bodyType) {
            case "none":
                break;
            case 'form-data':
                return (
                    <div style={{ marginTop: 16 }}>
                        {bodyContent['form-data'].map((item, index) => (
                            <Space key={index} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                <Input
                                    placeholder="键"
                                    value={item.key}
                                    onChange={(e) => handleFormDataChange(index, 'key', e.target.value)}
                                    style={{ width: '120px' }}
                                />
                                <Select
                                    value={item.type}
                                    onChange={(value) => handleFormDataChange(index, 'type', value)}
                                    style={{ width: '100px' }}
                                >
                                    <Select.Option value="text">text</Select.Option>
                                    <Select.Option value="file">file</Select.Option>
                                </Select>
                                {/*{item.type === 'text' ? (
                                    <Input
                                        placeholder="值"
                                        value={item.value}
                                        onChange={(e) => handleFormDataChange(index, 'value', e.target.value)}
                                    />
                                ) : (
                                    <Upload
                                        beforeUpload={(file) => {
                                            handleFileUpload(index, file);
                                            return false;
                                        }}
                                        showUploadList={false}
                                    >
                                        <Button icon={<UploadOutlined />}>
                                            {item.value || 'Select File'}
                                        </Button>
                                    </Upload>
                                )}*/}
                                <Input
                                    placeholder="值"
                                    value={item.value}
                                    onChange={(e) => handleFormDataChange(index, 'value', e.target.value)}
                                />
                                <MinusCircleOutlined onClick={() => removeFormDataField(index)} />
                            </Space>
                        ))}
                        <Button type="dashed" onClick={addFormDataField} icon={<PlusOutlined />}>
                            添加字段
                        </Button>
                    </div>
                );

            case 'x-www-form-urlencoded':
                return (
                    <div style={{ marginTop: 16 }}>
                        {bodyContent['x-www-form-urlencoded'].map((item, index) => (
                            <Space key={index} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                <Input
                                    placeholder="键"
                                    value={item.key}
                                    onChange={(e) => handleUrlEncodedChange(index, 'key', e.target.value)}
                                    style={{ width: '120px' }}
                                />
                                <Input
                                    placeholder="值"
                                    value={item.value}
                                    onChange={(e) => handleUrlEncodedChange(index, 'value', e.target.value)}
                                />
                                <MinusCircleOutlined onClick={() => removeUrlEncodedField(index)} />
                            </Space>
                        ))}
                        <Button type="dashed" onClick={addUrlEncodedField} icon={<PlusOutlined />}>
                            添加字段
                        </Button>
                    </div>
                );

            case 'JSON':
                return (
                    <div style={{ marginTop: 16 }}>
                        <TextArea
                            rows={8}
                            value={bodyContent['JSON']}
                            onChange={(e) => handleBodyContentChange('JSON', e.target.value)}
                            style={{ fontFamily: 'monospace' }}
                        />
                    </div>
                );

            case 'raw':
                return (
                    <div style={{ marginTop: 16 }}>
                        <TextArea
                            rows={8}
                            value={bodyContent['raw']}
                            onChange={(e) => handleBodyContentChange('raw', e.target.value)}
                        />
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <>
            <Form.Item label={'请求体'}>
                <Radio.Group
                    optionType="button"
                    buttonStyle="solid"
                    options={bodyTypes}
                    value={bodyType}
                    onChange={(e) => setBodyType(e.target.value)}
                />
                {renderBodySection()}
            </Form.Item>
        </>
    );
};

export default BodyConfig
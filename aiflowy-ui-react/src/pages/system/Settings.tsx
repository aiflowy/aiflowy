import React, {useEffect} from 'react';
import {Alert, Form, Input, Select} from "antd";
import FormTitle from "../../components/FormTitle";
import OptionsPage from "../../components/OptionsPage";
import {useLayout} from "../../hooks/useLayout.tsx";

const Settings: React.FC = () => {
    const {setOptions} = useLayout();
    useEffect(() => {
        setOptions({
            showBreadcrumb: true,
            breadcrumbs: [
                {title: '首页'},
                {title: '系统设置'}
            ],
        })

        return () => {
            setOptions({
                showBreadcrumb: true,
                breadcrumbs: []
            })
        }
    }, [])
    return (
        <OptionsPage style={{
            border: "1px solid #eee",
            height: 'calc(100vh - 68px)',
            overflowY: 'auto'}}>
            <FormTitle style={{marginTop: "0"}}>大模型配置</FormTitle>

            <Form.Item label="对话模型" name="model_of_chat">
                <Select>
                    <Select.Option value="openai">OpenAI</Select.Option>
                    <Select.Option value="ollama">Ollama</Select.Option>
                    <Select.Option value="chatglm">智普 AI （ChatGLM） </Select.Option>
                    <Select.Option value="spark">星火大模型</Select.Option>
                    <Select.Option value="yiyan">文心一言</Select.Option>
                    <Select.Option value="tongyi">通义大模型</Select.Option>
                </Select>
            </Form.Item>

            <FormTitle>ChatGPT 配置</FormTitle>

            <Form.Item wrapperCol={{span: 12, offset: 4}}>
                <Alert
                    message="注意：有很多大模型（比如：暗月之面等）都是兼容 ChatGPT 的，因此可以在这里进行配置。"
                    type="info"/>
            </Form.Item>

            <Form.Item label="Endpoint" name="chatgpt_endpoint">
                <Input placeholder="请输入 ChatGPT 的服务域名"/>
            </Form.Item>

            <Form.Item label="ApiKey" name="chatgpt_api_key">
                <Input placeholder="请输入 ChatGPT 的 Api Key"/>
            </Form.Item>

            <Form.Item label="模型名称" name="chatgpt_model_name">
                <Input placeholder="请输入 ChatGPT 的 模型名称"/>
            </Form.Item>


            <FormTitle>Ollama 配置</FormTitle>

            <Form.Item label="Endpoint" name="ollama_endpoint">
                <Input placeholder="请输入 Ollama 的服务域名"/>
            </Form.Item>

            <Form.Item label="模型名称" name="ollama_model_name">
                <Input placeholder="请输入 Ollama 的 模型名称"/>
            </Form.Item>




            <FormTitle>智普 AI 配置</FormTitle>


            <Form.Item wrapperCol={{span: 12, offset: 4}}>
                <Alert
                    message="注意：智普的 API Key 同时包含 “用户标识 id” 和 “签名密钥 secret”，即格式为 {id}.{secret}"
                    type="info"/>
            </Form.Item>

            <Form.Item label="ApiKey" name="chatglm_api_key" extra={<>智普 AI 的 Api Key 请去
                <a href='https://open.bigmodel.cn/usercenter/apikeys'
                   target='_blank'>https://open.bigmodel.cn/usercenter/apikeys</a> 获取，同时保证其账户还有可用余额。</>}>
                <Input placeholder="请输入智普 AI 的 Api Key"/>
            </Form.Item>

            <FormTitle>星火大模型配置</FormTitle>

            <Form.Item wrapperCol={{span: 12, offset: 4}}>
                <Alert message={<>注意：以下的星火大模型内容请到网址
                    <a href='https://console.xfyun.cn/services/bm35'
                       target='_blank'>https://console.xfyun.cn/services/bm35</a> 获取</>}
                       type="info"/>
            </Form.Item>

            <Form.Item label="AppId" name="spark_ai_app_id">
                <Input placeholder="请输入星火大模型 AppId"/>
            </Form.Item>

            <Form.Item label="ApiKey" name="spark_ai_api_key">
                <Input placeholder="请输入星火大模型 ApiKey"/>
            </Form.Item>

            <Form.Item label="ApiSecret" name="spark_ai_app_secret">
                <Input placeholder="请输入星火大模型 ApiSecret"/>
            </Form.Item>

            <Form.Item label="星火大模型版本" name="spark_ai_version">
                <Select>
                    <Select.Option value="v3.5">v3.5</Select.Option>
                    <Select.Option value="v3.0">v3.0</Select.Option>
                    <Select.Option value="v2.0">v2.0</Select.Option>
                    <Select.Option value="v1.5">v1.5</Select.Option>
                </Select>
            </Form.Item>

        </OptionsPage>
    );
};


export default {
    path: "sys/settings",
    element: Settings
};
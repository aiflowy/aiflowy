import React, {useEffect, useState} from 'react';
import { useLayout } from "../../../hooks/useLayout.tsx";
import { useLocation } from "react-router-dom";
import { Collapse, Spin } from "antd";
import { usePost } from "../../../hooks/useApis.ts";
import './less/pluginToolEdit.less'
import {EditOutlined, SettingOutlined} from "@ant-design/icons";

const text = `
  A dog is a type of domesticated animal.
  Known for its loyalty and faithfulness,
  it can be found as a welcome guest in many households across the world.
`

const PluginToolEdit: React.FC = () => {
    const { setOptions } = useLayout();
    const location = useLocation();
    const { id, pluginTitle, pluginToolTitle } = location.state || {};
    const { result: pluginToolInfo, doPost: doPostSearch, loading } = usePost('/api/v1/aiPluginTool/tool/search');
    const [showLoading, setShowLoading] = useState(true);
    useEffect(() => {
        setOptions({
            showBreadcrumb: true,
            breadcrumbs: [
                { title: '首页' },
                { title: '插件', href: `/ai/plugin` },
                { title: pluginTitle, href: `/ai/plugin` },
                { title: pluginToolTitle, href: `/ai/pluginTool` },
                { title: '修改' },
            ],
        });

        doPostSearch({
            data: {
                aiPluginToolId: id
            }
        }).then(() => {
            // 即使数据加载完成，仍然保持 loading 状态 1 秒
            setTimeout(() => {
                setShowLoading(false);
            }, 1000); // 1000ms = 1秒
        });

        return () => {
            setOptions({
                showBreadcrumb: true,
                breadcrumbs: []
            });
        };
    }, []);

    const onChange = (key: string | string[]) => {
        console.log(key);
    };

    // 如果正在加载，直接返回 null，不渲染任何内容
    if (showLoading) {
        return  (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Spin size="large"/>
            </div>
        )

    }

    const editPluginTool = (index: string) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }} onClick={(event) => {
            // If you don't want click extra trigger collapse, you can prevent this:
            event.stopPropagation();
        }}>
            <EditOutlined/>
            <span>编辑</span>
        </div>

    );
    const collapseItems = [
        {
            key: '1',
            label: '基本信息',
            children: (
                <div className="basic-info">
                    <div>工具名称:</div>
                    <p>{pluginToolInfo?.data?.data?.name}</p>
                    <div>工具描述:</div>
                    <p>{pluginToolInfo?.data?.data?.description}</p>
                    <div>工具路径:</div>
                    {pluginToolInfo?.data?.data?.basePath ? (
                        <p>{pluginToolInfo?.data?.aiPlugin.baseUrl}/{pluginToolInfo?.data?.data?.basePath}</p>
                    ) : (
                        <p>{pluginToolInfo?.data?.aiPlugin.baseUrl}/{pluginToolInfo?.data?.data?.name}</p>
                    )}
                    <div>请求方法:</div>
                    <p>{pluginToolInfo?.data?.data.requestMethod}</p>
                </div>
            ),
            extra: editPluginTool('1')
        },
        {
            key: '2',
            label: '配置输入参数',
            children: <p>{text}</p>,
            extra: editPluginTool('2')

        },
        {
            key: '3',
            label: '配置输出参数',
            children: <p>{text}</p>,
            extra: editPluginTool('3')

        },
    ];

    return (
        <div style={{ backgroundColor: '#F5F5F5' }}>
            <Collapse
                bordered={false}
                defaultActiveKey={['1', '2', '3']}
                onChange={onChange}
                items={collapseItems.map(item => ({
                    ...item,
                    style: {
                        header: { backgroundColor: '#F7F7FA' },
                        body: { backgroundColor: '#F5F5F5' },
                    },
                }))}
            />
        </div>
    );
};

export default {
    path: "/ai/pluginToolEdit",
    element: PluginToolEdit
};
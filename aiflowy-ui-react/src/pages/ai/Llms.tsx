import React, {useState} from 'react';
import TreePage from "../../components/TreePage";
import CrudPage from "../../components/CrudPage";
import {ColumnsConfig} from "../../components/AntdCrud";
import {Tag} from "antd";


const columns: ColumnsConfig<any> = [
    {
        title: '标识',
        key: 'id',
        hidden: true,
        form: {
            type: "hidden"
        }
    },
    {
        title: 'Icon',
        dataIndex: 'icon',
        key: 'icon',
        form: {
            type: "image",
        },
        render: (text: any) => <img src={text} alt="" style={{width: 30, height: 30}}/>,
    },
    {
        title: '名称',
        dataIndex: 'title',
        key: 'title',
        placeholder: "请输入名称",
        supportSearch: true,
        form: {
            type: "input",
            rules: [{required: true, message: '请输入名称'}],
        }
    },
    {
        title: '品牌',
        dataIndex: 'brand',
        key: 'brand',
        supportSearch: true,
        hidden: true,
        dict: '/api/v1/aiLlmBrand/list?asTree=true',
        form: {
            type: "select",
            attrs: {
                fieldNames: {
                    label: "title",
                    value: "key"
                }
            },
            rules: [{required: true, message: '请选择品牌'}]
        }
    },
    {
        title: '请求地址',
        dataIndex: 'llmEndpoint',
        key: 'llmEndpoint',
        hidden: true,
    },
    {
        title: '模型名称',
        dataIndex: 'llmModel',
        key: 'llmModel',
        hidden: true,
        form: {
            type: "input",
            rules: [{required: true, message: '请输入模型名称'}],
        }
    },
    {
        title: 'API Key',
        dataIndex: 'llmApiKey',
        key: 'llmApiKey',
        hidden: true,
    },
    {
        title: '其他配置',
        dataIndex: 'llmExtraConfig',
        key: 'llmExtraConfig',
        hidden: true,
        form: {
            type: "textarea",
            extra: "此项已 property 形式进行配置，例如：appId=123456",
            attrs: {
                rows: 3
            }
        }
    },
    {
        title: '能力',
        dataIndex: 'supportFeatures',
        key: 'supportFeatures',
        editCondition: () => false,
        render: (features: any) => {
            if (!features) {
                return ''
            }

            return features.map((feature: any) => <Tag key={feature}>{feature}</Tag>)
        },
    },
    {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        width: "20%",
        form: {
            type: "TextArea",
            attrs: {
                rows: 3
            }
        }
    },
    {
        title: '对话模型',
        dataIndex: 'supportChat',
        key: 'supportChat',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '方法调用',
        dataIndex: 'supportFunctionCalling',
        key: 'supportFunctionCalling',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '向量化',
        dataIndex: 'supportEmbed',
        key: 'supportEmbed',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '重排',
        dataIndex: 'supportReranker',
        key: 'supportReranker',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '文生图',
        dataIndex: 'supportTextToImage',
        key: 'supportTextToImage',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '图生图',
        dataIndex: 'supportImageToImage',
        key: 'supportImageToImage',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '文生音频',
        dataIndex: 'supportTextToAudio',
        key: 'supportTextToAudio',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '音频转音频',
        dataIndex: 'supportAudioToAudio',
        key: 'supportAudioToAudio',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '文生视频',
        dataIndex: 'supportTextToVideo',
        key: 'supportTextToVideo',
        hidden: true,
        form: {type: 'switch'}
    },
    {
        title: '图片转视频',
        dataIndex: 'supportImageToVideo',
        key: 'supportImageToVideo',
        hidden: true,
        form: {type: 'switch'}
    },
];


const Llms: React.FC<{ paramsToUrl: boolean }> = () => {

    const [groupId, setGroupId] = useState('')

    return (
        <TreePage treeTableAlias="aiLlmBrand"
                  treeEditable={false}
                  treeTitleIconRender={(item) => {
                      if (typeof item.icon === "string" && (item.icon.startsWith("http") || item.icon.startsWith("/"))) {
                          return <img src={item.icon} alt={item.title} style={{width: 14, height: 14}}/>
                      }
                      return React.isValidElement(item.icon)
                          ? item.icon
                          : <div dangerouslySetInnerHTML={{__html: item.icon || ""}} style={{padding: "3px"}}/>
                  }}
                  onTreeSelect={setGroupId}>
            <CrudPage columnsConfig={columns} tableAlias="aiLlm" params={{brand: groupId}} key={groupId}
                      editLayout={{openType: "modal"}}/>
        </TreePage>
    )
};

export default {
    path: "/ai/llms",
    element: Llms
};

import React, {useEffect, useRef, useState} from 'react';
import TreeClassifiedPage from "../../components/TreeClassifiedPage";
import CrudPage from "../../components/CrudPage";
import {ColumnsConfig} from "../../components/AntdCrud";
import {Form, FormInstance, Input, Select, Switch, Tag} from "antd";
import CustomLeftArrowIcon from "../../components/CustomIcon/CustomLeftArrowIcon.tsx";
import KeywordSearchForm from "../../components/AntdCrud/KeywordSearchForm.tsx";


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
        title: '自定义输入标识',
        key: 'isCustomInput',
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
        title: '供应商',
        dataIndex: 'brand',
        key: 'brand',
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
            rules: [{required: true, message: '请选择模型供应商'}]
        },

    },
    {
        title: '模型名称',
        dataIndex: 'llmModel',
        key: 'llmModel',
        hidden: true,
        form: {
            type: "input",
            rules: [{required: true, message: '请输入模型名称'}],
        },
    },
    {
        title: '请求地址',
        dataIndex: 'llmEndpoint',
        key: 'llmEndpoint',
        hidden: true,
        editCondition:(data) => {
            return data?.brand === "spark" || data?.isCustomInput
        }

    },
    {
        title: 'API Key',
        dataIndex: 'llmApiKey',
        key: 'llmApiKey',
        hidden: true,
    },
    {
        title: 'API Secret',
        dataIndex: 'llmExtraConfig.apiSecret',
        key: 'llmExtraConfig.apiSecret',
        hidden: true,
        form: {
            type: "input",
        },
    },
    {
        title: 'App Id',
        dataIndex: 'llmExtraConfig.appId',
        key: 'llmExtraConfig.appId',
        hidden: true,
        form: {
            type: "input",
        },
    },
    {
        title: 'Version',
        dataIndex: 'llmExtraConfig.version',
        key: 'llmExtraConfig.version',
        hidden: true,
        form: {
            type: "input",
        },
        editCondition: (data) => {
            return data?.brand === "spark";
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
        form: {type: 'switch'},
        editCondition: (data) => {
            console.log(1)
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '方法调用',
        dataIndex: 'supportFunctionCalling',
        key: 'supportFunctionCalling',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '向量化',
        dataIndex: 'supportEmbed',
        key: 'supportEmbed',
        hidden: true,
        form: {
            type: 'switch',
        },
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '重排',
        dataIndex: 'supportReranker',
        key: 'supportReranker',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '文生图',
        dataIndex: 'supportTextToImage',
        key: 'supportTextToImage',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '图生图',
        dataIndex: 'supportImageToImage',
        key: 'supportImageToImage',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '文生音频',
        dataIndex: 'supportTextToAudio',
        key: 'supportTextToAudio',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '音频转音频',
        dataIndex: 'supportAudioToAudio',
        key: 'supportAudioToAudio',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '文生视频',
        dataIndex: 'supportTextToVideo',
        key: 'supportTextToVideo',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '图片转视频',
        dataIndex: 'supportImageToVideo',
        key: 'supportImageToVideo',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
    {
        title: '多模态',
        dataIndex: 'options.multimodal',
        key: 'options.multimodal',
        hidden: true,
        form: {type: 'switch'},
        editCondition: (data) => {
            return data?.brand && data?.isCustomInput;
        },
    },
];

const ModelNameInput: React.FC<{
    treeData: any[];
    columnConfig: any;
    form: FormInstance;
}> = ({
          treeData,
          columnConfig,
          form,
      }) => {
    const [modelOptions, setModelOptions] = useState([]);
    const [isCustomInput, setIsCustomInput] = useState(false);
    // 监听brand字段变化
    const brandValue = Form.useWatch('brand', form);

    useEffect(() => {
        const brandData = treeData.find(brand => brand.key === brandValue);
        const options = brandData?.options.modelList?.map((model: { title: any; llmModel: any; }) => ({
            label: model.title,
            value: model.llmModel
        })) || [];
        setModelOptions(options);
    }, [brandValue, treeData]);

    useEffect(() => {

        const currentValues = form.getFieldsValue();
        const newValues = {
            ...currentValues,
            brand: brandValue,
            isCustomInput: isCustomInput,
        };

        form.setFieldsValue(newValues);

        columns.forEach(column => {

            const key = column.key + "";
            console.log(key,key.startsWith("support"))

            if (key !== "brand"){
                if (key.startsWith("support") || key === "options.multimodal" || key === "llmEndpoint"){
                    column.onValuesChange?.("","");
                }
            }
        })



    }, [isCustomInput, brandValue, form]);

    return (
        <div>
            {isCustomInput ? (
                <Input
                    placeholder={columnConfig.placeholder}
                    {...columnConfig.form?.attrs}
                />
            ) : (
                <Select
                    allowClear
                    showSearch
                    placeholder={columnConfig.placeholder}
                    {...columnConfig.form?.attrs}
                    options={modelOptions}
                    filterOption={(input, option) => {
                        return ((option?.label ?? '') as string).toLowerCase().includes(input.toLowerCase())
                    }}
                />
            )}
            <div style={{marginTop: 8}}>
                <Switch
                    size="small"
                    checked={isCustomInput}
                    onChange={(checked) => {
                        setIsCustomInput(checked);
                    }}
                    checkedChildren="自定义"
                    unCheckedChildren="选择"
                />
            </div>
        </div>
    );
}


const Llms: React.FC<{ paramsToUrl: boolean }> = () => {

    const crudRef = useRef<{ openAddModal: () => void, onSearch: (values: any) => void, formReset: () => void }>(null);

    const [groupId, setGroupId] = useState('')
    const [treeData, setTreeData] = useState<any[]>([]);


    // 自定义表单渲染工厂函数
    const customFormRenderFactory = (position: "edit" | "search", columnConfig: any, form: FormInstance): JSX.Element | null => {
        if (position === 'search') return null;


        if (columnConfig.key === "brand") {
            columnConfig.onValuesChange = (value: any) => {
                const brandData = treeData.find(brand => {
                    return brand.key === value.brand;
                });

                if (brandData) {
                    const options = brandData.options;
                    form.setFieldValue("llmEndpoint", options.llmEndpoint);
                    form.setFieldValue("chatPath", options.chatPath);
                    form.setFieldValue("embedPath", options.embedPath);


                    form.setFieldValue("llmModel", undefined);
                }

            }
        }

        // 自定义模型名称字段
        if (columnConfig.key === 'llmModel') {
            return <ModelNameInput treeData={treeData} columnConfig={columnConfig} form={form}
            />
        }


        return null;
    };

    return (
        <div style={{margin: "24px"}}>
            <KeywordSearchForm
                setIsEditOpen={() => {
                    crudRef.current?.openAddModal()
                }}
                addButtonText="新增大模型"
                columns={columns}
                tableAlias={"aiLlm"}
                onSearch={(values: any) => {
                    crudRef.current?.onSearch(values)
                }}/>

            <TreeClassifiedPage treeTableAlias="aiLlmBrand"
                                treeCardTitle={"接入平台"}
                                treeEditable={false}
                                onTreeDataLoaded={setTreeData}
                                treeTitleIconRender={(item) => {
                                    if (typeof item.icon === "string" && (item.icon.startsWith("http") || item.icon.startsWith("/"))) {
                                        return <img src={item.icon} alt={item.title} style={{width: 14, height: 14}}/>
                                    }
                                    return React.isValidElement(item.icon)
                                        ? item.icon
                                        : <div dangerouslySetInnerHTML={{__html: item.icon || ""}}
                                               style={{padding: "3px"}}/>
                                }}
                                treeCardExtra={
                                    <CustomLeftArrowIcon/>
                                }
                                onTreeSelect={setGroupId}>
                <CrudPage columnsConfig={columns} tableAlias="aiLlm" params={{brand: groupId}} key={groupId}
                          needHideSearchForm={true}
                          editLayout={{openType: "modal"}} ref={crudRef} formRenderFactory={customFormRenderFactory}/>
                {/*<CrudPage columnsConfig={columns} tableAlias="aiLlm" params={{brand: groupId}} key={groupId} needHideSearchForm={true}*/}
                {/*          editLayout={{openType: "modal"}} ref={crudRef}  />*/}
            </TreeClassifiedPage>
        </div>
    )
};

export default {
    path: "/ai/llms",
    element: Llms
};

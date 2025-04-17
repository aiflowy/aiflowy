import React, {useEffect, useState} from 'react';
import CardPage from "../../components/CardPage";
import {ColumnsConfig} from "../../components/AntdCrud";
import {RobotOutlined} from "@ant-design/icons";
import {Form, Modal} from "antd";
import AiHttpPlugin from "../../components/AiHttpPlugin";
import {usePostManual} from "../../hooks/useApis.ts";

//字段配置
const columnsConfig: ColumnsConfig<any> = [
    {
        hidden: true,
        form: {
            type: "hidden"
        },
        dataIndex: "id",
        key: "id"
    },
    {
        title: 'Icon',
        dataIndex: 'icon',
        key: 'icon',
        form: {
            type: "image"
        }
    },
    {
        form: {
            type: "input",
            rules: [{required: true, message: '请输入插件名称'}]
        },
        dataIndex: "pluginName",
        title: "插件名称",
        key: "pluginName",
        supportSearch: true,
        placeholder: "请输入插件名称",

    },

    {
        form: {
            type: "TextArea",
            rules: [{required: true, message: '请输入插件描述'}],
            attrs: {
                rows: 3
            }
        },
        dataIndex: "pluginDesc",
        title: "插件描述",
        key: "pluginDesc"
    },

    {
        hidden: true,
        form: {
            type: "hidden"
        },
        dataIndex: "options",
        title: "插件配置",
        key: "options"
    },

    {
        form: {
            type: "select"
        },
        dataIndex: "status",
        title: "数据状态",
        key: "status",
        dict: {
            name: "dataStatus"
        }
    },
];

const Plugins: React.FC<{ paramsToUrl: boolean }> = () => {

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [configJson, setConfigJson] = useState('{}')
    const [currentItem, setCurrentItem] = useState<any>({});
    const {loading: saveLoading, doPost: savePlugin} = usePostManual(`/api/v1/aiPlugins/update`);

    const configInit = {
        method: "",
        url: "",
        headers: [],
        params: [],
        body: {
            type: "none",
            content: null
        },
    }

    const [initValue, setInitValue] = useState<any>(configInit)

    useEffect(() => {
        if (currentItem.options) {
            setInitValue(JSON.parse(currentItem.options))
        } else {
            setInitValue(configInit)
        }
    }, [currentItem])

    const showModal = (item: any) => {
        setCurrentItem(item)
        setIsModalOpen(true);
    };

    const handleOk = () => {
        const data = {
            id: currentItem.id,
            options: configJson
        }
        // console.log('update', data)
        savePlugin({
            data: data
        }).then(() => {
            setIsModalOpen(false)
            window.location.reload()
        })
    };

    const handleCancel = () => {
        setIsModalOpen(false);
    };

    const setConfig = (attrs: any) => {
        //console.log('setConfig', attrs)
        setConfigJson(JSON.stringify(attrs))
    }

    const [form] = Form.useForm();

    return (
        <>
            <Modal title="编辑插件"
                   width={800}
                   open={isModalOpen}
                   onOk={handleOk}
                   onCancel={handleCancel}
                   confirmLoading={saveLoading}
            >
                <Form
                    layout="vertical"
                    form={form}
                    initialValues={{}}
                >
                    <AiHttpPlugin attrs={initValue} setAttrs={(attrs) => {
                        setConfig(attrs)
                    }}/>
                </Form>
            </Modal>
            <CardPage tableAlias={"aiPlugins"}
                      editModalTitle={"新增/编辑插件"}
                      columnsConfig={columnsConfig}
                      addButtonText={"新增插件"}
                      avatarKey="icon"
                      titleKey="pluginName"
                      descriptionKey="pluginDesc"
                      defaultAvatarSrc={"/favicon.png"}
                      editLayout={{labelWidth: 80}}
                      customActions={(item, existNodes) => {
                          return [
                              <RobotOutlined title="设计插件" onClick={() => showModal(item)}/>,
                              ...existNodes
                          ]
                      }}
            />
        </>
    )
};

export default {
    path: "/ai/plugins",
    element: Plugins
};

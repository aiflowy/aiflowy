import React, {useState} from 'react';
import {MenuUnfoldOutlined, SearchOutlined} from "@ant-design/icons";
import {ColumnsConfig} from "../../components/AntdCrud";
import CardPage from "../../components/CardPage";
import {Button, Form, Input, Modal, Space} from "antd";
import {useGetManual} from "../../hooks/useApis.ts";
import PreviewContainer from "./aiKnowledge/PreviewContainer.tsx";


const columnsColumns: ColumnsConfig<any> = [
    {
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
        }
    },
    {
        title: '名称',
        dataIndex: 'title',
        key: 'title',
        placeholder: "请输入知识库名称",
        supportSearch: true,
        form: {
            rules: [{required: true, message: '请输入知识库名称'}]
        }
    },
    {
        title: '英文名称',
        dataIndex: 'englishName',
        key: 'englishName',
        placeholder: "请输入英文名称, 不允许含中文",
        supportSearch: true,
        form: {
            rules: [{required: true, message: '必须由 a-z、A-Z、0-9 字符组成，或包含下划线和连字符，最大长度为 64 个字符', pattern: /^[a-zA-Z0-9_-]{1,64}$/}]
        }
    },
    {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        width: "50%",
        form: {
            type: "TextArea",
            attrs: {
                rows: 3,
                placeholder: "请输入知识库描述, 便于大模型更好理解并调用"
            },
            rules: [{required: true, message: '请输入知识库描述'}]

        }
    },
    {
        title: '是否启用向量数据库',
        dataIndex: 'vectorStoreEnable',
        key: 'vectorStoreEnable',
        form: {
            type: 'Switch'
        }
    },
    {
        title: '向量数据库类型',
        dataIndex: 'vectorStoreType',
        key: 'vectorStoreType',
        form: {
            type: 'select',
            attrs: {
                options: [
                    {value: 'milvus', label: 'Milvus'},
                    {value: 'redis', label: 'Redis'},
                    {value: 'opensearch', label: 'OpenSearch'},
                    {value: 'elasticsearch', label: 'ElasticSearch'},
                    {value: 'aliyun', label: '阿里云'},
                    {value: 'qcloud', label: '腾讯云'},
                ]
            },
            rules: [{required: true, message: '请选择向量数据库类型'}]

        }
    },

    {
        title: '向量数据库配置',
        dataIndex: 'vectorStoreConfig',
        key: 'vectorStoreConfig',
        form: {
            tooltip: (
                <span>
                     <p style={{ marginBottom: 8, width: 300 }}>数据库配置说明：</p>
                     <p>1.  Redis数据库,建议使用docker安装redis-stack向量数据库,普通redis不支持向量化</p>
                     <p style={{ fontSize: 12}}>uri=redis://:pwssword@127.0.0.1:6379</p>
                     <p>2.  ElasticSearch 数据库</p>
                     <span style={{ fontSize: 12}}>serverUrl=http://127.0.0.1:9200</span><br/>
                     <span style={{ fontSize: 12}}>username=elastic</span><br/>
                     <span style={{ fontSize: 12}}>password=123456</span><br/>

                </span>
            ),
            type: 'TextArea',
            extra: (<a target="_blank" href="https://aiflowy.tech/zh/development/ai/knowledge.html#redis向量数据库" style={{ fontSize: 12}}>更多配置参考地址</a>),
            attrs: {
                rows: 5
            },
            rules: [{required: true, message: '请输入向量数据库配置'}]
        }
    },

    {
        title: '向量数据库集合',
        dataIndex: 'vectorStoreCollection',
        key: 'vectorStoreCollection',
        placeholder: '只能包含字母、数字和下划线,且长度在3-20个字符之间',
        form: {
            type: 'input',
            rules: ([
                    {required: true, message: '请输入向量数据库配置'}
                ,
                {
                    pattern: /^[a-zA-Z0-9_]{3,20}$/,  // 正则表达式
                    message: '向量数据库集合只能包含字母、数字和下划线,且长度在3-20个字符之间',  // 校验失败提示
                    validateTrigger: ['blur']
                }
            ]),
        },

    },

    {
        title: 'Embedding 模型',
        dataIndex: 'vectorEmbedLlmId',
        key: 'vectorEmbedLlmId',
        dict: '/api/v1/aiLlm/list?supportEmbed=true',
        editCondition: (data: any) => {
            return data?.options?.canUpdateEmbedding != false;
        },
        form: {
            type: 'select',
            attrs: {
                fieldNames: {
                    label: 'title',
                    value: 'id'
                },
            },
            rules: [{required: true, message: '请选择向量模型'}]
        }
    },
    {
        title: '是否启用搜索引擎',
        dataIndex: 'searchEngineEnable',
        key: 'searchEngineEnable',
        form: {
            type: 'Switch'
        }
    },
    {
        title: '重排模型',
        dataIndex: 'rerankLlmId',
        key: 'rerankLlmId',
        dict: '/api/v1/aiLlm/list?supportReranker=true',
        form: {
            type: 'select',
            attrs: {
                fieldNames: {
                    label: 'title',
                    value: 'id'
                },
            },
            rules: [   ({ getFieldValue }: { getFieldValue: (name: string) => any }) => ({
                required: getFieldValue('searchEngineEnable') === true,
                message: '请选择重排模型'
            })]
        }
    }
];


const Knowledge: React.FC<{ paramsToUrl: boolean }> = () => {

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [form] = Form.useForm();
    const [id, setId] = useState()
    const [searchResult, setSearchResult] = useState<any[]>()

    const {doGet, loading: searchLoading} = useGetManual("/api/v1/aiKnowledge/search");

    const onFinish = (values: any) => {
        doGet({
            params: {
                id,
                ...values
            }
        }).then((resp) => {
            if (resp.data?.data) {
                setSearchResult(resp.data.data)
            } else {
                setSearchResult([]);
            }
        })
    };

    const onClose = () => {
        setIsModalOpen(false)
        setId(undefined)
        setSearchResult([]);
    }

    return (
        <>
            <Modal title="向量检索" open={isModalOpen} onCancel={onClose} footer={""} width={"50%"}
                            destroyOnClose>
                <div style={{minHeight: 500}}>
                    <Form form={form} onFinish={onFinish} preserve={false}>
                        <div style={{display: "flex", gap: 10}}>
                            <Form.Item
                                style={{flexGrow: 1}}
                                name="keyword"
                                rules={[{required: true, message: '请输入关键字!'}]}>
                                <Input placeholder="请输入关键字"/>
                            </Form.Item>
                            <Button type="primary" htmlType="submit">
                                搜索
                            </Button>
                        </div>
                    </Form>
                    <div>
                        <PreviewContainer data={searchResult} loading={searchLoading}
                                          isSearching={true}
                        />
                    </div>
                </div>
            </Modal>
            <CardPage tableAlias={"aiKnowledge"}
                      editModalTitle={"新增/编辑知识库"}
                      columnsConfig={columnsColumns}
                      addButtonText={"新增知识库"}
                      avatarKey="icon"
                      defaultAvatarSrc={"/src/assets/knowledgeIcon.png"}
                      editLayout={{labelWidth: 140}}
                      optionsText={{
                          addCardTitle: "创建知识库",
                          noDataText: "你还没有知识库，快来创建你的知识库吧！",
                          noDataAddButtonText: "创建知识库"
                      }}
                      customActions={(item, existNodes) => {
                          return [
                              <Space onClick={() => {
                                  // window.open(`/ai/knowledge/${item.slug || item.id}`)
                                  window.open(`/ai/knowledge/${item.id}`)
                              }}>
                                  <MenuUnfoldOutlined title="文档列表" />
                                  <span>列表</span>
                              </Space>
                              ,

                              <Space onClick={() => {
                                  setIsModalOpen(true)
                                  setId(item.id)
                              }}>
                              <SearchOutlined title="向量检索" />
                                  <span>检索</span>
                              </Space>
                                  ,
                              ...existNodes
                          ]
                      }}
            />
        </>
    );
};

export default {
    path: "/ai/knowledge",
    element: Knowledge
};

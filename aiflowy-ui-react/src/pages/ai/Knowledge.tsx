import React, {useState} from 'react';
import {MenuUnfoldOutlined, SearchOutlined} from "@ant-design/icons";
import {ColumnsConfig} from "../../components/AntdCrud";
import CardPage from "../../components/CardPage";
import DraggableModal from "../../components/DraggableModal";
import {Button, Form, Input, Spin} from "antd";
import {useGetManual} from "../../hooks/useApis.ts";


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
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        width: "50%",
        form: {
            type: "TextArea",
            attrs: {
                rows: 3
            }
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
            }
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
            }
        }
    },

    {
        title: '向量数据库集合',
        dataIndex: 'vectorStoreCollection',
        key: 'vectorStoreCollection',
    },

    {
        title: 'Embedding 模型',
        dataIndex: 'vectorEmbedLlmId',
        key: 'vectorEmbedLlmId',
        dict: '/api/v1/aiLlm/list?supportEmbed=true',
        form: {
            type: 'select',
            attrs: {
                fieldNames: {
                    label: 'title',
                    value: 'id'
                }
            }
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
            <DraggableModal title="向量检索" open={isModalOpen} onCancel={onClose} footer={""}
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
                        {/*<div>搜索结果:</div>*/}

                        {searchLoading ?  <div style={{width:"100%", textAlign: "center"}}>
                              <Spin tip="Loading...">
                                </Spin>
                                </div>:
                                <div>
                                    {searchResult?.map((item: any, index) => {
                                        return <div key={item?.id || index} style={{margin:"10px 0",background:"#efefef",padding:"5px 10px",borderRadius:"7px"}}>
                                            <h3>相似度:{item.similarityScore}</h3>
                                            <div dangerouslySetInnerHTML={{__html: item?.content}}></div>
                                        </div>
                                    })}
                                </div>
                            }



                    </div>
                </div>
            </DraggableModal>
            <CardPage tableAlias={"aiKnowledge"}
                      editModalTitle={"新增/编辑知识库"}
                      columnsConfig={columnsColumns}
                      addButtonText={"新增知识库"}
                      avatarKey="icon"
                      defaultAvatarSrc={"/favicon.png"}
                      editLayout={{labelWidth: 140}}
                      customActions={(item, existNodes) => {
                          return [
                              <MenuUnfoldOutlined title="文档列表" onClick={() => {
                                  // window.open(`/ai/knowledge/${item.slug || item.id}`)
                                  window.open(`/ai/knowledge/${item.id}`)
                              }}/>,

                              <SearchOutlined title="向量检索" onClick={() => {
                                  setIsModalOpen(true)
                                  setId(item.id)
                              }}/>,
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

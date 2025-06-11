import React, {useEffect, useState} from 'react';
import DocumentMenu from "./DocumentMenu";
import '../style/document.css'
import {Button, Form, Input, message, Popconfirm, Space, Table} from "antd";
import {useDetail, useGet, useGetManual, usePost} from "../../../hooks/useApis";
import {useParams} from 'react-router-dom';
import {convertDatetimeUtil} from "../../../libs/changeDatetimeUtil";
import Modal from "antd/es/modal/Modal";
import FileImportPanel from "./FileImportPanel";
import TextArea from "antd/es/input/TextArea";
import {useLayout} from "../../../hooks/useLayout.tsx";
import PreviewContainer from "./PreviewContainer.tsx";

interface EditTxtBoxState {
    content: string; // 文本内容
    id: string | number | null; // 唯一标识符，可以是字符串、数字或 null
}

type queryParamsType = {
    documentId: string;
};

const Document: React.FC = () => {

    const [pagination, setPagination] = useState({
        current: 1, // 当前页码
        pageSize: 10, // 每页显示条数
        total: 0, // 总记录数
    });
    //documentChunk
    const [paginationCk, setPaginationCk] = useState({
        current: 1, // 当前页码
        pageSize: 10, // 每页显示条数
        total: 0, // 总记录数
    });


    // url 参数
    const params = useParams();

    // 知识库详情
    const {result: knowledge} = useDetail("aiKnowledge", params.id);

    // 手动加载知识库的文档列表
    const {loading, result, doGet:doGetDocumentListManual} = useGet(`/api/v1/aiDocument/documentList`,
        {
            current: pagination.current,
            pageSize: pagination.pageSize,
            id: params.id
        });

    const [queryParams, setQueryParams] = useState<queryParamsType>()

    const {
        doGet: getDocChunk,
        result: docResult,
        loading: chunkLoading
    } = useGet(`/api/v1/aiDocumentChunk/page`, queryParams, {manual: true})

    // documentChunk查看弹框
    const [para] = useState();
    const [isDocChunkModalOpen, setIsDocChunkModalOpen] = useState(false);
    const {doPost, result: resultRemove} = usePost('/api/v1/aiDocument/removeDoc', para)
    const {doPost: doPostChunkRemove} = usePost('/api/v1/aiDocumentChunk/removeChunk', para)
    const {doPost: doPostEditUpdate} = usePost('/api/v1/aiDocumentChunk/update', para, {manual: true})
    // documentChunk弹框绑定的值
    const [isDocChunkContent, setIsDocChunkContent] = useState('');
    const [isConfirmDelete, setIsConfirmDelete] = useState(false);
    const [isTxtBoxEditModalOpen, setIsTxtBoxEditModalOpen] = useState(false);
    const [form] = Form.useForm();
    const [searchResult, setSearchResult] = useState<any[]>()
    const {doGet: doSearchGet, loading: searchLoading} = useGetManual("/api/v1/aiKnowledge/search");
    const onFinish = (values: any) => {
        doSearchGet({
            params: {
                id: params.id,
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

    const columns: any = [
        {
            title: '文件名',
            dataIndex: 'title',
            key: 'title',
            width: 400,
            ellipsis: true
        },
        {
            title: '文件类型',
            dataIndex: 'documentType',
            key: 'documentType',
            width: 150,

        },
        {
            title: '知识库条数',
            dataIndex: 'chunkCount',
            key: 'chunkCount',
            width: 150,

        },
        {
            title: '创建时间',
            dataIndex: 'created',
            key: 'created',
            width: 200,
            render: convertDatetimeUtil

        },
        {
            title: '更新时间',
            dataIndex: 'modified',
            key: 'modified',
            width: 200,
            render: convertDatetimeUtil
        },
        {
            title: '操作',
            key: 'action',
            fixed: 'right',
            width: 150,
            render: (_: any, record: any) => (
                <Space size="middle">
                    <a onClick={() => {
                        setIsOpenDocChunk(!isOpenDocChunk)
                        const param = {
                            pageNumber: 1,
                            pageSize: 10,
                            documentId: record.id,
                            sortKey: 'sorting',
                            sortType: 'asc'
                        }
                        setQueryParams(param)
                    }}>查看 {record.name}</a>
                    {/*<a onClick={() => {*/}
                    {/*    fetchFilePreview(record.id).then(r => console.log(r))*/}
                    {/*}}>预览</a>*/}
                    <a onClick={() => {
                        fetchFileDownload(record).then(r => console.log(r))
                    }}>下载</a>
                    <Popconfirm
                        title="确定删除"
                        description="您确定要删除这条数据吗？"
                        onConfirm={() => {
                            setIsConfirmDelete(true)
                            doPost({data: {id: record.id}}).then((res) => {
                                if (res.data.errorCode === 0) {
                                    message.success('删除成功')
                                } else {
                                    message.error('删除失败')
                                }
                            })
                        }}
                        onCancel={() => {

                        }}
                        okText="确定"
                        cancelText="取消"
                    >
                        <a style={{color: 'red'}}>删除</a>
                    </Popconfirm>

                </Space>
            ),
        },
    ];

    // columnsChunk内容列
    const columnsChunk: any = [
        {
            title: '顺序',
            dataIndex: 'sorting',
            key: 'sorting',
            align: 'center',
            width: 150,
            ellipsis: true
        },
        {
            title: '内容',
            dataIndex: 'content',
            key: 'content',
            align: 'center',
            width: 1000,
            ellipsis: true
        },
        {
            title: '操作',
            key: 'action',
            fixed: 'right',
            width: 150,
            align: 'center',
            render: (_: any, record: any) => (
                <Space size="middle">
                    <a onClick={() => {
                        setIsDocChunkContent(record.content)
                        setIsDocChunkModalOpen(true)
                    }}>查看 {record.name}</a>
                    <a onClick={() => {
                        setEditTxtBoxValue({content: record.content, id: record.id})
                        setIsTxtBoxEditModalOpen(true)
                    }}>修改 {record.name}</a>
                    <Popconfirm
                        title="确定删除"
                        description="您确定要删除这条数据吗？"
                        onConfirm={() => {
                            doPostChunkRemove({data: {id: record.id}}).then((resp) => {
                                if (resp.data.errorCode === 0) {
                                    message.success('删除成功')
                                    getDocChunk()
                                } else {
                                    message.error('删除失败')
                                }
                            })
                        }}
                        onCancel={() => {

                        }}
                        okText="确定"
                        cancelText="取消"
                    >
                        <a style={{color: 'red'}}>删除</a>
                    </Popconfirm>

                </Space>
            ),
        },
    ];
// 定义菜单项
    const menuItems = [
        {id: 'file-management', label: '文件管理'},
        {id: 'file-import', label: '文件导入'},
        {id: 'search-test', label: '检索测试'},
    ];

    // 定义状态
    const [editTxtBoxValue, setEditTxtBoxValue] = useState<EditTxtBoxState>({
        content: '', // 初始内容为空字符串
        id: null,    // 初始 ID 为 null
    });
    // 标识是否打开文档块
    const [isOpenDocChunk, setIsOpenDocChunk] = useState<boolean>(false);
    const [fileName, setFileName] = useState<string>('');

    // 状态管理：当前选中的菜单项
    const [selectedMenu, setSelectedMenu] = useState<string | null>('file-management');



    const getDocumentList = () => {
        doGetDocumentListManual({
            params: {
                current: 1,
                pageSize: 10,
                id: params.id
            }
        }).then(res => {
            setPagination({
                current: res.data.data.pageNumber, // 当前页码
                pageSize: res.data.data.pageSize, // 每页显示条数
                total: res.data.data.totalRow, // 总记录数
            })
            if (result) {
                result.data.records = res.data?.data?.records
            }
        })
    }
    useEffect(() => {
        if (queryParams?.documentId) {
            getDocChunk().then(res => {
                setPaginationCk({
                    current: res.data.data.pageNumber, // 当前页码
                    pageSize: res.data.data.pageSize, // 每页显示条数
                    total: res.data.data.totalRow, // 总记录数
                })
            })
        }
        if (resultRemove) {
            if (resultRemove.errorCode === 0 && isConfirmDelete) {
                setIsConfirmDelete(false)
                getDocumentList()
            }
        }
    }, [result, queryParams, resultRemove]); // 仅在 count 变化时执行
    // 分页变化时触发
    const handleTableChange = (newPagination: any) => {
        const {current, pageSize} = newPagination;
        doGetDocumentListManual().then(res => {
            setPagination({
                current: current, // 当前页码
                pageSize: pageSize, // 每页显示条数
                total: res.data.data.totalRow, // 总记录数
            })
            if (result) {
                result.data.records = res.data?.records;
            }
        })
    };

    const handleTableChangeCk = (newPagination: any) => {
        const {current, pageSize} = newPagination;
        setPaginationCk({
            current: current, // 当前页码
            pageSize: pageSize, // 每页显示条数
            total: 0, // 总记录数
        })
        const param = {
            pageNumber: current,
            pageSize: pageSize,
            documentId: queryParams?.documentId || '',
            sortKey: 'sorting',
            sortType: 'asc'
        }
        setQueryParams(param)
    };
    // 处理输入框变化
    const handleChange = (e: any) => {
        setFileName(e.target.value); // 更新状态
    };
    const handleSearch = () => {
        doGetDocumentListManual({
            params: {
                'fileName': fileName,
                current: pagination.current,
                pageSize: pagination.pageSize,
                id: params.id
            }
        }).then(res => {
            if (result) {
                result.data = res.data.data
            }

            setPagination({
                current: pagination.current, // 当前页码
                pageSize: pagination.pageSize, // 每页显示条数
                total: res.data.data.totalRow, // 总记录数
            })
        })
    }
    // 文件下载
    const fetchFileDownload = async (record: any) => {
        // s3 下载文件方法
        if (record?.documentPath.startsWith('http')){
            try {

                // 使用 fetch 获取文件流
                const response = await fetch(record?.documentPath);
                if (!response.ok) {
                    console.error('文件下载失败');
                }

                // 获取文件名（从响应头 Content-Disposition 中提取）
                const disposition = response.headers.get('Content-Disposition');
                let fileName = `${record?.title}.${record?.documentType}`; // 默认文件名
                if (disposition && disposition.indexOf('filename=') !== -1) {
                    const fileNameMatch = disposition.match(/filename="?([^"]+)"?/);
                    if (fileNameMatch != null){
                        if (fileNameMatch.length > 1) {
                            fileName = decodeURIComponent(fileNameMatch[1]); // 解码中文文件名
                        }
                    }

                }

                // 获取 Blob 数据
                const blob = await response.blob();

                // 创建 a 标签进行下载
                const downloadUrl = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = downloadUrl;
                link.setAttribute('download', fileName); // 设置下载的文件名
                document.body.appendChild(link);
                link.click();

                // 清理
                link.remove();
                window.URL.revokeObjectURL(downloadUrl);
            } catch (error) {
                message.error('文件下载失败');
            }
        }
        else {
            // 本地下下载
            try {
                const response = await fetch(`/api/v1/aiDocument/download?documentId=${record?.id}`, {
                    method: 'GET',
                    headers: {
                        // 如果需要 token 验证：
                        'Authorization': `${localStorage.getItem('authKey')}`
                    },
                });
                if (!response.ok) console.log('下载失败');

                const disposition = response.headers.get('Content-Disposition');
                let filename = 'file.pdf'; // 默认文件名

                if (disposition && disposition.indexOf('filename=') !== -1) {
                    const matches = /filename="?([^"]+)"?/.exec(disposition);
                    if (matches?.[1]) {
                        filename = decodeURIComponent(matches[1]); // 解码中文文件名
                    }
                }

                const blob = await response.blob();
                const downloadUrl = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = downloadUrl;
                link.setAttribute('download', filename);
                document.body.appendChild(link);
                link.click();

                // 清理
                link.remove();
                window.URL.revokeObjectURL(downloadUrl);
            } catch (error) {
                message.error('文件下载失败');
            }
        }

    };


    const handleOk = () => {
        setIsDocChunkModalOpen(false);
    };
    const handleCancel = () => {
        setIsDocChunkModalOpen(false);
    };
    const handleTxtBoxEditOk = () => {
        doPostEditUpdate({
            data: {
                id: editTxtBoxValue.id,
                content: editTxtBoxValue.content
            }
        }).then(res => {
            if (res.data.errorCode === 0) {
                message.success('更新成功')
                doGetDocumentListManual()
            } else {
                message.error(res.data.message)
            }
        })
        setIsTxtBoxEditModalOpen(false)
    }
    const handleTxtBoxEditCancel = () => {
        setIsTxtBoxEditModalOpen(false)
    }
    const onEditChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setEditTxtBoxValue({...editTxtBoxValue, content: e.target.value})
    };


    // 根据选中的菜单项渲染不同的内容
    const renderContent = () => {
        switch (selectedMenu) {
            case 'file-management':
                return (
                    <div className="content">
                        {
                            !isOpenDocChunk ?
                                (<div>
                                    <Space.Compact style={{margin: '0 0 10px 0'}}>
                                        <Input placeholder="请输入文件名进行搜索" value={fileName}
                                               onChange={handleChange}/>

                                    </Space.Compact>
                                    <Button type="primary" shape='default' size='middle' style={{marginLeft: '10px'}}
                                            onClick={handleSearch}
                                    >
                                        搜索
                                    </Button>
                                    <Button style={{margin: '0 5px'}} onClick={() => {
                                        setFileName('')
                                        doGetDocumentListManual()
                                    }}>
                                        重置
                                    </Button>

                                    <Table columns={columns} dataSource={result?.data?.records} scroll={{x: 1000}}
                                           pagination={{
                                               current: pagination.current,
                                               pageSize: pagination.pageSize,
                                               total: pagination.total,
                                               showSizeChanger: true, // 显示每页条数切换
                                               showTotal: (total) => `共 ${total} 条数据`
                                           }}
                                           onChange={handleTableChange}
                                           rowKey="id"
                                           loading={loading}
                                    />
                                </div>)
                                :
                                <div>
                                    <Modal title="详情" open={isDocChunkModalOpen} onOk={handleOk}
                                           onCancel={handleCancel} width={800}>
                                        <p>{isDocChunkContent}</p>
                                    </Modal>

                                    <Table columns={columnsChunk} dataSource={docResult?.data.records}
                                           pagination={{
                                               current: paginationCk.current,
                                               pageSize: paginationCk.pageSize,
                                               total: paginationCk.total,
                                               showSizeChanger: true, // 显示每页条数切换
                                               showTotal: (total) => `共 ${total} 条数据`
                                           }}
                                           rowKey="id"
                                           loading={chunkLoading}
                                           onChange={handleTableChangeCk}
                                           scroll={{x: 500, y: 800}} // 水平滚动宽度 1000px，垂直滚动高度 400px
                                    />
                                    {/*    文本分割修改模态框*/}
                                    <Modal title="文本修改" width={1000} height={800} open={isTxtBoxEditModalOpen}
                                           onOk={handleTxtBoxEditOk} onCancel={handleTxtBoxEditCancel}>
                                        <TextArea
                                            showCount
                                            value={editTxtBoxValue.content}
                                            onChange={onEditChange}
                                            placeholder="disable resize"
                                            style={{height: 500, resize: 'none', marginBottom: '10px'}}
                                        />
                                    </Modal>
                                </div>
                        }

                    </div>
                );
            case 'file-import':
                return (
                    <div className="content">
                        <FileImportPanel data={{knowledgeId: params.id}} maxCount={1}
                                         action="/api/v1/aiDocument/textSplit"/>
                    </div>
                );
            case 'search-test':
                return (
                    <div className="content" style={{width: '100%', height: '100%'}}>
                        <div style={{marginTop: 10, width: '100%',   display: 'flex', flexDirection: 'column'}}>
                            <div>
                                <Form form={form} onFinish={onFinish} preserve={false}>
                                    <div style={{display: "flex", gap: 10}}>
                                        <Form.Item
                                            style={{flexGrow: 1}}
                                            name="keyword"
                                            rules={[{required: true, message: '请输入关键字!'}]}>
                                            <TextArea style={{height: 50}} placeholder="请输入关键字"/>
                                        </Form.Item>
                                        <Button type="primary" htmlType="submit">
                                            搜索
                                        </Button>
                                    </div>
                                </Form>
                            </div>

                            <div style={{height: 'calc(100vh - 260px)'}}>
                                        <PreviewContainer data={searchResult} loading={searchLoading}
                                                           isSearching={true}
                                        />
                            </div>
                        </div>
                    </div>
                );
            case 'configuration':
                return (
                    <div className="content">
                        <h2>配置</h2>
                        <p>这里是配置的内容。</p>
                    </div>
                );
            default:
                return (
                    <div className="content">
                        <h2>请选择一个菜单项</h2>
                        <p>请点击左侧菜单以查看具体内容。</p>
                    </div>
                );
        }
    };

    const {setOptions} = useLayout();
    useEffect(() => {
        console.log('knowledge', knowledge)
        setOptions({
            showBreadcrumb: true,
            breadcrumbs: [
                {title: '首页'},
                {title: '知识库', href: `/ai/knowledge`},
                {title: knowledge?.data?.title,}
            ],
        })

        return () => {
            setOptions({
                showBreadcrumb: true,
                breadcrumbs: []
            })
        }
    }, [knowledge])

// 在Document组件的return部分修改布局结构
    return (
        <div className="app-container" style={{
            display: 'flex',
            flexDirection: 'row',
        }}>
            {/* 左侧菜单 - 固定不滚动 */}
            <div className="menu-container">
                <DocumentMenu
                    items={menuItems}
                    onSelect={(id) => {
                        if (id === 'search-test') {
                            setSearchResult([])
                        } else if (id === 'file-management') {
                            getDocumentList()
                        }
                        setIsOpenDocChunk(false)
                        setSelectedMenu(id)
                    }}
                    selectedId={selectedMenu}
                />
            </div>

            {/* 右侧内容区域 - 可滚动 */}
            <div className="content-container">
                {renderContent()}
            </div>
        </div>
    );
};

export default {
    path: " /ai/knowledge/:id;",
    element: Document
};

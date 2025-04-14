import React, {useEffect, useState} from 'react';
import DocumentMenu from "./DocumentMenu";
import '../style/document.css'
import {Button, Form, Input, message, Popconfirm, Space, Spin, Table} from "antd";
import {documentList, useGet, useGetManual, usePost} from "../../../hooks/useApis";
import {useParams} from 'react-router-dom';
import {convertDatetimeUtil} from "../../../libs/changeDatetimeUtil";
import Modal from "antd/es/modal/Modal";
import FileImportPanel from "./FileImportPanel";
import TextArea from "antd/es/input/TextArea";

const treeTableAlias = "aiDocument"

const Document: React.FC = () => {

    interface EditTxtBoxState {
        content: string; // 文本内容
        id: string | number | null; // 唯一标识符，可以是字符串、数字或 null
    }
    type queryParamsType = {
        documentId: string;
    };
    const {doGet: doGetManual} = useGetManual(`/api/v1/${treeTableAlias}/documentList`)
    const [queryParams, setQueryParams] = useState<queryParamsType>()
    const {doGet: getDocChunk, result: docResult, loading : chunkLoading} = useGet(`/api/v1/aiDocumentChunk/page`,queryParams,{manual: true})
    // documentChunk查看弹框
    const [para] = useState();
    const [isDocChunkModalOpen, setIsDocChunkModalOpen] = useState(false);
    const [isDocPreviewContent, setIsDocPreviewContent] = useState(false);
    const {doPost, result: resultRemove} = usePost('/api/v1/aiDocument/removeDoc', para)
    const {doPost: doPostChunkRemove} = usePost('/api/v1/aiDocumentChunk/removeChunk', para)
    const {doPost : doPostDocPreview} = usePost('/api/v1/aiDocument/docPreview', para,{manual: true})
    const {doPost : doPostEditUpdate} = usePost('/api/v1/aiDocumentChunk/update', para,{manual: true})
    // documentChunk弹框绑定的值
    const [isDocChunkContent, setIsDocChunkContent] = useState('');
    const [fileContent, setFileContent] = useState<string | null>(null);
    const [loadingPreview, setLoadingPreview] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [isConfirmDelete, setIsConfirmDelete] = useState(false);
    const [isTxtBoxEditModalOpen, setIsTxtBoxEditModalOpen] = useState(false);
    const [form] = Form.useForm();
    const [searchResult, setSearchResult] = useState<any[]>()
    const {doGet: doSearchGet, loading: searchLoading} = useGetManual("/api/v1/aiKnowledge/search");

    const onFinish = (values: any) => {
        doSearchGet({
            params: {
                id:params.kSlug,
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

    const columns:any = [
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
            render: (_:any, record:any) => (
                <Space size="middle">
                    <a onClick={() => {
                        setIsOpenDocChunk(!isOpenDocChunk)
                        const param = {
                            pageNumber:1,
                            pageSize: 10,
                            documentId: record.id,
                            sortKey: 'sorting',
                            sortType: 'asc'
                        }
                        setQueryParams(param)
                    }}>查看 {record.name}</a>
                    <a onClick={() => {
                        fetchFilePreview(record.id).then(r => console.log(r))
                    }}>预览</a>
                    <Popconfirm
                        title="确定删除"
                        description="您确定要删除这条数据吗？"
                        onConfirm={() =>{
                            setIsConfirmDelete(true)
                            doPost({data: {id: record.id}}).then((res) => {
                                if (res.data.errorCode === 0){
                                    message.success('删除成功')
                                } else {
                                    message.error('删除失败')
                                }
                            })
                        }}
                        onCancel={() =>{

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
    const columnsChunk:any = [
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
            render: (_:any, record:any) => (
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
                        onConfirm={() =>{
                            doPostChunkRemove({data: {id: record.id}}).then((resp) => {
                                if (resp.data.errorCode === 0){
                                    message.success('删除成功')
                                    getDocChunk()
                                } else {
                                    message.error('删除失败')
                                }
                            })
                        }}
                        onCancel={() =>{

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
        { id: 'file-management', label: '文件管理' },
        { id: 'file-import', label: '文件导入' },
        { id: 'search-test', label: '检索测试' },
    ];

    // 定义状态
    const [editTxtBoxValue, setEditTxtBoxValue] = useState<EditTxtBoxState>({
        content: '', // 初始内容为空字符串
        id: null,    // 初始 ID 为 null
    });
    const params = useParams();
    // 标识是否打开文档块
    const [isOpenDocChunk, setIsOpenDocChunk] = useState<boolean>(false);
    const [fileName, setFileName] = useState<string>('');

    // 状态管理：当前选中的菜单项
    const [selectedMenu, setSelectedMenu] = useState<string | null>('file-management');
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
    const {loading, result, doGet} = documentList(treeTableAlias,
        {
            current : pagination.current,
            pageSize : pagination.pageSize,
            kb_slug: params.kSlug});

    const getDocumentList = () => {
        doGetManual({
            params:{
                current : 1,
                pageSize : 10,
                kb_slug: params.kSlug
            }
        }).then(res => {
            setPagination({
                current: res.data.data.pageNumber, // 当前页码
                pageSize: res.data.data.pageSize, // 每页显示条数
                total: res.data.data.totalRow, // 总记录数
            })
            if (result) {
                result.data.records = res.data.data.records
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
        if (resultRemove){
            if (resultRemove.errorCode === 0 && isConfirmDelete){
                setIsConfirmDelete(false)
                getDocumentList()
            }
        }
    }, [result,queryParams, resultRemove]); // 仅在 count 变化时执行
    // 分页变化时触发
    const handleTableChange = (newPagination: any) => {
        const { current, pageSize } = newPagination;
        doGet().then(res => {
            setPagination({
                current: current, // 当前页码
                pageSize: pageSize, // 每页显示条数
                total: res.data.data.totalRow, // 总记录数
            })
            if (result) {
                result.data.records = res.data.records;
            }
        })
    };

    const handleTableChangeCk = (newPagination: any) => {
        const { current, pageSize } = newPagination;
        setPaginationCk({
            current: current, // 当前页码
            pageSize: pageSize, // 每页显示条数
            total: 0, // 总记录数
        })
        const param = {
            pageNumber:current,
            pageSize: pageSize,
            documentId: queryParams?.documentId || '',
            sortKey: 'sorting',
            sortType: 'asc'
        }
        setQueryParams(param)
    };
    // 处理输入框变化
    const handleChange = (e:any) => {
        setFileName(e.target.value); // 更新状态
    };
    const handleSearch = () => {
        doGetManual({
            params:{
                'fileName': fileName,
                current : pagination.current,
                pageSize : pagination.pageSize,
                kb_slug: params.kSlug
            }
        }).then(res => {
            if (result){
                result.data = res.data.data
            }

            setPagination({
                current: pagination.current, // 当前页码
                pageSize: pagination.pageSize, // 每页显示条数
                total: res.data.data.totalRow, // 总记录数
            })
        })
    }


    const fetchFilePreview = async (documentId: string) => {
        setLoadingPreview(true);
        setError(null);
        try {
            const response = await doPostDocPreview({ data: { documentId: documentId } });
            const contentType = response.data.data.headers['Content-Type'];
            const data = response.data.data.body;

            if (contentType.includes('text/html')) {
                // 如果是HTML内容（可能是DOCX转换后的HTML）
                setFileContent(data);
                setIsDocPreviewContent(true)

            } else if (contentType.includes('application/pdf')) {
                window.open(data)
            } else if (contentType.includes('application/vnd.openxmlformats-officedocument.wordprocessingml.document')) {
                // 直接处理DOCX文件 - 使用Mammoth.js转换为HTML
                setFileContent(await renderDocxAsHtml(data));
                setIsDocPreviewContent(true);

            } else if (contentType.includes('text/plain')) {
                // 纯文本文件
                setFileContent(data);
                setIsDocPreviewContent(true);

            } else {
                setError('不支持的文件类型');
                setIsDocPreviewContent(true);

            }
        } catch (err: any) {
            setError(err.response?.data || '无法加载文件内容');
        } finally {
            setLoadingPreview(false);
        }
    };
// 添加DOCX渲染函数
    const renderDocxAsHtml = async (base64Data: string) => {
        try {
            // 1. 首先添加Mammoth.js依赖（确保已安装）
            const mammoth = await import('mammoth');

            // 2. 将Base64转换为ArrayBuffer
            const binaryString = atob(base64Data);
            const bytes = new Uint8Array(binaryString.length);
            for (let i = 0; i < binaryString.length; i++) {
                bytes[i] = binaryString.charCodeAt(i);
            }
            const arrayBuffer = bytes.buffer;

            // 3. 使用Mammoth转换DOCX为HTML
            const result = await mammoth?.convertToHtml({ arrayBuffer });
            return result.value; // 返回HTML内容
        } catch (error: any) {
            console.error('DOCX转换错误:', error);
            return `<p>无法渲染DOCX文件: ${error.message}</p>`;
        }
    };

    const handleOk = () => {
        setIsDocChunkModalOpen(false);
    };
    const handleCancel = () => {
        setIsDocChunkModalOpen(false);
    };
    const handleTxtBoxEditOk= () =>{
        doPostEditUpdate({
            data:{
                id: editTxtBoxValue.id,
                content: editTxtBoxValue.content
            }
        }).then(res => {
            if(res.data.errorCode === 0){
                message.success('更新成功')
                doGet()
            }else{
                message.error(res.data.message)
            }
        })
        setIsTxtBoxEditModalOpen(false)
    }
    const handleTxtBoxEditCancel= () =>{
        setIsTxtBoxEditModalOpen(false)
    }
    const onEditChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setEditTxtBoxValue({...editTxtBoxValue, content: e.target.value})
    };
    const handleIsPreviewOk = () => {
        setIsDocPreviewContent(false);
        setFileContent(null)
    };

    // 根据选中的菜单项渲染不同的内容
    const renderContent = () => {
        switch (selectedMenu) {
            case 'file-management':
                return (
                    <div className="content">
                        {
                            !isOpenDocChunk?
                                (   <div>
                                    <Space.Compact style={{margin: '0 0 10px 0'}}>
                                        <Input  placeholder="请输入文件名进行搜索" value={fileName} onChange={handleChange}/>

                                    </Space.Compact>
                                    <Button type="primary" shape='default' size='middle'style={{marginLeft: '10px'}}
                                            onClick={handleSearch}
                                    >
                                        搜索
                                    </Button>
                                    <Button style={{margin: '0 5px'}} onClick={() => {
                                        setFileName('')
                                        doGet()
                                    }}>
                                        重置
                                    </Button>
                                    <Modal title="文件预览" open={isDocPreviewContent} onOk={handleIsPreviewOk} onCancel={() => {setIsDocPreviewContent(false)}} width={1500}>
                                        <div>
                                            {loadingPreview && <p>加载中...</p>}
                                            {error && <p style={{ color: 'red' }}>错误：{error}</p>}
                                            {fileContent && (
                                                <div>
                                                    {/* 如果是 blob URL，则直接用 iframe 加载 */}
                                                    {fileContent.startsWith('blob:') ? (
                                                        <iframe src={fileContent} width="100%" height="600px" title="PDF 预览" />
                                                    ) : (
                                                        // 其他情况（如 data URL 或纯文本）
                                                        <div>
                                                            {typeof fileContent === 'string' && fileContent.startsWith('data:image') ? (
                                                                <img src={fileContent} alt="预览" style={{ maxWidth: '100%' }} />
                                                            ) : typeof fileContent === 'string' && fileContent.startsWith('data:application/pdf') ? (
                                                                <iframe src={fileContent} width="100%" height="600px" title="PDF 预览" />
                                                            ) : typeof fileContent === 'string' && !fileContent.startsWith('data:') ? (
                                                                // 渲染 HTML 内容
                                                                <div dangerouslySetInnerHTML={{ __html: fileContent }} />
                                                            ) : (
                                                                <pre>{fileContent}</pre>
                                                            )}
                                                        </div>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    </Modal>
                                    <Table columns={columns} dataSource={result?.data.records} scroll={{ x: 1000 }}
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
                                    <Modal title="详情" open={isDocChunkModalOpen} onOk={handleOk}  onCancel={handleCancel}  width={800}>
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
                                                     scroll={{ x: 500, y: 800 }} // 水平滚动宽度 1000px，垂直滚动高度 400px
                                    />
                                {/*    文本分割修改模态框*/}
                                    <Modal title="文本修改"  width={1000} height={800} open={isTxtBoxEditModalOpen} onOk={handleTxtBoxEditOk} onCancel={handleTxtBoxEditCancel}>
                                        <TextArea
                                            showCount
                                            value={editTxtBoxValue.content}
                                            onChange={onEditChange}
                                            placeholder="disable resize"
                                            style={{ height: 500, resize: 'none',marginBottom: '10px' }}
                                        />
                                    </Modal>
                                </div>
                        }

                    </div>
                );
            case 'file-import':
                return (
                    <div className="content">
                        <FileImportPanel data={{knowledgeId: params.kSlug}} maxCount={1} action="/api/v1/aiDocument/upload"/>
                    </div>
                );
            case 'search-test':
                return (
                    <div className="content" style={{width: '1000px'}}>
                            <div style={{minHeight: 500}}>
                                <Form form={form} onFinish={onFinish} preserve={false}>
                                    <div style={{display: "flex", gap: 10}}>
                                        <Form.Item
                                            style={{flexGrow: 1}}
                                            name="keyword"
                                            rules={[{required: true, message: '请输入关键字!'}]}>
                                            <TextArea style={{ height: 50}} placeholder="请输入关键字"/>
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
                                    </div> :
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

    return (
        <div className="app-container">
            {/* 左侧菜单 */}
            <DocumentMenu
                items={menuItems}
                onSelect={(id) => {
                    if (id === 'search-test'){
                        setSearchResult([])
                    } else if (id === 'file-management'){
                        getDocumentList()
                    }
                    setIsOpenDocChunk(false)
                    setSelectedMenu(id)
                }}
                selectedId={selectedMenu}
            />

            {/* 右侧内容区域 */}
            <div className="content-container">{renderContent()}</div>
        </div>
    );
};

export default {
    path: "/ai/knowledge/document/:kSlug/:dSlug; /ai/knowledge/document/:kSlug;",
    element: Document
};

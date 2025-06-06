import React, {useEffect, useState} from "react";
import {Button, Input, List, message, Radio, Select, Upload, UploadProps,} from "antd";
import {InboxOutlined, MinusCircleTwoTone, PlusCircleTwoTone} from "@ant-design/icons";
import "../style/FileImportPanel.less";
import {isBrowser} from "../../../libs/ssr";
import axios from "axios";

const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;
const tokenKey = `${import.meta.env.VITE_APP_TOKEN_KEY}`;

interface FileImportPanelProps {
    data?: object; // 参数
    maxCount?: number; // 最大上传文件数量
    action?: string; // 上传接口地址
}
interface PreviewLoadingProps {
    spinning?: boolean;
    tip?: string;
}
interface AiDocumentType {
    chunkSize: string, // 分段大小
    overlapSize: string, // 分段重叠大小
    regex: string,
    rowsPerChunk: string,
    splitterName: string
}
interface AiDocumentData {
    chunkSize: number, // 分段大小
    content: string, // 分段重叠大小
    created: string,
    documentPath: string,
    documentType: string
    modified: string
    modifiedBy: string
    overlapSize: string
    title: string
}
// 文件导入页面组件
const FileImportPanel: React.FC<FileImportPanelProps> = ({ data, maxCount = 1, action }) => {
    const [disabledConfirm, setDisabledConfirm] = useState<boolean>(false);
    const [dataPreView, setDataPreView] = useState<PreviewItem[]>([]);
    const [aiDocumentData, setAiDocumentData] = useState<AiDocumentData>();
    const [confirmImport, setConfirmImport] = useState<boolean>(false);
    const [selectedSplitter, setSelectedSplitter] = useState<string>('SimpleDocumentSplitter');
    const [regex, setRegex] = useState<string>('');

    const token = isBrowser ? localStorage.getItem(authKey) : null;
    const [aiDocument, setAiDocument] = useState<AiDocumentType>({
        chunkSize: '200', // 分段大小
        overlapSize: '100', // 分段重叠大小
        regex: '',
        rowsPerChunk: '50',
        splitterName: selectedSplitter
    })

    const [previewListLoading, setPreviewListLoading] = useState<PreviewLoadingProps>({
        spinning: false,
        tip: '正在加载数据，请稍候...'
    })

    const [contentHeight, setContentHeight] = useState(500); // 默认高度

    useEffect(() => {
        setContentHeight(window.innerHeight - 300);
    }, []);


    interface PreviewItem {
        sorting: string; // 顺序编号
        content: string; // 内容
    }

    const headers = {
        Authorization: token || "",
        [tokenKey]: token || ""
    };
    interface CustomUploadProps extends UploadProps {
        [key: string]: any; // 添加字符串索引签名
        userWillSave?: string;
        overlapSize?: string;
        chunkSize?: string;
        knowledgeId?: string;
    }
    const uploadProps: CustomUploadProps = {
        ...data,
        chunkSize: aiDocument.chunkSize,
        overlapSize: aiDocument.overlapSize,
        userWillSave: 'false',
        regex: regex,
        rowsPerChunk: aiDocument.rowsPerChunk,
        splitterName: selectedSplitter
    };
    const uploadData: Record<string, unknown> = {
        ...uploadProps
    };
    // 定义文件上传前的校验逻辑
    const beforeUploadDocument = (file: File) => {

        const isAllowedType =
            file.type === "text/plain" ||
            file.type === "application/pdf" ||
            file.type === "application/markdown" ||
            file.type === "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ||
            file.type === "application/vnd.ms-powerpoint" ||  // PPT 文件类型
            file.type === "application/vnd.openxmlformats-officedocument.presentationml.presentation" ||  // PPTX 文件类型
            file.name.endsWith(".md") ||
            file.name.endsWith(".ppt") ||  // 添加 .ppt 扩展名检查
            file.name.endsWith(".pptx");   // 添加 .pptx 扩展名检查
        const isLt20M = file.size / 1024 / 1024 < 20;

        if (!isAllowedType) {
            message.error("仅支持 txt, pdf, md, docx, ppt, pptx 格式的文件！");
        }
        if (!isLt20M) {
            message.error("单个文件大小不能超过 20MB！");
        }
        if (isAllowedType && isLt20M){
            setPreviewListLoading({
                spinning: true,
                tip: '正在加载数据，请稍候...'
            })
        }

        return isAllowedType && isLt20M;
    };

    const beforeUploadExcel = (file: File) => {
        const isAllowedType =
            file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        const isLt20M = file.size / 1024 / 1024 < 20;
        if (!isAllowedType) {
            message.error("仅支持 xlsx 格式的文件！");
        }
        if (!isLt20M) {
            message.error("单个文件大小不能超过 20MB！");
        }
        if (isAllowedType && isLt20M){
            setPreviewListLoading({
                spinning: true,
                tip: '正在加载数据，请稍候...'
            })
        }
        return isAllowedType && isLt20M;
    };
    // 状态管理：当前选中的选项
    const [selectedOption, setSelectedOption] = useState("document");

    // 状态管理：上传文件列表
    const [fileList, setFileList] = useState<any[]>([]);

    // 更新文件列表的状态
    const handleFileChange = (newFileList: any[]) => {
        newFileList.forEach((file) => {
            // 如果用户是预览返回的分割效果
            if (!file.response?.data?.userWillSave && file.response){
                setPreviewListLoading({
                    spinning: false
                })
                //设置返回的分割别表
                setDataPreView(file.response?.data?.data);
            }

        });
        if (newFileList.length > 0) {
            // 如果用户是预览返回的分割效果
            if (!newFileList[0].response?.data?.userWillSave && newFileList[0].response){
                setPreviewListLoading({
                    spinning: false
                })
                //设置返回的分割列表
                setDataPreView(newFileList[0].response?.data?.previewData);
                setAiDocumentData(newFileList[0].response?.data?.aiDocumentData);
            }

            if (newFileList[0]?.response?.errorCode >= 1){
                message.error(newFileList[0].response.message)
            }
        }
        setFileList(newFileList);
        setConfirmImport(true)
    };
    // 保存文件
    const saveDocument = () => {
        setPreviewListLoading({ spinning: true,tip: "正在保存文件..."})
        setDisabledConfirm(true)
        // 构造 FormData 对象
        const formData = new FormData();
        formData.append("knowledgeId", uploadProps.knowledgeId as string); // 添加 knowledgeId
        formData.append("aiDocumentData", JSON.stringify(aiDocumentData));
        formData.append("previewData", JSON.stringify(dataPreView));
        uploadProps.userWillSave = 'true';
        formData.append("userWillSave", uploadProps.userWillSave);
        // 发起 POST 请求
        // 发起 POST 请求
        axios.post("/api/v1/aiDocument/saveText", formData, {
            headers: {
                ...headers,
                "Content-Type": "multipart/form-data",
            },
        }).then(res => {
            setPreviewListLoading({ spinning: false,tip: ''})
            if (res?.data?.errorCode === 0){
                //保存成功，清除展现的分割文档
                setDataPreView([]);
                setFileList([]);
                message.success("上传成功");
                setConfirmImport(false);
                setDisabledConfirm(false)
            } else if (res.data?.errorCode >= 1){
                message.error(res.data?.message);
                setDisabledConfirm(false)
            }
        });
    };
    // 右侧内容映射
    const contentMapping: { [key: string]: JSX.Element } = {
        document: (
            <div style={{width: "100%", height: "100%", display: "flex", flexDirection: "row"}}>
                <div className="file-content">
                    {/* 导入方式 */}
                    <Radio.Group defaultValue="local">
                        <Radio value="local">导入本地文档</Radio>
                    </Radio.Group>

                    {/* 上传文件 */}
                    <p className="section-description">
                        支持 txt, pdf, docx, md, ppt, pptx 格式文件，单次最多上传 {maxCount} 个文件，单个大小不超过 20M。
                    </p>

                    <div style={{display: "flex", flexDirection:"column", width:"500px", gap: "10px"}}>
                        {/* 分割器选择 */}
                        <div style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "10px"
                        }}>
                            <p style={{
                                width: "70px",  // 固定标签宽度
                                margin: 0,
                                textAlign: "right",
                                lineHeight: "32px"
                            }}>分割器:</p>
                            <Select
                                value={selectedSplitter}
                                style={{ width: 200 }}
                                onChange={(value) => setSelectedSplitter(value)}
                                options={[
                                    { value: 'SimpleDocumentSplitter', label: '简单文档分割器' },
                                    { value: 'RegexDocumentSplitter', label: '正则文档分割器' },
                                    { value: 'SimpleTokenizeSplitter', label: '简单分词器' }
                                ]}
                            />
                        </div>

                        {selectedSplitter === 'SimpleDocumentSplitter' || selectedSplitter === 'SimpleTokenizeSplitter' ? (
                            <>
                                {/* 分段长度 */}
                                <div style={{
                                    display: "flex",
                                    alignItems: "center",
                                    gap: "10px"
                                }}>
                                    <p style={{
                                        width: "70px",  // 固定标签宽度
                                        margin: 0,
                                        textAlign: "right",
                                        lineHeight: "32px"
                                    }}>分段长度:</p>
                                    <Input
                                        addonBefore={
                                            <MinusCircleTwoTone
                                                onClick={() => {
                                                    const newValue = (parseInt(aiDocument.chunkSize) - 10).toString();
                                                    setAiDocument({ ...aiDocument, chunkSize: newValue});
                                                }}
                                                style={{
                                                    fontSize: "18px",
                                                    cursor: "pointer",
                                                }}
                                            />
                                        }
                                        addonAfter={
                                            <PlusCircleTwoTone
                                                onClick={() => {
                                                    const newValue = (parseInt(aiDocument.chunkSize) + 10).toString();
                                                    setAiDocument({ ...aiDocument, chunkSize: newValue});
                                                }}
                                                style={{
                                                    fontSize: "18px",
                                                    cursor: "pointer",
                                                }}
                                            />
                                        }
                                        value={aiDocument.chunkSize}
                                        onChange={(e) => {
                                            const newValue = e.target.value;
                                            setAiDocument({ ...aiDocument, chunkSize: newValue});
                                        }}
                                        style={{
                                            width: 200,
                                            textAlign: "center",
                                        }}
                                    />
                                </div>

                                {/* 分段重叠长度 */}
                                <div style={{
                                    display: "flex",
                                    alignItems: "center",
                                    gap: "10px"
                                }}>
                                    <p style={{
                                        width: "70px",  // 固定标签宽度
                                        margin: 0,
                                        textAlign: "right",
                                        lineHeight: "32px"
                                    }}>分段重叠:</p>
                                    <Input
                                        addonBefore={
                                            <MinusCircleTwoTone
                                                onClick={() => {
                                                    const newValue = (parseInt(aiDocument.overlapSize) - 10).toString();
                                                    setAiDocument({ ...aiDocument, overlapSize: newValue});
                                                }}
                                                style={{
                                                    fontSize: "18px",
                                                    cursor: "pointer",
                                                }}
                                            />
                                        }
                                        addonAfter={
                                            <PlusCircleTwoTone
                                                onClick={() => {
                                                    const newValue = (parseInt(aiDocument.overlapSize) + 10).toString();
                                                    setAiDocument({ ...aiDocument, overlapSize: newValue});
                                                }}
                                                style={{
                                                    fontSize: "18px",
                                                    cursor: "pointer",
                                                }}
                                            />
                                        }
                                        value={aiDocument.overlapSize}
                                        onChange={(e) => {
                                            const newValue = e.target.value;
                                            setAiDocument({ ...aiDocument, overlapSize: newValue});
                                        }}
                                        style={{
                                            width: 200,
                                            textAlign: "center",
                                        }}
                                    />
                                </div>
                            </>
                        ) : selectedSplitter === 'RegexDocumentSplitter' ? (
                            <div style={{
                                display: "flex",
                                alignItems: "center",
                                gap: "10px"
                            }}>
                                <p style={{
                                    width: "80px",  // 固定标签宽度
                                    margin: 0,
                                    textAlign: "right",
                                    lineHeight: "32px"
                                }}>正则表达式:</p>
                                <Input
                                    size='large'
                                    placeholder="请输入文本分割的正则表达式"
                                    onChange={(e) => setRegex(e.target.value)}
                                    style={{width: "100%"}}
                                />
                            </div>
                        ) : null}

                        {/* 上传区域 */}
                        <Upload.Dragger
                            name="file"
                            multiple
                            accept=".txt,.pdf,.md,.docx,.ppt,.pptx"
                            beforeUpload={beforeUploadDocument}
                            fileList={fileList}
                            onChange={(info) => handleFileChange(info.fileList)}
                            maxCount={1}
                            data={uploadData}
                            action={action}
                            headers={headers}
                            className="upload-area"
                        >
                            <p className="upload-icon">
                                <InboxOutlined />
                            </p>
                            <p className="upload-text" style={{ userSelect: "none" }}>点击或拖拽文件到此区域上传</p>
                            <p className="upload-hint" style={{ userSelect: "none" }}>支持单次上传最多 {maxCount} 个文件。</p>
                        </Upload.Dragger>
                    </div>
                </div>
                <div style={{display:"flex", flexDirection:"column", width:"60%", height:"100%"}}>
                    <div style={{backgroundColor:"#f0f0f0", marginLeft:"20px",  height: `${contentHeight}px`, overflowY:"scroll", padding:"5px"}}>
                        <List
                            itemLayout="horizontal"
                            dataSource={dataPreView}
                            loading={previewListLoading}
                            renderItem={(item) => (
                                <List.Item>
                                    <List.Item.Meta
                                        title={<a href="https://ant.design">{`文本分段${item.sorting}:`}</a>}
                                        description={item.content}
                                    />
                                </List.Item>
                            )}
                        />
                    </div>
                    {confirmImport?     (<div style={{
                        display: "flex",
                        justifyContent: "flex-end",
                        gap: "20px",
                        backgroundColor: "#f0f0f0", // 背景色与容器一致
                        padding: "10px",   // 添加内边距
                        borderTop: "1px solid #ccc", // 可选：添加分隔线
                        textAlign: "center", // 文本居中
                        marginLeft:"20px"
                    }}>
                        <Button type="dashed" onClick={() => {
                            setConfirmImport(false);
                            setDataPreView([]);
                            setFileList([]);
                        }}>取消导入</Button>
                        <Button type="dashed"
                                disabled={disabledConfirm}
                                onClick={() => {
                                    saveDocument()
                                }}>确认导入</Button>
                    </div>) : ''
                    }

                </div>

            </div>
        ),
        table: (
            <div style={{width: "100%", display: "flex", flexDirection: "row"}}>
                <div className="file-content">
                    {/* 导入方式 */}
                    <Radio.Group defaultValue="local">
                        <Radio value="local">导入Excel表格</Radio>
                    </Radio.Group>

                    {/* 上传文件 */}
                    <p className="section-description">
                        支持 xlsx 格式文件，单次最多上传 {maxCount} 个文件，单个大小不超过 20M。
                    </p>
                    <div style={{display: "flex",  flexDirection:"column", width:"500px", gap:"10px"}}>
                        <div style={{
                            display: "flex",
                            flexDirection: "row",
                            width: "500px",
                            gap: "10px"
                        }}>
                            <p style={{
                                width: "70px",  // 固定标签宽度
                                margin: 0,
                                textAlign: "right",
                                lineHeight: "32px"
                            }}>分割器:</p>
                            <Select
                                value={selectedSplitter}
                                style={{ width: 200 }}
                                onChange={(value) => setSelectedSplitter(value)}
                                options={[
                                    { value: 'ExcelDocumentSplitter', label: 'Excel片段生成器' },
                                ]}
                            />
                        </div>
                        <div>
                            <div style={{display: "flex", flexDirection:"row", gap:"10px"}}>
                                <div style={{
                                    width: "70px",  // 固定标签宽度
                                    margin: 0,
                                    textAlign: "right",
                                    lineHeight: "32px"
                                }}>单块行数:</div>
                                <Input
                                    addonBefore={
                                        <MinusCircleTwoTone
                                            onClick={() => {
                                                // 更新输入框的值
                                                const newValue = (parseInt(aiDocument.rowsPerChunk) - 10).toString();
                                                setAiDocument({ ...aiDocument, rowsPerChunk: newValue}); // 确保输入的是数字
                                            }}
                                            style={{
                                                fontSize: "18px",
                                                cursor: "pointer", // 鼠标悬浮时显示小手
                                            }}
                                        />
                                    }
                                    addonAfter={
                                        <PlusCircleTwoTone
                                            onClick={() => {
                                                // 更新输入框的值
                                                const newValue = (parseInt(aiDocument.rowsPerChunk) + 10).toString();
                                                setAiDocument({ ...aiDocument, rowsPerChunk: newValue}); // 确保输入的是数字
                                            }}
                                            style={{
                                                fontSize: "18px",
                                                cursor: "pointer", // 鼠标悬浮时显示小手
                                            }}
                                        />
                                    }
                                    value={aiDocument.rowsPerChunk}
                                    onChange={(e) => {
                                        // 更新输入框的值
                                        const newValue = e.target.value;
                                        setAiDocument({ ...aiDocument, rowsPerChunk: newValue}); // 确保输入的是数字
                                    }}
                                    style={{
                                        width: 200, // 设置输入框的宽度
                                        textAlign: "center", // 值居中
                                    }}
                                />
                            </div>
                        </div>


                        <Upload.Dragger
                            name="file"
                            multiple
                            accept=".xlsx"
                            beforeUpload={beforeUploadExcel}
                            fileList={fileList}
                            onChange={(info) => handleFileChange(info.fileList)}
                            maxCount={1}
                            data={uploadData}
                            action={action}
                            headers={headers}
                            className="upload-area"
                        >
                            <p className="upload-icon">
                                <InboxOutlined />
                            </p>
                            <p className="upload-text" style={{ userSelect: "none" }}> 点击或拖拽文件到此区域上传</p>
                            <p className="upload-hint" style={{ userSelect: "none" }}>支持单次上传最多 {maxCount} 个文件。</p>
                        </Upload.Dragger>



                    </div>
                </div>
                <div style={{display:"flex", flexDirection:"column", width:"60%"}}>
                    <div style={{backgroundColor:"#f0f0f0", marginLeft:"20px", height: `${contentHeight}px`, overflowY:"scroll", padding:"5px"}}>
                        <List
                            itemLayout="horizontal"
                            dataSource={dataPreView}
                            loading={previewListLoading}
                            renderItem={(item) => (
                                <List.Item>
                                    <List.Item.Meta
                                        title={<a href="https://ant.design">{`文本分段${item.sorting}:`}</a>}
                                        description={item.content}
                                    />
                                </List.Item>
                            )}
                        />
                    </div>
                    {confirmImport?     (<div style={{
                        display: "flex",
                        justifyContent: "flex-end",
                        gap: "20px",
                        backgroundColor: "#f0f0f0", // 背景色与容器一致
                        padding: "10px",   // 添加内边距
                        borderTop: "1px solid #ccc", // 可选：添加分隔线
                        textAlign: "center", // 文本居中
                        marginLeft:"20px"
                    }}>
                        <Button type="dashed" onClick={() => {
                            setConfirmImport(false);
                            setDataPreView([]);
                            setFileList([]);
                        }}>取消导入</Button>
                        <Button type="dashed"
                                disabled={disabledConfirm}
                                onClick={()=>{
                                    saveDocument()
                                }}>确认导入</Button>
                    </div>) : ''
                    }

                </div>

            </div>
        )
    };

    return (
        <div className="file-import">
            <div className="options">
                <div className="option-group">
                    <div
                        className={`option ${selectedOption === "document" ? "active" : ""}`}
                        onClick={() => {
                            setSelectedOption("document")
                            setDataPreView([])
                            setSelectedSplitter("SimpleDocumentSplitter")
                            setAiDocument({...aiDocument, overlapSize: '100', chunkSize: '200'})
                            setFileList([])
                        }}
                    >
                        <span className="icon">📖</span>
                        <span className="label">文档</span>
                        <span className="description">自动解析文档，使用方便</span>
                    </div>
                    <div
                        className={`option ${selectedOption === "table" ? "active" : ""}`}
                        onClick={() => {
                            setSelectedOption("table")
                            setSelectedSplitter("ExcelDocumentSplitter")
                            setDataPreView([])
                            setAiDocument({...aiDocument, rowsPerChunk: '50'})
                            setFileList([])
                        }}
                    >
                        <span className="icon">📊</span>
                        <span className="label">表格</span>
                        <span className="description">结构化表格导入，支持 xlsx 格式</span>
                    </div>
                    <div
                        style={{visibility: 'hidden'}}
                        className={`option ${selectedOption === "qa" ? "active" : ""}`}
                        onClick={() => setSelectedOption("qa")}
                    >
                        <span className="icon">💬</span>
                        <span className="label">问答</span>
                        <span className="description">一问一答导入，准确性更佳</span>
                    </div>
                    <div
                        style={{visibility: 'hidden'}}
                        className={`option ${selectedOption === "webpage" ? "active" : ""}`}
                        onClick={() => setSelectedOption("webpage")}
                    >
                        <span className="icon">🌐</span>
                        <span className="label">网页</span>
                        <span className="description">自动获取网页内容导入</span>
                    </div>
                </div>
            </div>

            <div className="content">
                {contentMapping[selectedOption]}
            </div>
        </div>
    );
};

export default FileImportPanel;

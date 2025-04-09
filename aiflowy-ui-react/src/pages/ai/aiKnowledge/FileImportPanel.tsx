import React, {useState} from "react";
import {Button, Input, List, message, Radio, Upload, UploadProps,} from "antd";
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
}
// 文件导入页面组件
const FileImportPanel: React.FC<FileImportPanelProps> = ({ data, maxCount = 10, action }) => {
    const token = isBrowser ? localStorage.getItem(authKey) : null;
    const [aiDocument, setAiDocument] = useState<AiDocumentType>({
        chunkSize: '200', // 分段大小
        overlapSize: '100', // 分段重叠大小
    })

    const [previewListLoading, setPreviewListLoading] = useState<PreviewLoadingProps>({
        spinning: false,
        tip: '正在加载数据，请稍候...'
    })


    interface PreviewItem {
        sorting: string; // 顺序编号
        content: string; // 内容
    }

    const [disabledConfirm, setDisabledConfirm] = useState<boolean>(false);
    const [dataPreView, setDataPreView] = useState<PreviewItem[]>([]);
    const [confirmImport, setConfirmImport] = useState<boolean>(false);
    // 定义用户是预览还是保存上传的文件 true 用户保存当前分割的文档 false 用户当前的操作是预览文件分割的效果
    // const {userWillSave, setUserWillSave} = useState<boolean>(true);
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
        userWillSave: 'false'
    };
    const uploadData: Record<string, unknown> = {
        ...uploadProps
    };
    // 定义文件上传前的校验逻辑
    const beforeUpload = (file: File) => {
        setPreviewListLoading({
            spinning: true,
            tip: '正在加载数据，请稍候...'
        })
        const isAllowedType =
            file.type === "text/plain" ||
            file.type === "application/pdf" ||
            file.type === "application/markdown" ||
            file.type === "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ||
            file.name.endsWith(".md");
        const isLt15M = file.size / 1024 / 1024 < 15;

        if (!isAllowedType) {
            message.error("仅支持 txt, pdf, md, docx 格式的文件！");
        }
        if (!isLt15M) {
            message.error("单个文件大小不能超过 15MB！");
        }

        return isAllowedType && isLt15M;
    };
    // 状态管理：当前选中的选项
    const [selectedOption, setSelectedOption] = useState("document");

    // 状态管理：上传文件列表
    const [fileList, setFileList] = useState<any[]>([]);

    // 更新文件列表的状态
    const handleFileChange = (newFileList: any[]) => {
        newFileList.forEach((file) => {
            // 如果用户是预览返回的分割效果
            if (!file.response?.data.userWillSave && file.response){
                setPreviewListLoading({
                    spinning: false
                })
                //设置返回的分割别表
                setDataPreView(file.response?.data.data);

            } else {
                if (file.status === "done" && file.response.errorCode === 0) {
                    // 处理上传成功的文件
                    message.success(`文件 ${file.name} 上传成功`);
                } else if (file.status === "error") {
                    // 处理上传失败的文件
                    message.error(`文件 ${file.name} 上传失败`);
                }
            }

        });
        setFileList(newFileList);
        setConfirmImport(true)
    };

    // 右侧内容映射
    const contentMapping: { [key: string]: JSX.Element } = {
        document: (
            <div style={{width: "100%", display: "flex", flexDirection: "row"}}>
                <div className="file-content">
                    {/* 导入方式 */}
                    <Radio.Group defaultValue="local">
                        <Radio value="local">导入本地文档</Radio>
                    </Radio.Group>

                    {/* 上传文件 */}
                    <p className="section-description">
                        支持 txt, pdf, docx, md 格式文件，单次最多上传 {maxCount} 个文件，单个大小不超过 15M。
                    </p>
                    <div style={{display: "flex",  flexDirection:"column", width:"500px"}}>
                        <div style={{display: "flex", flexDirection:"column"}}>
                            <div>分段长度:</div>
                            <Input
                                addonBefore={
                                    <MinusCircleTwoTone
                                        onClick={() => {
                                            // 更新输入框的值
                                            const newValue = (parseInt(aiDocument.chunkSize) - 10).toString();
                                            setAiDocument({ ...aiDocument, chunkSize: newValue}); // 确保输入的是数字
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
                                            const newValue = (parseInt(aiDocument.chunkSize) + 10).toString();
                                            setAiDocument({ ...aiDocument, chunkSize: newValue}); // 确保输入的是数字
                                        }}
                                        style={{
                                            fontSize: "18px",
                                            cursor: "pointer", // 鼠标悬浮时显示小手
                                        }}
                                    />
                                }
                                value={aiDocument.chunkSize}
                                onChange={(e) => {
                                    // 更新输入框的值
                                    const newValue = e.target.value;
                                    setAiDocument({ ...aiDocument, chunkSize: newValue}); // 确保输入的是数字
                                }}
                                style={{
                                    width: 200, // 设置输入框的宽度
                                    textAlign: "center", // 值居中
                                }}
                            />
                        </div>
                        <div style={{display: "flex", flexDirection:"column", marginTop:"10px"}}>
                            <div style={{ userSelect: "none" }}>分段重叠长度:</div>
                            <Input
                                addonBefore={
                                    <MinusCircleTwoTone
                                        onClick={() => {
                                            // 更新输入框的值
                                            const newValue = (parseInt(aiDocument.overlapSize) - 10).toString();
                                            setAiDocument({ ...aiDocument, overlapSize: newValue}); // 确保输入的是数字
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
                                            const newValue = (parseInt(aiDocument.overlapSize) + 10).toString();
                                            setAiDocument({ ...aiDocument, overlapSize: newValue}); // 确保输入的是数字
                                        }}
                                        style={{
                                            fontSize: "18px",
                                            cursor: "pointer", // 鼠标悬浮时显示小手
                                        }}
                                    />
                                }
                                value={aiDocument.overlapSize}
                                onChange={(e) => {
                                    // 更新输入框的值
                                    const newValue = e.target.value;
                                    setAiDocument({ ...aiDocument, overlapSize: newValue}); // 确保输入的是数字
                                }}
                                style={{
                                    width: 200, // 设置输入框的宽度
                                    textAlign: "center", // 值居中
                                }}
                            />
                            <Upload.Dragger
                                name="file"
                                multiple
                                accept=".txt,.pdf,.md,.docx"
                                beforeUpload={beforeUpload}
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
                </div>
                <div style={{display:"flex", flexDirection:"column", width:"60%"}}>
                    <div style={{backgroundColor:"#f0f0f0", marginLeft:"20px", height:"500px", overflowY:"scroll", padding:"5px"}}>
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
                        <Button type="dashed" disabled={disabledConfirm} onClick={() => {
                            setPreviewListLoading({ spinning: true,tip: "正在保存文件..."})
                            setDisabledConfirm(true)
                            // 构造 FormData 对象
                            const formData = new FormData();
                            const file = fileList[0].originFileObj; // 获取用户选择的文件
                            formData.append("file", file); // 添加文件
                            formData.append("knowledgeId", uploadProps.knowledgeId as string); // 添加 knowledgeId
                            if (uploadProps.chunkSize !== undefined) {
                                formData.append("chunkSize", uploadProps.chunkSize);
                            }
                            if (uploadProps.overlapSize !== undefined) {
                                formData.append("overlapSize", uploadProps.overlapSize);
                            }
                            uploadProps.userWillSave = 'true';
                            formData.append("userWillSave", uploadProps.userWillSave);
                            // 发起 POST 请求
                            // 发起 POST 请求
                            axios.post("/api/v1/aiDocument/upload", formData, {
                                headers: {
                                    ...headers,
                                    "Content-Type": "multipart/form-data",
                                },
                            }).then(res => {
                                setPreviewListLoading({ spinning: false,tip: ''})
                                if (res.data.errorCode === 0){
                                    //保存成功，清除展现的分割文档
                                    setDataPreView([]);
                                    setFileList([]);
                                    message.success("上传成功");
                                    setConfirmImport(false);
                                    setDisabledConfirm(false)
                                }
                               });

                        }}>确认导入</Button>
                    </div>) : ''
                    }

                </div>

            </div>
        ),
        qa: (
            <div className="file-content">
                {/* 示例内容 */}
                <h3>问答导入</h3>
                <p>请上传包含问答对的文件，格式为 CSV 或 TXT。</p>
                <Upload.Dragger
                    name="files"
                    multiple
                    accept=".csv,.txt"
                    beforeUpload={beforeUpload}
                    fileList={fileList}
                    onChange={(info) => handleFileChange(info.fileList)}
                    maxCount={maxCount}
                    action={action}
                    className="upload-area"
                >
                    <p className="upload-icon">
                        <InboxOutlined />
                    </p>
                    <p className="upload-text">点击或拖拽文件到此区域上传</p>
                    <p className="upload-hint">支持单次上传最多 {maxCount} 个文件。</p>
                </Upload.Dragger>
            </div>
        ),
        table: (
            <div className="file-content">
                {/* 示例内容 */}
                <h3>表格导入</h3>
                <p>请上传结构化的表格文件，支持 CSV 或 Excel 格式。</p>
                <Upload.Dragger
                    name="files"
                    multiple
                    accept=".csv,.xlsx"
                    beforeUpload={beforeUpload}
                    fileList={fileList}
                    onChange={(info) => handleFileChange(info.fileList)}
                    maxCount={maxCount}
                    action={action}
                    className="upload-area"
                >
                    <p className="upload-icon">
                        <InboxOutlined />
                    </p>
                    <p className="upload-text">点击或拖拽文件到此区域上传</p>
                    <p className="upload-hint">支持单次上传最多 {maxCount} 个文件。</p>
                </Upload.Dragger>
            </div>
        ),
        webpage: (
            <div className="file-content">
                {/* 示例内容 */}
                <h3>网页导入</h3>
                <p>请输入网页 URL，我们将自动抓取并导入内容。</p>
                <input type="text" placeholder="输入网页 URL" className="url-input" />
            </div>
        ),
    };

    return (
        <div className="file-import">
            <div className="options">
                <div className="option-group">
                    <div
                        className={`option ${selectedOption === "document" ? "active" : ""}`}
                        onClick={() => setSelectedOption("document")}
                    >
                        <span className="icon">📖</span>
                        <span className="label">文档</span>
                        <span className="description">自动解析文档，使用方便</span>
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
                        className={`option ${selectedOption === "table" ? "active" : ""}`}
                        onClick={() => setSelectedOption("table")}
                    >
                        <span className="icon">📊</span>
                        <span className="label">表格</span>
                        <span className="description">结构化表格导入，支持多列检索</span>
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
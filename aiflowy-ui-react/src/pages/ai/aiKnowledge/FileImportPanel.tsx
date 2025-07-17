import React, { useState} from "react";
import {
    Button,
    Col,
    Input,
    InputNumber,
    InputNumberProps,
    message, Progress,
    Row,
    Select,
    Slider,
    Steps, Table, TableProps, Tag,
    Upload,
    UploadProps,
} from "antd";
import {
    CheckCircleOutlined, LeftOutlined, SyncOutlined,
} from "@ant-design/icons";
import "../style/FileImportPanel.less";
import {isBrowser} from "../../../libs/ssr";
import axios from "axios";
import PreviewContainer from "./PreviewContainer.tsx";
import uploadIcon from '../../../assets/upload.png'
import {UploadChangeParam, UploadFile} from "antd/es/upload";
const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;
const tokenKey = `${import.meta.env.VITE_APP_TOKEN_KEY}`;
import docIcon from '../../../assets/docIcon.png'
import excelIcon from '../../../assets/excelIcon.png'
import CustomDeleteIcon from "../../../components/CustomIcon/CustomDeleteIcon.tsx";
import {uuid} from "../../../libs/uuid.ts";
interface DataType {
    key: string;
    fileName: string;
    percent: number;
    size: number;
    operation?: React.ReactNode;
}

interface saveDocDataType {
    key: string;
    fileName: string;
    status: React.ReactNode;
}

interface FileImportPanelProps {
    data?: object; // 参数
    maxCount?: number; // 最大上传文件数量
    action?: string; // 上传接口地址
    onBack?: () => void;
    style?: React.CSSProperties;
}
interface PreviewLoadingProps {
    spinning?: boolean;
    tip?: string;
}
interface AiDocumentType {
    chunkSize: number, // 分段大小
    overlapSize: number, // 分段重叠大小
    regex: string,
    rowsPerChunk: number,
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
    overlapSize: number
    title: string
}
// 文件导入页面组件
const FileImportPanel: React.FC<FileImportPanelProps> = ({ data, action, onBack, style }) => {
    const [disabledConfirm, setDisabledConfirm] = useState<boolean>(false);
    const [dataPreView, setDataPreView] = useState<PreviewItem[]>([]);
    const [aiDocumentData, setAiDocumentData] = useState<AiDocumentData>();
    const [confirmImport, setConfirmImport] = useState<boolean>(false);
    const [selectedSplitter, setSelectedSplitter] = useState<string>('SimpleDocumentSplitter');
    const [regex, setRegex] = useState<string>('');
    const [confirmDisabled, setConfirmDisabled] = useState<boolean>(false);
    const [currentStep, setCurrentStep] = useState<number>(0);

    // 知识库上传文件后返回的地址
    const [filePath, setFilePath] = useState<string>('');

    const token = isBrowser ? localStorage.getItem(authKey) : null;
    const [aiDocument, setAiDocument] = useState<AiDocumentType>({
        chunkSize: 512, // 分段大小
        overlapSize: 128, // 分段重叠大小
        regex: '',
        rowsPerChunk: 50,
        splitterName: selectedSplitter
    })

    const [previewListLoading, setPreviewListLoading] = useState<PreviewLoadingProps>({
        spinning: false,
        tip: '正在加载数据，请稍候...'
    })


    interface PreviewItem {
        sorting: string; // 顺序编号
        content: string; // 内容
        score: string;
    }

    const headers = {
        Authorization: token || "",
        [tokenKey]: token || ""
    };
    interface CustomUploadProps extends UploadProps {
        [key: string]: any; // 添加字符串索引签名
        userWillSave?: string;
        overlapSize?: number;
        chunkSize?: number;
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

    const uploadColumns: TableProps<DataType>['columns'] = [
        {
            title: '文件名称',
            dataIndex: 'fileName',
            key: 'fileName',
            render: (text: any) =><span style={{display: 'flex', alignItems: 'center'}}><img src={text.endsWith('xlsx') ? excelIcon : docIcon} style={{marginRight: '8px',
                width: '32px', height: '32px'}} alt=""/> {text}</span>,
            width: '30%',
        },
        {
            title: '文件上传进度',
            dataIndex: 'percent',
            key: 'percent',
            render: (text: any)  =><Progress percent={text} size="default" />,
            width: '30%',
        },
        {
            title: '文件大小',
            dataIndex: 'size',
            key: 'size',
            render: (text: any)  =><span> {text} KB</span>,
        },
        {
            title: '操作',
            dataIndex: 'operation',
            key: 'operation',
            render: () =>   <a style={{color: 'red'}} onClick={() =>{
                setFileUploadPercentData([])
            }}> <CustomDeleteIcon /> 删除 </a>,
        },
    ];

    const saveDocColumns: TableProps<saveDocDataType>['columns'] = [
        {
            title: '文件名称',
            dataIndex: 'fileName',
            key: 'fileName',
            render: (text: any) =><span style={{display: 'flex', alignItems: 'center'}}><img src={text.endsWith('xlsx') ? excelIcon : docIcon} style={{height: 32, width: 32, marginRight: '8px'}} alt=""/> {text}</span>,
            width: '70%',
        },
        {
            title: '上传状态',
            dataIndex: 'status',
            key: 'status',
            render: (text: any)  => text,
            width: '30%',
        }
    ];


    const [fileUploadPercentData, setFileUploadPercentData] = useState<DataType[]>([]);
    const [saveDocData, setSaveDocData] = useState<saveDocDataType[]>([]);

    const onchangeChunkSize: InputNumberProps['onChange'] = (value) => {
        if (Number.isNaN(value)) {
            return;
        }
        setAiDocument({
            ...aiDocument,
            chunkSize: value !== null ? Number(value) : 512 // 如果 value 可能是 null，设置默认值
        });
    };
    const onchangeOverlapSize: InputNumberProps['onChange'] = (value) => {
        if (Number.isNaN(value)) {
            return;
        }
        setAiDocument({
            ...aiDocument,
            overlapSize: value !== null ? Number(value) : 128 // 如果 value 可能是 null，设置默认值
        });
    };
    const onchangeRowsPerChunk: InputNumberProps['onChange'] = (value) => {
        if (Number.isNaN(value)) {
            return;
        }
        setAiDocument({
            ...aiDocument,
            rowsPerChunk: value !== null ? Number(value) : 50 // 如果 value 可能是 null，设置默认值
        });
    };

    // 定义文件上传前的校验逻辑
    const beforeUploadDocument = (file: File) => {
        setSelectedSplitter("SimpleDocumentSplitter")
        const isAllowedType =
            file.type === "text/plain" ||
            file.type === "application/pdf" ||
            file.type === "application/markdown" ||
            file.type === "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ||
            file.type === "application/vnd.ms-powerpoint" ||  // PPT 文件类型
            file.type === "application/vnd.openxmlformats-officedocument.presentationml.presentation" ||  // PPTX 文件类型
            file.name.endsWith(".md") ||
            file.name.endsWith(".ppt") ||  // 添加 .ppt 扩展名检查
            file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            file.name.endsWith(".pptx");   // 添加 .pptx 扩展名检查


        const isLt20M = file.size / 1024 / 1024 < 20000;

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


    // 状态管理：当前选中的选项
    const [selectedOption] = useState("document");

    // 保存文件
    const saveDocument = () => {
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
                message.success("上传成功");
                setConfirmImport(false);
                setDisabledConfirm(false)
                setConfirmDisabled(true)
                setSaveDocData(prevData =>
                    prevData.map(item => ({
                        ...item,
                        status:  <Tag icon={<CheckCircleOutlined/>} color="green">已完成</Tag>,
                    }))
                );

            } else if (res.data?.errorCode >= 1){
                message.error(res.data?.message);
                setDisabledConfirm(false)
            }
        });
    };

    const  handleUploadChange = (info: UploadChangeParam<UploadFile<any>>) => {
        if (info.file.status === 'done'){
            setFilePath(info.file.response.path)
            return
        }
        if (info.fileList.length > 0){
                fileUploadPercentData.splice(0); // 清空旧数据
                info.fileList.forEach((file) => {
                    setFileUploadPercentData([
                        {
                            key: file.uid,
                            fileName: file.name,
                            percent: Number.parseInt(String(file?.percent ?? 0)),
                            size: file?.size ?? 0,
                        }
                    ]);

                    if (file?.name?.endsWith("xlsx")){
                        setSelectedSplitter("ExcelDocumentSplitter")
                    }
        })

    }
    }

    // 右侧内容映射
    const contentMapping: { [key: string]: JSX.Element } = {
        document: (
            <div style={{width: "100%", height: "100%", display: "flex", flexDirection: "row", ...style}}>

                {currentStep === 0 && selectedOption === 'document'?
                    (
                        <div style={{width: '100%', height: "192px"}} className="file-import-container">
                            {/* 上传区域 */}
                            <Upload.Dragger
                                style={{
                                    backgroundColor: "#FFFFFF"
                                }}
                                name="file"
                                accept=".txt,.pdf,.md,.docx,.ppt,.pptx,.xlsx"
                                beforeUpload={beforeUploadDocument}
                                onChange={handleUploadChange}
                                maxCount={1}
                                // data={uploadData}
                                action={action}
                                headers={headers}
                                className="upload-area"
                            >
                                <p className="upload-icon">
                                    <img src={uploadIcon} alt="" style={{ width: "48px", height: "48px" }} />
                                </p>
                                <p className="upload-text-title" style={{ userSelect: "none" }}>点击或拖拽文件到此区域上传</p>
                                <p className={"upload-text-tip"}>TXT, PDF, DOCX, MD, PPT, PPTX, XLSX 格式文件，单次最多支持上传1个文件，单个大小不超过20M。</p>
                            </Upload.Dragger>
                            <div style={{marginTop: "22px"}}>
                                <Table<DataType> columns={uploadColumns} dataSource={fileUploadPercentData} pagination={false} />
                            </div>
                        </div>
                    ):
                    (<></>)
                }

                {currentStep === 1 ? (
                    <div style={{width: "100%", padding: "0 315px 0 315px", height: "100%", display: "flex", flexDirection: "column"}}>
                        {/* 上传文件 */}
                        <p className={"params-title"}>
                            文件配置
                        </p>

                        <div className={"params-content"}>
                            <Row style={{alignItems: "center"}}>
                                <Col span={3}>
                                文件类型:
                                </Col>
                                <Col span={21}>
                                    <Select
                                        style={{width: "100%"}}
                                        defaultValue={fileUploadPercentData[0]?.fileName.endsWith("xlsx") ? "表格" : "文档"}
                                       />
                                </Col>
                            </Row>
                        </div>

                        <div className={"params-content"}>
                            <Row style={{alignItems: "center"}}>
                                <Col span={3}>
                                    分割器:
                                </Col>
                                <Col span={21}>
                                    {
                                        fileUploadPercentData[0]?.fileName.endsWith("xlsx") ? (
                                                <Select
                                                    value={selectedSplitter}
                                                    style={{ width: '100%' }}
                                                    onChange={(value) => setSelectedSplitter(value)}
                                                    options={[
                                                        { value: 'ExcelDocumentSplitter', label: 'Excel表格分割器' }
                                                    ]}
                                                />

                                        ) : (
                                            <Select
                                                value={selectedSplitter}
                                                style={{ width: '100%' }}
                                                onChange={(value) => setSelectedSplitter(value)}
                                                options={[
                                                    { value: 'SimpleDocumentSplitter', label: '简单文档分割器' },
                                                    { value: 'RegexDocumentSplitter', label: '正则文档分割器' },
                                                    { value: 'SimpleTokenizeSplitter', label: '简单分词器' },
                                                ]}
                                            />
                                        )
                                    }


                                </Col>
                            </Row>
                        </div>


                        {
                            selectedSplitter === 'SimpleDocumentSplitter' || selectedSplitter === 'SimpleTokenizeSplitter' ? (
                                <div>
                                    <div className={"params-content"}>
                                        <Row style={{alignItems: "center"}}>
                                            <Col span={3}>
                                                分段长度:
                                            </Col>
                                            <Col span={17}>
                                                <div style={{border: "1px solid #F0F0F0", borderRadius: "6px"}}>
                                                    <Slider
                                                        min={1}
                                                        max={2048}
                                                        onChange={onchangeChunkSize}
                                                        value={aiDocument.chunkSize}
                                                    />
                                                </div>
                                            </Col>
                                            <Col span={4} style={{ textAlign: "right"}}>
                                                <InputNumber
                                                    min={1}
                                                    max={2048}
                                                    value={aiDocument.chunkSize}
                                                    onChange={onchangeChunkSize}
                                                />
                                            </Col>
                                        </Row>
                                    </div>

                                    <div className={"params-content"}>
                                        <Row style={{alignItems: "center"}}>
                                            <Col span={3}>
                                                分段重叠:
                                            </Col>
                                            <Col span={17}>
                                                <div style={{border: "1px solid #F0F0F0", borderRadius: "6px"}}>
                                                    <Slider
                                                        min={1}
                                                        max={2048}
                                                        value={aiDocument.overlapSize}
                                                        onChange={onchangeOverlapSize}
                                                    />
                                                </div>
                                            </Col>
                                            <Col span={4} style={{ textAlign: "right"}}>
                                                <InputNumber
                                                    min={1}
                                                    max={2048}
                                                    value={aiDocument.overlapSize}
                                                    onChange={onchangeOverlapSize}
                                                />
                                            </Col>
                                        </Row>
                                    </div>
                                </div>
                            ) : selectedSplitter === 'RegexDocumentSplitter' && (
                                <Row style={{alignItems: "center"}}>
                                    <Col span={3}>
                                        正则表达式:
                                    </Col>
                                    <Col span={21}>
                                        <Input
                                            size='large'
                                            placeholder="请输入文本分割的正则表达式"
                                            onChange={(e) => setRegex(e.target.value)}
                                            style={{width: "100%"}}
                                        />
                                    </Col>
                                </Row>
                            )
                        }

                        { (selectedSplitter === "ExcelDocumentSplitter") &&
                            <div className={"params-content"}>
                                <Row style={{alignItems: "center"}}>
                                    <Col span={3}>
                                        单块行数:
                                    </Col>
                                    <Col span={17}>
                                        <Slider
                                            min={1}
                                            max={2048}
                                            value={aiDocument.rowsPerChunk}
                                            onChange={onchangeRowsPerChunk}
                                        />
                                    </Col>
                                    <Col span={4} style={{ textAlign: "right"}}>
                                        <InputNumber
                                            min={1}
                                            max={2048}
                                            value={aiDocument.rowsPerChunk}
                                            onChange={onchangeRowsPerChunk}
                                        />
                                    </Col>
                                </Row>
                            </div>
                        }
                    </div>

                ): (<></>)
                }

                {currentStep === 2 ? (
                    <>
                        <PreviewContainer
                            data={dataPreView}
                            loading={previewListLoading.spinning}
                            confirmImport={confirmImport}
                            disabledConfirm={disabledConfirm}
                            onCancel={() => {
                                setConfirmImport(false);
                                setDataPreView([]);
                            }}
                            onConfirm={saveDocument}
                        />
                    </>


                ):(<></>)}

                {
                    currentStep === 3 &&
                    <div style={{width: "100%", height: "100%", display: "flex", flexDirection: "column"}}>
                        <Table<saveDocDataType> columns={saveDocColumns} dataSource={saveDocData} pagination={false} />
                    </div>
                }
            </div>
        )
    };

    return (
        <div className="file-import">
            <div style={{paddingBottom: "22px"}}>
                <Button  onClick={() => onBack?.()}><LeftOutlined/>退出</Button>
            </div>
            <div className="options">
                <Steps
                    current={currentStep}
                    items={[
                        {
                            title: '文件上传',
                        },
                        {
                            title: '参数设置',
                        },
                        {
                            title: '分段预览',
                        },
                        {
                            title: '确认导入',
                        },
                    ]}
                />
            </div>

            <div className="file-import-content">
                {contentMapping[selectedOption]}
            </div>

            <div style={{display: "flex", flexDirection: "row", justifyContent: "flex-end", gap:"10px", marginTop: '10px'}}>
                {
                    currentStep !== 0 ? (
                        <>
                            <Button
                                style={{
                                    minWidth: '100px',
                                    height: '36px'
                                }}
                                onClick={() => {
                                    setCurrentStep(currentStep - 1)
                                }}
                            >
                                上一步
                            </Button>

                            {
                                (currentStep === 3 && !confirmDisabled) && (
                                    <Button
                                        type="primary"
                                        style={{
                                            minWidth: '100px',
                                            height: '36px'
                                        }}
                                        onClick={() => {
                                            setSaveDocData(prevData =>
                                                prevData.map(item => ({
                                                    ...item,
                                                    status: <Tag icon={<SyncOutlined spin />} color="processing">上传中</Tag>,
                                                }))
                                            );
                                            // setConfirmDisabled(true)
                                            saveDocument()
                                        }}
                                    >
                                        开始导入
                                    </Button>
                                )
                            }
                        </>
                    ) : null
                }


                {
                    currentStep === 3 ? (<></>) : (
                        <Button
                            type="primary"
                            style={{
                                minWidth: '100px',
                                height: '36px'
                            }}
                            loading={disabledConfirm}
                            onClick={()=>{
                                if (currentStep === 0){
                                    if (fileUploadPercentData.length === 0){
                                        message.error("请上传文件")
                                        return
                                    }
                                    if (fileUploadPercentData.length > 0 && filePath === ''){
                                        message.error("请等待文件上传成功！")
                                        return;
                                    }
                                }

                                if (currentStep === 1){
                                    // 处理数据，返回预览数据
                                    // 构造 FormData 对象
                                    const formData = new FormData();
                                    formData.append("filePath", filePath);
                                    formData.append("fileOriginName", fileUploadPercentData[0].fileName.toString().split('.')[0]);
                                    formData.append("knowledgeId", uploadProps.knowledgeId as string); // 添加 knowledgeId
                                    formData.append("splitterName", uploadProps.splitterName);
                                    formData.append("chunkSize", uploadProps.chunkSize?.toString() ?? "512");
                                    formData.append("overlapSize", uploadProps.overlapSize?.toString() ?? "128");
                                    formData.append("regex", uploadProps.regex);
                                    formData.append("rowsPerChunk", uploadProps.rowsPerChunk);

                                    // 发起 POST 请求
                                    axios.post("/api/v1/aiDocument/textSplit", formData, {
                                        headers: {
                                            ...headers,
                                            "Content-Type": "multipart/form-data",
                                        },
                                    }).then((res) => {
                                        setDataPreView(res.data.data.previewData)
                                        setAiDocumentData(res.data.data.aiDocumentData)
                                        setPreviewListLoading({
                                            spinning: false,
                                            tip: ''
                                        })
                                    })
                                }

                                if (currentStep === 2){
                                    if (dataPreView.length === 0){
                                        message.error('请等待数据处理完成')
                                    }
                                }

                                setSaveDocData([{
                                    key: uuid(),
                                    fileName: fileUploadPercentData[0].fileName,
                                    status: <Tag>待上传</Tag>
                                }])
                                setCurrentStep(currentStep+1)
                            }}
                        >
                            下一步
                        </Button>


                    )
                }

            </div>

        </div>
    );
};

export default FileImportPanel;

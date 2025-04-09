import React, {useState} from 'react';
import {ActionConfig, ColumnsConfig} from "../../../components/AntdCrud";
import CrudPage from "../../../components/CrudPage";
import {EditLayout} from "../../../components/AntdCrud/EditForm.tsx";
import {convertDatetimeUtil} from "../../../libs/changeDatetimeUtil";
import {Button, message, Modal, Upload, Image} from "antd";
import {InboxOutlined} from "@ant-design/icons";
import {isBrowser} from "../../../libs/ssr";
import {usePost} from "../../../hooks/useApis";

const baseUrl = `${import.meta.env.VITE_APP_SERVER_ENDPOINT}/`;
const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;
const tokenKey = `${import.meta.env.VITE_APP_TOKEN_KEY}`;
interface AiDocumentSourceMaterialProps {
	knowledgeId?: string;
}
//字段配置
const columnsConfig: ColumnsConfig<any> = [


	{
		form: {
			type: "input"
		},
		dataIndex: "fileName",
		title: "文件名称",
		key: "fileName",
		width: "400px",
	},
	{
		hidden: true,
		form: {
			type: "hidden"
		},
		dataIndex: "id",
		key: "id"
	},

	{
		form: {
			type: "select",
			attrs: {
				options: [
					{
						value: 1,
						label: "图片"
					},
					{
						value: 2,
						label: "视频"
					},
					{
						value: 3,
						label: "文本"
					}
				]
			}
		},
		dataIndex: "fileType",
		title: "文件类型",
		supportSearch: true,
		key: "fileType",
		width: "200px",
		render: (item) => {
			if (item === 1){
				return "图片";
			} else if(item === 2){
				return "视频"
			} else if(item === 3){
				return "文本"
			}
}
	},
	{
		form: {
			type: "input"
		},
		dataIndex: "created",
		title: "创建时间",
		width: "250px",
		render: convertDatetimeUtil,
		key: "created"
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "filePath",
		title: "图片",
		width: "250px",
		render: (item, record) =>{
			// record 是当前行的数据对象
			if (record.fileType === 1) { // 如果是图片类型
				return (
					<Image
						src={`${baseUrl}api/images${item}`}
						style={{ width: "55px", height: "40px"}}
					/>
				);
			} else {
				return <span></span>; // 非图片类型时显示占位符
			}
		},
		key: "created"
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "fileSize",
		title: "文件大小",
		width: "200px",
		key: "fileSize"
	}
];

//编辑页面设置
const editLayout = {
	labelLayout: "horizontal",
	labelWidth: 80,
	columnsCount: 1,
	openType: "modal"
} as EditLayout;
// 查看图片模态框


export const AiDocumentSourceMaterial: React.FC<AiDocumentSourceMaterialProps> = (knowledgeId:any) => {
	const token = isBrowser ? localStorage.getItem(authKey) : null;
	const headers = {
		Authorization: token || "",
		[tokenKey]: token || ""
	};

	const [isModalOpen, setIsModalOpen] = useState(false);
	const [seeImageModel, setSeeImageModel] = useState(false);
//操作列配置
	const actionConfig = {
		addButtonEnable: true,
		detailButtonEnable: false,
		deleteButtonEnable: true,
		editButtonEnable: false,
		hidden: false,
		width: "200px",

	} as ActionConfig<any>
	const handleOk = () => {
		setIsModalOpen(false);
		setFileList([]);
	};

	const handleCancel = () => {
		setIsModalOpen(false);
	};

	const handleImageOk = () => {
		setSeeImageModel(false);
	};
	const handleImageCancel = () => {
		setIsModalOpen(false);
	};
	const [fileList, setFileList] = useState<any[]>([]);
	const {doPost} = usePost('/api/v1/aiDocumentSourceMaterial/smsave')
	// 定义文件上传前的校验逻辑
	const beforeUpload = (file: File) => {
		const isLt15M = file.size / 1024 / 1024 < 15;
		if (!isLt15M) {
			message.error("单个文件大小不能超过 15MB！");
		}
		return isLt15M;
	};
	// 更新文件列表的状态
	const handleFileChange = (newFileList: any[]) => {
		newFileList.forEach((file) => {
			// 如果用户是预览返回的分割效果
				if (file.status === "done" && file.response.errorCode === 0) {
					// 处理上传成功的文件
					// 文件上传成功之后返回的文件地址保存到tb_document_source_material表中
					const paras = {
						filePath: file.response?.path,
						...knowledgeId
					}
					doPost({data: paras}).then(res => {
						console.log('res',res)
						if (res.data.errorCode === 0) {
							message.success(`文件 ${file.name} 上传成功`);
						} else {
						}
					})
				} else if (file.status === "error") {
					// 处理上传失败的文件
					message.error(`文件 ${file.name} 上传失败`);
				}
		});
		setFileList(newFileList);
	};
    return (
        <CrudPage columnsConfig={columnsConfig} tableAlias="aiDocumentSourceMaterial"
				  actionConfig={actionConfig} editLayout={editLayout} addButtonEnable={false} customButton={() => {
					  return (
						  <div>
							  <Button type="primary" onClick={() => {
								  setFileList([])
								  setIsModalOpen(true)
							  }}>上传文件</Button>
							  <Modal title="素材上传" open={isModalOpen} onOk={handleOk} onCancel={handleCancel}>
								  <Upload.Dragger
									  name="file"
									  multiple
									  // accept=".txt,.pdf,.md,.docx"
									  beforeUpload={beforeUpload}
									  fileList={fileList}
									  onChange={(info) => handleFileChange(info.fileList)}
									  // maxCount={1}
									  // data={uploadProps}
									  action={'/api/v1/commons/upload'}
									  headers={headers}
									  className="upload-area"
								  >
									  <p className="upload-icon">
										  <InboxOutlined />
									  </p>
									  <p className="upload-text" style={{ userSelect: "none" }}> 点击或拖拽文件到此区域上传</p>
									  <p className="upload-hint" style={{ userSelect: "none" }}>支持单次上传最多 {1} 个文件。</p>
								  </Upload.Dragger>
							  </Modal>
							{/*  查看图片模态框*/}
							  <Modal title="图片详情" open={seeImageModel} onOk={handleImageOk} onCancel={handleImageCancel} style={{textAlign: "center"}}>
								  <Image src={"http://localhost:8080/api/images"} alt="图片" style={{
									  width: '200px',
									  height: '200px',
									  objectFit: 'contain', // 等比例缩放，完整显示图片
								  }} />
							  </Modal>
						  </div>
					  )
		}}/>
    )
};

export default {
    path: "source_material/aiDocumentSourceMaterial",
    element:  AiDocumentSourceMaterial
};

// @ts-ignore
import React, {useEffect} from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
// @ts-ignore
import {EditLayout} from "../../components/AntdCrud/EditForm.tsx";
import {useLocation} from "react-router-dom";
import {useLayout} from "../../hooks/useLayout.tsx";
import {Button, Tooltip} from "antd";
import {ArrowLeftOutlined} from "@ant-design/icons";


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
		form: {
			type: "input"
		},
		dataIndex: "jobName",
		title: "任务名称",
		key: "jobName",
		width: "120px"
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "jobParams",
		title: "任务参数",
		key: "jobParams",
		render: (_,item) => {
			return (
				<Tooltip title={item.jobParams}>
					<div style={{
						whiteSpace: "nowrap",
						overflow: "hidden",
						textOverflow: "ellipsis",
						maxWidth: "260px",
						display: "inline-block",
					}}>{item.jobParams}</div>
				</Tooltip>
			)
		}
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "jobResult",
		title: "执行结果",
		key: "jobResult",
		render: (_,item) => {
			return (
				<Tooltip title={item.jobResult}>
					<div style={{
						whiteSpace: "nowrap",
						overflow: "hidden",
						textOverflow: "ellipsis",
						maxWidth: "260px",
						display: "inline-block",
					}}>{item.jobResult}</div>
				</Tooltip>
			)
		}
	},

	{
		showSorterTooltip: true,
		form: {
			type: "input"
		},
		dataIndex: "errorInfo",
		title: "错误信息",
		key: "errorInfo",
		width: "100px"
	},

	{
		supportSearch: true,
		form: {
			type: "select"
		},
		dataIndex: "status",
		title: "执行状态",
		key: "status",
		width: "100px",
		dict: {
			name: "jobResult"
		}
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "remark",
		title: "备注",
		key: "remark"
	}
];

//编辑页面设置
const editLayout = {
	labelLayout: "horizontal",
	labelWidth: 80,
	columnsCount: 1,
	openType: "modal"
} as EditLayout;


//操作列配置
const actionConfig = {
    addButtonEnable: false,
    detailButtonEnable: false,
    deleteButtonEnable: false,
    editButtonEnable: false,
    hidden: false,
    width: "50px",
    
} as ActionConfig<any>

export const SysJobLog: React.FC = () => {

	const location = useLocation();
	const { jobId } = location.state || {};

	const {setOptions} = useLayout();

	useEffect(() => {
		if (jobId) {
			setOptions({
				showBreadcrumb: true,
				breadcrumbs: [
					{title: '首页'},
					{title: '系统管理'},
					{title: '定时任务'},
					{title: '日志'},
				],
				breadcrumbRightEl: <Button onClick={() => history.back()}><ArrowLeftOutlined />返回</Button>
			})
		}
		return () => {
			setOptions({
				showBreadcrumb: true,
				breadcrumbs: [],
				breadcrumbRightEl: null
			})
		}
	},[jobId])

    return (
        <CrudPage
			rowSelectEnable={false}
			addButtonEnable={false}
			params={{ jobId }}
			columnsConfig={columnsConfig} tableAlias="sysJobLog"
            actionConfig={actionConfig} editLayout={editLayout}/>
    )
};

export default {
    path: "/sys/sysJobLog",
    element:  SysJobLog
};

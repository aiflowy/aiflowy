import React from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import {EditLayout} from "../../components/AntdCrud/EditForm.tsx";
import {dateFormat} from "../../libs/utils.ts";


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
			rules: [],
			type: "treeselect"
		},
		dataIndex: "deptId",
		title: "部门",
		key: "deptId",
		dict: {
			name: "sysDept",
		}
	},

	{
		supportSearch: true,
		form: {
			rules: [],
			type: "input"
		},
		dataIndex: "positionName",
		title: "岗位名称",
		key: "positionName"
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "positionCode",
		title: "岗位编码",
		key: "positionCode"
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "sortNo",
		title: "排序",
		key: "sortNo"
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

	{
		form: {
			type: "hidden"
		},
		dataIndex: "created",
		title: "创建时间",
		key: "created",
		render: (value) => {
			return <span>{dateFormat(value,"YYYY-MM-DD HH:mm:ss")}</span>
		},
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "remark",
		title: "备注",
		key: "remark"
	},
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
    addButtonEnable: true,
    detailButtonEnable: false,
    deleteButtonEnable: true,
    editButtonEnable: true,
    hidden: false,
    width: "200px",
    
} as ActionConfig<any>

export const SysPosition: React.FC = () => {
    return (
        <CrudPage columnsConfig={columnsConfig} tableAlias="sysPosition"
            actionConfig={actionConfig} editLayout={editLayout}/>
    )
};

export default {
    path: "/sys/sysPosition",
    element:  SysPosition
};

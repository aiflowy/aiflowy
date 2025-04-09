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

	// {
	// 	form: {
	// 		type: "input"
	// 	},
	// 	dataIndex: "tenantId",
	// 	title: "租户ID",
	// 	key: "tenantId"
	// },

	{
		hidden: true,
		dataIndex: "parentId",
		title: "父级部门",
		key: "parentId",
		dict: {
			name: "sysDept",
			disabledItemAndChildrenKey: "id",
			editExtraData: [
				{
					value: 0,
					label: "顶级部门"
				}
			]
		},
		form: {
			type: "select",
			rules: [{required: true, message: "请选择上级"}],
		},
		editCondition: (data) => {
			return data?.deptCode !== 'root_dept';
		},
	},

	// {
	// 	form: {
	// 		type: "input"
	// 	},
	// 	dataIndex: "ancestors",
	// 	title: "父级部门ID集合",
	// 	key: "ancestors"
	// },

	{
		supportSearch: true,
		form: {
			type: "input"
		},
		dataIndex: "deptName",
		title: "部门名称",
		key: "deptName"
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "deptCode",
		title: "部门编码",
		key: "deptCode",
		editCondition: (data) => {
			return data?.deptCode !== 'root_dept';
		},
	},

	{
		form: {
			type: "input"
		},
		dataIndex: "sortNo",
		title: "排序",
		key: "sortNo"
	},

	// {
	// 	form: {
	// 		type: "select"
	// 	},
	// 	dataIndex: "status",
	// 	title: "数据状态",
	// 	key: "status",
	// 	dict: {
	// 		name: "data_status"
	// 	}
	// },

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

export const SysDept: React.FC = () => {
    return (
        <CrudPage columnsConfig={columnsConfig} params={{asTree: true}} showType={"list"} tableAlias="sysDept"
            actionConfig={actionConfig} editLayout={editLayout}/>
    )
};

export default {
    path: "/sys/sysDept",
    element:  SysDept
};

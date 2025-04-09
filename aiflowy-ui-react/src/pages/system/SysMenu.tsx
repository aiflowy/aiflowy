import React from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import {EditLayout} from "../../components/AntdCrud/EditForm.tsx";
// import {dateFormat} from "../../libs/utils.ts";
import AntdIcon from "../../components/AntdIcon";


//字段配置
const columnsConfig: ColumnsConfig<any> = [
	// {
	// 	title: '',
	// 	dataIndex: 'key',
	// 	rowScope: 'row',
	// },
	{
		hidden: true,
		form: {
			type: "hidden"
		},
		dataIndex: "id",
		key: "id",
	},

	{
		hidden: true,
		title: '父菜单',
		dataIndex: 'parentId',
		key: 'parentId',
		dict: {
			name: "sysMenu",
			disabledItemAndChildrenKey: "id",
			editExtraData: [
				{
					value: 0,
					label: "顶级菜单"
				}
			]
		},
		form: {
			type: "select",
			rules: [{required: true, message: "请选择上级"}],
		}
	},

	{
		width: 150,
		supportSearch: true,
		form: {
			type: "input"
		},
		dataIndex: "menuTitle",
		title: "菜单标题",
		key: "menuTitle",
	},

	{
		width: 100,
		fixed: true,
		supportSearch: true,
		form: {
			type: "select"
		},
		dataIndex: "menuType",
		title: "菜单类型",
		key: "menuType",
		dict: {
			name: "menuType"
		}
	},

	{
		width: 100,
		form: {
			type: "input"
		},
		dataIndex: "menuUrl",
		title: "菜单url",
		key: "menuUrl",
	},

	// {
	// 	form: {
	// 		type: "input"
	// 	},
	// 	dataIndex: "component",
	// 	title: "组件路径",
	// 	key: "component"
	// },

	{
		width: 50,
		dataIndex: "menuIcon",
		title: "图标",
		key: "menuIcon",
		render: (value) => {
			return <AntdIcon name={value}/>
		},
		form: {
			type: "icon"
		}
	},

	{
		width: 100,
		form: {
			type: "select"
		},
		dataIndex: "isShow",
		title: "是否显示",
		key: "isShow",
		dict: {
			name: "yesOrNo"
		}
	},

	{
		width: 100,
		form: {
			type: "input"
		},
		dataIndex: "permissionTag",
		title: "权限标识",
		key: "permissionTag"
	},

	{
		width: 50,
		form: {
			type: "input"
		},
		dataIndex: "sortNo",
		title: "排序",
		key: "sortNo"
	},

	// {
	// 	width: 150,
	// 	form: {
	// 		type: "hidden"
	// 	},
	// 	dataIndex: "created",
	// 	title: "创建时间",
	// 	key: "created",
	// 	render: (text: any) => {
	// 		return dateFormat(text, "YYYY-MM-DD HH:mm:ss");
	// 	}
	// },

];

//编辑页面设置
const editLayout = {
	labelLayout: "horizontal",
	labelWidth: 0,
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
	fixedRight: true,
    
} as ActionConfig<any>

export const SysMenu: React.FC = () => {
    return (
        <CrudPage rowSelectEnable={false} columnsConfig={columnsConfig} tableAlias="sysMenu" params={{asTree: true}} showType={"list"}
            actionConfig={actionConfig} editLayout={editLayout}/>
    )
};

export default {
    path: "/sys/sysMenu",
    element:  SysMenu
};

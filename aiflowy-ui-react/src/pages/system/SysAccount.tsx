import React, {useEffect, useState} from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import {EditLayout} from "../../components/AntdCrud/EditForm.tsx";
import {dateFormat} from "../../libs/utils.ts";
import {Avatar, Form, Modal, Tree, TreeProps} from "antd";
import {SettingOutlined} from "@ant-design/icons";
import {useGetManual, usePostManual} from "../../hooks/useApis.ts";
import {Key} from "rc-table/lib/interface";
import {DictSelect} from "../../components/DictSelect";

//编辑页面设置
const editLayout = {
    labelLayout: "horizontal",
    labelWidth: 80,
    columnsCount: 1,
    openType: "modal"
} as EditLayout;

export const SysAccount: React.FC = () => {

    const [open, setOpen] = useState(false);
    const [currentAccount, setCurrentAccount] = useState({id: undefined, dataScope: undefined});
    const {result: treeData, doGet: getTreeData} = useGetManual("/api/v1/sysDept/list?asTree=true",);
    const [checkedKeys, setCheckedKeys] = useState<Key[]>([]);
    const {doGet: getCheckedKeys} = useGetManual(`/api/v1/sysAccount/detail?id=${currentAccount.id}`,);
    const {loading: saveLoading, doPost: saveDataScope} = usePostManual(`/api/v1/sysAccount/update`);
    const {doGet: getDataScopeState} = useGetManual(`/api/v1/public/getDataScopeState`)
    const [showDataScope, setShowDataScope] = useState(false);
    const onClose = () => {
        setOpen(false);
    };

    const onCheck: TreeProps['onCheck'] = (checkedKeys) => {
        if (typeof checkedKeys === "object" && checkedKeys !== null && "checked" in checkedKeys) {
            setCheckedKeys(checkedKeys.checked as Key[]);
        } else {
            setCheckedKeys(checkedKeys as Key[]);
        }
    };

    const changeDataScope = (value: any) => {
        setCurrentAccount({
            ...currentAccount,
            dataScope: value
        })
        // 非自定义部门数据就清空
        if (value !== 5) {
            setCheckedKeys([])
        }
    }

    useEffect(() => {
        getDataScopeState().then(res => {
            setShowDataScope(res.data.enable)
        })
        if (open) {
            getTreeData()
                .then(getCheckedKeys)
                .then((resp) => {
                    if (resp.data.data.deptIdList) {
                        setCheckedKeys(JSON.parse(resp.data.data.deptIdList))
                    } else {
                        setCheckedKeys([])
                    }
                    setCurrentAccount({
                        ...currentAccount,
                        dataScope: resp.data.data.dataScope
                    })
                })
        }
    }, [open])

    const isAdmin = (data: any) => {
        return (data?.accountType == 1 || data?.accountType == 99)
    }

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
            dataIndex: "loginName",
            title: "登录账号",
            key: "loginName",
            supportSearch: true,
            editCondition: (data) => {
                return !data?.id;
            },
        },

        {
            hidden: true,
            form: {
                type: "password"
            },
            dataIndex: "password",
            title: "密码",
            key: "password",
            editCondition: (data) => {
                return !data?.id;
            },
        },

        {
            form: {
                type: "input"
            },
            dataIndex: "nickname",
            title: "昵称",
            key: "nickname"
        },

        {
            form: {
                type: "input"
            },
            dataIndex: "mobile",
            title: "手机电话",
            key: "mobile"
        },

        {
            form: {
                type: "email"
            },
            dataIndex: "email",
            title: "邮件",
            key: "email"
        },

        {
            form: {
                type: "image"
            },
            dataIndex: "avatar",
            title: "账户头像",
            key: "avatar",
            render: (value) => {
                return value ? <Avatar src={value}/> : <></>
            },
        },

        {
            title: "部门",
            key: "deptId",
            form: {
                type: "select",
            },
            dict: {
                name: "sysDept"
            },
            editCondition: (data) => {
                return !isAdmin(data);
            },
        },
        {
            title: "岗位",
            key: "positionIds",
            form: {
                type: "select",
                attrs: {
                    mode: "multiple",
                }
            },
            dict: {
                name: "sysPosition",
                paramKeys: ["deptId"],
                asTree: true,
            }
        },
        {
            title: "角色",
            key: "roleIds",
            form: {
                type: "select",
                attrs: {
                    mode: "multiple",
                }
            },
            dict: {
                name: "sysRole",
            },
            editCondition: (data) => {
                return !isAdmin(data);
            },
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
            },
            editCondition: (data) => {
                return !isAdmin(data);
            },
        },
        {
            form: {
                type: "hidden"
            },
            dataIndex: "created",
            title: "创建时间",
            key: "created",
            render: (value) => {
                return <span>{dateFormat(value, "YYYY-MM-DD HH:mm:ss")}</span>
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

    //操作列配置
    const actionConfig = {
        addButtonEnable: true,
        detailButtonEnable: false,
        deleteButtonEnable: true,
        editButtonEnable: true,
        hidden: false,
        width: "200px",
        customActions: (data) => {
            if (isAdmin(data)) {
                return <></>
            }
            return showDataScope ? (<a onClick={() => {
                setCurrentAccount(data)
                setOpen(true)
            }}> <SettingOutlined/>数据权限</a>) : (<></>)
        },

    } as ActionConfig<any>

    return (
        <>
            <Modal title="设置数据权限"
                   open={open}
                   width={"600px"}
                   onCancel={onClose}
                   onOk={() => {
                       const updateData = {
                           id: currentAccount.id,
                           dataScope: currentAccount.dataScope,
                           deptIdList: JSON.stringify(checkedKeys)
                       }
                       saveDataScope({
                           data: updateData
                       }).then(() => {
                           setOpen(false)
                       })
                   }}
                   confirmLoading={saveLoading}
                   destroyOnClose
            >
                <Form
                    name="dataScope"
                    labelCol={{span: 5}}
                    autoComplete="off"
                >
                    <Form.Item
                        label="数据权限类型"
                        name="dataScope"
                    >
                        <DictSelect code={'dataScope'}
                                    initvalue={currentAccount.dataScope}
                                    onChange={changeDataScope}/>
                    </Form.Item>

                    {currentAccount.dataScope === 5 ? <Form.Item
                        label="选择部门"
                        name="deptIdList"
                    >
                        <Tree treeData={treeData?.data}
                              checkStrictly
                              checkable
                              checkedKeys={checkedKeys}
                              onCheck={onCheck}
                              fieldNames={{
                                  key: "id",
                                  title: "deptName",
                                  children: "children"
                              }}
                        />
                    </Form.Item> : <></>}
                </Form>
            </Modal>
            <CrudPage columnsConfig={columnsConfig} tableAlias="sysAccount"
                      actionConfig={actionConfig} editLayout={editLayout}/>
        </>
    )
};

export default {
    path: "/sys/sysAccount",
    element: SysAccount
};

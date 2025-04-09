import React, {useCallback, useMemo, useState} from 'react';
import {ColumnsConfig} from "../AntdCrud";
import {Button, Card, Popconfirm, Space, theme, Tree, TreeProps} from "antd";
import {
    DeleteOutlined,
    DownOutlined,
    FormOutlined,
    PlusOutlined,
    ReloadOutlined,
} from "@ant-design/icons";
import {useList, useRemove} from "../../hooks/useApis.ts";
import EditPage from "../EditPage";
import Layout, {Content} from "antd/es/layout/layout";
import Sider from "antd/es/layout/Sider";


export type TreePageProps = {
    treeTableAlias: string,
    treeCardTitle?: string,
    treeCardExtra?: React.ReactNode,
    treeEditModalTitle?: string,
    treeColumns?: ColumnsConfig<any>,
    primaryColumnName?: string,
    treeTitleColumnName?: string,
    treeTitleIconRender?: (title: any) => any,
    treeEditable?: boolean,
    onTreeSelect: (value: any) => void,
    queryParams?: Record<string, any>,
    children: React.ReactNode,
}

const TreePage: React.FC<TreePageProps> = ({
                                               treeTableAlias
                                               , treeCardTitle
                                               , treeCardExtra
                                               , treeEditModalTitle
                                               , treeColumns
                                               , primaryColumnName = "id"
                                               , treeTitleColumnName = "title"
                                               , treeTitleIconRender
                                               , treeEditable = true
                                               , onTreeSelect
                                               , queryParams
                                               , children
                                           }) => {

    const {loading, result, doGet} = useList(treeTableAlias, {asTree: true, ...queryParams});

    const {doRemove} = useRemove(treeTableAlias);

    const [isEditOpen, setIsEditOpen] = useState(false);
    const [editData, setEditData] = useState(null);

    const appendActionButtons = useCallback((items: any[]) => {
        if (items) {
            items.forEach((item) => {
                if (treeEditable) {
                    item.title = typeof item.title === "object" ? item.title : (
                        <div style={{width: "160px", display: "flex"}} className={"autoHiddenActions"}>
                            <div>{item[treeTitleColumnName] || item.name}</div>
                            <Space style={{marginLeft: "auto"}} className={"actions"}>
                                {!(item.withSystem) && <Popconfirm
                                    title="确定删除？"
                                    description="您确定要删除这条数据吗？"
                                    placement={"rightTop"}
                                    onCancel={(event) => {
                                        event?.stopPropagation();
                                    }}
                                    onConfirm={(event) => {
                                        event?.stopPropagation();
                                        doRemove({
                                            data: {[primaryColumnName]: item[primaryColumnName]}
                                        }).then(() => {
                                            onTreeSelect?.('')
                                        })
                                    }}
                                    okText="确定"
                                    cancelText="取消"
                                >
                                    <DeleteOutlined onClick={(event) => {
                                        event.stopPropagation()
                                    }}/>
                                </Popconfirm>}
                                <FormOutlined onClick={(event) => {
                                    event.stopPropagation()
                                    setEditData(item)
                                    setIsEditOpen(true)
                                }}/>
                            </Space>
                        </div>
                    )
                }

                if (treeTitleIconRender) {
                    item.icon = treeTitleIconRender(item);
                }

                if (item.children) {
                    appendActionButtons(item.children)
                }
            })
        }
    }, [])


    const treeData = useMemo(() => {
        appendActionButtons(result?.data)
        return [
            {
                title: '全部数据',
                key: '',
            }].concat(result?.data || [])
    }, [result]);

    const onSelect: TreeProps['onSelect'] = (selectedKeys) => {
        if (selectedKeys.length > 0) {
            onTreeSelect?.(selectedKeys[0] as string)
        } else {
            onTreeSelect?.('')
        }
    };

    const closeEditGroup = () => {
        setIsEditOpen(false)
        setEditData(null)
    }

    const {
        token: {colorBgContainer, borderRadiusLG},
    } = theme.useToken();

    return (
        <>
            {treeEditable && <EditPage modalTitle={treeEditModalTitle || ""}
                                       tableAlias={treeTableAlias}
                                       open={isEditOpen}
                                       columnsConfig={treeColumns || []}
                                       onRefresh={() => doGet()}
                                       data={editData}
                                       onSubmit={closeEditGroup}
                                       onCancel={closeEditGroup}
            />}

            <Layout
                style={{background: "#f5f5f5", borderRadius: borderRadiusLG, height: "100%"}}
            >
                <Sider style={{background: colorBgContainer,}} width={260}>
                    <Card title={treeCardTitle}
                          bordered={false}
                          loading={loading}
                          style={{
                              height: "100%",
                              boxShadow: "none",
                              borderRadius: 0,
                          }}
                          styles={{
                              body: {
                                  padding: "24px 0"
                              }
                          }}
                          extra={<Space>
                              <Button icon={<ReloadOutlined/>} onClick={() => doGet()}/>
                              {treeEditable && <Button icon={<PlusOutlined/>} onClick={() => setIsEditOpen(true)}/>}
                              {treeCardExtra}
                          </Space>}
                    >
                        <Tree
                            switcherIcon={<DownOutlined/>}
                            defaultExpandAll={true}
                            onSelect={onSelect}
                            treeData={treeData}
                            showIcon={!!treeTitleIconRender}
                        />
                    </Card>
                </Sider>
                <Content style={{
                    margin: '0 24px',
                    background: "#fff",
                    borderTopLeftRadius: "3px",
                    borderTopRightRadius: "3px"
                }}>
                    {children}
                </Content>
            </Layout>
        </>
    )
};

export default TreePage

import React, {useCallback, useMemo, useState} from 'react';
import {ColumnsConfig} from "../AntdCrud";
import {Card, Popconfirm, Space, theme } from "antd";
import {
    DeleteOutlined, DoubleLeftOutlined, DoubleRightOutlined,
    FormOutlined,
    PlusOutlined,
    ReloadOutlined,
} from "@ant-design/icons";
import {useList, useRemove} from "../../hooks/useApis.ts";
import EditPage from "../EditPage";
import Layout, {Content} from "antd/es/layout/layout";
import Sider from "antd/es/layout/Sider";
import './CustomTree.css';

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
                                               treeTableAlias,
                                               treeCardTitle,
                                               treeEditModalTitle,
                                               treeColumns,
                                               primaryColumnName = "id",
                                               treeTitleColumnName = "title",
                                               treeTitleIconRender,
                                               treeEditable = true,
                                               onTreeSelect,
                                               queryParams,
                                               children
                                           }) => {

    const {loading, result, doGet} = useList(treeTableAlias, {asTree: true, ...queryParams});

    const {doRemove} = useRemove(treeTableAlias);

    const [isEditOpen, setIsEditOpen] = useState(false);
    const [editData, setEditData] = useState(null);
    const [collapsed, setCollapsed] = useState(false);
    const [selectedKey, setSelectedKey] = useState('');

    // 处理列表项的操作按钮
    const processListItems = useCallback((items: any[]) => {
        if (!items) return [];

        return items.map(item => {
            const newItem = {...item};

            if (treeEditable) {
                // 构建列表项的显示内容，包括操作按钮
                newItem.title = (
                    <div style={{width: "100%", display: "flex", alignItems: "center"}}>
                        <div style={{flex: 1, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap"}}>
                            {item[treeTitleColumnName] || item.name}
                        </div>
                        <Space style={{marginLeft: "8px"}}>
                            {!(item.withSystem) && <Popconfirm
                                title="确定删除？"
                                description="您确定要删除这条数据吗？"
                                placement={"rightTop"}
                                onCancel={(event) => event?.stopPropagation()}
                                onConfirm={(event) => {
                                    event?.stopPropagation();
                                    doRemove({data: {[primaryColumnName]: item[primaryColumnName]}})
                                        .then(() => onTreeSelect(''));
                                }}
                            >
                                <DeleteOutlined />
                            </Popconfirm>}
                            <FormOutlined onClick={(event) => {
                                event.stopPropagation();
                                setEditData(item);
                                setIsEditOpen(true);
                            }} />
                        </Space>
                    </div>
                );
            } else {
                newItem.title = item[treeTitleColumnName] || item.name;
            }

            // 处理图标
            if (treeTitleIconRender) {
                newItem.icon = treeTitleIconRender(item);
            }

            return newItem;
        });
    }, [treeEditable, treeTitleColumnName, treeTitleIconRender, primaryColumnName, doRemove, onTreeSelect]);

    // 生成列表数据
    const listData = useMemo(() => {
        const processedItems = processListItems(result?.data || []);

        // 添加"全部数据"项
        return [
            {
                key: '',
                title: '全部',
                icon: <span />
            }
        ].concat(processedItems);
    }, [result, processListItems]);

    const onSelect = (item: any) => {
        onTreeSelect(item.key);
        setSelectedKey(item.key)
    };

    const closeEditGroup = () => {
        setIsEditOpen(false);
        setEditData(null);
    }

    const {
        token: {colorBgContainer},
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
                style={{ height: "100%", width: '100%', overflow: 'hidden'}}
            >
                <Sider style={{background: colorBgContainer, borderRadius: '8px 0px 0px 8px'}} width={220}
                       collapsed={collapsed}>
                    <Card title={collapsed ? null : treeCardTitle}
                          loading={loading}
                          style={{
                              height: "100%",
                              borderRadius: '8px 0px 0px 8px'
                          }}
                          styles={{
                              body: {
                                  padding: "8px 8px"
                              }
                          }}
                          extra={<Space>
                              <div style={{width: "100%", display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "center", gap: "10px"}}>
                                  {!collapsed &&
                                  <>
                                  <span style={{cursor: "pointer"}} onClick={() => doGet()}>
                                        <ReloadOutlined />
                                  </span>
                                      {treeEditable && <PlusOutlined onClick={() => setIsEditOpen(true)}/>}
                                  </>

                                  }

                                  {
                                      collapsed ?
                                          (
                                      <span onClick={() => setCollapsed(!collapsed)} style={{cursor: "pointer"}}>
                                         <DoubleRightOutlined />
                                      </span>
                                      )
                                          :
                                          (
                                          <span onClick={() => setCollapsed(!collapsed)} style={{cursor: "pointer"}}>
                                               <DoubleLeftOutlined />
                                          </span>
                                          )

                                  }

                              </div>
                          </Space>}
                    >
                        <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>

                            <div style={{ display: "flex", flexDirection: "column", gap: "8px",

                            } } >
                                {listData.map((item) => (
                                    <div
                                        onClick={() => onSelect(item)}
                                        key={item.key}
                                        className={`tree-item ${selectedKey === item.key ? 'tree-item-selected' : ''} ${
                                            collapsed && 'collapsed-only-icon'
                                        }`}
                                    >
                                        <span style={{ display: "flex", alignItems: "center", height: "100%" }}>
                                            {item?.icon}
                                        </span>
                                        {!collapsed && (
                                            <div style={{ display: "flex", alignItems: "center", flexGrow: 1 }}>
                                                {item.title}
                                            </div>
                                        )}
                                        {(collapsed && item.title == '全部') && (
                                            <div style={{ display: "flex", alignItems: "center", flexGrow: 1 }}>
                                                {item.title}
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>

                    </Card>
                </Sider>

                <Content style={{
                    background: "#fff",
                    borderTopLeftRadius: "3px",
                    borderTopRightRadius: "3px",
                    paddingLeft: "40px",
                }}>
                    {children}
                </Content>
            </Layout>
        </>
    )
};

export default TreePage;
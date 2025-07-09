import React, {forwardRef, ReactNode, useEffect, useImperativeHandle, useState} from 'react';
import {ColumnsConfig} from "../AntdCrud";
import {
    Avatar,
    Button,
    Card,
    Col,
    Dropdown, MenuProps,
    Modal,
    Pagination,
    Row,
    Space,
    Spin,
    Tooltip,
    Typography
} from "antd";
import {
    EditOutlined, EllipsisOutlined,
} from "@ant-design/icons";
import {usePage, useRemove} from "../../hooks/useApis.ts";
import EditPage from "../EditPage";
import {EditLayout} from "../AntdCrud/EditForm.tsx";
import {Empty} from "antd";
import "./card_page.less"
import {Page} from "../../types/Page.ts";
import {useUrlParams} from "../../hooks/useUrlParams.ts";
import "../../pages/commons/commonStyle.less"
import CustomDeleteIcon from "../CustomIcon/CustomDeleteIcon.tsx";
import {useCheckPermission} from "../../hooks/usePermissions.tsx";
import KeywordSearchForm from "../AntdCrud/KeywordSearchForm.tsx";
import defaultNoDataLogo from '../../assets/defaultNoData.png'

export type CardPageProps = {
    ref?: any,
    tableAlias: string,
    defaultPageSize?: number,
    editModalTitle?: string,
    editLayout?: EditLayout,
    addButtonText?: string,
    columnsConfig: ColumnsConfig<any>,
    avatarKey?: string,
    defaultAvatarSrc?: string,
    titleKey?: string,
    descriptionKey?: string,
    customActions?: (data: any, existNodes: React.ReactNode[]) => React.ReactNode[],
    customHandleButton?: () => React.ReactNode[],
    addCardTitle?: string,
    optionsText?: {
        addCardTitle?: string, // 新增按钮的文本
        noDataText?: string, // 暂无数据的文本
        noDataAddButtonText?: string, // 无任何数据新增按钮的文本
    },
    optionIconPath?: {
        noDataIconPath?: string, // 无任何数据时的图标
    },
    customMenuItems?: MenuProps['items']
}

const CardPage: React.FC<CardPageProps> = forwardRef(({
                                                          tableAlias,
                                                          defaultPageSize = 12,
                                                          editModalTitle,
                                                          editLayout,
                                                          columnsConfig,
                                                          avatarKey = "avatar",
                                                          defaultAvatarSrc,
                                                          addButtonText = "新增",
                                                          titleKey = "title",
                                                          descriptionKey = "description",
                                                          customActions = (_data: any, existNodes: any) => existNodes,
                                                          customHandleButton = () => [],
                                                          optionsText = {},
                                                          optionIconPath = {},
                                                          customMenuItems = []
                                                      }, ref) => {

    useImperativeHandle(ref, () => ({
        refresh: () => {
            doGet({
                params: {
                    ...searchParams,
                    pageNumber: localPageNumber,
                    pageSize,
                }
            });
        }
    }));


    const {
        loading,
        result,
        doGet
    } = usePage(tableAlias, {}, {manual: true})

    const {doRemove} = useRemove(tableAlias);

    const [isEditOpen, setIsEditOpen] = useState(false);
    const [editData, setEditData] = useState(null);

    const [urlParams, setUrlParams] = useUrlParams();
    const pageNumber = +(urlParams.pageNumber || ((result?.data) as Page<any>)?.pageNumber || 1)
    let pageSize = +(urlParams.pageSize || ((result?.data) as Page<any>)?.pageSize || defaultPageSize)

    const [localPageSize, setLocalPageSize] = useState(
        +(urlParams.pageSize || ((result?.data) as Page<any>)?.pageSize || defaultPageSize)
    );

    const [localPageNumber, setLocalPageNumber] = useState(pageNumber)
    const [searchParams, setSearchParams] = useState(urlParams)

    // const [sortKey, setSortKey] = useState<string | undefined>()
    // const [sortType, setSortType] = useState<"asc" | "desc" | undefined>()

    const savePermission = useCheckPermission(`/api/v1/${tableAlias}/save`)
    const removePermission = useCheckPermission(`/api/v1/${tableAlias}/remove`)

    const closeEdit = () => {
        setIsEditOpen(false)
        setEditData(null)
    }

    useEffect(() => {
        doGet({
            params: {
                ...searchParams,
                pageNumber: localPageNumber,
                pageSize: localPageSize, // 使用状态值
            }
        })
    }, [localPageNumber, localPageSize, searchParams])


    const buildActions = (item: any) => {
        // 存储所有操作的数组
        const allActions: ReactNode[] = [];

        // 添加编辑操作
        const editNode = savePermission ? (
            <Space onClick={() => {
                setEditData(item);
                setIsEditOpen(true);
            }}>
                <EditOutlined key="edit"/>
                <span>编辑</span>
            </Space>
        ) : null;

        if (editNode) {
            allActions.push(editNode);
        }

        // 获取自定义操作
        const customNodes = customActions(item, []);

        // 如果有自定义操作，将它们添加到allActions
        if (customNodes && customNodes.length > 0) {
            allActions.push(...customNodes);
        }

        // 计算直接显示的操作数量（不包括删除操作）
        const visibleActionsCount = Math.min(3, allActions.length);

        // 直接显示的操作
        const visibleActions = allActions.slice(0, visibleActionsCount);

        // 需要放入下拉菜单的操作（除删除外的多余操作）
        const dropdownActions = [...allActions.slice(visibleActionsCount)];

        // 创建删除操作节点并添加到下拉菜单
        if (removePermission) {
            dropdownActions.push(
                <Space onClick={() => {
                    Modal.confirm({
                        title: '确定要删除吗?',
                        content: '此操作不可逆，请谨慎操作。',
                        onOk() {
                            doRemove({data: {id: item.id}}).then(doGet);
                        },
                    });
                }}>
                    <CustomDeleteIcon key="delete-icon"/>
                    <span>删除</span>
                </Space>
            );
        }

        // 创建下拉菜单（如果有需要放入下拉菜单的操作）
        let dropdownMenu = null;
        if (dropdownActions.length > 0) {
            dropdownMenu = (
                <Dropdown
                    menu={{
                        items: dropdownActions.map((action, index) => ({
                            key: `dropdown-${index}`,
                            label: action,
                        })),
                    }}
                >
                    <EllipsisOutlined key="ellipsis" title="更多操作"/>
                </Dropdown>
            );
        }

        // 返回组合后的操作列表
        return dropdownMenu ? [...visibleActions, dropdownMenu] : visibleActions;
    };

    return (
        <div style={{padding: "8px"}}>
            <EditPage modalTitle={editModalTitle || ""}
                      tableAlias={tableAlias}
                      open={isEditOpen}
                      columnsConfig={columnsConfig}
                      onRefresh={() => doGet()}
                      data={editData}
                      onSubmit={closeEdit}
                      onCancel={closeEdit}
                      layout={editLayout}
            />
            <Spin spinning={loading}>

                <KeywordSearchForm
                    tableAlias={tableAlias}
                    onSearch={(values: any) => {
                        setLocalPageNumber(1);
                        setSearchParams(values);
                        setUrlParams(values);
                    }}
                    customMenuItems={customMenuItems}
                    columns={columnsConfig} addButtonText={addButtonText}
                    customHandleButton={customHandleButton()}
                    setIsEditOpen={() => {
                        setIsEditOpen(true)
                    }}
                />

                <Row className={"card-row"} gutter={[16, 16]}>

                    {result?.data?.records?.length > 0 ? result?.data?.records?.map((item: any) => (
                        <Col span={6} key={item.id}
                             xs={24}  // 在超小屏幕下占满一行
                             sm={12}  // 在小屏幕下每行显示2个
                             md={8}   // 在中等屏幕下每行显示3个
                             lg={6}   // 在大屏幕下每行显示4个（保持原来的span={6}效果）
                        >

                            <Card
                                hoverable={true}
                                className={"card-hover"}
                                actions={buildActions(item)}>
                                <Card.Meta
                                    avatar={<Avatar src={item[avatarKey] || defaultAvatarSrc}
                                                    style={{width: '48px', height: '48px'}}/>}
                                    title={item[titleKey]}
                                    description={
                                        <Tooltip title={item[descriptionKey] || "暂无描述"} placement="top">
                                            <div style={{
                                                display: '-webkit-box',
                                                WebkitLineClamp: 1,
                                                WebkitBoxOrient: 'vertical',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                                minHeight: '1em',
                                            }}>
                                                {item[descriptionKey] || "暂无描述"} {/* 使用破折号作为占位符 */}
                                            </div>
                                        </Tooltip>
                                    }
                                />
                            </Card>
                        </Col>
                    )) : (<>
                        <Empty
                            image={optionIconPath?.noDataIconPath ? optionIconPath.noDataIconPath : defaultNoDataLogo}
                            className={"empty-container"}
                            description={
                                <Typography.Text style={{color: '#969799'}}>
                                    {optionsText.noDataText || "暂无数据"}
                                </Typography.Text>
                            }
                        >

                            {useCheckPermission(`/api/v1/${tableAlias}/save`) && (
                                <Button
                                    style={{borderColor: '#0066FF', color: '#0066FF', width: '195px', height: '48px'}}
                                    onClick={() => {
                                        setIsEditOpen(true)
                                    }}>
                                    {optionsText.noDataAddButtonText || "创建"}
                                </Button>
                            )}

                        </Empty>

                    </>)}
                </Row>
                {result?.data?.records?.length > 0 &&
                    <div style={{display: "flex", justifyContent: "center", paddingTop: "20px"}}>
                        <Pagination
                            showSizeChanger
                            align="center"
                            defaultCurrent={1}
                            current={localPageNumber}
                            pageSizeOptions={["12", "24", "48", "96"]}
                            total={result?.data?.totalRow}
                            pageSize={localPageSize}
                            hideOnSinglePage={true}
                            onChange={(page, size) => {
                                setLocalPageNumber(page)
                                if (size !== localPageSize) {
                                    setLocalPageSize(size)
                                    setUrlParams({...urlParams, pageSize: size})
                                }
                            }}
                            onShowSizeChange={(_, size) => {
                                setLocalPageSize(size)
                                setLocalPageNumber(1) // 通常改变每页条数会回到第一页
                                setUrlParams({...urlParams, pageSize: size, pageNumber: 1})
                            }}
                        />
                    </div>}
            </Spin>
        </div>
    )
})

export default CardPage

import React, {useEffect, useState} from 'react';
import {ColumnsConfig} from "../AntdCrud";
import {Avatar, Button, Card, Col, Dropdown, Modal, Pagination, Row, Spin} from "antd";
import {
    DeleteOutlined,
    EditOutlined, EllipsisOutlined,
    PlusOutlined,
} from "@ant-design/icons";
import {usePage, useRemove} from "../../hooks/useApis.ts";
import EditPage from "../EditPage";
import {useBreadcrumbRightEl} from "../../hooks/useBreadcrumbRightEl.tsx";
import {EditLayout} from "../AntdCrud/EditForm.tsx";
import {Empty} from "antd";
import "./card_page.less"
import SearchForm from "../AntdCrud/SearchForm.tsx";
import {Page} from "../../types/Page.ts";
import {useUrlParams} from "../../hooks/useUrlParams.ts";

export type CardPageProps = {
    tableAlias: string,
    defaultPageSize?: number,
    editModalTitle?: string,
    editLayout?: EditLayout
    addButtonText?: string,
    columnsConfig: ColumnsConfig<any>,
    avatarKey?: string,
    defaultAvatarSrc?: string,
    titleKey?: string,
    descriptionKey?: string,
    customActions?: (data: any, existNodes: React.ReactNode[]) => React.ReactNode[]
}

const CardPage: React.FC<CardPageProps> = ({
                                               tableAlias
                                               , defaultPageSize = 12
                                               , editModalTitle
                                               , editLayout
                                               , addButtonText = "新增"
                                               , columnsConfig
                                               , avatarKey = "avatar"
                                               , defaultAvatarSrc
                                               , titleKey = "title"
                                               , descriptionKey = "description"
                                               , customActions = (_data, existNodes) => existNodes,
                                           }) => {

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


    const [localPageNumber, setLocalPageNumber] = useState(pageNumber)
    const [searchParams, setSearchParams] = useState(urlParams)

    // const [sortKey, setSortKey] = useState<string | undefined>()
    // const [sortType, setSortType] = useState<"asc" | "desc" | undefined>()

    useBreadcrumbRightEl(<Button type={"primary"} onClick={() => setIsEditOpen(true)}>
        <PlusOutlined/>{addButtonText}</Button>)

    const closeEdit = () => {
        setIsEditOpen(false)
        setEditData(null)
    }


    useEffect(() => {
        doGet({
            params: {
                ...searchParams,
                pageNumber: localPageNumber,
                pageSize,
            }
        })
    }, [localPageNumber, searchParams])


    return (
        <>

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

                <SearchForm columns={columnsConfig} colSpan={6}
                            onSearch={(values: any) => {
                                setLocalPageNumber(1)
                                setSearchParams(values)
                                setUrlParams(values)
                            }}
                            onSearchValueInit={(key) => urlParams[key]}
                />

                <Row className={"card-row"} gutter={[16, 16]}>
                    {result?.data?.records?.length > 0 ? result?.data?.records?.map((item: any) => (
                        <Col span={6} key={item.id}>
                            <Card actions={[
                                ...customActions(item, [
                                    <EditOutlined key="edit" title="编辑" onClick={() => {
                                        setEditData(item)
                                        setIsEditOpen(true)
                                    }}/>,
                                    <Dropdown menu={{
                                        items: [
                                            {
                                                key: 'delete',
                                                label: '删除',
                                                icon: <DeleteOutlined/>,
                                                danger: true,
                                                onClick: () => {
                                                    Modal.confirm({
                                                        title: '确定要删除吗?',
                                                        content: '此操作不可逆，请谨慎操作。',
                                                        onOk() {
                                                            doRemove({
                                                                data: {
                                                                    id: item.id
                                                                }
                                                            }).then(doGet)
                                                        },
                                                        onCancel() {
                                                        },
                                                    });
                                                },
                                            }
                                        ],
                                    }}>
                                        <EllipsisOutlined key="ellipsis" title="更多操作"/>
                                    </Dropdown>,
                                ]),
                            ]}>
                                <Card.Meta
                                    avatar={<Avatar src={item[avatarKey] || defaultAvatarSrc}/>}
                                    title={item[titleKey]}
                                    description={
                                        <p>{item[descriptionKey]}</p>
                                    }
                                />
                            </Card>
                        </Col>
                    )) : (<><Empty image={Empty.PRESENTED_IMAGE_SIMPLE} className={"empty-container"}/></>)}
                </Row>
                {result?.data?.records?.length > 0 &&
                    <div style={{display: "flex", justifyContent: "center", paddingTop: "20px"}}>
                        <Pagination
                            showQuickJumper
                            // showSizeChanger
                            defaultCurrent={1}
                            total={result?.data?.totalRow}
                            pageSize={pageSize}
                            showTotal={(total) => `共 ${total} 条数据`}
                            onChange={(page) => {
                                setLocalPageNumber(page)
                            }}
                        />
                    </div>}
            </Spin>
        </>
    )
};

export default CardPage

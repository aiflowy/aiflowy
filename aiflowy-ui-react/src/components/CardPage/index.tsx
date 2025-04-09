import React, {useState} from 'react';
import {ColumnsConfig} from "../AntdCrud";
import {Avatar, Button, Card, Col, Dropdown, Modal, Row, Spin} from "antd";
import {
    DeleteOutlined,
    EditOutlined, EllipsisOutlined,
    PlusOutlined,
} from "@ant-design/icons";
import {useList, useRemove} from "../../hooks/useApis.ts";
import EditPage from "../EditPage";
import {useBreadcrumbRightEl} from "../../hooks/useBreadcrumbRightEl.tsx";
import {EditLayout} from "../AntdCrud/EditForm.tsx";
import { Empty } from "antd";
import "./card_page.less"

export type CardPageProps = {
    tableAlias: string,
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

    const {doGet,loading, result} = useList(tableAlias);
    const {doRemove} = useRemove(tableAlias);

    const [isEditOpen, setIsEditOpen] = useState(false);
    const [editData, setEditData] = useState(null);

    useBreadcrumbRightEl(<Button type={"primary"} onClick={() => setIsEditOpen(true)}>
        <PlusOutlined/>{addButtonText}</Button>)

    const closeEdit = () => {
        setIsEditOpen(false)
        setEditData(null)
    }

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
                <Row className={"card-row"} gutter={[16, 16]}>
                    {result?.data.length > 0 ? result?.data.map((item: any) => (
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
                    )):(<><Empty image={Empty.PRESENTED_IMAGE_SIMPLE} className={"empty-container"} /></>)}
                </Row>
            </Spin>
        </>
    )
};

export default CardPage

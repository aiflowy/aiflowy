import {Button, Divider, Input, message, Modal} from "antd";
import {ModalProps} from "antd/es/modal/interface";
import React, {useEffect, useState} from "react";
import {SearchOutlined} from "@ant-design/icons";
import {usePage} from "../../../hooks/useApis.ts";
import {useNavigate} from "react-router-dom";


export type BotDataItemProps = {
    title?: string,
    description?: string,
    icon?: string,
    onButtonClick?: () => void
}

export const BotDataItem: React.FC<BotDataItemProps> = ({title, description, icon = "/favicon.png", onButtonClick}) => {
    return (
        <div style={{
            display: "flex",
            gap: "20px",
            alignItems: "center",
            background: "#efefef",
            padding: "10px",
            borderRadius: "3px"
        }}>
            <div style={{"width": "40px"}}><img src={icon} alt="" style={{maxWidth: "100%"}}/>
            </div>
            <div style={{flexGrow: 1}}>
                <div>{title}</div>
                <div style={{color: "#999999"}}>{description}</div>
            </div>
            <div>
                <Button onClick={() => {
                    onButtonClick?.()
                }}>添加</Button>
            </div>
        </div>
    )
}



export type BotModalProps = {
    tableAlias:string,
    tableTitle:string,
    goToPage:string,
    onSelectedItem?: (item: any) => void
} & ModalProps

export const BotModal: React.FC<BotModalProps> = (props) => {

    const [items, setItems] = useState<any[]>()
    const [keyword, setKeyword] = useState<string>('')
    const {result, doGet} = usePage(props.tableAlias, {pageSize: 10});

    const navigate = useNavigate();

    const goToPage = props.goToPage;

    useEffect(() => {
        if (result?.data?.records) {
            setItems(result.data.records)
        } else {
            setItems([])
        }
    }, [result]);

    const searchHandler = () => {
        doGet({
            params: {
                title: keyword,
                pageSize: 10,
                title__op: "like"
            }
        }).then((resp) => {
            if (!resp?.data?.data?.records?.length) {
                message.error("没有找到关于 \"" + keyword + "\" 相关数据")
            }
        })

    }

    return (
        <Modal title={<div style={{paddingLeft: "0"}}>添加{props.tableTitle}</div>} footer={null} {...props} width={"800px"}
               height={"600px"}>
            <div style={{display: "flex", gap: "20px", marginTop: "20px"}}>
                <div style={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    gap: "10px",
                    background: "#efefef",
                    padding: "20px 10px",
                    borderRadius: "3px"
                }}>
                    <div><Button onClick={() => { goToPage ? navigate(goToPage) : ''}}>创建{props.tableTitle}</Button></div>
                    <Divider/>
                    {/*<div style={{padding: "10px 20px", background: "#fff", borderRadius: "3px"}}>*/}
                    {/*    <UserOutlined/> 我的{props.tableTitle}*/}
                    {/*</div>*/}
                    {/*<div style={{padding: "10px 20px", background: "#fff", borderRadius: "3px"}}>*/}
                    {/*    <ShopOutlined/> {props.tableTitle}市场*/}
                    {/*</div>*/}
                </div>
                <div style={{flexGrow: 1}}>
                    <div style={{display: "flex", gap: "10px"}}>
                        <Input size="middle" placeholder="请输入关键字" prefix={<SearchOutlined/>}
                               value={keyword} onChange={(e) => setKeyword(e.target.value)}
                               style={{flexGrow: 1}}
                               onKeyDown={(e) => {
                                   if (e.key === "Enter") {
                                       searchHandler()
                                   }
                               }}
                        />
                        <Button onClick={searchHandler}>搜索</Button>
                    </div>
                    <div style={{marginTop: "20px", display: "flex", flexDirection: "column", gap: "15px",}}>
                        {items?.map((item) => <BotDataItem key={item.id}
                                                            onButtonClick={() => props.onSelectedItem?.(item)}
                                                            title={item.title}
                                                            description={item.description}
                                                            icon={item.icon}/>)}
                    </div>
                </div>
            </div>
        </Modal>
    );
}
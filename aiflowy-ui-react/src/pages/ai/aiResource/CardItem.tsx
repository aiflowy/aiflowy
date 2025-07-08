import React from "react";
import {Card, Space, Tag, Tooltip} from "antd";
import videoIcon from '../../../assets/video-icon.png'
import audioIcon from '../../../assets/audio-icon.png'
import docIcon from '../../../assets/doc-icon.png'
import otherIcon from '../../../assets/other-icon.png'

export type CardItemProps = {
    item: any,
    onPreview: (item: any) => void
}

export const CardItem: React.FC<CardItemProps> = (props) => {

    const getResourceTypeTag = (item: any) => {
        let res;
        switch (item.resourceType) {
            case 0:
                res = <Tag color="#E6F0FF"><span style={{color: "#0066FF"}}>图片</span></Tag>
                break;
            case 1:
                res = <Tag color="#FFEED1"><span style={{color: "#FFA200"}}>视频</span></Tag>
                break;
            case 2:
                res = <Tag color="#F1EBFF"><span style={{color: "#5600FF"}}>音频</span></Tag>
                break;
            case 3:
                res = <Tag color="#E6F9FF"><span style={{color: "#0099CC"}}>文档</span></Tag>
                break;
            default:
                res = <Tag>其他</Tag>
                break;
        }
        return res;
    }

    const getOringTypeTag = (item: any) => {
        let res;
        switch (item.origin) {
            case 0:
                res = <Tag color="#E6FEFC"><span style={{color: "#00B8A9"}}>系统上传</span></Tag>
                break;
            case 1:
                res = <Tag color="#E6F0FF"><span style={{color: "#0066FF"}}>工作流生成</span></Tag>
                break;
            default:
                res = <Tag>未知</Tag>
                break;
        }
        return res;
    }

    const getSrc = (item: any) => {
        let res;
        switch (item.resourceType) {
            case 1:
                res = videoIcon
                break;
            case 2:
                res = audioIcon
                break;
            case 3:
                res = docIcon
                break;
            default:
                res = otherIcon
        }
        return res;
    }

    return (
        <>
            <Card
                hoverable
                style={{
                    marginBottom: "24px",
                    borderRadius: "0",
                }} styles={{
                body: {padding: '12px 12px 16px 12px'},
            }}
            >
                <div className={"card-item-content"}
                     onClick={() => {
                         props.onPreview(props.item)
                     }}>
                    {props.item.resourceType === 0 && <img src={props.item.resourceUrl} style={{
                        width: "100%",
                        height: "100%",
                        borderRadius: "8px"
                    }}/>}
                    {props.item.resourceType !== 0 &&
                        <img src={getSrc(props.item)} style={{width: "100px", height: "100px"}}/>}
                </div>
                <div style={{marginTop: "10px", marginBottom: "12px"}}>
                    <Tooltip title={props.item.resourceName}>
                        <div style={{
                            whiteSpace: "nowrap",
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                            maxWidth: "100%",
                            fontWeight: "500"
                        }}>{props.item.resourceName}.{props.item.suffix}</div>
                    </Tooltip>
                </div>
                <Space size={1}>
                    {getOringTypeTag(props.item)}
                    {getResourceTypeTag(props.item)}
                </Space>
            </Card>
        </>
    )
}
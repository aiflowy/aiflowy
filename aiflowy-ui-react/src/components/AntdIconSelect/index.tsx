import React, {useState} from "react";
import * as Icon from "@ant-design/icons";
import {Flex, Input, Modal, Pagination, Tooltip} from "antd";
import AntdIcon from "../AntdIcon";
import styles from "./index.module.less";
import useChangesEffect from "../../hooks/useChangesEffect";

interface Props {
    value?: string;
    onChange?: (value: string) => void;
}

const iconNames = Object.keys(Icon);

const pageSize = 96;

const AntdIconSelect: React.FC<Props> = ({value, onChange}): React.ReactNode => {

    const [iconName, setIconName] = useState(value)
    const [page, setPage] = useState(1)
    const [open, setOpen] = useState(false);

    useChangesEffect(() => {
        onChange?.(iconName!)
    }, [iconName])

    const start = (page - 1) * pageSize;
    let end = start + pageSize;
    if (end >= iconNames.length) end = iconNames.length - 4
    const myIconNames = iconNames.slice(start, end);
    return (
        <>
            <Modal
                title="Icon 列表" width={"600px"} open={open}
                destroyOnClose
                onCancel={() => {
                    setOpen(false)
                }}
                okButtonProps={{style: {display: "none"}}}
            >
                <div style={{marginTop:"20px"}}>
                    <Flex wrap="wrap" gap="small">
                        {myIconNames.map((name) =>
                            <Tooltip placement="top" title={name} key={name}>
                                <div style={{padding: "6px"}} key={name} className={styles.icon} onClick={() => {
                                    setIconName(name)
                                    setOpen(false)
                                }}>
                                    <AntdIcon name={name}/>
                                </div>
                            </Tooltip>
                        )}
                    </Flex>
                    <Pagination defaultCurrent={1} total={iconNames.length} style={{marginTop: "40px"}}
                                pageSize={pageSize}
                                showSizeChanger={false}
                                onChange={(page) => {
                                    setPage(page)
                                }}
                    />
                </div>
            </Modal>
            <Input addonAfter={<AntdIcon name={iconName || "SettingOutlined"} onClick={() => setOpen(true)}/>}
                   value={iconName}/>
        </>
    )
};

export default AntdIconSelect;

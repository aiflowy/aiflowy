import React, { useState } from "react";
import * as Icon from "@ant-design/icons";
import { Flex, Input, Modal, Pagination, Tooltip } from "antd";
import AntdIcon from "../AntdIcon";
import styles from "./index.module.less";
import useChangesEffect from "../../hooks/useChangesEffect";
import * as CustomIcons from "../CustomIcon/CustomIcons.tsx";

interface Props {
    value?: string;
    onChange?: (value: string) => void;
}

// 合并Ant Design图标和自定义图标
const allIcons = {
    ...CustomIcons.customIcons,
    ...Icon
    };

const iconNames = Object.keys(allIcons).filter(name => !name.startsWith('IconFont')); // 排除IconFont组件

const pageSize = 96;

const AntdIconSelect: React.FC<Props> = ({ value, onChange }): React.ReactNode => {
    const [iconName, setIconName] = useState(value);
    const [page, setPage] = useState(1);
    const [open, setOpen] = useState(false);

    useChangesEffect(() => {
        onChange?.(iconName!);
    }, [iconName]);

    const start = (page - 1) * pageSize;
    let end = start + pageSize;
    if (end >= iconNames.length) end = iconNames.length;
    const myIconNames = iconNames.slice(start, end);

    return (
        <>
            <Modal
                title="图标列表"
                width={"600px"}
                open={open}
                destroyOnClose
                onCancel={() => {
                    setOpen(false);
                }}
                footer={null}
            >
                <div style={{ marginTop: "20px" }}>
                    <Flex wrap="wrap" gap="small">
                        {myIconNames.map((name) => (
                            <Tooltip placement="top" title={name} key={name}>
                                <div
                                    style={{ padding: "6px" }}
                                    className={styles.icon}
                                    onClick={() => {
                                        setIconName(name);
                                        setOpen(false);
                                    }}
                                >
                                    <AntdIcon name={name} style={{ fontSize: '24px' }} />
                                </div>
                            </Tooltip>
                        ))}
                    </Flex>
                    <Pagination
                        current={page}
                        total={iconNames.length}
                        style={{ marginTop: "40px" }}
                        pageSize={pageSize}
                        showSizeChanger={false}
                        onChange={(page) => {
                            setPage(page);
                        }}
                    />
                </div>
            </Modal>
            <Input
                value={iconName}
                readOnly
                addonAfter={
                    <span
                        style={{ cursor: 'pointer', display: 'inline-block' }}
                        onClick={() => setOpen(true)}
                    >
      <AntdIcon name={iconName || "SettingOutlined"} />
    </span>
                }
            />
        </>
    );
};

export default AntdIconSelect;

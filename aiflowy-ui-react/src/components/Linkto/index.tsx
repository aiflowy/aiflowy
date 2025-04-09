import React, {useState} from 'react';
import {Drawer, Modal} from "antd";
import {ModalProps} from "antd/es/modal/interface";
import {DrawerProps} from "antd/es/drawer";


export type LinktoProps = {
    children: React.ReactNode,
    extra?: React.ReactNode,
    component: React.FC<any>,
    params: any,
    dataMapping?: Record<string, string>
    openType?: "modal" | "drawer",
    modalProps?: ModalProps & DrawerProps,
    title?: React.ReactNode,
    instance?: any
    width?: string | number
}
const Linkto: React.FC<LinktoProps> = ({
                                           children
                                           , component
                                           , params
                                           , openType = "drawer"
                                           , modalProps
                                           , title
                                           , extra
                                           , instance
                                           , width = "70%"
                                       }) => {

    const [open, setOpen] = useState(false);
    const modalOrDrawerProps = {
        placement: "right",
        width,
        destroyOnClose: true,
        onCancel: () => {
            setOpen(false)
        },
        onClose: () => {
            setOpen(false)
        },
        title:title || children,
        extra,
        ...modalProps
    } as ModalProps & DrawerProps;

    if (instance) {
        instance.close = () => {
            setOpen(false)
        }
    }

    const element = React.createElement(component, {...params, instance});
    const modalOrDrawer = "modal" === openType
        ? <Modal open={open} {...modalOrDrawerProps}>{element}</Modal>
        : <Drawer open={open} {...modalOrDrawerProps}>{element}</Drawer>


    return <>
        {modalOrDrawer}
        <a onClick={() => {
            setOpen(true)
        }}>{children}</a>
    </>
};


export default Linkto;
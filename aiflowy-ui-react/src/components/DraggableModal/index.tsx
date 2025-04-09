import React, {useRef, useState} from "react";
import {ModalProps} from "antd/es/modal/interface";
import {Modal} from "antd";
import Draggable, {DraggableData, DraggableEvent} from "react-draggable";

export type DraggableModalProps = {} & ModalProps
const DraggableModal: React.FC<DraggableModalProps> = (props) => {
    const [disabled, setDisabled] = useState(true);
    const [bounds, setBounds] = useState({left: 0, top: 0, bottom: 0, right: 0});
    const draggleRef = useRef<HTMLDivElement>(null);

    const onStart = (_event: DraggableEvent, uiData: DraggableData) => {
        const {clientWidth, clientHeight} = window.document.documentElement;
        const targetRect = draggleRef.current?.getBoundingClientRect();
        if (!targetRect) {
            return;
        }
        setBounds({
            left: -targetRect.left + uiData.x,
            right: clientWidth - (targetRect.right - uiData.x),
            top: -targetRect.top + uiData.y,
            bottom: clientHeight - (targetRect.bottom - uiData.y),
        });
    };

    return (
        <Modal  {...props}
                title={
                    <div
                        style={{width: '100%', cursor: 'move'}}
                        onMouseOver={() => {
                            if (disabled) {
                                setDisabled(false);
                            }
                        }}
                        onMouseOut={() => {
                            setDisabled(true);
                        }}
                        onFocus={() => {
                        }}
                        onBlur={() => {
                        }}
                    >
                        {props.title}
                    </div>
                }

                modalRender={(modal) => (
                    <Draggable
                        disabled={disabled}
                        bounds={bounds}
                        nodeRef={draggleRef}
                        onStart={(event, uiData) => onStart(event, uiData)}
                    >
                        <div ref={draggleRef}>{modal}</div>
                    </Draggable>
                )}
        >
            {props.children}
        </Modal>
    )
}

export default DraggableModal;
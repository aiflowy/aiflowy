import React, {useRef, useState} from 'react';
import {useSse} from "../../hooks/useSse.ts";
import {Modal} from "antd";
import Draggable, {DraggableData, DraggableEvent} from 'react-draggable';
import {AiProChat} from "../AiProChat/AiProChat";
type AiChatModalProps = {
    open: boolean,
    onClose?: () => void
}

const AiChatModal: React.FC<AiChatModalProps> = ({open, onClose}) => {

    const {start:doStart} = useSse("/api/v1/ai/chat");
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
        <Modal
            styles={{
                body: {
                    margin: "10px -24px -24px -24px",
                },
                footer: {display: "none"}
            }}
            title={
                <div
                    style={{
                        width: '100%',
                        cursor: 'move',
                    }}
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
                    AIFlowy 智能助理
                </div>
            }
            footer={<></>}
            open={open}
            onCancel={onClose}
            modalRender={(modal) => (
                <Draggable
                    disabled={disabled}
                    bounds={bounds}
                    nodeRef={draggleRef}
                    onStart={(event, uiData) => onStart(event, uiData)}
                >
                    <div ref={draggleRef} style={{width: "600px"}}>{modal}</div>
                </Draggable>
            )}
        >
            <AiProChat
                style={{border: "1px solid #f3f3f3", height: "680px",}}
                helloMessage="欢迎使用 AIFlowy ，我是你的智能助理，有什么问题可以随时问我。"
                request={async (messages) => {
                    const readableStream = new ReadableStream({
                        async start(controller) {

                            const encoder = new TextEncoder();
                            doStart({
                                data: {
                                    topicId: 1,
                                    prompt: messages[messages.length - 1].content as string,
                                },
                                onMessage: (message) => {
                                    controller.enqueue(encoder.encode(message))
                                },
                                onError: (error) => {
                                    controller.error(error);
                                },
                                onFinished: () => {
                                    controller.close();
                                }
                            })
                        },
                    });
                    return new Response(readableStream);
                }}
            />
        </Modal>
    );
};

export default AiChatModal;

import {
    Conversations, ConversationsProps,
} from '@ant-design/x';
import {createStyles} from 'antd-style';
import React, {useEffect, useRef, useState} from 'react';
import {
    DeleteOutlined,
    EditOutlined, ExclamationCircleFilled,
} from '@ant-design/icons';
import {Button, type GetProp, Modal, Input, message} from 'antd';
import {AiProChat, ChatMessage} from "../components/AiProChat/AiProChat.tsx";
import {getExternalSessionId, setNewExternalSessionId, updateExternalSessionId} from "../libs/getExternalSessionId.ts";
import { useParams } from "react-router-dom";
import {useGetManual, usePostManual} from "../hooks/useApis.ts";
import {uuid} from "../libs/uuid.ts";
import {PresetQuestion} from "./ai/botDesign/BotDesign.tsx";
import { processArray} from "../libs/parseAnswerUtil.tsx";
import {useSseWithEvent} from "../hooks/useSseWithEvent.ts";
import messageIcon from '../assets/message.png'
import "./externalBot.less"
const useStyle = createStyles(({token, css}) => {
    return {
        layout: css`
            width: 100%;
            min-width: 1000px;
            height: 100vh;
            border-radius: ${token.borderRadius}px;
            display: flex;
            background: ${token.colorBgContainer};
            font-family: AlibabaPuHuiTi, ${token.fontFamily}, sans-serif;

            .ant-prompts {
                color: ${token.colorText};
            }
        `,
        menu: css`
            background: ${token.colorBgLayout}80;
            width: 239px;
            min-width: 239px;
            display: flex;
            flex-direction: column;
        `,
        conversations: css`
            padding: 0 15px;
            flex: 1;
            overflow-y: auto;
        `,
        chat: css`
            height: 100%;
            width: 100%;
            margin: 0 auto;
            padding: 0 213px 67px 213px;
            box-sizing: border-box;
            display: flex;
            flex-direction: column;
            gap: 16px;
        `,
        messages: css`
            flex: 1;
        `,
        placeholder: css`
            padding-top: 32px;
        `,
        sender: css`
            box-shadow: ${token.boxShadow};
        `,
        logo: css`
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 27px 24px 27px 24px;
            box-sizing: border-box;

            img {
                width: 45px;
                height: 40px;
                display: inline-block;
            }

            span {
                display: inline-block;
                margin: 0 8px;
                font-weight: bold;
                color: ${token.colorText};
                font-size: 16px;
            }
        `,
        addBtn: css`
            width: calc(100% - 24px);
            margin: 0 12px 24px 12px;
            height: 40px;
            background: rgba(0,102,255,0.06);
            border-radius: 8px;
            border: 1px solid #0066FF;
        `,
    };
});

export const ExternalBot: React.FC = () => {

    const params = useParams();
    var tempUserId = localStorage.getItem("tempUserId");
    if (!tempUserId) {
        localStorage.setItem("tempUserId", uuid().toString() + params.id)
        console.log('重新设置了')
    }

    const urlParams = new URLSearchParams(location.search);
    const [isExternalIFrame, setIsExternalIFrame] = useState<boolean>(false);
    const isExternalIFrameRef = useRef(isExternalIFrame);
    const {doGet: doGetCreateToken} = useGetManual('/api/temp-token/create')
    const [presetQuestions, setPresetOptions] = useState<PresetQuestion[]>([]);
    const [helloMessage, setHelloMessage] = useState<string>('');
    useEffect(() => {
        isExternalIFrameRef.current = isExternalIFrame;
    }, [isExternalIFrame]);
    useEffect(() => {
        const isFrame = urlParams.get('isIframe');
        const token = urlParams.get('authKey');
        if (isFrame) {
            const newValue = true;
            setIsExternalIFrame(newValue);
            isExternalIFrameRef.current = newValue; // 手动同步 ref
            doGetCreateToken().then((res: any) => {
                if (res.data.errorCode === 0) {
                    localStorage.setItem('authKey', res.data.data);
                    const link = document.querySelector("link[rel*='icon']") as HTMLLinkElement;
                    doGetBotInfo({params: {id: params?.id}}).then((r: any) => {
                        if (link) {
                            link.href = r?.data?.data?.icon || '/favicon.png';
                        }
                        document.title = r?.data?.data?.title;
                        setPresetOptions(r?.data?.data?.options?.presetQuestions)
                        setHelloMessage(r?.data?.data?.options?.welcomeMessage)

                    });
                }
            });
        } else if (token) {
            localStorage.setItem('authKey', token);
        }


    }, []); // 空依赖数组表示只在组件挂载时执行一次
    const [newTitle, setNewTitle] = useState<string>('');

    // ==================== Style ====================
    const {styles} = useStyle();

    // ==================== State ====================
    const [conversationsItems, setConversationsItems] = React.useState<{ key: string; label: string }[]>([]);
    const [activeKey, setActiveKey] = React.useState('');
    const [open, setOpen] = useState(false);
    const { doGet: doGetBotInfo, result: botInfo} =useGetManual("/api/v1/aiBot/detail")
    const { start: startChat } = useSseWithEvent("/api/v1/aiBot/chat");
    // 查询会话列表的数据
    const {doGet: getConversationManualGet} = useGetManual('/api/v1/conversation/externalList');
    const {doGet: doGetMessageList} = useGetManual("/api/v1/aiBotMessage/messageList");
    const {doGet: doGetConverManualDelete} = useGetManual("/api/v1/conversation/deleteConversation");
    const {doGet: doGetConverManualUpdate} = useGetManual("/api/v1/conversation/updateConversation");
    const {doPost: doClearMessage} = usePostManual("/api/v1/conversation/clearMessage")

    const menuConfig: ConversationsProps['menu'] = () => ({
        items: [
            {
                label: '重命名',
                key: 'update',
                icon: <EditOutlined/>,
            },
            {
                label: '删除',
                key: 'delete',
                icon: <DeleteOutlined/>,
                danger: true,
            },
        ],
        onClick: (menuInfo) => {
            if (menuInfo.key === 'delete') {
                Modal.confirm({
                    title: '删除对话',
                    icon: <ExclamationCircleFilled/>,
                    content: '删除后，该对话将不可恢复。确认删除吗？',
                    onOk() {
                        doGetConverManualDelete({
                            params: {
                                sessionId: getExternalSessionId(),
                                botId: params?.id,
                                tempUserId: localStorage.getItem("tempUserId")
                            },
                        }).then(async (res: any) => {
                            if (res.data.errorCode === 0) {
                                message.success('删除成功');
                                setChats([])
                                const resp = await getConversationManualGet({
                                    params: {
                                        "botId": params?.id,
                                        "tempUserId": localStorage.getItem("tempUserId")
                                    }
                                })
                                setConversationsItems(getConversations(resp?.data?.data?.cons))
                            }
                        });
                    },
                    onCancel() {
                    },
                });


            } else if (menuInfo.key === 'update') {
                showModal()
            }
        },
    });


    const [chats, setChats] = useState<ChatMessage[]>([]);

    const getConversations = (options: { sessionId: any; title: any }[]): { key: any; label: any }[] => {
        if (options) {
            return options.map((item) => ({
                key: item.sessionId,
                label: item.title,
            }));
        }
        return [];
    };
    useEffect(() => {
        // if (isExternalIFrameRef.current) {
        //     return;
        // }
        if (chats.length === 2 && chats[1].content.length < 1) {
            getConversationManualGet({
                params: {"botId": params?.id, "tempUserId": localStorage.getItem("tempUserId")}
            }).then((r: any) => {
                setConversationsItems(getConversations(r?.data?.data?.cons));
            });
        }
    }, [chats])


    useEffect(() => {
        if (isExternalIFrameRef.current) {
            return;
        }
        const link = document.querySelector("link[rel*='icon']") as HTMLLinkElement;
        doGetBotInfo({params: {id: params?.id}}).then((r: any) => {

            if (link) {
                link.href = r?.data?.data?.icon || '/favicon.png';
            }
            document.title = r?.data?.data?.title;
            setPresetOptions(r?.data?.data?.options?.presetQuestions)
            setHelloMessage(r?.data?.data?.options?.welcomeMessage)
        });

        updateExternalSessionId(uuid())
        getConversationManualGet(
            {
                params: {"botId": params?.id, "tempUserId": localStorage.getItem("tempUserId")}
            }
        ).then((r: any) => {
            setActiveKey(getExternalSessionId());
            setConversationsItems(getConversations(r?.data?.data?.cons));
        });
    }, [])

    const onAddConversation = () => {
        setNewExternalSessionId();
        setActiveKey(getExternalSessionId());
        setChats([])
    };

    const onConversationClick: GetProp<typeof Conversations, 'onActiveChange'> = (key) => {
        setActiveKey(key);
        updateExternalSessionId(key);
        doGetMessageList({
            params: {
                sessionId: key,
                botId: params?.id,
                // 是externalBot页面提交的消息记录
                isExternalMsg: 1,
                tempUserId: localStorage.getItem("tempUserId")
            },
        }).then((resp: any) => {

            if (resp.data.errorCode === 0) {

                const messageList = resp.data.data;
                const processedItems = processArray(messageList);

                setChats(processedItems);
            }

        });

    };

    const logoNode = (

        <div className={styles.logo}>
            <img
                src={botInfo?.data?.icon || "/favicon.png"}
                style={{width: 32, height: 32, borderRadius: '50%'}}
                draggable={false}
                alt="logo"
            />
            <span>{botInfo?.data?.title}</span>
        </div>
    );

    // 更新会话标题的辅助函数
    const updateConversationTitle = (sessionId: string, newTitle: string) => {
        setConversationsItems((prevItems) =>
            prevItems.map((item) =>
                item.key === sessionId ? {...item, label: newTitle} : item
            )
        );
    };
    const showModal = () => {
        setOpen(true);

    };
    const updateTitle = () => {
        doGetConverManualUpdate({
            params: {
                sessionId: activeKey,
                botId: params?.id,
                title: newTitle,
                tempUserId: localStorage.getItem("tempUserId")
            },
        }).then((res: any) => {
            if (res.data.errorCode === 0) {
                // 更新本地状态
                updateConversationTitle(activeKey, newTitle)

                message.success('更新成功');
                setOpen(false);

            }
        });
    };
    const hideModal = () => {
        setOpen(false);
    };


    const [inputDisabled, setInputDisabled] = useState<boolean>(false);

    const clearMessage = async (botId: any, sessionId: any, tempUserId: any) => {
        setInputDisabled(true);
        await doClearMessage({
            data: {
                botId,
                sessionId,
                tempUserId,
            }
        });

        const resp = await doGetMessageList({
            params: {
                sessionId,
                botId,
                // 是externalBot页面提交的消息记录
                isExternalMsg: 1,
                tempUserId
            },
        })
        setChats(resp?.data.data);
        setInputDisabled(false);

    }
    // ==================== Render ====================
    return (
        <div className={styles.layout}>
            <Modal
                title="修改会话名称"
                open={open}
                onOk={updateTitle}
                onCancel={hideModal}
                okText="确认"
                cancelText="取消"
            >
                <Input placeholder="请输入新的会话标题"
                       defaultValue={newTitle}
                       onChange={(e) => {
                           setNewTitle(e.target.value)
                       }}
                />
            </Modal>
            <div className={styles.menu}>
                {/* 🌟 Logo */}
                {logoNode}
                {/* 🌟 添加会话 */}
                <div className={"external-bot-button"}>
                    <Button
                        onClick={onAddConversation}
                        type="link"
                        className={styles.addBtn}
                        icon={<img src={messageIcon} style={{width: 20, height: 20}} alt=""/>}
                    >
                        新建会话
                    </Button>
                </div>

                <div style={{padding: '0px 24px 8px 24px'}} className={"bot-chat-history"}>历史记录</div>

                {/* 🌟 会话管理 */}
                <div className={"bot-conversation"}>
                    {conversationsItems && (
                        <Conversations
                            items={conversationsItems}
                            className={styles.conversations}
                            activeKey={activeKey}
                            menu={menuConfig}
                            onActiveChange={onConversationClick}
                        />
                    )}
                </div>

            </div>
            <div className={styles.chat}>
                <AiProChat
                    chats={chats}
                    onChatsChange={setChats} // 确保正确传递 onChatsChange
                    helloMessage={helloMessage}
                    llmDetail={botInfo?.data}
                    botAvatar={botInfo?.data?.icon}
                    clearMessage={() => clearMessage(params.id, getExternalSessionId(), localStorage.getItem("tempUserId"))}
                    inputDisabled={inputDisabled}
                    prompts={presetQuestions}
                    options={{
                        botTitle: botInfo?.data?.title,
                        botDescription: botInfo?.data?.description
                    }}
                    // setNewConversation={onAddConversation}
                    onCustomEvent={(eventType) => {
                        console.log("收到收到事件：",eventType)
                        if (eventType === "refreshSession") {
                            console.log(111)
                            getConversationManualGet(
                                {
                                    params: {"botId": params?.id, "tempUserId": localStorage.getItem("tempUserId")}
                                }
                            ).then((r: any) => {
                                setConversationsItems(getConversations(r?.data?.data?.cons));
                            });
                            return {handled:true};
                        }
                        return {handled: false};
                    }}
                    onCustomEventComplete={(eventType) => {
                        if (eventType === "refreshSession") {
                            return {handled:true};
                        }
                        return {handled: false};
                    }}
                    request={async (messages) => {
                        const readableStream = new ReadableStream({
                            async start(controller) {
                                const encoder = new TextEncoder();
                                startChat({
                                    data: {
                                        botId: params.id,
                                        sessionId: getExternalSessionId(),
                                        prompt: messages[messages.length - 1].content as string,
                                        fileList:messages[messages.length - 1].files as Array<string>,
                                        isExternalMsg: 1,
                                        tempUserId: localStorage.getItem("tempUserId")
                                    },
                                    onMessage: (msg) => {
                                        controller.enqueue(encoder.encode(msg));
                                    },
                                    onFinished: () => {
                                        controller.close();
                                    },
                                })
                            },
                        });
                        return new Response(readableStream);
                    }}
                />
            </div>
        </div>
    );
};
export default {
    path: "/ai/externalBot/:id",
    element: ExternalBot,
    frontEnable: true,
};

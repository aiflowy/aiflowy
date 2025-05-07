import {
    Conversations, ConversationsProps,
} from '@ant-design/x';
import { createStyles } from 'antd-style';
import React, { useEffect, useState } from 'react';
import {
    DeleteOutlined,
    EditOutlined, ExclamationCircleFilled,
    PlusOutlined,
} from '@ant-design/icons';
import {Button, type GetProp, Modal, Input, message} from 'antd';
import { AiProChat, ChatMessage } from "../components/AiProChat/AiProChat.tsx";
import {getExternalSessionId, setNewExternalSessionId, updateExternalSessionId} from "../libs/getExternalSessionId.ts";
import { useSse } from "../hooks/useSse.ts";
import { useParams } from "react-router-dom";
import { useGetManual } from "../hooks/useApis.ts";
import {uuid} from "../libs/uuid.ts";

const useStyle = createStyles(({ token, css }) => {
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
            width: 300px;
            min-width: 300px;
            height: 100%;
            display: flex;
            flex-direction: column;
        `,
        conversations: css`
            padding: 0 12px;
            flex: 1;
            overflow-y: auto;
        `,
        chat: css`
            height: 100%;
            width: 100%;
            margin: 0 auto;
            box-sizing: border-box;
            display: flex;
            flex-direction: column;
            padding: ${token.paddingLG}px;
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
            height: 72px;
            align-items: center;
            justify-content: start;
            padding: 0 24px;
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
            background: #1677ff0f;
            border: 1px solid #1677ff34;
            width: calc(100% - 24px);
            margin: 0 12px 24px 12px;
        `,
    };
});

export const ExternalBot: React.FC = () => {
    const [newTitle, setNewTitle] = useState<string>('');

    // ==================== Style ====================
    const { styles } = useStyle();

    // ==================== State ====================
    const [conversationsItems, setConversationsItems] = React.useState<{ key: string; label: string }[]>([]);
    const [activeKey, setActiveKey] = React.useState('');
    const [open, setOpen] = useState(false);
    const params = useParams();
    const { doGet: doGetBotInfo, result: botInfo} =useGetManual("/api/v1/aiBot/detail")
    const { start: startChat } = useSse("/api/v1/aiBot/chat");
    // 查询会话列表的数据
    const { doGet: getConversationManualGet } = useGetManual('/api/v1/conversation/externalList');
    const { doGet: doGetManual } = useGetManual("/api/v1/aiBotMessage/messageList");
    const { doGet: doGetConverManualDelete } = useGetManual("/api/v1/conversation/deleteConversation");
    const { doGet: doGetConverManualUpdate } = useGetManual("/api/v1/conversation/updateConversation");
    const menuConfig: ConversationsProps['menu'] = () => ({
        items: [
            {
                label: '重命名',
                key: 'update',
                icon: <EditOutlined />,
            },
            {
                label: '删除',
                key: 'delete',
                icon: <DeleteOutlined />,
                danger: true,
            },
        ],
        onClick: (menuInfo) => {
            if (menuInfo.key === 'delete') {
                Modal.confirm({
                    title: '删除对话',
                    icon: <ExclamationCircleFilled />,
                    content: '删除后，该对话将不可恢复。确认删除吗？',
                    onOk() {
                        doGetConverManualDelete({
                            params: {
                                sessionId: getExternalSessionId(),
                                botId: params?.id,
                            },
                        }).then((res: any) => {
                            if (res.data.errorCode === 0){
                                message.success('删除成功');
                                setChats([])
                                getConversationManualGet()
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
        if (chats.length === 2 && chats[1].content.length < 1){
            getConversationManualGet({
                params:  { "botId": params?.id }
            }).then((r: any) => {
                setConversationsItems(getConversations(r?.data?.data?.cons));
            });
        }
    }, [chats]);

    useEffect(() => {
        const link = document.querySelector("link[rel*='icon']") as HTMLLinkElement;
        doGetBotInfo({params: {id: params?.id}}).then((r: any) => {
            if (link) {
                link.href = r?.data?.data?.icon || '/favicon.png';
            }
            document.title = r?.data?.data?.title;
        });

        updateExternalSessionId(uuid())
        getConversationManualGet(
            {
                params:  { "botId": params?.id }
            }
        ).then((r: any) => {
            setActiveKey(getExternalSessionId());
            setConversationsItems(getConversations(r?.data?.data?.cons));
        });
    }, []);

    const onAddConversation = () => {
        setNewExternalSessionId();
        // setConversationsItems(prev => [ { key: getExternalSessionId(), label: '新建会话' }, ...prev]);
        setActiveKey(getExternalSessionId());
        setChats([])
    };

    const onConversationClick: GetProp<typeof Conversations, 'onActiveChange'> = (key) => {
        setActiveKey(key);
        updateExternalSessionId(key);
        doGetManual({
            params: {
                sessionId: key,
                botId: params?.id,
                // 是externalBot页面提交的消息记录
                isExternalMsg: 1
            },
        }).then((r: any) => {
            setChats(r?.data.data);
        });

    };

    const logoNode = (

        <div className={styles.logo}>
            <img
                src={botInfo?.data?.icon || "/favicon.png"}
                style={{ width: 32, height: 32, borderRadius: '50%' }}
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
                item.key === sessionId ? { ...item, label: newTitle } : item
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
            },
        }).then((res: any) => {
            if (res.data.errorCode === 0){
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
                <Button
                    onClick={onAddConversation}
                    type="link"
                    className={styles.addBtn}
                    icon={<PlusOutlined />}
                >
                    新建会话
                </Button>
                {/* 🌟 会话管理 */}
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
            <div className={styles.chat}>
                <AiProChat
                    chats={chats}
                    onChatsChange={setChats} // 确保正确传递 onChatsChange
                    helloMessage="欢迎使用 AIFlowy ，我是你的专属机器人，有什么问题可以随时问我。"
                    botAvatar={botInfo?.data?.icon}
                    request={async (messages) => {
                        const readableStream = new ReadableStream({
                            async start(controller) {
                                const encoder = new TextEncoder();
                                startChat({
                                    data: {
                                        botId: params.id,
                                        sessionId: getExternalSessionId(),
                                        prompt: messages[messages.length - 1].content as string,
                                        isExternalMsg: 1
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
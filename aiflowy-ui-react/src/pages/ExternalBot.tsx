import {
    Conversations,
    useXAgent,
    useXChat,
} from '@ant-design/x';
import { createStyles } from 'antd-style';
import React, { useEffect, useState } from 'react';

import {
    DownOutlined,
    PlusOutlined,
} from '@ant-design/icons';
import { Button, Dropdown, type GetProp, MenuProps, Space } from 'antd';
import { AiProChat, ChatMessage } from "../components/AiProChat/AiProChat.tsx";
import { getSessionId } from "../libs/getSessionId.ts";
import { useSse } from "../hooks/useSse.ts";
import { useParams } from "react-router-dom";
import {useGet} from "../hooks/useApis.ts";

const defaultConversationsItems = [
    {
        key: '0',
        label: 'What is Ant Design X?',
    },
];

const useStyle = createStyles(({ token, css }) => {
    return {
        layout: css`
            width: 100%;
            min-width: 1000px;
            height: 722px;
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
            width: 350px;
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
            //max-width: 700px;
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
                width: 24px;
                height: 24px;
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


    const [largeModel, setLargeModel] = useState("通义千问");
    // ==================== Style ====================
    const { styles } = useStyle();

    // ==================== State ====================

    const [conversationsItems, setConversationsItems] = React.useState(defaultConversationsItems);

    const [activeKey, setActiveKey] = React.useState(defaultConversationsItems[0].key);
    const params = useParams();

    const { start: startChat } = useSse("/api/v1/aiBot/chat");

    const {result: llms} = useGet('/api/v1/aiLlm/list')
    const {result: conversationResult} = useGet('/api/v1/aiBotMessage/externalList',{"botId": params?.id})
    console.log('conversationResult',conversationResult)
    const getOptions = (options: { id: any; title: any }[]): { key: any; label: any }[] => {
        if (options) {
            return options.map((item) => ({
                key: item.id,
                label: item.title,
            }));
        }
        return [];
    };
    const modelItems: MenuProps['items'] = getOptions(llms?.data)
    const [chats, setChats] = useState<ChatMessage[]>([]);

    console.log('params',params)
    // ==================== Runtime ====================
    const [agent] = useXAgent({
        request: async ({ message }, { onSuccess }) => {
            onSuccess(`Mock success return. You said: ${message}`);
        },
    });

    const { onRequest, setMessages } = useXChat({
        agent,
    });

    useEffect(() => {
        if (activeKey !== undefined) {
            setMessages([]);
        }
    }, [activeKey]);

    const onAddConversation = () => {
        setConversationsItems([
            ...conversationsItems,
            {
                key: `${conversationsItems.length}`,
                label: `New Conversation ${conversationsItems.length}`,
            },
        ]);
        setActiveKey(`${conversationsItems.length}`);
    };

    const onConversationClick: GetProp<typeof Conversations, 'onActiveChange'> = (key) => {
        setActiveKey(key);
    };

    const logoNode = (
        <div className={styles.logo}>
            <img
                src="../../public/favicon.png"
                draggable={false}
                alt="logo"
            />
            <span>AIFlowy</span>
        </div>
    );

    // ==================== Render ====================
    return (
        <div className={styles.layout}>
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
                <Conversations
                    items={conversationsItems}
                    className={styles.conversations}
                    activeKey={activeKey}
                    onActiveChange={onConversationClick}
                />
            </div>
            <div className={styles.chat}>
                {/*<div>*/}
                {/*    <Dropdown*/}
                {/*        menu={{*/}
                {/*            items: modelItems,*/}
                {/*            onClick: (item) => {*/}
                {/*                console.log('item',item);*/}
                {/*                // 更新 largeModel 状态为选中的模型名称*/}
                {/*                // @ts-ignore*/}
                {/*                setLargeModel(item.domEvent.target.innerText);*/}
                {/*            },*/}
                {/*        }}*/}
                {/*    >*/}
                {/*        <a onClick={(e) => {*/}
                {/*            e.preventDefault();*/}
                {/*        }}>*/}
                {/*            <Space>*/}
                {/*                {largeModel} /!* 显示当前选中的模型名称 *!/*/}
                {/*                <DownOutlined />*/}
                {/*            </Space>*/}
                {/*        </a>*/}
                {/*    </Dropdown>*/}
                {/*</div>*/}
                <AiProChat
                    chats={chats}
                    onChatsChange={setChats} // 确保正确传递 onChatsChange
                    // style={{ height: '600px' }}
                    helloMessage="欢迎使用 AIFlowy ，我是你的专属机器人，有什么问题可以随时问我。"
                    request={async (messages) => {
                        const readableStream = new ReadableStream({
                            async start(controller) {
                                const encoder = new TextEncoder();
                                startChat({
                                    data: {
                                        botId: params.id,
                                        sessionId: getSessionId(),
                                        prompt: messages[messages.length - 1].content as string,
                                    },
                                    onMessage: (msg) => {
                                        controller.enqueue(encoder.encode(msg));
                                    },
                                    onFinished: () => {
                                        controller.close();
                                    }
                                });
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
import React, {useEffect, useState} from 'react';

import {useDetail, useList} from "../../../hooks/useApis.ts";
import {useParams} from "react-router-dom";
import {useSse} from "../../../hooks/useSse.ts";
import {getSessionId} from "../../../libs/getSessionId.ts";
import {AiProChat, ChatMessage} from "../../../components/AiProChat/AiProChat";

const BotChat: React.FC = () => {

    const params = useParams();
    const {result: detail} = useDetail("aiBot", params.id);

    const {start: startChat} = useSse("/api/v1/aiBot/chat");

    const [chats, setChats] = useState<ChatMessage[]>([]);

    const {result: messageResult} = useList("aiBotMessage", {
            botId: params.id,
            sessionId: getSessionId()
        }
    );

    useEffect(() => {
        setChats(messageResult?.data)
    }, [messageResult]);

    return (
        <div>
            <div style={{
                height: "42px",
                background: "#eee",
                display: "flex",
                alignItems: "center",
                justifyContent: "center"
            }}>{detail?.data?.title}</div>
            <div style={{height: "calc(100vh - 42px)", width: "100%"}}>
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
                                        controller.enqueue(encoder.encode(msg))
                                    },
                                    onFinished: () => {
                                        controller.close()
                                    }
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
    path: "/bot/chat/:id",
    element: BotChat,
    frontEnable: true,
};

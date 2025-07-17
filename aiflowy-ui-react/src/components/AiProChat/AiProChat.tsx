import React, {useEffect, useLayoutEffect, useMemo, useRef, useState} from 'react';
import {
    Attachments,
    AttachmentsProps,
    Bubble,
    Prompts,
    Sender,
    ThoughtChain,
    ThoughtChainItem,
} from '@ant-design/x';
import {Avatar, Button, GetProp, GetRef, Image, message, Space, Spin, Typography, UploadFile} from 'antd';
import {
    CopyOutlined,
    FolderAddOutlined,
    PauseCircleOutlined,
    PlayCircleOutlined,
    SyncOutlined,
} from '@ant-design/icons';

import logo from "/favicon.png";
import './aiprochat.less'
import markdownit from 'markdown-it';
import {usePost, usePostManual} from "../../hooks/useApis.ts";
import senderIcon from "../../assets/senderIcon.png"
import senderIconSelected from "../../assets/senderIconSelected.png"
import clearButtonIcon from "../../assets/clearButton.png"
import fileIcon from "../../assets/fileLink.png"
import uploadIfle from "../../assets/uploadIfle.png"
// const fooAvatar: React.CSSProperties = {
//     color: '#fff',
//     backgroundColor: '#87d068',
// };

export interface ChatOptions {
    messageSessionId?: string;
    botTitle?: string;
    botDescription?: string;
    fileList?:string[];
}

export type ChatMessage = {
    id: string;
    content: string;
    files?: Array<string>;
    role: 'user' | 'assistant' | 'aiLoading' | string;
    created: number;
    updateAt?: number;
    loading?: boolean;
    thoughtChains?: Array<ThoughtChainItem>
    options?: ChatOptions;
};


// 事件类型
export type EventType = 'thinking' | 'thought' | 'toolCalling' | 'callResult' | 'messageSessionId' | string;

export type EventHandlerResult = {
    handled: boolean; // 是否已处理该事件
    data?: any; // 处理结果数据
};

// 事件处理器函数类型
export type EventHandler = (eventType: EventType, eventData: any, context: {
    chats: ChatMessage[];
    setChats: (value: ((prevState: ChatMessage[]) => ChatMessage[]) | ChatMessage[]) => void;
}) => EventHandlerResult | Promise<EventHandlerResult>;


export type AiProChatProps = {
    loading?: boolean;
    chats?: ChatMessage[];
    onChatsChange?: (value: ((prevState: ChatMessage[]) => ChatMessage[]) | ChatMessage[]) => void;
    style?: React.CSSProperties;
    appStyle?: React.CSSProperties;
    helloMessage?: string;
    botAvatar?: string;
    request: (messages: ChatMessage[]) => Promise<Response>;
    clearMessage?: () => void;
    showQaButton?: boolean;
    onQaButtonClick?: (currentChat: ChatMessage, index: number, allChats: ChatMessage[]) => void;
    prompts?: GetProp<typeof Prompts, 'items'>;
    inputDisabled?: boolean;
    customToolBarr?: React.ReactNode;
    onCustomEvent?: EventHandler;
    onCustomEventComplete?: EventHandler;
    llmDetail?: any;
    sessionId?: string;
    options?: any;
};

export const RenderMarkdown: React.FC<{ content: string, fileList?: Array<string> }> = ({content, fileList}) => {

    const md = markdownit({html: true, breaks: true});
    return (

        <>
            <div style={{display: "flex", gap: "10px", marginBottom: "10px"}}>
                {fileList && fileList.length > 0 && fileList.map(file => {
                    return <Image width={164} height={164} style={{borderRadius: "8px"}} src={file} key={Date.now().toString()}></Image>
                })}
            </div>
            <Typography>
                <div dangerouslySetInnerHTML={{__html: md.render(content)}}/>
            </Typography>
        </>

    );
};


export const AiProChat = ({
                              loading,
                              chats: parentChats,
                              onChatsChange: parentOnChatsChange,
                              style = {},
                              appStyle = {},
                              // @ts-ignore
                              helloMessage = '欢迎使用 AIFlowy',
                              // @ts-ignore
                              botAvatar = `${logo}`,
                              request,
                              showQaButton = false,
                              onQaButtonClick = (): void => {
                              },
                              clearMessage,
                              inputDisabled = false,
                              prompts,
                              customToolBarr,
                              onCustomEvent,
                              onCustomEventComplete,
                              llmDetail = {},
                              sessionId,
                              options
                          }: AiProChatProps) => {
    const isControlled = parentChats !== undefined && parentOnChatsChange !== undefined;
    const [internalChats, setInternalChats] = useState<ChatMessage[]>([]);
    const chats = useMemo(() => {
        return isControlled ? parentChats : internalChats;
    }, [isControlled, parentChats, internalChats]);
    const setChats = isControlled ? parentOnChatsChange : setInternalChats;
    const [content, setContent] = useState('');
    const [sendLoading, setSendLoading] = useState(false);
    const [isStreaming, setIsStreaming] = useState(false);
    const messagesContainerRef = useRef<HTMLDivElement>(null);
    const messagesEndRef = useRef<HTMLDivElement>(null);
    // 控制是否允许自动滚动
    const autoScrollEnabled = useRef(true); // 默认允许自动滚动
    const isUserScrolledUp = useRef(false); // 用户是否向上滚动过

    //  使用 ref 来跟踪事件状态，避免异步状态更新问题
    const currentEventType = useRef<string | null>(null);
    const eventContent = useRef<string>(''); // 当前事件累积的内容


    // 设置每次播放尝试合并的音频片段数量
    const CHUNK_SIZE = 20;
    // 存储所有 sessionId 对应的 base64 音频片段队列
    const voiceMapRef = useRef<Map<string, string[]>>(new Map());
    // 当前正在播放的 sessionId，用于多会话控制
    const currentSessionIdRef = useRef<string | null>(null);
    const [playingSessionId, setPlayingSessionId] = useState<string | null>()
    // 当前是否处于播放状态
    const isPlayingRef = useRef<boolean>(false);
    // 音频上下文 AudioContext 实例
    const audioPlayContextRef = useRef<AudioContext | null>(null);
    // 当前正在播放的音频源（用于手动停止）
    const currentAudioSourceRef = useRef<AudioBufferSourceNode | null>(null);

    const {loading: findVoiceLoading, doPost: findVoice} = usePostManual("/api/v1/aiBot/findVoice");

    // 播放指定 sessionId 的音频片段队列
    const playAudioQueue = async (sessionId: string) => {
        const voiceMap = voiceMapRef.current;
        const queue = voiceMap.get(sessionId);

        if (!queue || queue.length === 0) {
            console.warn(`Session ${sessionId} 的音频队列为空`);
            return;
        }

        // 创建或复用 AudioContext
        const audioContext = audioPlayContextRef.current ?? new AudioContext();
        audioPlayContextRef.current = audioContext;

        // 设置播放状态
        currentSessionIdRef.current = sessionId;
        isPlayingRef.current = true;
        setPlayingSessionId(sessionId);

        let playIndex = 0;

        try {
            while (playIndex < queue.length) {
                // 检查是否被中断
                if (currentSessionIdRef.current !== sessionId) {
                    console.log(`播放被中断，sessionId: ${sessionId}`);
                    return;
                }

                let chunkCount = CHUNK_SIZE;
                let audioBuffer: AudioBuffer | null = null;

                // 动态尝试从当前位置起合并 chunkCount 个片段进行解码
                while (playIndex + chunkCount <= queue.length) {
                    const base64List = queue.slice(playIndex, playIndex + chunkCount);
                    const mergedBuffer = mergeBase64Buffers(base64List);
                    try {
                        audioBuffer = await audioContext.decodeAudioData(mergedBuffer);
                        break;
                    } catch (e) {
                        chunkCount += 1;
                    }
                }

                // 如果解码失败且不足 CHUNK_SIZE，则尝试将剩余部分拼接解码
                if (!audioBuffer && playIndex < queue.length) {
                    const base64List = queue.slice(playIndex);
                    const mergedBuffer = mergeBase64Buffers(base64List);
                    try {
                        audioBuffer = await audioContext.decodeAudioData(mergedBuffer);
                        chunkCount = queue.length - playIndex;
                    } catch (e) {
                        console.warn("解码失败，跳过剩余段", e);
                        break;
                    }
                }

                if (!audioBuffer) break;

                try {
                    await playAudioBuffer(audioBuffer, audioContext);
                    playIndex += chunkCount;
                } catch (e) {
                    console.warn(`播放失败，跳过 index=${playIndex}`, e);
                    playIndex += chunkCount;
                }
            }
        } finally {
            // 确保播放完成后重置状态
            if (currentSessionIdRef.current === sessionId) {
                stopCurrentPlayback();
            }
        }
    };

    const isSessionPlaying = (sessionId: string): boolean => {
        return isPlayingRef.current && currentSessionIdRef.current === sessionId;
    };

    // 将多个 base64 音频片段合并为一个 ArrayBuffer，供 AudioContext 解码
    const mergeBase64Buffers = (base64List: string[]): ArrayBuffer => {
        const binaryList = base64List.map(b64 => atob(b64)); // 解码为二进制字符串
        const totalLength = binaryList.reduce((sum, bin) => sum + bin.length, 0);
        const result = new Uint8Array(totalLength);

        let offset = 0;
        for (const bin of binaryList) {
            for (let i = 0; i < bin.length; i++) {
                result[offset++] = bin.charCodeAt(i); // 字符转字节
            }
        }

        return result.buffer;
    };

    // 播放解码后的 AudioBuffer 音频数据
    const playAudioBuffer = (buffer: AudioBuffer, audioContext: AudioContext): Promise<void> => {
        return new Promise((resolve) => {
            const source = audioContext.createBufferSource();
            source.buffer = buffer;
            source.connect(audioContext.destination);
            source.onended = () => resolve(); // 播放结束后自动 resolve
            source.start();
            currentAudioSourceRef.current = source; // 用于后续停止
        });
    };

    // 手动停止当前播放任务
    const stopCurrentPlayback = () => {
        const source = currentAudioSourceRef.current;
        if (source) {
            try {
                source.stop();
            } catch (e) {
                console.warn("停止播放出错", e);
            }
            currentAudioSourceRef.current = null;
        }

        currentSessionIdRef.current = null;
        isPlayingRef.current = false;
        setPlayingSessionId(null);
    };

    useEffect(() => {
        const webSocket = new WebSocket(`${import.meta.env.VITE_APP_WS_SERVER_ENDPOINT}/api/v1/aiBot/ws/chat?sessionId=${sessionId}`);

        webSocket.onopen = () => {
            console.log("WebSocket 连接建立");
        };

        webSocket.onerror = (event: Event) => {
            console.error("WebSocket 连接错误:", event);
        };

        webSocket.onmessage = (event: MessageEvent) => {
            const voiceData: { data: string; messageSessionId: string } = JSON.parse(event.data);
            const voiceMap = voiceMapRef.current;

            // 若该 sessionId 尚未初始化队列，则创建空数组
            if (!voiceMap.has(voiceData.messageSessionId)) {
                voiceMap.set(voiceData.messageSessionId, []);
            }

            // 添加新音频片段到该 session 的队列末尾
            voiceMap.get(voiceData.messageSessionId)!.push(voiceData.data);

            // 若当前未播放或 session 发生切换，启动播放流程
            const shouldStart = !isPlayingRef.current || currentSessionIdRef.current !== voiceData.messageSessionId;

            if (shouldStart) {
                stopCurrentPlayback();

                playAudioQueue(voiceData.messageSessionId);

            }
        };

        const isDev = import.meta.env.DEV;

        if (!isDev) {
            return () => {
                if (webSocket) {
                    webSocket.close(1000, "正常关闭");
                }
            };
        }
    }, [sessionId]);


    useRef<string | null>(null);
    // 滚动到底部逻辑
    const scrollToBottom = () => {
        const container = messagesContainerRef.current;
        if (container && autoScrollEnabled.current) {
            container.scrollTop = container.scrollHeight;
        }
    };

    // 组件挂载时滚动
    useLayoutEffect(() => {
        scrollToBottom();
    }, []);


    // 消息更新时滚动
    useLayoutEffect(() => {
        if (autoScrollEnabled.current) {
            scrollToBottom();
        }
    }, [chats]);
    useLayoutEffect(() => {
        const container = messagesContainerRef.current;
        if (!container) return;

        const handleScroll = () => {
            const {scrollTop, scrollHeight, clientHeight} = container;
            const atBottom = scrollHeight - scrollTop <= clientHeight + 5; // 允许误差 5px

            if (atBottom) {
                // 用户回到底部，恢复自动滚动
                autoScrollEnabled.current = true;
                isUserScrolledUp.current = false;
            } else {
                // 用户向上滚动，禁用自动滚动
                autoScrollEnabled.current = false;
                isUserScrolledUp.current = true;
            }
        };

        container.addEventListener('scroll', handleScroll);
        return () => {
            container.removeEventListener('scroll', handleScroll);
        };
    }, []);


    // 处理事件进度（事件进行中）
    const handleEventProgress = async (eventType: EventType, eventData: any): Promise<boolean> => {
        if (onCustomEvent) {
            try {
                const result = await onCustomEvent(eventType, eventData, {
                    chats,
                    setChats,
                });

                if (result.handled) {
                    return true;
                }
            } catch (error) {
                console.error(`Custom event progress handler error for "${eventType}":`, error);
            }
        }


        // 使用现有的默认处理逻辑
        return handleDefaultEvent(eventType, eventData);
    };

    // 处理事件完成
    const handleEventComplete = async (eventType: EventType, finalContent: string): Promise<boolean> => {

        const eventData = {
            content: finalContent,
            accumulatedContent: finalContent,
            isComplete: true
        };


        if (onCustomEventComplete) {
            try {
                const result = await onCustomEventComplete(eventType, eventData, {
                    chats,
                    setChats
                });

                if (result.handled) {
                    return true;
                }
            } catch (error) {
                console.error(`Custom event complete handler error for "${eventType}":`, error);
            }
        }


        // 使用现有的默认处理逻辑
        return handleDefaultEvent(eventType, eventData);
    };


    const handleDefaultEvent = (eventType: EventType, eventData: any): boolean => {

        if (eventData.isComplete || eventType === "content") {
            return true;
        }

        // 🧠 处理 ThoughtChain 相关事件
        if (['thinking', 'thought', 'toolCalling', 'callResult'].includes(eventType)) {

            setChats((prevChats: ChatMessage[]) => {
                const newChats = [...prevChats];

                const lastAiIndex = (() => {
                    for (let i = newChats.length - 1; i >= 0; i--) {
                        if (newChats[i].role === 'assistant') {
                            return i;
                        }
                    }
                    return -1;
                })();

                const aiMessage = newChats[lastAiIndex];
                aiMessage.loading = false;

                return newChats;
            });

            setChats((prevChats: ChatMessage[]) => {
                const newChats = [...prevChats];

                // 找到最后一条 assistant 消息
                const lastAiIndex = (() => {
                    for (let i = newChats.length - 1; i >= 0; i--) {
                        if (newChats[i].role === 'assistant') {
                            return i;
                        }
                    }
                    return -1;
                })();

                if (lastAiIndex !== -1) {
                    const aiMessage = newChats[lastAiIndex];

                    // 初始化 thoughtChains 数组（如果不存在）
                    if (!aiMessage.thoughtChains) {
                        aiMessage.thoughtChains = [];
                    }

                    const title = eventData.metadataMap.chainTitle;
                    const description = (eventData.accumulatedContent || eventData.content || '') as string;

                    // 获取事件ID
                    const eventId = eventData.id || eventData.metadataMap?.id;

                    if (eventId) {
                        // 查找是否存在相同 id 的思维链项
                        const targetIndex = aiMessage.thoughtChains.findIndex(item =>
                            item.key === eventId || item.key === String(eventId)
                        );

                        if (targetIndex !== -1) {
                            // 找到相同 id 的项，更新该项
                            aiMessage.thoughtChains[targetIndex] = {
                                ...aiMessage.thoughtChains[targetIndex],
                                key: eventId,
                                title,
                                content: <RenderMarkdown content={description}/>,
                                status: 'pending'
                            };
                        } else {
                            // 没找到相同 id 的项，创建新项
                            const newItem: ThoughtChainItem = {
                                key: eventId,
                                title,
                                content: <RenderMarkdown content={description}/>,
                                status: 'pending'
                            };

                            aiMessage.thoughtChains.push(newItem);
                        }


                    } else {
                        console.warn(`Event ${eventType} has no id, skipping ThoughtChain processing`);
                    }

                    // 更新消息的更新时间
                    aiMessage.updateAt = Date.now();
                }


                return newChats;
            });

            return true;
        }

        return true;
    };

    // 提交流程优化

    // 提交流程优化
    const handleSubmit = async (newMessage: string) => {

        const messageContent = newMessage?.trim() || content.trim();


        setSendLoading(true);
        setIsStreaming(true);

        const files = fileUrlList.map(file => file.url);

        const userMessage: ChatMessage = {
            role: 'user',
            id: Date.now().toString(),
            files: files,
            content: messageContent,
            created: Date.now(),
            updateAt: Date.now(),
        };

        const aiMessage: ChatMessage = {
            role: 'assistant',
            id: Date.now().toString(),
            content: '',
            loading: true,
            created: Date.now(),
            updateAt: Date.now(),
        };

        const temp = [userMessage, aiMessage];


        setChats?.((prev: ChatMessage[]) => [...(prev || []), ...temp]);
        setTimeout(scrollToBottom, 50);
        setContent('');
        setFileItems([]);
        setFileUrlList([]);
        setHeaderOpen(false)

        try {
            const response = await request([...(chats || []), userMessage]);
            if (!response?.body) return;

            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let partial = '';
            let currentContent = '';
            let typingIntervalId: NodeJS.Timeout | null = null;

            // 用于等待打字效果完成的Promise
            const waitForTypingComplete = (): Promise<void> => {
                return new Promise((resolve) => {
                    const checkTypingComplete = () => {
                        if (currentContent === partial) {
                            resolve();
                        } else {
                            setTimeout(checkTypingComplete, 50);
                        }
                    };
                    checkTypingComplete();
                });
            };

            let isStreamFinished = false;
            let shouldContinueReading = true;
            //  重置事件状态
            currentEventType.current = null;
            eventContent.current = '';

            while (shouldContinueReading) {
                const {done, value} = await reader.read();
                if (done) {
                    isStreamFinished = true;
                    shouldContinueReading = false;
                    //  流结束时，如果还有未完成的事件，触发事件完成处理
                    if (currentEventType.current) {
                        await handleEventComplete(currentEventType.current, eventContent.current);
                        currentEventType.current = null;
                        eventContent.current = '';
                    }
                    break;
                }

                const decode = decoder.decode(value, {stream: true});
                const parse = JSON.parse(decode);
                const respData = JSON.parse(parse.data);

                // 🔍 调试：打印收到的数据
                // console.log('📥 收到数据:', {
                //     event: parse.event,
                //     content: respData.content,
                //     contentLength: (respData.content || '').length
                // });

                const incomingEventType = parse.event || 'content';

                // 检查是否切换到了新的事件类型（使用 ref.current）
                if (currentEventType.current && currentEventType.current !== incomingEventType) {

                    try {
                        // 上一个事件完成，触发完成处理
                        await handleEventComplete(currentEventType.current, eventContent.current);
                    } catch (error) {
                        console.error(` Event transition failed:`, error);
                    }

                    // 重置累积内容
                    eventContent.current = '';
                }

                //  更新当前事件类型
                currentEventType.current = incomingEventType;

                if (incomingEventType !== 'content') {
                    // 累积事件内容
                    const newEventContent = eventContent.current + (respData.content || '');
                    eventContent.current = newEventContent;

                    try {
                        //  事件处理失败时直接抛出错误
                        const eventHandled = await handleEventProgress(incomingEventType, {
                            ...respData,
                            accumulatedContent: newEventContent,
                            isComplete: false
                        });

                        // 如果事件已被处理，跳过内容更新逻辑
                        if (eventHandled) {
                            continue;
                        }
                    } catch (error) {
                        console.error(`Event processing failed, terminating stream:`, error);
                    }
                }

                // 处理内容更新
                const newContent = respData.content || '';
                if (newContent && !partial.endsWith(newContent)) {
                    partial += newContent;
                } else if (newContent && partial.endsWith(newContent)) {
                    console.warn('🚨 检测到重复内容，跳过累积:', newContent);
                }

                // console.log('📚 累积内容:', {
                //     partialLength: partial.length,
                //     partialContent: partial.substring(Math.max(0, partial.length - 50))
                // });

                // 清除之前的打字间隔
                if (typingIntervalId) {
                    clearInterval(typingIntervalId);
                }


                // 开始新的打字效果
                typingIntervalId = setInterval(() => {
                    if (currentContent.length < partial.length) {
                        currentContent = isStreamFinished ? partial : partial.slice(0, currentContent.length + 2);
                        setChats?.((prev: ChatMessage[]) => {
                            const newChats = [...(prev || [])];
                            const lastMsg = newChats[newChats.length - 1];
                            if (!lastMsg) return prev;

                            if (lastMsg?.role === 'assistant') {
                                lastMsg.loading = false;
                                lastMsg.content = currentContent;

                                if (!lastMsg.options?.messageSessionId && respData.metadataMap && respData.metadataMap.messageSessionId) {
                                    lastMsg.options = {messageSessionId: respData.metadataMap.messageSessionId};
                                }

                                lastMsg.updateAt = Date.now();
                            }


                            return newChats;
                        });

                        if (autoScrollEnabled.current) {
                            scrollToBottom();
                        }
                    }

                    // 当前内容已经追上完整内容时停止
                    if (currentContent == partial || isStreamFinished) {
                        clearInterval(typingIntervalId!);
                        typingIntervalId = null;
                    }
                }, 50);
            }

            // 等待最后的打字效果完成
            await waitForTypingComplete();

            // 清理间隔（如果还存在）
            if (typingIntervalId) {
                clearInterval(typingIntervalId);
            }

            setChats((prev: ChatMessage[]) => {
                const newChats = [...prev]; // 创建新数组而不是直接引用
                if (newChats.length > 0) {
                    const lastMessage = newChats[newChats.length - 1];
                    if (lastMessage && lastMessage.role === 'assistant') {
                        // 正确地移除 "Final Answer:" 前缀
                        lastMessage.content = lastMessage.content.replace(/^Final Answer:\s*/i, "");
                    }
                }
                return newChats;
            })

        } catch (error) {
            console.error(`Stream processing error:`, error);
        } finally {
            // 确保打字效果完成后再重置状态
            setIsStreaming(false);
            setSendLoading(false);
        }
    };

    // 重新生成消息
    const handleRegenerate = async (index: number) => {
        // 找到当前 assistant 消息对应的上一条用户消息
        const prevMessage: ChatMessage = {
            role: 'user',
            id: Date.now().toString(),
            content: chats[index - 1].content,
            files: chats[index - 1].files,
            loading: false,
            created: Date.now(),
            updateAt: Date.now(),
        };
        setContent(prevMessage.content)
        const aiMessage: ChatMessage = {
            role: 'assistant',
            id: Date.now().toString(),
            content: '',
            loading: true,
            created: Date.now(),
            updateAt: Date.now(),
        };
        setSendLoading(true);
        setIsStreaming(true);
        const temp = [prevMessage, aiMessage];
        setChats?.((prev: ChatMessage[]) => [...(prev || []), ...temp]);
        setTimeout(scrollToBottom, 50);
        setContent('');

        try {
            const response = await request([...(chats || []), prevMessage]);
            if (!response?.body) return;

            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let partial = '';
            let currentContent = '';
            let typingIntervalId: NodeJS.Timeout | null = null;

            // 用于等待打字效果完成的Promise
            const waitForTypingComplete = (): Promise<void> => {
                return new Promise((resolve) => {
                    const checkTypingComplete = () => {
                        if (currentContent === partial) {
                            resolve();
                        } else {
                            setTimeout(checkTypingComplete, 50);
                        }
                    };
                    checkTypingComplete();
                });
            };

            let isStreamFinished = false;
            let shouldContinueReading = true;

            //  重置事件状态
            currentEventType.current = null;
            eventContent.current = '';

            while (shouldContinueReading) {
                const {done, value} = await reader.read();
                if (done) {
                    isStreamFinished = true;
                    shouldContinueReading = false;

                    //  流结束时，如果还有未完成的事件，触发事件完成处理
                    if (currentEventType.current) {
                        await handleEventComplete(currentEventType.current, eventContent.current);
                        currentEventType.current = null;
                        eventContent.current = '';
                    }
                    continue;
                }

                const decode = decoder.decode(value, {stream: true});

                //  检查是否为包含事件的格式
                try {
                    const parse = JSON.parse(decode);
                    const respData = JSON.parse(parse.data);
                    const incomingEventType = parse.event || 'content';

                    //  检查是否切换到了新的事件类型
                    if (currentEventType.current && currentEventType.current !== incomingEventType) {
                        console.log(`Regenerate event type changed from ${currentEventType.current} to ${incomingEventType}, completing previous event`);

                        // 上一个事件完成，触发完成处理
                        await handleEventComplete(currentEventType.current, eventContent.current);

                        // 重置累积内容
                        eventContent.current = '';
                    }

                    //  更新当前事件类型
                    currentEventType.current = incomingEventType;

                    if (incomingEventType !== 'content') {
                        //  累积事件内容
                        const newEventContent = eventContent.current + (respData.content || '');
                        eventContent.current = newEventContent;

                        //  处理事件进度
                        const eventHandled = await handleEventProgress(incomingEventType, {
                            ...respData,
                            accumulatedContent: newEventContent,
                            isComplete: false
                        });

                        // 如果事件已被处理，跳过内容更新逻辑
                        if (eventHandled) {
                            continue;
                        }
                    }

                    // 处理内容更新
                    const newContent = respData.content || '';
                    if (newContent && !partial.endsWith(newContent)) {
                        partial += newContent;
                    } else if (newContent && partial.endsWith(newContent)) {
                        console.warn('🚨 检测到重复内容，跳过累积:', newContent);
                    }

                    // console.log('📚 累积内容:', {
                    //     partialLength: partial.length,
                    //     partialContent: partial.substring(Math.max(0, partial.length - 50))
                    // });
                    // 清除之前的打字间隔
                    if (typingIntervalId) {
                        clearInterval(typingIntervalId);
                    }

                    // 开始新的打字效果
                    typingIntervalId = setInterval(() => {
                        if (currentContent.length < partial.length) {
                            currentContent = isStreamFinished ? partial : partial.slice(0, currentContent.length + 2);
                            setChats?.((prev: ChatMessage[]) => {
                                const newChats = [...(prev || [])];
                                const lastMsg = newChats[newChats.length - 1];

                                if (!lastMsg) {
                                    return prev;
                                }

                                if (lastMsg.role === 'assistant') {
                                    lastMsg.loading = false;
                                    lastMsg.content = currentContent;

                                    if (!lastMsg.options?.messageSessionId && respData.metadataMap && respData.metadataMap.messageSessionId) {
                                        lastMsg.options = {messageSessionId: respData.metadataMap.messageSessionId};
                                    }

                                    lastMsg.updateAt = Date.now();
                                }
                                return newChats;
                            });

                            if (autoScrollEnabled.current) {
                                scrollToBottom();
                            }
                        }

                        // 当前内容已经追上完整内容时停止
                        if (currentContent === partial || isStreamFinished) {
                            clearInterval(typingIntervalId!);
                            typingIntervalId = null;
                        }
                    }, 50);
                } catch (error) {
                    //  如果解析失败，当作普通内容处理（兼容旧格式）
                    partial += decode;
                }


            }

            // 等待最后的打字效果完成
            await waitForTypingComplete();

            // 清理间隔（如果还存在）
            if (typingIntervalId) {
                clearInterval(typingIntervalId);
            }


        } catch (error) {
            console.error('Regenerate error:', error);
        } finally {
            // 确保打字效果完成后再重置状态
            setIsStreaming(false);
            setSendLoading(false);
        }
    };


    // 渲染消息列表
    const renderMessages = () => {
        if (!chats?.length) {
            return (
                <div style={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    paddingTop: '103px'
                }}>
                    <Avatar size={88} src={botAvatar} style={{marginBottom: '16px'}}/>
                    <div className={"bot-chat-title"}>{helloMessage}</div>
                    <div className={"bot-chat-description"}>{options?.botDescription}</div>
                </div>
            );
        }

        return (
            <Bubble.List
                autoScroll={true}
                items={chats.map((chat, index) => ({
                    key: chat.id + Math.random().toString(),
                    // typing: {suffix: <>💗</>},
                    header: (
                        <Space className={"bubble-header"}>
                            {new Date(chat.created).toLocaleString()}
                        </Space>
                    ),
                    loading: chat.loading,
                    loadingRender: () => (
                        <Space>
                            <Spin size="small"/>
                            AI正在思考中...
                        </Space>
                    ),
                    footer: (
                        <Space>
                            {(chat.role === 'assistant') && !isStreaming && (<Button
                                color="default"
                                variant="text"
                                size="small"
                                icon={<SyncOutlined/>}
                                onClick={() => {
                                    // 点击按钮时重新生成该消息
                                    if (chat.role === 'assistant') {
                                        handleRegenerate(index);
                                    }
                                }}
                            />)}

                            {
                                !isStreaming && <Button
                                    color="default"
                                    variant="text"
                                    size="small"
                                    icon={<CopyOutlined/>}
                                    onClick={async () => {
                                        try {
                                            await navigator.clipboard.writeText(chat.content);
                                            message.success('复制成功');
                                        } catch (error) {
                                            console.error(error);
                                            message.error('复制失败');
                                        }
                                    }}
                                />
                            }
                            {(chat.role === 'user' && showQaButton) && !isStreaming && <Button
                                color="default"
                                variant="text"
                                size="small"

                                icon={<FolderAddOutlined/>}
                                onClick={async () => {
                                    handleQaClick(chat, index)
                                }}
                            ></Button>}
                            {(
                                chat.role === "assistant" && llmDetail?.options?.voiceEnabled &&
                                !isStreaming &&
                                <Button
                                    color="default"
                                    variant="text"
                                    size="small"
                                    loading={findVoiceLoading}
                                    icon={chat.options?.messageSessionId && playingSessionId === chat.options?.messageSessionId ?
                                        <PauseCircleOutlined/> : <PlayCircleOutlined/>}
                                    onClick={async () => {
                                        // 如果没有 messageSessionId，先获取音频
                                        if (!chat.options || !chat.options.messageSessionId || !(voiceMapRef.current.has(chat.options.messageSessionId) && voiceMapRef.current.get(chat.options.messageSessionId)!.length > 0)) {
                                            const resp = await findVoice({
                                                data: {
                                                    botId:llmDetail.id,
                                                    fullText: chat.content,
                                                }
                                            });

                                            if (resp.data.errorCode == 0) {
                                                const {base64, messageSessionId} = resp.data.data;
                                                if (!chat.options) {
                                                    chat.options = {messageSessionId: ""};
                                                }
                                                chat.options.messageSessionId = messageSessionId;
                                                const voiceMap = voiceMapRef.current;

                                                if (!voiceMap.has(messageSessionId)) {
                                                    voiceMap.set(messageSessionId, []);
                                                }

                                                voiceMap.get(messageSessionId)!.push(base64);
                                                // 如果正在播放当前消息的音频，则停止
                                                stopCurrentPlayback();
                                                playAudioQueue(messageSessionId);
                                            }
                                            return;
                                        }

                                        const messageSessionId = chat.options.messageSessionId;

                                        if (isSessionPlaying(messageSessionId)) {
                                            // 如果正在播放当前消息的音频，则停止
                                            stopCurrentPlayback();
                                        } else {
                                            // 如果没有播放或播放的是其他消息，则开始播放当前消息
                                            stopCurrentPlayback(); // 先停止任何正在播放的音频

                                            playAudioQueue(messageSessionId);
                                        }

                                    }}
                                >

                                </Button>
                            )}
                        </Space>
                    ),
                    role: chat.role === 'user' ? 'local' : 'ai',
                    content: chat.role === 'assistant' ? (
                        <div>
                            {/* 🧠 使用 ThoughtChain 组件 */}
                            {chat.thoughtChains && chat.thoughtChains.length > 0 && (
                                <ThoughtChain
                                    items={chat.thoughtChains}
                                    style={{marginBottom: '12px'}}
                                />
                            )}

                            {/* 🌟 渲染主要内容 */}
                            <RenderMarkdown content={chat.content} fileList={chat.files || chat?.options?.fileList}/>
                        </div>
                    ) : <RenderMarkdown content={chat.content} fileList={chat.files || chat?.options?.fileList}/>,
                    // avatar: chat.role === 'assistant' ? (
                    //     <img
                    //         src={botAvatar}
                    //         style={{width: 32, height: 32, borderRadius: '50%'}}
                    //         alt="AI Avatar"
                    //     />
                    // ) : {icon: <UserOutlined/>, style: fooAvatar},
                }))}
                roles={{ai: {placement: 'start'}, local: {placement: 'end'}}}
            />
        );
    };

    // qa按钮点击事件
    const handleQaClick = (chat: ChatMessage, index: number) => {
        if (onQaButtonClick) {
            onQaButtonClick(chat, index, chats);
        }
    };

    const SENDER_PROMPTS = prompts || [
        {
            key: '1',
            description: '你好'
        },
        {
            key: '2',
            description: '你是谁？'
        }
    ];


    // 聊天输入框 header 属性

    const senderRef = React.useRef<GetRef<typeof Sender>>(null);

    const [headerOpen, setHeaderOpen] = React.useState(false);
    const [fileItems, setFileItems] = React.useState<GetProp<AttachmentsProps, 'items'>>([]);
    const [fileUrlList, setFileUrlList] = useState<Array<{ uid: string, url: string }>>([])
    const [fileUploading, setFileUploading] = useState(false);

    const {doPost: uploadFile} = usePost("/api/v1/commons/uploadPrePath");

    const imageExtensions = [
        '.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp',
        '.svg', '.ico', '.tiff', '.tif', '.avif', '.heic', '.heif'
    ];

    const senderHeader = (
        llmDetail && llmDetail.llmOptions && llmDetail.llmOptions.multimodal &&
        <Sender.Header
            title={<span className={"bot-chat-title"}>文件上传</span>}
            open={headerOpen}
            onOpenChange={setHeaderOpen}
            className={"chat-send-header"}
            styles={{
                content: {
                    padding: 0,
                },
            }}
        >
            <Attachments
                items={fileItems}
                overflow={"scrollX"}
                imageProps={{height: "100%", width: "100%"}}
                customRequest={async ({file, onSuccess}) => {

                    const uFile = file as UploadFile;

                    const fileData = new FormData();
                    fileData.append("file", file)


                    try {
                        setFileUploading(true)
                        const resp = await uploadFile({
                            params: {
                                prePath: "aibot/files/"
                            },
                            data: fileData
                        })

                        if (resp.data.errorCode !== 0) {
                            setFileItems((prev) => {
                                return prev.filter(fileItem => fileItem.originFileObj?.uid !== uFile.uid);
                            })
                            return;
                        }

                        const uid: string = uFile.uid;
                        const url: string = resp.data.data as string;

                        const fileUrlObj = {uid, url}

                        setFileUrlList((prev) => {
                            const fileUrlList = [];
                            prev.forEach(fileUrl => fileUrlList.push(fileUrl))
                            fileUrlList.push(fileUrlObj)
                            return fileUrlList;
                        })
                        onSuccess?.(resp.data.data, file)
                    } catch (e) {
                        setFileItems((prev) => {
                            return prev.filter(fileItem => fileItem.originFileObj?.uid !== uFile.uid);
                        })
                    } finally {
                        setFileUploading(false)
                    }

                }}
                onChange={({file, fileList}) => {

                    const isAdd = fileItems.length < fileList.length

                    const isDelete = fileItems.length > fileList.length


                    if (isAdd) {
                        const extension = file.name.toLowerCase().substring(file.name.lastIndexOf("."));

                        if (!imageExtensions.includes(extension)) {
                            message.error("仅支持图片文件!")
                            return;
                        }

                        if (fileItems.length >= 3) {
                            message.error("暂时仅支持上传最多三张图片!")
                            return;
                        }

                    }

                    if (isDelete) {
                        setFileUrlList((prev) => {
                            const newFileUrlList: { uid: string; url: string; }[] = [];
                            prev.forEach(fileUrl => {
                                if (fileUrl.uid !== file.originFileObj?.uid) {
                                    newFileUrlList.push(fileUrl)
                                }
                            })
                            return newFileUrlList
                        })
                    }


                    setFileItems(fileList)


                }}
                placeholder={(type) =>
                    type === 'drop'
                        ? {
                            title: 'Drop file here',
                        }
                        : {
                            icon: <img src={uploadIfle} alt="upload" style={{height: '24px', width: '24px'}}/>,
                            title: <span className={"upload-file-title"}>上传文件</span>,
                            description: <span className={"upload-file-description"}>点击或拖拽上传，目前仅支持图片</span>,
                        }
                }
                getDropContainer={() => senderRef.current?.nativeElement}
            />
        </Sender.Header>
    )


    const mediaStreamRef = useRef<MediaStream | null>(null);
    const audioContextRef = useRef<AudioContext | null>(null);
    const processorRef = useRef<ScriptProcessorNode | null>(null);
    const recordedBuffersRef = useRef<Int16Array[]>([])
    const [recording, setRecording] = React.useState(false);
    const {doPost: voiceInput} = usePost("/api/v1/aiBot/voiceInput")

    const startPCMRecording = async (): Promise<void> => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({
                audio: {
                    sampleRate: 16000,
                    channelCount: 1,
                    echoCancellation: true,
                    noiseSuppression: true,
                    autoGainControl: true
                }
            });

            mediaStreamRef.current = stream;

            const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)({
                sampleRate: 16000
            });
            audioContextRef.current = audioContext;

            const source = audioContext.createMediaStreamSource(stream);
            const processor = audioContext.createScriptProcessor(4096, 1, 1);
            processorRef.current = processor;

            recordedBuffersRef.current = [];

            processor.onaudioprocess = (event) => {
                const inputBuffer = event.inputBuffer;
                const inputData = inputBuffer.getChannelData(0);

                const pcmData = new Int16Array(inputData.length);
                for (let i = 0; i < inputData.length; i++) {
                    const sample = Math.max(-1, Math.min(1, inputData[i]));
                    pcmData[i] = sample < 0 ? sample * 0x8000 : sample * 0x7FFF;
                }

                recordedBuffersRef.current.push(pcmData);
            };

            source.connect(processor);
            processor.connect(audioContext.destination);


        } catch (error) {
            console.error('录制启动失败:', error);
            message.error('无法访问麦克风，请检查权限设置');
            throw error;
        }
    };


    const stopPCMRecording = (): Promise<Int16Array | null> => {
        return new Promise((resolve) => {
            try {
                if (mediaStreamRef.current) {
                    mediaStreamRef.current.getTracks().forEach(track => track.stop());
                    mediaStreamRef.current = null;
                }

                if (audioContextRef.current) {
                    audioContextRef.current.close();
                    audioContextRef.current = null;
                }

                if (processorRef.current) {
                    processorRef.current.disconnect();
                    processorRef.current = null;
                }

                const totalLength = recordedBuffersRef.current.reduce((acc, buffer) => acc + buffer.length, 0);

                if (totalLength === 0) {
                    console.warn('没有录制到音频数据');
                    resolve(null);
                    return;
                }

                const mergedData = new Int16Array(totalLength);
                let offset = 0;

                recordedBuffersRef.current.forEach(buffer => {
                    mergedData.set(buffer, offset);
                    offset += buffer.length;
                });

                console.log(`PCM录音结束，录制了 ${(totalLength / 16000).toFixed(2)} 秒音频`);

                recordedBuffersRef.current = [];
                resolve(mergedData);

            } catch (error) {
                console.error('停止录制失败:', error);
                resolve(null);
            }
        });
    };

    const uploadPCMData = async (pcmData: Int16Array): Promise<any> => {
        if (!pcmData || pcmData.length === 0) {
            message.warning('没有录制到音频数据');
            return null;
        }

        const formData = new FormData();
        const blob = new Blob([pcmData.buffer], {type: 'audio/pcm'});

        formData.append('audio', blob, 'voice_message.pcm');
        formData.append('sampleRate', '16000');
        formData.append('channels', '1');
        formData.append('bitDepth', '16');
        formData.append('duration', String(pcmData.length / 16000));

        const response = await voiceInput({
            data: formData
        })

        return response;

    };


    return (
        <div
            style={{
                width: '100%',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                background: '#fff',
                ...appStyle,
                ...style,
            }}
        >
            {/* 消息容器 */}
            <div
                ref={messagesContainerRef}
                style={{
                    flex: 1,
                    overflowY: 'auto',
                    padding: '16px',
                    scrollbarWidth: 'none',
                }}
            >
                {loading ? (
                    <Spin tip="加载中..."/>
                ) : (
                    <>
                        {renderMessages()}
                        <div ref={messagesEndRef}/>
                        {/* 锚点元素 */}
                    </>
                )}
            </div>
            {/* 输入区域 */}

            <div
                style={{
                    padding: '12px',
                    display: 'flex',
                    flexDirection: "column",
                    gap: '8px',
                }}
            >

                {/* 🌟 提示词 */}
                <div style={{display: "flex", flexDirection: "row", gap: "8px", justifyContent: "space-between", paddingBottom: 10}}>
                    <Prompts
                        items={SENDER_PROMPTS}
                        onItemClick={(info) => {
                            handleSubmit(info.data.description as string)
                        }}
                        styles={{
                            item: {padding: '6px 12px', borderRadius: '8px', height: 36, border: '1px solid #C7C7C7'},
                        }}
                    />
                    <div className={"chat-clear-text"}>
                        { chats?.length > 0 &&
                            <Button
                                // disabled={(sendLoading || isStreaming || recording || fileUploading) ? true : !fileItems.length && !chats?.length}  // 强制不禁用
                                onClick={async (e: any) => {
                                    e.preventDefault();  // 阻止默认行为（如果有）
                                    setSendLoading(true)
                                    await clearMessage?.();
                                    setSendLoading(false)
                                    setFileItems([])
                                    setFileUrlList([])
                                    setHeaderOpen(false)
                                }}
                            >
                                <img src={clearButtonIcon} style={{width: 24, height: 24}} alt="delete"/>
                                <span className={"chat-clear-button-text"}>
                                    清除上下文
                                </span>
                            </Button>
                        }

                    </div>

                </div>


                {customToolBarr ?
                    <div style={{
                        width: "100%",
                        display: "flex",
                        justifyContent: "start",
                        alignItems: "center",
                    }}>
                        {customToolBarr}
                    </div> : <></>
                }

                <div className={"chat-sender"}>
                    <Sender
                        ref={senderRef}
                        value={content}
                        onChange={setContent}
                        onSubmit={handleSubmit}
                        placeholder={'尽管问...'}
                        // onKeyDown={(e) => {
                        //     if (e.key === 'Enter' && !e.shiftKey) {
                        //         e.preventDefault(); // 防止换行（如果是 textarea）
                        //         handleSubmit(content);
                        //     }
                        // }}
                        allowSpeech={{
                            // When setting `recording`, the built-in speech recognition feature will be disabled
                            recording,
                            onRecordingChange: async (nextRecording) => {

                                if (nextRecording) {
                                    console.log("录音中....");
                                    try {
                                        await startPCMRecording();
                                    } catch (error) {
                                        setRecording(false);
                                        return;
                                    }
                                } else {
                                    console.log("录音结束，发送请求.");
                                    try {
                                        message.loading({content: '正在处理语音...', key: 'processing'});

                                        const pcmData = await stopPCMRecording();

                                        if (pcmData) {
                                            const result = await uploadPCMData(pcmData);


                                            if (result) {
                                                message.success({content: '语音发送成功', key: 'processing'});

                                                // 如果后端返回了转换的文本
                                                if (result.data.data) {
                                                    setContent(result.data.data);
                                                    handleSubmit(result.data.data)
                                                }
                                            }
                                        } else {
                                            message.warning({content: '没有录制到音频', key: 'processing'});
                                        }

                                    } catch (error) {
                                        message.error({content: '语音处理失败', key: 'processing'});
                                        console.error('语音处理失败:', error);
                                    }
                                }

                                setRecording(nextRecording);
                            },
                        }}
                        loading={sendLoading || isStreaming || fileUploading}
                        disabled={inputDisabled}
                        // header={<div style={{ display: "flex", alignItems: "center" , paddingTop: 8, height: 32, paddingLeft: 30}}>
                        //     <AntdVoiceWave
                        //         isRecording={true}
                        //         color="#1890ff"
                        //     />
                        // </div>}

                        header={senderHeader}
                        actions={false}
                        autoSize={{ minRows: 4, maxRows: 4}}
                        footer={({ components }) => {
                            const {SendButton, SpeechButton} = components ;
                            return (
                                <Space size="small" style={{display: "flex", justifyContent: "flex-end", gap: "0px"}}>

                                    {/*{*/}
                                    {/*<div className={"file-link-item ant-space-item"} onClick={() =>{*/}
                                    {/*}}> <img alt="" src={fileIcon} style={{width: 16, height: 16}}/></div>*/}
                                    {/*}*/}

                                    {
                                        llmDetail && llmDetail.llmOptions && llmDetail.llmOptions.multimodal &&
                                        // <Badge dot={fileItems.length > 0 && !headerOpen}>
                                            <div  className={"file-link-item ant-space-item"} onClick={() => setHeaderOpen(!headerOpen)} >
                                                <img src={fileIcon} alt="" style={{width: 18, height: 18, cursor: 'pointer'}}/>
                                            </div>
                                        // </Badge>
                                    }

                                    <SpeechButton className={"speech-button"}
                                                  disabled={sendLoading || isStreaming || fileUploading}
                                    />

                                    {/*<div onClick={handleSpeechIconClick}>*/}
                                    {/*    <img src={speechIcon} alt="" style={{width: 16, height: 16, cursor: "pointer"}}/>*/}
                                    {/*</div>*/}
                                    <SendButton
                                        type="primary"
                                        // onClick={() => handleSubmit(content)}
                                        disabled={content == '' || inputDisabled || recording || fileUploading}
                                        icon={<img alt="" src={content == '' ? senderIcon : senderIconSelected} style={{width: 30, height: 30}}/>}
                                        loading={sendLoading || isStreaming}
                                        style={{ marginLeft: '10px' }}
                                    />


                                </Space>
                            );
                        }}
                    />
                </div>

            </div>
        </div>
    );
};
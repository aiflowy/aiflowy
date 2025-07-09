import React from "react";
import {ChatMessage, RenderMarkdown} from "../components/AiProChat/AiProChat.tsx";



export function parseAnswerUtil(text: string | undefined | null) {
    const result = {
        thinking:"",
        thought: '',
        action: '',
        actionInput: '',
        finalAnswer: ''
    };

    // 添加类型检查和空值检查
    if (!text || typeof text !== 'string') {
        return result;
    }

    // 使用正则表达式匹配各个部分，去除前缀

    // 匹配 thinking: 开头的内容，但只保留内容部分
    const thinkingMatch = text.match(/(?:✅\s*)?thinking:\s*([\s\S]*?)$/);

    // 匹配 🧠 Thought: 或 Thought: 开头的内容，但只保留内容部分
    const thoughtMatch = text.match(/(?:🧠\s*)?Thought:\s*([\s\S]*?)(?=\n(?:Action:|Final Answer:)|$)/);

    // 匹配 Action: 开头的内容，但只保留内容部分
    const actionMatch = text.match(/Action:\s*([\s\S]*?)(?=\n(?:Action Input:|Final Answer:)|$)/);

    // 匹配 Action Input: 开头的内容，但只保留内容部分
    const actionInputMatch = text.match(/Action Input:\s*([\s\S]*?)(?=\n(?:Final Answer:)|$)/);

    // 匹配 ✅ Final Answer: 或 Final Answer: 开头的内容，但只保留内容部分
    const finalAnswerMatch = text.match(/(?:✅\s*)?Final Answer:\s*([\s\S]*?)$/);

    if (thinkingMatch) {
        result.thinking = thinkingMatch[1].trim();
        return result;
    }

    if (thoughtMatch) {
        result.thought = text;
    }

    if (actionMatch) {
        result.action = actionMatch[1].trim();
    }

    if (actionInputMatch) {
        result.actionInput = actionInputMatch[1].trim();
    }

    if (finalAnswerMatch) {
        result.finalAnswer = finalAnswerMatch[1].trim();
        return result;
    }

    return result;
}

// ThoughtChainItem接口定义
interface ThoughtChainItem {
    key?: string;
    icon?: React.ReactNode;
    title?: React.ReactNode;
    description?: React.ReactNode;
    extra?: React.ReactNode;
    content?: React.ReactNode;
    footer?: React.ReactNode;
    status?: 'pending' | 'success' | 'error';
}


function cleanFinalContent(content: string): string {
    if (!content) return '';

    // 去除"Thought:"开头的行
    let cleaned = content.replace(/^(?:🧠\s*)?Thought:\s*.*$/gm, '');

    // 去除"Final Answer:"前缀
    cleaned = cleaned.replace(/^(?:✅\s*)?Final Answer:\s*/gm, '');

    // 清理多余的空行
    cleaned = cleaned.replace(/\n\s*\n\s*\n/g, '\n\n').trim();

    return cleaned;
}



export function processArray(arr: any[]): ChatMessage[] {
    if (!arr || arr.length === 0) return [];

    // 第1步：以type为1，role为user的消息进行分组
    const groups: any[][] = [];
    let currentGroup: any[] = [];

    for (const item of arr) {
        if (!item) continue;

        // 如果是type为1且role为user的消息，开始新的分组
        if (item.options?.type === 1 && item.role === 'user') {
            // 如果当前分组不为空，保存它
            if (currentGroup.length > 0) {
                groups.push(currentGroup);
            }
            // 开始新的分组
            currentGroup = [item];
        } else {
            // 添加到当前分组
            currentGroup.push(item);
        }
    }

    // 添加最后一个分组
    if (currentGroup.length > 0) {
        groups.push(currentGroup);
    }

    const result: ChatMessage[] = [];

    // 第2步：遍历每个分组
    for (const group of groups) {
        if (group.length === 0) continue;

        // 首条消息（用户消息）
        const userMessage = group[0];
        result.push({
            content: userMessage.content || '',
            files:userMessage.files,
            created: userMessage.created || Date.now(),
            id: userMessage.id || '',
            role: 'user' as const,
            options: userMessage.options || {}
        });

        // 如果分组中只有一条消息，跳过
        if (group.length <= 1) continue;


        // 第3步：处理assistant消息，构建思维链
        const thoughtChains: ThoughtChainItem[] = [];
        const assistantMessages = group.slice(1); // 除了首条用户消息之外的所有消息
        let finalContent = '';

        // 遍历除最后一条消息之外的所有assistant消息，构建思维链
        for (let i = 0; i < assistantMessages.length - 1; i++) {
            const item = assistantMessages[i];
            if (item.role !== 'assistant') continue;

            if (item.role == "user") continue;


            const parsed = parseAnswerUtil(item.content);

            if (parsed.thinking) {
                thoughtChains.push({
                    key: `thinking-${item.id}-thinking`,
                    title: '🧠 思考',
                    content: <RenderMarkdown content={parsed.thinking} />,
                    status: 'pending' as const
                });
            }

            if (parsed.thought) {
                thoughtChains.push({
                    key: `thought-${item.id}-thought`,
                    title: '💭 思路',
                    content: <RenderMarkdown content={parsed.thought} />,
                    status: 'pending' as const
                });
            }



            if (item.options &&( item.options.type === 1 || item.options.type === 2 ) ) {
                const options = item.options;
                const title = options.chainTitle;
                let content = options.chainContent;


                if ( item.options.type === 2){

                    if (typeof content ==  "object" ){
                        for (const o in content){
                            content[o] = content[o].replace(/\n/g, " ");
                        }
                        content = JSON.stringify(content, null, 2);
                    }

                }

                thoughtChains.push({
                    key: `thought-${item.id}-input`,
                    title,
                    content:<RenderMarkdown content={content} />,
                    status:'pending' as const
                });
            }

        }

        // 第4步：处理最后一条消息，设置最终内容
        if (assistantMessages.length > 0) {
            const lastMessage = assistantMessages[assistantMessages.length - 1];
            if (lastMessage && lastMessage.role === 'assistant') {
                const lastParsed = parseAnswerUtil(lastMessage.content);

                // 设置最终内容，清理格式
                if (lastParsed.finalAnswer) {
                    finalContent = cleanFinalContent(lastParsed.finalAnswer);
                } else {
                    // 如果没有finalAnswer，清理整个内容
                    finalContent = cleanFinalContent(lastMessage.content || '');
                }

                // 创建带有思维链的assistant消息
                result.push({
                    content: finalContent,
                    created: lastMessage.created || Date.now(),
                    id: lastMessage.id || '',
                    role: 'assistant' as const,
                    options: lastMessage.options || {},
                    thoughtChains: thoughtChains.length > 0 ? thoughtChains : undefined
                });
            }
        }
    }

    return result;
}
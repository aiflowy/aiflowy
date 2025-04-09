import React, {useEffect, useState} from 'react';

import {useLayout} from '../../../hooks/useLayout.tsx';
import {Row} from 'antd/lib/index';
import {App, Button, Col, Collapse, Modal, Select} from 'antd';
import Title from 'antd/es/typography/Title';
import {DeleteOutlined, PlusOutlined} from "@ant-design/icons";
import {useDetail, useGet, useList, usePostManual, useRemove, useSave, useUpdate} from "../../../hooks/useApis.ts";
import {useParams} from "react-router-dom";
import {WorkflowsModal} from "./Workflows.tsx";
import {DebouncedTextArea} from "../../../components/DebouncedTextArea";
import {LlmSlider} from "./LlmSlider.tsx";
import {useSse} from "../../../hooks/useSse.ts";
import {KnowledgeModal} from "./Knowledge.tsx";
import TextArea from "antd/es/input/TextArea";
import {getSessionId} from "../../../libs/getSessionId.ts";
import {AiProChat, ChatMessage} from "../../../components/AiProChat/AiProChat";
import {PluginModal} from "./Plugins.tsx";
const colStyle: React.CSSProperties = {
    background: '#fafafa',
    padding: '8px',
    border: "1px solid #eee"
};


const text = (
    <p style={{paddingInlineStart: 24}}>
    </p>
);


type CollapseLabelProps = {
    text: string,
    onClick: () => void
}

const CollapseLabel: React.FC<CollapseLabelProps> = ({text, onClick}) => {
    return <div style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
    }}>
        <span>{text}</span>
        <PlusOutlined onClick={(event) => {
            event.stopPropagation();
            onClick?.();
        }}/>
    </div>
}


export const ListItem: React.FC<{
    title?: string,
    description?: string,
    icon?: string,
    onButtonClick?: () => void
}> = ({title, description, icon = "/favicon.png", onButtonClick}) => {
    return (
        <div style={{
            display: "flex",
            gap: "20px",
            alignItems: "center",
            background: "#eaeaea",
            padding: "10px",
            borderRadius: "3px"
        }}>
            <div style={{"width": "40px"}}><img src={icon} alt="" style={{maxWidth: "100%"}}/>
            </div>
            <div style={{flexGrow: 1}}>
                <div>{title}</div>
                <div style={{color: "#999999"}}>{description}</div>
            </div>
            <div>
                <Button type={"text"} onClick={() => {
                    onButtonClick?.()
                }} icon={<DeleteOutlined/>}/>
            </div>
        </div>
    )
}


const BotDesign: React.FC = () => {

    const [span] = useState(8)
    const params = useParams();
    const {result: detail, doGet: reGetDetail} = useDetail("aiBot", params.id);
    const {message} = App.useApp()

    const [systemPrompt, setSystemPrompt] = useState<string>('')
    const [welcomeMessage, setWelcomeMessage] = useState<string>('')

    const {setOptions} = useLayout();
    useEffect(() => {
        setOptions({
            leftMenuCollapsed: true, showBreadcrumb: false,
            headerLeftEl: <span style={{marginLeft: "20px"}}>{detail?.data?.title}</span>,
        })
        return () => {
            setOptions({leftMenuCollapsed: false, showBreadcrumb: true, headerLeftEl: ''})
        }
    }, [detail]);

    const {doUpdate} = useUpdate("aiBot");

    const updateBot = (values: any) => {
        doUpdate({
            data: {
                ...values,
                id: params.id,
            }
        }).then(reGetDetail)
            .then(() => {
                message.success("保存成功")
            })
    }

    const {doPost: updateBotLLMOptions} = usePostManual("/api/v1/aiBot/updateLlmOptions")
    const doUpdateBotLLMOptions = (values: any) => {
        updateBotLLMOptions({
            data: {
                llmOptions: values,
                id: params.id,
            }
        })
    }

    const {doPost: updateBotOptions} = usePostManual("/api/v1/aiBot/updateOptions")
    const doUpdateBotOptions = (values: any) => {
        updateBotOptions({
            data: {
                options: values,
                id: params.id,
            }
        })
    }

    useEffect(() => {
        setSystemPrompt(detail?.data?.llmOptions?.systemPrompt)
        setWelcomeMessage(detail?.data?.options?.welcomeMessage)
    }, [detail]);

    const {result: workflowResult, doGet: doGetWorkflow} = useList("aiBotWorkflow", {"botId": params.id});
    const {doSave: doSaveWorkflow} = useSave("aiBotWorkflow");
    const {doRemove: doRemoveAiBotWorkflow} = useRemove("aiBotWorkflow");
    const [workflowOpen, setWorkflowOpen] = useState(false)

    const {result:pluginResult,doGet: doGetPlugin} = useList("aiBotPlugins",{"botId": params.id});
    const {doSave:doSavePlugin} = useSave("aiBotPlugins");
    const {doRemove:doRemovePlugin} = useRemove("aiBotPlugins");
    const [pluginOpen,setPluginOpen] = useState(false)


    const {result: knowledgeResult, doGet: doGetKnowledge} = useList("aiBotKnowledge", {"botId": params.id});
    const {doSave: doSaveKnowledge} = useSave("aiBotKnowledge");
    const {doRemove: doRemoveAiBotKnowledge} = useRemove("aiBotKnowledge");
    const [knowledgeOpen, setKnowledgeOpen] = useState(false)


    const {result} = useGet("/api/v1/aiLlm/list", {supportFunctionCalling: true});

    const {start: startChat} = useSse("/api/v1/aiBot/chat");

    const [chats, setChats] = useState<ChatMessage[]>([]);
    const {result: messageResult} = useList("aiBotMessage", {
        botId: params.id,
        sessionId: getSessionId()
    });
    useEffect(() => {
        setChats(messageResult?.data)
    }, [messageResult]);

    return (
        <>
            <WorkflowsModal open={workflowOpen} onClose={() => setWorkflowOpen(false)}
                            onCancel={() => setWorkflowOpen(false)}
                            goToPage={"/ai/workflow"}
                            onSelectedItem={item => {
                                setWorkflowOpen(false)
                                doSaveWorkflow({
                                    data: {
                                        botId: params.id,
                                        workflowId: item.id,
                                    }
                                }).then(doGetWorkflow)
                            }}
            />

            <PluginModal open={pluginOpen} onClose={() => setPluginOpen(false)}
                         onCancel={() => setPluginOpen(false)}
                         goToPage={"/ai/plugins"}
                         onSelectedItem={item => {
                             setPluginOpen(false)
                             doSavePlugin({
                                 data: {
                                     botId: params.id,
                                     pluginId: item.id,
                                 }
                             }).then(doGetPlugin)
                         }}
            />


            <KnowledgeModal open={knowledgeOpen} onClose={() => setKnowledgeOpen(false)}
                            onCancel={() => setKnowledgeOpen(false)}
                            goToPage={"/ai/knowledge"}
                            onSelectedItem={item => {
                                setKnowledgeOpen(false)
                                doSaveKnowledge({
                                    data: {
                                        botId: params.id,
                                        knowledgeId: item.id,
                                    }
                                }).then(doGetKnowledge)
                            }}/>
            <div style={{height: 'calc(100vh - 68px)', display: "flex"}} className={"agentsflow"}>
                <Row style={{width: "100%"}}>
                    <Col span={span} style={colStyle}>
                        <Title level={5}>系统提示词（System Prompt）</Title>
                        <DebouncedTextArea value={systemPrompt} style={{height: "calc(100% - 30px)"}} autoSize={false}
                                           onChangeImmediately={(e) => {
                                               setSystemPrompt(e.target.value)
                                           }}
                                           onChange={e => {
                                               doUpdateBotLLMOptions({systemPrompt: e.target.value})
                                           }}/>
                    </Col>
                    <Col span={span} style={colStyle}>
                        <div style={{margin: "10px 0 5px", color: "#666"}}>大模型</div>
                        <div style={{
                            background: "#f5f5f5",
                            borderRadius: "3px",
                            padding: "10px 20px",
                            display: "flex",
                            flexDirection: "column",
                            gap: "10px"
                        }}>
                            <Select
                                style={{width: "100%"}}
                                placeholder="请选择大模型"
                                allowClear
                                fieldNames={{label: 'title', value: 'id'}}
                                options={[
                                    ...result?.data || []
                                ]}
                                value={detail?.data?.llmId}
                                onChange={(value: any) => {
                                    value = value || ''
                                    updateBot({llmId: value})
                                }}
                            />
                            <LlmSlider title="温度" min={0.1} max={1} step={0.1}
                                       defaultValue={detail?.data?.llmOptions?.temperature || 0.7}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({temperature: value})
                                       }}/>
                            <LlmSlider title="TopK" min={1} max={10} step={1}
                                       defaultValue={detail?.data?.llmOptions?.topK || 4}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({topK: value})
                                       }}/>
                            <LlmSlider title="TopP" min={0.1} max={1} step={0.1}
                                       defaultValue={detail?.data?.llmOptions?.topP || 0.7}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({topP: value})
                                       }}/>
                            <LlmSlider title="最大回复长度" min={1024} max={10000}
                                       defaultValue={detail?.data?.llmOptions?.maxReplyLength || 2048}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({maxReplyLength: value})
                                       }}/>
                            <LlmSlider title="携带历史条数" min={1} max={10} step={1}
                                       defaultValue={detail?.data?.llmOptions?.maxMessageCount || 3}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({maxMessageCount: value})
                                       }}/>
                        </div>

                        <div style={{margin: "10px 0 5px", color: "#666"}}>技能</div>
                        <Collapse items={[
                            {
                                key: 'workflow',
                                label: <CollapseLabel text="工作流" onClick={() => {
                                    setWorkflowOpen(true)
                                }}/>,
                                children: <div>
                                    {workflowResult?.data?.map((item: any) => {
                                        return <ListItem key={item.id} title={item.workflow.title}
                                                         description={item.workflow.description}
                                                         icon={item.workflow.icon}
                                                         onButtonClick={() => {
                                                             Modal.confirm({
                                                                 title: '确定要删除该工作流吗？',
                                                                 content: '删除后，该工作流将不再关联该机器人，但工作流本身不会被删除。',
                                                                 onOk: () => {
                                                                     doRemoveAiBotWorkflow({
                                                                         data: {id: item.id}
                                                                     }).then(doGetWorkflow)
                                                                 }
                                                             })
                                                         }}
                                        />
                                    })}
                                </div>,
                            },
                            {
                                key: 'trigger',
                                label: <CollapseLabel text="触发器" onClick={() => {
                                }}/>,
                                children: text,
                            },
                            {
                                key: 'plugins',
                                label: <CollapseLabel text="插件" onClick={() => {
                                    setPluginOpen(true)
                                }}/>,
                                children:
                                    <div>
                                        {pluginResult?.data?.map((item: any) => {
                                            return <ListItem key={item.id} title={item.aiPlugins.title}
                                                             description={item.aiPlugins.description}
                                                             onButtonClick={() => {
                                                                 Modal.confirm({
                                                                     title: '确定要删除该插件吗？',
                                                                     content: '删除后，该插件将不再关联该机器人，但插件本身不会被删除。',
                                                                     onOk: () => {
                                                                         doRemovePlugin({
                                                                             data: {id: item.id}
                                                                         }).then(doGetPlugin)
                                                                     }
                                                                 })
                                                             }}
                                            />
                                        })}
                                    </div>
                                ,
                            },
                        ]} bordered={false}/>

                        <div style={{margin: "20px 0 5px", color: "#666"}}>知识</div>
                        <Collapse items={[
                            {
                                key: 'knowledge',
                                label: <CollapseLabel text="知识库" onClick={() => {
                                    setKnowledgeOpen(true)
                                }}/>,
                                children: <div>
                                    {knowledgeResult?.data?.map((item: any) => {
                                        return <ListItem key={item.id} title={item.knowledge.title}
                                                         description={item.knowledge.description}
                                                         icon={item.knowledge.icon}
                                                         onButtonClick={() => {
                                                             Modal.confirm({
                                                                 title: '确定要删除该知识库吗？',
                                                                 content: '删除后，该知识库将不再关联该机器人，但知识库本身不会被删除。',
                                                                 onOk: () => {
                                                                     doRemoveAiBotKnowledge({
                                                                         data: {id: item.id}
                                                                     }).then(doGetKnowledge)
                                                                 }
                                                             })
                                                         }}
                                        />
                                    })}
                                </div>,
                            },
                            {
                                key: 'database',
                                label: <CollapseLabel text="数据库" onClick={() => {
                                }}/>,
                                children: text,
                            },
                        ]} bordered={false}/>

                        <div style={{margin: "20px 0 5px", color: "#666"}}>对话设置</div>
                        <Collapse items={[
                            {
                                key: 'welcomeMessage',
                                label: <CollapseLabel text="欢迎语" onClick={() => {
                                }}/>,
                                children: <div>
                                    <DebouncedTextArea
                                        value={welcomeMessage}
                                        onChange={(value) => {
                                            doUpdateBotOptions({welcomeMessage: value.target.value})
                                        }}
                                        onChangeImmediately={(event) => {
                                            setWelcomeMessage(event.target.value)
                                        }}
                                        placeholder="请输入欢迎语"
                                        autoSize={{minRows: 2, maxRows: 6}}
                                    />
                                </div>,
                            },
                            {
                                key: 'database',
                                label: <CollapseLabel text="问题预设" onClick={() => {
                                }}/>,
                                children: text,
                            },
                            {
                                key: 'ui',
                                label: <CollapseLabel text="UI 与 Logo" onClick={() => {
                                }}/>,
                                children: text,
                            },
                            {
                                key: 'embed',
                                label: <CollapseLabel text="嵌入" onClick={() => {
                                }}/>,
                                children: <div>
                                    <div>
                                        <span>
                                            外部访问地址 <a
                                            href={window.location.href.substring(0, window.location.href.indexOf('/ai')) + '/bot/chat/' + detail?.data.id}
                                            target={"_blank"}>打开</a>
                                        </span>
                                        <TextArea readOnly disabled
                                                  value={window.location.href.substring(0, window.location.href.indexOf('/ai')) + '/bot/chat/' + detail?.data.id}></TextArea>
                                    </div>
                                </div>,
                            },
                        ]} bordered={false}/>
                    </Col>


                    <Col span={span} style={colStyle}>
                        <Title level={5}>预览</Title>
                        <div style={{height: "calc(100vh - 122px)", width: "100%"}} className={"bot-chat"}>
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
                    </Col>
                </Row>
            </div>
        </>
    );
};

export default {
    path: "/ai/bot/design/:id",
    element: BotDesign
};
